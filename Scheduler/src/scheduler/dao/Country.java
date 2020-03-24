package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;

/**
 * Represents a data row from the country data table. Table definition: <code>CREATE TABLE `country` (
 *   `countryId` int(10) NOT NULL AUTO_INCREMENT,
 *   `country` varchar(50) NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`countryId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;</code>
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface Country extends DataObject {

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

    public static String toString(Country country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

    /**
     * Creates a read-only Country object from object values.
     *
     * @param pk The value of the primary key.
     * @param name The name of the country.
     * @return The read-only Country object.
     */
    public static Country of(int pk, String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new Country() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getPrimaryKey() {
                return pk;
            }

            @Override
            public DataRowState getRowState() {
                return DataRowState.UNMODIFIED;
            }

        };
    }

    /**
     * Creates a read-only Country object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @param columns The {@link TableColumnList} that created the current lookup query.
     * @return The read-only Country object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static Country of(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
        Optional<Integer> id = columns.tryGetInt(resultSet, DbName.ADDRESS_ID);
        if (id.isPresent()) {
            return of(id.get(), columns.getString(resultSet, DbColumn.COUNTRY_NAME, ""));
        }
        return null;
    }
}
