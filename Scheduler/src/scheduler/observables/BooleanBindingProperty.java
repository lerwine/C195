package scheduler.observables;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class BooleanBindingProperty extends BooleanBinding implements ReadOnlyProperty<Boolean> {

    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected BooleanBindingProperty(Object bean, String name, Observable... dependencies) {
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

    protected void addDependency(Observable observable) {
        if (!dependencies.contains(observable)) {
            super.bind(observable);
            dependencies.add(observable);
        }
    }

    protected void removeDependency(Observable observable) {
        if (dependencies.remove(observable)) {
            super.unbind(observable);
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
