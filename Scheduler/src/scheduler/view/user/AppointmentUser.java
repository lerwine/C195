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
import scheduler.dao.User;
import scheduler.dao.UserFactory;
import scheduler.view.ChildModel;

/**
 *
 * @author erwinel
 * @param <T>
 */
public interface AppointmentUser<T extends User> extends ChildModel<T> {
    String getUserName();
    ReadOnlyStringProperty userNameProperty();
    int getStatus();
    ReadOnlyIntegerProperty statusProperty();
    
    public static AppointmentUser<?> of(User dao) {
        if (null == dao)
            return null;
        if (dao instanceof UserFactory.UserImpl)
            return new UserModel((UserFactory.UserImpl)dao);
        return new AppointmentUser<User>() {
            private final ReadOnlyStringWrapper userName = new ReadOnlyStringWrapper(dao.getUserName());
            @Override
            public String getUserName() { return userName.get(); }
            @Override
            public ReadOnlyStringProperty userNameProperty() { return userName.getReadOnlyProperty(); }
            private final ReadOnlyIntegerWrapper status = new ReadOnlyIntegerWrapper(dao.getStatus());
            @Override
            public int getStatus() { return status.get(); }
            @Override
            public ReadOnlyIntegerProperty statusProperty() { return status.getReadOnlyProperty(); }
            @Override
            public User getDataObject() { return dao; }
        };
    }
}