package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import scheduler.controls.TimeZoneListCellFactory;

/**
 *
 * @author Leonard T. Erwine
 */
public class StartDateTimeProperty extends ObjectBinding<ZonedDateTime>
        implements ReadOnlyProperty<ZonedDateTime>, WritableObjectValue<ZonedDateTime> {

    private static final Logger LOG = Logger.getLogger(StartDateTimeProperty.class.getName());

    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final ObjectProperty<LocalDate> startDateValueProperty;
    private final StringConverter<LocalDate> startDateConverter;
    private final StringProperty startDateTextProperty;
    private final StringConverter<Integer> hourConverter;
    private final SpinnerValueFactory<Integer> hourValueFactory;
    private final StringProperty hourTextProperty;
    private final StringConverter<Integer> minuteConverter;
    private final SpinnerValueFactory<Integer> minuteValueFactory;
    private final StringProperty minuteTextProperty;
    private final StringConverter<Boolean> amPmConverter;
    private final SpinnerValueFactory<Boolean> amPmValueFactory;
    private final StringProperty amPmTextProperty;
    private final String amText;
    private final String pmText;
    private final SingleSelectionModel<TimeZone> timeZoneSelectionModel;
    private final ObservableList<TimeZone> timeZones;
    private final ObjectBinding<LocalTime> localTime;
    private int currentTimeZoneOffset;

    public StartDateTimeProperty(Object bean, String name, DatePicker startDatePicker, Spinner<Integer> startHourSpinner,
            Spinner<Integer> startMinuteSpinner, Spinner<Boolean> amPmSpinner, ComboBox<TimeZone> timeZoneComboBox) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("a");
        amText = LocalTime.of(0, 0, 0, 0).format(fmt);
        pmText = LocalTime.of(12, 0, 0, 0).format(fmt);
        startDateValueProperty = startDatePicker.valueProperty();
        startDateTextProperty = startDatePicker.getEditor().textProperty();
        startDateConverter = startDatePicker.getConverter();
        hourTextProperty = startHourSpinner.getEditor().textProperty();
        minuteTextProperty = startMinuteSpinner.getEditor().textProperty();
        amPmTextProperty = amPmSpinner.getEditor().textProperty();
        timeZones = TimeZoneListCellFactory.getZoneIdOptions();
        currentTimeZoneOffset = (TimeZone.getTimeZone(ZoneId.systemDefault())).getRawOffset();
        timeZoneSelectionModel = timeZoneComboBox.getSelectionModel();

        ZonedDateTime date = ZonedDateTime.now().plusDays(1).withNano(0).withSecond(0).withMinute(0);
        startDateValueProperty.set(date.toLocalDate());

        hourConverter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return (null == object) ? "" : String.format("%d", object);
            }

            @Override
            public Integer fromString(String string) {
                if (null == string || (string = string.trim()).isEmpty())
                    return null;
                return Integer.parseInt(string);
            }

        };

        hourValueFactory = new SpinnerValueFactory<Integer>() {
            {
                setValue((date.getHour() == 0) ? 12 : ((date.getHour() < 12) ? date.getHour() : date.getHour() - 12));
                setWrapAround(true);
                setConverter(hourConverter);
            }

            @Override
            public void decrement(int steps) {
                if (steps > 0) {
                    set(get().minusHours(steps));
                }
            }

            @Override
            public void increment(int steps) {
                if (steps > 0) {
                    set(get().plusHours(steps));
                }
            }
        };

        minuteConverter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return (null == object) ? "" : String.format("%02d", object);
            }

            @Override
            public Integer fromString(String string) {
                if (null == string || (string = string.trim()).isEmpty())
                    return null;
                return Integer.parseInt(string);
            }
        };

        minuteValueFactory = new SpinnerValueFactory<Integer>() {
            {
                setValue(date.getMinute());
                setWrapAround(true);
                setConverter(minuteConverter);
            }

            @Override
            public void decrement(int steps) {
                if (steps > 0) {
                    set(get().minusMinutes(steps));
                }
            }

            @Override
            public void increment(int steps) {
                if (steps > 0) {
                    set(get().plusMinutes(steps));
                }
            }
        };

        amPmConverter = new StringConverter<Boolean>() {
            @Override
            public String toString(Boolean object) {
                return (object) ? pmText : amText;
            }

            @Override
            public Boolean fromString(String string) {
                return null != string && string.trim().equalsIgnoreCase(pmText);
            }

        };

        amPmValueFactory = new SpinnerValueFactory<Boolean>() {
            {
                setValue(date.getHour() > 11);
                setWrapAround(true);
                setConverter(amPmConverter);
            }

            @Override
            public void decrement(int steps) {
                spin(steps);
            }

            @Override
            public void increment(int steps) {
                spin(steps);
            }

            private void spin(int steps) {
                if (steps > 0) {
                    if ((steps % 2) == 1) {
                        ZonedDateTime d = get();
                        int h = d.getHour();
                        set(d.withHour((h < 12) ? h + 12 : h - 12));
                    }
                }
            }
        };

        localTime = getLocalTime();
        dependencies = FXCollections.observableArrayList(startDateValueProperty, localTime, timeZoneSelectionModel.selectedItemProperty());
        super.bind(startDateValueProperty, localTime, timeZoneSelectionModel.selectedItemProperty());

        startHourSpinner.setValueFactory(hourValueFactory);

        startMinuteSpinner.setValueFactory(minuteValueFactory);

        amPmSpinner.setValueFactory(amPmValueFactory);
        
        timeZoneComboBox.setItems(timeZones);
        // Get the best match to initially select the time zone.
        String zId = TimeZone.getTimeZone(ZoneId.systemDefault()).getID();
        Optional<TimeZone> tz = timeZones.stream().filter((TimeZone t) -> t.getID().equals(zId)).findFirst();
        if (!tz.isPresent()) {
            tz = timeZones.stream().filter((TimeZone t) -> t.getRawOffset() == currentTimeZoneOffset).findFirst();
        }

        timeZoneSelectionModel.select((tz.isPresent()) ? tz.get() : timeZones.get(0));
    }

    public final ObjectBinding<LocalTime> getLocalTime() {
        return Bindings.createObjectBinding(() -> {
            Boolean a = amPmValueFactory.getValue();
            Integer h = hourValueFactory.getValue();
            Integer m = minuteValueFactory.getValue();
            if (null != a && null != h && null != m)
                return LocalTime.of((a) ? ((h < 12) ? h + 12 : 12) : ((h == 12) ? 0 : h), m, 0, 0);
            return null;
        }, amPmValueFactory.valueProperty(), hourValueFactory.valueProperty(), minuteValueFactory.valueProperty());
    }
    
    public StringBinding getValidationMessage() {
        return Bindings.createStringBinding(() -> {
            String dt = startDateTextProperty.get();
            String ht = hourTextProperty.get();
            String mt = minuteTextProperty.get();
            String at = amPmTextProperty.get();
            LocalDate dv = startDateValueProperty.get();
            Integer hv = hourValueFactory.getValue();
            Integer mv = minuteValueFactory.getValue();
            Boolean av = amPmValueFactory.getValue();
            TimeZone tz = timeZoneSelectionModel.getSelectedItem();
            // PENDING: Internationalize these
            if (dt.trim().isEmpty())
                return "Start date not specified";
            if (ht.trim().isEmpty())
                return "Start hour not specified";
            if (mt.trim().isEmpty())
                return "Start minute not specified";
            if ((at = at.trim()).isEmpty())
                return String.format("%s/%s designator not specified", amText, pmText);
            if (null == tz)
                return "Time zone not specified";
            
            boolean success;
            try {
                success = null != startDateConverter.fromString(dt);
            } catch (DateTimeParseException ex) {
                LOG.log(Level.INFO, "Caught start date string parse error", ex);
                success = false;
            }
            if (!success)
                return "Invalid start date";
            try {
                Integer i = hourConverter.fromString(ht);
                success = null != i && i > 0 && i < 13;
            } catch (NumberFormatException ex) {
                LOG.log(Level.INFO, "Caught start hour string parse error", ex);
                success = false;
            }
            if (!success)
                return "Invalid start hour";
            try {
                Integer i = minuteConverter.fromString(mt);
                success = null != i && i >= 0 && i < 60;
            } catch (NumberFormatException ex) {
                LOG.log(Level.INFO, "Caught start minute string parse error", ex);
                success = false;
            }
            if (!success)
                return "Invalid start minute";
            if (at.equalsIgnoreCase(amText) || at.equalsIgnoreCase(pmText))
                return "";
            return String.format("Invalid %s/%s designator", amText, pmText);
        }, startDateTextProperty, hourTextProperty, minuteTextProperty, amPmTextProperty, startDateValueProperty,
            hourValueFactory.valueProperty(), minuteValueFactory.valueProperty(), amPmValueFactory.valueProperty(),
            timeZoneSelectionModel.selectedItemProperty());
    }

    @Override
    protected ZonedDateTime computeValue() {
        LocalDate d = startDateValueProperty.get();
        LocalTime t = localTime.get();
        TimeZone z = timeZoneSelectionModel.getSelectedItem();
        if (null != d && null != t && null != z) {
            return ZonedDateTime.of(LocalDateTime.of(d, t), z.toZoneId());
        } else {
            return null;
        }
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setValue(ZonedDateTime value) {
        set(value);
    }

    @Override
    public void set(ZonedDateTime value) {
        LocalDate localDate = value.toLocalDate();
        if (!localDate.equals(startDateValueProperty.get())) {
            startDateValueProperty.set(value.toLocalDate());
        }
        int i = value.getHour();
        switch (i) {
            case 0:
                if (amPmValueFactory.getValue()) {
                    amPmValueFactory.setValue(false);
                }
                if (12 != hourValueFactory.getValue()) {
                    hourValueFactory.setValue(12);
                }
                break;
            case 12:
                if (!amPmValueFactory.getValue()) {
                    amPmValueFactory.setValue(true);
                }
                if (12 != hourValueFactory.getValue()) {
                    hourValueFactory.setValue(12);
                }
                break;
            default:
                if (i > 12) {
                    i -= 12;
                    if (amPmValueFactory.getValue()) {
                        amPmValueFactory.setValue(false);
                    }
                } else if (!amPmValueFactory.getValue()) {
                    amPmValueFactory.setValue(true);
                }
                if (i != hourValueFactory.getValue()) {
                    hourValueFactory.setValue(i);
                }
                break;
        }
    }

    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.unmodifiableObservableList(dependencies);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (!dependencies.isEmpty()) {
            dependencies.clear();
            super.unbind(startDateValueProperty, localTime, timeZoneSelectionModel.selectedItemProperty());
        }
    }
}
