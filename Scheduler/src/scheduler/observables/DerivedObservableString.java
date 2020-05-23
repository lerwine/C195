package scheduler.observables;

import java.util.Objects;
import java.util.function.Function;
import javafx.beans.value.ObservableValue;

/**
 * Calculates a string value from an {@link ObservableValue}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The source value for the calculation.
 */
public class DerivedObservableString<T> extends DerivedObservable<String> implements ObservableStringDerivitive {

    private String value;

    public DerivedObservableString(ObservableValue<T> source, Function<T, String> calculate) {
        value = calculate.apply(source.getValue());
        source.addListener((observable, oldValue, newValue) -> {
            String s = calculate.apply(newValue);
            if (!Objects.equals(s, value)) {
                value = s;
                fireValueChangedEvent();
            }
        });
    }

    protected DerivedObservableString(DerivedObservableString<T> source) {
        value = source.get();
        source.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(newValue, value)) {
                value = newValue;
                fireValueChangedEvent();
            }
        });
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public String getValue() {
        return get();
    }

}
