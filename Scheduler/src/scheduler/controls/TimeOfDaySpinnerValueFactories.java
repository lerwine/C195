package scheduler.controls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
public final class TimeOfDaySpinnerValueFactories {

    public static LocalTime roundToSeconds(LocalTime value) {
        if (value.getNano() > 499999999) {
            return value.minusNanos(value.getNano()).plusSeconds(1);
        }
        if (value.getNano() > 0) {
            return value.minusNanos(value.getNano());
        }
        return value;
    }

    public static LocalTime roundToSeconds(LocalTime value, boolean up) {
        if (value.getNano() > 0) {
            if (up) {
                return value.minusNanos(value.getNano()).plusSeconds(1);
            }
            return value.minusNanos(value.getNano());
        }
        return value;
    }

    public static LocalTime roundToMinutes(LocalTime value) {
        if ((value = roundToSeconds(value)).getSecond() > 29) {
            return value.minusSeconds(value.getSecond()).plusMinutes(1);
        }
        if (value.getSecond() > 0) {
            return value.minusSeconds(value.getSecond());
        }
        return value;
    }

    public static LocalTime roundToHours(LocalTime value) {
        if ((value = roundToMinutes(value)).getMinute() > 29) {
            return value.minusMinutes(value.getMinute()).plusMinutes(1);
        }
        if (value.getMinute() > 0) {
            return value.minusMinutes(value.getMinute());
        }
        return value;
    }

    private final IntegerProperty hour;
    private final IntegerProperty minute;
    private final ReadOnlyObjectWrapper<HourSpinnerFactory> hourSpinnerFactory;
    private final ReadOnlyObjectWrapper<MinuteSpinnerFactory> minuteSpinnerFactory;
    private final BooleanProperty pm;
    private final ReadOnlyObjectWrapper<AmPmSpinnerFactory> amPmSpinnerFactory;
    private final LocalDateTimeProperty value;

    public TimeOfDaySpinnerValueFactories(LocalTime initialValue, ObjectProperty<LocalDate> startDate) {
        pm = new SimpleBooleanProperty(this, "pm", initialValue.getHour() > 11);
        hour = new SimpleIntegerProperty(this, "hour", (pm.get()) ? ((initialValue.getHour() == 12) ? 12 : initialValue.getHour() - 12) :
                ((initialValue.getHour() == 0) ? 12 : initialValue.getHour()));
        minute = new SimpleIntegerProperty(this, "minute", initialValue.getMinute());
        hourSpinnerFactory = new ReadOnlyObjectWrapper<>(this, "hourSpinnerFactory", new HourSpinnerFactory(hour.get()));
        minuteSpinnerFactory = new ReadOnlyObjectWrapper<>(this, "minuteSpinnerFactory", new MinuteSpinnerFactory(minute.get()));
        amPmSpinnerFactory = new ReadOnlyObjectWrapper<>(this, "amPmSpinnerFactory", new AmPmSpinnerFactory(pm.get()));
        value = new LocalDateTimeProperty(startDate);
        pm.addListener((obj) -> {
            Boolean x = ((ObservableValue<Boolean>) obj).getValue();
            if ((boolean) x != amPmSpinnerFactory.get().getValue()) {
                amPmSpinnerFactory.get().setValue(x);
            }
        });
        hour.addListener((obj) -> {
            Integer i = ((ObservableValue<Integer>) obj).getValue();
            if ((int) i != hourSpinnerFactory.get().getValue()) {
                hourSpinnerFactory.get().setValue(i);
            }
        });
        minute.addListener((obj) -> {
            Integer i = ((ObservableValue<Integer>) obj).getValue();
            if ((int) i != minuteSpinnerFactory.get().getValue()) {
                minuteSpinnerFactory.get().setValue(i);
            }
        });
        hourSpinnerFactory.get().valueProperty().addListener((obj) -> {
            Integer n = ((ObservableValue<Integer>) obj).getValue();
            if (null != n) {
                int i = n;
                if (i < 1) {
                    do {
                        i += 12;
                    } while (i < 1);
                    hourSpinnerFactory.get().setValue(i);
                } else if (i > 12) {
                    do {
                        i -= 12;
                    } while (i > 12);
                    hourSpinnerFactory.get().setValue(i);
                } else if (hour.get() != i) {
                    hour.set(i);
                }
            }
        });
        minuteSpinnerFactory.get().valueProperty().addListener((obj) -> {
            Integer n = ((ObservableValue<Integer>) obj).getValue();
            if (null != n) {
                int i = n;
                if (i < 0) {
                    do {
                        i += 60;
                    } while (i < 1);
                    minuteSpinnerFactory.get().setValue(i);
                } else if (i > 59) {
                    do {
                        i -= 60;
                    } while (i > 59);
                    minuteSpinnerFactory.get().setValue(i);
                } else if (minute.get() != i) {
                    minute.set(i);
                }
            }
        });
        amPmSpinnerFactory.get().valueProperty().addListener((obj) -> {
            Boolean n = ((ObservableValue<Boolean>) obj).getValue();
            if (null != n) {
                boolean b = n;
                if (b != pm.get()) {
                    pm.set(b);
                }
            }
        });
    }

