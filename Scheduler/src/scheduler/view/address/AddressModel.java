package scheduler.view.address;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.ModelHelper;
import scheduler.model.db.CityRowData;
import scheduler.model.ui.AddressDbItem;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.FxDbModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.CityZipCountryProperty;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.util.Values;
import scheduler.view.ModelFilter;
import scheduler.view.city.CityModel;
import scheduler.view.city.RelatedCity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressModel extends FxRecordModel<AddressDAO> implements AddressDbItem<AddressDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    public static ZoneId getZoneId(AddressItem address) {
        if (null != address) {
            return CityModel.getZoneId(address.getCity());
        }
        return ZoneId.systemDefault();
    }

    private final NonNullableStringProperty address1;
    private final NonNullableStringProperty address2;
    private final AddressLinesProperty addressLines;
    private final SimpleObjectProperty<CityItem> city;
    private final NestedStringBindingProperty<CityItem> cityName;
    private final NestedStringBindingProperty<CityItem> countryName;
    private final NonNullableStringProperty postalCode;
    private final NonNullableStringProperty phone;
    private final CityZipCountryProperty cityZipCountry;

    public AddressModel(AddressDAO dao) {
        super(dao);
        address1 = new NonNullableStringProperty(this, "address1", dao.getAddress1());
        address2 = new NonNullableStringProperty(this, "address2", dao.getAddress2());
        addressLines = new AddressLinesProperty();
        CityRowData c = dao.getCity();
        city = new SimpleObjectProperty<>(this, "city", (null == c) ? null : new RelatedCity(c));
        cityName = new NestedStringBindingProperty<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new NestedStringBindingProperty<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new NonNullableStringProperty(this, "postalCode", dao.getPostalCode());
        phone = new NonNullableStringProperty(this, "phone", dao.getPhone());
        cityZipCountry = new CityZipCountryProperty(this, "cityZipCountry", this);
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
    public ReadOnlyProperty<String> addressLinesProperty() {
        return addressLines;
    }

    @Override
    public CityItem getCity() {
        return city.get();
    }

    public void setCity(CityItem value) {
        city.set(value);
    }

    @Override
    public ObjectProperty<CityItem> cityProperty() {
        return city;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public NestedStringBindingProperty<CityItem> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public NestedStringBindingProperty<CityItem> countryNameProperty() {
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
    public ReadOnlyProperty<String> cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public int hashCode() {
        if (isNewItem()) {
            int hash = 3;
            hash = 19 * hash + Objects.hashCode(address1.get());
            hash = 19 * hash + Objects.hashCode(address2.get());
            hash = 19 * hash + Objects.hashCode(city.get());
            hash = 19 * hash + Objects.hashCode(postalCode.get());
            hash = 19 * hash + Objects.hashCode(phone.get());
            return hash;
        }
        return getPrimaryKey();
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

    public final static class Factory extends FxRecordModel.ModelFactory<AddressDAO, AddressModel> {

        private Factory() {
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
        public void updateItem(AddressModel item, AddressDAO dao) {
            super.updateItem(item, dao);

            item.address1.set(dao.getAddress1());
            item.address2.set(dao.getAddress2());
            CityRowData c = dao.getCity();
            item.city.set((null == c) ? null : new RelatedCity(c));
            item.postalCode.set(dao.getPostalCode());
            item.phone.set(dao.getPhone());
        }

        @Override
        public AddressDAO updateDAO(AddressModel item) {
            AddressDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Address has been deleted");
            }
            String address1 = item.address1.get();
            String address2 = item.address2.get();
            if (address1.trim().isEmpty() && address2.trim().isEmpty()) {
                throw new IllegalArgumentException("Address lines 1 and 2 are empty");
            }
            CityItem cityModel = item.city.get();
            if (null == cityModel) {
                throw new IllegalArgumentException("No associated city");
            }

            if (cityModel instanceof FxDbModel) {
                CityRowData cityDAO = ((FxDbModel<? extends CityRowData>) cityModel).getDataObject();
                if (ModelHelper.getRowState(cityDAO) == DataRowState.DELETED) {
                    throw new IllegalArgumentException("Associated city has been deleted");
                }
                dao.setCity(cityDAO);
            } else {
                dao.setCity(cityModel);
            }
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
