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
        if (null != target.getPredefinedElement()) {
            ICityDAO city = target.getCity();
            if (null == city) {
                throw new IllegalStateException("City not specified");
            }
            ICityDAO.assertValidCity(city);
            if (Values.isNullWhiteSpaceOrEmpty(target.getAddress1()) && Values.isNullWhiteSpaceOrEmpty(target.getAddress2())) {
                throw new IllegalStateException("Street address not defined");
            }
        }
        return target;
    }

    @Override
    public ICityDAO getCity();

}
