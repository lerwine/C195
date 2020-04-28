package scheduler.observables;

import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The parent object type.
 */
public class NestedStringBindingProperty<T> extends StringBindingProperty {

    private final SimpleStringProperty whenSourceNull;
    private final ObjectBinding<ObservableValue<String>> childBinding;
    private ObservableValue<String> currentChildBinding;

    public NestedStringBindingProperty(Object bean, String name, ObservableValue<T> source, Function<T, ObservableValue<String>> getChildBinding,
            String whenSourceNull) {
        super(bean, name);
        this.whenSourceNull = new SimpleStringProperty(whenSourceNull);
        currentChildBinding = this.whenSourceNull;
        childBinding = Bindings.createObjectBinding(() -> {
            T obj = source.getValue();
            ObservableValue<String> newChildBinding;
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

    public NestedStringBindingProperty(Object bean, String name, ObjectExpression<T> source, Function<T, ObservableValue<String>> getChildBinding) {
        this(bean, name, source, getChildBinding, "");
    }

    public String getWhenSourceNull() {
        return whenSourceNull.get();
    }

    public void setWhenSourceNull(String value) {
        whenSourceNull.set(value);
    }

    public StringProperty whenSourceNullProperty() {
        return whenSourceNull;
    }

    @Override
    protected String computeValue() {
        return childBinding.get().getValue();
    }

}
