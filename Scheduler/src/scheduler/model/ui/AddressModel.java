package scheduler.model.ui;

import java.util.Objects;
import java.util.TimeZone;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventTarget;
import javafx.event.EventType;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.ICityDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.City;
import scheduler.model.CityProperties;
import scheduler.model.Country;
import scheduler.model.DataObject;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import static scheduler.util.Values.asNonNullAndWsNormalized;
import scheduler.view.ModelFilter;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import events.AddressEvent;
import events.DbOperationType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressModel extends FxRecordModel<AddressDAO> implements AddressItem<AddressDAO> {

    public static final Factory FACTORY = new Factory();

    /**
     * Formats both address lines into a one or two line string.
     *
     * @param line1 The first address line.
     * @param line2 The second address line.
     * @return A First and second address lines formatted as a 1 or 2 line white-space-normalized string.
     */
    public static String calculateAddressLines(String line1, String line2) {
        if ((line1 = asNonNullAndWsNormalized(line1)).isEmpty()) {
            return asNonNullAndWsNormalized(line2);
        }
        return ((line2 = asNonNullAndWsNormalized(line2)).isEmpty()) ? line1 : String.format("%s%n%s", line1, line2);
    }

    /**
     * Formats city, postal code and country into a single line.
     *
     * @param city The name of the city.
     * @param country The name of the country.
     * @param postalCode The postal code.
     * @return The city, postal code and country formated as a single white-space-normalized string.
     */
    public static String calculateCityZipCountry(String city, String country, String postalCode) {
        if ((city = asNonNullAndWsNormalized(city)).isEmpty()) {
            return ((postalCode = asNonNullAndWsNormalized(postalCode)).isEmpty())
                    ? asNonNullAndWsNormalized(country)
                    : (((asNonNullAndWsNormalized(country)).isEmpty())
                    ? postalCode : String.format("%s, %s", postalCode, country));
        }
        if ((country = asNonNullAndWsNormalized(country)).isEmpty()) {
            return ((postalCode = asNonNullAndWsNormalized(postalCode)).isEmpty())
                    ? city : String.format("%s %s", city, postalCode);
        }
        return ((postalCode = asNonNullAndWsNormalized(postalCode)).isEmpty())
                ? String.format("%s, %s", city, country)
                : String.format("%s %s, %s", city, postalCode, country);
    }

    public static String calculateCityZipCountry(City city, String postalCode) {
        if (null == city) {
            return calculateCityZipCountry("", "", postalCode);
        }
        Country country = city.getCountry();
        return calculateCityZipCountry(city.getName(), (null == country) ? "" : country.getName(), postalCode);
    }

    /**
     * Formats address as a multi-line string.
     *
     * @param address A 1 or 2 line white-space-normalized string, usually formatted using {@link #calculateAddressLines(String, String)}.
     * @param cityZipCountry A single line white-space-normalized string, usually formatted using
     * {@link #calculateCityZipCountry(String, String, String)}.
     * @param phone The phone number string which will be normalized in this method.
     * @return A multi-line white-space-normalized address string.
     */
    public static String calculateMultiLineAddress(String address, String cityZipCountry, String phone) {
        if (address.isEmpty()) {
            if (cityZipCountry.isEmpty()) {
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? ""
                        : String.format("%s %s", getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
            }
            return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? cityZipCountry : String.format("%s%n%s %s", cityZipCountry,
                    getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
        }
        if (cityZipCountry.isEmpty()) {
            return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? address
                    : String.format("%s%n%s %s", address, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
        }
        return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? String.format("%s%n%s", address, cityZipCountry)
                : String.format("%s%n%s%n%s %s", address, cityZipCountry, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
    }

    /**
     * Formats an address as a single line string.
     *
     * @param address1 The first line of the street address which will be normalized by this method.
     * @param address2 The second line of the street address which will be normalized by this method.
     * @param cityZipCountry A single line white-space-normalized string, usually formatted using
     * {@link #calculateCityZipCountry(String, String, String)}.
     * @param phone The phone number string which will be normalized in this method.
     * @return The address formatted as a single line white-space-normalized string.
     */
    public static String calculateSingleLineAddress(String address1, String address2, String cityZipCountry, String phone) {
        if ((address1 = asNonNullAndWsNormalized(address1)).isEmpty()) {
            if ((address2 = asNonNullAndWsNormalized(address2)).isEmpty()) {
                return (cityZipCountry.isEmpty()) ? asNonNullAndWsNormalized(phone)
                        : (((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                        ? cityZipCountry : String.format("%s, %s", cityZipCountry, phone));
            }
            if (cityZipCountry.isEmpty()) {
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? address2 : String.format("%s, %s", address2, phone);
            }
            return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                    ? String.format("%s, %s", address2, cityZipCountry)
                    : String.format("%s, %s, %s", address2, cityZipCountry, phone);
        }
        if ((address2 = asNonNullAndWsNormalized(address2)).isEmpty()) {
            if (cityZipCountry.isEmpty()) {
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? address1 : String.format("%s, %s", address1, phone);
            }
            return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                    ? String.format("%s, %s", address1, cityZipCountry)
                    : String.format("%s, %s, %s", address1, cityZipCountry, phone);
        }
        if (cityZipCountry.isEmpty()) {
            return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                    ? String.format("%s, %s", address1, address2)
                    : String.format("%s, %s, %s", address1, address2, phone);
        }
        return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                ? String.format("%s, %s, %s", address1, address2, cityZipCountry)
                : String.format("%s, %s, %s, %s", address1, address2, cityZipCountry, phone);
    }

    private final NonNullableStringProperty address1;
    private final NonNullableStringProperty address2;
    private final ReadOnlyStringBindingProperty addressLines;
    private final SimpleObjectProperty<CityItem<? extends ICityDAO>> city;
    private final ReadOnlyStringBindingProperty cityName;
    private final ReadOnlyStringBindingProperty countryName;
    private final NonNullableStringProperty postalCode;
    private final NonNullableStringProperty phone;
    private final ReadOnlyStringBindingProperty cityZipCountry;
    private final ReadOnlyStringBindingProperty language;
    private final ReadOnlyObjectBindingProperty<TimeZone> timeZone;
    private final ReadOnlyBooleanBindingProperty valid;

    /**
     * FX model for {@link scheduler.model.Address} objects.
     *
     * @param dao The backing {@link AddressDAO} object.
     */
    public AddressModel(AddressDAO dao) {
        super(dao);
        address1 = new NonNullableStringProperty(this, PROP_ADDRESS1, dao.getAddress1());
        address2 = new NonNullableStringProperty(this, PROP_ADDRESS2, dao.getAddress2());
        addressLines = new ReadOnlyStringBindingProperty(this, PROP_ADDRESSLINES,
                () -> AddressModel.calculateAddressLines(address1.get(), address2.get()), address1, address2);
        city = new SimpleObjectProperty<>(this, PROP_CITY, CityItem.createModel(dao.getCity()));
        cityName = new ReadOnlyStringBindingProperty(this, PROP_CITYNAME, Bindings.selectString(city, CityProperties.PROP_NAME));
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(city, CityItem.PROP_COUNTRYNAME));
        postalCode = new NonNullableStringProperty(this, PROP_POSTALCODE, dao.getPostalCode());
        phone = new NonNullableStringProperty(this, PROP_PHONE, dao.getPhone());
        cityZipCountry = new ReadOnlyStringBindingProperty(this, PROP_CITYZIPCOUNTRY,
                () -> AddressModel.calculateCityZipCountry(cityName.get(), countryName.get(), postalCode.get()),
                cityName, countryName, postalCode);
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(city, CityItem.PROP_LANGUAGE));
        timeZone = new ReadOnlyObjectBindingProperty<>(this, PROP_TIMEZONE, Bindings.select(city, CityItem.PROP_TIMEZONE));
        valid = new ReadOnlyBooleanBindingProperty(this, PROP_VALID,
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(address1.get()), address1)
                        .or(Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(address2.get()), address2))
                        .and(Bindings.selectBoolean(city, PROP_VALID)).and(Bindings.select(city, DataObject.PROP_ROWSTATE).isNotEqualTo(DataRowState.DELETED)));
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    public void setAddress1(String value) {
        address1.set(value);
    }

    @Override
    public StringProperty address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(String value) {
        address2.set(value);
    }

    @Override
    public StringProperty address2Property() {
        return address2;
    }

    public String getAddressLines() {
        return addressLines.get();
    }

    @Override
    public ReadOnlyStringProperty addressLinesProperty() {
        return addressLines;
    }

    @Override
    public CityItem<? extends ICityDAO> getCity() {
        return city.get();
    }

    public void setCity(CityItem<? extends ICityDAO> value) {
        city.set(value);
    }

    @Override
    public ObjectProperty<CityItem<? extends ICityDAO>> cityProperty() {
        return city;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ReadOnlyStringProperty cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String value) {
        postalCode.set(value);
    }

    @Override
    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String value) {
        phone.set(value);
    }

    @Override
    public StringProperty phoneProperty() {
        return phone;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyStringProperty cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public TimeZone getTimeZone() {
        return timeZone.get();
    }

    @Override
    public ReadOnlyObjectProperty<TimeZone> timeZoneProperty() {
        return timeZone;
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language;
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
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.address1);
        hash = 23 * hash + Objects.hashCode(this.city);
        hash = 23 * hash + Objects.hashCode(this.postalCode);
        hash = 23 * hash + Objects.hashCode(this.phone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof AddressModel) {
            final AddressModel other = (AddressModel) obj;
            if (isNewRow()) {
                return address1.isEqualTo(other.address1).get() && address2.isEqualTo(other.address2).get() && city.isEqualTo(other.city).get()
                        && postalCode.isEqualTo(other.postalCode).get() && phone.isEqualTo(other.phone).get();
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
                .addString(address1)
                .addString(address2)
                .addDataObject(city)
                .addString(postalCode)
                .addString(phone)
                .addLocalDateTime(createDateProperty())
                .addString(createdByProperty())
                .addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty())
                .addBoolean(valid);
    }

    public final static class Factory extends FxRecordModel.ModelFactory<AddressDAO, AddressModel, AddressEvent> {

//        private static final Logger LOG = Logger.getLogger(Factory.class.getName());
        // Singleton
        private Factory() {
            super(AddressEvent.ADDRESS_MODEL_EVENT_TYPE);
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public AddressDAO.FactoryImpl getDaoFactory() {
            return AddressDAO.FACTORY;
        }

        @Override
        public AddressModel createNew(AddressDAO dao) {
            return new AddressModel(dao);
        }

        @Deprecated
        @Override
        public AddressDAO updateDAO(AddressModel item) {
            AddressDAO dao = item.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Address has been deleted");
            }
            String address1 = item.address1.get();
            String address2 = item.address2.get();
            CityItem<? extends ICityDAO> cityModel = item.city.get();
            ICityDAO cityDAO = cityModel.dataObject();
            if (null == cityDAO || cityDAO.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated city has been deleted");
            }
            item.isNewRow();
            dao.setCity(cityDAO);
            dao.setAddress1(address1);
            dao.setAddress2(address2);
            dao.setPostalCode(item.getPostalCode());
            dao.setPhone(item.getPhone());
            return dao;
        }

        @Deprecated
        @Override
        protected void updateItemProperties(AddressModel item, AddressDAO dao) {
            item.setAddress1(dao.getAddress1());
            item.setAddress2(dao.getAddress2());
            item.setCity(CityItem.createModel(dao.getCity()));
            item.setPostalCode(dao.getPostalCode());
            item.setPhone(dao.getPhone());
        }

        @Override
        public ModelFilter<AddressDAO, AddressModel, ? extends DaoFilter<AddressDAO>> getAllItemsFilter() {
            return new ModelFilter<AddressDAO, AddressModel, DaoFilter<AddressDAO>>() {
                private final String headingText = AppResources.getResourceString(RESOURCEKEY_ALLADDRESSES);
                private final DaoFilter<AddressDAO> daoFilter = DaoFilter.all(AppResources.getResourceString(RESOURCEKEY_READINGFROMDB),
                        AppResources.getResourceString(RESOURCEKEY_LOADINGADDRESSES));

                @Override
                public String getHeadingText() {
                    return headingText;
                }

                @Override
                public DaoFilter<AddressDAO> getDaoFilter() {
                    return daoFilter;
                }

                @Override
                public boolean test(AddressModel t) {
                    return null != t;
                }

            };
        }

        @Override
        public ModelFilter<AddressDAO, AddressModel, ? extends DaoFilter<AddressDAO>> getDefaultFilter() {
            return getAllItemsFilter();
        }

        @Override
        public AddressEvent createDbOperationEvent(AddressModel model, Object source, EventTarget target, DbOperationType operation) {
            return new AddressEvent(model, source, target, operation);
        }

        @Override
        public EventType<AddressEvent> toEventType(DbOperationType operation) {
            return AddressEvent.toEventType(operation);
        }

    }

}
