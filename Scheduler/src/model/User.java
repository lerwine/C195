/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Optional;
import javax.persistence.Column;
import model.db.ObjectMapping;
import model.db.PropertyName;
import model.db.Table;

/**
 *
 * @author Leonard T. Erwine
 */
@Table(name = User.DB_TABLE_NAME, pk = User.DB_COL_USERID)
public class User extends Record {
    private static Optional<User> CURRENT = Optional.empty();
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public static Optional<User> GetCurrentUser() { return CURRENT; }
    
    /**
     * Name of associated data table.
     */
    public static final String DB_TABLE_NAME = "user";
    
    /**
     * Login name for user that has administrative privileges.
     */
    public static final String ADMIN_LOGIN_NAME = "admin";
    
    /**
     * Defines the name of the property that contains the user record identity value.
     */
    public static final String DB_COL_USERID = "userId";
    
    //<editor-fold defaultstate="collapsed" desc="userName">
    
    /**
     * Defines the name of the property that contains the user login name.
     */
    public static final String PROP_USERNAME = "userName";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String userName = "";
    
    /**
     * Gets the user login name
     * @return
     */
    public final String getUserName() { return userName; }
    
    /**
     * Sets the user login name.
     * @param value The login name for the user.
     * @throws Exception If null, empty or only whitespace.
     */
    public final void setUserName(String value) throws Exception {
        String u = (value == null) ? "" : value;
        String oldValue = userName;
        if (oldValue.equals(u))
            return;
        userName = u;
        firePropertyChange(PROP_USERNAME, oldValue, u);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="password">
    
    /**
     * Defines the name of the property that contains the user password.
     */
    public static final String PROP_PASSWORD = "password";
    
    @Column
    @model.db.ValueMap(model.db.MapType.STRING)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.STRING)
    private String password = "";
    
    /**
     * Gets the user password.
     * @return The user password.
     */
    public final String getPassword() { return password; }
    
    /**
     * Sets the user password.
     * @param value The new user password.
     * @throws Exception If null, empty or only whitespace.
     */
    public final void setPassword(String value) throws Exception {
        String pwd = (value == null) ? "" : value;
        String oldValue = password;
        if (oldValue.equals(pwd))
            return;
        password = pwd;
        firePropertyChange(PROP_PASSWORD, oldValue, pwd);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="isActive">
    
    /**
     * Defines the name of the database column that contains the active flag value.
     */
    public static final String DB_COL_ACTIVE = "active";
    
    /**
     * Defines the name of the property that contains the active flag value.
     */
    public static final String PROP_ISACTIVE = "isActive";
    
    @Column
    @PropertyName(PROP_ISACTIVE)
    @model.db.ValueMap(model.db.MapType.BOOLEAN)
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.FALSE)
    private boolean active = false;
    
    /**
     * Gets a value indicating whether the user is active.
     * @return True if the user is active; otherwise, false.
     */
    public final boolean isActive() { return active; }
    
    /**
     * Sets a value to indicate whether the user is active.
     * @param value True to make the user active; otherwise, false.
     */
    public final void setIsActive(boolean value) {
        boolean oldValue = active;
        if (oldValue == value)
            return;
        active = value;
        firePropertyChange(PROP_ISACTIVE, oldValue, value);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="isAdmin">
    
    @model.json.Property
    @model.json.ValueMap(model.json.MapType.TRUE)
    private boolean admin = false;
    
    /**
     * Gets a value indicating whether the user is an administrative account.
     * @return True if the user is an administrative account; otherwise, false.
     */
    public final boolean isAdmin() { return admin; }
    
//</editor-fold>
    
    /**
     * Creates a new user object that has not yet been saved to the database.
     */
    public User() { super(); }

    private static final Mappings MAPPINGS = new Mappings(User.class);
    
    @Override
    protected Mappings getMappings() { return MAPPINGS; }
}
