package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.function.BiFunction;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import scheduler.util.QuadFunction;
import scheduler.util.TriFunction;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ObservableStringDerivitive extends ObservableDerivitive<String>, ObservableStringValue {

    public static <T, U, S, V> ObservableStringDerivitive of(ObservableValue<T> s1, ObservableValue<U> s2, ObservableValue<S> s3, ObservableValue<V> s4, QuadFunction<T, U, S, V, String> calculate) {
        return new DerivedObservableString<>(new ObservableQuadruplet<>(s1, s2, s3, s4), (t) -> calculate.apply(t.getValue1(), t.getValue2(), t.getValue3(), t.getValue4()));
    }
    
    public static <T, U, S> ObservableStringDerivitive of(ObservableValue<T> s1, ObservableValue<U> s2, ObservableValue<S> s3, TriFunction<T, U, S, String> calculate) {
        return new DerivedObservableString<>(new ObservableTriplet<>(s1, s2, s3), (t) -> calculate.apply(t.getValue1(), t.getValue2(), t.getValue3()));
    }
    
    public static <T, U> ObservableStringDerivitive of(ObservableValue<T> s1, ObservableValue<U> s2, BiFunction<T, U, String> calculate) {
        return new DerivedObservableString<>(new ObservableTuple<>(s1, s2), (t) -> calculate.apply(t.getValue1(), t.getValue2()));
    }
    
    public static <T> ObservableStringDerivitive of(ObservableValue<T> source, Function<T, String> calculate) {
        return new DerivedObservableString<>(source, calculate);
    }
    
    public static <T> ObservableStringDerivitive ofNested(ObservableValue<T> source, Function<T, ObservableValue<String>> selector) {
        return new NestedStringDerivation(source, selector);
    }

    default DerivedObservableBoolean isNull() {
        return new DerivedObservableBoolean<>(this, (t) -> null == t);
    }

    default DerivedObservableBoolean isNullOrEmpty() {
        return new DerivedObservableBoolean<>(this, (t) -> null == t || t.isEmpty());
    }

    default DerivedObservableBoolean isNullOrWhitespace() {
        return new DerivedObservableBoolean<>(this, (t) -> null == t || t.trim().isEmpty());
    }

    default DerivedObservableBoolean isNotNull() {
        return new DerivedObservableBoolean<>(this, (t) -> null != t);
    }

    default DerivedObservableBoolean isNotNullOrEmpty() {
        return new DerivedObservableBoolean<>(this, (t) -> null != t && !t.isEmpty());
    }

    default DerivedObservableBoolean isNotNullOrWhitespace() {
        return new DerivedObservableBoolean<>(this, (t) -> null != t && !t.trim().isEmpty());
    }

    default StringExpression asStringExpression() {
        return new StringExpression() {

            private ExpressionHelper<String> helper = null;

            {
                ObservableStringDerivitive.this.addListener((observable) -> ExpressionHelper.fireValueChangedEvent(helper));
            }

            @Override
            public String get() {
                return ObservableStringDerivitive.this.get();
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

}
