package scheduler.model;

import java.io.Serializable;

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
public interface DataRecord<T extends Serializable & Comparable<? super T>> extends DbDataModel {

    /**
     * Gets the timestamp when the data row associated with the current data object was inserted into the database.
     * <p>
     * This property corresponds to {@link DbColumn#APPOINTMENT_CREATE_DATE}, {@link DbColumn#CUSTOMER_CREATE_DATE},
     * {@link DbColumn#ADDRESS_CREATE_DATE}, {@link DbColumn#CITY_CREATE_DATE}, {@link DbColumn#COUNTRY_CREATE_DATE} or
     * {@link DbColumn#USER_CREATE_DATE}.</p>
     *
     * @return The timestamp when the data row associated with the current data object was inserted into the database.
     */
    T getCreateDate();

    /**
     * Gets the user name of the person who inserted the data row associated with the current data object into the database.
     * <p>
     * This property corresponds to {@link DbColumn#APPOINTMENT_CREATED_BY}, {@link DbColumn#CUSTOMER_CREATED_BY},
     * {@link DbColumn#ADDRESS_CREATED_BY}, {@link DbColumn#CITY_CREATED_BY}, {@link DbColumn#COUNTRY_CREATED_BY} or
     * {@link DbColumn#USER_CREATED_BY}.</p>
     *
     * @return The user name of the person who inserted the data row associated with the current data object into the database.
     */
    String getCreatedBy();

    /**
     * Gets the timestamp when the data row associated with the current data object was last modified.
     * <p>
     * This property corresponds to {@link DbColumn#APPOINTMENT_LAST_UPDATE}, {@link DbColumn#CUSTOMER_LAST_UPDATE},
     * {@link DbColumn#ADDRESS_LAST_UPDATE}, {@link DbColumn#CITY_LAST_UPDATE}, {@link DbColumn#COUNTRY_LAST_UPDATE} or
     * {@link DbColumn#USER_LAST_UPDATE}.</p>
     *
     * @return The timestamp when the data row associated with the current data object was last modified.
     */
    T getLastModifiedDate();

    /**
     * Gets the user name of the person who last modified the data row associated with the current data object in the database.
     * <p>
     * This property corresponds to {@link DbColumn#APPOINTMENT_LAST_UPDATE_BY}, {@link DbColumn#CUSTOMER_LAST_UPDATE}_BY,
     * {@link DbColumn#ADDRESS_LAST_UPDATE_BY}, {@link DbColumn#CITY_LAST_UPDATE_BY}, {@link DbColumn#COUNTRY_LAST_UPDATE_BY} or
     * {@link DbColumn#USER_LAST_UPDATE_BY}.</p>
     *
     * @return The user name of the person who last modified the data row associated with the current data object in the database.
     */
    String getLastModifiedBy();
}
