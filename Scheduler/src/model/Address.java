/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Optional;
import model.db.Column;
import model.db.Table;
import utils.DataHelper;

/**
 *
 * @author Leonard T. Erwine
 */
@Table(name = Address.DB_TABLE_NAME, pk = Address.DB_COL_ADDRESSID)
public class Address extends Record {
    private static final DataHelper<Address> dataHelper = new DataHelper<>(Address.class);
    
    /**
     * Name of associated data table.
     */
    public static final String DB_TABLE_NAME = "address";
    
    /**
     * The name of the primary key column.
     */
    public static final String DB_COL_ADDRESSID = "addresId";
    
    //<editor-fold defaultstate="collapsed" desc="address1">
    
    /**
     * The name of the database column that contains the first line of the address.
     */
    public static final String DB_COL_ADDRESS = "address";
    
    /**
     * Defines the name of the property that contains the first line of the address.
     */
    public static final String PROP_ADDRESS1 = "address1";
    
    @Column(DB_COL_ADDRESS)
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String address1;
    
    /**
     * Gets the first line of the address.
     * @return
     */
    public final String getAddress1() { return address1; }
    
    /**
     * Sets the first line of the address.
     * @param value The first line of the address.
     */
    public final void setAddress1(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = value;
        if (oldValue.equals(n))
            return;
        value = n;
        address1 = value;
        firePropertyChange(PROP_ADDRESS1, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="address2">
    
    /**
     * Defines the name of the property that contains the city name.
     */
    public static final String PROP_ADDRESS2 = "address2";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.NONEMPTY)
    private String address2;
    
    /**
     * Gets the city name
     * @return
     */
    public final String getAddress2() { return address2; }
    /**
     * Sets the city name.
     * @param value The name of the city.
     */
    public final void setAddress2(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = value;
        if (oldValue.equals(n))
            return;
        value = n;
        address2 = value;
        firePropertyChange(PROP_ADDRESS2, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="postalCode">
    
    /**
     * Defines the name of the property that contains the city name.
     */
    public static final String PROP_POSTALCODE = "postalCode";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.NONEMPTY)
    private String postalCode;
    
    /**
     * Gets the city name
     * @return
     */
    public final String getPostalCode() { return postalCode; }
    
    /**
     * Sets the city name.
     * @param value The name of the city.
     */
    public final void setPostalCode(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = postalCode;
        if (oldValue.equals(n))
            return;
        value = n;
        postalCode = value;
        firePropertyChange(PROP_POSTALCODE, oldValue, n);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="phone">
    
    /**
     * Defines the name of the property that contains the city name.
     */
    public static final String PROP_PHONE = "phone";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.NONEMPTY)
    private String phone;
    
    /**
     * Gets the city name
     * @return
     */
    public final String getPhone() { return phone; }
    
    /**
     * Sets the phone number.
     * @param value The phone number.
     */
    public final void setPhone(String value) {
        String n = (value == null) ? "" : value;
        String oldValue = phone;
        if (oldValue.equals(n))
            return;
        value = n;
        phone = value;
        firePropertyChange(PROP_PHONE, oldValue, n);
    }
    
    //</editor-fold>
    
    /**
     * Defines the name of the property that contains the country unique database id.
     */
    public static final String PROP_CITYID = "cityId";
    
    @Column
    @model.db.ValueMap(model.db.MapType.INTEGER)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.INTEGER)
    private Optional<Integer> cityId;
    
    /**
     * Gets the unique database id of the country.
     * @return The unique database id of the country.
     */
    public final Optional<Integer> getCityId() { return cityId; }
    
    public Address() {
        super();
        phone = postalCode = address1 = address2 = "";
        cityId = Optional.empty();
    }
}
