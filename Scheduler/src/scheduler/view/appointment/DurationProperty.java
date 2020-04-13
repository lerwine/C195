package scheduler.view.appointment;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import scheduler.util.LogHelper;
import static scheduler.util.LogHelper.toLogText;
import scheduler.util.ResourceBundleHelper;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DurationProperty extends ObjectBinding<Duration>
        implements ReadOnlyProperty<Duration>, WritableObjectValue<Duration> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DurationProperty.class.getName()), Level.FINE);
    
    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final StartDateTimeProperty startDateTime;
    private final StringConverter<Integer> hourConverter;
    private final SpinnerValueFactory<Integer> hourValueFactory;
    private final StringProperty hourTextProperty;
    private final StringConverter<Integer> minuteConverter;
    private final SpinnerValueFactory<Integer> minuteValueFactory;
    private final StringProperty minuteTextProperty;

    public DurationProperty(Object bean, String name, StartDateTimeProperty startDateTime, Spinner<Integer> durationHourSpinner,
            Spinner<Integer> durationMinuteSpinner) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        this.startDateTime = startDateTime;
        hourTextProperty = durationHourSpinner.getEditor().textProperty();
        minuteTextProperty = durationMinuteSpinner.getEditor().textProperty();
        
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
                setValue(1);
                setWrapAround(false);
                setConverter(hourConverter);
            }

            @Override
            public void decrement(int steps) {
                LOG.fine(() -> String.format("Decrementing %s hour %d steps", name, steps));
                if (steps > 0) {
                    Duration d = get().minusHours(steps);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(d)));
                    set(d);
                }
            }

            @Override
            public void increment(int steps) {
                LOG.fine(() -> String.format("Incrementing %s hour %d steps", name, steps));
                if (steps > 0) {
                    Duration d = get().plusHours(steps);
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
                setValue(0);
                setWrapAround(true);
                setConverter(minuteConverter);
            }

            @Override
            public void decrement(int steps) {
                LOG.fine(() -> String.format("Decrementing %s minute %d steps", name, steps));
                if (steps > 0) {
                    Duration d = get();
                    if (d.getNano() > 0) {
                        d = d.withNanos(0);
                    }
                    long s = d.getSeconds() % 300L;
                    Duration result;
                    if (s > 0) {
                        d = d.minusSeconds(s);
                        result = (steps > 1) ? d.minusMinutes((steps - 1) * 5) : d;
                    } else
                        result = d.minusMinutes(steps * 5);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(result)));
                    set(result);
                }
            }

            @Override
            public void increment(int steps) {
                LOG.fine(() -> String.format("Incrementing %s minute %d steps", name, steps));
                if (steps > 0) {
                    Duration d = get();
                    if (d.getNano() > 0) {
                        d = d.withNanos(0).plusSeconds(1);
                    }
                    long s = d.getSeconds() % 300L;
                    Duration result;
                    if (s > 0) {
                        d = d.plusSeconds(300L - s);
                        result = (steps > 1) ? d.plusMinutes((steps - 1) * 5) : d;
                    } else
                        result = d.plusMinutes(steps * 5);
                    LOG.fine(() -> String.format("Changing value from %s to %s", toLogText(get()), toLogText(result)));
                    set(result);
                }
            }
        };

        dependencies = FXCollections.observableArrayList(startDateTime, hourValueFactory.valueProperty(), minuteValueFactory.valueProperty());
        super.bind(startDateTime, hourValueFactory.valueProperty(), minuteValueFactory.valueProperty());

        durationHourSpinner.setValueFactory(hourValueFactory);

        durationMinuteSpinner.setValueFactory(minuteValueFactory);
    }
    
    public StringBinding getValidationMessage() {
        return Bindings.createStringBinding(() -> {
            String ht = hourTextProperty.get();
            String mt = minuteTextProperty.get();
            Integer hv = hourValueFactory.getValue();
            Integer mv = minuteValueFactory.getValue();
            if (ht.trim().isEmpty())
                return ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_DURATIONHOURNOTSPECIFIED);
            if (mt.trim().isEmpty())
                return ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_DURATIONMINUTENOTSPECIFIED);
            
            boolean success = null != hv;
            if (success) {
                try {
                    Integer i = hourConverter.fromString(ht);
                    success = null != i && i > 0 && i < 13;
                } catch (NumberFormatException ex) {
                    LOG.log(Level.FINER, "Caught duration hour string parse error", ex);
                    success = false;
                }
            }
            if (!success)
                return ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_INVALIDDURATIONHOUR);
            success = null != mv;
            if (success) {
                try {
                    Integer i = minuteConverter.fromString(mt);
                    success = null != i && i >= 0 && i < 60;
                } catch (NumberFormatException ex) {
                    LOG.log(Level.FINER, "Caught duration minute string parse error", ex);
                    success = false;
                }
            }
            if (!success)
                return ResourceBundleHelper.getResourceString(EditAppointment.class, RESOURCEKEY_INVALIDDURATIONMINUTE);
            return "";
        }, hourTextProperty, minuteTextProperty, hourValueFactory.valueProperty(), minuteValueFactory.valueProperty());
    }

    @Override
    protected Duration computeValue() {
        Integer h = hourValueFactory.getValue();
        Integer m = minuteValueFactory.getValue();
        if (null != h && null != m)
            return Duration.ofMinutes(h * 60 + m);
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
    public void set(Duration value) {
        if (value.isNegative())
            throw new IllegalArgumentException("Duration cannot be negative");
        long m = value.toMinutes() % 60L;
        long h = (value.toMinutes() - m) / 60L;
        if (h > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Duration too large");
            
        if (minuteValueFactory.getValue() != m)
            minuteValueFactory.setValue((int)m);
        if (minuteValueFactory.getValue() != h)
            minuteValueFactory.setValue((int)h);
    }

    @Override
    public void setValue(Duration value) {
        set(value);
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
            super.unbind(startDateTime, hourValueFactory.valueProperty(), minuteValueFactory.valueProperty());
        }
    }
}
