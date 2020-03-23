package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import scheduler.AppResources;
import scheduler.dao.schema.DbName;
import scheduler.view.ItemModel;
import scheduler.view.user.UserModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface UserFilter extends ModelFilter<UserImpl, UserModel> {

    public static UserFilter all() {
        return new UserFilter() {
            @Override
            public String getHeading() {
                return AppResources.getResourceString(AppResources.RESOURCEKEY_ALLCUSTOMERS);
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
                    return AppResources.getResourceString(AppResources.RESOURCEKEY_ACTIVEUSERS);
                }

                @Override
                public String getSqlFilterExpr() {
                    return String.format("`%s` <> ?", DbName.ACTIVE);
                }

                @Override
                public int apply(PreparedStatement ps, int index) throws SQLException {
                    ps.setInt(index++, UserStatus.INACTIVE.getValue());
                    return index;
                }

                @Override
                public boolean test(UserModel t) {
                    return t.getStatus() != UserStatus.INACTIVE;
                }

            };
        }
        return new UserFilter() {
            @Override
            public String getHeading() {
                return AppResources.getResourceString(AppResources.RESOURCEKEY_INACTIVEUSERS);
            }

            @Override
            public String getSqlFilterExpr() {
                return String.format("`%s` = ?", DbName.ACTIVE);
            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index++, UserStatus.INACTIVE.getValue());
                return index;
            }

            @Override
            public boolean test(UserModel t) {
                return t.getStatus() == UserStatus.INACTIVE;
            }

        };
    }

    @Override
    public default DataObjectImpl.Factory<UserImpl, ? extends ItemModel<UserImpl>> getFactory() {
        return UserImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCUSTOMERS);
    }

    @Override
    public default String getSubHeading() {
        return "";
    }

}
