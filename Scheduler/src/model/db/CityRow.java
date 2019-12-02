package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
@PrimaryKey(CityRow.COLNAME_CITYID)
@TableName("city")
public class CityRow extends DataRow {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String SQL_SELECT = "SELECT city.*, country.country FROM city" +
        " LEFT OUTER JOIN country ON city.countryId = country.countryId";
    
    public static final String COLNAME_CITYID = "cityId";
    
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
        if (value == countryId && country != null)
            return;
        int oldId = countryId;
        Country oldCountry = country;
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        try {
            Optional<CountryRow> r = CountryRow.getById(dep.getconnection(), value);
            if (r.isPresent())
                country = r.get();
            else
                throw new InvalidArgumentException("value", "No Country found that matches that ID");
        } finally { dep.close(); }
        countryId = value;
        try { firePropertyChange(PROP_COUNTRYID, oldId, countryId); }
        finally { firePropertyChange(PROP_COUNTRY, oldCountry, country); }
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="country">
    
    private model.Country country;
    
    public static final String PROP_COUNTRY = "country";
    
    /**
     * Get the value of country
     *
     * @return the value of country
     */
    public final model.Country getCountry() { return country; }
    
    /**
     * Set the value of country
     *
     * @param value new value of country
     */
    public final void setCountry(model.Country value) {
        if (value == null)
            throw new InvalidArgumentException("value", "Country cannot be null");
        if (value instanceof UserRow) {
            int rowState = ((UserRow)value).getRowState();
            if (rowState == ROWSTATE_DELETED)
                throw new InvalidArgumentException("value", "Country was deleted");
            if (rowState == ROWSTATE_NEW)
                throw new InvalidArgumentException("value", "Country was not added to the database");
        }
        
        int oldId = countryId;
        Country oldCountry = country;
        model.Country oldValue = country;
        countryId = (country = value).getPrimaryKey();
        try { firePropertyChange(PROP_COUNTRY, oldCountry, country); }
        finally { firePropertyChange(PROP_COUNTRYID, oldId, countryId); }
    }
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public CityRow() {
        super();
        name = "";
        countryId = 0;
    }
    
    public CityRow(String name, CountryRow country) {
        super();
        if (country == null)
            throw new InvalidArgumentException("country", "Country cannot be null");
        if (country.getRowState() == ROWSTATE_DELETED)
            throw new InvalidArgumentException("country", "Country was deleted");
        if (country.getRowState() == ROWSTATE_NEW)
            throw new InvalidArgumentException("country", "Country was not added to the database");
        this.name = (name == null) ? "" : name;
        countryId = (this.country = country).getPrimaryKey();
    }
    
    public CityRow (ResultSet rs) throws SQLException {
        super(rs);
        name = rs.getString(COLNAME_CITY);
        if (rs.wasNull())
            name = "";
        countryId = rs.getInt(PROP_COUNTRYID);
        country = new Country(countryId, rs.getString(CountryRow.COLNAME_COUNTRY));
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<CityRow> getById(Connection connection, int id) throws SQLException {
        return selectFirstFromDb(connection, SQL_SELECT + " WHERE cityId = ?", (Function<ResultSet, CityRow>)(ResultSet rs) -> {
            CityRow u;
            try {
                u = new CityRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CityRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CityRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, id);
            } catch (SQLException ex) {
                Logger.getLogger(CityRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<CityRow> getByCountry(Connection connection, int countryId) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE countryId = ?", (Function<ResultSet, CityRow>)(ResultSet rs) -> {
            CityRow u;
            try {
                u = new CityRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CityRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CityRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, countryId);
            } catch (SQLException ex) {
                Logger.getLogger(CityRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<CityRow> getAll(Connection connection) throws SQLException {
        return selectFromDb(connection, SQL_SELECT, (Function<ResultSet, CityRow>)(ResultSet rs) -> {
            CityRow u;
            try {
                u = new CityRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CityRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CityRow object from result set.");
            }
            return u;
        }, null);
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        String oldName = name;
        Country oldCountry = country;
        int oldCountryId = countryId;
        name = rs.getString(COLNAME_CITY);
        if (rs.wasNull())
            name = "";
        countryId = rs.getInt(PROP_COUNTRYID);
        country = new Country(countryId, rs.getString(CountryRow.COLNAME_COUNTRY));
        // Execute property change events in nested try/finally statements to ensure that all
        // events get fired, even if one of the property change listeners throws an exception.
        try { firePropertyChange(PROP_NAME, oldName, name); }
        finally {
            try { firePropertyChange(PROP_COUNTRYID, oldCountryId, countryId); }
            finally { firePropertyChange(PROP_COUNTRY, oldCountry, country); }
        }
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
    
    @Override
    protected String getSelectQuery() { return SQL_SELECT; }
    
    //</editor-fold>
    
    static class Country implements model.Country {
        private final int id;
        private final String name;

        Country(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getName() { return name; }

        @Override
        public int getPrimaryKey() { return id; }
    }
}
