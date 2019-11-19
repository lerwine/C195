/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import model.db.Column;
import model.db.Table;
import utils.DataHelper;

/**
 *
 * @author Leonard T. Erwine
 */
@Table(name = Country.DB_TABLE_NAME, pk = Country.DB_COL_COUNTRYID)
public class Country extends Record {
    private static final DataHelper<Country> dataHelper = new DataHelper<>(Country.class);
    /**
     * Name of associated data table.
     */
    public static final String DB_TABLE_NAME = "country";
    
    /**
     * Defines the name of the property that contains the country record identity value.
     */
    public static final String DB_COL_COUNTRYID = "countryId";
    
    //<editor-fold defaultstate="collapsed" desc="name">
    
    /**
     * Defines the name of the database column that contains the country record identity value.
     */
    public static final String DB_COL_COUNTRY = "country";
    
    /**
     * Defines the name of the property that contains the country name.
     */
    public static final String PROP_NAME = "name";
    
    @Column(DB_COL_COUNTRY)
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String name = "";
    
    /**
     * Gets the country name.
     * @return
     */
    public final String getName() { return name; }
    
    /**
     * Sets country name.
     * @param value The name of the country.
     * @throws Exception If null, empty or only whitespace.
     */
    public final void setName(String value) throws Exception {
        String n = (name == null) ? "" : name;
        String oldValue = name;
        if (oldValue.equals(n))
            return;
        name = n;
        firePropertyChange(PROP_NAME, oldValue, n);
    }
    
    //</editor-fold>
    
    public Country() { super(); }
}
