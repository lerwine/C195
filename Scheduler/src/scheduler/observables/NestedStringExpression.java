package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.Objects;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class NestedStringExpression<T> extends CalculatedObjectExpression<String> implements ObservableStringValue {

    private final Function<T, ObservableValue<String>> selector;
    private ObservableValue<String> target;
    private String value = null;
    private StringExpression stringExpression;

    public NestedStringExpression(ObservableValue<T> source, Function<T, ObservableValue<String>> selector) {
        this.selector = selector;
        source.addListener(this::onSourceChange);
        onSourceChange(source, null, source.getValue());
    }

    private void onSourceChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            String u;
            synchronized (selector) {
                ObservableValue<String> newTarget;
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

    private void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        onValueChange(newValue);
    }

    private void onValueChange(String newValue) {
        if (!Objects.equals(value, newValue)) {
            value = newValue;
            fireValueChangedEvent();
        }
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public String getValue() {
        return get();
    }

    public StringExpression getStringExpression() {
        if (null == stringExpression) {
            stringExpression = new StringExpression() {

                private ExpressionHelper<String> helper = null;

                @Override
                public String get() {
                    return NestedStringExpression.this.get();
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

            };
        }
        return stringExpression;
    }

}
