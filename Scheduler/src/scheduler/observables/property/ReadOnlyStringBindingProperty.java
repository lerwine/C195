package scheduler.observables.property;

import java.util.Objects;
import java.util.concurrent.Callable;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ReadOnlyStringBindingProperty extends ReadOnlyStringProperty {

    private final StringBinding backingBinding;
    private final Object bean;
    private final String name;

    public ReadOnlyStringBindingProperty(Object bean, String name, StringBinding backingBinding) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        this.backingBinding = Objects.requireNonNull(backingBinding);
    }

    public ReadOnlyStringBindingProperty(Object bean, String name, Callable<String> func, Observable... dependencies) {
        this(bean, name, Bindings.createStringBinding(func, dependencies));
    }

    @Override
    public String get() {
        return backingBinding.get();
    }

    @Override
    public void addListener(ChangeListener<? super String> listener) {
        backingBinding.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super String> listener) {
        backingBinding.removeListener(listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        backingBinding.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        backingBinding.removeListener(listener);
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }
}
