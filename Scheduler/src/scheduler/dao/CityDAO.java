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
import scheduler.AppResourceBundleConstants;
import scheduler.AppResources;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.dao.schema.TableJoinType;
import scheduler.util.InternalException;
import scheduler.util.ResourceBundleLoader;
import static scheduler.util.Values.asNonNullAndTrimmed;
import scheduler.view.country.EditCountry;

@DatabaseTable(DbTable.CITY)
public class CityDAO extends DataAccessObject implements CityElement {

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'country' property.
     */
    public static final String PROP_COUNTRY = "country";

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private String name;
    private CountryElement country;

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
        } else if (country.validate() != ValidationResult.OK) {
            addValidation.accept(ValidationResult.INVALID_COUNTRY);
        } else if (country.getRowState() == DataRowState.NEW) {
            addValidation.accept(ValidationResult.COUNTRY_NOT_SAVED);
        }
    }

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
        String oldValue = this.name;
        this.name = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_NAME, oldValue, this.name);
    }

    @Override
    public CountryElement getCountry() {
        return country;
    }

    /**
     * Set the value of country
     *
     * @param country new value of country
     */
    public void setCountry(CountryElement country) {
        CountryElement oldValue = this.country;
        this.country = Objects.requireNonNull(country);
        firePropertyChange(PROP_COUNTRY, oldValue, this.country);
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
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CityElement) {
            CityElement other = (CityElement) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && name.equals(other.getName()) && country.equals(other.getCountry());
            }
            return other.getRowState() != DataRowState.NEW && getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

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
            return DaoFilter.all(AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(AppResourceBundleConstants.RESOURCEKEY_LOADINGCITIES));
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(CityDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldName = dao.name;
                private final CountryElement oldCountry = dao.country;

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
            dao.country = CountryDAO.getFactory().fromJoinedResultSet(rs);
            return propertyChanges;
        }

        CityElement fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new CityElement() {
                private final String name = asNonNullAndTrimmed(rs.getString(DbColumn.CITY_NAME.toString()));
                private final CountryElement country = CountryDAO.getFactory().fromJoinedResultSet(rs);
                private final int primaryKey = rs.getInt(DbColumn.ADDRESS_CITY.toString());

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public CountryElement getCountry() {
                    return country;
                }

                @Override
                public int getPrimaryKey() {
                    return primaryKey;
                }

                @Override
                public DataRowState getRowState() {
                    return DataRowState.UNMODIFIED;
                }

                @Override
                public boolean isExisting() {
                    return true;
                }

                @Override
                public int hashCode() {
                    return primaryKey;
                }

                @Override
                public boolean equals(Object obj) {
                    if (null != obj && obj instanceof CityElement) {
                        CityElement other = (CityElement) obj;
                        return other.getRowState() != DataRowState.NEW && other.getPrimaryKey() == getPrimaryKey();
                    }
                    return false;
                }

            };
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
                    return ResourceBundleLoader.getResourceString(EditCountry.class, EditCountry.RESOURCEKEY_DELETEMSGSINGLE);
                default:
                    return ResourceBundleLoader.formatResourceString(EditCountry.class, EditCountry.RESOURCEKEY_DELETEMSGMULTIPLE, count);
            }
        }

        @Override
        public String getSaveDbConflictMessage(CityDAO dao, Connection connection) throws SQLException {
            assert dao.getRowState() != DataRowState.DELETED : "Data access object already deleted";

            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.CITY_ID.getDbName())
                    .append(") FROM ").append(DbTable.CITY.getDbName())
                    .append(" WHERE ").append(DbColumn.CITY_COUNTRY.getDbName())
                    .append("=? AND LOWER(").append(DbColumn.CITY_NAME.getDbName()).append(")=?");
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
            // TODO: Internationalize this
            if (count > 0) {
                return "Another city has the same name";
            }
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

}
