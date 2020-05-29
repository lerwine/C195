package scheduler.observables.property;

import java.util.Objects;
import java.util.concurrent.Callable;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ReadOnlyObjectBindingProperty<T> extends ReadOnlyObjectProperty<T> {

    private final ObjectBinding<T> backingBinding;
    private final Object bean;
    private final String name;

    public ReadOnlyObjectBindingProperty(Object bean, String name, ObjectBinding<T> backingBinding) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        this.backingBinding = Objects.requireNonNull(backingBinding);
    }

    public ReadOnlyObjectBindingProperty(Object bean, String name, Callable<T> func, Observable... dependencies) {
        this(bean, name, Bindings.createObjectBinding(func, dependencies));
    }

    @Override
    public T get() {
        return backingBinding.get();
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        backingBinding.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
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
