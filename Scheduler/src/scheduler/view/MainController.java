package scheduler.view;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import scheduler.App;
import static scheduler.view.SchedulerController.getFXMLResourceName;
import scheduler.view.address.AddressModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.AppointmentsViewOptions;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.city.CityModel;
import scheduler.view.country.CountryModel;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModel;

/**
 * Root FXML Controller class
 * This also serves as the hub for all CRUD operations.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/MainView.fxml")
public class MainController extends SchedulerController {
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
    
    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    public static class ChildFactory<C extends SchedulerController, V extends Node> extends NestedViewFactory<BorderPane, C, V> {

        protected ChildFactory(Class<C> controllerClass) { super(controllerClass); }
        
        @Override
        protected void setChildOf(BorderPane parent) { parent.setCenter(getView()); }

        @Override
        protected void clearChild(BorderPane parent) { parent.setCenter(null); }
        
    }
    
    private ChildFactory<? extends SchedulerController, ? extends Node> childFactory;
    
    //<editor-fold defaultstate="collapsed" desc="JavaFX Properties">
    
    //<editor-fold defaultstate="collapsed" desc="Create/Update/Delete operation properties">
    
    //<editor-fold defaultstate="collapsed" desc="Appointment Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<AppointmentModel>> appointmentUpdated;
    
    /**
     * Gets the last {@link AppointmentModel} that was added, modified or deleted.
     * @return The last {@link AppointmentModel} that was added, modified or deleted.
     */
    public CrudAction<AppointmentModel> getAppointmentAdded() { return appointmentUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link AppointmentModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link AppointmentModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<AppointmentModel>> appointmentAddedProperty() { return appointmentUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Customer Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CustomerModel>> customerUpdated;
    
    /**
     * Gets the last {@link CustomerModel} that was added, modified or deleted.
     * @return The last {@link CustomerModel} that was added, modified or deleted.
     */
    public CrudAction<CustomerModel> getCustomerAdded() { return customerUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link CustomerModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link CustomerModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CustomerModel>> customerAddedProperty() { return customerUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Country Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CountryModel>> countryUpdated;
    
    /**
     * Gets the last {@link CountryModel} that was added, modified or deleted.
     * @return The last {@link CountryModel} that was added, modified or deleted.
     */
    public CrudAction<CountryModel> getCountryAdded() { return countryUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link CountryModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link CountryModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CountryModel>> countryAddedProperty() { return countryUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="City Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CityModel>> cityUpdated;
    
    /**
     * Gets the last {@link CityModel} that was added, modified or deleted.
     * @return The last {@link CityModel} that was added, modified or deleted.
     */
    public CrudAction<CityModel> getCityAdded() { return cityUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link CityModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link CityModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CityModel>> cityAddedProperty() { return cityUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Address Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<AddressModel>> addressUpdated;
    
    /**
     * Gets the last {@link AddressModel} that was added, modified or deleted.
     * @return The last {@link AddressModel} that was added, modified or deleted.
     */
    public CrudAction<AddressModel> getAddressAdded() { return addressUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@linkAddressModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link AddressModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<AddressModel>> addressAddedProperty() { return addressUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="User Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<UserModel>> userUpdated;
    
    /**
     * Gets the last {@link UserModel} that was added, modified or deleted.
     * @return The last {@link UserModel} that was added, modified or deleted.
     */
    public CrudAction<UserModel> getUserAdded() { return userUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link UserModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link UserModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<UserModel>> userAddedProperty() { return userUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //</editor-fold>
    
    //</editor-fold>
    
    public MainController() {
        appointmentUpdated = new ReadOnlyObjectWrapper<>();
        customerUpdated = new ReadOnlyObjectWrapper<>();
        countryUpdated = new ReadOnlyObjectWrapper<>();
        cityUpdated = new ReadOnlyObjectWrapper<>();
        addressUpdated = new ReadOnlyObjectWrapper<>();
        userUpdated = new ReadOnlyObjectWrapper<>();
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert appointmentsMenu != null : String.format("fx:id=\"appointmentsMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newAppointmentMenuItem, String.format("fx:id=\"newAppointmentMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewAppointment(event));
        Objects.requireNonNull(allAppointmentsMenuItem, String.format("fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            
        });
        assert customersMenu != null : String.format("fx:id=\"customersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCustomerMenuItem, String.format("fx:id=\"newCustomerMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCustomer(event));
        Objects.requireNonNull(allCustomersMenuItem, String.format("fx:id=\"allCustomersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> ManageCustomers.setAsRootContent());
        assert addressMenu != null : String.format("fx:id=\"addressMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCountryMenuItem, String.format("fx:id=\"newCountryMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCountry(event));
        Objects.requireNonNull(newCityMenuItem, String.format("fx:id=\"newCityMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCity(event));
        Objects.requireNonNull(newAddressMenuItem, String.format("fx:id=\"newAddressMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewAddress(event));
        Objects.requireNonNull(allCountriesMenuItem, String.format("fx:id=\"allCountriesMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> ManageCountries.setAsRootContent());
        assert usersMenu != null : String.format("fx:id=\"usersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newUserMenuItem, String.format("fx:id=\"newUserMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewUser(event));
        Objects.requireNonNull(allUsersMenuItem, String.format("fx:id=\"allUsersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> ManageUsers.setAsRootContent());
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        childFactory = new ManageAppointments.FactoryImpl(AppointmentsViewOptions.todayAndFuture(App.getCurrentUser()));
        try {
            childFactory.addToParent(contentPane, null);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    
    //<editor-fold defaultstate="collapsed" desc="CRUD implementation methods">
    
    //<editor-fold defaultstate="collapsed" desc="AppointmentImpl operations">
    
    public AppointmentModel addNewAppointment(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens an edit window to edit a {@link model.db.AppointmentImpl}.
     * @param event Contextual information about the event.
     * @param item The {@link AppointmentModel} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     */
    public boolean editAppointment(Event event, AppointmentModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean deleteAppointment(Event event, AppointmentModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CustomerImpl operations">
    
    public CustomerModel addNewCustomer(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean editCustomer(Event event, CustomerModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean deleteCustomer(Event event, CustomerModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CountryImpl operations">
    
    public CountryModel addNewCountry(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean editCountry(Event event, CountryModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean deleteCountry(Event event, CountryModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
         
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CityImpl operations">
    
    public CityModel addNewCity(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean editCity(Event event, CityModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean deleteCity(Event event, CityModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="AddressImpl operations">
    
    public AddressModel addNewAddress(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean editAddress(Event event, AddressModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean deleteAddress(Event event, AddressModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="UserImpl operations">
    
    public UserModel addNewUser(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean editUser(Event event, UserModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean deleteUser(Event event, UserModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
       
    //</editor-fold>
    
    /**
     * Represents a Create, Update or Delete operation.
     * @param <T> The type of {@link ItemModel} that was affected.
     */
    public final class CrudAction<T extends ItemModel<?>> {
        private final T model;
        
        /**
         * Gets the {@link ItemModel} that was affected.
         * @return The {@link ItemModel} that was affected.
         */
        public T getModel() { return model; }
        
        private final boolean delete;
        
        /**
         * Indicates whether the affected item was deleted.
         * 
         * @return {@code true} if the item was deleted. if the {@link add} property is {@code true}, then the item was added; otherwise, it was modified.
         */
        public boolean isDelete() { return delete; }
        
        private final boolean add;
        
        /**
         * Indicates whether the affected item is newly added.
         * 
         * @return {@code true} if the item was added. if the {@link delete} property is {@code true}, then the item was deleted; otherwise, it was modified.
         */
        public boolean isAdd() { return add; }
        
        /**
         * Creates a new CrudAction for an add or update operation.
         * @param row The {@link ItemModel} that was affected.
         * @param isAdd {@code true} if the affected item was added; otherwise {@code false} to indicate the item was updated.
         */
        private CrudAction(T row, boolean isAdd) {
            this.model = row;
            add = isAdd;
            delete = false;
        }
        
        /**
         * Creates a new CrudAction for a delete operation.
         * @param row The {@link ItemModel} that was affected.
         */
        private CrudAction(T row) {
            this.model = row;
            add = false;
            delete = true;
        }
    }
    
    //</editor-fold>    
}