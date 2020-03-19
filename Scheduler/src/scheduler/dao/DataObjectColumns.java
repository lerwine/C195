package scheduler.dao;

/**
 *
 * @author lerwi
 */
interface DataObjectColumns {
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
    
    public static String getDataObjectSelectFields(String tableAlias) {
        return String.format("%1$s.%2$s as %2$s, %1$s.%3$s as %3$s, %1$s.%4$s as %4$s, %1$s.%5$s as %5$s", tableAlias, COLNAME_CREATEDATE,
                COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY);
    }
}
