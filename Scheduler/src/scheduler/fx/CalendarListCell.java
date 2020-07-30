package scheduler.fx;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventDispatchChain;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.model.AppointmentType;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Values;
import scheduler.util.WeakChangeHandlingReference;
import scheduler.view.SymbolText;
import scheduler.view.appointment.CalendarCellData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CalendarListCell extends ListCell<CalendarCellData> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(CalendarListCell.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(CalendarListCell.class.getName());

    private static final float SINGLE_LINE_HEIGHT;

    static {
        Text text = new Text();
        Font font = text.getFont();
        final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        SINGLE_LINE_HEIGHT = fm.getLineHeight();
        LOG.finer(() -> String.format("SINGLE_LINE_HEIGHT = %f", SINGLE_LINE_HEIGHT));
    }

    private final DateTimeFormatter formatter;
    private final HBox graphic;
    private final OverviewTextFlow overviewTextFlow;
    private final LocationHBox locationHBox;
    private final Button openButton;
    private final ChangeListener<Boolean> singleLineChangeListener;
    private final WeakReference<CalendarListCellFactory> cellFactory;
    private boolean extentsBound = false;
    private boolean effectiveSingleLineMode = false;
    private boolean forceSingleLineMode = false;
    private CalendarCellData boundModel;

    public CalendarListCell(CalendarListCellFactory factory) {
        LOG.entering(getClass().getName(), "<init>", factory);
        cellFactory = new WeakReference<>(factory);
        formatter = Objects.requireNonNull(factory.getFormatter());
        BooleanProperty singleLine = factory.singleLineProperty();
        overviewTextFlow = new OverviewTextFlow();
        locationHBox = new LocationHBox();
        openButton = NodeUtil.createSymbolButton(SymbolText.EDIT, this::onOpenButtonAction);
        singleLineChangeListener = this::onSingleLineChanged;
        overviewTextFlow.initialize();
        locationHBox.initialize();
        forceSingleLineMode = singleLine.get();
        if (forceSingleLineMode || overviewTextFlow.continuedFromPrevious || overviewTextFlow.continuedOnNext) {
            overviewTextFlow.onSingleLineModeChanged();
            if (effectiveSingleLineMode) {
                onEffectiveSingleLineModeChanged();
            }
        } else {
            overviewTextFlow.onSingleLineModeChanged();
            if (!effectiveSingleLineMode) {
                onEffectiveSingleLineModeChanged();
            }
        }
        singleLine.addListener(new WeakChangeListener<>(singleLineChangeListener));
        graphic = NodeUtil.createFillingHBox(
                NodeUtil.setHBoxGrow(NodeUtil.createFillingVBox(
                        NodeUtil.setVBoxGrow(overviewTextFlow, Priority.ALWAYS),
                        locationHBox
                ), Priority.ALWAYS),
                NodeUtil.setHBoxGrow(openButton, Priority.NEVER)
        );
        LOG.exiting(getClass().getName(), "<init>");
    }

    @Override
    public void updateIndex(int i) {
        LOG.entering(getClass().getName(), "updateIndex", i);
        super.updateIndex(i);
        if (i == 0) {
            graphic.setPadding(new Insets(0, 0, 0, 16));
        } else {
            graphic.setPadding(Insets.EMPTY);
        }
        LOG.exiting(getClass().getName(), "updateIndex");
    }

    @Override
    protected synchronized void updateItem(CalendarCellData item, boolean empty) {
        LOG.entering(getClass().getName(), "updateItem", new Object[]{item, empty});
        super.updateItem(item, empty);
        if (empty || null == item) {
            LOG.finer("Initializing empty cell");
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            if (extentsBound) {
                LOG.finer("Unbinding graphic prefWidth and prefHeight");
                extentsBound = false;
                graphic.prefWidthProperty().unbind();
                graphic.prefHeightProperty().unbind();
            }
            setGraphic(null);
            if (null != boundModel) {
                LOG.finer("Unbinding model item properties");
                unbindAll();
                boundModel = null;
                overviewTextFlow.resetAll();
                locationHBox.resetAll();
            }
        } else {
            LOG.finer("Setting cell graphic");
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(graphic);
            if (!extentsBound) {
                LOG.finer("Binding graphic prefWidth and prefHeight");
                extentsBound = true;
                graphic.prefWidthProperty().bind(widthProperty());
                graphic.prefHeightProperty().bind(heightProperty());
            }
            if (null != boundModel) {
                if (boundModel == item) {
                    LOG.finer("Model item properties already bound");
                    if (getIndex() == 0) {
                        graphic.setPadding(new Insets(0, 0, 0, 16));
                    } else {
                        graphic.setPadding(Insets.EMPTY);
                    }
                    LOG.exiting(LOG.getName(), "updateIndex");
                    return;
                }
                LOG.finer("Unbinding old model item properties");
                unbindAll();
            }
            LOG.finer("Binding model item properties");
            boundModel = item;
            overviewTextFlow.onModelChanged();
            locationHBox.onModelChanged();
            overviewTextFlow.onSingleLineModeChanged();
            overviewTextFlow.bindAll();
            locationHBox.bindAll();

            if (getIndex() == 0) {
                graphic.setPadding(new Insets(0, 0, 0, 16));
            } else {
                graphic.setPadding(Insets.EMPTY);
            }
        }
        LOG.exiting(getClass().getName(), "updateIndex");
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(getClass().getName(), "buildEventDispatchChain", tail);
        CalendarListCellFactory factory = cellFactory.get();
        if (null != factory) {
            tail = factory.buildEventDispatchChain(tail);
        }
        tail = super.buildEventDispatchChain(tail);
        LOG.exiting(getClass().getName(), "buildEventDispatchChain", tail);
        return tail;
    }

    private void setEffectiveSingleLineMode(boolean value) {
        LOG.entering(getClass().getName(), "setEffectiveSingleLineMode", value);
        if (value != effectiveSingleLineMode) {
            LOG.finer(() -> String.format("setEffectiveSingleLineMode changing to %s", value));
            effectiveSingleLineMode = value;
            onEffectiveSingleLineModeChanged();
        }
        LOG.exiting(getClass().getName(), "setEffectiveSingleLineMode");
    }

    private void onEffectiveSingleLineModeChanged() {
        LOG.entering(getClass().getName(), "onEffectiveSingleLineModeChanged");
        if (effectiveSingleLineMode) {
            LOG.finer("Restoring locationHBox");
            restoreNode(locationHBox);
        } else {
            LOG.finer("Collapsing locationHBox");
            collapseNode(locationHBox);
        }
        LOG.exiting(getClass().getName(), "onEffectiveSingleLineModeChanged");
    }

    private void onOpenButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onOpenButtonAction", event);
        CalendarCellData item = getItem();
        if (null != item) {
            AppointmentOpRequestEvent e = new AppointmentOpRequestEvent(item.getModel(), event.getSource(), false);
            LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
            fireEvent(e);
        }
        LOG.exiting(getClass().getName(), "onOpenButtonAction");
    }

    private synchronized void onSingleLineChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        LOG.entering(getClass().getName(), "onSingleLineChanged", new Object[]{observable, oldValue, newValue});
        if (forceSingleLineMode != newValue) {
            LOG.finer(() -> String.format("forceSingleLineMode changing to %s", newValue));
            forceSingleLineMode = newValue;
            overviewTextFlow.onSingleLineModeChanged();
        }
        LOG.exiting(getClass().getName(), "onSingleLineChanged");
    }

    private void unbindAll() {
        overviewTextFlow.unbindAll();
        locationHBox.unbindAll();
    }

    private class OverviewTextFlow extends TextFlow {

        private final Text followsText;
        private final Text startDateTimeText;
        private final Text endDateTimeText;
        private final Text withText;
        private final Text customerNameText;
        private final Text appointmentTitleText;
        private final Text precedesText;
        private final WeakChangeHandlingReference<Boolean> continuedFromPreviousChangeListener;
        private final WeakChangeHandlingReference<LocalDateTime> startChangeListener;
        private final WeakChangeHandlingReference<LocalDateTime> endChangeListener;
        private final WeakChangeHandlingReference<String> titleChangeListener;
        private final WeakChangeHandlingReference<String> customerNameChangeListener;
        private final WeakChangeHandlingReference<Boolean> continuedOnNextChangeListener;
        private String currentTitle = "";
        private boolean continuedFromPrevious = false;
        private boolean continuedOnNext = false;
        private boolean singleLineMode = effectiveSingleLineMode;

        OverviewTextFlow() {
            LOG.entering(getClass().getName(), "<init>");
            followsText = NodeUtil.createText(SymbolText.LEFT_DOUBLE_ARROW.toString() + " ", CssClassName.SMALL_CONTROL, CssClassName.SYMBOL);
            startDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            endDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            customerNameText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            appointmentTitleText = NodeUtil.createText();
            precedesText = NodeUtil.createText(SymbolText.RIGHT_DOUBLE_ARROW.toString() + " ", CssClassName.SMALL_CONTROL, CssClassName.SYMBOL);
            withText = NodeUtil.createText(" with ");
            continuedFromPreviousChangeListener = WeakChangeHandlingReference.<Boolean>of(this::onContinuedFromPreviousChanged);
            startChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onStartChanged);
            endChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onEndChanged);
            customerNameChangeListener = WeakChangeHandlingReference.<String>of(this::onCustomerNameChanged);
            titleChangeListener = WeakChangeHandlingReference.<String>of(this::onTitleChanged);
            continuedOnNextChangeListener = WeakChangeHandlingReference.<Boolean>of(this::onContinuedOnNextChanged);
            LOG.exiting(getClass().getName(), "<init>");
        }

        private void initialize() {
            LOG.entering(getClass().getName(), "initialize");
            ObservableList<Node> children = getChildren();
            children.addAll(
                    followsText,
                    startDateTimeText,
                    NodeUtil.createText(" to "),
                    endDateTimeText,
                    withText,
                    customerNameText,
                    NodeUtil.createText(": "),
                    appointmentTitleText,
                    precedesText
            );
            LOG.exiting(getClass().getName(), "initialize");
        }

        private synchronized void onStartChanged(ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) {
            LOG.entering(getClass().getName(), "onStartChanged", new Object[]{observable, oldValue, newValue});
            startDateTimeText.setText((null == newValue) ? "(unspecified)" : formatter.format(newValue));
            LOG.exiting(getClass().getName(), "onStartChanged");
        }

        private synchronized void onEndChanged(ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) {
            LOG.entering(getClass().getName(), "onEndChanged", new Object[]{observable, oldValue, newValue});
            endDateTimeText.setText((null == newValue) ? "(unspecified)" : formatter.format(newValue));
            LOG.exiting(getClass().getName(), "onEndChanged");
        }

        private synchronized void onCustomerNameChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            LOG.entering(getClass().getName(), "onCustomerNameChanged", new Object[]{observable, oldValue, newValue});
            customerNameText.setText((null == newValue) ? "" : newValue);
            LOG.exiting(getClass().getName(), "onCustomerNameChanged");
        }

        private synchronized void onTitleChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            LOG.entering(getClass().getName(), "onTitleChanged", new Object[]{observable, oldValue, newValue});
            currentTitle = (null == newValue) ? "" : newValue;
            if (singleLineMode) {
                appointmentTitleText.setText(Values.asNonNullAndWsNormalized(currentTitle));
            } else {
                appointmentTitleText.setText(currentTitle);
            }
            LOG.exiting(getClass().getName(), "onTitleChanged");
        }

        private synchronized void onContinuedFromPreviousChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            LOG.entering(getClass().getName(), "onContinuedFromPreviousChanged", new Object[]{observable, oldValue, newValue});
            if (continuedFromPrevious != newValue) {
                LOG.finer(() -> String.format("continuedFromPrevious changing to %s", newValue));
                continuedFromPrevious = newValue;
                onSingleLineModeChanged();
            }
            LOG.exiting(getClass().getName(), "onContinuedFromPreviousChanged");
        }

        private synchronized void onContinuedOnNextChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            LOG.entering(getClass().getName(), "onContinuedOnNextChanged", new Object[]{observable, oldValue, newValue});
            if (continuedOnNext != newValue) {
                LOG.finer(() -> String.format("continuedOnNext changing to %s", newValue));
                continuedOnNext = newValue;
                onSingleLineModeChanged();
            }
            LOG.exiting(getClass().getName(), "onContinuedOnNextChanged");
        }

        private void onSingleLineModeChanged() {
            LOG.entering(getClass().getName(), "onSingleLineModeChanged");
            if (continuedFromPrevious) {
                LOG.finer("Restoring followsText; Collapsing withText and customerNameText");
                collapseNode(withText);
                collapseNode(customerNameText);
                restoreNode(followsText);
                if (continuedOnNext) {
                    LOG.finer("Restoring precedesText");
                    restoreNode(precedesText);
                } else {
                    LOG.finer("Collapsing precedesText");
                    collapseNode(precedesText);
                }
                if (singleLineMode) {
                    LOG.exiting(getClass().getName(), "onSingleLineModeChanged");
                    return;
                }
                singleLineMode = true;
                LOG.finer("singleLineMode changed to true");
            } else {
                LOG.finer("Collapsing followsText");
                collapseNode(followsText);
                if (continuedOnNext) {
                    LOG.finer("Restoring precedesText; Collapsing withText and customerNameText");
                    collapseNode(withText);
                    collapseNode(customerNameText);
                    restoreNode(precedesText);
                    if (singleLineMode) {
                        LOG.exiting(getClass().getName(), "onSingleLineModeChanged");
                        return;
                    }
                    singleLineMode = true;
                    LOG.finer("singleLineMode changed to true");
                } else {
                    LOG.finer("Restoring withText and customerNameText; Collapsing precedesText");
                    collapseNode(precedesText);
                    restoreNode(withText);
                    restoreNode(customerNameText);
                    if (forceSingleLineMode) {
                        if (singleLineMode) {
                            singleLineMode = false;
                            LOG.finer("singleLineMode changed to false");
                            setEffectiveSingleLineMode(true);
                            LOG.exiting(getClass().getName(), "onSingleLineModeChanged");
                            return;
                        }
                    } else {
                        if (singleLineMode) {
                            singleLineMode = false;
                            LOG.finer("Changing to multiple-line mode");
                            setPrefHeight(USE_COMPUTED_SIZE);
                            setMaxHeight(USE_COMPUTED_SIZE);
                            appointmentTitleText.setText(currentTitle);
                        }
                        setEffectiveSingleLineMode(false);
                        LOG.exiting(getClass().getName(), "onSingleLineModeChanged");
                        return;
                    }
                }
            }

            LOG.finer("Changing to single-line mode");
            setPrefHeight(SINGLE_LINE_HEIGHT);
            setMaxHeight(SINGLE_LINE_HEIGHT);
            appointmentTitleText.setText(Values.asNonNullAndWsNormalized(currentTitle));
            setEffectiveSingleLineMode(true);
            LOG.exiting(getClass().getName(), "onSingleLineModeChanged");
        }

        private void bindAll() {
            LOG.entering(getClass().getName(), "bindAll");
            boundModel.continuedFromPreviousProperty().addListener(continuedFromPreviousChangeListener.getWeakListener());
            boundModel.startProperty().addListener(startChangeListener.getWeakListener());
            boundModel.endProperty().addListener(endChangeListener.getWeakListener());
            boundModel.titleProperty().addListener(titleChangeListener.getWeakListener());
            boundModel.customerNameProperty().addListener(customerNameChangeListener.getWeakListener());
            boundModel.continuedOnNextProperty().addListener(continuedOnNextChangeListener.getWeakListener());
            LOG.exiting(getClass().getName(), "bindAll");
        }

        private void onModelChanged() {
            LOG.entering(getClass().getName(), "onModelChanged");
            currentTitle = boundModel.getTitle();
            continuedFromPrevious = boundModel.isContinuedFromPrevious();
            continuedOnNext = boundModel.isContinuedFromPrevious();
            onStartChanged(boundModel.endProperty(), null, boundModel.getStart());
            onEndChanged(boundModel.endProperty(), null, boundModel.getEnd());
            onCustomerNameChanged(boundModel.customerNameProperty(), "", boundModel.getCustomerName());
            LOG.exiting(getClass().getName(), "onModelChanged");
        }

        private void unbindAll() {
            LOG.entering(getClass().getName(), "unbindAll");
            boundModel.continuedFromPreviousProperty().removeListener(continuedFromPreviousChangeListener.getWeakListener());
            boundModel.startProperty().removeListener(startChangeListener.getWeakListener());
            boundModel.endProperty().removeListener(endChangeListener.getWeakListener());
            boundModel.titleProperty().removeListener(titleChangeListener.getWeakListener());
            boundModel.customerNameProperty().removeListener(customerNameChangeListener.getWeakListener());
            boundModel.continuedOnNextProperty().removeListener(continuedOnNextChangeListener.getWeakListener());
            LOG.exiting(getClass().getName(), "unbindAll");
        }

        private void resetAll() {
            LOG.entering(getClass().getName(), "resetAll");
            currentTitle = "";
            startDateTimeText.setText("");
            endDateTimeText.setText("");
            appointmentTitleText.setText("");
            customerNameText.setText("");
            if (continuedFromPrevious || continuedOnNext) {
                continuedFromPrevious = continuedOnNext = false;
                onSingleLineModeChanged();
            }
            LOG.exiting(getClass().getName(), "resetAll");
        }

    }

    private class LocationHBox extends HBox {

        private final Label locationHeadingLabel;
        private final Label locationValueLabel;
        private final Hyperlink urlHyperlink;
        private final WeakChangeHandlingReference<AppointmentType> typeChangeListener;
        private final WeakChangeHandlingReference<String> urlChangeListener;
        private final WeakChangeHandlingReference<String> locationChangeListener;
        private final WeakChangeHandlingReference<String> effectiveLocationChangeListener;
        private AppointmentType currentType = AppointmentType.OTHER;
        private String currentLocation = "";
        private String currentEffectiveLocation = "";

        LocationHBox() {
            LOG.entering(getClass().getName(), "<init>");
            locationHeadingLabel = NodeUtil.createLabel(CssClassName.BOLD_TEXT);
            locationValueLabel = NodeUtil.createLabel(CssClassName.SMALL_CONTROL);
            urlHyperlink = NodeUtil.createHyperlink(CssClassName.SMALL_CONTROL);
            typeChangeListener = WeakChangeHandlingReference.<AppointmentType>of(this::onTypeChanged);
            urlChangeListener = WeakChangeHandlingReference.<String>of(this::onUrlChanged);
            locationChangeListener = WeakChangeHandlingReference.<String>of(this::onLocationChanged);
            effectiveLocationChangeListener = WeakChangeHandlingReference.<String>of(this::onEffectiveLocationChanged);
            LOG.exiting(getClass().getName(), "<init>");
        }

        private void initialize() {
            LOG.entering(getClass().getName(), "initialize");
            NodeUtil.setMaxXY(NodeUtil.addCssClass(this, CssClassName.SMALL_CONTROL));
            setFillHeight(true);
            ObservableList<Node> children = getChildren();
            children.addAll(
                    locationHeadingLabel,
                    NodeUtil.setHBoxGrow(locationValueLabel, Priority.ALWAYS),
                    NodeUtil.setHBoxGrow(urlHyperlink, Priority.ALWAYS)
            );
            setCurrentType(currentType);
            LOG.exiting(getClass().getName(), "initialize");
        }

        public void setCurrentType(AppointmentType value) {
            LOG.entering(getClass().getName(), "setCurrentType", value);
            currentType = value;
            switch (currentType) {
                case VIRTUAL:
                    locationHeadingLabel.setText("URL: ");
                    restoreNode(urlHyperlink);
                    collapseNode(locationValueLabel);
                    break;
                case PHONE:
                    locationHeadingLabel.setText("Phone: ");
                    applyPhoneText();
                    break;
                default:
                    locationHeadingLabel.setText("Location: ");
                    restoreNode(locationValueLabel);
                    collapseNode(urlHyperlink);
                    locationValueLabel.setText(currentEffectiveLocation);
                    break;
            }
            LOG.exiting(getClass().getName(), "setCurrentType");
        }

        private void applyPhoneText() {
            LOG.entering(getClass().getName(), "applyPhoneText");
            try {
                URI uri = new URI("tel:" + currentLocation);
                restoreNode(urlHyperlink);
                LOG.finer(() -> String.format("Sertting urlHyperlink to %s", uri.toString()));
                urlHyperlink.setText(uri.toString());
                collapseNode(locationValueLabel);
            } catch (URISyntaxException ex) {
                LOG.log(Level.FINE, "Error creating URI from telephone number", ex);
                restoreNode(locationValueLabel);
                collapseNode(urlHyperlink);
                locationValueLabel.setText(currentEffectiveLocation);
            }
            LOG.exiting(getClass().getName(), "applyPhoneText");
        }

        private synchronized void onTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
            LOG.entering(getClass().getName(), "onTypeChanged", new Object[]{observable, oldValue, newValue});
            locationHBox.setCurrentType((null == newValue) ? AppointmentType.OTHER : newValue);
            LOG.exiting(getClass().getName(), "onTypeChanged");
        }

        private synchronized void onUrlChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            LOG.entering(getClass().getName(), "onUrlChanged", new Object[]{observable, oldValue, newValue});
            urlHyperlink.setText((null == newValue) ? "" : newValue);
            LOG.exiting(getClass().getName(), "onUrlChanged");
        }

        private synchronized void onLocationChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            LOG.entering(getClass().getName(), "onLocationChanged", new Object[]{observable, oldValue, newValue});
            currentLocation = (null == newValue) ? "" : newValue;
            if (currentType == AppointmentType.PHONE) {
                applyPhoneText();
            }
            LOG.exiting(getClass().getName(), "onLocationChanged");
        }

        private synchronized void onEffectiveLocationChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            LOG.entering(getClass().getName(), "onEffectiveLocationChanged", new Object[]{observable, oldValue, newValue});
            currentEffectiveLocation = (null == newValue) ? "" : newValue;
            switch (currentType) {
                case PHONE:
                case VIRTUAL:
                    break;
                default:
                    LOG.finer("Updating currentEffectiveLocation");
                    locationValueLabel.setText(currentEffectiveLocation);
                    break;
            }
            LOG.exiting(getClass().getName(), "onEffectiveLocationChanged");
        }

        private void bindAll() {
            LOG.entering(getClass().getName(), "bindAll");
            boundModel.typeProperty().addListener(typeChangeListener.getWeakListener());
            boundModel.urlProperty().addListener(urlChangeListener.getWeakListener());
            boundModel.locationProperty().addListener(locationChangeListener.getWeakListener());
            boundModel.effectiveLocationProperty().addListener(effectiveLocationChangeListener.getWeakListener());
            LOG.exiting(getClass().getName(), "bindAll");
        }

        private void onModelChanged() {
            LOG.entering(getClass().getName(), "onModelChanged");
            setCurrentType(boundModel.getType());
            onUrlChanged(boundModel.urlProperty(), "", boundModel.getUrl());
            onLocationChanged(boundModel.locationProperty(), "", boundModel.getLocation());
            onEffectiveLocationChanged(boundModel.effectiveLocationProperty(), "", boundModel.getEffectiveLocation());
            LOG.exiting(getClass().getName(), "onModelChanged");
        }

        private void unbindAll() {
            LOG.entering(getClass().getName(), "unbindAll");
            boundModel.typeProperty().removeListener(typeChangeListener.getWeakListener());
            boundModel.urlProperty().removeListener(urlChangeListener.getWeakListener());
            boundModel.locationProperty().removeListener(locationChangeListener.getWeakListener());
            boundModel.effectiveLocationProperty().removeListener(effectiveLocationChangeListener.getWeakListener());
            LOG.exiting(getClass().getName(), "unbindAll");
        }

        private void resetAll() {
            LOG.entering(getClass().getName(), "resetAll");
            currentLocation = currentEffectiveLocation = "";
            locationValueLabel.setText("");
            urlHyperlink.setText("");
            LOG.exiting(getClass().getName(), "resetAll");
        }

    }

}
