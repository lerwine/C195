/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.scene.control.Alert;
import static model.DbObject.PROP_CREATEDATE;

/**
 * Represent a user record from the database.
 * @author Leonard T. Erwine
 */
public class User extends DbObject {
    /**
     * Login name for user that can log in, regardless of whether there is an associated record in the user table.
     */
    public static final String ADMIN_LOGIN_NAME = "admin";
    private static final String ADMIN_PASSWORD = "P@$$w0rd123$%^";
    
    private static Optional<User> _currentUser = Optional.empty();
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public static Optional<User> CURRENT() { return _currentUser; }
    
    /**
     * Tries to load the current user using credentials.
     * @param userName The user login name.
     * @param password THe user's password/
     * @return True if new current user was loaded; otherwise, false.
     */
    public static boolean TryLoadCurrentUser(String userName, String password) {
        java.sql.Connection conn;
        try { conn = utils.DbConnection.GetConnection();}
        catch (Exception e) {
            utils.NotificationHelper.showNotificationDialog("Error", "Failed to get database connection", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from user WHERE " + PROP_USERENAME + "=?");
            stmt.setString(0, userName);
            ResultSet rs =  stmt.executeQuery();
            User user;
            if (rs.next()) {
                user = new User(rs);
                if (user.getPassword().equals(password) || (userName.equals(ADMIN_LOGIN_NAME) && password.equals(ADMIN_PASSWORD))) {
                    _currentUser = Optional.of(user);
                    return true;
                }
            } else if (userName.equals(ADMIN_LOGIN_NAME) && password.equals(ADMIN_PASSWORD)) {
                user = new User(ADMIN_LOGIN_NAME, ADMIN_PASSWORD, true);
                _currentUser = Optional.of(user);
                return true;
            }
            utils.NotificationHelper.showNotificationDialog("Invalid Login", "Invalid user name or password", "", Alert.AlertType.WARNING);
        } catch (SQLException e) {
            utils.NotificationHelper.showNotificationDialog("Login Error", "Error validating login", e.getMessage(), Alert.AlertType.ERROR);
        }
        return false;
    }
    
    /**
     * Defines the name of the property that contains the use record identity value.
     */
    public static final String DB_COL_USERID = "userId";
    /**
     * Defines the name of the property that contains the user login name.
     */
    public static final String PROP_USERENAME = "userName";
    private String userName;
    /**
     * Gets the user login name
     * @return 
     */
    public String getUserName() { return this.userName; }
    /**
     * Sets the user login name.
     * @param value The login name for the user.
     * @throws Exception If null, empty or only whitespace.
     */
    public void setUserName(String value) throws Exception {
        if (value == null || value.trim().length() == 0)
            throw new Exception("User name cannot be null or empty");
        String oldValue = this.userName;
        if (oldValue.equals(value))
            return;
        this.userName = value;
        this.firePropertyChange(PROP_USERENAME, oldValue, value);
    }
    
    /**
     * Defines the name of the property that contains the user password.
     */
    public static final String PROP_PASSWORD = "password";
    private String password;
    /**
     * Gets the user password.
     * @return The user password.
     */
    public String getPassword() { return this.password; }
    /**
     * Sets the user password.
     * @param value The new user password.
     * @throws Exception If null, empty or only whitespace.
     */
    public void setPassword(String value) throws Exception {
        if (value == null || value.trim().length() == 0)
            throw new Exception("Password cannot be null or empty");
        String oldValue = this.password;
        if (oldValue.equals(value))
            return;
        this.password = value;
        this.firePropertyChange(PROP_PASSWORD, oldValue, value);
    }
    
    /**
     * Defines the name of the database column that contains the active flag value.
     */
    public static final String DB_COL_ACTIVE = "active";
    /**
     * Defines the name of the property that contains the active flag value.
     */
    public static final String PROP_ISACTIVE = "isActive";
    private boolean isActive;
    /**
     * Gets a value indicating whether the user is active.
     * @return True if the user is active; otherwise, false.
     */
    public boolean getIsActive() { return this.isActive; }
    /**
     * Sets a value to indicate whether the user is active.
     * @param value True to make the user active; otherwise, false.
     */
    public void setIsActive(boolean value) {
        boolean oldValue = this.isActive;
        if (oldValue == value)
            return;
        this.isActive = value;
        this.firePropertyChange(PROP_ISACTIVE, oldValue, value);
    }
    
    private boolean isAdmin;
    /**
     * Gets a value indicating whether the user is an administrative account.
     * @return True if the user is an administrative account; otherwise, false.
     */
    public boolean getIsAdmin() { return this.isAdmin; }
    
    /**
     * Creates a new user object that has not yet been saved to the database.
     * @param userName The login name for the user.
     * @param password The password for the user.
     * @param active Indicates whether the user is to be active.
     */
    public User(String userName, String password, boolean active) {
        super(DB_COL_USERID);
        if (userName == null || userName.trim().length() == 0)
            throw new Error("User name cannot be empty or null");
        if (password == null || password.trim().length() == 0)
            throw new Error("Password cannot be empty or null");
        this.userName = userName;
        this.password = password;
        this.isActive = active;
        this.isAdmin = userName.equals(ADMIN_LOGIN_NAME);
    }
    
    private User(ResultSet rs) throws SQLException {
        super(rs, DB_COL_USERID);
        this.userName = rs.getString(rs.findColumn(PROP_USERENAME));
        this.password = rs.getString(rs.findColumn(PROP_PASSWORD));
        this.isActive = rs.getBoolean(rs.findColumn(DB_COL_ACTIVE));
        this.isAdmin = this.userName.equals(ADMIN_LOGIN_NAME);
    }
    
    /**
     * Looks up a user in the database that matches a specific user name.
     * @param userName The user name to match.
     * @return The matched user or empty if no user was found that matched the specified user name.
     * @throws SQLException
     * @throws Exception 
     */
    public static Optional<User> GetByUserName(String userName) throws SQLException, Exception {
        java.sql.Connection conn = utils.DbConnection.GetConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE (" + PROP_USERENAME + "=?)");
        stmt.setString(0, userName);
        ResultSet rs =  stmt.executeQuery();
        if (rs.next())
            return Optional.of(new User(rs));
        return Optional.empty();
    }
    
    /**
     * Gets all active users.
     * @return A list of all active users.
     * @throws SQLException
     * @throws Exception 
     */
    public static ArrayList<User> GetActive() throws SQLException, Exception {
        java.sql.Connection conn = utils.DbConnection.GetConnection();
        ArrayList<User> result = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE (" + DB_COL_ACTIVE + "=?)");
        stmt.setBoolean(0, true);
        ResultSet rs =  stmt.executeQuery();
        while(rs.next())
            result.add(new User(rs));
        return result;
    }
    
    /**
     * Gets all users.
     * @return A list of all users.
     * @throws SQLException
     * @throws Exception 
     */
    public static ArrayList<User> GetAll() throws SQLException, Exception {
        java.sql.Connection conn = utils.DbConnection.GetConnection();
        ArrayList<User> result = new ArrayList<>();
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs =  stmt.executeQuery("SELECT * FROM user");
        while(rs.next())
            result.add(new User(rs));
        return result;
    }

    public void save(boolean force) throws Exception {
        super.save(Arrays.asList(PROP_USERENAME, PROP_PASSWORD, DB_COL_ACTIVE), force);
    }
    
    @Override
    protected void setValuesForSave(Stream<String> fieldNames, PreparedStatement stmt) {
        fieldNames.forEach(new Consumer<String>() {
            private int index = 0;
            @Override
            public void accept(String n) {
                try {
                    switch (n) {
                        case PROP_USERENAME:
                            stmt.setString(index, User.this.userName);
                            break;
                        case PROP_PASSWORD:
                            stmt.setString(index, User.this.password);
                            break;
                        case DB_COL_ACTIVE:
                            stmt.setBoolean(index, User.this.isActive);
                            break;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                index++;
            }
        });
    }
}
