package scheduler.dao;

/**
 *
 * @author lerwi
 */
@Deprecated
public interface UserColumns extends DataObjectColumns {
    /**
     * The name of the database column that is mapped to the {@link DataObjectImpl#primaryKey} property.
     */
    @Deprecated
    public static final String COLNAME_USERID = "userId";

    /**
     * The name of the database column that is mapped to {@link UserImpl#userName} property.
     */
    @Deprecated
    public static final String COLNAME_USERNAME = "userName";

    /**
     * The name of the database column that is mapped to {@link UserImpl#password} property.
     */
    @Deprecated
    public static final String COLNAME_PASSWORD = "password";

    /**
     * The name of the database column that is mapped to {@link UserImpl#status} property.
     */
    @Deprecated
    public static final String COLNAME_ACTIVE_STATUS = "active";

    /**
     * The name of the database column that is mapped to {@link UserImpl#status} property.
     */
    @Deprecated
    public static final String COLALIAS_ACTIVE_STATUS = "status";
}
