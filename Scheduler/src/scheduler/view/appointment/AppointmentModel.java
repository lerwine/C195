package scheduler.view.appointment;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.IUserDAO;
import scheduler.model.AppointmentType;
import scheduler.model.ModelHelper;
import scheduler.model.UserStatus;
import scheduler.model.predefined.PredefinedAddress;
import scheduler.model.predefined.PredefinedData;
import scheduler.model.ui.AppointmentItem;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserItem;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.AppointmentTypeProperty;
import scheduler.observables.CalculatedStringExpression;
import scheduler.observables.CalculatedStringProperty;
import scheduler.observables.NestedBooleanProperty;
import scheduler.observables.NestedObjectValueProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.ObservableQuadruplet;
import scheduler.util.DB;
import scheduler.util.Quadruplet;
import scheduler.util.Values;
import scheduler.view.customer.RelatedCustomer;
import scheduler.view.user.RelatedUser;

/**
 * List item model for {@link AppointmentDAO} data access objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentModel extends FxRecordModel<AppointmentDAO> implements AppointmentItem<AppointmentDAO> {

    private static final Factory FACTORY = new Factory();

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
    private final NestedObjectValueProperty<UserItem<? extends IUserDAO>, UserStatus> userStatus;
    private final NestedStringProperty<UserItem<? extends IUserDAO>> userStatusDisplay;
    private final NonNullableStringProperty title;
    private final NonNullableStringProperty description;
    private final NonNullableStringProperty location;
    private final NonNullableStringProperty contact;
    private final AppointmentTypeProperty type;
    private final NonNullableStringProperty url;
    private final SimpleObjectProperty<LocalDateTime> start;
    private final SimpleObjectProperty<LocalDateTime> end;
    private final CalculatedStringProperty<Quadruplet<String, String, String, AppointmentType>> effectiveLocation;

    public AppointmentModel(AppointmentDAO dao) {
        super(dao);
        ICustomerDAO c = dao.getCustomer();
        customer = new SimpleObjectProperty<>(this, "customer", (null == c) ? null : new RelatedCustomer(c));
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
        IUserDAO u = dao.getUser();
        user = new SimpleObjectProperty<>(this, "user", (null == u) ? null : new RelatedUser(u));
        userName = new NestedStringProperty<>(this, "userName", user, (t) -> t.userNameProperty());
        userStatus = new NestedObjectValueProperty<>(this, "userStatus", user, (t) -> t.statusProperty());
        userStatusDisplay = new NestedStringProperty<>(this, "userStatusDisplay", user, (t) -> t.statusDisplayProperty());
        title = new NonNullableStringProperty(this, "title", dao.getTitle());
        description = new NonNullableStringProperty(this, "description", dao.getDescription());
        location = new NonNullableStringProperty(this, "location", dao.getLocation());
        contact = new NonNullableStringProperty(this, "contact", dao.getContact());
        type = new AppointmentTypeProperty(this, "type", dao.getType());
        url = new NonNullableStringProperty(this, "url", dao.getUrl());
        start = new SimpleObjectProperty<>(this, "start", DB.toLocalDateTime(dao.getStart()));
        end = new SimpleObjectProperty<>(this, "end", DB.toLocalDateTime(dao.getEnd()));
        effectiveLocation = new CalculatedStringProperty<>(this, "effectiveLocation", new ObservableQuadruplet<>(
                customerAddressText,
                new CalculatedStringExpression<>(location, Values::asNonNullAndWsNormalized),
                new CalculatedStringExpression<>(url, Values::asNonNullAndWsNormalized),
                type
        ), (t) -> {
            String s;
            switch (t.getValue4()) {
                case CUSTOMER_SITE:
                    s = t.getValue1();
                    if (s.isEmpty()) {
                        return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER);
                    }
                    break;
                case VIRTUAL:
                    s = t.getValue3();
                    if (s.isEmpty()) {
                        return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL);
                    }
                case CORPORATE_LOCATION:
                    s = t.getValue2();
                    PredefinedAddress a = PredefinedData.lookupAddress(s);
                    return (null == a) ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CORPORATE) : AddressTextProperty.convertToString(a);
                case PHONE:
                    s = t.getValue2();
                    if (s.isEmpty()) {
                        return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_PHONE);
                    }
                    return String.format("tel: %s", s);
                default:
                    s = t.getValue2();
                    break;
            }
            return s;
        });
        // CURRENT: Add validation properties
    }

    @Override
    protected void onDaoPropertyChanged(AppointmentDAO dao, String propertyName) {
        switch (propertyName) {
            case AppointmentDAO.PROP_CONTACT:
                contact.set(dao.getContact());
                break;
            case AppointmentDAO.PROP_CUSTOMER:
                ICustomerDAO c = dao.getCustomer();
                customer.set((null == c) ? null : new RelatedCustomer(c));
                break;
            case AppointmentDAO.PROP_DESCRIPTION:
                description.set(dao.getDescription());
                break;
            case AppointmentDAO.PROP_END:
                end.set(DB.toLocalDateTime(dao.getEnd()));
                break;
            case AppointmentDAO.PROP_LOCATION:
                location.set(dao.getLocation());
                break;
            case AppointmentDAO.PROP_START:
                start.set(DB.toLocalDateTime(dao.getStart()));
                break;
            case AppointmentDAO.PROP_TITLE:
                title.set(dao.getTitle());
                break;
            case AppointmentDAO.PROP_TYPE:
                type.set(dao.getType());
                break;
            case AppointmentDAO.PROP_URL:
                url.set(dao.getUrl());
                break;
            case AppointmentDAO.PROP_USER:
                IUserDAO u = dao.getUser();
                user.set((null == u) ? null : new RelatedUser(u));
                break;
        }
    }

    @Override
    protected void onDataObjectChanged(AppointmentDAO dao) {
        contact.set(dao.getContact());
        ICustomerDAO c = dao.getCustomer();
        customer.set((null == c) ? null : new RelatedCustomer(c));
        description.set(dao.getDescription());
        end.set(DB.toLocalDateTime(dao.getEnd()));
        location.set(dao.getLocation());
        start.set(DB.toLocalDateTime(dao.getStart()));
        title.set(dao.getTitle());
        IUserDAO u = dao.getUser();
        user.set((null == u) ? null : new RelatedUser(u));
        type.set(dao.getType());
        url.set(dao.getUrl());
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
        if (isNewItem()) {
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
            if (isNewItem()) {
                return customer.isEqualTo(other.customer).get() && user.isEqualTo(other.user).get() && title.isEqualTo(other.title).get()
                        && description.isEqualTo(other.description).get() && location.isEqualTo(other.location).get()
                        && contact.isEqualTo(other.contact).get() && type.isEqualTo(other.type).get() && url.isEqualTo(other.url).get()
                        && start.isEqualTo(other.start).get() && end.isEqualTo(other.end).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.appointment.AppointmentModel#isValid
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.appointment.AppointmentModel#validProperty
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
            LocalDateTime start = item.start.get();
            if (null == start) {
                throw new IllegalArgumentException("Appointment has no start date");
            }
            LocalDateTime end = item.end.get();
            if (null == end) {
                throw new IllegalArgumentException("Appointment has no end date");
            }
            if (start.compareTo(end) > 0) {
                throw new IllegalArgumentException("Appointment start date is after its end date");
            }
            String title = item.title.get();
            if (null == title || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Appointment has no title");
            }
            AppointmentType type = item.type.get();
            String location;
            String url = item.url.get();
            URI uri;
            if (url.trim().isEmpty()) {
                uri = null;
            } else {
                try {
                    uri = new URI(url);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(AppointmentModel.class.getName()).log(Level.WARNING, "Invalid URI", ex);
                    throw new IllegalArgumentException("Invalid URI");
                }
            }

            switch (type) {
                case CORPORATE_LOCATION:
                case CUSTOMER_SITE:
                    location = item.getEffectiveLocation();
                    break;
                case VIRTUAL:
                    if (null == uri) {
                        throw new IllegalArgumentException("Appointment has no URL");
                    }
                    location = item.getEffectiveLocation();
                    break;
                case PHONE:
                    location = item.location.get();
                    if (location.trim().isEmpty()) {
                        throw new IllegalArgumentException("Appointment has no phone");
                    }
                    break;
                default:
                    location = item.location.get();
                    if (location.trim().isEmpty()) {
                        throw new IllegalArgumentException("Appointment has no location");
                    }
                    break;
            }
            CustomerItem<? extends ICustomerDAO> customerModel = item.customer.get();
            if (null == customerModel) {
                throw new IllegalArgumentException("No associated customer");
            }
            UserItem<? extends IUserDAO> userModel = item.user.get();
            if (null == userModel) {
                throw new IllegalArgumentException("No associated user");
            }
            ICustomerDAO customerDAO = customerModel.getDataObject();
            if (ModelHelper.getRowState(customerDAO) == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated customer has been deleted");
            }
            IUserDAO userDAO = userModel.getDataObject();
            if (ModelHelper.getRowState(userDAO) == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated user has been deleted");
            }
            dao.setCustomer(customerDAO);
            dao.setUser(userDAO);
            dao.setTitle(title);
            dao.setType(type);
            dao.setContact(item.getContact());
            dao.setDescription(item.getDescription());
            dao.setStart(DB.toUtcTimestamp(start));
            dao.setEnd(DB.toUtcTimestamp(end));
            dao.setLocation(location);
            dao.setUrl(url);
            return dao;
        }

    }
}
