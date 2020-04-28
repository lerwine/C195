package scheduler.model;

import scheduler.dao.ValidationResult;

/**
 * A {@link DataModel} containing informational properties of a database entity joined by a foreign key relationship.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface RelatedRecord extends DataModel {

    public static ValidationResult validate(RelatedRecord obj) {
        if (null != obj) {
            if (obj instanceof DataRecord) {
                return ((DataRecord<?>) obj).validate();
            }
            return ValidationResult.OK;
        }

        return ValidationResult.DATA_OBJECT_DELETED;
    }

    /**
     * Gets the value of the primary key for the current data object.
     *
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();
}
