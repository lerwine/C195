package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
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
import scheduler.view.country.EditCountryResourceKeys;

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

    public static final String PROP_PREDEFINEDELEMENT = "predefinedElement";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private ICountryDAO country;
    private PredefinedCityElement predefinedElement;

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
    public PredefinedCityElement getPredefinedElement() {
        return predefinedElement;
    }

    // FIXME: When this is changed to another city, it will fail because the dataAccessObject on the predefined object is not changed.
    /**
     * Set the value of predefinedData
     *
     * @param city new value of predefinedData
     */
    public void setPredefinedElement(PredefinedCityElement city) {
        ICountryDAO oldCountry = country;
        String oldName = name;
        PredefinedCityElement oldPredefinedCity = predefinedElement;
        try {
            if (null == city) {
                if (null != oldPredefinedCity) {
                    name = "";
                }
            } else if (!Objects.equals(oldPredefinedCity, city)) {
                name = PredefinedData.getCityDisplayName(city.getKey());
                country = PredefinedData.lookupCountry(city.getCountry().getLocale().getCountry());
            }
            predefinedElement = city;
        } finally {
            firePropertyChange(PROP_NAME, oldName, name);
            firePropertyChange(PROP_COUNTRY, oldCountry, country);
            firePropertyChange(PROP_PREDEFINEDELEMENT, oldPredefinedCity, predefinedElement);
        }
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
                    ps.setString(index, dao.predefinedElement.getKey());
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
                private final PredefinedCityElement oldPredefinedElement = dao.predefinedElement;
                private final Country oldCountry = dao.country;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    t.firePropertyChange(PROP_PREDEFINEDELEMENT, oldPredefinedElement, dao.predefinedElement);
                    t.firePropertyChange(PROP_COUNTRY, oldCountry, dao.country);
                }
            };

            dao.setPredefinedElement(PredefinedData.getCityMap().get(rs.getString(DbColumn.CITY_NAME.toString())));
            dao.predefinedElement.dataAccessObject = dao;
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
                    return ResourceBundleHelper.getResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGSINGLE);
                default:
                    return ResourceBundleHelper.formatResourceString(EditCountry.class, EditCountryResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLE, count);
            }
        }

        public CityDAO getPredefinedCity(PredefinedCityElement city, Connection connection) throws SQLException {
            CountryDAO.PredefinedCountryElement c = city.getCountry();
            CountryDAO countryDao = c.getDataAccessObject();
            if (null == countryDao) {
                countryDao = CountryDAO.getFactory().getPredefinedCountry(c, connection);
            }
            if (countryDao.getRowState() != DataRowState.NEW) {
                String sql = new StringBuffer(createDmlSelectQueryBuilder().toString()).append(" WHERE ")
                        .append(DbColumn.CITY_COUNTRY).append("=? AND ")
                        .append(DbColumn.CITY_NAME).append("=?").toString();
                LOG.fine(() -> String.format("getPredefinedCity", "Executing DML statement: %s", sql));
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, countryDao.getPrimaryKey());
                    ps.setString(2, city.getKey());
                    try (ResultSet rs = ps.getResultSet()) {
                        if (rs.next()) {
                            return fromResultSet(rs);
                        }
                    }
                }
            }
            CityDAO result = new CityDAO();
            result.setPredefinedElement(city);
            result.predefinedElement.dataAccessObject = result;
            return result;
        }

        @Override
        public void save(CityDAO dao, Connection connection, boolean force) throws SQLException {
            ICountryDAO country = ICityDAO.assertValidCity(dao).country;
            if (country instanceof CountryDAO) {
                CountryDAO.getFactory().save((CountryDAO) country, connection, force);
            }
            super.save(dao, connection, force);
            dao.predefinedElement.dataAccessObject = dao;
        }

        @Override
        protected CityDAO fromResultSet(ResultSet rs) throws SQLException {
            CityDAO result = super.fromResultSet(rs);
            PredefinedCityElement pde = result.predefinedElement;
            if (null != pde) {
                if (null == pde.dataAccessObject) {
                    pde.dataAccessObject = result;
                } else {
                    result.predefinedElement = pde;
                    initializeFrom(pde.dataAccessObject, result);
                    return pde.dataAccessObject;
                }
            }
            return result;
        }

        @Override
        protected void onInitializingFrom(CityDAO target, CityDAO other) {
            String oldName = target.name;
            ICountryDAO oldCountry = target.country;
            PredefinedCityElement oldPredefinedElement = target.predefinedElement;
            target.name = other.name;
            target.predefinedElement = other.predefinedElement;
            target.firePropertyChange(PROP_NAME, oldName, target.name);
            target.firePropertyChange(PROP_COUNTRY, oldCountry, target.country);
            target.firePropertyChange(PROP_PREDEFINEDELEMENT, oldPredefinedElement, target.predefinedElement);
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
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
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
            LOG.fine(() -> String.format("getByCountry", "Executing DML statement: %s", sql));
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
            LOG.fine(() -> String.format("getByResourceKey", "Executing DML statement: %s", sql));
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
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
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
        private final PredefinedCityElement predefinedCity;

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
        public PredefinedCityElement getPredefinedElement() {
            return predefinedCity;
        }

    }

    @XmlRootElement(name = PredefinedCityElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    @XmlType(name = PredefinedCityElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PredefinedCityElement extends PredefinedData.PredefinedCity {

        public static final String ELEMENT_NAME = "city";

        @XmlTransient
        private CityDAO dataAccessObject = null;

        @XmlAttribute
        private String key;

        @XmlAttribute
        private String zoneId;

        public String getKey() {
            return key;
        }

        public String getZoneId() {
            return zoneId;
        }

        @Override
        public synchronized CityDAO getDataAccessObject() {
            return dataAccessObject;
        }

        private PredefinedCityElement() {

        }

        private static PredefinedCityElement createInstanceJAXB() {
            return new PredefinedCityElement();
        }

    }
}
