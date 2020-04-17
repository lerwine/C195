package scheduler.view;

import com.sun.javafx.binding.ExpressionHelper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
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
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModelImpl;
import static scheduler.view.MainControllerResourceKeys.*;
import scheduler.view.event.FxmlViewEventListener;

/**
 * FXML Controller class for main application content.
 * <p>
 * This controller will remain active from the time the user is logged in until the application exits.</p>
 * <p>
 * All data object create, update and delete operations should be initiated through this controller. This allows dynamically loaded views to be
 * notified of changes, if necessary.</p>
 * <p>
 * The associated view is {@code /resources/scheduler/view/MainView.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/MainView.fxml")
public final class MainController extends SchedulerController implements IMainCRUD {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());
    
    private EventHelper<DataObjectEventListener<? extends DataAccessObject>, DataObjectEvent<? extends DataAccessObject>> daoEventHelper;

    @FXML // fx:id="contentPane"
    private StackPane contentPane; // Value injected by FXMLLoader

    private MainContentController contentController;

    public StackPane getContentPane() {
        return contentPane;
    }

    @FXML
    void onAllAppointmentsMenuItemAction(ActionEvent event) {
        try {
            ManageAppointments.loadInto(MainController.this, (Stage) contentPane.getScene().getWindow(),
                    AppointmentModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert((Stage) contentPane.getScene().getWindow(), LOG,
                    "Error loading appointments listing", ex);
        }
    }

    @FXML
    void onAllCustomersMenuItemAction(ActionEvent event) {
        try {
            ManageCustomers.loadInto(MainController.this, (Stage) contentPane.getScene().getWindow(),
                    CustomerModelImpl.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert((Stage) contentPane.getScene().getWindow(), LOG,
                    "Error loading customers listing", ex);
        }
    }

    @FXML
    void onAllUsersMenuItemAction(ActionEvent event) {
        try {
            ManageUsers.loadInto(MainController.this, (Stage) contentPane.getScene().getWindow(),
                    UserModelImpl.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert((Stage) contentPane.getScene().getWindow(), LOG,
                    "Error loading users listing", ex);
        }
    }

    @FXML
    void onNewAddressMenuItem(ActionEvent event) {
        addNewAddress((Stage) contentPane.getScene().getWindow());
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AddressModelImpl} or {@code null} if the operation was canceled.
     */
    @Override
    public AddressModelImpl addNewAddress(Stage stage) {
        try {
            return EditAddress.editNew(this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new address edit window", ex);
        }
        return null;
    }

    @FXML
    void onNewAppointmentMenuItemAction(ActionEvent event) {
        addNewAppointment((Stage) contentPane.getScene().getWindow());
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AppointmentModel} or {@code null} if the operation was canceled.
     */
    @Override
    public AppointmentModel addNewAppointment(Stage stage) {
        try {
            return EditAppointment.editNew(this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new appointment edit window", ex);
        }
        return null;
    }

    @FXML
    void onNewCustomerMenuItemAction(ActionEvent event) {
        addNewCustomer((Stage) contentPane.getScene().getWindow());
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CustomerModelImpl} or {@code null} if the operation was canceled.
     */
    @Override
    public CustomerModelImpl addNewCustomer(Stage stage) {
        try {
            return EditCustomer.editNew(this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new customer edit window", ex);
        }
        return null;
    }

    @FXML
    void onNewUserMenuItemAction(ActionEvent event) {
        addNewUser((Stage) contentPane.getScene().getWindow());
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link UserModelImpl} or {@code null} if the operation was canceled.
     */
    @Override
    public UserModelImpl addNewUser(Stage stage) {
        try {
            return EditUser.editNew(this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading new user edit window", ex);
        }
        return null;
    }

    @FXML
    void onAllCountriesMenuItemAction(ActionEvent event) {
        try {
            ManageCountries.loadInto(MainController.this, (Stage) contentPane.getScene().getWindow(),
                    CountryModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert((Stage) contentPane.getScene().getWindow(), LOG,
                    "Error loading countries listing", ex);
        }
    }

    @FXML
    private void initialize() {
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'MainView.fxml'.";
        daoEventHelper = new EventHelper<>("onDataObjectEvent");
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
    @SuppressWarnings("incomplete-switch")
    public <T extends MainContentController> T loadContent(Class<T> controllerClass, Object loadEventListener) throws IOException {
        return ViewControllerLoader.replacePaneContent(this, contentPane, controllerClass,
                (FxmlViewControllerEventListener<Parent, T>) (event) -> {
                    switch (event.getType()) {
                        case LOADED:
                            event.getStage().setTitle(AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTSCHEDULER));
                            break;
                        case SHOWN:
                            ((MainContentController) event.getController()).mainController = MainController.this;
                            contentController = event.getController();
                            break;
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

    @Override
    public void addDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener) {
        daoEventHelper.addListener(listener);
    }
    
    @Override
    public void removeDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener) {
        daoEventHelper.removeListener(listener);
    }
    
    @Override
    public <T extends DataAccessObject> void fireDaoEvent(Object source, DaoChangeAction action, T dao) {
        DataObjectEvent<T> event = new DataObjectEvent<>(source, action, dao);
        EventHelper.fireDataObjectEvent(contentController, event);
        daoEventHelper.raiseEvent(event);
    }
    
    /**
     * Opens an {@link EditItem} window to edit an {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be edited.
     */
    @Override
    public void editAppointment(Stage stage, AppointmentModel item) {
        try {
            EditAppointment.edit(item, this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading appointment edit window", ex);
        }
    }

    /**
     * Deletes an {@link AppointmentModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be deleted.
     */
    @Override
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
     * Opens an {@link EditItem} window to edit a {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be edited.
     */
    @Override
    public void editCustomer(Stage stage, CustomerModelImpl item) {
        try {
            EditCustomer.edit(item, this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading customer edit window", ex);
        }
    }

    /**
     * Deletes a {@link CustomerModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be deleted.
     */
    @Override
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
     * Opens an {@link EditItem} window to edit a {@link CountryModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be edited.
     */
    @Override
    public void openCountry(Stage stage, CountryModel item) {
        try {
            EditCountry.edit(item, this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading country edit window", ex);
        }
    }

    /**
     * Deletes a {@link CountryModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be deleted.
     */
    @Override
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
     * Opens an {@link EditItem} window to edit a {@link CityModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be edited.
     */
    @Override
    public void openCity(Stage stage, CityModelImpl item) {
        try {
            EditCity.edit(item, this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading city edit window", ex);
        }
    }

    /**
     * Deletes a {@link CityModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be deleted.
     */
    @Override
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
     * Opens an {@link EditItem} window to edit an {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be edited.
     */
    @Override
    public void editAddress(Stage stage, AddressModelImpl item) {
        try {
            EditAddress.edit(item, this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading address edit window", ex);
        }
    }

    /**
     * Deletes an {@link AddressModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be deleted.
     */
    @Override
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
     * Opens an {@link EditItem} window to edit a {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be edited.
     */
    @Override
    public void editUser(Stage stage, UserModelImpl item) {
        try {
            EditUser.edit(item, this, stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading user edit window", ex);
        }
    }

    /**
     * Deletes a {@link UserModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be deleted.
     */
    @Override
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
        event.getStage().setTitle(AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTSCHEDULER));
        try {
            ManageAppointments.loadInto(this, event.getStage(), AppointmentModel.getFactory().getDefaultFilter());
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(event.getStage(), LOG, "Error loading appointments", ex);
        }
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.UNLOADED)
    private void onUnloaded(FxmlViewEvent<? extends Parent> event) {
        ViewControllerLoader.clearPaneContent(this, contentPane);
    }

    // PENDING: Replace with annotated field
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
