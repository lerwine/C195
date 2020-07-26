package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
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
import scheduler.events.AddressEvent;
import scheduler.events.AddressFailedEvent;
import scheduler.events.AddressSuccessEvent;
import scheduler.events.CityEvent;
import scheduler.events.CityFailedEvent;
import scheduler.events.CitySuccessEvent;
import scheduler.model.Address;
import scheduler.model.AddressEntity;
import scheduler.model.AddressLookup;
import scheduler.model.City;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.model.fx.AddressModel;
import scheduler.model.fx.CityModel;
import scheduler.model.fx.PartialCityModel;
import scheduler.util.DB;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.Values;
import static scheduler.util.Values.asNonNullAndWsNormalized;

/**
 * Data access object for the {@code address} database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.ADDRESS)
public final class AddressDAO extends DataAccessObject implements PartialAddressDAO, AddressEntity<Timestamp> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AddressDAO.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(AddressDAO.class.getName());

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final EventHandler<CitySuccessEvent> CITY_UPDATE_EVENT_HANDLER;

    static {
        CITY_UPDATE_EVENT_HANDLER = FACTORY::onCitySaved;
        CityDAO.FACTORY.addEventHandler(CitySuccessEvent.UPDATE_SUCCESS, CITY_UPDATE_EVENT_HANDLER);
    }

    private final OriginalPropertyValues originalValues;
    private String address1;
    private String address2;
    private PartialCityDAO city;
    private String postalCode;
    private String phone;
    private WeakReference<AddressModel> _cachedModel = null;

    /**
     * Initializes a {@link DataRowState#NEW} address object.
     */
    public AddressDAO() {
        address1 = "";
        address2 = "";
        city = null;
        postalCode = "";
        phone = "";
        originalValues = new OriginalPropertyValues();
    }

    public AddressDAO(PredefinedData.PredefinedAddress address) {
        address1 = address.getAddress1();
        address2 = address.getAddress2();
        city = address.getCity().getDataAccessObject();
        postalCode = address.getPostalCode();
        phone = address.getPhone();
        originalValues = new OriginalPropertyValues();
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
    private synchronized void setAddress1(String value) {
        String oldValue = address1;
        address1 = asNonNullAndWsNormalized(value);
        if (!address1.equals(oldValue)) {
            firePropertyChange(PROP_ADDRESS1, oldValue, address1);
            setModified();
        }
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
    private synchronized void setAddress2(String value) {
        String oldValue = address2;
        address2 = asNonNullAndWsNormalized(value);
        if (!address2.equals(oldValue)) {
            firePropertyChange(PROP_ADDRESS2, oldValue, address2);
            setModified();
        }
    }

    @Override
    public PartialCityDAO getCity() {
        return city;
    }

    /**
     * Set the value of city
     *
     * @param city new value of city
     */
    synchronized void setCity(PartialCityDAO city) {
        PartialCityDAO oldValue = this.city;
        if (Objects.equals(oldValue, city)) {
            return;
        }
        this.city = city;
        firePropertyChange(PROP_CITY, oldValue, this.city);
        setModified();
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
    private synchronized void setPostalCode(String value) {
        String oldValue = postalCode;
        postalCode = asNonNullAndWsNormalized(value);
        if (!postalCode.equals(oldValue)) {
            firePropertyChange(PROP_POSTALCODE, oldValue, postalCode);
            setModified();
        }
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
    private synchronized void setPhone(String value) {
        String oldValue = phone;
        phone = asNonNullAndWsNormalized(value);
        if (!phone.equals(oldValue)) {
            firePropertyChange(PROP_PHONE, oldValue, phone);
            setModified();
        }
    }

    @Override
    public synchronized AddressModel cachedModel(boolean create) {
        AddressModel model;
        if (null != _cachedModel) {
            model = _cachedModel.get();
            if (null != model) {
                return model;
            }
            _cachedModel = null;
        }
        if (create) {
            model = AddressModel.FACTORY.createNew(this);
            _cachedModel = new WeakReference<>(model);
            return model;
        }
        return null;
    }

    private synchronized void setCachedModel(AddressModel model) {
        if (null == model) {
            if (null != _cachedModel) {
                if (null != _cachedModel.get()) {
                    _cachedModel.clear();
                }
                _cachedModel = null;
            }
        } else if (null == _cachedModel || !Objects.equals(_cachedModel.get(), model)) {
            _cachedModel = new WeakReference<>(model);
        }
    }

    @Override
    protected boolean verifyModified() {
        return !(address1.equals(originalValues.address1) && address2.equals(originalValues.address2) && ModelHelper.areSameRecord(city, originalValues.city)
                && postalCode.equals(originalValues.postalCode) && phone.equals(originalValues.phone));
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.address1 = address1;
        originalValues.address2 = address2;
        originalValues.city = city;
        originalValues.postalCode = postalCode;
        originalValues.phone = phone;
    }

    @Override
    protected void onRejectChanges() {
        String oldAddress1 = address1;
        String oldAddress2 = address2;
        PartialCityDAO oldCity = city;
        String oldPostalCode = postalCode;
        String oldPhone = phone;
        address1 = originalValues.address1;
        address2 = originalValues.address2;
        city = originalValues.city;
        postalCode = originalValues.postalCode;
        phone = originalValues.phone;
        firePropertyChange(PROP_ADDRESS1, oldAddress1, address1);
        firePropertyChange(PROP_ADDRESS2, oldAddress2, address2);
        firePropertyChange(PROP_CITY, oldCity, city);
        firePropertyChange(PROP_POSTALCODE, oldPostalCode, postalCode);
        firePropertyChange(PROP_PHONE, oldPhone, phone);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        EventDispatchChain result = FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
        LOG.exiting(LOG.getName(), "buildEventDispatchChain");
        return result;
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(address1);
        hash = 79 * hash + Objects.hashCode(address2);
        hash = 79 * hash + Objects.hashCode(city);
        hash = 79 * hash + Objects.hashCode(postalCode);
        hash = 79 * hash + Objects.hashCode(phone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = ModelHelper.AddressHelper.appendDaoProperties(this, new StringBuilder(AddressDAO.class.getName()).append(" { "));
        if (null == getCity()) {
            return sb.append("}").toString();
        }
        return sb.append(Values.LINEBREAK_STRING).append("}").toString();
    }

    private synchronized void onCityUpdated(CityModel newModel) {
        if (null == city) {
            return;
        }
        CityDAO newDao = newModel.dataObject();
        if (city == newDao || city.getPrimaryKey() != newDao.getPrimaryKey()) {
            return;
        }
        PartialCityDAO oldCity = city;
        city = newDao;
        firePropertyChange(PROP_CITY, oldCity, city);

        AddressModel addressModel = cachedModel(false);
        if (null != addressModel) {
            PartialCityModel<? extends PartialCityDAO> oldModel = addressModel.getCity();
            if (null != oldModel && oldModel != newModel) {
                addressModel.setCity(newModel);
            }
        }
    }

    /**
     * Factory implementation for {@link AddressDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<AddressDAO, AddressModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        void onBeforeSave(AddressModel model) {
            AddressDAO dao = model.dataObject();
            dao.setAddress1(model.getAddress1());
            dao.setAddress2(model.getAddress2());
            PartialCityModel<? extends PartialCityDAO> c = model.getCity();
            if (null != c) {
                if (c instanceof CityModel) {
                    CityDAO.FACTORY.onBeforeSave((CityModel) c);
                }
                dao.setCity(c.dataObject());
            } else {
                dao.setCity(null);
            }
            dao.setPostalCode(model.getPostalCode());
            dao.setPhone(model.getPhone());
        }

        @Override
        public boolean isCompoundSelect() {
            return true;
        }

        @Override
        protected void applyColumnValue(AddressDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case ADDRESS1:
                    ps.setString(index, dao.address1);
                    break;
                case ADDRESS2:
                    ps.setString(index, dao.address2);
                    break;
                case ADDRESS_CITY:
                    ps.setInt(index, dao.city.getPrimaryKey());
                    break;
                case POSTAL_CODE:
                    ps.setString(index, dao.postalCode);
                    break;
                case PHONE:
                    ps.setString(index, dao.phone);
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public AddressDAO createNew() {
            return new AddressDAO();
        }

        @Override
        public DaoFilter<AddressDAO> getAllItemsFilter() {
            return DaoFilter.all(AppResources.getResourceString(RESOURCEKEY_READINGFROMDB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGADDRESSES));
        }

        public DaoFilter<AddressDAO> getByCityFilter(int pk) {
            return DaoFilter.of(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES),
                    IntColumnValueFilter.of(DbColumn.ADDRESS_CITY, IntValueFilter.of(pk, ComparisonOperator.EQUALS),
                            (AddressDAO t) -> ModelHelper.getPrimaryKey(t.getCity())));
        }

        public AddressDAO lookupCacheByValues(AddressLookup values, String cityName, String regionCode) {
            final AddressLookup nzValues = values.asNormalizedAddressLookup();
            final String nzName = Values.asNonNullAndWsNormalized(cityName);
            final String nzRegionCode = Values.asNonNullAndWsNormalized(regionCode);
            return findFirstCached((t) -> {
                if (t.address1.equals(nzValues.getAddress1()) && t.address2.equals(nzValues.getAddress2()) && t.postalCode.equals(nzValues.getPostalCode()) && t.phone.equals(nzValues.getPhone())) {
                    PartialCityDAO c = t.getCity();
                    if (null != c && c.getName().equals(nzName)) {
                        PartialCountryDAO n = c.getCountry();
                        if (null != n && n.getLocale().getCountry().equals(nzRegionCode)) {
                            return true;
                        }
                    }
                }
                return false;
            }).orElse(null);
        }

        public AddressDAO lookupCacheByValues(AddressLookup values, String cityName, final int countryPk) {
            final AddressLookup nzValues = values.asNormalizedAddressLookup();
            final String nzName = Values.asNonNullAndWsNormalized(cityName);
            return findFirstCached((t) -> {
                if (t.address1.equals(nzValues.getAddress1()) && t.address2.equals(nzValues.getAddress2()) && t.postalCode.equals(nzValues.getPostalCode()) && t.phone.equals(nzValues.getPhone())) {
                    PartialCityDAO c = t.getCity();
                    if (null != c && c.getName().equals(nzName)) {
                        PartialCountryDAO n = c.getCountry();
                        if (null != n && n.getPrimaryKey() == countryPk) {
                            return true;
                        }
                    }
                }
                return false;
            }).orElse(null);
        }

        public AddressDAO lookupCacheByValues(AddressLookup values, final int cityPk) {
            final AddressLookup nzValues = values.asNormalizedAddressLookup();
            return findFirstCached((t) -> {
                if (t.address1.equals(nzValues.getAddress1()) && t.address2.equals(nzValues.getAddress2()) && t.postalCode.equals(nzValues.getPostalCode()) && t.phone.equals(nzValues.getPhone())) {
                    PartialCityDAO c = t.getCity();
                    if (null != c && c.getPrimaryKey() == cityPk) {
                        return true;
                    }
                }
                return false;
            }).orElse(null);
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            DmlSelectQueryBuilder builder = new DmlSelectQueryBuilder(DbTable.ADDRESS, SchemaHelper.getTableColumns(DbTable.ADDRESS));
            builder.join(DbColumn.ADDRESS_CITY, TableJoinType.LEFT, DbColumn.CITY_ID,
                    SchemaHelper.getTableColumns(DbTable.CITY, SchemaHelper::isForJoinedData))
                    .join(DbColumn.CITY_COUNTRY, TableJoinType.LEFT, DbColumn.COUNTRY_ID,
                            SchemaHelper.getTableColumns(DbTable.COUNTRY, SchemaHelper::isForJoinedData));
            return builder;
        }

        @Override
        protected void onCloneProperties(AddressDAO fromDAO, AddressDAO toDAO) {
            String oldAddress1 = toDAO.address1;
            String oldAddress2 = toDAO.address2;
            City oldCity = toDAO.city;
            String oldPostalCode = toDAO.postalCode;
            String oldPhone = toDAO.phone;
            toDAO.address1 = fromDAO.address1;
            toDAO.address2 = fromDAO.address2;
            toDAO.city = fromDAO.city;
            toDAO.postalCode = fromDAO.postalCode;
            toDAO.phone = fromDAO.phone;
            toDAO.originalValues.address1 = fromDAO.originalValues.address1;
            toDAO.originalValues.address2 = fromDAO.originalValues.address2;
            toDAO.originalValues.city = fromDAO.originalValues.city;
            toDAO.originalValues.postalCode = fromDAO.originalValues.postalCode;
            toDAO.originalValues.phone = fromDAO.originalValues.phone;
            toDAO.firePropertyChange(PROP_ADDRESS1, oldAddress1, toDAO.address1);
            toDAO.firePropertyChange(PROP_ADDRESS2, oldAddress2, toDAO.address2);
            toDAO.firePropertyChange(PROP_CITY, oldCity, toDAO.city);
            toDAO.firePropertyChange(PROP_POSTALCODE, oldPostalCode, toDAO.postalCode);
            toDAO.firePropertyChange(PROP_POSTALCODE, oldPhone, toDAO.phone);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(AddressDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldAddress1 = dao.address1;
                private final String oldAddress2 = dao.address2;
                private final City oldCity = dao.city;
                private final String oldPostalCode = dao.postalCode;
                private final String oldPhone = dao.phone;

                @Override
                public void accept(PropertyChangeSupport t) {
                    t.firePropertyChange(PROP_ADDRESS1, oldAddress1, dao.address1);
                    t.firePropertyChange(PROP_ADDRESS2, oldAddress2, dao.address2);
                    t.firePropertyChange(PROP_CITY, oldCity, dao.city);
                    t.firePropertyChange(PROP_POSTALCODE, oldPostalCode, dao.postalCode);
                    t.firePropertyChange(PROP_POSTALCODE, oldPhone, dao.phone);
                }
            };

            dao.address1 = asNonNullAndWsNormalized(rs.getString(DbColumn.ADDRESS1.toString()));
            dao.address2 = asNonNullAndWsNormalized(rs.getString(DbColumn.ADDRESS2.toString()));
            dao.city = CityDAO.FACTORY.fromJoinedResultSet(rs);
            dao.postalCode = asNonNullAndWsNormalized(rs.getString(DbColumn.POSTAL_CODE.toString()));
            dao.phone = asNonNullAndWsNormalized(rs.getString(DbColumn.PHONE.toString()));
            return propertyChanges;
        }

        PartialAddressDAO fromJoinedResultSet(ResultSet resultSet) throws SQLException {
            return new Partial(resultSet.getInt(DbColumn.CUSTOMER_ADDRESS.toString()),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.ADDRESS1.toString())),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.ADDRESS2.toString())),
                    CityDAO.FACTORY.fromJoinedResultSet(resultSet),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.POSTAL_CODE.toString())),
                    asNonNullAndWsNormalized(resultSet.getString(DbColumn.PHONE.toString())));
        }

        @Override
        public Class<? extends AddressDAO> getDaoClass() {
            return AddressDAO.class;
        }

        public AddressDAO getByValues(Connection connection, AddressLookup values, final int cityPk) throws SQLException {
            String sql = new StringBuffer(createDmlSelectQueryBuilder().build().toString()).append(" WHERE ")
                    .append(DbTable.ADDRESS).append(".").append(DbColumn.ADDRESS_CITY).append("=? AND ")
                    .append(DbColumn.ADDRESS1.getDbName()).append(" LIKE ? AND ")
                    .append(DbColumn.ADDRESS2.getDbName()).append(" LIKE ? AND ")
                    .append(DbColumn.POSTAL_CODE.getDbName()).append(" LIKE ? AND ")
                    .append(DbColumn.PHONE.getDbName()).append(" LIKE ?").toString();
            LOG.fine(() -> String.format("Executing DML statement: %s", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, cityPk);
                ps.setString(2, DB.escapeWC(values.getAddress1()));
                ps.setString(3, DB.escapeWC(values.getAddress2()));
                ps.setString(4, DB.escapeWC(values.getPostalCode()));
                ps.setString(5, DB.escapeWC(values.getPhone()));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        AddressDAO result = fromResultSet(rs);
                        if (result.address1.equals(values.getAddress1()) && result.address2.equals(values.getAddress2()) && result.postalCode.equals(values.getPostalCode())
                                && result.phone.equalsIgnoreCase(values.getPhone())) {
                            LogHelper.logWarnings(connection, LOG);
                            return result;
                        }
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            return null;
        }

        int countByCity(int primaryKey, Connection connection) throws SQLException {
            String sql = "SELECT COUNT(" + DbColumn.ADDRESS_ID.getDbName() + ") FROM " + DbTable.ADDRESS.getDbName()
                    + " WHERE " + DbColumn.ADDRESS_CITY.getDbName() + "=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, primaryKey);
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int result = rs.getInt(1);
                        LogHelper.logWarnings(connection, LOG);
                        return result;
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            }
            throw new SQLException("Unexpected lack of results from database query");
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
            EventDispatchChain result = AddressModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
            LOG.exiting(LOG.getName(), "buildEventDispatchChain");
            return result;
        }

        private void onCitySaved(CitySuccessEvent event) {
            LOG.entering(LOG.getName(), "onCitySaved", event);
            CityModel newModel = event.getEntityModel();
            streamCached().forEach((t) -> t.onCityUpdated(newModel));
            CityDAO newDao = newModel.dataObject();
            Consumer<PartialAddressDAO> addressConsumer = (t) -> {
                if (null != t && t instanceof Partial) {
                    ((Partial) t).onCityUpdated(newDao);
                }
            };
            CustomerDAO.FACTORY.streamCached().map((t) -> t.getAddress()).forEach(addressConsumer);
            AppointmentDAO.FACTORY.streamCached().map((t) -> t.getCustomer()).filter(Objects::nonNull).map((t) -> t.getAddress()).forEach(addressConsumer);
            LOG.exiting(LOG.getName(), "onCitySaved");
        }

    }

    public static class SaveTask extends SaveDaoTask<AddressDAO, AddressModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(SaveTask.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(SaveTask.class.getName());

        private static final String ERROR_CHECKING_CONFLICTS = "Error checking address conflicts";
        private static final String MATCHING_ITEM_EXISTS = "Another matching address exists";

        public SaveTask(AddressModel model, boolean alreadyValidated) {
            super(model, AddressModel.FACTORY, alreadyValidated);
        }

        @Override
        protected AddressEvent validate(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "validate", connection);
            AddressModel targetModel = getEntityModel();
            AddressEvent saveEvent = AddressModel.FACTORY.validateForSave(targetModel);
            if (null != saveEvent && saveEvent instanceof AddressFailedEvent) {
                return saveEvent;
            }
            AddressDAO dao = getDataAccessObject();
            StringBuffer sb = new StringBuffer("SELECT COUNT(").append(DbColumn.ADDRESS_ID.getDbName())
                    .append(") FROM ").append(DbTable.ADDRESS.getDbName())
                    .append(" WHERE ").append(DbColumn.ADDRESS_CITY.getDbName()).append("=?");
            if (dao.address1.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.ADDRESS1.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.ADDRESS1.getDbName()).append(")=?");
            }
            if (dao.address2.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.ADDRESS2.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.ADDRESS2.getDbName()).append(")=?");
            }
            if (dao.postalCode.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.POSTAL_CODE.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.POSTAL_CODE.getDbName()).append(")=?");
            }
            if (dao.phone.isEmpty()) {
                sb.append(" AND LENGTH(").append(DbColumn.PHONE.getDbName()).append(")=0");
            } else {
                sb.append(" AND LOWER(").append(DbColumn.PHONE.getDbName()).append(")=?");
            }
            if (getOriginalRowState() != DataRowState.NEW) {
                sb.append(" AND ").append(DbColumn.ADDRESS_ID.getDbName()).append("<>?");
            }
            String sql = sb.toString();
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, ModelHelper.getPrimaryKey(dao.getCity()));
                int index = 2;
                if (!dao.address1.isEmpty()) {
                    ps.setString(index++, dao.address1.toLowerCase());
                }
                if (!dao.address2.isEmpty()) {
                    ps.setString(index++, dao.address2.toLowerCase());
                }
                if (!dao.postalCode.isEmpty()) {
                    ps.setString(index++, dao.postalCode.toLowerCase());
                }
                if (!dao.phone.isEmpty()) {
                    ps.setString(index, dao.phone.toLowerCase());
                }
                if (getOriginalRowState() != DataRowState.NEW) {
                    ps.setInt(index, dao.getPrimaryKey());
                }
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    LogHelper.logWarnings(connection, LOG);
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, ERROR_CHECKING_CONFLICTS, ex);
                throw new OperationFailureException(ERROR_CHECKING_CONFLICTS, ex);
            }
            if (count > 0) {
                if (getOriginalRowState() == DataRowState.NEW) {
                    return AddressEvent.createInsertInvalidEvent(targetModel, this, MATCHING_ITEM_EXISTS);
                }
                return AddressEvent.createUpdateInvalidEvent(targetModel, this, MATCHING_ITEM_EXISTS);
            }

            PartialCityModel<? extends PartialCityDAO> cityModel = targetModel.getCity();

            AddressEvent resultEvent;
            if (cityModel instanceof CityModel) {
                switch (cityModel.getRowState()) {
                    case NEW:
                    case MODIFIED:
                        CityDAO.SaveTask saveTask = new CityDAO.SaveTask((CityModel) cityModel, false);
                        saveTask.run();
                        CityEvent event = (CityEvent) saveTask.get();
                        if (null != event && event instanceof CityFailedEvent) {
                            if (getOriginalRowState() == DataRowState.NEW) {
                                resultEvent = AddressEvent.createInsertInvalidEvent(targetModel, this, (CityFailedEvent) event);
                            } else {
                                resultEvent = AddressEvent.createUpdateInvalidEvent(targetModel, this, (CityFailedEvent) event);
                            }
                        } else {
                            resultEvent = null;
                        }
                        break;
                    default:
                        resultEvent = null;
                        break;
                }
            } else {
                resultEvent = null;
            }
            LOG.exiting(LOG.getName(), "validate", resultEvent);
            return resultEvent;
        }

        @Override
        protected AddressEvent createCanceledEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AddressEvent.createInsertCanceledEvent(getEntityModel(), this);
            }
            return AddressEvent.createUpdateCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected AddressEvent createSuccessEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AddressEvent.createInsertSuccessEvent(getEntityModel(), this);
            }
            return AddressEvent.createUpdateSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected AddressEvent createFaultedEvent() {
            if (getOriginalRowState() == DataRowState.NEW) {
                return AddressEvent.createInsertFaultedEvent(getEntityModel(), this, getException());
            }
            return AddressEvent.createUpdateFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            AddressEvent event = (AddressEvent) getValue();
            if (null != event && event instanceof AddressSuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    public static final class DeleteTask extends DeleteDaoTask<AddressDAO, AddressModel> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DeleteTask.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(DeleteTask.class.getName());

        private static final String REFERENCED_BY_N = "Address is referenced by %d other customers";
        private static final String REFERENCED_BY_ONE = "Address is referenced by one customer.";
        private static final String ERROR_CHECKING_DEPENDENCIES = "Error checking dependencies";

        public DeleteTask(AddressModel target, boolean alreadyValidated) {
            super(target, AddressModel.FACTORY, alreadyValidated);
        }

        @Override
        protected AddressEvent validate(Connection connection) throws Exception {
            LOG.entering(LOG.getName(), "validate", connection);
            AddressDAO dao = getDataAccessObject();
            int count;
            try {
                count = CustomerDAO.FACTORY.countByAddress(connection, dao.getPrimaryKey());
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, ERROR_CHECKING_DEPENDENCIES, ex);
                throw new OperationFailureException(ERROR_CHECKING_DEPENDENCIES, ex);
            }
            AddressEvent resultEvent;
            switch (count) {
                case 0:
                    resultEvent = null;
                    break;
                case 1:
                    resultEvent = AddressEvent.createDeleteInvalidEvent(getEntityModel(), this, REFERENCED_BY_ONE);
                    break;
                default:
                    resultEvent = AddressEvent.createDeleteInvalidEvent(getEntityModel(), this, String.format(REFERENCED_BY_N, count));
                    break;
            }
            LOG.exiting(LOG.getName(), "validate", resultEvent);
            return resultEvent;
        }

        @Override
        protected AddressEvent createSuccessEvent() {
            return AddressEvent.createDeleteSuccessEvent(getEntityModel(), this);
        }

        @Override
        protected AddressEvent createCanceledEvent() {
            return AddressEvent.createDeleteCanceledEvent(getEntityModel(), this);
        }

        @Override
        protected AddressEvent createFaultedEvent() {
            return AddressEvent.createDeleteFaultedEvent(getEntityModel(), this, getException());
        }

        @Override
        protected void succeeded() {
            LOG.entering(LOG.getName(), "succeeded");
            AddressEvent event = (AddressEvent) getValue();
            if (null != event && event instanceof AddressSuccessEvent) {
                getDataAccessObject().setCachedModel(getEntityModel());
            }
            super.succeeded();
            LOG.exiting(LOG.getName(), "succeeded");
        }

    }

    public static class Partial extends PropertyBindable implements PartialAddressDAO {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Partial.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(Partial.class.getName());

        private final int primaryKey;
        private final String address1;
        private final String address2;
        private PartialCityDAO city;
        private final String postalCode;
        private final String phone;

        private Partial(int primaryKey, String address1, String address2, PartialCityDAO city, String postalCode, String phone) {
            this.primaryKey = primaryKey;
            this.address1 = asNonNullAndWsNormalized(address1);
            this.address2 = asNonNullAndWsNormalized(address2);
            this.city = city;
            this.postalCode = asNonNullAndWsNormalized(postalCode);
            this.phone = asNonNullAndWsNormalized(phone);
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public PartialCityDAO getCity() {
            return city;
        }

        @Override
        public String getAddress1() {
            return address1;
        }

        @Override
        public String getAddress2() {
            return address2;
        }

        @Override
        public String getPostalCode() {
            return postalCode;
        }

        @Override
        public String getPhone() {
            return phone;
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof Address && ModelHelper.areSameRecord(this, (Address) obj);
        }

        @Override
        public String toString() {
            StringBuilder sb = ModelHelper.AddressHelper.appendPartialDaoProperties(this, new StringBuilder(Partial.class.getName()).append(" { "));
            if (null == getCity()) {
                return sb.append("}").toString();
            }
            return sb.append(Values.LINEBREAK_STRING).append("}").toString();
        }

        private void onCityUpdated(CityDAO newDao) {
            if (city == newDao || city.getPrimaryKey() != newDao.getPrimaryKey()) {
                return;
            }
            PartialCityDAO oldCity = city;
            city = newDao;
            firePropertyChange(PROP_CITY, oldCity, city);
        }

    }

    private class OriginalPropertyValues {

        private String address1;
        private String address2;
        private PartialCityDAO city;
        private String postalCode;
        private String phone;

        private OriginalPropertyValues() {
            this.address1 = AddressDAO.this.address1;
            this.address2 = AddressDAO.this.address2;
            this.city = AddressDAO.this.city;
            this.postalCode = AddressDAO.this.postalCode;
            this.phone = AddressDAO.this.phone;
        }
    }

}
