package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import scheduler.AppointmentAlertManager;
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
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.CustomerEvent;
import scheduler.events.CustomerFailedEvent;
import scheduler.events.CustomerSuccessEvent;
import scheduler.events.UserEvent;
import scheduler.events.UserFailedEvent;
import scheduler.events.UserSuccessEvent;
import scheduler.model.Appointment;
import scheduler.model.AppointmentEntity;
import scheduler.model.AppointmentType;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.User;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.PartialUserModel;
import scheduler.model.fx.UserModel;
import scheduler.util.DateTimeUtil;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.Values;
import static scheduler.util.Values.asNonNullAndTrimmed;
import static scheduler.util.Values.asNonNullAndWsNormalized;
import static scheduler.util.Values.asNonNullAndWsNormalizedMultiLine;

/**
 * Data access object for the {@code appointment} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.APPOINTMENT)
public final class AppointmentDAO extends DataAccessObject implements AppointmentEntity<Timestamp> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentDAO.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(AppointmentDAO.class.getName());

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final EventHandler<CustomerSuccessEvent> CUSTOMER_UPDATE_EVENT_HANDLER;
    private static final EventHandler<UserSuccessEvent> USER_UPDATE_EVENT_HANDLER;

    static {
        CUSTOMER_UPDATE_EVENT_HANDLER = FACTORY::onCustomerSaved;
        USER_UPDATE_EVENT_HANDLER = FACTORY::onUserSaved;
        CustomerDAO.FACTORY.addEventHandler(CustomerSuccessEvent.UPDATE_SUCCESS, CUSTOMER_UPDATE_EVENT_HANDLER);
        UserDAO.FACTORY.addEventHandler(UserSuccessEvent.UPDATE_SUCCESS, USER_UPDATE_EVENT_HANDLER);
    }

    public static boolean updateModelList(List<AppointmentDAO> source, ObservableList<AppointmentModel> target) {
        if (null == source || source.isEmpty()) {
            target.clear();
            return true;
        }
        ArrayList<AppointmentModel> toAdd = new ArrayList<>();
        ArrayList<AppointmentModel> toRemove = new ArrayList<>();
        toRemove.addAll(target);
        source.forEach((dao) -> {
            int pk = dao.getPrimaryKey();
            AppointmentModel model = target.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != model) {
                toRemove.remove(model);
            } else {
                toAdd.add(dao.cachedModel(true));
            }
        });
        if (toRemove.isEmpty()) {
            if (toAdd.isEmpty()) {
                return false;
            }
        } else {
            target.removeAll(toRemove);
            if (toAdd.isEmpty()) {
                return false;
            }
        }
        target.addAll(toAdd);
        return true;
    }

    private final OriginalValues originalValues;
    private PartialCustomerDAO customer;
    private PartialUserDAO user;
    private String title;
    private String description;
    private String locationSl;
    private String location;
    private String contact;
    private AppointmentType type;
    private String url;
    private Timestamp start;
    private Timestamp end;
    private WeakReference<AppointmentModel> _cachedModel = null;

    /**
     * Initializes a {@link DataRowState#NEW} appointment object.
     */
    public AppointmentDAO() {
        customer = null;
        user = null;
        title = "";
        description = "";
        locationSl = null;
        location = "";
        contact = "";
        type = AppointmentType.OTHER;
        url = null;
        LocalDateTime d = LocalDateTime.now().plusHours(1).plusMinutes(30);
        d = d.minusMinutes(d.getMinute()).minusSeconds(d.getSecond()).minusNanos(d.getNano());
        start = DateTimeUtil.toUtcTimestamp(d);
        end = DateTimeUtil.toUtcTimestamp(d.plusHours(1));
        originalValues = new OriginalValues();
    }

    @Override
    public synchronized AppointmentModel cachedModel(boolean create) {
        AppointmentModel model;
        if (null != _cachedModel) {
            model = _cachedModel.get();
            if (null != model) {
                return model;
            }
            _cachedModel = null;
        }
        if (create) {
            model = AppointmentModel.FACTORY.createNew(this);
            _cachedModel = new WeakReference<>(model);
            return model;
        }
        return null;
    }

    private synchronized void setCachedModel(AppointmentModel model) {
        if (null == model) {
            if (null != _cachedModel) {
                if (null != _cachedModel.get()) {
                    _cachedModel.clear();
                }
                _cachedModel = null;
            }
        } else if (null == _cachedModel || !Objects.equals(_cachedModel.get(), model)) {
            _cachedModel = new WeakReference<>(model);
        }
    }

    @Override
    public PartialCustomerDAO getCustomer() {
        return customer;
    }

    /**
     * Set the value of customer
     *
     * @param customer new value of customer
     */
    synchronized void setCustomer(PartialCustomerDAO customer) {
        PartialCustomerDAO oldValue = this.customer;
        if (Objects.equals(oldValue, customer)) {
            return;
        }
        this.customer = customer;
        firePropertyChange(PROP_CUSTOMER, oldValue, this.customer);
        setModified();
    }

    @Override
    public PartialUserDAO getUser() {
        return user;
    }

    /**
     * Set the value of user
     *
     * @param user new value of user
     */
    synchronized void setUser(PartialUserDAO user) {
        PartialUserDAO oldValue = this.user;
        if (Objects.equals(oldValue, user)) {
            return;
        }
        this.user = user;
        firePropertyChange(PROP_USER, oldValue, this.user);
        setModified();
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
    private synchronized void setTitle(String value) {
        String oldValue = this.title;
        title = asNonNullAndWsNormalized(value);
        if (!title.equals(oldValue)) {
            firePropertyChange(PROP_TITLE, oldValue, title);
            setModified();
        }
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
    private synchronized void setDescription(String value) {
        String oldValue = this.description;
        description = asNonNullAndWsNormalizedMultiLine(value);
        if (!description.equals(oldValue)) {
            firePropertyChange(PROP_DESCRIPTION, oldValue, description);
            setModified();
        }
    }

    @Override
    public String getLocation() {
        switch (this.type) {
            case CORPORATE_LOCATION:
            case PHONE:
                return locationSl;
            default:
                return location;
        }
    }

    /**
     * Set the value of location
     *
     * @param value new value of location
     */
    private synchronized void setLocation(String value) {
        String oldValue;
        switch (this.type) {
            case CORPORATE_LOCATION:
            case PHONE:
                oldValue = locationSl;
                locationSl = location = asNonNullAndWsNormalized(value);
                break;
            default:
                oldValue = location;
                location = asNonNullAndWsNormalizedMultiLine(value);
                locationSl = null;
                break;
        }
        if (!location.equals(oldValue)) {
            firePropertyChange(PROP_LOCATION, oldValue, location);
            setModified();
        }
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
    private synchronized void setContact(String value) {
        String oldValue = contact;
        contact = asNonNullAndWsNormalized(value);
        if (!contact.equals(oldValue)) {
            firePropertyChange(PROP_CONTACT, oldValue, contact);
            setModified();
        }
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
    private synchronized void setType(AppointmentType type) {
        AppointmentType oldValue = this.type;
        String oldLocation = getLocation();
        this.type = (null == type) ? AppointmentType.OTHER : type;
        if (this.type == oldValue) {
            return;
        }
        switch (this.type) {
            case CORPORATE_LOCATION:
            case PHONE:
                if (null == locationSl) {
                    switch (oldValue) {
                        case CORPORATE_LOCATION:
                        case PHONE:
                            break;
                        default:
                            locationSl = asNonNullAndWsNormalized(oldLocation);
                            firePropertyChange(PROP_TYPE, oldValue, this.type);
                            firePropertyChange(PROP_LOCATION, oldLocation, getLocation());
                            setModified();
                            return;
                    }
                }
                break;
            default:
                break;
        }
        firePropertyChange(PROP_TYPE, oldValue, this.type);
        setModified();
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
    private synchronized void setUrl(String value) {
        String oldValue = url;
        url = asNonNullAndTrimmed(value);
        if (!url.equals(oldValue)) {
            firePropertyChange(PROP_URL, oldValue, url);
            setModified();
        }
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
    private synchronized void setStart(Timestamp value) {
        Timestamp oldValue = start;
        start = Objects.requireNonNull(value);
        if (!Objects.equals(oldValue, start)) {
            firePropertyChange(PROP_START, oldValue, start);
            setModified();
        }
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
    private synchronized void setEnd(Timestamp value) {
        Timestamp oldValue = end;
        end = Objects.requireNonNull(value);
        if (!Objects.equals(oldValue, end)) {
            firePropertyChange(PROP_END, oldValue, end);
            setModified();
        }
    }

    @Override
    public boolean startEquals(Object value) {
        Timestamp s = getStart();
        if (null == s) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof Date) {
            return s.equals(value);
        }

        Timestamp other;
        if (value instanceof ZonedDateTime) {
            other = DateTimeUtil.toUtcTimestamp((ZonedDateTime) value);
        } else if (value instanceof LocalDateTime) {
            other = DateTimeUtil.toUtcTimestamp((LocalDateTime) value);
        } else {
            return false;
        }
        return s.equals(other);
    }

    @Override
    public int compareStart(Object value) {
        Timestamp s = getStart();
        if (null == s) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof Date) {
            return s.compareTo((Date) value);
        }

        if (value instanceof ZonedDateTime) {
            return s.compareTo(DateTimeUtil.toUtcTimestamp((ZonedDateTime) value));
        }

        if (value instanceof LocalDateTime) {
            return s.compareTo(DateTimeUtil.toUtcTimestamp((LocalDateTime) value));
        }

        throw new IllegalArgumentException();
    }

    @Override
    public boolean endEquals(Object value) {
        Timestamp e = getEnd();
        if (null == e) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof Date) {
            return e.equals(value);
        }

        Timestamp other;
        if (value instanceof ZonedDateTime) {
            other = DateTimeUtil.toUtcTimestamp((ZonedDateTime) value);
        } else if (value instanceof LocalDateTime) {
            other = DateTimeUtil.toUtcTimestamp((LocalDateTime) value);
        } else {
            return false;
        }
        return e.equals(other);
    }

    @Override
    public int compareEnd(Object value) {
        Timestamp e = getEnd();
        if (null == e) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof Date) {
            return e.compareTo((Date) value);
        }

        if (value instanceof ZonedDateTime) {
            return e.compareTo(DateTimeUtil.toUtcTimestamp((ZonedDateTime) value));
        }

        if (value instanceof LocalDateTime) {
            return e.compareTo(DateTimeUtil.toUtcTimestamp((LocalDateTime) value));
        }

        throw new IllegalArgumentException();
    }

    @Override
    protected boolean verifyModified() {
        if (originalValues.type == type && title.equals(originalValues.title) && contact.equals(originalValues.contact)
                && ModelHelper.areSameRecord(customer, originalValues.customer) && ModelHelper.areSameRecord(user, originalValues.user)
                && description.equals(originalValues.description) && url.equals(originalValues.url) && Objects.equals(start, originalValues.start)
                && Objects.equals(end, originalValues.end)) {
            switch (type) {
                case CORPORATE_LOCATION:
                case PHONE:
                    return !locationSl.equals(originalValues.locationSl);
                default:
                    return !location.equals(originalValues.location);
            }
        }
        return true;
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.customer = customer;
        originalValues.user = user;
        originalValues.title = title;
        originalValues.description = description;
        originalValues.location = location;
        originalValues.locationSl = locationSl;
        originalValues.contact = contact;
        originalValues.type = type;
        originalValues.url = url;
        originalValues.start = start;
        originalValues.end = end;
    }

    @Override
    protected void onRejectChanges() {
        PartialCustomerDAO oldCustomer = customer;
        PartialUserDAO oldUser = user;
        String oldTitle = title;
        String oldDescription = description;
        String oldLocation = getLocation();
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
        locationSl = originalValues.locationSl;
        contact = originalValues.contact;
        type = originalValues.type;
        url = originalValues.url;
        start = originalValues.start;
        end = originalValues.end;
        firePropertyChange(PROP_CUSTOMER, oldCustomer, customer);
        firePropertyChange(PROP_USER, oldUser, user);
        firePropertyChange(PROP_TITLE, oldTitle, title);
        firePropertyChange(PROP_DESCRIPTION, oldDescription, description);
        firePropertyChange(PROP_LOCATION, oldLocation, getLocation());
        firePropertyChange(PROP_CUSTOMER, oldContact, contact);
        firePropertyChange(PROP_TYPE, oldType, type);
        firePropertyChange(PROP_URL, oldUrl, url);
        firePropertyChange(PROP_START, oldStart, start);
        firePropertyChange(PROP_END, oldEnd, end);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        EventDispatchChain result = AppointmentAlertManager.INSTANCE.buildEventDispatchChain(FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail)));
        LOG.exiting(LOG.getName(), "buildEventDispatchChain");
        return result;
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
        hash = 97 * hash + Objects.hashCode(getLocation());
        hash = 97 * hash + Objects.hashCode(contact);
        hash = 97 * hash + Objects.hashCode(type);
        hash = 97 * hash + Objects.hashCode(url);
        hash = 97 * hash + Objects.hashCode(start);
        hash = 97 * hash + Objects.hashCode(end);
        return hash;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return null != obj && obj.getClass().isAssignableFrom(AppointmentDAO.class) && ModelHelper.areSameRecord(this, (Appointment<Timestamp>) obj);
    }

    @Override
    public String toString() {
        return ModelHelper.AppointmentHelper.appendDaoProperties(this, new StringBuilder(AppointmentDAO.class.getName()).append(" {"))
                .append(Values.LINEBREAK_STRING).append("}").toString();
    }

    private void onCustomerUpdated(CustomerModel newCustomerModel) {
        if (null == customer) {
            return;
        }
        CustomerDAO newDao = newCustomerModel.dataObject();
        if (customer == newDao || customer.getPrimaryKey() != newDao.getPrimaryKey()) {
            return;
        }
        PartialCustomerDAO oldCustomer = customer;
        customer = newDao;
        firePropertyChange(PROP_CUSTOMER, oldCustomer, customer);

        AppointmentModel appointmentModel = cachedModel(false);
        if (null != appointmentModel) {
            PartialCustomerModel<? extends PartialCustomerDAO> oldCustomerModel = appointmentModel.getCustomer();
            if (!Objects.equals(newCustomerModel, oldCustomerModel)) {
                appointmentModel.setCustomer(newCustomerModel);
            }
        }
    }

    private void onUserUpdated(UserModel newModel) {
        if (null == user) {
            return;
        }
        UserDAO newDao = newModel.dataObject();
        if (user == newDao || user.getPrimaryKey() != newDao.getPrimaryKey()) {
            return;
        }
        PartialUserDAO oldUser = user;
        user = newDao;
        firePropertyChange(PROP_USER, oldUser, user);

        AppointmentModel appointmentModel = cachedModel(false);
        if (null != appointmentModel) {
            PartialUserModel<? extends PartialUserDAO> oldModel = appointmentModel.getUser();
            if (null != oldModel && oldModel != newModel) {
                appointmentModel.setUser(newModel);
            }
        }
    }

    /**
     * Factory implementation for {@link AppointmentDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<AppointmentDAO, AppointmentModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        void onBeforeSave(AppointmentModel model) {
            AppointmentDAO dao = model.dataObject();
            dao.setType(model.getType());
            dao.setTitle(model.getTitle());
            PartialCustomerModel<? extends PartialCustomerDAO> c = model.getCustomer();
            if (null != c) {
                if (c instanceof CustomerModel) {
                    CustomerDAO.FACTORY.onBeforeSave((CustomerModel) c);
                }
                dao.setCustomer(c.dataObject());
            } else {
                dao.setCustomer(null);
            }
            PartialUserModel<? extends PartialUserDAO> u = model.getUser();
            if (null != u) {
                if (u instanceof UserModel) {
                    UserDAO.FACTORY.onBeforeSave((UserModel) u);
                }
                dao.setUser(u.dataObject());
            } else {
                dao.setUser(null);
            }
            dao.setContact(model.getContact());
            dao.setLocation(model.getLocation());
            dao.setUrl(model.getUrl());
            dao.setStart(DateTimeUtil.toUtcTimestamp(model.getStart()));
            dao.setEnd(DateTimeUtil.toUtcTimestamp(model.getEnd()));
            dao.setDescription(model.getDescription());
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
                    ps.setString(index, dao.getLocation());
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
                private final String oldLocation = dao.getLocation();
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
                    t.firePropertyChange(PROP_LOCATION, oldLocation, dao.getLocation());
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
            dao.type = AppointmentType.of(rs.getString(DbColumn.TYPE.toString()), dao.type);
            switch (dao.type) {
                case CORPORATE_LOCATION:
                case PHONE:
                    dao.location = dao.locationSl = asNonNullAndWsNormalized(rs.getString(DbColumn.LOCATION.toString()));
                    break;
                default:
                    dao.locationSl = null;
                    dao.location = asNonNullAndWsNormalizedMultiLine(rs.getString(DbColumn.LOCATION.toString()));
                    break;
            }
            dao.contact = asNonNullAndTrimmed(rs.getString(DbColumn.CONTACT.toString()));
            dao.url = asNonNullAndTrimmed(rs.getString(DbColumn.URL.toString()));
            dao.start = rs.getTimestamp(DbColumn.START.toString());
            if (rs.wasNull()) {
                dao.end = rs.getTimestamp(DbColumn.END.toString());
                if (rs.wasNull()) {
                    dao.start = dao.end = DateTimeUtil.toUtcTimestamp(LocalDateTime.now());
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

        public Optional<AppointmentDAO> nextOnOrAfter(Connection connection, Timestamp start) throws SQLException {
            LOG.entering(LOG.getName(), "nextOnOrAfter", new Object[]{connection, start});
            Objects.requireNonNull(connection, "Connection cannot be null");
            String sql = createDmlSelectQueryBuilder().build().append(" WHERE ").append(DbTable.APPOINTMENT).append(".").append(DbColumn.END).append(">?").toString();
            Optional<AppointmentDAO> result;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setTimestamp(1, start);
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result = Optional.of(fromResultSet(rs));
                        LogHelper.logWarnings(connection, LOG);
                        LOG.exiting(LOG.getName(), "nextOnOrAfter", result);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            result = Optional.empty();
            LOG.exiting(LOG.getName(), "nextOnOrAfter", result);
            return result;
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
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(end));
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
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.APPOINTMENT_ID.getDbName()).append(") AS c, ").append(DbColumn.TYPE.getDbName())
                    .append(" FROM ").append(DbTable.APPOINTMENT.getDbName());
            if (null != start) {
                sb.append(" WHERE ").append(DbColumn.END.getDbName()).append(" > ?");
                if (null != end) {
                    sb.append(" AND ").append(DbColumn.START.getDbName()).append(" < ?");
                }
            } else if (null != end) {
                sb.append(" WHERE ").append(DbColumn.START.getDbName()).append(" < ?");
            }
            sb.append(" GROUP BY ").append(DbColumn.TYPE.getDbName());
            String sql = sb.toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int index = 0;
                if (null != start) {
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(end));
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
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.APPOINTMENT_ID.getDbName()).append(") AS r, n.")
                    .append(DbColumn.COUNTRY_NAME.getDbName()).append(" AS ").append(DbColumn.COUNTRY_NAME.getDbName())
                    .append(" FROM ").append(DbTable.APPOINTMENT.getDbName())
                    .append(" a LEFT JOIN ").append(DbTable.CUSTOMER.getDbName()).append(" c ON a.").append(DbColumn.CUSTOMER_ID.getDbName()).append("=c.")
                    .append(DbColumn.CUSTOMER_ID.getDbName()).append(" LEFT JOIN ").append(DbTable.ADDRESS.getDbName())
                    .append(" l ON c.").append(DbColumn.ADDRESS_ID.getDbName()).append("=l.").append(DbColumn.ADDRESS_ID.getDbName())
                    .append(" LEFT JOIN ").append(DbTable.CITY.getDbName())
                    .append(" t ON l.").append(DbColumn.CITY_ID.getDbName()).append("=t.").append(DbColumn.CITY_ID.getDbName())
                    .append(" LEFT JOIN ").append(DbTable.COUNTRY.getDbName())
                    .append(" n on t.").append(DbColumn.COUNTRY_ID.getDbName()).append("=n.").append(DbColumn.COUNTRY_ID.getDbName());
            if (null != start) {
                sb.append(" WHERE ").append(DbColumn.END.getDbName()).append(" > ?");
                if (null != end) {
                    sb.append(" AND ").append(DbColumn.START.getDbName()).append(" <= ?");
                }
            } else if (null != end) {
                sb.append(" WHERE ").append(DbColumn.START.getDbName()).append(" <= ?");
            }
            String sql = sb.append(" GROUP BY ").append(DbColumn.COUNTRY_NAME.getDbName()).toString();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int index = 0;
                if (null != start) {
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(end));
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    ArrayList<ItemCountResult<String>> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(new ItemCountResult<>(rs.getString(DbColumn.COUNTRY_NAME.toString()), rs.getInt("r")));
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
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(end));
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
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(start));
                }
                if (null != end) {
                    ps.setTimestamp(++index, DateTimeUtil.toUtcTimestamp(end));
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
            PartialCustomerDAO oldCustomer = toDAO.customer;
            String oldDescription = toDAO.description;
            Timestamp oldEnd = toDAO.end;
            String oldLocation = toDAO.getLocation();
            Timestamp oldStart = toDAO.start;
            String oldTitle = toDAO.title;
            AppointmentType oldType = toDAO.type;
            String oldUrl = toDAO.url;
            PartialUserDAO oldUser = toDAO.user;
            toDAO.contact = fromDAO.contact;
            toDAO.customer = fromDAO.customer;
            toDAO.description = fromDAO.description;
            toDAO.end = fromDAO.end;
            toDAO.location = fromDAO.location;
            toDAO.locationSl = fromDAO.locationSl;
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
            toDAO.firePropertyChange(PROP_LOCATION, oldLocation, toDAO.getLocation());
            toDAO.firePropertyChange(PROP_CONTACT, oldContact, toDAO.contact);
            toDAO.firePropertyChange(PROP_TYPE, oldType, (null == toDAO.type) ? AppointmentType.OTHER : toDAO.type);
            toDAO.firePropertyChange(PROP_URL, oldUrl, toDAO.url);
            toDAO.firePropertyChange(PROP_START, oldStart, toDAO.start);
            toDAO.firePropertyChange(PROP_END, oldEnd, toDAO.end);
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
            EventDispatchChain result = AppointmentModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
            LOG.exiting(LOG.getName(), "buildEventDispatchChain");
            return result;
        }

        private void onCustomerSaved(CustomerSuccessEvent event) {
            LOG.entering(LOG.getName(), "onCustomerSaved", event);
            CustomerModel newModel = event.getEntityModel();
            streamCached().forEach((t) -> t.onCustomerUpdated(newModel));
            LOG.exiting(LOG.getName(), "onCustomerSaved");
        }

        private void onUserSaved(UserSuccessEvent event) {
            LOG.entering(LOG.getName(), "onUserSaved", event);
            UserModel newModel = event.getEntityModel();
            streamCached().forEach((t) -> t.onUserUpdated(newModel));
            LOG.exiting(LOG.getName(), "onUserSaved");
        }

    }

    public static class SaveTask extends SaveDaoTask<AppointmentDAO, AppointmentModel> {

        public SaveTask(AppointmentModel model, boolean alreadyValidated) {
            super(model, AppointmentModel.FACTORY, alreadyValidated);
        }

        @Override
        protected AppointmentEvent validate(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "validate", connection);
            AppointmentModel targetModel = getEntityModel();
            AppointmentEvent event = AppointmentModel.FACTORY.validateForSave(targetModel);
            if (null != event && event instanceof AppointmentFailedEvent) {
                return event;
            }

            PartialCustomerModel<? extends PartialCustomerDAO> c = targetModel.getCustomer();
            if (c instanceof CustomerModel) {
                switch (c.getRowState()) {
                    case NEW:
                    case MODIFIED:
                        CustomerDAO.SaveTask saveTask = new CustomerDAO.SaveTask((CustomerModel) c, false);
                        saveTask.run();
                        CustomerEvent customerEvent = (CustomerEvent) saveTask.get();
                        if (null != customerEvent && customerEvent instanceof CustomerFailedEvent) {
                            if (getOriginalRowState() == DataRowState.NEW) {
                                return AppointmentEvent.createInsertInvalidEvent(targetModel, this, (CustomerFailedEvent) customerEvent);
                            }
                            return AppointmentEvent.createUpdateInvalidEvent(targetModel, this, (CustomerFailedEvent) customerEvent);
                        }
                        break;
                    default:
                        break;
                }
            }
            PartialUserModel<? extends PartialUserDAO> u = targetModel.getUser();
            AppointmentEvent resultEvent;
            if (u instanceof UserModel) {
                switch (u.getRowState()) {
                    case NEW:
                    case MODIFIED:
                        UserDAO.SaveTask saveTask = new UserDAO.SaveTask((UserModel) u, false);
                        saveTask.run();
                        UserEvent userEvent = (UserEvent) saveTask.get();
                        if (null != userEvent && userEvent instanceof UserFailedEvent) {
                            if (getOriginalRowState() == DataRowState.NEW) {
                                resultEvent = AppointmentEvent.createInsertInvalidEvent(targetModel, this, (UserFailedEvent) userEvent);
                            } else {
                                resultEvent = AppointmentEvent.createUpdateInvalidEvent(targetModel, this, (UserFailedEvent) userEvent);
                            }
                        } else {
                            resultEvent = null;
                        }
                        break;
                    default:
                        resultEvent = null;
                        break;
                }
            } else {
                resultEvent = null;
            }
            LOG.exiting(LOG.getName(), "validate", resultEvent);
            return resultEvent;
        }

        @Override
        protected AppointmentEvent createSuccessEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertSuccessEvent(getEntityModel(), this);
            }
            return AppointmentEvent.createUpdateSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected AppointmentEvent createCanceledEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertCanceledEvent(getEntityModel(), this);
            }
            return AppointmentEvent.createUpdateCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected AppointmentEvent createFaultedEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertFaultedEvent(getEntityModel(), this, getException());
            }
            return AppointmentEvent.createUpdateFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            AppointmentEvent event = (AppointmentEvent) getValue();
            if (null != event && event instanceof AppointmentSuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    public static final class DeleteTask extends DeleteDaoTask<AppointmentDAO, AppointmentModel> {

        public DeleteTask(AppointmentModel target, boolean alreadyValidated) {
            super(target, AppointmentModel.FACTORY, alreadyValidated);
        }

        @Override
        protected AppointmentEvent validate(Connection connection) throws Exception {
            return null;
        }

        @Override
        protected AppointmentEvent createSuccessEvent() {
            return AppointmentEvent.createDeleteSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected AppointmentEvent createCanceledEvent() {
            return AppointmentEvent.createDeleteCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected AppointmentEvent createFaultedEvent() {
            return AppointmentEvent.createDeleteFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            AppointmentEvent event = (AppointmentEvent) getValue();
            if (null != event && event instanceof AppointmentSuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    private class OriginalValues {

        private PartialCustomerDAO customer;
        private PartialUserDAO user;
        private String title;
        private String description;
        private String location;
        private String locationSl;
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
            this.locationSl = AppointmentDAO.this.locationSl;
            this.contact = AppointmentDAO.this.contact;
            this.type = AppointmentDAO.this.type;
            this.url = AppointmentDAO.this.url;
            this.start = AppointmentDAO.this.start;
            this.end = AppointmentDAO.this.end;
        }
    }

}
