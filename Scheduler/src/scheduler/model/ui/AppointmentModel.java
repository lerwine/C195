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
import scheduler.model.PredefinedData;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.DateTimeUtil;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.appointment.AppointmentModelFilter;

/**
 * List item model for {@link AppointmentDAO} data access objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentModel extends EntityModel<AppointmentDAO> implements PartialAppointmentModel<AppointmentDAO>,
        AppointmentEntity<LocalDateTime> {

    public static final Factory FACTORY = new Factory();

    public static String calculateEffectiveLocation(final AppointmentType type, final String customerAddress,
            final String url, final String location) {
        switch (type) {
            case CUSTOMER_SITE:
                return (customerAddress.isEmpty())
                        ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER)
                        : customerAddress;
            case VIRTUAL:
                return (url.isEmpty())
                        ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL)
                        : url;
            case CORPORATE_LOCATION:
                final CorporateAddress corporateAddress = PredefinedData.getCorporateAddress(location);
                return AddressModel.calculateSingleLineAddress(corporateAddress.getAddress1(),
                        corporateAddress.getAddress2(),
                        AddressModel.calculateCityZipCountry(corporateAddress.getCity().getName(),
                                corporateAddress.getCity().getCountry().getName(), corporateAddress.getPostalCode()),
                        corporateAddress.getPhone());
            case PHONE:
                return (location.isEmpty())
                        ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_PHONE)
                        : String.format("tel: %s", location);
            default:
                return location;
        }
    }

    public static int compareByDates(final AppointmentModel a, final AppointmentModel b) {
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
        final int c = x.compareTo(y);
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
        customer = new SimpleObjectProperty<>(this, PROP_CUSTOMER, PartialCustomerModel.createModel(dao.getCustomer()));
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
        user = new SimpleObjectProperty<>(this, PROP_USER, PartialUserModel.createModel(dao.getUser()));
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
                            return AddressModel.calculateMultiLineAddress(
                                    AddressModel.calculateAddressLines(corporateAddress.getAddress1(),
                                            corporateAddress.getAddress2()),
                                    AddressModel.calculateCityZipCountry(corporateAddress.getCity().getName(),
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

    }

    @Override
    protected void onModelSaved(ModelEvent<AppointmentDAO, ? extends EntityModel<AppointmentDAO>> event) {
        AppointmentDAO dao = event.getDataAccessObject();
        PartialCustomerModel<? extends PartialCustomerDAO> currentCustomer = customer.get();
        PartialCustomerDAO newCustomer = dao.getCustomer();
        if (null == currentCustomer || null == newCustomer) {
            customer.set(PartialCustomerModel.createModel(dao.getCustomer()));
        } else {
            PartialCustomerDAO currentDao = currentCustomer.dataObject();
            if (currentDao != newCustomer && !(ModelHelper.areSameRecord(currentDao, newCustomer) && currentDao instanceof CustomerDAO)) {
                customer.set(PartialCustomerModel.createModel(dao.getCustomer()));
            }
        }
        PartialUserModel<? extends PartialUserDAO> currentUser = user.get();
        PartialUserDAO newUser = dao.getUser();
        if (null == currentUser || null == newUser) {
            user.set(PartialUserModel.createModel(dao.getUser()));
        } else {
            PartialUserDAO currentDao = currentUser.dataObject();
            if (currentDao != newUser && !(ModelHelper.areSameRecord(currentDao, newUser) && currentDao instanceof UserDAO)) {
                user.set(PartialUserModel.createModel(dao.getUser()));
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

    @Override
    public ObjectProperty<? extends PartialCustomerModel<? extends PartialCustomerDAO>> customerProperty() {
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
    public PartialUserModel<? extends PartialUserDAO> getUser() {
        return user.get();
    }

    public void setUser(final PartialUserModel<? extends PartialUserDAO> value) {
        user.set(value);
    }

    @Override
    public ObjectProperty<PartialUserModel<? extends PartialUserDAO>> userProperty() {
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

    public void setTitle(final String value) {
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

    public void setDescription(final String value) {
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

    public void setLocation(final String value) {
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

    public void setContact(final String value) {
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

    public void setType(final AppointmentType value) {
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

    public void setUrl(final String value) {
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

    public void setStart(final LocalDateTime value) {
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

    public void setEnd(final LocalDateTime value) {
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
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        final ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(primaryKeyProperty());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState()).addDataObject(customer).addDataObject(user)
                .addString(title).addString(description).addString(location).addString(contact).addEnum(type)
                .addString(url).addLocalDateTime(start).addLocalDateTime(end).addLocalDateTime(createDateProperty())
                .addString(createdByProperty()).addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty());
    }

    public final static class Factory extends EntityModel.EntityModelFactory<AppointmentDAO, AppointmentModel, AppointmentEvent, AppointmentSuccessEvent> {

        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<AppointmentDAO, AppointmentEvent> getDaoFactory() {
            return AppointmentDAO.FACTORY;
        }

        @Override
        protected AppointmentModel onCreateNew(AppointmentDAO dao) {
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
        public DataAccessObject.SaveDaoTask<AppointmentDAO, AppointmentModel, AppointmentEvent> createSaveTask(AppointmentModel model) {
            return new AppointmentDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<AppointmentDAO, AppointmentModel, AppointmentEvent> createDeleteTask(AppointmentModel model) {
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
        public Class<AppointmentEvent> getModelEventClass() {
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
