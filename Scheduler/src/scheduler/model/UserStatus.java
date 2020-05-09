package scheduler.model;

import java.util.Optional;
import scheduler.AppResourceKeys;
import scheduler.AppResources;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum UserStatus {
    NORMAL(1),
    ADMIN(2),
    INACTIVE(0);

    public static String toDisplayValue(UserStatus value) {
        switch (value) {
            case INACTIVE:
                return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_INACTIVE);
            case ADMIN:
                return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_ADMINISTRATOR);
            default:
                return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_ACTIVE);
        }
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

    private final int value;

    private UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
