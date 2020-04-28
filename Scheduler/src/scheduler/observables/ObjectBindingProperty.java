package scheduler.observables;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of the wrapped {@code Object}
 */
public abstract class ObjectBindingProperty<T> extends ObjectBinding<T> implements BindingProperty<T> {

    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected ObjectBindingProperty(Object bean, String name, Observable... dependencies) {
        this.bean = bean;
        this.name = name;
        if (null != dependencies && dependencies.length > 0) {
            this.dependencies = FXCollections.observableArrayList(dependencies);
            super.bind(dependencies);
        } else {
            this.dependencies = FXCollections.observableArrayList();
        }
        readOnlyDependencies = FXCollections.unmodifiableObservableList(this.dependencies);
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    protected boolean containsDependency(Observable observable) {
        return dependencies.contains(observable);
    }

    protected void addDependency(Observable ...observable) {
        if (null != observable && observable.length > 0) {
            for (final Observable dep : dependencies) {
                if (!dependencies.contains(dep)) {
                    super.bind(dep);
                    dependencies.add(dep);
                }
            }
        }
    }

    protected void removeDependency(Observable ...observable) {
        if (null != observable && observable.length > 0) {
            for (final Observable dep : dependencies) {
                if (dependencies.remove(dep)) {
                    super.unbind(dep);
                }
            }
        }
    }

    @Override
    public ObservableList<Observable> getDependencies() {
        return readOnlyDependencies;
    }

    @Override
    public void dispose() {
        super.dispose();
        dependencies.forEach((t) -> super.unbind(t));
        dependencies.clear();
    }
}
