/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model.schema;

import java.util.Arrays;

/**
 *
 * @author lerwi
 */
public final class CityTable extends DbTable<CityTable.Column> {
    
    public static final CityTable INSTANCE = new CityTable();

    /**
     * The name of the 'city' data table.
     */
    public static final String TABLENAME_CITY = "city";

    /**
     * The name of the 'cityId' column.
     */
    public static final String COLNAME_CITYID = "cityId";

    /**
     * The name of the 'addressId' column.
     */
    public static final String COLNAME_CITY = "city";

    /**
     * The name of the 'countryId' column.
     */
    public static final String COLNAME_COUNTRYID = "countryId";

    private final Column[] columns;
    private final Column primaryKeyColumn;
    private final Column cityColumn;
    private final CountryColumn countryColumn;
    private final Column createDateColumn;
    private final Column createdByColumn;
    private final Column lastUpdateColumn;
    private final Column lastUpdateByColumn;

    private CityTable() {
        primaryKeyColumn = new Column(this, DbColType.INT, COLNAME_CITYID);
        cityColumn = new Column(this, DbColType.STRING, COLNAME_CITY);
        countryColumn = new CountryColumn(this);
        createDateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_CREATEDATE);
        createdByColumn = new Column(this, DbColType.STRING, COLNAME_CREATEDBY);
        lastUpdateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_LASTUPDATE);
        lastUpdateByColumn = new Column(this, DbColType.STRING, COLNAME_LASTUPDATEBY);
        columns = new Column[]{primaryKeyColumn, cityColumn, countryColumn, createDateColumn, createdByColumn, lastUpdateColumn, lastUpdateByColumn};
    }

    @Override
    protected Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Column getCityColumn() {
        return cityColumn;
    }

    public CountryColumn getCountryColumn() {
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
        return TABLENAME_CITY;
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

    public class Column extends DbTable.DbColumn<CityTable> {

        private Column(CityTable schema, DbColType type, String name) {
            super(schema, type, name);
        }

    }
    
    public class CountryColumn extends Column implements IChildColumn<CityTable, CountryTable.Column> {
        
        private CountryColumn(CityTable schema) {
            super(schema, DbColType.INT, COLNAME_COUNTRYID);
        }

        @Override
        public CountryTable.Column getParentColumn() {
            return CountryTable.INSTANCE.getPrimaryKeyColumn();
        }

    }
}
