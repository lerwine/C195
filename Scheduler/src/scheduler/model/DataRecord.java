package scheduler.model;

import java.io.Serializable;
import scheduler.dao.DataRowState;
import scheduler.dao.ValidationResult;

/**
 * Interface for a {@code DataModel} that contains all columns of a data base entity.
 * <p>
 * Extending types:</p>
 * <dl>
 * <dt>{@link scheduler.dao.DataAccessObject}</dt><dd>Abstract data access object implementation.</dd>
 * <dt>{@link scheduler.view.model.ItemModel}</dt><dd>Abstract implementation for Java FX view binding.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of object for date/time values
 */
public interface DataRecord<T extends Serializable & Comparable<? super T>> extends RelatedRecord {

    /**
     * Gets the timestamp when the data row associated with the current data object was inserted into the database.
     * <p>
     * This property corresponds to {@link scheduler.dao.schema.DbColumn#APPOINTMENT_CREATE_DATE}, {@link scheduler.dao.schema.DbColumn#CUSTOMER_CREATE_DATE},
     * {@link scheduler.dao.schema.DbColumn#ADDRESS_CREATE_DATE}, {@link scheduler.dao.schema.DbColumn#CITY_CREATE_DATE}, {@link scheduler.dao.schema.DbColumn#COUNTRY_CREATE_DATE} or
     * {@link scheduler.dao.schema.DbColumn#USER_CREATE_DATE}.</p>
     *
     * @return The timestamp when the data row associated with the current data object was inserted into the database.
     */
    T getCreateDate();

    /**
     * Gets the user name of the person who inserted the data row associated with the current data object into the database.
     * <p>
     * This property corresponds to {@link scheduler.dao.schema.DbColumn#APPOINTMENT_CREATED_BY}, {@link scheduler.dao.schema.DbColumn#CUSTOMER_CREATED_BY},
     * {@link scheduler.dao.schema.DbColumn#ADDRESS_CREATED_BY}, {@link scheduler.dao.schema.DbColumn#CITY_CREATED_BY}, {@link scheduler.dao.schema.DbColumn#COUNTRY_CREATED_BY} or
     * {@link scheduler.dao.schema.DbColumn#USER_CREATED_BY}.</p>
     *
     * @return The user name of the person who inserted the data row associated with the current data object into the database.
     */
    String getCreatedBy();

    /**
     * Gets the timestamp when the data row associated with the current data object was last modified.
     * <p>
     * This property corresponds to {@link scheduler.dao.schema.DbColumn#APPOINTMENT_LAST_UPDATE}, {@link scheduler.dao.schema.DbColumn#CUSTOMER_LAST_UPDATE},
     * {@link scheduler.dao.schema.DbColumn#ADDRESS_LAST_UPDATE}, {@link scheduler.dao.schema.DbColumn#CITY_LAST_UPDATE}, {@link scheduler.dao.schema.DbColumn#COUNTRY_LAST_UPDATE} or
     * {@link scheduler.dao.schema.DbColumn#USER_LAST_UPDATE}.</p>
     *
     * @return The timestamp when the data row associated with the current data object was last modified.
     */
    T getLastModifiedDate();

    /**
     * Gets the user name of the person who last modified the data row associated with the current data object in the database.
     * <p>
     * This property corresponds to {@link scheduler.dao.schema.DbColumn#APPOINTMENT_LAST_UPDATE_BY}, {@link scheduler.dao.schema.DbColumn#CUSTOMER_LAST_UPDATE}_BY,
     * {@link scheduler.dao.schema.DbColumn#ADDRESS_LAST_UPDATE_BY}, {@link scheduler.dao.schema.DbColumn#CITY_LAST_UPDATE_BY}, {@link scheduler.dao.schema.DbColumn#COUNTRY_LAST_UPDATE_BY} or
     * {@link scheduler.dao.schema.DbColumn#USER_LAST_UPDATE_BY}.</p>
     *
     * @return The user name of the person who last modified the data row associated with the current data object in the database.
     */
    String getLastModifiedBy();
    
    /**
     * Gets a value which indicates the disposition of the current data object in relation to the corresponding data row in the database.
     *
     * @return {@link DataRowState} value that indicates the disposition of the current data object in relation to the corresponding data row in the
     * database.
     */
    DataRowState getRowState();

    /**
     * Checks validity of {@link DataRecord} properties. This simply checks property values and does not do any other referential validation.
     *
     * @return A {@link ValidationResult} value indicating validation status.
     * @todo Remove default implementation from {@link DataRecord#validate()} and implement all validations.
     * @todo Ensure {@link #validate()} is being utilized by all factories before database inserts and updates.
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

}