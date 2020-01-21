/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import scheduler.filter.OrderBy;
import view.country.CityCountry;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectImpl.TABLENAME_COUNTRY)
@PrimaryKeyColumn(CountryImpl.COLNAME_COUNTRYID)
public class CountryImpl extends DataObjectImpl implements Country {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT `");
        Stream.of(COLNAME_COUNTRYID, COLNAME_COUNTRY, COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY).forEach((t) -> {
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
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_COUNTRY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY,
                    COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="name property">
    
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() { return name; }

    /**
     * Set the value of name.
     * @param value new value of name.
     */
    public void setName(String value) { name = (value == null) ? "" : value; }

    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} country object.
     */
    public CountryImpl() {
        super();
        name = "";
    }
    
    /**
     * Initializes a country object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public CountryImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        name = resultSet.getString(COLNAME_COUNTRY);
        if (resultSet.wasNull())
            name = "";
    }
    
    public static ArrayList<CountryImpl> loadAll(Connection connection, Iterable<OrderBy> orderBy) throws Exception {
        return loadAll(connection, getBaseSelectQuery(), orderBy, (rs) -> new CountryImpl(rs));
//        String sql = getBaseSelectQuery() + SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(),
//                () -> SelectOrderSpec.of(SelectOrderSpec.of(Country.COLNAME_COUNTRY)));
//        try (PreparedStatement ps = connection.prepareStatement(getBaseSelectQuery())) {
//            return toList(ps, (rs) -> new CountryImpl(rs));
//        }
    }
    
    public static Iterable<CountryImpl> loadAll(Connection connection) throws Exception {
        return loadAll(connection, null);
    }
    
    public static Optional<CountryImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE p.`%s` = %%", getBaseSelectQuery(), COLNAME_COUNTRYID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new CountryImpl(rs));
        }
    }

    @Override
    public synchronized void delete(Connection connection) throws Exception {
        assert CityImpl.getCount(connection, CityImpl.countryIs(CityCountry.of(this))) == 0 : "Country is associated with one or more cities.";
        super.delete(connection);
    }

}
