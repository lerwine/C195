package scheduler.model.ui;

import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.ICityDAO;
import scheduler.dao.filter.DaoFilter;
import static scheduler.model.AddressProperties.MAX_LENGTH_ADDRESS1;
import static scheduler.model.AddressProperties.MAX_LENGTH_ADDRESS2;
import static scheduler.model.AddressProperties.MAX_LENGTH_PHONE;
import static scheduler.model.AddressProperties.MAX_LENGTH_POSTALCODE;
import scheduler.model.City;
import scheduler.model.CityProperties;
import scheduler.model.Country;
import scheduler.model.DataObject;
import scheduler.model.ModelHelper;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.AnyTrueSet;
import scheduler.util.LogHelper;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import static scheduler.util.Values.asNonNullAndWsNormalized;
import scheduler.view.ModelFilter;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

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

    private final AnyTrueSet changeIndicator;
    private final AnyTrueSet validityIndicator;
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper changed;
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
    private final AnyTrueSet.Node address1Changed;
    private final AnyTrueSet.Node address2Changed;
    private final AnyTrueSet.Node cityChanged;
    private final AnyTrueSet.Node postalCodeChanged;
    private final AnyTrueSet.Node phoneChanged;
    private final AnyTrueSet.Node addressValid;
    private final AnyTrueSet.Node cityValid;
    private final AnyTrueSet.Node postalCodeValid;
    private final AnyTrueSet.Node phoneValid;

    /**
     * FX model for {@link scheduler.model.Address} objects.
     *
     * @param dao The backing {@link AddressDAO} object.
     */
    public AddressModel(AddressDAO dao) {
        super(dao);
        changeIndicator = new AnyTrueSet();
        validityIndicator = new AnyTrueSet();
        address1 = new NonNullableStringProperty(this, PROP_ADDRESS1, dao.getAddress1());
        address1Changed = changeIndicator.add(false);
        address2 = new NonNullableStringProperty(this, PROP_ADDRESS2, dao.getAddress2());
        address2Changed = changeIndicator.add(false);
        addressLines = new ReadOnlyStringBindingProperty(this, PROP_ADDRESSLINES,
                () -> AddressModel.calculateAddressLines(address1.get(), address2.get()), address1, address2);
        addressValid = validityIndicator.add(Values.isNotNullWhiteSpaceOrEmpty(addressLines.get()));
        address1.addListener((observable, oldValue, newValue) -> {
            addressValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(addressLines.get()));
            String n = dao.getAddress1();
            address1Changed.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        address2.addListener((observable, oldValue, newValue) -> {
            addressValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(addressLines.get()));
            String n = dao.getAddress2();
            address2Changed.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        city = new SimpleObjectProperty<>(this, PROP_CITY, CityItem.createModel(dao.getCity()));
        cityChanged = changeIndicator.add(false);
        cityValid = validityIndicator.add(null != city.get());
        city.addListener((observable, oldValue, newValue) -> {
            cityValid.setValid(null != newValue);
            cityChanged.setValid(!ModelHelper.areSameRecord(newValue, dao.getCity()));
        });
        cityName = new ReadOnlyStringBindingProperty(this, PROP_CITYNAME, Bindings.selectString(city, CityProperties.PROP_NAME));
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(city, CityItem.PROP_COUNTRYNAME));
        postalCode = new NonNullableStringProperty(this, PROP_POSTALCODE, dao.getPostalCode());
        postalCodeChanged = changeIndicator.add(false);
        postalCodeValid = validityIndicator.add(Values.isNotNullWhiteSpaceOrEmpty(postalCode.get()));
        postalCode.addListener((observable, oldValue, newValue) -> {
            postalCodeValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(newValue));
            String n = dao.getPostalCode();
            postalCodeChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        phone = new NonNullableStringProperty(this, PROP_PHONE, dao.getPhone());
        phoneChanged = changeIndicator.add(false);
        phoneValid = validityIndicator.add(Values.isNotNullWhiteSpaceOrEmpty(phone.get()));
        phone.addListener((observable, oldValue, newValue) -> {
            phoneValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(newValue));
            String n = dao.getPhone();
            phoneChanged.setValid((null == newValue || newValue.isEmpty()) ? n.isEmpty() : newValue.equals(n));
        });
        cityZipCountry = new ReadOnlyStringBindingProperty(this, PROP_CITYZIPCOUNTRY,
                () -> AddressModel.calculateCityZipCountry(cityName.get(), countryName.get(), postalCode.get()),
                cityName, countryName, postalCode);
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(city, CityItem.PROP_LANGUAGE));
        timeZone = new ReadOnlyObjectBindingProperty<>(this, PROP_TIMEZONE, Bindings.select(city, CityItem.PROP_TIMEZONE));
        
        dao.addPropertyChangeListener((evt) -> {
            switch (evt.getPropertyName()) {
                case PROP_ADDRESS1:
                    // FIXME: update validity and change
                    break;
                case PROP_ADDRESS2:
                    // FIXME: update validity and change
                    break;
                case PROP_CITY:
                    // FIXME: update validity and change
                    break;
                case PROP_POSTALCODE:
                    // FIXME: update validity and change
                    break;
                case PROP_PHONE:
                    // FIXME: update validity and change
                    break;
            }
        });
        
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
    public boolean isChanged() {
        return changed.get();
    }

    @Override
    public ReadOnlyBooleanProperty changedProperty() {
        return changed;
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

    public final static class Factory extends FxRecordModel.FxModelFactory<AddressDAO, AddressModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Factory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(Factory.class.getName());

        // Singleton
        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<AddressDAO> getDaoFactory() {
            return AddressDAO.FACTORY;
        }

        @Override
        public AddressModel createNew(AddressDAO dao) {
            return new AddressModel(dao);
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
        public String validateProperties(AddressModel fxRecordModel) {
            AddressDAO dao = fxRecordModel.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                return "Address has already been deleted";
            }
            String a = dao.getAddress1();
            String s = dao.getAddress2();
            if (Values.isNullWhiteSpaceOrEmpty(a) && Values.isNullWhiteSpaceOrEmpty(s)) {
                return "Street address not defined";
            }
            if (a.length() > MAX_LENGTH_ADDRESS1) {
                return "First address line too long";
            }
            if (s.length() > MAX_LENGTH_ADDRESS2) {
                return "Second address line too long";
            }
            if (dao.getPostalCode().length() > MAX_LENGTH_POSTALCODE) {
                return "Postal code too long";
            }
            if (dao.getPhone().length() > MAX_LENGTH_PHONE) {
                return "Phone number too long";
            }
            CityItem<? extends ICityDAO> c = fxRecordModel.getCity();
            if (null == c) {
                return "City not specified.";
            }
            if (c instanceof CityModel) {
                String message = CityModel.FACTORY.validateProperties((CityModel) c);
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
            }

            return null;
        }

        @Override
        public DataAccessObject.SaveDaoTask<AddressDAO, AddressModel> createSaveTask(AddressModel model, boolean force) {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.model.ui.AddressModel.Factory#createSaveTask
        }

        @Override
        public DataAccessObject.DeleteDaoTask<AddressDAO, AddressModel> createDeleteTask(AddressModel model) {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.model.ui.AddressModel.Factory#createDeleteTask
        }

    }

}
