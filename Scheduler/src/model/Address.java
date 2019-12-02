package model;

/**
 *
 * @author Leonard T. Erwine
 */
public interface Address extends Record {
    String getAddress1();
    String getAddress2();
    City getCity();
    String getPostalCode();
    String getPhone();
}
