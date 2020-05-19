package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.IAddressDAO;
import scheduler.model.Address;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_PHONENUMBER;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AddressItem extends Address, FxModel {

    // TODO: Replace this with calculated expressions
    @Deprecated
    public static StringBinding createMultiLineAddressBinding(ReadOnlyProperty<String> address1, ReadOnlyProperty<String> address2,
            ReadOnlyProperty<String> cityZipCountry, ReadOnlyProperty<String> phone) {
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

    public static StringBinding createMultiLineAddressBinding(AddressItem source) {
        return createMultiLineAddressBinding(source.address1Property(), source.address2Property(), source.cityZipCountryProperty(),
                source.phoneProperty());
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
    public CityItem getCity();

    /**
     * Gets the property that contains the city model for the address.
     *
     * @return The property that contains the city model for the address.
     */
    ReadOnlyObjectProperty<? extends CityItem> cityProperty();

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

    ZoneId getZoneId();

    ReadOnlyObjectProperty<ZoneId> zoneIdProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    @Override
    IAddressDAO getDataObject();

    @Override
    ReadOnlyObjectProperty<? extends IAddressDAO> dataObjectProperty();

}
