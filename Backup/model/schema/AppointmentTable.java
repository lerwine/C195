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
public final class AppointmentTable extends DbTable<AppointmentTable.Column> {

    public static final AppointmentTable INSTANCE = new AppointmentTable();

    /**
     * The name of the 'appointment' data table.
     */
    public static final String TABLENAME_APPOINTMENT = "appointment";

    /**
     * The name of the 'appointmentId' column.
     */
    public static final String COLNAME_APPOINTMENTID = "appointmentId";

    /**
     * The name of the 'customerId' column.
     */
    public static final String COLNAME_CUSTOMERID = "customerId";

    /**
     * The name of the 'title' column.
     */
    public static final String COLNAME_TITLE = "title";

    /**
     * The name of the 'description' column.
     */
    public static final String COLNAME_DESCRIPTION = "description";

    /**
     * The name of the 'location' column.
     */
    public static final String COLNAME_LOCATION = "location";

    /**
     * The name of the 'contact' column.
     */
    public static final String COLNAME_CONTACT = "contact";

    /**
     * The name of the 'type' column.
     */
    public static final String COLNAME_TYPE = "type";

    /**
     * The name of the 'url' column.
     */
    public static final String COLNAME_URL = "url";

    /**
     * The name of the 'start' column.
     */
    public static final String COLNAME_START = "start";

    /**
     * The name of the 'end' column.
     */
    public static final String COLNAME_END = "end";

    /**
     * The name of the 'userId' column.
     */
    public static final String COLNAME_USERID = "userId";

    private final Column[] columns;
    private final Column primaryKeyColumn;
    private final CustomerColumn customerColumn;
    private final UserColumn userColumn;
    private final Column titleColumn;
    private final Column descriptionColumn;
    private final Column locationColumn;
    private final Column contactColumn;
    private final Column typeColumn;
    private final Column urlColumn;
    private final Column startColumn;
    private final Column endColumn;
    private final Column createDateColumn;
    private final Column createdByColumn;
    private final Column lastUpdateColumn;
    private final Column lastUpdateByColumn;

    private AppointmentTable() {
        primaryKeyColumn = new Column(this, DbColType.INT, COLNAME_APPOINTMENTID);
        customerColumn = new CustomerColumn(this);
        userColumn = new UserColumn(this);
        titleColumn = new Column(this, DbColType.STRING, COLNAME_TITLE);
        descriptionColumn = new Column(this, DbColType.STRING, COLNAME_DESCRIPTION);
        locationColumn = new Column(this, DbColType.STRING, COLNAME_LOCATION);
        contactColumn = new Column(this, DbColType.STRING, COLNAME_CONTACT);
        typeColumn = new Column(this, DbColType.STRING, COLNAME_TYPE);
        urlColumn = new Column(this, DbColType.STRING, COLNAME_URL);
        startColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_START);
        endColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_END);
        createDateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_CREATEDATE);
        createdByColumn = new Column(this, DbColType.STRING, COLNAME_CREATEDBY);
        lastUpdateColumn = new Column(this, DbColType.TIMESTAMP, COLNAME_LASTUPDATE);
        lastUpdateByColumn = new Column(this, DbColType.STRING, COLNAME_LASTUPDATEBY);
        columns = new Column[]{primaryKeyColumn, customerColumn, userColumn, titleColumn, descriptionColumn, locationColumn, contactColumn,
            typeColumn, urlColumn, startColumn, endColumn, createDateColumn, createdByColumn, lastUpdateColumn, lastUpdateByColumn};
    }

    @Override
    protected Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public CustomerColumn getCustomerColumn() {
        return customerColumn;
    }

    public UserColumn getUserColumn() {
        return userColumn;
    }

    public Column getTitleColumn() {
        return titleColumn;
    }

    public Column getDescriptionColumn() {
        return descriptionColumn;
    }

    public Column getLocationColumn() {
        return locationColumn;
    }

    public Column getContactColumn() {
        return contactColumn;
    }

    public Column getTypeColumn() {
        return typeColumn;
    }

    public Column getUrlColumn() {
        return urlColumn;
    }

    public Column getStartColumn() {
        return startColumn;
    }

    public Column getEndColumn() {
        return endColumn;
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
        return TABLENAME_APPOINTMENT;
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

    public class Column extends DbTable.DbColumn<AppointmentTable> {

        private Column(AppointmentTable schema, DbColType type, String name) {
            super(schema, type, name);
        }

    }

    public class CustomerColumn extends Column implements IChildColumn<AppointmentTable, CustomerTable.Column> {

        private CustomerColumn(AppointmentTable schema) {
            super(schema, DbColType.INT, COLNAME_CUSTOMERID);
        }

        @Override
        public CustomerTable.Column getParentColumn() {
            return CustomerTable.INSTANCE.getPrimaryKeyColumn();
        }

    }

    public class UserColumn extends Column implements IChildColumn<AppointmentTable, UserTable.Column> {

        private UserColumn(AppointmentTable schema) {
            super(schema, DbColType.INT, COLNAME_CUSTOMERID);
        }

        @Override
        public UserTable.Column getParentColumn() {
            return UserTable.INSTANCE.getPrimaryKeyColumn();
        }

    }
}
