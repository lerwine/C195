package model.db;

import expressions.ActiveStateProperty;
import expressions.NonNullableStringProperty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import scheduler.dao.TableName;
import scheduler.InternalException;
import scheduler.InvalidOperationException;
import util.DB;
import util.PwHash;
import scheduler.dao.PrimaryKeyColumn;

/**
 * Represents a user account data row in the database.
 * @author Leonard T. Erwine
 */
@PrimaryKeyColumn(UserRow.COLNAME_USERID)
@TableName("user")
@Deprecated
public class UserRow extends DataRow implements model.User {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">
    
    public static final String SQL_SELECT = "SELECT * FROM `user`";
    
    //<editor-fold defaultstate="collapsed" desc="Active status constants">
    
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
    
    //</editor-fold>
    
    /**
     * The name of the primary key column for the user data table.
     */
    public static final String COLNAME_USERID = "userId";
    
    //<editor-fold defaultstate="collapsed" desc="userName">
    
    public static final String PROP_USERNAME = "userName";
    
    private final ReadOnlyStringWrapper userName;

    /**
     * Gets the user name for the current data row.
     * @return The user name for the current data row.
     */
    @Override
    public String getUserName() { return userName.get(); }

    public ReadOnlyStringProperty userNameProperty() { return userName.getReadOnlyProperty(); }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="password">
    
    public static final String PROP_PASSWORD = "password";
    
    private final StringProperty password;

    /**
     * Gets the password hash for the current data row.
     * @return The password hash for the current data row.
     */
    public String getPassword() { return password.get(); }

    /**
     * Sets the new password hash for the current data row.
     * @param value The new password hash for the current data row.
     */
    public void setPassword(String value) { password.set(value); }

