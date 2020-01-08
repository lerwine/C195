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
import model.db.*;
import scheduler.App;
import util.Alerts;
import util.DbConnectedCallable;
import util.DbConnector;
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
    
    private final ReadOnlyObjectWrapper<CrudAction<AppointmentRow>> appointmentUpdated;
    public CrudAction<AppointmentRow> getAppointmentAdded() { return appointmentUpdated.get(); }
    public ReadOnlyObjectProperty<CrudAction<AppointmentRow>> appointmentAddedProperty() { return appointmentUpdated.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CrudAction<CustomerRow>> customerUpdated;
    public CrudAction<CustomerRow> getCustomerAdded() { return customerUpdated.get(); }
    public ReadOnlyObjectProperty<CrudAction<CustomerRow>> customerAddedProperty() { return customerUpdated.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CrudAction<CountryRow>> countryUpdated;
    public CrudAction<CountryRow> getCountryAdded() { return countryUpdated.get(); }
    public ReadOnlyObjectProperty<CrudAction<CountryRow>> countryAddedProperty() { return countryUpdated.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CrudAction<CityRow>> cityUpdated;
    public CrudAction<CityRow> getCityAdded() { return cityUpdated.get(); }
    public ReadOnlyObjectProperty<CrudAction<CityRow>> cityAddedProperty() { return cityUpdated.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CrudAction<AddressRow>> addressUpdated;
    public CrudAction<AddressRow> getAddressAdded() { return addressUpdated.get(); }
    public ReadOnlyObjectProperty<CrudAction<AddressRow>> addressAddedProperty() { return addressUpdated.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CrudAction<UserRow>> userUpdated;
    public CrudAction<UserRow> getUserAdded() { return userUpdated.get(); }
    public ReadOnlyObjectProperty<CrudAction<UserRow>> userAddedProperty() { return userUpdated.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<SchedulerController> contentControllerChanging;
    public SchedulerController getContentControllerChanging() { return contentControllerChanging.get(); }
    public ReadOnlyObjectProperty<SchedulerController> contentControllerChangingProperty() { return contentControllerChanging.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<SchedulerController> currentContentController;
    public SchedulerController getCurrentContentController() { return currentContentController.get(); }
    public ReadOnlyObjectProperty<SchedulerController> currentContentControllerProperty() { return currentContentController.getReadOnlyProperty(); }

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
        
        ManageAppointments.setAsRootContent(new AppointmentsFilter(App.CURRENT.get().getCurrentUser(), true));
    }
    
    //<editor-fold defaultstate="collapsed" desc="CRUD implementation methods">
    
    //<editor-fold defaultstate="collapsed" desc="AppointmentRow operations">
    
    @FXML
    private void newAppointmentMenuItemClick(ActionEvent event) { addNewAppointment(); }

    /**
     * Opens a new window to add a new {@link model.db.AppointmentRow}.
     * The {@link appointmentUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     */
    public AppointmentRow addNewAppointment() {
        AppointmentRow model = EditAppointment.addNew();
        if (model != null)
            appointmentUpdated.set(new CrudAction<>(model, true));
        return model;
    }

    /**
     * Opens an edit window to edit a {@link model.db.AppointmentRow}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.AppointmentRow} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     */
    public boolean editAppointment(AppointmentRow item) {
        if (EditAppointment.edit(item)) {
            if (item.getRowState() == DataRow.ROWSTATE_DELETED)
                appointmentUpdated.set(new CrudAction<>(item));
            else
                appointmentUpdated.set(new CrudAction<>(item, false));
            return true;
        }
        return false;
    }

    private static boolean deleteItem(DataRow item) {
        ResourceBundle rb = App.CURRENT.get().getResources();
        Alert alert = new Alert(Alert.AlertType.ERROR, rb.getString(App.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        alert.setTitle(rb.getString(App.RESOURCEKEY_CONFIRMDELETE));
        alert.initStyle(StageStyle.UTILITY);
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.YES) {
            try {
                DbConnector.apply((Connection c) -> item.delete(c));
                if (item.getRowState() == DataRow.ROWSTATE_DELETED)
                    return true;
            } catch (Exception ex) {
                Logger.getLogger(RootController.class.getName()).log(Level.SEVERE, null, ex);
                // TODO: User needs pop-up here
            }
        }
        return false;
    }
    
    /**
     * Deletes an {@link model.db.AppointmentRow} after confirming with the current user.
     * 
     * @param item The {@link model.db.AppointmentRow} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     */
    public boolean deleteAppointment(AppointmentRow item) {
        if (deleteItem(item)) {
            appointmentUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }

    @FXML
    private void allAppointmentsMenuItemClick(ActionEvent event) { ManageAppointments.setAsRootContent(); }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CustomerRow operations">
    
    @FXML
    private void newCustomerMenuItemClick(ActionEvent event) { addNewCustomer(); }

    /**
     * Opens a new window to add a new {@link model.db.CustomerRow}.
     * The {@link customerUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     */
    public CustomerRow addNewCustomer() {
        CustomerRow model = EditCustomer.addNew();
        if (model != null)
            customerUpdated.set(new CrudAction<>(model, true));
        return model;
    }

    /**
     * Opens an edit window to edit a {@link model.db.CustomerRow}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.CustomerRow} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     */
    public boolean editCustomer(CustomerRow item) {
        if (EditCustomer.edit(item)) {
            if (item.getRowState() == DataRow.ROWSTATE_DELETED)
                customerUpdated.set(new CrudAction<>(item));
            else
                customerUpdated.set(new CrudAction<>(item, false));
            return true;
        }
        return false;
    }

    /**
     * Deletes a {@link model.db.CustomerRow} after confirming with the current user.
     * The {@link customerUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.CustomerRow} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     */
    public boolean deleteCustomer(CustomerRow item) {
        if (deleteItem(item)) {
            customerUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }

    @FXML
    private void allCustomersMenuItemClick(ActionEvent event) { ManageCustomers.setAsRootContent(); }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CountryRow operations">
    
    @FXML
    private void newCountryMenuItemClick(ActionEvent event) { addNewCountry(); }

    /**
     * Opens a new window to add a new {@link model.db.CountryRow}.
     * The {@link countryUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     */
    public CountryRow addNewCountry() {
        CountryRow model = EditCountry.addNew();
        if (model != null)
            countryUpdated.set(new CrudAction<>(model, true));
        return model;
    }

    /**
     * Opens an edit window to edit a {@link model.db.CountryRow}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.CountryRow} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     */
    public boolean editCountry(CountryRow item) {
        if (EditCountry.edit(item)) {
            if (item.getRowState() == DataRow.ROWSTATE_DELETED)
                countryUpdated.set(new CrudAction<>(item));
            else
                countryUpdated.set(new CrudAction<>(item, false));
            return true;
        }
        return false;
    }

    /**
     * Deletes a {@link model.db.CountryRow} after confirming with the current user.
     * The {@link countryUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.CountryRow} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     */
    public boolean deleteCountry(CountryRow item) {
        if (deleteItem(item)) {
            countryUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }

    @FXML
    private void allCountriesMenuItemClick(ActionEvent event) { ManageCountries.setAsRootContent(); }
            
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CityRow operations">
    
    @FXML
    private void newCityMenuItemClick(ActionEvent event) { addNewCity(); }

    /**
     * Opens a new window to add a new {@link model.db.CityRow}.
     * The {@link cityUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     */
    public CityRow addNewCity() {
        CityRow model = EditCity.addNew();
        if (model != null)
            cityUpdated.set(new CrudAction<>(model, true));
        return model;
    }

    /**
     * Opens an edit window to edit a {@link model.db.CityRow}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.CityRow} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     */
    public boolean editCity(CityRow item) {
        if (EditCity.edit(item)) {
            if (item.getRowState() == DataRow.ROWSTATE_DELETED)
                cityUpdated.set(new CrudAction<>(item));
            else
                cityUpdated.set(new CrudAction<>(item, false));
            return true;
        }
        return false;
    }

    /**
     * Deletes a {@link model.db.CityRow} after confirming with the current user.
     * The {@link cityUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.CityRow} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     */
    public boolean deleteCity(CityRow item) {
        if (deleteItem(item)) {
            cityUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }
            
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="AddressRow operations">
    
    @FXML
    private void newAddressMenuItemClick(ActionEvent event) { addNewAddress(); }

    /**
     * Opens a new window to add a new {@link model.db.AddressRow}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     */
    public AddressRow addNewAddress() {
        AddressRow model = EditAddress.addNew();
        if (model != null)
            addressUpdated.set(new CrudAction<>(model, true));
        return model;
    }

    /**
     * Opens an edit window to edit a {@link model.db.AddressRow}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.AddressRow} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     */
    public boolean editAddress(AddressRow item) {
        if (EditAddress.edit(item)) {
            if (item.getRowState() == DataRow.ROWSTATE_DELETED)
                addressUpdated.set(new CrudAction<>(item));
            else
                addressUpdated.set(new CrudAction<>(item, false));
            return true;
        }
        return false;
    }

    /**
     * Deletes an {@link model.db.AddressRow} after confirming with the current user.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.AddressRow} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     */
    public boolean deleteAddress(AddressRow item) {
        if (deleteItem(item)) {
            addressUpdated.set(new CrudAction<>(item));
            return true;
        }
        return false;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="UserRow operations">
    
    @FXML
    private void newUserMenuItemClick(ActionEvent event) { addNewUser(); }

    /**
     * Opens a new window to add a new {@link model.db.UserRow}.
     * The {@link userUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the operation has been canceled.
     * 
     * @return {@code true} if the item was added to the database; otherwise, {@code false} to indicate the add operation was canceled.
     */
    public UserRow addNewUser() {
        UserRow model = EditUser.addNew();
        if (model != null)
            userUpdated.set(new CrudAction<>(model, true));
        return model;
    }

    /**
     * Opens an edit window to edit a {@link model.db.UserRow}.
     * The {@link addressUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when the edit window closes unless the edit is canceled.
     * 
     * @param item The {@link model.db.UserRow} to be edited.
     * @return {@code true} if the item was edited or deleted from the database; otherwise, {@code false} to indicate the edit operation was canceled.
     */
    public boolean editUser(UserRow item) {
        if (EditUser.edit(item)) {
            if (item.getRowState() == DataRow.ROWSTATE_DELETED)
                userUpdated.set(new CrudAction<>(item));
            else
                userUpdated.set(new CrudAction<>(item, false));
            return true;
        }
        return false;
    }

    /**
     * Deletes a {@link model.db.UserRow} after confirming with the current user.
     * The {@link userUpdated} property will be updated with a new {@link view.RootController.CrudAction} object when successfully deleted.
     * 
     * @param item The {@link model.db.UserRow} to be deleted.
     * @return {@code true} if the item was successfully deleted from the database; otherwise, {@code false}.
     */
    public boolean deleteUser(UserRow item) {
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
    private void exitButtonClick(ActionEvent event) { App.CURRENT.get().getPrimaryStage().hide(); }
    
    /**
     * Represents a Create, Update or Delete operation.
     * 
     * @param <T> The type of {@link model.db.DataRow} that was affected.
     */
    public final class CrudAction<T extends DataRow> {
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
     * This first sets the {@link contentControllerChanging} property with the candidate {@link view.SchedulerController} before changing the content.
     * After the content is changed, this updates the @{link currentContentController property} with the new {@link view.SchedulerController}.
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
