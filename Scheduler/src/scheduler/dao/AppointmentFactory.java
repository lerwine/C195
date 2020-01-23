package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.SqlStatementBuilder;
import scheduler.filter.ValueAccessor;
import scheduler.view.ChildModel;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.customer.AppointmentCustomer;
import scheduler.view.user.AppointmentUser;

/**
 *
 * @author erwinel
 */
public class AppointmentFactory extends DataObjectFactory<AppointmentImpl, AppointmentModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Appointment type values">
    
    /**
     * The value of {@link #getType()} when the appointment is a phone-based meeting.
     * {@link #getUrl()} returns the telephone number encoded as a URL using the format "tel:+" + international_code + "-" + phone_number
     * and {@link #getLocation()} returns an empty string for this appointment type.
     */
    public static final String APPOINTMENTTYPE_PHONE = "phone";
    
    /**
     * The value of {@link #getType()} when the appointment is an online virtual meeting.
     * {@link #getUrl()} returns the internet address of the virtual meeting and {@link #getLocation()} returns an empty string for this appointment type.
     */
    public static final String APPOINTMENTTYPE_VIRTUAL = "virtual";
    
    /**
     * The value of {@link #getType()} when the appointment located at the customer address.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_CUSTOMER = "customer";
    
    /**
     * The value of {@link #getType()} when the appointment is at the home (USA) office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_HOME = "home";
    
    /**
     * The value of {@link #getType()} when the appointment is at the Germany office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_GERMANY = "germany";
    
    /**
     * The value of {@link #getType()} when the appointment is at the India office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_INDIA = "india";
    
    /**
     * The value of {@link #getType()} when the appointment is at the Honduras office.
     * {@link #getUrl()} and {@link #getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_HONDURAS = "honduras";
    
    /**
     * The value of {@link #getType()} when the appointment is at an explicit address returned by {@link #getLocation()}.
     * {@link #getUrl()} returns an empty string for this appointment type.
     */
    public static final String APPOINTMENTTYPE_OTHER = "other";
    
    public static String asValidAppointmentType(String value) {
        if (value != null) {
            if ((value = value.trim()).equalsIgnoreCase(APPOINTMENTTYPE_CUSTOMER))
                return APPOINTMENTTYPE_CUSTOMER;
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_GERMANY))
                return APPOINTMENTTYPE_GERMANY;
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_HOME))
                return APPOINTMENTTYPE_HOME;
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_HONDURAS))
                return APPOINTMENTTYPE_HONDURAS;
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_INDIA))
                return APPOINTMENTTYPE_INDIA;
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_PHONE))
                return APPOINTMENTTYPE_PHONE;
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_VIRTUAL))
                return APPOINTMENTTYPE_VIRTUAL;
        }
        return APPOINTMENTTYPE_OTHER;
    }
    
    //</editor-fold>
    
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
        sql.append(COLNAME_APPOINTMENTID).append("` AS ").append(COLNAME_APPOINTMENTID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY, COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION,
                COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL, COLNAME_START, COLNAME_END).forEach((t) -> {
            sql.append(", e.`").append(t).append("` AS ").append(t);
        });
        sql.append(", p.`").append(CustomerFactory.COLNAME_CUSTOMERNAME).append("` AS ").append(CustomerFactory.COLNAME_CUSTOMERNAME)
                .append(", p.`").append(CustomerFactory.COLNAME_ADDRESSID).append("` AS ").append(CustomerFactory.COLNAME_ADDRESSID);
        Stream.of(AddressFactory.COLNAME_ADDRESS, AddressFactory.COLNAME_ADDRESS2, AddressFactory.COLNAME_CITYID, AddressFactory.COLNAME_POSTALCODE,
                AddressFactory.COLNAME_PHONE).forEach((t) -> {
            sql.append(", a.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", u.`").append(UserFactory.COLNAME_USERID).append("` AS ").append(UserFactory.COLNAME_USERID)
                .append(", c.`").append(CityFactory.COLNAME_CITY).append("` AS ").append(CityFactory.COLNAME_CITY)
                .append(", c.`").append(CityFactory.COLNAME_COUNTRYID).append("` AS ").append(CityFactory.COLNAME_COUNTRYID)
                .append(", n.`").append(CountryFactory.COLNAME_COUNTRY).append("` AS ").append(CountryFactory.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(AppointmentImpl.class))
                .append("` e LEFT JOIN `").append(getTableName(CustomerImpl.class)).append("` p ON e.`").append(COLNAME_CUSTOMERID).append("`=p.`").append(CustomerFactory.COLNAME_CUSTOMERID)
                .append("` LEFT JOIN `").append(getTableName(AddressImpl.class)).append("` a ON p.`").append(CustomerFactory.COLNAME_ADDRESSID).append("`=a.`").append(AddressFactory.COLNAME_ADDRESSID)
                .append("` LEFT JOIN `").append(getTableName(CityImpl.class)).append("` c ON a.`").append(AddressFactory.COLNAME_CITYID).append("`=c.`").append(CityFactory.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(CityFactory.COLNAME_COUNTRYID).append("`=n.`").append(CountryFactory.COLNAME_COUNTRYID)
                .append("` LEFT JOIN `").append(getTableName(UserImpl.class)).append("` u ON e.`").append(COLNAME_USERID).append("`=u.`").append(UserFactory.COLNAME_USERID).toString();
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
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">

    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#start} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<AppointmentModel, LocalDateTime> ACCESSOR_START = new ValueAccessor<AppointmentModel, LocalDateTime>() {
        @Override
        public String get() { return COLNAME_START; }
        @Override
        public LocalDateTime apply(AppointmentModel t) { return t.getStart(); }
        @Override
        public void accept(LocalDateTime t, ParameterConsumer u) throws SQLException {
            u.setDateTime(t);
        }
    };
    
    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#end} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<AppointmentModel, LocalDateTime> ACCESSOR_END = new ValueAccessor<AppointmentModel, LocalDateTime>() {
        @Override
        public String get() { return COLNAME_END; }
        @Override
        public LocalDateTime apply(AppointmentModel t) { return t.getEnd(); }
        @Override
        public void accept(LocalDateTime t, ParameterConsumer u) throws SQLException {
            u.setDateTime(t);
        }
    };
    
    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#customer} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<AppointmentModel, Integer> ACCESSOR_CUSTOMER_ID = new ValueAccessor<AppointmentModel, Integer>() {
        @Override
        public String get() { return COLNAME_CUSTOMERID; }
        @Override
        public Integer apply(AppointmentModel t) {
            AppointmentCustomer<?> c = t.getCustomer();
            return (null != c && c.getDataObject().isExisting()) ? c.getDataObject().getPrimaryKey() : Integer.MIN_VALUE;
        }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    
    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#user} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<AppointmentModel, Integer> ACCESSOR_USER_ID = new ValueAccessor<AppointmentModel, Integer>() {
        @Override
        public String get() { return COLNAME_USERID; }
        @Override
        public Integer apply(AppointmentModel t) {
            AppointmentUser<?> u = t.getUser();
            return (null != u && u.getDataObject().isExisting()) ? u.getDataObject().getPrimaryKey() : Integer.MIN_VALUE;
        }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    public static ModelFilter<AppointmentModel> customerWithinRange(int id, LocalDateTime start, LocalDateTime end) {
        return withinRange(start, end).and(customerIdIs(id));
    }
    
    public static ModelFilter<AppointmentModel> userWithinRange(int id, LocalDateTime start, LocalDateTime end) {
        return withinRange(start, end).and(userIdIs(id));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments that occur within a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range ({@code model.getEnd() &gt;= start &amp;&amp; model.getStart() &lt; end}).
     * if {@code start} is greater than {@code end}, then this will create a filter for appointments that occur entirely outside the range values;
     * otherwise, it creates a filter for appointments that occur within the range values.
     * @param start The inclusive starting {@link LocalDateTime} value of the date/time range.
     * @param end The exclusive ending {@link LocalDateTime} value of the date/time range.
     * @return A {@link ModelFilter} for appointments that occur within a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range.
     */
    public static ModelFilter<AppointmentModel> withinRange(LocalDateTime start, LocalDateTime end) {
        return withinRange(start, false, end, false);
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments that occur within a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range.
     * @param start The starting {@link LocalDateTime} value of the date/time range.
     * @param startIsExclusive {@code true} to exclude the exact matches to the date/time range start ({@code model.getEnd() &gt; start});
     * otherwise {@code false} to include exact matches ({@code model.getEnd() &gt;= start}).
     * @param end The ending {@link LocalDateTime} value of the date/time range.
     * @param endIsInclusive {@code true} to include the exact matches to the date/time range end ({@code model.getStart() &lt;<= end});
     * otherwise {@code false} to exclude exact matches ({@code model.getStart() &lt; end}).
     * @return A {@link ModelFilter} for appointments that occur within a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range.
     */
    public static ModelFilter<AppointmentModel> withinRange(LocalDateTime start, boolean startIsExclusive, LocalDateTime end, boolean endIsInclusive) {
        assert Objects.requireNonNull(start, "Start cannot be null").compareTo(Objects.requireNonNull(end, "End cannot be null")) <= 0 :
                "Start date/time cannot be greater than end date/time";
        return ((startIsExclusive) ? endIsGreaterThan(start) : endIsGreaterThanOrEqualTo(start))
                .and((endIsInclusive) ? startIsLessThanOrEqualTo(end) : startIsLessThan(end));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments that occur outside of a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range ({@code model.getEnd() &lt; start || model.getStart() &gt;= end}).
     * @param start The exclusive starting {@link LocalDateTime} value of the date/time range.
     * @param end The inclusive ending {@link LocalDateTime} value of the date/time range.
     * @return A {@link ModelFilter} for appointments that occur outside of a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range.
     */
    public static ModelFilter<AppointmentModel> outsideRange(LocalDateTime start, LocalDateTime end) {
        return outsideRange(start, false, end, false);
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments that occur outside of a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range.
     * @param start The starting {@link LocalDateTime} value of the date/time range.
     * @param startIsInclusive {@code true} to include the exact matches to the date/time range start ({@code model.getEnd() &lt;= start});
     * otherwise {@code false} to include exact matches ({@code model.getEnd() &lt; start}).
     * @param end The ending {@link LocalDateTime} value of the date/time range.
     * @param endIsExclusive {@code true} to exclude the exact matches to the date/time range end ({@code model.getStart() &gt;< end});
     * otherwise {@code false} to exclude exact matches ({@code model.getStart() &gt;= end}).
     * @return A {@link ModelFilter} for appointments that occur outside of a {@link AppointmentModel#start} / {@link AppointmentModel#end}
     * date/time range.
     */
    public static ModelFilter<AppointmentModel> outsideRange(LocalDateTime start, boolean startIsInclusive, LocalDateTime end, boolean endIsExclusive) {
        assert Objects.requireNonNull(start, "Start cannot be null").compareTo(Objects.requireNonNull(end, "End cannot be null")) <= 0 :
                "Start date/time cannot be greater than end date/time";
        return ((startIsInclusive) ? endIsLessThanOrEqualTo(start) : endIsLessThan(start))
                .or((endIsExclusive) ? startIsGreaterThan(end) : startIsGreaterThanOrEqualTo(end));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#start} column/property is greater than the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#start} column/property
     * is greater than the specified value.
     */
    public static ModelFilter<AppointmentModel> startIsGreaterThan(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThan(ACCESSOR_START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#start} column/property is greater than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#start} column/property
     * is greater than or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> startIsGreaterThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#start} column/property is less than the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#start} column/property
     * is less than the specified value.
     */
    public static ModelFilter<AppointmentModel> startIsLessThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#start} column/property is less than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#start} column/property
     * is less than or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> startIsLessThan(LocalDateTime value) {
        return ModelFilter.columnIsLessThan(ACCESSOR_START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#end} column/property is greater than the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property
     * is greater than the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsGreaterThan(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThan(ACCESSOR_END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#end} column/property is greater than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property
     * is greater than or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsGreaterThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#end} column/property is less than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property is less than
     * or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsLessThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments objects where the value of the {@link AppointmentModel#end} column/property is less than the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property is less than
     * the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsLessThan(LocalDateTime value) {
        return ModelFilter.columnIsLessThan(ACCESSOR_END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    public static ModelFilter<AppointmentModel> customerIdIs(int id) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_CUSTOMER_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    public static ModelFilter<AppointmentModel> customerIdIsNot(int id) {
        return ModelFilter.columnIsNotEqualTo(ACCESSOR_CUSTOMER_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    
    public static ModelFilter<AppointmentModel> userIdIs(int id) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_USER_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    
    public static ModelFilter<AppointmentModel> userIdIsNot(int id) {
        return ModelFilter.columnIsNotEqualTo(ACCESSOR_USER_ID, ModelFilter.COMPARATOR_INTEGER, id);
    }
    
    public static ModelFilter<AppointmentModel> todayAndFuture(int userId, int customerId) {
        return endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0)).and(userIdIs(userId)).and(customerIdIs(customerId));
    }
    
    public static ModelFilter<AppointmentModel> todayAndFuture(int userId) {
        return endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0)).and(userIdIs(userId));
    }
    
    public static ModelFilter<AppointmentModel> todayAndFuture() {
        return endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0));
    }
    
    public static ModelFilter<AppointmentModel> customerTodayAndFuture(int customerId) {
        return endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0)).and(customerIdIs(customerId));
    }
    
    public static ModelFilter<AppointmentModel> yesterdayAndPast(int userId, int customerId) {
        return endIsLessThanOrEqualTo(LocalDate.now().atTime(0, 0, 0, 0)).and(userIdIs(userId)).and(customerIdIs(customerId));
    }
    
    public static ModelFilter<AppointmentModel> yesterdayAndPast(int userId) {
        return endIsLessThanOrEqualTo(LocalDate.now().atTime(0, 0, 0, 0)).and(userIdIs(userId));
    }
    
    public static ModelFilter<AppointmentModel> yesterdayAndPast() {
        return endIsLessThanOrEqualTo(LocalDate.now().atTime(0, 0, 0, 0));
    }
    
    public static ModelFilter<AppointmentModel> customerYesterdayAndPast(int customerId) {
        return endIsLessThanOrEqualTo(LocalDate.now().atTime(0, 0, 0, 0)).and(customerIdIs(customerId));
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, todayAndFuture(userId, customerId), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId, int customerId) throws Exception {
        return load(connection, todayAndFuture(userId, customerId));
    }
    
    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, todayAndFuture(userId), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, int userId) throws Exception {
        return load(connection, todayAndFuture(userId));
    }
    
    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, todayAndFuture(), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadTodayAndFuture(Connection connection) throws Exception {
        return load(connection, todayAndFuture());
    }
    
    public ArrayList<AppointmentImpl> loadCustomerTodayAndFuture(Connection connection, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, customerTodayAndFuture(customerId), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadCustomerTodayAndFuture(Connection connection, int customerId) throws Exception {
        return load(connection, customerTodayAndFuture(customerId));
    }
    
    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, yesterdayAndPast(userId, customerId), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId, int customerId) throws Exception {
        return load(connection, yesterdayAndPast(userId, customerId));
    }
    
    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, yesterdayAndPast(userId), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, int userId) throws Exception {
        return load(connection, yesterdayAndPast(userId));
    }
    
    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, yesterdayAndPast(), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadYesterdayAndPast(Connection connection) throws Exception {
        return load(connection, yesterdayAndPast());
    }
    
    public ArrayList<AppointmentImpl> loadCustomerYesterdayAndPast(Connection connection, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, customerYesterdayAndPast(customerId), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadCustomerYesterdayAndPast(Connection connection, int customerId) throws Exception {
        return load(connection, customerYesterdayAndPast(customerId));
    }
    
    public ArrayList<AppointmentImpl> load(Connection connection, int userId, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, userIdIs(userId).and(customerIdIs(customerId)), orderBy);
    }
    
    public ArrayList<AppointmentImpl> load(Connection connection, int userId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, userIdIs(userId), orderBy);
    }
    
    public ArrayList<AppointmentImpl> loadByCustomer(Connection connection, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, customerIdIs(customerId), orderBy);
    }
    
    public int countByCustomer(Connection connection, int customerId, LocalDateTime start, LocalDateTime end) throws Exception {
        return count(connection, customerIdIs(customerId).and(withinRange(start, end)));
    }
    
    public int countByCustomer(Connection connection, int customerId) throws Exception {
        return count(connection, customerIdIs(customerId));
    }
    
    public int countByUser(Connection connection, int userId, LocalDateTime start, LocalDateTime end) throws Exception {
        return count(connection, userIdIs(userId).and(withinRange(start, end)));
    }
    
    public int countByUser(Connection connection, int userId) throws Exception {
        return count(connection, userIdIs(userId));
    }
    
    @Override
    protected AppointmentImpl fromResultSet(ResultSet resultSet) throws SQLException { return new AppointmentImpl(resultSet); }

    @Override
    public AppointmentModel fromDataAccessObject(AppointmentImpl dao) { return (dao == null) ? null : new AppointmentModel(dao); }

    @Override
    public String getBaseQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<? extends AppointmentImpl> getDaoClass() { return AppointmentImpl.class; }
    
}
