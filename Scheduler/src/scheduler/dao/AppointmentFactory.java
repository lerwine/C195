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
import javafx.collections.ObservableSet;
import scheduler.App;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.appointment.AppointmentModel;

/**
 *
 * @author erwinel
 */
public class AppointmentFactory extends DataObjectFactory<AppointmentFactory.AppointmentImpl, AppointmentModel> {
    
    private static final ObservableMap<String, String> APPOINTMENT_TYPES = FXCollections.observableHashMap();
    private static ObservableMap<String, String> appointmentTypes = null;
    private static String appointmentTypesLocale = null;
    public static ObservableMap<String, String> getAppointmentTypes() {
        synchronized(APPOINTMENT_TYPES) {
            if (null == appointmentTypes)
                appointmentTypes = FXCollections.unmodifiableObservableMap(APPOINTMENT_TYPES);
            else if (null != appointmentTypesLocale && appointmentTypesLocale.equals(Locale.getDefault(Locale.Category.DISPLAY).toLanguageTag()))
                return appointmentTypes;
            Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
            appointmentTypesLocale = locale.toLanguageTag();
            ResourceBundle rb = App.getResources();
            Stream.of(Values.APPOINTMENTTYPE_PHONE, Values.APPOINTMENTTYPE_VIRTUAL, Values.APPOINTMENTTYPE_CUSTOMER, Values.APPOINTMENTTYPE_HOME,
                    Values.APPOINTMENTTYPE_GERMANY, Values.APPOINTMENTTYPE_INDIA, Values.APPOINTMENTTYPE_HONDURAS,
                    Values.APPOINTMENTTYPE_OTHER).forEach((String key) -> {
                        APPOINTMENT_TYPES.put(key, (rb.containsKey(key)) ? rb.getString(key) : key);
                });
        }
        return appointmentTypes;
    }
    
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
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT e.`");
        sql.append(COLNAME_APPOINTMENTID).append("` AS `").append(COLNAME_APPOINTMENTID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY, COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION,
                COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL, COLNAME_START, COLNAME_END).forEach((t) -> {
            sql.append("`, e.`").append(t).append("` AS `").append(t);
        });
        sql.append("`, p.`").append(CustomerFactory.COLNAME_CUSTOMERNAME).append("` AS `").append(CustomerFactory.COLNAME_CUSTOMERNAME)
                .append("`, p.`").append(CustomerFactory.COLNAME_ADDRESSID).append("` AS `").append(CustomerFactory.COLNAME_ADDRESSID);
        Stream.of(AddressFactory.COLNAME_ADDRESS, AddressFactory.COLNAME_ADDRESS2, AddressFactory.COLNAME_CITYID, AddressFactory.COLNAME_POSTALCODE,
                AddressFactory.COLNAME_PHONE).forEach((t) -> {
            sql.append("`, a.`").append(t).append("` AS `").append(t);
        });
        baseSelectQuery = sql.append("`, u.`").append(UserFactory.COLNAME_USERID).append("` AS `").append(UserFactory.COLNAME_USERID)
                .append("`, c.`").append(CityFactory.COLNAME_CITY).append("` AS `").append(CityFactory.COLNAME_CITY)
                .append("`, c.`").append(CityFactory.COLNAME_COUNTRYID).append("` AS `").append(CityFactory.COLNAME_COUNTRYID)
                .append("`, n.`").append(CountryFactory.COLNAME_COUNTRY).append("` AS `").append(CountryFactory.COLNAME_COUNTRY)
                .append("` FROM `").append((new AppointmentFactory()).getTableName())
                .append("` e LEFT JOIN `").append((new CustomerFactory()).getTableName()).append("` p ON e.`").append(COLNAME_CUSTOMERID).append("`=p.`").append(CustomerFactory.COLNAME_CUSTOMERID)
                .append("` LEFT JOIN `").append((new AddressFactory()).getTableName()).append("` a ON p.`").append(CustomerFactory.COLNAME_ADDRESSID).append("`=a.`").append(AddressFactory.COLNAME_ADDRESSID)
                .append("` LEFT JOIN `").append((new CityFactory()).getTableName()).append("` c ON a.`").append(AddressFactory.COLNAME_CITYID).append("`=c.`").append(CityFactory.COLNAME_CITYID)
                .append("` LEFT JOIN `").append((new CountryFactory()).getTableName()).append("` n ON c.`").append(CityFactory.COLNAME_COUNTRYID).append("`=n.`").append(CountryFactory.COLNAME_COUNTRYID)
                .append("` LEFT JOIN `").append((new UserFactory()).getTableName()).append("` u ON e.`").append(COLNAME_USERID).append("`=u.`").append(UserFactory.COLNAME_USERID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(CustomerFactory.COLNAME_CUSTOMERNAME,
                    UserFactory.COLNAME_USERNAME, COLNAME_TITLE, COLNAME_CONTACT, COLNAME_TYPE, COLNAME_START, COLNAME_END,
                    COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE,
                    COLNAME_CREATEDBY));
        return sortOptions;
    }

    //</editor-fold>
    
//    public ArrayList<AppointmentImpl> load(Connection connection, int userId, int customerId, Iterable<OrderBy> orderBy) throws Exception {
//        throw new UnsupportedOperationException("Not implemented");
//    }
//    
//    public ArrayList<AppointmentImpl> load(Connection connection, int userId, Iterable<OrderBy> orderBy) throws Exception {
//        throw new UnsupportedOperationException("Not implemented");
//    }
//    
//    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId, int customerId, Iterable<OrderBy> orderBy) throws Exception {
//        throw new UnsupportedOperationException("Not implemented");
//    }
    
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
    
    @Override
    protected AppointmentImpl fromResultSet(ResultSet resultSet) throws SQLException {
        AppointmentImpl r = new AppointmentImpl();
        initializeDao(r, resultSet);
        return r;
    }

    @Override
    public AppointmentModel fromDataAccessObject(AppointmentImpl dao) { return (dao == null) ? null : new AppointmentModel(dao); }

    @Override
    public String getBaseQuery() { return getBaseSelectQuery(); }

    @Override
    public Class<? extends AppointmentImpl> getDaoClass() { return AppointmentImpl.class; }

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
    protected void initializeDao(AppointmentImpl target, ResultSet resultSet) throws SQLException {
        super.initializeDao(target, resultSet);
           target.customer = Customer.of(resultSet, AppointmentFactory.COLNAME_CUSTOMERID);
           target.user = User.of(resultSet, AppointmentFactory.COLNAME_USERID);
           target.title = resultSet.getString(AppointmentFactory.COLNAME_TITLE);
           if (resultSet.wasNull())
               target.title = "";
           target.description = resultSet.getString(AppointmentFactory.COLNAME_DESCRIPTION);
           if (resultSet.wasNull())
               target.description = "";
           target.location = resultSet.getString(AppointmentFactory.COLNAME_LOCATION);
           if (resultSet.wasNull())
               target.location = "";
           target.contact = resultSet.getString(AppointmentFactory.COLNAME_CONTACT);
           if (resultSet.wasNull())
               target.contact = "";
           target.type = resultSet.getString(AppointmentFactory.COLNAME_TYPE);
           if (resultSet.wasNull())
               target.type =Values.APPOINTMENTTYPE_OTHER;
           else
               target.type = Values.asValidAppointmentType(target.type);
           target.url = resultSet.getString(AppointmentFactory.COLNAME_URL);
           if (resultSet.wasNull())
               target.url = "";
           target.start = resultSet.getTimestamp(AppointmentFactory.COLNAME_START);
           if (resultSet.wasNull()) {
               target.end = resultSet.getTimestamp(AppointmentFactory.COLNAME_END);
               if (resultSet.wasNull())
                   target.end = DB.toUtcTimestamp(LocalDateTime.now());
               target.start = target.end;
           } else {
               target.end = resultSet.getTimestamp(AppointmentFactory.COLNAME_END);
               if (resultSet.wasNull())
                   target.end = target.start;
           }
    }

    @Override
    public String getTableName() { return TABLENAME_APPOINTMENT; }

    @Override
    public String getPrimaryKeyColName() { return COLNAME_APPOINTMENTID; }
    
    /**
    *
    * @author erwinel
    */
   @TableName(DataObjectFactory.TABLENAME_APPOINTMENT)
   @PrimaryKeyColumn(AppointmentFactory.COLNAME_APPOINTMENTID)
   public static final class AppointmentImpl extends DataObjectFactory.DataObjectImpl implements Appointment {
       //<editor-fold defaultstate="collapsed" desc="Properties and Fields">

       //<editor-fold defaultstate="collapsed" desc="customer property">

       private Customer customer;

       /**
        * {@inheritDoc}
        */
       @Override
       public Customer getCustomer() { return customer; }

       /**
        * Set the value of customer
        *
        * @param value new value of customer
        */
       public void setCustomer(Customer value) {
           Objects.requireNonNull(value);
           customer = value;
       }

       //</editor-fold>

       //<editor-fold defaultstate="collapsed" desc="user property">

       private User user;

       /**
        * {@inheritDoc}
        */
       @Override
       public User getUser() { return user; }

       /**
        * Set the value of user
        *
        * @param value new value of user
        */
       public void setUser(User value) {
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
       public String getTitle() { return title; }

       /**
        * Set the value of title
        *
        * @param value new value of title
        */
       public void setTitle(String value) { title = (value == null) ? "" : value; }

       //</editor-fold>

       //<editor-fold defaultstate="collapsed" desc="description property">

       private String description;

       /**
        * {@inheritDoc}
        */
       @Override
       public String getDescription() { return description; }

       /**
        * Set the value of description
        *
        * @param value new value of description
        */
       public void setDescription(String value) { description = (value == null) ? "" : value; }

       //</editor-fold>

       //<editor-fold defaultstate="collapsed" desc="location property">

       private String location;

       /**
        * {@inheritDoc}
        */
       @Override
       public String getLocation() { return location; }

       /**
        * Set the value of location
        *
        * @param value new value of location
        */
       public void setLocation(String value) { location = (value == null) ? "" : value; }

       //</editor-fold>

       //<editor-fold defaultstate="collapsed" desc="contact property">

       private String contact;

       /**
        * {@inheritDoc}
        */
       @Override
       public String getContact() { return contact; }

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
       public String getType() { return type; }

       /**
        * Set the value of type
        *
        * @param value new value of type
        */
       public void setType(String value) { type = Values.asValidAppointmentType(value); }

       //</editor-fold>

       //<editor-fold defaultstate="collapsed" desc="url property">

       private String url;

       /**
        * {@inheritDoc}
        */
       @Override
       public String getUrl() { return url; }

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
       public Timestamp getStart() { return start; }

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
       public Timestamp getEnd() { return end; }

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

       @Override
       public void saveChanges(Connection connection) throws Exception {
           (new AppointmentFactory()).save(this, connection);
       }

       @Override
       public void delete(Connection connection) throws Exception {
           (new AppointmentFactory()).delete(this, connection);
       }

   }
}
