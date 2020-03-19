/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author lerwi
 */
public abstract class StringBindingProperty extends StringBinding implements ReadOnlyProperty<String> {

    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected StringBindingProperty(Object bean, String name, Observable... dependencies) {
        this.bean = bean;
        this.name = name;
        this.dependencies = FXCollections.observableArrayList(dependencies);
        readOnlyDependencies = FXCollections.unmodifiableObservableList(this.dependencies);
        super.bind(dependencies);
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
        return readOnlyDependencies;
    }

    @Override
    public void dispose() {
        super.dispose();
        dependencies.forEach((t) -> super.unbind(t));
        dependencies.clear();
    }
}
