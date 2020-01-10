/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import expressions.NonNullableStringProperty;
import expressions.UserStatusProperty;
import java.sql.Connection;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import util.PwHash;

/**
 *
 * @author erwinel
 */
@TableName("user")
@PrimaryKey("userId")
public class UserImpl extends DataObjectImpl implements User {
    
    public static final String[] SQL_EXTENDED_FIELDNAMES;
    
    public static final String COLNAME_USERID = "userId";
    
    //<editor-fold defaultstate="collapsed" desc="userName property">
    
    public static final String COLNAME_USERNAME = "userName";
    
    private final NonNullableStringProperty userName;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserName() { return userName.get(); }
    
    public ReadOnlyStringProperty userNameProperty() { return userName.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="password property">
    
    public static final String COLNAME_PASSWORD = "password";
    
    private final NonNullableStringProperty password;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() { return password.get(); }
    
    public ReadOnlyStringProperty passwordProperty() { return password.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="status property">
    
    public static final String COLNAME_ACTIVE = "active";
    
    private final UserStatusProperty status;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() { return status.get(); }
    
    public ReadOnlyIntegerProperty statusProperty() { return status.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    static {
        SQL_EXTENDED_FIELDNAMES = new String[] { COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE };
    }
    
    public UserImpl() {
        userName = new NonNullableStringProperty();
        password = new NonNullableStringProperty();
        status = new UserStatusProperty();
    }

    public Editable createEditable() { return new Editable(); }
    
    public static Iterable<UserImpl> getAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Iterable<UserImpl> getActive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static UserImpl getByPrimaryKey(int pk) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getAppointmentCount(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public class Editable extends EditableBase implements User {
        
        private final NonNullableStringProperty userName;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getUserName() { return userName.get(); }

        public void setUserName(String value) { userName.set(value); }

        public StringProperty userNameProperty() { return userName; }
        
        private final NonNullableStringProperty password;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPassword() { return password.get(); }

        public void setPassword(String value) { password.set(value); }

        public StringProperty passwordProperty() { return password; }

        private final UserStatusProperty status;

        /**
         * {@inheritDoc}
         */
        @Override
        public int getStatus() { return status.get(); }

        public void setStatus(int value) { status.set(value); }

        public IntegerProperty statusProperty() { return status; }

        private final BooleanBinding valid;
        
        @Override
        public final BooleanBinding isValid() { return valid; }
        
        public Editable() {
            userName = new NonNullableStringProperty(UserImpl.this.getUserName());
            password = new NonNullableStringProperty("");
            status = new UserStatusProperty(UserImpl.this.getStatus());
            valid = userName.isWhiteSpaceOrEmpty().not().and(password.isWhiteSpaceOrEmpty().and(UserImpl.this.password.isWhiteSpaceOrEmpty()).not());
        }

        @Override
        protected void beforeSaveChanges(Connection connection, SaveQueryBuilder queryBuilder) {
            if (userName.isWhiteSpaceOrEmpty().get())
                throw new IllegalStateException("User name cannot be empty");
            if (password.isWhiteSpaceOrEmpty().get() && UserImpl.this.password.isWhiteSpaceOrEmpty().get())
                throw new IllegalStateException("Password cannot be empty");
            super.beforeSaveChanges(connection, queryBuilder);
        }

        @Override
        protected void onBeforeDelete(Connection connection) throws IllegalStateException {
            if (getAppointmentCount(connection) > 0)
                throw new IllegalStateException("User has one or more appointments");
            super.onBeforeDelete(connection);
        }

        @Override
        public void undoChanges() {
            if (Platform.isFxApplicationThread()) {
                userName.set(UserImpl.this.getUserName());
                password.set("");
                status.set(UserImpl.this.getStatus());
            } else
                Platform.runLater(() -> {
                    undoChanges();
                });
        }

        @Override
        public void applyChanges() {
            if (Platform.isFxApplicationThread()) {
                UserImpl.this.userName.set(getUserName());
                String p = password.get();
                if (p.isEmpty())
                    UserImpl.this.password.set("");
                else {
                    PwHash pw = new PwHash(p, true);
                    UserImpl.this.password.set(pw.getEncodedHash());
                }
                UserImpl.this.status.set(getStatus());
            } else
                Platform.runLater(() -> {
                    applyChanges();
                });
        }

    }
}
