package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.SelectList;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.util.DB;
import scheduler.view.appointment.AppointmentModel;

public class AppointmentImpl extends DataObjectImpl implements Appointment {

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private DataObjectReference<CustomerImpl, Customer> customer;
    private DataObjectReference<UserImpl, User> user;
    private String title;
    private String description;
    private String location;
    private String contact;
    private AppointmentType type;
    private String url;
    private Timestamp start;
    private Timestamp end;

    /**
     * Initializes a {@link DataRowState#NEW} appointment object.
     */
    public AppointmentImpl() {
        customer = DataObjectReference.of(null);
        user = DataObjectReference.of(null);
        title = "";
        description = "";
        location = "";
        contact = "";
        type = AppointmentType.OTHER;
        url = null;
        LocalDateTime d = LocalDateTime.now().plusHours(1).plusMinutes(30);
        d = d.minusMinutes(d.getMinute()).minusSeconds(d.getSecond()).minusNanos(d.getNano());
        start = DB.toUtcTimestamp(d);
        end = DB.toUtcTimestamp(d.plusHours(1));
    }

    @Override
    public DataObjectReference<CustomerImpl, Customer> getCustomerReference() {
        return customer;
    }

    @Override
    public Customer getCustomer() {
        return customer.getPartial();
    }

    /**
     * Set the value of customer
     *
     * @param value new value of customer
     */
    public void setCustomer(Customer value) {
        Objects.requireNonNull(value);
        customer = DataObjectReference.of(value);
    }

    @Override
    public DataObjectReference<UserImpl, User> getUserReference() {
        return user;
    }

    @Override
    public User getUser() {
        return user.getPartial();
    }

    /**
     * Set the value of user
     *
     * @param value new value of user
     */
    public void setUser(User value) {
        Objects.requireNonNull(value);
        user = DataObjectReference.of(value);
    }

    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param value new value of title
     */
    public void setTitle(String value) {
        title = (value == null) ? "" : value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param value new value of description
     */
    public void setDescription(String value) {
        description = (value == null) ? "" : value;
    }

    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Set the value of location
     *
     * @param value new value of location
     */
    public void setLocation(String value) {
        location = (value == null) ? "" : value;
    }

    @Override
    public String getContact() {
        return contact;
    }

    /**
     * Set the value of contact
     *
     * @param value new value of contact
     */
    public void setContact(String value) {
        contact = (value == null) ? "" : value;
    }

    @Override
    public AppointmentType getType() {
        return type;
    }

    public void setType(AppointmentType value) {
        type = (null == value) ? AppointmentType.OTHER : value;
    }

    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Set the value of url
     *
     * @param value new value of url
     */
    public void setUrl(String value) {
        url = (value == null) ? "" : value;
    }

    @Override
    public Timestamp getStart() {
        return start;
    }

    /**
     * Set the value of start
     *
     * @param value new value of start
     */
    public void setStart(Timestamp value) {
        Objects.requireNonNull(value);
        start = value;
    }

    @Override
    public Timestamp getEnd() {
        return end;
    }

    /**
     * Set the value of end
     *
     * @param value new value of end
     */
    public void setEnd(Timestamp value) {
        Objects.requireNonNull(value);
        end = value;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<AppointmentImpl, AppointmentModel> {

        private static final SelectList DETAIL_DML;

        static {
            DETAIL_DML = new SelectList(DbTable.APPOINTMENT);
            DETAIL_DML.leftJoin(DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_ID)
                    .leftJoin(DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID)
                    .leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID)
                    .leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
            DETAIL_DML.leftJoin(DbColumn.APPOINTMENT_USER, DbColumn.USER_ID);
            DETAIL_DML.makeUnmodifiable();
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected AppointmentImpl fromResultSet(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            AppointmentImpl r = new AppointmentImpl();
            initializeDao(r, resultSet, columns);
            return r;
        }

        @Override
        public SelectList getDetailDml() {
            return DETAIL_DML;
        }

        @Override
        public Class<? extends AppointmentImpl> getDaoClass() {
            return AppointmentImpl.class;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.APPOINTMENT;
        }

        @Override
        protected void setSaveStatementValue(AppointmentImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
            switch (column) {
                case APPOINTMENT_CUSTOMER:
                    ps.setInt(index, dao.getCustomer().getPrimaryKey());
                    break;
                case CUSTOMER_ADDRESS:
                    ps.setInt(index, dao.getUser().getPrimaryKey());
                    break;
                case TITLE:
                    ps.setString(index, dao.getTitle());
                    break;
                case DESCRIPTION:
                    ps.setString(index, dao.getDescription());
                    break;
                case LOCATION:
                    ps.setString(index, dao.getLocation());
                    break;
                case CONTACT:
                    ps.setString(index, dao.getContact());
                    break;
                case TYPE:
                    ps.setString(index, dao.getType().getDbValue());
                    break;
                case URL:
                    ps.setString(index, dao.getUrl());
                    break;
                case START:
                    ps.setTimestamp(index, dao.getStart());
                    break;
                case END:
                    ps.setTimestamp(index, dao.getEnd());
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected column name");
            }
        }

        @Override
        protected void onInitializeDao(AppointmentImpl target, ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
            target.customer = DataObjectReference.of(Customer.of(resultSet, columns));
            target.user = DataObjectReference.of(User.of(resultSet, columns));
            target.title = columns.getString(resultSet, DbColumn.TITLE, "");
            target.description = columns.getString(resultSet, DbColumn.DESCRIPTION, "");
            target.location = columns.getString(resultSet, DbColumn.LOCATION, "");
            target.contact = columns.getString(resultSet, DbColumn.CONTACT, "");
            target.type = AppointmentType.of(columns.getString(resultSet, DbColumn.TYPE, ""), AppointmentType.OTHER);
            target.url = columns.getString(resultSet, DbColumn.URL, "");
            target.start = columns.getTimestamp(resultSet, DbColumn.START);
            target.end = columns.getTimestamp(resultSet, DbColumn.END);
        }

        @Override
        public AppointmentFilter getAllItemsFilter() {
            return AppointmentFilter.all();
        }

        @Override
        public AppointmentFilter getDefaultFilter() {
            return AppointmentFilter.myCurrentAndFuture();
        }

        public int countByCustomer(Connection connection, int customerId, LocalDateTime start, LocalDateTime end) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByCustomer(Connection connection, int customerId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByUser(Connection connection, int userId, LocalDateTime start, LocalDateTime end) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByUser(Connection connection, int userId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getDeleteDependencyMessage(AppointmentImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSaveConflictMessage(AppointmentImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
