package scheduler.observables;

import java.util.function.Predicate;
import javafx.beans.value.ObservableValue;

/**
 * Calculates a boolean value from an {@link ObservableValue}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The source value for the calculation.
 * @deprecated 
 */
@Deprecated
public class DerivedObservableBoolean<T> extends DerivedObservable<Boolean> implements ObservableBooleanDerivitive {

    private boolean value;

    public DerivedObservableBoolean(ObservableValue<T> source, Predicate<T> calculate) {
        value = calculate.test(source.getValue());
        source.addListener((observable, oldValue, newValue) -> {
            boolean b = calculate.test(newValue);
            if (b != value) {
                value = b;
                fireValueChangedEvent();
            }
        });
    }

    protected DerivedObservableBoolean(DerivedObservableBoolean<T> source) {
        value = source.get();
        source.addListener((observable, oldValue, newValue) -> {
            if (newValue != value) {
                value = newValue;
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

}
