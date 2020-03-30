package scheduler.view;

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
import scheduler.AppResources;
import scheduler.dao.DaoChangeAction;
import scheduler.dao.DataObjectEvent;
import scheduler.dao.DataObjectImpl;
import scheduler.util.AlertHelper;
import scheduler.util.AnnotationHelper;
import scheduler.view.address.AddressModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.annotations.HandlesViewLifecycleEvent;
import scheduler.view.annotations.ViewLifecycleEventType;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.city.CityModel;
import scheduler.view.country.CountryModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.user.UserModel;

/**
 * FXML Controller class for main application content. All application views are loaded into the {@link #contentPane} using
 * {@link MainContentController#setContent(scheduler.view.MainController, Class, Stage)}. The initial content is loaded using
 * {@link ManageAppointments#setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.AppointmentFilter)}. All Create, Update and
 * Delete operations on {@link ItemModel} objects are to be initiated through this controller.
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

//    private final ItemEventManager<ItemEvent<AppointmentModel>> appointmentAddManager;
//    private final ItemEventManager<ItemEvent<AppointmentModel>> appointmentRemoveManager;
//    private final ItemEventManager<ItemEvent<CustomerModel>> customerAddManager;
//    private final ItemEventManager<ItemEvent<CustomerModel>> customerRemoveManager;
//    private final ItemEventManager<ItemEvent<UserModel>> userAddManager;
//    private final ItemEventManager<ItemEvent<UserModel>> userRemoveManager;
//    private final ItemEventManager<ItemEvent<AddressModel>> addressAddManager;
//    private final ItemEventManager<ItemEvent<AddressModel>> addressRemoveManager;
//    private final ItemEventManager<ItemEvent<CityModel>> cityAddManager;
//    private final ItemEventManager<ItemEvent<CityModel>> cityRemoveManager;
//    private final ItemEventManager<ItemEvent<CountryModel>> countryAddManager;
//    private final ItemEventManager<ItemEvent<CountryModel>> countryRemoveManager;
    public MainController() {
//        appointmentAddManager = new ItemEventManager<>();
//        appointmentRemoveManager = new ItemEventManager<>();
//        customerAddManager = new ItemEventManager<>();
//        customerRemoveManager = new ItemEventManager<>();
//        userAddManager = new ItemEventManager<>();
//        userRemoveManager = new ItemEventManager<>();
//        addressAddManager = new ItemEventManager<>();
//        addressRemoveManager = new ItemEventManager<>();
//        cityAddManager = new ItemEventManager<>();
//        cityRemoveManager = new ItemEventManager<>();
//        countryAddManager = new ItemEventManager<>();
//        countryRemoveManager = new ItemEventManager<>();
    }

    public BorderPane getContentPane() {
        return contentPane;
    }

//    public ItemEventManager<ItemEvent<AppointmentModel>> getAppointmentAddManager() {
//        return appointmentAddManager;
//    }
//
//    public ItemEventManager<ItemEvent<AppointmentModel>> getAppointmentRemoveManager() {
//        return appointmentRemoveManager;
//    }
//
//    public ItemEventManager<ItemEvent<CustomerModel>> getCustomerAddManager() {
//        return customerAddManager;
//    }
//
//    public ItemEventManager<ItemEvent<CustomerModel>> getCustomerRemoveManager() {
//        return customerRemoveManager;
//    }
//
//    public ItemEventManager<ItemEvent<UserModel>> getUserAddManager() {
//        return userAddManager;
//    }
//
//    public ItemEventManager<ItemEvent<UserModel>> getUserRemoveManager() {
//        return userRemoveManager;
//    }
//    public ItemEventManager<ItemEvent<AddressModel>> getAddressAddManager() {
//        return addressAddManager;
//    }
//
//    public ItemEventManager<ItemEvent<AddressModel>> getAddressRemoveManager() {
//        return addressRemoveManager;
//    }
//
//    public ItemEventManager<ItemEvent<CityModel>> getCityAddManager() {
//        return cityAddManager;
//    }
//
//    public ItemEventManager<ItemEvent<CityModel>> getCityRemoveManager() {
//        return cityRemoveManager;
//    }
//
//    public ItemEventManager<ItemEvent<CountryModel>> getCountryAddManager() {
//        return countryAddManager;
//    }
//
//    public ItemEventManager<ItemEvent<CountryModel>> getCountryRemoveManager() {
//        return countryRemoveManager;
//    }
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert appointmentsMenu != null : String.format("fx:id=\"appointmentsMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newAppointmentMenuItem, String.format("fx:id=\"newAppointmentMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewAppointment(event));
        Objects.requireNonNull(allAppointmentsMenuItem, String.format("fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
//            ManageAppointments.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
//                        AppointmentImpl.getFactory().getAllItemsFilter());
            throw new UnsupportedOperationException();
            // TODO: Implement this
        });
        assert customersMenu != null : String.format("fx:id=\"customersMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCustomerMenuItem, String.format("fx:id=\"newCustomerMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewCustomer(event));
        Objects.requireNonNull(allCustomersMenuItem, String.format("fx:id=\"allCustomersMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
//            ManageCustomers.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
//                        CustomerModel.getFactory().getAllItemsFilter());
            throw new UnsupportedOperationException();
            // TODO: Implement this
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
//            try {
//                ManageCountries.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
//                        CountryModel.getFactory().getAllItemsFilter());
//            } catch (IOException ex) {
//                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//            }
            throw new UnsupportedOperationException();
            // TODO: Implement this
        });
        assert usersMenu != null : String.format("fx:id=\"usersMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newUserMenuItem, String.format("fx:id=\"newUserMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> addNewUser(event));
        Objects.requireNonNull(allUsersMenuItem, String.format("fx:id=\"allUsersMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
//            ManageUsers.setContent(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
//                    UserImpl.getFactory().getAllItemsFilter());
            throw new UnsupportedOperationException();
            // TODO: Implement this
        });
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
    }

    @HandlesViewLifecycleEvent(type = ViewLifecycleEventType.ADDED)
    private void onBeforeShow(Node currentView, Stage stage) {
//        ManageAppointments.setContent(MainController.this, stage, AppointmentImpl.getFactory().getDefaultFilter());
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    @HandlesViewLifecycleEvent(type = ViewLifecycleEventType.UNLOADED)
    private void onUnloaded(Node view) {
        if (null != contentController) {
            AnnotationHelper.invokeViewLifecycleEventMethods(contentController, new ViewLifecycleEvent<>(this, ViewLifecycleEventReason.UNLOADED,
                    (Parent) view, (Stage) contentPane.getScene().getWindow()));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link AppointmentModel} or {@code null} if the operation was canceled.
     */
    public AppointmentModel addNewAppointment(Event event) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AppointmentModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AppointmentModel} to be edited.
     */
    public void editAppointment(Event event, AppointmentModel item) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Deletes an {@link AppointmentModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AppointmentModel} to be deleted.
     */
    public void deleteAppointment(Event event, AppointmentModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), AppointmentModel.getFactory(),
                    (m) -> AnnotationHelper.invokeDataObjectEventMethods(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CustomerModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link CustomerModel} or {@code null} if the operation was canceled.
     */
    public CustomerModel addNewCustomer(Event event) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CustomerModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CustomerModel} to be edited.
     */
    public void editCustomer(Event event, CustomerModel item) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Deletes a {@link CustomerModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CustomerModel} to be deleted.
     */
    public void deleteCustomer(Event event, CustomerModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CustomerModel.getFactory(),
                    (m) -> AnnotationHelper.invokeDataObjectEventMethods(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CountryModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link CountryModel} or {@code null} if the operation was canceled.
     */
    public CountryModel addNewCountry(Event event) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CountryModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CountryModel} to be edited.
     */
    public void editCountry(Event event, CountryModel item) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Deletes a {@link CountryModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CountryModel} to be deleted.
     */
    public void deleteCountry(Event event, CountryModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CountryModel.getFactory(),
                    (m) -> AnnotationHelper.invokeDataObjectEventMethods(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CityModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link CityModel} or {@code null} if the operation was canceled.
     */
    public CityModel addNewCity(Event event) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CityModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CityModel} to be edited.
     */
    public void editCity(Event event, CityModel item) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Deletes a {@link CityModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link CityModel} to be deleted.
     */
    public void deleteCity(Event event, CityModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CityModel.getFactory(),
                    (m) -> AnnotationHelper.invokeDataObjectEventMethods(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link AddressModel} or {@code null} if the operation was canceled.
     */
    public AddressModel addNewAddress(Event event) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AddressModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AddressModel} to be edited.
     */
    public void editAddress(Event event, AddressModel item) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Deletes an {@link AddressModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link AddressModel} to be deleted.
     */
    public void deleteAddress(Event event, AddressModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), AddressModel.getFactory(),
                    (m) -> AnnotationHelper.invokeDataObjectEventMethods(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModel}.
     *
     * @param event Contextual information about the initiating event.
     * @return The newly added {@link UserModel} or {@code null} if the operation was canceled.
     */
    public UserModel addNewUser(Event event) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link UserModel}.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link UserModel} to be edited.
     */
    public void editUser(Event event, UserModel item) {
        throw new UnsupportedOperationException();
        // TODO: Implement this
    }

    /**
     * Deletes a {@link UserModel} item after confirming with user.
     *
     * @param event Contextual information about the initiating event.
     * @param item The {@link UserModel} to be deleted.
     */
    public void deleteUser(Event event, UserModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.execute(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), UserModel.getFactory(),
                    (m) -> AnnotationHelper.invokeDataObjectEventMethods(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    @HandlesDataObjectEvent
    private void onDataObjectEvent(DataObjectEvent<? extends DataObjectImpl> event) {
        AnnotationHelper.invokeDataObjectEventMethods(contentController, event);
    }

    /**
     * Base class for controllers that represent content views for the {@link MainController}. This allows content controllers to raise events on the
     * main controller.
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

    }

    private class DeleteTask<D extends DataObjectImpl, M extends ItemModel<D>> extends TaskWaiter<String> {

        private final M model;
        private final Consumer<M> onDeleted;
        private final DataObjectImpl.DaoFactory<D> factory;

        DeleteTask(M model, Stage stage, ItemModel.ModelFactory<D, M> factory, Consumer<M> onDeleted) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETINGRECORD));
            this.model = model;
            this.onDeleted = onDeleted;
            this.factory = factory.getDaoFactory();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), message);
            } else if (null != onDeleted) {
                onDeleted.accept(model);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error deleting record", ex);
            AlertHelper.showErrorAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORDELETINGFROMDB), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = factory.getDeleteDependencyMessage(model.getDataObject(), connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }

            factory.delete(model.getDataObject(), connection);
            return null;
        }
    }
}
