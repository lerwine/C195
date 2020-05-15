package scheduler.observables.validation;

import java.util.function.Predicate;
import javafx.beans.Observable;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class TestValidation<T> extends ValidationNode<Boolean> implements ObservableBooleanValue {

    private final Predicate<T> predicate;

    public TestValidation(Object bean, String name, ObservableValue<T> observable, Predicate<T> predicate) {
        super(bean, name, predicate.test(observable.getValue()));
        this.predicate = predicate;
        observable.addListener(this::onObservableChanged);
    }

    @Override
    public Boolean getValue() {
        return isValidValue();
    }

    @Override
    public boolean get() {
        return isValidValue();
    }

    @Override
    protected void onValidValueChanged(boolean isValidValue) {
        super.onValidValueChanged(isValidValue);
        fireValueChangedEvent();
    }

    private void onObservableChanged(Observable observable) {
        updateValidity((t) -> {
            t.accept(predicate.test(((ObservableValue<T>) observable).getValue()));
        });
    }

}
