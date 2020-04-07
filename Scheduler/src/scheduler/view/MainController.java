package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.util.AlertHelper;
import scheduler.util.EventHelper;
import scheduler.util.ViewControllerLoader;
import scheduler.view.address.AddressModelImpl;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.city.CityModelImpl;
import scheduler.view.city.EditCity;
import scheduler.view.country.CountryModel;
import scheduler.view.country.EditCountry;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.customer.EditCustomer;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventType;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModelImpl;

/**
 * FXML Controller class for main application content.
 * <p>
 * This controller will remain active from the time the user is logged in until the application exits.</p>
 * <p>
 * All data object create, update and delete operations should be initiated through this controller. This allows dynamically loaded views to be
 * notified of changes, if necessary.</p>
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
    private StackPane contentPane;

    private MainContentController contentController;

    public StackPane getContentPane() {
        return contentPane;
    }

    @FXML
    private void initialize() {
        assert appointmentsMenu != null : String.format("fx:id=\"appointmentsMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newAppointmentMenuItem, String.format("fx:id=\"newAppointmentMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            addNewAppointment((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow());
        });
        Objects.requireNonNull(allAppointmentsMenuItem, String.format("fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageAppointments.loadInto(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        AppointmentModel.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                // TODO: Internationalize message
                AlertHelper.showErrorAlert((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(), LOG,
                        "Error loading appointments listing", ex);
            }
        });
        assert customersMenu != null : String.format("fx:id=\"customersMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCustomerMenuItem, String.format("fx:id=\"newCustomerMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            addNewCustomer((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow());
        });
        Objects.requireNonNull(allCustomersMenuItem, String.format("fx:id=\"allCustomersMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageCustomers.loadInto(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        CustomerModelImpl.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                // TODO: Internationalize message
                AlertHelper.showErrorAlert((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(), LOG,
                        "Error loading customers listing", ex);
            }
        });
        assert addressMenu != null : String.format("fx:id=\"addressMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newCountryMenuItem, String.format("fx:id=\"newCountryMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            addNewCountry((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow());
        });
        Objects.requireNonNull(newCityMenuItem, String.format("fx:id=\"newCityMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            addNewCity((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow());
        });
        Objects.requireNonNull(newAddressMenuItem, String.format("fx:id=\"newAddressMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            addNewAddress((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow());
        });
        Objects.requireNonNull(allCountriesMenuItem, String.format("fx:id=\"allCountriesMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageCountries.loadInto(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        CountryModel.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                // TODO: Internationalize message
                AlertHelper.showErrorAlert((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(), LOG,
                        "Error loading countries listing", ex);
            }
        });
        assert usersMenu != null : String.format("fx:id=\"usersMenu\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(newUserMenuItem, String.format("fx:id=\"newUserMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            addNewUser((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow());
        });
        Objects.requireNonNull(allUsersMenuItem, String.format("fx:id=\"allUsersMenuItem\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            try {
                ManageUsers.loadInto(MainController.this, (Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(),
                        UserModelImpl.getFactory().getAllItemsFilter());
            } catch (IOException ex) {
                // TODO: Internationalize message
                AlertHelper.showErrorAlert((Stage) ((MenuItem) event.getSource()).getGraphic().getScene().getWindow(), LOG,
                        "Error loading users listing", ex);
            }
        });
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
    }

    /**
     * Loads a view and controller for the {@link #contentPane}.
     *
     * @param <T> The type of controller that will be instantiated.
     * @param controllerClass The controller class that will be used to load the FXML view.
     * @param loadEventListener An object that can listen for FXML load events. This object can implement {@link FxmlViewControllerEventListener} or
     * use the {@link scheduler.view.annotations.HandlesFxmlViewEvent} annotation to handle view/controller life-cycle events.
     * @return The instantiated controller.
     * @throws IOException if not able to load the FXML resource.
     */
    public <T extends MainContentController> T loadContent(Class<T> controllerClass, Object loadEventListener) throws IOException {
        return ViewControllerLoader.replacePaneContent(this, contentPane, controllerClass,
                (FxmlViewControllerEventListener<Parent, T>) (event) -> {
                    if (event.getType() == FxmlViewEventType.SHOWN) {
                        ((MainContentController) event.getController()).mainController = MainController.this;
                        contentController = event.getController();
                    }
                    EventHelper.fireFxmlViewEvent(loadEventListener, event);
                });
    }

    /**
     * Loads a view and controller for the {@link #contentPane}.
     *
     * @param <T> The type of controller that will be instantiated.
     * @param controllerClass The controller class that will be used to load the FXML view.
     * @return The instantiated controller.
     * @throws IOException if not able to load the FXML resource.
     */
    public <T extends MainContentController> T loadContent(Class<T> controllerClass) throws IOException {
        return loadContent(controllerClass, null);
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AppointmentModel} or {@code null} if the operation was canceled.
     */
    public AppointmentModel addNewAppointment(Stage stage) {
        try {
            return EditAppointment.editNew(this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new appointment edit window", ex);
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be edited.
     */
    public void editAppointment(Stage stage, AppointmentModel item) {
        try {
            EditAppointment.edit(item, this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading appointment edit window", ex);
        }
    }

    /**
     * Deletes an {@link AppointmentModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be deleted.
     */
    public void deleteAppointment(Stage stage, AppointmentModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), AppointmentModel.getFactory(),
                    (m) -> EventHelper.fireDataObjectEvent(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CustomerModelImpl} or {@code null} if the operation was canceled.
     */
    public CustomerModelImpl addNewCustomer(Stage stage) {
        try {
            return EditCustomer.editNew(this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new customer edit window", ex);
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be edited.
     */
    public void editCustomer(Stage stage, CustomerModelImpl item) {
        try {
            EditCustomer.edit(item, this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading customer edit window", ex);
        }
    }

    /**
     * Deletes a {@link CustomerModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be deleted.
     */
    public void deleteCustomer(Stage stage, CustomerModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CustomerModelImpl.getFactory(),
                    (m) -> EventHelper.fireDataObjectEvent(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CountryModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CountryModel} or {@code null} if the operation was canceled.
     */
    public CountryModel addNewCountry(Stage stage) {
        try {
            return EditCountry.editNew(this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new country edit window", ex);
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CountryModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be edited.
     */
    public void editCountry(Stage stage, CountryModel item) {
        try {
            EditCountry.edit(item, this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading country edit window", ex);
        }
    }

    /**
     * Deletes a {@link CountryModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be deleted.
     */
    public void deleteCountry(Stage stage, CountryModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CountryModel.getFactory(),
                    (m) -> EventHelper.fireDataObjectEvent(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CityModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CityModelImpl} or {@code null} if the operation was canceled.
     */
    public CityModelImpl addNewCity(Stage stage) {
        try {
            return EditCity.editNew(this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new city edit window", ex);
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CityModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be edited.
     */
    public void editCity(Stage stage, CityModelImpl item) {
        try {
            EditCity.edit(item, this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading city edit window", ex);
        }
    }

    /**
     * Deletes a {@link CityModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be deleted.
     */
    public void deleteCity(Stage stage, CityModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CityModelImpl.getFactory(),
                    (m) -> EventHelper.fireDataObjectEvent(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AddressModelImpl} or {@code null} if the operation was canceled.
     */
    public AddressModelImpl addNewAddress(Stage stage) {
        try {
            return EditAddress.editNew(this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new address edit window", ex);
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be edited.
     */
    public void editAddress(Stage stage, AddressModelImpl item) {
        try {
            EditAddress.edit(item, this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading address edit window", ex);
        }
    }

    /**
     * Deletes an {@link AddressModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be deleted.
     */
    public void deleteAddress(Stage stage, AddressModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), AddressModelImpl.getFactory(),
                    (m) -> EventHelper.fireDataObjectEvent(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link UserModelImpl} or {@code null} if the operation was canceled.
     */
    public UserModelImpl addNewUser(Stage stage) {
        try {
            return EditUser.editNew(this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new user edit window", ex);
        }
        return null;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be edited.
     */
    public void editUser(Stage stage, UserModelImpl item) {
        try {
            EditUser.edit(item, this, stage);
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading user edit window", ex);
        }
    }

    /**
     * Deletes a {@link UserModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be deleted.
     */
    public void deleteUser(Stage stage, UserModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), UserModelImpl.getFactory(),
                    (m) -> EventHelper.fireDataObjectEvent(contentController, new DataObjectEvent<>(this,
                            DaoChangeAction.DELETED, m.getDataObject()))));
        }
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.SHOWN)
    private void onShown(FxmlViewEvent<? extends Parent> event) {
        try {
            ManageAppointments.loadInto(this, event.getStage(), AppointmentModel.getFactory().getDefaultFilter());
        } catch (IOException ex) {
            // TODO: Internationalize message
            AlertHelper.showErrorAlert(event.getStage(), LOG, "Error loading appointments", ex);
        }
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.UNLOADED)
    private void onUnloaded(FxmlViewEvent<? extends Parent> event) {
        ViewControllerLoader.clearPaneContent(this, contentPane);
    }

    // TODO: Replace with annotated field
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

    private class DeleteTask<D extends DataAccessObject, M extends ItemModel<D>> extends TaskWaiter<String> {

        private final M model;
        private final Consumer<M> onDeleted;
        private final DataAccessObject.DaoFactory<D> factory;

        DeleteTask(M model, Stage stage, ItemModel.ModelFactory<D, M> factory, Consumer<M> onDeleted) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETINGRECORD));
            this.model = model;
            this.onDeleted = onDeleted;
            this.factory = factory.getDaoFactory();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), message);
            } else if (null != onDeleted) {
                onDeleted.accept(model);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.log(Level.SEVERE, "Error deleting record", ex);
            AlertHelper.showErrorAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORDELETINGFROMDB), ex);
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
