package scheduler.view.appointment;

import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
import scheduler.dao.AppointmentFactory;
import scheduler.dao.Customer;
import scheduler.dao.User;
import scheduler.filter.ModelFilter;
import scheduler.filter.ParameterConsumer;

/**
 *
 * @author erwinel
 */
public interface AppointmentsViewOptions extends ModelFilter<AppointmentModel> {

    String getWindowTitle(ResourceBundle rb);

    default String getHeadingText(ResourceBundle rb) { return ""; }

    //ModelFilter<AppointmentModel> getFilter();
    
    public static AppointmentsViewOptions of(ModelFilter<AppointmentModel> filter, Function<ResourceBundle, String> getHeadingText,
            Function<ResourceBundle, String> getTitleText) {
        return new AppointmentsViewOptions() {
            @Override
            public String getWindowTitle(ResourceBundle rb) {
                if (null == getTitleText)
                    return rb.getString(ManageAppointments.RESOURCEKEY_MANAGEAPPOINTMENTS);
                return getTitleText.apply(rb);
            }
            @Override
            public String getHeadingText(ResourceBundle rb) {
                if (null == getHeadingText)
                    return "";
                return getHeadingText.apply(rb);
            }
            @Override
            public String getColName() { return filter.getColName(); }
            @Override
            public String getOperator() { return filter.getOperator(); }
            @Override
            public void setParameterValues(ParameterConsumer consumer) throws SQLException { filter.setParameterValues(consumer); }
            @Override
            public String get() { return filter.get(); }
            @Override
            public ModelFilter<AppointmentModel> makeClone() {
                return AppointmentsViewOptions.of(filter.makeClone(), getHeadingText, getTitleText);
            }
            @Override
            public boolean isCompound() { return filter.isCompound(); }
            @Override
            public boolean isEmpty() { return filter.isEmpty(); }
            @Override
            public boolean test(AppointmentModel t) { return filter.test(t); }
        };
    }
    
    public static AppointmentsViewOptions of(ModelFilter<AppointmentModel> filter, Function<ResourceBundle, String> getHeadingText) {
        return of(filter, getHeadingText, null);
    }
    
    public static AppointmentsViewOptions all() {
        return AppointmentsViewOptions.of(ModelFilter.empty(), null);
    }
    
    public static AppointmentsViewOptions todayAndFuture() {
        return AppointmentsViewOptions.of(AppointmentFactory.todayAndFuture(), (rb) -> rb.getString(ManageAppointments.RESOURCEKEY_CURRENTANDFUTURE));
    }
    
    public static AppointmentsViewOptions todayAndFuture(User user) {
        return AppointmentsViewOptions.of(AppointmentFactory.todayAndFuture(Objects.requireNonNull(user).getPrimaryKey()), (rb) ->
                String.format(rb.getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORUSER), user.getUserName()),
                (rb) -> rb.getString(ManageAppointments.RESOURCEKEY_CURRENTANDFUTURE));
    }
    
    public static AppointmentsViewOptions todayAndFuture(Customer customer) {
        return AppointmentsViewOptions.of(AppointmentFactory.customerTodayAndFuture(Objects.requireNonNull(customer).getPrimaryKey()), (rb) ->
                String.format(rb.getString(ManageAppointments.RESOURCEKEY_APPOINTMENTSFORCUSTOMER), customer.getName()),
                (rb) -> rb.getString(ManageAppointments.RESOURCEKEY_CURRENTANDFUTURE));
    }
}
