package scheduler.view.customer;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectImpl.DaoFactory;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ValueBindings;
import scheduler.view.EditItem;
import scheduler.view.ItemModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.country.CountryModel;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/customer/EditCustomer")
@FXMLResource("/scheduler/view/customer/EditCustomer.fxml")
public final class EditCustomer extends EditItem.EditController<CustomerImpl, CustomerModel> {

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Active:"}.
     */
    public static final String RESOURCEKEY_ACTIVE = "active";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New Customer"}.
     */
    public static final String RESOURCEKEY_ADDNEWCUSTOMER = "addNewCustomer";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Address:"}.
     */
    public static final String RESOURCEKEY_ADDRESS = "address";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "City:"}.
     */
    public static final String RESOURCEKEY_CITY = "city";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Country:"}.
     */
    public static final String RESOURCEKEY_COUNTRY = "country";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit Customer"}.
     */
    public static final String RESOURCEKEY_EDITCUSTOMER = "editCustomer";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name:"}.
     */
    public static final String RESOURCEKEY_NAME = "name";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name cannot be empty."}.
     */
    public static final String RESOURCEKEY_NAMECANNOTBEEMPTY = "nameCannotBeEmpty";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "No"}.
     */
    public static final String RESOURCEKEY_NO = "no";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Yes"}.
     */
    public static final String RESOURCEKEY_YES = "yes";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading customers..."}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGCUSTOMERS = "errorLoadingCustomers";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for
     * {@code "That customer is referenced in one or more appointments and cannot be deleted."}.
     */
    public static final String RESOURCEKEY_CUSTOMERHASAPPOINTMENTS = "customerHasAppointments";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code:"}.
     */
    public static final String RESOURCEKEY_POSTALCODE = "postalCode";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone Number:"}.
     */
    public static final String RESOURCEKEY_PHONENUMBER = "phoneNumber";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Change"}.
     */
    public static final String RESOURCEKEY_CHANGE = "change";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Appointments"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTS = "appointments";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Current and Future"}.
     */
    public static final String RESOURCEKEY_CURRENTANDFUTURE = "currentAndFuture";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Past"}.
     */
    public static final String RESOURCEKEY_PAST = "past";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "All"}.
     */
    public static final String RESOURCEKEY_ALL = "all";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "End"}.
     */
    public static final String RESOURCEKEY_END = "end";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Start"}.
     */
    public static final String RESOURCEKEY_START = "start";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Title"}.
     */
    public static final String RESOURCEKEY_TITLE = "title";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Type"}.
     */
    public static final String RESOURCEKEY_TYPE = "type";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "User"}.
     */
    public static final String RESOURCEKEY_USER = "user";

    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="nameErrorLabel"
    private Label nameErrorLabel; // Value injected by FXMLLoader

    @FXML // fx:id="activeYesRadioButton"
    private RadioButton activeYesRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="activeNoRadioButton"
    private RadioButton activeNoRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="changeAddressButton"
    private Button changeAddressButton; // Value injected by FXMLLoader

    @FXML // fx:id="address1TextField"
    private TextField address1TextField; // Value injected by FXMLLoader

    @FXML // fx:id="address2TextField"
    private TextField address2TextField; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<?> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="editCityButton"
    private Button editCityButton; // Value injected by FXMLLoader

    @FXML // fx:id="newCityButton"
    private Button newCityButton; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountryModel> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="editCountryButton"
    private Button editCountryButton; // Value injected by FXMLLoader

    @FXML // fx:id="newCountryButton"
    private Button newCountryButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsLabel"
    private Label appointmentsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsFilterComboBox"
    private ComboBox<String> appointmentsFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addAppointmentButton"
    private Button addAppointmentButton; // Value injected by FXMLLoader

    private StringBinding normalizedName;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert nameErrorLabel != null : "fx:id=\"nameErrorLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeYesRadioButton != null : "fx:id=\"activeYesRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeNoRadioButton != null : "fx:id=\"activeNoRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert changeAddressButton != null : "fx:id=\"changeAddressButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address1TextField != null : "fx:id=\"address1TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address2TextField != null : "fx:id=\"address2TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert editCityButton != null : "fx:id=\"editCityButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert newCityButton != null : "fx:id=\"newCityButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert editCountryButton != null : "fx:id=\"editCountryButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert newCountryButton != null : "fx:id=\"newCountryButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsLabel != null : "fx:id=\"appointmentsLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsFilterComboBox != null : "fx:id=\"appointmentsFilterComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addAppointmentButton != null : "fx:id=\"addAppointmentButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        normalizedName = ValueBindings.asNormalized(nameTextField.textProperty());
        normalizedName.isEmpty().addListener((observable) -> nameEmptyChanged());
    }

    private void nameEmptyChanged() {
        if (normalizedName.isEmpty().get()) {
            restoreNode(nameErrorLabel);
        } else {
            collapseNode(nameErrorLabel);
        }
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ItemModel.ModelFactory<CustomerImpl, CustomerModel> getFactory() {
        return CustomerModel.getFactory();
    }

}
