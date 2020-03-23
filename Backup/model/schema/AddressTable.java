package scheduler.model.schema;

import java.util.Arrays;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class AddressTable extends DbTable<AddressTable.Column> {
    
    public static final AddressTable INSTANCE = new AddressTable();

    /**
     * The name of the 'address' data table.
     */
    public static final String TABLENAME_ADDRESS = "address";

    /**
     * The name of the 'addressId' column.
     */
    public static final String COLNAME_ADDRESSID = "addressId";

    /**
     * The name of the 'address2' column.
     */
    public static final String COLNAME_ADDRESS = "address";

    /**
     * The name of the 'address' column.
     */
    public static final String COLNAME_ADDRESS2 = "address2";

    /**
     * The name of the 'cityId' column.
     */
    public static final String COLNAME_CITYID = "cityId";

    /**
     * The name of the 'address' column.
     */
    public static final String COLNAME_POSTALCODE = "postalCode";

    /**
     * The name of the 'address' column.
     */
    public static final String COLNAME_PHONE = "phone";

    private final Column[] columns;
    private final Column primaryKeyColumn;
    private final Column addressColumn;
    private final Column address2Column;
    private final CityColumn cityColumn;
    private final Column postalCodeColumn;
    private final Column phoneColumn;
    private final Column createDateColumn;
    private final Column createdByColumn;
    private final Column lastUpdateColumn;
    private final Column lastUpdateByColumn;

    private AddressTable() {
        primaryKeyColumn = new Column(this, DbColType.INT, COLNAME_ADDRESSID);
        addressColumn = new Column(this, DbColType.STRING, COLNAME_ADDRESS);
        address2Column = new Column(this, DbColType.STRING, COLNAME_ADDRESS2);
        cityColumn = new CityColumn(this);
        postalCodeColumn = new Column(this, DbColType.STRING, COLNAME_POSTALCODE);
        phoneColumn = new Column(this, DbColType.STRING, COLNAME_PHONE);
        createDateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_CREATEDATE);
        createdByColumn = new Column(this, DbColType.STRING, COLNAME_CREATEDBY);
        lastUpdateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_LASTUPDATE);
        lastUpdateByColumn = new Column(this, DbColType.STRING, COLNAME_LASTUPDATEBY);
        columns = new Column[]{primaryKeyColumn, addressColumn, address2Column, cityColumn, postalCodeColumn, phoneColumn,
            createDateColumn, createdByColumn, lastUpdateColumn, lastUpdateByColumn};
    }

    @Override
    protected Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Column getAddressColumn() {
        return addressColumn;
    }

    public Column getAddress2Column() {
        return address2Column;
    }

    public CityColumn getCityColumn() {
        return cityColumn;
    }

    public Column getPostalCodeColumn() {
        return postalCodeColumn;
    }

    public Column getPhoneColumn() {
        return phoneColumn;
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
        return TABLENAME_ADDRESS;
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

    public class Column extends DbTable.DbColumn<AddressTable> {

        private Column(AddressTable schema, DbColType type, String name) {
            super(schema, type, name);
        }

    }
    
    public class CityColumn extends Column implements IChildColumn<AddressTable, CityTable.Column> {
        
        private CityColumn(AddressTable schema) {
            super(schema, DbColType.INT, COLNAME_CITYID);
        }

        @Override
        public CityTable.Column getParentColumn() {
            return CityTable.INSTANCE.getPrimaryKeyColumn();
        }

    }
}
