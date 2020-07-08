package scheduler.model.ui;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.IUserDAO;
import static scheduler.model.Appointment.MAX_LENGTH_TITLE;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.model.UserStatus;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.AnyTrueSet;
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

    private final AnyTrueSet changeIndicator;
    private final AnyTrueSet validityIndicator;
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper changed;
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
    private final AnyTrueSet.Node customerChanged;
    private final AnyTrueSet.Node customerValid;
    private final AnyTrueSet.Node userChanged;
    private final AnyTrueSet.Node userValid;
    private final AnyTrueSet.Node titleChanged;
    private final AnyTrueSet.Node titleValid;
    private final AnyTrueSet.Node descriptionChanged;
    private final AnyTrueSet.Node locationChanged;
    private final AnyTrueSet.Node locationValid;
    private final AnyTrueSet.Node typeChanged;
    private final AnyTrueSet.Node contactChanged;
    private final AnyTrueSet.Node contactValid;
    private final AnyTrueSet.Node urlChanged;
    private final AnyTrueSet.Node urlValid;
    private final AnyTrueSet.Node startChanged;
    private final AnyTrueSet.Node startValid;
    private final AnyTrueSet.Node endChanged;
    private final AnyTrueSet.Node endValid;

    @SuppressWarnings("incomplete-switch")
    public AppointmentModel(final AppointmentDAO dao) {
        super(dao);
        changeIndicator = new AnyTrueSet();
        validityIndicator = new AnyTrueSet();
        customer = new SimpleObjectProperty<>(this, "customer", CustomerItem.createModel(dao.getCustomer()));
        customerChanged = changeIndicator.add(false);
        customerValid = validityIndicator.add(null != customer.get());
        customer.addListener((observable, oldValue, newValue) -> {
            customerValid.setValid(null != newValue);
            customerChanged.setValid(!ModelHelper.areSameRecord(newValue, dao.getCustomer()));
        });
        customerName = new ReadOnlyStringBindingProperty(this, "customerName", Bindings.selectString(customer, "name"));
        customerAddress1 = new ReadOnlyStringBindingProperty(this, "customerName",
                Bindings.selectString(customer, "address1"));
        customerAddress2 = new ReadOnlyStringBindingProperty(this, "customerAddress2",
                Bindings.selectString(customer, "address2"));
        customerCityName = new ReadOnlyStringBindingProperty(this, "customerCityName",
                Bindings.selectString(customer, "cityName"));
        customerCountryName = new ReadOnlyStringBindingProperty(this, "customerCountryName",
                Bindings.selectString(customer, "countryName"));
        customerPostalCode = new ReadOnlyStringBindingProperty(this, "customerPostalCode",
                Bindings.selectString(customer, "postalCode"));
        customerPhone = new ReadOnlyStringBindingProperty(this, "customerPhone",
                Bindings.selectString(customer, "phone"));
        customerCityZipCountry = new ReadOnlyStringBindingProperty(this, "customerCityZipCountry",
                Bindings.selectString(customer, "cityZipCountry"));
        customerAddressText = new ReadOnlyStringBindingProperty(this, "customerAddressText",
                Bindings.selectString(customer, "addressText"));
        customerActive = new ReadOnlyBooleanBindingProperty(this, "customerActive",
                Bindings.selectBoolean(customer, "active"));
        user = new SimpleObjectProperty<>(this, "user", UserItem.createModel(dao.getUser()));
        userChanged = changeIndicator.add(false);
        userValid = validityIndicator.add(null != user.get());
        user.addListener((observable, oldValue, newValue) -> {
            userValid.setValid(null != newValue);
            userChanged.setValid(!ModelHelper.areSameRecord(newValue, dao.getUser()));
        });
        userName = new ReadOnlyStringBindingProperty(this, "userName", Bindings.selectString(user, "userName"));
        userStatus = new ReadOnlyObjectBindingProperty<>(this, "userStatus", Bindings.select(user, "status"));
        userStatusDisplay = new ReadOnlyStringBindingProperty(this, "userStatusDisplay",
                Bindings.selectString(user, "statusDisplay"));
        title = new NonNullableStringProperty(this, "title", dao.getTitle());
        titleChanged = changeIndicator.add(false);
        titleValid = validityIndicator.add(Values.isNotNullWhiteSpaceOrEmpty(title.get()));
        title.addListener((observable, oldValue, newValue) -> {
            titleValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(newValue));
            String n = dao.getTitle();
            titleChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        description = new NonNullableStringProperty(this, "description", dao.getDescription());
        descriptionChanged = changeIndicator.add(false);
        description.addListener((observable, oldValue, newValue) -> {
            String n = dao.getDescription();
            descriptionChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        final AppointmentType at = dao.getType();
        type = new SimpleObjectProperty<>(this, "type", (null == at) ? AppointmentType.OTHER : at);
        location = new NonNullableStringProperty(this, "location", dao.getLocation());
        locationChanged = changeIndicator.add(false);
        locationValid = validityIndicator.add(type.get() == AppointmentType.VIRTUAL || type.get() == AppointmentType.CUSTOMER_SITE ||
                Values.isNotNullWhiteSpaceOrEmpty(location.get()));
        location.addListener((observable, oldValue, newValue) -> {
            locationValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(newValue));
            String n = dao.getLocation();
            locationChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        typeChanged = changeIndicator.add(false);
        typeDisplay = new ReadOnlyStringBindingProperty(this, "typeDisplay",
                Bindings.createStringBinding(() -> AppointmentType.toDisplayText(type.get()), type));
        contact = new NonNullableStringProperty(this, "contact", dao.getContact());
        contactChanged = changeIndicator.add(false);
        contactValid = validityIndicator.add(type.get() != AppointmentType.OTHER || Values.isNotNullWhiteSpaceOrEmpty(contact.get()));
        contact.addListener((observable, oldValue, newValue) -> {
            contactValid.setValid(type.get() != AppointmentType.OTHER || Values.isNotNullWhiteSpaceOrEmpty(newValue));
            String n = dao.getContact();
            contactChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        url = new NonNullableStringProperty(this, "url", dao.getUrl());
        urlChanged = changeIndicator.add(false);
        urlValid = validityIndicator.add(type.get() != AppointmentType.VIRTUAL || Values.isNotNullWhiteSpaceOrEmpty(url.get()));
        url.addListener((observable, oldValue, newValue) -> {
            urlValid.setValid(type.get() != AppointmentType.VIRTUAL || Values.isNotNullWhiteSpaceOrEmpty(newValue));
            String n = dao.getUrl();
            urlChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        start = new SimpleObjectProperty<>(this, "start", DB.toLocalDateTime(dao.getStart()));
        end = new SimpleObjectProperty<>(this, "end", DB.toLocalDateTime(dao.getEnd()));
        startChanged = changeIndicator.add(false);
        startValid = validityIndicator.add(null != start.get() && (null == end.get() || start.get().compareTo(end.get()) <= 0));
        endChanged = changeIndicator.add(false);
        endValid = validityIndicator.add(null != end.get());
        start.addListener((observable, oldValue, newValue) -> {
            startValid.setValid(null != newValue && (null == end.get() || newValue.compareTo(end.get()) <= 0));
            Timestamp s = dao.getStart();
            startChanged.setValid(((null == newValue) ? null == s : null != s && newValue.equals(DB.toLocalDateTime(s))));
        });
        end.addListener((observable, oldValue, newValue) -> {
            Timestamp e = dao.getEnd();
            endValid.setValid(null != newValue);
            endChanged.setValid(((null == newValue) ? null == e : null != e && newValue.equals(DB.toLocalDateTime(e))));
        });
        type.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case VIRTUAL:
                    urlValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(url.get()));
                    contactValid.setValid(true);
                    locationValid.setValid(true);
                    break;
                case CUSTOMER_SITE:
                    urlValid.setValid(true);
                    contactValid.setValid(true);
                    locationValid.setValid(true);
                    break;
                case OTHER:
                    urlValid.setValid(true);
                    contactValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(contact.get()));
                    locationValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(location.get()));
                    break;
                default:
                    urlValid.setValid(true);
                    contactValid.setValid(true);
                    locationValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(location.get()));
                    break;
            }
            typeChanged.setValid(newValue != dao.getType());
        });
        
        dao.addPropertyChangeListener((evt) -> {
            switch (evt.getPropertyName()) {
                case PROP_CUSTOMER:
                    // FIXME: update validity and change
                    break;
                case PROP_USER:
                    // FIXME: update validity and change
                    break;
                case PROP_TITLE:
                    // FIXME: update validity and change
                    break;
                case PROP_DESCRIPTION:
                    // FIXME: update validity and change
                    break;
                case PROP_TYPE:
                    // FIXME: update validity and change
                    break;
                case PROP_LOCATION:
                    // FIXME: update validity and change
                    break;
                case PROP_CONTACT:
                    // FIXME: update validity and change
                    break;
                case PROP_URL:
                    // FIXME: update validity and change
                    break;
                case PROP_START:
                    // FIXME: update validity and change
                    break;
                case PROP_END:
                    // FIXME: update validity and change
                    break;
            }
        });
        

        final StringBinding wsNormalizedLocation = Bindings
                .createStringBinding(() -> Values.asNonNullAndWsNormalized(location.get()), location);
        final StringBinding wsNormalizedUrl = Bindings
                .createStringBinding(() -> Values.asNonNullAndWsNormalized(url.get()), url);
        effectiveLocation = new ReadOnlyStringBindingProperty(this, "effectiveLocation", () -> {
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

        valid = new ReadOnlyBooleanWrapper(this, PROP_VALID, validityIndicator.isValid());
        validityIndicator.validProperty().addListener((observable, oldValue, newValue) -> {
            valid.set(newValue);
        });
        changed = new ReadOnlyBooleanWrapper(this, PROP_CHANGED, changeIndicator.isValid());
        changeIndicator.validProperty().addListener((observable, oldValue, newValue) -> {
            changed.set(newValue);
        });
    }

    @Override
    public CustomerItem<? extends ICustomerDAO> getCustomer() {
        return customer.get();
    }

    public void setCustomer(final CustomerItem<? extends ICustomerDAO> value) {
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

    public void setUser(final UserItem<? extends IUserDAO> value) {
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
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    @Override
    public boolean isChanged() {
        return changed.get();
    }

    @Override
    public ReadOnlyBooleanProperty changedProperty() {
        return changed;
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
                .addString(lastModifiedByProperty()).addBoolean(valid);
    }

    public final static class Factory extends FxRecordModel.FxModelFactory<AppointmentDAO, AppointmentModel> {

        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<AppointmentDAO> getDaoFactory() {
            return AppointmentDAO.FACTORY;
        }

        @Override
        public AppointmentModel createNew(final AppointmentDAO dao) {
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
        public DataAccessObject.SaveDaoTask<AppointmentDAO, AppointmentModel> createSaveTask(final AppointmentModel model, boolean force) {
            return new AppointmentDAO.SaveTask(model, force);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<AppointmentDAO, AppointmentModel> createDeleteTask(final AppointmentModel model) {
            return new AppointmentDAO.DeleteTask(model);
        }

        @Override
        public String validateProperties(final AppointmentModel fxRecordModel) {
            final AppointmentDAO dao = fxRecordModel.dataObject();
            String s;
            if (dao.getRowState() == DataRowState.DELETED) {
                return "Appointment has already been deleted";
            }
            if ((s = dao.getTitle()).isEmpty()) {
                return "Title not defined";
            }
            if (s.length() > MAX_LENGTH_TITLE) {
                return "Title too long";
            }
            final Timestamp start = dao.getStart();
            Timestamp end;
            if (null == start) {
                return "Start date/time not defined";
            }
            if (null == (end = dao.getEnd())) {
                return "End date/time not defined";
            }
            if (start.compareTo(end) > 0) {
                return "Start is after end date/time";
            }
            switch (dao.getType()) {
                case CORPORATE_LOCATION:
                    s = dao.getLocation();
                    if (s.isEmpty()) {
                        return "Location not defined";
                    }
                    if (!PredefinedData.getCorporateAddressMap().containsKey(s)) {
                        return "Invalid corporate location key";
                    }
                    break;
                case VIRTUAL:
                    if (Values.isNullWhiteSpaceOrEmpty(dao.getUrl())) {
                        return "URL not defined";
                    }
                    break;
                case CUSTOMER_SITE:
                    if (Values.isNullWhiteSpaceOrEmpty(dao.getContact())) {
                        return "Contact not defined";
                    }
                    break;
                default:
                    if (Values.isNullWhiteSpaceOrEmpty(dao.getLocation())) {
                        return "Location not defined";
                    }
                    break;
            }
            final CustomerItem<? extends ICustomerDAO> customer = fxRecordModel.getCustomer();
            if (null == customer) {
                return "Customer not specified";
            }
            if (customer instanceof CustomerModel) {
                String message = CustomerModel.FACTORY.validateProperties((CustomerModel) customer);
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
            }
            final UserItem<? extends IUserDAO> user = fxRecordModel.getUser();
            if (null == user) {
                return "User not specified";
            }
            if (user instanceof UserModel) {
                return UserModel.FACTORY.validateProperties((UserModel) user);
            }
            return null;
        }

    }
}
