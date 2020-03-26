package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import scheduler.AppResources;
import scheduler.dao.dml.deprecated.BooleanComparisonStatement;
import scheduler.dao.dml.deprecated.ColumnComparisonStatement;
import scheduler.dao.dml.deprecated.ColumnReference;
import scheduler.dao.dml.deprecated.ComparisonOperator;
import scheduler.dao.dml.deprecated.QueryColumnSelector;
import scheduler.dao.dml.deprecated.SelectColumnList;
import scheduler.dao.dml.deprecated.TableReference;
import scheduler.dao.dml.deprecated.WhereStatement;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.view.ItemModel;
import scheduler.view.user.UserModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface UserFilter extends ModelListingFilter<UserImpl, UserModel> {

    public static UserFilter all() {
        return new UserFilter() {
            @Override
            public String getHeading() {
                return AppResources.getResourceString(AppResources.RESOURCEKEY_ALLCUSTOMERS);
            }

            @Override
            public WhereStatement<UserImpl, UserModel> getWhereStatement() {
                return null;
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
                private final QueryColumnSelector selector = QueryColumnSelector.of(DbColumn.STATUS);
                private final ColumnComparisonStatement<UserImpl, UserModel> whereStatement = (isActive) ?
                        new ColumnComparisonStatement<UserImpl, UserModel>() {
                            @Override
                            public ComparisonOperator getOperator() {
                                return ComparisonOperator.NOT_EQUAL_TO;
                            }

                            @Override
                            public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                                ps.setInt(currentIndex, UserStatus.INACTIVE.getValue());
                                return currentIndex + 1;
                            }

                            @Override
                            public boolean test(UserModel t) {
                                return t.getStatus() != UserStatus.INACTIVE;
                            }

                            @Override
                            public QueryColumnSelector getColumnSelector() {
                                return selector;
                            }
                        } : 
                        new ColumnComparisonStatement<UserImpl, UserModel>() {

                            @Override
                            public QueryColumnSelector getColumnSelector() {
                                return selector;
                            }

                            @Override
                            public ComparisonOperator getOperator() {
                                return ComparisonOperator.EQUAL_TO;
                            }

                            @Override
                            public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                                ps.setInt(currentIndex, UserStatus.INACTIVE.getValue());
                                return currentIndex + 1;
                            }

                            @Override
                            public boolean test(UserModel t) {
                                return t.getStatus() == UserStatus.INACTIVE;
                            }

                        };
                
                @Override
                public String getHeading() {
                    return AppResources.getResourceString(AppResources.RESOURCEKEY_ACTIVEUSERS);
                }

                @Override
                public WhereStatement<UserImpl, UserModel> getWhereStatement() {
                    return whereStatement;
                }

                @Override
                public int apply(PreparedStatement ps, int index) throws SQLException {
                    ps.setInt(index++, UserStatus.INACTIVE.getValue());
                    return index;
                }

                // TODO: Is test necessary to be implemented every time since we have a WhereStatement? Can it be implemented as default on interface?
                @Override
                public boolean test(UserModel t) {
                    return whereStatement.test(t);
                }

            };
        }
        return new UserFilter() {
            @Override
            public String getHeading() {
                return AppResources.getResourceString(AppResources.RESOURCEKEY_INACTIVEUSERS);
            }

//            @Override
//            public String getSqlFilterExpr() {
//                return String.format("`%s` = ?", DbName.ACTIVE);
//            }

            @Override
            public WhereStatement<UserImpl, UserModel> getWhereStatement() {
                // TODO: Implement this
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public default DataObjectImpl.Factory_obsolete<UserImpl, UserModel> getFactory() {
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
