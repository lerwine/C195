package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
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
    
    public static final String PROP_NAME = "name";
    
    public static final String COLNAME_COUNTRY = "country";
    
    private final StringProperty name;

    /**
    * Get the value of name
    *
    * @return the value of name
    */
    @Override
    public String getName() { return name.get(); }

    /**
     * Set the value of name
     *
     * @param value new value of name
     */
    public void setName(String value) { name.set(value); }

    public StringProperty nameProperty() { return name; }
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public CountryRow() {
        super();
        name = new NonNullableStringProperty();
    }
    
    public CountryRow(String name) {
        super();
        this.name = new NonNullableStringProperty(name);
    }
    
    public CountryRow (ResultSet rs) throws SQLException {
        super(rs);
        name = new NonNullableStringProperty(scheduler.util.resultStringOrDefault(rs, COLNAME_COUNTRY, ""));
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">

    @Override
    protected String getSelectQuery() { return SQL_SELECT; }
    
    /**
     * 
     * @param connection
     * @param id
     * @return
     * @throws SQLException
     */
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
    
    /**
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
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
        name.set(rs.getString(COLNAME_COUNTRY));
        if (rs.wasNull())
            name.set("");
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { COLNAME_COUNTRY };
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            if (fieldNames[index].equals(COLNAME_COUNTRY)) {
                ps.setString(index + 1, getName());
                break;
            }
        }
    }
    
    //</editor-fold>
    
    @Override
    public int hashCode() { return getPrimaryKey(); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((getRowState() != ROWSTATE_MODIFIED && getRowState() != ROWSTATE_UNMODIFIED) || obj == null || !(obj instanceof model.Country))
            return false;
        final model.Country other = (model.Country)obj;
        return (other.getRowState() == ROWSTATE_MODIFIED || other.getRowState() == ROWSTATE_UNMODIFIED) && getPrimaryKey() == other.getPrimaryKey();
    }
}
