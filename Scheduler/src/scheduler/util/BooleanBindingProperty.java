package scheduler.util;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public abstract class BooleanBindingProperty extends BooleanBinding implements ReadOnlyProperty<Boolean> {

    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected BooleanBindingProperty(Object bean, String name, Observable... dependencies) {
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
