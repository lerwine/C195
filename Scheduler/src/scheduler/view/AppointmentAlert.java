package scheduler.view;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import scheduler.AppResources;
import static scheduler.Scheduler.getCurrentUser;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.AppointmentElement;
import scheduler.dao.AppointmentType;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.util.DB;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.setBorderedNode;
import static scheduler.util.NodeUtil.setLeftControlLabel;
import static scheduler.util.NodeUtil.setLeftLabeledControl;
import static scheduler.view.MainResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModel;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/AppointmentAlert.fxml")
public class AppointmentAlert {

    private static final Logger LOG = Logger.getLogger(AppointmentAlert.class.getName());

    private static final String NODE_PROPERTYNAME_ALERT_MODEL = "scheduler.view.MainController.AppointmentAlerts.model";
    private static final String NODE_PROPERTYNAME_ALERT_TITLE = "scheduler.view.MainController.AppointmentAlerts.title";
    private static final String NODE_PROPERTYNAME_ALERT_START = "scheduler.view.MainController.AppointmentAlerts.start";
    private static final String NODE_PROPERTYNAME_ALERT_END = "scheduler.view.MainController.AppointmentAlerts.end";
    private static final String NODE_PROPERTYNAME_ALERT_TYPE = "scheduler.view.MainController.AppointmentAlerts.type";
    private static final String NODE_PROPERTYNAME_ALERT_CUSTOMER = "scheduler.view.MainController.AppointmentAlerts.customer";
    private static final String NODE_PROPERTYNAME_ALERT_LOCATION = "scheduler.view.MainController.AppointmentAlerts.location";

    private Timer appointmentCheckTimer;
    private List<Integer> dismissed;
    private int alertLeadtime;
    private DateTimeFormatter formatter;
    private Pane parent;
    
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootBorderPane"
    private BorderPane rootBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentAlertsVBox"
    private VBox appointmentAlertsVBox; // Value injected by FXMLLoader

    @FXML
    private void onDismissAllAppointmentAlerts(ActionEvent event) {
        dismissAll();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'AppointmentAlert.fxml'.";
        assert appointmentAlertsVBox != null : "fx:id=\"appointmentAlertsVBox\" was not injected: check your FXML file 'AppointmentAlert.fxml'.";
    }

    void initialize(Pane parent) {
        this.parent = parent;
        rootBorderPane.setVisible(false);
        parent.getChildren().add(rootBorderPane);
        rootBorderPane.prefWidthProperty().bind(parent.widthProperty());
        rootBorderPane.minWidthProperty().bind(parent.widthProperty());
        rootBorderPane.prefHeightProperty().bind(parent.heightProperty());
        rootBorderPane.minHeightProperty().bind(parent.heightProperty());
        try {
            alertLeadtime = AppResources.getAppointmentAlertLeadTime();
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            alertLeadtime = 15;
        }
        dismissed = Collections.synchronizedList(new ArrayList<>());
        appointmentCheckTimer = new Timer();
        appointmentCheckTimer.schedule(new CheckAppointmentsTask(alertLeadtime), 0, 120000);
        formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        
    }

