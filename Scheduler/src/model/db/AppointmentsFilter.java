/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 *
 * @author Leonard T. Erwine
 */
public class AppointmentsFilter implements QueryFilter<AppointmentRow> {
    private final Optional<CustomerRow> customer;
    
    public Optional<CustomerRow> getCustomer() { return customer; }
    
    private final Optional<UserRow> user;
    
    public Optional<UserRow> getUser() { return user; }
    
    private final boolean currentAndFuture;
    
    public boolean isCurrentAndFuture() { return currentAndFuture; }
    
    private final Optional<LocalDate> start;
    
    public Optional<LocalDate> getStart() { return start; }
    
    private final Optional<LocalDate> end;
    
    public Optional<LocalDate> getEnd() { return end; }
    
    public AppointmentsFilter(CustomerRow customer, LocalDate start, LocalDate end) {
        currentAndFuture = false;
        this.customer = (customer == null) ? Optional.empty() : Optional.of(customer);
        this.user = Optional.empty();
        if (start == null) {
            this.start = Optional.empty();
            this.end = (end == null) ? Optional.empty() : Optional.of(end);
        } else if (end == null) {
            this.start = Optional.of(start);
            this.end = Optional.empty();
        } else if (end.compareTo(start) < 0) {
            this.start = Optional.of(end);
            this.end = Optional.of(start);
        } else {
            this.start = Optional.of(start);
            this.end = Optional.of(end);
        }
    }
    
    public AppointmentsFilter(CustomerRow customer, LocalDate date) {
        currentAndFuture = false;
        this.customer = (customer == null) ? Optional.empty() : Optional.of(customer);
        this.user = Optional.empty();
        this.start = this.end = (date == null) ? Optional.empty() : Optional.of(date);
    }
    
    public AppointmentsFilter(CustomerRow customer, boolean currentAndFuture) {
        this.currentAndFuture = currentAndFuture;
        this.customer = (customer == null) ? Optional.empty() : Optional.of(customer);
        this.user = Optional.empty();
        this.start = this.end = Optional.empty();
    }
    
    public AppointmentsFilter(UserRow user, LocalDate start, LocalDate end) {
        currentAndFuture = false;
        this.customer = Optional.empty();
        this.user = (user == null) ? Optional.empty() : Optional.of(user);
        if (start == null) {
            this.start = Optional.empty();
            this.end = (end == null) ? Optional.empty() : Optional.of(end);
        } else if (end == null) {
            this.start = Optional.of(start);
            this.end = Optional.empty();
        } else if (end.compareTo(start) < 0) {
            this.start = Optional.of(end);
            this.end = Optional.of(start);
        } else {
            this.start = Optional.of(start);
            this.end = Optional.of(end);
        }
    }
    
    public AppointmentsFilter(UserRow user, LocalDate date) {
        currentAndFuture = false;
        this.customer = Optional.empty();
        this.user = (user == null) ? Optional.empty() : Optional.of(user);
        this.start = this.end = (date == null) ? Optional.empty() : Optional.of(date);
    }
    
    public AppointmentsFilter(UserRow user, boolean currentAndFuture) {
        this.currentAndFuture = currentAndFuture;
        this.customer = Optional.empty();
        this.user = (user == null) ? Optional.empty() : Optional.of(user);
        this.start = this.end = Optional.empty();
    }
    
    public AppointmentsFilter(LocalDate start, LocalDate end) {
        currentAndFuture = false;
        this.user = Optional.empty();
        this.customer = Optional.empty();
        if (start == null) {
            this.start = Optional.empty();
            this.end = (end == null) ? Optional.empty() : Optional.of(end);
        } else if (end == null) {
            this.start = Optional.of(start);
            this.end = Optional.empty();
        } else if (end.compareTo(start) < 0) {
            this.start = Optional.of(end);
            this.end = Optional.of(start);
        } else {
            this.start = Optional.of(start);
            this.end = Optional.of(end);
        }
    }
    
