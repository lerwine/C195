package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGCUSTOMERS;
import scheduler.AppResources;
import scheduler.dao.CustomerDAO;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface CustomerFilter extends DaoFilter<CustomerDAO> {
    
    DaoFilterExpression<CustomerDAO> getExpression();

    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS);
    }
    
    public static CustomerFilter of(DaoFilterExpression<CustomerDAO> expr) {
        return new CustomerFilter() {
            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                expr.appendSimpleDmlConditional(sb);
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                expr.appendJoinedDmlConditional(sb);
            }
            
            @Override
            public DaoFilterExpression<CustomerDAO> getExpression() {
                return expr;
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return expr.applyWhereParameters(ps, index);
            }

            @Override
            public boolean test(CustomerDAO t) {
                return expr.test(t);
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof CustomerFilter) {
                    CustomerFilter other = (CustomerFilter)obj;
                    return other.getLoadingMessage().equals(getLoadingMessage()) && other.getLoadingTitle().equals(getLoadingTitle()) &&
                            other.getExpression().equals(expr);
                }
                return false;
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 29 * hash + Objects.hashCode(getLoadingMessage());
                hash = 29 * hash + Objects.hashCode(getLoadingTitle());
                hash = 29 * hash + Objects.hashCode(expr);
                return hash;
            }

        };
    }
    
    public static BooleanColumnValueFilter<CustomerDAO> byActiveStatus(boolean isActive) {
        return BooleanColumnValueFilter.of(DbColumn.ACTIVE, isActive, (t) -> t.isActive());
    }
    
    public static IntColumnValueFilter<CustomerDAO> byAddress(int addressId) {
        return IntColumnValueFilter.of(DbColumn.CUSTOMER_NAME, ComparisonOperator.EQUALS, addressId, (t) -> t.getAddress().getPrimaryKey());
    }
    
    public static StringColumnValueFilter<CustomerDAO> byName(String name) {
        return StringColumnValueFilter.of(DbColumn.CUSTOMER_NAME, ComparisonOperator.EQUALS_CASE_INSENSITIVE, name, (t) -> t.getName());
    }
}
