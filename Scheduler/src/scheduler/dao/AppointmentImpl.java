package scheduler.dao;

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

public class AppointmentImpl extends DataObjectImpl implements Appointment, AppointmentColumns, UserColumns {

    private static final String BASE_SELECT_QUERY = String.format("SELECT %s.%s as %s, %s.%s as %s, %s.%s as %s, %s.%s as %s, %s.%s as %s, "
            + "%s.%s as %s, %s.%s as %s, %s.%s as %s, %s.%s as %s, %s%s, %s.%s as %s, %s.%s as %s, %s.%s as %s, %s%s, %s FROM %s %s "
            + "LEFT JOIN %s %s on %s.%s = %s.%s LEFT JOIN %s %s on %s.%s = %s.%s %s",
            TABLEALIAS_APPOINTMENT, COLNAME_APPOINTMENTID, COLNAME_APPOINTMENTID, TABLEALIAS_APPOINTMENT, COLNAME_TITLE, COLNAME_TITLE,
            TABLEALIAS_APPOINTMENT, COLNAME_DESCRIPTION, COLNAME_DESCRIPTION, TABLEALIAS_APPOINTMENT, COLNAME_LOCATION, COLNAME_LOCATION,
            TABLEALIAS_APPOINTMENT, COLNAME_CONTACT, COLNAME_CONTACT, TABLEALIAS_APPOINTMENT, COLNAME_TYPE, COLNAME_TYPE,
            TABLEALIAS_APPOINTMENT, COLNAME_URL, COLNAME_URL, TABLEALIAS_APPOINTMENT, COLNAME_START, COLNAME_START,
            TABLEALIAS_APPOINTMENT, COLNAME_END, COLNAME_END, TABLEALIAS_APPOINTMENT, SQL_CUSTOMER_SELECT_FIELDS,
            TABLEALIAS_APPOINTMENT, COLNAME_USERID, COLNAME_USERID, TABLEALIAS_USER, COLNAME_USERNAME, COLNAME_USERNAME,
            TABLEALIAS_USER, COLNAME_ACTIVE_STATUS, COLALIAS_ACTIVE_STATUS, TABLEALIAS_APPOINTMENT, SQL_CUSTOMER_SELECT_FIELDS,
            DataObjectColumns.getDataObjectSelectFields(TABLEALIAS_APPOINTMENT),
            TABLENAME_APPOINTMENT, TABLEALIAS_APPOINTMENT, TABLENAME_USER, TABLEALIAS_USER, TABLEALIAS_APPOINTMENT, COLNAME_USERID,
            TABLEALIAS_USER, COLNAME_USERID, TABLENAME_CUSTOMER, TABLEALIAS_CUSTOMER, TABLEALIAS_APPOINTMENT, COLNAME_CUSTOMERID,
            TABLEALIAS_CUSTOMER, COLNAME_CUSTOMERID, SQL_JOIN_ADDRESS);
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
    private String type;
    private String url;
    private Timestamp start;
    private Timestamp end;

    /**
     * Initializes a {@link Values#ROWSTATE_NEW} appointment object.
     */
    public AppointmentImpl() {
        customer = null;
        user = null;
        title = "";
        description = "";
        location = "";
        contact = "";
        type = Values.APPOINTMENTTYPE_OTHER;
        url = null;
        LocalDateTime d = LocalDateTime.now().plusHours(1).plusMinutes(30);
        d = d.minusMinutes(d.getMinute()).minusSeconds(d.getSecond()).minusNanos(d.getNano());
        start = DB.toUtcTimestamp(d);
        end = DB.toUtcTimestamp(d.plusHours(1));
    }

    @Override
    public DataObjectReference<CustomerImpl, Customer> getCustomer() {
        return customer;
    }

    /**
     * Set the value of customer
     *
     * @param value new value of customer
     */
    public void setCustomer(DataObjectReference<CustomerImpl, Customer> value) {
        Objects.requireNonNull(value);
        customer = value;
    }

    @Override
    public DataObjectReference<UserImpl, User> getUser() {
        return user;
    }

    /**
     * Set the value of user
     *
     * @param value new value of user
     */
    public void setUser(DataObjectReference<UserImpl, User> value) {
        Objects.requireNonNull(value);
        user = value;
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
    public String getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param value new value of type
     */
    public void setType(String value) {
        type = Values.asValidAppointmentType(value);
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
        public String getTableName() {
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
            ps.setString(7, dao.getType());
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
            target.type = resultSet.getString(COLNAME_TYPE);
            if (resultSet.wasNull()) {
                target.type = Values.APPOINTMENTTYPE_OTHER;
            } else {
                target.type = Values.asValidAppointmentType(target.type);
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
