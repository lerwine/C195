package scheduler.observables;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.model.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTypeProperty extends SimpleObjectProperty<AppointmentType> {

    private final ReadOnlyPropertyImpl readOnlyProperty;
    private final StringBindingProperty displayText;
    private final BooleanBindingProperty valid;

    /**
     *
     * @param bean
     * @param name
     * @param initialValue
     */
    public AppointmentTypeProperty(Object bean, String name, AppointmentType initialValue) {
        super(bean, name, initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
        displayText = new StringBindingProperty(this, "displayText", this) {
            @Override
            protected String computeValue() {
                return AppointmentType.toDisplayText(AppointmentTypeProperty.this.get());
            }
        };
        valid = new BooleanBindingProperty(this, "valid", this) {
            @Override
            protected boolean computeValue() {
                return null != AppointmentTypeProperty.this.get();
            }
        };
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

    public BooleanBindingProperty validProperty() {
        return valid;
    }

    public String getDisplayText() {
        return displayText.get();
    }

    public StringBindingProperty displayTextProperty() {
        return displayText;
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
