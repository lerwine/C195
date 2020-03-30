package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.AppResourceBundleConstants;
import scheduler.AppResources;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;

public class AddressImpl extends DataObjectImpl implements Address<City<? extends Country>> {

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
    private City<? extends Country> city;
    private String postalCode;
    private String phone;

    /**
     * Initializes a {@link DataRowState#NEW} address object.
     */
    public AddressImpl() {
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
        address1 = (value == null) ? "" : value;
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
        address1 = (value == null) ? "" : value;
        firePropertyChange(PROP_ADDRESS2, oldValue, address2);
    }

    @Override
    public City<? extends Country> getCity() {
        return city;
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public void setCity(City<? extends Country> city) {
        City<? extends Country> oldValue = this.city;
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
        postalCode = (value == null) ? "" : value;
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
        phone = (value == null) ? "" : value;
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
        if (null != obj && obj instanceof Address) {
            Address other = (Address) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && address1.equals(other.getAddress1()) && address2.equals(other.getAddress2())
                        && city.equals(other.getCity()) && postalCode.equals(other.getPostalCode()) && phone.equals(other.getPhone());
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    public static final class FactoryImpl extends DataObjectImpl.DaoFactory<AddressImpl> {

        @Override
        public DbTable getDbTable() {
            return DbTable.ADDRESS;
        }

        @Override
        public DbColumn getPrimaryKeyColumn() {
            return DbColumn.ADDRESS_ID;
        }

        @Override
        public AddressImpl createNew() {
            return new AddressImpl();
        }

        @Override
        public DaoFilter<AddressImpl> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_LOADINGADDRESSES));
        }

        @Override
        public DaoFilter<AddressImpl> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public StringBuilder getBaseSelectQuery() {
            StringBuilder sb = new StringBuilder();
            CityImpl.getFactory().appendSelectColumns(sb.append("SELECT ")
                    .append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_ID).append(" AS ").append(DbColumn.ADDRESS_ID)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS1).append(" AS ").append(DbColumn.ADDRESS1)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS2).append(" AS ").append(DbColumn.ADDRESS2));
            CityImpl.getFactory().appendJoinStatement(sb
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.POSTAL_CODE).append(" AS ").append(DbColumn.POSTAL_CODE)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.PHONE).append(" AS ").append(DbColumn.PHONE)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_CREATE_DATE).append(" AS ").append(DbColumn.ADDRESS_CREATE_DATE)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_CREATED_BY).append(" AS ").append(DbColumn.ADDRESS_CREATED_BY)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_LAST_UPDATE).append(" AS ").append(DbColumn.ADDRESS_LAST_UPDATE)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_LAST_UPDATE_BY).append(" AS ").append(DbColumn.ADDRESS_LAST_UPDATE_BY)
                    .append(" FROM ").append(DbTable.ADDRESS.getDbName()).append(" ").append(DbTable.ADDRESS));
            return sb;
        }

        void appendSelectColumns(StringBuilder sb) {
            CityImpl.getFactory().appendSelectColumns(sb
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS1).append(" AS ").append(DbColumn.ADDRESS1)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS2).append(" AS ").append(DbColumn.ADDRESS2));
            sb.append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.POSTAL_CODE).append(" AS ").append(DbColumn.POSTAL_CODE)
                    .append(", ").append(DbTable.ADDRESS).append(".").append(DbColumn.PHONE).append(" AS ").append(DbColumn.PHONE);
        }

        void appendJoinStatement(StringBuilder sb) {
            CityImpl.getFactory().appendJoinStatement(sb.append(" LEFT JOIN ").append(DbTable.ADDRESS.getDbName()).append(" ").append(DbTable.ADDRESS)
                    .append(" ON ").append(DbTable.CUSTOMER).append(".").append(DbColumn.CUSTOMER_ADDRESS).append(" = ")
                    .append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_ID));
        }

        @Override
        protected void onInitializeFromResultSet(AddressImpl dao, ResultSet rs) throws SQLException {
            String oldAddress1 = dao.address1;
            dao.address1 = rs.getString(DbColumn.ADDRESS1.toString());
            String oldAddress2 = dao.address2;
            dao.address2 = rs.getString(DbColumn.ADDRESS2.toString());
            City<? extends Country> oldCity = dao.city;
            dao.city = CityImpl.getFactory().fromJoinedResultSet(rs);
            String oldPostalCode = dao.postalCode;
            dao.postalCode = rs.getString(DbColumn.POSTAL_CODE.toString());
            String oldPhone = dao.phone;
            dao.phone = rs.getString(DbColumn.PHONE.toString());
            dao.firePropertyChange(PROP_ADDRESS1, oldAddress1, dao.address1);
            dao.firePropertyChange(PROP_ADDRESS2, oldAddress2, dao.address2);
            dao.firePropertyChange(PROP_CITY, oldCity, dao.city);
            dao.firePropertyChange(PROP_POSTALCODE, oldPostalCode, dao.postalCode);
            dao.firePropertyChange(PROP_PHONE, oldPhone, dao.phone);
        }

        Address<? extends City> fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Address<City<? extends Country>>() {
                private final String address1 = rs.getString(DbColumn.ADDRESS1.toString());
                private final String address2 = rs.getString(DbColumn.ADDRESS2.toString());
                private final City<? extends Country> city = CityImpl.getFactory().fromJoinedResultSet(rs);
                private final String postalCode = rs.getString(DbColumn.POSTAL_CODE.toString());
                private final String phone = rs.getString(DbColumn.PHONE.toString());
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
                public City<? extends Country> getCity() {
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
                    if (null != obj && obj instanceof Address) {
                        Address<? extends City> other = (Address<? extends City>) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

//        @Override
//        protected AddressImpl fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
//            AddressImpl r = new AddressImpl();
//            initializeDao(r, resultSet, columns);
//            return r;
//        }
//
//        @Override
//        public SelectColumnList getSelectColumns() {
//            return DETAIL_DML;
//        }
        @Override
        public Class<? extends AddressImpl> getDaoClass() {
            return AddressImpl.class;
        }

//        @Override
//        public DbTable getDbTable() {
//            return DbTable.ADDRESS;
//        }
        @Override
        public String getDeleteDependencyMessage(AddressImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getSaveConflictMessage(AddressImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isAssignableFrom(DataObjectImpl dao) {
            return null != dao && dao instanceof AddressImpl;
        }

    }

}
