package scheduler.view.appointment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentFailedEvent;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.model.ModelHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.isInShownWindow;
import scheduler.util.ViewControllerLoader;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/AppointmentsByMonth.fxml")
public final class AppointmentsByMonth extends VBox {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentsByMonth.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(AppointmentsByMonth.class.getName());

    public static AppointmentsByMonth loadIntoMainContent(YearMonth targetMonth) {
        AppointmentsByMonth newContent = new AppointmentsByMonth(targetMonth);
        try {
            ViewControllerLoader.initializeCustomControl(newContent);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
        Scheduler.getMainController().replaceContent(newContent);
        return newContent;
    }

    private final ReadOnlyObjectWrapper<YearMonth> targetMonth;
    private final ReadOnlyObjectWrapper<AppointmentModelFilter> modelFilter;
    private final ObservableList<AppointmentModel> allAppointments;
    private final ObservableList<AppointmentDay> appointmentDays;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentInsertEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentUpdateEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentDeleteEventHandler;

    @FXML // fx:id="monthNameLabel"
    private Label monthNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableTreeView"
    private TreeTableView<AppointmentDay> appointmentsTableTreeView; // Value injected by FXMLLoader

    @FXML // fx:id="yearSpinner"
    private Spinner<Integer> yearSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="monthComboBox"
    private ComboBox<Month> monthComboBox; // Value injected by FXMLLoader

    private AppointmentsByMonth(YearMonth targetMonth) {
        this.targetMonth = new ReadOnlyObjectWrapper<>(this, "targetMonth", (null == targetMonth) ? YearMonth.now() : targetMonth);
        modelFilter = new ReadOnlyObjectWrapper<>(this, "modelFilter");
        allAppointments = FXCollections.observableArrayList();
        appointmentDays = FXCollections.observableArrayList();
        allAppointments.addListener(this::onAllAppointmentsChanged);
        modelFilter.addListener(this::onModelFilterChanged);
        appointmentInsertEventHandler = WeakEventHandlingReference.create(this::onAppointmentInserted);
        appointmentUpdateEventHandler = WeakEventHandlingReference.create(this::onAppointmentUpdated);
        appointmentDeleteEventHandler = WeakEventHandlingReference.create(this::onAppointmentDeleted);
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onAppointmentsTableTreeViewViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentsTableTreeViewViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            TreeItem<AppointmentDay> item;
            switch (event.getCode()) {
                case DELETE:
                    item = appointmentsTableTreeView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteItem(item.getValue().getModel());
                    }
                    break;
                case ENTER:
                    item = appointmentsTableTreeView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editItem(item.getValue().getModel());
                    }
                    break;
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentsTableTreeViewViewKeyReleased");
    }

