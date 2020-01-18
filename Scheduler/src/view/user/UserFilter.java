/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.user;

import expressions.OptionalUserStatusProperty;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import scheduler.dao.DataObjectFilter;
import scheduler.dao.UserImpl;

/**
 *
 * @author erwinel
 */
public class UserFilter implements view.ModelFilter<UserImpl, UserModel> {

    //<editor-fold defaultstate="collapsed" desc="startRange property">
    
    private final OptionalUserStatusProperty status;
    
    public Optional<Integer> getStatus() { return status.get(); }
    
    public void setStatus(Optional<Integer> value) { status.set(value); }
    
    public OptionalUserStatusProperty statusProperty() { return status; }
    
    //</editor-fold>
    
    private final SimpleBooleanProperty statusNegated;

    public boolean isStatusNegated() { return statusNegated.get(); }

    public void setStatusNegated(boolean value) { statusNegated.set(value); }

    public BooleanProperty statusNegatedProperty() { return statusNegated; }
    
    public UserFilter() { this(null); }
    
    public UserFilter(UserFilter other) {
        if (null == other) {
            status = new OptionalUserStatusProperty();
            statusNegated = new SimpleBooleanProperty(false);
        } else {
            status = new OptionalUserStatusProperty(other.status.get());
            statusNegated = new SimpleBooleanProperty(other.statusNegated.get());
        }
    }

    @Override
    public DataObjectFilter<UserImpl> createClone() { return new UserFilter(this); }

    @Override
    public int setWhereParameters(PreparedStatement ps, int startIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toWhereClause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean test(UserModel t) {
        return t != null && status.fromPresence((v) -> (t.getStatus() == v) != statusNegated.get(), () -> true);
    }
    
}
