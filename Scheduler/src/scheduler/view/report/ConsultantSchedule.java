package scheduler.view.report;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.UserDAO;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentFailedEvent;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.model.ModelHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.UserModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.clearAndSelect;
import static scheduler.util.NodeUtil.isInShownWindow;
import scheduler.util.ParentWindowChangeListener;
import scheduler.util.ViewControllerLoader;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentDay;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.task.WaitTitledPane;

@GlobalizationResource("scheduler/view/report/Reports")
@FXMLResource("/scheduler/view/report/ConsultantSchedule.fxml")
public class ConsultantSchedule extends VBox {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ConsultantSchedule.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ConsultantSchedule.class.getName());

    public static ConsultantSchedule create() {
        ConsultantSchedule newContent = new ConsultantSchedule();
        try {
            ViewControllerLoader.initializeCustomControl(newContent);
        } catch (IOException ex) {
            throw new InternalError("Error loading view", ex);
        }
        return newContent;
    }

    private final ParentWindowChangeListener stageChangeListener;
    private final ObservableList<UserModel> consultantList;
    private final ObservableList<AppointmentModel> allAppointments;
    private final ObservableList<AppointmentDay> appointmentDays;
    private final TreeItem<AppointmentDay> root;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentInsertEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentUpdateEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentDeleteEventHandler;
    AppointmentModelFilter modelFilter;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rangeStartDatePicker"
    private DatePicker rangeStartDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="rangeEndDatePicker"
    private DatePicker rangeEndDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="rangeValidationLabel"
    private Label rangeValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="consultantsComboBox"
    private ComboBox<UserModel> consultantsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="consultantValidationLabel"
    private Label consultantValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="runButton"
    private Button runButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTreeView"
    private TreeView<AppointmentDay> appointmentsTreeView; // Value injected by FXMLLoader

    private ConsultantSchedule() {
        stageChangeListener = new ParentWindowChangeListener(sceneProperty());
        stageChangeListener.currentStageProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                newValue.setTitle("Consultant Schedule");
            }
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "new", "stageChangeHandler#currentStage"), "change");
        });
        allAppointments = FXCollections.observableArrayList();
        appointmentDays = FXCollections.observableArrayList();
        consultantList = FXCollections.observableArrayList();
        root = new TreeItem<>();
        allAppointments.addListener(this::onAllAppointmentsChanged);
        appointmentInsertEventHandler = WeakEventHandlingReference.create(this::onAppointmentInserted);
        appointmentUpdateEventHandler = WeakEventHandlingReference.create(this::onAppointmentUpdated);
        appointmentDeleteEventHandler = WeakEventHandlingReference.create(this::onAppointmentDeleted);
    }

    @FXML
    private void onItemActionRequest(AppointmentOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            editItem(event.getEntityModel());
        } else {
            deleteItem(event.getEntityModel());
        }
        LOG.exiting(LOG.getName(), "onItemActionRequest");
    }

    @FXML
    private void onParameterAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onParameterAction", event);
        LocalDate start = rangeStartDatePicker.getValue();
        LocalDate end = rangeEndDatePicker.getValue();
        if (null == start || null == end) {
            rangeValidationLabel.setVisible(true);
            rangeValidationLabel.setText(resources.getString("required"));
        } else if (end.compareTo(start) < 0) {
            rangeValidationLabel.setVisible(true);
            rangeValidationLabel.setText(resources.getString("endCannotBeBeforeStart"));
        } else {
            rangeValidationLabel.setVisible(false);
            if (null == consultantsComboBox.getValue()) {
                consultantValidationLabel.setVisible(true);
                runButton.setDisable(true);
            } else {
                consultantValidationLabel.setVisible(false);
                runButton.setDisable(false);
            }
            return;
        }
        consultantValidationLabel.setVisible(true);
        runButton.setDisable(null == consultantsComboBox.getValue());
        LOG.exiting(getClass().getName(), "onParameterAction");
    }

    @FXML
    private void onRunButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onRunButtonAction", event);
        LocalDate start = rangeStartDatePicker.getValue();
        LocalDate end = rangeEndDatePicker.getValue();
        UserModel user = consultantsComboBox.getSelectionModel().getSelectedItem();
        if (null != user && null != start && null != end && !start.isAfter(end)) {
            modelFilter = AppointmentModelFilter.of(start, end.plusDays(1L), user);
            MainController.startBusyTaskNow(new AppointmentReloadTask());
        }
        LOG.exiting(getClass().getName(), "onRunButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert rangeStartDatePicker != null : "fx:id=\"rangeStartDatePicker\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert rangeEndDatePicker != null : "fx:id=\"rangeEndDatePicker\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert rangeValidationLabel != null : "fx:id=\"rangeValidationLabel\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert runButton != null : "fx:id=\"runButton\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert appointmentsTreeView != null : "fx:id=\"appointmentsTreeView\" was not injected: check your FXML file 'AppointmentsByMonth.fxml'.";

        consultantsComboBox.setItems(consultantList);
        appointmentsTreeView.setRoot(root);
        WaitTitledPane pane = WaitTitledPane.create();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        MainController.startBusyTaskNow(pane, new InitializeTask());
    }

    private void onAppointmentInserted(AppointmentSuccessEvent event) {
        LOG.entering(getClass().getName(), "onAppointmentInserted", event);
        if (isInShownWindow(this)) {
            AppointmentModel entityModel = event.getEntityModel();
            if (modelFilter.test(entityModel)) {
                allAppointments.add(entityModel);
            }
        }
        LOG.exiting(getClass().getName(), "onAppointmentInserted");
    }

    private void onAppointmentUpdated(AppointmentSuccessEvent event) {
        LOG.entering(getClass().getName(), "onAppointmentUpdated", event);
        if (isInShownWindow(this)) {
            AppointmentModel updatedModel = event.getEntityModel();
            AppointmentModel currentModel = ModelHelper.findByPrimaryKey(updatedModel.getPrimaryKey(), allAppointments).orElse(null);
            if (modelFilter.test(updatedModel)) {
                if (null != currentModel) {
                    if (currentModel == updatedModel) {
                        return;
                    }
                    allAppointments.remove(currentModel);
                }
                allAppointments.add(updatedModel);
            } else if (null == currentModel) {
                allAppointments.remove(currentModel);
            }
        }
        LOG.exiting(getClass().getName(), "onAppointmentUpdated");
    }

    private void onAppointmentDeleted(AppointmentSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentDeleted", event);
        if (isInShownWindow(this)) {
            AppointmentModel currentModel = ModelHelper.findByPrimaryKey(event.getEntityModel().getPrimaryKey(), allAppointments).orElse(null);
            if (null != currentModel) {
                allAppointments.remove(currentModel);
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentDeleted");
    }

    private void onAllAppointmentsChanged(ListChangeListener.Change<? extends AppointmentModel> c) {
        LOG.entering(getClass().getName(), "onAllAppointmentsChanged");
        if (AppointmentDay.importSourceChanges(c, appointmentDays)) {
            appointmentDays.sort(AppointmentDay::compareByDates);
        }

        ObservableList<TreeItem<AppointmentDay>> childItems = root.getChildren();
        childItems.clear();
        AppointmentDay.createBranches(appointmentDays).forEach((t) -> childItems.add(t));
        LOG.exiting(getClass().getName(), "onAllAppointmentsChanged");
    }

    private void editItem(AppointmentModel item) {
        try {
            EditAppointment.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void deleteItem(AppointmentModel target) {
        AppointmentOpRequestEvent deleteRequestEvent = new AppointmentOpRequestEvent(target, this, true);
        Event.fireEvent(target.dataObject(), deleteRequestEvent);
        Stage stage = (Stage) getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stage, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    DataAccessObject.DeleteDaoTask<AppointmentDAO, AppointmentModel> task = AppointmentModel.FACTORY.createDeleteTask(target);
                    task.setOnSucceeded((e) -> {
                        AppointmentEvent appointmentEvent = (AppointmentEvent) task.getValue();
                        if (null != appointmentEvent && appointmentEvent instanceof AppointmentFailedEvent) {
                            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                                    ((ModelFailedEvent<AppointmentDAO, AppointmentModel>) appointmentEvent).getMessage(), ButtonType.OK);
                        }
                    });
                    MainController.startBusyTaskNow(task);
                }
            });
        }

    }

    private class InitializeTask extends Task<List<UserDAO>> {

        private List<AppointmentDAO> appointments;
        private final LocalDate start;
        private final LocalDate end;
        private final UserDAO user;

        private InitializeTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            start = LocalDate.now();
            end = start.plusDays(7L);
            user = Scheduler.getCurrentUser();
            modelFilter = AppointmentModelFilter.of(start, end.plusDays(1L), user);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            List<UserDAO> result = getValue();
            rangeStartDatePicker.setValue(start);
            rangeEndDatePicker.setValue(end);
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> consultantList.add(t.cachedModel(true)));
                clearAndSelect(consultantsComboBox, user);
            }

            AppointmentDAO.updateModelList(appointments, allAppointments);
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {

                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                appointments = AppointmentDAO.FACTORY.load(dbConnector.getConnection(), modelFilter.getDaoFilter());
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                return uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
            }
        }

    }

    private class AppointmentReloadTask extends Task<List<AppointmentDAO>> {

        private AppointmentReloadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            AppointmentDAO.updateModelList(getValue(), allAppointments);
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                return AppointmentDAO.FACTORY.load(dbConnector.getConnection(), modelFilter.getDaoFilter());
            }
        }

    }

}
