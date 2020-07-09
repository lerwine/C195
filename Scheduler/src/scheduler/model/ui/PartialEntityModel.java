package scheduler.model.ui;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import scheduler.dao.DataRowState;
import scheduler.model.PartialDataEntity;
import scheduler.dao.PartialDataAccessObject;

/**
 * Base interface for data entity objects intended for {@code JavaFX} property binding, and contains a backing {@link PartialDataAccessObject}.
 * <table>
 * <caption>Backing {@link PartialDataAccessObject} types for extending types</caption>
 * <tr>
 * <th>Extending Type</th>
 * <th>Backing {@link PartialDataAccessObject} type</th>
 * </tr>
 * <tr>
 * <td>{@link PartialAppointmentModel}</td>
 * <td>{@link scheduler.dao.PartialAppointmentDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link PartialCustomerModel}</td>
 * <td>{@link scheduler.dao.PartialCustomerDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link PartialUserModel}</td>
 * <td>{@link scheduler.dao.PartialUserDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link PartialAddressModel}</td>
 * <td>{@link scheduler.dao.PartialAddressDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link PartialCityModel}</td>
 * <td>{@link scheduler.dao.PartialCityDAO}</td>
 * </tr>
 * <tr>
 * <td>{@link PartialCountryModel}</td>
 * <td>{@link scheduler.dao.PartialCountryDAO}</td>
 * </tr>
 * </table>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface PartialEntityModel<T extends PartialDataAccessObject> extends PartialDataEntity {

    /**
     * The name of the 'valid' property.
     */
    public static final String PROP_VALID = "valid";

    /**
     * Gets the backing data access object.
     *
     * @return The backing data access object.
     */
    T dataObject();

    ReadOnlyIntegerProperty primaryKeyProperty();

    @Override
    DataRowState getRowState();

    /**
     * Gets the property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     *
     * @return The property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     */
    ReadOnlyObjectProperty<? extends DataRowState> rowStateProperty();

}
