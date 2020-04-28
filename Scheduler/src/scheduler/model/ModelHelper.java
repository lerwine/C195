package scheduler.model;

import java.util.Objects;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.model.predefined.PredefinedItem;
import scheduler.model.ui.FxRecordModel;

/**
 * Helper class for getting information about {@link DataModel} objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ModelHelper {

    /**
     * Tests whether two {@link DataModel} objects represent the same database entity.
     *
     * @param <T> The {@link DataModel} type.
     * @param a The first {@link DataModel} compare.
     * @param b The second {@link DataModel} to compare.
     * @return {@code true} if both {@link DataModel}s represent the same database entity; otherwise, {@code false}.
     */
    public static <T extends DataModel> boolean areSameRecord(T a, T b) {
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
                return ((RelatedRecord) a).getPrimaryKey() == ((RelatedRecord) b).getPrimaryKey();
            }
        } else if (!existsInDatabase(b)) {
            if (a instanceof Appointment) {
                return b instanceof Appointment && Appointment.arePropertiesEqual((Appointment) a, (Appointment) b);
            }

            if (a instanceof Customer) {
                return b instanceof Customer && Customer.arePropertiesEqual((Customer) a, (Customer) b);
            }

            if (a instanceof Address) {
                return b instanceof Address && Address.arePropertiesEqual((Address) a, (Address) b);
            }

            if (a instanceof City) {
                return b instanceof City && City.arePropertiesEqual((City) a, (City) b);
            }

            if (a instanceof Country) {
                return b instanceof Country && Country.arePropertiesEqual((Country) a, (Country) b);
            }

            if (a instanceof User) {
                return b instanceof User && User.arePropertiesEqual((User) a, (User) b);
            }
        }
        return false;
    }

    public static DataRowState getRowState(DataModel obj) {
        if (null == obj) {
            return DataRowState.DELETED;
        }
        if (obj instanceof DataRecord) {
            return ((DataRecord) obj).getRowState();
        }
        if (obj instanceof PredefinedItem) {
            return DataRowState.NEW;
        }
        return (obj instanceof RelatedRecord) ? DataRowState.UNMODIFIED : DataRowState.NEW;
    }

    /**
     * Gets the primary key value for a {@link DataModel} object. If the target object has never been saved to the database or if it has been deleted,
     * then this will return {@link Integer#MIN_VALUE}.
     *
     * @param obj The target {@link DataModel} object.
     * @return The value from {@link RelatedRecord#getPrimaryKey()} or {@link Integer#MIN_VALUE} if the object does not have a valid primary key
     * value.
     */
    public static int getPrimaryKey(DataModel obj) {
        if (existsInDatabase(obj)) {
            return ((RelatedRecord) obj).getPrimaryKey();
        }
        return Integer.MIN_VALUE;
    }

    public static boolean existsInDatabase(DataModel obj) {
        if (null != obj) {
            if (obj instanceof DataRecord) {
                switch (((DataRecord) obj).getRowState()) {
                    case MODIFIED:
                    case UNMODIFIED:
                        return true;
                    default:
                        return false;
                }
            }
            return obj instanceof RelatedRecord;
        }
        return false;
    }

    /**
     * Asserts that the properties of a {@link DataAccessObject} can be applied to a {@link FxRecordModel}.
     * 
     * @param <T> The {@link DataAccessObject} type.
     * @param <U> The {@link FxRecordModel} type.
     * @param source The source {@link DataAccessObject}.
     * @param targetFxModel The target {@link FxRecordModel}.
     * @return The target {@link FxRecordModel}.
     * @throws IllegalArgumentException if the objects do not represent the same entity.
     */
    public static <T extends DataAccessObject, U extends FxRecordModel<T>> U requiredAssignable(T source, U targetFxModel) {
        Objects.requireNonNull(source);
        if (null != targetFxModel && targetFxModel.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW ||
                targetFxModel.getPrimaryKey() != source.getPrimaryKey())) {
            throw new IllegalArgumentException("Objects do not represent the same database entity");
        }
        return targetFxModel;
    }

    /**
     * Asserts that the properties of a {@link FxRecordModel} can be applied to a {@link DataAccessObject}.
     * 
     * @param <T> The {@link FxRecordModel} type.
     * @param <U> The {@link DataAccessObject} type.
     * @param source The source {@link FxRecordModel}.
     * @param targetDataAccessObject The target {@link DataAccessObject}.
     * @return The target {@link DataAccessObject}.
     * @throws IllegalArgumentException if the objects do not represent the same entity.
     */
    public static <T extends FxRecordModel<U>, U extends DataAccessObject> U requiredAssignable(T source, U targetDataAccessObject) {
        Objects.requireNonNull(source);
        if (null != targetDataAccessObject && targetDataAccessObject.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW ||
                targetDataAccessObject.getPrimaryKey() != source.getPrimaryKey())) {
            throw new IllegalArgumentException("Objects do not represent the same database entity");
        }
        return targetDataAccessObject;
    }
}
