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
 * Calculates a new value from an {@link ObservableValue}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of source value for the calculation.
 * @param <U> The type of calculated value.
 */
public class CalculatedObjectValueExpression<T, U> extends CalculatedObjectExpression<U> implements ObservableObjectValue<U> {

    private U value;

    private ObjectExpression<U> objectExpression;

    public CalculatedObjectValueExpression(ObservableValue<T> source, Function<T, U> calculate) {
        source.addListener((observable, oldValue, newValue) -> {
            U o = calculate.apply(newValue);
            if (!Objects.equals(value, o)) {
                value = o;
                fireValueChangedEvent();
            }
        });
        value = calculate.apply(source.getValue());
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
                    return CalculatedObjectValueExpression.this.get();
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
