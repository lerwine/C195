package scheduler.observables;

import java.util.Objects;
import javafx.beans.value.ObservableValue;
import scheduler.util.QuadFunction;
import scheduler.util.QuadPredicate;
import scheduler.util.Quadruplet;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @param <V>
 */
public class ObservableQuadruplet<T, U, S, V> extends DerivedObservable<Quadruplet<T, U, S, V>>
        implements ObservableObjectDerivitive<Quadruplet<T, U, S, V>> {

    private Quadruplet<T, U, S, V> value;

    public ObservableQuadruplet(ObservableValue<T> source1, ObservableValue<U> source2, ObservableValue<S> source3, ObservableValue<V> source4) {
        value = Quadruplet.of(source1.getValue(), source2.getValue(), source3.getValue(), source4.getValue());
        source1.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue1(), newValue)) {
                value = Quadruplet.of(newValue, value.getValue2(), source3.getValue(), source4.getValue());
                fireValueChangedEvent();
            }
        });
        source2.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue2(), newValue)) {
                value = Quadruplet.of(value.getValue1(), newValue, source3.getValue(), source4.getValue());
                fireValueChangedEvent();
            }
        });
        source3.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue3(), newValue)) {
                value = Quadruplet.of(value.getValue1(), source2.getValue(), newValue, source4.getValue());
                fireValueChangedEvent();
            }
        });
        source4.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue4(), newValue)) {
                value = Quadruplet.of(value.getValue1(), source2.getValue(), source3.getValue(), newValue);
                fireValueChangedEvent();
            }
        });
        value = Quadruplet.of(source1.getValue(), source2.getValue(), source3.getValue(), source4.getValue());
    }

    @Override
    public Quadruplet<T, U, S, V> getValue() {
        return get();
    }

    @Override
    public Quadruplet<T, U, S, V> get() {
        return value;
    }

    public ObservableBooleanDerivitive asBoolean(QuadPredicate<T, U, S, V> func) {
        return new DerivedObservableBoolean<>(this, (t) -> func.test(t.getValue1(), t.getValue2(), t.getValue3(), t.getValue4()));
    }
    
    public ObservableStringDerivitive asString(QuadFunction<T, U, S, V, String> func) {
        return new DerivedObservableString<>(this, (t) -> func.apply(t.getValue1(), t.getValue2(), t.getValue3(), t.getValue4()));
    }
    
}
