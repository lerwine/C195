/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.util.Optional;
import scheduler.AppResources;

/**
 *
 * @author lerwi
 */
public enum UserStatus {
    NORMAL(1),
    ADMIN(2),
    INACTIVE(0);

    public static String toDisplayValue(UserStatus value) {
        switch (value) {
            case INACTIVE:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_INACTIVE);
            case ADMIN:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_ADMINISTRATOR);
            default:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_ACTIVE);
        }
    }
    
    private final int value;

    public int getValue() {
        return value;
    }
    
    private UserStatus(int value) {
        this.value = value;
    }
    
    public static UserStatus of(int dbValue, UserStatus defaultValue) {
        for (UserStatus t : UserStatus.values()) {
            if (t.getValue() == dbValue) {
                return t;
            }
        }
        return defaultValue;
    }

    public static Optional<UserStatus> of(int dbValue) {
        for (UserStatus t : UserStatus.values()) {
            if (t.getValue() == dbValue) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
    
}
