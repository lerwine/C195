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
    
    public static String calculateAddressLines(String line1, String line2) {
                    if (line1.isEmpty()) {
                        return line2;
                    }
                    return (line2.isEmpty()) ? line1 : String.format("%s%n%s", line1, line2);
    }
    
    public static String calculateCityZipCountry(String city, String country, String postalCode) {
                    if (city.isEmpty()) {
                        return (postalCode.isEmpty()) ? country : ((country.isEmpty()) ? postalCode : String.format("%s, %s", postalCode, country));
                    }
                    if (country.isEmpty()) {
                        return (postalCode.isEmpty()) ? city : String.format("%s %s", city, postalCode);
                    }
                    return (postalCode.isEmpty()) ? String.format("%s, %s", city, country) : String.format("%s %s, %s", city, postalCode, country);
    }
    
    public static String calculateMultiLineAddress(String address, String cityZipCountry, String phone) {
            if (address.isEmpty()) {
                if (cityZipCountry.isEmpty()) {
                    return (phone.isEmpty()) ? "" : String.format("%s %s", getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
                }
                return (phone.isEmpty()) ? cityZipCountry : String.format("%s%n%s %s", cityZipCountry,
                        getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
            }
            if (cityZipCountry.isEmpty()) {
                return (phone.isEmpty()) ? address
                        : String.format("%s%n%s %s", address, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
            }
            return (phone.isEmpty()) ? String.format("%s%n%s", address, cityZipCountry)
                    : String.format("%s%n%s%n%s %s", address, cityZipCountry, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
    }
    
    public static CalculatedStringExpression<Tuple<String, String>> multiLineExpression(ObservableValue<String> line1, ObservableValue<String> line2) {
        return new CalculatedStringExpression<>(
                new ObservableTuple<>(
                        new CalculatedStringExpression<>(line1, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(line2, Values::asNonNullAndWsNormalized)
                ), (t) -> calculateAddressLines(t.getValue1(), t.getValue2())
        );
    }
    
    public static CalculatedStringExpression<Triplet<String, String, String>> cityZipCountryExpression(ObservableValue<String> city, ObservableValue<String> country,
            ObservableValue<String> postalCodeNormalized) {
        return new CalculatedStringExpression<>(
                new ObservableTriplet<>(
                        new CalculatedStringExpression<>(city, Values::asNonNullAndWsNormalized),
                        new CalculatedStringExpression<>(country, Values::asNonNullAndWsNormalized),
                        postalCodeNormalized
                ), (t) -> calculateCityZipCountry(t.getValue1(), t.getValue2(), t.getValue3())
        );
    }
    
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
