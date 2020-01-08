package view;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import model.db.*;
import scheduler.App;
import util.Alerts;
import view.address.EditAddress;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;
import view.appointment.EditAppointment;
import view.appointment.ManageAppointments;
import view.city.EditCity;
import view.country.EditCountry;
import view.country.ManageCountries;
import view.customer.EditCustomer;
import view.customer.ManageCustomers;
import view.user.EditUser;
import view.user.ManageUsers;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/Root")
@FXMLResource("/view/Root.fxml")
public class RootController extends SchedulerController {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_ADDRESS = "address";
    public static final String RESOURCEKEY_ALLAPPOINTMENTS = "allAppointments";
    public static final String RESOURCEKEY_ALLCOUNTRIES = "allCountries";
    public static final String RESOURCEKEY_ALLCUSTOMERS = "allCustomers";
    public static final String RESOURCEKEY_ALLUSERS = "allUsers";
    public static final String RESOURCEKEY_APPOINTMENTS = "appointments";
    public static final String RESOURCEKEY_CUSTOMER = "customer";
    public static final String RESOURCEKEY_CUSTOMERS = "customers";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    public static final String RESOURCEKEY_EDITAPPOINTMENT = "editAppointment";
    public static final String RESOURCEKEY_END = "end";
    public static final String RESOURCEKEY_LOADINGDATA = "loadingData";
    public static final String RESOURCEKEY_MYCURRENTANDUPCOMING = "myCurrentAndUpcoming";
    public static final String RESOURCEKEY_NEW = "new";
    public static final String RESOURCEKEY_NEWADDRESS = "newAddress";
    public static final String RESOURCEKEY_NEWCITY = "newCity";
    public static final String RESOURCEKEY_NEWCOUNTRY = "newCountry";
    public static final String RESOURCEKEY_START = "start";
    public static final String RESOURCEKEY_TITLE = "title";
    public static final String RESOURCEKEY_TYPE = "type";
    public static final String RESOURCEKEY_USERS = "users";

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXML controls">
    
    @FXML
    private Menu appointmentsMenu;

    @FXML
    private MenuItem newAppointmentMenuItem;

    @FXML
    private MenuItem allAppointmentsMenuItem;

    @FXML
    private Menu customersMenu;

    @FXML
    private MenuItem newCustomerMenuItem;

    @FXML
    private MenuItem allCustomersMenuItem;

    @FXML
    private Menu addressMenu;

    @FXML
    private MenuItem newCountryMenuItem;

    @FXML
    private MenuItem newCityMenuItem;

    @FXML
    private MenuItem newAddressMenuItem;

    @FXML
    private MenuItem allCountriesMenuItem;

    @FXML
    private Menu usersMenu;

    @FXML
    private MenuItem newUserMenuItem;

    @FXML
    private MenuItem allUsersMenuItem;

    @FXML
    private BorderPane contentPane;

    public BorderPane getContentPane() { return contentPane; }
    
    //</editor-fold>
    
    private static RootController current;
    
    public static RootController getCurrent() { return current; }
    
