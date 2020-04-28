package scheduler.model;

import scheduler.model.ui.AddressItem;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_PHONENUMBER;

/**
 * Interface for objects that contain either partial or complete information from the {@code address} database entity.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Address extends DataModel {

    public static boolean arePropertiesEqual(Address a, Address b) {
        if (null == a) {
            return null == b;
        }
        if (a == b) {
            return true;
        }
        return null != b && a.getAddress1().equalsIgnoreCase(b.getAddress1()) && a.getAddress2().equalsIgnoreCase(b.getAddress2())
                && ModelHelper.areSameRecord(a.getCity(), b.getCity()) && a.getPostalCode().equalsIgnoreCase(b.getPostalCode()) &&
                a.getPhone().equalsIgnoreCase(b.getPhone());
    }

    /**
     * Gets the first line of the current address. Column definition: <code>`address` varchar(50) NOT NULL</code>
     *
     * @return the first line of the current address.
     */
    String getAddress1();

    /**
     * Gets the second line of the current address. Column definition: <code>`address2` varchar(50) NOT NULL</code>
     *
     * @return the second line of the current address.
     */
    String getAddress2();

    /**
     * Gets the {@link City} for the current address. This corresponds to the "city" data row referenced by the "cityId" database column.
     * Column definition: <code>`cityId` int(10) NOT NULL</code> Key constraint definition:
     * <code>CONSTRAINT `address_ibfk_1` FOREIGN KEY (`cityId`) REFERENCES `city` (`cityId`)</code>
     *
     * @return The {@link City} for the current address.
     */
    City getCity();

    /**
     * Gets the postal code for the current address. Column definition: <code>`postalCode` varchar(10) NOT NULL</code>
     *
     * @return the postal code for the current address.
     */
    String getPostalCode();

    /**
     * Gets the phone number associated with the current address. Column definition: <code>`phone` varchar(20) NOT NULL</code>
     *
     * @return the phone number associated with the current address.
     */
    String getPhone();

}
