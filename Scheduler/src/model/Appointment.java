package model;

import java.time.LocalDateTime;

/**
 *
 * @author Leonard T. Erwine
 */
public interface Appointment extends Record {
    model.Customer getCustomer();
    model.User getUser();
    String getTitle();
    String getDescription();
    String getLocation();
    String getContact();
    String getType();
    String getUrl();
    LocalDateTime getStart();
    LocalDateTime getEnd();
}
