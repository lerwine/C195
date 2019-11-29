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
import static model.db.DataRow.selectFromDbById;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import utils.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(Country.COLNAME_COUNTRYID)
@TableName("country")
public class Country extends DataRow {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    private final static HashMap<Integer, Country> LOOKUP_CACHE = new HashMap<>();
    
    //<editor-fold defaultstate="collapsed" desc="name">
    
    private String name;
    
    public static final String PROP_NAME = "name";
    
    public static final String COLNAME_COUNTRY = "country";
    
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
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public Country() {
        super();
        name = "";
    }
    
    public Country(String name) {
        super();
        this.name = (name == null) ? "" : name;
    }
    
    public Country (ResultSet rs) throws SQLException {
        super(rs);
        name = rs.getString(COLNAME_COUNTRY);
        if (rs.wasNull())
            name = "";
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<Country> getById(Connection connection, int id, boolean includeCache) throws SQLException {
        if (includeCache && LOOKUP_CACHE.containsKey(id))
            return Optional.of(LOOKUP_CACHE.get(id));
        
        return selectFromDbById(connection, (Class<Country>)Country.class, (Function<ResultSet, Country>)(ResultSet rs) -> {
            Country r;
            try {
                r = new Country(rs);
                if (LOOKUP_CACHE.containsKey(id))
                    LOOKUP_CACHE.remove(id);
                LOOKUP_CACHE.put(id, r);
            } catch (SQLException ex) {
                Logger.getLogger(Country.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return r;
        }, id);
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        String oldName = name;
        name = rs.getString(COLNAME_COUNTRY);
        if (rs.wasNull())
            name = "";
        if (!LOOKUP_CACHE.containsKey(getPrimaryKey()))
            LOOKUP_CACHE.put(getPrimaryKey(), this);
        firePropertyChange(PROP_NAME, oldName, name);
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { COLNAME_COUNTRY };
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            if (fieldNames[index].equals(COLNAME_COUNTRY)) {
                ps.setString(index + 1, name);
                break;
            }
        }
    }
    
    //</editor-fold>
}
