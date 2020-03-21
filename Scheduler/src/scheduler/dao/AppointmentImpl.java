package scheduler.dao;

import scheduler.dao.schema.DbTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.appointment.AppointmentModel;

public class AppointmentImpl extends DataObjectImpl implements Appointment, AppointmentColumns {

    @Deprecated
    private static final String BASE_SELECT_QUERY = String.format("SELECT %1$s.%2$s as %2$s, %1$s.%3$s as %3$s, %1$s.%4$s as %4$s, %1$s.%5$s as %5$s,"
            + " %1$s.%6$s as %6$s, %1$s.%7$s as %7$s, %1$s.%8$s as %8$s, %1$s.%9$s as %9$s, %1$s.%10$s as %10$s, %1$s%11$s, %12$s FROM %13$s %1$s"
            + " LEFT JOIN %14$s %15$s on %1$s.%16$s = %15$s.%16$s LEFT JOIN %17$s %18$s on %1$s.%19$s = %18$s.%19$s %20$s",
            TABLEALIAS_APPOINTMENT, COLNAME_APPOINTMENTID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION, COLNAME_CONTACT, COLNAME_TYPE,
            COLNAME_URL, COLNAME_START, COLNAME_END, SQL_CUSTOMER_SELECT_FIELDS, DataObjectColumns.getDataObjectSelectFields(TABLEALIAS_APPOINTMENT),
            TABLENAME_APPOINTMENT, TABLENAME_USER, TABLEALIAS_USER, COLNAME_USERID, TABLENAME_CUSTOMER, TABLEALIAS_CUSTOMER, COLNAME_CUSTOMERID,
            SQL_JOIN_ADDRESS);
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
     * Initializes a {@link Values#ROWSTATE_NEW} appointment object.
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

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected AppointmentImpl fromResultSet(ResultSet resultSet) throws SQLException {
            AppointmentImpl r = new AppointmentImpl();
            initializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseSelectQuery() {
            return BASE_SELECT_QUERY;
        }

        @Override
        public Class<? extends AppointmentImpl> getDaoClass() {
            return AppointmentImpl.class;
        }

        @Override
        public DbTable getTableName() {
            return DbTable.APPOINTMENT;
        }

        @Override
        public String getTableName_old() {
            return TABLENAME_APPOINTMENT;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_APPOINTMENTID;
        }

        @Override
        protected List<String> getExtendedColNames() {
            return Arrays.asList(COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION,
                    COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL, COLNAME_START, COLNAME_END);
        }

        @Override
        protected void setSaveStatementValues(AppointmentImpl dao, PreparedStatement ps) throws SQLException {
            ps.setInt(1, dao.getCustomer().getPrimaryKey());
            ps.setInt(2, dao.getUser().getPrimaryKey());
            ps.setString(3, dao.getTitle());
            ps.setString(4, dao.getDescription());
            ps.setString(5, dao.getLocation());
            ps.setString(6, dao.getContact());
            ps.setString(7, dao.getType().getDbValue());
            ps.setString(8, dao.getUrl());
            ps.setTimestamp(9, dao.getStart());
            ps.setTimestamp(10, dao.getEnd());
        }

        @Override
        protected void onInitializeDao(AppointmentImpl target, ResultSet resultSet) throws SQLException {
            target.customer = DataObjectReference.of(Customer.of(resultSet, COLNAME_CUSTOMERID));
            target.user = DataObjectReference.of(User.of(resultSet, COLNAME_USERID));
            target.title = resultSet.getString(COLNAME_TITLE);
            if (resultSet.wasNull()) {
                target.title = "";
            }
            target.description = resultSet.getString(COLNAME_DESCRIPTION);
            if (resultSet.wasNull()) {
                target.description = "";
            }
            target.location = resultSet.getString(COLNAME_LOCATION);
            if (resultSet.wasNull()) {
                target.location = "";
            }
            target.contact = resultSet.getString(COLNAME_CONTACT);
            if (resultSet.wasNull()) {
                target.contact = "";
            }
            target.type = AppointmentType.of(resultSet.getString(COLNAME_TYPE), AppointmentType.OTHER);
            if (resultSet.wasNull()) {
                target.type = AppointmentType.OTHER;
            }
            target.url = resultSet.getString(COLNAME_URL);
            if (resultSet.wasNull()) {
                target.url = "";
            }
            target.start = resultSet.getTimestamp(COLNAME_START);
            if (resultSet.wasNull()) {
                target.end = resultSet.getTimestamp(COLNAME_END);
                if (resultSet.wasNull()) {
                    target.end = DB.toUtcTimestamp(LocalDateTime.now());
                }
                target.start = target.end;
            } else {
                target.end = resultSet.getTimestamp(COLNAME_END);
                if (resultSet.wasNull()) {
                    target.end = target.start;
                }
            }
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
