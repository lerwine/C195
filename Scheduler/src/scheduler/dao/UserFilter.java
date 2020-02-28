package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import scheduler.App;
import static scheduler.dao.UserImpl.COLNAME_ACTIVE;
import static scheduler.util.Values.USER_STATUS_INACTIVE;
import scheduler.view.ItemModel;
import scheduler.view.user.UserModel;

/**
 *
 * @author lerwi
 */
public interface UserFilter extends ModelFilter<UserImpl, UserModel> {

    @Override
    public default DataObjectImpl.Factory<UserImpl, ? extends ItemModel<UserImpl>> getFactory() {
        return UserImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return App.getResourceString(App.RESOURCEKEY_LOADINGCUSTOMERS);
    }

    @Override
    public default String getSubHeading() {
        return "";
    }

    public static UserFilter all() {
        return new UserFilter() {
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
            public boolean test(UserModel t) {
                return true;
            }

        };
    }

    public static UserFilter active(boolean isActive) {
        if (isActive) {
            return new UserFilter() {
                @Override
                public String getHeading() {
                    return App.getResourceString(App.ACTIVE_USERS);
                }

                @Override
                public String getSqlFilterExpr() {
                    return String.format("`%s` <> ?", COLNAME_ACTIVE);
                }

                @Override
                public int apply(PreparedStatement ps, int index) throws SQLException {
                    ps.setInt(index++, USER_STATUS_INACTIVE);
                    return index;
                }

                @Override
                public boolean test(UserModel t) {
                    return t.getStatus() != USER_STATUS_INACTIVE;
                }

            };
        }
        return new UserFilter() {
            @Override
            public String getHeading() {
                return App.getResourceString(App.INACTIVE_USERS);
            }

            @Override
            public String getSqlFilterExpr() {
                return String.format("`%s` = ?", COLNAME_ACTIVE);
            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index++, USER_STATUS_INACTIVE);
                return index;
            }

            @Override
            public boolean test(UserModel t) {
                return t.getStatus() == USER_STATUS_INACTIVE;
            }

        };
    }

}