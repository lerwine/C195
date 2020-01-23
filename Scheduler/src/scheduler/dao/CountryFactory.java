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
import scheduler.view.country.CountryModel;

/**
 *
 * @author erwinel
 */
public class CountryFactory extends DataObjectFactory<CountryImpl, CountryModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Column names">
    
    public static final String COLNAME_COUNTRYID = "countryId";
    
    public static final String COLNAME_COUNTRY = "country";
    
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

    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static final String BASE_SELECT_SQL = String.format("SELECT `%s`, `%s`, `%s`, `%s`, `%s`, `%s` FROM `%s`", COLNAME_COUNTRYID, COLNAME_COUNTRY, COLNAME_CREATEDATE,
            COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, TABLENAME_COUNTRY);
    
    @Override
    public String getBaseQuery() { return BASE_SELECT_SQL; }

    //</editor-fold>
    
    @Override
    public Class<? extends CountryImpl> getDaoClass() { return CountryImpl.class; }

}
