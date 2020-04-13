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
import javafx.beans.binding.IntegerBinding;
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
import scheduler.util.LogHelper;
import static scheduler.util.LogHelper.toLogText;
import scheduler.util.ResourceBundleHelper;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class StartDateTimeProperty extends ObjectBinding<ZonedDateTime>
        implements ReadOnlyProperty<ZonedDateTime>, WritableObjectValue<ZonedDateTime> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(StartDateTimeProperty.class.getName()), Level.FINE);

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
                LOG.fine(() -> String.format("Decrementing %s hour %d steps", name, steps));
                if (steps > 0) {
                    ZonedDateTime d = get().minusHours(steps);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(d)));
                    set(d);
                }
            }

            @Override
            public void increment(int steps) {
                LOG.fine(() -> String.format("Incrementing %s hour %d steps", name, steps));
                if (steps > 0) {
                    ZonedDateTime d = get().plusHours(steps);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(d)));
                    set(d);
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
                LOG.fine(() -> String.format("Decrementing %s minute %d steps", name, steps));
                if (steps > 0) {
                    ZonedDateTime d = get();
                    if (d.getNano() > 0) {
                        d = d.withNano(0);
                    }
                    if (d.getSecond() > 0) {
                        d = d.withSecond(0);
                    }
                    long s = d.getMinute()% 5L;
                    ZonedDateTime result;
                    if (s > 0) {
                        d = d.minusMinutes(s);
                        result = (steps > 1) ? d.minusMinutes((steps - 1) * 5) : d;
                    } else
                        result = d.minusMinutes(steps * 5);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(result)));
                    set(result);
                }
                LOG.fine(() -> String.format("Decrementing %s minute %d steps", name, steps));
                if (steps > 0) {
                    ZonedDateTime d = get().minusMinutes(steps);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(d)));
                    set(d);
                }
            }

            @Override
            public void increment(int steps) {
                LOG.fine(() -> String.format("Incrementing %s minute %d steps", name, steps));
                if (steps > 0) {
                    ZonedDateTime d = get();
                    if (d.getNano() > 0) {
                        d = d.withNano(0).plusSeconds(1);
                    }
                    if (d.getSecond()> 0) {
                        d = d.withSecond(0).plusMinutes(1);
                    }
                    long s = d.getMinute()% 5L;
                    ZonedDateTime result;
                    if (s > 0) {
                        d = d.plusMinutes(5L - s);
                        result = (steps > 1) ? d.plusMinutes((steps - 1) * 5) : d;
                    } else
                        result = d.plusMinutes(steps * 5);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(result)));
                    set(result);
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
                LOG.fine(() -> String.format("Decrementing %s am/pm %d steps", name, steps));
                spin(steps);
            }

            @Override
            public void increment(int steps) {
                LOG.fine(() -> String.format("Incrementing %s am/pm %d steps", name, steps));
                spin(steps);
            }

            private void spin(int steps) {
                if (steps > 0) {
                    if ((steps % 2) == 1) {
                        ZonedDateTime oldValue = get();
                        int h = oldValue.getHour();
                        ZonedDateTime newValue = oldValue.withHour((h < 12) ? h + 12 : h - 12);
                        LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(newValue)));
                        set(newValue);
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

    public final IntegerBinding getHour24() {
        return Bindings.createIntegerBinding(() -> {
            Integer h = hourValueFactory.getValue();
            Boolean a = amPmValueFactory.getValue();
            LOG.fine(() -> String.format("Recalculating %s hour24: hourValueFactory=%s; amPmValueFactory=%s", name, toLogText(h), toLogText(a)));
            if (null == h || null == a) {
                LOG.fine("Returning -1");
                return -1;
            }
            int result = (a) ? ((h == 12) ? 12 : h + 12) : ((h == 12) ? 0 : h);
            LOG.fine(() -> String.format("Returning %d", result));
            return result;
        }, amPmValueFactory.valueProperty(), hourValueFactory.valueProperty());
    }
    
    public final ObjectBinding<LocalTime> getLocalTime() {
        IntegerBinding hour24 = getHour24();
        return Bindings.createObjectBinding(() -> {
            int h = hour24.get();
            Integer m = minuteValueFactory.getValue();
            LOG.fine(() -> String.format("Recalculating %s localTime: hour24=%s; minuteValue=%s", name, toLogText(h), toLogText(m)));
            if (h < 0 || null == m) {
                LOG.fine("Returning null");
                return null;
            }
            LocalTime result = LocalTime.of(h, m, 0, 0);
            LOG.fine(() -> String.format("Returning %s", result));
            return result;
        }, hour24, minuteValueFactory.valueProperty());
    }
    
    public StringBinding getValidationMessage() {
        return Bindings.createStringBinding(() -> {
            final String dt = startDateTextProperty.get();
            final String ht = hourTextProperty.get();
            final String mt = minuteTextProperty.get();
            String at = amPmTextProperty.get();
            final LocalDate dv = startDateValueProperty.get();
            final Integer hv = hourValueFactory.getValue();
            final Integer mv = minuteValueFactory.getValue();
            final Boolean av = amPmValueFactory.getValue();
            final TimeZone tz = timeZoneSelectionModel.getSelectedItem();
            LOG.fine(() -> String.format("Recalculating %s validationMessage: startDateText = %s; hourText = %s; minuteText = %s; amPmText = %s;"
                    + " startDateValue = %s; hourValue = %s; minuteValue = %s; amPmValue = %s; selectedTimeZone = %s", name, toLogText(dt),
                    toLogText(ht), toLogText(mt), toLogText(amPmTextProperty.get()), toLogText(dv), toLogText(hv), toLogText(mv), toLogText(av),
                    toLogText(tz)));
            String message;
            if (dt.trim().isEmpty())
                message = ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_STARTDATENOTSPECIFIED);
            else if (ht.trim().isEmpty())
                message = ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_STARTHOURNOTSPECIFIED);
            else if (mt.trim().isEmpty())
                message = ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_STARTMINUTENOTSPECIFIED);
            else if ((at = at.trim()).isEmpty())
                message = String.format(ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_AMPMDESIGNATORNOTSPECIFIED),
                        amText, pmText);
            else if (null == tz)
                message = ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_TIMEZONENOTSPECIFIED);
            else {
                boolean success;
                try {
                    success = null != dv && null != startDateConverter.fromString(dt);
                } catch (DateTimeParseException ex) {
                    LOG.log(Level.INFO, "Caught start date string parse error", ex);
                    success = false;
                }
                if (success) {
                    try {
                        Integer i = hourConverter.fromString(ht);
                        success = null != i && i > 0 && i < 13;
                    } catch (NumberFormatException ex) {
                        LOG.log(Level.INFO, "Caught start hour string parse error", ex);
                        success = false;
                    }
                    if (null != hv && success) {
                        try {
                            Integer i = minuteConverter.fromString(mt);
                            success = null != i && i >= 0 && i < 60;
                        } catch (NumberFormatException ex) {
                            LOG.log(Level.INFO, "Caught start minute string parse error", ex);
                            success = false;
                        }
                        if (null == mv || !success)
                            message = ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_INVALIDSTARTMINUTE);
                        else if (null != av && (at.equalsIgnoreCase(amText) || at.equalsIgnoreCase(pmText))) {
                            message = "";
                        } else
                        message = String.format(ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_INVALIDAMPMDESIGNATOR),
                                amText, pmText);
                    } else
                        message = ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_INVALIDSTARTHOUR);
                } else
                    message = ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_INVALIDSTARTDATE);
            }
            LOG.fine(() -> String.format("Returning %s", toLogText(message)));
            return message;
        }, startDateTextProperty, hourTextProperty, minuteTextProperty, amPmTextProperty, startDateValueProperty,
            hourValueFactory.valueProperty(), minuteValueFactory.valueProperty(), amPmValueFactory.valueProperty(),
            timeZoneSelectionModel.selectedItemProperty());
    }

    @Override
    protected ZonedDateTime computeValue() {
        LocalDate d = startDateValueProperty.get();
        LocalTime t = localTime.get();
        TimeZone z = timeZoneSelectionModel.getSelectedItem();
        LOG.fine(() -> String.format("Computing %s: startDateValue=%s; localTime=%s; selectedTimeZone=%s", name, toLogText(d), toLogText(t),
                toLogText(z)));
        if (null != d && null != t && null != z) {
            ZonedDateTime result = ZonedDateTime.of(LocalDateTime.of(d, t), z.toZoneId());
            LOG.fine(() -> String.format("Returning %s", toLogText(result)));
            return result;
        }
        LOG.fine("Returning null");
        return null;
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
        LOG.fine(() -> String.format("Setting new %s value from %s to %s", name, get(), value));
        LocalDate localDate = value.toLocalDate();
        if (!localDate.equals(startDateValueProperty.get())) {
            LOG.fine(() -> String.format("Changing startDateValue from %s to %s", startDateValueProperty.get(), localDate));
            startDateValueProperty.set(localDate);
        }
        int i = value.getHour();
        switch (i) {
            case 0:
                if (amPmValueFactory.getValue()) {
                    LOG.fine("Changing amPmValue to false");
                    amPmValueFactory.setValue(false);
                }
                if (12 != hourValueFactory.getValue()) {
                    LOG.fine(() -> String.format("Changing hourValue from %s to 12", hourValueFactory.getValue()));
                    hourValueFactory.setValue(12);
                }
                break;
            case 12:
                if (!amPmValueFactory.getValue()) {
                    LOG.fine("Changing amPmValue to true");
                    amPmValueFactory.setValue(true);
                }
                if (12 != hourValueFactory.getValue()) {
                    LOG.fine(() -> String.format("Changing hourValue from %s to 12", hourValueFactory.getValue()));
                    hourValueFactory.setValue(12);
                }
                break;
            default:
                if (i > 12) {
                    i -= 12;
                    if (amPmValueFactory.getValue()) {
                        LOG.fine("Changing amPmValue to false");
                        amPmValueFactory.setValue(false);
                    }
                } else if (!amPmValueFactory.getValue()) {
                    LOG.fine("Changing amPmValue to true");
                    amPmValueFactory.setValue(true);
                }
                if (i != hourValueFactory.getValue()) {
                    int hv = i;
                    LOG.fine(() -> String.format("Changing hourValue from %s to %d", hourValueFactory.getValue(), hv));
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
