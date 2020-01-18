package view;

import java.sql.Connection;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;
import scheduler.App;
import scheduler.dao.AddressImpl;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.CityImpl;
import scheduler.dao.CountryImpl;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObject;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.UserImpl;
import util.Alerts;
import util.DbConnector;
import view.address.AddressModel;
import view.address.EditAddress;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;
import view.appointment.AppointmentFilter;
import view.appointment.AppointmentModel;
import view.appointment.EditAppointment;
import view.appointment.ManageAppointments;
import view.city.CityModel;
import view.city.EditCity;
import view.country.CountryModel;
import view.country.EditCountry;
import view.country.ManageCountries;
import view.customer.CustomerModel;
import view.customer.EditCustomer;
import view.customer.ManageCustomers;
import view.user.EditUser;
import view.user.ManageUsers;
import view.user.UserModel;

/**
 * Root FXML Controller class
 * This also serves as the hub for all CRUD operations.
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

    //<editor-fold defaultstate="collapsed" desc="JavaFX Properties">
    
    //<editor-fold defaultstate="collapsed" desc="Create/Update/Delete operation properties">
    
    //<editor-fold defaultstate="collapsed" desc="Appointment Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<AppointmentImpl>> appointmentUpdated;
    
    /**
     * Gets the last {@link model.db.AppointmentImpl} that was added, modified or deleted.
     * @return The last {@link model.db.AppointmentImpl} that was added, modified or deleted.
     */
    public CrudAction<AppointmentImpl> getAppointmentAdded() { return appointmentUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link model.db.AppointmentImpl} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link model.db.AppointmentImpl} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<AppointmentImpl>> appointmentAddedProperty() { return appointmentUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Customer Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CustomerImpl>> customerUpdated;
    
    /**
     * Gets the last {@link model.db.CustomerImpl} that was added, modified or deleted.
     * @return The last {@link model.db.CustomerImpl} that was added, modified or deleted.
     */
    public CrudAction<CustomerImpl> getCustomerAdded() { return customerUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link model.db.CustomerImpl} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link model.db.CustomerImpl} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CustomerImpl>> customerAddedProperty() { return customerUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Country Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CountryImpl>> countryUpdated;
    
    /**
     * Gets the last {@link model.db.CountryImpl} that was added, modified or deleted.
     * @return The last {@link model.db.CountryImpl} that was added, modified or deleted.
     */
    public CrudAction<CountryImpl> getCountryAdded() { return countryUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link model.db.CountryImpl} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link model.db.CountryImpl} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CountryImpl>> countryAddedProperty() { return countryUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="City Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CityImpl>> cityUpdated;
    
    /**
     * Gets the last {@link model.db.CityImpl} that was added, modified or deleted.
     * @return The last {@link model.db.CityImpl} that was added, modified or deleted.
     */
    public CrudAction<CityImpl> getCityAdded() { return cityUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link model.db.CityImpl} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link model.db.CityImpl} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CityImpl>> cityAddedProperty() { return cityUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Address Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<AddressImpl>> addressUpdated;
    
    /**
     * Gets the last {@link model.db.AddressImpl} that was added, modified or deleted.
     * @return The last {@link model.db.AddressImpl} that was added, modified or deleted.
     */
    public CrudAction<AddressImpl> getAddressAdded() { return addressUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link model.db.AddressImpl} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link model.db.AddressImpl} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<AddressImpl>> addressAddedProperty() { return addressUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="User Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<UserImpl>> userUpdated;
    
    /**
     * Gets the last {@link model.db.UserImpl} that was added, modified or deleted.
     * @return The last {@link model.db.UserImpl} that was added, modified or deleted.
     */
    public CrudAction<UserImpl> getUserAdded() { return userUpdated.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link model.db.UserImpl} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link model.db.UserImpl} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<UserImpl>> userAddedProperty() { return userUpdated.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Content/Controller Changing">
    
    private final ReadOnlyObjectWrapper<SchedulerController> contentControllerChanging;
    
    /**
     * Gets the prospective {@link view.SchedulerController} that is associated with the {@link javafx.scene.Node} that will become the contents of the {@link contentPane}.
     * @return The prospective {@link view.SchedulerController} that is associated with the {@link javafx.scene.Node} that will become the contents of the {@link contentPane}.
     */
    public SchedulerController getContentControllerChanging() { return contentControllerChanging.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for then the contents of the {@link contentPane} and its associated controller is about to change.
     * @return A JavaFX property that contains the prospective {@link view.SchedulerController} that is associated with the {@link javafx.scene.Node} that will become the contents of the {@link contentPane}.
     */
    public ReadOnlyObjectProperty<SchedulerController> contentControllerChangingProperty() { return contentControllerChanging.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Content/Controller changed">
    
    private final ReadOnlyObjectWrapper<SchedulerController> currentContentController;
    
    /**
     * Gets the {@link view.SchedulerController} that is associated with the {@link javafx.scene.Node} that is the contents of the {@link contentPane}.
     * @return The {@link view.SchedulerController} that is associated with the {@link javafx.scene.Node} that is the contents of the {@link contentPane}.
     */
    public SchedulerController getCurrentContentController() { return currentContentController.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for then the contents of the {@link contentPane} and its associated controller has changed.
     * @return A JavaFX property that contains the {@link view.SchedulerController} that is associated with the {@link javafx.scene.Node} that is the contents of the {@link contentPane}.
     */
    public ReadOnlyObjectProperty<SchedulerController> currentContentControllerProperty() { return currentContentController.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //</editor-fold>
    
    public RootController() {
        appointmentUpdated = new ReadOnlyObjectWrapper<>();
        customerUpdated = new ReadOnlyObjectWrapper<>();
        countryUpdated = new ReadOnlyObjectWrapper<>();
        cityUpdated = new ReadOnlyObjectWrapper<>();
        addressUpdated = new ReadOnlyObjectWrapper<>();
        userUpdated = new ReadOnlyObjectWrapper<>();
        currentContentController = new ReadOnlyObjectWrapper<>();
        contentControllerChanging = new ReadOnlyObjectWrapper<>();
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
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
        AppointmentFilter filter = new AppointmentFilter();
        filter.setUser(Optional.of(App.getCurrentUser()));
        ManageAppointments.setAsRootContent(filter);
    }
    
    //<editor-fold defaultstate="collapsed" desc="CRUD implementation methods">
    
    //<editor-fold defaultstate="collapsed" desc="AppointmentImpl operations">
    
    @FXML
    private void newAppointmentMenuItemClick(ActionEvent event) { addNewAppointment(event); }

    public AppointmentModel addNewAppointment(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens a new window to add a new {@link model.db.AppointmentImpl}.
     * The {@link appointmentUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     * @deprecated Use {@link #addNewAppointment(javafx.event.Event)}, instead.
     */
    @Deprecated
    public AppointmentImpl addNewAppointment() {
        throw new UnsupportedOperationException("Not supported yet.");
//        AppointmentImpl model = EditAppointment.addNew();
//        if (model != null)
//            appointmentUpdated.set(new CrudAction<>(model, true));
//        return model;
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
    
    /**
     * @param item The {@link model.db.AppointmentImpl} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     * @deprecated Use {@link #editAppointment(view.appointment.AppointmentModel)}, instead.
     */
    @Deprecated
    public boolean editAppointment(AppointmentImpl item) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (EditAppointment.edit(item)) {
//            if (item.getRowState() == DataObject.ROWSTATE_DELETED)
//                appointmentUpdated.set(new CrudAction<>(item));
//            else
//                appointmentUpdated.set(new CrudAction<>(item, false));
//            return true;
//        }
//        return false;
    }

    private static boolean deleteItem(DataObjectImpl item) {
        ResourceBundle rb = App.getCurrent().getResources();
        Alert alert = new Alert(Alert.AlertType.ERROR, rb.getString(App.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        alert.setTitle(rb.getString(App.RESOURCEKEY_CONFIRMDELETE));
        alert.initStyle(StageStyle.UTILITY);
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.YES) {
            try {
                DbConnector.apply((Connection c) -> item.delete(c));
                if (item.getRowState() == DataObject.ROWSTATE_DELETED)
                    return true;
            } catch (Exception ex) {
                Logger.getLogger(RootController.class.getName()).log(Level.SEVERE, null, ex);
                // TODO: User needs pop-up here
            }
        }
        return false;
    }
    
    public boolean deleteAppointment(Event event, AppointmentModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Deletes an {@link model.db.AppointmentImpl} after confirming with the current user.
     * 
     * @param item The {@link model.db.AppointmentImpl} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     * @deprecated Use {@link #deleteAppointment(javafx.event.Event, view.appointment.AppointmentModel)}, instead.
     */
    @Deprecated
    public boolean deleteAppointment(AppointmentImpl item) {
        if (deleteItem(item)) {
            appointmentUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }

    @FXML
    private void allAppointmentsMenuItemClick(ActionEvent event) { ManageAppointments.setAsRootContent(); }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CustomerImpl operations">
    
    @FXML
    private void newCustomerMenuItemClick(ActionEvent event) { addNewCustomer(event); }

    public CustomerModel addNewCustomer(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens a new window to add a new {@link model.db.CustomerImpl}.
     * The {@link customerUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     * @deprecated Use {@link #addNewCustomer(javafx.event.Event)}, instead.
     */
    @Deprecated
    public CustomerImpl addNewCustomer() {
        throw new UnsupportedOperationException("Not supported yet.");
//        CustomerImpl model = EditCustomer.addNew();
//        if (model != null)
//            customerUpdated.set(new CrudAction<>(model, true));
//        return model;
    }

    public boolean editCustomer(Event event, CustomerModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens an edit window to edit a {@link model.db.CustomerImpl}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.CustomerImpl} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     * @deprecated Use {@link #editCustomer(javafx.event.Event, view.customer.CustomerModel)}, instead.
     */
    @Deprecated
    public boolean editCustomer(CustomerImpl item) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (EditCustomer.edit(item)) {
//            if (item.getRowState() == DataObject.ROWSTATE_DELETED)
//                customerUpdated.set(new CrudAction<>(item));
//            else
//                customerUpdated.set(new CrudAction<>(item, false));
//            return true;
//        }
//        return false;
    }

    public boolean deleteCustomer(Event event, CustomerModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Deletes a {@link model.db.CustomerImpl} after confirming with the current user.
     * The {@link customerUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.CustomerImpl} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     * @deprecated Use {@link #deleteCustomer(javafx.event.Event, view.customer.CustomerModel)}, instead.
     */
    @Deprecated
    public boolean deleteCustomer(CustomerImpl item) {
        if (deleteItem(item)) {
            customerUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }

    @FXML
    private void allCustomersMenuItemClick(ActionEvent event) { ManageCustomers.setAsRootContent(); }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CountryImpl operations">
    
    @FXML
    private void newCountryMenuItemClick(ActionEvent event) { addNewCountry(); }

    public CountryModel addNewCountry(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens a new window to add a new {@link model.db.CountryImpl}.
     * The {@link countryUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     * @deprecated Use {@link #addNewCountry(javafx.event.Event)}, instead.
     */
    @Deprecated
    public CountryImpl addNewCountry() {
        throw new UnsupportedOperationException("Not supported yet.");
//        CountryImpl model = EditCountry.addNew();
//        if (model != null)
//            countryUpdated.set(new CrudAction<>(model, true));
//        return model;
    }

    public boolean editCountry(Event event, CountryModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens an edit window to edit a {@link model.db.CountryImpl}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.CountryImpl} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     * @deprecated Use {@link #editCountry(javafx.event.Event, view.country.CountryModel)}, instead.
     */
    @Deprecated
    public boolean editCountry(CountryImpl item) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (EditCountry.edit(item)) {
//            if (item.getRowState() == DataObject.ROWSTATE_DELETED)
//                countryUpdated.set(new CrudAction<>(item));
//            else
//                countryUpdated.set(new CrudAction<>(item, false));
//            return true;
//        }
//        return false;
    }

    public boolean deleteCountry(Event event, CountryModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Deletes a {@link model.db.CountryImpl} after confirming with the current user.
     * The {@link countryUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.CountryImpl} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     * @deprecated Use {@link #deleteCountry(javafx.event.Event, view.country.CountryModel)}, instead.
     */
    @Deprecated
    public boolean deleteCountry(CountryImpl item) {
        if (deleteItem(item)) {
            countryUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }

    @FXML
    private void allCountriesMenuItemClick(ActionEvent event) { ManageCountries.setAsRootContent(); }
            
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CityImpl operations">
    
    @FXML
    private void newCityMenuItemClick(ActionEvent event) { addNewCity(); }

    public CityModel addNewCity(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens a new window to add a new {@link model.db.CityImpl}.
     * The {@link cityUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     * @deprecated Use {@link #addNewCity(javafx.event.Event)}, instead.
     */
    @Deprecated
    public CityImpl addNewCity() {
        throw new UnsupportedOperationException("Not supported yet.");
//        CityImpl model = EditCity.addNew();
//        if (model != null)
//            cityUpdated.set(new CrudAction<>(model, true));
//        return model;
    }

    public boolean editCity(Event event, CityModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens an edit window to edit a {@link model.db.CityImpl}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.CityImpl} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     * @deprecated Use {@link #editCity(javafx.event.Event, view.city.CityModel)}, instead.
     */
    @Deprecated
    public boolean editCity(CityImpl item) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (EditCity.edit(item)) {
//            if (item.getRowState() == DataObject.ROWSTATE_DELETED)
//                cityUpdated.set(new CrudAction<>(item));
//            else
//                cityUpdated.set(new CrudAction<>(item, false));
//            return true;
//        }
//        return false;
    }

    public boolean deleteCity(Event event, CityModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Deletes a {@link model.db.CityImpl} after confirming with the current user.
     * The {@link cityUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.CityImpl} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     * @deprecated Use {@link #deleteCity(javafx.event.Event, view.city.CityModel)}, instead.
     */
    @Deprecated
    public boolean deleteCity(CityImpl item) {
        if (deleteItem(item)) {
            cityUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }
            
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="AddressImpl operations">
    
    @FXML
    private void newAddressMenuItemClick(ActionEvent event) { addNewAddress(); }

    public AddressModel addNewAddress(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens a new window to add a new {@link model.db.AddressImpl}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     * @deprecated Use {@link #addNewAddress(javafx.event.Event)}, instead.
     */
    @Deprecated
    public AddressImpl addNewAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
//        AddressImpl model = EditAddress.addNew();
//        if (model != null)
//            addressUpdated.set(new CrudAction<>(model, true));
//        return model;
    }

    public boolean editAddress(Event event, AddressModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens an edit window to edit a {@link model.db.AddressImpl}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.AddressImpl} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     * @deprecated Use {@link #editAddress(javafx.event.Event, view.address.AddressModel)}, instead.
     */
    @Deprecated
    public boolean editAddress(AddressImpl item) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (EditAddress.edit(item)) {
//            if (item.getRowState() == DataObject.ROWSTATE_DELETED)
//                addressUpdated.set(new CrudAction<>(item));
//            else
//                addressUpdated.set(new CrudAction<>(item, false));
//            return true;
//        }
//        return false;
    }

    public boolean deleteAddress(Event event, AddressModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Deletes an {@link model.db.AddressImpl} after confirming with the current user.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.AddressImpl} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     * @deprecated Use {@link #deleteAddress(javafx.event.Event, view.address.AddressModel)}, instead.
     */
    @Deprecated
    public boolean deleteAddress(AddressImpl item) {
        if (deleteItem(item)) {
            addressUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="UserImpl operations">
    
    @FXML
    private void newUserMenuItemClick(ActionEvent event) { addNewUser(); }

    public UserModel addNewUser(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens a new window to add a new {@link model.db.UserImpl}.
     * The {@link #userUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     * @deprecated Use {@link #addNewUser(javafx.event.Event)}, instead.
     */
    @Deprecated
    public UserImpl addNewUser() {
        throw new UnsupportedOperationException("Not supported yet.");
//        UserImpl model = EditUser.addNew();
//        if (model != null)
//            userUpdated.set(new CrudAction<>(model, true));
//        return model;
    }

    public boolean editUser(Event event, UserModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Opens an edit window to edit a {@link model.db.UserImpl}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.UserImpl} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     * @deprecated Use {@link #editUser(javafx.event.Event, view.user.UserModel)}, instead.
     */
    @Deprecated
    public boolean editUser(UserImpl item) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (EditUser.edit(item)) {
//            if (item.getRowState() == DataObject.ROWSTATE_DELETED)
//                userUpdated.set(new CrudAction<>(item));
//            else
//                userUpdated.set(new CrudAction<>(item, false));
//            return true;
//        }
//        return false;
    }

    public boolean deleteUser(Event event, UserModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Deletes a {@link model.db.UserImpl} after confirming with the current user.
     * The {@link userUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.UserImpl} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     * @deprecated Use {@link #deleteUser(javafx.event.Event, view.user.UserModel)}, instead.
     */
    public boolean deleteUser(UserImpl item) {
        if (deleteItem(item)) {
            userUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }

    @FXML
    private void allUsersMenuItemClick(ActionEvent event) { ManageUsers.setAsRootContent(); }
            
    //</editor-fold>
            
    @FXML
    private void exitButtonClick(ActionEvent event) { App.getCurrent().getPrimaryStage().hide(); }

    /**
     * Represents a Create, Update or Delete operation.
     * @deprecated Targe of CRUD action should derive from {@link view.ModelBase}.
     * @param <T> The type of {@link model.db.DataRow} that was affected.
     */
    @Deprecated
    public final class CrudAction<T extends DataObjectImpl> {
        private final T row;
        
        /**
         * Gets the {@link model.db.DataRow} that was affected.
         * @return The {@link model.db.DataRow} that was affected.
         */
        public T getRow() { return row; }
        
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
         * @param row The {@link model.db.DataRow} that was affected.
         * @param isAdd {@code true} if the affected item was added; otherwise {@code false} to indicate the item was updated.
         */
        private CrudAction(T row, boolean isAdd) {
            this.row = row;
            add = isAdd;
            delete = false;
        }
        
        /**
         * Creates a new CrudAction for a delete operation.
         * @param row The {@link model.db.DataRow} that was affected.
         */
        private CrudAction(T row) {
            this.row = row;
            add = false;
            delete = true;
        }
    }
    
    //</editor-fold>

    /**
     * Changes the contents associated with the current RootController.
     * This first sets the {@link #contentControllerChanging} property with the candidate {@link view.SchedulerController} before changing the content.
     * After the content is changed, this updates the {@link #currentContentController} property with the new {@link view.SchedulerController}.
     * 
     * @param content The {@link javafx.scene.Node} that will be the new contents associated with the current RootController.
     * @param controller The {@link view.SchedulerController} associated with the content node.
     */
    public void setContent(Node content, SchedulerController controller) {
        contentControllerChanging.set(controller);
        contentPane.getChildren().clear();
        contentPane.setCenter(content);
        currentContentController.set(controller);
    }
    
    /**
     * Changes the application root stage to the {@link javafx.scene.Parent} that is associated a new RootController
     */
    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootStageScene() {
        App app = App.getCurrent(); 
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