    public TimeOfDaySpinnerValueFactories(ObjectProperty<LocalDate> startDate) {
        this(roundToHours(LocalTime.now().plusMinutes(30)), startDate);
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

    public boolean isPm() {
        return pm.get();
    }

    public void setPm(boolean value) {
        pm.set(value);
    }

    public BooleanProperty pmProperty() {
        return pm;
    }

    public final AmPmSpinnerFactory getAmPmSpinnerFactory() {
        return amPmSpinnerFactory.get();
    }

    public final ReadOnlyObjectProperty<AmPmSpinnerFactory> amPmSpinnerFactoryProperty() {
        return amPmSpinnerFactory.getReadOnlyProperty();
    }

    public LocalDateTime getValue() {
        return value.getValue();
    }

    public ReadOnlyProperty<LocalDateTime> valueProperty() {
        return value;
    }

    private class LocalDateTimeProperty extends ObjectBinding<LocalDateTime> implements ReadOnlyProperty<LocalDateTime> {

        private final ObservableList<Observable> dependencies;
        private final ObservableList<Observable> readOnlyDependencies;
        private final ObjectProperty<LocalDate> startDate;

        private LocalDateTimeProperty(ObjectProperty<LocalDate> startDate) {
            this.startDate = startDate;
            dependencies = FXCollections.observableArrayList(startDate, hour, minute, pm);
            readOnlyDependencies = FXCollections.unmodifiableObservableList(dependencies);
            super.bind(startDate, hour, minute, pm);
        }

        @Override
        public ObservableList<?> getDependencies() {
            return readOnlyDependencies;
        }

        @Override
        public void dispose() {
            super.dispose();
            super.unbind(startDate, hour, minute, pm);
            dependencies.clear();
        }

        @Override
        protected LocalDateTime computeValue() {
            int h = hour.get();
            int m = minute.get();
            while (h < 1) {
                h += 12;
            }
            while (h > 12) {
                h -= 12;
            }
            while (m < 0) {
                m += 60;
            }
            while (m > 59) {
                m -= 60;
            }
            
            return LocalDateTime.of(startDate.getValue(), LocalTime.of((pm.get()) ? (h == 12 ? h : h + 12) : ((h == 12) ? 0 : h), m, 0, 0));
        }

        @Override
        public Object getBean() {
            return TimeOfDaySpinnerValueFactories.this;
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

        private HourSpinnerFactory(int initialValue) {
            assert initialValue > 0 && initialValue < 13 : "Invalid hour value";
            setConverter(new NumberValueConverter(hour, "%d"));
            setValue(initialValue);
        }

        @Override
        public void decrement(int steps) {
            if (steps < 1) {
                return;
            }
            int h = getValue();
            if (amPmSpinnerFactory.get().getValue()) {
                if (h > 12) {
                    h -= 12;
                }
            } else if (h == 12) {
                h = 0;
            }
            h -= steps;
            while (h < 0) {
                h += 24;
            }
            switch (h) {
                case 0:
                    setValue(12);
                    amPmSpinnerFactory.get().setValue(false);
                    break;
                case 12:
                    setValue(12);
                    amPmSpinnerFactory.get().setValue(true);
                    break;
                default:
                    if (h > 12) {
                        setValue(h - 12);
                        amPmSpinnerFactory.get().setValue(true);
                    } else {
                        setValue(h);
                        amPmSpinnerFactory.get().setValue(false);
                    }
                    break;
            }
        }

        @Override
        public void increment(int steps) {
            if (steps < 1) {
                return;
            }
            int h = getValue();
            if (amPmSpinnerFactory.get().getValue()) {
                if (h > 12) {
                    h -= 12;
                }
            } else if (h == 12) {
                h = 0;
            }
            h += steps;
            while (h > 23) {
                h -= 24;
            }
            switch (h) {
                case 0:
                    setValue(12);
                    amPmSpinnerFactory.get().setValue(false);
                    break;
                case 12:
                    setValue(12);
                    amPmSpinnerFactory.get().setValue(true);
                    break;
                default:
                    if (h > 12) {
                        setValue(h - 12);
                        amPmSpinnerFactory.get().setValue(true);
                    } else {
                        setValue(h);
                        amPmSpinnerFactory.get().setValue(false);
                    }
                    break;
            }
        }

    }

    public class AmPmSpinnerFactory extends SpinnerValueFactory<Boolean> {

        private final String amText;
        private final String pmText;

        public AmPmSpinnerFactory(boolean isPm) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("a");
            amText = LocalTime.MIN.format(fmt);
            pmText = LocalTime.MAX.format(fmt);
            setConverter(new StringConverter<Boolean>() {
                @Override
                public String toString(Boolean object) {
                    if (null == object) {
                        return "";
                    }
                    return (object) ? pmText : amText;
                }

                @Override
                public Boolean fromString(String string) {
                    if (null == string || (string = string.trim()).isEmpty()) {
                        return getValue();
                    }
                    if (string.equalsIgnoreCase(amText)) {
                        return false;
                    }
                    return string.equalsIgnoreCase(pmText) || pmText.toLowerCase().startsWith(string);
                }
            });
            setValue(isPm);
        }

        @Override
        public void decrement(int steps) {
            if (steps > 0 && steps % 2 == 1) {
                setValue(!getValue());
            }
        }

        @Override
        public void increment(int steps) {
            if (steps > 0 && steps % 2 == 1) {
                setValue(!getValue());
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
