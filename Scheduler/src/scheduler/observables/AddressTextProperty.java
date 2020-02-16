/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.observables;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.Customer;
import scheduler.util.Values;
import scheduler.view.customer.CustomerReferenceModel;

/**
 *
 * @author lerwi
 */
public class AddressTextProperty extends StringBinding implements ReadOnlyProperty<String> {
    private final Object bean;
    private final String name;
    private final ReadOnlyProperty<String> address1;
    private final ReadOnlyProperty<String> address2;
    private final ReadOnlyProperty<String> cityZipCountry;
    private final ReadOnlyProperty<String> phone;
    
    public AddressTextProperty(Object bean, String name, CustomerReferenceModel<? extends Customer> customer) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        address1 = customer.address1Property();
        address2 = customer.address2Property();
        cityZipCountry = customer.cityZipCountryProperty();
        phone = customer.phoneProperty();
        super.bind(address1, address2, cityZipCountry, phone);
    }

    @Override
    protected String computeValue() {
        String a1 = Values.asNonNullAndWsNormalized(address1.getValue());
        String a2 = Values.asNonNullAndWsNormalized(address2.getValue());
        String c = Values.asNonNullAndWsNormalized(cityZipCountry.getValue());
        String p = Values.asNonNullAndWsNormalized(phone.getValue());
        if (a1.isEmpty()) {
            if (a2.isEmpty())
                return (c.isEmpty()) ? p : ((p.isEmpty()) ? c : String.format("%s, %s", c, p));
            if (c.isEmpty())
                return (p.isEmpty()) ? a2 : String.format("%s, %s", a2, p);
            return (p.isEmpty()) ? String.format("%s, %s", a2, c) : String.format("%s, %s, %s", a2, c, p);
        }
        if (a2.isEmpty()) {
            if (c.isEmpty())
                return (p.isEmpty()) ? a1 : String.format("%s, %s", a1, p);
            return (p.isEmpty()) ? String.format("%s, %s", a1, c) : String.format("%s, %s, %s", a1, c, p);
        }
        if (c.isEmpty())
            return (p.isEmpty()) ? String.format("%s, %s", a1, a2) : String.format("%s, %s, %s", a1, a2, p);
        return (p.isEmpty()) ? String.format("%s, %s, %s", a1, a2, c) : String.format("%s, %s, %s, %s", a1, a2, c, p);
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.observableArrayList(address1, address2, cityZipCountry, phone);
    }

    @Override
    public void dispose() {
        super.unbind(address1, address2, cityZipCountry, phone);
        super.dispose();
    }

}
