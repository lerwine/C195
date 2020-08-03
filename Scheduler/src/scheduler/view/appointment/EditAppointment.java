package scheduler.view.appointment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.UserFilter;
import scheduler.events.AppointmentEvent;
import scheduler.events.CustomerSuccessEvent;
import scheduler.events.UserSuccessEvent;
import static scheduler.model.Appointment.MAX_LENGTH_TITLE;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.PartialUserModel;
import scheduler.model.fx.UserModel;
import scheduler.observables.BindingHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.clearAndSelectEntity;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.isInShownWindow;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ThrowableConsumer;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.task.WaitBorderPane;

/**
 * FXML Controller class for editing an {@link AppointmentModel}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public class EditAppointment extends StackPane implements EditItem.ModelEditorController<AppointmentModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAppointment.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditAppointment.class.getName());

    public static void editNew(PartialCustomerModel<? extends Customer> customer, PartialUserModel<? extends User> user,
            Window parentWindow, boolean keepOpen, Consumer<AppointmentModel> beforeShow) throws IOException {
        AppointmentModel model = AppointmentDAO.FACTORY.createNew().cachedModel(true);
        if (null != customer) {
            model.setCustomer(customer);
        }
        if (null != user) {
            model.setUser(user);
        }
        if (null != beforeShow) {
            beforeShow.accept(model);
        }
        EditItem.showAndWait(parentWindow, EditAppointment.class, model, keepOpen);
    }

    public static void editNew(PartialCustomerModel<? extends Customer> customer, PartialUserModel<? extends User> user,
            Window parentWindow) throws IOException {
        editNew(customer, user, parentWindow, false, null);
    }

    public static void edit(AppointmentModel model, Window parentWindow, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem.showAndWait(parentWindow, EditAppointment.class, model, false, beforeShow);
    }

    public static void edit(AppointmentModel model, Window parentWindow) throws IOException {
        edit(model, parentWindow, null);
    }

    //<editor-fold defaultstate="collapsed" desc="Instance Fields">
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CustomerModel> customerModelList;
    private final ObservableList<UserModel> userModelList;
    private final TypeContextController typeContext;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerInsertEventHandler;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerUpdateEventHandler;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerDeleteEventHandler;
    private final WeakEventHandlingReference<UserSuccessEvent> userInsertEventHandler;
    private final WeakEventHandlingReference<UserSuccessEvent> userUpdateEventHandler;
    private final WeakEventHandlingReference<UserSuccessEvent> userDeleteEventHandler;
    private StringBinding normalizedTitleBinding;
    private StringBinding titleValidationMessage;
    private StringBinding normalizedDescriptionBinding;
    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;

    @ModelEditor
    private EditItem<AppointmentModel, EditAppointment> editWindowRoot;

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

    @FXML // fx:id="checkConflictsButton"
    private Button checkConflictsButton; // Value injected by FXMLLoader

    @FXML // fx:id="showConflictsButton"
    private Button showConflictsButton; // Value injected by FXMLLoader

    @FXML // fx:id="startDatePicker"
    private DatePicker startDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="startHourTextField"
    private TextField startHourTextField; // Value injected by FXMLLoader

    @FXML // fx:id="startMinuteTextField"
    private TextField startMinuteTextField; // Value injected by FXMLLoader

    @FXML // fx:id="amPmComboBox"
    private ComboBox<Boolean> amPmComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="startValidationLabel"
    private Label startValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="durationHourTextField"
    private TextField durationHourTextField; // Value injected by FXMLLoader

    @FXML // fx:id="durationMinuteTextField"
    private TextField durationMinuteTextField; // Value injected by FXMLLoader

    @FXML // fx:id="endDateTimeLabel"
    private Label endDateTimeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="durationValidationLabel"
    private Label durationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="locationLabel"
    private Label locationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="includeRemoteCheckBox"
    private CheckBox includeRemoteCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="corporateLocationComboBox"
    private ComboBox<CorporateAddress> corporateLocationComboBox; // Value injected by FXMLLoader

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

    @FXML // fx:id="appointmentConflicts"
    private BorderPane appointmentConflictsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="conflictingAppointmentsTableView"
    private TableView<AppointmentModel> conflictingAppointmentsTableView; // Value injected by FXMLLoader

    @FXML // fx:id="hideConflictsButton"
    private Button hideConflictsButton; // Value injected by FXMLLoader
    private BooleanBinding validationBinding;
    private BooleanBinding modificationBinding;

    //</editor-fold>
    public EditAppointment() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        typeContext = new TypeContextController(this);
        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        showActiveCustomers = Optional.of(true);
        showActiveUsers = Optional.of(true);
        customerUpdateEventHandler = WeakEventHandlingReference.create(this::onCustomerUpdated);
        customerInsertEventHandler = WeakEventHandlingReference.create(this::onCustomerInserted);
        customerDeleteEventHandler = WeakEventHandlingReference.create(this::onCustomerDeleted);
        userUpdateEventHandler = WeakEventHandlingReference.create(this::onUserUpdated);
        userInsertEventHandler = WeakEventHandlingReference.create(this::onUserInserted);
        userDeleteEventHandler = WeakEventHandlingReference.create(this::onUserDeleted);
    }

    @ModelEditor
    private void onModelInserted(AppointmentEvent event) {
        LOG.entering(LOG.getName(), "onModelInserted", event);
        windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
        LOG.exiting(LOG.getName(), "onModelInserted");
    }

    @FXML
    private void onCustomerDropDownOptionsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCustomerDropDownOptionsButtonAction", event);
        editingUserOptions = false;
        if (showActiveCustomers.orElse(editingUserOptions)) {
            dropdownOptions.selectToggle((showActiveCustomers.get()) ? dropdownOptionsInactiveRadioButton : dropdownOptionsActiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(resources.getString(RESOURCEKEY_CUSTOMERSTOSHOW));
        restoreNode(dropdownOptionsBorderPane);
        dropdownOptionsBorderPane.minWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(heightProperty());
        LOG.exiting(LOG.getName(), "onCustomerDropDownOptionsButtonAction");
    }

    @FXML
    private void onDropdownOptionsCancelButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDropdownOptionsCancelButtonAction", event);
        dropdownOptionsBorderPane.minWidthProperty().unbind();
        dropdownOptionsBorderPane.prefWidthProperty().unbind();
        dropdownOptionsBorderPane.minHeightProperty().unbind();
        dropdownOptionsBorderPane.prefHeightProperty().unbind();
        collapseNode(dropdownOptionsBorderPane);
        LOG.exiting(LOG.getName(), "onDropdownOptionsCancelButtonAction");
    }

    @FXML
    private void onDropdownOptionsOkButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDropdownOptionsOkButtonAction", event);
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
            waitBorderPane.startNow(new UserReloadTask(null));
        } else {
            if (dropdownOptionsInactiveRadioButton.isSelected()) {
                showActiveCustomers = Optional.of(false);
            } else if (dropdownOptionsAllRadioButton.isSelected()) {
                showActiveCustomers = Optional.empty();
            } else {
                showActiveCustomers = Optional.of(true);
            }
            waitBorderPane.startNow(new CustomerReloadTask(null));
        }
        collapseNode(dropdownOptionsBorderPane);
        LOG.exiting(LOG.getName(), "onDropdownOptionsOkButtonAction");
    }

    @FXML
    private void onUserDropDownOptionsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onUserDropDownOptionsButtonAction", event);
        editingUserOptions = true;
        if (showActiveUsers.isPresent()) {
            dropdownOptions.selectToggle((showActiveUsers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(resources.getString(RESOURCEKEY_USERSTOSHOW));
        restoreNode(dropdownOptionsBorderPane);
        dropdownOptionsBorderPane.minWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(heightProperty());
        LOG.exiting(LOG.getName(), "onUserDropDownOptionsButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleValidationLabel != null : "fx:id=\"titleValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerValidationLabel != null : "fx:id=\"customerValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userValidationLabel != null : "fx:id=\"userValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactTextField != null : "fx:id=\"contactTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactValidationLabel != null : "fx:id=\"contactValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startHourTextField != null : "fx:id=\"startHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startMinuteTextField != null : "fx:id=\"startMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert amPmComboBox != null : "fx:id=\"amPmComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert showConflictsButton != null : "fx:id=\"showConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert checkConflictsButton != null : "fx:id=\"checkConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationHourTextField != null : "fx:id=\"durationHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationMinuteTextField != null : "fx:id=\"durationMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert endDateTimeLabel != null : "fx:id=\"endDateTimeLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert includeRemoteCheckBox != null : "fx:id=\"includeRemoteCheckBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert corporateLocationComboBox != null : "fx:id=\"corporateLocationComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationTextArea != null : "fx:id=\"locationTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert implicitLocationLabel != null : "fx:id=\"implicitLocationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationValidationLabel != null : "fx:id=\"locationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlTextField != null : "fx:id=\"urlTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlValidationLabel != null : "fx:id=\"urlValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert descriptionTextArea != null : "fx:id=\"descriptionTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsBorderPane != null : "fx:id=\"dropdownOptionsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsLabel != null : "fx:id=\"dropdownOptionsLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsActiveRadioButton != null : "fx:id=\"dropdownOptionsActiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptions != null : "fx:id=\"dropdownOptions\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsInactiveRadioButton != null : "fx:id=\"dropdownOptionsInactiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsAllRadioButton != null : "fx:id=\"dropdownOptionsAllRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert appointmentConflictsBorderPane != null : "fx:id=\"appointmentConflictsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert hideConflictsButton != null : "fx:id=\"hideConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        typeContext.initialize();

        //<editor-fold defaultstate="collapsed" desc="Title field init">
        titleTextField.setText(model.getTitle());
        normalizedTitleBinding = BindingHelper.asNonNullAndWsNormalized(titleTextField.textProperty());
        titleValidationMessage = Bindings.when(normalizedTitleBinding.isEmpty())
                .then("* Required")
                .otherwise(
                        Bindings.when(normalizedTitleBinding.length().greaterThan(MAX_LENGTH_TITLE))
                                .then("Title too long")
                                .otherwise("")
                );
        titleValidationLabel.visibleProperty().bind(titleValidationMessage.isNotEmpty());

        //</editor-fold>
        customerComboBox.setItems(customerModelList);

        userComboBox.setItems(userModelList);

        //<editor-fold defaultstate="collapsed" desc="description">
        descriptionTextArea.setText(model.getDescription());
        normalizedDescriptionBinding = BindingHelper.asNonNullAndWsNormalizedMultiLine(descriptionTextArea.textProperty());

        //</editor-fold>
        validationBinding = typeContext.validBinding().and(titleValidationMessage.isEmpty());
        LOG.info(String.format("Setting valid to %s", validationBinding.get()));
        valid.set(validationBinding.get());
        validationBinding.addListener((observable, oldValue, newValue) -> {
            LOG.info(String.format("valid changed from %s to %s; modified is %s", oldValue, newValue, modified.get()));
            valid.set(newValue);
        });
        modified.addListener((observable, oldValue, newValue) -> {
            LOG.info(String.format("modified changed from %s to %s; valid is %s", oldValue, newValue, valid.get()));
        });

        if (model.isNewRow()) {
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWAPPOINTMENT));
        } else {
            initEditMode();
        }

        InitializationTask task = new InitializationTask();
        waitBorderPane.startNow(task);
        LOG.exiting(LOG.getName(), "initialize");
    }

    public AppointmentModel getModel() {
        return model;
    }

    public ResourceBundle getResources() {
        return resources;
    }

    public ComboBox<CustomerModel> getCustomerComboBox() {
        return customerComboBox;
    }

    public Label getCustomerValidationLabel() {
        return customerValidationLabel;
    }

    public ComboBox<UserModel> getUserComboBox() {
        return userComboBox;
    }

    public Button getCheckConflictsButton() {
        return checkConflictsButton;
    }

    public Label getUserValidationLabel() {
        return userValidationLabel;
    }

    public TextField getContactTextField() {
        return contactTextField;
    }

    public Label getContactValidationLabel() {
        return contactValidationLabel;
    }

    public ComboBox<AppointmentType> getTypeComboBox() {
        return typeComboBox;
    }

    public ComboBox<CorporateAddress> getCorporateLocationComboBox() {
        return corporateLocationComboBox;
    }

    public TextArea getLocationTextArea() {
        return locationTextArea;
    }

    public TextField getPhoneTextField() {
        return phoneTextField;
    }

    public TextField getUrlTextField() {
        return urlTextField;
    }

    public Label getUrlValidationLabel() {
        return urlValidationLabel;
    }

    public CheckBox getIncludeRemoteCheckBox() {
        return includeRemoteCheckBox;
    }

    public Label getLocationLabel() {
        return locationLabel;
    }

    public Label getLocationValidationLabel() {
        return locationValidationLabel;
    }

    public Label getImplicitLocationLabel() {
        return implicitLocationLabel;
    }

    public Button getShowConflictsButton() {
        return showConflictsButton;
    }

    public Button getHideConflictsButton() {
        return hideConflictsButton;
    }

    public Optional<Boolean> getShowActiveCustomers() {
        return showActiveCustomers;
    }

    public Optional<Boolean> getShowActiveUsers() {
        return showActiveUsers;
    }

    public DatePicker getStartDatePicker() {
        return startDatePicker;
    }

    public TextField getStartHourTextField() {
        return startHourTextField;
    }

    public TextField getStartMinuteTextField() {
        return startMinuteTextField;
    }

    public ComboBox<Boolean> getAmPmComboBox() {
        return amPmComboBox;
    }

    public Label getStartValidationLabel() {
        return startValidationLabel;
    }

    public TextField getDurationHourTextField() {
        return durationHourTextField;
    }

    public TextField getDurationMinuteTextField() {
        return durationMinuteTextField;
    }

    public Label getDurationValidationLabel() {
        return durationValidationLabel;
    }

    public Label getEndDateTimeLabel() {
        return endDateTimeLabel;
    }

    public ObservableList<CustomerModel> getCustomerModelList() {
        return customerModelList;
    }

    public ObservableList<UserModel> getUserModelList() {
        return userModelList;
    }

    public EditItem<AppointmentModel, EditAppointment> getEditWindowRoot() {
        return editWindowRoot;
    }

    public WaitBorderPane getWaitBorderPane() {
        return waitBorderPane;
    }

    public BorderPane getAppointmentConflictsBorderPane() {
        return appointmentConflictsBorderPane;
    }

    public TableView<AppointmentModel> getConflictingAppointmentsTableView() {
        return conflictingAppointmentsTableView;
    }

    @Override
    public EntityModel.EntityModelFactory<AppointmentDAO, AppointmentModel> modelFactory() {
        return AppointmentModel.FACTORY;
    }

    //<editor-fold defaultstate="collapsed" desc="Properties">
    @Override
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
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

    //</editor-fold>
    @Override
    public boolean applyChanges() {
        if (!typeContext.canSave()) {
            return false;
        }

        model.setTitle(normalizedTitleBinding.get());
        model.setContact(typeContext.normalizedContactBinding().get());
        model.setUrl(typeContext.parsedUrlBinding().get().toPrimary(""));
        model.setDescription(normalizedDescriptionBinding.get());
        model.setCustomer(typeContext.selectedCustomerProperty().get());
        model.setUser(typeContext.selectedUserProperty().get());
        model.setType(typeContext.selectedTypeProperty().get());
        model.setStart(typeContext.getStartDateTimeValue());
        model.setEnd(typeContext.getEndDateTimeValue());
        model.setLocation(typeContext.daoLocationBinding().get());
        return true;
    }

    private synchronized void onCustomersLoaded(List<CustomerDAO> customerDaoList) {
        CustomerModel selectedItem = typeContext.selectedCustomerProperty().get();
        customerModelList.clear();
        if (null != customerDaoList && !customerDaoList.isEmpty()) {
            customerDaoList.forEach((t) -> customerModelList.add(t.cachedModel(true)));
        }
        clearAndSelectEntity(customerComboBox, selectedItem);
    }

    private synchronized void onUsersLoaded(List<UserDAO> userDaoList) {
        UserModel selectedItem = typeContext.selectedUserProperty().get();
        if (null != userDaoList && !userDaoList.isEmpty()) {
            userDaoList.forEach((t) -> userModelList.add(t.cachedModel(true)));
        }
        clearAndSelectEntity(userComboBox, selectedItem);
    }

    private void onCustomerInserted(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerInserted", event);
        if (isInShownWindow(this)) {
            CustomerModel entityModel = event.getEntityModel();
            if (entityModel.isActive() == showActiveCustomers.orElse(entityModel.isActive())) {
                customerModelList.add(entityModel);
                customerModelList.sort(ModelHelper.CustomerHelper::compare);
                clearAndSelectEntity(customerComboBox, entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onCustomerInserted");
    }

    private void onCustomerUpdated(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerUpdated", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            if (customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().map((t) -> {
                if (showActiveUsers.map((u) -> u != t.isActive()).orElse(true)) {
                    CustomerModel selectedItem = typeContext.selectedCustomerProperty().get();
                    if (null != selectedItem && selectedItem == t) {
                        customerComboBox.getSelectionModel().clearSelection();
                        customerModelList.remove(t);
                    }
                }
                return false;
            }).orElse(true)) {
                CustomerModel entityModel = event.getEntityModel();
                if (showActiveUsers.map((t) -> t == entityModel.isActive()).orElse(true)) {
                    customerModelList.add(entityModel);
                    customerModelList.sort(ModelHelper.CustomerHelper::compare);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onCustomerUpdated");
    }

    private void onCustomerDeleted(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerDeleted", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> {
                CustomerModel selectedItem = typeContext.selectedCustomerProperty().get();
                if (null != selectedItem && selectedItem == t) {
                    customerComboBox.getSelectionModel().clearSelection();
                }
                customerModelList.remove(t);
            });
        }
        LOG.exiting(LOG.getName(), "onCustomerDeleted");
    }

    private void onUserInserted(UserSuccessEvent event) {
        LOG.entering(LOG.getName(), "onUserInserted", event);
        if (isInShownWindow(this)) {
            UserModel entityModel = event.getEntityModel();
            boolean isActive = entityModel.getStatus() != UserStatus.INACTIVE;
            if (isActive == showActiveUsers.orElse(isActive)) {
                userModelList.add(entityModel);
                userModelList.sort(ModelHelper.UserHelper::compare);
                clearAndSelectEntity(userComboBox, entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onUserInserted");
    }

    private void onUserUpdated(UserSuccessEvent event) {
        LOG.entering(LOG.getName(), "onUserUpdated", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            if (userModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().map((t) -> {
                if (showActiveUsers.map((u) -> u != (t.getStatus() != UserStatus.INACTIVE)).orElse(true)) {
                    UserModel selectedItem = typeContext.selectedUserProperty().get();
                    if (null != selectedItem && selectedItem == t) {
                        userComboBox.getSelectionModel().clearSelection();
                        userModelList.remove(t);
                    }
                }
                return false;
            }).orElse(true)) {
                UserModel entityModel = event.getEntityModel();
                if (showActiveUsers.map((t) -> t == (entityModel.getStatus() != UserStatus.INACTIVE)).orElse(true)) {
                    userModelList.add(entityModel);
                    userModelList.sort(ModelHelper.UserHelper::compare);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onUserUpdated");
    }

    private void onUserDeleted(UserSuccessEvent event) {
        LOG.entering(LOG.getName(), "onUserDeleted", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> {
                UserModel selectedItem = typeContext.selectedUserProperty().get();
                if (null != selectedItem && selectedItem == t) {
                    userComboBox.getSelectionModel().clearSelection();
                }
                userModelList.remove(t);
            });
        }
        LOG.exiting(LOG.getName(), "onUserDeleted");
    }

    private void initEditMode() {
        windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
        modificationBinding = normalizedTitleBinding.isNotEqualTo(model.titleProperty()).or(normalizedDescriptionBinding.isNotEqualTo(model.descriptionProperty()))
                .or(typeContext.modifiedBinding());
        modificationBinding.addListener((observable, oldValue, newValue) -> {
            LOG.info(String.format("modificationBinding changed from %s to %s; valid is %s", oldValue, newValue, valid.get()));
            modified.set(newValue);
        });
        LOG.info(String.format("Setting modified to %s; typeModified is %s", modificationBinding.get(), typeContext.modifiedBinding().get()));
        modified.set(modificationBinding.get());
    }

    private class CustomerReloadTask extends Task<List<CustomerDAO>> {

        private final Optional<Boolean> loadOption;
        private final Connection existingConnection;

        private CustomerReloadTask(Connection existingConnection) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            loadOption = showActiveCustomers;
            this.existingConnection = existingConnection;
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "succeeded");
            Optional<Boolean> currentOption = showActiveCustomers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                EditAppointment.this.onCustomersLoaded(getValue());
            }
            super.succeeded();
            LOG.exiting("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "succeeded");
        }

        @Override
        protected List<CustomerDAO> call() throws Exception {
            LOG.entering("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            List<CustomerDAO> result;
            if (null == existingConnection) {
                try (DbConnector dbConnector = new DbConnector()) {
                    updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                    result = getCustomers(dbConnector.getConnection());
                }
            } else {
                result = getCustomers(existingConnection);
            }
            LOG.exiting("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "call");
            return result;
        }

        private List<CustomerDAO> getCustomers(final Connection connection) throws SQLException {
            List<CustomerDAO> result;
            CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
            if (loadOption.isPresent()) {
                result = cf.load(connection, cf.getActiveStatusFilter(loadOption.get()));
            } else {
                result = cf.load(connection, cf.getAllItemsFilter());
            }
            return result;
        }

    }

    private class UserReloadTask extends Task<List<UserDAO>> {

        private final Optional<Boolean> loadOption;
        private final Connection existingConnection;

        private UserReloadTask(Connection existingConnection) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            loadOption = showActiveUsers;
            this.existingConnection = existingConnection;
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.appointment.EditAppointment.UserReloadTask", "succeeded");
            Optional<Boolean> currentOption = showActiveUsers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                EditAppointment.this.onUsersLoaded(getValue());
            }
            super.succeeded();
            LOG.exiting("scheduler.view.appointment.EditAppointment.UserReloadTask", "succeeded");
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            LOG.entering("scheduler.view.appointment.EditAppointment.UserReloadTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            List<UserDAO> result;
            if (null == existingConnection) {
                try (DbConnector dbConnector = new DbConnector()) {
                    updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                    Connection connection = dbConnector.getConnection();
                    result = getUsers(connection);
                }
            } else {
                result = getUsers(existingConnection);
            }
            LOG.exiting("scheduler.view.appointment.EditAppointment.UserReloadTask", "call");
            return result;
        }

        private List<UserDAO> getUsers(Connection connection) throws SQLException {
            List<UserDAO> result;
            UserDAO.FactoryImpl uf = UserDAO.FACTORY;
            if (loadOption.isPresent()) {
                if (loadOption.get()) {
                    result = uf.load(connection, uf.getActiveUsersFilter());
                } else {
                    result = uf.load(connection, UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                }
            } else {
                result = uf.load(connection, uf.getAllItemsFilter());
            }
            return result;
        }

    }

    private class InitializationTask extends Task<List<AppointmentDAO>> {

        private final Customer appointmentCustomer;
        private final User appointmentUser;

        private InitializationTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            PartialCustomerModel<? extends Customer> customer = model.getCustomer();
            appointmentCustomer = (null == customer) ? null : customer.dataObject();
            PartialUserModel<? extends User> user = model.getUser();
            appointmentUser = (null == user) ? null : user.dataObject();
        }

        @Override
        protected synchronized List<AppointmentDAO> call() throws Exception {
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));

            List<AppointmentDAO> result;
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
                LOG.fine("Running CustomerReloadTask");
                CustomerReloadTask customerReloadTask = new CustomerReloadTask(dbConnector.getConnection());
                customerReloadTask.run();
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
                LOG.fine("Running UserReloadTask");
                UserReloadTask userReloadTask = new UserReloadTask(dbConnector.getConnection());
                userReloadTask.run();
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                LOG.fine("Loading appointments");
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS));

                if (null != appointmentCustomer && null != appointmentUser) {
                    result = af.load(dbConnector.getConnection(), AppointmentFilter.of(appointmentCustomer, appointmentUser, null, null));
                } else {
                    result = null;
                }
            }
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "call");
            return result;
        }

        private void initialize(Task<List<AppointmentDAO>> task) {
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.INSERT_SUCCESS, customerInsertEventHandler.getWeakEventHandler());
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.UPDATE_SUCCESS, customerUpdateEventHandler.getWeakEventHandler());
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, customerDeleteEventHandler.getWeakEventHandler());
            UserModel.FACTORY.addEventHandler(UserSuccessEvent.INSERT_SUCCESS, userInsertEventHandler.getWeakEventHandler());
            UserModel.FACTORY.addEventHandler(UserSuccessEvent.UPDATE_SUCCESS, userUpdateEventHandler.getWeakEventHandler());
            UserModel.FACTORY.addEventHandler(UserSuccessEvent.DELETE_SUCCESS, userDeleteEventHandler.getWeakEventHandler());
            typeContext.initialize(task);
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "succeeded");
            initialize(this);
            super.succeeded();
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "succeeded");
        }

        @Override
        protected void cancelled() {
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "cancelled");
            initialize(this);
            super.cancelled();
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "cancelled");
        }

        @Override
        protected void failed() {
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "failed");
            initialize(this);
            super.failed();
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "failed");
        }

    }

}
