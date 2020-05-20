package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.IntColumnValueFilter;
import scheduler.dao.filter.value.IntValueFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.model.Address;
import scheduler.model.City;
import scheduler.model.ModelHelper;
import scheduler.util.InternalException;
import scheduler.util.PropertyBindable;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Data access object for the {@code address} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.ADDRESS)
public final class AddressDAO extends DataAccessObject implements AddressDbRecord {

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static final int MAX_LENGTH_ADDRESS1 = 50;

    /**
     * The name of the 'address1' property.
     */
    public static final String PROP_ADDRESS1 = "address1";

    public static final int MAX_LENGTH_ADDRESS2 = 50;

    /**
     * The name of the 'address2' property.
     */
    public static final String PROP_ADDRESS2 = "address2";

    /**
     * The name of the 'city' property.
     */
    public static final String PROP_CITY = "city";

    public static final int MAX_LENGTH_POSTALCODE = 10;

    /**
     * The name of the 'postalCode' property.
     */
    public static final String PROP_POSTALCODE = "postalCode";

    public static final int MAX_LENGTH_PHONE = 20;

    /**
     * The name of the 'phone' property.
     */
    public static final String PROP_PHONE = "phone";

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String address1;
    private String address2;
    private ICityDAO city;
    private String postalCode;
    private String phone;

    /**
     * Initializes a {@link DataRowState#NEW} address object.
     */
    public AddressDAO() {
        address1 = "";
        address2 = "";
        city = null;
        postalCode = "";
        phone = "";
    }

    @Override
    public String getAddress1() {
        return address1;
    }

