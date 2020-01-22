package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import scheduler.dao.factory.AppointmentFactory;
import scheduler.dao.factory.DataObjectFactory;
import util.DB;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectFactory.TABLENAME_APPOINTMENT)
@PrimaryKeyColumn(AppointmentFactory.COLNAME_APPOINTMENTID)
public class AppointmentImpl extends DataObjectImpl implements Appointment {
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
    public void setType(String value) { type = AppointmentFactory.asValidAppointmentType(value); }

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
        type = AppointmentFactory.APPOINTMENTTYPE_OTHER;
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
        customer = Customer.of(resultSet, AppointmentFactory.COLNAME_CUSTOMERID);
        user = User.of(resultSet, AppointmentFactory.COLNAME_USERID);
        title = resultSet.getString(AppointmentFactory.COLNAME_TITLE);
        if (resultSet.wasNull())
            title = "";
        description = resultSet.getString(AppointmentFactory.COLNAME_DESCRIPTION);
        if (resultSet.wasNull())
            description = "";
        location = resultSet.getString(AppointmentFactory.COLNAME_LOCATION);
        if (resultSet.wasNull())
            location = "";
        contact = resultSet.getString(AppointmentFactory.COLNAME_CONTACT);
        if (resultSet.wasNull())
            contact = "";
        type = resultSet.getString(AppointmentFactory.COLNAME_TYPE);
        if (resultSet.wasNull())
            type =AppointmentFactory.APPOINTMENTTYPE_OTHER;
        else
            type =AppointmentFactory.asValidAppointmentType(type);
        url = resultSet.getString(AppointmentFactory.COLNAME_URL);
        if (resultSet.wasNull())
            url = "";
        start = resultSet.getTimestamp(AppointmentFactory.COLNAME_START);
        if (resultSet.wasNull()) {
            end = resultSet.getTimestamp(AppointmentFactory.COLNAME_END);
            if (resultSet.wasNull())
                end = DB.toUtcTimestamp(LocalDateTime.now());
            start = end;
        } else {
            end = resultSet.getTimestamp(AppointmentFactory.COLNAME_END);
            if (resultSet.wasNull())
                end = start;
        }
    }

    @Override
    public void saveChanges(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
