package scheduler.view.appointment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.Customer;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.dao.User;
import scheduler.dao.UserImpl;
import scheduler.observables.AppointmentTypeDisplayProperty;
import scheduler.observables.AppointmentTypeProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.EffectiveLocationProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.util.DB;
import scheduler.view.ItemModel;
import scheduler.view.customer.CustomerReferenceModel;
import scheduler.view.customer.CustomerReferenceModelImpl;
import scheduler.view.user.UserReferenceModel;
import scheduler.view.user.UserReferenceModelImpl;

/**
 * List item model for {@link AppointmentImpl} data access objects.
 *
 * @author erwinel
 */
public final class AppointmentModel extends ItemModel<AppointmentImpl> implements AppointmentReferenceModel<AppointmentImpl> {

    private final SimpleObjectProperty<CustomerReferenceModel<? extends Customer>> customer;
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
    private final SimpleObjectProperty<UserReferenceModel<? extends User>> user;
    private final ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userName;
    private final ChildPropertyWrapper<Number, UserReferenceModel<? extends User>> userStatus;
    private final ChildPropertyWrapper<String, UserReferenceModel<? extends User>> userStatusDisplay;
    private final NonNullableStringProperty title;
    private final NonNullableStringProperty description;
    private final NonNullableStringProperty location;
    private final NonNullableStringProperty contact;
    private final AppointmentTypeProperty type;
    private final AppointmentTypeDisplayProperty typeDisplay;
    private final NonNullableStringProperty url;
    private final SimpleObjectProperty<LocalDateTime> start;
    private final SimpleObjectProperty<LocalDateTime> end;
    private final EffectiveLocationProperty effectiveLocation;

    public AppointmentModel(AppointmentImpl dao) throws SQLException, ClassNotFoundException {
        super(dao);
        Customer c = dao.getCustomer().ensurePartial(CustomerImpl.getFactory());
        customer = new SimpleObjectProperty<>(this, "customer", (null == c) ? null : new CustomerReferenceModelImpl(c));
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
        user = new SimpleObjectProperty<>(this, "user", (null == u) ? null : new UserReferenceModelImpl(u));
        userName = new ChildPropertyWrapper<>(this, "userName", user, (t) -> t.userNameProperty());
        userStatus = new ChildPropertyWrapper<>(this, "userStatus", user, (t) -> t.statusProperty());
        userStatusDisplay = new ChildPropertyWrapper<>(this, "phone", user, (t) -> t.statusDisplayProperty());
        title = new NonNullableStringProperty(this, "title", dao.getTitle());
        description = new NonNullableStringProperty(this, "description", dao.getDescription());
        location = new NonNullableStringProperty(this, "location", dao.getLocation());
        contact = new NonNullableStringProperty(this, "contact", dao.getContact());
        type = new AppointmentTypeProperty(this, "type", dao.getType());
        typeDisplay = new AppointmentTypeDisplayProperty(this, "typeDisplay", type);
        url = new NonNullableStringProperty(this, "url", dao.getUrl());
        start = new SimpleObjectProperty<>(this, "start", DB.fromUtcTimestamp(dao.getStart()));
        end = new SimpleObjectProperty<>(this, "end", DB.fromUtcTimestamp(dao.getEnd()));
        effectiveLocation = new EffectiveLocationProperty(this, "effectiveLocation", this);
    }

    @Override
    public CustomerReferenceModel<? extends Customer> getCustomer() {
        return customer.get();
    }

    public void setCustomer(CustomerReferenceModel<? extends Customer> value) {
        customer.set(value);
    }

    @Override
    public ObjectProperty<CustomerReferenceModel<? extends Customer>> customerProperty() {
        return customer;
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

    public void setUser(UserReferenceModel<? extends Customer> value) {
        user.set(value);
    }

    @Override
    public ObjectProperty<UserReferenceModel<? extends User>> userProperty() {
        return user;
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

    public void setTitle(String value) {
        title.set(value);
    }

    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    @Override
    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String getLocation() {
        return location.get();
    }

    public void setLocation(String value) {
        location.set(value);
    }

    @Override
    public StringProperty locationProperty() {
        return location;
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

    public void setContact(String value) {
        contact.set(value);
    }

    @Override
    public StringProperty contactProperty() {
        return contact;
    }

    @Override
    public String getType() {
        return type.get();
    }

    public void setType(String value) {
        type.set(value);
    }

    @Override
    public AppointmentTypeProperty typeProperty() {
        return type;
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

    public void setUrl(String value) {
        url.set(value);
    }

    @Override
    public StringProperty urlProperty() {
        return url;
    }

    @Override
    public LocalDateTime getStart() {
        return start.get();
    }

    public void setStart(LocalDateTime value) {
        start.set(value);
    }

    @Override
    public ObjectProperty<LocalDateTime> startProperty() {
        return start;
    }

    @Override
    public LocalDateTime getEnd() {
        return end.get();
    }

    public void setEnd(LocalDateTime value) {
        end.set(value);
    }

    @Override
    public ObjectProperty<LocalDateTime> endProperty() {
        return end;
    }

    @Override
    protected void refreshFromDAO(AppointmentImpl dao) throws SQLException, ClassNotFoundException {
        Customer c = dao.getCustomer().ensurePartial(CustomerImpl.getFactory());
        customer.set((null == c) ? null : new CustomerReferenceModelImpl(c));
        User u = dao.getUser().ensurePartial(UserImpl.getFactory());
        user.set((null == u) ? null : new UserReferenceModelImpl(u));
        title.set(dao.getTitle());
        description.set(dao.getDescription());
        location.set(dao.getLocation());
        contact.set(dao.getContact());
        type.set(dao.getType());
        url.set(dao.getUrl());
        start.set(DB.fromUtcTimestamp(dao.getStart()));
        end.set(DB.fromUtcTimestamp(dao.getEnd()));
    }

    @Override
    public Factory<AppointmentImpl> getDaoFactory() {
        return AppointmentImpl.getFactory();
    }

}
