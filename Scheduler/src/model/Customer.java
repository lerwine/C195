package model;

/**
 *
 * @author Leonard T. Erwine
 */
@Deprecated
public interface Customer extends Record {
    String getName();
    Address getAddress();
    boolean isActive();
}
