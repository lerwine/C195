/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;

import model.entity.City;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Leonard T. Erwine
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = Country.NAMED_QUERY_ALL, query = "SELECT c FROM Country c"),
    @NamedQuery(name = Country.NAMED_QUERY_BY_ID, query = "SELECT c FROM Country c WHERE c.countryId = :" + Country.PARAMETER_NAME_COUNTRYID)
})
public class Country implements DbEntity {
    public static final String NAMED_QUERY_ALL = "Country.findAll";
    public static final String NAMED_QUERY_BY_ID = "Country.findByCountryId";
    public static final String PARAMETER_NAME_COUNTRYID = "countryId";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer countryId;
    @Basic(optional = false)
    private String country;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "countryId")
    private Collection<City> cities;

    public Country() {
    }

    public Country(Integer countryId) {
        this.countryId = countryId;
    }

    public Country(Integer countryId, String country, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.countryId = countryId;
        this.country = country;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    @Override
    public Integer getPrimaryKey() { return countryId; }
    
    @Override
    public void setPrimaryKey(Integer value) { countryId = value; }
    
    public Integer getCountryId() { return countryId; }

    public void setCountryId(Integer countryId) { this.countryId = countryId; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

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
    public Collection<City> getCities() {
        return cities;
    }

    public void setCities(Collection<City> cityCollection) {
        this.cities = cityCollection;
    }

    @Override
    public int hashCode() { return (countryId == null) ? 0 : countryId.hashCode();  }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Country)) {
            return false;
        }
        Country other = (Country) object;
        if ((this.countryId == null && other.countryId != null) || (this.countryId != null && !this.countryId.equals(other.countryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Country[ countryId=" + countryId + " ]";
    }
    
}
