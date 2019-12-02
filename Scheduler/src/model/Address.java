/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
