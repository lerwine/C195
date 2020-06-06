package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @deprecated
 */
@Deprecated
public class WrappedObjectObservableProperty<T> extends DerivedObservable<T> implements ObservableObjectDerivitive<T>, ReadOnlyProperty<T> {

    private final ObservableValue<T> source;
    private final Object bean;
    private final String name;
    private ReadOnlyObjectProperty<T> readOnlyObjectProperty = null;

    public WrappedObjectObservableProperty(Object bean, String name, ObservableValue<T> source) {
        (this.source = source).addListener((observable) -> fireValueChangedEvent());
        this.bean = bean;
        this.name = (null == name) ? "" : name;
    }

    @Override
    public T getValue() {
        return get();
    }

    @Override
    public T get() {
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

    public ReadOnlyObjectProperty<T> getReadOnlyObjectProperty() {
        if (null == readOnlyObjectProperty) {
            readOnlyObjectProperty = new ReadOnlyObjectProperty<T>() {

                private ExpressionHelper<T> helper = null;

                @Override
                public T get() {
                    return WrappedObjectObservableProperty.this.get();
                }

                @Override
                public void addListener(ChangeListener<? super T> listener) {
                    helper = ExpressionHelper.addListener(helper, this, listener);
                }

                @Override
                public void removeListener(ChangeListener<? super T> listener) {
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
                    return WrappedObjectObservableProperty.this.getBean();
                }

                @Override
                public String getName() {
                    return WrappedObjectObservableProperty.this.getName();
                }

            };
        }
        return readOnlyObjectProperty;
    }

}
