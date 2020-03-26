package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppResources;
import scheduler.dao.dml.deprecated.WhereStatement;
import scheduler.dao.schema.DbName;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.ItemModel;
import scheduler.view.address.AddressReferenceModel;
import scheduler.view.city.CityReferenceModel;
import scheduler.view.country.CountryReferenceModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.ManageCustomers;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface CustomerFilter extends ModelListingFilter<CustomerImpl, CustomerModel> {

    public static CustomerFilter all() {
        return new CustomerFilter() {
            @Override
            public String getHeading() {
                return AppResources.getResourceString(AppResources.RESOURCEKEY_ALLCUSTOMERS);
            }

            @Override
            public WhereStatement<CustomerImpl, CustomerModel> getWhereStatement() {
                return null;
            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                return index;
            }

            @Override
            public boolean test(CustomerModel t) {
                return true;
            }

            @Override
            public ArrayList<CustomerImpl> get(Connection connection) throws SQLException {
                return CustomerImpl.getFactory().getAll(connection);
            }

        };
    }

    public static CustomerFilter byStatus(boolean isActive) {
        return new CustomerFilter() {
            private final String heading = AppResources.getResourceString((isActive) ? AppResources.RESOURCEKEY_ACTIVECUSTOMERS : AppResources.RESOURCEKEY_INACTIVECUSTOMERS);

            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public WhereStatement<CustomerImpl, CustomerModel> getWhereStatement() {
                // TODO: Implement this
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

//            @Override
//            public String getSqlFilterExpr() {
//                return String.format("`%s`.`%s` = ?", DbName.CUSTOMER, DbName.ACTIVE);
//            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                ps.setBoolean(index++, isActive);
                return index;
            }

            @Override
            public boolean test(CustomerModel t) {
                return t.isActive() == isActive;
            }

        };
    }

    public static CustomerFilter byAddress(Address address, boolean isActive) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static CustomerFilter byAddress(Address address) {
        return new CustomerFilter() {
            private final int id = address.getPrimaryKey();
            private final String heading = ResourceBundleLoader.getResourceString(ManageCustomers.class,
                    ManageCustomers.RESOURCEKEY_CUSTOMERSBYADDRESS);
            private final String subHeading;

            {
                String h;
                try {
                    h = Address.toString(address);
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(CustomerFilter.class.getName()).log(Level.SEVERE, null, ex);
                    h = "";
                }
                subHeading = h;
            }

            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSubHeading() {
                return subHeading;
            }

            @Override
            public WhereStatement<CustomerImpl, CustomerModel> getWhereStatement() {
                // TODO: Implement this
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

//            @Override
//            public String getSqlFilterExpr() {
//                return String.format("`%s`.`%s` = ?", DbName.CUSTOMER, DbName.CITY_ID);
//            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index++, id);
                return index;
            }

            @Override
            public boolean test(CustomerModel t) {
                AddressReferenceModel<? extends Address> addr = t.getAddress();
                return null != addr && addr.getPrimaryKey() == id;
            }

        };
    }

    public static CustomerFilter byCity(City country, boolean isActive) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static CustomerFilter byCity(City city) {
        return new CustomerFilter() {
            private final int id = city.getPrimaryKey();
            private final String heading = ResourceBundleLoader.getResourceString(ManageCustomers.class, ManageCustomers.RESOURCEKEY_CUSTOMERSBYCITY);
            private final String subHeading = city.getName();

            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSubHeading() {
                return subHeading;
            }

            @Override
            public WhereStatement<CustomerImpl, CustomerModel> getWhereStatement() {
                // TODO: Implement this
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

//            @Override
//            public String getSqlFilterExpr() {
//                return String.format("`%s`.`%s` = ?", DbName.CUSTOMER, DbName.CITY_ID);
//            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index++, id);
                return index;
            }

            @Override
            public boolean test(CustomerModel t) {
                AddressReferenceModel<? extends Address> addr = t.getAddress();
                if (null != addr) {
                    CityReferenceModel<? extends City> city = addr.getCity();
                    return null != city && city.getPrimaryKey() == id;
                }
                return false;
            }

        };
    }

    public static CustomerFilter byCountry(Country country, boolean isActive) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static CustomerFilter byCountry(Country country) {
        return new CustomerFilter() {
            private final int id = country.getPrimaryKey();
            private final String heading = ResourceBundleLoader.getResourceString(ManageCustomers.class,
                    ManageCustomers.RESOURCEKEY_CUSTOMERSBYCOUNTRY);
            private final String subHeading = country.getName();

            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSubHeading() {
                return subHeading;
            }

            @Override
            public WhereStatement<CustomerImpl, CustomerModel> getWhereStatement() {
                // TODO: Implement this
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

//            @Override
//            public String getSqlFilterExpr() {
//                return String.format("`%s`.`%s` = ?", DbName.CUSTOMER, DbName.COUNTRY_ID);
//            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index++, id);
                return index;
            }

            @Override
            public boolean test(CustomerModel t) {
                AddressReferenceModel<? extends Address> addr = t.getAddress();
                if (null != addr) {
                    CityReferenceModel<? extends City> city = addr.getCity();
                    if (null != city) {
                        CountryReferenceModel<? extends Country> country = city.getCountry();
                        return null != country && country.getPrimaryKey() == id;
                    }
                }
                return false;
            }

        };
    }

    @Override
    public default DataObjectImpl.Factory_obsolete<CustomerImpl, CustomerModel> getFactory() {
        return CustomerImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCUSTOMERS);
    }

    @Override
    public default String getSubHeading() {
        return "";
    }

}
