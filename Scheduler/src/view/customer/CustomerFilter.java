/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.customer;

import expressions.OptionalDataObjectProperty;
import expressions.OptionalValueProperty;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import scheduler.dao.Address;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectFilter;
import view.address.CustomerAddress;

/**
 *
 * @author erwinel
 */
public class CustomerFilter implements view.ModelFilter<CustomerImpl, CustomerModel> {

    //<editor-fold defaultstate="collapsed" desc="address property">
    
    private final OptionalDataObjectProperty<Address> address;
    
    public Optional<Address> getAddress() { return address.get(); }
    
    public void setAddress(Optional<Address> value) { address.set(value); }

    public OptionalDataObjectProperty<Address> addressProperty() { return address; }
    
    //</editor-fold>
    
    private final SimpleBooleanProperty addressNegated;

    public boolean isAddressNegated() { return addressNegated.get(); }

    public void setAddressNegated(boolean value) { addressNegated.set(value); }

    public BooleanProperty addressNegatedProperty() { return addressNegated; }
    
    //<editor-fold defaultstate="collapsed" desc="active property">
    
    private final OptionalValueProperty<Boolean> active;
    
    public Optional<Boolean> isActive() { return active.get(); }
    
    public void setActive(Optional<Boolean> value) { active.set(value); }
    
    public OptionalValueProperty<Boolean> activeProperty() { return active; }
    
    //</editor-fold>
    
    public CustomerFilter() { this(null); }
    
    public CustomerFilter(CustomerFilter other) {
        if (null == other) {
            address = new OptionalDataObjectProperty<>();
            active = new OptionalValueProperty<>();
            addressNegated = new SimpleBooleanProperty(false);
        } else {
            address = new OptionalDataObjectProperty<>(other.address.get());
            active = new OptionalValueProperty<>(other.active.get());
            addressNegated = new SimpleBooleanProperty(other.addressNegated.get());
        }
    }

    @Override
    public DataObjectFilter<CustomerImpl> createClone() { return new CustomerFilter(this); }
    
    @Override
    public int setWhereParameters(PreparedStatement ps, int startIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toWhereClause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean test(CustomerModel t) {
        return t != null && address.fromPresence((v) -> {
            CustomerAddress<?> c = t.getAddress();
            return c != null && (v.getPrimaryKey() == c.getDataObject().getPrimaryKey()) != addressNegated.get() &&
                    active.fromPresence((b) -> t.isActive() == b, () -> true);
        }, () -> active.fromPresence((b) -> t.isActive() == b, () -> true));
    }
    
}