    private static final Logger LOG = Logger.getLogger(RootController.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="Event handler delegates">
    
    private final ReadOnlyObjectWrapper<AppointmentRow> appointmentAdded;
    public AppointmentRow getAppointmentAdded() { return appointmentAdded.get(); }
    public ReadOnlyObjectProperty<AppointmentRow> appointmentAddedProperty() { return appointmentAdded.getReadOnlyProperty(); }
       
    private final ReadOnlyObjectWrapper<CustomerRow> customerAdded;
    public CustomerRow getCustomerAdded() { return customerAdded.get(); }
    public ReadOnlyObjectProperty<CustomerRow> customerAddedProperty() { return customerAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CountryRow> countryAdded;
    public CountryRow getCountryAdded() { return countryAdded.get(); }
    public ReadOnlyObjectProperty<CountryRow> countryAddedProperty() { return countryAdded.getReadOnlyProperty(); }
     
    private final ReadOnlyObjectWrapper<CityRow> cityAdded;
    public CityRow getCityAdded() { return cityAdded.get(); }
    public ReadOnlyObjectProperty<CityRow> cityAddedProperty() { return cityAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<AddressRow> addressAdded;
    public AddressRow getAddressAdded() { return addressAdded.get(); }
    public ReadOnlyObjectProperty<AddressRow> addressAddedProperty() { return addressAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<UserRow> userAdded;
    public UserRow getUserAdded() { return userAdded.get(); }
    public ReadOnlyObjectProperty<UserRow> userAddedProperty() { return userAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<SchedulerController> contentControllerChanging;
    public SchedulerController getContentControllerChanging() { return contentControllerChanging.get(); }
    public ReadOnlyObjectProperty<SchedulerController> contentControllerChangingProperty() { return contentControllerChanging.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<SchedulerController> currentContentController;
    public SchedulerController getCurrentContentController() { return currentContentController.get(); }
    public ReadOnlyObjectProperty<SchedulerController> currentContentControllerProperty() { return currentContentController.getReadOnlyProperty(); }

    //</editor-fold>
    
    public RootController() {
        appointmentAdded = new ReadOnlyObjectWrapper<>();
        customerAdded = new ReadOnlyObjectWrapper<>();
        countryAdded = new ReadOnlyObjectWrapper<>();
        cityAdded = new ReadOnlyObjectWrapper<>();
        addressAdded = new ReadOnlyObjectWrapper<>();
        userAdded = new ReadOnlyObjectWrapper<>();
        currentContentController = new ReadOnlyObjectWrapper<>();
        contentControllerChanging = new ReadOnlyObjectWrapper<>();
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert appointmentsMenu != null : String.format("fx:id=\"appointmentsMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newAppointmentMenuItem != null : String.format("fx:id=\"newAppointmentMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allAppointmentsMenuItem != null : String.format("fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert customersMenu != null : String.format("fx:id=\"customersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newCustomerMenuItem != null : String.format("fx:id=\"newCustomerMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allCustomersMenuItem != null : String.format("fx:id=\"allCustomersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert addressMenu != null : String.format("fx:id=\"addressMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newCountryMenuItem != null : String.format("fx:id=\"newCountryMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newCityMenuItem != null : String.format("fx:id=\"newCityMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newAddressMenuItem != null : String.format("fx:id=\"newAddressMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allCountriesMenuItem != null : String.format("fx:id=\"allCountriesMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert usersMenu != null : String.format("fx:id=\"usersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newUserMenuItem != null : String.format("fx:id=\"newUserMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allUsersMenuItem != null : String.format("fx:id=\"allUsersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));

        current = this;
        
        ManageAppointments.setAsRootContent(new AppointmentsFilter(App.CURRENT.get().getCurrentUser(), true));
    }
    
    //<editor-fold defaultstate="collapsed" desc="Event handler methods">
    
    @FXML
    void newAppointmentMenuItemClick(ActionEvent event) { addNewAppointment(); }

    public AppointmentRow addNewAppointment() {
        AppointmentRow model = EditAppointment.addNew();
        if (model != null)
            appointmentAdded.set(model);
        return model;
    }
    
    @FXML
    void allAppointmentsMenuItemClick(ActionEvent event) { ManageAppointments.setAsRootContent(); }
         
    @FXML
    void newCustomerMenuItemClick(ActionEvent event) { addNewCustomer(); }
    
    public CustomerRow addNewCustomer() {
        CustomerRow model = EditCustomer.addNew();
        if (model != null)
            customerAdded.set(model);
        return model;
    }
    
    @FXML
    void allCustomersMenuItemClick(ActionEvent event) { ManageCustomers.setAsRootContent(); }
        
    @FXML
    void newCountryMenuItemClick(ActionEvent event) { addNewCountry(); }
    
    public CountryRow addNewCountry() {
        CountryRow model = EditCountry.addNew();
        if (model != null)
            countryAdded.set(model);
        return model;
    }
    
    @FXML
    void allCountriesMenuItemClick(ActionEvent event) { ManageCountries.setAsRootContent(); }
    
    @FXML
    void newCityMenuItemClick(ActionEvent event) { addNewCity(); }
    
    public CityRow addNewCity() {
        CityRow model = EditCity.addNew();
        if (model != null)
            cityAdded.set(model);
        return model;
    }
    
    @FXML
    void newAddressMenuItemClick(ActionEvent event) { addNewAddress(); }
    
    public AddressRow addNewAddress() {
        AddressRow model = EditAddress.addNew();
        if (model != null)
            addressAdded.set(model);
        return model;
    }
    
    @FXML
    void newUserMenuItemClick(ActionEvent event) { addNewUser(); }
    
    public UserRow addNewUser() {
        UserRow model = EditUser.addNew();
        if (model != null)
            userAdded.set(model);
        return model;
    }
    
    @FXML
    void allUsersMenuItemClick(ActionEvent event) { ManageUsers.setAsRootContent(); }
    
    @FXML
    void exitButtonClick(ActionEvent event) { App.CURRENT.get().getPrimaryStage().hide(); }
    
    //</editor-fold>
    
    public void setContent(Node content, SchedulerController controller) {
        contentControllerChanging.set(controller);
        contentPane.getChildren().clear();
        contentPane.setCenter(content);
        currentContentController.set(controller);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootStageScene() {
        App app = App.CURRENT.get(); 
        try {
            ResourceBundle rb = ResourceBundle.getBundle(SchedulerController.getGlobalizationResourceName(RootController.class), Locale.getDefault(Locale.Category.DISPLAY));
            FXMLLoader loader = new FXMLLoader(RootController.class.getResource(SchedulerController.getFXMLResourceName(RootController.class)), rb);
            Scene scene = new Scene(loader.load());
            current = (RootController)loader.getController();
            app.getPrimaryStage().setScene(scene);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            ResourceBundle arb = app.getResources();
            Alerts.showErrorAlert(arb.getString(App.RESOURCEKEY_FXMLLOADERERRORTITLE), arb.getString(App.RESOURCEKEY_FXMLLOADERERRORMESSAGE));
        }
    }
    
}
