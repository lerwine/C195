package model;

/**
 *
 * @author Leonard T. Erwine
 */
public interface Customer extends Record {
    String getCustomerName();
    Address getAddress();
    boolean isActive();
}