    /**
     * Set the value of address1
     *
     * @param value new value of address1
     */
    public void setAddress1(String value) {
        String oldValue = address1;
        address1 = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_ADDRESS1, oldValue, address1);
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    /**
     * Set the value of address2
     *
     * @param value new value of address2
     */
    public void setAddress2(String value) {
        String oldValue = address2;
        address1 = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_ADDRESS2, oldValue, address2);
    }

    @Override
    public ICityDAO getCity() {
        return city;
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public void setCity(ICityDAO city) {
        ICityDAO oldValue = this.city;
        this.city = city;
        firePropertyChange(PROP_CITY, oldValue, this.city);
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Set the value of postalCode
     *
     * @param value new value of postalCode
     */
    public void setPostalCode(String value) {
        String oldValue = postalCode;
        postalCode = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_POSTALCODE, oldValue, postalCode);
    }

    @Override
    public String getPhone() {
        return phone;
    }

    /**
     * Set the value of phone
     *
     * @param value new value of phone
     */
    public void setPhone(String value) {
        String oldValue = phone;
        phone = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_PHONE, oldValue, phone);
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.address1);
        hash = 79 * hash + Objects.hashCode(this.address2);
        hash = 79 * hash + Objects.hashCode(this.city);
        hash = 79 * hash + Objects.hashCode(this.postalCode);
        hash = 79 * hash + Objects.hashCode(this.phone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
    }

    /**
     * Factory implementation for {@link AddressDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<AddressDAO> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(AddressDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case ADDRESS1:
                    ps.setString(index, dao.address1);
                    break;
                case ADDRESS2:
                    ps.setString(index, dao.address2);
                    break;
                case ADDRESS_CITY:
                    ps.setInt(index, dao.city.getPrimaryKey());
                    break;
                case POSTAL_CODE:
                    ps.setString(index, dao.postalCode);
                    break;
                case PHONE:
                    ps.setString(index, dao.phone);
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public AddressDAO createNew() {
            return new AddressDAO();
        }

        @Override
        public DaoFilter<AddressDAO> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGADDRESSES));
        }

        public DaoFilter<AddressDAO> getByCityFilter(int pk) {
            return DaoFilter.of(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES),
                    IntColumnValueFilter.of(DbColumn.ADDRESS_CITY, IntValueFilter.of(pk, ComparisonOperator.EQUALS),
                            (AddressDAO t) -> ModelHelper.getPrimaryKey(t.getCity())));
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            DmlSelectQueryBuilder builder = new DmlSelectQueryBuilder(DbTable.ADDRESS, SchemaHelper.getTableColumns(DbTable.ADDRESS));
            builder.join(DbColumn.ADDRESS_CITY, TableJoinType.LEFT, DbColumn.CITY_ID,
                    SchemaHelper.getTableColumns(DbTable.CITY, SchemaHelper::isForJoinedData))
                    .join(DbColumn.CITY_COUNTRY, TableJoinType.LEFT, DbColumn.COUNTRY_ID,
                            SchemaHelper.getTableColumns(DbTable.COUNTRY, SchemaHelper::isForJoinedData));
            return builder;
        }

        @Override
        public void save(AddressDAO dao, Connection connection, boolean force) throws SQLException {
            super.save(dao, connection, force); // CURRENT: Save city if it is has been modified
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(AddressDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldAddress1 = dao.address1;
                private final String oldAddress2 = dao.address2;
                private final City oldCity = dao.city;
                private final String oldPostalCode = dao.postalCode;
                private final String oldPhone = dao.phone;

                @Override
                public void accept(PropertyChangeSupport t) {
                    if (!dao.address1.equals(oldAddress2)) {
                        t.firePropertyChange(PROP_ADDRESS1, oldAddress1, dao.address1);
                    }
                    if (!dao.address2.equals(oldAddress2)) {
                        t.firePropertyChange(PROP_ADDRESS2, oldAddress2, dao.address2);
                    }
                    if (!Objects.equals(dao.city, oldCity)) {
                        t.firePropertyChange(PROP_CITY, oldCity, dao.city);
                    }
                    if (!dao.postalCode.equals(oldPostalCode)) {
                        t.firePropertyChange(PROP_POSTALCODE, oldPostalCode, dao.postalCode);
                    }
                    if (!dao.phone.equals(oldPhone)) {
                        t.firePropertyChange(PROP_POSTALCODE, oldPhone, dao.phone);
                    }
                }
            };

            dao.address1 = asNonNullAndTrimmed(rs.getString(DbColumn.ADDRESS1.toString()));
            dao.address2 = asNonNullAndTrimmed(rs.getString(DbColumn.ADDRESS2.toString()));
            dao.city = CityDAO.getFactory().fromJoinedResultSet(rs);
            dao.postalCode = asNonNullAndTrimmed(rs.getString(DbColumn.POSTAL_CODE.toString()));
            dao.phone = asNonNullAndTrimmed(rs.getString(DbColumn.PHONE.toString()));
            return propertyChanges;
        }

        IAddressDAO fromJoinedResultSet(ResultSet resultSet) throws SQLException {
            return new Related(resultSet.getInt(DbColumn.CUSTOMER_ADDRESS.toString()),
                    asNonNullAndTrimmed(resultSet.getString(DbColumn.ADDRESS1.toString())),
                    asNonNullAndTrimmed(resultSet.getString(DbColumn.ADDRESS2.toString())),
                    CityDAO.getFactory().fromJoinedResultSet(resultSet),
                    asNonNullAndTrimmed(resultSet.getString(DbColumn.POSTAL_CODE.toString())),
                    asNonNullAndTrimmed(resultSet.getString(DbColumn.PHONE.toString())));
        }

        @Override
        public Class<? extends AddressDAO> getDaoClass() {
            return AddressDAO.class;
        }

        @Override
        public String getDeleteDependencyMessage(AddressDAO dao, Connection connection) throws SQLException {
            if (null == dao || !DataRowState.existsInDb(dao.getRowState())) {
                return "";
            }

            int count = CustomerDAO.getFactory().countByAddress(connection, dao.getPrimaryKey());
            // PENDING: Internationalize these
            switch (count) {
                case 0:
                    // CURRENT: Get city conflict message if it is has been modified.
                    return "";
                case 1:
                    return "Address is referenced by one customer.";
                default:
                    return String.format("Address is referenced by %d other customers", count);
            }
        }

        @Override
        public String getSaveDbConflictMessage(AddressDAO dao, Connection connection) throws SQLException {
            assert dao.getRowState() != DataRowState.DELETED : "Data access object already deleted";

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.ADDRESS_ID.getDbName())
                    .append(") FROM ").append(DbTable.ADDRESS.getDbName())
                    .append(" WHERE ").append(DbColumn.ADDRESS_CITY.getDbName()).append("=?");
            if (dao.address1.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.ADDRESS1.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.ADDRESS1.getDbName()).append(")=?");
            }
            if (dao.address2.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.ADDRESS2.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.ADDRESS2.getDbName()).append(")=?");
            }
            if (dao.postalCode.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.POSTAL_CODE.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.POSTAL_CODE.getDbName()).append(")=?");
            }
            if (dao.phone.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.PHONE.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.PHONE.getDbName()).append(")=?");
            }
            if (dao.getRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.ADDRESS_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, ModelHelper.getPrimaryKey(dao.getCity()));
                int index = 2;
                if (!dao.address1.isEmpty()) {
                    ps.setString(index++, dao.address1.toLowerCase());
                }
                if (!dao.address2.isEmpty()) {
                    ps.setString(index++, dao.address2.toLowerCase());
                }
                if (!dao.postalCode.isEmpty()) {
                    ps.setString(index++, dao.postalCode.toLowerCase());
                }
                if (!dao.phone.isEmpty()) {
                    ps.setString(index++, dao.phone.toLowerCase());
                }
                if (dao.getRowState() != DataRowState.NEW) {
                    ps.setInt(index, dao.getPrimaryKey());
                }
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                }
            }
            // PENDING: Internationalize this
            if (count > 0) {
                return "Another matching address exists";
            }
            return "";
        }

        int countByCity(int primaryKey, Connection connection) throws SQLException {
            String sql = "SELECT COUNT(" + DbColumn.ADDRESS_ID.getDbName() + ") FROM " + DbTable.CITY.getDbName()
                    + " WHERE " + DbColumn.ADDRESS_CITY.getDbName() + "=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, primaryKey);
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        public Address ensureSaved(Address requireNonNull, Connection connection) {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.dao.AddressDAO.FactoryImpl#ensureSaved
        }

    }

    public static class Related extends PropertyBindable implements IAddressDAO {

        private final int primaryKey;
        private final String address1;
        private final String address2;
        private final ICityDAO city;
        private final String postalCode;
        private final String phone;

        Related(int primaryKey, String address1, String address2, ICityDAO city, String postalCode, String phone) {
            this.primaryKey = primaryKey;
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.postalCode = postalCode;
            this.phone = phone;
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public ICityDAO getCity() {
            return city;
        }

        @Override
        public String getAddress1() {
            return address1;
        }

        @Override
        public String getAddress2() {
            return address2;
        }

        @Override
        public String getPostalCode() {
            return postalCode;
        }

        @Override
        public String getPhone() {
            return phone;
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
        }

    }
}
