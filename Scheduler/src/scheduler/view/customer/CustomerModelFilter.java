package scheduler.view.customer;

import java.util.function.Predicate;
import static scheduler.AppResourceKeys.RESOURCEKEY_ACTIVECUSTOMERS;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCUSTOMERS;
import static scheduler.AppResourceKeys.RESOURCEKEY_INACTIVECUSTOMERS;
import scheduler.AppResources;
import scheduler.dao.CustomerDAO;
import scheduler.dao.filter.CustomerFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.model.ui.CustomerModel;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.ModelFilter;

/**
 * Model filter for customers.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerModelFilter extends ModelFilter<CustomerDAO, CustomerModel, CustomerFilter> {

    static CustomerModelFilter of(String headingText, CustomerFilter daoFilter, Predicate<CustomerModel> predicate) {
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
            public boolean test(CustomerModel t) {
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
        return CustomerModelFilter.of(AppResources.getResourceString(RESOURCEKEY_ALLCUSTOMERS),
                CustomerFilter.of(DaoFilterExpression.empty()),
                (t) -> true
        );
    }

    public static CustomerModelFilter active() {
        return CustomerModelFilter.of(ResourceBundleHelper.getResourceString(ManageCustomers.class, RESOURCEKEY_ACTIVECUSTOMERS),
                CustomerFilter.of(CustomerFilter.expressionOf(true)),
                (t) -> null != t && t.isActive()
        );
    }

    public static CustomerModelFilter inactive() {
        return CustomerModelFilter.of(ResourceBundleHelper.getResourceString(ManageCustomers.class, RESOURCEKEY_INACTIVECUSTOMERS),
                CustomerFilter.of(CustomerFilter.expressionOf(false)),
                (t) -> null != t && !t.isActive()
        );
    }

}
