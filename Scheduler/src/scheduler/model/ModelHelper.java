package scheduler.model;

import java.util.Objects;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.model.ui.EntityModelImpl;

/**
 * Helper class for getting information about {@link PartialDataEntity} objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ModelHelper {

    /**
     * Tests whether two {@link PartialDataEntity} objects represent the same database entity.
     *
     * @param <T> The {@link PartialDataEntity} type.
     * @param a The first {@link PartialDataEntity} compare.
     * @param b The second {@link PartialDataEntity} to compare.
     * @return {@code true} if both {@link PartialDataEntity}s represent the same database entity; otherwise, {@code false}.
     */
    public static <T extends PartialDataEntity> boolean areSameRecord(T a, T b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        if (a == b) {
            return true;
        }
        if (existsInDatabase(a)) {
            if (existsInDatabase(b)) {
                return a.getPrimaryKey() == b.getPrimaryKey();
            }
        } else if (!existsInDatabase(b)) {
            if (a instanceof Appointment) {
                return b instanceof Appointment && Appointment.arePropertiesEqual((Appointment<?>) a, (Appointment<?>) b);
            }

            if (a instanceof Customer) {
                return b instanceof Customer && Customer.arePropertiesEqual((Customer) a, (Customer) b);
            }

            if (a instanceof AddressProperties) {
                return b instanceof AddressProperties && AddressProperties.arePropertiesEqual((AddressProperties) a, (AddressProperties) b);
            }

            if (a instanceof CityProperties) {
                return b instanceof CityProperties && CityProperties.arePropertiesEqual((CityProperties) a, (CityProperties) b);
            }

            if (a instanceof CountryProperties) {
                return b instanceof CountryProperties && CountryProperties.arePropertiesEqual((CountryProperties) a, (CountryProperties) b);
            }

            if (a instanceof User) {
                return b instanceof User && User.arePropertiesEqual((User) a, (User) b);
            }
        }
        return false;
    }

    /**
     * Gets the primary key value for a {@link PartialDataEntity} object. If the target object has never been saved to the database or if it has been
     * deleted, then this will return {@link Integer#MIN_VALUE}.
     *
     * @param obj The target {@link PartialDataEntity} object.
     * @return The value from {@link PartialDataEntity#getPrimaryKey()} or {@link Integer#MIN_VALUE} if the object does not have a valid primary key value.
     */
    public static int getPrimaryKey(PartialDataEntity obj) {
        if (existsInDatabase(obj)) {
            return obj.getPrimaryKey();
        }
        return Integer.MIN_VALUE;
    }

    public static boolean existsInDatabase(PartialDataEntity obj) {
        if (null != obj) {
            if (obj instanceof DataEntity) {
                switch (((DataEntity<?>) obj).getRowState()) {
                    case MODIFIED:
                    case UNMODIFIED:
                        return true;
                    default:
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Asserts that the properties of a {@link DataAccessObject} can be applied to a {@link EntityModelImpl}.
     *
     * @param <T> The {@link DataAccessObject} type.
     * @param <U> The {@link EntityModelImpl} type.
     * @param source The source {@link DataAccessObject}.
     * @param targetFxModel The target {@link EntityModelImpl}.
     * @return The target {@link EntityModelImpl}.
     * @throws IllegalArgumentException if the objects do not represent the same entity.
     */
    public static <T extends DataAccessObject, U extends EntityModelImpl<T>> U requiresAssignable(T source, U targetFxModel) {
        Objects.requireNonNull(source);
        if (null != targetFxModel && targetFxModel.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW
                || targetFxModel.getPrimaryKey() != source.getPrimaryKey())) {
            throw new IllegalArgumentException("Objects do not represent the same database entity");
        }
        return targetFxModel;
    }

    /**
     * Asserts that the properties of a {@link EntityModelImpl} can be applied to a {@link DataAccessObject}.
     *
     * @param <T> The {@link EntityModelImpl} type.
     * @param <U> The {@link DataAccessObject} type.
     * @param source The source {@link EntityModelImpl}.
     * @param targetDataAccessObject The target {@link DataAccessObject}.
     * @return The target {@link DataAccessObject}.
     * @throws IllegalArgumentException if the objects do not represent the same entity.
     */
    public static <T extends EntityModelImpl<U>, U extends DataAccessObject> U requiresAssignable(T source, U targetDataAccessObject) {
        Objects.requireNonNull(source);
        if (null != targetDataAccessObject && targetDataAccessObject.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW
                || targetDataAccessObject.getPrimaryKey() != source.getPrimaryKey())) {
            throw new IllegalArgumentException("Objects do not represent the same database entity");
        }
        return targetDataAccessObject;
    }
}
