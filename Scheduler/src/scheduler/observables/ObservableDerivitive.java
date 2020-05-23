package scheduler.observables;

import java.util.Objects;
import java.util.function.Function;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import scheduler.util.Values;

/**
 * Base interfaces for Observable values derived other observable values.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <R> The result (derived) value type.
 */
public interface ObservableDerivitive<R> extends ObservableValue<R> {

    public static <T> ObservableBooleanDerivitive areEqual(ObservableValue<T> a, ObservableValue<T> b) {
        return new DerivedObservableBoolean<>(new ObservableTuple<>(a, b), (t) -> Objects.equals(t.getValue1(), t.getValue2()));
    }
    
    public static <T> ObservableBooleanDerivitive areNotEqual(ObservableValue<T> a, ObservableValue<T> b) {
        return new DerivedObservableBoolean<>(new ObservableTuple<>(a, b), (t) -> !Objects.equals(t.getValue1(), t.getValue2()));
    }
    
    public static <T> ObservableStringDerivitive asString(ObservableValue<T> source, Function<T, String> func) {
        return ObservableStringDerivitive.of(source, func);
    }
    
    public static ObservableStringDerivitive wsNormalized(ObservableValue<String> source) {
        return new DerivedObservableString<>(source, Values::asNonNullAndWsNormalized);
    }
    
    public static DerivedObservableBoolean isNull(ObservableValue<?> source) {
        return new DerivedObservableBoolean<>(source, (t) -> null == t);
    }

    public static DerivedObservableBoolean isNullOrEmpty(ObservableValue<String> source) {
        return new DerivedObservableBoolean<>(source, (t) -> null == t || t.isEmpty());
    }

    public static DerivedObservableBoolean isNotNullOrWhiteSpace(ObservableValue<String> source) {
        return new DerivedObservableBoolean<>(source, (t) -> null == t || t.trim().isEmpty());
    }

    public static DerivedObservableBoolean isNotNull(ObservableValue<?> source) {
        return new DerivedObservableBoolean<>(source, (t) -> null != t);
    }

    public static DerivedObservableBoolean isNotNullOrEmpty(ObservableValue<String> source) {
        return new DerivedObservableBoolean<>(source, (t) -> null != t && !t.isEmpty());
    }

    public static DerivedObservableBoolean isNotNullOrWhitespace(ObservableValue<String> source) {
        return new DerivedObservableBoolean<>(source, (t) -> null != t && !t.trim().isEmpty());
    }

    public static ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5, ObservableBooleanValue b6, ObservableBooleanValue b7) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(and(b1, b2, b3, b4), b5, b6, b7), (t) -> t.getValue1() && t.getValue2()
                && t.getValue3() && t.getValue4());
    }

    public static ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5, ObservableBooleanValue b6) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(and(b1, b2, b3), b4, b5, b6), (t) -> t.getValue1() && t.getValue2()
                && t.getValue3() && t.getValue4());
    }

    public static ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(and(b1, b2), b3, b4, b5), (t) -> t.getValue1() && t.getValue2()
                && t.getValue3() && t.getValue4());
    }

    public static ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(b1, b2, b3, b4), (t) -> t.getValue1() && t.getValue2() && t.getValue3() &&
                t.getValue4());
    }

    public static ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3) {
        return new DerivedObservableBoolean<>(new ObservableTriplet<>(b1, b2, b3), (t) -> t.getValue1() && t.getValue2() && t.getValue3());
    }

    public static ObservableBooleanDerivitive and(ObservableBooleanValue b1, ObservableBooleanValue b2) {
        return new DerivedObservableBoolean<>(new ObservableTuple<>(b1, b2), (t) -> t.getValue1() && t.getValue2());
    }

    public static ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5, ObservableBooleanValue b6, ObservableBooleanValue b7) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(and(b1, b2, b3, b4), b5, b6, b7), (t) -> t.getValue1() || t.getValue2()
                || t.getValue3() || t.getValue4());
    }

    public static ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5, ObservableBooleanValue b6) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(and(b1, b2, b3), b4, b5, b6), (t) -> t.getValue1() || t.getValue2()
                || t.getValue3() || t.getValue4());
    }

    public static ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4, ObservableBooleanValue b5) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(and(b1, b2), b3, b4, b5), (t) -> t.getValue1() || t.getValue2()
                || t.getValue3() || t.getValue4());
    }

    public static ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3,
            ObservableBooleanValue b4) {
        return new DerivedObservableBoolean<>(new ObservableQuadruplet<>(b1, b2, b3, b4), (t) -> t.getValue1() || t.getValue2() || t.getValue3() ||
                t.getValue4());
    }

    public static ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2, ObservableBooleanValue b3) {
        return new DerivedObservableBoolean<>(new ObservableTriplet<>(b1, b2, b3), (t) -> t.getValue1() || t.getValue2() || t.getValue3());
    }

    public static ObservableBooleanDerivitive or(ObservableBooleanValue b1, ObservableBooleanValue b2) {
        return new DerivedObservableBoolean<>(new ObservableTuple<>(b1, b2), (t) -> t.getValue1() || t.getValue2());
    }

    default ObservableStringDerivitive asString(Function<R, String> func) {
        return asString(this, func);
    }
    
    default ObservableBooleanDerivitive isEqualTo(ObservableValue<R> other) {
        return areEqual(this, other);
    }

    default ObservableBooleanDerivitive isNotEqualTo(ObservableValue<R> other) {
        return areNotEqual(this, other);
    }

}
