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
import javafx.collections.ObservableList;
import model.Address;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;
import scheduler.InvalidArgumentException;
import scheduler.SqlConnectionDependency;

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
    
    private int customerId;
    
    public static final String PROP_CUSTOMERID = "customerId";
    
    /**
     * Get the value of customerId
     *
     * @return the value of customerId
     */
    public final int getCustomerId() { return customerId; }
    
    /**
     * Set the value of customerId
     *
     * @param value new value of customerId
     * @throws java.sql.SQLException
     * @throws scheduler.InvalidArgumentException
     */
    public final void setCustomerId(int value) throws SQLException, InvalidArgumentException {
        if (value == customerId)
            return;
        int oldId = customerId;
        model.Customer oldCustomer = customer;
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        try {
            Optional<CustomerRow> r = CustomerRow.getById(dep.getconnection(), value);
            if (r.isPresent())
                customer = r.get();
            else
                throw new InvalidArgumentException("value", "No address found that matches that ID");
        } finally { dep.close(); }
        customerId = value;
        try { firePropertyChange(PROP_CUSTOMERID, oldId, customerId); }
        finally { firePropertyChange(PROP_CUSTOMER, oldCustomer, customer); }
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="customer">
    
    private model.Customer customer;
    
    public static final String PROP_CUSTOMER = "customer";
    
    /**
     * Get the value of customer
     *
     * @return the value of customer
     */
    @Override
    public final model.Customer getCustomer() { return customer; }
    
    /**
     * Set the value of customer
     *
     * @param value new value of customer
     * @throws scheduler.InvalidArgumentException
     */
    public final void setCustomer(model.Customer value) throws InvalidArgumentException {
        if (value == null)
            throw new InvalidArgumentException("value", "Customer cannot be null");
        if (value instanceof CustomerRow) {
            int rowState = ((CustomerRow)value).getRowState();
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidArgumentException("value", "Customer was deleted");
            if (rowState == ROWSTATE_NEW)
                throw new InvalidArgumentException("value", "Customer was not added to the database");
        }
        
        int oldId = customerId;
        model.Customer oldCustomer = customer;
        customerId = (customer = value).getPrimaryKey();
        
        try { firePropertyChange(PROP_CUSTOMER, oldCustomer, customer); }
        finally { firePropertyChange(PROP_CUSTOMERID, oldId, customerId); }
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="userId">
    
    private int userId;
    
    public static final String PROP_USERID = "userId";
    
    /**
     * Get the value of userId
     *
     * @return the value of userId
     */
    public final int getUserId() { return userId; }
    
    /**
     * Set the value of userId
     *
     * @param value new value of userId
     * @throws java.sql.SQLException
     * @throws scheduler.InvalidArgumentException
     */
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
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="user">
    
    private model.User user;
    
    public static final String PROP_USER = "user";
    
    /**
     * Get the value of userId
     *
     * @return the value of userId
     */
    @Override
    public final model.User getUser() { return user; }
    
    /**
     * Set the value of userId
     *
     * @param value new value of userId
     * @throws java.sql.SQLException
     * @throws scheduler.InvalidArgumentException
     */
    public final void setUser(model.User value) throws SQLException, InvalidArgumentException {
        if (value == null)
            throw new InvalidArgumentException("value", "User cannot be null");
        if (value instanceof UserRow) {
            int rowState = ((UserRow)value).getRowState();
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidArgumentException("value", "User was deleted");
            if (rowState == ROWSTATE_NEW)
                throw new InvalidArgumentException("value", "User was not added to the database");
        }
        
        model.User oldUser = user;
        int oldId = userId;
        userId = (user = value).getPrimaryKey();
        
        try { firePropertyChange(PROP_USER, oldUser, user); }
        finally { firePropertyChange(PROP_USERID, oldId, userId); }
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="title">
    
    private String title;
    
    public static final String PROP_TITLE = "title";
    
    /**
     * Get the value of title
     *
     * @return the value of title
     */
    @Override
    public final String getTitle() { return title; }
    
    /**
     * Set the value of title
     *
     * @param value new value of title
     */
    public final void setTitle(String value) {
        String oldValue = title;
        title = (value == null) ? "" : value;
        firePropertyChange(PROP_TITLE, oldValue, title);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="description">
    
    private String description;
    
    public static final String PROP_DESCRIPTION = "description";
    
    /**
     * Get the value of description
     *
     * @return the value of description
     */
    @Override
    public final String getDescription() { return description; }
    
    /**
     * Set the value of description
     *
     * @param value new value of description
     */
    public final void setDescription(String value) {
        String oldValue = description;
        description = (value == null) ? "" : value;
        firePropertyChange(PROP_DESCRIPTION, oldValue, description);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="location">
    
    private String location;
    
    public static final String PROP_LOCATION = "location";
    
    /**
     * Get the value of location
     *
     * @return the value of location
     */
    @Override
    public final String getLocation() { return location; }
    
    /**
     * Set the value of location
     *
     * @param value new value of location
     */
    public final void setLocation(String value) {
        String oldValue = location;
        location = (value == null) ? "" : value;
        firePropertyChange(PROP_LOCATION, oldValue, location);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="contact">
    
    private String contact;
    
    public static final String PROP_CONTACT = "contact";
    
    /**
     * Get the value of contact
     *
     * @return the value of contact
     */
    @Override
    public final String getContact() { return contact; }
    
    /**
     * Set the value of contact
     *
     * @param value new value of contact
     */
    public final void setContact(String value) {
        String oldValue = contact;
        contact = (value == null) ? "" : value;
        firePropertyChange(PROP_CONTACT, oldValue, contact);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="type">
    
    private String type;
    
    public static final String PROP_TYPE = "type";
    
    /**
     * Get the value of type
     *
     * @return the value of v
     */
    @Override
    public final String getType() { return type; }
    
    /**
     * Set the value of type
     *
     * @param value new value of type
     */
    public final void setType(String value) {
        String oldValue = type;
        type = (value == null) ? "" : value;
        firePropertyChange(PROP_TYPE, oldValue, type);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="url">
    
    private String url;
    
    public static final String PROP_URL = "url";
    
    /**
     * Get the value of url
     *
     * @return the value of url
     */
    @Override
    public final String getUrl() { return url; }
    
    /**
     * Set the value of url
     *
     * @param value new value of url
     */
    public final void setUrl(String value) {
        String oldValue = url;
        url = (value == null) ? "" : value;
        firePropertyChange(PROP_URL, oldValue, url);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="start">
    
    public static final String PROP_START = "start";
    
    private LocalDateTime start;
    
    /**
     * Gets the value of start.
     * @return The value of start.
     */
    @Override
    public final LocalDateTime getStart() { return start; }
    
    /**
     * Set the value of start
     *
     * @param value new value of start
     */
    public final void setStart(LocalDateTime value) {
        LocalDateTime oldValue = start;
        start = (value == null) ? LocalDateTime.now() : value;
        firePropertyChange(PROP_START, oldValue, start);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="end">
    
    public static final String PROP_END = "end";
    
    private LocalDateTime end;
    
    /**
     * Gets the value of end.
     * @return The value of end.
     */
    @Override
    public final LocalDateTime getEnd() { return end; }
    
    /**
     * Set the value of end
     *
     * @param value new value of end
     */
    public final void setEnd(LocalDateTime value) {
        LocalDateTime oldValue = end;
        end = (value == null) ? LocalDateTime.now() : value;
        firePropertyChange(PROP_END, oldValue, end);
    }
    
    //</editor-fold>
   
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public AppointmentRow() {
        super();
        customerId = userId = 0;
        title = description = location = contact = type = url = "";
        start = end = LocalDateTime.now();
    }
    
    public AppointmentRow(CustomerRow customer, UserRow user, String title, String description, String location, String contact,
            String type, String url, LocalDateTime start, LocalDateTime end) throws InvalidArgumentException {
        super();
        if (customer == null)
            throw new InvalidArgumentException("address", "Customer cannot be null");
        if (customer.getRowState() == ROWSTATE_DELETED)
            throw new InvalidArgumentException("address", "Customer was deleted");
        if (customer.getRowState() == ROWSTATE_NEW)
            throw new InvalidArgumentException("address", "Customer was not added to the database");
        if (user == null)
            throw new InvalidArgumentException("address", "User cannot be null");
        if (user.getRowState() == ROWSTATE_DELETED)
            throw new InvalidArgumentException("address", "User was deleted");
        if (user.getRowState() == ROWSTATE_NEW)
            throw new InvalidArgumentException("address", "User was not added to the database");
        customerId = (this.customer = customer).getPrimaryKey();
        userId = (this.user = user).getPrimaryKey();
        this.title = (title == null) ? "" : title;
        this.description = (description == null) ? "" : description;
        this.location = (location == null) ? "" : location;
        this.contact = (contact == null) ? "" : contact;
        this.type = (type == null) ? "" : type;
        this.url = (url == null) ? "" : url;
        this.start = (start == null) ? LocalDateTime.now() : start;
        this.end = (end == null) ? LocalDateTime.now() : end;
    }
    
    public AppointmentRow (ResultSet rs) throws SQLException {
        super(rs);
        customerId = rs.getInt(PROP_CUSTOMERID);
        customer = new Customer(customerId, rs.getString(CustomerRow.PROP_CUSTOMERNAME),
                new CustomerRow.Address(rs.getInt(CustomerRow.PROP_ADDRESSID), rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE)),
                rs.getBoolean(CustomerRow.PROP_ACTIVE));
        userId = rs.getInt(PROP_USERID);
        user = new User(userId, rs.getString(UserRow.PROP_USERNAME), rs.getShort(UserRow.PROP_ACTIVE));
        title = rs.getString(PROP_TITLE);
        if (rs.wasNull())
            title = "";
        description = rs.getString(PROP_DESCRIPTION);
        if (rs.wasNull())
            description = "";
        location = rs.getString(PROP_LOCATION);
        if (rs.wasNull())
            location = "";
        contact = rs.getString(PROP_CONTACT);
        if (rs.wasNull())
            contact = "";
        type = rs.getString(PROP_TYPE);
        if (rs.wasNull())
            type = "";
        url = rs.getString(PROP_URL);
        if (rs.wasNull())
            url = "";
        Timestamp t = rs.getTimestamp(PROP_START);
        start = (rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime();
        t = rs.getTimestamp(PROP_END);
        end = (rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime();
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
        return selectFromDb(connection, String.format("%s WHERE `address`.`userId` = ? AND %s ORDER BY `%s`, `%s`",
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
        try {
            deferPropertyChangeEvent(PROP_CUSTOMERID);
            deferPropertyChangeEvent(PROP_USERID);
            deferPropertyChangeEvent(PROP_TITLE);
            deferPropertyChangeEvent(PROP_DESCRIPTION);
            deferPropertyChangeEvent(PROP_LOCATION);
            deferPropertyChangeEvent(PROP_CONTACT);
            deferPropertyChangeEvent(PROP_TYPE);
            deferPropertyChangeEvent(PROP_URL);
            deferPropertyChangeEvent(PROP_START);
            deferPropertyChangeEvent(PROP_END);
            deferPropertyChangeEvent(PROP_CUSTOMER);
            deferPropertyChangeEvent(PROP_USER);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(AppointmentRow.class.getName()).log(Level.SEVERE, null, ex);
        }
        customerId = rs.getInt(PROP_CUSTOMERID);
        customer = new Customer(customerId, rs.getString(CustomerRow.PROP_CUSTOMERNAME),
                new CustomerRow.Address(rs.getInt(CustomerRow.PROP_ADDRESSID), rs.getString(AddressRow.COLNAME_ADDRESS), rs.getString(AddressRow.PROP_ADDRESS2),
                new AddressRow.City(rs.getInt(AddressRow.PROP_CITYID), rs.getString(AddressRow.PROP_CITY),
                new CityRow.Country(rs.getInt(CityRow.PROP_COUNTRYID), rs.getString(CityRow.PROP_COUNTRY))),
                rs.getString(AddressRow.PROP_POSTALCODE), rs.getString(AddressRow.PROP_PHONE)),
                rs.getBoolean(CustomerRow.PROP_ACTIVE));
        userId = rs.getInt(PROP_USERID);
        user = new User(userId, rs.getString(UserRow.PROP_USERNAME), rs.getShort(UserRow.PROP_ACTIVE));
        title = rs.getString(PROP_TITLE);
        if (rs.wasNull())
            title = "";
        description = rs.getString(PROP_DESCRIPTION);
        if (rs.wasNull())
            description = "";
        location = rs.getString(PROP_LOCATION);
        if (rs.wasNull())
            location = "";
        contact = rs.getString(PROP_CONTACT);
        if (rs.wasNull())
            contact = "";
        type = rs.getString(PROP_TYPE);
        if (rs.wasNull())
            type = "";
        url = rs.getString(PROP_URL);
        if (rs.wasNull())
            url = "";
        Timestamp t = rs.getTimestamp(PROP_START);
        start = (rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime();
        t = rs.getTimestamp(PROP_END);
        end = (rs.wasNull()) ? LocalDateTime.now() : t.toLocalDateTime();
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
                    ps.setInt(index + 1, customerId);
                    break;
                case PROP_USERID:
                    ps.setInt(index + 1, userId);
                    break;
                case PROP_TITLE:
                    ps.setString(index + 1, title);
                    break;
                case PROP_DESCRIPTION:
                    ps.setString(index + 1, description);
                    break;
                case PROP_LOCATION:
                    ps.setString(index + 1, location);
                    break;
                case PROP_CONTACT:
                    ps.setString(index + 1, contact);
                    break;
                case PROP_TYPE:
                    ps.setString(index + 1, type);
                    break;
                case PROP_URL:
                    ps.setString(index + 1, url);
                    break;
                case PROP_START:
                    ps.setTimestamp(index + 1, Timestamp.valueOf(start));
                    break;
                case PROP_END:
                    ps.setTimestamp(index + 1, Timestamp.valueOf(end));
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
        private final short active;
        User(int id, String userName, short active) {
            this.id = id;
            this.userName = userName;
            this.active = active;
        }

        @Override
        public String getUserName() { return userName; }

        @Override
        public short getActive() { return active; }

        @Override
        public int getPrimaryKey() { return id; }
    }
    
    static class Customer implements model.Customer {
        private final int id;
        private final String customerName;
        private final CustomerRow.Address address;
        private final boolean active;
        Customer(int id, String customerName, CustomerRow.Address address, boolean active) {
            this.id = id;
            this.customerName = customerName;
            this.address = address;
            this.active = active;
        }

        @Override
        public String getCustomerName() { return customerName; }

        @Override
        public Address getAddress() { return address; }

        @Override
        public boolean isActive() { return active; }

        @Override
        public int getPrimaryKey() { return id; }
    }
}
