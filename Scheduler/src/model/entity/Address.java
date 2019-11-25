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
    @NamedQuery(name = Address.NAMED_QUERY_ALL, query = "SELECT a FROM Address a"),
    @NamedQuery(name = Address.NAMED_QUERY_BY_ID,
            query = "SELECT a FROM Address a WHERE a.addressId = :" + Address.PARAMETER_NAME_ADDRESSID),
    @NamedQuery(name = Address.NAMED_QUERY_BY_CITY,
            query = "SELECT a FROM Address a WHERE a.cityId = :" + Address.PARAMETER_NAME_CITYID)
})
@Table(name = "address")
public class Address implements DbEntity {
    public static final String NAMED_QUERY_ALL = "Address.findAll";
    public static final String NAMED_QUERY_BY_ID = "Address.findByAddressId";
    public static final String NAMED_QUERY_BY_CITY = "Address.findByCityId";
    public static final String PARAMETER_NAME_ADDRESSID = "addressId";
    public static final String PARAMETER_NAME_CITYID = "cityId";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer addressId;
    @Basic(optional = false)
    private String address;
    @Basic(optional = false)
    private String address2;
    @Basic(optional = false)
    private String postalCode;
    @Basic(optional = false)
    private String phone;
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
    @JoinColumn(name = "cityId", referencedColumnName = "cityId")
    @ManyToOne(optional = false)
    private City cityId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "addressId")
    private Collection<Customer> customers;

    public Address() {
    }

    public Address(Integer addressId) {
        this.addressId = addressId;
    }

    public Address(Integer addressId, String address, String address2, String postalCode, String phone, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.addressId = addressId;
        this.address = address;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    @Override
    public Integer getPrimaryKey() { return addressId; }
    
    @Override
    public void setPrimaryKey(Integer value) { addressId = value; }
    
    public Integer getAddressId() { return addressId; }

    public void setAddressId(Integer addressId) { this.addressId = addressId; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getAddress2() { return address2; }

    public void setAddress2(String address2) { this.address2 = address2; }

    public String getPostalCode() { return postalCode; }

    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

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

    public City getCityId() { return cityId; }

    public void setCityId(City cityId) { this.cityId = cityId; }

    @XmlTransient
    public Collection<Customer> getCustomers() { return customers; }

    public void setCustomers(Collection<Customer> customerCollection) {
        customers = customerCollection;
    }

    @Override
    public int hashCode() { return (addressId == null) ? 0 : addressId.hashCode();  }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Address)) {
            return false;
        }
        Address other = (Address) object;
        if ((this.addressId == null && other.addressId != null) || (this.addressId != null && !this.addressId.equals(other.addressId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Address[ addressId=" + addressId + " ]";
    }
    
}
