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
import javafx.event.EventDispatchChain;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.events.CountryEvent;
import scheduler.events.DbOperationType;
import scheduler.events.EventEvaluationStatus;
import scheduler.model.Country;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.model.ui.CountryModel;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.country.EditCountry;
import static scheduler.view.country.EditCountryResourceKeys.*;

/**
 * Data access object for the {@code country} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.COUNTRY)
public final class CountryDAO extends DataAccessObject implements CountryDbRecord {

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final Logger LOG = Logger.getLogger(CountryDAO.class.getName());

    private final OriginalValues originalValues;
    private String name;
    private Locale locale;

    /**
     * Initializes a {@link DataRowState#NEW} country data access object.
     */
    public CountryDAO() {
        super();
        name = "";
        originalValues = new OriginalValues();
    }

    @Override
    public String getName() {
        return name;
    }

    private void setName(String value) {
        String oldValue = name;
        name = Values.asNonNullAndWsNormalized(value);
        firePropertyChange(PROP_NAME, oldValue, name);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        Locale oldLocale = this.locale;
        String oldName = name;
        this.locale = locale;
        name = CountryProperties.getCountryAndLanguageDisplayText(this.locale);
        firePropertyChange(PROP_LOCALE, oldLocale, this.locale);
        firePropertyChange(PROP_NAME, oldName, name);
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.name = name;
        originalValues.locale = locale;
    }

    @Override
    protected void onRejectChanges() {
        String oldName = name;
        Locale oldLocale = locale;
        name = originalValues.name;
        locale = originalValues.locale;
        firePropertyChange(PROP_NAME, oldName, name);
        firePropertyChange(PROP_LOCALE, oldLocale, locale);
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.fine(() -> String.format("Adding %s to dispatch chain", FACTORY.getClass().getName()));
        return FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
    }

    @Override
    public int hashCode() {
        if (getRowState() == DataRowState.NEW) {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(name);
            hash = 67 * hash + Objects.hashCode(locale);
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(PROP_PRIMARYKEY, getPrimaryKey());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(PROP_NAME, name)
                .addLocale(PROP_LOCALE, locale)
                .addTimestamp(PROP_CREATEDATE, getCreateDate())
                .addString(PROP_CREATEDBY, getCreatedBy())
                .addTimestamp(PROP_LASTMODIFIEDDATE, getLastModifiedDate())
                .addString(PROP_LASTMODIFIEDBY, getLastModifiedBy());
    }

    /**
     * Factory implementation for {@link CountryDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<CountryDAO, CountryEvent> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        CountryEvent insert(CountryEvent event, Connection connection) {
            if (event.getStatus() != EventEvaluationStatus.EVALUATING) {
                return event;
            }
            String sql = "SELECT COUNT(" + DbColumn.COUNTRY_ID.getDbName() + ") FROM " + DbTable.COUNTRY.getDbName()
                    + " WHERE " + DbColumn.COUNTRY_NAME.getDbName() + "=?";
            CountryDAO dao = ICountryDAO.assertValidCountry(event.getDataAccessObject());
            String lt = dao.locale.toLanguageTag();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, lt);
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking country naming conflicts", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                return event;
            }

            if (count > 0) {
                event.setInvalid("Name in use", ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_SAVECONFLICTMESSAGE));
                return event;
            }
            return super.insert(event, connection);
        }

        @Override
        CountryEvent update(CountryEvent event, Connection connection) {
            if (event.getStatus() != EventEvaluationStatus.EVALUATING) {
                return event;
            }
            String sql = "SELECT COUNT(" + DbColumn.COUNTRY_ID.getDbName() + ") FROM " + DbTable.COUNTRY.getDbName()
                    + " WHERE " + DbColumn.COUNTRY_NAME.getDbName() + "=?" + " AND " + DbColumn.COUNTRY_ID.getDbName() + "<>?";
            CountryDAO dao = ICountryDAO.assertValidCountry(event.getDataAccessObject());
            String lt = dao.locale.toLanguageTag();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, lt);
                ps.setInt(2, dao.getPrimaryKey());
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking country naming conflicts", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                return event;
            }

            if (count > 0) {
                event.setInvalid("Name in use", ResourceBundleHelper.getResourceString(EditCountry.class, RESOURCEKEY_SAVECONFLICTMESSAGE));
                return event;
            }
            return super.update(event, connection);
        }

        @Override
        protected CountryEvent delete(CountryEvent event, Connection connection) {
            if (event.getStatus() != EventEvaluationStatus.EVALUATING) {
                return event;
            }
            CountryDAO dao = event.getDataAccessObject();
            int count;
            try {
                count = CityDAO.FACTORY.countByCountry(dao.getPrimaryKey(), connection);
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking dependencies", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                return event;
            }
            switch (count) {
                case 0:
                    return super.delete(event, connection);
                case 1:
                    event.setInvalid("Country in use", ResourceBundleHelper.getResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGSINGLECOUNTRY));
                    break;
                default:
                    event.setInvalid("Country in use", ResourceBundleHelper.formatResourceString(AppResources.class, AppResourceKeys.RESOURCEKEY_DELETEMSGMULTIPLECOUNTRY, count));
                    break;
            }
            return event;
        }

        @Override
        public boolean isCompoundSelect() {
            return false;
        }

        @Override
        protected void applyColumnValue(CountryDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case COUNTRY_NAME:
                    ps.setString(index, dao.locale.toLanguageTag());
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public CountryDAO createNew() {
            return new CountryDAO();
        }

        public CountryDAO fromLocale(Locale locale) {
            CountryDAO result = new CountryDAO();
            result.setLocale(locale);
            result.setName(locale.getDisplayCountry());
            return result;
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

        ICountryDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            String n = rs.getString(DbColumn.COUNTRY_NAME.toString());
            return new Related(rs.getInt(DbColumn.CITY_COUNTRY.toString()), Locale.forLanguageTag(n));
        }

        @Override
        public Class<? extends CountryDAO> getDaoClass() {
            return CountryDAO.class;
        }

        @Override
        protected void onCloneProperties(CountryDAO fromDAO, CountryDAO toDAO) {
            String oldName = toDAO.name;
            Locale oldLocale = toDAO.locale;
            toDAO.name = fromDAO.name;
            toDAO.locale = fromDAO.locale;
            toDAO.originalValues.name = fromDAO.originalValues.name;
            toDAO.originalValues.locale = fromDAO.originalValues.locale;
            toDAO.firePropertyChange(PROP_NAME, oldName, toDAO.name);
            toDAO.firePropertyChange(PROP_LOCALE, oldLocale, toDAO.locale);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CountryDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                Locale oldLocale = dao.locale;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_NAME, oldName, dao.name);
                    t.firePropertyChange(PROP_LOCALE, oldLocale, dao.locale);
                }
            };

            String s = rs.getString(DbColumn.COUNTRY_NAME.toString());
            dao.locale = Locale.forLanguageTag(s);
            dao.name = CountryProperties.getCountryAndLanguageDisplayText(dao.locale);

            return propertyChanges;
        }

        public ArrayList<CountryDAO> getAllCountries(Connection connection) throws SQLException {
            String sql = createDmlSelectQueryBuilder().build().toString();
            LOG.fine(() -> String.format("Executing query \"%s\"", sql));
            ArrayList<CountryDAO> result = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        do {
                            result.add(fromResultSet(rs));
                        } while (rs.next());
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        return result;
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null == sqlWarning) {
                        LOG.log(Level.WARNING, "No results, no warnings.");
                    } else {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            }
            return result;
        }

        public CountryDAO getByRegionCode(Connection connection, String rc) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().build().toString()).append(" WHERE ")
                    .append(DbColumn.COUNTRY_NAME).append("=?").toString();
            LOG.fine(() -> String.format("getByRegionCode", "Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, rc);
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        CountryDAO result = fromResultSet(rs);
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        return result;
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            }
            return null;
        }

        @Override
        protected CountryEvent createDbOperationEvent(CountryEvent sourceEvent, DbOperationType operation) {
            CountryModel model = sourceEvent.getModel();
            if (null != model) {
                return new CountryEvent(model, sourceEvent.getSource(), this, operation);
            }
            return new CountryEvent(sourceEvent.getSource(), this, sourceEvent.getDataAccessObject(), operation);
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.fine(() -> String.format("Adding %s to dispatch chain", CountryModel.FACTORY.getClass().getName()));
            return CountryModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
        }

    }

    public final static class Related extends PropertyBindable implements ICountryDAO {

        private final int primaryKey;
        private final String name;
        private final Locale locale;

        private Related(int primaryKey, Locale locale) {
            this.primaryKey = primaryKey;
            this.locale = locale;
            this.name = CountryProperties.getCountryAndLanguageDisplayText(this.locale);
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
        public Locale getLocale() {
            return locale;
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
        public String toString() {
            return toStringBuilder().build();
        }

        @Override
        public ToStringPropertyBuilder toStringBuilder() {
            return ToStringPropertyBuilder.create(this)
                    .addNumber(PROP_PRIMARYKEY, getPrimaryKey())
                    .addString(PROP_NAME, name)
                    .addLocale(PROP_LOCALE, locale);
        }

    }

    private class OriginalValues {

        private String name;
        private Locale locale;

        private OriginalValues() {
            this.name = CountryDAO.this.name;
            this.locale = CountryDAO.this.locale;
        }
    }

}
