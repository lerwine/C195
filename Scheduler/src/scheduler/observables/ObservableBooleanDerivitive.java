package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import scheduler.util.QuadPredicate;
import scheduler.util.TriPredicate;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @deprecated
 */
@Deprecated
public interface ObservableBooleanDerivitive extends ObservableDerivitive<Boolean>, ObservableBooleanValue {

    public static <T, U, S, V> ObservableBooleanDerivitive of(ObservableValue<T> s1, ObservableValue<U> s2, ObservableValue<S> s3, ObservableValue<V> s4, QuadPredicate<T, U, S, V> calculate) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(s1, s2, s3, s4), (t) -> calculate.test(t.getValue1(), t.getValue2(), t.getValue3(), t.getValue4()));
    }

    public static <T, U, S> ObservableBooleanDerivitive of(ObservableValue<T> s1, ObservableValue<U> s2, ObservableValue<S> s3, TriPredicate<T, U, S> calculate) {
        return new DerivedObservableBoolean<>(new ObservableTriplet<>(s1, s2, s3), (t) -> calculate.test(t.getValue1(), t.getValue2(), t.getValue3()));
    }

    public static <T, U> ObservableBooleanDerivitive of(ObservableValue<T> s1, ObservableValue<U> s2, BiPredicate<T, U> calculate) {
        return new DerivedObservableBoolean<>(new ObservableTuple<>(s1, s2), (t) -> calculate.test(t.getValue1(), t.getValue2()));
    }

    public static <T> ObservableBooleanDerivitive of(ObservableValue<T> source, Predicate<T> calculate) {
        return new DerivedObservableBoolean<>(source, calculate);
    }

    public static <T> ObservableBooleanDerivitive ofNested(ObservableValue<T> source, Function<T, ObservableBooleanValue> selector, Optional<Boolean> defaultValue) {
        return new NestedBooleanDerivation<>(source, selector, defaultValue);
    }

    public static <T> ObservableBooleanDerivitive ofNested(ObservableValue<T> source, Function<T, ObservableBooleanValue> selector, boolean defaultValue) {
        return new NestedBooleanDerivation<>(source, selector, defaultValue);
    }

    public static <T> ObservableBooleanDerivitive ofNested(ObservableValue<T> source, Function<T, ObservableBooleanValue> selector) {
        return new NestedBooleanDerivation<>(source, selector);
    }

    default ObservableBooleanDerivitive not() {
        return new DerivedObservableBoolean<>(this, (t) -> !t);
    }

    default ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5, ObservableBooleanValue b6) {
        return ObservableDerivitive.and(this, b1, b2, b3, b4, b5, b6);
    }

    default ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5) {
        return ObservableDerivitive.and(this, b1, b2, b3, b4, b5);
    }

    default ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4) {
        return ObservableDerivitive.and(this, b1, b2, b3, b4);
    }

    default ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3) {
        return ObservableDerivitive.and(this, b1, b2, b3);
    }

    default ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2) {
        return ObservableDerivitive.and(this, b1, b2);
    }

    default ObservableBooleanDerivitive and(ObservableBooleanValue other) {
        return ObservableDerivitive.and(this, other);
    }

    default ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5, ObservableBooleanValue b6) {
        return ObservableDerivitive.or(this, b1, b2, b3, b4, b5, b6);
    }

    default ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5) {
        return ObservableDerivitive.or(this, b1, b2, b3, b4, b5);
    }

    default ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4) {
        return ObservableDerivitive.or(this, b1, b2, b3, b4);
    }

    default ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3) {
        return ObservableDerivitive.or(this, b1, b2, b3);
    }

    default ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2) {
        return ObservableDerivitive.or(this, b1, b2);
    }

    default ObservableBooleanDerivitive or(ObservableBooleanValue other) {
        return ObservableDerivitive.or(this, other);
    }

    default BooleanExpression getBooleanExpression() {
        return new BooleanExpression() {
            private ExpressionHelper<Boolean> helper = null;

            {
                ObservableBooleanDerivitive.this.addListener((observable) -> ExpressionHelper.fireValueChangedEvent(helper));
            }

            @Override
            public boolean get() {
                return ObservableBooleanDerivitive.this.get();
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

}
