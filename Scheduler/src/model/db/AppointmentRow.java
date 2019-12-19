package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import model.Address;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(AppointmentRow.COLNAME_APPOINTMENTID)
@TableName("appointment")
public class AppointmentRow extends DataRow implements model.Appointment {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String SQL_SELECT = "SELECT appointment.*, `user`.userName, `user`.`active`, customer.customerName," +
        " customer.addressId, address.address, address.address2, address.cityId, city.city, address.postalCode, address.phone," +
        " city.countryId, country.country, customer.`active` FROM appointment" +
        " LEFT OUTER JOIN customer ON appointment.customerId = customer.customerId" +
        " LEFT OUTER JOIN address ON customer.addressId = address.addressId" +
        " LEFT OUTER JOIN city ON address.cityId = city.cityId" +
        " LEFT OUTER JOIN country ON city.countryId = country.countryId" +
        " LEFT OUTER JOIN `user` ON appointment.userId = `user`.userId";
    
    public static final String COLNAME_APPOINTMENTID = "appointmentId";

    //<editor-fold defaultstate="collapsed" desc="customerId">
    
    public static final String PROP_CUSTOMERID = "customerId";
    
    private final ReadOnlyIntegerWrapper customerId;

    /**
    * Get the value of customerId
    *
    * @return the value of customerId
    */
    public int getCustomerId() { return customerId.get(); }

    public ReadOnlyIntegerProperty customerIdProperty() { return customerId.getReadOnlyProperty(); }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="customer">
    
    public static final String PROP_CUSTOMER = "customer";
    
    private final ObjectProperty<model.Customer> customer;

    /**
     * Get the value of customer
     *
     * @return the value of customer
     */
    @Override
    public model.Customer getCustomer() { return customer.get(); }

    public void setCustomer(model.Customer value) {
        customer.set(value);
    }

    public ObjectProperty<model.Customer> customerProperty() {
        return customer;
    }
    
    private final RowIdChangeListener<model.Customer> customerIdChangeListener;
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="userId">
    
    public static final String PROP_USERID = "userId";
    
    private final ReadOnlyIntegerWrapper userId;
    
    public int getUserId() { return userId.get(); }

    public ReadOnlyIntegerProperty userIdProperty() { return userId.getReadOnlyProperty(); }
    
    private final RowIdChangeListener<model.User> userIdChangeListener;
    
    /*
    private int userId;
    
    /**
    * Get the value of userId
    *
    * @return the value of userId
    * /
    public final int getUserId() { return userId; }
    
    /**
     * Set the value of userId
     *
     * @param value new value of userId
     * @throws java.sql.SQLException
     * @throws scheduler.InvalidArgumentException
     * /
    public final void setUserId(int value) throws SQLException, InvalidArgumentException {
        if (userId == value)
            return;
        model.User oldUser = user;
        int oldId = userId;
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        try {
            Optional<UserRow> u = UserRow.getById(dep.getconnection(), value);
            if (u.isPresent())
                user = u.get();
            else
                throw new InvalidArgumentException("userId", "No user found that matches that ID");
        } finally { dep.close(); }
        
        userId = value;
        
        try { firePropertyChange(PROP_USERID, oldId, userId); }
        finally { firePropertyChange(PROP_USER, oldUser, user); }
    }
    */
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="user">
    
    public static final String PROP_USER = "user";
    
    private final ObjectProperty<model.User> user;

    public model.User getUser() {
        return user.get();
    }

    public void setUser(model.User value) {
        user.set(value);
    }

