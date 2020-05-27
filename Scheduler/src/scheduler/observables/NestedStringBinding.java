package scheduler.observables;

import java.util.concurrent.Callable;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The value type for the input {@link ObservableValue}.
 */
public class NestedStringBinding<T> extends StringBinding {
    private final ObservableList<ObservableValue<?>> dependencies;
    private final ObjectBinding<ObservableValue<String>> nestedBinding;

    public NestedStringBinding(ObservableValue<T> source, Function<T, ObservableValue<String>> selector) {
        dependencies = FXCollections.observableArrayList();
        // Create intermediate binding that also updates the dependency bindings when the source value changes.
        nestedBinding = Bindings.createObjectBinding(new Callable<ObservableValue<String>>() {
            // Track the current nested dependency so we know when it is being replaced.
            private ObservableValue<String> nestedDependency;
            {
                T value = source.getValue();
                nestedDependency = (null == value) ? null  : selector.apply(value);
                if (null != nestedDependency) {
                    NestedStringBinding.super.bind(nestedDependency);
                    dependencies.add(nestedDependency);
                }
            }
            @Override
            public ObservableValue<String> call() throws Exception {
                // If this is empty, then the object has already been disposed.
                if (dependencies.isEmpty()) {
                    throw new IllegalStateException();
                }
                // Get the source object which will be used to get the new observable value.
                T target = source.getValue();
                ObservableValue<String> newDep = (null == target) ? null : selector.apply(target);
                // Are we removing an old dependency?
                if (null != nestedDependency) {
                    // ...in the off-chance that the new and old values return the same nested dependency, do nothing.
                    if (nestedDependency == newDep) {
                        return nestedDependency;
                    }
                    // Remove and unbind the old dependency.
                    dependencies.remove(nestedDependency);
                    NestedStringBinding.super.unbind(nestedDependency);
                }
                if (null != (nestedDependency = newDep)) {
                    // Add and bind the new dependency.
                    dependencies.add(nestedDependency);
                    NestedStringBinding.super.bind(nestedDependency);
                }
                return nestedDependency;
            }
        }, source);
        super.bind(nestedBinding);
    }

    /**
     * Gets the value that is returned by {@link #computeValue()} when there is no nested {@link ObservableValue} object. This can happen either when
     * the source {@link ObservableValue} produces a {@code null} value or the selector {@link Function} returns {@code null} instead of an
     * {@link ObservableValue} object.
     *
     * @return The value that is returned by {@link #computeValue()} when there is no nested {@link ObservableValue} object. This will be
     * {@code null} unless this method is overloaded.
     */
    protected String getMissingNestedObservableValue() {
        return null;
    }

    @Override
    protected String computeValue() {
        ObservableValue<String> dep = nestedBinding.get();
        return (null == dep) ? getMissingNestedObservableValue() : dep.getValue();
    }

    @Override
    public ObservableList<ObservableValue<?>> getDependencies() {
        return FXCollections.unmodifiableObservableList(dependencies);
    }

    @Override
    public void dispose() {
        if (!dependencies.isEmpty()) {
            dependencies.forEach((d) -> super.unbind(d));
            dependencies.clear();
            nestedBinding.dispose();
        }
        super.dispose();
    }

}
