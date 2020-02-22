package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.function.Predicate;
import scheduler.App;
import static scheduler.dao.AppointmentImpl.COLNAME_CUSTOMERID;
import static scheduler.dao.AppointmentImpl.COLNAME_START;
import static scheduler.dao.AppointmentImpl.COLNAME_END;
import static scheduler.dao.AppointmentImpl.COLNAME_USERID;
import scheduler.util.DB;
import scheduler.util.ThrowableBiFunction;
import scheduler.view.ItemModel;
import scheduler.view.appointment.AppointmentModel;

/**
 *
 * @author lerwi
 */
public interface AppointmentFilter extends ModelFilter<AppointmentImpl, AppointmentModel> {

    @Override
    public default DataObjectImpl.Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
        return AppointmentImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return App.getResourceString(App.RESOURCEKEY_LOADINGAPPOINTMENTS);
    }

    public static AppointmentFilter of(String heading, String subHeading, Predicate<AppointmentModel> predicate,
            String sqlFilterExpr, ThrowableBiFunction<PreparedStatement, Integer, Integer, SQLException> applyValues) {
        if (null == subHeading) {
            return of(heading, "", predicate, sqlFilterExpr, applyValues);
        }
        Objects.requireNonNull(heading);
        Objects.requireNonNull(sqlFilterExpr);
        Objects.requireNonNull(applyValues);
        Objects.requireNonNull(predicate);
        return new AppointmentFilter() {
            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSubHeading() {
                return subHeading;
            }

            @Override
            public String getSqlFilterExpr() {
                return sqlFilterExpr;
            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                return applyValues.apply(ps, index);
            }

            @Override
            public boolean test(AppointmentModel t) {
                return predicate.test(t);
            }

        };
    }

    public static AppointmentFilter of(String heading, Predicate<AppointmentModel> predicate,
            String sqlFilterExpr, ThrowableBiFunction<PreparedStatement, Integer, Integer, SQLException> applyValues) {
        return of(heading, "", predicate, sqlFilterExpr, applyValues);
    }

    public static AppointmentFilter all() {
        return AppointmentFilter.of(
                // heading
                App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTS),
                // predicate
                (m) -> true,
                // sqlFilterExpr
                "",
                // applyValues
                (ps, i) -> i);
    }

    public static AppointmentFilter byCustomer(int customerId, String heading) {
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId,
                // sqlFilterExpr
                String.format("`%s` = ?", COLNAME_CUSTOMERID),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, customerId);
                    return i;
                });
    }

    public static AppointmentFilter byCustomer(Customer customer) {
        return byCustomer(customer.getPrimaryKey(), String.format(App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFOR), customer.getName()));
    }

    public static AppointmentFilter byUser(int userId, String heading) {
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getUser().getPrimaryKey() == userId,
                // sqlFilterExpr
                String.format("`%s` = ?", COLNAME_USERID),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, userId);
                    return i;
                });
    }

    public static AppointmentFilter byUser(User user) {
        return byUser(user.getPrimaryKey(), (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey())
                ? App.getResourceString(App.RESOURCEKEY_ALLMYAPPOINTMENTS) : String.format(App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFOR),
                user.getUserName()));
    }

    public static AppointmentFilter allMyItems() {
        return byUser(App.getCurrentUser().getPrimaryKey(), App.getResourceString(App.RESOURCEKEY_ALLMYAPPOINTMENTS));
    }

    public static AppointmentFilter byCustomerAndUser(int customerId, int userId, String heading) {
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` = ?", COLNAME_CUSTOMERID, COLNAME_USERID),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, customerId);
                    ps.setInt(i++, userId);
                    return i;
                });
    }

    public static AppointmentFilter byCustomerAndUser(Customer customer, User user) {
        return byCustomerAndUser(user.getPrimaryKey(), customer.getPrimaryKey(),
                String.format(App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFORBOTH), customer.getName(), user.getUserName()));
    }

    public static AppointmentFilter beforeDate(LocalDate date, String heading) {
        final LocalDateTime e = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s` < ?", COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    return i;
                });
    }

    public static AppointmentFilter beforeDate(LocalDate date) {
        return beforeDate(date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFORE),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))));
    }

    public static AppointmentFilter byCustomerBeforeDate(int customerId, LocalDate date, String heading) {
        final LocalDateTime e = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` < ?", COLNAME_CUSTOMERID, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, customerId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    return i;
                });
    }

    public static AppointmentFilter byCustomerBeforeDate(Customer customer, LocalDate date) {
        return byCustomerBeforeDate(customer.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFOREFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName()));
    }

    public static AppointmentFilter byUserBeforeDate(int userId, LocalDate date, String heading) {
        final LocalDateTime e = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` < ?", COLNAME_USERID, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, userId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    return i;
                });
    }

    public static AppointmentFilter byUserBeforeDate(User user, LocalDate date) {
        return byCustomerBeforeDate(user.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFOREFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), user.getUserName()));
    }

    public static AppointmentFilter byCustomerAndUserBeforeDate(int customerId, int userId, LocalDate date, String heading) {
        final LocalDateTime e = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` < ?", COLNAME_USERID, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, userId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    return i;
                });
    }

    public static AppointmentFilter byCustomerAndUserBeforeDate(Customer customer, User user, LocalDate date) {
        return byCustomerAndUserBeforeDate(customer.getPrimaryKey(), user.getPrimaryKey(), date,
                String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFOREFORBOTH),
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName(), user.getUserName()));
    }

    public static AppointmentFilter onOrAfterDate(LocalDate date, String heading) {
        final LocalDateTime d = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s` >= ?", COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setTimestamp(i++, DB.toUtcTimestamp(d));
                    return i;
                });
    }

    public static AppointmentFilter onOrAfterDate(LocalDate date) {
        return onOrAfterDate(date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTER),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))));
    }

    public static AppointmentFilter byCustomerOnOrAfterDate(int customerId, LocalDate date, String heading) {
        final LocalDateTime d = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` >= ?", COLNAME_CUSTOMERID, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, customerId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(d));
                    return i;
                });
    }

    public static AppointmentFilter byCustomerOnOrAfterDate(Customer customer, LocalDate date) {
        return byCustomerOnOrAfterDate(customer.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTERFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName()));
    }

    public static AppointmentFilter byUserOnOrAfterDate(int userId, LocalDate date, String heading) {
        final LocalDateTime d = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` >= ?", COLNAME_USERID, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, userId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(d));
                    return i;
                });
    }

    public static AppointmentFilter byUserOnOrAfterDate(User user, LocalDate date) {
        return byUserOnOrAfterDate(user.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTERFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), user.getUserName()));
    }

    public static AppointmentFilter byCustomerAndUserOnOrAfterDate(int customerId, int userId, LocalDate date, String heading) {
        final LocalDateTime d = date.atTime(0, 0, 0, 0);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` >= ?", COLNAME_USERID, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, userId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(d));
                    return i;
                });
    }

    public static AppointmentFilter byCustomerAndUserOnOrAfterDate(Customer customer, User user, LocalDate date) {
        return byCustomerAndUserOnOrAfterDate(customer.getPrimaryKey(), user.getPrimaryKey(), date,
                String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTERFORBOTH),
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName(), user.getUserName()));
    }

    public static AppointmentFilter range(LocalDate start, LocalDate end, String heading) {
        final LocalDateTime s = start.atTime(0, 0, 0, 0);
        final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getStart().compareTo(e) < 0 && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s` < ? AND `%s` >= ?", COLNAME_START, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    ps.setTimestamp(i++, DB.toUtcTimestamp(s));
                    return i;
                });
    }

    public static AppointmentFilter range(LocalDate start, LocalDate end) {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return range(start, end, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBETWEEN), df.format(start), df.format(end)));
    }

    public static AppointmentFilter byCustomerWithin(int customerId, LocalDate start, LocalDate end, String heading) {
        final LocalDateTime s = start.atTime(0, 0, 0, 0);
        final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getStart().compareTo(e) < 0 && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` < ? AND `%s` >= ?", COLNAME_CUSTOMERID, COLNAME_START, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, customerId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    ps.setTimestamp(i++, DB.toUtcTimestamp(s));
                    return i;
                });
    }

    public static AppointmentFilter byUserWithin(int userId, LocalDate start, LocalDate end, String heading) {
        final LocalDateTime s = start.atTime(0, 0, 0, 0);
        final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getUser().getPrimaryKey() == userId && t.getStart().compareTo(e) < 0 && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` < ? AND `%s` >= ?", COLNAME_USERID, COLNAME_START, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, userId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    ps.setTimestamp(i++, DB.toUtcTimestamp(s));
                    return i;
                });
    }

    public static AppointmentFilter byCustomerAndUserWithin(int customerId, int userId, LocalDate start, LocalDate end, String heading) {
        final LocalDateTime s = start.atTime(0, 0, 0, 0);
        final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId && t.getStart().compareTo(e) < 0
                && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s` = ? AND `%s` = ? AND `%s` < ? AND `%s` >= ?", COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_START, COLNAME_END),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, customerId);
                    ps.setInt(i++, userId);
                    ps.setTimestamp(i++, DB.toUtcTimestamp(e));
                    ps.setTimestamp(i++, DB.toUtcTimestamp(s));
                    return i;
                });
    }

    public static AppointmentFilter on(LocalDate date) {
        return range(date, date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSON),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))));
    }

    public static AppointmentFilter byCustomerOn(Customer customer, LocalDate date) {
        return byCustomerWithin(customer.getPrimaryKey(), date, date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSONFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName()));
    }

    public static AppointmentFilter byUserOn(User user, LocalDate date) {
        return byUserWithin(user.getPrimaryKey(), date, date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSONFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), user.getUserName()));
    }

    public static AppointmentFilter byCustomerWithin(Customer customer, LocalDate start, LocalDate end) {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return byCustomerWithin(customer.getPrimaryKey(), start, end, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBETWEENFOR),
                df.format(Objects.requireNonNull(start)), df.format(Objects.requireNonNull(end)), customer.getName()));
    }

    public static AppointmentFilter byUserWithin(User user, LocalDate start, LocalDate end) {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return byUserWithin(user.getPrimaryKey(), start, end, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBETWEENFOR),
                df.format(Objects.requireNonNull(start)), df.format(Objects.requireNonNull(end)), user.getUserName()));
    }

    public static AppointmentFilter currentAndFuture() {
        return onOrAfterDate(LocalDate.now(), App.getResourceString(App.RESOURCEKEY_CURRENTANDFUTURE));
    }

    public static AppointmentFilter byCustomerCurrentAndFuture(Customer customer) {
        return byCustomerOnOrAfterDate(customer.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_CURRENTANDFUTUREFOR), customer.getName()));
    }

    public static AppointmentFilter byUserCurrentAndFuture(User user) {
        return byUserOnOrAfterDate(user.getPrimaryKey(), LocalDate.now(), (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey())
                ? App.getResourceString(App.RESOURCEKEY_MYCURRENTANDFUTURE)
                : String.format(App.getResourceString(App.RESOURCEKEY_CURRENTANDFUTUREFOR), user.getUserName()));
    }

    public static AppointmentFilter byCustomerAndUserCurrentAndFuture(Customer customer, User user) {
        return byCustomerAndUserOnOrAfterDate(customer.getPrimaryKey(), user.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_CURRENTANDFUTUREFORBOTH), customer.getName(), user.getUserName()));
    }

    public static AppointmentFilter myCurrentAndFuture() {
        return byUserOnOrAfterDate(App.getCurrentUser().getPrimaryKey(), LocalDate.now(), App.getResourceString(App.RESOURCEKEY_MYCURRENTANDFUTURE));
    }

    public static AppointmentFilter past() {
        return beforeDate(LocalDate.now(), App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTS));
    }

    public static AppointmentFilter byCustomerPast(Customer customer) {
        return byCustomerBeforeDate(customer.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTSFOR), customer.getName()));
    }

    public static AppointmentFilter byUserPast(User user) {
        return byUserBeforeDate(user.getPrimaryKey(), LocalDate.now(), (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey())
                ? App.getResourceString(App.RESOURCEKEY_MYPASTAPPOINTMENTS)
                : String.format(App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTSFOR), user.getUserName()));
    }

    public static AppointmentFilter byCustomerAndUserPast(Customer customer, User user) {
        return byCustomerAndUserBeforeDate(customer.getPrimaryKey(), user.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTSFORBOTH), customer.getName(), user.getUserName()));
    }

    public static AppointmentFilter myPast() {
        return byUserOnOrAfterDate(App.getCurrentUser().getPrimaryKey(), LocalDate.now(), App.getResourceString(App.RESOURCEKEY_MYPASTAPPOINTMENTS));
    }

}
