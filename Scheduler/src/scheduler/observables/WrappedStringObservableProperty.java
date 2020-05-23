package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class WrappedStringObservableProperty extends DerivedObservable<String> implements ObservableStringDerivitive, ReadOnlyProperty<String> {

    private final ObservableValue<String> source;
    private final Object bean;
    private final String name;
    private ReadOnlyStringProperty readOnlyStringProperty = null;

    public WrappedStringObservableProperty(Object bean, String name, ObservableValue<String> source) {
        (this.source = source).addListener((observable) -> fireValueChangedEvent());
        this.bean = bean;
        this.name = (null == name) ? "" : name;
    }

    @Override
    public String getValue() {
        return get();
    }

    @Override
    public String get() {
        return source.getValue();
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
                    return WrappedStringObservableProperty.this.get();
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
                    return WrappedStringObservableProperty.this.getBean();
                }

                @Override
                public String getName() {
                    return WrappedStringObservableProperty.this.getName();
                }

            };
        }
        return readOnlyStringProperty;
    }

}
