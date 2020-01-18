package scheduler.dao;

/**
 * Base interface for all data objects representing a data row in the database.
 * 
 * @author erwinel
 */
public interface DataObject {

    //<editor-fold defaultstate="collapsed" desc="Database table names">

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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    /**
     * The name of the 'createDate' column.
     */
    public static final String COLNAME_CREATEDATE = "createDate";
    
    /**
     * The name of the 'createdBy' column.
     */
    public static final String COLNAME_CREATEDBY = "createdBy";
    
    /**
     * The name of the 'lastUpdate' column.
     */
    public static final String COLNAME_LASTUPDATE = "lastUpdate";
    
    /**
     * The name of the 'lastUpdateBy' column.
     */
    public static final String COLNAME_LASTUPDATEBY = "lastUpdateBy";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Row state values">
    
    /**
     * Value of {@link #getRowState()} when the current data object has been deleted from the database.
     */
    public static final int ROWSTATE_DELETED = -1;
    
    /**
     * Value of {@link #getRowState()} when the current data object has not yet been added to the database.
     */
    public static final int ROWSTATE_NEW = 0;
    
    /**
     * Value of {@link #getRowState()} when the properties of the current data object has not been modified since it was last synchronized with the database.
     */
    public static final int ROWSTATE_UNMODIFIED = 1;
    
    /**
     * Value of {@link #getRowState()} when the properties of the current data object differ from the data stored in the database.
     */
    public static final int ROWSTATE_MODIFIED = 2;
    
    //</editor-fold>
    
    /**
     * Gets the value of the primary key for the current data object.
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();
    
    /**
     * Gets a value which indicates the disposition of the current data object in relation to the corresponding data row in the database.
     * @return {@link #ROWSTATE_UNMODIFIED}, {@link #ROWSTATE_MODIFIED}, {@link #ROWSTATE_NEW} or {@link #ROWSTATE_DELETED}.
     */
    int getRowState();
    
    /**
     * Gets a value which indicates whether the current data object exists in the database.
     * @return {@code true} if the row state is {@link #ROWSTATE_UNMODIFIED}, {@link #ROWSTATE_MODIFIED}, otherwise, {@code false} if
     * the row state is {@link #ROWSTATE_NEW} or {@link #ROWSTATE_DELETED}.
     */
    default  boolean isExisting() { return getRowState() == ROWSTATE_MODIFIED || getRowState() == ROWSTATE_UNMODIFIED; }
}
