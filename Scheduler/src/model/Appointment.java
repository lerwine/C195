/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalDateTime;
import java.util.Optional;
import model.db.Column;
import model.db.Table;
import utils.DataHelper;

/**
 *
 * @author Leonard T. Erwine
 */
@Table(name = Appointment.DB_TABLE_NAME, pk = Appointment.DB_COL_APPOINTMENTID)
public class Appointment extends Record {
    private static final DataHelper<Appointment> dataHelper = new DataHelper<>(Appointment.class);
    /**
     * Name of associated data table.
     */
    public static final String DB_TABLE_NAME = "appointment";
    
    /**
     * Defines the name of the property that contains the city record identity value.
     */
    public static final String DB_COL_APPOINTMENTID = "appointmentId";
    
    //<editor-fold defaultstate="collapsed" desc="title">
    
    public static final String PROP_TITLE = "title";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String title = "";
    
    public String getTitle() { return title; }
    
    public void setTitle(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = title;
        if (oldValue.equals(n))
            return;
        title = n;
        firePropertyChange(PROP_TITLE, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="description">
    
    public static final String PROP_DESCRIPTION = "description";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.NONEMPTY)
    private String description = "";
    
    public String getDescription() { return title; }
    
    public void setDescription(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = description;
        if (oldValue.equals(n))
            return;
        description = n;
        firePropertyChange(PROP_DESCRIPTION, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="location">
    
    public static final String PROP_LOCATION = "location";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.NONEMPTY)
    private String location = "";
    
    public String getLocation() { return title; }
    
    public void setLocation(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = location;
        if (oldValue.equals(n))
            return;
        location = n;
        firePropertyChange(PROP_LOCATION, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="contact">
    
    public static final String PROP_CONTACT = "contact";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.NONEMPTY)
    private String contact = "";
    
    public String getContact() { return title; }
    
    public void setContact(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = contact;
        if (oldValue.equals(n))
            return;
        contact = n;
        firePropertyChange(PROP_CONTACT, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="type">
    
    public static final String PROP_TYPE = "type";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String type = "";
    
    public String getType() { return title; }
    
    public void setType(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = type;
        if (oldValue.equals(n))
            return;
        type = n;
        firePropertyChange(PROP_TYPE, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="url">
    
    public static final String PROP_URL = "url";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.NONEMPTY)
    private String url = "";
    
    public String getUrl() { return title; }
    
    public void setUrl(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = url;
        if (oldValue.equals(n))
            return;
        url = n;
        firePropertyChange(PROP_URL, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="start">
    
    public static final String PROP_START = "start";
    
    @Column
    @model.db.ValueMap(model.db.MapType.DATETIME)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.DATETIME)
    private LocalDateTime start;
    
    public LocalDateTime getStart() { return start; }
    
    public void setStart(LocalDateTime value) {
        LocalDateTime oldValue = start;
        if (oldValue.equals(value))
            return;
        start = value;
        firePropertyChange(PROP_START, oldValue, value);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="end">
    
    public static final String PROP_END = "end";
    
    @Column
    @model.db.ValueMap(model.db.MapType.DATETIME)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.DATETIME)
    private LocalDateTime end;
    
    public LocalDateTime getEnd() { return end; }
    
    public void setEnd(LocalDateTime value) {
        LocalDateTime oldValue = end;
        if (oldValue.equals(value))
            return;
        end = value;
        firePropertyChange(PROP_END, oldValue, value);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="customerId">
    
    public static final String PROP_CUSTOMERID = "customerId";
    
    @Column
    @model.db.ValueMap(model.db.MapType.INTEGER)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.INTEGER)
    private Optional<Integer> customerId = Optional.empty();
    
    public Optional<Integer> getCustomerId() { return customerId; }
    
    public void setCustomerId(Optional<Integer> value) {
        Optional<Integer> oldValue = customerId;
        if ((oldValue.isPresent()) ? value.isPresent() && oldValue.get().equals(value.get()) : !value.isPresent())
            return;
        customerId = value;
        firePropertyChange(PROP_CUSTOMERID, oldValue, value);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="userId">
    
    public static final String PROP_USERID = "userId";
    
    @Column
    @model.db.ValueMap(model.db.MapType.INTEGER)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.INTEGER)
    private Optional<Integer> userId = Optional.empty();
    
    public Optional<Integer> getUserId() { return userId; }
    
    public void setUserId(Optional<Integer> value) {
        Optional<Integer> oldValue = userId;
        if ((oldValue.isPresent()) ? value.isPresent() && oldValue.get().equals(value.get()) : !value.isPresent())
            return;
        userId = value;
        firePropertyChange(PROP_USERID, oldValue, value);
    }
    
    //</editor-fold>
    
    public Appointment() {
        super(); 
        start = end = getCreateDate();
    }
}
