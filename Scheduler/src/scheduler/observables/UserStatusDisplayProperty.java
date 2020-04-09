package scheduler.observables;

import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class UserStatusDisplayProperty extends StringBinding implements ReadOnlyProperty<String> {

    private final ReadOnlyObjectProperty<UserStatus> backingProperty;
    private final Object bean;
    private final String name;

    public UserStatusDisplayProperty(Object bean, String name, ReadOnlyObjectProperty<UserStatus> statusProperty) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        super.bind(Objects.requireNonNull(backingProperty = Objects.requireNonNull(statusProperty)));
    }

    @Override
    protected String computeValue() {
        return UserStatus.toDisplayValue(backingProperty.get());
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
