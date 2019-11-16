/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Base object for modeling database records.
 * @author Leonard T. Erwine
 */
abstract class DbObject {
    public static final String PROP_ID = "id";
    private Optional<Integer> id;
    public Optional<Integer> getId() { return this.id; }
    
    public static final String PROP_ISSAVED = "isSaved";
    private boolean isSaved;
    public boolean getIsSaved() { return this.isSaved; }
    
    public static final String PROP_CREATEDATE = "createDate";
    private LocalDateTime createDate;
    public LocalDateTime getCreateDate() { return this.createDate; }
    
    public static final String PROP_CREATEDBY = "createdBy";
    private String createdBy;
    public String getCreatedBy() { return this.createdBy; }
    
    public static final String PROP_LASTUPDATE = "lastUpdate";
    private LocalDateTime lastUpdate;
    public LocalDateTime getLastUpdate() { return this.lastUpdate; }
    
    public static final String PROP_LASTUPDATEBY = "lastUpdateBy";
    private String lastUpdateBy;
    public String getLastUpdateBy() { return this.lastUpdateBy; }
    
    private final String idFieldName;
    
    protected DbObject(String idFieldName) {
        this.idFieldName = idFieldName;
        this.id = Optional.empty();
        this.createDate = this.lastUpdate = LocalDateTime.now();
        Optional<User> user = User.CURRENT();
        this.createdBy = this.lastUpdateBy = (user.isPresent()) ? user.get().getUserName() : User.ADMIN_LOGIN_NAME;
        this.isSaved = true;
    }
    
    protected DbObject(ResultSet rs, String idFieldName) throws SQLException {
        this.idFieldName = idFieldName;
        this.id = Optional.of(rs.getInt(rs.findColumn(this.idFieldName)));
        this.createDate = rs.getTimestamp(rs.findColumn(PROP_CREATEDATE)).toLocalDateTime();
        this.createdBy = rs.getString(rs.findColumn(PROP_CREATEDBY));
        this.lastUpdate = rs.getTimestamp(rs.findColumn(PROP_LASTUPDATE)).toLocalDateTime();
        this.lastUpdateBy = rs.getString(rs.findColumn(PROP_LASTUPDATEBY));
        this.isSaved = false;
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        LocalDateTime prevLastUpdate = this.lastUpdate;
        this.lastUpdate = LocalDateTime.now();
        boolean prevIsSaved = this.isSaved;
        this.isSaved = true;
        Optional<User> user = User.CURRENT();
        String prevLastUpdateBy = this.lastUpdateBy;
        this.lastUpdateBy = (user.isPresent()) ? user.get().getUserName() : User.ADMIN_LOGIN_NAME;
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        propertyChangeSupport.firePropertyChange(PROP_LASTUPDATE, prevLastUpdate, this.lastUpdate);
        if (!prevLastUpdateBy.equals(this.lastUpdateBy))
            propertyChangeSupport.firePropertyChange(PROP_LASTUPDATEBY, prevLastUpdateBy, this.lastUpdateBy);
        if (prevIsSaved != this.isSaved)
            propertyChangeSupport.firePropertyChange(PROP_ISSAVED, prevIsSaved, this.isSaved);
    }
    
    protected abstract void setValuesForSave(Stream<String> fieldNames, PreparedStatement stmt);
    
    protected void save(List<String> fieldNames, boolean force) throws Exception, SQLException {
        if (this.isSaved && !force)
            return;
        fieldNames.add(PROP_LASTUPDATE);
        fieldNames.add(PROP_LASTUPDATEBY);
        java.sql.Connection conn = utils.DbConnection.GetConnection();
        PreparedStatement stmt;
        Stream<String> stream;
        if (this.id.isPresent()) {
            fieldNames.add(PROP_CREATEDATE);
            fieldNames.add(PROP_CREATEDBY);
            stream = fieldNames.stream();
            stmt = conn.prepareStatement("UPDATE users SET " +
                    stream.reduce("", (a, f) -> ((a.length() == 0) ? f : a + ", " + f) + "=?") +
                    " WHERE " + this.idFieldName + "=?");
        } else {
            stream = fieldNames.stream();
            stmt = conn.prepareStatement("INSERT INTO users (" +
                    stream.reduce("", (a, f) -> ((a.length() == 0) ? f : a + ", " + f) + "=?") +
                    ") VALUES (" + stream.reduce("", (a, f) -> ((a.length() == 0) ? "?" : a + ", ?")) + ")");
        }
        fieldNames.forEach(new Consumer<String>() {
            private int index = 0;
            @Override
            public void accept(String n) {
                try {
                    switch (n) {
                        case PROP_CREATEDATE:
                            stmt.setTimestamp(index, Timestamp.valueOf(DbObject.this.createDate));
                            break;
                        case PROP_CREATEDBY:
                            stmt.setString(index, DbObject.this.createdBy);
                            break;
                        case PROP_LASTUPDATE:
                            stmt.setTimestamp(index, Timestamp.valueOf(DbObject.this.lastUpdate));
                            break;
                        case PROP_LASTUPDATEBY:
                            stmt.setString(index, DbObject.this.lastUpdateBy);
                            break;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                index++;
            }
        });
        this.setValuesForSave(stream, stmt);
        stmt.executeUpdate();
        boolean idChanged = !this.id.isPresent();
        if (idChanged) {
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            this.id = Optional.of(rs.getInt(0));
        }
        boolean isSaved = this.isSaved;
        this.isSaved = true;
        if (idChanged)
            propertyChangeSupport.firePropertyChange(PROP_ID, Optional.empty(), this.id);
        if (isSaved != this.isSaved)
            propertyChangeSupport.firePropertyChange(PROP_ISSAVED, isSaved, this.isSaved);
    }
    /**
     * Allows for more efficient detection of property value changes.
     */
    public final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
}
