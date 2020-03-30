package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import scheduler.AppResourceBundleConstants;
import scheduler.AppResources;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.util.DB;

public class AppointmentImpl extends DataObjectImpl implements Appointment<Customer<? extends Address>, User> {

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

    /**
     * The name of the 'start' property.
     */
    public static final String PROP_START = "start";

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private Customer<? extends Address> customer;
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
    public Customer<? extends Address> getCustomer() {
        return customer;
    }

    /**
     * Set the value of customer
     *
     * @param customer new value of customer
     */
    public void setCustomer(Customer<? extends Address> customer) {
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

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.customer);
        hash = 97 * hash + Objects.hashCode(this.user);
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + Objects.hashCode(this.description);
        hash = 97 * hash + Objects.hashCode(this.location);
        hash = 97 * hash + Objects.hashCode(this.contact);
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.url);
        hash = 97 * hash + Objects.hashCode(this.start);
        hash = 97 * hash + Objects.hashCode(this.end);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof Appointment) {
            Appointment other = (Appointment) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && customer.equals(other.getCustomer()) && user.equals(other.getUser())
                        && title.equals(other.getTitle()) && description.equals(other.getDescription()) && location.equals(other.getLocation())
                        && contact.equals(other.getContact()) && type.equals(other.getType()) && url.equals(other.getUrl())
                        && start.equals(other.getStart()) && end.equals(other.getEnd());
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    public static final class FactoryImpl extends DataObjectImpl.DaoFactory<AppointmentImpl> {

        @Override
        public boolean isAssignableFrom(DataObjectImpl dao) {
            return null != dao && dao instanceof AppointmentImpl;
        }

        @Override
        public DbTable getDbTable() {
            return DbTable.APPOINTMENT;
        }

        @Override
        public DbColumn getPrimaryKeyColumn() {
            return DbColumn.APPOINTMENT_ID;
        }

        @Override
        public AppointmentImpl createNew() {
            return new AppointmentImpl();
        }

        @Override
        public DaoFilter<AppointmentImpl> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_LOADINGAPPOINTMENTS));
        }

        @Override
        public DaoFilter<AppointmentImpl> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public StringBuilder getBaseSelectQuery() {
            StringBuilder sb = new StringBuilder();
            CustomerImpl.getFactory().appendSelectColumns(sb.append("SELECT ")
                    .append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_ID).append(" AS ").append(DbColumn.APPOINTMENT_ID)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_ID).append(" AS ").append(DbColumn.APPOINTMENT_ID));
            UserImpl.getFactory().appendSelectColumns(sb
                    .append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_USER).append(" AS ").append(DbColumn.APPOINTMENT_USER));
            CustomerImpl.getFactory().appendJoinStatement(sb
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.TITLE).append(" AS ").append(DbColumn.TITLE)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.DESCRIPTION).append(" AS ").append(DbColumn.DESCRIPTION)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.LOCATION).append(" AS ").append(DbColumn.LOCATION)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.CONTACT).append(" AS ").append(DbColumn.CONTACT)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.TYPE).append(" AS ").append(DbColumn.TYPE)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.URL).append(" AS ").append(DbColumn.URL)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.START).append(" AS ").append(DbColumn.START)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.END).append(" AS ").append(DbColumn.END)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_CREATE_DATE).append(" AS ").append(DbColumn.APPOINTMENT_CREATE_DATE)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_CREATED_BY).append(" AS ").append(DbColumn.APPOINTMENT_CREATED_BY)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_LAST_UPDATE).append(" AS ").append(DbColumn.APPOINTMENT_LAST_UPDATE)
                    .append(", ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.APPOINTMENT_LAST_UPDATE_BY).append(" AS ").append(DbColumn.APPOINTMENT_LAST_UPDATE_BY)
                    .append(" FROM ").append(DbTable.APPOINTMENT.getDbName()).append(" ").append(DbTable.APPOINTMENT));
            UserImpl.getFactory().appendJoinStatement(sb);
            return sb;
        }

        @Override
        protected void onInitializeFromResultSet(AppointmentImpl dao, ResultSet rs) throws SQLException {
            Customer oldCustomer = dao.customer;
            dao.customer = CustomerImpl.getFactory().fromJoinedResultSet(rs);
            User oldUser = dao.user;
            dao.user = UserImpl.getFactory().fromJoinedResultSet(rs);
            String oldTitle = dao.title;
            dao.title = rs.getString(DbColumn.TITLE.toString());
            String oldDescription = dao.description;
            dao.description = rs.getString(DbColumn.DESCRIPTION.toString());
            String oldLocation = dao.location;
            dao.location = rs.getString(DbColumn.LOCATION.toString());
            String oldContact = dao.contact;
            dao.contact = rs.getString(DbColumn.CONTACT.toString());
            AppointmentType oldType = dao.type;
            dao.type = AppointmentType.of(rs.getString(DbColumn.TYPE.toString()), oldType);
            String oldUrl = dao.url;
            dao.url = rs.getString(DbColumn.URL.toString());
            Timestamp oldStart = dao.start;
            dao.start = rs.getTimestamp(DbColumn.START.toString());
            Timestamp oldEnd = dao.end;
            dao.end = rs.getTimestamp(DbColumn.END.toString());
            dao.firePropertyChange(PROP_CUSTOMER, oldCustomer, dao.customer);
            dao.firePropertyChange(PROP_USER, oldUser, dao.user);
            dao.firePropertyChange(PROP_TITLE, oldTitle, dao.title);
            dao.firePropertyChange(PROP_DESCRIPTION, oldDescription, dao.description);
            dao.firePropertyChange(PROP_LOCATION, oldLocation, dao.location);
            dao.firePropertyChange(PROP_CONTACT, oldContact, dao.contact);
            dao.firePropertyChange(PROP_TYPE, oldType, dao.type);
            dao.firePropertyChange(PROP_URL, oldUrl, dao.url);
            dao.firePropertyChange(PROP_START, oldStart, dao.start);
            dao.firePropertyChange(PROP_END, oldEnd, dao.end);
        }

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public Class<? extends AppointmentImpl> getDaoClass() {
            return AppointmentImpl.class;
        }

        public int countByCustomer(Connection connection, int customerId, LocalDateTime start, LocalDateTime end) throws Exception {
            // TODO: Implement this if used
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int countByCustomer(Connection connection, int customerId) throws Exception {
            // TODO: Implement this if used
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int countByUser(Connection connection, int userId, LocalDateTime start, LocalDateTime end) throws Exception {
            // TODO: Implement this if used
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int countByUser(Connection connection, int userId) throws Exception {
            // TODO: Implement this if used
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getDeleteDependencyMessage(AppointmentImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getSaveConflictMessage(AppointmentImpl dao, Connection connection) throws SQLException {
            // TODO: Implement this
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}
