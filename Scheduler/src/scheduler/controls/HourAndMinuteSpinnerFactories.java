package scheduler.controls;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

/**
 *
 * @author lerwi
 */
public class HourAndMinuteSpinnerFactories {

    private final NumberFormat formatter = NumberFormat.getNumberInstance();
    private final ObservableValue<LocalDateTime> startDateTime;
    private final IntegerProperty hour;
    private final IntegerProperty minute;
    private final ReadOnlyObjectWrapper<HourSpinnerFactory> hourSpinnerFactory;
    private final ReadOnlyObjectWrapper<MinuteSpinnerFactory> minuteSpinnerFactory;
    private final EndDateTimeProperty value;

    public HourAndMinuteSpinnerFactories(int initialHour, int initialMinute, ObservableValue<LocalDateTime> startDateTime) {
        this.startDateTime = startDateTime;
        hour = new SimpleIntegerProperty(this, "hour", initialHour);
        minute = new SimpleIntegerProperty(this, "minute", initialMinute);
        hourSpinnerFactory = new ReadOnlyObjectWrapper<>(this, "hourSpinnerFactory", new HourSpinnerFactory(initialHour));
        minuteSpinnerFactory = new ReadOnlyObjectWrapper<>(this, "minuteSpinnerFactory", new MinuteSpinnerFactory(initialMinute));
        value = new EndDateTimeProperty();
    }

    public int getHour() {
        return hour.get();
    }

    public void setHour(int value) {
        hour.set(value);
    }

    public IntegerProperty hourProperty() {
        return hour;
    }

    public HourSpinnerFactory getHourSpinnerFactory() {
        return hourSpinnerFactory.get();
    }

    public ReadOnlyObjectProperty<HourSpinnerFactory> hourSpinnerFactoryProperty() {
        return hourSpinnerFactory.getReadOnlyProperty();
    }

    public int getMinute() {
        return minute.get();
    }

    public void setMinute(int value) {
        minute.set(value);
    }

    public IntegerProperty minuteProperty() {
        return minute;
    }

    public MinuteSpinnerFactory getMinuteSpinnerFactory() {
        return minuteSpinnerFactory.get();
    }

    public ReadOnlyObjectProperty<MinuteSpinnerFactory> minuteSpinnerFactoryProperty() {
        return minuteSpinnerFactory.getReadOnlyProperty();
    }

    public LocalDateTime getValue() {
        return value.getValue();
    }

    public ReadOnlyProperty<LocalDateTime> valueProperty() {
        return value;
    }

    private class EndDateTimeProperty extends ObjectBinding<LocalDateTime> implements ReadOnlyProperty<LocalDateTime> {

        private final ObservableList<Observable> dependencies;
        private final ObservableList<Observable> readOnlyDependencies;

        private EndDateTimeProperty() {
            dependencies = FXCollections.observableArrayList(startDateTime, hour, minute);
            readOnlyDependencies = FXCollections.unmodifiableObservableList(dependencies);
            super.bind(startDateTime, hour, minute);
        }

        @Override
        public ObservableList<?> getDependencies() {
            return readOnlyDependencies;
        }

        @Override
        public void dispose() {
            super.dispose();
            super.unbind(startDateTime, hour, minute);
            dependencies.clear();
        }

        @Override
        protected LocalDateTime computeValue() {
            long h = hour.get();
            long m = minute.get();
            return startDateTime.getValue().plusHours(h).plusMinutes(m);
        }

        @Override
        public Object getBean() {
            return HourAndMinuteSpinnerFactories.this;
        }

        @Override
        public String getName() {
            return "value";
        }

    }

    public class MinuteSpinnerFactory extends SpinnerValueFactory<Integer> {

        public MinuteSpinnerFactory(int initialValue) {
            assert initialValue >= 0 && initialValue < 60 : "Invalid minute value";
            setConverter(new NumberValueConverter(minute, "%02d"));
            setValue(initialValue);
        }

        @Override
        public void decrement(int steps) {
            if (steps < 1) {
                return;
            }
            int value = getValue();
            int r = value % 5;
            if (r > 0) {
                value -= r;
            } else {
                value--;
            }
            if (steps > 1) {
                value -= (steps - 1) * 5;
            }
            int d = 0;
            while (value < 0) {
                d++;
                value += 60;
            }
            setValue(value);
            if (d > 0) {
                hourSpinnerFactory.get().decrement(d);
            }
        }

        @Override
        public void increment(int steps) {
            if (steps < 1) {
                return;
            }
            int value = getValue();
            int r = value % 5;
            if (r > 0) {
                value += (5 - r);
            } else {
                value++;
            }
            if (steps > 1) {
                value += (steps - 1) * 5;
            }
            int i = 0;
            while (value > 59) {
                i++;
                value -= 60;
            }
            setValue(value);
            if (i > 0) {
                hourSpinnerFactory.get().increment(i);
            }
        }

    }

    public class HourSpinnerFactory extends SpinnerValueFactory<Integer> {

        public HourSpinnerFactory(int initialValue) {
            assert initialValue >= 0 : "Invalid hour value";
            setConverter(new NumberValueConverter(hour, "%d"));
            setValue(initialValue);
        }

        @Override
        public void decrement(int steps) {
            if (steps < 1)
                return;
            int h = getValue() - steps;
            setValue((h > 0) ? h : 0);
        }

        @Override
        public void increment(int steps) {
            if (steps > 0) {
                setValue(getValue() + steps);
            }
        }

    }

    private class NumberValueConverter extends StringConverter<Integer> {

        private final IntegerProperty target;
        private final String fmt;

        private NumberValueConverter(IntegerProperty target, String fmt) {
            this.fmt = fmt;
            this.target = target;
        }

        @Override
        public String toString(Integer value) {
            if (null == value) {
                return "";
            }
            return String.format(fmt, value);
        }

        @Override
        public Integer fromString(String string) {
            if (null != string && !string.trim().isEmpty()) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException ex) {
                    Logger.getLogger(HourAndMinuteSpinnerFactories.class.getName()).log(Level.WARNING, "Number format error", ex);
                }
            }
            return target.get();
        }

    }

}
