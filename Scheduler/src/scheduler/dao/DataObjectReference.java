package scheduler.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;
import scheduler.util.DbConnector;

/**
 * Represents a reference to another data object.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The full data access object type.
 * @param <U> The partial data access object type.
 */
public final class DataObjectReference<T extends U, U extends DataObject> implements IDataObjectReference<T, U> {

    private Supplier<Integer> primaryKeySuppler;

    @Override
    public int getPrimaryKey() {
        return (null == primaryKeySuppler) ? Integer.MIN_VALUE : primaryKeySuppler.get();
    }

    @Override
    public boolean isEmpty() {
        return null == primaryKeySuppler;
    }

    private T full;

    @Override
    public T getFull() {
        return full;
    }

    private U partial;

    @Override
    public U getPartial() {
        return partial;
    }

    /**
     * Creates a data object reference that does not reference any data object.
     */
    public DataObjectReference() {
        this(null);
    }

    /**
     * Creates a data object reference that only contains the primary key value.
     *
     * @param primaryKey The primary key of the reference data object.
     */
    public DataObjectReference(int primaryKey) {
        primaryKeySuppler = () -> primaryKey;
        full = null;
        partial = null;
    }

    /**
     * Creates a full data object reference.
     *
     * @param dao The full data object instance.
     */
    public DataObjectReference(T dao) {
        this(dao, dao);
    }

    private DataObjectReference(T full, U partial) {
        if (null != (this.full = full)) {
            this.partial = full;
            primaryKeySuppler = () -> this.full.getPrimaryKey();
        } else if (null != (this.partial = partial)) {
            primaryKeySuppler = () -> this.partial.getPrimaryKey();
        } else {
            primaryKeySuppler = null;
        }
    }

    /**
     * Creates a partial data object reference.
     *
     * @param <T> The full data object type.
     * @param <U> The type of partial data object.
     * @param dao The partial data object.
     * @return A reference to a partial data object.
     */
    public static <T extends U, U extends DataObject> DataObjectReference<T, U> of(U dao) {
        return new DataObjectReference<>(null, dao);
    }

    /**
     * Loads a full data object reference from the database.
     *
     * @param <T> The full data object type.
     * @param primaryKey The primary key for the data object to load.
     * @param factory The data object factory.
     * @param connection The SQL database connection to use.
     * @return A full data object reference.
     * @throws SQLException if unable to retrieve data from the database.
     */
    public static <T extends DataObjectImpl> DataObjectReference<T, ? super T> of(int primaryKey,
            DataObjectImpl.Factory<T, ?> factory, Connection connection) throws SQLException {
        Optional<T> result = factory.loadByPrimaryKey(connection, primaryKey);
        if (result.isPresent()) {
            return new DataObjectReference<>(result.get());
        }
        return new DataObjectReference<>(null);
    }

    private void loadFull(DataObjectImpl.Factory<? super T, ?> factory, Connection connection) throws SQLException {
        Optional<? super T> result = factory.loadByPrimaryKey(connection, primaryKeySuppler.get());
        if (result.isPresent()) {
            partial = full = (T) result.get();
            primaryKeySuppler = () -> this.full.getPrimaryKey();
        } else {
            primaryKeySuppler = null;
        }
    }

    /**
     * Ensures that at least a {@link #partial} data object reference is loaded. If {@link #isEmpty()} is {@code true}, then this will have no effect.
     *
     * @param factory The data object factory.
     * @param connection The SQL database connection to use.
     * @return The loaded {@link #partial} data object or {@code null} if it does not exist. If the record does not exist, then {@link #isEmpty()} will return {@code true} after
     * this.
     * @throws SQLException if unable to retrieve data from the database.
     */
    public U ensurePartial(DataObjectImpl.Factory<? super T, ?> factory,
            Connection connection) throws SQLException {
        if (null == partial && null != primaryKeySuppler) {
            loadFull(factory, connection);
        }
        return this.partial;
    }

    /**
     * Ensures that at least a {@link #partial} data object reference is loaded. If {@link #isEmpty()} is {@code true}, then this will have no effect.
     *
     * @param factory The data object factory.
     * @return The loaded {@link #partial} data object or {@code null} if it does not exist. If the record does not exist, then {@link #isEmpty()} will return {@code true} after
     * this.
     * @throws SQLException if unable to retrieve data from the database.
     * @throws ClassNotFoundException if unable to load the database driver.
     */
    public U ensurePartial(
            DataObjectImpl.Factory<? super T, ?> factory) throws SQLException, ClassNotFoundException {
        if (null == partial && null != primaryKeySuppler) {
            DbConnector.accept((connection) -> loadFull(factory, connection));
        }
        return this.partial;
    }

    /**
     * Ensures that the {@link #full} data object reference is loaded. If {@link #isEmpty()} is {@code true}, then this will have no effect.
     *
     * @param factory The data object factory.
     * @param connection The SQL database connection to use.
     * @return The loaded {@link #full} data object or {@code null} if it does not exist. If the record does not exist, then {@link #isEmpty()} will return {@code true} after this.
     * @throws SQLException if unable to retrieve data from the database.
     */
    public T ensureFull(DataObjectImpl.Factory<? super T, ?> factory,
            Connection connection) throws SQLException {
        if (null == full && null != primaryKeySuppler) {
            loadFull(factory, connection);
        }
        return this.full;
    }

    /**
     * Ensures that the {@link #full} data object reference is loaded. If {@link #isEmpty()} is {@code true}, then this will have no effect.
     *
     * @param factory The data object factory.
     * @return The loaded {@link #full} data object or {@code null} if it does not exist. If the record does not exist, then {@link #isEmpty()} will return {@code true} after this.
     * @throws SQLException if unable to retrieve data from the database.
     * @throws ClassNotFoundException if unable to load the database driver.
     */
    public T ensureFull(
            DataObjectImpl.Factory<? super T, ?> factory) throws SQLException, ClassNotFoundException {
        if (null == full && null != primaryKeySuppler) {
            DbConnector.accept((connection) -> loadFull(factory, connection));
        }
        return this.full;
    }

}
