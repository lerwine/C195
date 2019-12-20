package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
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
public class CityRow extends DataRow implements model.City {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String SQL_SELECT = "SELECT city.*, country.country FROM city" +
        " LEFT OUTER JOIN country ON city.countryId = country.countryId";
    
    public static final String COLNAME_CITYID = "cityId";
    
    //<editor-fold defaultstate="collapsed" desc="name">
    
    public static final String PROP_NAME = "name";
    
    public static final String COLNAME_CITY = "city";
    
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
    //<editor-fold defaultstate="collapsed" desc="countryId">
    
    public static final String PROP_COUNTRYID = "countryId";
    
    private ReadOnlyIntegerWrapper countryId;
    
    /**
     * Get the value of countryId
     *
     * @return the value of countryId
     */
    public final int getCountryId() { return countryId.get(); }
    
    public ReadOnlyIntegerProperty countryIdProperty() { return countryId.getReadOnlyProperty(); }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="country">
    
    public static final String PROP_COUNTRY = "country";
    
    private final ObjectProperty<model.Country> country;

    /**
    * Get the value of country
    *
    * @return the value of country
    */
    @Override
    public model.Country getCountry() { return country.get(); }

    /**
     * Set the value of country
     *
     * @param value new value of country
     */
    public void setCountry(model.Country value) { country.set(value); }

    public ObjectProperty<model.Country> countryProperty() { return country; }
    
    private final RowIdChangeListener<model.Country> countryIdChangeListener;
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public CityRow() {
        super();
        name = new NonNullableStringProperty();
        countryId =  new ReadOnlyIntegerWrapper();
        country = new SimpleObjectProperty<>();
        countryIdChangeListener = new RowIdChangeListener<>(country, countryId);
    }
    
    public CityRow(String name, CountryRow country) {
        super();
        this.name = new NonNullableStringProperty(name);
        countryId =  new ReadOnlyIntegerWrapper();
        this.country = new SimpleObjectProperty<>(country);
        countryIdChangeListener = new RowIdChangeListener<>(this.country, countryId);
    }
    
    public CityRow (ResultSet rs) throws SQLException {
        super(rs);
        name = new NonNullableStringProperty(rs.getString(COLNAME_CITY));
        if (rs.wasNull())
            name.set("");
        countryId =  new ReadOnlyIntegerWrapper();
        country = new SimpleObjectProperty<>(new Country(rs.getInt(PROP_COUNTRYID), rs.getString(CountryRow.COLNAME_COUNTRY)));
        countryIdChangeListener = new RowIdChangeListener<>(country, countryId);
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
        name.set(rs.getString(COLNAME_CITY));
        if (rs.wasNull())
            name.set("");
        country.set(new Country(rs.getInt(PROP_COUNTRYID), rs.getString(CountryRow.COLNAME_COUNTRY)));
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
                    ps.setString(index + 1, getName());
                    break;
                case PROP_COUNTRYID:
                    ps.setInt(index + 1, getCountryId());
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
