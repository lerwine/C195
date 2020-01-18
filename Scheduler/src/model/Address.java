package model;

/**
 *
 * @author Leonard T. Erwine
 */
@Deprecated
public interface Address extends Record {
    String getAddress1();
    String getAddress2();
    City getCity();
    String getPostalCode();
    String getPhone();
}
