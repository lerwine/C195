package scheduler.view.appointment;

import java.time.Duration;
import java.time.ZonedDateTime;
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

/**
 *
 * @author Leonard T. Erwine
 */
public class DurationProperty extends ObjectBinding<Duration>
        implements ReadOnlyProperty<Duration>, WritableObjectValue<Duration> {

    private static final Logger LOG = Logger.getLogger(DurationProperty.class.getName());
    
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
                setValue(0);
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
            // PENDING: Internationalize these
            if (ht.trim().isEmpty())
                return "Duration hour not specified";
            if (mt.trim().isEmpty())
                return "Duration minute not specified";
            
            boolean success = null != hv;
            if (success) {
                try {
                    Integer i = hourConverter.fromString(ht);
                    success = null != i && i > 0 && i < 13;
                } catch (NumberFormatException ex) {
                    LOG.log(Level.INFO, "Caught duration hour string parse error", ex);
                    success = false;
                }
            }
            if (!success)
                return "Invalid duration hour";
            success = null != mv;
            if (success) {
                try {
                    Integer i = minuteConverter.fromString(mt);
                    success = null != i && i >= 0 && i < 60;
                } catch (NumberFormatException ex) {
                    LOG.log(Level.INFO, "Caught duration minute string parse error", ex);
                    success = false;
                }
            }
            if (!success)
                return "Invalid duration minute";
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
        ZonedDateTime start = startDateTime.get();
        if (null == start)
            throw new IllegalStateException("Start date/time not set");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
