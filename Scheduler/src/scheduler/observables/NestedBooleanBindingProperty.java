package scheduler.observables;

import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.observables.NestedBooleanBindingProperty}
 */
public class NestedBooleanBindingProperty<T> extends BooleanBindingProperty {

    private final SimpleBooleanProperty whenSourceNull;
    private final ObjectBinding<ObservableValue<Boolean>> childBinding;
    private ObservableValue<Boolean> currentChildBinding;

    public NestedBooleanBindingProperty(Object bean, String name, ObservableValue<T> source, Function<T, ObservableValue<Boolean>> getChildBinding,
            boolean whenSourceNull) {
        super(bean, name);
        this.whenSourceNull = new SimpleBooleanProperty(whenSourceNull);
        currentChildBinding = this.whenSourceNull;
        childBinding = Bindings.createObjectBinding(() -> {
            T obj = source.getValue();
            ObservableValue<Boolean> newChildBinding;
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

    public NestedBooleanBindingProperty(Object bean, String name, ObjectExpression<T> source, Function<T, ObservableValue<Boolean>> getChildBinding) {
        this(bean, name, source, getChildBinding, false);
    }

    public boolean getWhenSourceNull() {
        return whenSourceNull.get();
    }

    public void setWhenSourceNull(boolean value) {
        whenSourceNull.set(value);
    }

    public BooleanProperty whenSourceNullProperty() {
        return whenSourceNull;
    }

    @Override
    protected boolean computeValue() {
        return childBinding.get().getValue();
    }

}
