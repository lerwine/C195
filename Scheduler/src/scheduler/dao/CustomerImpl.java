package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.SelectList;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.view.customer.CustomerModel;

public class CustomerImpl extends DataObjectImpl implements Customer {

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private DataObjectReference<AddressImpl, Address> address;
    private boolean active;

    /**
     * Initializes a {@link DataRowState#NEW} customer object.
     */
    public CustomerImpl() {
        super();
        name = "";
        address = DataObjectReference.of(null);
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
        this.name = name;
    }

    @Override
    public DataObjectReference<AddressImpl, Address> getAddressReference() {
        return address;
    }

    @Override
    public Address getAddress() {
        return address.getPartial();
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(Address address) {
        this.address = DataObjectReference.of(address);
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
        this.active = active;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<CustomerImpl, CustomerModel> {

        private static final SelectList DETAIL_DML;

        static {
            DETAIL_DML = new SelectList(DbTable.CUSTOMER);
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
        public SelectList getDetailDml() {
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
        protected void setSaveStatementValue(CustomerImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
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
                target.address = DataObjectReference.of(Address.of(addressId.get(), columns.getString(resultSet, DbColumn.ADDRESS1, ""),
                        columns.getString(resultSet, DbColumn.ADDRESS2, ""), DataObjectReference.of(City.of(resultSet, columns)),
                        columns.getString(resultSet, DbColumn.POSTAL_CODE, ""),
                        columns.getString(resultSet, DbColumn.PHONE, "")));
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
