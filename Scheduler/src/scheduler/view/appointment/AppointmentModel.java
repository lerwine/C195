package scheduler.view.appointment;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.AppointmentDAO;
import scheduler.model.AppointmentType;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.model.UserStatus;
import scheduler.observables.AppointmentTypeDisplayProperty;
import scheduler.observables.AppointmentTypeProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.EffectiveLocationProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.util.DB;
import scheduler.view.city.SupportedLocale;
import scheduler.view.model.ItemModel;
import scheduler.view.user.RelatedUserModel;
import scheduler.model.db.CustomerRowData;
import scheduler.model.db.UserRowData;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.UserItem;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.customer.RelatedCustomerModel;

/**
 * List item model for {@link AppointmentDAO} data access objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentModel extends ItemModel<AppointmentDAO> {

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

    static ZoneId getZoneId(AppointmentModel model) {
        if (null != model) {
            ZoneId result;
            switch (model.getType()) {
                case CORPORATE_HQ_MEETING:
                    result = SupportedLocale.toZoneId(SupportedLocale.EN);
                    break;
                case GERMANY_SITE_MEETING:
                    result = SupportedLocale.toZoneId(SupportedLocale.DE);
                    break;
                case GUATEMALA_SITE_MEETING:
                    result = SupportedLocale.toZoneId(SupportedLocale.ES);
                    break;
                case INDIA_SITE_MEETING:
                    result = SupportedLocale.toZoneId(SupportedLocale.HI);
                    break;
                case CUSTOMER_SITE:
                    return CustomerModelImpl.getZoneId(model.getCustomer());
                default:
                    result = null;
                    break;
            }
            if (null != result) {
                return result;
            }
        }
        return ZoneId.systemDefault();
    }

    private final SimpleObjectProperty<CustomerItem<? extends CustomerRowData>> customer;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerName;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerAddress1;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerAddress2;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerCityName;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerCountryName;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerPostalCode;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerPhone;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerAddressText;
    private final ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerCityZipCountry;
    private final ChildPropertyWrapper<Boolean, CustomerItem<? extends CustomerRowData>> customerActive;
    private final SimpleObjectProperty<UserItem<? extends UserRowData>> user;
    private final ChildPropertyWrapper<String, UserItem<? extends UserRowData>> userName;
    private final ChildPropertyWrapper<UserStatus, UserItem<? extends UserRowData>> userStatus;
    private final ChildPropertyWrapper<String, UserItem<? extends UserRowData>> userStatusDisplay;
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

    public AppointmentModel(AppointmentDAO dao) {
        super(dao);
        CustomerRowData c = dao.getCustomer();
        customer = new SimpleObjectProperty<>(this, "customer", (null == c) ? null : new RelatedCustomerModel(c));
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
        UserRowData u = dao.getUser();
        user = new SimpleObjectProperty<>(this, "user", (null == u) ? null : new RelatedUserModel(u));
        userName = new ChildPropertyWrapper<>(this, "userName", user, (t) -> t.userNameProperty());
        userStatus = new ChildPropertyWrapper<>(this, "userStatus", user, (t) -> t.statusProperty());
        userStatusDisplay = new ChildPropertyWrapper<>(this, "userStatusDisplay", user, (t) -> t.statusDisplayProperty());
        title = new NonNullableStringProperty(this, "title", dao.getTitle());
        description = new NonNullableStringProperty(this, "description", dao.getDescription());
        location = new NonNullableStringProperty(this, "location", dao.getLocation());
        contact = new NonNullableStringProperty(this, "contact", dao.getContact());
        type = new AppointmentTypeProperty(this, "type", dao.getType());
        typeDisplay = new AppointmentTypeDisplayProperty(this, "typeDisplay", type);
        url = new NonNullableStringProperty(this, "url", dao.getUrl());
        start = new SimpleObjectProperty<>(this, "start", DB.toLocalDateTime(dao.getStart()));
        end = new SimpleObjectProperty<>(this, "end", DB.toLocalDateTime(dao.getEnd()));
        effectiveLocation = new EffectiveLocationProperty(this, "effectiveLocation", this);
    }

    public CustomerItem<? extends CustomerRowData> getCustomer() {
        return customer.get();
    }

    public void setCustomer(CustomerItem<? extends CustomerRowData> value) {
        customer.set(value);
    }

    public ObjectProperty<CustomerItem<? extends CustomerRowData>> customerProperty() {
        return customer;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerNameProperty() {
        return customerName;
    }

    public String getCustomerAddress1() {
        return customerAddress1.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerAddress1Property() {
        return customerAddress1;
    }

    public String getCustomerAddress2() {
        return customerAddress2.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerAddress2Property() {
        return customerAddress2;
    }

    public String getCustomerCityName() {
        return customerCityName.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerCityNameProperty() {
        return customerCityName;
    }

    public String getCustomerCountryName() {
        return customerCountryName.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerCountryNameProperty() {
        return customerCountryName;
    }

    public String getCustomerPostalCode() {
        return customerPostalCode.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerPostalCodeProperty() {
        return customerPostalCode;
    }

    public String getCustomerPhone() {
        return customerPhone.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerPhoneProperty() {
        return customerPhone;
    }

    public String getCustomerCityZipCountry() {
        return customerCityZipCountry.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerCityZipCountryProperty() {
        return customerCityZipCountry;
    }

    public String getCustomerAddressText() {
        return customerAddressText.get();
    }

    public ChildPropertyWrapper<String, CustomerItem<? extends CustomerRowData>> customerAddressTextProperty() {
        return customerAddressText;
    }

    public boolean isCustomerActive() {
        return customerActive.get();
    }

    public ChildPropertyWrapper<Boolean, CustomerItem<? extends CustomerRowData>> customerActiveProperty() {
        return customerActive;
    }

    public UserItem<? extends UserRowData> getUser() {
        return user.get();
    }

    public void setUser(UserItem<? extends UserRowData> value) {
        user.set(value);
    }

    public ObjectProperty<UserItem<? extends UserRowData>> userProperty() {
        return user;
    }

    public String getUserName() {
        return userName.get();
    }

    public ChildPropertyWrapper<String, UserItem<? extends UserRowData>> userNameProperty() {
        return userName;
    }

    public UserStatus getUserStatus() {
        return (UserStatus) userStatus.get();
    }

    public ChildPropertyWrapper<UserStatus, UserItem<? extends UserRowData>> userStatusProperty() {
        return userStatus;
    }

    public String getUserStatusDisplay() {
        return userStatusDisplay.get();
    }

    public ChildPropertyWrapper<String, UserItem<? extends UserRowData>> userStatusDisplayProperty() {
        return userStatusDisplay;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String value) {
        location.set(value);
    }

    public StringProperty locationProperty() {
        return location;
    }

    public String getEffectiveLocation() {
        return effectiveLocation.get();
    }

    public ReadOnlyProperty<String> effectiveLocationProperty() {
        return effectiveLocation;
    }

    public String getContact() {
        return contact.get();
    }

    public void setContact(String value) {
        contact.set(value);
    }

    public StringProperty contactProperty() {
        return contact;
    }

    public AppointmentType getType() {
        return type.get();
    }

    public void setType(AppointmentType value) {
        type.set(value);
    }

    public AppointmentTypeProperty typeProperty() {
        return type;
    }

    public String getTypeDisplay() {
        return typeDisplay.get();
    }

    public ReadOnlyProperty<String> typeDisplayProperty() {
        return typeDisplay;
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String value) {
        url.set(value);
    }

    public StringProperty urlProperty() {
        return url;
    }

    public LocalDateTime getStart() {
        return start.get();
    }

    public void setStart(LocalDateTime value) {
        start.set(value);
    }

    public ObjectProperty<LocalDateTime> startProperty() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end.get();
    }

    public void setEnd(LocalDateTime value) {
        end.set(value);
    }

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

    public final static class Factory extends ItemModel.ModelFactory<AppointmentDAO, AppointmentModel> {

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
        public void updateItem(AppointmentModel item, AppointmentDAO dao) {
            super.updateItem(item, dao);
            item.contact.set(dao.getContact());
            CustomerRowData customer = dao.getCustomer();
            item.customer.set((null == customer) ? null : new RelatedCustomerModel(customer));
            item.description.set(dao.getDescription());
            item.end.set(DB.toLocalDateTime(dao.getEnd()));
            item.location.set(dao.getLocation());
            item.start.set(DB.toLocalDateTime(dao.getStart()));
            item.title.set(dao.getTitle());
            UserRowData user = dao.getUser();
            item.user.set((null == user) ? null : new RelatedUserModel(user));
            item.type.set(dao.getType());
            item.url.set(dao.getUrl());
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
                case CORPORATE_HQ_MEETING:
                case GERMANY_SITE_MEETING:
                case CUSTOMER_SITE:
                case GUATEMALA_SITE_MEETING:
                case INDIA_SITE_MEETING:
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
            CustomerItem<? extends CustomerRowData> customerModel = item.customer.get();
            if (null == customerModel) {
                throw new IllegalArgumentException("No associated customer");
            }
            UserItem<? extends UserRowData> userModel = item.user.get();
            if (null == userModel) {
                throw new IllegalArgumentException("No associated user");
            }
            CustomerRowData customerDAO = customerModel.getDataObject();
            if (customerDAO.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated customer has been deleted");
            }
            UserRowData userDAO = userModel.getDataObject();
            if (userDAO.getRowState() == DataRowState.DELETED) {
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