    public AppointmentsFilter(LocalDate date) {
        currentAndFuture = false;
        this.user = Optional.empty();
        this.customer = Optional.empty();
        this.start = this.end = (date == null) ? Optional.empty() : Optional.of(date);
    }
    
    public AppointmentsFilter(boolean currentAndFuture) {
        this.currentAndFuture = currentAndFuture;
        this.user = Optional.empty();
        this.customer = Optional.empty();
        this.start = this.end = Optional.empty();
        
    }
    
    @Override
    public String getWindowTitle(ResourceBundle rb) {
        if (customer.isPresent())
            return String.format(rb.getString("appointmentsForCustomer"), customer.get().getName());
        if (user.isPresent())
            return String.format(rb.getString("appointmentsForUser"), user.get().getUserName());
        if (currentAndFuture)
            return rb.getString("currentAndFutureAppointments");
        if (start.isPresent()) {
            DateTimeFormatter formatter = scheduler.App.getCurrent().getFullDateFormatter();
            if (end.isPresent()) {
                if (start.get().compareTo(end.get()) == 0)
                    return String.format(rb.getString("appointmentsOnDate"), formatter.format(start.get()));
                return String.format(rb.getString("appointmentsInRange"), formatter.format(start.get()), formatter.format(end.get()));
            }
            return String.format(rb.getString("appointmentsOnOrAfter"), formatter.format(start.get()));
        }
        if (end.isPresent())
            return String.format(rb.getString("appointmentsOnOrBefore"), scheduler.App.getCurrent().getFullDateFormatter().format(end.get()));
        return rb.getString("manageAppointments");
    }

    @Override
    public String getSubHeading(ResourceBundle rb) {
        if (customer.isPresent() || user.isPresent()) {
            if (currentAndFuture)
                return rb.getString("currentAndFuture");
            if (start.isPresent()) {
                DateTimeFormatter formatter = scheduler.App.getCurrent().getFullDateFormatter();
                if (end.isPresent()) {
                    if (start.get().compareTo(end.get()) == 0)
                        return String.format(rb.getString("onDate"), formatter.format(start.get()));
                    return String.format(rb.getString("inRange"), formatter.format(start.get()), formatter.format(end.get()));
                }
                return String.format(rb.getString("onOrAfter"), formatter.format(start.get()));
            }
            if (end.isPresent())
                return String.format(rb.getString("onOrBefore"), scheduler.App.getCurrent().getFullDateFormatter().format(end.get()));
        }
        return "";
    }

    @Override
    public String getSqlQueryString() {
        HashSet<String> clauses = new HashSet<>();
        if (customer.isPresent())
            clauses.add("`address`.`customerId` = ?");
        else if (user.isPresent())
            clauses.add("`address`.`userId` = ?");
        if (currentAndFuture)
            clauses.add("CAST(`end` AS Date) >= CURRENT_DATE");
        else {
            if (start.isPresent()) {
                if (end.isPresent()) {
                    clauses.add("CAST(`end` AS Date) >= ?");
                    clauses.add("CAST(`start` AS Date) <= ?");
                } else
                    clauses.add("CAST(`start` AS Date) >= ?");
            } else if (end.isPresent())
                clauses.add("CAST(`end` AS Date) <= ?");
        }
        if (clauses.isEmpty())
            return AppointmentRow.SQL_SELECT;
        String queryString = AppointmentRow.SQL_SELECT + " WHERE " + clauses.stream().findFirst().get();
        if (clauses.size() > 1)
            return clauses.stream().skip(1).reduce(queryString, (t, u) -> {
                return t + " AND " + u;
            });
        return queryString;
    }

    @Override
    public void setStatementValues(PreparedStatement ps) throws SQLException {
        int index = 0;
        if (customer.isPresent())
            ps.setInt(index++, customer.get().getPrimaryKey());
        else if (user.isPresent())
            ps.setInt(index++, user.get().getPrimaryKey());
        if (currentAndFuture)
            return;
        if (start.isPresent())
            ps.setDate(index++, Date.valueOf(start.get()));
        if (end.isPresent())
            ps.setDate(index, Date.valueOf(end.get()));
    }
    
}
