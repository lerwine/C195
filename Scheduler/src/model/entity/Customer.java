/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;

import java.util.Collection;
import java.sql.Timestamp;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Leonard T. Erwine
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findByCustomerId", query = "SELECT c FROM Customer c WHERE c.customerId = :" + Customer.PARAMETER_NAME_CUSTOMERID),
    @NamedQuery(name = "Customer.findByActive", query = "SELECT c FROM Customer c WHERE c.active = :" + Customer.PARAMETER_NAME_ACTIVE),
    @NamedQuery(name = "Customer.findByAddressId", query = "SELECT c FROM Customer c WHERE c.addressId = :" + Customer.PARAMETER_NAME_ADDRESSID)
})
public class Customer implements DbEntity {
    public static final String NAMED_QUERY_ALL = "Customer.findAll";
    public static final String NAMED_QUERY_BY_ID = "Customer.findByCountryId";
    public static final String NAMED_QUERY_BY_ACTIVE = "Customer.findByActive";
    public static final String NAMED_QUERY_BY_ADDRESS = "Customer.findByAddressId";
    public static final String PARAMETER_NAME_ACTIVE = "active";
    public static final String PARAMETER_NAME_CUSTOMERID = "customerId";
    public static final String PARAMETER_NAME_ADDRESSID = "addressId";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer customerId;
    @Basic(optional = false)
    private String customerName;
    @Basic(optional = false)
    private boolean active;
    @Basic(optional = false)
    private Timestamp createDate;
    @Basic(optional = false)
    private String createdBy;
    @Basic(optional = false)
    private Timestamp lastUpdate;
    @Basic(optional = false)
    private String lastUpdateBy;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customerId")
    private Collection<Appointment> appointments;
    @JoinColumn(name = "addressId", referencedColumnName = "addressId")
    @ManyToOne(optional = false)
    private Address addressId;

    public Customer() {
    }

    public Customer(Integer customerId) {
        this.customerId = customerId;
    }

    public Customer(Integer customerId, String customerName, boolean active, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.active = active;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    @Override
    public Integer getPrimaryKey() { return customerId; }
    
    @Override
    public void setPrimaryKey(Integer value) { customerId = value; }
    
    public Integer getCustomerId() { return customerId; }

    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public boolean getActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    @Override
    public Timestamp getCreateDate() { return createDate; }

    @Override
    public void setCreateDate(Timestamp createDate) { this.createDate = createDate; }

    @Override
    public String getCreatedBy() { return createdBy; }

    @Override
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    @Override
    public Timestamp getLastUpdate() { return lastUpdate; }

    @Override
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }

    @Override
    public String getLastUpdateBy() { return lastUpdateBy; }

    @Override
    public void setLastUpdateBy(String lastUpdateBy) { this.lastUpdateBy = lastUpdateBy; }

    @XmlTransient
    public Collection<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Collection<Appointment> appointmentCollection) {
        appointments = appointmentCollection;
    }

    public Address getAddressId() { return addressId; }

    public void setAddressId(Address addressId) { this.addressId = addressId; }

    @Override
    public int hashCode() { return (customerId == null) ? 0 : customerId.hashCode();  }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Customer[ customerId=" + customerId + " ]";
    }
    
}
