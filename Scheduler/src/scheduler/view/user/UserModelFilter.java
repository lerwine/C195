package scheduler.view.user;

import java.util.function.Predicate;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_ALLUSERS;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.UserFilter;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.ModelFilter;

/**
 * UserDAO model filter.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface UserModelFilter extends ModelFilter<UserDAO, UserModelImpl, UserFilter> {

    static UserModelFilter of(String headingText, UserFilter daoFilter, Predicate<UserModel<UserDAO>> predicate) {
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
            public boolean test(UserModelImpl t) {
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
        return UserModelFilter.of(
                ResourceBundleLoader.getResourceString(ManageUsers.class, RESOURCEKEY_ALLUSERS),
                UserFilter.of(DaoFilterExpression.empty()),
                (t) -> true
        );
    }

}
