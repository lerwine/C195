package scheduler.fx;

import java.io.IOException;
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
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import scheduler.AppResources;
import static scheduler.Scheduler.getCurrentUser;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.event.AppointmentDaoEvent;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.Appointment;
import scheduler.model.AppointmentType;
import scheduler.model.ui.AppointmentModel;
import scheduler.util.AlertHelper;
import scheduler.util.DB;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import static scheduler.util.NodeUtil.setBorderedNode;
import static scheduler.util.NodeUtil.setLeftControlLabel;
import static scheduler.util.NodeUtil.setLeftLabeledControl;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.MainResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/fx/AppointmentAlert.fxml")
public class AppointmentAlert extends BorderPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentAlert.class.getName()), Level.FINER);

    private static final String NODE_PROPERTYNAME_ALERT_MODEL = "scheduler.fx.AppointmentAlerts.model";
    private static final String NODE_PROPERTYNAME_ALERT_TITLE = "scheduler.fx.AppointmentAlerts.title";
    private static final String NODE_PROPERTYNAME_ALERT_START = "scheduler.fx.AppointmentAlerts.start";
    private static final String NODE_PROPERTYNAME_ALERT_END = "scheduler.fx.AppointmentAlerts.end";
    private static final String NODE_PROPERTYNAME_ALERT_TYPE = "scheduler.fx.AppointmentAlerts.type";
    private static final String NODE_PROPERTYNAME_ALERT_CUSTOMER = "scheduler.fx.AppointmentAlerts.customer";
    private static final String NODE_PROPERTYNAME_ALERT_LOCATION = "scheduler.fx.AppointmentAlerts.location";

    private final int checkFrequency;
    private final int alertLeadtime;
    private DateTimeFormatter formatter;
    private Timer appointmentCheckTimer;
    private List<Integer> dismissed;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="appointmentAlertsVBox"
    private VBox appointmentAlertsVBox; // Value injected by FXMLLoader
    private ReadOnlyObjectProperty<Window> currentWindowProperty;
    private Window currentWindow;

    @SuppressWarnings("LeakingThisInConstructor")
    public AppointmentAlert() {
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
        int i;
        try {
            i = AppResources.getAppointmentAlertLeadTime();
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            i = 15;
        }
        alertLeadtime = i;
        try {
            i = AppResources.getAppointmentCheckFrequency();
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            i = 2;
        }
        checkFrequency = i;
        addEventFilter(AppointmentDaoEvent.APPOINTMENT_DAO_INSERT, this::onAppointmentInserted);
        addEventFilter(AppointmentDaoEvent.APPOINTMENT_DAO_UPDATE, this::onAppointmentUpdated);
        addEventFilter(AppointmentDaoEvent.APPOINTMENT_DAO_DELETE, this::onAppointmentDeleted);
    }

    private synchronized void onAppointmentInserted(AppointmentDaoEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        AppointmentDAO dao = event.getTarget();
        LocalDateTime start = LocalDateTime.now();
        if (start.compareTo(DB.toLocalDateTime(dao.getEnd())) < 0) {
            LocalDateTime end = start.plusMinutes(alertLeadtime);
            if (end.compareTo(DB.toLocalDateTime(dao.getStart())) >= 0) {
                ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
                Stream<AppointmentModel> stream = itemsViewList.stream().map((t) -> (AppointmentModel) t.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL));
                Stream.concat(stream, Stream.of(new AppointmentModel(event.getTarget()))).sorted(AppointmentModel::compareByDates).forEach(new Consumer<AppointmentModel>() {
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
            }
        }
    }

    private synchronized void onAppointmentUpdated(AppointmentDaoEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        AppointmentDAO dao = event.getTarget();
        int key = dao.getPrimaryKey();
        FlowPane view = getViewNode(key);
        ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
        LocalDateTime start = LocalDateTime.now();
        AppointmentModel item;
        if (null == view) {
            item = new AppointmentModel(dao);
        } else {
            item = (AppointmentModel) view.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL);
            if (!Objects.equals(item.dataObject(), dao)) {
                AppointmentModel.getFactory().updateItem(item, dao);
            }
        }
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
                setVisible(false);
            }
        }
    }

    private synchronized void onAppointmentDeleted(AppointmentDaoEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        AppointmentDAO dao = event.getTarget();
        int pk = dao.getPrimaryKey();
        if (dismissed.contains(pk)) {
            dismissed.remove(pk);
        } else {
            FlowPane view = getViewNode(pk);
            if (null != view) {
                ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
                reBind(view, null);
                itemsViewList.remove(view);
                if (itemsViewList.isEmpty()) {
                    setVisible(false);
                }
            }
        }

    }

    @FXML
    private void onDismissAllAppointmentAlerts(ActionEvent event) {
        dismissAll();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert appointmentAlertsVBox != null : "fx:id=\"appointmentAlertsVBox\" was not injected: check your FXML file 'AppointmentAlert.fxml'.";
        dismissed = Collections.synchronizedList(new ArrayList<>());
        formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        // Add listener to see when scene changes, so we can add listeners for when the window is hidden.
        sceneProperty().addListener(this::onSceneChanged);
    }

    @SuppressWarnings("unchecked")
    private synchronized void onSceneChanged(Observable observable) {
        if (null != currentWindowProperty) {
            currentWindowProperty.removeListener(this::onWindowChanged);
        }
        Scene scene = ((ReadOnlyObjectProperty<Scene>) observable).get();
        if (null == scene) {
            currentWindowProperty = null;
            onWindowChanged(Bindings.createObjectBinding(() -> (Window) null));
        } else {
            (currentWindowProperty = scene.windowProperty()).addListener(this::onWindowChanged);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void onWindowChanged(Observable observable) {
        if (null != currentWindow) {
            currentWindow.removeEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowHidden);
        }
        currentWindow = ((ReadOnlyObjectProperty<Window>) observable).get();
        if (null != currentWindow) {
            currentWindow.addEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowHidden);
        }
    }

    private void onWindowHidden(WindowEvent event) {
        stop(true);
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
            collapseNode(this);
        }
    }

    private synchronized void dismiss(FlowPane flowPane) {
        ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
        dismissed.add(((AppointmentModel) flowPane.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey());
        reBind(flowPane, null);
        itemsViewList.remove(flowPane);
        if (itemsViewList.isEmpty()) {
            collapseNode(this);
        }
    }

    private FlowPane getViewNode(int key) {
        Optional<Node> result = appointmentAlertsVBox.getChildren().stream().filter((t)
                -> ((AppointmentModel) t.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey() == key).findFirst();
        return (FlowPane) result.orElse((Node) null);
    }

    public void start() {
        start(true);
    }

    private synchronized void start(boolean isInitial) {
        if (null != appointmentCheckTimer) {
            if (isInitial) {
                return;
            }
            appointmentCheckTimer.purge();
        }
        appointmentCheckTimer = new Timer();
        appointmentCheckTimer.schedule(new CheckAppointmentsTask(alertLeadtime), 0, (long) checkFrequency * 60_000L);
    }

    private synchronized boolean stop(boolean isPermanent) {
        if (null == appointmentCheckTimer) {
            return false;
        }
        appointmentCheckTimer.cancel();
        if (isPermanent) {
            appointmentCheckTimer.purge();
            appointmentCheckTimer = null;
        }
        return true;
    }

    private void onCheckAppointmentsTaskError(Throwable ex) {
        if (AppointmentAlert.this.stop(false)) {
            try {
                LOG.log(Level.SEVERE, "Error while checking for new appointments", ex);
                AlertHelper.showErrorAlert(resources.getString(RESOURCEKEY_APPOINTMENTLOADERROR),
                        resources.getString(RESOURCEKEY_ERRORCHECKINGIMPENDINGAPPOINTMENTS));
            } finally {
                start(false);
            }
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

            appointments.stream().sorted(Appointment::compareByDates).forEach(new Consumer<AppointmentDAO>() {
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
            collapseNode(this);
            return;
        }
        restoreNode(this);
    }

    private class CheckAppointmentsTask extends TimerTask {

        private final UserDAO user;
        private final AppointmentDAO.FactoryImpl factory;
        private final int alertLeadTime;

        private CheckAppointmentsTask(int alertLeadTime) {
            this.alertLeadTime = alertLeadTime;
            user = Objects.requireNonNull(getCurrentUser());
            factory = AppointmentDAO.FACTORY;
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
                LOG.log(Level.SEVERE, "Error checking impending appointments", ex);
                Platform.runLater(() -> onCheckAppointmentsTaskError(ex));
                return;
            }
            Platform.runLater(() -> onPeriodicCheckFinished(appointments));
        }
    }

}
