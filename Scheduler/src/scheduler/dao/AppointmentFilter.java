package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import scheduler.Scheduler;
import scheduler.dao.dml.ComparisonOperator;
import scheduler.dao.dml.IntegerComparisonStatement;
import scheduler.dao.dml.SelectColumnList;
import scheduler.dao.dml.WhereStatement;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;
import scheduler.util.DB;
import scheduler.util.ResourceBundleLoader;
import scheduler.util.ThrowableBiFunction;
import scheduler.view.ItemModel;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.customer.CustomerModel;
import scheduler.view.user.UserModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
// TODO: Deprecated this after it is replaced
public interface AppointmentFilter extends ModelFilter<AppointmentImpl, AppointmentModel> {

    
    public static AppointmentFilter of(FilterType type, String heading, String subHeading,
            Function<SelectColumnList, WhereStatement<AppointmentImpl>> getFilter, Consumer<AppointmentModel> initializeNew) {
        if (null == subHeading) {
            return of(type, heading, "", getFilter, initializeNew);
        }

        Objects.requireNonNull(heading);
        Objects.requireNonNull(getFilter);
        return new AppointmentFilter() {
            private final WhereStatement<AppointmentImpl> whereStatement = getFilter.apply(AppointmentImpl.getFactory().getDetailDml());
            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSubHeading() {
                return subHeading;
            }

            @Override
            public WhereStatement<AppointmentImpl> getWhereStatement() {
                return whereStatement;
            }
            
            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                return whereStatement.applyValues(ps, index);
            }

            @Override
            public boolean test(AppointmentModel t) {
                return whereStatement.test(t);
            }

            @Override
            public void initializeNew(AppointmentModel model) {
                if (null != initializeNew)
                    initializeNew.accept(model);
            }

            @Override
            public FilterType getType() {
                return type;
            }

        };
    }

    /**
     * Create a new appointment filter.
     *
     * @param type
     * @param heading The heading to display in the items listing view.
     * @param getFilter
     * @param initializeNew Initializes new {@link AppointmentModel} objects with default values appropriate for the filter.
     * @return A new appointment filter.
     */
    public static AppointmentFilter of(FilterType type, String heading,
            Function<SelectColumnList, WhereStatement<AppointmentImpl>> getFilter,
            Consumer<AppointmentModel> initializeNew) {
        return of(type, heading, "", getFilter, initializeNew);
    }

    /**
     * Creates a new appointment filter to show all appointments.
     *
     * @return An appointment filter to show all appointments.
     */
    public static AppointmentFilter all() {
        return AppointmentFilter.of(FilterType.ALL,
                // heading
                ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_ALLAPPOINTMENTS),
                // predicate
                (m) -> true,
                // sqlFilterExpr
                null,
                // initializeNew
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
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
        return AppointmentFilter.of(FilterType.ALL, Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId,
                // sqlFilterExpr
                (SelectColumnList t) -> IntegerComparisonStatement.columnEquals(t.findFirst(DbColumn.CONTACT), customerId,
                        (AppointmentModel m) -> m.getCustomer().getPrimaryKey()),
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
        return byCustomer(customer.getPrimaryKey(),
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_ALLAPPOINTMENTSFORCUST, customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
        return AppointmentFilter.of(FilterType.ALL, Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getUser().getPrimaryKey() == userId,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ?", DbName.APPOINTMENT, DbName.USER_ID),
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
        return byUser(user.getPrimaryKey(), (user.getPrimaryKey() == Scheduler.getCurrentUser().getPrimaryKey())
                ? ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_ALLMYAPPOINTMENTS)
                : ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_ALLAPPOINTMENTSFORUSER, user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show all appointments for the current user.
     *
     * @return An appointment filter to show all appointments for the current user.
     */
    public static AppointmentFilter allMyItems() {
        return byUser(Scheduler.getCurrentUser().getPrimaryKey(),
                ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_ALLMYAPPOINTMENTS),
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
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
        return AppointmentFilter.of(FilterType.ALL, Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` = ?", DbName.APPOINTMENT, DbName.CUSTOMER_ID, DbName.APPOINTMENT, DbName.USER_ID),
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_ALLAPPOINTMENTSFORBOTH, customer.getName(), user.getUserName()),
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_PAST
                : ((date.compareTo(LocalDate.now().minusDays(1L)) == 0) ? FilterType.PAST : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` < ?", DbName.APPOINTMENT, DbName.END),
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
        return beforeDate(date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_APPOINTMENTSBEFOREDATE, Objects.requireNonNull(date)));
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_PAST
                : ((date.compareTo(LocalDate.now().minusDays(1L)) == 0) ? FilterType.PAST : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` < ?", DbName.APPOINTMENT, DbName.CUSTOMER_ID, DbName.APPOINTMENT, DbName.END),
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
        return byCustomerBeforeDate(customer.getPrimaryKey(), date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_APPOINTMENTSBEFOREDATEFORCUST, Objects.requireNonNull(date), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_PAST
                : ((date.compareTo(LocalDate.now().minusDays(1L)) == 0) ? FilterType.PAST : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` < ?", DbName.APPOINTMENT, DbName.USER_ID, DbName.APPOINTMENT, DbName.END),
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
        return byCustomerBeforeDate(user.getPrimaryKey(), date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_APPOINTMENTSBEFOREDATEFORUSER, Objects.requireNonNull(date), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for the current user that have ended before a specified date.
     *
     * @param date The exclusive end date.
     * @return An appointment filter to show appointments for the current user that have ended before a specified date.
     */
    public static AppointmentFilter myBeforeDate(LocalDate date) {
        return byCustomerBeforeDate(Scheduler.getCurrentUser().getPrimaryKey(), date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYAPPOINTMENTSBEFOREDATE,
                        Objects.requireNonNull(date)),
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_PAST
                : ((date.compareTo(LocalDate.now().minusDays(1L)) == 0) ? FilterType.PAST : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getEnd().compareTo(e) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` < ?", DbName.APPOINTMENT, DbName.USER_ID, DbName.APPOINTMENT, DbName.END),
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_APPOINTMENTSBEFOREDATEFORBOTH, Objects.requireNonNull(date), customer.getName(),
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_FUTURE
                : ((date.compareTo(LocalDate.now().plusDays(1L)) == 0) ? FilterType.FUTURE : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.END),
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
        return onOrAfterDate(date, ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_APPOINTMENTSAFTERDATE, Objects.requireNonNull(date)));
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_FUTURE
                : ((date.compareTo(LocalDate.now().plusDays(1L)) == 0) ? FilterType.FUTURE : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.CUSTOMER_ID, DbName.APPOINTMENT, DbName.END),
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
        return byCustomerOnOrAfterDate(customer.getPrimaryKey(), date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_APPOINTMENTSAFTERDATEFORCUST,
                        Objects.requireNonNull(date), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_FUTURE
                : ((date.compareTo(LocalDate.now().plusDays(1L)) == 0) ? FilterType.FUTURE : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.USER_ID, DbName.APPOINTMENT, DbName.END),
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
        return byUserOnOrAfterDate(user.getPrimaryKey(), date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_APPOINTMENTSAFTERDATEFORUSER,
                        Objects.requireNonNull(date), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for the current user that end on or after a specified date.
     *
     * @param date The inclusive start date.
     * @return An appointment filter to show appointments for the current user that end on or after a specified date.
     */
    public static AppointmentFilter myOnOrAfterDate(LocalDate date) {
        return byUserOnOrAfterDate(Scheduler.getCurrentUser().getPrimaryKey(), date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYAPPOINTMENTSONORAFTERDATE,
                        Objects.requireNonNull(date)),
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
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
        return AppointmentFilter.of((date.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT_AND_FUTURE
                : ((date.compareTo(LocalDate.now().plusDays(1L)) == 0) ? FilterType.FUTURE : FilterType.CUSTOM), Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == userId && t.getStart().compareTo(d) < 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.USER_ID, DbName.APPOINTMENT, DbName.END),
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_APPOINTMENTSAFTERDATEFORBOTH,
                        Objects.requireNonNull(date), customer.getName(), user.getUserName()),
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
        return AppointmentFilter.of((start.compareTo(end) == 0 && start.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT : FilterType.CUSTOM,
                Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getStart().compareTo(e) < 0 && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` < ? AND `%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.START, DbName.APPOINTMENT, DbName.END),
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
        return range(start, end, ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_APPOINTMENTSBETWEENDATES, Objects.requireNonNull(start), Objects.requireNonNull(end)));
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
        return AppointmentFilter.of((start.compareTo(end) == 0 && start.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT : FilterType.CUSTOM,
                Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getStart().compareTo(e) < 0 && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` < ? AND `%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.CUSTOMER_ID, DbName.APPOINTMENT, DbName.START, DbName.APPOINTMENT, DbName.END),
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
        return AppointmentFilter.of((start.compareTo(end) == 0 && start.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT : FilterType.CUSTOM,
                Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getUser().getPrimaryKey() == userId && t.getStart().compareTo(e) < 0 && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` < ? AND `%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.USER_ID, DbName.APPOINTMENT, DbName.START, DbName.APPOINTMENT, DbName.END),
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
        return AppointmentFilter.of((start.compareTo(end) == 0 && start.compareTo(LocalDate.now()) == 0) ? FilterType.CURRENT : FilterType.CUSTOM,
                Objects.requireNonNull(heading),
                // predicate
                (t) -> t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId && t.getStart().compareTo(e) < 0
                && t.getEnd().compareTo(s) >= 0,
                // sqlFilterExpr
                String.format("`%s`.`%s` = ? AND `%s`.`%s` = ? AND `%s`.`%s` < ? AND `%s`.`%s` >= ?", DbName.APPOINTMENT, DbName.CUSTOMER_ID, DbName.APPOINTMENT, DbName.USER_ID, DbName.APPOINTMENT, DbName.START, DbName.APPOINTMENT, DbName.END),
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
        return range(date, date, ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_APPOINTMENTSONDATE, Objects.requireNonNull(date)));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose range occurs a specific date ranges.
     *
     * @param customer The customer to match.
     * @param date The target date.
     * @return An appointment filter to show appointments for a specific customer whose range occurs a specific date ranges.
     */
    public static AppointmentFilter byCustomerOn(CustomerImpl customer, LocalDate date) {
        String heading = ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_APPOINTMENTSONDATEFORCUST, Objects.requireNonNull(date), customer.getName());
        return byCustomerWithin(customer.getPrimaryKey(), date, date, heading, (m) -> {
            m.setCustomer(new CustomerModel(customer));
            m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
        return byUserWithin(user.getPrimaryKey(), date, date, ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_APPOINTMENTSONDATEFORUSER, Objects.requireNonNull(date), user.getUserName()),
                (m) -> m.setUser(new UserModel(user)));
    }

    /**
     * Creates a new appointment filter to show appointments for the current user whose range occurs a specific date ranges.
     *
     * @param date The target date.
     * @return An appointment filter to show appointments for the current user whose range occurs a specific date ranges.
     */
    public static AppointmentFilter myOn(LocalDate date) {
        return byUserWithin(Scheduler.getCurrentUser().getPrimaryKey(), date, date,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYAPPOINTMENTSONDATE,
                        Objects.requireNonNull(date)),
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
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
        return byCustomerWithin(customer.getPrimaryKey(), start, end,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_APPOINTMENTSBETWEENDATESFORCUST,
                        Objects.requireNonNull(start), Objects.requireNonNull(end), customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
        return byUserWithin(user.getPrimaryKey(), start, end,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_APPOINTMENTSBETWEENDATESFORUSER,
                        Objects.requireNonNull(start), Objects.requireNonNull(end), user.getUserName()),
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
        return byUserWithin(Scheduler.getCurrentUser().getPrimaryKey(), start, end,
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYAPPOINTMENTSBETWEENDATES,
                        Objects.requireNonNull(start), Objects.requireNonNull(end)),
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
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
        return range(date, date, ResourceBundleLoader.getResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_CURRENTAPPOINTMENTS));
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_CURRENTFORCUSTOMER,
                        customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_CURRENTFORUSER,
                        user.getUserName()),
                (m) -> {
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_CURRENTFORBOTH,
                        customer.getName(), user.getUserName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
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
        return byUserWithin(Scheduler.getCurrentUser().getPrimaryKey(), date, date,
                ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYCURRENT),
                (m) -> {
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments whose end date is on after the current date.
     *
     * @return An appointment filter to show appointments whose end date is on after the current date.
     */
    public static AppointmentFilter currentAndFuture() {
        return onOrAfterDate(LocalDate.now(), ResourceBundleLoader.getResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_ALLCURRENTANDFUTURE));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose end date is on after the current date.
     *
     * @param customer The customer to match.
     * @return An appointment filter to show appointments for a specific customer whose end date is on after the current date.
     */
    public static AppointmentFilter byCustomerCurrentAndFuture(CustomerImpl customer) {
        return byCustomerOnOrAfterDate(customer.getPrimaryKey(), LocalDate.now(),
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_CURRENTANDFUTUREFORCUST,
                        customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose end date is on after the current date.
     *
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific user whose end date is on after the current date.
     */
    public static AppointmentFilter byUserCurrentAndFuture(UserImpl user) {
        return byUserOnOrAfterDate(user.getPrimaryKey(), LocalDate.now(), (user.getPrimaryKey() == Scheduler.getCurrentUser().getPrimaryKey())
                ? ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYCURRENTANDFUTURE)
                : ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_CURRENTANDFUTUREFORUSER,
                        user.getUserName()),
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_CURRENTANDFUTUREFORBOTH, customer.getName(), user.getUserName()),
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
        return byUserOnOrAfterDate(Scheduler.getCurrentUser().getPrimaryKey(), LocalDate.now(),
                ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYCURRENTANDFUTURE),
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
    }

    /**
     * Creates a new appointment filter to show appointments whose end date is before the current date.
     *
     * @return An appointment filter to show appointments whose end date is before the current date.
     */
    public static AppointmentFilter past() {
        return beforeDate(LocalDate.now(), ResourceBundleLoader.getResourceString(ManageAppointments.class,
                ManageAppointments.RESOURCEKEY_PASTAPPOINTMENTS));
    }

    /**
     * Creates a new appointment filter to show appointments for a specific customer whose end date is before the current date.
     *
     * @param customer The customer to match.
     * @return An appointment filter to show appointments for a specific customer whose end date is before the current date.
     */
    public static AppointmentFilter byCustomerPast(CustomerImpl customer) {
        return byCustomerBeforeDate(customer.getPrimaryKey(), LocalDate.now(),
                ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_PASTAPPOINTMENTSFORCUSTOMER,
                        customer.getName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(Scheduler.getCurrentUser()));
                });
    }

    /**
     * Creates a new appointment filter to show appointments for a specific user whose end date is before the current date.
     *
     * @param user The user to match.
     * @return An appointment filter to show appointments for a specific user whose end date is before the current date.
     */
    public static AppointmentFilter byUserPast(UserImpl user) {
        return byUserBeforeDate(user.getPrimaryKey(), LocalDate.now(), (user.getPrimaryKey() == Scheduler.getCurrentUser().getPrimaryKey())
                ? ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYPASTAPPOINTMENTS)
                : ResourceBundleLoader.formatResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_PASTAPPOINTMENTSFORUSER,
                        user.getUserName()),
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
                ResourceBundleLoader.formatResourceString(ManageAppointments.class,
                        ManageAppointments.RESOURCEKEY_PASTAPPOINTMENTSFORBOTH, customer.getName(), user.getUserName()),
                (m) -> {
                    m.setCustomer(new CustomerModel(customer));
                    m.setUser(new UserModel(user));
                });
    }

    public static AppointmentFilter myPast() {
        return byUserOnOrAfterDate(Scheduler.getCurrentUser().getPrimaryKey(), LocalDate.now(),
                ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_MYPASTAPPOINTMENTS),
                (m) -> m.setUser(new UserModel(Scheduler.getCurrentUser())));
    }

    FilterType getType();

    void initializeNew(AppointmentModel model);

    @Override
    public default DataObjectImpl.Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
        return AppointmentImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return ResourceBundleLoader.getResourceString(ManageAppointments.class, ManageAppointments.RESOURCEKEY_LOADINGAPPOINTMENTS);
    }

    public enum FilterType {
        CURRENT,
        FUTURE,
        CURRENT_AND_FUTURE,
        PAST,
        CURRENT_AND_PAST,
        ALL,
        CUSTOM
    }

}
