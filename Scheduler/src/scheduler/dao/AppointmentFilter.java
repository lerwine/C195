/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import scheduler.App;
import scheduler.util.ThrowableBiFunction;
import scheduler.view.ItemModel;
import static scheduler.dao.AppointmentImpl.COLNAME_CUSTOMERID;
import static scheduler.dao.AppointmentImpl.COLNAME_END;
import static scheduler.dao.AppointmentImpl.COLNAME_START;
import static scheduler.dao.AppointmentImpl.COLNAME_USERID;
import scheduler.util.DB;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.user.UserModel;

/**
 *
 * @author lerwi
 */
public interface AppointmentFilter extends ModelFilter<AppointmentImpl, AppointmentModel> {

    void initializeNew(AppointmentModel model);

    @Override
    public default DataObjectImpl.Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
        return AppointmentImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return App.getResourceString(App.RESOURCEKEY_LOADINGAPPOINTMENTS);
    }

    /**
     * Create a new appointment filter.
     *
     * @param heading The heading to display in the items listing view.
     * @param subHeading The sub-heading to display in the items listing view.
     * @param predicate The {@link Predicate} that corresponds to the SQL filter expression.
     * @param sqlFilterExpr The WHERE clause sub-expression for filtering results.
     * @param applyValues Sets the parameterized values of the {@link PreparedStatemement}. The second argument of this {@link ThrowableBiFunction} is
     * the next sequential parameterized value index, and the return value is the next available sequential index.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return A new appointment filter.
     */
    public static AppointmentFilter of(String heading, String subHeading, Predicate<AppointmentModel> predicate, String sqlFilterExpr,
            ThrowableBiFunction<PreparedStatement, Integer, Integer, SQLException> applyValues, Consumer<AppointmentModel> initializeNew) {
        if (null == subHeading) {
            return of(heading, "", predicate, sqlFilterExpr, applyValues, initializeNew);
        }

        if (null == initializeNew) {
            return of(heading, subHeading, predicate, sqlFilterExpr, applyValues, (m) -> m.setUser(new UserModel(App.getCurrentUser())));
        }

        Objects.requireNonNull(heading);
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

            @Override
            public void initializeNew(AppointmentModel model) {
                initializeNew.accept(model);
            }
        };
    }

    /**
     * Create a new appointment filter.
     *
     * @param heading The heading to display in the items listing view.
     * @param predicate The {@link Predicate} that corresponds to the SQL filter expression.
     * @param sqlFilterExpr The WHERE clause sub-expression for filtering results.
     * @param applyValues Sets the parameterized values of the {@link PreparedStatemement}. The second argument of this {@link ThrowableBiFunction} is
     * the next sequential parameterized value index, and the return value is the next available sequential index.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return A new appointment filter.
     */
    public static AppointmentFilter of(String heading, Predicate<AppointmentModel> predicate, String sqlFilterExpr,
            ThrowableBiFunction<PreparedStatement, Integer, Integer, SQLException> applyValues, Consumer<AppointmentModel> initializeNew) {
        return of(heading, "", predicate, sqlFilterExpr, applyValues, initializeNew);
    }

    /**
     * Creates a new appointment filter to show all appointments.
     *
     * @return An appointment filter to show all appointments.
     */
    public static AppointmentFilter all() {
        return AppointmentFilter.of(
                // heading
                App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTS),
                // predicate
                (m) -> true,
                // sqlFilterExpr
                "",
                // applyValues
                (ps, i) -> i,
                // initializeNew
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show all appointments for a specific customer.
     *
     * @param customerId The primary key of the customer record.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show all appointments for a specific customer.
     */
    public static AppointmentFilter byCustomer(int customerId, String heading, Consumer<AppointmentModel> initializeNew) {
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId,
                // sqlFilterExpr
                String.format("`%s` = ?", COLNAME_CUSTOMERID),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, customerId);
                    return i;
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show all appointments for a specific customer.
     *
     * @param customer The customer to match.
     * @return An appointment filter to show all appointments for a specific customer.
     */
    public static AppointmentFilter byCustomer(CustomerImpl customer) {
        return byCustomer(customer.getPrimaryKey(), String.format(App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFOR), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show all appointments for a specific user.
     *
     * @param userId The primary key of the user record.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show all appointments for a specific user.
     */
    public static AppointmentFilter byUser(int userId, String heading, Consumer<AppointmentModel> initializeNew) {
        return AppointmentFilter.of(Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getUser().getPrimaryKey() == userId,
                // sqlFilterExpr
                String.format("`%s` = ?", COLNAME_USERID),
                // applyValues
                (ps, i) -> {
                    ps.setInt(i++, userId);
                    return i;
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show all appointments for a specific user.
     *
     * @param user The user to match.
     * @return An appointment filter to show all appointments for a specific user.
     */
    public static AppointmentFilter byUser(UserImpl user) {
        return byUser(user.getPrimaryKey(), (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey())
                ? App.getResourceString(App.RESOURCEKEY_ALLMYAPPOINTMENTS) : String.format(App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFOR),
                user.getUserName()), (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show all appointments for the current user.
     *
     * @return An appointment filter to show all appointments for the current user.
     */
    public static AppointmentFilter allMyItems() {
        return byUser(App.getCurrentUser().getPrimaryKey(), App.getResourceString(App.RESOURCEKEY_ALLMYAPPOINTMENTS),
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show all appointments for a specific customer and user.
     *
     * @param customerId The primary key of the customer record.
     * @param userId The primary key of the user record.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show all appointments for a specific customer and user.
     */
    public static AppointmentFilter byCustomerAndUser(int customerId, int userId, String heading, Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show all appointments for a specific customer and user.
     *
     * @param customer The customer to match.
     * @param user The user to match.
     * @return An appointment filter to show all appointments for a specific customer and user.
     */
    public static AppointmentFilter byCustomerAndUser(CustomerImpl customer, UserImpl user) {
        return byCustomerAndUser(user.getPrimaryKey(), customer.getPrimaryKey(),
                String.format(App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFORBOTH), customer.getName(), user.getUserName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(user));
                });
    }

    /**
     * Creates a new appointment filter to show appointments that have ended before a specified date.
     *
     * @param date The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @return An appointment filter to show appointments that have ended before a specified date.
     */
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
                },
                // initializeNew
                null);
    }

    /**
     * Creates a new appointment filter to show appointments that have ended before a specified date.
     *
     * @param date The exclusive end date.
     * @return An appointment filter to show appointments that have ended before a specified date.
     */
    public static AppointmentFilter beforeDate(LocalDate date) {
        return beforeDate(date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFORE),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))));
    }

    /**
     * Creates a new appointment filter to show appointments for a specified customer that have ended before a specified date.
     *
     * @param customerId The primary key of the customer record.
     * @param date The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specified customer that have ended before a specified date.
     */
    public static AppointmentFilter byCustomerBeforeDate(int customerId, LocalDate date, String heading, Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specified customer that have ended before a specified date.
     *
     * @param customer The customer to match.
     * @param date The exclusive end date.
     * @return An appointment filter to show appointments for a specified customer that have ended before a specified date.
     */
    public static AppointmentFilter byCustomerBeforeDate(CustomerImpl customer, LocalDate date) {
        return byCustomerBeforeDate(customer.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFOREFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specified user that have ended before a specified date.
     *
     * @param userId The primary key of the user record.
     * @param date The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specified user that have ended before a specified date.
     */
    public static AppointmentFilter byUserBeforeDate(int userId, LocalDate date, String heading, Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specified user that have ended before a specified date.
     *
     * @param user The user to match.
     * @param date The exclusive end date.
     * @return An appointment filter to show appointments for a specified user that have ended before a specified date.
     */
    public static AppointmentFilter byUserBeforeDate(UserImpl user, LocalDate date) {
        return byCustomerBeforeDate(user.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFOREFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for the current user that have ended before a specified date.
     *
     * @param date The exclusive end date.
     * @return An appointment filter to show appointments for the current user that have ended before a specified date.
     */
    public static AppointmentFilter myBeforeDate(LocalDate date) {
        return byCustomerBeforeDate(App.getCurrentUser().getPrimaryKey(), date,
                String.format(App.getResourceString(App.RESOURCEKEY_MYAPPOINTMENTSBEFORE),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))),
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show appointments for a specified user and customer that have ended before a specified date.
     *
     * @param customerId The primary key of the customer record.
     * @param userId The primary key of the user record.
     * @param date The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specified user and customer that have ended before a specified date.
     */
    public static AppointmentFilter byCustomerAndUserBeforeDate(int customerId, int userId, LocalDate date, String heading,
            Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specified user and customer that have ended before a specified date.
     *
     * @param customer The customer to match.
     * @param user The user to match.
     * @param date The exclusive end date.
     * @return An appointment filter to show appointments for a specified user and customer that have ended before a specified date.
     */
    public static AppointmentFilter byCustomerAndUserBeforeDate(CustomerImpl customer, UserImpl user, LocalDate date) {
        return byCustomerAndUserBeforeDate(customer.getPrimaryKey(), user.getPrimaryKey(), date,
                String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBEFOREFORBOTH),
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName(),
                        user.getUserName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(user));
                });
    }

    /**
     * Creates a new appointment filter to show appointments that end on or after a specified date.
     *
     * @param date The inclusive start date.
     * @param heading The heading to display in the items listing view.
     * @return An appointment filter to show appointments that end on or after a specified date.
     */
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
                },
                // initializeNew
                null);
    }

    /**
     * Creates a new appointment filter to show appointments that end on or after a specified date.
     *
     * @param date The inclusive start date.
     * @return An appointment filter to show appointments that end on or after a specified date.
     */
    public static AppointmentFilter onOrAfterDate(LocalDate date) {
        return onOrAfterDate(date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTER),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))));
    }

    /**
     * Creates a new appointment filter to show appointments for a specified customer that end on or after a specified date.
     *
     * @param customerId The primary key of the customer record.
     * @param date The inclusive start date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specified customer that end on or after a specified date.
     */
    public static AppointmentFilter byCustomerOnOrAfterDate(int customerId, LocalDate date, String heading,
            Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specified customer that end on or after a specified date.
     *
     * @param customer The customer to match;
     * @param date The inclusive start date.
     * @return An appointment filter to show appointments for a specified customer that end on or after a specified date.
     */
    public static AppointmentFilter byCustomerOnOrAfterDate(CustomerImpl customer, LocalDate date) {
        return byCustomerOnOrAfterDate(customer.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTERFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specified user that end on or after a specified date.
     *
     * @param userId The primary key of the user record.
     * @param date The inclusive start date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specified user that end on or after a specified date.
     */
    public static AppointmentFilter byUserOnOrAfterDate(int userId, LocalDate date, String heading, Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specified user that end on or after a specified date.
     *
     * @param user The user to match.
     * @param date The inclusive start date.
     * @return An appointment filter to show appointments for a specified user that end on or after a specified date.
     */
    public static AppointmentFilter byUserOnOrAfterDate(UserImpl user, LocalDate date) {
        return byUserOnOrAfterDate(user.getPrimaryKey(), date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTERFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for the current user that end on or after a specified date.
     *
     * @param date The inclusive start date.
     * @return An appointment filter to show appointments for the current user that end on or after a specified date.
     */
    public static AppointmentFilter myOnOrAfterDate(LocalDate date) {
        return byUserOnOrAfterDate(App.getCurrentUser().getPrimaryKey(), date,
                String.format(App.getResourceString(App.RESOURCEKEY_MYAPPOINTMENTSONORAFTER),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))),
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show appointments for a specified customer and user that end on or after a specified date.
     *
     * @param customerId The primary key of the customer record.
     * @param userId The primary key of the user record.
     * @param date The inclusive start date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specified customer and user that end on or after a specified date.
     */
    public static AppointmentFilter byCustomerAndUserOnOrAfterDate(int customerId, int userId, LocalDate date, String heading,
            Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specified customer and user that end on or after a specified date.
     *
     * @param customer The customer to match.
     * @param user The user to match.
     * @param date The inclusive start date.
     * @return An appointment filter to show appointments for a specified customer and user that end on or after a specified date.
     */
    public static AppointmentFilter byCustomerAndUserOnOrAfterDate(CustomerImpl customer, UserImpl user, LocalDate date) {
        return byCustomerAndUserOnOrAfterDate(customer.getPrimaryKey(), user.getPrimaryKey(), date,
                String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSAFTERFORBOTH),
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName(),
                        user.getUserName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(user));
                });
    }

    /**
     * Creates a new appointment filter to show appointments whose date ranges overlap the specified date range.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @return An appointment filter to show appointments whose date ranges overlap the specified date range.
     */
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
                },
                // initializeNew
                null);
    }

    /**
     * Creates a new appointment filter to show appointments whose date ranges overlap the specified date range.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @return An appointment filter to show appointments whose date ranges overlap the specified date range.
     */
    public static AppointmentFilter range(LocalDate start, LocalDate end) {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return range(start, end, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBETWEEN), df.format(start), df.format(end)));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose date ranges overlap the specified date range.
     *
     * @param customerId The primary key of the customer record.
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specific customer whose date ranges overlap the specified date range.
     */
    public static AppointmentFilter byCustomerWithin(int customerId, LocalDate start, LocalDate end, String heading,
            Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose date ranges overlap the specified date range.
     *
     * @param userId The primary key of the user record.
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specific user whose date ranges overlap the specified date range.
     */
    public static AppointmentFilter byUserWithin(int userId, LocalDate start, LocalDate end, String heading,
            Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer and user whose date ranges overlap the specified date range.
     *
     * @param customerId The primary key of the customer record.
     * @param userId The primary key of the customer record.
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @param heading The heading to display in the items listing view.
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return An appointment filter to show appointments for a specific customer and user whose date ranges overlap the specified date range.
     */
    public static AppointmentFilter byCustomerAndUserWithin(int customerId, int userId, LocalDate start, LocalDate end, String heading,
            Consumer<AppointmentModel> initializeNew) {
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
                },
                // initializeNew
                initializeNew);
    }

    /**
     * Creates a new appointment filter to show appointments whose range occurs a specific date ranges.
     *
     * @param date The target date.
     * @return An appointment filter to show appointments whose range occurs a specific date ranges.
     */
    public static AppointmentFilter on(LocalDate date) {
        return range(date, date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSON),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose range occurs a specific date ranges.
     *
     * @param customer The customer to match.
     * @param date The target date.
     * @return An appointment filter to show appointments for a specific customer whose range occurs a specific date ranges.
     */
    public static AppointmentFilter byCustomerOn(CustomerImpl customer, LocalDate date) {
        return byCustomerWithin(customer.getPrimaryKey(), date, date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSONFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose range occurs a specific date ranges.
     *
     * @param user The user to match.
     * @param date The target date.
     * @return An appointment filter to show appointments for a specific user whose range occurs a specific date ranges.
     */
    public static AppointmentFilter byUserOn(UserImpl user, LocalDate date) {
        return byUserWithin(user.getPrimaryKey(), date, date, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSONFOR),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date)), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for the current user whose range occurs a specific date ranges.
     *
     * @param date The target date.
     * @return An appointment filter to show appointments for the current user whose range occurs a specific date ranges.
     */
    public static AppointmentFilter myOn(LocalDate date) {
        return byUserWithin(App.getCurrentUser().getPrimaryKey(), date, date, String.format(App.getResourceString(App.RESOURCEKEY_MYAPPOINTMENTSON),
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(Objects.requireNonNull(date))),
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose date ranges overlap the specified date range.
     *
     * @param customer The customer to match.
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @return An appointment filter to show appointments for a specific customer whose date ranges overlap the specified date range.
     */
    public static AppointmentFilter byCustomerWithin(CustomerImpl customer, LocalDate start, LocalDate end) {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return byCustomerWithin(customer.getPrimaryKey(), start, end, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBETWEENFOR),
                df.format(Objects.requireNonNull(start)), df.format(Objects.requireNonNull(end)), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose date ranges overlap the specified date range.
     *
     * @param user The user to match.
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @return An appointment filter to show appointments for a specific user whose date ranges overlap the specified date range.
     */
    public static AppointmentFilter byUserWithin(UserImpl user, LocalDate start, LocalDate end) {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return byUserWithin(user.getPrimaryKey(), start, end, String.format(App.getResourceString(App.RESOURCEKEY_APPOINTMENTSBETWEENFOR),
                df.format(Objects.requireNonNull(start)), df.format(Objects.requireNonNull(end)), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for the current user whose date ranges overlap the specified date range.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @return An appointment filter to show appointments for the current user whose date ranges overlap the specified date range.
     */
    public static AppointmentFilter myWithin(LocalDate start, LocalDate end) {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return byUserWithin(App.getCurrentUser().getPrimaryKey(), start, end,
                String.format(App.getResourceString(App.RESOURCEKEY_MYAPPOINTMENTSBETWEEN),
                df.format(Objects.requireNonNull(start)), df.format(Objects.requireNonNull(end))),
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show appointments whose end date is on after the current date and the start date is on or before the
     * current date.
     *
     * @return An appointment filter to show appointments whose end date is on after the current date and the start date is on or before the current
     * date.
     */
    public static AppointmentFilter current() {
        LocalDate date = LocalDate.now();
        return range(date, date, App.getResourceString(App.RESOURCEKEY_CURRENTAPPOINTMENTS));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose end date is on after the current date and the start date is
     * on or before the current date.
     *
     * @param customer The customer to match.
     * @return An appointment filter to show appointments for a specific customer whose end date is on after the current date and the start date is on
     * or before the current date.
     */
    public static AppointmentFilter byCustomerCurrent(CustomerImpl customer) {
        LocalDate date = LocalDate.now();
        return byCustomerWithin(customer.getPrimaryKey(), date, date,
                String.format(App.getResourceString(App.RESOURCEKEY_CURRENTFOR), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose end date is on after the current date and the start date is on
     * or before the current date.
     *
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific user whose end date is on after the current date and the start date is on or
     * before the current date.
     */
    public static AppointmentFilter byUserCurrent(UserImpl user) {
        LocalDate date = LocalDate.now();
        return byUserWithin(user.getPrimaryKey(), date, date,
                String.format(App.getResourceString(App.RESOURCEKEY_CURRENTFOR), user.getUserName()),
                (m) -> {
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer and user whose end date is on after the current date and the
     * start date is on or before the current date.
     *
     * @param customer The customer to match.
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific customer and user whose end date is on after the current date and the start
     * date is on or before the current date.
     */
    public static AppointmentFilter byCustomerAndUserCurrent(CustomerImpl customer, UserImpl user) {
        LocalDate date = LocalDate.now();
        return byCustomerAndUserWithin(customer.getPrimaryKey(), user.getPrimaryKey(), date, date,
                String.format(App.getResourceString(App.RESOURCEKEY_CURRENTFORBOTH), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for the current user whose end date is on after the current date and the start date is on
     * or before the current date.
     *
     * @return An appointment filter to show appointments for the current user whose end date is on after the current date and the start date is on or
     * before the current date.
     */
    public static AppointmentFilter myCurrent() {
        LocalDate date = LocalDate.now();
        return byUserWithin(App.getCurrentUser().getPrimaryKey(), date, date,
                App.getResourceString(App.RESOURCEKEY_MYCURRENT),
                (m) -> {
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments whose end date is on after the current date.
     *
     * @return An appointment filter to show appointments whose end date is on after the current date.
     */
    public static AppointmentFilter currentAndFuture() {
        return onOrAfterDate(LocalDate.now(), App.getResourceString(App.RESOURCEKEY_ALLCURRENTANDFUTURE));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose end date is on after the current date.
     *
     * @param customer The customer to match.
     * @return An appointment filter to show appointments for a specific customer whose end date is on after the current date.
     */
    public static AppointmentFilter byCustomerCurrentAndFuture(CustomerImpl customer) {
        return byCustomerOnOrAfterDate(customer.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_CURRENTANDFUTUREFOR), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose end date is on after the current date.
     *
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific user whose end date is on after the current date.
     */
    public static AppointmentFilter byUserCurrentAndFuture(UserImpl user) {
        return byUserOnOrAfterDate(user.getPrimaryKey(), LocalDate.now(), (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey())
                ? App.getResourceString(App.RESOURCEKEY_MYCURRENTANDFUTURE)
                : String.format(App.getResourceString(App.RESOURCEKEY_CURRENTANDFUTUREFOR), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer and user whose end date is on after the current date.
     *
     * @param customer The customer to match.
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific customer and user whose end date is on after the current date.
     */
    public static AppointmentFilter byCustomerAndUserCurrentAndFuture(CustomerImpl customer, UserImpl user) {
        return byCustomerAndUserOnOrAfterDate(customer.getPrimaryKey(), user.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_CURRENTANDFUTUREFORBOTH), customer.getName(), user.getUserName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(user));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for the current user whose end date is on after the current date.
     *
     * @return An appointment filter to show appointments for the current user whose end date is on after the current date.
     */
    public static AppointmentFilter myCurrentAndFuture() {
        return byUserOnOrAfterDate(App.getCurrentUser().getPrimaryKey(), LocalDate.now(), App.getResourceString(App.RESOURCEKEY_MYCURRENTANDFUTURE),
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show appointments whose end date is before the current date.
     *
     * @return An appointment filter to show appointments whose end date is before the current date.
     */
    public static AppointmentFilter past() {
        return beforeDate(LocalDate.now(), App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTS));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose end date is before the current date.
     *
     * @param customer The customer to match.
     * @return An appointment filter to show appointments for a specific customer whose end date is before the current date.
     */
    public static AppointmentFilter byCustomerPast(CustomerImpl customer) {
        return byCustomerBeforeDate(customer.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTSFOR), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(App.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose end date is before the current date.
     *
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific user whose end date is before the current date.
     */
    public static AppointmentFilter byUserPast(UserImpl user) {
        return byUserBeforeDate(user.getPrimaryKey(), LocalDate.now(), (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey())
                ? App.getResourceString(App.RESOURCEKEY_MYPASTAPPOINTMENTS)
                : String.format(App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTSFOR), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer and user whose end date is before the current date.
     *
     * @param customer The customer to match.
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific customer and user whose end date is before the current date.
     */
    public static AppointmentFilter byCustomerAndUserPast(CustomerImpl customer, UserImpl user) {
        return byCustomerAndUserBeforeDate(customer.getPrimaryKey(), user.getPrimaryKey(), LocalDate.now(),
                String.format(App.getResourceString(App.RESOURCEKEY_PASTAPPOINTMENTSFORBOTH), customer.getName(), user.getUserName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(user));
                });
    }

    public static AppointmentFilter myPast() {
        return byUserOnOrAfterDate(App.getCurrentUser().getPrimaryKey(), LocalDate.now(), App.getResourceString(App.RESOURCEKEY_MYPASTAPPOINTMENTS),
                (m) -> m.setUser(new UserModel(App.getCurrentUser())));
    }

}
