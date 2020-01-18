/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.city;

import expressions.OptionalDataObjectProperty;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import scheduler.dao.CityImpl;
import scheduler.dao.Country;
import scheduler.dao.DataObjectFilter;
import view.country.CityCountry;

/**
 *
 * @author erwinel
 */
public final class CityFilter implements view.ModelFilter<CityImpl, CityModel> {
    
    //<editor-fold defaultstate="collapsed" desc="country property">
    
    private final OptionalDataObjectProperty<Country> country;
    
    public Optional<Country> getCountry() { return country.get(); }
    
    public void setCountry(Optional<Country> value) { country.set(value); }
    
    public OptionalDataObjectProperty<Country> countryProperty() { return country; }
    
    //</editor-fold>
    
    private final SimpleBooleanProperty countryNegated;

    public boolean isCountryNegated() { return countryNegated.get(); }

    public void setCountryNegated(boolean value) { countryNegated.set(value); }

    public BooleanProperty countryNegatedProperty() { return countryNegated; }
    
    public CityFilter() { this(null); }
    
    public CityFilter(CityFilter other) {
        if (null == other) {
            country = new OptionalDataObjectProperty<>();
            countryNegated = new SimpleBooleanProperty(false);
        } else {
            country = new OptionalDataObjectProperty<>(other.country.get());
            countryNegated = new SimpleBooleanProperty(other.countryNegated.get());
            
        }
    }

    @Override
    public DataObjectFilter<CityImpl> createClone() { return new CityFilter(this); }

    @Override
    public int setWhereParameters(PreparedStatement ps, int startIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toWhereClause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean test(CityModel t) {
        return t != null && country.fromPresence((v) -> {
            CityCountry<?> c = t.getCountry();
            return c != null && (v.getPrimaryKey() == c.getDataObject().getPrimaryKey()) != countryNegated.get();
        }, () -> true);
    }
    
}
