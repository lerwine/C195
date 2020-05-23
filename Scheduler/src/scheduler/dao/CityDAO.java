package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.IntColumnValueFilter;
import scheduler.dao.filter.value.IntValueFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.util.InternalException;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.city.EditCity;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.country.EditCountry;
import static scheduler.view.country.EditCountryResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLE;
import static scheduler.view.country.EditCountryResourceKeys.RESOURCEKEY_DELETEMSGSINGLE;

/**
 * Data access object for the {@code city} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.CITY)
public final class CityDAO extends DataAccessObject implements CityDbRecord {

    public static final int MAX_LENGTH_NAME = 50;

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'country' property.
     */
    public static final String PROP_COUNTRY = "country";

    public static final String PROP_PREDEFINEDCITY = "predefinedCity";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private ICountryDAO country;
    private PredefinedElement predefinedData;

    /**
     * Initializes a {@link DataRowState#NEW} city object.
     */
    public CityDAO() {
        super();
        name = "";
        country = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ICountryDAO getCountry() {
        return country;
    }

    /**
     * Get the value of predefinedData
     *
     * @return the value of predefinedData
     */
    @Override
    public PredefinedElement getPredefinedElement() {
        return predefinedData;
    }

    /**
     * Set the value of predefinedData
     *
     * @param city new value of predefinedData
     */
    public void setPredefinedElement(PredefinedElement city) {
        ICountryDAO oldCountry = country;
        String oldName = name;
        PredefinedElement oldPredefinedCity = predefinedData;
        if (null == city) {
            if (null == oldPredefinedCity) {
                return;
            }
            name = "";
        } else {
            if (null != oldPredefinedCity && city == oldPredefinedCity) {
                return;
            }
            name = PredefinedData.getCityDisplayName(city.getKey());
            country = PredefinedData.lookupCountry(city.getCountry().getLocale().getCountry());
        }
        ;
        predefinedData = city;
        firePropertyChange(PROP_NAME, oldName, name);
        firePropertyChange(PROP_PREDEFINEDCITY, oldPredefinedCity, predefinedData);
        firePropertyChange(PROP_COUNTRY, oldCountry, country);
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.country);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
    }

    /**
     * Factory implementation for {@link CityDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CityDAO> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(CityDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case CITY_NAME:
                    ps.setString(index, dao.predefinedData.getKey());
                    break;
                case CITY_COUNTRY:
                    ps.setInt(index, dao.country.getPrimaryKey());
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public CityDAO createNew() {
            return new CityDAO();
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            DmlSelectQueryBuilder builder = new DmlSelectQueryBuilder(DbTable.CITY, SchemaHelper.getTableColumns(DbTable.CITY));
            builder.join(DbColumn.CITY_COUNTRY, TableJoinType.LEFT, DbColumn.COUNTRY_ID,
                    SchemaHelper.getTableColumns(DbTable.COUNTRY, SchemaHelper::isForJoinedData));
            return builder;
        }

        @Override
        public DaoFilter<CityDAO> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
        }

        public DaoFilter<CityDAO> getByCountryFilter(int pk) {
            return DaoFilter.of(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES),
                    IntColumnValueFilter.of(DbColumn.CITY_COUNTRY, IntValueFilter.of(pk, ComparisonOperator.EQUALS),
                            (CityDAO t) -> ModelHelper.getPrimaryKey(t.getCountry())));
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CityDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                private final Country oldCountry = dao.country;

                @Override
                public void accept(PropertyChangeSupport t) {
                    if (!dao.name.equals(oldName)) {
                        t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    }
                    if (!Objects.equals(dao.country, oldCountry)) {
                        t.firePropertyChange(PROP_COUNTRY, oldCountry, dao.country);
                    }
                }
            };

            dao.setPredefinedElement(PredefinedData.getCityMap().get(rs.getString(DbColumn.CITY_NAME.toString())));
            dao.country = CountryDAO.getFactory().fromJoinedResultSet(rs);
            return propertyChanges;
        }

        ICityDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Related(rs.getInt(DbColumn.ADDRESS_CITY.toString()), rs.getString(DbColumn.CITY_NAME.toString()),
                    CountryDAO.getFactory().fromJoinedResultSet(rs));
        }

        @Override
        public Class<? extends CityDAO> getDaoClass() {
            return CityDAO.class;
        }

        @Override
        public String getDeleteDependencyMessage(CityDAO dao, Connection connection) throws SQLException {
            if (null == dao || !DataRowState.existsInDb(dao.getRowState())) {
                return "";
            }
            int count = AddressDAO.getFactory().countByCity(dao.getPrimaryKey(), connection);
            switch (count) {
                case 0:
                    return "";
                case 1:
                    return ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_DELETEMSGSINGLE);
                default:
                    return ResourceBundleHelper.formatResourceString(EditCountry.class, RESOURCEKEY_DELETEMSGMULTIPLE, count);
            }
        }

        @Override
        public void save(CityDAO dao, Connection connection, boolean force) throws SQLException {
            ICountryDAO c = ICityDAO.assertValidCity(dao).country;
//            if (c.getRowState() == DataRowState.NEW) {
//                dao.country = PredefinedCountry.save(connection, c);
//            }
            super.save(dao, connection, force);
        }

        // CURRENT: Need to re-think this - perhaps tracking primary key in the predefined objects for city and country, and maybe address as well...
        @Override
        public String getSaveDbConflictMessage(CityDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                return ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYALREADYDELETED);
            }

            ICountryDAO country = dao.getCountry();

            if (country instanceof CountryDAO && country.getRowState() != DataRowState.UNMODIFIED) {
                String msg = CountryDAO.getFactory().getSaveDbConflictMessage((CountryDAO) country, connection);
                if (!msg.isEmpty()) {
                    return msg;
                }
            }

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.CITY_ID.getDbName())
                    .append(") FROM ").append(DbTable.CITY.getDbName())
                    .append(" LEFT JOIN ").append(DbTable.COUNTRY.getDbName()).append(" ON ")
                    .append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_COUNTRY.getDbName())
                    .append("=").append(DbTable.COUNTRY.getDbName()).append(".")
                    .append(DbColumn.COUNTRY_ID.getDbName()).append(" WHERE ")
                    .append(DbTable.CITY.getDbName()).append(".").append(DbColumn.CITY_NAME.getDbName())
                    .append("=? AND ").append(DbTable.COUNTRY.getDbName()).append(".")
                    .append(DbColumn.COUNTRY_NAME.getDbName()).append("=?");
            if (dao.getRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.CITY_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();

            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, dao.getCountry().getPrimaryKey());
                ps.setString(1, dao.getPredefinedElement().getKey());
                if (dao.getRowState() != DataRowState.NEW) {
                    ps.setInt(1, dao.getPrimaryKey());
                }
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                }
            }

            if (count > 0) {
                return ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYNAMEINUSE);
            }
            // CURRENT: Get country conflict message if it is has been modified.
            return "";
        }

        public ArrayList<CityDAO> getByCountry(Connection connection, int countryId) throws SQLException {
            String sql = createDmlSelectQueryBuilder().build().append(" WHERE ")
                    .append(DbTable.CITY).append(".").append(DbColumn.CITY_COUNTRY).append("=?").toString();
            ArrayList<CityDAO> result = new ArrayList<>();
            LOG.log(Level.INFO, String.format("getByCountry", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, countryId);
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        result.add(fromResultSet(rs));
                    }
                }
            }
            return result;
        }

        public CityDAO getByResourceKey(Connection connection, String rk) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().toString()).append(" WHERE ")
                    .append(DbColumn.CITY_NAME.getDbName()).append("=?").toString();
            LOG.log(Level.INFO, String.format("getByResourceKey", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, rk);
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        return fromResultSet(rs);
                    }
                }
            }
            return null;
        }

        int countByCountry(int primaryKey, Connection connection) throws SQLException {
            String sql = "SELECT COUNT(" + DbColumn.CITY_ID.getDbName() + ") FROM " + DbTable.CITY.getDbName()
                    + " WHERE " + DbColumn.CITY_COUNTRY.getDbName() + "=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, primaryKey);
                LOG.log(Level.INFO, String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

    }

    private static final class Related extends PropertyBindable implements ICityDAO {

        private final int primaryKey;
        private final String name;
        private final ICountryDAO country;
        private final PredefinedElement predefinedCity;

        Related(int primaryKey, String resourceKey, ICountryDAO country) {
            this.primaryKey = primaryKey;
            predefinedCity = PredefinedData.getCityMap().get(resourceKey);
            name = PredefinedData.getCityDisplayName(predefinedCity.getKey());
            this.country = country;
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ICountryDAO getCountry() {
            return country;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public PredefinedElement getPredefinedElement() {
            return predefinedCity;
        }

    }

    @XmlRootElement(name = PredefinedElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public class PredefinedElement extends PredefinedData.PredefinedCity {

        public static final String ELEMENT_NAME = "city";

        private CityDAO dataAccessObject = null;

        @XmlAttribute
        private String key;

        @XmlAttribute
        private String zoneId;

        @XmlElement(name = AddressDAO.PredefinedElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
        private final List<AddressDAO.PredefinedElement> addresses;

        public String getKey() {
            return key;
        }

        public String getZoneId() {
            return zoneId;
        }

        public List<AddressDAO.PredefinedElement> getAddresses() {
            return addresses;
        }

        @Override
        public synchronized CityDAO getDataAccessObject() {
            return dataAccessObject;
        }

        public PredefinedElement() {
            addresses = new ArrayList<>();
        }

    }
}
