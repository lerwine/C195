/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import static model.db.DataRow.selectFromDbById;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(Appointment.COLNAME_APPOINTMENTID)
@TableName("appointment")
public class Appointment extends DataRow {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String COLNAME_APPOINTMENTID = "appointmentId";

    private final static HashMap<Integer, Appointment> LOOKUP_CACHE = new HashMap<>();
    
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
     */
    public final void setCustomerId(int value) {
        int oldValue = customerId;
        customerId = value;
        firePropertyChange(PROP_CUSTOMERID, oldValue, customerId);
    }
    
    public Optional<Customer> lookupCurrentCustomer(Connection connection) throws SQLException {
        return Customer.getById(connection, customerId, true);
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
     */
    public final void setUserId(int value) {
        int oldValue = userId;
        userId = value;
        firePropertyChange(PROP_USERID, oldValue, userId);
    }
    
    public Optional<User> lookupCurrentUser(Connection connection) throws SQLException {
        return User.getById(connection, userId, true);
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
    
    public Appointment() {
        super();
        customerId = userId = 0;
        title = description = location = contact = type = url = "";
        start = end = LocalDateTime.now();
    }
    
    public Appointment(int customerId, int userId, String title, String description, String location, String contact,
            String type, String url, LocalDateTime start, LocalDateTime end) {
        super();
        this.customerId = customerId;
        this.userId = userId;
        this.title = (title == null) ? "" : title;
        this.description = (description == null) ? "" : description;
        this.location = (location == null) ? "" : location;
        this.contact = (contact == null) ? "" : contact;
        this.type = (type == null) ? "" : type;
        this.url = (url == null) ? "" : url;
        this.start = (start == null) ? LocalDateTime.now() : start;
        this.end = (end == null) ? LocalDateTime.now() : end;
    }
    
    public Appointment (ResultSet rs) throws SQLException {
        super(rs);
        customerId = rs.getInt(PROP_CUSTOMERID);
        userId = rs.getInt(PROP_USERID);
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
    
    public static final Optional<Appointment> getById(Connection connection, int id, boolean includeCache) throws SQLException {
        if (includeCache && LOOKUP_CACHE.containsKey(id))
            return Optional.of(LOOKUP_CACHE.get(id));
        
        return selectFromDbById(connection, (Class<Appointment>)Appointment.class, (Function<ResultSet, Appointment>)(ResultSet rs) -> {
            Appointment r;
            try {
                r = new Appointment(rs);
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, id);
    }
    
    public static final ObservableList<Appointment> getByCustomer(Connection connection, int customerId) throws SQLException {
        return selectFromDb(connection, (Class<Appointment>)Appointment.class, (Function<ResultSet, Appointment>)(ResultSet rs) -> {
            Appointment r;
            try {
                r = new Appointment(rs);
                int id = r.getPrimaryKey();
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, "`" + PROP_CUSTOMERID + "` = ?",
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, customerId);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<Appointment> getByUser(Connection connection, int userId) throws SQLException {
        return selectFromDb(connection, (Class<Appointment>)Appointment.class, (Function<ResultSet, Appointment>)(ResultSet rs) -> {
            Appointment r;
            try {
                r = new Appointment(rs);
                int id = r.getPrimaryKey();
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, "`" + PROP_USERID + "` = ?",
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, userId);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        int oldCustomerId = customerId;
        int oldUserId = userId;
        String oldTitle = title;
        String oldDescription = description;
        String oldLocation = location;
        String oldContact = contact;
        String oldType = type;
        String oldUrl = url;
        LocalDateTime oldStart = start;
        LocalDateTime oldEnd = end;
        customerId = rs.getInt(PROP_CUSTOMERID);
        userId = rs.getInt(PROP_USERID);
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
        if (!LOOKUP_CACHE.containsKey(getPrimaryKey()))
            LOOKUP_CACHE.put(getPrimaryKey(), this);
        // Execute property change events in nested try/finally statements to ensure that all
        // events get fired, even if one of the property change listeners throws an exception.
        try { firePropertyChange(PROP_CUSTOMERID, oldCustomerId, customerId); }
        finally {
            try { firePropertyChange(PROP_USERID, oldUserId, userId); }
            finally {
                try { firePropertyChange(PROP_TITLE, oldTitle, title); }
                finally {
                    try { firePropertyChange(PROP_DESCRIPTION, oldDescription, description); }
                    finally {
                        try { firePropertyChange(PROP_LOCATION, oldLocation, location); }
                        finally {
                            try { firePropertyChange(PROP_CONTACT, oldContact, contact); }
                            finally {
                                try { firePropertyChange(PROP_TYPE, oldType, type); }
                                finally {
                                    try { firePropertyChange(PROP_URL, oldUrl, url); }
                                    finally {
                                        try { firePropertyChange(PROP_START, oldStart, start); }
                                        finally { firePropertyChange(PROP_END, oldEnd, end); }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
    
    //</editor-fold>
}
