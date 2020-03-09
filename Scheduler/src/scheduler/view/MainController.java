package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.App;
import scheduler.AppResources;
import scheduler.dao.AddressImpl;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.CityImpl;
import scheduler.dao.CountryImpl;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.UserImpl;
import scheduler.util.Alerts;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.address.AddressModel;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModel;
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
import scheduler.view.user.UserReferenceModelImpl;

/**
 * FXML Controller class for main application content. All application views are loaded into the {@link #contentPane} using
 * {@link MainContentController#setContent(scheduler.view.MainController, java.lang.Class, javafx.stage.Stage)}. The initial content is loaded using
 * {@link ManageAppointments#setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.ModelFilter)}. All Create, Update and Delete
 * operations on {@link ItemModel} objects are initiated through this controller.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/MainView.fxml")
public final class MainController extends SchedulerController implements MainControllerConstants {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

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

    private MainContentController contentController;

    private final ItemEventManager<ItemEvent<AppointmentModel>> appointmentAddManager;
    private final ItemEventManager<ItemEvent<AppointmentModel>> appointmentRemoveManager;
    private final ItemEventManager<ItemEvent<CustomerModel>> customerAddManager;
    private final ItemEventManager<ItemEvent<CustomerModel>> customerRemoveManager;
    private final ItemEventManager<ItemEvent<UserModel>> userAddManager;
    private final ItemEventManager<ItemEvent<UserModel>> userRemoveManager;
    private final ItemEventManager<ItemEvent<AddressModel>> addressAddManager;
    private final ItemEventManager<ItemEvent<AddressModel>> addressRemoveManager;
    private final ItemEventManager<ItemEvent<CityModel>> cityAddManager;
    private final ItemEventManager<ItemEvent<CityModel>> cityRemoveManager;
    private final ItemEventManager<ItemEvent<CountryModel>> countryAddManager;
    private final ItemEventManager<ItemEvent<CountryModel>> countryRemoveManager;

    public MainController() {
        appointmentAddManager = new ItemEventManager<>();
        appointmentRemoveManager = new ItemEventManager<>();
        customerAddManager = new ItemEventManager<>();
        customerRemoveManager = new ItemEventManager<>();
        userAddManager = new ItemEventManager<>();
        userRemoveManager = new ItemEventManager<>();
        addressAddManager = new ItemEventManager<>();
        addressRemoveManager = new ItemEventManager<>();
        cityAddManager = new ItemEventManager<>();
        cityRemoveManager = new ItemEventManager<>();
        countryAddManager = new ItemEventManager<>();
        countryRemoveManager = new ItemEventManager<>();
    }

    public BorderPane getContentPane() {
        return contentPane;
    }

    public ItemEventManager<ItemEvent<AppointmentModel>> getAppointmentAddManager() {
        return appointmentAddManager;
    }

    public ItemEventManager<ItemEvent<AppointmentModel>> getAppointmentRemoveManager() {
        return appointmentRemoveManager;
    }

    public ItemEventManager<ItemEvent<CustomerModel>> getCustomerAddManager() {
        return customerAddManager;
    }

    public ItemEventManager<ItemEvent<CustomerModel>> getCustomerRemoveManager() {
        return customerRemoveManager;
    }

    public ItemEventManager<ItemEvent<UserModel>> getUserAddManager() {
        return userAddManager;
    }

    public ItemEventManager<ItemEvent<UserModel>> getUserRemoveManager() {
        return userRemoveManager;
    }

    public ItemEventManager<ItemEvent<AddressModel>> getAddressAddManager() {
        return addressAddManager;
    }

    public ItemEventManager<ItemEvent<AddressModel>> getAddressRemoveManager() {
        return addressRemoveManager;
    }

    public ItemEventManager<ItemEvent<CityModel>> getCityAddManager() {
        return cityAddManager;
    }

    public ItemEventManager<ItemEvent<CityModel>> getCityRemoveManager() {
        return cityRemoveManager;
    }

    public ItemEventManager<ItemEvent<CountryModel>> getCountryAddManager() {
        return countryAddManager;
    }

    public ItemEventManager<ItemEvent<CountryModel>> getCountryRemoveManager() {
        return countryRemoveManager;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert appointmentsMenu != null : String.format("fx:id=\"appointmentsMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newAppointmentMenuItem, String.format("fx:id=\"newAppointmentMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewAppointment(event));
        Objects.requireNonNull(allAppointmentsMenuItem, String.format("fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageAppointments.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        AppointmentImpl.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error loading ManageAppointments view/controller", ex);
            }
        });
        assert customersMenu != null : String.format("fx:id=\"customersMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCustomerMenuItem, String.format("fx:id=\"newCustomerMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCustomer(event));
        Objects.requireNonNull(allCustomersMenuItem, String.format("fx:id=\"allCustomersMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageCustomers.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        CustomerImpl.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error loading ManageCustomers view/controller", ex);
            }
        });
        assert addressMenu != null : String.format("fx:id=\"addressMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCountryMenuItem, String.format("fx:id=\"newCountryMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCountry(event));
        Objects.requireNonNull(newCityMenuItem, String.format("fx:id=\"newCityMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCity(event));
        Objects.requireNonNull(newAddressMenuItem, String.format("fx:id=\"newAddressMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewAddress(event));
        Objects.requireNonNull(allCountriesMenuItem, String.format("fx:id=\"allCountriesMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageCountries.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        CountryImpl.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error loading ManageCountries view/controller", ex);
            }
        });
        assert usersMenu != null : String.format("fx:id=\"usersMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newUserMenuItem, String.format("fx:id=\"newUserMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewUser(event));
        Objects.requireNonNull(allUsersMenuItem, String.format("fx:id=\"allUsersMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageUsers.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        UserImpl.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error loading ManageUsers view/controller", ex);
            }
        });
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
    }

    @Override
    protected void onBeforeShow(Node currentView, Stage stage) {
        super.onBeforeShow(currentView, stage);
        try {
            ManageAppointments.setContent(MainController.this, stage, AppointmentImpl.getFactory().getDefaultFilter());
        } catch (IOException ex) {
            LOG.logp(Level.SEVERE, MainController.class.getName(), "onBeforeShow", "Error loading ManageAppointments view/controller", ex);
        }
    }

    @Override
    protected void onUnloaded(Node view) {
        super.onUnloaded(view);
        if (null != contentController) {
            contentController.onUnloaded(contentPane.getCenter());
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link AppointmentModel} or {@link null} if the operation was canceled.
     */
    public AppointmentModel addNewAppointment(Event event) {
        AppointmentModel model = new AppointmentModel(new AppointmentImpl());
        model.setUser(new UserReferenceModelImpl(App.getCurrentUser()));
        EditItem.ShowAndWaitResult<AppointmentModel> result = EditItem.waitEdit(EditAppointment.class, model, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            appointmentAddManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
            return result.getTarget();
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AppointmentModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AppointmentModel} to be edited.
     * @return A {@code EditItem.ShowAndWaitResult<AppointmentModel>} object that contains the operation results.
     */
    public EditItem.ShowAndWaitResult<AppointmentModel> editAppointment(Event event, AppointmentModel item) {
        EditItem.ShowAndWaitResult<AppointmentModel> result = EditItem.waitEdit(EditAppointment.class, item, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful() && result.isDeleteOperation()) {
            appointmentRemoveManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
        }
        return result;
    }

    /**
     * Deletes an {@link AppointmentModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AppointmentModel} to be deleted.
     */
    public void deleteAppointment(Event event, AppointmentModel item) {
        Optional<ButtonType> response = Alerts.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(),
                    (m) -> appointmentRemoveManager.fireEvent(new ItemEvent<>(MainController.this, m))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CustomerModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link CustomerModel} or {@link null} if the operation was canceled.
     */
    public CustomerModel addNewCustomer(Event event) {
        CustomerModel model = new CustomerModel(new CustomerImpl());
        EditItem.ShowAndWaitResult<CustomerModel> result = EditItem.waitEdit(EditCustomer.class, model, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            customerAddManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
            return result.getTarget();
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CustomerModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CustomerModel} to be edited.
     * @return A {@code EditItem.ShowAndWaitResult<CustomerModel>} object that contains the operation results.
     */
    public EditItem.ShowAndWaitResult<CustomerModel> editCustomer(Event event, CustomerModel item) {
        EditItem.ShowAndWaitResult<CustomerModel> result = EditItem.waitEdit(EditCustomer.class, item, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful() && result.isDeleteOperation()) {
            customerRemoveManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
        }
        return result;
    }

    /**
     * Deletes a {@link CustomerModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CustomerModel} to be deleted.
     */
    public void deleteCustomer(Event event, CustomerModel item) {
        Optional<ButtonType> response = Alerts.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(),
                    (m) -> customerRemoveManager.fireEvent(new ItemEvent<>(MainController.this, m))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CountryModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link CountryModel} or {@link null} if the operation was canceled.
     */
    public CountryModel addNewCountry(Event event) {
        EditItem.ShowAndWaitResult<CountryModel> result = EditItem.waitEdit(EditCountry.class,
                new CountryModel(new CountryImpl()), (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            countryAddManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
            return result.getTarget();
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CountryModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CountryModel} to be edited.
     * @return A {@code EditItem.ShowAndWaitResult<CountryModel>} object that contains the operation results.
     */
    public EditItem.ShowAndWaitResult<CountryModel> editCountry(Event event, CountryModel item) {
        EditItem.ShowAndWaitResult<CountryModel> result = EditItem.waitEdit(EditCountry.class, item, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful() && result.isDeleteOperation()) {
            countryRemoveManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
        }
        return result;
    }

    /**
     * Deletes a {@link CountryModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CountryModel} to be deleted.
     */
    public void deleteCountry(Event event, CountryModel item) {
        Optional<ButtonType> response = Alerts.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(),
                    (m) -> countryRemoveManager.fireEvent(new ItemEvent<>(MainController.this, m))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CityModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link CityModel} or {@link null} if the operation was canceled.
     */
    public CityModel addNewCity(Event event) {
        CityModel model = new CityModel(new CityImpl());
        EditItem.ShowAndWaitResult<CityModel> result = EditItem.waitEdit(EditCity.class, model, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            cityAddManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
            return result.getTarget();
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CityModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CityModel} to be edited.
     * @return A {@code EditItem.ShowAndWaitResult<CityModel>} object that contains the operation results.
     */
    public EditItem.ShowAndWaitResult<CityModel> editCity(Event event, CityModel item) {
        EditItem.ShowAndWaitResult<CityModel> result = EditItem.waitEdit(EditCity.class, item, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful() && result.isDeleteOperation()) {
            cityRemoveManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
        }
        return result;
    }

    /**
     * Deletes a {@link CityModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CityModel} to be deleted.
     */
    public void deleteCity(Event event, CityModel item) {
        Optional<ButtonType> response = Alerts.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(),
                    (m) -> cityRemoveManager.fireEvent(new ItemEvent<>(MainController.this, m))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link AddressModel} or {@link null} if the operation was canceled.
     */
    public AddressModel addNewAddress(Event event) {
        AddressModel model = new AddressModel(new AddressImpl());
        EditItem.ShowAndWaitResult<AddressModel> result = EditItem.waitEdit(EditAddress.class, model, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            addressAddManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
            return result.getTarget();
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AddressModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AddressModel} to be edited.
     * @return A {@code EditItem.ShowAndWaitResult<AddressModel>} object that contains the operation results.
     */
    public EditItem.ShowAndWaitResult<AddressModel> editAddress(Event event, AddressModel item) {
        EditItem.ShowAndWaitResult<AddressModel> result = EditItem.waitEdit(EditAddress.class, item, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful() && result.isDeleteOperation()) {
            addressRemoveManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
        }
        return result;
    }

    /**
     * Deletes an {@link AddressModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AddressModel} to be deleted.
     */
    public void deleteAddress(Event event, AddressModel item) {
        Optional<ButtonType> response = Alerts.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(),
                    (m) -> addressRemoveManager.fireEvent(new ItemEvent<>(MainController.this, m))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link UserModel} or {@link null} if the operation was canceled.
     */
    public UserModel addNewUser(Event event) {
        EditItem.ShowAndWaitResult<UserModel> result = EditItem.waitEdit(EditUser.class,
                new UserModel(new UserImpl()), (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful()) {
            userAddManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
            return result.getTarget();
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link UserModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link UserModel} to be edited.
     * @return A {@code EditItem.ShowAndWaitResult<UserModel>} object that contains the operation results.
     */
    public EditItem.ShowAndWaitResult<UserModel> editUser(Event event, UserModel item) {
        EditItem.ShowAndWaitResult<UserModel> result = EditItem.waitEdit(EditUser.class, item, (Stage) contentPane.getScene().getWindow());
        if (result.isSuccessful() && result.isDeleteOperation()) {
            userRemoveManager.fireEvent(new ItemEvent<>(MainController.this, result.getTarget()));
        }
        return result;
    }

    /**
     * Deletes a {@link UserModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link UserModel} to be deleted.
     */
    public void deleteUser(Event event, UserModel item) {
        Optional<ButtonType> response = Alerts.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(),
                    (m) -> userRemoveManager.fireEvent(new ItemEvent<>(MainController.this, m))));
        }
    }

    /**
     * Base class for controllers that represent content views for the {@link MainController}.
     */
    public static class MainContentController extends SchedulerController {

        private MainController mainController;

        /**
         * Gets the current {@link MainController}.
         *
         * @return The current {@link MainController}.
         */
        protected MainController getMainController() {
            return mainController;
        }

        /**
         * Loads content for the {@link #contentPane}.
         *
         * @param <C> The type of controller for the contents of the {@link #contentPane}.
         * @param mainController The parent controller for the content.
         * @param controllerClass The controller class for the contents of the {@link #contentPane}.
         * @param stage The {@link Stage} for the view associated with the current main controller.
         * @return The instantiated controller.
         * @throws IOException If unable to load the new view and controller.
         */
        protected static <C extends MainContentController> C setContent(MainController mainController, Class<C> controllerClass,
                Stage stage) throws IOException {
            return load(stage, controllerClass, (Parent v, C c) -> {
                ((MainContentController) c).mainController = mainController;
            }, (Parent v, C c) -> {
                MainContentController oldController = mainController.contentController;
                Node oldView = mainController.contentPane.getCenter();
                try {
                    mainController.contentController = c;
                    mainController.contentPane.setCenter(v);
                } finally {
                    if (null != oldController) {
                        oldController.onUnloaded(oldView);
                    }
                }
            });
        }
    }

    private class DeleteTask<D extends DataObjectImpl, M extends ItemModel<D>> extends TaskWaiter<String> {

        private final M model;
        private final Consumer<M> onDeleted;

        DeleteTask(M model, Stage stage, Consumer<M> onDeleted) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETINGRECORD));
            this.model = model;
            this.onDeleted = onDeleted;
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null != message && !message.trim().isEmpty()) {
                Alerts.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), message);
            } else if (null != onDeleted) {
                onDeleted.accept(model);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error deleting record", ex);
            Alerts.showErrorAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORDELETINGFROMDB), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = model.getDaoFactory().getDeleteDependencyMessage(model.getDataObject(), connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }

            model.getDaoFactory().delete(model.getDataObject(), connection);
            return null;
        }
    }
}
