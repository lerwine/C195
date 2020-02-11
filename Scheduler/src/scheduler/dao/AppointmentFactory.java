package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
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
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.ValueAccessor;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.customer.AppointmentCustomer;
import scheduler.view.user.AppointmentUser;

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
    
    public ArrayList<AppointmentImpl> load(Connection connection, int userId, int customerId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, userIdIs(userId).and(customerIdIs(customerId)), orderBy);
    }
    
    public ArrayList<AppointmentImpl> load(Connection connection, int userId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, userIdIs(userId), orderBy);
    }
    
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
   public static final class AppointmentImpl extends DataObjectImpl implements Appointment {
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
