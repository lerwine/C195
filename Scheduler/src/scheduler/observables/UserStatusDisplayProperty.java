/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.observables;

import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.util.Values;

/**
 *
 * @author lerwi
 */
public class UserStatusDisplayProperty extends StringBinding implements ReadOnlyProperty<String> {

    private final ReadOnlyIntegerProperty backingProperty;
    private final Object bean;
    private final String name;

    public UserStatusDisplayProperty(Object bean, String name, ReadOnlyIntegerProperty statusProperty) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        super.bind(Objects.requireNonNull(backingProperty = Objects.requireNonNull(statusProperty)));
    }

    @Override
    protected String computeValue() {
        return Values.toUserStatusDisplay(backingProperty.get());
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.singletonObservableList(backingProperty);
    }

    @Override
    public void dispose() {
        super.unbind(backingProperty);
    }

}
