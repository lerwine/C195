/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import scheduler.InternalException;
import scheduler.InvalidOperationException;

/**
 * Represents a user account data row in the database.
 * @author Leonard T. Erwine
 */
@PrimaryKey(User.COLNAME_USERID)
@TableName("user")
public class User extends DataRow {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String COLNAME_USERID = "userId";
    
    /**
     * Value of {@link #getActive()} when the current user is inactive.
     */
    public static final short STATE_INACTIVE = 0;
    
    /**
     * Value of {@link #getActive()} when the current user is active and not an administrative account.
     */
    public static final short STATE_USER = 1;
    
    /**
     * Value of {@link #getActive()} when the current user is an active administrative account.
     */
    public static final short STATE_ADMIN = 2;
    
    private final static HashMap<String, User> BY_NAME_CACHE = new HashMap<>();
    
    private final static HashMap<Integer, User> LOOKUP_CACHE = new HashMap<>();
    
    //<editor-fold defaultstate="collapsed" desc="userName">
    
    private String userName;
    
    public static final String PROP_USERNAME = "userName";
    
    /**
     * Gets the user name for the current data row.
     * @return The user name for the current data row.
     */
    public final String getUserName() { return userName; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="password">
    
    private String password;
    
    public static final String PROP_PASSWORD = "password";
    
    /**
     * Gets the password hash for the current data row.
     * @return The password hash for the current data row.
     */
    public final String getPassword() { return password; }
    
    /**
     * Sets the new password hash for the current data row.
     * @param value The new password hash for the current data row.
     */
    public final void setPassword(String value) {
        String oldValue = password;
        password = (value == null) ? "" : value;
        firePropertyChange(PROP_PASSWORD, oldValue, password);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="active">
    
    private short active;
    
    public static final String PROP_ACTIVE = "active";
    
    /**
     * Gets the value of active state for the current row.
     * @return The value of active state for the current row.
     */
    public final short getActive() { return active; }
    
    /**
     * Sets the value of active state for the current row.
     * @param value The new active state value for the current row.
     */
    public final void setActive(short value) {
        short oldActive = active;
        active = (value < STATE_INACTIVE) ? STATE_INACTIVE : ((value > STATE_ADMIN) ? STATE_ADMIN : value);
        firePropertyChange(PROP_ACTIVE, oldActive, active);
    }
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public User() {
        super();
        userName = password = "";
        active = STATE_USER;
    }
    
    public User(String userName, String password, short active) {
        super();
        this.userName = (userName == null) ? "" : userName;
        this.password = (password == null) ? "" : password;
        this.active = (active < STATE_INACTIVE) ? STATE_INACTIVE : ((active > STATE_ADMIN) ? STATE_ADMIN : active);
    }
    
    protected User(User user) throws InvalidOperationException {
        super(user);
        userName = user.userName;
        password = user.password;
        active = user.active;
    }
    
    private User(ResultSet rs) throws SQLException {
        super(rs);
        userName = rs.getString(PROP_USERNAME);
        if (rs.wasNull())
            userName = "";
        password = rs.getString(PROP_PASSWORD);
        if (rs.wasNull())
            password = "";
        short a = rs.getShort(PROP_ACTIVE);
        active = (rs.wasNull()) ? STATE_INACTIVE : (a < STATE_INACTIVE) ? STATE_INACTIVE : ((a > STATE_ADMIN) ? STATE_ADMIN : a);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    public static final Optional<User> getById(Connection connection, int id, boolean includeCache) throws SQLException {
        if (includeCache && LOOKUP_CACHE.containsKey(id))
            return Optional.of(LOOKUP_CACHE.get(id));
        
        return selectFromDbById(connection, (Class<User>)User.class, (Function<ResultSet, User>)(ResultSet rs) -> {
            User u;
            try {
                u = new User(rs);
                if (LOOKUP_CACHE.containsKey(id)) {
                    BY_NAME_CACHE.remove(u.userName);
                    LOOKUP_CACHE.remove(id);
                }
                BY_NAME_CACHE.put(u.userName, u);
                LOOKUP_CACHE.put(id, u);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return u;
        }, id);
    }
    
    public static final Optional<User> getByUserName(Connection connection, String userName,
            boolean includeCache) throws SQLException {
        if (includeCache && BY_NAME_CACHE.containsKey(userName))
            return Optional.of(BY_NAME_CACHE.get(userName));
        
        return selectFirstFromDb(connection, (Class<User>)User.class, (Function<ResultSet, User>)(ResultSet rs) -> {
            User u;
            try {
                u = new User(rs);
                if (BY_NAME_CACHE.containsKey(userName)) {
                    BY_NAME_CACHE.remove(userName);
                    LOOKUP_CACHE.remove(u.getPrimaryKey());
                }
                BY_NAME_CACHE.put(userName, u);
                LOOKUP_CACHE.put(u.getPrimaryKey(), u);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return u;
        }, "`" + PROP_USERNAME + "` = ?",
        (Consumer<PreparedStatement>)(PreparedStatement ps) -> {
            try {
                ps.setString(1, userName);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<User> getActive(Connection connection, String userName) throws SQLException {
        return selectFromDb(connection, (Class<User>)User.class, (Function<ResultSet, User>)(ResultSet rs) -> {
            User u;
            try {
                u = new User(rs);
                if (BY_NAME_CACHE.containsKey(u.userName)) {
                    BY_NAME_CACHE.remove(u.userName);
                    LOOKUP_CACHE.remove(u.getPrimaryKey());
                }
                BY_NAME_CACHE.put(u.userName, u);
                LOOKUP_CACHE.put(u.getPrimaryKey(), u);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return u;
        }, "`" + PROP_ACTIVE + "` > ?",
        (PreparedStatement ps) -> {
            try {
                ps.setShort(1, STATE_INACTIVE);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static final ObservableList<User> getAll(Connection connection, String userName) throws SQLException {
        return selectAllFromDb(connection, (Class<User>)User.class, (Function<ResultSet, User>)(ResultSet rs) -> {
            User u;
            try {
                u = new User(rs);
                if (BY_NAME_CACHE.containsKey(u.userName)) {
                    BY_NAME_CACHE.remove(u.userName);
                    LOOKUP_CACHE.remove(u.getPrimaryKey());
                }
                BY_NAME_CACHE.put(u.userName, u);
                LOOKUP_CACHE.put(u.getPrimaryKey(), u);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return u;
        });
    }

    @Override
    public void delete(Connection connection) throws SQLException, InvalidOperationException {
        super.delete(connection);
        if (getRowState() == ROWSTATE_DELETED && BY_NAME_CACHE.containsKey(userName)) {
            BY_NAME_CACHE.remove(userName);
            LOOKUP_CACHE.remove(getPrimaryKey());
        }
    }
    
    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        String oldUserName = userName;
        String oldPassword = password;
        short oldActive = active;
        userName = rs.getString(PROP_USERNAME);
        if (rs.wasNull())
            userName = "";
        password = rs.getString(PROP_PASSWORD);
        if (rs.wasNull())
            password = "";
        short a = rs.getShort(PROP_ACTIVE);
        active = (rs.wasNull()) ? STATE_INACTIVE : (a < STATE_INACTIVE) ? STATE_INACTIVE : ((a > STATE_ADMIN) ? STATE_ADMIN : a);
        if (!BY_NAME_CACHE.containsKey(userName)) {
            BY_NAME_CACHE.put(userName, this);
            LOOKUP_CACHE.put(getPrimaryKey(), this);
        }
        // Execute property change events in nested try/finally statements to ensure that all
        // events get fired, even if one of the property change listeners throws an exception.
        try { firePropertyChange(PROP_USERNAME, oldUserName, userName); }
        finally {
            try { firePropertyChange(PROP_PASSWORD, oldPassword, password); }
            finally { firePropertyChange(PROP_ACTIVE, oldActive, active); }
        }
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { PROP_USERNAME, PROP_PASSWORD, PROP_ACTIVE };
    }

    @Override
    protected void setColumnValues(PreparedStatement ps, String[] fieldNames) throws SQLException {
        for (int index = 0; index < fieldNames.length; index++) {
            switch (fieldNames[index]) {
                case PROP_USERNAME:
                    ps.setString(index + 1, userName);
                    break;
                case PROP_PASSWORD:
                    ps.setString(index + 1, password);
                    break;
                case PROP_ACTIVE:
                    ps.setShort(index + 1, active);
                    break;
            }
        }
    }
    
    //</editor-fold>
}
