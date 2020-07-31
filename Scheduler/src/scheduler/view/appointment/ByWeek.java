/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
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
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
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
@GlobalizationResource("scheduler/view/appointment/Calendar")
@FXMLResource("/scheduler/view/appointment/ByWeek.fxml")
public class ByWeek extends StackPane {

    private static final Logger LOG = Logger.getLogger(ByWeek.class.getName());

    public static ByWeek loadIntoMainContent(LocalDate week) {
        ByWeek newContent = new ByWeek(week);
        try {
            ViewControllerLoader.initializeCustomControl(newContent);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
        Scheduler.getMainController().replaceContent(newContent);
        return newContent;
    }

    private static LocalDate atStartofWeek(LocalDate value) {
        LocalDate d = value;
        while (d.getDayOfWeek() != DayOfWeek.SUNDAY) {
            d = d.minusDays(1);
        }
        return d;
    }

    private final ReadOnlyObjectWrapper<LocalDate> targetDate;
    private final ReadOnlyObjectWrapper<LocalDate> startOfWeek;
    private final ReadOnlyObjectWrapper<AppointmentModelFilter> modelFilter;
    private final ObservableList<AppointmentModel> allAppointments;
    private final ObservableList<AppointmentDay> appointmentDays;
    private final ObservableList<Integer> weekNumberOptions;
    private final HashMap<DayOfWeek, ObservableList<AppointmentModel>> itemsByDay;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentInsertEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentUpdateEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentDeleteEventHandler;

    @FXML // fx:id="weekAndMonthNameLabel"
    private Label weekAndMonthNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="sundayTitledPane"
    private TitledPane sundayTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="sundayTableView"
    private TableView<AppointmentModel> sundayTableView; // Value injected by FXMLLoader

    @FXML // fx:id="mondayTitledPane"
    private TitledPane mondayTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="mondayTableView"
    private TableView<AppointmentModel> mondayTableView; // Value injected by FXMLLoader

    @FXML // fx:id="tuesdayTitledPane"
    private TitledPane tuesdayTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="tuesdayTableView"
    private TableView<AppointmentModel> tuesdayTableView; // Value injected by FXMLLoader

    @FXML // fx:id="wednesdayTitledPane"
    private TitledPane wednesdayTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="wednesdayTableView"
    private TableView<AppointmentModel> wednesdayTableView; // Value injected by FXMLLoader

    @FXML // fx:id="thursdayTitledPane"
    private TitledPane thursdayTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="thursdayTableView"
    private TableView<AppointmentModel> thursdayTableView; // Value injected by FXMLLoader

    @FXML // fx:id="fridayTitledPane"
    private TitledPane fridayTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="fridayTableView"
    private TableView<AppointmentModel> fridayTableView; // Value injected by FXMLLoader

    @FXML // fx:id="saturdayTitledPane"
    private TitledPane saturdayTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="saturdayTableView"
    private TableView<AppointmentModel> saturdayTableView; // Value injected by FXMLLoader

    @FXML // fx:id="yearSpinner"
    private Spinner<Integer> yearSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="monthComboBox"
    private ComboBox<Month> monthComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="weekComboBox"
    private ComboBox<Integer> weekComboBox; // Value injected by FXMLLoader

    public ByWeek(LocalDate targetDate) {
        this.targetDate = new ReadOnlyObjectWrapper<>(this, "targetDate", (null == targetDate) ? LocalDate.now() : targetDate);
        startOfWeek = new ReadOnlyObjectWrapper<>(this, "startOfWeek", atStartofWeek(this.targetDate.get()));
        modelFilter = new ReadOnlyObjectWrapper<>(this, "modelFilter");
        allAppointments = FXCollections.observableArrayList();
        appointmentDays = FXCollections.observableArrayList();
        weekNumberOptions = FXCollections.observableArrayList();
        allAppointments.addListener(this::onAllAppointmentsChanged);
        modelFilter.addListener(this::onModelFilterChanged);
        itemsByDay = new HashMap<>();
        for (DayOfWeek d : DayOfWeek.values()) {
            itemsByDay.put(d, FXCollections.observableArrayList());
        }
        weekNumberOptions.addAll(1, 2, 3, 4);
        this.targetDate.addListener(this::onTargetDateChanged);
        appointmentInsertEventHandler = WeakEventHandlingReference.create(this::onAppointmentInserted);
        appointmentUpdateEventHandler = WeakEventHandlingReference.create(this::onAppointmentUpdated);
        appointmentDeleteEventHandler = WeakEventHandlingReference.create(this::onAppointmentDeleted);
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
    @SuppressWarnings("incomplete-switch")
    private void onAppointmentsTableViewTableViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentsTableViewTableViewKeyReleased", event);
        @SuppressWarnings("unchecked")
        TableView<AppointmentModel> source = (TableView<AppointmentModel>) event.getSource();
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AppointmentModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = source.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteItem(item);
                    }
                    break;
                case ENTER:
                    item = source.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editItem(item);
                    }
                    break;
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentsTableViewTableViewKeyReleased");
    }

    @FXML
    void onNextWeekButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onNextWeekButtonAction", event);
        targetDate.set(targetDate.get().plusDays(1));
        LOG.exiting(getClass().getName(), "onNextWeekButtonAction");
    }

    @FXML
    void onPreviousWeekButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onPreviousWeekButtonAction", event);
        targetDate.set(targetDate.get().minusMonths(1));
        LOG.exiting(getClass().getName(), "onPreviousWeekButtonAction");
    }

    @FXML
    void onSearchButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onSearchButtonAction", event);
        int year = yearSpinner.getValue();
        Month month = monthComboBox.getSelectionModel().getSelectedItem();
        int weekNum = weekComboBox.getSelectionModel().getSelectedItem();
        LocalDate current = startOfWeek.get();
        if (year != current.getYear() || month != current.getMonth() && weekNum != ((current.getDayOfMonth() - 1) % 7 + 1)) {
            LocalDate newValue = LocalDate.of(year, month, (weekNum - 1) * 7 + 1);
            LOG.info(() -> String.format("Changing target week from %s to %s", current, newValue));
            targetDate.set(newValue);
        }
        LOG.exiting(getClass().getName(), "onSearchButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert weekAndMonthNameLabel != null : "fx:id=\"weekAndMonthNameLabel\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert sundayTitledPane != null : "fx:id=\"sundayTitledPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert sundayTableView != null : "fx:id=\"sundayTableView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert mondayTitledPane != null : "fx:id=\"mondayTitledPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert mondayTableView != null : "fx:id=\"mondayTableView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert tuesdayTitledPane != null : "fx:id=\"tuesdayTitledPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert tuesdayTableView != null : "fx:id=\"tuesdayTableView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert wednesdayTitledPane != null : "fx:id=\"wednesdayTitledPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert wednesdayTableView != null : "fx:id=\"wednesdayTableView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert thursdayTitledPane != null : "fx:id=\"thursdayTitledPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert thursdayTableView != null : "fx:id=\"thursdayTableView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert fridayTitledPane != null : "fx:id=\"fridayTitledPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert fridayTableView != null : "fx:id=\"fridayTableView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert saturdayTitledPane != null : "fx:id=\"saturdayTitledPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert saturdayTableView != null : "fx:id=\"saturdayTableView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert yearSpinner != null : "fx:id=\"yearSpinner\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert monthComboBox != null : "fx:id=\"monthComboBox\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert weekComboBox != null : "fx:id=\"weekComboBox\" was not injected: check your FXML file 'ByWeek.fxml'.";

        sundayTableView.setItems(itemsByDay.get(DayOfWeek.SUNDAY));
        mondayTableView.setItems(itemsByDay.get(DayOfWeek.MONDAY));
        tuesdayTableView.setItems(itemsByDay.get(DayOfWeek.TUESDAY));
        wednesdayTableView.setItems(itemsByDay.get(DayOfWeek.WEDNESDAY));
        thursdayTableView.setItems(itemsByDay.get(DayOfWeek.THURSDAY));
        fridayTableView.setItems(itemsByDay.get(DayOfWeek.FRIDAY));
        saturdayTableView.setItems(itemsByDay.get(DayOfWeek.SATURDAY));

        LocalDate t = targetDate.get();
        int y = yearSpinner.getValue();
        if (y < t.getYear()) {
        } else if (y > t.getYear()) {
            yearSpinner.decrement(y - t.getYear());
        }
        monthComboBox.getSelectionModel().select(t.getMonth());
        y = t.getDayOfMonth() - 1;
        y = ((y - (y % 7)) / 7) + 1;
        while (weekNumberOptions.size() < y) {
            weekNumberOptions.add(weekNumberOptions.size());
        }
        weekComboBox.getSelectionModel().clearAndSelect(y - 1);

        onStartOfWeekChanged(startOfWeek, null, startOfWeek.get());
        updateWeekOptions(yearSpinner.getValue(), monthComboBox.getSelectionModel().getSelectedItem());
        startOfWeek.addListener(this::onStartOfWeekChanged);
        yearSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateWeekOptions(newValue, monthComboBox.getSelectionModel().getSelectedItem());
        });
        monthComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateWeekOptions(yearSpinner.getValue(), newValue);
        });
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

    private void onAppointmentInserted(AppointmentSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentAdded", event);
        if (isInShownWindow(this)) {
            AppointmentModel entityModel = event.getEntityModel();
            if (modelFilter.get().test(entityModel)) {
                allAppointments.add(entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentInserted");
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
        LOG.entering(getClass().getName(), "onAppointmentDeleted", event);
        if (isInShownWindow(this)) {
            AppointmentModel currentModel = ModelHelper.findByPrimaryKey(event.getEntityModel().getPrimaryKey(), allAppointments).orElse(null);
            if (null != currentModel) {
                allAppointments.remove(currentModel);
            }
        }
        LOG.exiting(getClass().getName(), "onAppointmentDeleted");
    }

    private void updateWeekOptions(int year, Month month) {
        int d = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1).getDayOfMonth() - 1;
        d = ((d - (d % 7)) / 7) + 1;
        if (weekNumberOptions.size() < d) {
            do {
                weekNumberOptions.add(weekNumberOptions.size());
            } while (weekNumberOptions.size() < d);
        } else if (weekNumberOptions.size() < d) {
            boolean b = weekComboBox.getSelectionModel().getSelectedItem() >= d;
            do {
                weekNumberOptions.remove(weekNumberOptions.size());
            } while (weekNumberOptions.size() < d);
            if (b) {
                weekComboBox.getSelectionModel().selectLast();
            }
        }
    }

    private void onTargetDateChanged(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
        LOG.entering(getClass().getName(), "onTargetDayChanged", new Object[]{observable, oldValue, newValue});
        LocalDate start = atStartofWeek(newValue);
        if (!start.equals(startOfWeek.get())) {
            startOfWeek.set(start);
        }
        LOG.exiting(getClass().getName(), "onTargetDayChanged");
    }

    private void onStartOfWeekChanged(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
        LOG.entering(getClass().getName(), "onTargetMonthChanged", new Object[]{observable, oldValue, newValue});
        modelFilter.set(AppointmentModelFilter.of(newValue, newValue.plusWeeks(1)));
        LOG.exiting(getClass().getName(), "onTargetMonthChanged");
    }

    private void onModelFilterChanged(ObservableValue<? extends AppointmentModelFilter> observable, AppointmentModelFilter oldValue, AppointmentModelFilter newValue) {
        LOG.entering(getClass().getName(), "onModelFilterChanged", new Object[]{observable, oldValue, newValue});
        LoadItemsTask task = new LoadItemsTask(newValue.getDaoFilter());
        MainController.startBusyTaskNow(task);
        LOG.exiting(getClass().getName(), "onModelFilterChanged");
    }

    private void onAllAppointmentsChanged(ListChangeListener.Change<? extends AppointmentModel> c) {
        LOG.entering(getClass().getName(), "onAllAppointmentsChanged", c);
        AppointmentDay.update(c, appointmentDays);
        itemsByDay.values().forEach((t) -> t.clear());
        appointmentDays.forEach((t) -> {
            DayOfWeek dayOfWeek = t.getDate().getDayOfWeek();
            itemsByDay.get(dayOfWeek).add(t.getModel());
        });
        itemsByDay.values().forEach((t) -> t.sort(AppointmentHelper::compareByDates));
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
