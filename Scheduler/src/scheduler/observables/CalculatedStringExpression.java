package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.Objects;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.util.Values;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_PHONENUMBER;

/**
 * Calculates a string value from an {@link ObservableValue}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The source value for the calculation.
 */
public class CalculatedStringExpression<T> extends CalculatedObjectExpression<String> implements ObservableStringValue {
    
    private String value;
    private StringExpression stringExpression;

    public CalculatedStringExpression(ObservableValue<T> source, Function<T, String> calculate) {
        value = calculate.apply(source.getValue());
        source.addListener((observable, oldValue, newValue) -> {
            String s = calculate.apply(newValue);
            if (!Objects.equals(s, value)) {
                value = s;
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

    public StringExpression getStringExpression() {
        if (null == stringExpression) {
            stringExpression = new StringExpression() {

                private ExpressionHelper<String> helper = null;

                @Override
                public String get() {
                    return CalculatedStringExpression.this.get();
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
        return stringExpression;
    }

}
