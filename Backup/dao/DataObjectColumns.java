package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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
        return String.format("%s.%s as %s, %s.%s as %s, %s.%s as %s, %s.%s as %s", tableAlias, COLNAME_CREATEDATE, COLNAME_CREATEDATE,
                tableAlias, COLNAME_CREATEDBY, COLNAME_CREATEDBY, tableAlias, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE,
                tableAlias, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY);
    }
}
