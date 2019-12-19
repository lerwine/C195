package model;

/**
 *
 * @author Leonard T. Erwine
 */
public interface Customer extends Record {
    String getName();
    Address getAddress();
    boolean isActive();
}
