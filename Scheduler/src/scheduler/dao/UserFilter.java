package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import scheduler.AppResources;
import scheduler.dao.dml.BooleanComparisonStatement;
import scheduler.dao.dml.ColumnComparisonStatement;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.ComparisonOperator;
import scheduler.dao.dml.SelectColumnList;
import scheduler.dao.dml.TableReference;
import scheduler.dao.dml.WhereStatement;
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
                private final SelectColumnList table = UserImpl.getFactory().getSelectColumns();
                private final ColumnReference column = table.findFirst(DbColumn.STATUS);
                private final ColumnComparisonStatement<UserImpl, UserModel> whereStatement = (isActive) ? new ColumnComparisonStatement<UserImpl, UserModel>() {
                    @Override
                    public ColumnReference getColumn() {
                        return column;
                    }

                    @Override
                    public TableReference getTable() {
                        return table;
                    }

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
                        // TODO: Need to add generic for ItemModel to the ColumnComparisonStatement interface.
                        return ((UserModel)t).getStatus() != UserStatus.INACTIVE;
                    }

                    } : new ColumnComparisonStatement<UserImpl, UserModel>() {
                    @Override
                    public ColumnReference getColumn() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public TableReference getTable() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public ComparisonOperator getOperator() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public boolean test(UserModel t) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
