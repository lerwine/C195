package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.filter.ModelFilter;
import scheduler.filter.ParameterConsumer;
import scheduler.filter.ValueAccessor;
import view.country.CountryModel;

/**
 *
 * @author erwinel
 */
public class CountryFactory extends DataObjectFactory<CountryImpl, CountryModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    public static final String COLNAME_COUNTRY = "country";
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT `");
        Stream.of(COLNAME_COUNTRYID, COLNAME_COUNTRY, COLNAME_CREATEDATE, COLNAME_CREATEDBY,
                COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY).forEach((t) -> {
            sql.append("`, `").append(t).append(t);
        });
        baseSelectQuery = sql.append("` FROM `").append(getTableName(CountryImpl.class)).append("`").toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_COUNTRY, COLNAME_LASTUPDATE,
                    COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Filter definitions">
    
    public static final ValueAccessor<CountryModel, String> ACCESSOR_NAME = new ValueAccessor<CountryModel, String>() {
        @Override
        public String get() { return COLNAME_COUNTRY; }
        @Override
        public String apply(CountryModel t) { return t.getName(); }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public static ModelFilter<CountryModel> nameIs(String value) {
        return ModelFilter.columnIsEqualTo(ACCESSOR_NAME, ModelFilter.COMPARATOR_STRING, value);
    }
    
    //</editor-fold>
    
    public Optional<CountryImpl> findByName(Connection connection, String value) throws Exception { return loadFirst(connection, nameIs(value)); }
    
    @Override
    protected CountryImpl fromResultSet(ResultSet resultSet) throws SQLException { return new CountryImpl(resultSet); }

    @Override
    public CountryModel fromDataAccessObject(CountryImpl dao) { return (dao == null) ? null : new CountryModel(dao); }

    @Override
    public String getBaseQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<? extends CountryImpl> getDaoClass() { return CountryImpl.class; }

}