    public StringProperty passwordProperty() { return password; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="passwordHash">
    
    private final ReadOnlyObjectWrapper<PwHash> passwordHash;

    public PwHash getPasswordHash() { return passwordHash.get(); }

    public ReadOnlyObjectProperty<PwHash> passwordHashProperty() { return passwordHash.getReadOnlyProperty(); }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="active">
    
    public static final String PROP_ACTIVE = "active";
    
    private final ActiveStateProperty active;

    /**
     * Gets the value of active state for the current row.
     * @return The value of active state for the current row.
     */
    @Override
    public int getActive() { return active.get(); }

    /**
     * Sets the value of active state for the current row.
     * @param value The new active state value for the current row.
     */
    public void setActive(int value) { active.set(value); }

    public IntegerProperty activeProperty() { return active; }
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    private final PasswordChangeManager passwordChangeManager;

    /**
     * 
     */
    public UserRow() {
        super();
        userName = new ReadOnlyStringWrapper();
        password = new NonNullableStringProperty();
        active = new ActiveStateProperty(STATE_USER);
        passwordHash = new ReadOnlyObjectWrapper<>(new PwHash("", true));
        passwordChangeManager = new PasswordChangeManager();
    }
    
    /**
     * 
     * @param userName
     * @param password
     * @param isRawPassword
     * @param active
     */
    public UserRow(String userName, String password, boolean isRawPassword, int active) {
        super();
        this.userName = new ReadOnlyStringWrapper((userName == null) ? "" : userName);
        this.password = new NonNullableStringProperty((isRawPassword && password != null) ? password : "");
        this.active = new ActiveStateProperty(active);
        passwordHash = new ReadOnlyObjectWrapper<>(new PwHash(password, isRawPassword));
        passwordChangeManager = new PasswordChangeManager();
    }
    
    /**
     * 
     * @param user
     * @throws InvalidOperationException
     */
    protected UserRow(UserRow user) throws InvalidOperationException {
        super(user);
        passwordHash = new ReadOnlyObjectWrapper<>(new PwHash(user.getPasswordHash().getEncodedHash(), false));
        userName = new ReadOnlyStringWrapper(user.getUserName());
        password = new NonNullableStringProperty(user.getPassword());
        active = new ActiveStateProperty(user.getActive());
        passwordChangeManager = new PasswordChangeManager();
    }
    
    private UserRow(ResultSet rs) throws SQLException {
        super(rs);
        passwordHash = new ReadOnlyObjectWrapper<>(new PwHash(DB.resultStringOrDefault(rs, PROP_PASSWORD, ""), false));
        userName = new ReadOnlyStringWrapper(DB.resultStringOrDefault(rs, PROP_USERNAME, ""));
        password = new NonNullableStringProperty();
        active = new ActiveStateProperty(DB.resultShortOrDefault(rs, PROP_ACTIVE, STATE_INACTIVE));
        passwordChangeManager = new PasswordChangeManager();
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Database read/write methods">
    
    @Override
    protected String getSelectQuery() { return SQL_SELECT; }
    /**
     * 
     * @param connection
     * @param id
     * @return
     * @throws SQLException
     */
    public static final Optional<UserRow> getById(Connection connection, int id) throws SQLException {
        return selectFirstFromDb(connection, SQL_SELECT + " WHERE `user`.`userId` = ?", (Function<ResultSet, UserRow>)(ResultSet rs) -> {
            UserRow u;
            try {
                u = new UserRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(UserRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setInt(1, id);
            } catch (SQLException ex) {
                Logger.getLogger(UserRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * 
     * @param connection
     * @param userName
     * @return
     * @throws SQLException
     */
    public static final Optional<UserRow> getByUserName(Connection connection, String userName) throws SQLException {
        return selectFirstFromDb(connection, SQL_SELECT + " WHERE `user`.`userName` = ?", (Function<ResultSet, UserRow>)(ResultSet rs) -> {
            UserRow u;
            try {
                u = new UserRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(UserRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setString(1, userName);
            } catch (SQLException ex) {
                Logger.getLogger(UserRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static final ObservableList<UserRow> getActive(Connection connection) throws SQLException {
        return selectFromDb(connection, SQL_SELECT + " WHERE `user`.`" + PROP_ACTIVE + "` > ?", (Function<ResultSet, UserRow>)(ResultSet rs) -> {
            UserRow u;
            try {
                u = new UserRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(UserRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing user object from result set.");
            }
            return u;
        },
        (PreparedStatement ps) -> {
            try {
                ps.setShort(1, STATE_INACTIVE);
            } catch (SQLException ex) {
                Logger.getLogger(UserRow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static final ObservableList<UserRow> getAll(Connection connection) throws SQLException {
        return selectFromDb(connection, SQL_SELECT, (Function<ResultSet, UserRow>)(ResultSet rs) -> {
            UserRow u;
            try {
                u = new UserRow(rs);
            } catch (SQLException ex) {
                Logger.getLogger(UserRow.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Error initializing UserRow object from result set.");
            }
            return u;
        }, null);
    }

    @Override
    protected void refreshFromDb(ResultSet rs) throws SQLException {
        userName.set(rs.getString(PROP_USERNAME));
        if (rs.wasNull())
            userName.set("");
        password.set(rs.getString(PROP_PASSWORD));
        if (rs.wasNull())
            password.set("");
        active.set(rs.getShort(PROP_ACTIVE));
        if (rs.wasNull())
            active.set(STATE_INACTIVE);
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
                    ps.setString(index + 1, getUserName());
                    break;
                case PROP_PASSWORD:
                    ps.setString(index + 1, getPassword());
                    break;
                case PROP_ACTIVE:
                    ps.setShort(index + 1, (short)getActive());
                    break;
            }
        }
    }
    
    //</editor-fold>
    
    @Override
    public int hashCode() { return getPrimaryKey(); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((getRowState() != ROWSTATE_MODIFIED && getRowState() != ROWSTATE_UNMODIFIED) || obj == null || !(obj instanceof model.User))
            return false;
        final model.User other = (model.User)obj;
        return (other.getRowState() == ROWSTATE_MODIFIED || other.getRowState() == ROWSTATE_UNMODIFIED) && getPrimaryKey() == other.getPrimaryKey();
    }
    
    @Override
    public String toString() { return userName.get(); }
    
    private class PasswordChangeManager {
        private String currentPassword;
        
        PasswordChangeManager() {
            currentPassword = null;
            password.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if (newValue.equals(currentPassword))
                    return;
                
                currentPassword = newValue;
                try {
                    passwordHash.get().setFromRawPassword(newValue);
                } finally {
                    if (newValue.equals(currentPassword))
                        currentPassword = null;
                }
            });
            passwordHash.get().encodedHashProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if (currentPassword != null) {
                    String pw = password.get();
                    if (currentPassword.equals(pw) && passwordHash.get().test(pw))
                        return;
                }
                currentPassword = "";
                try { password.set(""); }
                finally {
                    if (currentPassword != null && currentPassword.isEmpty())
                        currentPassword = null;
                }
            });
        }
    }
}
