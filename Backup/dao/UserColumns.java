package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface UserColumns extends DataObjectColumns {
    /**
     * The name of the database column that is mapped to the {@link DataObjectImpl#primaryKey} property.
     */
    public static final String COLNAME_USERID = "userId";

    /**
     * The name of the database column that is mapped to {@link UserImpl#userName} property.
     */
    public static final String COLNAME_USERNAME = "userName";

    /**
     * The name of the database column that is mapped to {@link UserImpl#password} property.
     */
    public static final String COLNAME_PASSWORD = "password";

    /**
     * The name of the database column that is mapped to {@link UserImpl#status} property.
     */
    public static final String COLNAME_ACTIVE_STATUS = "active";

    /**
     * The name of the database column that is mapped to {@link UserImpl#status} property.
     */
    public static final String COLALIAS_ACTIVE_STATUS = "status";
}
