package scheduler.observables;

import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.model.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTypeProperty extends SimpleObjectProperty<AppointmentType> {

    private final ReadOnlyPropertyImpl readOnlyProperty;
    private final CalculatedStringProperty<AppointmentType> displayText;
    private final CalculatedBooleanProperty<AppointmentType> valid;

    /**
     *
     * @param bean
     * @param name
     * @param initialValue
     */
    public AppointmentTypeProperty(Object bean, String name, AppointmentType initialValue) {
        super(bean, name, initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
        displayText = new CalculatedStringProperty<>(this, "displayText", this, AppointmentType::toDisplayText);
        valid = new CalculatedBooleanProperty<>(this, "valid", this, Objects::nonNull);
    }

    public AppointmentType getSafe() {
        AppointmentType type = get();
        return (null == type) ? AppointmentType.OTHER : type;
    }

    public ReadOnlyObjectProperty<AppointmentType> getReadOnlyProperty() {
        return readOnlyProperty;
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    public String getDisplayText() {
        return displayText.get();
    }

    public ReadOnlyStringProperty displayTextProperty() {
        return displayText.getReadOnlyStringProperty();
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<AppointmentType> {

        @Override
        public AppointmentType get() {
            return AppointmentTypeProperty.this.getSafe();
        }

        @Override
        public Object getBean() {
            return AppointmentTypeProperty.this.getBean();
        }

        @Override
        public String getName() {
            return AppointmentTypeProperty.this.getName();
        }

        private ReadOnlyPropertyImpl() {
            AppointmentTypeProperty.this.addListener((observable, oldValue, newValue) -> {
                ReadOnlyPropertyImpl.this.fireValueChangedEvent();
            });
        }

    }

}
