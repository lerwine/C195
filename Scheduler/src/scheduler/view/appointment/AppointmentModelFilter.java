package scheduler.view.appointment;

import java.time.LocalDate;
import java.util.function.Predicate;
import static scheduler.Scheduler.getCurrentUser;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.IntColumnValueFilter;
import scheduler.dao.filter.LogicalFilter;
import scheduler.dao.filter.LogicalOperator;
import scheduler.dao.filter.TimestampColumnValueFilter;
import scheduler.dao.schema.DbColumn;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.ModelFilter;
import static scheduler.view.appointment.ManageAppointmentsResourceKeys.RESOURCEKEY_ALLAPPOINTMENTS;
import static scheduler.view.appointment.ManageAppointmentsResourceKeys.RESOURCEKEY_MYCURRENTANDFUTURE;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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
        return AppointmentModelFilter.of(
                ResourceBundleLoader.getResourceString(ManageAppointments.class, RESOURCEKEY_ALLAPPOINTMENTS),
                AppointmentFilter.of(DaoFilterExpression.empty()),
                (t) -> true
        );
    }

    public static AppointmentModelFilter myCurrentAndFuture() {
        return AppointmentModelFilter.of(
                ResourceBundleLoader.getResourceString(ManageAppointments.class, RESOURCEKEY_MYCURRENTANDFUTURE),
                AppointmentFilter.of(
                        LogicalFilter.of(LogicalOperator.AND,
                                IntColumnValueFilter.of(DbColumn.CONTACT, ComparisonOperator.EQUALS, getCurrentUser().getPrimaryKey(),
                                        (t) -> t.getPrimaryKey()),
                                TimestampColumnValueFilter.of(DbColumn.END, ComparisonOperator.GREATER_THAN, LocalDate.now(),
                                        (t) -> t.getEnd())
                        )
                ),
                (t) -> t.getEnd().compareTo(LocalDate.now().atStartOfDay()) > 0
        );
    }
}
