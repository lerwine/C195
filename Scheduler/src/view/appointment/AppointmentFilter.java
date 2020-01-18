package view.appointment;

import expressions.OptionalDataObjectProperty;
import expressions.OptionalValueProperty;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import scheduler.dao.Appointment;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.Customer;
import scheduler.dao.DataObjectFilter;
import scheduler.dao.User;
import util.DB;
import view.customer.AppointmentCustomer;
import view.user.AppointmentUser;

/**
 *
 * @author erwinel
 */
public final class AppointmentFilter implements view.ModelFilter<AppointmentImpl, AppointmentModel> {
    
    //<editor-fold defaultstate="collapsed" desc="startRange property">
    
    private final OptionalValueProperty<LocalDateTime> startRange;
    
    public Optional<LocalDateTime> getStartRange() { return startRange.get(); }
    
    public void setStartRange(Optional<LocalDateTime> value) { startRange.set(value); }
    
    public OptionalValueProperty<LocalDateTime> startRangeProperty() { return startRange; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="startExclusive property">
    
    private final BooleanProperty startExclusive;

    public boolean isStartExclusive() { return startExclusive.get(); }

    public void setStartExclusive(boolean value) { startExclusive.set(value); }

    public BooleanProperty startExclusiveProperty() { return startExclusive; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="endRange property">
    
    private final OptionalValueProperty<LocalDateTime> endRange;
    
    public Optional<LocalDateTime> getEndRange() { return endRange.get(); }
    
    public void setEndRange(Optional<LocalDateTime> value) { endRange.set(value); }
    
    public OptionalValueProperty<LocalDateTime> endRangeProperty() { return endRange; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="endInclusive property">
    
    private final SimpleBooleanProperty endInclusive;

    public boolean isEndInclusive() { return endInclusive.get(); }

    public void setEndInclusive(boolean value) { endInclusive.set(value); }

    public BooleanProperty endInclusiveProperty() { return endInclusive; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="customer property">
    
    private final OptionalDataObjectProperty<Customer> customer;
    
    public Optional<Customer> getCustomer() { return customer.get(); }
    
    public void setCustomer(Optional<Customer> value) { customer.set(value); }

    public OptionalDataObjectProperty<Customer> customerProperty() { return customer; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="user property">
    
    private final OptionalDataObjectProperty<User> user;
    
    public Optional<User> getUser() { return user.get(); }
    
    public void setUser(Optional<User> value) { user.set(value); }
    
    public OptionalDataObjectProperty<User> userProperty() { return user; }
    
    //</editor-fold>
    
    public AppointmentFilter() { this(null); }
    
    public AppointmentFilter(AppointmentFilter other) {
        if (null == other) {
            startRange = new OptionalValueProperty<>();
            startExclusive = new SimpleBooleanProperty(false);
            endRange = new OptionalValueProperty<>();
            endInclusive = new SimpleBooleanProperty(false);
            customer = new OptionalDataObjectProperty<>();
            user = new OptionalDataObjectProperty<>();
        } else {
            startRange = new OptionalValueProperty<>(other.getStartRange());
            startExclusive = new SimpleBooleanProperty(other.isStartExclusive());
            endRange = new OptionalValueProperty<>(other.getEndRange());
            endInclusive = new SimpleBooleanProperty(other.isEndInclusive());
            customer = new OptionalDataObjectProperty<>(other.getCustomer());
            user = new OptionalDataObjectProperty<>(other.getUser());
        }
    }
    
    @Override
    public DataObjectFilter<AppointmentImpl> createClone() { return new AppointmentFilter(this); }

    @Override
    public boolean test(AppointmentModel t) {
        return t != null && startRange.fromPresence((s) ->
                endRange.fromPresence((e) -> {
                    if (s.compareTo(e) > 0)
                        return ((startExclusive.get()) ? t.getEnd().compareTo(s) < 0 : t.getEnd().compareTo(s) <= 0) ||
                                ((endInclusive.get()) ? t.getStart().compareTo(e) >= 0 : t.getStart().compareTo(e) > 0);
                    return ((startExclusive.get()) ? t.getEnd().compareTo(s) > 0 : t.getEnd().compareTo(s) >= 0) &&
                            ((endInclusive.get()) ? t.getStart().compareTo(e) <= 0 : t.getStart().compareTo(e) < 0);
                }, () ->
                        (startExclusive.get()) ? t.getEnd().compareTo(s) > 0 : t.getEnd().compareTo(s) >= 0
                ),
            () ->
                endRange.fromPresence((e) ->
                        (endInclusive.get()) ? t.getStart().compareTo(e) <= 0 : t.getStart().compareTo(e) < 0,
                    () ->
                        true
                )
        ) && customer.fromPresence((c) -> {
                AppointmentCustomer<?> x = t.getCustomer();
                return x != null && x.getDataObject().getPrimaryKey() == c.getPrimaryKey() && user.fromPresence((u) -> {
                    AppointmentUser o = t.getUser();
                    return o != null && u.getPrimaryKey() == o.getDataObject().getPrimaryKey();
                }, () -> true);
            },
            () ->
                user.fromPresence((u) -> {
                    AppointmentUser o = t.getUser();
                    return o != null && u.getPrimaryKey() == o.getDataObject().getPrimaryKey();
                }, () -> true)
        );
    }
    
    @Override
    public int setWhereParameters(PreparedStatement ps, int startIndex) throws SQLException {
        startIndex = startRange.fromPresentOrDefault(startIndex, (t, i) -> {
            ps.setTimestamp(i, DB.toUtcTimestamp(t));
            return i + 1;
        }, startIndex);
        startIndex = endRange.fromPresentOrDefault(startIndex, (t, i) -> {
            ps.setTimestamp(i, DB.toUtcTimestamp(t));
            return i + 1;
        }, startIndex);
        startIndex = customer.fromPresentOrDefault(startIndex, (t, i) -> {
            ps.setInt(i, t.getPrimaryKey());
            return i + 1;
        }, startIndex);
        return user.fromPresentOrDefault(startIndex, (t, i) -> {
            ps.setInt(i, t.getPrimaryKey());
            return i + 1;
        }, startIndex);
    }
    
    @Override
    public String toWhereClause() {
        return startRange.fromPresence((s) ->
                endRange.fromPresence((e) -> {
                    if (s.compareTo(e) > 0) {
                        if (customer.isPresent()) {
                            if (user.isPresent())
                                return String.format(" WHERE (`%s`%s%% OR `%s`%s%%) AND `%s`=%% AND `%s`=%%", Appointment.COLNAME_END,
                                        (startExclusive.get()) ? "<" : "<=", Appointment.COLNAME_START, (endInclusive.get()) ? ">=" : ">",
                                        Appointment.COLNAME_CUSTOMERID, Appointment.COLNAME_USERID);

                            return String.format(" WHERE (`%s`%s%% OR `%s`%s%%) AND `%s`=%%", Appointment.COLNAME_END, (startExclusive.get()) ? "<" : "<=",
                                    Appointment.COLNAME_START, (endInclusive.get()) ? ">=" : ">", Appointment.COLNAME_CUSTOMERID);
                        }

                        if (user.isPresent())
                            return String.format(" WHERE (`%s`%s%% OR `%s`%s%%) AND `%s`=%%", Appointment.COLNAME_END, (startExclusive.get()) ? "<" : "<=",
                                    Appointment.COLNAME_START, (endInclusive.get()) ? ">=" : ">", Appointment.COLNAME_USERID);

                        return String.format(" WHERE `%s`%s%% OR `%s`%s%%", Appointment.COLNAME_END, (startExclusive.get()) ? "<" : "<=",
                                Appointment.COLNAME_START, (endInclusive.get()) ? ">=" : ">");
                    }
                    // s.compareTo(e) <= 0
                    if (customer.isPresent()) {
                        if (user.isPresent())
                            return String.format(" WHERE `%s`%s%% AND `%s`%s%% AND `%s`=%% AND `%s`=%%", Appointment.COLNAME_END,
                                    (startExclusive.get()) ? ">" : ">=", Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<",
                                    Appointment.COLNAME_CUSTOMERID, Appointment.COLNAME_USERID);

                        return String.format(" WHERE `%s`%s%% AND `%s`%s%% AND `%s`=%%", Appointment.COLNAME_END, (startExclusive.get()) ? ">" : ">=",
                                Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<", Appointment.COLNAME_CUSTOMERID);
                    }

                    if (user.isPresent())
                        return String.format(" WHERE `%s`%s%% AND `%s`%s%% AND `%s`=%%", Appointment.COLNAME_END, (startExclusive.get()) ? ">" : ">=",
                                Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<", Appointment.COLNAME_USERID);

                    return String.format(" WHERE `%s`%s%% AND `%s`%s%%", Appointment.COLNAME_END, (startExclusive.get()) ? ">" : ">=",
                            Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<");
                },
                () -> { // startRange.isPresent() == true; endRange.isPresent() == false
                    if (customer.isPresent()) {
                        if (user.isPresent())
                            return String.format(" WHERE `%s`%s%% AND `%s`=%% AND `%s`=%%", Appointment.COLNAME_END, (startExclusive.get()) ? ">" : ">=",
                                    Appointment.COLNAME_CUSTOMERID, Appointment.COLNAME_USERID);

                        return String.format(" WHERE `%s`%s%% AND `%s`=%%", Appointment.COLNAME_END, (startExclusive.get()) ? ">" : ">=",
                                Appointment.COLNAME_CUSTOMERID);
                    }

                    if (user.isPresent())
                        return String.format(" WHERE `%s`%s%% AND `%s`=%%", Appointment.COLNAME_END, (startExclusive.get()) ? ">" : ">=",
                                Appointment.COLNAME_USERID);

                    return String.format(" WHERE `%s`%s%%", Appointment.COLNAME_END, (startExclusive.get()) ? ">" : ">=");
                }),
            () -> { // startRange.isPresent() == false
                if (endRange.isPresent()) {
                    if (customer.isPresent()) {
                        if (user.isPresent())
                            return String.format(" WHERE `%s`%s%% AND `%s`=%% AND `%s`=%%", Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<",
                                    Appointment.COLNAME_CUSTOMERID, Appointment.COLNAME_USERID);

                        return String.format(" WHERE `%s`%s%% AND `%s`=%%", Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<",
                                Appointment.COLNAME_CUSTOMERID);
                    }

                    if (user.isPresent())
                        return String.format(" WHERE `%s`%s%% AND `%s`=%%", Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<",
                                Appointment.COLNAME_USERID);

                    return String.format(" WHERE `%s`%s%%", Appointment.COLNAME_START, (endInclusive.get()) ? "<=" : "<");
                }

                // endRange.isPresent() == false; startRange.isPresent() == false
                
                if (customer.isPresent()) {
                    if (user.isPresent())
                        return String.format(" WHERE `%s`=%% AND `%s`=%%", Appointment.COLNAME_CUSTOMERID, Appointment.COLNAME_USERID);

                    return String.format(" WHERE `%s`=%%", Appointment.COLNAME_CUSTOMERID);
                }

                if (user.isPresent())
                    return String.format(" WHERE `%s`=%%", Appointment.COLNAME_USERID);

                return "";
            });
    }
    
}
