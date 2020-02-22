package scheduler.view.appointment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Appointment;
import scheduler.dao.Customer;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.User;
import scheduler.dao.UserImpl;
import scheduler.observables.AppointmentTypeDisplayProperty;
import scheduler.observables.AppointmentTypeProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.EffectiveLocationProperty;
import scheduler.util.DB;
import scheduler.view.customer.CustomerReferenceModel;
import scheduler.view.customer.CustomerReferenceModelImpl;
import scheduler.view.user.UserReferenceModel;
import scheduler.view.user.UserReferenceModelImpl;

/**
 *
 * @author lerwi
 */
public class AppointmentReferenceModelImpl extends DataObjectImpl.DataObjectReferenceModelImpl<Appointment> implements AppointmentReferenceModel<Appointment> {

    private final ReadOnlyObjectWrapper<CustomerReferenceModel<? extends Customer>> customer;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerName;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddress1;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddress2;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCityName;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCountryName;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerPostalCode;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerPhone;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddressText;
    private final ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCityZipCountry;
    private final ChildPropertyWrapper<Boolean, CustomerReferenceModel<? extends Customer>> customerActive;
    private final ReadOnlyObjectWrapper<UserReferenceModel<? extends User>> user;
    private final ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userName;
    private final ChildPropertyWrapper<Number, UserReferenceModel<? extends User>> userStatus;
    private final ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userStatusDisplay;
    private final ReadOnlyStringWrapper title;
    private final ReadOnlyStringWrapper description;
    private final ReadOnlyStringWrapper location;
    private final ReadOnlyStringWrapper contact;
    private final AppointmentTypeProperty type;
    private final AppointmentTypeDisplayProperty typeDisplay;
    private final ReadOnlyStringWrapper url;
    private final ReadOnlyObjectWrapper<LocalDateTime> start;
    private final ReadOnlyObjectWrapper<LocalDateTime> end;
    private final EffectiveLocationProperty effectiveLocation;

    public AppointmentReferenceModelImpl(Appointment dao) throws SQLException, ClassNotFoundException {
        super(dao);
        Customer c = dao.getCustomer().ensurePartial(CustomerImpl.getFactory());
        customer = new ReadOnlyObjectWrapper<>(this, "customer", (null == c) ? null : new CustomerReferenceModelImpl(c));
        customerName = new ChildPropertyWrapper<>(this, "customerName", customer, (t) -> t.nameProperty());
        customerAddress1 = new ChildPropertyWrapper<>(this, "customerAddress1", customer, (t) -> t.address1Property());
        customerAddress2 = new ChildPropertyWrapper<>(this, "customerAddress2", customer, (t) -> t.address2Property());
        customerCityName = new ChildPropertyWrapper<>(this, "customerCityName", customer, (t) -> t.cityNameProperty());
        customerCountryName = new ChildPropertyWrapper<>(this, "customerCountryName", customer, (t) -> t.countryNameProperty());
        customerPostalCode = new ChildPropertyWrapper<>(this, "customerPostalCode", customer, (t) -> t.postalCodeProperty());
        customerPhone = new ChildPropertyWrapper<>(this, "customerPhone", customer, (t) -> t.phoneProperty());
        customerCityZipCountry = new ChildPropertyWrapper<>(this, "customerCityZipCountry", customer, (t) -> t.cityZipCountryProperty());
        customerAddressText = new ChildPropertyWrapper<>(this, "customerAddressText", customer, (t) -> t.addressTextProperty());
        customerActive = new ChildPropertyWrapper<>(this, "customerActive", customer, (t) -> t.activeProperty());
        User u = dao.getUser().ensurePartial(UserImpl.getFactory());
        user = new ReadOnlyObjectWrapper<>(this, "user", (null == u) ? null : new UserReferenceModelImpl(u));
        userName = new ChildPropertyWrapper<>(this, "userName", user, (t) -> t.userNameProperty());
        userStatus = new ChildPropertyWrapper<>(this, "userStatus", user, (t) -> t.statusProperty());
        userStatusDisplay = new ChildPropertyWrapper<>(this, "phone", user, (t) -> t.statusDisplayProperty());
        title = new ReadOnlyStringWrapper(this, "title", dao.getTitle());
        description = new ReadOnlyStringWrapper(this, "description", dao.getDescription());
        location = new ReadOnlyStringWrapper(this, "location", dao.getLocation());
        contact = new ReadOnlyStringWrapper(this, "contact", dao.getContact());
        type = new AppointmentTypeProperty(this, "type", dao.getType());
        typeDisplay = new AppointmentTypeDisplayProperty(this, "typeDisplay", type);
        url = new ReadOnlyStringWrapper(this, "url", dao.getUrl());
        start = new ReadOnlyObjectWrapper<>(this, "start", DB.fromUtcTimestamp(dao.getStart()));
        end = new ReadOnlyObjectWrapper<>(this, "end", DB.fromUtcTimestamp(dao.getEnd()));
        effectiveLocation = new EffectiveLocationProperty(this, "effectiveLocation", this);
    }

