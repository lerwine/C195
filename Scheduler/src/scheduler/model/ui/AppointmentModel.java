package scheduler.model.ui;

import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.PredefinedData;
import scheduler.model.UserStatus;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.event.AppointmentMutateEvent;
import scheduler.view.event.ItemMutateEvent;

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
                CorporateAddress corporateAddress = PredefinedData.getCorporateAddress(location);
                return AddressModel.calculateSingleLineAddress(corporateAddress.getAddress1(), corporateAddress.getAddress2(),
                        AddressModel.calculateCityZipCountry(corporateAddress.getCity().getName(), corporateAddress.getCity().getCountry().getName(),
                                corporateAddress.getPostalCode()), corporateAddress.getPhone());
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
    private final ReadOnlyStringBindingProperty customerName;
    private final ReadOnlyStringBindingProperty customerAddress1;
    private final ReadOnlyStringBindingProperty customerAddress2;
    private final ReadOnlyStringBindingProperty customerCityName;
    private final ReadOnlyStringBindingProperty customerCountryName;
    private final ReadOnlyStringBindingProperty customerPostalCode;
    private final ReadOnlyStringBindingProperty customerPhone;
    private final ReadOnlyStringBindingProperty customerAddressText;
    private final ReadOnlyStringBindingProperty customerCityZipCountry;
    private final ReadOnlyBooleanBindingProperty customerActive;
    private final SimpleObjectProperty<UserItem<? extends IUserDAO>> user;
    private final ReadOnlyStringBindingProperty userName;
    private final ReadOnlyObjectBindingProperty<UserStatus> userStatus;
    private final ReadOnlyStringBindingProperty userStatusDisplay;
    private final NonNullableStringProperty title;
    private final NonNullableStringProperty description;
    private final NonNullableStringProperty location;
    private final NonNullableStringProperty contact;
    private final SimpleObjectProperty<AppointmentType> type;
    private final ReadOnlyStringBindingProperty typeDisplay;
    private final NonNullableStringProperty url;
    private final SimpleObjectProperty<LocalDateTime> start;
    private final SimpleObjectProperty<LocalDateTime> end;
    private final ReadOnlyStringBindingProperty effectiveLocation;
    private final ReadOnlyBooleanBindingProperty valid;

    @SuppressWarnings("incomplete-switch")
    public AppointmentModel(AppointmentDAO dao) {
        super(dao);
        customer = new SimpleObjectProperty<>(this, "customer");
        customerName = new ReadOnlyStringBindingProperty(this, "customerName", Bindings.selectString(customer, "name"));
        customerAddress1 = new ReadOnlyStringBindingProperty(this, "customerName", Bindings.selectString(customer, "address1"));
        customerAddress2 = new ReadOnlyStringBindingProperty(this, "customerAddress2", Bindings.selectString(customer, "address2"));
        customerCityName = new ReadOnlyStringBindingProperty(this, "customerCityName", Bindings.selectString(customer, "cityName"));
        customerCountryName = new ReadOnlyStringBindingProperty(this, "customerCountryName", Bindings.selectString(customer, "countryName"));
        customerPostalCode = new ReadOnlyStringBindingProperty(this, "customerPostalCode", Bindings.selectString(customer, "postalCode"));
        customerPhone = new ReadOnlyStringBindingProperty(this, "customerPhone", Bindings.selectString(customer, "phone"));
        customerCityZipCountry = new ReadOnlyStringBindingProperty(this, "customerCityZipCountry", Bindings.selectString(customer, "cityZipCountry"));
        customerAddressText = new ReadOnlyStringBindingProperty(this, "customerAddressText", Bindings.selectString(customer, "addressText"));
        customerActive = new ReadOnlyBooleanBindingProperty(this, "customerActive", Bindings.selectBoolean(customer, "active"));
        user = new SimpleObjectProperty<>(this, "user");
        userName = new ReadOnlyStringBindingProperty(this, "userName", Bindings.selectString(user, "userName"));
        userStatus = new ReadOnlyObjectBindingProperty<>(this, "userStatus", Bindings.select(user, "status"));
        userStatusDisplay = new ReadOnlyStringBindingProperty(this, "userStatusDisplay", Bindings.selectString(user, "statusDisplay"));
        title = new NonNullableStringProperty(this, "title", dao.getTitle());
        description = new NonNullableStringProperty(this, "description", dao.getDescription());
        location = new NonNullableStringProperty(this, "location", dao.getLocation());
        AppointmentType at = dao.getType();
        type = new SimpleObjectProperty<>(this, "type", (null == at) ? AppointmentType.OTHER : at);
        typeDisplay = new ReadOnlyStringBindingProperty(this, "typeDisplay",
                Bindings.createStringBinding(() -> AppointmentType.toDisplayText(type.get()), type));
        contact = new NonNullableStringProperty(this, "contact");
        url = new NonNullableStringProperty(this, "url");
        start = new SimpleObjectProperty<>(this, "start");
        end = new SimpleObjectProperty<>(this, "end");

        StringBinding wsNormalizedLocation = Bindings.createStringBinding(() -> Values.asNonNullAndWsNormalized(location.get()), location);
        StringBinding wsNormalizedUrl = Bindings.createStringBinding(() -> Values.asNonNullAndWsNormalized(url.get()), url);
        effectiveLocation = new ReadOnlyStringBindingProperty(this, "effectiveLocation", () -> {
            AppointmentType t = type.get();
            String l = wsNormalizedLocation.get();
            String c = customerAddressText.get();
            String u = wsNormalizedUrl.get();
            if (null != t) {
                switch (t) {
                    case CORPORATE_LOCATION:
                        if (!l.isEmpty()) {
                            CorporateAddress corporateAddress = PredefinedData.getCorporateAddress(l);
                            return AddressModel.calculateMultiLineAddress(AddressModel.calculateAddressLines(corporateAddress.getAddress1(), corporateAddress.getAddress2()),
                                    AddressModel.calculateCityZipCountry(corporateAddress.getCity().getName(), corporateAddress.getCity().getCountry().getName(),
                                            corporateAddress.getPostalCode()), corporateAddress.getPhone());
                        }
                        break;
                    case CUSTOMER_SITE:
                        return c;
                    case VIRTUAL:
                        return u;
                }
            }
            return l;
        }, type, wsNormalizedLocation, customerAddressText, wsNormalizedUrl);

        valid = new ReadOnlyBooleanBindingProperty(this, "valid",
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(title.get()), title)
                        .and(Bindings.selectBoolean(customer, "valid"))
                        .and(Bindings.selectBoolean(user, "valid"))
                        .and(Bindings.createBooleanBinding(() -> {
                            LocalDateTime s = start.get();
                            LocalDateTime e = end.get();
                            return null != s && null != e && s.compareTo(e) <= 0;
                        }, start, end))
                        .and(Bindings.createBooleanBinding(() -> {
                            AppointmentType t = type.get();
                            String l = location.get();
                            String c = contact.get();
                            String u = url.get();
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
                        }, type, location, contact, url))
        );
        customer.set(CustomerItem.createModel(dao.getCustomer()));
        user.set(UserItem.createModel(dao.getUser()));
        title.set(dao.getTitle());
        description.set(dao.getDescription());
        location.set(dao.getLocation());
        contact.set(dao.getContact());
        url.set(dao.getUrl());
        start.set(DB.toLocalDateTime(dao.getStart()));
        end.set(DB.toLocalDateTime(dao.getEnd()));
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
        return customerName;
    }

    @Override
    public String getCustomerAddress1() {
        return customerAddress1.get();
    }

    @Override
    public ReadOnlyStringProperty customerAddress1Property() {
        return customerAddress1;
    }

    @Override
    public String getCustomerAddress2() {
        return customerAddress2.get();
    }

    @Override
    public ReadOnlyStringProperty customerAddress2Property() {
        return customerAddress2;
    }

    @Override
    public String getCustomerCityName() {
        return customerCityName.get();
    }

    @Override
    public ReadOnlyStringProperty customerCityNameProperty() {
        return customerCityName;
    }

    @Override
    public String getCustomerCountryName() {
        return customerCountryName.get();
    }

    @Override
    public ReadOnlyStringProperty customerCountryNameProperty() {
        return customerCountryName;
    }

    @Override
    public String getCustomerPostalCode() {
        return customerPostalCode.get();
    }

    @Override
    public ReadOnlyStringProperty customerPostalCodeProperty() {
        return customerPostalCode;
    }

    @Override
    public String getCustomerPhone() {
        return customerPhone.get();
    }

    @Override
    public ReadOnlyStringProperty customerPhoneProperty() {
        return customerPhone;
    }

    @Override
    public String getCustomerCityZipCountry() {
        return customerCityZipCountry.get();
    }

    @Override
    public ReadOnlyStringProperty customerCityZipCountryProperty() {
        return customerCityZipCountry;
    }

    @Override
    public String getCustomerAddressText() {
        return customerAddressText.get();
    }

    @Override
    public ReadOnlyStringProperty customerAddressTextProperty() {
        return customerAddressText;
    }

    @Override
    public boolean isCustomerActive() {
        return customerActive.get();
    }

    @Override
    public ReadOnlyBooleanProperty customerActiveProperty() {
        return customerActive;
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
        return userName;
    }

    @Override
    public UserStatus getUserStatus() {
        return userStatus.get();
    }

    @Override
    public ReadOnlyObjectProperty<UserStatus> userStatusProperty() {
        return userStatus;
    }

    @Override
    public String getUserStatusDisplay() {
        return userStatusDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty userStatusDisplayProperty() {
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
    public ReadOnlyStringProperty effectiveLocationProperty() {
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
    public SimpleObjectProperty<AppointmentType> typeProperty() {
        return type;
    }

    @Override
    public String getTypeDisplay() {
        return typeDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty typeDisplayProperty() {
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
        return valid;
    }

    public final static class Factory extends FxRecordModel.ModelFactory<AppointmentDAO, AppointmentModel> {

        private Factory() {
        }

        @Override
        public DaoFactory<AppointmentDAO> getDaoFactory() {
            return AppointmentDAO.FACTORY;
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
            AppointmentDAO dao = item.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Appointment has been deleted");
            }
            if (!item.isValid()) {
                throw new IllegalStateException();
            }

            dao.setCustomer(item.getCustomer().dataObject());
            dao.setUser(item.getUser().dataObject());
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

        @Override
        public ItemMutateEvent<AppointmentModel> createInsertEvent(AppointmentModel source, Event fxEvent) {
            return new AppointmentMutateEvent(source, AppointmentMutateEvent.APPOINTMENT_INSERT_EVENT, fxEvent);
        }

        @Override
        public ItemMutateEvent<AppointmentModel> createUpdateEvent(AppointmentModel source, Event fxEvent) {
            return new AppointmentMutateEvent(source, AppointmentMutateEvent.APPOINTMENT_UPDATE_EVENT, fxEvent);
        }

        @Override
        public ItemMutateEvent<AppointmentModel> createDeleteEvent(AppointmentModel source, Event fxEvent) {
            return new AppointmentMutateEvent(source, AppointmentMutateEvent.APPOINTMENT_DELETE_EVENT, fxEvent);
        }

    }
}
