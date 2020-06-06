package scheduler.observables;

import java.util.Objects;
import javafx.beans.value.ObservableValue;
import scheduler.util.Triplet;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @deprecated
 */
@Deprecated
public class ObservableTriplet<T, U, S> extends DerivedObservable<Triplet<T, U, S>> implements ObservableObjectDerivitive<Triplet<T, U, S>> {

    private Triplet<T, U, S> value;

    public ObservableTriplet(ObservableValue<T> source1, ObservableValue<U> source2, ObservableValue<S> source3) {
        value = Triplet.of(source1.getValue(), source2.getValue(), source3.getValue());
        source1.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue1(), newValue)) {
                value = Triplet.of(newValue, value.getValue2(), source3.getValue());
                fireValueChangedEvent();
            }
        });
        source2.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue2(), newValue)) {
                value = Triplet.of(value.getValue1(), newValue, source3.getValue());
                fireValueChangedEvent();
            }
        });
        source3.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue3(), newValue)) {
                value = Triplet.of(value.getValue1(), source2.getValue(), newValue);
                fireValueChangedEvent();
            }
        });
        value = Triplet.of(source1.getValue(), source2.getValue(), source3.getValue());
    }

    @Override
    public Triplet<T, U, S> getValue() {
        return get();
    }

    @Override
    public Triplet<T, U, S> get() {
        return value;
    }

}
