package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS;
import scheduler.AppResources;
import scheduler.dao.CustomerDAO;
import scheduler.dao.schema.DbColumn;
import scheduler.model.ModelHelper;
import scheduler.model.Address;
import scheduler.model.City;
import scheduler.model.Country;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerFilter extends DaoFilter<CustomerDAO> {

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
                    CustomerFilter other = (CustomerFilter) obj;
                    return other.getLoadingMessage().equals(getLoadingMessage()) && other.getLoadingTitle().equals(getLoadingTitle())
                            && other.getExpression().equals(expr);
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

            @Override
            public boolean isEmpty() {
                return expr.isEmpty();
            }

        };
    }

    public static BooleanColumnValueFilter<CustomerDAO> expressionOf(boolean isActive) {
        return BooleanColumnValueFilter.of(DbColumn.ACTIVE, isActive, (t) -> t.isActive());
    }

    public static DaoFilterExpression<CustomerDAO> expressionOf(Address address) {
        if (null != address && ModelHelper.existsInDatabase(address)) {
            return IntColumnValueFilter.of(DbColumn.CUSTOMER_ADDRESS, ComparisonOperator.EQUALS, address.getPrimaryKey(),
                    (t) -> {
                        Address element = t.getAddress();
                        return (null == element) ? Integer.MIN_VALUE : element.getPrimaryKey();
                    });
        }
        return DaoFilterExpression.empty();
    }

    public static DaoFilterExpression<CustomerDAO> expressionOf(City city) {
        if (null != city && ModelHelper.existsInDatabase(city)) {
            return IntColumnValueFilter.of(DbColumn.ADDRESS_CITY, ComparisonOperator.EQUALS, city.getPrimaryKey(),
                    (t) -> {
                        Address address = t.getAddress();
                        if (null != address) {
                            City element = address.getCity();
                            if (null != element) {
                                return element.getPrimaryKey();
                            }
                        }
                        return Integer.MIN_VALUE;
                    });
        }
        return DaoFilterExpression.empty();
    }

    public static DaoFilterExpression<CustomerDAO> expressionOf(Country country) {
        if (null != country && ModelHelper.existsInDatabase(country)) {
            return IntColumnValueFilter.of(DbColumn.CITY_COUNTRY, ComparisonOperator.EQUALS, country.getPrimaryKey(),
                    (t) -> {
                        Address address = t.getAddress();
                        if (null != address) {
                            City city = address.getCity();
                            if (null != city) {
                                Country element = city.getCountry();
                                if (null != element) {
                                    return element.getPrimaryKey();
                                }
                            }
                        }
                        return Integer.MIN_VALUE;
                    });
        }
        return DaoFilterExpression.empty();
    }

    public static StringColumnValueFilter<CustomerDAO> expressionOf(String name) {
        return StringColumnValueFilter.of(DbColumn.CUSTOMER_NAME, ComparisonOperator.EQUALS_CASE_INSENSITIVE, name, (t) -> t.getName());
    }

    DaoFilterExpression<CustomerDAO> getExpression();

    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS);
    }

}
