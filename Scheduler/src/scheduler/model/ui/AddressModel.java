package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.ICityDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.ModelHelper;
import scheduler.model.predefined.PredefinedCity;
import scheduler.observables.CalculatedBooleanProperty;
import scheduler.observables.CalculatedStringExpression;
import scheduler.observables.CalculatedStringProperty;
import scheduler.observables.NestedObjectValueExpression;
import scheduler.observables.NestedObjectValueProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.ObservableTriplet;
import scheduler.observables.ObservableTuple;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.util.Values;
import scheduler.view.ModelFilter;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressModel extends FxRecordModel<AddressDAO> implements AddressItem<AddressDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    public static String calculateAddressLines(String line1, String line2) {
        if (line1.isEmpty()) {
            return line2;
        }
        return (line2.isEmpty()) ? line1 : String.format("%s%n%s", line1, line2);
    }

    public static String calculateCityZipCountry(String city, String country, String postalCode) {
        if (city.isEmpty()) {
            return (postalCode.isEmpty()) ? country : ((country.isEmpty()) ? postalCode : String.format("%s, %s", postalCode, country));
        }
        if (country.isEmpty()) {
            return (postalCode.isEmpty()) ? city : String.format("%s %s", city, postalCode);
        }
        return (postalCode.isEmpty()) ? String.format("%s, %s", city, country) : String.format("%s %s, %s", city, postalCode, country);
    }

    public static String calculateMultiLineAddress(String address, String cityZipCountry, String phone) {
        if (address.isEmpty()) {
            if (cityZipCountry.isEmpty()) {
                return (phone.isEmpty()) ? "" : String.format("%s %s", getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
            }
            return (phone.isEmpty()) ? cityZipCountry : String.format("%s%n%s %s", cityZipCountry,
                    getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
        }
        if (cityZipCountry.isEmpty()) {
            return (phone.isEmpty()) ? address
                    : String.format("%s%n%s %s", address, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
        }
        return (phone.isEmpty()) ? String.format("%s%n%s", address, cityZipCountry)
                : String.format("%s%n%s%n%s %s", address, cityZipCountry, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
    }

    public static String calculateSingleLineAddress(String address1, String address2, String cityZipCountry, String phone) {
        if (address1.isEmpty()) {
            if (address2.isEmpty()) {
                return (cityZipCountry.isEmpty()) ? phone : ((phone.isEmpty()) ? cityZipCountry : String.format("%s, %s", cityZipCountry, phone));
            }
            if (cityZipCountry.isEmpty()) {
                return (phone.isEmpty()) ? address2 : String.format("%s, %s", address2, phone);
            }
            return (phone.isEmpty()) ? String.format("%s, %s", address2, cityZipCountry) : String.format("%s, %s, %s", address2, cityZipCountry, phone);
        }
        if (address2.isEmpty()) {
            if (cityZipCountry.isEmpty()) {
                return (phone.isEmpty()) ? address1 : String.format("%s, %s", address1, phone);
            }
            return (phone.isEmpty()) ? String.format("%s, %s", address1, cityZipCountry) : String.format("%s, %s, %s", address1, cityZipCountry, phone);
        }
        if (cityZipCountry.isEmpty()) {
            return (phone.isEmpty()) ? String.format("%s, %s", address1, address2) : String.format("%s, %s, %s", address1, address2, phone);
        }
        return (phone.isEmpty()) ? String.format("%s, %s, %s", address1, address2, cityZipCountry) : String.format("%s, %s, %s, %s", address1, address2, cityZipCountry, phone);
    }

    public static CalculatedStringExpression<Triplet<String, String, String>> cityZipCountryExpression(ObservableValue<String> city, ObservableValue<String> country,
            ObservableValue<String> postalCodeNormalized) {
        return new CalculatedStringExpression<>(
                new ObservableTriplet<>(
                        new CalculatedStringExpression<>(city, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(country, Values::asNonNullAndWsNormalized),
                        postalCodeNormalized
                ), (t) -> calculateCityZipCountry(t.getValue1(), t.getValue2(), t.getValue3())
        );
    }

    private final NonNullableStringProperty address1;
    private final NonNullableStringProperty address2;
    private final CalculatedStringProperty<Tuple<String, String>> addressLines;
    private final SimpleObjectProperty<CityItem<? extends ICityDAO>> city;
    private final NestedStringProperty<CityItem<? extends ICityDAO>> cityName;
    private final NestedStringProperty<CityItem<? extends ICityDAO>> countryName;
    private final NonNullableStringProperty postalCode;
    private final NonNullableStringProperty phone;
    private final CalculatedStringProperty<Triplet<String, String, String>> cityZipCountry;
    private final NestedStringProperty<CityItem<? extends ICityDAO>> language;
    private final NestedObjectValueProperty<CityItem<? extends ICityDAO>, ZoneId> zoneId;
    private final CalculatedBooleanProperty<Triplet<String, PredefinedCity, String>> valid;

    public AddressModel(AddressDAO dao) {
        super(dao);
        address1 = new NonNullableStringProperty(this, "address1", dao.getAddress1());
        address2 = new NonNullableStringProperty(this, "address2", dao.getAddress2());
        addressLines = new CalculatedStringProperty<>(this, "addressLines",
                new ObservableTuple<>(
                        new CalculatedStringExpression<>(address1, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(address2, Values::asNonNullAndWsNormalized)
                ), (t) -> calculateAddressLines(t.getValue1(), t.getValue2())
        );
        ICityDAO c = dao.getCity();
        city = new SimpleObjectProperty<>(this, "city", (null == c) ? null : new RelatedCity(c));
        cityName = new NestedStringProperty<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new NestedStringProperty<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new NonNullableStringProperty(this, "postalCode", dao.getPostalCode());
        phone = new NonNullableStringProperty(this, "phone", dao.getPhone());
        CalculatedStringExpression<String> zipNormalized = new CalculatedStringExpression<>(postalCode, Values::asNonNullAndWsNormalized);
        cityZipCountry = new CalculatedStringProperty<>(this, "cityZipCountry",
                new ObservableTriplet<>(
                        new CalculatedStringExpression<>(cityName, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(countryName, Values::asNonNullAndWsNormalized),
                        zipNormalized
                ), (t) -> calculateCityZipCountry(t.getValue1(), t.getValue2(), t.getValue3())
        );
        language = new NestedStringProperty<>(this, "language", city, (t) -> t.languageProperty());
        zoneId = new NestedObjectValueProperty<>(this, "zoneId", city, (t) -> t.zoneIdProperty());
        valid = new CalculatedBooleanProperty<>(this, "valid",
                new ObservableTriplet<>(
                        addressLines,
                        new NestedObjectValueExpression<>(city, (CityItem<? extends ICityDAO> u) -> u.predefinedDataProperty()),
                        zipNormalized
                ), (t) -> !(t.getValue1().isEmpty() || null == t.getValue2() || t.getValue1().isEmpty())
        );
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
        return addressLines.getReadOnlyStringProperty();
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
        return cityName.getReadOnlyStringProperty();
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName.getReadOnlyStringProperty();
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
        return cityZipCountry.getReadOnlyStringProperty();
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyObjectProperty();
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyStringProperty();
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + addressLines.get().hashCode();
        hash = 79 * hash + phone.get().hashCode();
        hash = 79 * hash + cityZipCountry.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof AddressModel) {
            final AddressModel other = (AddressModel) obj;
            if (isNewItem()) {
                return address1.isEqualTo(other.address1).get() && address2.isEqualTo(other.address2).get() && city.isEqualTo(other.city).get()
                        && postalCode.isEqualTo(other.postalCode).get() && phone.isEqualTo(other.phone).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    protected void onDaoPropertyChanged(AddressDAO dao, String propertyName) {
        switch (propertyName) {
            case AddressDAO.PROP_ADDRESS1:
                address1.set(dao.getAddress1());
                break;
            case AddressDAO.PROP_ADDRESS2:
                address2.set(dao.getAddress2());
                break;
            case AddressDAO.PROP_CITY:
                ICityDAO c = dao.getCity();
                city.set((null == c) ? null : new RelatedCity(c));
                break;
            case AddressDAO.PROP_PHONE:
                phone.set(dao.getPhone());
                break;
            case AddressDAO.PROP_POSTALCODE:
                postalCode.set(dao.getPostalCode());
                break;
        }
    }

    @Override
    protected void onDataObjectChanged(AddressDAO dao) {
        address1.set(dao.getAddress1());
        address2.set(dao.getAddress2());
        ICityDAO c = dao.getCity();
        city.set((null == c) ? null : new RelatedCity(c));
        postalCode.set(dao.getPostalCode());
        phone.set(dao.getPhone());
    }

    public final static class Factory extends FxRecordModel.ModelFactory<AddressDAO, AddressModel> {

        // Singleton
        private Factory() {
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DaoFactory<AddressDAO> getDaoFactory() {
            return AddressDAO.getFactory();
        }

        @Override
        public AddressModel createNew(AddressDAO dao) {
            return new AddressModel(dao);
        }

        @Override
        public AddressDAO updateDAO(AddressModel item) {
            AddressDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Address has been deleted");
            }
            String address1 = item.address1.get();
            String address2 = item.address2.get();
            CityItem<? extends ICityDAO> cityModel = item.city.get();
            ICityDAO cityDAO = cityModel.getDataObject();
            if (ModelHelper.getRowState(cityDAO) == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated city has been deleted");
            }
            item.isNewItem();
            dao.setCity(cityDAO);
            dao.setAddress1(address1);
            dao.setAddress2(address2);
            dao.setPostalCode(item.getPostalCode());
            dao.setPhone(item.getPhone());
            return dao;
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

    }

    @Deprecated
    class AddressLinesProperty extends StringBinding implements ReadOnlyProperty<String> {

        AddressLinesProperty() {
            super.bind(address1, address2);
        }

        @Override
        protected String computeValue() {
            String a1 = Values.asNonNullAndWsNormalized(address1.get());
            String a2 = Values.asNonNullAndWsNormalized(address2.get());
            if (a2.isEmpty()) {
                return a1;
            }
            return a1.isEmpty() ? a2 : String.format("%s%n%s", a1, a2);
        }

        @Override
        public Object getBean() {
            return AddressModel.this;
        }

        @Override
        public String getName() {
            return "addressLines";
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.observableArrayList(address1, address2);
        }

        @Override
        public void dispose() {
            super.unbind(address1, address2);
            super.dispose();
        }

    }

}