    public ObjectProperty<model.User> userProperty() {
        return user;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="title">
    
    public static final String PROP_TITLE = "title";
    
    private final NonNullableStringProperty title;

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public NonNullableStringProperty titleProperty() {
        return title;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="description">
    
    public static final String PROP_DESCRIPTION = "description";
    
    private final NonNullableStringProperty description;

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    public NonNullableStringProperty descriptionProperty() {
        return description;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="location">
    
    public static final String PROP_LOCATION = "location";
    
    private final NonNullableStringProperty location;

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String value) {
        location.set(value);
    }

    public NonNullableStringProperty locationProperty() {
        return location;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="contact">
    
    public static final String PROP_CONTACT = "contact";
    
    private final NonNullableStringProperty contact;

    public String getContact() {
        return contact.get();
    }

    public void setContact(String value) {
        contact.set(value);
    }

    public NonNullableStringProperty contactProperty() {
        return contact;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="type">
    
    public static final String PROP_TYPE = "type";
    
    private final NonNullableStringProperty type;

    public String getType() {
        return type.get();
    }

    public void setType(String value) {
        type.set(value);
    }

    public NonNullableStringProperty typeProperty() {
        return type;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="url">
    
    public static final String PROP_URL = "url";
    
    private final NonNullableStringProperty url;

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String value) {
        url.set(value);
    }

    public NonNullableStringProperty urlProperty() {
        return url;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="start">
    
    public static final String PROP_START = "start";
    
    private final NonNullableLocalDateTimeProperty start;

    public LocalDateTime getStart() {
        return start.get();
    }

    public void setStart(LocalDateTime value) {
        start.set(value);
    }

    public ObjectProperty<LocalDateTime> startProperty() {
        return start;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="end">
    
    public static final String PROP_END = "end";
    private final NonNullableLocalDateTimeProperty end;

    public LocalDateTime getEnd() {
        return end.get();
    }

    public void setEnd(LocalDateTime value) {
        end.set(value);
    }

    public ObjectProperty<LocalDateTime> endProperty() {
        return end;
    }
    
    //</editor-fold>
   
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public AppointmentRow() {
        super();
        customerId = new ReadOnlyIntegerWrapper();
        customer = new SimpleObjectProperty<>();
        userId = new ReadOnlyIntegerWrapper();
        user = new SimpleObjectProperty<>();
        title = new NonNullableStringProperty();
        description = new NonNullableStringProperty();
        location = new NonNullableStringProperty();
        contact = new NonNullableStringProperty();
        type = new NonNullableStringProperty();
        url = new NonNullableStringProperty();
        start = new NonNullableLocalDateTimeProperty(LocalDateTime.now());
        end = new NonNullableLocalDateTimeProperty(start.getValue());
        customerIdChangeListener = new RowIdChangeListener<>(this.customer, customerId);
        userIdChangeListener = new RowIdChangeListener<>(this.user, userId);
    }
    
    public AppointmentRow(CustomerRow customer, UserRow user, String title, String description, String location, String contact,
            String type, String url, LocalDateTime start, LocalDateTime end) {
        super();
        customerId = new ReadOnlyIntegerWrapper();
        this.customer = new SimpleObjectProperty<>(customer);
        userId = new ReadOnlyIntegerWrapper();
        this.user = new SimpleObjectProperty<>(user);
        this.title = new NonNullableStringProperty(title);
        this.description = new NonNullableStringProperty(description);
        this.location = new NonNullableStringProperty(location);
        this.contact = new NonNullableStringProperty(contact);
        this.type = new NonNullableStringProperty(type);
        this.url = new NonNullableStringProperty(url);
        this.start = new NonNullableLocalDateTimeProperty(start);
        this.end = new NonNullableLocalDateTimeProperty(end);
        customerIdChangeListener = new RowIdChangeListener<>(this.customer, customerId);
        userIdChangeListener = new RowIdChangeListener<>(this.user, userId);
    }
    
    public AppointmentRow (ResultSet rs) throws SQLException {
        super(rs);
        customerId = new ReadOnlyIntegerWrapper();
        customer = new SimpleObjectProperty<>(new Customer(rs.getInt(PROP_CUSTOMERID), rs.getString(CustomerRow.PROP_CUSTOMERNAME),
                new CustomerRow.Address(rs.getInt(CustomerRow.PROP_ADDRESSID), rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE)),
                rs.getBoolean(CustomerRow.PROP_ACTIVE)));
        userId = new ReadOnlyIntegerWrapper();
        user = new SimpleObjectProperty<>(new User(rs.getInt(PROP_USERID), rs.getString(UserRow.PROP_USERNAME), rs.getShort(UserRow.PROP_ACTIVE)));
        title = new NonNullableStringProperty(rs.getString(PROP_TITLE));
        if (rs.wasNull())
            title.setValue("");
        description = new NonNullableStringProperty(rs.getString(PROP_DESCRIPTION));
        if (rs.wasNull())
            description.setValue("");
        location = new NonNullableStringProperty(rs.getString(PROP_LOCATION));
        if (rs.wasNull())
            location.setValue("");
        contact = new NonNullableStringProperty(rs.getString(PROP_CONTACT));
        if (rs.wasNull())
            contact.setValue("");
        type = new NonNullableStringProperty(rs.getString(PROP_TYPE));
        if (rs.wasNull())
            type.setValue("");
        url = new NonNullableStringProperty(rs.getString(PROP_URL));
        if (rs.wasNull())
            url.setValue("");
        Timestamp t = rs.getTimestamp(PROP_START);
        start = new NonNullableLocalDateTimeProperty((rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime());
        t = rs.getTimestamp(PROP_END);
        end = new NonNullableLocalDateTimeProperty((rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime());
        customerIdChangeListener = new RowIdChangeListener<>(this.customer, customerId);
        userIdChangeListener = new RowIdChangeListener<>(this.user, userId);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<AppointmentRow> getById(Connection connection, int id) throws SQLException {
        return selectFirstFromDb(connection, SQL_SELECT + " WHERE appointment.appointmentId = ?", (Function<ResultSet, AppointmentRow>)(ResultSet rs) -> {
            AppointmentRow u;
            try {
                u = new AppointmentRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AppointmentRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, id);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<AppointmentRow> getByCustomer(Connection connection, int customerId) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE `address`.`customerId` = ?", (Function<ResultSet, AppointmentRow>)(ResultSet rs) -> {
            AppointmentRow u;
            try {
                u = new AppointmentRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AppointmentRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, customerId);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<AppointmentRow> getAllByUser(Connection connection, int userId) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE `address`.`userId` = ?", (Function<ResultSet, AppointmentRow>)(ResultSet rs) -> {
            AppointmentRow u;
            try {
                u = new AppointmentRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AppointmentRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, userId);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<AppointmentRow> getTodayAndFuture(Connection connection) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE CAST(%s AS Date) >= CURRENT_DATE", (Function<ResultSet, AppointmentRow>)(ResultSet rs) -> {
            AppointmentRow u;
            try {
                u = new AppointmentRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AppointmentRow object from result set.");
            }
            return u;
        }, null);
    }
    
    public static final ObservableList<AppointmentRow> getTodayAndFutureByUser(Connection connection, int userId) throws SQLException {
        return getByUser(connection, String.format("CAST(%s AS Date) >= CURRENT_DATE", PROP_END), userId);
    }
    
    private static ObservableList<AppointmentRow> getByUser(Connection connection, String whereAppend, int userId) throws SQLException {
        return selectFromDb(connection, String.format("%s WHERE appointment.userId = ? AND %s ORDER BY `%s`, `%s`",
                SQL_SELECT, whereAppend, PROP_START, PROP_END), (Function<ResultSet, AppointmentRow>)(ResultSet rs) -> {
            AppointmentRow u;
            try {
                u = new AppointmentRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing AppointmentRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, userId);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        customerId.setValue(rs.getInt(PROP_CUSTOMERID));
        customer.setValue(new Customer(getCustomerId(), rs.getString(CustomerRow.PROP_CUSTOMERNAME),
                new CustomerRow.Address(rs.getInt(CustomerRow.PROP_ADDRESSID), rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE)),
                rs.getBoolean(CustomerRow.PROP_ACTIVE)));
        userId.setValue(rs.getInt(PROP_USERID));
        user.setValue(new User(getUserId(), rs.getString(UserRow.PROP_USERNAME), rs.getShort(UserRow.PROP_ACTIVE)));
        title.setValue(rs.getString(PROP_TITLE));
        if (rs.wasNull())
            title.setValue("");
        description.setValue(rs.getString(PROP_DESCRIPTION));
        if (rs.wasNull())
            description.setValue("");
        location.setValue(rs.getString(PROP_LOCATION));
        if (rs.wasNull())
            location.setValue("");
        contact.setValue(rs.getString(PROP_CONTACT));
        if (rs.wasNull())
            contact.setValue("");
        type.setValue(rs.getString(PROP_TYPE));
        if (rs.wasNull())
            type.setValue("");
        url.setValue(rs.getString(PROP_URL));
        if (rs.wasNull())
            url.setValue("");
        Timestamp t = rs.getTimestamp(PROP_START);
        start.setValue((rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime());
        t = rs.getTimestamp(PROP_END);
        end.setValue((rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime());
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { PROP_CUSTOMERID, PROP_USERID, PROP_TITLE, PROP_DESCRIPTION, PROP_LOCATION,
            PROP_CONTACT, PROP_TYPE, PROP_URL, PROP_START, PROP_END };
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            switch (fieldNames[index]) {
                case PROP_CUSTOMERID:
                    ps.setInt(index + 1, getCustomerId());
                    break;
                case PROP_USERID:
                    ps.setInt(index + 1, getUserId());
                    break;
                case PROP_TITLE:
                    ps.setString(index + 1, getTitle());
                    break;
                case PROP_DESCRIPTION:
                    ps.setString(index + 1, getDescription());
                    break;
                case PROP_LOCATION:
                    ps.setString(index + 1, getLocation());
                    break;
                case PROP_CONTACT:
                    ps.setString(index + 1, getContact());
                    break;
                case PROP_TYPE:
                    ps.setString(index + 1, getType());
                    break;
                case PROP_URL:
                    ps.setString(index + 1, getUrl());
                    break;
                case PROP_START:
                    ps.setTimestamp(index + 1, Timestamp.valueOf(getStart()));
                    break;
                case PROP_END:
                    ps.setTimestamp(index + 1, Timestamp.valueOf(getEnd()));
                    break;
            }
        }
    }
    
    @Override
    protected String getSelectQuery() { return SQL_SELECT; }
    
    //</editor-fold>
    
    static class User implements model.User {
        private final int id;
        private final String userName;
        private final int active;
        User(int id, String userName, short active) {
            this.id = id;
            this.userName = userName;
            this.active = active;
        }

        @Override
        public String getUserName() { return userName; }

        @Override
        public int getActive() { return active; }

        @Override
        public int getPrimaryKey() { return id; }
    }
    
    static class Customer implements model.Customer {
        private final int id;
        private final String name;
        private final CustomerRow.Address address;
        private final boolean active;
        Customer(int id, String name, CustomerRow.Address address, boolean active) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.active = active;
        }

        @Override
        public String getName() { return name; }

        @Override
        public Address getAddress() { return address; }

        @Override
        public boolean isActive() { return active; }

        @Override
        public int getPrimaryKey() { return id; }
    }
}
