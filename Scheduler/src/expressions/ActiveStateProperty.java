package expressions;

import javafx.beans.property.SimpleIntegerProperty;
import scheduler.dao.factory.UserFactory;

/**
 * An integer property that only stores specific integer values that represent active status for users.
 * @author Leonard T. Erwine
 */
public class ActiveStateProperty extends SimpleIntegerProperty {
    public static int asValidValue(int value) {
        return (value < UserFactory.STATUS_INACTIVE) ? UserFactory.STATUS_INACTIVE : ((value > UserFactory.STATUS_ADMIN) ? UserFactory.STATUS_ADMIN : value);
    }
    
    /**
     * 
     */
    public ActiveStateProperty() { super(UserFactory.STATUS_USER); }

    /**
     * 
     * @param initialValue
     */
    public ActiveStateProperty(int initialValue) { super(asValidValue(initialValue)); }

    /**
     * 
     * @param bean
     * @param name
     */
    public ActiveStateProperty(Object bean, String name) { super(bean, name, UserFactory.STATUS_USER); }

    /**
     * 
     * @param bean
     * @param name
     * @param initialValue
     */
    public ActiveStateProperty(Object bean, String name, int initialValue) { super(bean, name, asValidValue(initialValue)); }

    
    @Override
    public void set(int newValue) { super.set(asValidValue(newValue)); }

    @Override
    public void setValue(Number v) {
        if (v != null) {
            if (v instanceof Integer) {
                super.set(asValidValue((int)v));
                return;
            }
            try {
                super.set(asValidValue(v.intValue()));
                return;
            } catch (Exception ex) { }
        }
        super.set(UserFactory.STATUS_INACTIVE);
    }
}
