package scheduler.model.db;

import scheduler.dao.DataRowState;
import scheduler.dao.ValidationResult;
import scheduler.model.DbDataModel;

/**
 * {@code DataModel} interface for data access objects.
 * <p>
 * Extending types:</p>
 * Data Access Object:
 * <ul>
 * <li>{@link scheduler.dao.DataAccessObject}</li>
 * </ul>
 * Entity-specific extensions:
 * <ul>
 * <li>{@link AppointmentRowData}</li>
 * <li>{@link CustomerRowData}</li>
 * <li>{@link AddressRowData}</li>
 * <li>{@link CityRowData}</li>
 * <li>{@link CountryRowData}</li>
 * </ul>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface RowData extends DbDataModel {

    /**
     * Gets the value of the primary key for the current data object.
     *
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();

    /**
     * Gets a value which indicates the disposition of the current data object in relation to the corresponding data row in the database.
     *
     * @return {@link DataRowState} value that indicates the disposition of the current data object in relation to the corresponding data row in the
     * database.
     */
    DataRowState getRowState();

    /**
     * Checks validity of {@link RowData} properties. This simply checks property values and does not do any other referential validation.
     *
     * @return A {@link ValidationResult} value indicating validation status.
     */
    default ValidationResult validate() {
        return ValidationResult.OK;
    }

    /**
     * Gets a value which indicates whether the current data object exists in the database.
     *
     * @return {@code true} if the row state is {@link DataRowState#UNMODIFIED} or {@link DataRowState#MODIFIED}, otherwise, {@code false} if the row
     * state is {@link DataRowState#NEW} or {@link DataRowState#DELETED}.
     */
    default boolean isExisting() {
        return DataRowState.existsInDb(getRowState());
    }

    public static int getPrimaryKeyOf(RowData obj) {
        if (null != obj && obj.isExisting()) {
            return obj.getPrimaryKey();
        }
        return Integer.MIN_VALUE;
    }

    public static boolean isExisting(RowData obj) {
        return null != obj && obj.isExisting();
    }

}
