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
    @NamedQuery(name = City.NAMED_QUERY_ALL, query = "SELECT c FROM City c"),
    @NamedQuery(name = City.NAMED_QUERY_BY_ID, query = "SELECT c FROM City c WHERE c.cityId = :" + City.PARAMETER_NAME_CITYID),
    @NamedQuery(name = City.NAMED_QUERY_BY_COUNTRY, query = "SELECT c FROM City c WHERE c.countryId = :" + City.PARAMETER_NAME_COUNTRYID)
})
public class City implements DbEntity {
    public static final String NAMED_QUERY_ALL = "City.findAll";
    public static final String NAMED_QUERY_BY_ID = "City.findByCityId";
    public static final String NAMED_QUERY_BY_COUNTRY = "City.findByCountryId";
    public static final String PARAMETER_NAME_CITYID = "cityId";
    public static final String PARAMETER_NAME_COUNTRYID = "countryId";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer cityId;
    @Basic(optional = false)
    private String city;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cityId")
    private Collection<Address> addresses;
    @JoinColumn(name = "countryId", referencedColumnName = "countryId")
    @ManyToOne(optional = false)
    private Country countryId;

    public City() {
    }

    public City(Integer cityId) {
        this.cityId = cityId;
    }

    public City(Integer cityId, String city, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.cityId = cityId;
        this.city = city;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    @Override
    public Integer getPrimaryKey() { return cityId; }
    
    @Override
    public void setPrimaryKey(Integer value) { cityId = value; }
    
    public Integer getCityId() { return cityId; }

    public void setCityId(Integer cityId) { this.cityId = cityId; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

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
    public Collection<Address> getAddresses() { return addresses; }

    public void setAddresses(Collection<Address> addressCollection) {
        addresses = addressCollection;
    }

    public Country getCountryId() { return countryId; }

    public void setCountryId(Country countryId) { this.countryId = countryId; }

    @Override
    public int hashCode() { return (cityId == null) ? 0 : cityId.hashCode();  }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof City)) {
            return false;
        }
        City other = (City) object;
        if ((this.cityId == null && other.cityId != null) || (this.cityId != null && !this.cityId.equals(other.cityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.City[ cityId=" + cityId + " ]";
    }
    
}
