package scheduler.view.customer;

import java.util.function.Predicate;
import static scheduler.AppResourceKeys.RESOURCEKEY_ACTIVECUSTOMERS;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCUSTOMERS;
import static scheduler.AppResourceKeys.RESOURCEKEY_INACTIVECUSTOMERS;
import scheduler.dao.CustomerDAO;
import scheduler.dao.filter.CustomerFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.util.ResourceBundleHelper;
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
        return CustomerModelFilter.of(ResourceBundleHelper.getResourceString(ManageCustomers.class, RESOURCEKEY_ALLCUSTOMERS),
                CustomerFilter.of(DaoFilterExpression.empty()),
                (t) -> true
        );
    }

    public static CustomerModelFilter active() {
        return CustomerModelFilter.of(ResourceBundleHelper.getResourceString(ManageCustomers.class, RESOURCEKEY_ACTIVECUSTOMERS),
                CustomerFilter.of(DaoFilterExpression.empty()),
                (t) -> null != t && t.isActive()
        );
    }

    public static CustomerModelFilter inactive() {
        return CustomerModelFilter.of(ResourceBundleHelper.getResourceString(ManageCustomers.class, RESOURCEKEY_INACTIVECUSTOMERS),
                CustomerFilter.of(DaoFilterExpression.empty()),
                (t) -> null != t && !t.isActive()
        );
    }

}