    @Override
    public CustomerReferenceModel<? extends Customer> getCustomer() {
        return customer.get();
    }

    @Override
    public ReadOnlyProperty<CustomerReferenceModel<? extends Customer>> customerProperty() {
        return customer.getReadOnlyProperty();
    }

    @Override
    public String getCustomerName() {
        return customerName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerNameProperty() {
        return customerName;
    }

    @Override
    public String getCustomerAddress1() {
        return customerAddress1.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddress1Property() {
        return customerAddress1;
    }

    @Override
    public String getCustomerAddress2() {
        return customerAddress2.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddress2Property() {
        return customerAddress2;
    }

    @Override
    public String getCustomerCityName() {
        return customerCityName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCityNameProperty() {
        return customerCityName;
    }

    @Override
    public String getCustomerCountryName() {
        return customerCountryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCountryNameProperty() {
        return customerCountryName;
    }

    @Override
    public String getCustomerPostalCode() {
        return customerPostalCode.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerPostalCodeProperty() {
        return customerPostalCode;
    }

    @Override
    public String getCustomerPhone() {
        return customerPhone.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerPhoneProperty() {
        return customerPhone;
    }

    @Override
    public String getCustomerCityZipCountry() {
        return customerCityZipCountry.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerCityZipCountryProperty() {
        return customerCityZipCountry;
    }

    @Override
    public String getCustomerAddressText() {
        return customerAddressText.get();
    }

    @Override
    public ChildPropertyWrapper<String, CustomerReferenceModel<? extends Customer>> customerAddressTextProperty() {
        return customerAddressText;
    }

    @Override
    public boolean isCustomerActive() {
        return customerActive.get();
    }

    @Override
    public ChildPropertyWrapper<Boolean, CustomerReferenceModel<? extends Customer>> customerActiveProperty() {
        return customerActive;
    }

    @Override
    public UserReferenceModel<? extends User> getUser() {
        return user.get();
    }

    @Override
    public ReadOnlyProperty<UserReferenceModel<? extends User>> userProperty() {
        return user.getReadOnlyProperty();
    }

    @Override
    public String getUserName() {
        return userName.get();
    }

    @Override
    public ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userNameProperty() {
        return userName;
    }

    @Override
    public int getUserStatus() {
        return (int) userStatus.get();
    }

    @Override
    public ChildPropertyWrapper<Number, UserReferenceModel<? extends User>> userStatusProperty() {
        return userStatus;
    }

    @Override
    public String getUserStatusDisplay() {
        return userStatusDisplay.get();
    }

    @Override
    public ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userStatusDisplayProperty() {
        return userStatusDisplay;
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    @Override
    public ReadOnlyProperty<String> titleProperty() {
        return title.getReadOnlyProperty();
    }

    @Override
    public String getDescription() {
        return description.get();
    }

    @Override
    public ReadOnlyProperty<String> descriptionProperty() {
        return description.getReadOnlyProperty();
    }

    @Override
    public String getLocation() {
        return location.get();
    }

    @Override
    public ReadOnlyProperty<String> locationProperty() {
        return location.getReadOnlyProperty();
    }

    @Override
    public String getEffectiveLocation() {
        return effectiveLocation.get();
    }

    @Override
    public ReadOnlyProperty<String> effectiveLocationProperty() {
        return effectiveLocation;
    }

    @Override
    public String getContact() {
        return contact.get();
    }

    @Override
    public ReadOnlyProperty<String> contactProperty() {
        return contact.getReadOnlyProperty();
    }

    @Override
    public String getType() {
        return type.get();
    }

    @Override
    public ReadOnlyProperty<String> typeProperty() {
        return type.getReadOnlyProperty();
    }

    @Override
    public String getTypeDisplay() {
        return typeDisplay.get();
    }

    @Override
    public ReadOnlyProperty<String> typeDisplayProperty() {
        return typeDisplay;
    }

    @Override
    public String getUrl() {
        return url.get();
    }

    @Override
    public ReadOnlyProperty<String> urlProperty() {
        return url.getReadOnlyProperty();
    }

    @Override
    public LocalDateTime getStart() {
        return start.get();
    }

    @Override
    public ReadOnlyProperty<LocalDateTime> startProperty() {
        return start.getReadOnlyProperty();
    }

    @Override
    public LocalDateTime getEnd() {
        return end.get();
    }

    @Override
    public ReadOnlyProperty<LocalDateTime> endProperty() {
        return end.getReadOnlyProperty();
    }

}
