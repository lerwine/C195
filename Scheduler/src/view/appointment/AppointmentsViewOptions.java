package view.appointment;

import java.time.LocalDate;
import scheduler.dao.factory.AppointmentFactory;
import scheduler.filter.ModelFilter;
import view.SchedulerController;
import view.customer.AppointmentCustomer;
import view.user.AppointmentUser;

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
            @Override
            public String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return context.getResources().getString(ManageAppointments.RESOURCEKEY_MANAGEAPPOINTMENTS);
            }
            @Override
            public ModelFilter<AppointmentModel> getFilter() {
                return AppointmentFactory.userIs(user).and(AppointmentFactory.endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0)));
            }

            @Override
            public String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return String.format(context.getResources().getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORUSER), user.getUserName());
            }

        };
    }
    
    public static AppointmentsViewOptions todayAndFuture(AppointmentCustomer customer) {
        return new AppointmentsViewOptions() {
            @Override
            public String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return context.getResources().getString(ManageAppointments.RESOURCEKEY_MANAGEAPPOINTMENTS);
            }
            @Override
            public ModelFilter<AppointmentModel> getFilter() {
                return AppointmentFactory.customerIs(customer).and(AppointmentFactory.endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0)));
            }

            @Override
            public String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return String.format(context.getResources().getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORCUSTOMER), customer.getName());
            }

        };
    }
}
