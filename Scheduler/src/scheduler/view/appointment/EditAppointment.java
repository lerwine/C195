package scheduler.view.appointment;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.UserFilter;
import scheduler.model.AppointmentType;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserItem;
import scheduler.model.ui.UserModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import scheduler.util.ViewControllerLoader;
import scheduler.view.EditItem;
import scheduler.view.ViewAndController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.events.AppointmentEvent;
import scheduler.events.EventEvaluationStatus;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing an {@link AppointmentModel}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public final class EditAppointment extends StackPane implements EditItem.ModelEditor<AppointmentDAO, AppointmentModel, AppointmentEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAppointment.class.getName()), Level.FINE);

    public static AppointmentModel editNew(CustomerItem<? extends Customer> customer, UserItem<? extends User> user,
            Window parentWindow, boolean keepOpen) throws IOException {
        AppointmentModel.Factory factory = AppointmentModel.FACTORY;
        AppointmentModel model = factory.createNew(factory.getDaoFactory().createNew());
        if (null != customer) {
            model.setCustomer(customer);
        }
        if (null != user) {
            model.setUser(user);
        }
        return EditItem.showAndWait(parentWindow, EditAppointment.class, model, keepOpen);
    }

    public static AppointmentModel edit(AppointmentModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditAppointment.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    // Items for the customerComboBox control.
    private final ObservableList<CustomerModel> customerModelList;

    // Items for the corporateLocationComboBox control.
    private final ObservableList<AddressDAO> corporateLocationList;
    private final ObservableList<AddressDAO> remoteLocationList;

    // Items for the userComboBox control.
    private final ObservableList<UserModel> userModelList;

    private final HashSet<String> invalidControlIds;

    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;
    private AppointmentConflicts appointmentConflictsController;

    @ModelEditor
    private AppointmentModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="titleTextField"
    private TextField titleTextField; // Value injected by FXMLLoader

    @FXML // fx:id="titleValidationLabel"
    private Label titleValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="customerComboBox"
    private ComboBox<CustomerModel> customerComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="userComboBox"
    private ComboBox<UserModel> userComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="lowerLeftVBox"
    private VBox lowerLeftVBox; // Value injected by FXMLLoader

    @FXML // fx:id="dateRangeControl"
    private DateRangeControl dateRangeControl;

    @FXML // fx:id="locationLabel"
    private Label locationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="includeRemoteCheckBox"
    private CheckBox includeRemoteCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="corporateLocationComboBox"
    private ComboBox<AddressDAO> corporateLocationComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="locationTextArea"
    private TextArea locationTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="phoneTextField"
    private TextField phoneTextField; // Value injected by FXMLLoader

    @FXML // fx:id="implicitLocationLabel"
    private Label implicitLocationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="locationValidationLabel"
    private Label locationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="typeComboBox"
    private ComboBox<AppointmentType> typeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="contactTextField"
    private TextField contactTextField; // Value injected by FXMLLoader

    @FXML // fx:id="urlTextField"
    private TextField urlTextField; // Value injected by FXMLLoader

    @FXML // fx:id="urlValidationLabel"
    private Label urlValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="descriptionTextArea"
    private TextArea descriptionTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="customerValidationLabel"
    private Label customerValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="userValidationLabel"
    private Label userValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="contactValidationLabel"
    private Label contactValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsBorderPane"
    private BorderPane dropdownOptionsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsLabel"
    private Label dropdownOptionsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsActiveRadioButton"
    private RadioButton dropdownOptionsActiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptions"
    private ToggleGroup dropdownOptions; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsInactiveRadioButton"
    private RadioButton dropdownOptionsInactiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsAllRadioButton"
    private RadioButton dropdownOptionsAllRadioButton; // Value injected by FXMLLoader

    public EditAppointment() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        invalidControlIds = new HashSet<>();
        corporateLocationList = FXCollections.observableArrayList();
        remoteLocationList = FXCollections.observableArrayList();
        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        showActiveCustomers = Optional.of(true);
        showActiveUsers = Optional.of(true);
        addEventHandler(AppointmentEvent.INSERTING_EVENT_TYPE, this::onAppointmentUpdating);
        addEventHandler(AppointmentEvent.UPDATING_EVENT_TYPE, this::onAppointmentUpdating);
        addEventHandler(AppointmentEvent.INSERTED_EVENT_TYPE, this::onAppointmentInserted);
    }

    private void onAppointmentUpdating(AppointmentEvent event) {
        if (event.getStatus() != EventEvaluationStatus.EVALUATING) {
            return;
        }
        throw new UnsupportedOperationException("Not supported yet."); // PENDING: (TODO) Implement scheduler.view.appointment.EditAppointment#onAppointmentUpdating
    }
    
    private void onAppointmentInserted(AppointmentEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); // PENDING: (TODO) Implement scheduler.view.appointment.EditAppointment#onAppointmentInserted
    }
    
    @FXML
    private void onCustomerDropDownOptionsButtonAction(ActionEvent event) {
        editingUserOptions = false;
        if (showActiveCustomers.isPresent()) {
            dropdownOptions.selectToggle((showActiveCustomers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(resources.getString(RESOURCEKEY_CUSTOMERSTOSHOW));
        dropdownOptionsBorderPane.setVisible(true);
        dropdownOptionsBorderPane.minWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(heightProperty());

    }

    @FXML
    private void onDropdownOptionsCancelButtonAction(ActionEvent event) {
        dropdownOptionsBorderPane.minWidthProperty().unbind();
        dropdownOptionsBorderPane.prefWidthProperty().unbind();
        dropdownOptionsBorderPane.minHeightProperty().unbind();
        dropdownOptionsBorderPane.prefHeightProperty().unbind();
        dropdownOptionsBorderPane.setVisible(false);
    }

    @FXML
    private void onDropdownOptionsOkButtonAction(ActionEvent event) {
        dropdownOptionsBorderPane.minWidthProperty().unbind();
        dropdownOptionsBorderPane.prefWidthProperty().unbind();
        dropdownOptionsBorderPane.minHeightProperty().unbind();
        dropdownOptionsBorderPane.prefHeightProperty().unbind();
        if (editingUserOptions) {
            if (dropdownOptionsInactiveRadioButton.isSelected()) {
                showActiveUsers = Optional.of(false);
            } else if (dropdownOptionsAllRadioButton.isSelected()) {
                showActiveUsers = Optional.empty();
            } else {
                showActiveUsers = Optional.of(true);
            }
            waitBorderPane.startNow(new UserReloadTask());
        } else {
            if (dropdownOptionsInactiveRadioButton.isSelected()) {
                showActiveCustomers = Optional.of(false);
            } else if (dropdownOptionsAllRadioButton.isSelected()) {
                showActiveCustomers = Optional.empty();
            } else {
                showActiveCustomers = Optional.of(true);
            }
            waitBorderPane.startNow(new CustomerReloadTask());
        }
        dropdownOptionsBorderPane.setVisible(false);
    }

    @FXML
    private void onIncludeRemoteCheckBoxAction(ActionEvent event) {
        if (includeRemoteCheckBox.isSelected()) {
            remoteLocationList.forEach((t) -> {
                if (!corporateLocationList.contains(t)) {
                    corporateLocationList.add(t);
                }
            });
        } else {
            if (remoteLocationList.contains(corporateLocationComboBox.getValue())) {
                corporateLocationComboBox.getSelectionModel().clearSelection();
            }
            remoteLocationList.forEach((t) -> corporateLocationList.remove(t));
        }
    }

    @FXML
    private void onUserDropDownOptionsButtonAction(ActionEvent event) {
        editingUserOptions = true;
        if (showActiveUsers.isPresent()) {
            dropdownOptions.selectToggle((showActiveUsers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(resources.getString(RESOURCEKEY_USERSTOSHOW));
        dropdownOptionsBorderPane.setVisible(true);
        dropdownOptionsBorderPane.minWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(heightProperty());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleValidationLabel != null : "fx:id=\"titleValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert lowerLeftVBox != null : "fx:id=\"lowerLeftVBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert includeRemoteCheckBox != null : "fx:id=\"includeRemoteCheckBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert corporateLocationComboBox != null : "fx:id=\"corporateLocationComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationTextArea != null : "fx:id=\"locationTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert implicitLocationLabel != null : "fx:id=\"implicitLocationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationValidationLabel != null : "fx:id=\"locationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactTextField != null : "fx:id=\"contactTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlTextField != null : "fx:id=\"urlTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlValidationLabel != null : "fx:id=\"urlValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert descriptionTextArea != null : "fx:id=\"descriptionTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerValidationLabel != null : "fx:id=\"customerValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userValidationLabel != null : "fx:id=\"userValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactValidationLabel != null : "fx:id=\"contactValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsBorderPane != null : "fx:id=\"dropdownOptionsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsLabel != null : "fx:id=\"dropdownOptionsLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsActiveRadioButton != null : "fx:id=\"dropdownOptionsActiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptions != null : "fx:id=\"dropdownOptions\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsInactiveRadioButton != null : "fx:id=\"dropdownOptionsInactiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsAllRadioButton != null : "fx:id=\"dropdownOptionsAllRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        // PENDING: (FIXME) Reimplement
//        PredefinedData.getCityMap().values().stream().flatMap((c) -> c.getAddresses().stream()).map((t) -> {
//            AddressDAO dao = t.getDataAccessObject();
//            if (null == dao) {
//                (dao = new AddressDAO()).setPredefinedElement(t);
//            }
//            return dao;
//        }).sorted((AddressDAO o1, AddressDAO o2) -> {
//            AddressDAO.PredefinedAddressElement pd1 = o1.getPredefinedElement();
//            AddressDAO.PredefinedAddressElement pd2 = o2.getPredefinedElement();
//            if (pd1.isMainOffice()) {
//                if (!pd2.isMainOffice()) {
//                    return -1;
//                }
//            } else if (pd2.isMainOffice()) {
//                return 1;
//            }
//            ICityDAO c1 = o1.getCity();
//            ICityDAO c2 = o2.getCity();
//            int result = c1.getCountry().getName().compareTo(c2.getCountry().getName());
//            if (result == 0 && (result = c1.getName().compareTo(c2.getName())) == 0
//                    && (result = o1.getPostalCode().compareTo(o2.getPostalCode())) == 0
//                    && (result = o1.getAddress1().compareTo(o2.getAddress2())) == 0) {
//                return o1.getAddress2().compareTo(o2.getAddress2());
//            }
//            return result;
//        }).forEach((t) -> {
//            if (t.getPredefinedElement().isMainOffice()) {
//                corporateLocationList.add(t);
//            } else {
//                remoteLocationList.add(t);
//            }
//        });
        invalidControlIds.add(titleTextField.getId());
        invalidControlIds.add(customerComboBox.getId());
        invalidControlIds.add(contactTextField.getId());
        invalidControlIds.add(locationTextArea.getId());
        invalidControlIds.add(userComboBox.getId());
        invalidControlIds.add(userComboBox.getId());

        corporateLocationComboBox.setItems(corporateLocationList);

        // Get appointment type options.
        typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
        typeComboBox.getSelectionModel().select(AppointmentType.OTHER);

        try {
            ViewAndController<BorderPane, AppointmentConflicts> acVc = ViewControllerLoader.loadViewAndController(AppointmentConflicts.class);
            appointmentConflictsController = acVc.getController();
            BorderPane bp = acVc.getView();
            getChildren().add(bp);
            bp.setVisible(false);
            bp.prefHeightProperty().bind(heightProperty());
            bp.minHeightProperty().bind(heightProperty());
            bp.prefWidthProperty().bind(widthProperty());
            bp.minWidthProperty().bind(widthProperty());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error while checking for new appointments", ex);
            AlertHelper.showErrorAlert(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORTITLE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_ERRORLOADINGAPPOINTMENTS));
        }
        windowTitle.set(resources.getString((model.isNewRow()) ? RESOURCEKEY_ADDNEWAPPOINTMENT : RESOURCEKEY_EDITAPPOINTMENT));
        SingleSelectionModel<AppointmentType> typeSelectionModel = typeComboBox.getSelectionModel();
        typeSelectionModel.select(model.getType());
        titleTextField.setText(model.getTitle());
        contactTextField.setText(model.getContact());
        LocalDateTime start = model.getStart();
        LocalDateTime end = model.getEnd();
        CustomerItem<? extends ICustomerDAO> c = model.getCustomer();
        TimeZone z = null;
        if (null != c) {
            AddressItem<? extends IAddressDAO> a = c.getAddress();
            if (null != a) {
                CityItem<? extends ICityDAO> t = a.getCity();
                if (null != t) {
                    z = t.getTimeZone();
                }
            }
        }
        if (null == z) {
            z = TimeZone.getTimeZone(ZoneId.systemDefault());
        }
        Duration duration;
        if (null != start && null != end) {
            duration = Duration.between(start, end);
        } else {
            duration = null;
        }
        dateRangeControl.setDateRange(start, duration, z);
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        waitBorderPane.startNow(pane, new ItemsLoadTask());

        switch (typeSelectionModel.getSelectedItem()) {
            case OTHER:
                locationTextArea.setText(model.getLocation());
                break;
            case PHONE:
                phoneTextField.setText(model.getLocation());
                break;
            case CORPORATE_LOCATION:
//                corporateLocationComboBox.getSelectionModel().select(PredefinedData.lookupAddress(model.getLocation()));
                break;
            default:
                model.setLocation("");
                break;
        }
        urlTextField.setText(model.getUrl());
        descriptionTextArea.setText(model.getDescription());
    }

    public boolean applyChangesToModel() {
        ZonedAppointmentTimeSpan ts = dateRangeControl.getTimeSpan();
        LocalDateTime apptStart = ts.toZonedStartDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime apptEnd = ts.toZonedEndDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime busStart;
        LocalDateTime busEnd;
        try {
            busStart = apptStart.toLocalDate().atTime(AppResources.getBusinessHoursStart());
            busEnd = busStart.plusHours(AppResources.getBusinessHoursDuration());
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting application-configured business hours", ex);
            AlertHelper.showErrorAlert(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORTITLE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_SETTINGSLOADERROR));
            return false;
        }
        Optional<ButtonType> response;
        if (!appointmentConflictsController.isConflictCheckingCurrent()) {
            if (apptStart.compareTo(busEnd) > 0) {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                        resources.getString(RESOURCEKEY_NOTCHECKEDOCCURSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
            } else if (apptEnd.compareTo(busStart) < 0) {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                        resources.getString(RESOURCEKEY_NOTCHECKEDOCCURSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
            } else if (apptStart.compareTo(busStart) < 0) {
                if (apptEnd.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                            resources.getString(RESOURCEKEY_NOTCHECKEDOUTSIDEBUSHRS), ButtonType.YES, ButtonType.NO);
                } else {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                            resources.getString(RESOURCEKEY_NOTCHECKEDSTARTSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                }
            } else if (apptEnd.compareTo(busEnd) > 0) {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                        resources.getString(RESOURCEKEY_NOTCHECKEDENDSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
            } else {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                        resources.getString(RESOURCEKEY_NOTCHECKEDMESSAGE), ButtonType.YES, ButtonType.NO);
            }
        } else if (this.appointmentConflictsController.hasConflicts()) {
            if (apptStart.compareTo(busEnd) > 0) {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                        resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTOCCURSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
            } else if (apptEnd.compareTo(busStart) < 0) {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                        resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTOCCURSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
            } else if (apptStart.compareTo(busStart) < 0) {
                if (apptEnd.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                            resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTOUTSIDEBUSHRS), ButtonType.YES, ButtonType.NO);
                } else {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                            resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTSTARTSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                }
            } else if (apptEnd.compareTo(busEnd) > 0) {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                        resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTENDSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
            } else {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                        resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTMESSAGE), ButtonType.YES, ButtonType.NO);
            }
        } else if (apptStart.compareTo(busEnd) > 0) {
            response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                    resources.getString(RESOURCEKEY_BUSHREXCOCCURSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
        } else if (apptEnd.compareTo(busStart) < 0) {
            response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                    resources.getString(RESOURCEKEY_BUSHREXCOCCURSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
        } else if (apptStart.compareTo(busStart) < 0) {
            if (apptEnd.compareTo(busEnd) > 0) {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                        resources.getString(RESOURCEKEY_BUSHREXCOUTSIDEBUSHRS), ButtonType.YES, ButtonType.NO);
            } else {
                response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                        resources.getString(RESOURCEKEY_BUSHREXCSTARTSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
            }
        } else if (apptEnd.compareTo(busEnd) > 0) {
            response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                    resources.getString(RESOURCEKEY_BUSHREXCENDSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
        } else {
            response = Optional.of(ButtonType.YES);
        }

        return response.isPresent() && response.get() == ButtonType.YES;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public boolean isModified() {
        return modified.get();
    }

    @Override
    public ReadOnlyBooleanProperty modifiedProperty() {
        return modified.getReadOnlyProperty();
    }

    @Override
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    CustomerModel getCustomer() {
        return customerComboBox.getValue();
    }

    UserModel getUser() {
        return userComboBox.getValue();
    }

    DateRangeControl getDateRangeControl() {
        return dateRangeControl;
    }

    @Override
    public FxRecordModel.ModelFactory<AppointmentDAO, AppointmentModel, AppointmentEvent> modelFactory() {
        return AppointmentModel.FACTORY;
    }

    private class CustomerReloadTask extends Task<List<CustomerDAO>> {

        private final Optional<Boolean> loadOption;

        private CustomerReloadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            loadOption = showActiveCustomers;
        }

        @Override
        protected void succeeded() {
            List<CustomerDAO> result = getValue();
            Optional<Boolean> currentOption = showActiveCustomers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                SingleSelectionModel<CustomerModel> customerSelectionModel = customerComboBox.getSelectionModel();
                CustomerModel selectedItem = customerSelectionModel.getSelectedItem();
                customerModelList.clear();
                if (null != result && !result.isEmpty()) {
                    result.forEach((t) -> customerModelList.add(new CustomerModel(t)));
                }
                if (null != selectedItem) {
                    int pk = selectedItem.getPrimaryKey();
                    Optional<CustomerModel> matching = customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
                    if (matching.isPresent()) {
                        customerSelectionModel.select(matching.get());
                    } else {
                        customerSelectionModel.clearSelection();
                    }
                }
            }
            super.succeeded();
        }

        @Override
        protected List<CustomerDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
                if (loadOption.isPresent()) {
                    return cf.load(dbConnector.getConnection(), cf.getActiveStatusFilter(loadOption.get()));
                }
                return cf.load(dbConnector.getConnection(), cf.getAllItemsFilter());
            }
        }

    }

    private class UserReloadTask extends Task<List<UserDAO>> {

        private final Optional<Boolean> loadOption;

        private UserReloadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            loadOption = showActiveUsers;
        }

        @Override
        protected void succeeded() {
            List<UserDAO> result = getValue();
            Optional<Boolean> currentOption = showActiveUsers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                SingleSelectionModel<UserModel> userSelectionModel = userComboBox.getSelectionModel();
                UserModel selectedItem = userSelectionModel.getSelectedItem();
                userModelList.clear();
                if (null != result && !result.isEmpty()) {
                    result.forEach((t) -> userModelList.add(new UserModel(t)));
                }
                if (null != selectedItem) {
                    int pk = selectedItem.getPrimaryKey();
                    Optional<UserModel> matching = userModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
                    if (matching.isPresent()) {
                        userSelectionModel.select(matching.get());
                    } else {
                        userSelectionModel.clearSelection();
                    }
                }
            }
            super.succeeded();
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                if (loadOption.isPresent()) {
                    if (loadOption.get()) {
                        return uf.load(dbConnector.getConnection(), uf.getActiveUsersFilter());
                    }
                    return uf.load(dbConnector.getConnection(), UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                }
                return uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
            }
        }

    }

    private class ItemsLoadTask extends Task<List<AppointmentDAO>> {

        private List<CustomerDAO> customerDaoList;
        private List<UserDAO> userDaoList;
        private List<AppointmentDAO> appointments;
        private final Optional<Boolean> customerLoadOption;
        private final Optional<Boolean> userLoadOption;
        private final Customer appointmentCustomer;
        private final User appointmentUser;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            customerDaoList = null;
            userDaoList = null;
            CustomerItem<? extends Customer> customer = model.getCustomer();
            appointmentCustomer = (null == customer) ? null : customer.dataObject();
            UserItem<? extends User> user = model.getUser();
            appointmentUser = (null == user) ? null : user.dataObject();
            customerLoadOption = showActiveCustomers;
            userLoadOption = showActiveUsers;
        }

        @Override
        protected void succeeded() {
            List<AppointmentDAO> result = getValue();
            if (null != customerDaoList && !customerDaoList.isEmpty()) {
                customerDaoList.forEach((t) -> customerModelList.add(new CustomerModel(t)));
            }
            if (null != userDaoList && !userDaoList.isEmpty()) {
                userDaoList.forEach((t) -> userModelList.add(new UserModel(t)));
            }
            customerComboBox.setItems(customerModelList);
            userComboBox.setItems(userModelList);
            CustomerItem<? extends Customer> customer = model.getCustomer();
            if (null != customer) {
                int cpk = customer.getPrimaryKey();
                customerModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                        -> customerComboBox.getSelectionModel().select(t));
            }
            UserItem<? extends User> user = model.getUser();
            int upk = (null == user) ? Scheduler.getCurrentUser().getPrimaryKey() : user.getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == upk).findFirst().ifPresent((t)
                    -> userComboBox.getSelectionModel().select(t));
            if (null != result && !result.isEmpty()) {
                appointments.addAll(result);
            }
            appointmentConflictsController.initializeConflicts(appointments, EditAppointment.this, EditAppointment.this.waitBorderPane);
            super.succeeded();
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
                if (customerLoadOption.isPresent()) {
                    customerDaoList = cf.load(dbConnector.getConnection(), cf.getActiveStatusFilter(customerLoadOption.get()));
                } else {
                    customerDaoList = cf.load(dbConnector.getConnection(), cf.getAllItemsFilter());
                }
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
                if (userLoadOption.isPresent()) {
                    if (userLoadOption.get()) {
                        userDaoList = uf.load(dbConnector.getConnection(), uf.getActiveUsersFilter());
                    } else {
                        userDaoList = uf.load(dbConnector.getConnection(), UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                    }
                } else {
                    userDaoList = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                }

                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS));
                if (null != customerDaoList && null != userDaoList && !(customerDaoList.isEmpty() || userDaoList.isEmpty())) {
                    if (null != appointmentCustomer && ModelHelper.existsInDatabase(appointmentCustomer)) {
                        if (null != appointmentUser && ModelHelper.existsInDatabase(appointmentUser)) {
                            return af.load(dbConnector.getConnection(), AppointmentFilter.of(appointmentCustomer, appointmentUser, null, null));
                        }
                        return af.load(dbConnector.getConnection(), AppointmentFilter.of(appointmentCustomer, null, null, null));
                    }
                    if (null != appointmentUser && ModelHelper.existsInDatabase(appointmentUser)) {
                        return af.load(dbConnector.getConnection(), AppointmentFilter.of(null, appointmentUser, null, null));
                    }
                }
            }
            return null;
        }

    }

}
