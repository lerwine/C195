package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The input value type.
 */
public class CalculatedStringProperty<T> extends CalculatedStringExpression<T> implements ReadOnlyProperty<String> {

    private final Object bean;
    private final String name;
    private ReadOnlyStringProperty readOnlyStringProperty = null;

    public CalculatedStringProperty(Object bean, String name, ObservableValue<T> source, Function<T, String> calculate) {
        super(source, calculate);
        this.bean = bean;
        this.name = (null == name) ? "" : name;
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    public ReadOnlyStringProperty getReadOnlyStringProperty() {
        if (null == readOnlyStringProperty) {
            readOnlyStringProperty = new ReadOnlyStringProperty() {

                private ExpressionHelper<String> helper = null;

                @Override
                public String get() {
                    return CalculatedStringProperty.this.get();
                }

                @Override
                public void addListener(ChangeListener<? super String> listener) {
                    helper = ExpressionHelper.addListener(helper, this, listener);
                }

                @Override
                public void removeListener(ChangeListener<? super String> listener) {
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
                    return CalculatedStringProperty.this.getBean();
                }

                @Override
                public String getName() {
                    return CalculatedStringProperty.this.getName();
                }

            };
        }
        return readOnlyStringProperty;
    }

}
