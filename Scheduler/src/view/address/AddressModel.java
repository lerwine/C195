/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.address;

import java.sql.Connection;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.AddressImpl;
import view.city.AddressCity;

/**
 *
 * @author erwinel
 */
public class AddressModel extends view.ItemModel<AddressImpl> implements CustomerAddress<AddressImpl> {

    private final ReadOnlyStringWrapper address1;

    @Override
    public ReadOnlyStringProperty address1Property() { return address1.getReadOnlyProperty(); }

    @Override
    public String getAddress1() { return address1.get(); }
    
    private final ReadOnlyStringWrapper address2;

    @Override
    public String getAddress2() { return address2.get(); }

    @Override
    public ReadOnlyStringProperty address2Property() { return address2.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<AddressCity<?>> city;

    @Override
    public AddressCity<?> getCity() { return city.get(); }

    @Override
    public ReadOnlyObjectProperty<AddressCity<?>> cityProperty() { return city.getReadOnlyProperty(); }
    
    @Override
    public String getCityName() { return city.get().getName(); }

    @Override
    public ReadOnlyStringProperty cityNameProperty() { return city.get().nameProperty(); }
    
    @Override
    public String getCountryName() { return city.get().getCountry().getName(); }

    @Override
    public ReadOnlyStringProperty countryNameProperty() { return city.get().getCountry().nameProperty(); }
    
    private final ReadOnlyStringWrapper postalCode;

    @Override
    public String getPostalCode() { return postalCode.get(); }

    @Override
    public ReadOnlyStringProperty postalCodeProperty() { return postalCode.getReadOnlyProperty(); }
    
    private final ReadOnlyStringWrapper phone;

    @Override
    public String getPhone() { return phone.get(); }

    @Override
    public ReadOnlyStringProperty phoneProperty() { return phone.getReadOnlyProperty(); }

    public AddressModel(AddressImpl dao) {
        super(dao);
        this.address1 = new ReadOnlyStringWrapper();
        this.address2 = new ReadOnlyStringWrapper();
        this.city = new ReadOnlyObjectWrapper<>();
        this.postalCode = new ReadOnlyStringWrapper();
        this.phone = new ReadOnlyStringWrapper();
    }

    @Override
    public void refreshFromDAO() {
        super.refreshFromDAO(); //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean delete(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveChanges(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
