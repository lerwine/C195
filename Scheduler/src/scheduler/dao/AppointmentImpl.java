package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.SelectColumnList;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.util.DB;
import scheduler.view.appointment.AppointmentModel;

public class AppointmentImpl extends DataObjectImpl implements Appointment<Customer, User> {

    private static final FactoryImpl FACTORY = new FactoryImpl();

    /**
     * The name of the 'customer' property.
     */
    public static final String PROP_CUSTOMER = "customer";

    /**
     * The name of the 'user' property.
     */
    public static final String PROP_USER = "user";

    /**
     * The name of the 'title' property.
     */
    public static final String PROP_TITLE = "title";

    /**
     * The name of the 'description' property.
     */
    public static final String PROP_DESCRIPTION = "description";

    /**
     * The name of the 'location' property.
     */
    public static final String PROP_LOCATION = "location";

    /**
     * The name of the 'contact' property.
     */
    public static final String PROP_CONTACT = "contact";

    /**
     * The name of the 'type' property.
     */
    public static final String PROP_TYPE = "type";

    /**
     * The name of the 'url' property.
     */
    public static final String PROP_URL = "url";

    /**
     * The name of the 'end' property.
     */
    public static final String PROP_END = "end";

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    /**
     * The name of the 'start' property.
     */
    public static final String PROP_START = "start";

    private Customer customer;
    private User user;
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
        customer = null;
        user = null;
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
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Set the value of customer
     *
     * @param customer new value of customer
     */
    public void setCustomer(Customer customer) {
        Customer oldValue = this.customer;
        this.customer = customer;
        firePropertyChange(PROP_CUSTOMER, oldValue, this.customer);
    }

    @Override
    public User getUser() {
        return user;
    }

    /**
     * Set the value of user
     *
     * @param user new value of user
     */
    public void setUser(User user) {
        User oldValue = this.user;
        this.user = user;
        firePropertyChange(PROP_USER, oldValue, this.user);
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
        String oldValue = this.title;
        this.title = (title == null) ? "" : title;
        firePropertyChange(PROP_TITLE, oldValue, this.title);
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = (description == null) ? "" : description;
        firePropertyChange(PROP_DESCRIPTION, oldValue, this.description);
    }

    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Set the value of location
     *
     * @param location new value of location
     */
    public void setLocation(String location) {
        String oldValue = this.location;
        this.location = (location == null) ? "" : location;
        firePropertyChange(PROP_LOCATION, oldValue, this.location);
    }

    @Override
    public String getContact() {
        return contact;
    }

    /**
     * Set the value of contact
     *
     * @param contact new value of contact
     */
    public void setContact(String contact) {
        String oldValue = this.contact;
        this.contact = (contact == null) ? "" : contact;
        firePropertyChange(PROP_CONTACT, oldValue, this.contact);
    }

    @Override
    public AppointmentType getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(AppointmentType type) {
        AppointmentType oldValue = this.type;
        this.type = (null == type) ? AppointmentType.OTHER : type;
        firePropertyChange(PROP_TYPE, oldValue, this.type);
    }

    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Set the value of url
     *
     * @param url new value of url
     */
    public void setUrl(String url) {
        String oldValue = this.url;
        this.url = (url == null) ? "" : url;
        firePropertyChange(PROP_URL, oldValue, this.url);
    }

    @Override
    public Timestamp getStart() {
        return start;
    }

    /**
     * Set the value of start
     *
     * @param start new value of start
     */
    public void setStart(Timestamp start) {
        Timestamp oldValue = this.start;
        this.start = start;
        firePropertyChange(PROP_START, oldValue, this.start);
    }

    @Override
    public Timestamp getEnd() {
        return end;
    }

    /**
     * Set the value of end
     *
     * @param end new value of end
     */
    public void setEnd(Timestamp end) {
        Timestamp oldValue = this.end;
        this.end = end;
        firePropertyChange(PROP_END, oldValue, this.end);
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<AppointmentImpl, AppointmentModel> {

        private static final SelectColumnList DETAIL_DML;

        static {
            DETAIL_DML = new SelectColumnList(DbTable.APPOINTMENT);
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
        public SelectColumnList getSelectColumns() {
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
        protected void setSqlParameter(AppointmentImpl dao, DbColumn column, PreparedStatement ps, int index) throws SQLException {
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
            target.customer = Customer.of(resultSet, columns);
            target.user = User.of(resultSet, columns);
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
