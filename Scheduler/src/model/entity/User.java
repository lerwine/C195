/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;

import java.util.Collection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents data from a row in the user data table.
 * @author Leonard T. Erwine
 */
@Entity
@XmlRootElement
@NamedQueries({
    /**
     * Selects all users.
     */
    @NamedQuery(name = User.NAMED_QUERY_ALL, query = "SELECT u FROM User u"),
    /**
     * Selects user by primary key.
     */
    @NamedQuery(name = User.NAMED_QUERY_BY_ID, query = "SELECT u FROM User u WHERE u.userId = :" + User.PARAMETER_NAME_USERID),
    /**
     * Selects user by user name.
     */
    @NamedQuery(name = User.NAMED_QUERY_BY_USERNAME_AVAIL, query = "SELECT u FROM User u WHERE u.userName = :" + User.PARAMETER_NAME_USERNAME + " AND u.userId <> :" + User.PARAMETER_NAME_USERID),
    /**
     * Selects user by user name.
     */
    @NamedQuery(name = User.NAMED_QUERY_BY_USERNAME, query = "SELECT u FROM User u WHERE u.userName = :" + User.PARAMETER_NAME_USERNAME),
    /**
     * Selects users by active state value.
     */
    @NamedQuery(name = User.NAMED_QUERY_BY_ACTIVESTATE, query = "SELECT u FROM User u WHERE u.active = :" + User.PARAMETER_NAME_ACTIVE),
    /**
     * Selects users where active state value does not match.
     */
    @NamedQuery(name = User.NAMED_QUERY_NOT_ACTIVESTATE, query = "SELECT u FROM User u WHERE u.active <> :" + User.PARAMETER_NAME_ACTIVE)
})
@Table(name = "user")
@SuppressWarnings("ValidPrimaryTableName")
public class User implements DbEntity {
    public static final short STATE_INACTIVE = 0;
    public static final short STATE_USER = 1;
    public static final short STATE_ADMIN = 2;
    
    //<editor-fold defaultstate="collapsed" desc="Query names">
    
    /**
     * Name of query that selects all users.
     */
    public static final String NAMED_QUERY_ALL = "User.findAll";
    /**
     * Name of query that selects user by primary key.
     */
    public static final String NAMED_QUERY_BY_ID = "User.findByUserId";
    /**
     * Name of query that selects user by active state value.
     */
    public static final String NAMED_QUERY_BY_ACTIVESTATE = "User.findByActive";
    /**
     * Name of query that selects user where active state value does not match.
     */
    public static final String NAMED_QUERY_NOT_ACTIVESTATE = "User.findNotActive";
    /**
     * Name of query that selects selects user by user name.
     */
    public static final String NAMED_QUERY_BY_USERNAME = "User.findByUserName";
    /**
     * Name of query that selects selects user by user name and not matching a specified userId.
     */
    public static final String NAMED_QUERY_BY_USERNAME_AVAIL = "User.findByUserNameAvailability";
    /**
     * Name of the single parameter for query that selects user by primary key.
     */
    public static final String PARAMETER_NAME_USERID = "userId";
    /**
     * Name of the single parameter for query that selects user by user name.
     */
    public static final String PARAMETER_NAME_USERNAME = "userName";
    /**
     * Name of the single parameter for query that selects user by active status.
     */
    public static final String PARAMETER_NAME_ACTIVE = "active";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="userId">
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer userId;
    
    /**
     * Gets the primary key value for the user.
     * @return The primary key value for the user or null if the user has not been inserted into the database.
     */
    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public Integer getPrimaryKey() { return userId; }
    
    @Override
    public void setPrimaryKey(Integer value) { userId = value; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="userName">
    
    @Basic(optional = false)
    private String userName;
    
    /**
     * Gets the user's login name.
     * @return The user's login name.
     */
    public String getUserName() { return userName; }
    
    /**
     * Sets the user's login name.
     * @param userName 
     */
    public void setUserName(String userName) { this.userName = userName; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="password">
    
    @Basic(optional = false)
    private String password;
    
    /**
     * Gets the hash for the user's password.
     * @return The 50-character hash for the user's password.
     */
    public String getPassword() { return password; }
    
    /**
     * Sets the hash for the user's password.
     * @param password 
     */
    public void setPassword(String password) { this.password = password; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="active">
    
    @Basic(optional = false)
    private short active;
    
    /**
     * Gets the active status value for the user.
     * @return The active status value for the user.
     */
    public short getActive() { return (active < STATE_INACTIVE || active > STATE_ADMIN) ? STATE_INACTIVE : active; }
    
    public boolean isActive() { return getActive() != STATE_INACTIVE; }
    
    public boolean isUser() { return getActive() == STATE_USER; }
    
    public boolean isAdmin() { return getActive() == STATE_ADMIN; }
    
    public void setActive(short active) {
        this.active = (active < STATE_INACTIVE || active > STATE_ADMIN) ? STATE_INACTIVE : active;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createDate">
    
    @Basic(optional = false)
    private Timestamp createDate;
    
    @Override
    public Timestamp getCreateDate() { return createDate; }
    
    @Override
    public void setCreateDate(Timestamp createDate) { this.createDate = createDate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createdBy">
    
    @Basic(optional = false)
    private String createdBy;
    
    @Override
    public String getCreatedBy() { return createdBy; }
    
    @Override
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdate">
    
    @Basic(optional = false)
    private Timestamp lastUpdate;
    
    @Override
    public Timestamp getLastUpdate() { return lastUpdate; }
    
    @Override
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdateBy">
    
    @Basic(optional = false)
    private String lastUpdateBy;
    
    @Override
    public String getLastUpdateBy() { return lastUpdateBy; }
    
    @Override
    public void setLastUpdateBy(String lastUpdateBy) { this.lastUpdateBy = lastUpdateBy; }
    
    //</editor-fold>
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<Appointment> appointments;
    
    @XmlTransient
    public Collection<Appointment> getAppointments() { return appointments; }

    public void setAppointments(Collection<Appointment> appointmentCollection) {
        appointments = appointmentCollection;
    }

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public User() {
        userName = password = "";
        active = STATE_USER;
        createDate = lastUpdate = Timestamp.valueOf(LocalDateTime.now());
        User u = scheduler.Context.getCurrentUser_entity();
        createdBy = lastUpdateBy = (u == null) ? "" : u.userName;
    }
    
    public User(int userId) {
        this.userId = userId;
        password = "";
        active = STATE_USER;
        createDate = lastUpdate = Timestamp.valueOf(LocalDateTime.now());
        User u = scheduler.Context.getCurrentUser_entity();
        createdBy = lastUpdateBy = (u == null) ? "" : u.userName;
    }
    
    public User(Integer userId, String userName, String password, short active, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.active = (active < STATE_INACTIVE || active > STATE_ADMIN) ? STATE_INACTIVE : active;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    //</editor-fold>
    
    @Override
    public int hashCode() { return (userId == null) ? 0 : userId.hashCode(); }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User))
            return false;
        User other = (User) object;
        return (userId == null) ? other.userId == null && userName.equals(other.userName) : other.userId != null && other.userId == userId;
    }

    @Override
    public String toString() { return "entity.User[ userId=" + userId + " ]"; }
}
