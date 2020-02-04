/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.user;

import java.sql.Connection;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.UserImpl;
import scheduler.view.ItemModel;

/**
 *
 * @author erwinel
 */
public class UserModel extends ItemModel<UserImpl> implements AppointmentUser<UserImpl> {

    private final ReadOnlyStringWrapper userName;

    @Override
    public String getUserName() { return userName.get(); }

    @Override
    public ReadOnlyStringProperty userNameProperty() { return userName.getReadOnlyProperty(); }
    
    private final ReadOnlyIntegerWrapper status;

    @Override
    public int getStatus() { return status.get(); }

    @Override
    public ReadOnlyIntegerProperty statusProperty() { return status.getReadOnlyProperty(); }
    
    public UserModel(UserImpl dao) {
        super(dao);
        this.userName = new ReadOnlyStringWrapper(dao.getUserName());
        this.status = new ReadOnlyIntegerWrapper(dao.getStatus());
    }

    @Override
    public void saveChanges(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
