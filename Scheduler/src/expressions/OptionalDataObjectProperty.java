/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import java.util.Objects;
import java.util.Optional;
import scheduler.dao.DataObject;

/**
 *
 * @author erwinel
 * @param <T>
 */
public class OptionalDataObjectProperty<T extends DataObject> extends OptionalValueProperty<T> {

    public OptionalDataObjectProperty() {
    }

    public OptionalDataObjectProperty(Optional<T> initialValue) {
        super(requireExisting(initialValue, "Object does not exist"));
    }

    public OptionalDataObjectProperty(Object bean, String name) {
        super(bean, name);
    }

    public OptionalDataObjectProperty(Object bean, String name, Optional<T> initialValue) {
        super(bean, name, requireExisting(initialValue, "Object does not exist"));
    }

    @Override
    public void set(Optional<T> newValue) {
        super.set(requireExisting(newValue, "Object does not exist")); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static <V extends DataObject> Optional<V> requireExisting(Optional<V> value, String message) {
        if (value != null)
            value.ifPresent((t) -> {
                assert t.isExisting() : message;
            });
        return value;
    }
}
