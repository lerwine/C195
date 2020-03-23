package scheduler.observables;

import javafx.beans.property.SimpleObjectProperty;
import scheduler.dao.UserStatus;

/**
 * An integer property that only stores specific integer values that represent active status for users.
 *
 * @author Leonard T. Erwine
 */
public class ActiveStateProperty extends SimpleObjectProperty<UserStatus> {

    /**
     *
     */
    public ActiveStateProperty() {
        super(UserStatus.NORMAL);
    }

    /**
     *
     * @param initialValue
     */
    public ActiveStateProperty(UserStatus initialValue) {
        super((null == initialValue) ? UserStatus.NORMAL : initialValue);
    }

    /**
     *
     * @param bean
     * @param name
     */
    public ActiveStateProperty(Object bean, String name) {
        super(bean, name, UserStatus.NORMAL);
    }

    /**
     *
     * @param bean
     * @param name
     * @param initialValue
     */
    public ActiveStateProperty(Object bean, String name, UserStatus initialValue) {
        super(bean, name, (null == initialValue) ? UserStatus.NORMAL : initialValue);
    }

    @Override
    public void set(UserStatus newValue) {
        super.set((null == newValue) ? UserStatus.NORMAL : newValue);
    }

    @Override
    public void setValue(UserStatus v) {
        if (v != null) {
            if (v instanceof UserStatus) {
                super.set(v);
                return;
            }
        }
        super.set(UserStatus.INACTIVE);
    }
}
