/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import scheduler.App;
import scheduler.util.DB;
import scheduler.util.Values;

public class AppointmentImpl extends DataObjectImpl implements Appointment {

    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    //<editor-fold defaultstate="collapsed" desc="Column names">
    /**
     * The name of the 'appointmentId' column in the 'appointment' table, which is also the primary key.
     */
    public static final String COLNAME_APPOINTMENTID = "appointmentId";

    /**
     * The name of the 'customerId' column in the 'appointment' table.
     */
    public static final String COLNAME_CUSTOMERID = "customerId";

    /**
     * The name of the 'userId' column in the 'appointment' table.
     */
    public static final String COLNAME_USERID = "userId";

    /**
     * The name of the 'title' column in the 'appointment' table.
     */
    public static final String COLNAME_TITLE = "title";

    /**
     * The name of the 'description' column in the 'appointment' table.
     */
    public static final String COLNAME_DESCRIPTION = "description";

    /**
     * The name of the 'location' column in the 'appointment' table.
     */
    public static final String COLNAME_LOCATION = "location";

    /**
     * The name of the 'contact' column in the 'appointment' table.
     */
    public static final String COLNAME_CONTACT = "contact";

    /**
     * The name of the 'type' column in the 'appointment' table.
     */
    public static final String COLNAME_TYPE = "type";

    /**
     * The name of the 'url' column in the 'appointment' table.
     */
    public static final String COLNAME_URL = "url";

    /**
     * The name of the 'start' column in the 'appointment' table.
     */
    public static final String COLNAME_START = "start";

    /**
     * The name of the 'end' column in the 'appointment' table.
     */
    public static final String COLNAME_END = "end";

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="customer property">
    private DataObjectReference<CustomerImpl, Customer> customer;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="user property">
    private DataObjectReference<UserImpl, User> user;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="title property">
    private String title;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="description property">
    private String description;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="location property">
    private String location;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="contact property">
    private String contact;

    /**
     * {@inheritDoc}
     */
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
        Objects.requireNonNull(value);
        contact = value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="type property">
    private String type;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="url property">
    private String url;

