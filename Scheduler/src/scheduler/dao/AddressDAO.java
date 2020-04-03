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
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.util.InternalException;
import static scheduler.util.Values.asNonNullAndTrimmed;

@DatabaseTable(DbTable.ADDRESS)
public class AddressDAO extends DataAccessObject implements AddressElement {

    private static final FactoryImpl FACTORY = new FactoryImpl();
    /**
     * The name of the 'address1' property.
     */
    public static final String PROP_ADDRESS1 = "address1";
    /**
     * The name of the 'address2' property.
     */
    public static final String PROP_ADDRESS2 = "address2";
    /**
     * The name of the 'city' property.
     */
    public static final String PROP_CITY = "city";
    /**
     * The name of the 'postalCode' property.
     */
    public static final String PROP_POSTALCODE = "postalCode";
    /**
     * The name of the 'phone' property.
     */
    public static final String PROP_PHONE = "phone";

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String address1;
    private String address2;
    private CityElement city;
    private String postalCode;
    private String phone;

    @Override
    protected void reValidate(Consumer<ValidationResult> addValidation) {
        if (address1.trim().isEmpty() && address2.trim().isEmpty()) {
            addValidation.accept(ValidationResult.ADDRESS_EMPTY);
        }
        if (null == city) {
            addValidation.accept(ValidationResult.NO_CITY);
        } else if (city.validate() != ValidationResult.OK) {
            addValidation.accept(ValidationResult.INVALID_CITY);
        } else if (city.getRowState() == DataRowState.NEW) {
            addValidation.accept(ValidationResult.CITY_NOT_SAVED);
        }
    }

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
    public CityElement getCity() {
        return city;
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public void setCity(CityElement city) {
        CityElement oldValue = this.city;
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
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof AddressElement) {
            AddressElement other = (AddressElement) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && address1.equals(other.getAddress1()) && address2.equals(other.getAddress2())
                        && city.equals(other.getCity()) && postalCode.equals(other.getPostalCode()) && phone.equals(other.getPhone());
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

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
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(AddressDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldAddress1 = dao.address1;
                private final String oldAddress2 = dao.address2;
                private final CityElement oldCity = dao.city;
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

        AddressElement fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new AddressElement() {
                private final String address1 = asNonNullAndTrimmed(rs.getString(DbColumn.ADDRESS1.toString()));
                private final String address2 = asNonNullAndTrimmed(rs.getString(DbColumn.ADDRESS2.toString()));
                private final CityElement city = CityDAO.getFactory().fromJoinedResultSet(rs);
                private final String postalCode = asNonNullAndTrimmed(rs.getString(DbColumn.POSTAL_CODE.toString()));
                private final String phone = asNonNullAndTrimmed(rs.getString(DbColumn.PHONE.toString()));
                private final int primaryKey = rs.getInt(DbColumn.CUSTOMER_ADDRESS.toString());

                @Override
                public String getAddress1() {
                    return address1;
                }

                @Override
                public String getAddress2() {
                    return address2;
                }

                @Override
                public CityElement getCity() {
                    return city;
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
                public int getPrimaryKey() {
                    return primaryKey;
                }

                @Override
                public DataRowState getRowState() {
                    return DataRowState.UNMODIFIED;
                }

                @Override
                public boolean isExisting() {
                    return true;
                }

                @Override
                public int hashCode() {
                    return primaryKey;
                }

                @Override
                public boolean equals(Object obj) {
                    if (null != obj && obj instanceof AddressElement) {
                        AddressElement other = (AddressElement) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
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
            // TODO: Internationalize these
            switch (count) {
                case 0:
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
                ps.setInt(1, dao.getCity().getPrimaryKey());
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
            // TODO: Internationalize this
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

    }

}
