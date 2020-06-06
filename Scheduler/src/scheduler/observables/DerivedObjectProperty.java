package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @deprecated
 */
@Deprecated
public class DerivedObjectProperty<T, U> extends DerivedObservableObject<T, U> implements ReadOnlyProperty<U> {

    private final Object bean;
    private final String name;
    private ReadOnlyObjectProperty<U> readOnlyObjectProperty = null;

    public DerivedObjectProperty(Object bean, String name, ObservableValue<T> source, Function<T, U> calculate) {
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

    public ReadOnlyObjectProperty<U> getReadOnlyObjectProperty() {
        if (null == readOnlyObjectProperty) {
            readOnlyObjectProperty = new ReadOnlyObjectProperty<U>() {

                private ExpressionHelper<U> helper = null;

                @Override
                public U get() {
                    return DerivedObjectProperty.this.get();
                }

                @Override
                public void addListener(ChangeListener<? super U> listener) {
                    helper = ExpressionHelper.addListener(helper, this, listener);
                }

                @Override
                public void removeListener(ChangeListener<? super U> listener) {
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
                    return DerivedObjectProperty.this.getBean();
                }

                @Override
                public String getName() {
                    return DerivedObjectProperty.this.getName();
                }

            };
        }
        return readOnlyObjectProperty;
    }

}
