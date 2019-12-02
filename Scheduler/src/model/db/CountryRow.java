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
@PrimaryKey(CountryRow.COLNAME_COUNTRYID)
@TableName("country")
public class CountryRow extends DataRow implements model.Country {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String SQL_SELECT = "SELECT * FROM country";
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    //<editor-fold defaultstate="collapsed" desc="name">
    
    private String name;
    
    public static final String PROP_NAME = "name";
    
    public static final String COLNAME_COUNTRY = "country";
    
    /**
     * Get the value of name
     *
     * @return the value of name
     */
    @Override
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
    
    public CountryRow() {
        super();
        name = "";
    }
    
    public CountryRow(String name) {
        super();
        this.name = (name == null) ? "" : name;
    }
    
    public CountryRow (ResultSet rs) throws SQLException {
        super(rs);
        name = rs.getString(COLNAME_COUNTRY);
        if (rs.wasNull())
            name = "";
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">

    @Override
    protected String getSelectQuery() { return SQL_SELECT; }
    
    public static final Optional<CountryRow> getById(Connection connection, int id) throws SQLException {
        return selectFirstFromDb(connection, SQL_SELECT + " WHERE countryId = ?", (Function<ResultSet, CountryRow>)(ResultSet rs) -> {
            CountryRow u;
            try {
                u = new CountryRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CountryRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CountryRow object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, id);
            } catch (SQLException ex) {
                Logger.getLogger(CountryRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<CountryRow> getAll(Connection connection) throws SQLException {
        return selectFromDb(connection, SQL_SELECT, (Function<ResultSet, CountryRow>)(ResultSet rs) -> {
            CountryRow u;
            try {
                u = new CountryRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(CountryRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing CountryRow object from result set.");
            }
            return u;
        }, null);
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        String oldName = name;
        name = rs.getString(COLNAME_COUNTRY);
        if (rs.wasNull())
            name = "";
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
