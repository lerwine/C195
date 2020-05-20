package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.Objects;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class NestedObjectValueExpression<T, U> extends CalculatedObjectExpression<U> implements ObservableObjectValue<U> {

    private final Function<T, ObservableValue<U>> selector;
    private ObservableValue<U> target;
    private U value = null;
    private ObjectExpression<U> objectExpression;

    public NestedObjectValueExpression(ObservableValue<T> source, Function<T, ObservableValue<U>> selector) {
        this.selector = selector;
        source.addListener(this::onSourceChange);
        onSourceChange(source, null, source.getValue());
    }

    private void onSourceChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            U u;
            synchronized (selector) {
                ObservableValue<U> newTarget;
                if (null != newValue) {
                    newTarget = selector.apply(newValue);
                } else {
                    newTarget = null;
                }
                if (Objects.equals(target, newTarget)) {
                    return;
                }
                if (null != target) {
                    target.removeListener(this::changed);
                }
                target = newTarget;
                if (null != target) {
                    target.addListener(this::changed);
                    u = target.getValue();
                } else {
                    u = null;
                }
            }
            onValueChange(u);
        }
    }

    private void changed(ObservableValue<? extends U> observable, U oldValue, U newValue) {
        onValueChange(newValue);
    }

    private void onValueChange(U newValue) {
        if (!Objects.equals(value, newValue)) {
            value = newValue;
            fireValueChangedEvent();
        }
    }

    @Override
    public U get() {
        return value;
    }

    @Override
    public U getValue() {
        return get();
    }

    public ObjectExpression<U> getObjectExpression() {
        if (null == objectExpression) {
            objectExpression = new ObjectExpression<U>() {

                private ExpressionHelper<U> helper = null;

                @Override
                public U get() {
                    return NestedObjectValueExpression.this.get();
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

            };
        }
        return objectExpression;
    }

}
