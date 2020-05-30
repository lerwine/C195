package scheduler.observables.validation;

import java.util.function.Function;
import javafx.beans.Observable;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class MessageValidation<T> extends ValidationNode<String> implements ObservableStringValue {

    private final Function<T, String> getMessage;
    private String message;

    public MessageValidation(Object bean, String name, ObservableValue<T> observable, Function<T, String> getMessage) {
        super(bean, name, true);
        this.getMessage = getMessage;
        observable.addListener(this::onObservableChanged);
        if (null == (message = getMessage.apply(observable.getValue())))
            message = "";
        else if (!(message = message.trim()).isEmpty()) {
            setValidValue(false);
        }
    }

    @Override
    public String getValue() {
        return message;
    }

    @Override
    public String get() {
        return message;
    }
    
    @SuppressWarnings("unchecked")
    private void onObservableChanged(Observable observable) {
        String value = getMessage.apply(((ObservableValue<T>)observable).getValue());
        String s = (null == value) ? "" : value.trim();
        if (testUpdateValidity((t) -> {
            if (message.equals(s)) {
                return false;
            }
            t.accept((message = s).isEmpty());
            return true;
        }))
            fireValueChangedEvent();
    }

}
