package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.util.InternalException;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.country.EditCountry;
import static scheduler.view.country.EditCountryResourceKeys.*;

/**
 * Data access object for the {@code country} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.COUNTRY)
public final class CountryDAO extends DataAccessObject implements CountryDbRecord {

    public static final int MAX_LENGTH_NAME = 50;

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";
    public static final String PROP_PREDEFINEDELEMENT = "predefinedElement";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private PredefinedCountryElement predefinedElement;

    private String name;

    /**
     * Initializes a {@link DataRowState#NEW} country object.
     */
    public CountryDAO() {
        super();
        name = "";
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    @Override
    public PredefinedCountryElement getPredefinedElement() {
        return predefinedElement;
    }

    // FIXME: When this is changed to another country, it will fail because the dataAccessObject on the predefined object is not changed.
    /**
     * Set the value of predefinedCountry
     *
     * @param country new value of predefinedCountry
     */
    public void setPredefinedElement(CountryDAO.PredefinedCountryElement country) {
        CountryDAO.PredefinedCountryElement oldPredefinedCountry = predefinedElement;
        String oldName = name;
        try {
            if (null == country) {
                if (null != oldPredefinedCountry) {
                    name = "";
                }
            } else if (!Objects.equals(oldPredefinedCountry, country)) {
                name = country.getLocale().getDisplayCountry();
            }
            predefinedElement = country;
        } finally {
            firePropertyChange(PROP_NAME, oldName, name);
            firePropertyChange(PROP_PREDEFINEDELEMENT, oldPredefinedCountry, country);
        }
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
    }

    /**
     * Factory implementation for {@link CountryDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CountryDAO> {

        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        public boolean isCompoundSelect() {
            return false;
        }

        @Override
        protected void applyColumnValue(CountryDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case COUNTRY_NAME:
                    ps.setString(index, dao.predefinedElement.getLocale().getCountry());
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public CountryDAO createNew() {
            return new CountryDAO();
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            return new DmlSelectQueryBuilder(DbTable.COUNTRY, SchemaHelper.getTableColumns(DbTable.COUNTRY));
        }

        @Override
        public DaoFilter<CountryDAO> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CountryDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                private final PredefinedCountryElement oldPredefinedElement = dao.predefinedElement;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    t.firePropertyChange(PROP_PREDEFINEDELEMENT, oldPredefinedElement, dao.predefinedElement);
                }
            };
            dao.setPredefinedElement(PredefinedData.getCountryMap().get(rs.getString(DbColumn.COUNTRY_NAME.toString())));
            dao.predefinedElement.dataAccessObject = dao;
            return propertyChanges;
        }

        ICountryDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Related(rs.getInt(DbColumn.CITY_COUNTRY.toString()), rs.getString(DbColumn.COUNTRY_NAME.toString()));
        }

        @Override
        public Class<? extends CountryDAO> getDaoClass() {
            return CountryDAO.class;
        }

        @Override
        public String getDeleteDependencyMessage(CountryDAO dao, Connection connection) throws SQLException {
            if (null == dao || !DataRowState.existsInDb(dao.getRowState())) {
                return "";
            }
            int count = CityDAO.getFactory().countByCountry(dao.getPrimaryKey(), connection);
            switch (count) {
                case 0:
                    return "";
                case 1:
                    return ResourceBundleHelper.getResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGSINGLECOUNTRY);
                default:
                    return ResourceBundleHelper.formatResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLECOUNTRY,
                            count);
            }
        }

        @Override
        public void save(CountryDAO dao, Connection connection, boolean force) throws SQLException {
            super.save(ICountryDAO.assertValidCountry(dao), connection, force);
            dao.predefinedElement.dataAccessObject = dao;
        }

        @Override
        protected CountryDAO fromResultSet(ResultSet rs) throws SQLException {
            CountryDAO result = super.fromResultSet(rs);
            PredefinedCountryElement pde = result.predefinedElement;
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
        protected void onInitializingFrom(CountryDAO target, CountryDAO other) {
            String oldName = target.name;
            PredefinedCountryElement oldPredefinedElement = target.predefinedElement;
            target.name = other.name;
            target.predefinedElement = other.predefinedElement;
            target.firePropertyChange(PROP_NAME, oldName, target.name);
            target.firePropertyChange(PROP_PREDEFINEDELEMENT, oldPredefinedElement, target.predefinedElement);
        }

        @Override
        public String getSaveDbConflictMessage(CountryDAO dao, Connection connection) throws SQLException {
            if (dao.getRowState() == DataRowState.DELETED) {
                return ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_COUNTRYALREADYDELETED);
            }

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.COUNTRY_ID.getDbName())
                    .append(") FROM ").append(DbTable.COUNTRY.getDbName())
                    .append(" WHERE ").append(DbColumn.COUNTRY_NAME.getDbName()).append("=?");

            if (dao.getRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.COUNTRY_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, dao.predefinedElement.getLocale().getCountry());
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
                return ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_SAVECONFLICTMESSAGE);
            }

            return "";
        }

        public ArrayList<CountryDAO> getAllCountries(Connection connection) throws SQLException {
            String sql = createDmlSelectQueryBuilder().toString();
            LOG.fine(() -> String.format("Executing query \"%s\"", sql));
            ArrayList<CountryDAO> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs) {
                        while (rs.next()) {
                            result.add(fromResultSet(rs));
                        }
                    }
                    // PENDING: Check for warnings on other queries.
                    SQLWarning w = connection.getWarnings();
                    if (null == w) {
                        LOG.log(Level.WARNING, "Null results, no warnings.");
                    } else {
                        LOG.log(Level.WARNING, "Encountered warning", w);
                    }
                }
            }
            return result;
        }

        public CountryDAO getPredefinedCountry(PredefinedCountryElement element, Connection connection) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().toString()).append(" WHERE ")
                    .append(DbColumn.COUNTRY_NAME).append("=?").toString();
            LOG.fine(() -> String.format("getPredefinedCountry", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, element.getLocale().getCountry());
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        return fromResultSet(rs);
                    }
                }
            }
            if (null == element.dataAccessObject) {
                (element.dataAccessObject = new CountryDAO()).setPredefinedElement(element);
            }
            return element.dataAccessObject;
        }

        public CountryDAO getByRegionCode(Connection connection, String rc) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().toString()).append(" WHERE ")
                    .append(DbColumn.COUNTRY_NAME).append("=?").toString();
            LOG.fine(() -> String.format("getByRegionCode", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, rc);
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        return fromResultSet(rs);
                    }
                }
            }
            return null;
        }

    }

    private final static class Related extends PropertyBindable implements ICountryDAO {

        private final int primaryKey;
        private final String name;
        private final CountryDAO.PredefinedCountryElement predefinedCountry;

        Related(int primaryKey, String countryCode) {
            this.primaryKey = primaryKey;
            predefinedCountry = PredefinedData.getCountryMap().get(countryCode);
            name = predefinedCountry.getLocale().getDisplayCountry(Locale.getDefault());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public CountryDAO.PredefinedCountryElement getPredefinedElement() {
            return predefinedCountry;
        }

    }

    @XmlRootElement(name = PredefinedCountryElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    @XmlType(name = PredefinedCountryElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod="createInstanceJAXB")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PredefinedCountryElement extends PredefinedData.PredefinedCountry {

        public static final String ELEMENT_NAME = "country";

        @XmlTransient
        private CountryDAO dataAccessObject = null;

        @XmlAttribute
        private String languageTag;

        @XmlAttribute
        private String defaultZoneId;

        @Override
        public String getLanguageTag() {
            return languageTag;
        }

        @Override
        public String getDefaultZoneId() {
            return defaultZoneId;
        }

        @Override
        public synchronized CountryDAO getDataAccessObject() {
            return dataAccessObject;
        }

        private PredefinedCountryElement() {
            
        }
        
        private static PredefinedCountryElement createInstanceJAXB() {
            return new PredefinedCountryElement();
        }
        
    }
}
