package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.filter.ModelFilter;
import scheduler.filter.OrderBy;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.SqlStatementBuilder;
import scheduler.filter.ValueAccessor;
import view.ChildModel;
import view.city.CityModel;
import view.country.CityCountry;

/**
 *
 * @author erwinel
 */
public class CityFactory extends DataObjectFactory<CityImpl, CityModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_CITYID = "cityId";
    
    public static final String COLNAME_CITY = "city";
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT c.`");
        sql.append(COLNAME_CITYID).append("` AS ").append(COLNAME_CITYID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY).forEach((t) -> {
            sql.append(", c.`").append(t).append("` AS ").append(t);
        });
        baseSelectQuery = sql.append(", c.`").append(COLNAME_CITY).append("` AS ").append(COLNAME_CITY)
                .append(", c.`").append(COLNAME_COUNTRYID).append("` AS ").append(COLNAME_COUNTRYID)
                .append(", n.`").append(CountryFactory.COLNAME_COUNTRY).append("` AS ").append(CountryFactory.COLNAME_COUNTRY)
                .append("` FROM `").append(getTableName(CityImpl.class))
                .append("` c LEFT JOIN `").append(getTableName(CountryImpl.class)).append("` n ON c.`").append(COLNAME_COUNTRYID)
                .append("`=n.`").append(CountryFactory.COLNAME_COUNTRYID).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_CITY, CountryFactory.COLNAME_COUNTRY,
                    COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE,
                    COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    //<editor-fold defaultstate="collapsed" desc="Static ValueAccessor definitions">

    public static final ValueAccessor<CityModel, String> ACCESSOR_NAME = new ValueAccessor<CityModel, String>() {
        @Override
        public String get() { return COLNAME_CITY; }
        @Override
        public String apply(CityModel t) { return t.getName(); }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public static final ValueAccessor<CityModel, Integer> ACCESSOR_COUNTRY_ID = new ValueAccessor<CityModel, Integer>() {
        @Override
        public String get() { return COLNAME_COUNTRYID; }
        @Override
        public Integer apply(CityModel t) {
            CityCountry<?> c = t.getCountry();
            return (null != c && c.getDataObject().isExisting()) ? c.getDataObject().getPrimaryKey() : Integer.MIN_VALUE;
        }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    
    public static final ValueAccessor<CityModel, String> ACCESSOR_COUNTRY_NAME = new ValueAccessor<CityModel, String>() {
        @Override
        public String get() { return CountryFactory.COLNAME_COUNTRY; }
        @Override
        public String apply(CityModel t) {
            CityCountry<?> c = t.getCountry();
            return (null != c) ? c.getName() : "";
        }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Static ModelFilter definitions">

    public static ModelFilter<CityModel> nameIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_NAME, ModelFilter.COMPARATOR_STRING, value);
    }
    
    public static ModelFilter<CityModel> countryIdIs(int value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_COUNTRY_ID, ModelFilter.COMPARATOR_INTEGER, value);
    }
    
    public static ModelFilter<CityModel> countryNameIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_COUNTRY_NAME, ModelFilter.COMPARATOR_STRING, value);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    public Optional<CityImpl> findByName(Connection connection, int countryId, String value) throws Exception {
        return loadFirst(connection, countryIdIs(countryId).and(nameIs(value)));
    }
    
    public ArrayList<CityImpl> loadByCountryId(Connection connection, int countryId, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, countryIdIs(countryId), orderBy);
    }
    
    public ArrayList<CityImpl> loadByCountryName(Connection connection, String value, Iterable<OrderBy> orderBy) throws Exception {
        return load(connection, countryNameIs(value), orderBy);
    }
    
    public int countByCountry(Connection connection, int countryId) throws Exception {
        return count(connection, countryIdIs(countryId));
    }
    
    @Override
    protected CityImpl fromResultSet(ResultSet resultSet) throws SQLException { return new CityImpl(resultSet); }

    @Override
    public CityModel fromDataAccessObject(CityImpl dao) { return (dao == null) ? null : new CityModel(dao); }

    @Override
    public String getBaseQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<? extends CityImpl> getDaoClass() { return CityImpl.class; }
    
}
