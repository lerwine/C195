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
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.model.AppointmentType;
import scheduler.model.ModelHelper;
import scheduler.model.UserStatus;
import scheduler.model.db.CustomerRowData;
import scheduler.model.db.UserRowData;
import scheduler.model.ui.AppointmentItem;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserItem;
import scheduler.observables.AppointmentTypeDisplayProperty;
import scheduler.observables.AppointmentTypeProperty;
import scheduler.observables.EffectiveLocationProperty;
import scheduler.observables.NestedBooleanBindingProperty;
import scheduler.observables.NestedObjectBindingProperty;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.util.DB;
import scheduler.view.customer.CustomerModel;
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

    static ZoneId getZoneId(AppointmentModel model) {
        if (null != model) {
            ZoneId result;
            // TODO: Detect zone ID:
            switch (model.getType()) {
                case CORPORATE_LOCATION:
                    result = null;
                    break;
                case CUSTOMER_SITE:
                    return CustomerModel.getZoneId(model.getCustomer());
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
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerName;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerAddress1;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerAddress2;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerCityName;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerCountryName;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerPostalCode;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerPhone;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerAddressText;
    private final NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerCityZipCountry;
    private final NestedBooleanBindingProperty<CustomerItem<? extends CustomerRowData>> customerActive;
    private final SimpleObjectProperty<UserItem<? extends UserRowData>> user;
    private final NestedStringBindingProperty<UserItem<? extends UserRowData>> userName;
    private final NestedObjectBindingProperty<UserItem<? extends UserRowData>, UserStatus> userStatus;
    private final NestedStringBindingProperty<UserItem<? extends UserRowData>> userStatusDisplay;
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
        customer = new SimpleObjectProperty<>(this, "customer", (null == c) ? null : new RelatedCustomer(c));
        customerName = new NestedStringBindingProperty<>(this, "customerName", customer, (t) -> t.nameProperty());
        customerAddress1 = new NestedStringBindingProperty<>(this, "customerAddress1", customer, (t) -> t.address1Property());
        customerAddress2 = new NestedStringBindingProperty<>(this, "customerAddress2", customer, (t) -> t.address2Property());
        customerCityName = new NestedStringBindingProperty<>(this, "customerCityName", customer, (t) -> t.cityNameProperty());
        customerCountryName = new NestedStringBindingProperty<>(this, "customerCountryName", customer, (t) -> t.countryNameProperty());
        customerPostalCode = new NestedStringBindingProperty<>(this, "customerPostalCode", customer, (t) -> t.postalCodeProperty());
        customerPhone = new NestedStringBindingProperty<>(this, "customerPhone", customer, (t) -> t.phoneProperty());
        customerCityZipCountry = new NestedStringBindingProperty<>(this, "customerCityZipCountry", customer, (t) -> t.cityZipCountryProperty());
        customerAddressText = new NestedStringBindingProperty<>(this, "customerAddressText", customer, (t) -> t.addressTextProperty());
        customerActive = new NestedBooleanBindingProperty<>(this, "customerActive", customer, (t) -> t.activeProperty());
        UserRowData u = dao.getUser();
        user = new SimpleObjectProperty<>(this, "user", (null == u) ? null : new RelatedUser(u));
        userName = new NestedStringBindingProperty<>(this, "userName", user, (t) -> t.userNameProperty());
        userStatus = new NestedObjectBindingProperty<>(this, "userStatus", user, (t) -> t.statusProperty());
        userStatusDisplay = new NestedStringBindingProperty<>(this, "userStatusDisplay", user, (t) -> t.statusDisplayProperty());
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

    @Override
    public CustomerItem<? extends CustomerRowData> getCustomer() {
        return customer.get();
    }

    public void setCustomer(CustomerItem<? extends CustomerRowData> value) {
        customer.set(value);
    }

    @Override
    public ObjectProperty<? extends CustomerItem<? extends CustomerRowData>> customerProperty() {
        return customer;
    }

    @Override
    public String getCustomerName() {
        return customerName.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerNameProperty() {
        return customerName;
    }

    @Override
    public String getCustomerAddress1() {
        return customerAddress1.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerAddress1Property() {
        return customerAddress1;
    }

    @Override
    public String getCustomerAddress2() {
        return customerAddress2.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerAddress2Property() {
        return customerAddress2;
    }

    @Override
    public String getCustomerCityName() {
        return customerCityName.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerCityNameProperty() {
        return customerCityName;
    }

    @Override
    public String getCustomerCountryName() {
        return customerCountryName.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerCountryNameProperty() {
        return customerCountryName;
    }

    @Override
    public String getCustomerPostalCode() {
        return customerPostalCode.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerPostalCodeProperty() {
        return customerPostalCode;
    }

    @Override
    public String getCustomerPhone() {
        return customerPhone.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerPhoneProperty() {
        return customerPhone;
    }

    @Override
    public String getCustomerCityZipCountry() {
        return customerCityZipCountry.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerCityZipCountryProperty() {
        return customerCityZipCountry;
    }

    @Override
    public String getCustomerAddressText() {
        return customerAddressText.get();
    }

    @Override
    public NestedStringBindingProperty<CustomerItem<? extends CustomerRowData>> customerAddressTextProperty() {
        return customerAddressText;
    }

    @Override
    public boolean isCustomerActive() {
        return customerActive.get();
    }

    @Override
    public NestedBooleanBindingProperty<CustomerItem<? extends CustomerRowData>> customerActiveProperty() {
        return customerActive;
    }

    @Override
    public UserItem<? extends UserRowData> getUser() {
        return user.get();
    }

    public void setUser(UserItem<? extends UserRowData> value) {
        user.set(value);
    }

    @Override
    public ObjectProperty<UserItem<? extends UserRowData>> userProperty() {
        return user;
    }

    @Override
    public String getUserName() {
        return userName.get();
    }

    @Override
    public NestedStringBindingProperty<UserItem<? extends UserRowData>> userNameProperty() {
        return userName;
    }

    @Override
    public UserStatus getUserStatus() {
        return (UserStatus) userStatus.get();
    }

    @Override
    public NestedObjectBindingProperty<UserItem<? extends UserRowData>, UserStatus> userStatusProperty() {
        return userStatus;
    }

    @Override
    public String getUserStatusDisplay() {
        return userStatusDisplay.get();
    }

    @Override
    public NestedStringBindingProperty<UserItem<? extends UserRowData>> userStatusDisplayProperty() {
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
    public AppointmentType getType() {
        return type.get();
    }

    public void setType(AppointmentType value) {
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
        public void updateItem(AppointmentModel item, AppointmentDAO dao) {
            super.updateItem(item, dao);
            item.contact.set(dao.getContact());
            CustomerRowData customer = dao.getCustomer();
            item.customer.set((null == customer) ? null : new RelatedCustomer(customer));
            item.description.set(dao.getDescription());
            item.end.set(DB.toLocalDateTime(dao.getEnd()));
            item.location.set(dao.getLocation());
            item.start.set(DB.toLocalDateTime(dao.getStart()));
            item.title.set(dao.getTitle());
            UserRowData user = dao.getUser();
            item.user.set((null == user) ? null : new RelatedUser(user));
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
            CustomerItem<? extends CustomerRowData> customerModel = item.customer.get();
            if (null == customerModel) {
                throw new IllegalArgumentException("No associated customer");
            }
            UserItem<? extends UserRowData> userModel = item.user.get();
            if (null == userModel) {
                throw new IllegalArgumentException("No associated user");
            }
            CustomerRowData customerDAO = customerModel.getDataObject();
            if (ModelHelper.getRowState(customerDAO) == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated customer has been deleted");
            }
            UserRowData userDAO = userModel.getDataObject();
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
