package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.dao.factory.CityFactory;
import scheduler.dao.factory.DataObjectFactory;

/**
 * Represents a data row from the "city" database table.
 * Table definition: <code>CREATE TABLE `city` (
 *   `cityId` int(10) NOT NULL AUTO_INCREMENT,
 *   `city` varchar(50) NOT NULL,
 *   `countryId` int(10) NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`cityId`),
 *   KEY `countryId` (`countryId`),
 *   CONSTRAINT `city_ibfk_1` FOREIGN KEY (`countryId`) REFERENCES `country` (`countryId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;</code>
 * @author erwinel
 */
public interface City extends DataObject {
    
    /**
     * Gets the name of the current city.
     * This corresponds to the "city" database column.
     * 
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link Country} for the current city.
     * This corresponds to the "country" data row referenced by the "countryId" database column.
     * 
     * @return The {@link Country} for the current city.
     */
    Country getCountry();

    /**
     * Creates a read-only City object from object values.
     * @param pk The value of the primary key.
     * @param name The name of the city.
     * @param country The country of the city.
     * @return The read-only City object.
     */
    public static City of(int pk, String name, Country country) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new City() {
            @Override
            public String getName() { return name; }
            @Override
            public int getPrimaryKey() { return pk; }
            @Override
            public Country getCountry() { return country; }
            @Override
            public int getRowState() { return DataObjectFactory.ROWSTATE_UNMODIFIED; }
        };
    }
    
    /**
     * Creates a read-only City object from a result set.
     * @param resultSet The data retrieved from the database.
     * @param pkColName The name of the column containing the value of the primary key.
     * @return The read-only City object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static City of(ResultSet resultSet, String pkColName) throws SQLException {
        Objects.requireNonNull(pkColName, "Primary key column name cannot be null");
        int id = resultSet.getInt(pkColName);
        if (resultSet.wasNull())
            return null;
        
        Country country = Country.of(resultSet, CityFactory.COLNAME_COUNTRYID);
        String name = resultSet.getString(CityFactory.COLNAME_CITY);
        return of(id, (resultSet.wasNull()) ? "" : name, country);
    }
}
