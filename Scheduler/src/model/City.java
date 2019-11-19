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
@Table(name = City.DB_TABLE_NAME, pk = City.DB_COL_CITYID)
public class City extends Record {
    private static final DataHelper<City> dataHelper = new DataHelper<>(City.class);
    /**
     * Name of associated data table.
     */
    public static final String DB_TABLE_NAME = "city";
    
    /**
     * Defines the name of the property that contains the city record identity value.
     */
    public static final String DB_COL_CITYID = "cityId";
    
    //<editor-fold defaultstate="collapsed" desc="name">
    
    /**
     * Defines the name of the property that contains the city record identity value.
     */
    public static final String DB_COL_CITY = "city";
    
    /**
     * Defines the name of the property that contains the city name.
     */
    public static final String PROP_NAME = "name";
    
    @Column(DB_COL_CITY)
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String name = "";
    /**
     * Gets the city name
     * @return
     */
    public final String getName() { return name; }
    /**
     * Sets the city name.
     * @param value The name of the city.
     * @throws Exception If null, empty or only whitespace.
     */
    public final void setName(String value) throws Exception {
        String n = (value == null) ? "" : value;
        String oldValue = name;
        if (oldValue.equals(n))
            return;
        name = n;
        firePropertyChange(PROP_NAME, oldValue, n);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="countryId">
    
    /**
     * Defines the name of the property that contains the country unique database id.
     */
    public static final String PROP_COUNTRYID = "countryId";
    
    @Column
    @model.db.ValueMap(model.db.MapType.INTEGER)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.INTEGER)
    private Optional<Integer> countryId = Optional.empty();
    
    /**
     * Gets the unique database id of the country.
     * @return The unique database id of the country.
     */
    public final Optional<Integer> getCountryId() { return countryId; }
    
    //</editor-fold>
    
    public City() { super(); }
}
