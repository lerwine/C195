/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.mysql.jdbc.Connection;
import model.User;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import model.db.Column;
import model.db.DbConnectionManager;
import model.db.FieldMapping;
import model.db.Mode;
import model.db.ObjectMapping;
import model.db.QueryMode;

/**
 * Base object for database records.
 * @author Leonard T. Erwine
 */
public abstract class Record {
    //<editor-fold defaultstate="collapsed" desc="id">
    
    public static final String PROP_ID = "id";
    
    @model.json.Property
    private Optional<Integer> id = Optional.empty();
    
    public final Optional<Integer> getId() { return id; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="isSaved">
    
    public static final String PROP_ISSAVED = "isSaved";
    
    private boolean saved = false;
    
    public final boolean isSaved() { return saved; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createDate">
    
    public static final String PROP_CREATEDATE = "createDate";
    
    @Column
    @model.db.ValueMap(model.db.MapType.DATETIME)
    @Mode(QueryMode.InsertOnly)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.DATETIME)
    private final LocalDateTime createDate;
    
    public final LocalDateTime getCreateDate() { return createDate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="createdBy">
    
    public static final String PROP_CREATEDBY = "createdBy";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @Mode(QueryMode.InsertOnly)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private final String createdBy;
    
    public final String getCreatedBy() { return createdBy; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdate">
    
    public static final String PROP_LASTUPDATE = "lastUpdate";
    
    @Column
    @model.db.ValueMap(model.db.MapType.DATETIME)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.DATETIME)
    private LocalDateTime lastUpdate;
    
    public final LocalDateTime getLastUpdate() { return lastUpdate; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lastUpdateBy">
    
    public static final String PROP_LASTUPDATEBY = "lastUpdateBy";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String lastUpdateBy;
    
    public final String getLastUpdateBy() { return lastUpdateBy; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    protected Record() {
        this.createDate = this.lastUpdate = LocalDateTime.now();
        Optional<User> user = User.GetCurrentUser();
        createdBy = lastUpdateBy = (user.isPresent()) ? user.get().getUserName() : User.ADMIN_LOGIN_NAME;
    }
    
    //</editor-fold>
    
    private static void applyResultSet(Record target, ResultSet rs, Stream<model.db.FieldMapping> mappings) {
        Class<?> t = target.getClass();
        Stream<model.db.FieldMapping> changed = mappings.filter((m) -> {
            try {
                Field f = t.getField(m.getFieldName());
                int index = rs.findColumn(m.getColumnName());
                Object current = f.get(target);
                Object nv;
                switch (m.getMapType()) {
                    case BOOLEAN:
                        nv = rs.getBoolean(index);
                        break;
                    case INTEGER:
                        nv = rs.getInt(index);
                        break;
                    case DATETIME:
                        nv = rs.getTimestamp(index).toLocalDateTime();
                        break;
                    default:
                        nv = rs.getString(index);
                        break;
                }
                if (current == null) {
                    if (nv == null)
                        return false;
                    if (m.isOptional())
                        f.set(target, Optional.of(nv));
                    else
                        f.set(target, nv);
                } else if (nv == null) {
                    if (m.isOptional()) {
                        if (!((Optional<?>)current).isPresent())
                            return false;
                        f.set(target, Optional.empty());
                    } else
                        f.set(target, nv);
                } else if (m.isOptional()) {
                    if (((Optional<?>)current).isPresent() && ((Optional<?>)current).get() == nv)
                        return false;
                    f.set(target, Optional.of(nv));
                } else if (current != nv)
                    f.set(target, nv);
                else
                    return false;
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error applying result set to " + m.getFieldName() + ": " + ex.getMessage(), ex);
            }
            return true;
        });
        changed.forEach((m) -> {

        });
    }
    
    private static String getQueryString(String source, Function<String, String> queryFunc) {
        return (queryFunc == null) ? source : queryFunc.apply(source);
    }
    
    public static <R extends Record> Iterator<R> FindAll(Connection c, Supplier<R> creator) throws SQLException {
        return new ResultIterator<>(c, creator);
    }
    
    public static <R extends Record> Iterator<R> FindAll(Connection c, Supplier<R> creator, Function<String, String> queryFunc,
            Consumer<PreparedStatement> setValues) throws SQLException {
        return new ResultIterator<>(c, creator, queryFunc, setValues);
    }
    
    public static <R extends Record> boolean FindById(Connection connection, R target, int id) throws SQLException {
        final Stream<model.db.FieldMapping> pk = target.getMappings().dbMapping.getFields().filter((f) -> f.isPrimaryKey());
        if (pk.count() == 0)
            throw new InternalException("No primary key defined");
        return FindFirst(connection, target, (q) -> q + " WHERE " + pk.findFirst().get().getColumnName() + "=?",
                (ps) -> {
                    try {
                        ps.setInt(0, id);
                    } catch (SQLException ex) {
                        Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException("Error applying setting primary key value for lookup: " + ex.getMessage(), ex);
                    }
                });
    }
    
    public static <R extends Record> boolean FindFirst(Connection c, R target, Function<String, String> queryFunc,
            Consumer<PreparedStatement> setValues) throws SQLException {
        ObjectMapping dbMapping = target.getMappings().dbMapping;
        Stream<model.db.FieldMapping> mappings = dbMapping.getFields();
        PreparedStatement ps = c.prepareStatement(getQueryString("SELECT " + mappings.map((f) -> f.getColumnName()).reduce((p, n) -> p + ", " + n) +
                " FROM " + dbMapping.getTableName(), queryFunc));
        if (setValues != null)
            setValues.accept(ps);
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            applyResultSet(target, rs, mappings);
            return true;
        }
        return false;
    }
    
    public static class SetStatementFieldValue implements Consumer<FieldMapping> {
        private int index = 0;
        private final Class<? extends Record> type;
        private final Record record;
        private final PreparedStatement ps;
        public SetStatementFieldValue(Class<? extends Record> t, Record r, PreparedStatement s) {
            this.type = t;
            record = r;
            ps = s;
        }
        @Override
        public void accept(FieldMapping m) {
            try {
                Field f = type.getField(m.getFieldName());
                boolean isAccessible = f.isAccessible();
                if (!isAccessible)
                    f.setAccessible(true);
                try {
                    switch (m.getMapType()) {
                        case INTEGER:
                            if (m.isOptional()) {
                                Optional<Integer> value = (Optional<Integer>)f.get(record);
                                if (value.isPresent())
                                    ps.setInt(index++, value.get());
                                else
                                    ps.setNull(index++, Types.INTEGER);
                            } else {
                                Object value = f.get(record);
                                if (value == null)
                                    ps.setNull(index++, Types.INTEGER);
                                else
                                    ps.setInt(index++, (int)value);
                            }
                            break;
                        case BOOLEAN:
                            if (m.isOptional()) {
                                Optional<Boolean> value = (Optional<Boolean>)f.get(record);
                                if (value.isPresent())
                                    ps.setBoolean(index++, value.get());
                                else
                                    ps.setNull(index++, Types.BOOLEAN);
                            } else {
                                Object value = f.get(record);
                                if (value == null)
                                    ps.setNull(index++, Types.BOOLEAN);
                                else
                                    ps.setBoolean(index++, (boolean)value);
                            }
                            break;
                        case DATETIME:
                            if (m.isOptional()) {
                                Optional<LocalDateTime> value = (Optional<LocalDateTime>)f.get(record);
                                if (value.isPresent())
                                    ps.setTimestamp(index++, Timestamp.valueOf(value.get()));
                                else
                                    ps.setNull(index++, Types.TIMESTAMP);
                            } else {
                                Object value = f.get(record);
                                if (value == null)
                                    ps.setNull(index++, Types.TIMESTAMP);
                                else
                                    ps.setTimestamp(index++, Timestamp.valueOf((LocalDateTime)value));
                            }
                            break;
                        default:
                            if (m.isOptional()) {
                                Optional<String> value = (Optional<String>)f.get(record);
                                if (value.isPresent())
                                    ps.setString(index++, value.get());
                                else
                                    ps.setNull(index++, Types.VARCHAR);
                            } else {
                                Object value = f.get(record);
                                if (value == null)
                                    ps.setNull(index++, Types.VARCHAR);
                                else
                                    ps.setString(index++, (String)value);
                            }
                            break;
                    }
                } finally {
                    if (!isAccessible)
                        f.setAccessible(false);
                }
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(type.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error setting column value " + m.getColumnName() + ": " + ex.getMessage(), ex);
            }
        }
    }
    
    public void saveChanges(boolean force) throws SQLException {
        DbConnectionManager mgr = new DbConnectionManager();
        mgr.apply(this, (Connection connection, Record record) -> {
            ObjectMapping dbMapping = getMappings().dbMapping;
            Stream<model.db.FieldMapping> mappings = dbMapping.getFields();
            Optional<Integer> pk = record.getId();
            if (pk.isPresent()) {
                if (record.isSaved() && !force)
                    return;
                mappings = mappings.filter((m) -> m.getMode() == QueryMode.All);
                try {
                    final PreparedStatement ps = connection.prepareStatement("UPDATE " + dbMapping.getTableName() + " SET " +
                            mappings.map((m) -> m.getColumnName() + "=?").reduce((p, n) -> p + ", " + n) +
                            " WHERE " + dbMapping.getFields().filter((f) -> f.isPrimaryKey()).findFirst().get().getColumnName() + "=?");
                    mappings.forEach(new SetStatementFieldValue(getClass(), record, ps));
                    ps.setInt((int)mappings.count(), pk.get());
                    ps.execute();
                } catch (SQLException ex) {
                    Logger.getLogger(record.getClass().getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException("Error saving changes: " + ex.getMessage(), ex);
                }
            } else {
                mappings = mappings.filter((f) -> f.getMode() != QueryMode.ReadOnly);
                try {
                    final PreparedStatement ps = connection.prepareStatement("INSERT INTO " + dbMapping.getTableName() + " (" +
                            mappings.map((f) -> f.getColumnName()).reduce((p, n) -> p + ", " + n) +
                            ") VALUES (" + mappings.map((f) -> "?").reduce((p, n) -> p + ", " + n) + ")");
                    mappings.forEach(new SetStatementFieldValue(getClass(), record, ps));
                    ps.execute();
                    ResultSet resultSet = ps.getGeneratedKeys();
                    if (!resultSet.next())
                        throw new RuntimeException("Failed to insert new record");
                    record.id = Optional.of(resultSet.getInt(1));
                } catch (SQLException ex) {
                    Logger.getLogger(record.getClass().getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException("Error saving changes: " + ex.getMessage(), ex);
                }
            }
            record.saved = true;
        });
    }
    
    public boolean delete() throws SQLException {
        final Optional<Integer> pk = getId();
        if (!pk.isPresent())
            return false;
        DbConnectionManager mgr = new DbConnectionManager();
        mgr.apply(this, (Connection connection, Record record) -> {
            try {
                ObjectMapping dbMapping = getMappings().dbMapping;
                PreparedStatement ps = connection.prepareStatement("DELETE FROM " + dbMapping.getTableName() +
                        " WHERE " + dbMapping.getFields().filter((f) -> f.isPrimaryKey()).findFirst().get().getColumnName() + "=?");
                ps.setInt(0, pk.get());
                ps.execute();
                record.id = Optional.empty();
            } catch (SQLException ex) {
                Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error deleting record: " + ex.getMessage(), ex);
            }
        });
        return true;
    }
    
    protected abstract Mappings getMappings();
    
    //<editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        LocalDateTime prevLastUpdate = this.lastUpdate;
        this.lastUpdate = LocalDateTime.now();
        boolean wasSaved = saved;
        saved = false;
        Optional<User> user = User.GetCurrentUser();
        String prevLastUpdateBy = this.lastUpdateBy;
        lastUpdateBy = (user.isPresent()) ? user.get().getUserName() : User.ADMIN_LOGIN_NAME;
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        propertyChangeSupport.firePropertyChange(PROP_LASTUPDATE, prevLastUpdate, this.lastUpdate);
        if (!prevLastUpdateBy.equals(this.lastUpdateBy))
            propertyChangeSupport.firePropertyChange(PROP_LASTUPDATEBY, prevLastUpdateBy, this.lastUpdateBy);
        if (wasSaved != saved)
            propertyChangeSupport.firePropertyChange(PROP_ISSAVED, wasSaved, saved);
    }
    
    /**
     * Allows for more efficient detection of property value changes.
     */
    public final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    //</editor-fold>
    
    public static class ResultIterator<R extends Record> implements Iterator<R> {
        private final Stream<model.db.FieldMapping> mappings;
        private final Supplier<R> creator;
        private final ResultSet resultSet;
        private Optional<R> current;
        
        public ResultIterator(Connection c, Supplier<R> creator) throws SQLException { this(c, creator, null, null); }
        
        public ResultIterator(Connection c, Supplier<R> creator, Function<String, String> queryFunc,
            Consumer<PreparedStatement> setValues) throws SQLException {
            this.creator = creator;
            R target = creator.get();
            ObjectMapping dbMapping = target.getMappings().dbMapping;
            mappings = dbMapping.getFields();
            PreparedStatement ps = c.prepareStatement(getQueryString("SELECT " + mappings.map((f) -> f.getColumnName()).reduce((p, n) -> p + ", " + n) +
                    " FROM " + dbMapping.getTableName(), queryFunc));
            if (setValues != null)
                setValues.accept(ps);
            resultSet = ps.getResultSet();
            if (resultSet.next()) {
                applyResultSet(target, resultSet, mappings);
                current = Optional.of(target);
            } else
                current = Optional.empty();
        }
        
        @Override
        public boolean hasNext() { return current.isPresent(); }

        @Override
        public R next() {
            R result = current.get();
            try {
                if (resultSet.next()) {
                    R target = creator.get();
                    applyResultSet(target, resultSet, mappings);
                    current = Optional.of(target);
                } else
                    current = Optional.empty();
            } catch (SQLException ex) {
                Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error applying result set: " + ex.getMessage(), ex);
            }
            return result;
        }
    }
    
    protected static class Mappings {
        private final ObjectMapping dbMapping;
        private final Stream<model.json.FieldMapping> jsonProperties;
        public Mappings(Class<? extends Record> c){
            dbMapping = new ObjectMapping(c);
            jsonProperties = model.json.FieldMapping.getFields(c);
        }
    }
}
