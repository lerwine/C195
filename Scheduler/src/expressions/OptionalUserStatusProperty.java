/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import java.util.Optional;
import scheduler.dao.User;

/**
 *
 * @author erwinel
 */
public class OptionalUserStatusProperty extends OptionalValueProperty<Integer> {

    public OptionalUserStatusProperty() {
        super(Optional.empty());
    }

    public OptionalUserStatusProperty(Optional<Integer> initialValue) {
        super(User.requireValidStatus(initialValue));
    }

    public OptionalUserStatusProperty(Object bean, String name) {
        super(bean, name, Optional.empty());
    }

    public OptionalUserStatusProperty(Object bean, String name, Optional<Integer> initialValue) {
        super(bean, name, User.requireValidStatus(initialValue));
    }

    @Override
    public void set(Optional<Integer> newValue) {
        super.set(User.requireValidStatus(newValue));
    }
    
}
