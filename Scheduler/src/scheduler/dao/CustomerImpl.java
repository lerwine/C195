package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.SelectColumnList;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.view.customer.CustomerModel;

public class CustomerImpl extends DataObjectImpl implements Customer<Address<? extends City>> {

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'address' property.
     */
    public static final String PROP_ADDRESS = "address";

    /**
     * The name of the 'active' property.
     */
    public static final String PROP_ACTIVE = "active";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private Address<? extends City> address;
    private boolean active;

    /**
     * Initializes a {@link DataRowState#NEW} customer object.
     */
    public CustomerImpl() {
        super();
        name = "";
        address = null;
        active = true;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = (name == null) ? "" : name;
        firePropertyChange(PROP_NAME, oldValue, this.name);
    }

    @Override
    public Address<? extends City> getAddress() {
        return address;
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(Address<? extends City> address) {
        Address oldValue = this.address;
        this.address = address;
        firePropertyChange(PROP_ADDRESS, oldValue, this.address);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Set the value of active
     *
     * @param active new value of active
     */
    public void setActive(boolean active) {
        boolean oldValue = this.active;
        this.active = active;
        firePropertyChange(PROP_ADDRESS, oldValue, this.active);
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<CustomerImpl, CustomerModel> {

        private static final SelectColumnList DETAIL_DML;

        static {
            DETAIL_DML = new SelectColumnList(DbTable.CUSTOMER);
            DETAIL_DML.leftJoin(DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID)
                    .leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID)
                    .leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
            DETAIL_DML.makeUnmodifiable();
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        public Optional<CustomerImpl> findByName(Connection connection, String value) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByAddress(Connection connection, int addressId) throws SQLException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        protected CustomerImpl fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            CustomerImpl r = new CustomerImpl();
            initializeDao(r, resultSet, columns);
            return r;
        }

        @Override
        public SelectColumnList getSelectColumns() {
            return DETAIL_DML;
        }

        @Override
        public Class<? extends CustomerImpl> getDaoClass() {
            return CustomerImpl.class;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.CUSTOMER;
        }

        @Override
        protected void setSqlParameter(CustomerImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
            switch (column) {
                case CUSTOMER_NAME:
                    ps.setString(index, dao.getName());
                    break;
                case ACTIVE:
                    ps.setBoolean(index, dao.isActive());
                    break;
                case CUSTOMER_ADDRESS:
                    ps.setInt(index, dao.getAddress().getPrimaryKey());
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected column name");
            }
        }

        @Override
        protected void onInitializeDao(CustomerImpl target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            target.name = columns.getString(resultSet, DbColumn.CUSTOMER_NAME, "");
            Optional<Integer> addressId = columns.tryGetInt(resultSet, DbColumn.CUSTOMER_ADDRESS);
            if (addressId.isPresent()) {
                target.address = Address.of(addressId.get(), columns.getString(resultSet, DbColumn.ADDRESS1, ""),
                        columns.getString(resultSet, DbColumn.ADDRESS2, ""), City.of(resultSet, columns),
                        columns.getString(resultSet, DbColumn.POSTAL_CODE, ""),
                        columns.getString(resultSet, DbColumn.PHONE, ""));
            } else {
                target.address = null;
            }

            target.active = columns.getBoolean(resultSet, DbColumn.ACTIVE, false);
            if (resultSet.wasNull()) {
                target.active = false;
            }
        }

        @Override
        public CustomerFilter getAllItemsFilter() {
            return CustomerFilter.all();
        }

        @Override
        public CustomerFilter getDefaultFilter() {
            return CustomerFilter.byStatus(true);
        }

        @Override
        public String getDeleteDependencyMessage(CustomerImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSaveConflictMessage(CustomerImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public ArrayList<CustomerImpl> getAll(Connection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
