package scheduler.model.schema;

import java.util.Arrays;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class CountryTable extends DbTable<CountryTable.Column> {
    
    public static final CountryTable INSTANCE = new CountryTable();

    /**
     * The name of the 'country' data table.
     */
    public static final String TABLENAME_COUNTRY = "country";

    /**
     * The name of the 'countryId' column.
     */
    public static final String COLNAME_COUNTRYID = "countryId";

    /**
     * The name of the 'country' column.
     */
    public static final String COLNAME_COUNTRY = "country";

    private final Column[] columns;
    private final Column primaryKeyColumn;
    private final Column countryColumn;
    private final Column createDateColumn;
    private final Column createdByColumn;
    private final Column lastUpdateColumn;
    private final Column lastUpdateByColumn;

    private CountryTable() {
        primaryKeyColumn = new Column(this, DbColType.INT, COLNAME_COUNTRYID);
        countryColumn = new Column(this, DbColType.STRING, COLNAME_COUNTRY);
        createDateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_CREATEDATE);
        createdByColumn = new Column(this, DbColType.STRING, COLNAME_CREATEDBY);
        lastUpdateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_LASTUPDATE);
        lastUpdateByColumn = new Column(this, DbColType.STRING, COLNAME_LASTUPDATEBY);
        columns = new Column[]{primaryKeyColumn, countryColumn, createDateColumn, createdByColumn, lastUpdateColumn, lastUpdateByColumn};
    }

    @Override
    protected Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Column getCountryColumn() {
        return countryColumn;
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
        return TABLENAME_COUNTRY;
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

    public class Column extends DbTable.DbColumn<CountryTable> {

        private Column(CountryTable schema, DbColType type, String name) {
            super(schema, type, name);
        }

    }
}
