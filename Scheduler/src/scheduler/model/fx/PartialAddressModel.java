package scheduler.model.fx;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCityDAO;
import scheduler.model.Address;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface PartialAddressModel<T extends PartialAddressDAO> extends Address, PartialEntityModel<T> {

    /**
     * The name of the 'addressLines' property.
     */
    public static final String PROP_ADDRESSLINES = "addressLines";

    /**
     * The name of the 'cityName' property.
     */
    public static final String PROP_CITYNAME = "cityName";

    /**
     * The name of the 'countryName' property.
     */
    public static final String PROP_COUNTRYNAME = "countryName";

    /**
     * The name of the 'cityZipCountry' property.
     */
    public static final String PROP_CITYZIPCOUNTRY = "cityZipCountry";

    /**
     * The name of the 'language' property.
     */
    public static final String PROP_LANGUAGE = "language";

    /**
     * Gets the property that contains the first line of the address.
     *
     * @return The property that contains the first line of the address.
     */
    ReadOnlyStringProperty address1Property();

    /**
     * Gets the property that contains the second line of the address.
     *
     * @return The property that contains the second line of the address.
     */
    ReadOnlyStringProperty address2Property();

    ReadOnlyStringProperty addressLinesProperty();

    @Override
    public PartialCityModel<? extends PartialCityDAO> getCity();

    /**
     * Gets the property that contains the city model for the address.
     *
     * @return The property that contains the city model for the address.
     */
    ReadOnlyObjectProperty<? extends PartialCityModel<? extends PartialCityDAO>> cityProperty();

    /**
     * Gets the property that contains the postal code for the address.
     *
     * @return The property that contains the postal code for the address.
     */
    ReadOnlyStringProperty postalCodeProperty();

    /**
     * Gets the property that contains the phone number associated with the address.
     *
     * @return The property that contains the phone number associated with the address.
     */
    ReadOnlyStringProperty phoneProperty();

    String getCityName();

    ReadOnlyStringProperty cityNameProperty();

    String getCountryName();

    ReadOnlyStringProperty countryNameProperty();

    String getCityZipCountry();

    ReadOnlyStringProperty cityZipCountryProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    @Override
    T dataObject();

}
