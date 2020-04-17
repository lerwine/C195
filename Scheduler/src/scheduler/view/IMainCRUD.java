package scheduler.view;

import javafx.stage.Stage;
import scheduler.dao.DataAccessObject;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEventListener;
import scheduler.view.address.AddressModelImpl;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.city.CityModelImpl;
import scheduler.view.country.CountryModel;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.user.UserModelImpl;

/**
 * Interface for exposing functionality of {@link MainController} either directly or via proxy class (ie. {@link EditItem.EditController}).
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IMainCRUD {

    void addDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener);

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AddressModelImpl} or {@code null} if the operation was canceled.
     */
    AddressModelImpl addNewAddress(Stage stage);

    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AppointmentModel} or {@code null} if the operation was canceled.
     */
    AppointmentModel addNewAppointment(Stage stage);

    /**
     * Opens an {@link EditItem} window to edit a new {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CustomerModelImpl} or {@code null} if the operation was canceled.
     */
    CustomerModelImpl addNewCustomer(Stage stage);

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link UserModelImpl} or {@code null} if the operation was canceled.
     */
    UserModelImpl addNewUser(Stage stage);

    /**
     * Deletes an {@link AddressModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be deleted.
     */
    void deleteAddress(Stage stage, AddressModelImpl item);

    /**
     * Deletes an {@link AppointmentModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be deleted.
     */
    void deleteAppointment(Stage stage, AppointmentModel item);

    /**
     * Deletes a {@link CityModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be deleted.
     */
    void deleteCity(Stage stage, CityModelImpl item);

    /**
     * Deletes a {@link CountryModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be deleted.
     */
    void deleteCountry(Stage stage, CountryModel item);

    /**
     * Deletes a {@link CustomerModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be deleted.
     */
    void deleteCustomer(Stage stage, CustomerModelImpl item);

    /**
     * Deletes a {@link UserModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be deleted.
     */
    void deleteUser(Stage stage, UserModelImpl item);

    /**
     * Opens an {@link EditItem} window to edit an {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be edited.
     */
    void editAddress(Stage stage, AddressModelImpl item);

    /**
     * Opens an {@link EditItem} window to edit an {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be edited.
     */
    void editAppointment(Stage stage, AppointmentModel item);

    /**
     * Opens an {@link EditItem} window to edit a {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be edited.
     */
    void editCustomer(Stage stage, CustomerModelImpl item);

    /**
     * Opens an {@link EditItem} window to edit a {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be edited.
     */
    void editUser(Stage stage, UserModelImpl item);

    <T extends DataAccessObject> void fireDaoEvent(Object source, DaoChangeAction action, T dao);

    /**
     * Opens an {@link EditItem} window to edit a {@link CityModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be edited.
     */
    void openCity(Stage stage, CityModelImpl item);

    /**
     * Opens an {@link EditItem} window to edit a {@link CountryModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be edited.
     */
    void openCountry(Stage stage, CountryModel item);

    void removeDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener);

}
