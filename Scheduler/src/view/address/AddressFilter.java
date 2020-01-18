/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.address;

import expressions.OptionalDataObjectProperty;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import scheduler.dao.AddressImpl;
import scheduler.dao.City;
import scheduler.dao.DataObjectFilter;
import view.city.AddressCity;

/**
 *
 * @author erwinel
 */
public final class AddressFilter implements view.ModelFilter<AddressImpl, AddressModel> {
    
    //<editor-fold defaultstate="collapsed" desc="city property">
    
    private final OptionalDataObjectProperty<City> city;
    
    public Optional<City> getCity() { return city.get(); }
    
    public void setCity(Optional<City> value) { city.set(value); }

    public OptionalDataObjectProperty<City> cityProperty() { return city; }
    
    //</editor-fold>
    
    private final SimpleBooleanProperty cityNegated;

    public boolean isCityNegated() { return cityNegated.get(); }

    public void setCityNegated(boolean value) { cityNegated.set(value); }

    public BooleanProperty cityNegatedProperty() { return cityNegated; }
    
    public AddressFilter() { this(null); }
    
    public AddressFilter(AddressFilter other) {
        if (null == other) {
            city = new OptionalDataObjectProperty<>();
            cityNegated = new SimpleBooleanProperty(false);
        } else {
            city = new OptionalDataObjectProperty<>(other.city.get());
            cityNegated = new SimpleBooleanProperty(other.cityNegated.get());
        }
    }

    @Override
    public DataObjectFilter<AddressImpl> createClone() { return new AddressFilter(this); }

    @Override
    public int setWhereParameters(PreparedStatement ps, int startIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toWhereClause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean test(AddressModel t) {
        return t != null && city.fromPresence((v) -> {
            AddressCity<?> c = t.getCity();
            return c != null && (v.getPrimaryKey() == c.getDataObject().getPrimaryKey()) != cityNegated.get();
        }, () -> true);
    }

}
