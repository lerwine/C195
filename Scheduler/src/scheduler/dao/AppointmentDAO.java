package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentFailedEvent;
import scheduler.events.CustomerEvent;
import scheduler.events.CustomerFailedEvent;
import scheduler.events.UserEvent;
import scheduler.events.UserFailedEvent;
import scheduler.model.Appointment;
import scheduler.model.AppointmentType;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.RecordModelContext;
import scheduler.model.User;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.UserItem;
import scheduler.model.ui.UserModel;
import scheduler.util.DB;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.ToStringPropertyBuilder;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Data access object for the {@code appointment} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.APPOINTMENT)
public final class AppointmentDAO extends DataAccessObject implements AppointmentDbRecord {

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentDAO.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentDAO.class.getName());

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private final OriginalValues originalValues;
    private ICustomerDAO customer;
    private IUserDAO user;
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
        originalValues = new OriginalValues();
    }

    @Override
    public ICustomerDAO getCustomer() {
        return customer;
    }

    /**
     * Set the value of customer
     *
     * @param customer new value of customer
     */
    private void setCustomer(ICustomerDAO customer) {
        ICustomerDAO oldValue = this.customer;
        this.customer = customer;
        firePropertyChange(PROP_CUSTOMER, oldValue, this.customer);
    }

    @Override
    public IUserDAO getUser() {
        return user;
    }

    /**
     * Set the value of user
     *
     * @param user new value of user
     */
    private void setUser(IUserDAO user) {
        IUserDAO oldValue = this.user;
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
    private void setTitle(String value) {
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
    private void setDescription(String value) {
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
    private void setLocation(String value) {
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
    private void setContact(String value) {
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
    private void setType(AppointmentType type) {
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
    private void setUrl(String value) {
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
    private void setStart(Timestamp value) {
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
    private void setEnd(Timestamp value) {
        Timestamp oldValue = this.end;
        this.end = Objects.requireNonNull(value);
        firePropertyChange(PROP_END, oldValue, this.end);
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.customer = customer;
        originalValues.user = user;
        originalValues.title = title;
        originalValues.description = description;
        originalValues.location = location;
        originalValues.contact = contact;
        originalValues.type = type;
        originalValues.url = url;
        originalValues.start = start;
        originalValues.end = end;
    }

    @Override
    protected void onRejectChanges() {
        ICustomerDAO oldCustomer = customer;
        IUserDAO oldUser = user;
        String oldTitle = title;
        String oldDescription = description;
        String oldLocation = location;
        String oldContact = contact;
        AppointmentType oldType = type;
        String oldUrl = url;
        Timestamp oldStart = start;
        Timestamp oldEnd = end;
        customer = originalValues.customer;
        user = originalValues.user;
        title = originalValues.title;
        description = originalValues.description;
        location = originalValues.location;
        contact = originalValues.contact;
        type = originalValues.type;
        url = originalValues.url;
        start = originalValues.start;
        end = originalValues.end;
        firePropertyChange(PROP_CUSTOMER, oldCustomer, customer);
        firePropertyChange(PROP_USER, oldUser, user);
        firePropertyChange(PROP_TITLE, oldTitle, title);
        firePropertyChange(PROP_DESCRIPTION, oldDescription, description);
        firePropertyChange(PROP_LOCATION, oldLocation, location);
        firePropertyChange(PROP_CUSTOMER, oldContact, contact);
        firePropertyChange(PROP_TYPE, oldType, type);
        firePropertyChange(PROP_URL, oldUrl, url);
        firePropertyChange(PROP_START, oldStart, start);
        firePropertyChange(PROP_END, oldEnd, end);
    }

    @Override
    public int hashCode() {
        if (getRowState() != DataRowState.NEW) {
            return getPrimaryKey();
        }
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(customer);
        hash = 97 * hash + Objects.hashCode(user);
        hash = 97 * hash + Objects.hashCode(title);
        hash = 97 * hash + Objects.hashCode(description);
        hash = 97 * hash + Objects.hashCode(location);
        hash = 97 * hash + Objects.hashCode(contact);
        hash = 97 * hash + Objects.hashCode(type);
        hash = 97 * hash + Objects.hashCode(url);
        hash = 97 * hash + Objects.hashCode(start);
        hash = 97 * hash + Objects.hashCode(end);
        return hash;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Appointment && ModelHelper.areSameRecord(this, (Appointment) obj);
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(PROP_PRIMARYKEY, getPrimaryKey());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addDataObject(PROP_CUSTOMER, customer)
                .addDataObject(PROP_USER, user)
                .addString(PROP_TITLE, title)
                .addString(PROP_DESCRIPTION, description)
                .addString(PROP_LOCATION, location)
                .addString(PROP_CONTACT, contact)
                .addEnum(PROP_TYPE, type)
                .addString(PROP_URL, url)
                .addTimestamp(PROP_START, start)
                .addTimestamp(PROP_END, end)
                .addTimestamp(PROP_CREATEDATE, getCreateDate())
                .addString(PROP_CREATEDBY, getCreatedBy())
                .addTimestamp(PROP_LASTMODIFIEDDATE, getLastModifiedDate())
                .addString(PROP_LASTMODIFIEDBY, getLastModifiedBy());
    }

    /**
     * Factory implementation for {@link AppointmentDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<AppointmentDAO> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

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
                private final Customer oldCustomer = dao.customer;
                private final User oldUser = dao.user;
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
                    t.firePropertyChange(PROP_CUSTOMER, oldCustomer, dao.customer);
                    t.firePropertyChange(PROP_USER, oldUser, dao.user);
                    t.firePropertyChange(PROP_TITLE, oldTitle, dao.title);
                    t.firePropertyChange(PROP_DESCRIPTION, oldDescription, dao.description);
                    t.firePropertyChange(PROP_LOCATION, oldLocation, dao.location);
                    t.firePropertyChange(PROP_CONTACT, oldContact, dao.contact);
                    t.firePropertyChange(PROP_TYPE, oldType, (null == dao.type) ? AppointmentType.OTHER : dao.type);
                    t.firePropertyChange(PROP_URL, oldUrl, dao.url);
                    t.firePropertyChange(PROP_START, oldStart, dao.start);
                    t.firePropertyChange(PROP_END, oldEnd, dao.end);
                }
            };

            dao.customer = CustomerDAO.FACTORY.fromJoinedResultSet(rs);
            dao.user = UserDAO.FACTORY.fromJoinedResultSet(rs);
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

        public int countByRange(Connection connection, LocalDateTime start, LocalDateTime end) throws SQLException {
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.APPOINTMENT_ID.getDbName())
                    .append(") FROM ").append(DbTable.APPOINTMENT.getDbName());
            if (null != start) {
                sb.append(" WHERE ").append(DbColumn.END.getDbName()).append(">?");
                if (null != end) {
                    sb.append(" AND ").append(DbColumn.START.getDbName()).append("<=?");
                }
            } else if (null != end) {
                sb.append(" WHERE ").append(DbColumn.START.getDbName()).append("<=?");
            }
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int index = 0;
                if (null != start) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(end));
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int result = rs.getInt(1);
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        public List<AppointmentCountByType> getCountsByType(Connection connection, LocalDateTime start, LocalDateTime end) throws SQLException {
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.APPOINTMENT_ID.getDbName()).append(") AS c, ")
                    .append(DbColumn.START.getDbName()).append(", ").append(DbColumn.END.getDbName()).append(", ")
                    .append(DbColumn.TYPE.getDbName())
                    .append(" FROM ").append(DbTable.APPOINTMENT.getDbName())
                    .append(" GROUP BY ").append(DbColumn.TYPE.getDbName());
            if (null != start) {
                sb.append(" HAVING ").append(DbColumn.END.getDbName()).append(" > ?");
                if (null != end) {
                    sb.append(" AND ").append(DbColumn.START.getDbName()).append(" < ?");
                }
            } else if (null != end) {
                sb.append(" HAVING ").append(DbColumn.START.getDbName()).append(" < ?");
            }
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int index = 0;
                if (null != start) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(end));
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    ArrayList<AppointmentCountByType> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(new AppointmentCountByType(AppointmentType.of(rs.getString(DbColumn.TYPE.toString()), AppointmentType.OTHER), rs.getInt("c")));
                    }
                    LogHelper.logWarnings(connection, LOG);
                    return result;
                }
            }
        }

        public List<ItemCountResult<String>> getCountsByCustomerRegion(Connection connection, LocalDateTime start, LocalDateTime end) throws SQLException {
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.APPOINTMENT_ID.getDbName()).append("), a.")
                    .append(DbColumn.START.getDbName()).append(" AS ").append(DbColumn.START.getDbName()).append(", a.")
                    .append(DbColumn.END.getDbName()).append(" AS ").append(DbColumn.END.getDbName()).append(", n.")
                    .append(DbColumn.COUNTRY_NAME.getDbName()).append(" AS ").append(DbColumn.COUNTRY_NAME.getDbName())
                    .append(" FROM ").append(DbTable.APPOINTMENT.getDbName())
                    .append(" a LEFT JOIN ").append(DbTable.CUSTOMER.getDbName()).append(" c ON a.").append(DbColumn.CUSTOMER_ID.getDbName()).append("=c.")
                    .append(DbColumn.CUSTOMER_ID.getDbName()).append(" LEFT JOIN ").append(DbTable.ADDRESS.getDbName())
                    .append(" l ON c.").append(DbColumn.ADDRESS_ID.getDbName()).append("=l.").append(DbColumn.ADDRESS_ID.getDbName())
                    .append(" LEFT JOIN ").append(DbTable.CITY.getDbName())
                    .append(" t ON l.").append(DbColumn.CITY_ID.getDbName()).append("=t.").append(DbColumn.CITY_ID.getDbName())
                    .append(" LEFT JOIN ").append(DbTable.COUNTRY.getDbName())
                    .append(" n on t.").append(DbColumn.COUNTRY_ID.getDbName()).append("=n.").append(DbColumn.COUNTRY_ID.getDbName())
                    .append(" GROUP BY ").append(DbColumn.COUNTRY_NAME.getDbName());
            if (null != start) {
                sb.append(" HAVING ").append(DbColumn.END.getDbName()).append(" > ?");
                if (null != end) {
                    sb.append(" AND ").append(DbColumn.START.getDbName()).append(" <= ?");
                }
            } else if (null != end) {
                sb.append(" HAVING ").append(DbColumn.START.getDbName()).append(" <= ?");
            }
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int index = 0;
                if (null != start) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DB.toUtcTimestamp(end));
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    ArrayList<ItemCountResult<String>> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(new ItemCountResult<>(rs.getString(DbColumn.COUNTRY_NAME.toString()), rs.getInt("c")));
                    }
                    LogHelper.logWarnings(connection, LOG);
                    return result;
                }
            }
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
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int result = rs.getInt(1);
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
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
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int result = rs.getInt(1);
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        protected void onCloneProperties(AppointmentDAO fromDAO, AppointmentDAO toDAO) {
            String oldContact = toDAO.contact;
            ICustomerDAO oldCustomer = toDAO.customer;
            String oldDescription = toDAO.description;
            Timestamp oldEnd = toDAO.end;
            String oldLocation = toDAO.location;
            Timestamp oldStart = toDAO.start;
            String oldTitle = toDAO.title;
            AppointmentType oldType = toDAO.type;
            String oldUrl = toDAO.url;
            IUserDAO oldUser = toDAO.user;
            toDAO.contact = fromDAO.contact;
            toDAO.customer = fromDAO.customer;
            toDAO.description = fromDAO.description;
            toDAO.end = fromDAO.end;
            toDAO.location = fromDAO.location;
            toDAO.start = fromDAO.start;
            toDAO.title = fromDAO.title;
            toDAO.type = fromDAO.type;
            toDAO.url = fromDAO.url;
            toDAO.user = fromDAO.user;
            toDAO.originalValues.contact = fromDAO.originalValues.contact;
            toDAO.originalValues.customer = fromDAO.originalValues.customer;
            toDAO.originalValues.description = fromDAO.originalValues.description;
            toDAO.originalValues.end = fromDAO.originalValues.end;
            toDAO.originalValues.location = fromDAO.originalValues.location;
            toDAO.originalValues.start = fromDAO.originalValues.start;
            toDAO.originalValues.title = fromDAO.originalValues.title;
            toDAO.originalValues.type = fromDAO.originalValues.type;
            toDAO.originalValues.url = fromDAO.originalValues.url;
            toDAO.originalValues.user = fromDAO.originalValues.user;
            toDAO.firePropertyChange(PROP_CUSTOMER, oldCustomer, toDAO.customer);
            toDAO.firePropertyChange(PROP_USER, oldUser, toDAO.user);
            toDAO.firePropertyChange(PROP_TITLE, oldTitle, toDAO.title);
            toDAO.firePropertyChange(PROP_DESCRIPTION, oldDescription, toDAO.description);
            toDAO.firePropertyChange(PROP_LOCATION, oldLocation, toDAO.location);
            toDAO.firePropertyChange(PROP_CONTACT, oldContact, toDAO.contact);
            toDAO.firePropertyChange(PROP_TYPE, oldType, (null == toDAO.type) ? AppointmentType.OTHER : toDAO.type);
            toDAO.firePropertyChange(PROP_URL, oldUrl, toDAO.url);
            toDAO.firePropertyChange(PROP_START, oldStart, toDAO.start);
            toDAO.firePropertyChange(PROP_END, oldEnd, toDAO.end);
        }

    }

    public static class SaveTask extends SaveDaoTask<AppointmentDAO, AppointmentModel, AppointmentEvent> {

        public SaveTask(RecordModelContext<AppointmentDAO, AppointmentModel> target, boolean alreadyValidated) {
            super(target, AppointmentModel.FACTORY, AppointmentEvent.APPOINTMENT_EVENT_TYPE, alreadyValidated);
            AppointmentModel model = target.getFxRecordModel();
            if (null != model) {
                AppointmentDAO dao = target.getDataAccessObject();
                dao.setType(model.getType());
                dao.setTitle(model.getTitle());
                dao.setCustomer(model.getCustomer().dataObject());
                dao.setUser(model.getUser().dataObject());
                dao.setContact(model.getContact());
                dao.setLocation(model.getLocation());
                dao.setUrl(model.getUrl());
                dao.setStart(DB.toUtcTimestamp(model.getStart()));
                dao.setEnd(DB.toUtcTimestamp(model.getEnd()));
                dao.setDescription(model.getDescription());
            }
        }

        @Override
        protected AppointmentEvent createSuccessEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertSuccessEvent(this, this);
            }
            return AppointmentEvent.createUpdateSuccessEvent(this, this);
        }

        @Override
        protected AppointmentEvent validate(Connection connection) throws Exception {
            AppointmentEvent event = AppointmentModel.FACTORY.validateForSave(this);
            if (null != event && event instanceof AppointmentFailedEvent) {
                return event;
            }

            AppointmentDAO appointment = getDataAccessObject();
            ICustomerDAO c = appointment.customer;
            if (c instanceof CustomerDAO) {
                switch (c.getRowState()) {
                    case NEW:
                    case MODIFIED:
                        AppointmentModel model = getFxRecordModel();
                        CustomerItem<? extends ICustomerDAO> cm;
                        CustomerDAO.SaveTask saveTask;
                        if (null != model && null != (cm = model.getCustomer()) && cm instanceof CustomerModel) {
                            saveTask = new CustomerDAO.SaveTask(RecordModelContext.of((CustomerModel) cm), false);
                        } else {
                            cm = null;
                            saveTask = new CustomerDAO.SaveTask(RecordModelContext.of((CustomerDAO) c), false);
                        }
                        saveTask.run();
                        CustomerEvent customerEvent = saveTask.get();
                        if (null != customerEvent && customerEvent instanceof CustomerFailedEvent) {
                            if (getOriginalRowState() == DataRowState.NEW) {
                                return AppointmentEvent.createInsertInvalidEvent(this, this, (CustomerFailedEvent) customerEvent);
                            }
                            return AppointmentEvent.createUpdateInvalidEvent(this, this, (CustomerFailedEvent) customerEvent);
                        }
                        break;
                    default:
                        break;
                }
            }
            IUserDAO u = appointment.user;
            if (u instanceof UserDAO) {
                switch (u.getRowState()) {
                    case NEW:
                    case MODIFIED:
                        AppointmentModel model = getFxRecordModel();
                        UserItem<? extends IUserDAO> um;
                        UserDAO.SaveTask saveTask;
                        if (null != model && null != (um = model.getUser()) && um instanceof UserModel) {
                            saveTask = new UserDAO.SaveTask(RecordModelContext.of((UserModel) um), false);
                        } else {
                            saveTask = new UserDAO.SaveTask(RecordModelContext.of((UserDAO) u), false);
                        }
                        saveTask.run();
                        UserEvent userEvent = saveTask.get();
                        if (null != userEvent && userEvent instanceof UserFailedEvent) {
                            if (getOriginalRowState() == DataRowState.NEW) {
                                return AppointmentEvent.createInsertInvalidEvent(this, this, (UserFailedEvent) userEvent);
                            }
                            return AppointmentEvent.createUpdateInvalidEvent(this, this, (UserFailedEvent) userEvent);
                        }
                        break;
                    default:
                        break;
                }
            }

            return null;
        }

        @Override
        protected AppointmentEvent createFaultedEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertFaultedEvent(this, this, getException());
            }
            return AppointmentEvent.createUpdateFaultedEvent(this, this, getException());
        }

        @Override
        protected AppointmentEvent createCanceledEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertCanceledEvent(this, this);
            }
            return AppointmentEvent.createUpdateCanceledEvent(this, this);
        }

    }

    public static final class DeleteTask extends DeleteDaoTask<AppointmentDAO, AppointmentModel, AppointmentEvent> {

        public DeleteTask(RecordModelContext<AppointmentDAO, AppointmentModel> target, boolean alreadyValidated) {
            super(target, AppointmentModel.FACTORY, AppointmentEvent.APPOINTMENT_EVENT_TYPE, alreadyValidated);
        }

        @Override
        protected AppointmentEvent createSuccessEvent() {
            return AppointmentEvent.createDeleteSuccessEvent(this, this);
        }

        @Override
        protected AppointmentEvent validate(Connection connection) throws Exception {
            return null;
        }

        @Override
        protected AppointmentEvent createFaultedEvent() {
            return AppointmentEvent.createDeleteFaultedEvent(this, this, getException());
        }

        @Override
        protected AppointmentEvent createCanceledEvent() {
            return AppointmentEvent.createDeleteCanceledEvent(this, this);
        }

    }

    private class OriginalValues {

        private ICustomerDAO customer;
        private IUserDAO user;
        private String title;
        private String description;
        private String location;
        private String contact;
        private AppointmentType type;
        private String url;
        private Timestamp start;
        private Timestamp end;

        private OriginalValues() {
            this.customer = AppointmentDAO.this.customer;
            this.user = AppointmentDAO.this.user;
            this.title = AppointmentDAO.this.title;
            this.description = AppointmentDAO.this.description;
            this.location = AppointmentDAO.this.location;
            this.contact = AppointmentDAO.this.contact;
            this.type = AppointmentDAO.this.type;
            this.url = AppointmentDAO.this.url;
            this.start = AppointmentDAO.this.start;
            this.end = AppointmentDAO.this.end;
        }
    }

}
