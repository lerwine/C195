package scheduler.observables;

import java.util.Objects;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTypeDisplayProperty extends StringBindingProperty {

    private final ReadOnlyProperty<AppointmentType> backingProperty;

    public AppointmentTypeDisplayProperty(Object bean, String name, ReadOnlyProperty<AppointmentType> statusProperty) {
        super(bean, name, statusProperty);
        backingProperty = Objects.requireNonNull(statusProperty);
    }

    @Override
    protected String computeValue() {
        return AppointmentType.toDisplayText(backingProperty.getValue());
    }

}
