package scheduler.model.ui;

import java.sql.Timestamp;
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
import javafx.event.EventType;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.events.CustomerEvent;
import scheduler.events.CustomerFailedEvent;
import scheduler.events.UserEvent;
import scheduler.events.UserFailedEvent;
import static scheduler.model.Appointment.MAX_LENGTH_TITLE;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.PredefinedData;
import scheduler.model.RecordModelContext;
import scheduler.model.UserStatus;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.DB;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.appointment.AppointmentModelFilter;

/**
 * List item model for {@link AppointmentDAO} data access objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentModel extends FxRecordModel<AppointmentDAO> implements AppointmentItem<AppointmentDAO> {

    public static final Factory FACTORY = new Factory();

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
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
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
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(primaryKeyProperty());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addDataObject(customer)
                .addDataObject(user)
                .addString(title)
                .addString(description)
                .addString(location)
                .addString(contact)
                .addEnum(type)
                .addString(url)
                .addLocalDateTime(start)
                .addLocalDateTime(end)
                .addLocalDateTime(createDateProperty())
                .addString(createdByProperty())
                .addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty())
                .addBoolean(valid);
    }

    public final static class Factory extends FxRecordModel.FxModelFactory<AppointmentDAO, AppointmentModel, AppointmentEvent> {

        private Factory() {
            super(AppointmentEvent.APPOINTMENT_EVENT_TYPE);
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<AppointmentDAO, AppointmentEvent> getDaoFactory() {
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
        public DataAccessObject.SaveDaoTask<AppointmentDAO, AppointmentModel, AppointmentEvent> createSaveTask(RecordModelContext<AppointmentDAO, AppointmentModel> model) {
            return new AppointmentDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<AppointmentDAO, AppointmentModel, AppointmentEvent> createDeleteTask(RecordModelContext<AppointmentDAO, AppointmentModel> model) {
            return new AppointmentDAO.DeleteTask(model, false);
        }

        @Override
        public AppointmentEvent validateForSave(RecordModelContext<AppointmentDAO, AppointmentModel> target) {
            AppointmentDAO dao = target.getDataAccessObject();
            String message;
            String s;
            if (dao.getRowState() == DataRowState.DELETED) {
                message = "Appointment has already been deleted";
            } else if ((s = dao.getTitle()).isEmpty()) {
                message = "Title not defined";
            } else if (s.length() > MAX_LENGTH_TITLE) {
                message = "Title too long";
            } else {
                Timestamp start = dao.getStart();
                Timestamp end;
                if (null == start) {
                    message = "Start date/time not defined";
                } else if (null == (end = dao.getEnd())) {
                    message = "End date/time not defined";
                } else if (start.compareTo(end) > 0) {
                    message = "Start is after end date/time";
                } else {
                    message = null;
                    switch (dao.getType()) {
                        case CORPORATE_LOCATION:
                            s = dao.getLocation();
                            if (s.isEmpty()) {
                                message = "Location not defined";
                            } else if (!PredefinedData.getCorporateAddressMap().containsKey(s)) {
                                message = "Invalid corporate location key";
                            }
                            break;
                        case VIRTUAL:
                            if (Values.isNullWhiteSpaceOrEmpty(dao.getUrl())) {
                                message = "URL not defined";
                            }
                            break;
                        case CUSTOMER_SITE:
                            if (Values.isNullWhiteSpaceOrEmpty(dao.getContact())) {
                                message = "Contact not defined";
                            }
                            break;
                        default:
                            if (Values.isNullWhiteSpaceOrEmpty(dao.getLocation())) {
                                message = "Location not defined";
                            }
                            break;
                    }
                    if (null == message) {
                        AppointmentModel fxRecordModel = target.getFxRecordModel();
                        CustomerEvent customerEvent = null;
                        UserEvent userEvent = null;
                        if (null != fxRecordModel) {
                            CustomerItem<? extends ICustomerDAO> customer = fxRecordModel.getCustomer();
                            if (null == customer) {
                                message = "Customer not specified";
                            } else if (customer instanceof CustomerModel) {
                                customerEvent = CustomerModel.FACTORY.validateForSave(RecordModelContext.of((CustomerModel) customer));
                                if (null == customerEvent || !(customerEvent instanceof CustomerFailedEvent)) {
                                    customerEvent = null;
                                    UserItem<? extends IUserDAO> user = fxRecordModel.getUser();
                                    if (null == user) {
                                        message = "User not specified";
                                    } else if (user instanceof UserModel) {
                                        userEvent = UserModel.FACTORY.validateForSave(RecordModelContext.of((UserModel) user));
                                    }
                                }
                            }
                        } else {
                            ICustomerDAO customer = dao.getCustomer();
                            if (null == customer) {
                                message = "Customer not specified";
                            } else if (customer instanceof CustomerDAO) {
                                customerEvent = CustomerModel.FACTORY.validateForSave(RecordModelContext.of((CustomerDAO) customer));
                                if (null == customerEvent || !(customerEvent instanceof CustomerFailedEvent)) {
                                    customerEvent = null;
                                    IUserDAO user = dao.getUser();
                                    if (null == user) {
                                        message = "User not specified";
                                    } else if (user instanceof UserDAO) {
                                        userEvent = UserModel.FACTORY.validateForSave(RecordModelContext.of((UserDAO) user));
                                    }
                                }
                            }
                        }

                        if (null == message) {
                            if (null != customerEvent) {
                                if (dao.getRowState() == DataRowState.NEW) {
                                    return AppointmentEvent.createInsertInvalidEvent(target, this, (CustomerFailedEvent) customerEvent);
                                }
                                return AppointmentEvent.createUpdateInvalidEvent(target, this, (CustomerFailedEvent) customerEvent);
                            }
                            if (null != userEvent && userEvent instanceof UserFailedEvent) {
                                if (dao.getRowState() == DataRowState.NEW) {
                                    return AppointmentEvent.createInsertInvalidEvent(target, this, (UserFailedEvent) userEvent);
                                }
                                return AppointmentEvent.createUpdateInvalidEvent(target, this, (UserFailedEvent) userEvent);
                            }
                            return null;
                        }
                    }
                }
            }

            if (dao.getRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertInvalidEvent(target, this, message);
            }
            return AppointmentEvent.createUpdateInvalidEvent(target, this, message);
        }

        @Override
        @SuppressWarnings("unchecked")
        public AppointmentOpRequestEvent createEditRequestEvent(AppointmentModel model, Object source) {
            return new AppointmentOpRequestEvent(model, source, false);
        }

        @Override
        @SuppressWarnings("unchecked")
        public AppointmentOpRequestEvent createDeleteRequestEvent(AppointmentModel model, Object source) {
            return new AppointmentOpRequestEvent(model, source, true);
        }

        @Override
        @SuppressWarnings("unchecked")
        public EventType<AppointmentOpRequestEvent> getBaseRequestEventType() {
            return AppointmentOpRequestEvent.APPOINTMENT_OP_REQUEST;
        }

        @Override
        @SuppressWarnings("unchecked")
        public EventType<AppointmentOpRequestEvent> getEditRequestEventType() {
            return AppointmentOpRequestEvent.EDIT_REQUEST;
        }

        @Override
        @SuppressWarnings("unchecked")
        public EventType<AppointmentOpRequestEvent> getDeleteRequestEventType() {
            return AppointmentOpRequestEvent.DELETE_REQUEST;
        }

    }
}
