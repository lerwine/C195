package scheduler.model.schema;

import java.util.Arrays;

public final class UserTable extends DbTable<UserTable.Column> {

    public static final UserTable INSTANCE = new UserTable();

    /**
     * The name of the 'user' data table.
     */
    public static final String TABLENAME_USER = "user";

    /**
     * The name of the 'userId' column.
     */
    public static final String COLNAME_USERID = "userId";

    /**
     * The name of the 'userName' column.
     */
    public static final String COLNAME_USERNAME = "userName";

    /**
     * The name of the 'active' column.
     */
    public static final String COLNAME_ACTIVE = "active";

    private final Column[] columns;
    private final Column primaryKeyColumn;
    private final Column createDateColumn;
    private final Column createdByColumn;
    private final Column lastUpdateColumn;
    private final Column lastUpdateByColumn;
    private final Column userNameColumn;
    private final Column activeColumn;

    private UserTable() {
        primaryKeyColumn = new Column(this, DbColType.INT, COLNAME_USERID);
        createDateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_CREATEDATE);
        createdByColumn = new Column(this, DbColType.STRING, COLNAME_CREATEDBY);
        lastUpdateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_LASTUPDATE);
        lastUpdateByColumn = new Column(this, DbColType.STRING, COLNAME_LASTUPDATEBY);
        userNameColumn = new Column(this, DbColType.STRING, COLNAME_USERNAME);
        activeColumn = new Column(this, DbColType.INT, COLNAME_ACTIVE);
        columns = new Column[]{primaryKeyColumn, userNameColumn, activeColumn, createDateColumn, createdByColumn, lastUpdateColumn,
            lastUpdateByColumn};
    }

    @Override
    protected Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public Column getUserNameColumn() {
        return userNameColumn;
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
        return TABLENAME_USER;
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

    public class Column extends DbTable.DbColumn<UserTable> {

        private Column(UserTable schema, DbColType type, String name) {
            super(schema, type, name);
        }

    }
}
