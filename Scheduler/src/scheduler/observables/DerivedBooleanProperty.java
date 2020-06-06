package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @deprecated 
 */
@Deprecated
public class DerivedBooleanProperty<T> extends DerivedObservableBoolean<T> implements ReadOnlyProperty<Boolean> {

    private final Object bean;
    private final String name;
    private ReadOnlyBooleanProperty readOnlyBooleanProperty = null;

    public DerivedBooleanProperty(Object bean, String name, ObservableValue<T> source, Predicate<T> calculate) {
        super(source, calculate);
        this.bean = bean;
        this.name = (null == name) ? "" : name;
    }

    public DerivedBooleanProperty(Object bean, String name, DerivedObservableBoolean<T> source) {
        super(source);
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

    public ReadOnlyBooleanProperty getReadOnlyBooleanProperty() {
        if (null == readOnlyBooleanProperty) {
            readOnlyBooleanProperty = new ReadOnlyBooleanProperty() {

                private ExpressionHelper<Boolean> helper = null;

                @Override
                public boolean get() {
                    return DerivedBooleanProperty.this.get();
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
                    return DerivedBooleanProperty.this.getBean();
                }

                @Override
                public String getName() {
                    return DerivedBooleanProperty.this.getName();
                }

            };
        }
        return readOnlyBooleanProperty;
    }

}
