package expressions;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import scheduler.dao.UserFactory;

/**
 *
 * @author Leonard T. Erwine
 */
public class ReadonlyActiveStateProperty extends ReadOnlyIntegerWrapper {

    public ReadonlyActiveStateProperty() { super(UserFactory.STATUS_USER); }

    public ReadonlyActiveStateProperty(int initialValue) { super(ActiveStateProperty.asValidValue(initialValue)); }

    public ReadonlyActiveStateProperty(Object bean, String name) { super(bean, name, UserFactory.STATUS_USER); }

    public ReadonlyActiveStateProperty(Object bean, String name, int initialValue) { super(bean, name, ActiveStateProperty.asValidValue(initialValue)); }

    @Override
    public void set(int newValue) { super.set(ActiveStateProperty.asValidValue(newValue)); }

    @Override
    public void setValue(Number v) {
        if (v != null) {
            if (v instanceof Integer) {
                super.set(ActiveStateProperty.asValidValue((int)v));
                return;
            }
            try {
                super.set(ActiveStateProperty.asValidValue(v.intValue()));
                return;
            } catch (Exception ex) { }
        }
        super.set(UserFactory.STATUS_INACTIVE);
    }
}
