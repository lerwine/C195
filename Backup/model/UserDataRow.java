/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author lerwi
 */
public final class UserDataRow extends DataRow implements IUserDataRow {
    
    private String userName;

    public static final String PROP_USERNAME = "userName";

    /**
     * Get the value of userName
     *
     * @return the value of userName
     */
    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * Set the value of userName
     *
     * @param userName new value of userName
     */
    public void setUserName(String userName) {
        String oldUserName = this.userName;
        this.userName = userName;
        firePropertyChange(PROP_USERNAME, oldUserName, userName);
    }

}