    /**
     * {@inheritDoc}
     */
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
        Objects.requireNonNull(value);
        url = value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="start property">
    private Timestamp start;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="end property">
    private Timestamp end;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    private static String baseSelectQuery = null;

    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery) {
            return baseSelectQuery;
        }
        final StringBuilder sql = new StringBuilder("SELECT e.`");
        sql.append(COLNAME_APPOINTMENTID).append("` AS `").append(COLNAME_APPOINTMENTID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY, COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION,
                COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL, COLNAME_START, COLNAME_END).forEach((t) -> {
                    sql.append("`, e.`").append(t).append("` AS `").append(t);
                });
        sql.append("`, p.`").append(CustomerImpl.COLNAME_CUSTOMERNAME).append("` AS `").append(CustomerImpl.COLNAME_CUSTOMERNAME)
                .append("`, p.`").append(CustomerImpl.COLNAME_ADDRESSID).append("` AS `").append(CustomerImpl.COLNAME_ADDRESSID);
        Stream.of(AddressImpl.COLNAME_ADDRESS, AddressImpl.COLNAME_ADDRESS2, AddressImpl.COLNAME_CITYID, AddressImpl.COLNAME_POSTALCODE,
                AddressImpl.COLNAME_PHONE).forEach((t) -> {
                    sql.append("`, a.`").append(t).append("` AS `").append(t);
                });
        baseSelectQuery = sql.append("`, u.`").append(UserImpl.COLNAME_USERID).append("` AS `").append(UserImpl.COLNAME_USERID)
                .append("`, c.`").append(CityImpl.COLNAME_CITY).append("` AS `").append(CityImpl.COLNAME_CITY)
                .append("`, c.`").append(CityImpl.COLNAME_COUNTRYID).append("` AS `").append(CityImpl.COLNAME_COUNTRYID)
                .append("`, n.`").append(CountryImpl.COLNAME_COUNTRY).append("` AS `").append(CountryImpl.COLNAME_COUNTRY)
                .append("` FROM `").append((new AppointmentImpl.FactoryImpl()).getTableName())
                .append("` e LEFT JOIN `").append(CustomerImpl.getFactory().getTableName()).append("` p ON e.`").append(COLNAME_CUSTOMERID).append("`=p.`").append(CustomerImpl.COLNAME_CUSTOMERID)
                .append("` LEFT JOIN `").append(AddressImpl.getFactory().getTableName()).append("` a ON p.`").append(CustomerImpl.COLNAME_ADDRESSID).append("`=a.`").append(AddressImpl.COLNAME_ADDRESSID)
                .append("` LEFT JOIN `").append(CityImpl.getFactory().getTableName()).append("` c ON a.`").append(AddressImpl.COLNAME_CITYID).append("`=c.`").append(CityImpl.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(CountryImpl.getFactory().getTableName()).append("` n ON c.`").append(CityImpl.COLNAME_COUNTRYID).append("`=n.`").append(CountryImpl.COLNAME_COUNTRYID)
                .append("` LEFT JOIN `").append(UserImpl.getFactory().getTableName()).append("` u ON e.`").append(COLNAME_USERID).append("`=u.`").append(UserImpl.COLNAME_USERID).toString();
        return baseSelectQuery;
    }

    //</editor-fold>
    //</editor-fold>
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} appointment object.
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

    private static final FactoryImpl FACTORY = new FactoryImpl();
    
    public static FactoryImpl getFactory() { return FACTORY; }
    
    public static final class FactoryImpl extends DataObjectImpl.Factory<AppointmentImpl> {

        // This is a singleton instance
        private FactoryImpl() { }
        
        public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId, int customerId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId, Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
        public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
        public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadCustomerTodayAndFuture(Connection connection, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
        public ArrayList<AppointmentImpl> loadCustomerTodayAndFuture(Connection connection, int customerId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId, int customerId,
        //            Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
        public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId, int customerId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId, Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
        public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
        public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadCustomerYesterdayAndPast(Connection connection, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
        public ArrayList<AppointmentImpl> loadCustomerYesterdayAndPast(Connection connection, int customerId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        //    public ArrayList<AppointmentImpl> loadByCustomer(Connection connection, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        //        throw new UnsupportedOperationException("Not implemented");
        //    }
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

        //    @Override
        //    protected void onApplyChanges(AppointmentModel model) {
        //        AppointmentImpl dao = model.getDataObject();
        //        dao.contact = model.getContact();
        //        AppointmentCustomer<?> customer = model.getCustomer();
        //        dao.customer = (null == customer) ? null : customer.getDataObject();
        //        dao.description = model.getDescription();
        //        dao.end = (null == model.getEnd()) ? null : DB.toUtcTimestamp(model.getEnd());
        //        dao.location = model.getLocation();
        //        dao.start = (null == model.getStart()) ? null : DB.toUtcTimestamp(model.getStart());
        //        dao.title = model.getTitle();
        //        dao.type = model.getType();
        //        dao.url = model.getUrl();
        //        AppointmentUser<?> user = model.getUser();
        //        dao.user = (null == user) ? null : user.getDataObject();
        //    }
        @Override
        protected AppointmentImpl fromResultSet(ResultSet resultSet) throws SQLException {
            AppointmentImpl r = new AppointmentImpl();
            onInitializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseQuery() {
            return getBaseSelectQuery();
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
        protected Stream<String> getExtendedColNames() {
            return Stream.of(COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION,
                    COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL, COLNAME_START, COLNAME_END);
        }

        @Override
        protected void setStatementValues(AppointmentImpl dao, PreparedStatement ps) throws SQLException {
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
            target.customer = DataObjectReference.of(Customer.of(resultSet, AppointmentImpl.COLNAME_CUSTOMERID));
            target.user = DataObjectReference.of(User.of(resultSet, AppointmentImpl.COLNAME_USERID));
            target.title = resultSet.getString(AppointmentImpl.COLNAME_TITLE);
            if (resultSet.wasNull()) {
                target.title = "";
            }
            target.description = resultSet.getString(AppointmentImpl.COLNAME_DESCRIPTION);
            if (resultSet.wasNull()) {
                target.description = "";
            }
            target.location = resultSet.getString(AppointmentImpl.COLNAME_LOCATION);
            if (resultSet.wasNull()) {
                target.location = "";
            }
            target.contact = resultSet.getString(AppointmentImpl.COLNAME_CONTACT);
            if (resultSet.wasNull()) {
                target.contact = "";
            }
            target.type = resultSet.getString(AppointmentImpl.COLNAME_TYPE);
            if (resultSet.wasNull()) {
                target.type = Values.APPOINTMENTTYPE_OTHER;
            } else {
                target.type = Values.asValidAppointmentType(target.type);
            }
            target.url = resultSet.getString(AppointmentImpl.COLNAME_URL);
            if (resultSet.wasNull()) {
                target.url = "";
            }
            target.start = resultSet.getTimestamp(AppointmentImpl.COLNAME_START);
            if (resultSet.wasNull()) {
                target.end = resultSet.getTimestamp(AppointmentImpl.COLNAME_END);
                if (resultSet.wasNull()) {
                    target.end = DB.toUtcTimestamp(LocalDateTime.now());
                }
                target.start = target.end;
            } else {
                target.end = resultSet.getTimestamp(AppointmentImpl.COLNAME_END);
                if (resultSet.wasNull()) {
                    target.end = target.start;
                }
            }
        }

    }

}
