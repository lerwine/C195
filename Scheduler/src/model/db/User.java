/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.annotations.DbColumn;
import model.annotations.PrimaryKey;
import model.annotations.TableName;
import model.meta.TableInfo;
import utils.InvalidArgumentException;

/**
 * Represents a user account data row in the database.
 * @author Leonard T. Erwine
 */
@PrimaryKey("userId")
@TableName("user")
public class User extends DataRow {
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
    
    //<editor-fold defaultstate="collapsed" desc="userName">

    @DbColumn
    private String userName;

    public static final String PROP_USERNAME = "userName";
    
    /**
     * Gets the user name for the current data row.
     * @return The user name for the current data row.
     */
    public String getUserName() { return userName; }
    
    /**
     * Sets the new user name for the current data row.
     * @param value The new user name for the current data row.
     */
    public void setUserName(String value) {
        String oldValue = userName;
        userName = (value == null) ? "" : value;
        firePropertyChange(PROP_USERNAME, oldValue, userName);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="password">
    
    @DbColumn
    private String password;
    
    public static final String PROP_PASSWORD = "password";
    
    /**
     * Gets the password hash for the current data row.
     * @return The password hash for the current data row.
     */
    public String getPassword() { return password; }
    
    /**
     * Sets the new password hash for the current data row.
     * @param value The new password hash for the current data row.
     */
    public void setPassword(String value) {
        String oldValue = password;
        password = (value == null) ? "" : value;
        firePropertyChange(PROP_PASSWORD, oldValue, password);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="active">
    
    @DbColumn
    private short active;
    
    public static final String PROP_ACTIVE = "active";
    
    /**
     * Gets the value of active state for the current row.
     * @return The value of active state for the current row.
     */
    public short getActive() { return active; }
    
    /**
     * Sets the value of active state for the current row.
     * @param value The new active state value for the current row.
     */
    public void setActive(short value) {
        short oldActive = active;
        active = (value < STATE_INACTIVE) ? STATE_INACTIVE : ((value > STATE_ADMIN) ? STATE_ADMIN : value);
        firePropertyChange(PROP_ACTIVE, oldActive, active);
    }
    
    //</editor-fold>

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

    protected User(RowBuilder rb) { super(rb); }
    
    /**
     * Looks up user by user name.
     * @param userName - The login name of the user.
     * @param connection - The database connection to use.
     * @return The user or empty if no matching user was found.
     * @throws InvalidArgumentException
     * @throws SQLException 
     */
    public static Optional<User> getByUserName(String userName, Connection connection) throws InvalidArgumentException, SQLException {
        if (userName == null || userName.isEmpty())
            return Optional.empty();
        return getFirstFromDatabase(User.class, connection, 
                (RowBuilder<User> rb) -> { return new User(rb); },
                PROP_USERNAME + " =?",
                (PreparedStatement ps) -> {
                    try {
                        ps.setString(0, userName);
                    } catch (SQLException ex) {
                        Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException("Error setting user name parameter", ex);
                    }
                }
        );
    }
}
