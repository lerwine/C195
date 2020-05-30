package scheduler.model.ui;

import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.dao.AddressDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.Values;
import scheduler.model.Address;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedAddress extends RelatedModel<IAddressDAO> implements AddressItem<IAddressDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedAddress.class.getName());

    private final ReadOnlyJavaBeanStringProperty address1;
    private final ReadOnlyJavaBeanStringProperty address2;
    private final ReadOnlyStringBindingProperty addressLines;
    private final ReadOnlyJavaBeanObjectProperty<ICityDAO> cityDAO;
    private final ReadOnlyObjectBindingProperty<CityItem<? extends ICityDAO>> city;
    private final ReadOnlyStringBindingProperty cityName;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyJavaBeanStringProperty postalCode;
    private final ReadOnlyJavaBeanStringProperty phone;
    private final ReadOnlyStringBindingProperty cityZipCountry;
    private final ReadOnlyStringBindingProperty language;
    private final ReadOnlyObjectBindingProperty<ZoneId> zoneId;
    private final ReadOnlyBooleanBindingProperty valid;

    public RelatedAddress(IAddressDAO rowData) {
        super(rowData);

        try {
            address1 = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(AddressDAO.PROP_ADDRESS1).build();
            address2 = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(AddressDAO.PROP_ADDRESS2).build();
            cityDAO = ReadOnlyJavaBeanObjectPropertyBuilder.<ICityDAO>create().bean(rowData).name(AddressDAO.PROP_CITY).build();
            postalCode = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(AddressDAO.PROP_POSTALCODE).build();
            phone = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(AddressDAO.PROP_PHONE).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        addressLines = new ReadOnlyStringBindingProperty(this, "addressLines",
                () -> AddressModel.calculateAddressLines(address1.get(), address2.get()), address1, address2);
        city = new ReadOnlyObjectBindingProperty<>(this, "city", () -> CityItem.createModel(cityDAO.get()), cityDAO);
        cityName = new ReadOnlyStringBindingProperty(this, "cityName", Bindings.selectString(city, "name"));
        countryName = new ReadOnlyStringBindingProperty(this, "cityName", Bindings.selectString(city, "countryName"));
        cityZipCountry = new ReadOnlyStringBindingProperty(this, "cityZipCountry",
                () -> AddressModel.calculateCityZipCountry(cityName.get(), countryName.get(), postalCode.get()),
                cityName, countryName, postalCode);
        language = new ReadOnlyStringBindingProperty(this, "language", Bindings.selectString(city, "language"));
        zoneId = new ReadOnlyObjectBindingProperty<>(this, "zoneId", Bindings.select(city, "zoneId"));
        valid = new ReadOnlyBooleanBindingProperty(this, "valid",
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(address1.get()), address1)
                .or(Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(address2.get()), address2))
                .and(Bindings.selectBoolean(city, "valid")).and(Bindings.select(city, "rowState").isNotEqualTo(DataRowState.DELETED)));
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ReadOnlyStringProperty address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ReadOnlyStringProperty address2Property() {
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

    @Override
    public ReadOnlyObjectProperty<CityItem<? extends ICityDAO>> cityProperty() {
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

    @Override
    public ReadOnlyStringProperty postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ReadOnlyStringProperty phoneProperty() {
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
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId;
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
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
    }

}
