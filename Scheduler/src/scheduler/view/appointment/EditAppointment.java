package scheduler.view.appointment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.controls.HourAndMinuteSpinnerFactories;
import scheduler.controls.TimeOfDaySpinnerValueFactories;
import scheduler.controls.TimeZoneListCellFactory;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.AppointmentType;
import scheduler.dao.CustomerDAO;
import scheduler.dao.UserDAO;
import scheduler.util.AlertHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.UserModel;
import scheduler.view.user.UserModelImpl;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public final class EditAppointment extends EditItem.EditController<AppointmentDAO, AppointmentModel> implements EditAppointmentConstants {

    private static final Logger LOG = Logger.getLogger(EditAppointment.class.getName());

    public static AppointmentModel editNew(MainController mainController, Stage stage) throws IOException {
        return editNew(EditAppointment.class, mainController, stage);
    }

    public static AppointmentModel edit(AppointmentModel model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditAppointment.class, mainController, stage);
    }

    // TODO: Implement customerComboBox validation
    @FXML // Label for displaying customer selection validation message.
    private Label customerValidationLabel;

    // TODO: Implement userComboBox validation
    @FXML // Label for displaying user selection validation message.
    private Label userValidationLabel;

    @FXML // CustomerDAO selection control
    private ComboBox<CustomerModelImpl> customerComboBox;

    @FXML // UserDAO selection control.
    private ComboBox<UserModelImpl> userComboBox;

    // TODO: Implement titleTextField validation
    @FXML // Label for displaying appointment title validation message.
    private Label titleValidationLabel;

    @FXML // Control for the appointment title.
    private TextField titleTextField;

    // TODO: Implement validation for startDatePicker, startHourSpinner, startMinuteSpinner, amPmSpinner and timeZoneComboBox
    @FXML // Label for displaying appointment start date validation message.
    private Label startValidationLabel;

    @FXML // Control for selecting the appointment start date.
    private DatePicker startDatePicker;

    @FXML // Control for selecting the appointment start hour.
    private Spinner<Integer> startHourSpinner;

    @FXML // Control for selecting the appointment start minute.
    private Spinner<Integer> startMinuteSpinner;

    @FXML // Control for selecting the appointment start minute.
    private Spinner<Boolean> amPmSpinner;

    // TODO: Implement validation for durationHourSpinner and durationMinuteSpinner
    @FXML // Label for displaying appointment duration validation message.
    private Label durationValidationLabel;

    @FXML // Control for selecting the appointment duration (end time).
    private Spinner<Integer> durationHourSpinner;

    @FXML // Control for selecting the appointment duration (end time).
    private Spinner<Integer> durationMinuteSpinner;

    @FXML // Control for selecting the time zone for the appointment start and end.
    private ComboBox<TimeZone> timeZoneComboBox;

    @FXML // Field label that gets hidden when the user selects the default time zone.
    private Label currentTimeZoneLabel;

    @FXML // Label for displaying the selected time, converted to the default time zone.
    private Label currentTimeZoneValue;

    // TODO: Hidden for AppointmentType.PHONE and AppointmentType.VIRTUAL
    @FXML // Field label for phone number control as well as explicit and implicit location.
    private Label locationLabel;

    // TODO: Implement locationLabel validation - required only if type == AppointmentType.OTHER
    @FXML // Label for displaying phone or explicit location validation message.
    private Label locationValidationLabel;

    // TODO: Read-only for AppointmentType CUSTOMER_SITE, CORPORATE_HQ_MEETING, GERMANY_SITE_MEETING, INDIA_SITE_MEETING, HONDURAS_SITE_MEETING
    // TODO: Hidden for AppointmentType.PHONE and AppointmentType.VIRTUAL
    @FXML // Explicit location input control.
    private TextArea locationTextArea;

    // TODO: Shown only for AppointmentType.PHONE
    @FXML // Phone number input control.
    private TextField phoneTextField;

    @FXML // AppointmentDAO type selection control.
    private ComboBox<AppointmentType> typeComboBox;

    // TODO: Implement contactTextField validation
    @FXML // Label for displaying point-of-contact validation message.
    private Label contactValidationLabel;

    @FXML // Point-of-Contact input control.
    private TextField contactTextField;

    @FXML // Field label for the Meeting URL control.
    private Label urlLabel;

    // TODO: Implement urlTextField validation, required only for AppointmentType.VIRTUAL
    @FXML // Label for displaying URL validation message.
    private Label urlValidationLabel;

    @FXML // fx:id="urlTextField"
    private TextField urlTextField; // Value injected by FXMLLoader

    @FXML // Label to contain the implicit location (CustomerDAO's address).
    private Label implicitLocationLabel;

    // TODO: See if this is even used
    @FXML // AppointmentDAO description input control.
    private TextArea descriptionTextArea;

    private TimeOfDaySpinnerValueFactories timeOfDaySpinnerValueFactories;
    
    private HourAndMinuteSpinnerFactories hourAndMinuteSpinnerFactories;
    
    // Items for the customerComboBox control.
    private ObservableList<CustomerModelImpl> customerModelList;

    // Items for the userComboBox control.
    private ObservableList<UserModelImpl> userModelList;

    // Items for the timeZoneComboBox control.
    private ObservableList<TimeZone> timeZones;

    // Items for the typeComboBox control.
    private ObservableList<AppointmentType> types;

    private int currentTimeZoneOffset;

    private StringBinding currentTzDisplay;
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert customerValidationLabel != null : "fx:id=\"customerValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userValidationLabel != null : "fx:id=\"userValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleValidationLabel != null : "fx:id=\"titleValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startHourSpinner != null : "fx:id=\"startHourSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startMinuteSpinner != null : "fx:id=\"startMinuteSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert amPmSpinner != null : "fx:id=\"amPmSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationHourSpinner != null : "fx:id=\"durationHourSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationMinuteSpinner != null : "fx:id=\"durationMinuteSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert currentTimeZoneLabel != null : "fx:id=\"currentTimeZoneLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert currentTimeZoneValue != null : "fx:id=\"currentTimeZoneValue\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationValidationLabel != null : "fx:id=\"locationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationTextArea != null : "fx:id=\"locationTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactValidationLabel != null : "fx:id=\"contactValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactTextField != null : "fx:id=\"contactTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlLabel != null : "fx:id=\"urlLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlValidationLabel != null : "fx:id=\"urlValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlTextField != null : "fx:id=\"urlTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert implicitLocationLabel != null : "fx:id=\"implicitLocationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert descriptionTextArea != null : "fx:id=\"descriptionTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        
        initializeDateTimeControls();
        
        // Get appointment type options.
        types = FXCollections.observableArrayList(AppointmentType.values());
        typeComboBox.setItems(types);
        SingleSelectionModel<AppointmentType> tsm = typeComboBox.getSelectionModel();
        tsm.select(AppointmentType.OTHER);
        onAppointmentTypeChanged(AppointmentType.OTHER);
        tsm.selectedItemProperty().addListener((obj) -> {
            onAppointmentTypeChanged(((ObservableObjectValue<AppointmentType>)obj).getValue());
        });
    }

    private void initializeDateTimeControls() {
        // Initialize options list for time zone combo box.
        timeZones = TimeZoneListCellFactory.getZoneIdOptions();

        timeZoneComboBox.setItems(timeZones);
        currentTimeZoneOffset = (TimeZone.getTimeZone(ZoneId.systemDefault())).getRawOffset();
        // Get the best match to initially select the time zone.
        String zId = TimeZone.getTimeZone(ZoneId.systemDefault()).getID();
        Optional<TimeZone> tz = timeZones.stream().filter((TimeZone t) -> t.getID().equals(zId)).findFirst();
        if (!tz.isPresent()) {
            tz = timeZones.stream().filter((TimeZone t) -> t.getRawOffset() == currentTimeZoneOffset).findFirst();
        }

        timeZoneComboBox.getSelectionModel().select((tz.isPresent()) ? tz.get() : timeZones.get(0));
        
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        startDatePicker.setValue(date.toLocalDate());
        timeOfDaySpinnerValueFactories = new TimeOfDaySpinnerValueFactories(TimeOfDaySpinnerValueFactories.roundToHours(date.toLocalTime()),
                startDatePicker.valueProperty());
        hourAndMinuteSpinnerFactories = new HourAndMinuteSpinnerFactories(1, 0, timeOfDaySpinnerValueFactories.valueProperty());
        startHourSpinner.setValueFactory(timeOfDaySpinnerValueFactories.getHourSpinnerFactory());
        startMinuteSpinner.setValueFactory(timeOfDaySpinnerValueFactories.getMinuteSpinnerFactory());
        amPmSpinner.setValueFactory(timeOfDaySpinnerValueFactories.getAmPmSpinnerFactory());
        durationHourSpinner.setValueFactory(hourAndMinuteSpinnerFactories.getHourSpinnerFactory());
        durationMinuteSpinner.setValueFactory(hourAndMinuteSpinnerFactories.getMinuteSpinnerFactory());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        currentTzDisplay = Bindings.createStringBinding(() -> {
            ZoneId zoneId = timeZoneComboBox.getSelectionModel().getSelectedItem().toZoneId();
            ZonedDateTime start = timeOfDaySpinnerValueFactories.getValue().atZone(zoneId).withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime end = hourAndMinuteSpinnerFactories.getValue().atZone(zoneId).withZoneSameInstant(ZoneId.systemDefault());
            return String.format(getResourceString(RESOURCEKEY_TIMERANGE), start.format(formatter), end.format(formatter));
        }, timeOfDaySpinnerValueFactories.valueProperty(), hourAndMinuteSpinnerFactories.valueProperty(),
        timeZoneComboBox.getSelectionModel().selectedItemProperty());
        currentTzDisplay.addListener((obj) -> {
            currentTimeZoneValue.setText(((StringBinding)obj).get());
        });
        currentTimeZoneValue.setText(currentTzDisplay.get());
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.ADDED)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
    }

    @FXML
    void addCustomerClick(ActionEvent event) {
        AlertHelper.showWarningAlert(((Button) event.getSource()).getScene().getWindow(), LOG, "addCustomerClick not implemented");
        // TODO: Implement addCustomerClick(ActionEvent event) or see if this is no longer being used.
//        CustomerModelImpl customer = EditCustomer.addNew(getViewManager());
//        if (null == customer)
//            return;
//        customers.add(customer);
//        customerComboBox.getSelectionModel().select(customer);
    }

    @FXML
    void addUserClick(ActionEvent event) {
        AlertHelper.showWarningAlert(((Button) event.getSource()).getScene().getWindow(), LOG, "addUserClick not implemented");
        // TODO: Implement addUserClick(ActionEvent event) or see if this is no longer being used.
//        UserModelImpl user = EditUser.addNew(getViewManager());
//        if (null == user)
//            return;
//        users.add(user);
//        userComboBox.getSelectionModel().select(user);
    }

    @FXML
    void showConflictsButtonClick(ActionEvent event) {
        AlertHelper.showWarningAlert(((Button) event.getSource()).getScene().getWindow(), LOG, "showConflictsButtonClick not implemented");
        // TODO: Implement showConflictsButtonClick(ActionEvent event) or see if this is no longer being used.
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return Bindings.createBooleanBinding(() -> true);
    }

    @Override
    protected ItemModel.ModelFactory<AppointmentDAO, AppointmentModel> getFactory() {
        return AppointmentModel.getFactory();
    }

    private void onAppointmentTypeChanged(AppointmentType type) {
        switch (type) {
            case CORPORATE_HQ_MEETING:
                restoreNode(locationTextArea);
                collapseNode(phoneTextField);
                locationTextArea.setEditable(false);
                break;
            case CUSTOMER_SITE:
                restoreNode(locationTextArea);
                collapseNode(phoneTextField);
                locationTextArea.setEditable(false);
                break;
            case GERMANY_SITE_MEETING:
                restoreNode(locationTextArea);
                collapseNode(phoneTextField);
                locationTextArea.setEditable(false);
                break;
            case HONDURAS_SITE_MEETING:
                restoreNode(locationTextArea);
                collapseNode(phoneTextField);
                locationTextArea.setEditable(false);
                break;
            case INDIA_SITE_MEETING:
                restoreNode(locationTextArea);
                collapseNode(phoneTextField);
                locationTextArea.setEditable(false);
                break;
            case PHONE:
                collapseNode(locationTextArea);
                restoreNode(phoneTextField);
                break;
            case VIRTUAL:
                collapseNode(locationTextArea);
                collapseNode(phoneTextField);
                break;
            default:
                restoreNode(locationTextArea);
                collapseNode(phoneTextField);
                locationTextArea.setEditable(true);
                break;
        }
    }

    private class ItemsLoadTask extends TaskWaiter<Boolean> {

        private List<CustomerDAO> customerDaoList;
        private List<UserDAO> userDaoList;

        public ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_INITIALIZING));
            customerDaoList = null;
            userDaoList = null;
        }

        @Override
        protected void processResult(Boolean result, Stage owner) {
            if (null != customerDaoList && !customerDaoList.isEmpty()) {
                customerDaoList.forEach((c) -> customerModelList.add(new CustomerModelImpl(c)));
            }
            if (null != userDaoList && !userDaoList.isEmpty()) {
                userDaoList.forEach((u) -> userModelList.add(new UserModelImpl(u)));
            }
            customerComboBox.setItems(customerModelList);
            userComboBox.setItems(userModelList);
            int pk = Scheduler.getCurrentUser().getPrimaryKey();
            Optional<UserModelImpl> toSelect = userModelList.stream().filter((UserModel t) -> t.getPrimaryKey() == pk).findFirst();
            if (toSelect.isPresent())
                userComboBox.getSelectionModel().select(toSelect.get());
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            AlertHelper.showErrorAlert(owner, LOG, ex);
            owner.close();
        }

        @Override
        protected Boolean getResult(Connection connection) throws SQLException {
            CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            customerDaoList = cf.load(connection, cf.getActiveStatusFilter(true));
            userDaoList = uf.load(connection, uf.getActiveUsersFilter());
            return null == customerDaoList || null == userDaoList || customerDaoList.isEmpty() || userDaoList.isEmpty();
        }

    }

}
