package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.util.DB;
import scheduler.util.InternalException;
import static scheduler.util.Values.asNonNullAndTrimmed;

@DatabaseTable(DbTable.APPOINTMENT)
public class AppointmentDAO extends DataAccessObject implements AppointmentElement {

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

    private CustomerElement customer;
    private UserElement user;
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
    public AppointmentDAO() {
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
    protected void reValidate(Consumer<ValidationResult> addValidation) {
        if (null == customer) {
            addValidation.accept(ValidationResult.NO_CUSTOMER);
        } else if (customer.validate() != ValidationResult.OK) {
            addValidation.accept(ValidationResult.CUSTOMER_INVALID);
        } else if (customer.getRowState() == DataRowState.NEW) {
            addValidation.accept(ValidationResult.CUSTOMER_NOT_SAVED);
        }
        if (null == user) {
            addValidation.accept(ValidationResult.NO_USER);
        } else if (user.validate() != ValidationResult.OK) {
            addValidation.accept(ValidationResult.USER_INVALID);
        } else if (user.getRowState() == DataRowState.NEW) {
            addValidation.accept(ValidationResult.USER_NOT_SAVED);
        }
        if (title.trim().isEmpty()) {
            addValidation.accept(ValidationResult.TITLE_EMPTY);
        }
        if (null == start) {
            addValidation.accept(ValidationResult.NO_START_DATE);
        } else if (null == end) {
            addValidation.accept(ValidationResult.NO_END_DATE);
        } else if (start.compareTo(end) > 0) {
            addValidation.accept(ValidationResult.START_FOLLOWS_END);
        }
        switch (type) {
            case CORPORATE_HQ_MEETING:
            case CUSTOMER_SITE:
            case GERMANY_SITE_MEETING:
            case HONDURAS_SITE_MEETING:
            case INDIA_SITE_MEETING:
                break;
            case PHONE:
            case VIRTUAL:
                if (url.trim().isEmpty()) {
                    addValidation.accept(ValidationResult.URL_EMPTY);
                }
            default:
                if (location.trim().isEmpty()) {
                    addValidation.accept(ValidationResult.LOCATION_EMPTY);
                }
                break;
        }
    }

    @Override
    public CustomerElement getCustomer() {
        return customer;
    }

    /**
     * Set the value of customer
     *
     * @param customer new value of customer
     */
    public void setCustomer(CustomerElement customer) {
        CustomerElement oldValue = this.customer;
        this.customer = customer;
        firePropertyChange(PROP_CUSTOMER, oldValue, this.customer);
    }

    @Override
    public UserElement getUser() {
        return user;
    }

    /**
     * Set the value of user
     *
     * @param user new value of user
     */
    public void setUser(UserElement user) {
        UserElement oldValue = this.user;
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
        this.title = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_TITLE, oldValue, this.title);
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
        String oldValue = this.description;
        this.description = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_DESCRIPTION, oldValue, this.description);
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
        String oldValue = this.location;
        this.location = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_LOCATION, oldValue, this.location);
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
        String oldValue = this.contact;
        this.contact = asNonNullAndTrimmed(value);
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
     * @param value new value of url
     */
    public void setUrl(String value) {
        String oldValue = this.url;
        this.url = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_URL, oldValue, this.url);
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
        Timestamp oldValue = this.start;
        this.start = Objects.requireNonNull(value);
        firePropertyChange(PROP_START, oldValue, this.start);
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
        Timestamp oldValue = this.end;
        this.end = Objects.requireNonNull(value);
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
        if (null != obj && obj instanceof AppointmentElement) {
            AppointmentElement other = (AppointmentElement) obj;
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

    public static final class FactoryImpl extends DataAccessObject.DaoFactory<AppointmentDAO> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(AppointmentDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case APPOINTMENT_CUSTOMER:
                    ps.setInt(index, dao.getCustomer().getPrimaryKey());
                    break;
                case APPOINTMENT_USER:
                    ps.setInt(index, dao.getUser().getPrimaryKey());
                    break;
                case TITLE:
                    ps.setString(index, dao.title);
                    break;
                case DESCRIPTION:
                    ps.setString(index, dao.description);
                    break;
                case LOCATION:
                    ps.setString(index, dao.location);
                    break;
                case CONTACT:
                    ps.setString(index, dao.contact);
                    break;
                case TYPE:
                    ps.setString(index, dao.type.toString());
                    break;
                case URL:
                    ps.setString(index, dao.url);
                    break;
                case START:
                    ps.setTimestamp(index, dao.start);
                    break;
                case END:
                    ps.setTimestamp(index, dao.end);
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public AppointmentDAO createNew() {
            return new AppointmentDAO();
        }

        @Override
        public DaoFilter<AppointmentDAO> getAllItemsFilter() {
            return AppointmentFilter.of(DaoFilterExpression.empty());
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            DmlSelectQueryBuilder builder = new DmlSelectQueryBuilder(DbTable.APPOINTMENT, SchemaHelper.getTableColumns(DbTable.APPOINTMENT));
            builder.join(DbColumn.APPOINTMENT_CUSTOMER, TableJoinType.LEFT, DbColumn.CUSTOMER_ID,
                        SchemaHelper.getTableColumns(DbTable.CUSTOMER, SchemaHelper::isForJoinedData))
                    .join(DbColumn.CUSTOMER_ADDRESS, TableJoinType.LEFT, DbColumn.ADDRESS_ID,
                            SchemaHelper.getTableColumns(DbTable.ADDRESS, SchemaHelper::isForJoinedData))
                    .join(DbColumn.ADDRESS_CITY, TableJoinType.LEFT, DbColumn.CITY_ID,
                            SchemaHelper.getTableColumns(DbTable.CITY, SchemaHelper::isForJoinedData))
                    .join(DbColumn.CITY_COUNTRY, TableJoinType.LEFT, DbColumn.COUNTRY_ID,
                            SchemaHelper.getTableColumns(DbTable.COUNTRY, SchemaHelper::isForJoinedData));
            builder.join(DbColumn.APPOINTMENT_USER, TableJoinType.LEFT, DbColumn.USER_ID,
                    SchemaHelper.getTableColumns(DbTable.USER, SchemaHelper::isForJoinedData));
            return builder;
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(AppointmentDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final CustomerElement oldCustomer = dao.customer;
                private final UserElement oldUser = dao.user;
                private final String oldTitle = dao.title;
                private final String oldDescription = dao.description;
                private final String oldLocation = dao.location;
                private final String oldContact = dao.contact;
                private final AppointmentType oldType = dao.type;
                private final String oldUrl = dao.url;
                private final Timestamp oldStart = dao.start;
                private final Timestamp oldEnd = dao.end;

                @Override
                public void accept(PropertyChangeSupport t) {
                    if (!Objects.equals(dao.customer, oldCustomer)) {
                        t.firePropertyChange(PROP_CUSTOMER, oldCustomer, dao.customer);
                    }
                    if (!Objects.equals(dao.user, oldUser)) {
                        t.firePropertyChange(PROP_USER, oldUser, dao.user);
                    }
                    if (!dao.title.equals(oldTitle)) {
                        t.firePropertyChange(PROP_TITLE, oldTitle, dao.title);
                    }
                    if (!dao.description.equals(oldDescription)) {
                        t.firePropertyChange(PROP_DESCRIPTION, oldDescription, dao.description);
                    }
                    if (!dao.location.equals(oldLocation)) {
                        t.firePropertyChange(PROP_LOCATION, oldLocation, dao.location);
                    }
                    if (!dao.contact.equals(oldContact)) {
                        t.firePropertyChange(PROP_CONTACT, oldContact, dao.contact);
                    }
                    if (dao.type == oldType) {
                        t.firePropertyChange(PROP_TYPE, oldType, (null == dao.type) ? AppointmentType.OTHER : dao.type);
                    }
                    if (!dao.url.equals(oldUrl)) {
                        t.firePropertyChange(PROP_URL, oldUrl, dao.url);
                    }
                    if (!dao.start.equals(oldStart)) {
                        t.firePropertyChange(PROP_START, oldStart, dao.start);
                    }
                    if (!dao.end.equals(oldEnd)) {
                        t.firePropertyChange(PROP_END, oldEnd, dao.end);
                    }
                }
            };

            dao.customer = CustomerDAO.getFactory().fromJoinedResultSet(rs);
            dao.user = UserDAO.getFactory().fromJoinedResultSet(rs);
            dao.title = asNonNullAndTrimmed(rs.getString(DbColumn.TITLE.toString()));
            dao.description = asNonNullAndTrimmed(rs.getString(DbColumn.DESCRIPTION.toString()));
            dao.location = asNonNullAndTrimmed(rs.getString(DbColumn.LOCATION.toString()));
            dao.contact = asNonNullAndTrimmed(rs.getString(DbColumn.CONTACT.toString()));
            dao.type = AppointmentType.of(rs.getString(DbColumn.TYPE.toString()), dao.type);
            dao.url = asNonNullAndTrimmed(rs.getString(DbColumn.URL.toString()));
            dao.start = rs.getTimestamp(DbColumn.START.toString());
            if (rs.wasNull()) {
                dao.end = rs.getTimestamp(DbColumn.END.toString());
                if (rs.wasNull()) {
                    dao.start = dao.end = DB.toUtcTimestamp(LocalDateTime.now());
                } else {
                    dao.start = dao.end;
                }
            } else {
                dao.end = rs.getTimestamp(DbColumn.END.toString());
                if (rs.wasNull()) {
                    dao.end = dao.start;
                }
            }

            return propertyChanges;
        }

        @Override
        public Class<? extends AppointmentDAO> getDaoClass() {
            return AppointmentDAO.class;
        }

        /**
         * Gets the number of appointments that reference the specified customer ID.
         *
         * @param connection The database connection to use.
         * @param customerId The primary key value of the customer.
         * @param start The start date range or {@code null} for no start range.
         * @param end The end date range or {@code null} for no end range.
         * @return The number of appointments that reference the specified customer ID.
         * @throws SQLException if unable to read data from the database.
         */
        public int countByCustomer(Connection connection, int customerId, LocalDateTime start, LocalDateTime end) throws SQLException {
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.APPOINTMENT_ID.getDbName())
                    .append(") FROM ").append(DbTable.APPOINTMENT.getDbName())
                    .append(" WHERE ").append(DbColumn.APPOINTMENT_CUSTOMER.getDbName()).append("=?");
            if (null != start) {
                sb.append(" AND ").append(DbColumn.END.getDbName()).append(">?");
            }
            if (null != end) {
                sb.append(" AND ").append(DbColumn.START.getDbName()).append("<=?");
            }
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                int index = 1;
                if (null != start) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(end));
                }
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        /**
         * Gets the number of appointments that reference the specified user ID.
         *
         * @param connection The database connection to use.
         * @param userId The primary key value of the user.
         * @param start The start date range or {@code null} for no start range.
         * @param end The end date range or {@code null} for no end range.
         * @return The number of appointments that reference the specified user ID.
         * @throws SQLException if unable to read data from the database.
         */
        public int countByUser(Connection connection, int userId, LocalDateTime start, LocalDateTime end) throws SQLException {
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.APPOINTMENT_ID.getDbName())
                    .append(") FROM ").append(DbTable.APPOINTMENT.getDbName())
                    .append(" WHERE ").append(DbColumn.APPOINTMENT_USER.getDbName()).append("=?");
            if (null != start) {
                sb.append(" AND ").append(DbColumn.END.getDbName()).append(">?");
            }
            if (null != end) {
                sb.append(" AND ").append(DbColumn.START.getDbName()).append("<=?");
            }
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, userId);
                int index = 1;
                if (null != start) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(end));
                }
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        public String getDeleteDependencyMessage(AppointmentDAO dao, Connection connection) throws SQLException {
            // No database dependencies
            return "";
        }

        @Override
        public String getSaveDbConflictMessage(AppointmentDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Data access object already deleted");
            }
            // Nothing needs to be unique.
            return "";
        }

    }

}
