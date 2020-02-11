package scheduler.observables;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine
 */
public class ReadonlyActiveStateProperty extends ReadOnlyIntegerWrapper {

    public ReadonlyActiveStateProperty() { super(Values.USER_STATUS_NORMAL); }

    public ReadonlyActiveStateProperty(int initialValue) { super(Values.asValidUserStatus(initialValue)); }

    public ReadonlyActiveStateProperty(Object bean, String name) { super(bean, name, Values.USER_STATUS_NORMAL); }

    public ReadonlyActiveStateProperty(Object bean, String name, int initialValue) { super(bean, name, Values.asValidUserStatus(initialValue)); }

    @Override
    public void set(int newValue) { super.set(Values.asValidUserStatus(newValue)); }

    @Override
    public void setValue(Number v) {
        if (v != null) {
            if (v instanceof Integer) {
                super.set(Values.asValidUserStatus((int)v));
                return;
            }
            try {
                super.set(Values.asValidUserStatus(v.intValue()));
                return;
            } catch (Exception ex) { }
        }
        super.set(Values.USER_STATUS_INACTIVE);
    }
}
