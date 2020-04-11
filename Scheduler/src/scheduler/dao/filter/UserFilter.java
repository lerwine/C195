package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGUSERS;
import scheduler.AppResources;
import scheduler.dao.UserDAO;
import scheduler.dao.UserStatus;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface UserFilter extends DaoFilter<UserDAO> {

    DaoFilterExpression<UserDAO> getExpression();
    
    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS);
    }

    public static UserFilter of(DaoFilterExpression<UserDAO> expr) {
        return new UserFilter() {
            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                expr.appendSimpleDmlConditional(sb);
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                expr.appendJoinedDmlConditional(sb);
            }
            
            @Override
            public DaoFilterExpression<UserDAO> getExpression() {
                return expr;
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return expr.applyWhereParameters(ps, index);
            }

            @Override
            public boolean test(UserDAO t) {
                return expr.test(t);
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof UserFilter) {
                    UserFilter other = (UserFilter)obj;
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
    
    public static IntColumnValueFilter<UserDAO> expressionOf(UserStatus status, ComparisonOperator operator) {
        return IntColumnValueFilter.of(DbColumn.STATUS, operator, status.getValue(), (t) -> t.getStatus().getValue());
    }
    
    public static StringColumnValueFilter<UserDAO> expressionOf(String userName) {
        return StringColumnValueFilter.of(DbColumn.USER_NAME, ComparisonOperator.EQUALS_CASE_INSENSITIVE, userName, (t) -> t.getUserName());
    }
}
