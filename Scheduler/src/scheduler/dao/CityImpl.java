/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class CityImpl extends DataObjectImpl implements City {

    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    //<editor-fold defaultstate="collapsed" desc="Column names">
    public static final String COLNAME_CITYID = "cityId";

    public static final String COLNAME_CITY = "city";

    public static final String COLNAME_COUNTRYID = "countryId";

    //</editor-fold>
    private static final String BASE_SELECT_SQL = String.format("SELECT c.`%s` AS `%s`, c.`%s` AS `%s`, c.`%s` AS `%s`, n.`%s` AS `%s` FROM `%s` c"
            + " LEFT JOIN `%s` n ON c.`%s`=n.`%s`", COLNAME_CITYID, COLNAME_CITYID, COLNAME_CITY, COLNAME_CITY, COLNAME_COUNTRYID,
            COLNAME_COUNTRYID, CountryImpl.COLNAME_COUNTRY, CountryImpl.COLNAME_COUNTRY, COLNAME_CREATEDATE, COLNAME_CREATEDATE, COLNAME_CREATEDBY,
            COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY, TABLENAME_CITY, TABLENAME_COUNTRY,
            COLNAME_COUNTRYID, CountryImpl.COLNAME_COUNTRYID);

    //<editor-fold defaultstate="collapsed" desc="name property">
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param value new value of name
     */
    public void setName(String value) {
        name = (value == null) ? "" : value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="country property">
    private DataObjectReference<CountryImpl, Country> country;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataObjectReference<CountryImpl, Country> getCountry() {
        return country;
    }

    /**
     * Set the value of country
     *
     * @param country new value of country
     */
    public void setCountry(DataObjectReference<CountryImpl, Country> country) {
        this.country = country;
    }

    //</editor-fold>
    //</editor-fold>
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} city object.
     */
    public CityImpl() {
        super();
        name = "";
        country = new DataObjectReference<>();
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();
    
    public static FactoryImpl getFactory() { return FACTORY; }
    
    public static final class FactoryImpl extends DataObjectImpl.Factory<CityImpl> {

        // This is a singleton instance
        private FactoryImpl() { }
        
        //    @Override
        //    protected void onApplyChanges(CityModel model) {
        //        CityImpl dao = model.getDataObject();
        //        dao.name = model.getName();
        //        CityCountry<?> country = model.getCountry();
        //        dao.country = (null == country) ? null : country.getDataObject();
        //    }
        @Override
        protected CityImpl fromResultSet(ResultSet resultSet) throws SQLException {
            CityImpl r = new CityImpl();
            onInitializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseQuery() {
            return BASE_SELECT_SQL;
        }

        @Override
        public Class<? extends CityImpl> getDaoClass() {
            return CityImpl.class;
        }

        @Override
        public String getTableName() {
            return TABLENAME_CITY;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_CITYID;
        }

        @Override
        protected Stream<String> getExtendedColNames() {
            return Stream.of(COLNAME_CITY, COLNAME_COUNTRYID);
        }

        @Override
        protected void setStatementValues(CityImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getName());
            ps.setInt(2, dao.getCountry().getPrimaryKey());
        }

        @Override
        protected void onInitializeDao(CityImpl target, ResultSet resultSet) throws SQLException {
            target.name = resultSet.getString(CityImpl.COLNAME_CITY);
            if (resultSet.wasNull()) {
                target.name = "";
            }
            int countryId = resultSet.getInt(CityImpl.COLNAME_COUNTRYID);
            if (resultSet.wasNull()) {
                target.country = new DataObjectReference<>();
            } else {
                String countryName = resultSet.getString(CountryImpl.COLNAME_COUNTRY);
                target.country = DataObjectReference.of(Country.of(countryId, resultSet.wasNull() ? "" : countryName));
            }
        }

    }

}
