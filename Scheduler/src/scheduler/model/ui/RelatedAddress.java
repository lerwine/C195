package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.AddressDAO;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.model.Address;
import scheduler.model.ModelHelper;
import scheduler.model.RelatedModel;
import scheduler.observables.CalculatedStringExpression;
import static scheduler.observables.CalculatedStringExpression.calculateAddressLines;
import static scheduler.observables.CalculatedStringExpression.calculateCityZipCountry;
import scheduler.observables.CalculatedStringProperty;
import scheduler.observables.NestedObjectValueProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.ObservableTriplet;
import scheduler.observables.ObservableTuple;
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.util.Values;
import scheduler.view.city.RelatedCity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedAddress extends RelatedModel<IAddressDAO> implements AddressDbItem<IAddressDAO> {

    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final CalculatedStringProperty<Tuple<String, String>> addressLines;
    private final ReadOnlyObjectWrapper<CityItem> city;
    private final NestedStringProperty<CityItem> cityName;
    private final NestedStringProperty<CityItem> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final CalculatedStringProperty<Triplet<String, String, String>> cityZipCountry;
    private final NestedStringProperty<CityItem> language;
    private final NestedObjectValueProperty<CityItem, ZoneId> zoneId;

    public RelatedAddress(IAddressDAO rowData) {
        super(rowData);
        address1 = new ReadOnlyStringWrapper(this, "address1", rowData.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", rowData.getAddress2());
        addressLines = new CalculatedStringProperty<>(this, "addressLines",
                new ObservableTuple<>(
                        new CalculatedStringExpression<>(address1, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(address2, Values::asNonNullAndWsNormalized)
                ), (t) -> calculateAddressLines(t.getValue1(), t.getValue2())
        );
        ICityDAO c = rowData.getCity();
        city = new ReadOnlyObjectWrapper<>(this, "city", (null == c) ? null : new RelatedCity(c));
        cityName = new NestedStringProperty<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new NestedStringProperty<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new ReadOnlyStringWrapper(this, "postalCode", rowData.getPostalCode());
        phone = new ReadOnlyStringWrapper(this, "phone", rowData.getPhone());
        cityZipCountry = new CalculatedStringProperty<>(this, "cityZipCountry",
                new ObservableTriplet<>(
                        new CalculatedStringExpression<>(cityName, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(countryName, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(postalCode, Values::asNonNullAndWsNormalized)
                ), (t) -> calculateCityZipCountry(t.getValue1(), t.getValue2(), t.getValue3())
        );
        language = new NestedStringProperty<>(this, "language", city, (t) -> t.languageProperty());
        zoneId = new NestedObjectValueProperty<>(this, "zoneId", city, (t) -> t.zoneIdProperty());
    }

    @Override
    protected void onDataObjectPropertyChanged(IAddressDAO dao, String propertyName) {
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
    public ReadOnlyStringProperty addressLinesProperty() {
        return addressLines.getReadOnlyStringProperty();
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
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
    }

}
