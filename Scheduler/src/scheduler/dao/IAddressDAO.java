package scheduler.dao;

import scheduler.model.Address;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IAddressDAO extends DbObject, Address {

    public static <T extends IAddressDAO> T assertValidAddress(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Address has already been deleted");
        }

        ICityDAO city = target.getCity();

        if (null == city) {
            throw new IllegalStateException("City not specified");
        }

        ICityDAO.assertValidCity(city);

        if (Values.isNullWhiteSpaceOrEmpty(target.getAddress1()) && Values.isNullWhiteSpaceOrEmpty(target.getAddress2())) {
            throw new IllegalStateException("Street address not defined");
        }

        String s = target.getAddress1();
        if (s.length() > MAX_LENGTH_ADDRESS1) {
            throw new IllegalStateException("First address line too long");
        }
        s = target.getAddress2();
        if (s.length() > MAX_LENGTH_ADDRESS2) {
            throw new IllegalStateException("Second address line too long");
        }
        s = target.getPostalCode();
        if (s.length() > MAX_LENGTH_POSTALCODE) {
            throw new IllegalStateException("Postal code too long");
        }
        s = target.getPhone();
        if (s.length() > MAX_LENGTH_PHONE) {
            throw new IllegalStateException("Phone number too long");
        }

        return target;
    }

    @Override
    public ICityDAO getCity();

}
