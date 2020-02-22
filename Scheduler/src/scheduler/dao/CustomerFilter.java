package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import scheduler.App;
import static scheduler.dao.CustomerImpl.COLNAME_ACTIVE;
import scheduler.view.ItemModel;
import scheduler.view.customer.CustomerModel;

/**
 *
 * @author lerwi
 */
public interface CustomerFilter extends ModelFilter<CustomerImpl, CustomerModel> {

    @Override
    public default DataObjectImpl.Factory<CustomerImpl, ? extends ItemModel<CustomerImpl>> getFactory() {
        return CustomerImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return App.getResourceString(App.RESOURCEKEY_LOADINGCUSTOMERS);
    }

    @Override
    public default String getSubHeading() {
        return "";
    }

    public static CustomerFilter all() {
        return new CustomerFilter() {
            @Override
            public String getHeading() {
                return App.getResourceString(App.ALL_CUSTOMERS);
            }

            @Override
            public String getSqlFilterExpr() {
                return "";
            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                return index;
            }

            @Override
            public boolean test(CustomerModel t) {
                return true;
            }

        };
    }

    public static CustomerFilter active(boolean isActive) {
        return new CustomerFilter() {
            private final String heading = App.getResourceString((isActive) ? App.ACTIVE_CUSTOMERS : App.INACTIVE_CUSTOMERS);

            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSqlFilterExpr() {
                return String.format("`%s` = ?", COLNAME_ACTIVE);
            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                ps.setBoolean(index++, isActive);
                return index;
            }

            @Override
            public boolean test(CustomerModel t) {
                return t.isActive() == isActive;
            }

        };
    }

}
