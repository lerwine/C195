package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import scheduler.model.ModelHelper;
import scheduler.model.RelatedRecord;
import scheduler.model.db.CityRowData;
import scheduler.model.db.CountryRowData;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.predefined.PredefinedData;
import scheduler.util.InternalException;
import scheduler.util.ResourceBundleHelper;
import static scheduler.util.Values.asNonNullAndTrimmed;
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
public class CityDAO extends DataAccessObject implements CityRowData {
    
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
    private CountryRowData country;
    private PredefinedCity predefinedCity;

    /**
     * Initializes a {@link DataRowState#NEW} city object.
     */
    public CityDAO() {
        super();
        name = "";
        country = null;
    }

    @Override
    protected void reValidate(Consumer<ValidationResult> addValidation) {
        if (name.trim().isEmpty()) {
            addValidation.accept(ValidationResult.NAME_EMPTY);
        }
        if (null == country) {
            addValidation.accept(ValidationResult.NO_COUNTRY);
        } else if (RelatedRecord.validate(country) != ValidationResult.OK) {
            addValidation.accept(ValidationResult.INVALID_COUNTRY);
        } else if (ModelHelper.getRowState(country) == DataRowState.DELETED) {
            addValidation.accept(ValidationResult.COUNTRY_DELETED);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CountryRowData getCountry() {
        return country;
    }

    /**
     * Get the value of predefinedCity
     *
     * @return the value of predefinedCity
     */
    public PredefinedCity getPredefinedCity() {
        return predefinedCity;
    }

    /**
     * Set the value of predefinedCity
     *
     * @param city new value of predefinedCity
     */
    public void setPredefinedCity(PredefinedCity city) {
        CountryRowData oldCountry = country;
        String oldName = name;
        PredefinedCity oldPredefinedCity = predefinedCity;
        if (null == city) {
            if (null == oldPredefinedCity) {
                return;
            }
            name = "";
        } else {
            if (null != oldPredefinedCity && city == oldPredefinedCity) {
                return;
            }
            name = city.getName();
            country = city.getCountry();
        }

        name = city.getName();
        predefinedCity = city;
        firePropertyChange(PROP_NAME, oldName, name);
        firePropertyChange(PROP_PREDEFINEDCITY, oldPredefinedCity, predefinedCity);
        firePropertyChange(PROP_COUNTRY, oldCountry, country);
    }

    @Override
    public PredefinedCity asPredefinedData() {
        return predefinedCity;
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
     * Factory implementation for {@link scheduler.model.db.CityRowData} objects.
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
                    ps.setString(index, dao.name);
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
                private final CountryRowData oldCountry = dao.country;

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

            dao.name = asNonNullAndTrimmed(rs.getString(DbColumn.CITY_NAME.toString()));
            dao.setPredefinedCity(PredefinedData.lookupCity(dao.name));
            dao.country = CountryDAO.getFactory().fromJoinedResultSet(rs);
            return propertyChanges;
        }

        CityRowData fromJoinedResultSet(ResultSet rs) throws SQLException {
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
            assertValidCity(dao);
            super.save(dao, connection, force);
        }

        public CityDAO assertValidCity(CityDAO target) {
            if (target.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Data access object already deleted");
            }
            CountryRowData country = target.getCountry();
            if (null == country)
                throw new IllegalStateException("Country not specified");
            
            if (target.predefinedCity.getCountry() != country.asPredefinedData())
                throw new IllegalStateException("Invalid country association");
            return target;
        }

        @Override
        public String getSaveDbConflictMessage(CityDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                return ResourceBundleHelper.getResourceString(EditCity.class, RESOURCEKEY_CITYALREADYDELETED);
            }

            CountryRowData country = assertValidCity(dao).getCountry();

            if (country instanceof CountryDAO && ModelHelper.getRowState(country) != DataRowState.UNMODIFIED) {
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
                ps.setString(1, dao.getName().toLowerCase());
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

    private static final class Related implements CityRowData {

        private final int primaryKey;
        private final String name;
        private final CountryRowData country;
        private final PredefinedCity predefinedCity;

        Related(int primaryKey, String resourceKey, CountryRowData country) {
            this.primaryKey = primaryKey;
            predefinedCity = PredefinedData.getCityMap().get(resourceKey);
            name = predefinedCity.getName();
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
        public CountryRowData getCountry() {
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
        public PredefinedCity asPredefinedData() {
            return predefinedCity;
        }

    }
}
