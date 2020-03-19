package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import scheduler.view.address.AddressModel;

public class AddressImpl extends DataObjectImpl implements Address, AddressColumns {

    private static final String BASE_SELECT_SQL = String.format("SELECT %1$s.`%2$s` AS `%2$s`, %1$s.`%3$s` AS `%3$s`, %1$s.`%4$s` AS `%4$s`,"
            + " %1$s.`%5$s` AS `%5$s`, %6$s.`%7$s` AS `%7$s`, %6$s.`%8$s` AS `%8$s`, %9$s.`%10$s` AS `%10$s`, %1$s.`%11$s` AS `%11$s`,"
            + " %1$s.`%12$s` AS `%12$s`, %1$s.`%13$s` AS `%13$s`, %1$s.`%14$s` AS `%14$s`, %1$s.`%15$s` AS `%15$s`, %1$s.`%16$s` AS `%16$s`"
            + " FROM `%17$s` %1$s"
            + " LEFT JOIN `%18$s` %6$s ON %1$s.`%5$s`=%6$s.`%5$s`"
            + " LEFT JOIN `%19$s` %9$s ON %6$s.`%8$s`=%9$s.`%8$s`", TABLEALIAS_ADDRESS, COLNAME_ADDRESSID, COLNAME_ADDRESS,
            COLNAME_ADDRESS2, COLNAME_CITYID, TABLEALIAS_CITY, COLNAME_CITY, COLNAME_COUNTRYID, TABLEALIAS_COUNTRY,
            COLNAME_COUNTRY, COLNAME_POSTALCODE, COLNAME_PHONE, COLNAME_CREATEDATE,
            COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY,
            TABLENAME_ADDRESS, TABLENAME_CITY, TABLENAME_COUNTRY);
    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String address1;
    private String address2;
    private DataObjectReference<CityImpl, City> city;
    private String postalCode;
    private String phone;

    /**
     * Initializes a {@link scheduler.util.Values#ROWSTATE_NEW} address object.
     */
    public AddressImpl() {
        address1 = "";
        address2 = "";
        city = DataObjectReference.of(null);
        postalCode = "";
        phone = "";
    }

    @Override
    public String getAddress1() {
        return address1;
    }

    /**
     * Set the value of address1
     *
     * @param value new value of address1
     */
    public void setAddress1(String value) {
        address1 = (value == null) ? "" : value;
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    /**
     * Set the value of address2
     *
     * @param value new value of address2
     */
    public void setAddress2(String value) {
        address2 = (value == null) ? "" : value;
    }

    @Override
    public DataObjectReference<CityImpl, City> getCityReference() {
        return city;
    }

    @Override
    public City getCity() {
        return city.getPartial();
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public void setCity(City city) {
        this.city = DataObjectReference.of(city);
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Set the value of postalCode
     *
     * @param value new value of postalCode
     */
    public void setPostalCode(String value) {
        postalCode = (value == null) ? "" : value;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    /**
     * Set the value of phone
     *
     * @param value new value of phone
     */
    public void setPhone(String value) {
        phone = (value == null) ? "" : value;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<AddressImpl, AddressModel> {

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected AddressImpl fromResultSet(ResultSet resultSet) throws SQLException {
            AddressImpl r = new AddressImpl();
            initializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseSelectQuery() {
            return BASE_SELECT_SQL;
        }

        @Override
        public Class<? extends AddressImpl> getDaoClass() {
            return AddressImpl.class;
        }

        @Override
        public String getTableName() {
            return TABLENAME_ADDRESS;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_ADDRESSID;
        }

        @Override
        protected List<String> getExtendedColNames() {
            return Arrays.asList(COLNAME_ADDRESS, COLNAME_ADDRESS2, COLNAME_CITYID, COLNAME_POSTALCODE, COLNAME_PHONE);
        }

        @Override
        protected void setSaveStatementValues(AddressImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getAddress1());
            ps.setString(2, dao.getAddress2());
            ps.setInt(3, dao.getCity().getPrimaryKey());
            ps.setString(4, dao.getPostalCode());
            ps.setString(5, dao.getPhone());
        }

        @Override
        protected void onInitializeDao(AddressImpl target, ResultSet resultSet) throws SQLException {
            target.address1 = resultSet.getString(COLNAME_ADDRESS);
            if (resultSet.wasNull()) {
                target.address1 = "";
            }
            target.address2 = resultSet.getString(COLNAME_ADDRESS2);
            if (resultSet.wasNull()) {
                target.address2 = "";
            }
            int cityId = resultSet.getInt(COLNAME_CITYID);
            if (resultSet.wasNull()) {
                target.city = null;
            } else {
                String cityName = resultSet.getString(COLNAME_CITY);
                if (resultSet.wasNull()) {
                    cityName = "";
                }
                int countryId = resultSet.getInt(COLNAME_COUNTRYID);
                if (resultSet.wasNull()) {
                    target.city = DataObjectReference.of(City.of(cityId, cityName, null));
                } else {
                    String countryName = resultSet.getString(COLNAME_COUNTRY);
                    target.city = DataObjectReference.of(City.of(cityId, cityName,
                            DataObjectReference.of(Country.of(countryId, resultSet.wasNull() ? "" : countryName))));
                }
            }
            target.postalCode = resultSet.getString(COLNAME_POSTALCODE);
            if (resultSet.wasNull()) {
                target.postalCode = "";
            }
            target.phone = resultSet.getString(COLNAME_PHONE);
            if (resultSet.wasNull()) {
                target.phone = "";
            }
        }

        @Override
        public ModelFilter<AddressImpl, AddressModel> getAllItemsFilter() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ModelFilter<AddressImpl, AddressModel> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getDeleteDependencyMessage(AddressImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSaveConflictMessage(AddressImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
