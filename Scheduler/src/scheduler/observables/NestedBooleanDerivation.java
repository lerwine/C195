package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class NestedBooleanDerivation<T> extends DerivedObservable<Boolean> implements ObservableBooleanDerivitive {

    private final Function<T, ObservableBooleanValue> selector;
    private final Optional<Boolean> defaultValue;
    private ObservableBooleanValue target;
    private boolean value;
    private BooleanExpression booleanExpression;

    public NestedBooleanDerivation(ObservableValue<T> source, Function<T, ObservableBooleanValue> selector, Optional<Boolean> defaultValue) {
        this.selector = selector;
        this.defaultValue = (null == defaultValue) ? Optional.empty() : defaultValue;
        value = this.defaultValue.orElse(false);
        source.addListener(this::onSourceChange);
        onSourceChange(source, null, source.getValue());
    }

    public NestedBooleanDerivation(ObservableValue<T> source, Function<T, ObservableBooleanValue> selector, boolean defaultValue) {
        this(source, selector, Optional.of(defaultValue));
    }

    public NestedBooleanDerivation(ObservableValue<T> source, Function<T, ObservableBooleanValue> selector) {
        this(source, selector, Optional.empty());
    }

    private void onSourceChange(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            boolean u;
            synchronized (selector) {
                ObservableBooleanValue newTarget;
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
                    if (defaultValue.isPresent()) {
                        u = defaultValue.get();
                    } else {
                        return;
                    }
                }
            }
            onValueChange(u);
        }
    }

    private void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        onValueChange(newValue);
    }

    private void onValueChange(boolean newValue) {
        if (value != newValue) {
            value = newValue;
            fireValueChangedEvent();
        }
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
                    return NestedBooleanDerivation.this.get();
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