    private FlowPane createNew(AppointmentModel model) {
        FlowPane view = setBorderedNode(new FlowPane());
        view.setPadding(new Insets(8));
        ObservableList<Node> rootChildren = view.getChildren();
        ObservableMap<Object, Object> properties = view.getProperties();
        properties.put(NODE_PROPERTYNAME_ALERT_MODEL, model);
        HBox hBox = new HBox();
        rootChildren.add(hBox);
        ObservableList<Node> children = hBox.getChildren();
        children.add(setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_TITLE)));
        Label label = setLeftLabeledControl(new Label(), true);
        label.textProperty().bind(model.titleProperty());
        children.add(label);
        properties.put(NODE_PROPERTYNAME_ALERT_TITLE, label.textProperty());

        hBox = new HBox();
        rootChildren.add(hBox);
        children = hBox.getChildren();
        label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_START));
        label.setPadding(new Insets(0, 0, 0, 8));
        children.add(label);
        label = setLeftLabeledControl(new Label(), true);
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            LocalDateTime d = model.getStart();
            return (null == d) ? "" : formatter.format(d);
        }, model.startProperty()));
        children.add(label);
        properties.put(NODE_PROPERTYNAME_ALERT_START, label.textProperty());

        hBox = new HBox();
        rootChildren.add(hBox);
        children = hBox.getChildren();
        label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_END));
        label.setPadding(new Insets(0, 0, 0, 8));
        children.add(label);
        label = setLeftLabeledControl(new Label(), true);
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            LocalDateTime d = model.getEnd();
            return (null == d) ? "" : formatter.format(d);
        }, model.endProperty()));
        children.add(label);
        properties.put(NODE_PROPERTYNAME_ALERT_END, label.textProperty());

        hBox = new HBox();
        rootChildren.add(hBox);
        children = hBox.getChildren();
        label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_TYPE));
        label.setPadding(new Insets(0, 0, 0, 8));
        children.add(label);
        label = setLeftLabeledControl(new Label(), true);
        label.textProperty().bind(Bindings.createStringBinding(() -> AppointmentType.toDisplayText(model.getType()), model.typeProperty()));
        children.add(label);
        properties.put(NODE_PROPERTYNAME_ALERT_TYPE, label.textProperty());

        hBox = new HBox();
        rootChildren.add(hBox);
        children = hBox.getChildren();
        label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_CUSTOMER));
        label.setPadding(new Insets(0, 0, 0, 8));
        children.add(label);
        label = setLeftLabeledControl(new Label(), true);
        label.textProperty().bind(model.customerNameProperty());
        children.add(label);
        properties.put(NODE_PROPERTYNAME_ALERT_CUSTOMER, label.textProperty());

        hBox = new HBox();
        rootChildren.add(hBox);
        children = hBox.getChildren();
        label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_LOCATION));
        label.setPadding(new Insets(0, 0, 0, 8));
        children.add(label);
        label = setLeftLabeledControl(new Label(), true);
        label.textProperty().bind(model.effectiveLocationProperty());
        children.add(label);
        properties.put(NODE_PROPERTYNAME_ALERT_LOCATION, label.textProperty());

        Button button = new Button();
        button.setPadding(new Insets(0, 0, 0, 8));
        rootChildren.add(button);
        button.setText(AppResources.getResourceString(RESOURCEKEY_DISMISS));
        button.setOnAction((event) -> dismiss((FlowPane) ((Button) event.getSource()).getParent()));
        return view;
    }

    private void reBind(FlowPane view, AppointmentModel model) {
        StringProperty stringProperty;
        ObservableMap<Object, Object> properties = view.getProperties();
        if (null == model) {
            properties.remove(NODE_PROPERTYNAME_ALERT_MODEL);
            ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TITLE)).unbind();
            properties.remove(NODE_PROPERTYNAME_ALERT_TITLE);
            ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_START)).unbind();
            properties.remove(NODE_PROPERTYNAME_ALERT_START);
            ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_END)).unbind();
            properties.remove(NODE_PROPERTYNAME_ALERT_END);
            ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TYPE)).unbind();
            properties.remove(NODE_PROPERTYNAME_ALERT_TYPE);
            ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_CUSTOMER)).unbind();
            properties.remove(NODE_PROPERTYNAME_ALERT_CUSTOMER);
            ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_LOCATION)).unbind();
            properties.remove(NODE_PROPERTYNAME_ALERT_LOCATION);
        } else {
            properties.put(NODE_PROPERTYNAME_ALERT_MODEL, model);
            stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TITLE);
            stringProperty.unbind();
            stringProperty.bind(model.titleProperty());

            stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_START);
            stringProperty.unbind();
            stringProperty.bind(Bindings.createStringBinding(() -> {
                LocalDateTime d = model.getStart();
                return (null == d) ? "" : formatter.format(d);
            }, model.startProperty()));

            stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_END);
            stringProperty.unbind();
            stringProperty.bind(Bindings.createStringBinding(() -> {
                LocalDateTime d = model.getEnd();
                return (null == d) ? "" : formatter.format(d);
            }, model.endProperty()));

            stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TYPE);
            stringProperty.unbind();
            stringProperty.bind(Bindings.createStringBinding(() -> AppointmentType.toDisplayText(model.getType()), model.typeProperty()));

            stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_CUSTOMER);
            stringProperty.unbind();
            stringProperty.bind(model.customerNameProperty());

            stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_LOCATION);
            stringProperty.unbind();
            stringProperty.bind(model.effectiveLocationProperty());
        }
    }

    private synchronized void dismissAll() {
        ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
        if (!itemsViewList.isEmpty()) {
            itemsViewList.forEach((t) -> {
                FlowPane f = (FlowPane) t;
                ObservableList<Node> children = f.getChildren();
                ObservableMap<Object, Object> properties = f.getProperties();
                dismissed.add(((AppointmentModel) properties.get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey());
                reBind(f, null);
                children.clear();
            });
            itemsViewList.clear();
            rootBorderPane.setVisible(false);
        }
    }

    private synchronized void dismiss(FlowPane flowPane) {
        ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
        dismissed.add(((AppointmentModel) flowPane.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey());
        reBind(flowPane, null);
        itemsViewList.remove(flowPane);
        if (itemsViewList.isEmpty()) {
            rootBorderPane.setVisible(false);
        }
    }

    private FlowPane getViewNode(int key) {
        Optional<Node> result = appointmentAlertsVBox.getChildren().stream().filter((t)
                -> ((AppointmentModel) t.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey() == key).findFirst();
        return (FlowPane) result.orElse((Node) null);
    }

    void shutdown() {
        appointmentCheckTimer.cancel();
    }

    private synchronized void onCheckAppointmentsTaskError(Throwable ex) {
        appointmentCheckTimer.cancel();
        try {
            ErrorDetailDialog.showAndWait(resources.getString(RESOURCEKEY_APPOINTMENTLOADERROR), ex,
                    resources.getString(RESOURCEKEY_ERRORCHECKINGIMPENDINGAPPOINTMENTS));
        } finally {
            appointmentCheckTimer = new Timer();
            appointmentCheckTimer.schedule(new CheckAppointmentsTask(alertLeadtime), 0, 120000);
        }
    }

    private synchronized void onPeriodicCheckFinished(List<AppointmentDAO> appointments) {
        ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
        if (appointments.isEmpty()) {
            itemsViewList.forEach((t) -> {
                reBind((FlowPane) t, null);
            });
            itemsViewList.clear();
        } else {
            ArrayList<Integer> d = new ArrayList<>();
            dismissed.forEach((i) -> d.add(i));
            dismissed.clear();
            appointments.stream().sorted(AppointmentElement::compareByDates).forEach(new Consumer<AppointmentDAO>() {
                int index = -1;

                @Override
                public void accept(AppointmentDAO t) {
                    int pk = t.getPrimaryKey();
                    if (d.contains(pk)) {
                        dismissed.add(pk);
                    } else if (++index < itemsViewList.size()) {
                        reBind((FlowPane) itemsViewList.get(index), new AppointmentModel(t));
                    } else {
                        itemsViewList.add(createNew(new AppointmentModel(t)));
                    }
                }
            });
            int e = appointments.size() - dismissed.size();
            while (itemsViewList.size() > e) {
                reBind((FlowPane) itemsViewList.get(e), null);
                itemsViewList.remove(e);
            }
        }
        if (itemsViewList.isEmpty()) {
            rootBorderPane.setVisible(false);
            return;
        }
        ObservableList<Node> children = parent.getChildren();
        if (children.indexOf(rootBorderPane) != children.size() - 1) {
            children.remove(rootBorderPane);
            children.add(rootBorderPane);
        }
        rootBorderPane.setVisible(true);
    }

    private synchronized void onAppointmentUpdate(AppointmentModel item) {
        int key = item.getPrimaryKey();
        ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
        LocalDateTime start = LocalDateTime.now();
        FlowPane view = getViewNode(key);
        if (start.compareTo(item.getEnd()) < 0) {
            LocalDateTime end = start.plusMinutes(alertLeadtime);
            if (end.compareTo(item.getStart()) >= 0) {
                if (dismissed.contains(key)) {
                    return;
                }
                Stream<AppointmentModel> stream = itemsViewList.stream().map((t) -> (AppointmentModel) t.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL));
                if (null != view) {
                    stream = stream.filter((t) -> t.getPrimaryKey() != key);
                }
                Stream.concat(stream, Stream.of(item)).sorted(AppointmentModel::compareByDates).forEach(new Consumer<AppointmentModel>() {
                    int index = -1;

                    @Override
                    public void accept(AppointmentModel t) {
                        if (++index < itemsViewList.size()) {
                            reBind((FlowPane) itemsViewList.get(index), t);
                        } else {
                            itemsViewList.add(createNew(t));
                        }
                    }
                });
                return;
            }
        }
        if (dismissed.contains(key)) {
            dismissed.remove(key);
        } else if (null != view) {
            reBind(view, null);
            itemsViewList.remove(view);
            if (itemsViewList.isEmpty()) {
                rootBorderPane.setVisible(false);
            }
        }
    }

    private synchronized void onAppointmentDeleted(AppointmentModel item) {
        int pk = item.getPrimaryKey();
        if (dismissed.contains(pk)) {
            dismissed.remove(pk);
        } else {
            FlowPane view = getViewNode(pk);
            if (null != view) {
                ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
                reBind(view, null);
                itemsViewList.remove(view);
                if (itemsViewList.isEmpty()) {
                    rootBorderPane.setVisible(false);
                }
            }
        }
    }

    private class CheckAppointmentsTask extends TimerTask {

        private final UserDAO user;
        private final AppointmentDAO.FactoryImpl factory;
        private final int alertLeadTime;

        private CheckAppointmentsTask(int alertLeadTime) {
            this.alertLeadTime = alertLeadTime;
            user = Objects.requireNonNull(getCurrentUser());
            factory = AppointmentDAO.getFactory();
        }

        @Override
        public void run() {
            List<AppointmentDAO> appointments;
            try {
                appointments = DbConnector.apply((t) -> {
                    LocalDateTime start = LocalDateTime.now();
                    return factory.load(t, AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(start),
                            DB.toUtcTimestamp(start.plusMinutes(alertLeadTime))).and(AppointmentFilter.expressionOf(user))));
                });
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error checking impending appointments", ex);
                Platform.runLater(() -> onCheckAppointmentsTaskError(ex));
                return;
            }
            Platform.runLater(() -> onPeriodicCheckFinished(appointments));
        }
    }

}
