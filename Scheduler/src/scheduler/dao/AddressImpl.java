package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import scheduler.view.address.AddressModel;

public class AddressImpl extends DataObjectImpl implements Address, AddressColumns {

    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    private static final String BASE_SELECT_SQL = String.format("SELECT a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, c.`%s` AS `%s`,"
            + " c.`%s` AS `%s`, n.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`, a.`%s` AS `%s`"
            + " FROM `%s` a"
            + " LEFT JOIN `%s` c ON a.`%s`=c.`%s`"
            + " LEFT JOIN `%s` n ON c.`%s`=n.`%s`", COLNAME_ADDRESSID, COLNAME_ADDRESSID, COLNAME_ADDRESS, COLNAME_ADDRESS, COLNAME_ADDRESS2,
            COLNAME_ADDRESS2, COLNAME_CITYID, COLNAME_CITYID, COLNAME_CITY, COLNAME_CITY, COLNAME_COUNTRYID, COLNAME_COUNTRYID, COLNAME_COUNTRY,
            COLNAME_COUNTRY, COLNAME_POSTALCODE, COLNAME_POSTALCODE, COLNAME_PHONE, COLNAME_PHONE, COLNAME_CREATEDATE, COLNAME_CREATEDATE,
            COLNAME_CREATEDBY, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_LASTUPDATEBY,
            TABLENAME_ADDRESS, TABLENAME_CITY, COLNAME_CITYID, COLNAME_CITYID, TABLENAME_COUNTRY, COLNAME_COUNTRYID, COLNAME_COUNTRYID);

    //<editor-fold defaultstate="collapsed" desc="address1 property">
    private String address1;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="address2 property">
    private String address2;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="city property">
    private DataObjectReference<CityImpl, City> city;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataObjectReference<CityImpl, City> getCity() {
        return city;
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    public void setCity(DataObjectReference<CityImpl, City> city) {
        this.city = city;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="postalCode property">
    private String postalCode;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="phone property">
    private String phone;

    /**
     * {@inheritDoc}
     */
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

    //</editor-fold>
    //</editor-fold>
    /**
     * Initializes a {@link scheduler.util.Values#ROWSTATE_NEW} address object.
     */
    public AddressImpl() {
        address1 = "";
        address2 = "";
        city = null;
        postalCode = "";
        phone = "";
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
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
