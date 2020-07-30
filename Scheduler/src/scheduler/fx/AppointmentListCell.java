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
import scheduler.model.fx.AppointmentModel;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Values;
import scheduler.util.WeakChangeHandlingReference;
import scheduler.view.SymbolText;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentListCell extends ListCell<AppointmentModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentListCell.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentListCell.class.getName());

    private static final float SINGLE_LINE_HEIGHT;

    static {
        Text text = new Text();
        Font font = text.getFont();
        final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        SINGLE_LINE_HEIGHT = fm.getLineHeight();
        LOG.finer(() -> String.format("SINGLE_LINE_HEIGHT = %f", SINGLE_LINE_HEIGHT));
    }

    private final WeakReference<AppointmentListCellFactory> cellFactory;
    private final DateTimeFormatter formatter;
    private final HBox graphic;
    private final OverviewTextFlow overviewTextFlow;
    private final LocationHBox locationHBox;
    private final Button openButton;
    private final Button dismissButton;
    private final ChangeListener<Boolean> singleLineChangeListener;
    private boolean extentsBound = false;
    private AppointmentModel boundModel;
    private boolean singleLineMode = false;

    public AppointmentListCell(AppointmentListCellFactory factory) {
        LOG.entering(getClass().getName(), "<init>", factory);
        cellFactory = new WeakReference<>(factory);
        formatter = Objects.requireNonNull(factory.getFormatter());
        BooleanProperty singleLine = factory.singleLineProperty();
        overviewTextFlow = new OverviewTextFlow();
        locationHBox = new LocationHBox();
        openButton = NodeUtil.createSymbolButton(SymbolText.EDIT, this::onOpenButtonAction);
        dismissButton = NodeUtil.createSymbolButton(SymbolText.DISMISS, this::onDismissButtonAction);
        singleLineChangeListener = this::onSingleLineChanged;
        overviewTextFlow.initialize();
        locationHBox.initialize();
        singleLineMode = singleLine.get();
        overviewTextFlow.onSingleLineModeChanged();

        singleLine.addListener(new WeakChangeListener<>(singleLineChangeListener));
        graphic = NodeUtil.createFillingHBox(
                NodeUtil.setHBoxGrow(NodeUtil.createFillingVBox(
                        NodeUtil.setVBoxGrow(overviewTextFlow, Priority.ALWAYS),
                        locationHBox
                ), Priority.ALWAYS),
                NodeUtil.setHBoxGrow(openButton, Priority.NEVER),
                NodeUtil.setHBoxGrow(dismissButton, Priority.NEVER)
        );
        LOG.exiting(getClass().getName(), "<init>");
    }

    @Override
    protected synchronized void updateItem(AppointmentModel item, boolean empty) {
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
        AppointmentListCellFactory factory = cellFactory.get();
        if (null != factory) {
            tail = factory.buildEventDispatchChain(tail);
        }
        tail = super.buildEventDispatchChain(tail);
        LOG.exiting(getClass().getName(), "buildEventDispatchChain", tail);
        return tail;
    }

    private void onOpenButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onOpenButtonAction", event);
        AppointmentModel item = getItem();
        if (null != item) {
            AppointmentOpRequestEvent e = new AppointmentOpRequestEvent(item, event.getSource(), false);
            LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
            fireEvent(e);
        }
        LOG.exiting(getClass().getName(), "onOpenButtonAction");
    }

    private void onDismissButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onDismissButtonAction", event);
        AppointmentModel item = getItem();
        if (null != item) {
            AppointmentOpRequestEvent e = new AppointmentOpRequestEvent(item, event.getSource(), true);
            LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
            fireEvent(e);
        }
        LOG.exiting(getClass().getName(), "onDismissButtonAction");
    }

    private synchronized void onSingleLineChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        LOG.entering(getClass().getName(), "onSingleLineChanged", new Object[]{observable, oldValue, newValue});
        if (singleLineMode != newValue) {
            LOG.finer(() -> String.format("singleLineMode changing to %s", newValue));
            singleLineMode = newValue;
            if (singleLineMode) {
                LOG.finer("Restoring locationHBox");
                restoreNode(locationHBox);
            } else {
                LOG.finer("Collapsing locationHBox");
                collapseNode(locationHBox);
            }
            overviewTextFlow.onSingleLineModeChanged();
        }
        LOG.exiting(getClass().getName(), "onSingleLineChanged");
    }

    private void unbindAll() {
        overviewTextFlow.unbindAll();
        locationHBox.unbindAll();
    }

    private class OverviewTextFlow extends TextFlow {

        private final Text startDateTimeText;
        private final Text endDateTimeText;
        private final Text customerNameText;
        private final Text appointmentTitleText;
        private final WeakChangeHandlingReference<LocalDateTime> startChangeListener;
        private final WeakChangeHandlingReference<LocalDateTime> endChangeListener;
        private final WeakChangeHandlingReference<String> titleChangeListener;
        private final WeakChangeHandlingReference<String> customerNameChangeListener;
        private String currentTitle = "";

        OverviewTextFlow() {
            LOG.entering(getClass().getName(), "<init>");
            startDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            endDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            customerNameText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            appointmentTitleText = NodeUtil.createText();
            startChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onStartChanged);
            endChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onEndChanged);
            customerNameChangeListener = WeakChangeHandlingReference.<String>of(this::onCustomerNameChanged);
            titleChangeListener = WeakChangeHandlingReference.<String>of(this::onTitleChanged);
            LOG.exiting(getClass().getName(), "<init>");
        }

        private void initialize() {
            LOG.entering(getClass().getName(), "initialize");
            ObservableList<Node> children = getChildren();
            children.addAll(
                    startDateTimeText,
                    NodeUtil.createText(" to "),
                    endDateTimeText,
                    NodeUtil.createText(" with "),
                    customerNameText,
                    NodeUtil.createText(": "),
                    appointmentTitleText
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

        private void onSingleLineModeChanged() {
            LOG.entering(getClass().getName(), "onSingleLineModeChanged");
            LOG.finer("Restoring withText and customerNameText");
            if (singleLineMode) {
                setPrefHeight(SINGLE_LINE_HEIGHT);
                setMaxHeight(SINGLE_LINE_HEIGHT);
                appointmentTitleText.setText(Values.asNonNullAndWsNormalized(currentTitle));
            } else {
                setPrefHeight(USE_COMPUTED_SIZE);
                setMaxHeight(USE_COMPUTED_SIZE);
                appointmentTitleText.setText(currentTitle);
            }

            LOG.exiting(getClass().getName(), "onSingleLineModeChanged");
        }

        private void bindAll() {
            LOG.entering(getClass().getName(), "bindAll");
            boundModel.startProperty().addListener(startChangeListener.getWeakListener());
            boundModel.endProperty().addListener(endChangeListener.getWeakListener());
            boundModel.titleProperty().addListener(titleChangeListener.getWeakListener());
            boundModel.customerNameProperty().addListener(customerNameChangeListener.getWeakListener());
            LOG.exiting(getClass().getName(), "bindAll");
        }

        private void onModelChanged() {
            LOG.entering(getClass().getName(), "onModelChanged");
            currentTitle = boundModel.getTitle();
            onStartChanged(boundModel.endProperty(), null, boundModel.getStart());
            onEndChanged(boundModel.endProperty(), null, boundModel.getEnd());
            onCustomerNameChanged(boundModel.customerNameProperty(), "", boundModel.getCustomerName());
            LOG.exiting(getClass().getName(), "onModelChanged");
        }

        private void unbindAll() {
            LOG.entering(getClass().getName(), "unbindAll");
            boundModel.startProperty().removeListener(startChangeListener.getWeakListener());
            boundModel.endProperty().removeListener(endChangeListener.getWeakListener());
            boundModel.titleProperty().removeListener(titleChangeListener.getWeakListener());
            boundModel.customerNameProperty().removeListener(customerNameChangeListener.getWeakListener());
            LOG.exiting(getClass().getName(), "unbindAll");
        }

        private void resetAll() {
            LOG.entering(getClass().getName(), "resetAll");
            currentTitle = "";
            startDateTimeText.setText("");
            endDateTimeText.setText("");
            appointmentTitleText.setText("");
            customerNameText.setText("");
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
