package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class NestedBooleanProperty<T> extends NestedBooleanDerivation<T> implements ReadOnlyProperty<Boolean> {

    private final Object bean;
    private final String name;
    private ReadOnlyBooleanProperty readOnlyBooleanProperty = null;

    public NestedBooleanProperty(Object bean, String name, ObservableValue<T> source, Function<T, ObservableBooleanValue> selector, Optional<Boolean> defaultValue) {
        super(source, selector, defaultValue);
        this.bean = bean;
        this.name = (null == name) ? "" : name;
    }

    public NestedBooleanProperty(Object bean, String name, ObservableValue<T> source, Function<T, ObservableBooleanValue> selector) {
        this(bean, name, source, selector, Optional.empty());
    }
    
    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    public ReadOnlyBooleanProperty getReadOnlyBooleanProperty() {
        if (null == readOnlyBooleanProperty) {
            readOnlyBooleanProperty = new ReadOnlyBooleanProperty() {

                private ExpressionHelper<Boolean> helper = null;

                @Override
                public boolean get() {
                    return NestedBooleanProperty.this.get();
                }

                @Override
                public void addListener(ChangeListener<? super Boolean> listener) {
                    helper = ExpressionHelper.addListener(helper, this, listener);
                }

                @Override
                public void removeListener(ChangeListener<? super Boolean> listener) {
                    helper = ExpressionHelper.removeListener(helper, listener);
                }

                @Override
                public void addListener(InvalidationListener listener) {
                    helper = ExpressionHelper.addListener(helper, this, listener);
                }

                @Override
                public void removeListener(InvalidationListener listener) {
                    helper = ExpressionHelper.removeListener(helper, listener);
                }

                @Override
                public Object getBean() {
                    return NestedBooleanProperty.this.getBean();
                }

                @Override
                public String getName() {
                    return NestedBooleanProperty.this.getName();
                }

            };
        }
        return readOnlyBooleanProperty;
    }

}
