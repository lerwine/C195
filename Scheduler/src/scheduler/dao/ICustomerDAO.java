package scheduler.dao;

import scheduler.model.Customer;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICustomerDAO extends DbObject, Customer {

    public static <T extends ICustomerDAO> T assertValidCustomer(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Customer has already been deleted");
        }
        IAddressDAO address = target.getAddress();
        if (null == address) {
            throw new IllegalStateException("Address not specified");
        }
        IAddressDAO.assertValidAddress(address);
        if (Values.isNullWhiteSpaceOrEmpty(target.getName())) {
            throw new IllegalStateException("Customer name not defined");
        }
        return target;
    }

    @Override
    public IAddressDAO getAddress();

}
