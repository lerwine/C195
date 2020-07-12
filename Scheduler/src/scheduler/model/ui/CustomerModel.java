package scheduler.model.ui;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.PartialAddressDAO;
import scheduler.events.AddressEvent;
import scheduler.events.AddressFailedEvent;
import scheduler.events.CustomerEvent;
import scheduler.events.CustomerOpRequestEvent;
import scheduler.events.CustomerSuccessEvent;
import scheduler.events.ModelEvent;
import scheduler.model.AddressProperties;
import static scheduler.model.Customer.MAX_LENGTH_NAME;
import scheduler.model.CustomerEntity;
import scheduler.model.ModelHelper;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.LogHelper;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.view.customer.CustomerModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CustomerModel extends EntityModel<CustomerDAO> implements PartialCustomerModel<CustomerDAO>, CustomerEntity<LocalDateTime> {

    public static final Factory FACTORY = new Factory();

    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<PartialAddressModel<? extends PartialAddressDAO>> address;
    private final ReadOnlyStringBindingProperty address1;
    private final ReadOnlyStringBindingProperty address2;
    private final ReadOnlyStringBindingProperty cityName;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty postalCode;
    private final ReadOnlyStringBindingProperty phone;
    private final ReadOnlyStringBindingProperty cityZipCountry;
    private final ReadOnlyStringBindingProperty addressText;
    private final SimpleBooleanProperty active;
    private final ReadOnlyStringBindingProperty multiLineAddress;

    private CustomerModel(CustomerDAO dao) {
        super(dao);
        name = new NonNullableStringProperty(this, PROP_NAME, dao.getName());
        address = new SimpleObjectProperty<>(this, PROP_ADDRESS, PartialAddressModel.createModel(dao.getAddress()));
        active = new SimpleBooleanProperty(this, PROP_ACTIVE, dao.isActive());
        address1 = new ReadOnlyStringBindingProperty(this, PROP_ADDRESS1, Bindings.selectString(address, AddressProperties.PROP_ADDRESS1));
        address2 = new ReadOnlyStringBindingProperty(this, PROP_ADDRESS2, Bindings.selectString(address, AddressProperties.PROP_ADDRESS2));
        cityName = new ReadOnlyStringBindingProperty(this, PROP_CITYNAME, Bindings.selectString(address, PartialAddressModel.PROP_CITYNAME));
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(address, PartialAddressModel.PROP_COUNTRYNAME));
        postalCode = new ReadOnlyStringBindingProperty(this, PROP_POSTALCODE, Bindings.selectString(address, AddressProperties.PROP_POSTALCODE));
        phone = new ReadOnlyStringBindingProperty(this, PROP_PHONE, Bindings.selectString(address, AddressProperties.PROP_PHONE));
        cityZipCountry = new ReadOnlyStringBindingProperty(this, PROP_CITYZIPCOUNTRY, Bindings.selectString(address, PartialAddressModel.PROP_CITYZIPCOUNTRY));
        addressText = new ReadOnlyStringBindingProperty(this, PROP_ADDRESSTEXT,
                () -> AddressModel.calculateSingleLineAddress(address1.get(), address2.get(), cityZipCountry.get(), phone.get()));
        multiLineAddress = new ReadOnlyStringBindingProperty(this, PROP_MULTILINEADDRESS,
                () -> AddressModel.calculateMultiLineAddress(AddressModel.calculateAddressLines(address1.get(), address2.get()),
                        cityZipCountry.get(), phone.get()));
    }

    @Override
    protected void onModelSaved(ModelEvent<CustomerDAO, ? extends EntityModel<CustomerDAO>> event) {
        CustomerDAO dao = event.getDataAccessObject();
        name.set(dao.getName());
        PartialAddressModel<? extends PartialAddressDAO> currentAddress = address.get();
        PartialAddressDAO newAddress = dao.getAddress();
        if (null == currentAddress || null == newAddress) {
            address.set(PartialAddressModel.createModel(dao.getAddress()));
        } else {
            PartialAddressDAO currentDao = currentAddress.dataObject();
            if (currentDao != newAddress && !(ModelHelper.areSameRecord(currentDao, newAddress) && currentDao instanceof AddressDAO)) {
                address.set(PartialAddressModel.createModel(dao.getAddress()));
            }
        }
    }

    @Override
    public String getMultiLineAddress() {
        return multiLineAddress.get();
    }

    @Override
    public ReadOnlyStringProperty multiLineAddressProperty() {
        return multiLineAddress;
    }

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public PartialAddressModel<? extends PartialAddressDAO> getAddress() {
        return address.get();
    }

    public void setAddress(PartialAddressModel<? extends PartialAddressDAO> value) {
        address.set(value);
    }

    @Override
    public ObjectProperty<PartialAddressModel<? extends PartialAddressDAO>> addressProperty() {
        return address;
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ReadOnlyStringBindingProperty address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ReadOnlyStringBindingProperty address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ReadOnlyStringBindingProperty cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringBindingProperty countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ReadOnlyStringBindingProperty postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ReadOnlyStringBindingProperty phoneProperty() {
        return phone;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean value) {
        active.set(value);
    }

    @Override
    public BooleanProperty activeProperty() {
        return active;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyStringBindingProperty cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public String getAddressText() {
        return addressText.get();
    }

    @Override
    public ReadOnlyProperty<String> addressTextProperty() {
        return addressText;
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public int hashCode() {
        if (isNewRow()) {
            int hash = 5;
            hash = 73 * hash + Objects.hashCode(name.get());
            hash = 73 * hash + Objects.hashCode(address.get());
            hash = 73 * hash + Objects.hashCode(active.get());
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CustomerModel) {
            final CustomerModel other = (CustomerModel) obj;
            if (isNewRow()) {
                return name.isEqualTo(other.name).get() && address.isEqualTo(other.address).get() && active.isEqualTo(other.active).get();
            }
            return !other.isNewRow() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(primaryKeyProperty());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(name)
                .addDataObject(address)
                .addBoolean(active)
                .addLocalDateTime(createDateProperty())
                .addString(createdByProperty())
                .addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty());
    }

    public final static class Factory extends EntityModel.EntityModelFactory<CustomerDAO, CustomerModel, CustomerEvent, CustomerSuccessEvent> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Factory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(Factory.class.getName());

        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<CustomerDAO, CustomerEvent> getDaoFactory() {
            return CustomerDAO.FACTORY;
        }

        @Override
        protected CustomerModel onCreateNew(CustomerDAO dao) {
            return new CustomerModel(dao);
        }

        @Override
        public CustomerModelFilter getAllItemsFilter() {
            return CustomerModelFilter.all();
        }

        @Override
        public CustomerModelFilter getDefaultFilter() {
            return CustomerModelFilter.active();
        }

        @Override
        public DataAccessObject.SaveDaoTask<CustomerDAO, CustomerModel, CustomerEvent> createSaveTask(CustomerModel model) {
            return new CustomerDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<CustomerDAO, CustomerModel, CustomerEvent> createDeleteTask(CustomerModel model) {
            return new CustomerDAO.DeleteTask(model, false);
        }

        @Override
        public CustomerEvent validateForSave(CustomerModel fxRecordModel) {
            CustomerDAO dao = fxRecordModel.dataObject();
            String message;
            if (dao.getRowState() == DataRowState.DELETED) {
                message = "Customer has already been deleted";
            } else {
                String name = dao.getName();
                if (name.isEmpty()) {
                    message = "Customer name not defined";
                } else if (name.length() > MAX_LENGTH_NAME) {
                    message = "Name too long";
                } else {
                    AddressEvent event;
                    PartialAddressModel<? extends PartialAddressDAO> a = fxRecordModel.getAddress();
                    if (null != a) {
                        if (a instanceof AddressModel) {
                            if (null == (event = AddressModel.FACTORY.validateForSave((AddressModel) a))) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        event = null;
                    }
                    if (null != event) {
                        if (event instanceof AddressFailedEvent) {
                            if (dao.getRowState() == DataRowState.NEW) {
                                return CustomerEvent.createInsertInvalidEvent(fxRecordModel, this, (AddressFailedEvent) event);
                            }
                            return CustomerEvent.createUpdateInvalidEvent(fxRecordModel, this, (AddressFailedEvent) event);
                        }
                        return null;
                    }

                    message = "Address not specified.";
                }
            }

            if (dao.getRowState() == DataRowState.NEW) {
                return CustomerEvent.createInsertInvalidEvent(fxRecordModel, this, message);
            }
            return CustomerEvent.createUpdateInvalidEvent(fxRecordModel, this, message);
        }

        @Override
        public CustomerOpRequestEvent createEditRequestEvent(CustomerModel model, Object source) {
            return new CustomerOpRequestEvent(model, source, false);
        }

        @Override
        public CustomerOpRequestEvent createDeleteRequestEvent(CustomerModel model, Object source) {
            return new CustomerOpRequestEvent(model, source, true);
        }

        @Override
        public Class<CustomerEvent> getModelEventClass() {
            return CustomerEvent.class;
        }

        @Override
        public EventType<CustomerSuccessEvent> getSuccessEventType() {
            return CustomerSuccessEvent.SUCCESS_EVENT_TYPE;
        }

        @Override
        public EventType<CustomerOpRequestEvent> getBaseRequestEventType() {
            return CustomerOpRequestEvent.CUSTOMER_OP_REQUEST;
        }

        @Override
        public EventType<CustomerOpRequestEvent> getEditRequestEventType() {
            return CustomerOpRequestEvent.EDIT_REQUEST;
        }

        @Override
        public EventType<CustomerOpRequestEvent> getDeleteRequestEventType() {
            return CustomerOpRequestEvent.DELETE_REQUEST;
        }

    }

}
