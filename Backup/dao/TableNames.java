package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface TableNames {
    public static final String TABLEALIAS_COUNTRY = "n";
    public static final String TABLEALIAS_CITY = "l";
    public static final String TABLEALIAS_ADDRESS = "a";
    public static final String TABLEALIAS_CUSTOMER = "c";
    public static final String TABLEALIAS_APPOINTMENT = "e";
    public static final String TABLEALIAS_USER  = "u";
    /**
     * The name of the {@link User} database table.
     */
    public static final String TABLENAME_USER = "user";

    /**
     * The name of the {@link Country} database table.
     */
    public static final String TABLENAME_COUNTRY = "country";

    /**
     * The name of the {@link City} database table.
     */
    public static final String TABLENAME_CITY = "city";

    /**
     * The name of the {@link Address} database table.
     */
    public static final String TABLENAME_ADDRESS = "address";

    /**
     * The name of the {@link Customer} database table.
     */
    public static final String TABLENAME_CUSTOMER = "customer";

    /**
     * The name of the {@link Appointment} database table.
     */
    public static final String TABLENAME_APPOINTMENT = "appointment";

}
