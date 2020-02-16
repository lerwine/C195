/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.customer;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.Address;
import scheduler.dao.Customer;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.address.AddressReferenceModel;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface CustomerReferenceModel<T extends Customer> extends DataObjectReferenceModel<T> {

    String getName();

    ReadOnlyProperty<String> nameProperty();

    AddressReferenceModel<? extends Address> getAddress();

    ReadOnlyProperty<AddressReferenceModel<? extends Address>> addressProperty();

    String getAddress1();

    ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address1Property();

    String getAddress2();

    ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address2Property();

    String getCityName();

    ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityNameProperty();

    String getCountryName();

    ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> countryNameProperty();

    String getPostalCode();

    ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> postalCodeProperty();

    String getPhone();

    ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> phoneProperty();

    String getCityZipCountry();

    ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityZipCountryProperty();

    String getAddressText();

    ReadOnlyProperty<String> addressTextProperty();

    boolean isActive();

    ReadOnlyProperty<Boolean> activeProperty();
}
