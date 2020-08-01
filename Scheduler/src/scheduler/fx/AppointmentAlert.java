package scheduler.fx;

import java.io.IOException;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import scheduler.AppointmentAlertManager;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.AlertHelper;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ParentWindowChangeListener;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.EditAppointment;

/**
 * AppointmentAlert Custom Control class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/fx/AppointmentAlert.fxml")
public final class AppointmentAlert extends BorderPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentAlert.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentAlert.class.getName());

    private final ParentWindowChangeListener windowChangeListener;
    private final ChangeListener<Boolean> alertingChangeListener;
    private final ChangeListener<Throwable> faultChangeListener;
    private ObservableList<AppointmentModel> activeAlerts;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="appointmentsListView"
    private ListView<AppointmentModel> appointmentsListView; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public AppointmentAlert() {
        LOG.entering(getClass().getName(), "<init>");
        windowChangeListener = new ParentWindowChangeListener(sceneProperty());
        activeAlerts = FXCollections.emptyObservableList();
        alertingChangeListener = (observable, oldValue, newValue) -> onAlertingChanged(newValue);
        faultChangeListener = (observable, oldValue, newValue) -> onFaultChanged(newValue);
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            throw new InternalError("Error loading view", ex);
        }
        LOG.exiting(getClass().getName(), "<init>");
    }

    @FXML
    void onItemActionRequest(AppointmentOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentEditRequest", event);
        AppointmentModel appointment = event.getEntityModel();
        AppointmentAlertManager.INSTANCE.dismiss(appointment);
        if (event.isEdit()) {
            try {
                EditAppointment.edit(appointment, getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentEditRequest");
    }

    @FXML
    private void onDismissAllAppointmentAlerts(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDismissAllAppointmentAlerts", event);
        AppointmentAlertManager.INSTANCE.dismissAll();
        LOG.exiting(LOG.getName(), "onDismissAllAppointmentAlerts");
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onAppointmentsListViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentsTableViewTableViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AppointmentModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = appointmentsListView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        dismissAppointment(item);
                    }
                    break;
                case ENTER:
                    item = appointmentsListView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editAppointment(item);
                    }
                    break;
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentsTableViewTableViewKeyReleased");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        assert appointmentsListView != null : "fx:id=\"appointmentsListView\" was not injected: check your FXML file 'AppointmentAlert.fxml'.";
        appointmentsListView.setItems(activeAlerts);
        Window window = windowChangeListener.getCurrentWindow();
        if (null != window && window.isShowing()) {
            onWindowNotNullAndShown();
        } else {
            windowChangeListener.setOnWindowNotNullAndShown(this::onWindowNotNullAndShown);
        }
        LOG.exiting(LOG.getName(), "initialize");
    }

    private void dismissAppointment(AppointmentModel item) {
        AppointmentAlertManager.INSTANCE.dismiss(item);
    }

    private void editAppointment(AppointmentModel item) {
        AppointmentAlertManager.INSTANCE.dismiss(item);
        // TODO: Snooze time shouldn't be an inline constant
        AppointmentAlertManager.INSTANCE.snoozeAll(Duration.ofMinutes(5));
        try {
            EditAppointment.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void onWindowNotNullAndShown() {
        activeAlerts = AppointmentAlertManager.INSTANCE.activeAlertsProperty();
        appointmentsListView.setItems(activeAlerts);
        if (AppointmentAlertManager.INSTANCE.isAlerting()) {
            onAlertingChanged(true);
        }
        AppointmentAlertManager.INSTANCE.alertingProperty().addListener(alertingChangeListener);
        AppointmentAlertManager.INSTANCE.faultProperty().addListener(faultChangeListener);
        windowChangeListener.setOnWindowNullOrHidden(this::onWindowNullOrHidden);
    }

    private synchronized void onWindowNotNullAndShown(WindowEvent event) {
        LOG.entering(LOG.getName(), "onWindowNotNullAndShown", event);
        windowChangeListener.setOnWindowNotNullAndShown(null);
        onWindowNotNullAndShown();
        LOG.exiting(LOG.getName(), "onWindowNotNullAndShown");
    }

    private synchronized void onWindowNullOrHidden(WindowEvent event) {
        LOG.entering(LOG.getName(), "onWindowHidden", event);
        windowChangeListener.setOnWindowNullOrHidden(null);
        AppointmentAlertManager.INSTANCE.alertingProperty().removeListener(alertingChangeListener);
        AppointmentAlertManager.INSTANCE.faultProperty().removeListener(faultChangeListener);
        activeAlerts = FXCollections.emptyObservableList();
        appointmentsListView.setItems(activeAlerts);
        if (AppointmentAlertManager.INSTANCE.isAlerting()) {
            onAlertingChanged(false);
        }
        LOG.exiting(LOG.getName(), "onWindowHidden");
    }

    private void onAlertingChanged(boolean isAlerting) {
        LOG.entering(LOG.getName(), "onAlertingChanged", isAlerting);
        if (isAlerting) {
            restoreNode(this);
        } else {
            collapseNode(this);
        }
        LOG.exiting(LOG.getName(), "onAlertingChanged");
    }

    private void onFaultChanged(Throwable fault) {
        LOG.entering(LOG.getName(), "onFaultChanged", fault);
        if (null != fault) {
            AppointmentAlertManager.INSTANCE.faultProperty().removeListener(faultChangeListener);
            try {
                AlertHelper.showErrorAlert("Error loading appointments",
                        String.format("An unexpected error occurred while checking for upcoming appointments: %s", fault));
            } finally {
                AppointmentAlertManager.INSTANCE.faultProperty().addListener(faultChangeListener);
                AppointmentAlertManager.INSTANCE.clearFault();
            }
        }
        LOG.exiting(LOG.getName(), "onFaultChanged");
    }

}
