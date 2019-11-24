/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.sql.Timestamp;
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
 *
 * @author Leonard T. Erwine
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DbUser.findAll", query = "SELECT u FROM DbUser u")
    , @NamedQuery(name = DbUser.NAMED_QUERY_BY_USERID, query = "SELECT u FROM DbUser u WHERE u.userId = :userId")
    , @NamedQuery(name = "DbUser.findByUserName", query = "SELECT u FROM DbUser u WHERE u.userName = :userName")
    , @NamedQuery(name = "DbUser.findByPassword", query = "SELECT u FROM DbUser u WHERE u.password = :password")
    , @NamedQuery(name = "DbUser.findByActive", query = "SELECT u FROM DbUser u WHERE u.active = :active")
    , @NamedQuery(name = "DbUser.findByCreateDate", query = "SELECT u FROM DbUser u WHERE u.createDate = :createDate")
    , @NamedQuery(name = "DbUser.findByCreatedBy", query = "SELECT u FROM DbUser u WHERE u.createdBy = :createdBy")
    , @NamedQuery(name = "DbUser.findByLastUpdate", query = "SELECT u FROM DbUser u WHERE u.lastUpdate = :lastUpdate")
    , @NamedQuery(name = "DbUser.findByLastUpdateBy", query = "SELECT u FROM DbUser u WHERE u.lastUpdateBy = :lastUpdateBy")})
@Table(name = "user")
public class DbUser implements Serializable {
    public static final String NAMED_QUERY_BY_USERID = "DbUser.findByUserId";
    public static final String NAMED_QUERY_BY_USERNAME = "DbUser.findByUserName";
    public static final String PARAMETER_NAME_USERID = "userId";
    public static final String PARAMETER_NAME_USERNAME = "userName";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer userId;
    @Basic(optional = false)
    private String userName;
    @Basic(optional = false)
    private String password;
    @Basic(optional = false)
    private short active;
    @Basic(optional = false)
    //@Temporal(TemporalType.TIMESTAMP)
    private Timestamp createDate;
    @Basic(optional = false)
    private String createdBy;
    @Basic(optional = false)
    //@Temporal(TemporalType.TIMESTAMP)
    private Timestamp lastUpdate;
    @Basic(optional = false)
    private String lastUpdateBy;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<Appointment> appointmentCollection;

    public DbUser() {
    }

    public DbUser(Integer userId) {
        this.userId = userId;
    }

    public DbUser(Integer userId, String userName, String password, short active, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.active = active;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public short getActive() {
        return active;
    }

    public void setActive(short active) {
        this.active = active;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    @XmlTransient
    public Collection<Appointment> getAppointmentCollection() {
        return appointmentCollection;
    }

    public void setAppointmentCollection(Collection<Appointment> appointmentCollection) {
        this.appointmentCollection = appointmentCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DbUser)) {
            return false;
        }
        DbUser other = (DbUser) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.User[ userId=" + userId + " ]";
    }
    
}
