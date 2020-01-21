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
import java.util.Objects;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.SqlStatementBuilder;
import scheduler.filter.ValueAccessor;
import util.DB;
import view.ChildModel;
import view.appointment.AppointmentModel;
import view.customer.AppointmentCustomer;
import view.user.AppointmentUser;

/**
 *
 * @author erwinel
 */
public class AppointmentImpl extends DataObjectImpl implements Appointment {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT e.`");
        sql.append(COLNAME_APPOINTMENTID).append("` AS ").append(COLNAME_APPOINTMENTID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CUSTOMERID, COLNAME_USERID,
            COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION, COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL,
            COLNAME_START, COLNAME_END).forEach((t) -> {
            sql.append(", e.`").append(t).append("` AS ").append(t);
        });
        sql.append(", p.`").append(Customer.COLNAME_CUSTOMERNAME).append("` AS ").append(Customer.COLNAME_CUSTOMERNAME)
                .append(", p.`").append(Customer.COLNAME_ADDRESSID).append("` AS ").append(Customer.COLNAME_ADDRESSID);
        Stream.of(Address.COLNAME_ADDRESS, Address.COLNAME_ADDRESS2, Address.COLNAME_CITYID, Address.COLNAME_POSTALCODE, Address.COLNAME_PHONE).forEach((t) -> {
            sql.append(", a.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", u.`").append(User.COLNAME_USERID).append("` AS ").append(User.COLNAME_USERID)
                .append(", c.`").append(City.COLNAME_CITY).append("` AS ").append(City.COLNAME_CITY)
                .append(", c.`").append(City.COLNAME_COUNTRYID).append("` AS ").append(City.COLNAME_COUNTRYID)
                .append(", n.`").append(Country.COLNAME_COUNTRY).append("` AS ").append(Country.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(AppointmentImpl.class))
                .append("` e LEFT JOIN `").append(getTableName(CustomerImpl.class)).append("` p ON e.`").append(COLNAME_CUSTOMERID).append("`=p.`").append(Customer.COLNAME_CUSTOMERID)
                .append("` LEFT JOIN `").append(getTableName(AddressImpl.class)).append("` a ON p.`").append(Customer.COLNAME_ADDRESSID).append("`=a.`").append(Address.COLNAME_ADDRESSID)
                .append("` LEFT JOIN `").append(getTableName(CityImpl.class)).append("` c ON a.`").append(Address.COLNAME_CITYID).append("`=c.`").append(City.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(City.COLNAME_COUNTRYID).append("`=n.`").append(Country.COLNAME_COUNTRYID)
                .append("` LEFT JOIN `").append(getTableName(UserImpl.class)).append("` u ON e.`").append(COLNAME_USERID).append("`=u.`").append(User.COLNAME_USERID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(Customer.COLNAME_CUSTOMERNAME,
                    User.COLNAME_USERNAME, COLNAME_TITLE, COLNAME_CONTACT, COLNAME_TYPE,
                    COLNAME_START, COLNAME_END, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }

    //</editor-fold>
    
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
    public void setType(String value) { type = Appointment.asValidAppointmentType(value); }

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
        type = Appointment.APPOINTMENTTYPE_OTHER;
        url = null;
        LocalDateTime d = LocalDateTime.now().plusHours(1).plusMinutes(30);
        d = d.minusMinutes(d.getMinute()).minusSeconds(d.getSecond()).minusNanos(d.getNano());
        start = DB.toUtcTimestamp(d);
        end = DB.toUtcTimestamp(d.plusHours(1));
    }

    /**
     * Initializes an appointment object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public AppointmentImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        customer = Customer.of(resultSet, COLNAME_CUSTOMERID);
        user = User.of(resultSet, COLNAME_USERID);
        title = resultSet.getString(COLNAME_TITLE);
        if (resultSet.wasNull())
            title = "";
        description = resultSet.getString(COLNAME_DESCRIPTION);
        if (resultSet.wasNull())
            description = "";
        location = resultSet.getString(COLNAME_LOCATION);
        if (resultSet.wasNull())
            location = "";
        contact = resultSet.getString(COLNAME_CONTACT);
        if (resultSet.wasNull())
            contact = "";
        type = resultSet.getString(COLNAME_TYPE);
        if (resultSet.wasNull())
            type = Appointment.APPOINTMENTTYPE_OTHER;
        else
            type = Appointment.asValidAppointmentType(type);
        url = resultSet.getString(COLNAME_URL);
        if (resultSet.wasNull())
            url = "";
        start = resultSet.getTimestamp(COLNAME_START);
        if (resultSet.wasNull()) {
            end = resultSet.getTimestamp(COLNAME_END);
            if (resultSet.wasNull())
                end = DB.toUtcTimestamp(LocalDateTime.now());
            start = end;
        } else {
            end = resultSet.getTimestamp(COLNAME_END);
            if (resultSet.wasNull())
                end = start;
        }
    }

    @Override
    public void saveChanges(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">

    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#start} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<AppointmentModel, LocalDateTime> START = new ValueAccessor<AppointmentModel, LocalDateTime>() {
        @Override
        public String get() { return Appointment.COLNAME_START; }
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
    public static final ValueAccessor<AppointmentModel, LocalDateTime> END = new ValueAccessor<AppointmentModel, LocalDateTime>() {
        @Override
        public String get() { return Appointment.COLNAME_END; }
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
    public static final ValueAccessor<AppointmentModel, AppointmentCustomer<?>> CUSTOMER = new ValueAccessor<AppointmentModel, AppointmentCustomer<?>>() {
        @Override
        public String get() { return Appointment.COLNAME_CUSTOMERID; }
        @Override
        public AppointmentCustomer<?> apply(AppointmentModel t) { return t.getCustomer(); }
        @Override
        public void accept(AppointmentCustomer<?> t, ParameterConsumer u) throws SQLException {
            u.setInt(t.getDataObject().getPrimaryKey());
        }
    };
    
    /**
     * The {@link AppointmentAccessor} that gets the value of the {@link AppointmentModel#user} property and sets the
     * corresponding {@link PreparedStatement} parameter value.
     */
    public static final ValueAccessor<AppointmentModel, AppointmentUser<?>> USER = new ValueAccessor<AppointmentModel, AppointmentUser<?>>() {
        @Override
        public String get() { return Appointment.COLNAME_USERID; }
        @Override
        public AppointmentUser<?> apply(AppointmentModel t) { return t.getUser(); }
        @Override
        public void accept(AppointmentUser<?> t, ParameterConsumer u) throws SQLException {
            u.setInt(t.getDataObject().getPrimaryKey());
        }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    public static ModelFilter<AppointmentModel> customerWithinRange(AppointmentCustomer<?> customer, LocalDateTime start, LocalDateTime end) {
        return withinRange(start, end).and(customerIs(customer));
    }
    
    public static ModelFilter<AppointmentModel> userWithinRange(AppointmentUser<?> user, LocalDateTime start, LocalDateTime end) {
        return withinRange(start, end).and(userIs(user));
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
        return ModelFilter.columnIsGreaterThan(START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#start} column/property is greater than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#start} column/property
     * is greater than or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> startIsGreaterThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThanOrEqualTo(START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#start} column/property is less than the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#start} column/property
     * is less than the specified value.
     */
    public static ModelFilter<AppointmentModel> startIsLessThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsLessThanOrEqualTo(START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#start} column/property is less than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#start} column/property
     * is less than or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> startIsLessThan(LocalDateTime value) {
        return ModelFilter.columnIsLessThan(START, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#end} column/property is greater than the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property
     * is greater than the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsGreaterThan(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThan(END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#end} column/property is greater than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property
     * is greater than or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsGreaterThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsGreaterThanOrEqualTo(END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the value of the {@link AppointmentModel#end} column/property is less than or equal to the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property is less than
     * or equal to the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsLessThanOrEqualTo(LocalDateTime value) {
        return ModelFilter.columnIsLessThanOrEqualTo(END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments objects where the value of the {@link AppointmentModel#end} column/property is less than the specified value.
     * @param value The {@link LocalDateTime} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the value of the {@link AppointmentModel#end} column/property is less than
     * the specified value.
     */
    public static ModelFilter<AppointmentModel> endIsLessThan(LocalDateTime value) {
        return ModelFilter.columnIsLessThan(END, ModelFilter.COMPARATOR_LOCALDATETIME, Objects.requireNonNull(value, "Value cannot be null"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the primary key of the {@link AppointmentModel#customer} column/property is equal to the primary key of the specified {@link AppointmentCustomer} object.
     * @param value The {@link AppointmentCustomer} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the primary key of the {@link AppointmentModel#customer} column/property
     * is equal to the primary key of the specified {@link AppointmentCustomer} object.
     */
    public static ModelFilter<AppointmentModel> customerIs(AppointmentCustomer<?> value) {
        return ModelFilter.columnIsEqualTo(CUSTOMER, ModelFilter.COMPARATOR_CUSTOMER, ChildModel.requireExisting(value, "Customer"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the primary key of the {@link AppointmentModel#customer} column/property is not equal to the primary key of the specified {@link AppointmentCustomer} object.
     * @param value The {@link AppointmentCustomer} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the primary key of the {@link AppointmentModel#customer} column/property
     * is not equal to the primary key of the specified {@link AppointmentCustomer} object.
     */
    public static ModelFilter<AppointmentModel> customerIsNot(AppointmentCustomer<?> value) {
        return ModelFilter.columnIsNotEqualTo(CUSTOMER, ModelFilter.COMPARATOR_CUSTOMER, ChildModel.requireExisting(value, "Customer"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the primary key of the {@link AppointmentModel#user} column/property is equal to the primary key of the specified {@link AppointmentUser} object.
     * @param value The {@link AppointmentCustomer} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the primary key of the {@link AppointmentModel#user} column/property
     * is equal to the primary key of the specified {@link AppointmentUser} object.
     */
    public static ModelFilter<AppointmentModel> userIs(AppointmentUser<?> value) {
        return ModelFilter.columnIsEqualTo(USER, ModelFilter.COMPARATOR_USER, ChildModel.requireExisting(value, "User"));
    }
    
    /**
     * Creates a {@link ModelFilter} for appointments where the primary key of the {@link AppointmentModel#user} column/property is not equal to the primary key of the specified {@link AppointmentCustomer} object.
     * @param value The {@link AppointmentCustomer} to compare to.
     * @return A {@link ModelFilter} for {@link AppointmentModel} objects where the primary key of the {@link AppointmentModel#user} column/property
     * is not equal to the primary key of the specified {@link AppointmentUser} object.
     */
    public static ModelFilter<AppointmentModel> userIsNot(AppointmentUser<?> value) {
        return ModelFilter.columnIsNotEqualTo(USER, ModelFilter.COMPARATOR_USER, ChildModel.requireExisting(value, "User"));
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="load* overloads">
    
    public static ArrayList<AppointmentImpl> loadAll(Connection connection) throws Exception {
        return loadAll(connection, null);
    }
    
    public static ArrayList<AppointmentImpl> loadAll(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return loadAll(connection, getBaseSelectQuery(),
                OrderBy.getOrderByOrDefault(orderBy, () -> OrderBy.of(OrderBy.of(COLNAME_START, true), OrderBy.of(COLNAME_END, true))),
                (rs) -> new AppointmentImpl(rs));
    }
    
    public static ArrayList<AppointmentImpl> load(Connection connection, ModelFilter<AppointmentModel> filter) throws Exception {
        return load(connection, filter, null);
    }
    
    public static ArrayList<AppointmentImpl> load(Connection connection, ModelFilter<AppointmentModel> filter,
            Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, getBaseSelectQuery(), filter,
                OrderBy.getOrderByOrDefault(orderBy, () -> OrderBy.of(OrderBy.of(COLNAME_START, true), OrderBy.of(COLNAME_END, true))),
                (rs) -> new AppointmentImpl(rs));
    }
    
    //</editor-fold>
    
    public static int getCount(Connection connection, ModelFilter<AppointmentModel> filter) throws Exception {
        try (SqlStatementBuilder<PreparedStatement> builder = SqlStatementBuilder.fromConnection(connection)) {
            builder.appendSql("SELECT COUNT(`").appendSql(COLNAME_APPOINTMENTID).appendSql("`) FROM `")
                    .appendSql(TABLENAME_APPOINTMENT).appendSql("`");
            if (null != filter) {
                String s = filter.get();
                if (!s.isEmpty())
                    builder.appendSql(" WHERE ").appendSql(s);
                filter.setParameterValues(builder.finalizeSql());
            }
        
            try (ResultSet rs = builder.getResult().executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        return 0;
    }
    
}
