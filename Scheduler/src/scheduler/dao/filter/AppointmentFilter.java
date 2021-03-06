package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.schema.DbColumn;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import static scheduler.model.ModelHelper.getPrimaryKey;
import scheduler.model.User;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AppointmentFilter extends DaoFilter<AppointmentDAO> {

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
                    AppointmentFilter other = (AppointmentFilter) obj;
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

    public static DaoFilterExpression<AppointmentDAO> expressionOf(Customer customer) {
        if (ModelHelper.existsInDatabase(customer)) {
            return IntColumnValueFilter.of(DbColumn.APPOINTMENT_CUSTOMER, ComparisonOperator.EQUALS, customer.getPrimaryKey(),
                    (t) -> getPrimaryKey(t.getCustomer()));
        }
        return DaoFilterExpression.empty();
    }

    public static DaoFilterExpression<AppointmentDAO> expressionOf(User user) {
        if (ModelHelper.existsInDatabase(user)) {
            return IntColumnValueFilter.of(DbColumn.APPOINTMENT_USER, ComparisonOperator.EQUALS, user.getPrimaryKey(),
                    (t) -> getPrimaryKey(t.getUser()));
        }
        return DaoFilterExpression.empty();
    }

    public static DaoFilterExpression<AppointmentDAO> expressionOf(Customer customer, User user) {
        return expressionOf(customer).or(expressionOf(user));
    }

    /**
     * Creates a filter expression for a date range.
     *
     * @param start The inclusive start date/time.
     * @param end The exclusive end date/time.
     * @return A filter expression for appointments that occur on or after {@code start} and before {@code end};
     */
    public static DaoFilterExpression<AppointmentDAO> expressionOf(Timestamp start, Timestamp end) {

        /*
            appointments from 11:15 to 12:15
            end = 12:15
            start = 11:15
            DbColumn.START = 10:00
            DbColumn.END = 12:00
        
            DbColumn.START < end && DbColumn.END > start
                    10:00  < 12:15 && 12:00 > 11:15
         */
        if (null != start) {
            if (null != end) {
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                        TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.LESS_THAN, end, (t) -> t.getStart()),
                        TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.GREATER_THAN, start, (t) -> t.getEnd())
                ));
            }
            return TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.GREATER_THAN, start, (t) -> t.getEnd());
        }
        if (null != end) {
            return TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.LESS_THAN, end, (t) -> t.getStart());
        }
        return DaoFilterExpression.empty();
    }

    /**
     * Creates a filter expression for a date range.
     *
     * @param customer
     * @param start The inclusive start date/time.
     * @param end The exclusive end date/time.
     * @return A filter expression for appointments that occur on or after {@code start} and before {@code end};
     */
    public static DaoFilterExpression<AppointmentDAO> expressionOf(Customer customer, Timestamp start, Timestamp end) {
        if (ModelHelper.existsInDatabase(customer)) {
            return expressionOf(start, end);
        }
        if (null != start) {
            if (null != end) {
                return AppointmentFilter.of(LogicalFilter.of(LogicalOperator.AND,
                        TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.LESS_THAN, end, (t) -> t.getStart()),
                        TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.GREATER_THAN, start, (t) -> t.getEnd())
                ));
            }
            return TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.GREATER_THAN, start, (t) -> t.getEnd());
        }
        if (null != end) {
            return TimestampColumnValueFilter.of(DbColumn.START, ComparisonOperator.LESS_THAN, end, (t) -> t.getStart());
        }
        return DaoFilterExpression.empty();
    }

    public static AppointmentFilter of(Customer customer, User user, Timestamp start, Timestamp end) {
        return AppointmentFilter.of(expressionOf(customer, user).and(expressionOf(start, end)));
    }

    DaoFilterExpression<AppointmentDAO> getExpression();

    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS);
    }

}
