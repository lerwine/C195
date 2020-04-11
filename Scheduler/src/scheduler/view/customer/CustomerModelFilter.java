package scheduler.view.customer;

import java.util.function.Predicate;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_ALLCUSTOMERS;
import scheduler.dao.CustomerDAO;
import scheduler.dao.filter.CustomerFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.ModelFilter;

/**
 * Model filter for customers.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerModelFilter extends ModelFilter<CustomerDAO, CustomerModelImpl, CustomerFilter> {

    static CustomerModelFilter of(String headingText, CustomerFilter daoFilter, Predicate<CustomerModel<CustomerDAO>> predicate) {
        return new CustomerModelFilter() {
            @Override
            public String getHeadingText() {
                return headingText;
            }

            @Override
            public CustomerFilter getDaoFilter() {
                return daoFilter;
            }

            @Override
            public boolean test(CustomerModelImpl t) {
                return predicate.test(t);
            }

        };
    }

    /**
     * Model filter for all customers.
     *
     * @return A model filter that matches any customer.
     */
    public static CustomerModelFilter all() {
        return CustomerModelFilter.of(
                ResourceBundleLoader.getResourceString(ManageCustomers.class, RESOURCEKEY_ALLCUSTOMERS),
                CustomerFilter.of(DaoFilterExpression.empty()),
                (t) -> true
        );
    }

}
