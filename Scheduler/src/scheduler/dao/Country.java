package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Represents a data row from the country data table.
 * Table definition: <code>CREATE TABLE `country` (
 *   `countryId` int(10) NOT NULL AUTO_INCREMENT,
 *   `country` varchar(50) NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`countryId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;</code>
 * @author erwinel
 */
public interface Country extends DataObject {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    public static final String COLNAME_COUNTRY = "country";
    
    //</editor-fold>

    /**
     * The name of the "name" property.
     */
    public static final String PROP_NAME = "name";
    
    /**
     * Gets the name of the current country.
     * 
     * @return The name of the current country.
     */
    String getName();

    /**
     * Creates a read-only Country object from object values.
     * @param pk The value of the primary key.
     * @param name The name of the country.
     * @return The read-only Country object.
     */
    public static Country of(int pk, String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new Country() {
            @Override
            public String getName() { return name; }
            @Override
            public int getPrimaryKey() { return pk; }
            @Override
            public int getRowState() { return ROWSTATE_UNMODIFIED; }
        };
    }
    
    /**
     * Creates a read-only Country object from a result set.
     * @param resultSet The data retrieved from the database.
     * @param pkColName The name of the column containing the value of the primary key.
     * @return The read-only Country object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static Country of(ResultSet resultSet, String pkColName) throws SQLException {
        Objects.requireNonNull(pkColName, "Primary key column name cannot be null");
        int id = resultSet.getInt(pkColName);
        if (resultSet.wasNull())
            return null;
        String name = resultSet.getString(COLNAME_COUNTRY);
        return of(id, (resultSet.wasNull()) ? "" : name);
    }
}
