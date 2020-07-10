package scheduler.model.ui;

import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.model.Address;
import scheduler.model.CityProperties;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCityDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PartialAddressModelImpl extends RelatedModel<PartialAddressDAO> implements PartialAddressModel<PartialAddressDAO> {

    private static final Logger LOG = Logger.getLogger(PartialAddressModelImpl.class.getName());

    private final ReadOnlyJavaBeanStringProperty address1;
    private final ReadOnlyJavaBeanStringProperty address2;
    private final ReadOnlyStringBindingProperty addressLines;
    private final ReadOnlyJavaBeanObjectProperty<PartialCityDAO> cityDAO;
    private final ReadOnlyObjectBindingProperty<PartialCityModel<? extends PartialCityDAO>> city;
    private final ReadOnlyStringBindingProperty cityName;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyJavaBeanStringProperty postalCode;
    private final ReadOnlyJavaBeanStringProperty phone;
    private final ReadOnlyStringBindingProperty cityZipCountry;
    private final ReadOnlyStringBindingProperty language;

    public PartialAddressModelImpl(PartialAddressDAO rowData) {
        super(rowData);

        try {
            address1 = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(PROP_ADDRESS1).build();
            address2 = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(PROP_ADDRESS2).build();
            cityDAO = ReadOnlyJavaBeanObjectPropertyBuilder.<PartialCityDAO>create().bean(rowData).name(PROP_CITY).build();
            postalCode = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(PROP_POSTALCODE).build();
            phone = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(PROP_PHONE).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        addressLines = new ReadOnlyStringBindingProperty(this, PROP_ADDRESSLINES,
                () -> AddressModel.calculateAddressLines(address1.get(), address2.get()), address1, address2);
        city = new ReadOnlyObjectBindingProperty<>(this, PROP_CITY, () -> PartialCityModel.createModel(cityDAO.get()), cityDAO);
        cityName = new ReadOnlyStringBindingProperty(this, PROP_CITYNAME, Bindings.selectString(city, CityProperties.PROP_NAME));
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(city, PartialCityModel.PROP_COUNTRYNAME));
        cityZipCountry = new ReadOnlyStringBindingProperty(this, PROP_CITYZIPCOUNTRY,
                () -> AddressModel.calculateCityZipCountry(cityName.get(), countryName.get(), postalCode.get()),
                cityName, countryName, postalCode);
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(city, PartialCityModel.PROP_LANGUAGE));
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
    public PartialCityModel<? extends PartialCityDAO> getCity() {
        return city.get();
    }

    @Override
    public ReadOnlyObjectProperty<PartialCityModel<? extends PartialCityDAO>> cityProperty() {
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
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        return ToStringPropertyBuilder.create(this)
                .addNumber(primaryKeyProperty())
                .addString(address1)
                .addString(address2)
                .addDataObject(city)
                .addString(postalCode)
                .addString(phone);
    }

}
