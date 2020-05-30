package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import scheduler.Scheduler;
import static scheduler.Scheduler.getCurrentUser;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.ICustomerDAO;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.IntColumnValueFilter;
import scheduler.dao.filter.LogicalFilter;
import scheduler.dao.filter.LogicalOperator;
import scheduler.dao.filter.TimestampColumnValueFilter;
import scheduler.dao.schema.DbColumn;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.User;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.UserItem;
import scheduler.util.DB;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.ModelFilter;
import static scheduler.view.appointment.ManageAppointmentsResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AppointmentModelFilter extends ModelFilter<AppointmentDAO, AppointmentModel, AppointmentFilter> {

    static AppointmentModelFilter of(String headingText, AppointmentFilter daoFilter, Predicate<AppointmentModel> predicate) {
        return new AppointmentModelFilter() {
            @Override
            public String getHeadingText() {
                return headingText;
            }

            @Override
            public AppointmentFilter getDaoFilter() {
                return daoFilter;
            }

            @Override
            public boolean test(AppointmentModel t) {
                return predicate.test(t);
            }

        };
    }

    public static AppointmentModelFilter all() {
        return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_ALLAPPOINTMENTS),
                AppointmentFilter.of(DaoFilterExpression.empty()),
                (t) -> true
        );
    }

    /**
     * Creates an appointment model filter for a date range.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @return An appointment model filter for appointments that occur on or after the specified {@code start} and before the specified {@code end}.
     */
    public static AppointmentModelFilter of(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        LocalDateTime endDateTime;
        if (null != start) {
            LocalDateTime startDateTime = start.atStartOfDay();
            if (start.equals(today)) {
                if (null == end) {
                    return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_ALLCURRENTANDFUTURE),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)),
                            (t) -> t.getEnd().compareTo(startDateTime) > 0
                    );
                }
                if (end.equals(today.plusDays(1L))) {
                    return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_ALLCURRENTAPPOINTMENTS),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                                    DB.toUtcTimestamp(end.atStartOfDay()))),
                            (t) -> t.getEnd().compareTo(startDateTime) > 0 && t.getStart().compareTo(startDateTime) <= 0
                    );
                }
            } else if (null == end) {
                return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSONORAFTERDATE), start),
                        AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)),
                        (t) -> t.getEnd().compareTo(startDateTime) > 0
                );
            }
            endDateTime = end.atStartOfDay();
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBETWEENDATES), start, end),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                            DB.toUtcTimestamp(endDateTime))),
                    (t) -> t.getEnd().compareTo(startDateTime) > 0 && t.getStart().compareTo(endDateTime) < 0
            );
        }
        if (null == end) {
            return all();
        }
        endDateTime = end.atStartOfDay();
        if (end.equals(today)) {
            return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_PASTAPPOINTMENTS),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))),
                    (t) -> t.getEnd().compareTo(endDateTime) <= 0
            );
        }
        if (end.equals(today.plusDays(1L))) {
            return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTANDPASTAPPOINTMENTS),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))),
                    (t) -> t.getEnd().compareTo(endDateTime) <= 0
            );
        }
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBEFOREDATE), start),
                AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))),
                (t) -> t.getEnd().compareTo(endDateTime) <= 0
        );
    }

    /**
     * Creates an appointment model filter for a date range for a specified customer.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @param customer The customer element.
     * @return An appointment model filter for appointments that occur on or after the specified {@code start} and before the specified {@code end}.
     */
    public static AppointmentModelFilter of(LocalDate start, LocalDate end, Customer customer) {
        if (null == customer || !ModelHelper.existsInDatabase(customer)) {
            return of(start, end);
        }
        final int pk = customer.getPrimaryKey();
        final LocalDate today = LocalDate.now();
        LocalDateTime endDateTime;
        if (null != start) {
            LocalDateTime startDateTime = start.atStartOfDay();
            if (start.equals(today)) {
                if (null == end) {
                    return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTANDFUTUREFORCUST),
                            customer.getName()),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                    .and(AppointmentFilter.expressionOf(customer))),
                            (t) -> {
                                CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                                return null != c && c.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0;
                            }
                    );
                }
                if (end.equals(today.plusDays(1L))) {
                    return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTFORCUSTOMER),
                            customer.getName()),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                                    DB.toUtcTimestamp(end.atStartOfDay()))
                                    .and(AppointmentFilter.expressionOf(customer))),
                            (t) -> {
                                CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                                return null != c && c.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0
                                && t.getStart().compareTo(startDateTime) <= 0;
                            }
                    );
                }
            } else if (null == end) {
                return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSONORAFTERFORCUST),
                        start, customer.getName()),
                        AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                .and(AppointmentFilter.expressionOf(customer))),
                        (t) -> {
                            CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                            return null != c && c.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0;
                        }
                );
            }
            endDateTime = end.atStartOfDay();
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBETWEENDATESFORCUST),
                    start, end, customer.getName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                            DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(customer))),
                    (t) -> {
                        CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                        return null != c && c.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0
                        && t.getStart().compareTo(endDateTime) < 0;
                    }
            );
        }
        if (null == end) {
            return of(customer);
        }
        endDateTime = end.atStartOfDay();
        if (end.equals(today)) {
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_PASTAPPOINTMENTSFORCUSTOMER),
                    customer.getName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(customer))),
                    (t) -> {
                        CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                        return null != c && c.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                    }
            );
        }
        if (end.equals(today.plusDays(1L))) {
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTANDPASTFORCUSTOMER),
                    customer.getName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(customer))),
                    (t) -> {
                        CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                        return null != c && c.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                    }
            );
        }
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBEFOREDATEFORCUST),
                start, customer.getName()),
                AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                        .and(AppointmentFilter.expressionOf(customer))),
                (t) -> {
                    CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                    return null != c && c.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                }
        );
    }

    /**
     * Creates an appointment model filter for a date range for a specified customer.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @return An appointment model filter for appointments that occur on or after the specified {@code start} and before the specified {@code end}.
     */
    public static AppointmentModelFilter ofMy(LocalDate start, LocalDate end) {
        UserDAO user = Scheduler.getCurrentUser();
        final int pk = user.getPrimaryKey();
        LocalDate today = LocalDate.now();
        LocalDateTime endDateTime;
        if (null != start) {
            LocalDateTime startDateTime = start.atStartOfDay();
            if (start.equals(today)) {
                if (null == end) {
                    return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYCURRENTANDFUTURE),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                    .and(AppointmentFilter.expressionOf(user))),
                            (t) -> {
                                UserItem<? extends IUserDAO> model = t.getUser();
                                return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0;
                            }
                    );
                }
                if (end.equals(today.plusDays(1L))) {
                    return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYCURRENT),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                                    DB.toUtcTimestamp(end.atStartOfDay()))
                                    .and(AppointmentFilter.expressionOf(user))),
                            (t) -> {
                                UserItem<? extends IUserDAO> model = t.getUser();
                                return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0
                                && t.getStart().compareTo(startDateTime) <= 0;
                            }
                    );
                }
            } else if (null == end) {
                return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYAPPOINTMENTSONORAFTERDATE),
                        start),
                        AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                .and(AppointmentFilter.expressionOf(user))),
                        (t) -> {
                            UserItem<? extends IUserDAO> model = t.getUser();
                            return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0;
                        }
                );
            }
            endDateTime = end.atStartOfDay();
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYAPPOINTMENTSBETWEENDATES),
                    start, end),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                            DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(user))),
                    (t) -> {
                        UserItem<? extends IUserDAO> model = t.getUser();
                        return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0
                        && t.getStart().compareTo(endDateTime) < 0;
                    }
            );
        }
        if (null == end) {
            return all();
        }
        endDateTime = end.atStartOfDay();
        if (end.equals(today)) {
            return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYPASTAPPOINTMENTS),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(user))),
                    (t) -> {
                        UserItem<? extends IUserDAO> model = t.getUser();
                        return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                    }
            );
        }
        if (end.equals(today.plusDays(1L))) {
            return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYCURRENTANDPAST),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(user))),
                    (t) -> {
                        UserItem<? extends IUserDAO> model = t.getUser();
                        return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                    }
            );
        }
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYAPPOINTMENTSBEFOREDATE),
                start),
                AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                        .and(AppointmentFilter.expressionOf(user))),
                (t) -> {
                    UserItem<? extends IUserDAO> model = t.getUser();
                    return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                }
        );
    }

    /**
     * Creates an appointment model filter for a date range for a specified customer.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @param user The user element.
     * @return An appointment model filter for appointments that occur on or after the specified {@code start} and before the specified {@code end}.
     */
    public static AppointmentModelFilter of(LocalDate start, LocalDate end, User user) {
        if (null == user || !ModelHelper.existsInDatabase(user)) {
            return of(start, end);
        }
        if (user.getPrimaryKey() == Scheduler.getCurrentUser().getPrimaryKey()) {
            return ofMy(start, end);
        }
        final int pk = user.getPrimaryKey();
        LocalDate today = LocalDate.now();
        LocalDateTime endDateTime;
        if (null != start) {
            LocalDateTime startDateTime = start.atStartOfDay();
            if (start.equals(today)) {
                if (null == end) {
                    return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTANDFUTUREFORUSER),
                            user.getUserName()),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                    .and(AppointmentFilter.expressionOf(user))),
                            (t) -> {
                                UserItem<? extends IUserDAO> model = t.getUser();
                                return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0;
                            }
                    );
                }
                if (end.equals(today.plusDays(1L))) {
                    return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTFORUSER),
                            user.getUserName()),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                                    DB.toUtcTimestamp(end.atStartOfDay()))
                                    .and(AppointmentFilter.expressionOf(user))),
                            (t) -> {
                                UserItem<? extends IUserDAO> model = t.getUser();
                                return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0
                                && t.getStart().compareTo(startDateTime) <= 0;
                            }
                    );
                }
            } else if (null == end) {
                return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSAFTERDATEFORUSER),
                        start, user.getUserName()),
                        AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                .and(AppointmentFilter.expressionOf(user))),
                        (t) -> {
                            UserItem<? extends IUserDAO> model = t.getUser();
                            return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0;
                        }
                );
            }
            endDateTime = end.atStartOfDay();
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBETWEENDATESFORUSER),
                    start, end, user.getUserName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                            DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(user))),
                    (t) -> {
                        UserItem<? extends IUserDAO> model = t.getUser();
                        return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(startDateTime) > 0
                        && t.getStart().compareTo(endDateTime) < 0;
                    }
            );
        }
        if (null == end) {
            return of(user);
        }
        endDateTime = end.atStartOfDay();
        if (end.equals(today)) {
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_PASTAPPOINTMENTSFORUSER),
                    user.getUserName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(user))),
                    (t) -> {
                        UserItem<? extends IUserDAO> model = t.getUser();
                        return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                    }
            );
        }
        if (end.equals(today.plusDays(1L))) {
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTANDPASTFORUSER),
                    user.getUserName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(user))),
                    (t) -> {
                        UserItem<? extends IUserDAO> model = t.getUser();
                        return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                    }
            );
        }
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBEFOREDATEFORUSER),
                start, user.getUserName()),
                AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                        .and(AppointmentFilter.expressionOf(user))),
                (t) -> {
                    UserItem<? extends IUserDAO> model = t.getUser();
                    return null != model && model.getPrimaryKey() == pk && t.getEnd().compareTo(endDateTime) <= 0;
                }
        );
    }

    public static AppointmentModelFilter of(Customer customer) {
        if (null == customer || !ModelHelper.existsInDatabase(customer)) {
            return all();
        }
        final int pk = customer.getPrimaryKey();
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_ALLAPPOINTMENTSFORCUST),
                customer.getName()),
                AppointmentFilter.of(AppointmentFilter.expressionOf(customer)),
                (t) -> {
                    CustomerItem<? extends ICustomerDAO> model = t.getCustomer();
                    return null != model && model.getPrimaryKey() == pk;
                }
        );
    }

    public static AppointmentModelFilter of(User user) {
        if (null == user || !ModelHelper.existsInDatabase(user)) {
            return all();
        }
        final int pk = user.getPrimaryKey();
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_ALLAPPOINTMENTSFORUSER),
                user.getUserName()),
                AppointmentFilter.of(AppointmentFilter.expressionOf(user)),
                (t) -> {
                    UserItem<? extends IUserDAO> model = t.getUser();
                    return null != model && model.getPrimaryKey() == pk;
                }
        );
    }

    public static AppointmentModelFilter of(Customer customer, User user) {
        if (null == customer || !ModelHelper.existsInDatabase(customer)) {
            return of(user);
        }
        if (null == user || !ModelHelper.existsInDatabase(user)) {
            return of(customer);
        }
        final int cPk = customer.getPrimaryKey();
        final int uPk = user.getPrimaryKey();
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_ALLAPPOINTMENTSFORBOTH),
                customer.getName()),
                AppointmentFilter.of(LogicalFilter.of(LogicalOperator.OR, AppointmentFilter.expressionOf(customer),
                        AppointmentFilter.expressionOf(user))),
                (t) -> {
                    CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                    if (null != c && c.getPrimaryKey() == cPk) {
                        return true;
                    }
                    UserItem<? extends IUserDAO> u = t.getUser();
                    return null != u && u.getPrimaryKey() == uPk;
                }
        );

    }

    /**
     * Creates an appointment model filter for a date range for a specified customer.
     *
     * @param start The inclusive start date.
     * @param end The exclusive end date.
     * @param customer The customer element.
     * @param user
     * @return An appointment model filter for appointments that occur on or after the specified {@code start} and before the specified {@code end}.
     */
    public static AppointmentModelFilter of(LocalDate start, LocalDate end, Customer customer, User user) {
        if (null == customer || !ModelHelper.existsInDatabase(customer)) {
            return of(start, end, user);
        }
        if (null == user || !ModelHelper.existsInDatabase(user)) {
            return of(start, end, customer);
        }
        final int cPk = customer.getPrimaryKey();
        final int uPk = user.getPrimaryKey();
        final LocalDate today = LocalDate.now();
        LocalDateTime endDateTime;
        if (null != start) {
            LocalDateTime startDateTime = start.atStartOfDay();
            if (start.equals(today)) {
                if (null == end) {
                    return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTANDFUTUREFORBOTH),
                            customer.getName(), user.getUserName()),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                    .and(AppointmentFilter.expressionOf(customer).or(AppointmentFilter.expressionOf(user)))),
                            (t) -> {
                                if (t.getEnd().compareTo(startDateTime) > 0) {
                                    CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                                    if (null != c && c.getPrimaryKey() == cPk) {
                                        return true;
                                    }
                                    UserItem<? extends IUserDAO> u = t.getUser();
                                    return null != u && u.getPrimaryKey() == uPk;
                                }
                                return false;
                            }
                    );
                }
                if (end.equals(today.plusDays(1L))) {
                    return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTFORBOTH),
                            customer.getName(), user.getUserName()),
                            AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                                    DB.toUtcTimestamp(end.atStartOfDay()))
                                    .and(AppointmentFilter.expressionOf(customer).or(AppointmentFilter.expressionOf(user)))),
                            (t) -> {
                                if (t.getEnd().compareTo(startDateTime) > 0 && t.getStart().compareTo(startDateTime) <= 0) {
                                    CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                                    if (null != c && c.getPrimaryKey() == cPk) {
                                        return true;
                                    }
                                    UserItem<? extends IUserDAO> u = t.getUser();
                                    return null != u && u.getPrimaryKey() == uPk;
                                }
                                return false;
                            }
                    );
                }
            } else if (null == end) {
                return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSONORAFTERFORBOTH),
                        start, customer.getName(), user.getUserName()),
                        AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime), null)
                                .and(AppointmentFilter.expressionOf(customer).or(AppointmentFilter.expressionOf(user)))),
                        (t) -> {
                            if (t.getEnd().compareTo(startDateTime) > 0) {
                                CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                                if (null != c && c.getPrimaryKey() == cPk) {
                                    return true;
                                }
                                UserItem<? extends IUserDAO> u = t.getUser();
                                return null != u && u.getPrimaryKey() == uPk;
                            }
                            return false;
                        }
                );
            }
            endDateTime = end.atStartOfDay();
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBETWEENDATESFORBOTH),
                    start, end, customer.getName(), user.getUserName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(startDateTime),
                            DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(customer).or(AppointmentFilter.expressionOf(user)))),
                    (t) -> {
                        if (t.getEnd().compareTo(startDateTime) > 0 && t.getStart().compareTo(endDateTime) < 0) {
                            CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                            if (null != c && c.getPrimaryKey() == cPk) {
                                return true;
                            }
                            UserItem<? extends IUserDAO> u = t.getUser();
                            return null != u && u.getPrimaryKey() == uPk;
                        }
                        return false;
                    }
            );
        }
        if (null == end) {
            return of(customer, user);
        }
        endDateTime = end.atStartOfDay();
        if (end.equals(today)) {
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_PASTAPPOINTMENTSFORBOTH),
                    customer.getName(), user.getUserName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(customer).or(AppointmentFilter.expressionOf(user)))),
                    (t) -> {
                        if (t.getEnd().compareTo(endDateTime) <= 0) {
                            CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                            if (null != c && c.getPrimaryKey() == cPk) {
                                return true;
                            }
                            UserItem<? extends IUserDAO> u = t.getUser();
                            return null != u && u.getPrimaryKey() == uPk;
                        }
                        return false;
                    }
            );
        }
        if (end.equals(today.plusDays(1L))) {
            return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_CURRENTANDPASTFORBOTH),
                    customer.getName(), user.getUserName()),
                    AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                            .and(AppointmentFilter.expressionOf(customer).or(AppointmentFilter.expressionOf(user)))),
                    (t) -> {
                        if (t.getEnd().compareTo(endDateTime) <= 0) {
                            CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                            if (null != c && c.getPrimaryKey() == cPk) {
                                return true;
                            }
                            UserItem<? extends IUserDAO> u = t.getUser();
                            return null != u && u.getPrimaryKey() == uPk;
                        }
                        return false;
                    }
            );
        }
        return AppointmentModelFilter.of(String.format(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_APPOINTMENTSBEFOREDATEFORBOTH),
                start, customer.getName(), user.getUserName()),
                AppointmentFilter.of(AppointmentFilter.expressionOf(null, DB.toUtcTimestamp(endDateTime))
                        .and(AppointmentFilter.expressionOf(customer).or(AppointmentFilter.expressionOf(user)))),
                (t) -> {
                    if (t.getEnd().compareTo(endDateTime) <= 0) {
                        CustomerItem<? extends ICustomerDAO> c = t.getCustomer();
                        if (null != c && c.getPrimaryKey() == cPk) {
                            return true;
                        }
                        UserItem<? extends IUserDAO> u = t.getUser();
                        return null != u && u.getPrimaryKey() == uPk;
                    }
                    return false;
                }
        );
    }

    public static AppointmentModelFilter myCurrentAndFuture() {
        return AppointmentModelFilter.of(ResourceBundleHelper.getResourceString(ManageAppointments.class, RESOURCEKEY_MYCURRENTANDFUTURE),
                AppointmentFilter.of(
                        LogicalFilter.of(LogicalOperator.AND,
                                IntColumnValueFilter.of(DbColumn.USER_ID, ComparisonOperator.EQUALS, getCurrentUser().getPrimaryKey(),
                                        (t) -> t.getPrimaryKey()),
                                TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.GREATER_THAN, LocalDate.now(),
                                        (t) -> t.getEnd())
                        )
                ),
                (t) -> t.getEnd().compareTo(LocalDate.now().atStartOfDay()) > 0
        );
    }
}
