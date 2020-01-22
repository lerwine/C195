package scheduler.dao.factory;

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
import scheduler.dao.CountryImpl;
import scheduler.filter.OrderBy;
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
    
    @Deprecated
    public static ArrayList<CountryImpl> loadAll(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return loadAll(connection, getBaseSelectQuery(), orderBy, (rs) -> new CountryImpl(rs));
//        String sql = getBaseSelectQuery() + SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
//                () -> SelectOrderSpec.of(SelectOrderSpec.of(Country.COLNAME_COUNTRY)));
//        try (PreparedStatement ps = connection.prepareStatement(getBaseSelectQuery())) {
//            return toList(ps, (rs) -> new CountryImpl(rs));
//        }
    }
    
    @Deprecated
    public static Iterable<CountryImpl> loadAll(Connection connection) throws Exception {
        return loadAll(connection, null);
    }
    
    @Deprecated
    public static Optional<CountryImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s` = %%", getBaseSelectQuery(), COLNAME_COUNTRYID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new CountryImpl(rs));
        }
    }

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
