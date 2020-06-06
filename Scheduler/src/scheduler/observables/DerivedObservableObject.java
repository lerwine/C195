package scheduler.observables;

import java.util.Objects;
import java.util.function.Function;
import javafx.beans.value.ObservableValue;

/**
 * Calculates a new value from an {@link ObservableValue}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of source value for the calculation.
 * @param <U> The type of calculated value.
 * @deprecated
 */
@Deprecated
public class DerivedObservableObject<T, U> extends DerivedObservable<U> implements ObservableObjectDerivitive<U> {

    private U value;

    public DerivedObservableObject(ObservableValue<T> source, Function<T, U> calculate) {
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

}
