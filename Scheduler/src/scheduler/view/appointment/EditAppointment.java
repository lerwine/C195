package scheduler.view.appointment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import static scheduler.AppResourceKeys.RESOURCEKEY_ERRORLOADINGEDITWINDOWCONTENT;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.UserFilter;
import scheduler.model.AppointmentType;
import scheduler.model.ModelHelper;
import scheduler.model.UserStatus;
import scheduler.model.db.CustomerRowData;
import scheduler.model.db.UserRowData;
import scheduler.model.predefined.PredefinedAddress;
import scheduler.model.predefined.PredefinedData;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserItem;
import scheduler.observables.BindingHelper;
import scheduler.observables.CssClassSwitchBinding;
import scheduler.util.LogHelper;
import scheduler.util.ViewControllerLoader;
import scheduler.view.EditItem;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.MainController;
import scheduler.view.ViewAndController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.RelatedCustomer;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventType;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.RelatedUser;
import scheduler.view.user.UserModel;

/**
 * FXML Controller class for editing an {@link AppointmentModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/appointment/EditAppointment.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public final class EditAppointment extends EditItem.EditController<AppointmentDAO, AppointmentModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAppointment.class.getName()), Level.FINE);

    public static AppointmentModel editNew(MainController mainController, Stage stage, CustomerRowData customer, UserRowData user) throws IOException {
        return editNew(EditAppointment.class, mainController, stage, (FxmlViewControllerEventListener<StackPane, EditAppointment>) (FxmlViewControllerEvent<StackPane, EditAppointment> event) -> {
            if (event.getType() == FxmlViewEventType.LOADED) {
                EditAppointment controller = event.getController();
                if (null != user)
                    controller.getModel().setUser(new RelatedUser(user));
                if (null != customer)
                    controller.getModel().setCustomer(new RelatedCustomer(customer));
            }
        });
    }

    public static AppointmentModel edit(AppointmentModel model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditAppointment.class, mainController, stage);
    }

    @FXML
    private StackPane rootStackPane;

    @FXML // fx:id="titleValidationLabel"
    private Label titleValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="titleTextField"
    private TextField titleTextField; // Value injected by FXMLLoader

    @FXML // fx:id="customerValidationLabel"
    private Label customerValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="userValidationLabel"
    private Label userValidationLabel; // Value injected by FXMLLoader

    @FXML // CustomerDAO selection control
    private ComboBox<CustomerModel> customerComboBox;

    @FXML // UserDAO selection control.
    private ComboBox<UserModel> userComboBox;

    @FXML // fx:id="lowerLeftVBox"
    private VBox lowerLeftVBox;

    @FXML // fx:id="locationLabel"
    private Label locationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="corporateLocationComboBox"
    private ComboBox<PredefinedAddress> corporateLocationComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="locationValidationLabel"
    private Label locationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="locationTextArea"
    private TextArea locationTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="phoneTextField"
    private TextField phoneTextField; // Value injected by FXMLLoader

    @FXML // fx:id="implicitLocationLabel"
    private Label implicitLocationLabel; // Value injected by FXMLLoader

    @FXML // AppointmentDAO type selection control.
    private ComboBox<AppointmentType> typeComboBox;

    @FXML // fx:id="contactValidationLabel"
    private Label contactValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="contactTextField"
    private TextField contactTextField; // Value injected by FXMLLoader

    @FXML // fx:id="urlValidationLabel"
    private Label urlValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="urlTextField"
    private TextField urlTextField; // Value injected by FXMLLoader

    @FXML // fx:id="descriptionTextArea"
    private TextArea descriptionTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsBorderPane"
    private BorderPane dropdownOptionsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsInactiveRadioButton"
    private RadioButton dropdownOptionsInactiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptions"
    private ToggleGroup dropdownOptions; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsLabel"
    private Label dropdownOptionsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsActiveRadioButton"
    private RadioButton dropdownOptionsActiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsAllRadioButton"
    private RadioButton dropdownOptionsAllRadioButton; // Value injected by FXMLLoader

    // Items for the customerComboBox control.
    private ObservableList<CustomerModel> customerModelList;

    // Items for the corporateLocationComboBox control.
    private ObservableList<PredefinedAddress> corporateLocationList;

    // Items for the userComboBox control.
    private ObservableList<UserModel> userModelList;

    private SingleSelectionModel<AppointmentType> typeSelectionModel;
    private SingleSelectionModel<CustomerModel> customerSelectionModel;
    private SingleSelectionModel<UserModel> userSelectionModel;
    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;
    private SingleSelectionModel<PredefinedAddress> corporateLocationSelectionModel;
    private DateRange dateRangeController;
    private AppointmentConflicts appointmentConflictsController;

    @FXML
    private void onCustomerDropDownOptionsButtonAction(ActionEvent event) {
        editingUserOptions = false;
        if (showActiveCustomers.isPresent()) {
            dropdownOptions.selectToggle((showActiveCustomers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(getResourceString(RESOURCEKEY_CUSTOMERSTOSHOW));
        dropdownOptionsBorderPane.setVisible(true);
        dropdownOptionsBorderPane.minWidthProperty().bind(rootStackPane.widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(rootStackPane.widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(rootStackPane.heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(rootStackPane.heightProperty());
    }

    @FXML
    private void onUserDropDownOptionsButtonAction(ActionEvent event) {
        editingUserOptions = true;
        if (showActiveUsers.isPresent()) {
            dropdownOptions.selectToggle((showActiveUsers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(getResourceString(RESOURCEKEY_USERSTOSHOW));
        dropdownOptionsBorderPane.setVisible(true);
        dropdownOptionsBorderPane.minWidthProperty().bind(rootStackPane.widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(rootStackPane.widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(rootStackPane.heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(rootStackPane.heightProperty());
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
            TaskWaiter.startNow(new UserReloadTask((Stage) ((Button) event.getSource()).getScene().getWindow()));
        } else {
            if (dropdownOptionsInactiveRadioButton.isSelected()) {
                showActiveCustomers = Optional.of(false);
            } else if (dropdownOptionsAllRadioButton.isSelected()) {
                showActiveCustomers = Optional.empty();
            } else {
                showActiveCustomers = Optional.of(true);
            }
            TaskWaiter.startNow(new CustomerReloadTask((Stage) ((Button) event.getSource()).getScene().getWindow()));
        }
        dropdownOptionsBorderPane.setVisible(false);
    }

    @SuppressWarnings({"unchecked", "incomplete-switch"})
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleValidationLabel != null : "fx:id=\"titleValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
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
        assert dropdownOptionsInactiveRadioButton != null : "fx:id=\"dropdownOptionsInactiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptions != null : "fx:id=\"dropdownOptions\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsLabel != null : "fx:id=\"dropdownOptionsLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsActiveRadioButton != null : "fx:id=\"dropdownOptionsActiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsAllRadioButton != null : "fx:id=\"dropdownOptionsAllRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        customerSelectionModel = customerComboBox.getSelectionModel();
        userSelectionModel = userComboBox.getSelectionModel();
        typeSelectionModel = typeComboBox.getSelectionModel();
        corporateLocationSelectionModel = corporateLocationComboBox.getSelectionModel();

        corporateLocationList = FXCollections.observableArrayList();
        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        showActiveCustomers = Optional.of(true);
        showActiveUsers = Optional.of(true);
        PredefinedData.getCityMap().values().stream().flatMap((c) -> c.getAddresses().stream()).sorted((PredefinedAddress o1, PredefinedAddress o2) -> {
            if (o1.isMainOffice()) {
                if (!o2.isMainOffice()) {
                    return -1;
                }
            } else if (o2.isMainOffice()) {
                return 1;
            }
            int result = o1.getCountryName().compareTo(o2.getCountryName());
            if (result == 0 && (result = o1.getCityName().compareTo(o2.getCityName())) == 0
                    && (result = o1.getPostalCode().compareTo(o2.getPostalCode())) == 0) {
                return o1.addressLinesProperty().getValue().compareTo(o2.addressLinesProperty().getValue());
            }
            return result;
        }).forEach((t) -> corporateLocationList.add(t));

        BooleanBinding titleNotValid = BindingHelper.isNullOrWhiteSpace(titleTextField.textProperty());
        titleValidationLabel.visibleProperty().bind(titleNotValid);

        BooleanBinding customerNotValid = customerSelectionModel.selectedItemProperty().isNull();
        customerValidationLabel.visibleProperty().bind(customerNotValid);

        BooleanBinding userNotValid = userSelectionModel.selectedItemProperty().isNull();
        userValidationLabel.visibleProperty().bind(userNotValid);

        BooleanBinding contactNotValid = typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.OTHER)
                .and(BindingHelper.isNullOrWhiteSpace(contactTextField.textProperty()));
        contactValidationLabel.visibleProperty().bind(contactNotValid);

        locationLabel.textProperty().bind(Bindings.when(typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.PHONE))
                .then(getResourceString(RESOURCEKEY_PHONENUMBER))
                .otherwise(getResourceString(RESOURCEKEY_LOCATIONLABELTEXT)));

        CssClassSwitchBinding.collapseIfTrue(corporateLocationComboBox, typeSelectionModel.selectedItemProperty().isNotEqualTo(AppointmentType.CORPORATE_LOCATION));
        CssClassSwitchBinding.collapseIfTrue(locationTextArea, typeSelectionModel.selectedItemProperty().isNotEqualTo(AppointmentType.OTHER));
        CssClassSwitchBinding.collapseIfTrue(phoneTextField, typeSelectionModel.selectedItemProperty().isNotEqualTo(AppointmentType.PHONE));
        CssClassSwitchBinding.collapseIfTrue(implicitLocationLabel, typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.PHONE)
                .or(typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.VIRTUAL))
                .or(typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.OTHER)));

        BooleanBinding corporateLocationIsInvalid = typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.CORPORATE_LOCATION).and(corporateLocationSelectionModel.selectedItemProperty().isNull());
        BooleanBinding locationTextAreaIsInvalid = typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.OTHER)
                .and(BindingHelper.isNullOrWhiteSpace(locationTextArea.textProperty()));
        BooleanBinding phoneTextFieldIsInvalid = typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.PHONE)
                .and(BindingHelper.isNullOrWhiteSpace(phoneTextField.textProperty()));
        BooleanBinding isLocationInvalid = corporateLocationIsInvalid.or(locationTextAreaIsInvalid).or(phoneTextFieldIsInvalid);

        locationValidationLabel.visibleProperty().bind(isLocationInvalid);

        StringBinding parseValidationMsg = Bindings.when(BindingHelper.isNullOrWhiteSpace(urlTextField.textProperty()))
                .then(Bindings.when(typeSelectionModel.selectedItemProperty().isEqualTo(AppointmentType.VIRTUAL))
                        .then(getResourceString(RESOURCEKEY_REQUIRED))
                        .otherwise("")
                )
                .otherwise(Bindings.createStringBinding(() -> {
                    try {
                        (new URI(urlTextField.getText())).toURL();
                    } catch (MalformedURLException | IllegalArgumentException ex) {
                        return getResourceString(RESOURCEKEY_INVALIDURL);
                    }
                    return "";
                }, urlTextField.textProperty()));

        urlValidationLabel.textProperty().bind(parseValidationMsg);
        urlValidationLabel.visibleProperty().bind(parseValidationMsg.isNotEmpty());

        corporateLocationComboBox.setItems(corporateLocationList);

        // Get appointment type options.
        typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
        typeSelectionModel.select(AppointmentType.OTHER);

    }
    
    @SuppressWarnings("incomplete-switch")
    @HandlesFxmlViewEvent(FxmlViewEventHandling.LOADED)
    protected void onLoaded(FxmlViewEvent<? extends Parent> event) {
        try {
            ViewAndController<GridPane, DateRange> drVc = ViewControllerLoader.loadViewAndController(DateRange.class);
            ViewAndController<BorderPane, AppointmentConflicts> acVc = ViewControllerLoader.loadViewAndController(AppointmentConflicts.class);
            dateRangeController = drVc.getController();
            GridPane gp = drVc.getView();
            lowerLeftVBox.getChildren().add(0, gp);
            gp.prefWidthProperty().bind(lowerLeftVBox.widthProperty());
            gp.minWidthProperty().bind(lowerLeftVBox.widthProperty());
            appointmentConflictsController = acVc.getController();
            BorderPane bp = acVc.getView();
            rootStackPane.getChildren().add(bp);
            bp.setVisible(false);
            bp.prefHeightProperty().bind(rootStackPane.heightProperty());
            bp.minHeightProperty().bind(rootStackPane.heightProperty());
            bp.prefWidthProperty().bind(rootStackPane.widthProperty());
            bp.minWidthProperty().bind(rootStackPane.widthProperty());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_ERRORLOADINGEDITWINDOWCONTENT), event.getStage(), ex);
        }
        
    }

    @SuppressWarnings("incomplete-switch")
    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        AppointmentModel model = this.getModel();
        event.getStage().setTitle(getResourceString((model.isNewItem()) ? RESOURCEKEY_ADDNEWAPPOINTMENT : RESOURCEKEY_EDITAPPOINTMENT));

        typeSelectionModel.select(model.getType());
        titleTextField.setText(model.getTitle());
        contactTextField.setText(model.getContact());
        if (null != dateRangeController) {
            LocalDateTime start = model.getStart();
            LocalDateTime end = model.getEnd();
            ZoneId z = AppointmentModel.getZoneId(model);
            Duration duration;
            if (null != start && null != end) {
                duration = Duration.between(start, end);
            } else {
                duration = null;
            }
            dateRangeController.setDateRange(start, duration, TimeZone.getTimeZone(z));
            TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        }
        switch (typeSelectionModel.getSelectedItem()) {
            case OTHER:
                locationTextArea.setText(model.getLocation());
                break;
            case PHONE:
                phoneTextField.setText(model.getLocation());
                break;
            // CURRENT: Set selection for corporate
        }
        urlTextField.setText(model.getUrl());
        descriptionTextArea.setText(model.getDescription());
    }

    @SuppressWarnings("incomplete-switch")
    @HandlesFxmlViewEvent(FxmlViewEventHandling.SHOWN)
    protected void onShown(FxmlViewEvent<? extends Parent> event) {
        LOG.info("shown");
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return dateRangeController.getValid().and(
                contactValidationLabel.visibleProperty()
                        .or(locationValidationLabel.visibleProperty())
                        .or(titleValidationLabel.visibleProperty())
                        .or(customerValidationLabel.visibleProperty())
                        .or(userValidationLabel.visibleProperty())
                        .or(urlValidationLabel.visibleProperty()).not()
        );
    }

    @Override
    protected FxRecordModel.ModelFactory<AppointmentDAO, AppointmentModel> getFactory() {
        return AppointmentModel.getFactory();
    }

    @Override
    protected void updateModel(AppointmentModel model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        CustomerModel customer = customerSelectionModel.getSelectedItem();
        assert null != customer : "No customer is selected";
        assert customer.getDataObject().isExisting() : "Customer does not exist in database";
        UserModel user = userSelectionModel.getSelectedItem();
        assert null != user : "No user is selected";
        assert user.getDataObject().isExisting() : "User does not exist in database";
        String title = titleTextField.getText().trim();
        assert !title.isEmpty() : "Title is empty";
        AppointmentType type = typeSelectionModel.getSelectedItem();
        assert null != type : "Type is not selected";
        ZonedDateTime zdtStart = dateRangeController.getStartDateTime();
        if (null == zdtStart) {
            throw new IllegalStateException("Start date/time is not valid");
        }
        ZonedDateTime zdtEnd = dateRangeController.getEndDateTime();
        if (null == zdtEnd) {
            throw new IllegalStateException("End date/time is not valid");
        }
        model.setTitle(title);
        model.setCustomer(customer);
        model.setUser(user);
        model.setUrl(urlTextField.getText().trim());
        model.setType(type);
        model.setStart(zdtStart.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        model.setEnd(zdtEnd.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        switch (type) {
            case PHONE:
                model.setLocation(phoneTextField.getText().trim());
                break;
            case OTHER:
                model.setLocation(locationTextArea.getText().trim());
                break;
            default:
                model.setLocation("");
                break;
        }
        model.setDescription(descriptionTextArea.getText().trim());
        model.setContact(contactTextField.getText().trim());
    }

    private class CustomerReloadTask extends TaskWaiter<List<CustomerDAO>> {

        private final Optional<Boolean> loadOption;

        private CustomerReloadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCUSTOMERS));
            loadOption = showActiveCustomers;
        }

        @Override
        protected void processResult(List<CustomerDAO> result, Stage stage) {
            Optional<Boolean> currentOption = showActiveCustomers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
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
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<CustomerDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
            if (loadOption.isPresent()) {
                return cf.load(connection, cf.getActiveStatusFilter(loadOption.get()));
            }
            return cf.load(connection, cf.getAllItemsFilter());
        }

    }

    private class UserReloadTask extends TaskWaiter<List<UserDAO>> {

        private final Optional<Boolean> loadOption;

        private UserReloadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGUSERS));
            loadOption = showActiveUsers;
        }

        @Override
        protected void processResult(List<UserDAO> result, Stage stage) {
            Optional<Boolean> currentOption = showActiveUsers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
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
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<UserDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            if (loadOption.isPresent()) {
                if (loadOption.get()) {
                    return uf.load(connection, uf.getActiveUsersFilter());
                }
                return uf.load(connection, UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
            }
            return uf.load(connection, uf.getAllItemsFilter());
        }

    }

    private class ItemsLoadTask extends TaskWaiter<List<AppointmentDAO>> {

        private List<CustomerDAO> customerDaoList;
        private List<UserDAO> userDaoList;
        private List<AppointmentDAO> appointments;
        private final Optional<Boolean> customerLoadOption;
        private final Optional<Boolean> userLoadOption;
        private final CustomerRowData appointmentCustomer;
        private final UserRowData appointmentUser;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_INITIALIZING));
            customerDaoList = null;
            userDaoList = null;
            AppointmentModel model = getModel();
            CustomerItem<? extends CustomerRowData> customer = model.getCustomer();
            appointmentCustomer = (null == customer) ? null : customer.getDataObject();
            UserItem<? extends UserRowData> user = model.getUser();
            appointmentUser = (null == user) ? null : user.getDataObject();
            customerLoadOption = showActiveCustomers;
            userLoadOption = showActiveUsers;
        }

        @Override
        protected void processResult(List<AppointmentDAO> result, Stage owner) {
            if (null != customerDaoList && !customerDaoList.isEmpty()) {
                customerDaoList.forEach((t) -> customerModelList.add(new CustomerModel(t)));
            }
            if (null != userDaoList && !userDaoList.isEmpty()) {
                userDaoList.forEach((t) -> userModelList.add(new UserModel(t)));
            }
            customerComboBox.setItems(customerModelList);
            userComboBox.setItems(userModelList);
            CustomerItem<? extends CustomerRowData> customer = getModel().getCustomer();
            if (null != customer) {
                int cpk = customer.getPrimaryKey();
                customerModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                        -> customerComboBox.getSelectionModel().select(t));
            }
            UserItem<? extends UserRowData> user = getModel().getUser();
            int upk = (null == user) ? Scheduler.getCurrentUser().getPrimaryKey() : user.getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == upk).findFirst().ifPresent((t)
                    -> userSelectionModel.select(t));
            appointmentConflictsController.initializeConflicts(appointments, customerSelectionModel, userSelectionModel, dateRangeController);
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), owner, ex);
            owner.close();
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
            if (customerLoadOption.isPresent()) {
                customerDaoList = cf.load(connection, cf.getActiveStatusFilter(customerLoadOption.get()));
            } else {
                customerDaoList = cf.load(connection, cf.getAllItemsFilter());
            }
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
            if (userLoadOption.isPresent()) {
                if (userLoadOption.get()) {
                    userDaoList = uf.load(connection, uf.getActiveUsersFilter());
                } else {
                    userDaoList = uf.load(connection, UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                }
            } else {
                userDaoList = uf.load(connection, uf.getAllItemsFilter());
            }

            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
            if (null != customerDaoList && null != userDaoList && !(customerDaoList.isEmpty() || userDaoList.isEmpty())) {
                if (null != appointmentCustomer && ModelHelper.existsInDatabase(appointmentCustomer)) {
                    if (null != appointmentUser && ModelHelper.existsInDatabase(appointmentUser)) {
                        return af.load(connection, AppointmentFilter.of(appointmentCustomer, appointmentUser, null, null));
                    }
                    return af.load(connection, AppointmentFilter.of(appointmentCustomer, null, null, null));
                }
                if (null != appointmentUser && ModelHelper.existsInDatabase(appointmentUser)) {
                    return af.load(connection, AppointmentFilter.of(null, appointmentUser, null, null));
                }
            }
            return null;
        }

    }

}
