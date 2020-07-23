package scheduler.model.fx;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
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
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.PartialCustomerDAO;
import scheduler.dao.PartialUserDAO;
import scheduler.dao.UserDAO;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.CustomerEvent;
import scheduler.events.CustomerFailedEvent;
import scheduler.events.ModelEvent;
import scheduler.events.UserEvent;
import scheduler.events.UserFailedEvent;
import scheduler.model.Address;
import static scheduler.model.Appointment.MAX_LENGTH_TITLE;
import scheduler.model.AppointmentEntity;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.AddressHelper;
import scheduler.model.ModelHelper.CustomerHelper;
import scheduler.model.ModelHelper.UserHelper;
import scheduler.model.PredefinedData;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.DateTimeUtil;
import scheduler.util.Values;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.appointment.AppointmentModelFilter;

/**
 * List item model for {@link AppointmentDAO} data access objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentModel extends EntityModel<AppointmentDAO> implements AppointmentEntity<LocalDateTime> {

    public static final Factory FACTORY = new Factory();

    public static final String PROP_EFFECTIVELOCATION = "effectiveLocation";
    public static final String PROP_TYPEDISPLAY = "typeDisplay";
    public static final String PROP_USERSTATUSDISPLAY = "userStatusDisplay";
    public static final String PROP_USERSTATUS = "userStatus";
    public static final String PROP_USERNAME = "userName";
    public static final String PROP_CUSTOMERACTIVE = "customerActive";
    public static final String PROP_CUSTOMERADDRESSTEXT = "customerAddressText";
    public static final String PROP_CUSTOMERCITYZIPCOUNTRY = "customerCityZipCountry";
    public static final String PROP_CUSTOMERPHONE = "customerPhone";
    public static final String PROP_CUSTOMERPOSTALCODE = "customerPostalCode";
    public static final String PROP_CUSTOMERCOUNTRYNAME = "customerCountryName";
    public static final String PROP_CUSTOMERCITYNAME = "customerCityName";
    public static final String PROP_CUSTOMERADDRESS2 = "customerAddress2";
    public static final String PROP_CUSTOMERADDRESS1 = "customerAddress1";
    public static final String PROP_CUSTOMERNAME = "customerName";

    private final WeakEventHandlingReference<AppointmentSuccessEvent> modelEventHandler;
    private final SimpleObjectProperty<PartialCustomerModel<? extends PartialCustomerDAO>> customer;
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
    private final SimpleObjectProperty<PartialUserModel<? extends PartialUserDAO>> user;
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

    @SuppressWarnings("incomplete-switch")
    private AppointmentModel(final AppointmentDAO dao) {
        super(dao);
        customer = new SimpleObjectProperty<>(this, PROP_CUSTOMER, CustomerHelper.createModel(dao.getCustomer()));
        customerName = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERNAME, Bindings.selectString(customer, Customer.PROP_NAME));
        customerAddress1 = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERADDRESS1,
                Bindings.selectString(customer, Address.PROP_ADDRESS1));
        customerAddress2 = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERADDRESS2,
                Bindings.selectString(customer, Address.PROP_ADDRESS2));
        customerCityName = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERCITYNAME,
                Bindings.selectString(customer, PartialCustomerModel.PROP_CITYNAME));
        customerCountryName = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERCOUNTRYNAME,
                Bindings.selectString(customer, PartialCustomerModel.PROP_COUNTRYNAME));
        customerPostalCode = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERPOSTALCODE,
                Bindings.selectString(customer, Address.PROP_POSTALCODE));
        customerPhone = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERPHONE,
                Bindings.selectString(customer, Address.PROP_PHONE));
        customerCityZipCountry = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERCITYZIPCOUNTRY,
                Bindings.selectString(customer, PartialCustomerModel.PROP_CITYZIPCOUNTRY));
        customerAddressText = new ReadOnlyStringBindingProperty(this, PROP_CUSTOMERADDRESSTEXT,
                Bindings.selectString(customer, PartialCustomerModel.PROP_ADDRESSTEXT));
        customerActive = new ReadOnlyBooleanBindingProperty(this, PROP_CUSTOMERACTIVE,
                Bindings.selectBoolean(customer, Customer.PROP_ACTIVE));
        user = new SimpleObjectProperty<>(this, PROP_USER, UserHelper.createModel(dao.getUser()));
        userName = new ReadOnlyStringBindingProperty(this, PROP_USERNAME, Bindings.selectString(user, User.PROP_USERNAME));
        userStatus = new ReadOnlyObjectBindingProperty<>(this, PROP_USERSTATUS, Bindings.select(user, User.PROP_STATUS));
        userStatusDisplay = new ReadOnlyStringBindingProperty(this, PROP_USERSTATUSDISPLAY,
                Bindings.selectString(user, PartialUserModel.PROP_STATUSDISPLAY));
        title = new NonNullableStringProperty(this, PROP_TYPE, dao.getTitle());
        description = new NonNullableStringProperty(this, PROP_DESCRIPTION, dao.getDescription());
        location = new NonNullableStringProperty(this, PROP_LOCATION, dao.getLocation());
        final AppointmentType at = dao.getType();
        type = new SimpleObjectProperty<>(this, PROP_TYPE, (null == at) ? AppointmentType.OTHER : at);
        typeDisplay = new ReadOnlyStringBindingProperty(this, PROP_TYPEDISPLAY,
                Bindings.createStringBinding(() -> AppointmentType.toDisplayText(type.get()), type));
        contact = new NonNullableStringProperty(this, PROP_CONTACT, dao.getContact());
        url = new NonNullableStringProperty(this, PROP_URL, dao.getUrl());
        start = new SimpleObjectProperty<>(this, PROP_START, DateTimeUtil.toLocalDateTime(dao.getStart()));
        end = new SimpleObjectProperty<>(this, PROP_END, DateTimeUtil.toLocalDateTime(dao.getEnd()));

        final StringBinding wsNormalizedLocation = Bindings
                .createStringBinding(() -> Values.asNonNullAndWsNormalized(location.get()), location);
        final StringBinding wsNormalizedUrl = Bindings
                .createStringBinding(() -> Values.asNonNullAndWsNormalized(url.get()), url);
        effectiveLocation = new ReadOnlyStringBindingProperty(this, PROP_EFFECTIVELOCATION, () -> {
            final AppointmentType t = type.get();
            final String l = wsNormalizedLocation.get();
            final String c = customerAddressText.get();
            final String u = wsNormalizedUrl.get();
            if (null != t) {
                switch (t) {
                    case CORPORATE_LOCATION:
                        if (!l.isEmpty()) {
                            final CorporateAddress corporateAddress = PredefinedData.getCorporateAddress(l);
                            return AddressHelper.calculateMultiLineAddress(
                                    AddressHelper.calculateAddressLines(corporateAddress.getAddress1(),
                                            corporateAddress.getAddress2()),
                                    AddressHelper.calculateCityZipCountry(corporateAddress.getCity().getName(),
                                            corporateAddress.getCity().getCountry().getName(),
                                            corporateAddress.getPostalCode()),
                                    corporateAddress.getPhone());
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
        modelEventHandler = WeakEventHandlingReference.create(this::onModelEvent);
    }

    @Override
    protected void onDaoChanged(ModelEvent<AppointmentDAO, ? extends EntityModel<AppointmentDAO>> event) {
        AppointmentDAO dao = event.getDataAccessObject();
        PartialCustomerModel<? extends PartialCustomerDAO> currentCustomer = customer.get();
        PartialCustomerDAO newCustomer = dao.getCustomer();
        if (null == currentCustomer || null == newCustomer) {
            customer.set(CustomerHelper.createModel(dao.getCustomer()));
        } else {
            PartialCustomerDAO currentDao = currentCustomer.dataObject();
            if (currentDao != newCustomer && !(ModelHelper.areSameRecord(currentDao, newCustomer) && currentDao instanceof CustomerDAO)) {
                customer.set(CustomerHelper.createModel(dao.getCustomer()));
            }
        }
        PartialUserModel<? extends PartialUserDAO> currentUser = user.get();
        PartialUserDAO newUser = dao.getUser();
        if (null == currentUser || null == newUser) {
            user.set(UserHelper.createModel(dao.getUser()));
        } else {
            PartialUserDAO currentDao = currentUser.dataObject();
            if (currentDao != newUser && !(ModelHelper.areSameRecord(currentDao, newUser) && currentDao instanceof UserDAO)) {
                user.set(UserHelper.createModel(dao.getUser()));
            }
        }
        title.set(dao.getTitle());
        description.set(dao.getDescription());
        location.set(dao.getLocation());
        type.set(dao.getType());
        contact.set(dao.getContact());
        url.set(dao.getUrl());
        start.set(DateTimeUtil.toLocalDateTime(dao.getStart()));
        end.set(DateTimeUtil.toLocalDateTime(dao.getEnd()));
    }

    @Override
    public PartialCustomerModel<? extends PartialCustomerDAO> getCustomer() {
        return customer.get();
    }

    public void setCustomer(final PartialCustomerModel<? extends PartialCustomerDAO> value) {
        customer.set(value);
    }

    public ObjectProperty<? extends PartialCustomerModel<? extends PartialCustomerDAO>> customerProperty() {
        return customer;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public ReadOnlyStringProperty customerNameProperty() {
        return customerName;
    }

    public String getCustomerAddress1() {
        return customerAddress1.get();
    }

    public ReadOnlyStringProperty customerAddress1Property() {
        return customerAddress1;
    }

    public String getCustomerAddress2() {
        return customerAddress2.get();
    }

    public ReadOnlyStringProperty customerAddress2Property() {
        return customerAddress2;
    }

    public String getCustomerCityName() {
        return customerCityName.get();
    }

    public ReadOnlyStringProperty customerCityNameProperty() {
        return customerCityName;
    }

    public String getCustomerCountryName() {
        return customerCountryName.get();
    }

    public ReadOnlyStringProperty customerCountryNameProperty() {
        return customerCountryName;
    }

    public String getCustomerPostalCode() {
        return customerPostalCode.get();
    }

    public ReadOnlyStringProperty customerPostalCodeProperty() {
        return customerPostalCode;
    }

    public String getCustomerPhone() {
        return customerPhone.get();
    }

    public ReadOnlyStringProperty customerPhoneProperty() {
        return customerPhone;
    }

    public String getCustomerCityZipCountry() {
        return customerCityZipCountry.get();
    }

    public ReadOnlyStringProperty customerCityZipCountryProperty() {
        return customerCityZipCountry;
    }

    public String getCustomerAddressText() {
        return customerAddressText.get();
    }

    public ReadOnlyStringProperty customerAddressTextProperty() {
        return customerAddressText;
    }

    public boolean isCustomerActive() {
        return customerActive.get();
    }

    public ReadOnlyBooleanProperty customerActiveProperty() {
        return customerActive;
    }

    @Override
    public PartialUserModel<? extends PartialUserDAO> getUser() {
        return user.get();
    }

    public void setUser(final PartialUserModel<? extends PartialUserDAO> value) {
        user.set(value);
    }

    public ObjectProperty<PartialUserModel<? extends PartialUserDAO>> userProperty() {
        return user;
    }

    public String getUserName() {
        return userName.get();
    }

    public ReadOnlyStringProperty userNameProperty() {
        return userName;
    }

    public UserStatus getUserStatus() {
        return userStatus.get();
    }

    public ReadOnlyObjectProperty<UserStatus> userStatusProperty() {
        return userStatus;
    }

    public String getUserStatusDisplay() {
        return userStatusDisplay.get();
    }

    public ReadOnlyStringProperty userStatusDisplayProperty() {
        return userStatusDisplay;
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    public void setTitle(final String value) {
        title.set(value);
    }

    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public String getDescription() {
        return description.get();
    }

    public void setDescription(final String value) {
        description.set(value);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String getLocation() {
        return location.get();
    }

    public void setLocation(final String value) {
        location.set(value);
    }

    public StringProperty locationProperty() {
        return location;
    }

    public String getEffectiveLocation() {
        return effectiveLocation.get();
    }

    public ReadOnlyStringProperty effectiveLocationProperty() {
        return effectiveLocation;
    }

    @Override
    public String getContact() {
        return contact.get();
    }

    public void setContact(final String value) {
        contact.set(value);
    }

    public StringProperty contactProperty() {
        return contact;
    }

    @Override
    public AppointmentType getType() {
        return type.get();
    }

    public void setType(final AppointmentType value) {
        type.set(value);
    }

    public SimpleObjectProperty<AppointmentType> typeProperty() {
        return type;
    }

    public String getTypeDisplay() {
        return typeDisplay.get();
    }

    public ReadOnlyStringProperty typeDisplayProperty() {
        return typeDisplay;
    }

    @Override
    public String getUrl() {
        return url.get();
    }

    public void setUrl(final String value) {
        url.set(value);
    }

    public StringProperty urlProperty() {
        return url;
    }

    @Override
    public LocalDateTime getStart() {
        return start.get();
    }

    public void setStart(final LocalDateTime value) {
        start.set(value);
    }

    public ObjectProperty<LocalDateTime> startProperty() {
        return start;
    }

    @Override
    public LocalDateTime getEnd() {
        return end.get();
    }

    public void setEnd(final LocalDateTime value) {
        end.set(value);
    }

    public ObjectProperty<LocalDateTime> endProperty() {
        return end;
    }

    @Override
    public boolean startEquals(Object value) {
        LocalDateTime s = getStart();
        if (null == s) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof ChronoLocalDateTime) {
            return s.equals(value);
        }

        if (value instanceof ZonedDateTime) {
            return s.equals(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }
        return value instanceof Timestamp && s.equals(DateTimeUtil.toLocalDateTime((Timestamp) value));
    }

    @Override
    public int compareStart(Object value) {
        LocalDateTime s = getStart();
        if (null == s) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof ChronoLocalDateTime) {
            return s.compareTo((ChronoLocalDateTime<?>) value);
        }

        if (value instanceof ZonedDateTime) {
            return s.compareTo(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }

        if (value instanceof Timestamp) {
            return s.compareTo(((Timestamp) value).toLocalDateTime());
        }

        if (value instanceof Date) {
            return s.compareTo(new Timestamp(((Date) value).getTime()).toLocalDateTime());
        }

        throw new IllegalArgumentException();
    }

    @Override
    public boolean endEquals(Object value) {
        LocalDateTime e = getEnd();
        if (null == e) {
            return null == value;
        }

        if (null == value) {
            return false;
        }

        if (value instanceof ChronoLocalDateTime) {
            return e.equals(value);
        }

        if (value instanceof ZonedDateTime) {
            return e.equals(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }
        return value instanceof Timestamp && e.equals(DateTimeUtil.toLocalDateTime((Timestamp) value));
    }

    @Override
    public int compareEnd(Object value) {
        LocalDateTime e = getEnd();
        if (null == e) {
            return (null == value) ? 0 : 1;
        }

        if (null == value) {
            return -1;
        }

        if (value instanceof ChronoLocalDateTime) {
            return e.compareTo((ChronoLocalDateTime<?>) value);
        }

        if (value instanceof ZonedDateTime) {
            return e.compareTo(((ZonedDateTime) value).withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime());
        }

        if (value instanceof Timestamp) {
            return e.compareTo(((Timestamp) value).toLocalDateTime());
        }

        if (value instanceof Date) {
            return e.compareTo(new Timestamp(((Date) value).getTime()).toLocalDateTime());
        }

        throw new IllegalArgumentException();
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof AppointmentModel) {
            final AppointmentModel other = (AppointmentModel) obj;
            if (isNewRow()) {
                return customer.isEqualTo(other.customer).get() && user.isEqualTo(other.user).get()
                        && title.isEqualTo(other.title).get() && description.isEqualTo(other.description).get()
                        && location.isEqualTo(other.location).get() && contact.isEqualTo(other.contact).get()
                        && type.isEqualTo(other.type).get() && url.isEqualTo(other.url).get()
                        && start.isEqualTo(other.start).get() && end.isEqualTo(other.end).get();
            }
            return !other.isNewRow() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    public String toString() {
        return ModelHelper.AppointmentHelper.appendModelProperties(this, new StringBuilder(AppointmentModel.class.getName()).append(" {"))
                .append(Values.LINEBREAK_STRING).append("}").toString();
    }

    public final static class Factory extends EntityModel.EntityModelFactory<AppointmentDAO, AppointmentModel> {

        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<AppointmentDAO, AppointmentModel> getDaoFactory() {
            return AppointmentDAO.FACTORY;
        }

        @Override
        public AppointmentModel createNew(AppointmentDAO dao) {
            AppointmentModel newModel = new AppointmentModel(dao);
            dao.addEventFilter(AppointmentSuccessEvent.SUCCESS_EVENT_TYPE, newModel.modelEventHandler.getWeakEventHandler());
            return newModel;
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
        public DataAccessObject.SaveDaoTask<AppointmentDAO, AppointmentModel> createSaveTask(AppointmentModel model) {
            return new AppointmentDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<AppointmentDAO, AppointmentModel> createDeleteTask(AppointmentModel model) {
            return new AppointmentDAO.DeleteTask(model, false);
        }

        @Override
        public AppointmentEvent validateForSave(final AppointmentModel fxRecordModel) {
            final AppointmentDAO dao = fxRecordModel.dataObject();
            String message;
            String s;
            if (dao.getRowState() == DataRowState.DELETED) {
                message = "Appointment has already been deleted";
            } else if ((s = dao.getTitle()).isEmpty()) {
                message = "Title not defined";
            } else if (s.length() > MAX_LENGTH_TITLE) {
                message = "Title too long";
            } else {
                final Timestamp start = dao.getStart();
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
                        CustomerEvent customerEvent = null;
                        UserEvent userEvent = null;
                        final PartialCustomerModel<? extends PartialCustomerDAO> customer = fxRecordModel.getCustomer();
                        if (null == customer) {
                            message = "Customer not specified";
                        } else if (customer instanceof CustomerModel) {
                            customerEvent = CustomerModel.FACTORY.validateForSave((CustomerModel) customer);
                            if (null == customerEvent || !(customerEvent instanceof CustomerFailedEvent)) {
                                customerEvent = null;
                                final PartialUserModel<? extends PartialUserDAO> user = fxRecordModel.getUser();
                                if (null == user) {
                                    message = "User not specified";
                                } else if (user instanceof UserModel) {
                                    userEvent = UserModel.FACTORY.validateForSave((UserModel) user);
                                }
                            }
                        }

                        if (null == message) {
                            if (null != customerEvent) {
                                if (dao.getRowState() == DataRowState.NEW) {
                                    return AppointmentEvent.createInsertInvalidEvent(fxRecordModel, this,
                                            (CustomerFailedEvent) customerEvent);
                                }
                                return AppointmentEvent.createUpdateInvalidEvent(fxRecordModel, this,
                                        (CustomerFailedEvent) customerEvent);
                            }
                            if (null != userEvent && userEvent instanceof UserFailedEvent) {
                                if (dao.getRowState() == DataRowState.NEW) {
                                    return AppointmentEvent.createInsertInvalidEvent(fxRecordModel, this,
                                            (UserFailedEvent) userEvent);
                                }
                                return AppointmentEvent.createUpdateInvalidEvent(fxRecordModel, this,
                                        (UserFailedEvent) userEvent);
                            }
                            return null;
                        }
                    }
                }
            }

            if (dao.getRowState() == DataRowState.NEW) {
                return AppointmentEvent.createInsertInvalidEvent(fxRecordModel, this, message);
            }
            return AppointmentEvent.createUpdateInvalidEvent(fxRecordModel, this, message);
        }

        @Override
        public AppointmentOpRequestEvent createEditRequestEvent(final AppointmentModel model, final Object source) {
            return new AppointmentOpRequestEvent(model, source, false);
        }

        @Override
        public AppointmentOpRequestEvent createDeleteRequestEvent(final AppointmentModel model, final Object source) {
            return new AppointmentOpRequestEvent(model, source, true);
        }

        @Override
        public Class<AppointmentEvent> getModelResultEventClass() {
            return AppointmentEvent.class;
        }

        @Override
        public EventType<AppointmentSuccessEvent> getSuccessEventType() {
            return AppointmentSuccessEvent.SUCCESS_EVENT_TYPE;
        }

        @Override
        public EventType<AppointmentOpRequestEvent> getBaseRequestEventType() {
            return AppointmentOpRequestEvent.APPOINTMENT_OP_REQUEST;
        }

        @Override
        public EventType<AppointmentOpRequestEvent> getEditRequestEventType() {
            return AppointmentOpRequestEvent.EDIT_REQUEST;
        }

        @Override
        public EventType<AppointmentOpRequestEvent> getDeleteRequestEventType() {
            return AppointmentOpRequestEvent.DELETE_REQUEST;
        }

    }
}
