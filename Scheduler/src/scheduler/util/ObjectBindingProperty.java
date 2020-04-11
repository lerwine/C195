package scheduler.util;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class ObjectBindingProperty<T> extends ObjectBinding<T> implements ReadOnlyProperty<T> {

    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected ObjectBindingProperty(Object bean, String name, Observable... dependencies) {
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
