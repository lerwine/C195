package scheduler.view.address;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.DataRowState;
import scheduler.model.Address;
import scheduler.model.ModelHelper;
import scheduler.model.db.AddressRowData;
import scheduler.model.db.CityRowData;
import scheduler.model.ui.AddressDbItem;
import scheduler.model.ui.CityItem;
import scheduler.observables.CityZipCountryProperty;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.observables.RowStateProperty;
import scheduler.util.Values;
import scheduler.view.city.RelatedCity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedAddress implements AddressDbItem<AddressRowData> {

    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final AddressLinesProperty addressLines;
    private final ReadOnlyObjectWrapper<CityItem> city;
    private final NestedStringBindingProperty<CityItem> cityName;
    private final NestedStringBindingProperty<CityItem> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final CityZipCountryProperty cityZipCountry;
    private final ReadOnlyObjectWrapper<AddressRowData> dataObject;
    private final RowStateProperty rowState;

    public RelatedAddress(AddressRowData rowData) {
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", rowData.getPrimaryKey());
        address1 = new ReadOnlyStringWrapper(this, "address1", rowData.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", rowData.getAddress2());
        addressLines = new AddressLinesProperty();
        CityRowData c = rowData.getCity();
        city = new ReadOnlyObjectWrapper<>(this, "city", (null == c) ? null : new RelatedCity(c));
        cityName = new NestedStringBindingProperty<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new NestedStringBindingProperty<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new ReadOnlyStringWrapper(this, "postalCode", rowData.getPostalCode());
        phone = new ReadOnlyStringWrapper(this, "phone", rowData.getPhone());
        cityZipCountry = new CityZipCountryProperty(this, "cityZipCountry", this);
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", rowData);
        rowState = new RowStateProperty(this, "rowState", ModelHelper.getRowState(rowData));
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ReadOnlyStringProperty address1Property() {
        return address1.getReadOnlyProperty();
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ReadOnlyStringProperty address2Property() {
        return address2.getReadOnlyProperty();
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
    public ReadOnlyObjectProperty<CityItem> cityProperty() {
        return city.getReadOnlyProperty();
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ReadOnlyStringProperty postalCodeProperty() {
        return postalCode.getReadOnlyProperty();
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ReadOnlyStringProperty phoneProperty() {
        return phone.getReadOnlyProperty();
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
    public AddressRowData getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<AddressRowData> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
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
            return RelatedAddress.this;
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
