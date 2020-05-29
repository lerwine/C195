package scheduler.observables.property;

import java.util.Objects;
import java.util.concurrent.Callable;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ReadOnlyBooleanBindingProperty extends ReadOnlyBooleanProperty {

    private final BooleanBinding backingBinding;
    private final Object bean;
    private final String name;

    public ReadOnlyBooleanBindingProperty(Object bean, String name, BooleanBinding backingBinding) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        this.backingBinding = Objects.requireNonNull(backingBinding);
    }

    public ReadOnlyBooleanBindingProperty(Object bean, String name, Callable<Boolean> func, Observable... dependencies) {
        this(bean, name, Bindings.createBooleanBinding(func, dependencies));
    }

    @Override
    public boolean get() {
        return backingBinding.get();
    }

    @Override
    public void addListener(ChangeListener<? super Boolean> listener) {
        backingBinding.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Boolean> listener) {
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
