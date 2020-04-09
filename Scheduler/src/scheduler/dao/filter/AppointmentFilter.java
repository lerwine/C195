package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGAPPOINTMENTS;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerElement;
import scheduler.dao.UserElement;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface AppointmentFilter extends DaoFilter<AppointmentDAO> {

    DaoFilterExpression<AppointmentDAO> getExpression();
    
    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS);
    }

    public static AppointmentFilter of(DaoFilterExpression<AppointmentDAO> expr) {
        return new AppointmentFilter() {

            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                expr.appendSimpleDmlConditional(sb);
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                expr.appendJoinedDmlConditional(sb);
            }
            
            @Override
            public DaoFilterExpression<AppointmentDAO> getExpression() {
                return expr;
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return expr.applyWhereParameters(ps, index);
            }

            @Override
            public boolean test(AppointmentDAO t) {
                return expr.test(t);
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof AppointmentFilter) {
                    AppointmentFilter other = (AppointmentFilter)obj;
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
    
    public static AppointmentFilter of(CustomerElement customer, UserElement user, Timestamp start, Timestamp end) {
        if (null != customer && customer.isExisting()) {
            if (null != user && user.isExisting()) {
                if (null != start) {
                    if (null != end) {
                        return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                                LogicalFilter.of(LogicalOperator.OR,
                                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                                                (t) -> t.getCustomer().getPrimaryKey()),
                                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                                                (t) -> t.getUser().getPrimaryKey())
                                ),
                                TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart()),
                                TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart())
                        ));
                    }
                    return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                            LogicalFilter.of(LogicalOperator.OR,
                                    IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                                            (t) -> t.getCustomer().getPrimaryKey()),
                                    IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                                            (t) -> t.getUser().getPrimaryKey())
                            ),
                            TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart())
                    ));
                }
                if (null != end) {
                    return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                            LogicalFilter.of(LogicalOperator.OR,
                                    IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                                            (t) -> t.getCustomer().getPrimaryKey()),
                                    IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                                            (t) -> t.getUser().getPrimaryKey())
                            ),
                            TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart())
                    ));
                }
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.OR,
                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                                (t) -> t.getCustomer().getPrimaryKey()),
                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                                (t) -> t.getUser().getPrimaryKey())
                ));
            }
            
            if (null != start) {
                if (null != end) {
                    return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                            IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                                            (t) -> t.getCustomer().getPrimaryKey()),
                            TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart()),
                            TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart())
                    ));
                }
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                                        (t) -> t.getCustomer().getPrimaryKey()),
                        TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart())
                ));
            }
            if (null != end) {
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                                        (t) -> t.getCustomer().getPrimaryKey()),
                        TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart())
                ));
            }
            return AppointmentFilter.of(IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                            (t) -> t.getCustomer().getPrimaryKey()));
        }
        
        
        
        
        if (null != user && user.isExisting()) {
            if (null != start) {
                if (null != end) {
                    return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                            IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                                            (t) -> t.getUser().getPrimaryKey()),
                            TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart()),
                            TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart())
                    ));
                }
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                                        (t) -> t.getUser().getPrimaryKey()),
                        TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart())
                ));
            }
            if (null != end) {
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                        IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                                        (t) -> t.getUser().getPrimaryKey()),
                        TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart())
                ));
            }
            return AppointmentFilter.of(IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                            (t) -> t.getUser().getPrimaryKey()));
        }

        if (null != start) {
            if (null != end) {
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                        TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart()),
                        TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart())
                ));
            }
            return AppointmentFilter.of(TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.NOT_LESS_THAN, start, (t) -> t.getStart()));
        }
        if (null != end) {
            return AppointmentFilter.of(TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.NOT_GREATER_THAN, end, (t) -> t.getStart()));
        }
        return AppointmentFilter.of(DaoFilterExpression.empty());
    }
}
