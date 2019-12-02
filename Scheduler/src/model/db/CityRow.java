/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import static model.db.DataRow.selectFromDbById;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(City.COLNAME_CITYID)
@TableName("city")
public class City extends DataRow {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String COLNAME_CITYID = "cityId";
    
    private final static HashMap<Integer, City> LOOKUP_CACHE = new HashMap<>();
    
    //<editor-fold defaultstate="collapsed" desc="name">
    
    private String name;
    
    public static final String PROP_NAME = "name";
    
    public static final String COLNAME_CITY = "city";
    
    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public final String getName() { return name; }
    
    /**
     * Set the value of name
     *
     * @param value new value of name
     */
    public final void setName(String value) {
        String oldValue = name;
        name = (value == null) ? "" : value;
        firePropertyChange(PROP_NAME, oldValue, name);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="countryId">
    
    private int countryId;
    
    public static final String PROP_COUNTRYID = "countryId";
    
    /**
     * Get the value of countryId
     *
     * @return the value of countryId
     */
    public final int getCountryId() { return countryId; }
    
    /**
     * Set the value of countryId
     *
     * @param value new value of countryId
     */
    public final void setCountryId(int value) {
        int oldValue = countryId;
        countryId = value;
        firePropertyChange(PROP_COUNTRYID, oldValue, countryId);
    }
    
    public Optional<Country> lookupCurrentCountry(Connection connection) throws SQLException {
        return Country.getById(connection, countryId, true);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public City() {
        super();
        name = "";
        countryId = 0;
    }
    
    public City(String name, int countryId) {
        super();
        this.name = (name == null) ? "" : name;
        this.countryId = countryId;
    }
    
    public City (ResultSet rs) throws SQLException {
        super(rs);
        name = rs.getString(COLNAME_CITY);
        if (rs.wasNull())
            name = "";
        countryId = rs.getInt(PROP_COUNTRYID);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<City> getById(Connection connection, int id, boolean includeCache) throws SQLException {
        if (includeCache && LOOKUP_CACHE.containsKey(id))
            return Optional.of(LOOKUP_CACHE.get(id));
        
        return selectFromDbById(connection, (Class<City>)City.class, (Function<ResultSet, City>)(ResultSet rs) -> {
            City r;
            try {
                r = new City(rs);
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, id);
    }
    
    public static final ObservableList<City> getByCountry(Connection connection, int countryId) throws SQLException {
        return selectFromDb(connection, (Class<City>)City.class, (Function<ResultSet, City>)(ResultSet rs) -> {
            City r;
            try {
                r = new City(rs);
                int id = r.getPrimaryKey();
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, "`" + PROP_COUNTRYID + "` = ?",
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, countryId);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        String oldName = name;
        int oldCountryId = countryId;
        name = rs.getString(COLNAME_CITY);
        if (rs.wasNull())
            name = "";
        countryId = rs.getInt(PROP_COUNTRYID);
        if (!LOOKUP_CACHE.containsKey(getPrimaryKey()))
            LOOKUP_CACHE.put(getPrimaryKey(), this);
        // Execute property change events in nested try/finally statements to ensure that all
        // events get fired, even if one of the property change listeners throws an exception.
        try { firePropertyChange(PROP_NAME, oldName, name); }
        finally { firePropertyChange(PROP_COUNTRYID, oldCountryId, countryId); }
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { COLNAME_CITY, PROP_COUNTRYID };
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            switch (fieldNames[index]) {
                case COLNAME_CITY:
                    ps.setString(index + 1, name);
                    break;
                case PROP_COUNTRYID:
                    ps.setInt(index + 1, countryId);
                    break;
            }
        }
    }
    
    //</editor-fold>
}