    @FXML
    void onItemActionRequest(AppointmentOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            editItem(event.getEntityModel());
        } else {
            deleteItem(event.getEntityModel());
        }
        LOG.exiting(LOG.getName(), "onItemActionRequest");
    }

    @FXML
    void onNextMonthButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onNextMonthButtonAction", event);
        targetMonth.set(targetMonth.get().plusMonths(1));
        LOG.exiting(getClass().getName(), "onNextMonthButtonAction");
    }

    @FXML
    void onPreviousMonthButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onPreviousMonthButtonAction", event);
        targetMonth.set(targetMonth.get().minusMonths(1));
        LOG.exiting(getClass().getName(), "onPreviousMonthButtonAction");
    }

    @FXML
    void onSearchButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onSearchButtonAction", event);
        int year = yearSpinner.getValue();
        Month month = monthComboBox.getSelectionModel().getSelectedItem();
        YearMonth current = targetMonth.get();
        if (year != current.getYear() || month != current.getMonth()) {
            YearMonth newValue = YearMonth.of(year, month);
            LOG.info(() -> String.format("Changing target month from %s to %s", current, newValue));
            targetMonth.set(newValue);
        }
        LOG.exiting(getClass().getName(), "onSearchButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOG.entering(getClass().getName(), "initialize");
        assert monthNameLabel != null : "fx:id=\"monthNameLabel\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert appointmentsTableTreeView != null : "fx:id=\"appointmentsTableTreeView\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert yearSpinner != null : "fx:id=\"yearSpinner\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert monthComboBox != null : "fx:id=\"monthComboBox\" was not injected: check your FXML file 'ByMonth.fxml'.";

        YearMonth t = targetMonth.get();
        int y = yearSpinner.getValue();
        if (y < t.getYear()) {
        } else if (y > t.getYear()) {
            yearSpinner.decrement(y - t.getYear());
        }
        monthComboBox.getSelectionModel().select(t.getMonth());
        onTargetMonthChanged(targetMonth, null, targetMonth.get());
        targetMonth.addListener(this::onTargetMonthChanged);
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.INSERT_SUCCESS, appointmentInsertEventHandler.getWeakEventHandler());
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.UPDATE_SUCCESS, appointmentUpdateEventHandler.getWeakEventHandler());
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.DELETE_SUCCESS, appointmentDeleteEventHandler.getWeakEventHandler());
        LOG.exiting(getClass().getName(), "initialize");
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

    public YearMonth getTargetMonth() {
        return targetMonth.get();
    }

    public ReadOnlyObjectProperty<YearMonth> targetMonthProperty() {
        return targetMonth.getReadOnlyProperty();
    }

    public AppointmentModelFilter getModelFilter() {
        return modelFilter.get();
    }

    public ReadOnlyObjectProperty<AppointmentModelFilter> modelFilterProperty() {
        return modelFilter.getReadOnlyProperty();
    }

    private void onAppointmentInserted(AppointmentSuccessEvent event) {
        LOG.entering(getClass().getName(), "onAppointmentAdded", event);
        if (isInShownWindow(this)) {
            AppointmentModel entityModel = event.getEntityModel();
            if (modelFilter.get().test(entityModel)) {
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
            if (modelFilter.get().test(updatedModel)) {
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

    private void onTargetMonthChanged(ObservableValue<? extends YearMonth> observable, YearMonth oldValue, YearMonth newValue) {
        LOG.entering(getClass().getName(), "onTargetMonthChanged", new Object[]{observable, oldValue, newValue});
        LocalDate start = newValue.atDay(1);
        modelFilter.set(AppointmentModelFilter.of(start, start.plusMonths(1)));
        LOG.exiting(getClass().getName(), "onTargetMonthChanged");
    }

    private void onModelFilterChanged(ObservableValue<? extends AppointmentModelFilter> observable, AppointmentModelFilter oldValue, AppointmentModelFilter newValue) {
        LOG.entering(getClass().getName(), "onModelFilterChanged", new Object[]{observable, oldValue, newValue});
        LoadItemsTask task = new LoadItemsTask(newValue.getDaoFilter());
        MainController.startBusyTaskNow(task);
        LOG.exiting(getClass().getName(), "onModelFilterChanged");
    }

    private void onAllAppointmentsChanged(Change<? extends AppointmentModel> c) {
        LOG.entering(getClass().getName(), "onAllAppointmentsChanged", c);
        if (AppointmentDay.update(c, appointmentDays)) {
            appointmentDays.sort(AppointmentDay::compareByDates);
        }
        LOG.exiting(getClass().getName(), "onAllAppointmentsChanged");
    }

    private class LoadItemsTask extends Task<List<AppointmentDAO>> {

        private final AppointmentFilter filter;

        LoadItemsTask(AppointmentFilter filter) {
            this.filter = filter;
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.entering(getClass().getName(), "call");
            updateMessage("Connecting to database");
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage("Connected. Loading appointments.");
                List<AppointmentDAO> result = AppointmentDAO.FACTORY.load(dbConnector.getConnection(), filter);
                LOG.exiting(getClass().getName(), "call", result);
                return result;
            }
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            List<AppointmentDAO> list = getValue();
            AppointmentDAO.updateModelList(list, allAppointments);
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            AppointmentDAO.updateModelList(Collections.emptyList(), allAppointments);
        }

        @Override
        protected void failed() {
            super.failed();
            AppointmentDAO.updateModelList(Collections.emptyList(), allAppointments);
        }

    }
}
