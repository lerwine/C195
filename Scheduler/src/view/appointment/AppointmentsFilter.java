/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.appointment;

import java.time.LocalDate;
import scheduler.App;
import scheduler.dao.AppointmentImpl;
import scheduler.filter.ModelFilter;
import view.SchedulerController;
import view.customer.AppointmentCustomer;
import view.user.AppointmentUser;

/**
 *
 * @author erwinel
 */
public interface AppointmentsFilter {

    String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context);

    default String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
        return "";
    }

    ModelFilter<AppointmentModel> getFilter();
    
    public static AppointmentsFilter todayAndFuture(AppointmentUser user) {
        return new AppointmentsFilter() {
            @Override
            public String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return context.getResources().getString(ManageAppointments.RESOURCEKEY_MANAGEAPPOINTMENTS);
            }
            @Override
            public ModelFilter<AppointmentModel> getFilter() {
                return AppointmentImpl.userIs(user).and(AppointmentImpl.endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0)));
            }

            @Override
            public String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return String.format(context.getResources().getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORUSER), user.getUserName());
            }

        };
    }
    
    public static AppointmentsFilter todayAndFuture(AppointmentCustomer customer) {
        return new AppointmentsFilter() {
            @Override
            public String getWindowTitle(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return context.getResources().getString(ManageAppointments.RESOURCEKEY_MANAGEAPPOINTMENTS);
            }
            @Override
            public ModelFilter<AppointmentModel> getFilter() {
                return AppointmentImpl.customerIs(customer).and(AppointmentImpl.endIsGreaterThan(LocalDate.now().atTime(0, 0, 0, 0)));
            }

            @Override
            public String getHeadingText(SchedulerController.ContentChangeContext<ManageAppointments> context) {
                return String.format(context.getResources().getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORCUSTOMER), customer.getName());
            }

        };
    }
}
