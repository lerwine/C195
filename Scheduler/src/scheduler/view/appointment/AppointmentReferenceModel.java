/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.Appointment;
import scheduler.dao.Customer;
import scheduler.dao.User;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.customer.CustomerReferenceModel;
import scheduler.view.user.UserReferenceModel;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface AppointmentReferenceModel<T extends Appointment> extends DataObjectReferenceModel<T> {

    CustomerReferenceModel<? extends Customer> getCustomer();

    ReadOnlyProperty<CustomerReferenceModel<? extends Customer>> customerProperty();

    String getCustomerName();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerNameProperty();

    String getCustomerAddress1();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddress1Property();

    String getCustomerAddress2();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddress2Property();

    String getCustomerCityName();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCityNameProperty();

    String getCustomerCountryName();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCountryNameProperty();

    String getCustomerPostalCode();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerPostalCodeProperty();

    String getCustomerPhone();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerPhoneProperty();

    String getCustomerCityZipCountry();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCityZipCountryProperty();

    String getCustomerAddressText();

    ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddressTextProperty();

    boolean isCustomerActive();

    ChildPropertyWrapper<Boolean, CustomerReferenceModel<? extends Customer>> customerActiveProperty();

    UserReferenceModel<? extends User> getUser();

    ReadOnlyProperty<UserReferenceModel<? extends User>> userProperty();

    String getUserName();

    ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userNameProperty();

    int getUserStatus();

    ChildPropertyWrapper<Number, UserReferenceModel<? extends User>> userStatusProperty();

    String getUserStatusDisplay();

    ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userStatusDisplayProperty();

    String getTitle();

    ReadOnlyProperty<String> titleProperty();

    String getDescription();

    ReadOnlyProperty<String> descriptionProperty();

    String getLocation();

    ReadOnlyProperty<String> locationProperty();

    String getEffectiveLocation();

    ReadOnlyProperty<String> effectiveLocationProperty();

    String getContact();

    ReadOnlyProperty<String> contactProperty();

    String getType();

    ReadOnlyProperty<String> typeProperty();

    String getTypeDisplay();

    ReadOnlyProperty<String> typeDisplayProperty();

    String getUrl();

    ReadOnlyProperty<String> urlProperty();

    LocalDateTime getStart();

    ReadOnlyProperty<LocalDateTime> startProperty();

    LocalDateTime getEnd();

    ReadOnlyProperty<LocalDateTime> endProperty();
}