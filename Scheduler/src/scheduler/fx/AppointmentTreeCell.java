package scheduler.fx;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventDispatchChain;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.model.AppointmentType;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.WeakChangeHandlingReference;
import scheduler.view.SymbolText;
import scheduler.view.appointment.AppointmentDay;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTreeCell extends TreeCell<AppointmentDay> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentTreeCell.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentTreeCell.class.getName());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eeee, d", Locale.getDefault(Locale.Category.FORMAT)).withZone(ZoneId.systemDefault());

    private final WeakReference<AppointmentTreeCellFactory> cellFactory;
    private final DateTimeFormatter formatter;
    private final VBox branchGraphic;
    private final HBox leafGraphic;
    private final OverviewTextFlow overviewTextFlow;
    private final LocationHBox locationHBox;
    private final Button editButton;
    private final Button deleteButton;
    private AppointmentDay boundModel;
    private Region currentGraphic = null;

    public AppointmentTreeCell(AppointmentTreeCellFactory factory) {
        LOG.entering(getClass().getName(), "<init>", factory);
        cellFactory = new WeakReference<>(factory);
        formatter = Objects.requireNonNull(factory.getFormatter());
        overviewTextFlow = new OverviewTextFlow();
        locationHBox = new LocationHBox();
        editButton = NodeUtil.createSymbolButton(SymbolText.EDIT, this::onOpenButtonAction);
        editButton.setMinWidth(USE_PREF_SIZE);
        editButton.setMinHeight(USE_PREF_SIZE);
        deleteButton = NodeUtil.createSymbolButton(SymbolText.DELETE, this::onDismissButtonAction);
        deleteButton.setMinWidth(USE_PREF_SIZE);
        deleteButton.setMinHeight(USE_PREF_SIZE);
        overviewTextFlow.initialize();
        locationHBox.initialize();

        branchGraphic = NodeUtil.setHBoxGrow(NodeUtil.createFillingVBox(
                NodeUtil.setVBoxGrow(overviewTextFlow, Priority.ALWAYS),
                locationHBox
        ), Priority.ALWAYS);
        leafGraphic = NodeUtil.createFillingHBox(
                branchGraphic,
                NodeUtil.setHBoxGrow(editButton, Priority.NEVER),
                NodeUtil.setHBoxGrow(deleteButton, Priority.NEVER)
        );
        LOG.exiting(getClass().getName(), "<init>");
    }

    @Override
    protected void updateItem(AppointmentDay item, boolean empty) {
        LOG.entering(getClass().getName(), "updateItem", new Object[]{item, empty});
        super.updateItem(item, empty);
        if (empty || null == item) {
            LOG.finer("Initializing empty cell");
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
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
            if (null == item.getModel()) {
                if (currentGraphic == null || currentGraphic == leafGraphic) {
                    leafGraphic.getChildren().remove(branchGraphic);
                }
                NodeUtil.removeCssClass(leafGraphic, CssClassName.FIRST_ITEM);
                NodeUtil.addCssClass(branchGraphic, CssClassName.DAY_GROUP);
                currentGraphic = branchGraphic;
            } else {
                if (null != currentGraphic && currentGraphic == branchGraphic) {
                    leafGraphic.getChildren().add(0, branchGraphic);
                }
                NodeUtil.removeCssClass(branchGraphic, CssClassName.DAY_GROUP);
                if (item.isFirstItem()) {
                    NodeUtil.addCssClass(leafGraphic, CssClassName.FIRST_ITEM);
                } else {
                    NodeUtil.removeCssClass(leafGraphic, CssClassName.FIRST_ITEM);
                }
                currentGraphic = leafGraphic;
            }
            setGraphic(currentGraphic);
            if (null != boundModel) {
                if (boundModel == item) {
                    LOG.finer("Model item properties already bound");
                    if (getIndex() == 0) {
                        currentGraphic.setPadding(new Insets(0, 0, 0, 16));
                    } else {
                        currentGraphic.setPadding(Insets.EMPTY);
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
            overviewTextFlow.bindAll();
            locationHBox.bindAll();
        }
        LOG.exiting(getClass().getName(), "updateIndex");
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(getClass().getName(), "buildEventDispatchChain", tail);
        AppointmentTreeCellFactory factory = cellFactory.get();
        if (null != factory) {
            tail = factory.buildEventDispatchChain(tail);
        }
        tail = super.buildEventDispatchChain(tail);
        LOG.exiting(getClass().getName(), "buildEventDispatchChain", tail);
        return tail;
    }

    private void onOpenButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onOpenButtonAction", event);
        AppointmentDay item = getItem();
        if (null != item) {
            AppointmentModel model = item.getModel();
            if (null != model) {
                AppointmentOpRequestEvent e = new AppointmentOpRequestEvent(model, event.getSource(), false);
                LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
                fireEvent(e);
            }
        }
        LOG.exiting(getClass().getName(), "onOpenButtonAction");
    }

    private void onDismissButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onDismissButtonAction", event);
        AppointmentDay item = getItem();
        if (null != item) {
            AppointmentModel model = item.getModel();
            if (null != model) {
                AppointmentOpRequestEvent e = new AppointmentOpRequestEvent(model, event.getSource(), true);
                LOG.fine(() -> String.format("Firing %s%n\ton %s", e, getClass().getName()));
                fireEvent(e);
            }
        }
        LOG.exiting(getClass().getName(), "onDismissButtonAction");
    }

    private void unbindAll() {
        overviewTextFlow.unbindAll();
        locationHBox.unbindAll();
    }

    private class OverviewTextFlow extends TextFlow {

        private final Text startDateTimeText;
        private final Text toText;
        private final Text endDateTimeText;
        private final Text withText;
        private final Text customerNameText;
        private final Text colonText;
        private final Text appointmentTitleText;
        private final WeakChangeHandlingReference<LocalDateTime> startChangeListener;
        private final WeakChangeHandlingReference<LocalDateTime> endChangeListener;
        private final WeakChangeHandlingReference<String> titleChangeListener;
        private final WeakChangeHandlingReference<String> customerNameChangeListener;

        OverviewTextFlow() {
            LOG.entering(getClass().getName(), "<init>");
            startDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            toText = NodeUtil.createText(" to ");
            endDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            withText = NodeUtil.createText(" with ");
            customerNameText = NodeUtil.createText(CssClassName.BOLD_TEXT);
            colonText = NodeUtil.createText(": ");
            appointmentTitleText = NodeUtil.createText();
            startChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onStartChanged);
            endChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onEndChanged);
            customerNameChangeListener = WeakChangeHandlingReference.<String>of(this::onCustomerNameChanged);
            titleChangeListener = WeakChangeHandlingReference.<String>of(this::onTitleChanged);
            LOG.exiting(getClass().getName(), "<init>");
        }

        private void initialize() {
            LOG.entering(getClass().getName(), "initialize");
            NodeUtil.addCssClass(this, CssClassName.SMALL_CONTROL);
            ObservableList<Node> children = getChildren();
            children.addAll(
                    startDateTimeText,
                    toText,
                    endDateTimeText,
                    withText,
                    customerNameText,
                    colonText,
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
            appointmentTitleText.setText((null == newValue) ? "" : newValue);
            LOG.exiting(getClass().getName(), "onTitleChanged");
        }

        private void bindAll() {
            LOG.entering(getClass().getName(), "bindAll");
            AppointmentModel model = boundModel.getModel();
            if (null != model) {
                model.startProperty().addListener(startChangeListener.getWeakListener());
                model.endProperty().addListener(endChangeListener.getWeakListener());
                model.titleProperty().addListener(titleChangeListener.getWeakListener());
                model.customerNameProperty().addListener(customerNameChangeListener.getWeakListener());
            }
            LOG.exiting(getClass().getName(), "bindAll");
        }

        private void onModelChanged() {
            LOG.entering(getClass().getName(), "onModelChanged");
            AppointmentModel model = boundModel.getModel();
            if (null != model) {
                NodeUtil.restoreNodes(toText, endDateTimeText, withText, customerNameText, colonText, appointmentTitleText);
                appointmentTitleText.setText(model.getTitle());
                onStartChanged(model.startProperty(), null, model.getStart());
                onEndChanged(model.endProperty(), null, model.getEnd());
                onCustomerNameChanged(model.customerNameProperty(), "", model.getCustomerName());
            } else {
                endDateTimeText.setText("");
                customerNameText.setText("");
                appointmentTitleText.setText("");
                NodeUtil.collapseNodes(toText, endDateTimeText, withText, customerNameText, colonText, appointmentTitleText);
                startDateTimeText.setText(FORMATTER.format(boundModel.getDate()));
            }
            LOG.exiting(getClass().getName(), "onModelChanged");
        }

        private void unbindAll() {
            LOG.entering(getClass().getName(), "unbindAll");
            AppointmentModel model = boundModel.getModel();
            if (null != model) {
                model.startProperty().removeListener(startChangeListener.getWeakListener());
                model.endProperty().removeListener(endChangeListener.getWeakListener());
                model.titleProperty().removeListener(titleChangeListener.getWeakListener());
                model.customerNameProperty().removeListener(customerNameChangeListener.getWeakListener());
            }
            LOG.exiting(getClass().getName(), "unbindAll");
        }

        private void resetAll() {
            LOG.entering(getClass().getName(), "resetAll");
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
            locationHeadingLabel = NodeUtil.createLabel(CssClassName.BOLD_TEXT, CssClassName.SMALL_CONTROL);
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
            AppointmentModel model = boundModel.getModel();
            if (null != model) {
                model.typeProperty().addListener(typeChangeListener.getWeakListener());
                model.urlProperty().addListener(urlChangeListener.getWeakListener());
                model.locationProperty().addListener(locationChangeListener.getWeakListener());
                model.effectiveLocationProperty().addListener(effectiveLocationChangeListener.getWeakListener());
            }
            LOG.exiting(getClass().getName(), "bindAll");
        }

        private void onModelChanged() {
            LOG.entering(getClass().getName(), "onModelChanged");
            AppointmentModel model = boundModel.getModel();
            if (null != model) {
                restoreNode(this);
                setCurrentType(model.getType());
                onUrlChanged(model.urlProperty(), "", model.getUrl());
                onLocationChanged(model.locationProperty(), "", model.getLocation());
                onEffectiveLocationChanged(model.effectiveLocationProperty(), "", model.getEffectiveLocation());
            } else {
                collapseNode(this);
                currentLocation = currentEffectiveLocation = "";
                locationValueLabel.setText("");
                urlHyperlink.setText("");
            }
            LOG.exiting(getClass().getName(), "onModelChanged");
        }

        private void unbindAll() {
            LOG.entering(getClass().getName(), "unbindAll");
            AppointmentModel model = boundModel.getModel();
            if (null != model) {
                model.typeProperty().removeListener(typeChangeListener.getWeakListener());
                model.urlProperty().removeListener(urlChangeListener.getWeakListener());
                model.locationProperty().removeListener(locationChangeListener.getWeakListener());
                model.effectiveLocationProperty().removeListener(effectiveLocationChangeListener.getWeakListener());
            }
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
