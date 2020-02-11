package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.App;
import scheduler.dao.AddressFactory.AddressImpl;
import scheduler.dao.AppointmentFactory.AppointmentImpl;
import scheduler.dao.CityFactory.CityImpl;
import scheduler.dao.CountryFactory.CountryImpl;
import scheduler.dao.CustomerFactory.CustomerImpl;
import scheduler.dao.DataObjectFactory.DataObjectImpl;
import scheduler.dao.UserFactory.UserImpl;
import scheduler.filter.ModelFilter;
import scheduler.util.Alerts;
import scheduler.util.DbConnector;
import scheduler.util.ThrowableFunction;
import scheduler.view.address.AddressModel;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.AppointmentsViewOptions;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.city.CityModel;
import scheduler.view.city.EditCity;
import scheduler.view.country.CountryModel;
import scheduler.view.country.EditCountry;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.EditCustomer;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModel;

/**
 * Root FXML Controller class
 * This also serves as the hub for all CRUD operations.
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/MainView.fxml")
public final class MainController extends SchedulerController {
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
    public static final String RESOURCEKEY_DELETINGRECORD = "deletingRecord";
    public static final String RESOURCEKEY_DELETEFAILURE = "deleteFailure";
    public static final String RESOURCEKEY_ERRORDELETINGFROMDB = "errorDeletingFromDb";
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
    public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";

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

    private MainContentController contentController;
    
    //<editor-fold defaultstate="collapsed" desc="JavaFX Properties">
    
    //<editor-fold defaultstate="collapsed" desc="Create/Update/Delete operation properties">
    
    //<editor-fold defaultstate="collapsed" desc="Appointment Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<AppointmentModel>> appointmentChanged;
    
    /**
     * Gets the last {@link AppointmentModel} that was added, modified or deleted.
     * @return The last {@link AppointmentModel} that was added, modified or deleted.
     */
    public CrudAction<AppointmentModel> getAppointmentChanged() { return appointmentChanged.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link AppointmentModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link AppointmentModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<AppointmentModel>> appointmentChangedProperty() { return appointmentChanged.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Customer Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CustomerModel>> customerChanged;
    
    /**
     * Gets the last {@link CustomerModel} that was added, modified or deleted.
     * @return The last {@link CustomerModel} that was added, modified or deleted.
     */
    public CrudAction<CustomerModel> getCustomerChanged() { return customerChanged.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link CustomerModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link CustomerModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CustomerModel>> customerChangedProperty() { return customerChanged.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Country Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CountryModel>> countryChanged;
    
    /**
     * Gets the last {@link CountryModel} that was added, modified or deleted.
     * @return The last {@link CountryModel} that was added, modified or deleted.
     */
    public CrudAction<CountryModel> getCountryChanged() { return countryChanged.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link CountryModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link CountryModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CountryModel>> countryChangedProperty() { return countryChanged.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="City Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<CityModel>> cityChanged;
    
    /**
     * Gets the last {@link CityModel} that was added, modified or deleted.
     * @return The last {@link CityModel} that was added, modified or deleted.
     */
    public CrudAction<CityModel> getCityChanged() { return cityChanged.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link CityModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link CityModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<CityModel>> cityChangedProperty() { return cityChanged.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Address Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<AddressModel>> addressChanged;
    
    /**
     * Gets the last {@link AddressModel} that was added, modified or deleted.
     * @return The last {@link AddressModel} that was added, modified or deleted.
     */
    public CrudAction<AddressModel> getAddressChanged() { return addressChanged.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@linkAddressModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link AddressModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<AddressModel>> addressChangedProperty() { return addressChanged.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="User Create/Update/Delete op results">
    
    private final ReadOnlyObjectWrapper<CrudAction<UserModel>> userChanged;
    
    /**
     * Gets the last {@link UserModel} that was added, modified or deleted.
     * @return The last {@link UserModel} that was added, modified or deleted.
     */
    public CrudAction<UserModel> getUserChanged() { return userChanged.get(); }
    
    /**
     * Gets a JavaFX property that can be used to listen for when a {@link UserModel} has been added, modified or deleted.
     * @return A JavaFX property that contains the last {@link UserModel} that was added, modified or deleted.
     */
    public ReadOnlyObjectProperty<CrudAction<UserModel>> userChangedProperty() { return userChanged.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //</editor-fold>
    
    //</editor-fold>
    
    public MainController() {
        appointmentChanged = new ReadOnlyObjectWrapper<>();
        customerChanged = new ReadOnlyObjectWrapper<>();
        countryChanged = new ReadOnlyObjectWrapper<>();
        cityChanged = new ReadOnlyObjectWrapper<>();
        addressChanged = new ReadOnlyObjectWrapper<>();
        userChanged = new ReadOnlyObjectWrapper<>();
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert appointmentsMenu != null : String.format("fx:id=\"appointmentsMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newAppointmentMenuItem, String.format("fx:id=\"newAppointmentMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewAppointment(event));
        Objects.requireNonNull(allAppointmentsMenuItem, String.format("fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageAppointments.loadInto(MainController.this, (Stage)contentPane.getScene().getWindow(), AppointmentsViewOptions.all());
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error loading appointments", ex);
            }
        });
        assert customersMenu != null : String.format("fx:id=\"customersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCustomerMenuItem, String.format("fx:id=\"newCustomerMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCustomer(event));
        Objects.requireNonNull(allCustomersMenuItem, String.format("fx:id=\"allCustomersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageCustomers.loadInto(this, (Stage)contentPane.getScene().getWindow(), ModelFilter.empty());
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error loading customers", ex);
            }
        });
        assert addressMenu != null : String.format("fx:id=\"addressMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCountryMenuItem, String.format("fx:id=\"newCountryMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCountry(event));
        Objects.requireNonNull(newCityMenuItem, String.format("fx:id=\"newCityMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCity(event));
        Objects.requireNonNull(newAddressMenuItem, String.format("fx:id=\"newAddressMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewAddress(event));
        Objects.requireNonNull(allCountriesMenuItem, String.format("fx:id=\"allCountriesMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageCountries.loadInto(this, (Stage)contentPane.getScene().getWindow(), ModelFilter.empty());
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error loading countries", ex);
            }
        });
        assert usersMenu != null : String.format("fx:id=\"usersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(newUserMenuItem, String.format("fx:id=\"newUserMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> addNewUser(event));
        Objects.requireNonNull(allUsersMenuItem, String.format("fx:id=\"allUsersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageUsers.loadInto(this, (Stage)contentPane.getScene().getWindow(), ModelFilter.empty());
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error loading users", ex);
            }
        });
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
    }

    public static void loadInto(Stage stage) throws IOException {
        MainController mc = SchedulerController.load(stage, MainController.class, (Parent v, MainController c) -> {
            stage.setScene(new Scene(v));
        });
        ManageAppointments.loadInto(mc, stage, AppointmentsViewOptions.todayAndFuture(App.getCurrentUser()));
    }

    //<editor-fold defaultstate="collapsed" desc="CRUD implementation methods">
    
    //<editor-fold defaultstate="collapsed" desc="AppointmentImpl operations">
    
    public AppointmentModel addNewAppointment(Event event) {
        EditItem.ShowAndWaitResult<AppointmentModel> result = EditItem.waitEdit(EditAppointment.class,
                new AppointmentModel(new AppointmentImpl()), (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            appointmentChanged.set(new CrudAction<>(result.getTarget(), true));
            return result.getTarget();
        }
        return null;
    }
    
    /**
     * Opens an edit window to edit an {@link AppointmentModel}.
     * @param event Contextual information about the event.
     * @param item The {@link AppointmentModel} to be edited.
     * @return {@code CrudAction<AppointmentModel>} if the item was edited or deleted from the database; otherwise, {@code null} to indicate the edit operation was canceled.
     */
    public CrudAction<AppointmentModel> editAppointment(Event event, AppointmentModel item) {
        EditItem.ShowAndWaitResult<AppointmentModel> result = EditItem.waitEdit(EditAppointment.class, item, (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            CrudAction<AppointmentModel> cr = (result.isDeleteOperation()) ? new CrudAction<>(result.getTarget()) :
                    new CrudAction<>(result.getTarget(), false);
            appointmentChanged.set(cr);
            return cr;
        }
        return null;
    }
    
    public void deleteAppointment(Event event, AppointmentModel item, ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage) {
        Optional<ButtonType> response = Alerts.showWarningAlert(getResourceString(RESOURCEKEY_CONFIRMDELETE),
                getResourceString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES)
            TaskWaiter.execute(new DeleteTask<>(item, (Stage)contentPane.getScene().getWindow(), getDeleteDependencyMessage,
                    (m) -> appointmentChanged.set(new CrudAction<>(m))));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CustomerImpl operations">
    
    public CustomerModel addNewCustomer(Event event) {
        EditItem.ShowAndWaitResult<CustomerModel> result = EditItem.waitEdit(EditCustomer.class,
                new CustomerModel(new CustomerImpl()), (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            customerChanged.set(new CrudAction<>(result.getTarget(), true));
            return result.getTarget();
        }
        return null;
    }
    
    public CrudAction<CustomerModel> editCustomer(Event event, CustomerModel item) {
        EditItem.ShowAndWaitResult<CustomerModel> result = EditItem.waitEdit(EditCustomer.class, item, (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            CrudAction<CustomerModel> cr = (result.isDeleteOperation()) ? new CrudAction<>(result.getTarget()) :
                    new CrudAction<>(result.getTarget(), false);
            customerChanged.set(cr);
            return cr;
        }
        return null;
    }
    
    public void deleteCustomer(Event event, CustomerModel item, ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage) {
        Optional<ButtonType> response = Alerts.showWarningAlert(getResourceString(RESOURCEKEY_CONFIRMDELETE),
                getResourceString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES)
            TaskWaiter.execute(new DeleteTask<>(item, (Stage)contentPane.getScene().getWindow(), getDeleteDependencyMessage,
                    (m) -> customerChanged.set(new CrudAction<>(m))));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CountryImpl operations">
    
    public CountryModel addNewCountry(Event event) {
        EditItem.ShowAndWaitResult<CountryModel> result = EditItem.waitEdit(EditCountry.class,
                new CountryModel(new CountryImpl()), (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            countryChanged.set(new CrudAction<>(result.getTarget(), true));
            return result.getTarget();
        }
        return null;
    }
    
    public CrudAction<CountryModel> editCountry(Event event, CountryModel item) {
        EditItem.ShowAndWaitResult<CountryModel> result = EditItem.waitEdit(EditCountry.class, item, (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            CrudAction<CountryModel> cr = (result.isDeleteOperation()) ? new CrudAction<>(result.getTarget()) :
                    new CrudAction<>(result.getTarget(), false);
            countryChanged.set(cr);
            return cr;
        }
        return null;
    }
    
    public void deleteCountry(Event event, CountryModel item, ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage) {
        Optional<ButtonType> response = Alerts.showWarningAlert(getResourceString(RESOURCEKEY_CONFIRMDELETE),
                getResourceString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES)
            TaskWaiter.execute(new DeleteTask<>(item, (Stage)contentPane.getScene().getWindow(), getDeleteDependencyMessage,
                    (m) -> countryChanged.set(new CrudAction<>(m))));
    }
         
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CityImpl operations">
    
    public CityModel addNewCity(Event event) {
        EditItem.ShowAndWaitResult<CityModel> result = EditItem.waitEdit(EditCity.class,
                new CityModel(new CityImpl()), (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            cityChanged.set(new CrudAction<>(result.getTarget(), true));
            return result.getTarget();
        }
        return null;
    }
    
    public CrudAction<CityModel> editCity(Event event, CityModel item) {
        EditItem.ShowAndWaitResult<CityModel> result = EditItem.waitEdit(EditCity.class, item, (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            CrudAction<CityModel> cr = (result.isDeleteOperation()) ? new CrudAction<>(result.getTarget()) :
                    new CrudAction<>(result.getTarget(), false);
            cityChanged.set(cr);
            return cr;
        }
        return null;
    }
    
    public void deleteCity(Event event, CityModel item, ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage) {
        Optional<ButtonType> response = Alerts.showWarningAlert(getResourceString(RESOURCEKEY_CONFIRMDELETE),
                getResourceString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES)
            TaskWaiter.execute(new DeleteTask<>(item, (Stage)contentPane.getScene().getWindow(), getDeleteDependencyMessage,
                    (m) -> cityChanged.set(new CrudAction<>(m))));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="AddressImpl operations">
    
    public AddressModel addNewAddress(Event event) {
        EditItem.ShowAndWaitResult<AddressModel> result = EditItem.waitEdit(EditAddress.class,
                new AddressModel(new AddressImpl()), (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            addressChanged.set(new CrudAction<>(result.getTarget(), true));
            return result.getTarget();
        }
        return null;
    }
    
    public CrudAction<AddressModel> editAddress(Event event, AddressModel item) {
        EditItem.ShowAndWaitResult<AddressModel> result = EditItem.waitEdit(EditAddress.class, item, (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            CrudAction<AddressModel> cr = (result.isDeleteOperation()) ? new CrudAction<>(result.getTarget()) :
                    new CrudAction<>(result.getTarget(), false);
            addressChanged.set(cr);
            return cr;
        }
        return null;
    }
    
    public void deleteAddress(Event event, AddressModel item, ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage) {
        Optional<ButtonType> response = Alerts.showWarningAlert(getResourceString(RESOURCEKEY_CONFIRMDELETE),
                getResourceString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES)
            TaskWaiter.execute(new DeleteTask<>(item, (Stage)contentPane.getScene().getWindow(), getDeleteDependencyMessage,
                    (m) -> addressChanged.set(new CrudAction<>(m))));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="UserImpl operations">
    
    public UserModel addNewUser(Event event) {
        EditItem.ShowAndWaitResult<UserModel> result = EditItem.waitEdit(EditUser.class,
                new UserModel(new UserImpl()), (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            userChanged.set(new CrudAction<>(result.getTarget(), true));
            return result.getTarget();
        }
        return null;
    }
    
    public CrudAction<UserModel> editUser(Event event, UserModel item) {
        EditItem.ShowAndWaitResult<UserModel> result = EditItem.waitEdit(EditUser.class, item, (Stage)contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            CrudAction<UserModel> cr = (result.isDeleteOperation()) ? new CrudAction<>(result.getTarget()) :
                    new CrudAction<>(result.getTarget(), false);
            userChanged.set(cr);
            return cr;
        }
        return null;
    }
    
    public void deleteUser(Event event, UserModel item, ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage) {
        Optional<ButtonType> response = Alerts.showWarningAlert(getResourceString(RESOURCEKEY_CONFIRMDELETE),
                getResourceString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES)
            TaskWaiter.execute(new DeleteTask<>(item, (Stage)contentPane.getScene().getWindow(), getDeleteDependencyMessage,
                    (m) -> userChanged.set(new CrudAction<>(m))));
    }
    
    //</editor-fold>

    /**
     * Loads content for the {@link #contentPane}.
     * @param <C> The type of controller for the contents of the {@link #contentPane}.
     * @param controllerClass The controller class for the contents of the {@link #contentPane}.
     * @param stage The {@link Stage} for the view associated with the current main controller.
     * @param onShown This gets called after the loaded view has been added to the {@link #contentPane}.
     * @throws IOException If unable to load the new view and controller.
     */
    public <C extends MainContentController> void setContent(Class<C> controllerClass, Stage stage, BiConsumer<Parent, C> onShown) throws IOException {
        load(stage, controllerClass, (Parent v, C c) -> {
            ((MainContentController)c).mainController = MainController.this;
        }, (Parent v, C c) -> {
            MainContentController oldController = contentController;
            Node oldView = contentPane.getCenter();
            contentController = c;
            contentPane.setCenter(v);
            try {
                if (null != oldController)
                    oldController.onUnloaded(oldView);
            } finally {
                if (null != onShown)
                    onShown.accept(v, c);
            }
        });
    }
    
    private class DeleteTask<M extends ItemModel<?>> extends TaskWaiter<String> {
        private final DataObjectImpl dao;
        private final M model;
        private final Consumer<M> onDeleted;
        private final ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage;
        DeleteTask(M model, Stage stage, ThrowableFunction<Connection, String, Exception> getDeleteDependencyMessage, Consumer<M> onDeleted) {
            super(stage, getResourceString(RESOURCEKEY_DELETINGRECORD));
            dao = (this.model = model).getDataObject();
            this.onDeleted = onDeleted;
            this.getDeleteDependencyMessage = Objects.requireNonNull(getDeleteDependencyMessage);
        }

        @Override
        protected void processResult(String message, Window owner) {
            if (null != message && !message.trim().isEmpty())
                Alerts.showWarningAlert(getResourceString(RESOURCEKEY_DELETEFAILURE), message);
            else if (null != onDeleted)
                onDeleted.accept(model);
        }

        @Override
        protected void processException(Throwable ex, Window owner) {
            Alerts.showWarningAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORDELETINGFROMDB));
            LOG.log(Level.SEVERE, "Error deleting record", ex);
        }

        @Override
        protected String getResult() throws Exception {
            try (DbConnector dep = new DbConnector()) {
                String message = getDeleteDependencyMessage.apply(dep.getConnection());
                if (null != message && !message.trim().isEmpty())
                    return message;
                dao.delete(dep.getConnection());
            }
            return null;
        }
    }
    
    /**
     * Base class for controllers that represent content views for the {@link MainController}.
     */
    public static class MainContentController extends SchedulerController {

        private MainController mainController;

        /**
         * Gets the current {@link MainController}.
         * @return The current {@link MainController}.
         */
        protected MainController getMainController() { return mainController; }
        
    }
}