package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

/**
 * Calculates a boolean value from an {@link ObservableValue}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The source value for the calculation.
 */
public class CalculatedBooleanExpression<T> extends CalculatedObjectExpression<Boolean> implements ObservableBooleanValue {

    private boolean value;

    private BooleanExpression booleanExpression = null;

    public CalculatedBooleanExpression(ObservableValue<T> source, Predicate<T> calculate) {
        value = calculate.test(source.getValue());
        source.addListener((observable, oldValue, newValue) -> {
            boolean b = calculate.test(newValue);
            if (b != value) {
                value = b;
                fireValueChangedEvent();
            }
        });
    }

    @Override
    public Boolean getValue() {
        return get();
    }

    @Override
    public boolean get() {
        return value;
    }

    public BooleanExpression getBooleanExpression() {
        if (null == booleanExpression) {
            booleanExpression = new BooleanExpression() {

                private ExpressionHelper<Boolean> helper = null;

                @Override
                public boolean get() {
                    return CalculatedBooleanExpression.this.get();
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

            };
        }
        return booleanExpression;
    }

}
