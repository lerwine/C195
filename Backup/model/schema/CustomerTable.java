package scheduler.model.schema;

import java.util.Arrays;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class CustomerTable extends DbTable<CustomerTable.Column> {
    
    public static final CustomerTable INSTANCE = new CustomerTable();

    /**
     * The name of the 'customer' data table.
     */
    public static final String TABLENAME_CUSTOMER = "customer";

    /**
     * The name of the 'customerId' column.
     */
    public static final String COLNAME_CUSTOMERID = "customerId";

    /**
     * The name of the 'name' column.
     */
    public static final String COLNAME_CUSTOMERNAME = "customerName";

    /**
     * The name of the 'addressId' column.
     */
    public static final String COLNAME_ADDRESSID = "addressId";

    /**
     * The name of the 'addressId' column.
     */
    public static final String COLNAME_ACTIVE = "active";

    private final Column[] columns;
    private final Column primaryKeyColumn;
    private final Column customerNameColumn;
    private final AddressColumn addressColumn;
    private final Column activeColumn;
    private final Column createDateColumn;
    private final Column createdByColumn;
    private final Column lastUpdateColumn;
    private final Column lastUpdateByColumn;

    private CustomerTable() {
        primaryKeyColumn = new Column(this, DbColType.INT, COLNAME_CUSTOMERID);
        customerNameColumn = new Column(this, DbColType.STRING, COLNAME_CUSTOMERNAME);
        addressColumn = new AddressColumn(this);
        activeColumn = new Column(this, DbColType.BOOLEAN, COLNAME_ACTIVE);
        createDateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_CREATEDATE);
        createdByColumn = new Column(this, DbColType.STRING, COLNAME_CREATEDBY);
        lastUpdateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_LASTUPDATE);
        lastUpdateByColumn = new Column(this, DbColType.STRING, COLNAME_LASTUPDATEBY);
        columns = new Column[]{primaryKeyColumn, customerNameColumn, addressColumn, activeColumn, createDateColumn, createdByColumn,
            lastUpdateColumn, lastUpdateByColumn};
    }

    @Override
    protected Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Column getCustomerNameColumn() {
        return customerNameColumn;
    }

    public AddressColumn getAddressColumn() {
        return addressColumn;
    }

    public Column getActiveColumn() {
        return activeColumn;
    }

    @Override
    protected Column getCreateDateColumn() {
        return createDateColumn;
    }

    @Override
    protected Column getCreatedByColumn() {
        return createdByColumn;
    }

    @Override
    protected Column getLastUpdateColumn() {
        return lastUpdateColumn;
    }

    @Override
    protected Column getLastUpdateByColumn() {
        return lastUpdateByColumn;
    }

    @Override
    public String getTableName() {
        return TABLENAME_CUSTOMER;
    }

    @Override
    public int size() {
        return columns.length;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[columns.length];
        System.arraycopy(columns, 0, result, 0, columns.length);
        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < columns.length) {
            return (T[]) Arrays.copyOf(columns, columns.length, a.getClass());
        }
        System.arraycopy(columns, 0, a, 0, columns.length);
        if (a.length > columns.length) {
            a[columns.length] = null;
        }
        return a;
    }

    @Override
    public Column get(int index) {
        return columns[index];
    }

    public class Column extends DbTable.DbColumn<CustomerTable> {

        private Column(CustomerTable schema, DbColType type, String name) {
            super(schema, type, name);
        }

    }
    
    public class AddressColumn extends Column implements IChildColumn<CustomerTable, AddressTable.Column> {
        
        private AddressColumn(CustomerTable schema) {
            super(schema, DbColType.INT, COLNAME_ADDRESSID);
        }

        @Override
        public AddressTable.Column getParentColumn() {
            return AddressTable.INSTANCE.getPrimaryKeyColumn();
        }

    }
}
