package scheduler.dao;

import scheduler.model.Customer;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICustomerDAO extends DbObject, Customer {

    // FIXME: Throw ValidationFailureException, instead
    public static <T extends ICustomerDAO> T assertValidCustomer(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Customer has already been deleted");
        }

        IAddressDAO address = target.getAddress();

        if (null == address) {
            throw new IllegalStateException("Address not specified");
        }

        IAddressDAO.assertValidAddress(address);

        String name = target.getName();

        if (name.isEmpty()) {
            throw new IllegalStateException("Customer name not defined");
        }
        if (name.length() > MAX_LENGTH_NAME) {
            throw new IllegalStateException("Name too long");
        }

        return target;
    }

    @Override
    public IAddressDAO getAddress();

}
