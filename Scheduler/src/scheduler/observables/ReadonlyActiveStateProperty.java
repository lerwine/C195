package scheduler.observables;

import javafx.beans.property.ReadOnlyObjectWrapper;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class ReadonlyActiveStateProperty extends ReadOnlyObjectWrapper<UserStatus> {

    public ReadonlyActiveStateProperty() {
        super(UserStatus.NORMAL);
    }

    public ReadonlyActiveStateProperty(UserStatus initialValue) {
        super((null == initialValue) ? UserStatus.INACTIVE : initialValue);
    }

    public ReadonlyActiveStateProperty(Object bean, String name) {
        super(bean, name, UserStatus.NORMAL);
    }

    public ReadonlyActiveStateProperty(Object bean, String name, UserStatus initialValue) {
        super(bean, name, (null == initialValue) ? UserStatus.INACTIVE : initialValue);
    }

    @Override
    public void set(UserStatus newValue) {
        super.set((null == newValue) ? UserStatus.INACTIVE : newValue);
    }

    @Override
    public void setValue(UserStatus v) {
        if (v != null) {
            if (v instanceof UserStatus) {
                super.set((UserStatus) v);
                return;
            }
        }
        super.set(UserStatus.INACTIVE);
    }
}
