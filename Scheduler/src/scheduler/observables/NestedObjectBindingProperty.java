package scheduler.observables;

import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The parent object type.
 * @param <U> The nested binding type.
 */
public class NestedObjectBindingProperty<T, U> extends ObjectBindingProperty<U> {

    private final ObjectProperty<U> whenSourceNull;
    private final ObjectBinding<ObservableValue<U>> childBinding;
    private ObservableValue<U> currentChildBinding;

    public NestedObjectBindingProperty(Object bean, String name, ObservableValue<T> source, Function<T, ObservableValue<U>> getChildBinding,
            U whenSourceNull) {
        super(bean, name);
        this.whenSourceNull = new SimpleObjectProperty<>(whenSourceNull);
        currentChildBinding = this.whenSourceNull;
        childBinding = Bindings.createObjectBinding(() -> {
            T obj = source.getValue();
            ObservableValue<U> newChildBinding;
            if (null == obj || null == (newChildBinding = getChildBinding.apply(obj))) {
                newChildBinding = this.whenSourceNull;
            }
            if (!containsDependency(newChildBinding)) {
                currentChildBinding = newChildBinding;
                removeDependency(currentChildBinding);
                addDependency(newChildBinding);
            }
            return newChildBinding;
        }, source);
        super.bind(childBinding);
    }

    public NestedObjectBindingProperty(Object bean, String name, ObjectExpression<T> source, Function<T, ObservableValue<U>> getChildBinding) {
        this(bean, name, source, getChildBinding, null);
    }

    public U getWhenSourceNull() {
        return whenSourceNull.get();
    }

    public void setWhenSourceNull(U value) {
        whenSourceNull.set(value);
    }

    public ObjectProperty whenSourceNullProperty() {
        return whenSourceNull;
    }

    @Override
    protected U computeValue() {
        return childBinding.get().getValue();
    }

}
