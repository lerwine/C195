package scheduler.view.appointment;

import java.time.LocalDate;
import scheduler.dao.AppointmentFactory;
import scheduler.filter.ModelFilter;
import scheduler.view.SchedulerController;
import scheduler.view.customer.AppointmentCustomer;
import scheduler.view.user.AppointmentUser;

/**
 *
 * @author erwinel
 */
public interface AppointmentsViewOptions {

    String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context);

    default String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
        return "";
    }

    ModelFilter<AppointmentModel> getFilter();
    
    public static AppointmentsViewOptions todayAndFuture(AppointmentUser user) {
        return new AppointmentsViewOptions() {
            private final ModelFilter<AppointmentModel> filter = AppointmentFactory.todayAndFuture(user.getDataObject().getPrimaryKey());
            @Override
            public String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return context.getResources().getString(ManageAppointments.RESOURCEKEY_MANAGEAPPOINTMENTS);
            }
            @Override
            public ModelFilter<AppointmentModel> getFilter() { return filter; }
            @Override
            public String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return String.format(context.getResources().getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORUSER), user.getUserName());
            }

        };
    }
    
    public static AppointmentsViewOptions todayAndFuture(AppointmentCustomer customer) {
        return new AppointmentsViewOptions() {
            private final ModelFilter<AppointmentModel> filter = AppointmentFactory.customerTodayAndFuture(customer.getDataObject().getPrimaryKey());
            @Override
            public String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return context.getResources().getString(ManageAppointments.RESOURCEKEY_MANAGEAPPOINTMENTS);
            }
            @Override
            public ModelFilter<AppointmentModel> getFilter() { return filter; }
            @Override
            public String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return String.format(context.getResources().getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORCUSTOMER), customer.getName());
            }

        };
    }
}
