package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import scheduler.App;
import static scheduler.dao.CustomerImpl.COLNAME_ACTIVE;
import static scheduler.dao.CityImpl.COLNAME_COUNTRYID;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.ItemModel;
import scheduler.view.address.AddressReferenceModel;
import scheduler.view.city.CityReferenceModel;
import scheduler.view.country.CountryReferenceModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.ManageCustomers;

/**
 *
 * @author lerwi
 */
public interface CustomerFilter extends ModelFilter<CustomerImpl, CustomerModel> {

    @Override
    public default DataObjectImpl.Factory<CustomerImpl, ? extends ItemModel<CustomerImpl>> getFactory() {
        return CustomerImpl.getFactory();
    }

    @Override
    public default String getLoadingMessage() {
        return App.getResourceString(App.RESOURCEKEY_LOADINGCUSTOMERS);
    }

    @Override
    public default String getSubHeading() {
        return "";
    }

    public static CustomerFilter all() {
        return new CustomerFilter() {
            @Override
            public String getHeading() {
                return App.getResourceString(App.RESOURCEKEY_ALLCUSTOMERS);
            }

            @Override
            public String getSqlFilterExpr() {
                return "";
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
            private final String heading = App.getResourceString((isActive) ? App.RESOURCEKEY_ACTIVECUSTOMERS : App.RESOURCEKEY_INACTIVECUSTOMERS);

            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSqlFilterExpr() {
                return String.format("`%s` = ?", COLNAME_ACTIVE);
            }

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
    
    public static CustomerFilter byCountry(Country country) {
        return new CustomerFilter() {
            private final int id = country.getPrimaryKey();
            private final String heading = ResourceBundleLoader.getResourceString(ManageCustomers.class, ManageCustomers.RESOURCEKEY_CUSTOMERSBYCOUNTRY);
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
            public String getSqlFilterExpr() {
                return String.format("`%s` = ?", COLNAME_COUNTRYID);
            }

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
    
}
