package scheduler.view.user;

import java.util.function.Predicate;
import static scheduler.AppResourceKeys.RESOURCEKEY_ACTIVEUSERS;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLUSERS;
import static scheduler.AppResourceKeys.RESOURCEKEY_INACTIVEUSERS;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.UserFilter;
import scheduler.model.UserStatus;
import scheduler.model.fx.UserModel;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.ModelFilter;

/**
 * UserDAO model filter.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface UserModelFilter extends ModelFilter<UserDAO, UserModel, UserFilter> {

    static UserModelFilter of(String headingText, UserFilter daoFilter, Predicate<UserModel> predicate) {
        return new UserModelFilter() {
            @Override
            public String getHeadingText() {
                return headingText;
            }

            @Override
            public UserFilter getDaoFilter() {
                return daoFilter;
            }

            @Override
            public boolean test(UserModel t) {
                return predicate.test(t);
            }

        };
    }

    /**
     * Model filter for all users.
     *
     * @return A model filter that matches any user.
     */
    public static UserModelFilter all() {
        return UserModelFilter.of(ResourceBundleHelper.getResourceString(ManageUsers.class, RESOURCEKEY_ALLUSERS),
                UserFilter.of(DaoFilterExpression.empty()),
                (t) -> true
        );
    }

    public static UserModelFilter active() {
        return UserModelFilter.of(ResourceBundleHelper.getResourceString(ManageUsers.class, RESOURCEKEY_ACTIVEUSERS),
                UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.NOT_EQUALS)),
                (t) -> null != t && t.getStatus() != UserStatus.INACTIVE
        );
    }

    public static UserModelFilter inactive() {
        return UserModelFilter.of(ResourceBundleHelper.getResourceString(ManageUsers.class, RESOURCEKEY_INACTIVEUSERS),
                UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)),
                (t) -> null != t && t.getStatus() == UserStatus.INACTIVE
        );
    }

}
