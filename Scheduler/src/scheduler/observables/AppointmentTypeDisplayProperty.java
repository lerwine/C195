package scheduler.observables;

import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.AppointmentType;

/**
 *
 * @author lerwi
 */
public class AppointmentTypeDisplayProperty extends StringBinding implements ReadOnlyProperty<String> {

    private final ReadOnlyProperty<AppointmentType> backingProperty;
    private final Object bean;
    private final String name;

    public AppointmentTypeDisplayProperty(Object bean, String name, ReadOnlyProperty<AppointmentType> statusProperty) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        super.bind(Objects.requireNonNull(backingProperty = Objects.requireNonNull(statusProperty)));
    }

    @Override
    protected String computeValue() {
        return AppointmentType.toAppointmentTypeDisplay(backingProperty.getValue());
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
    public ObservableList<?> getDependencies() {
        return FXCollections.singletonObservableList(backingProperty);
    }

    @Override
    public void dispose() {
        super.unbind(backingProperty);
    }

}
