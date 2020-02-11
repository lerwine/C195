/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.user;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.UserFactory;
import scheduler.view.ItemModel;

/**
 *
 * @author erwinel
 */
public class UserModel extends ItemModel<UserFactory.UserImpl> implements AppointmentUser<UserFactory.UserImpl> {

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
    
    public UserModel(UserFactory.UserImpl dao) {
        super(dao);
        this.userName = new ReadOnlyStringWrapper(dao.getUserName());
        this.status = new ReadOnlyIntegerWrapper(dao.getStatus());
    }

    @Override
    protected void refreshFromDAO(UserFactory.UserImpl dao) {
        userName.set(dao.getUserName());
        status.set(dao.getStatus());
    }
}
