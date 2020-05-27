package scheduler.model.ui;

import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.AppointmentType;
import scheduler.model.PredefinedData;
import scheduler.model.UserStatus;
import scheduler.observables.AppointmentTypeProperty;
import scheduler.observables.NestedBooleanProperty;
import scheduler.observables.NestedObjectProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.ObservableBooleanDerivitive;
import scheduler.observables.ObservableDerivitive;
import scheduler.observables.ObservableStringDerivitive;
import scheduler.observables.WrappedBooleanObservableProperty;
import scheduler.observables.WrappedStringObservableProperty;
import scheduler.util.DB;
import scheduler.view.appointment.AppointmentModelFilter;

/**
 * List item model for {@link AppointmentDAO} data access objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentModel extends FxRecordModel<AppointmentDAO> implements AppointmentItem<AppointmentDAO> {

    private static final Factory FACTORY = new Factory();

    public static String calculateEffectiveLocation(AppointmentType type, String customerAddress, String url, String location) {
        switch (type) {
            case CUSTOMER_SITE:
                return (customerAddress.isEmpty()) ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER)
                        : customerAddress;
            case VIRTUAL:
                return (url.isEmpty()) ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL)
                        : url;
            case CORPORATE_LOCATION:
                AddressDAO a = PredefinedData.lookupAddress(location);
                return AddressModel.calculateSingleLineAddress(a.getAddress1(), a.getAddress2(),
                                AddressModel.calculateCityZipCountry(a.getCity(), a.getPostalCode()), a.getPhone());
            case PHONE:
                return (location.isEmpty()) ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_PHONE)
                        : String.format("tel: %s", location);
            default:
                return location;
        }
    }

    public static int compareByDates(AppointmentModel a, AppointmentModel b) {
        if (null == a) {
            return (null == b) ? 0 : 1;
        }
        if (null == b) {
            return -1;
        }
        LocalDateTime x = a.getStart();
        LocalDateTime y = b.getStart();
        if (null == x) {
            return (null == x) ? 0 : 1;
        }
        if (null == y) {
            return -1;
        }
        int c = x.compareTo(y);
        if (c != 0) {
            return c;
        }
        x = a.getEnd();
        y = b.getEnd();
        if (null == x) {
            return (null == x) ? 0 : 1;
        }
        if (null == y) {
            return -1;
        }
        return x.compareTo(y);
    }

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final SimpleObjectProperty<CustomerItem<? extends ICustomerDAO>> customer;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerName;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerAddress1;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerAddress2;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerCityName;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerCountryName;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerPostalCode;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerPhone;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerAddressText;
    private final NestedStringProperty<CustomerItem<? extends ICustomerDAO>> customerCityZipCountry;
    private final NestedBooleanProperty<CustomerItem<? extends ICustomerDAO>> customerActive;
    private final SimpleObjectProperty<UserItem<? extends IUserDAO>> user;
    private final NestedStringProperty<UserItem<? extends IUserDAO>> userName;
    private final NestedObjectProperty<UserItem<? extends IUserDAO>, UserStatus> userStatus;
    private final NestedStringProperty<UserItem<? extends IUserDAO>> userStatusDisplay;
    private final NonNullableStringProperty title;
    private final NonNullableStringProperty description;
    private final NonNullableStringProperty location;
    private final NonNullableStringProperty contact;
    private final AppointmentTypeProperty type;
    private final NonNullableStringProperty url;
    private final SimpleObjectProperty<LocalDateTime> start;
    private final SimpleObjectProperty<LocalDateTime> end;
    private final WrappedStringObservableProperty effectiveLocation;
    private final WrappedBooleanObservableProperty valid;

    @SuppressWarnings("incomplete-switch")
    public AppointmentModel(AppointmentDAO dao) {
        super(dao);
        ICustomerDAO customerDao = dao.getCustomer();
        customer = new SimpleObjectProperty<>(this, "customer", (null == customerDao) ? null : new RelatedCustomer(customerDao));
        customerName = new NestedStringProperty<>(this, "customerName", customer, (t) -> t.nameProperty());
        customerAddress1 = new NestedStringProperty<>(this, "customerAddress1", customer, (t) -> t.address1Property());
        customerAddress2 = new NestedStringProperty<>(this, "customerAddress2", customer, (t) -> t.address2Property());
        customerCityName = new NestedStringProperty<>(this, "customerCityName", customer, (t) -> t.cityNameProperty());
        customerCountryName = new NestedStringProperty<>(this, "customerCountryName", customer, (t) -> t.countryNameProperty());
        customerPostalCode = new NestedStringProperty<>(this, "customerPostalCode", customer, (t) -> t.postalCodeProperty());
        customerPhone = new NestedStringProperty<>(this, "customerPhone", customer, (t) -> t.phoneProperty());
        customerCityZipCountry = new NestedStringProperty<>(this, "customerCityZipCountry", customer, (t) -> t.cityZipCountryProperty());
        customerAddressText = new NestedStringProperty<>(this, "customerAddressText", customer, (t) -> t.addressTextProperty());
        customerActive = new NestedBooleanProperty<>(this, "customerActive", customer, (t) -> t.activeProperty());
        IUserDAO userDao = dao.getUser();
        user = new SimpleObjectProperty<>(this, "user", (null == userDao) ? null : new RelatedUser(userDao));
        userName = new NestedStringProperty<>(this, "userName", user, (t) -> t.userNameProperty());
        userStatus = new NestedObjectProperty<>(this, "userStatus", user, (t) -> t.statusProperty());
        userStatusDisplay = new NestedStringProperty<>(this, "userStatusDisplay", user, (t) -> t.statusDisplayProperty());
        title = new NonNullableStringProperty(this, "title", dao.getTitle());
        description = new NonNullableStringProperty(this, "description", dao.getDescription());
        location = new NonNullableStringProperty(this, "location", dao.getLocation());
        AppointmentType at = dao.getType();
        type = new AppointmentTypeProperty(this, "type", (null == at) ? AppointmentType.OTHER : at);
        contact = new NonNullableStringProperty(this, "contact", dao.getContact());
        url = new NonNullableStringProperty(this, "url", dao.getUrl());
        start = new SimpleObjectProperty<>(this, "start", DB.toLocalDateTime(dao.getStart()));
        end = new SimpleObjectProperty<>(this, "end", DB.toLocalDateTime(dao.getEnd()));

        ObservableStringDerivitive locZ = ObservableDerivitive.wsNormalized(location);
        effectiveLocation = new WrappedStringObservableProperty(this, "effectiveLocation", ObservableStringDerivitive.of(type, locZ, customerAddressText,
                ObservableDerivitive.wsNormalized(url),
                (t, l, c, u) -> {
                    if (null != t) {
                        switch (t) {
                            case CORPORATE_LOCATION:
                                if (!l.isEmpty()) {
                                    AddressDAO a = PredefinedData.lookupAddress(l);
                                    return AddressModel.calculateMultiLineAddress(
                                            AddressModel.calculateAddressLines(a.getAddress1(), a.getAddress2()),
                                            AddressModel.calculateCityZipCountry(a.getCity(), a.getPostalCode()),
                                            a.getPhone()
                                    );
                                }
                                break;
                            case CUSTOMER_SITE:
                                return c;
                            case VIRTUAL:
                                return u;
                        }
                    }
                    return l;
                }));

        valid = new WrappedBooleanObservableProperty(this, "valid", locZ.isNotNullOrEmpty().and(
                ObservableBooleanDerivitive.ofNested(customer, (s) -> s.validProperty(), false),
                ObservableBooleanDerivitive.ofNested(user, (s) -> s.validProperty(), false),
                ObservableDerivitive.isNotNullOrWhiteSpace(title),
                ObservableBooleanDerivitive.of(start, end, (s, e) -> null != s && null != e && s.compareTo(e) <= 0),
                ObservableBooleanDerivitive.of(type, location, contact, url, (AppointmentType t, String l, String c, String u) -> {
                    if (null == t || l.isEmpty()) {
                        return false;
                    }
                    switch (t) {
                        case CUSTOMER_SITE:
                            if (c.isEmpty()) {
                                return false;
                            }
                            break;
                        case VIRTUAL:
                            if (u.isEmpty()) {
                                return false;
                            }
                            break;
                    }
                    return true;
                })
        ));
    }

    @Override
    public CustomerItem<? extends ICustomerDAO> getCustomer() {
        return customer.get();
    }

    public void setCustomer(CustomerItem<? extends ICustomerDAO> value) {
        customer.set(value);
    }

    @Override
    public ObjectProperty<? extends CustomerItem<? extends ICustomerDAO>> customerProperty() {
        return customer;
    }

    @Override
    public String getCustomerName() {
        return customerName.get();
    }

    @Override
    public ReadOnlyStringProperty customerNameProperty() {
        return customerName.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerAddress1() {
        return customerAddress1.get();
    }

    @Override
    public ReadOnlyStringProperty customerAddress1Property() {
        return customerAddress1.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerAddress2() {
        return customerAddress2.get();
    }

    @Override
    public ReadOnlyStringProperty customerAddress2Property() {
        return customerAddress2.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerCityName() {
        return customerCityName.get();
    }

    @Override
    public ReadOnlyStringProperty customerCityNameProperty() {
        return customerCityName.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerCountryName() {
        return customerCountryName.get();
    }

    @Override
    public ReadOnlyStringProperty customerCountryNameProperty() {
        return customerCountryName.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerPostalCode() {
        return customerPostalCode.get();
    }

    @Override
    public ReadOnlyStringProperty customerPostalCodeProperty() {
        return customerPostalCode.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerPhone() {
        return customerPhone.get();
    }

    @Override
    public ReadOnlyStringProperty customerPhoneProperty() {
        return customerPhone.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerCityZipCountry() {
        return customerCityZipCountry.get();
    }

    @Override
    public ReadOnlyStringProperty customerCityZipCountryProperty() {
        return customerCityZipCountry.getReadOnlyStringProperty();
    }

    @Override
    public String getCustomerAddressText() {
        return customerAddressText.get();
    }

    @Override
    public ReadOnlyStringProperty customerAddressTextProperty() {
        return customerAddressText.getReadOnlyStringProperty();
    }

    @Override
    public boolean isCustomerActive() {
        return customerActive.get();
    }

    @Override
    public ReadOnlyBooleanProperty customerActiveProperty() {
        return customerActive.getReadOnlyBooleanProperty();
    }

    @Override
    public UserItem<? extends IUserDAO> getUser() {
        return user.get();
    }

    public void setUser(UserItem<? extends IUserDAO> value) {
        user.set(value);
    }

    @Override
    public ObjectProperty<UserItem<? extends IUserDAO>> userProperty() {
        return user;
    }

    @Override
    public String getUserName() {
        return userName.get();
    }

    @Override
    public ReadOnlyStringProperty userNameProperty() {
        return userName.getReadOnlyStringProperty();
    }

    @Override
    public UserStatus getUserStatus() {
        return (UserStatus) userStatus.get();
    }

    @Override
    public ReadOnlyObjectProperty<UserStatus> userStatusProperty() {
        return userStatus.getReadOnlyObjectProperty();
    }

    @Override
    public String getUserStatusDisplay() {
        return userStatusDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty userStatusDisplayProperty() {
        return userStatusDisplay.getReadOnlyStringProperty();
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
    public ReadOnlyStringProperty effectiveLocationProperty() {
        return effectiveLocation.getReadOnlyStringProperty();
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
    public AppointmentType getType() {
        return type.get();
    }

    public void setType(AppointmentType value) {
        type.set(value);
    }

    @Override
    public SimpleObjectProperty<AppointmentType> typeProperty() {
        return type;
    }

    @Override
    public String getTypeDisplay() {
        return type.getDisplayText();
    }

    @Override
    public ReadOnlyStringProperty typeDisplayProperty() {
        return type.displayTextProperty();
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
    public int hashCode() {
        if (isNewRow()) {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(customer.get());
            hash = 71 * hash + Objects.hashCode(user.get());
            hash = 71 * hash + Objects.hashCode(title.get());
            hash = 71 * hash + Objects.hashCode(description.get());
            hash = 71 * hash + Objects.hashCode(location.get());
            hash = 71 * hash + Objects.hashCode(contact.get());
            hash = 71 * hash + Objects.hashCode(type.get());
            hash = 71 * hash + Objects.hashCode(url.get());
            hash = 71 * hash + Objects.hashCode(start.get());
            hash = 71 * hash + Objects.hashCode(end.get());
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof AppointmentModel) {
            final AppointmentModel other = (AppointmentModel) obj;
            if (isNewRow()) {
                return customer.isEqualTo(other.customer).get() && user.isEqualTo(other.user).get() && title.isEqualTo(other.title).get()
                        && description.isEqualTo(other.description).get() && location.isEqualTo(other.location).get()
                        && contact.isEqualTo(other.contact).get() && type.isEqualTo(other.type).get() && url.isEqualTo(other.url).get()
                        && start.isEqualTo(other.start).get() && end.isEqualTo(other.end).get();
            }
            return !other.isNewRow() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    public final static class Factory extends FxRecordModel.ModelFactory<AppointmentDAO, AppointmentModel> {

        private Factory() {
        }

        @Override
        public DaoFactory<AppointmentDAO> getDaoFactory() {
            return AppointmentDAO.getFactory();
        }

        @Override
        public AppointmentModel createNew(AppointmentDAO dao) {
            return new AppointmentModel(dao);
        }

        @Override
        public AppointmentModelFilter getAllItemsFilter() {
            return AppointmentModelFilter.all();
        }

        @Override
        public AppointmentModelFilter getDefaultFilter() {
            return AppointmentModelFilter.myCurrentAndFuture();
        }

        @Override
        public AppointmentDAO updateDAO(AppointmentModel item) {
            AppointmentDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Appointment has been deleted");
            }
            if (!item.isValid()) {
                throw new IllegalStateException();
            }

            dao.setCustomer(item.getCustomer().getDataObject());
            dao.setUser(item.getUser().getDataObject());
            dao.setTitle(item.getTitle());
            dao.setDescription(item.getDescription());
            dao.setContact(item.getContact());
            dao.setEnd(DB.toUtcTimestamp(item.getEnd()));
            dao.setLocation(item.getLocation());
            dao.setStart(DB.toUtcTimestamp(item.getStart()));
            dao.setType(item.getType());
            dao.setUrl(item.getUrl());
            return dao;
        }

        @Override
        protected void updateItemProperties(AppointmentModel item, AppointmentDAO dao) {
            ICustomerDAO c = dao.getCustomer();
            item.setCustomer((null == c) ? null : ((c instanceof CustomerDAO) ? new CustomerModel((CustomerDAO) c) : new RelatedCustomer(c)));
            IUserDAO u = dao.getUser();
            item.setUser((null == u) ? null : ((u instanceof UserDAO) ? new UserModel((UserDAO) u) : new RelatedUser(u)));
            item.setTitle(dao.getTitle());
            item.setDescription(dao.getDescription());
            item.setLocation(dao.getLocation());
            item.setContact(dao.getContact());
            item.setType(dao.getType());
            item.setUrl(dao.getUrl());
            item.setStart(item.getStart());
            item.setEnd(item.getEnd());
        }

    }
}
