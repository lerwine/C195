package scheduler.observables;

import java.util.Objects;
import javafx.beans.value.ObservableValue;
import scheduler.util.Tuple;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 */
public class ObservableTuple<T, U> extends DerivedObservable<Tuple<T, U>> implements ObservableObjectDerivitive<Tuple<T, U>> {

    private Tuple<T, U> value;

    public ObservableTuple(ObservableValue<T> source1, ObservableValue<U> source2) {
        value = Tuple.of(source1.getValue(), source2.getValue());
        source1.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue1(), newValue)) {
                value = Tuple.of(newValue, value.getValue2());
                fireValueChangedEvent();
            }
        });
        source2.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(value.getValue2(), newValue)) {
                value = Tuple.of(value.getValue1(), newValue);
                fireValueChangedEvent();
            }
        });
        value = Tuple.of(source1.getValue(), source2.getValue());
    }

    @Override
    public Tuple<T, U> getValue() {
        return get();
    }

    @Override
    public Tuple<T, U> get() {
        return value;
    }

}
