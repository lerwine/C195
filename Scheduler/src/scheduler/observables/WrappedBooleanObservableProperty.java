package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class WrappedBooleanObservableProperty extends DerivedObservable<Boolean> implements ObservableBooleanDerivitive, ReadOnlyProperty<Boolean> {

    private final ObservableBooleanValue source;
    private final Object bean;
    private final String name;
    private ReadOnlyBooleanProperty readOnlyBooleanProperty = null;

    public WrappedBooleanObservableProperty(Object bean, String name, ObservableBooleanValue source) {
        (this.source = source).addListener((observable) -> fireValueChangedEvent());
        this.bean = bean;
        this.name = (null == name) ? "" : name;
    }

    @Override
    public Boolean getValue() {
        return get();
    }

    @Override
    public boolean get() {
        return source.get();
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
                    return WrappedBooleanObservableProperty.this.get();
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
                    return WrappedBooleanObservableProperty.this.getBean();
                }

                @Override
                public String getName() {
                    return WrappedBooleanObservableProperty.this.getName();
                }

            };
        }
        return readOnlyBooleanProperty;
    }

}
