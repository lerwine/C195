package scheduler.model.ui;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.IDataAccessObject;
import scheduler.model.DataEntity;

/**
 * Base interface for {@link DataEntity} objects that contain all database entity properties, is intended for {@code JavaFX} property binding, and
 * contains a backing {@link IDataAccessObject}.
 * <table>
 * <caption>Backing {@link IDataAccessObject} types for extending types</caption>
 * <tr>
 * <th>Extending Type</th>
 * <th>Backing {@link IDataAccessObject} type</th>
 * </tr>
 * <tr>
 * <td>{@link AppointmentModel}</td>
 * <td>{@link scheduler.dao.IAppointmentDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link CustomerModel}</td>
 * <td>{@link scheduler.dao.ICustomerDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link UserModel}</td>
 * <td>{@link scheduler.dao.IUserDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link AddressModel}</td>
 * <td>{@link scheduler.dao.IAddressDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link CityModel}</td>
 * <td>{@link scheduler.dao.ICityDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link CountryModel}</td>
 * <td>{@link scheduler.dao.ICountryDAO}</td>
 * </tr>
 * </table>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface EntityModel<T extends IDataAccessObject> extends PartialEntityModel<T>, DataEntity<LocalDateTime> {

    ReadOnlyObjectProperty<LocalDateTime> createDateProperty();

    ReadOnlyStringProperty createdByProperty();

    ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty();

    ReadOnlyStringProperty lastModifiedByProperty();

}
