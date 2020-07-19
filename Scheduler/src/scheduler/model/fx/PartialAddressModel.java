package scheduler.model.fx;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ObservableValue;
import scheduler.dao.AddressDAO;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCityDAO;
import scheduler.model.Address;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_PHONENUMBER;

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

    @SuppressWarnings("unchecked")
    public static PartialAddressModel<? extends PartialAddressDAO> createModel(PartialAddressDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof AddressDAO) {
            return ((AddressDAO) t).cachedModel(true);
        }

        return new PartialAddressModelImpl((AddressDAO.Partial) t);
    }

    public static StringBinding createMultiLineAddressBinding(ObservableValue<String> address1, ObservableValue<String> address2,
            ObservableValue<String> cityZipCountry, ObservableValue<String> phone) {
        return Bindings.createStringBinding(() -> {
            String a1 = address1.getValue().trim();
            String a2 = address2.getValue().trim();
            String c = cityZipCountry.getValue().trim();
            String p = phone.getValue().trim();
            if (a1.isEmpty()) {
                if (a2.isEmpty()) {
                    if (c.isEmpty()) {
                        return (p.isEmpty()) ? "" : String.format("%s %s", getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                    }
                    return (p.isEmpty()) ? c : String.format("%s%n%s %s", c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                if (c.isEmpty()) {
                    return (p.isEmpty()) ? a2 : String.format("%s%n%s %s", a2, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                return (p.isEmpty()) ? String.format("%s%n%s", a2, c)
                        : String.format("%s%n%s%n%s %s", a2, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
            }
            if (a2.isEmpty()) {
                if (c.isEmpty()) {
                    return (p.isEmpty()) ? a1 : String.format("%s%n%s %s", a1, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                return (p.isEmpty()) ? String.format("%s%n%s", a1, c)
                        : String.format("%s%n%s%n%s %s", a1, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
            }
            if (c.isEmpty()) {
                return (p.isEmpty()) ? String.format("%s%n%s", a1, a2)
                        : String.format("%s%n%s%n%s %s", a1, a2, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
            }
            return (p.isEmpty()) ? String.format("%s%n%s%n%s", a1, a2, c)
                    : String.format("%s%n%s%n%s%n%s %s", a1, a2, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
        }, address1, address2, cityZipCountry, phone);
    }

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
