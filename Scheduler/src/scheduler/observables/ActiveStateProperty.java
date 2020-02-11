package scheduler.observables;

import javafx.beans.property.SimpleIntegerProperty;
import scheduler.util.Values;

/**
 * An integer property that only stores specific integer values that represent active status for users.
 * @author Leonard T. Erwine
 */
public class ActiveStateProperty extends SimpleIntegerProperty {
    /**
     * 
     */
    public ActiveStateProperty() { super(Values.USER_STATUS_NORMAL); }

    /**
     * 
     * @param initialValue
     */
    public ActiveStateProperty(int initialValue) { super(Values.asValidUserStatus(initialValue)); }

    /**
     * 
     * @param bean
     * @param name
     */
    public ActiveStateProperty(Object bean, String name) { super(bean, name, Values.USER_STATUS_NORMAL); }

    /**
     * 
     * @param bean
     * @param name
     * @param initialValue
     */
    public ActiveStateProperty(Object bean, String name, int initialValue) { super(bean, name, Values.asValidUserStatus(initialValue)); }

    
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
