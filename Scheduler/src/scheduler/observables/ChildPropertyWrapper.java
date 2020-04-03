package scheduler.observables;

import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A wrapper binding for a child property
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 * @param <S>
 */
public class ChildPropertyWrapper<T, S> extends ReadOnlyObjectPropertyBase<T> implements Binding<T> {

    private ReadOnlyObjectProperty<S> source;
    private final Function<S, ReadOnlyProperty<T>> getChildProperty;
    private final Object bean;
    private final String name;
    private ReadOnlyProperty<T> property;
    private final ObservableList<Observable> dependencies;
    private ObservableList<?> unmodifiableDependencies;
    private boolean valid = true;
    private final InvalidationListener sourceInvalidationListener;
    private final InvalidationListener propertyInvalidationListener;

    /**
     * Initializes a new child property wrapper object.
     *
     * @param bean The {@code Object} that contains this property or {@code null} if this property is not contained in an object.
     * @param name The name of the property.
     * @param source The parent {@link ObjectExpression}.
     * @param getChildProperty Gets the {@link ObjectExpression} that represents the child property.
     */
    public ChildPropertyWrapper(Object bean, String name, ReadOnlyObjectProperty<S> source, Function<S, ReadOnlyProperty<T>> getChildProperty) {
        this.source = source;
        dependencies = FXCollections.observableArrayList(source);
        this.getChildProperty = getChildProperty;
        this.bean = bean;
        this.name = (name == null) ? "" : name;
        propertyInvalidationListener = (Observable observable) -> {
            if (property == observable) {
                invalidate();
            }
        };
        sourceInvalidationListener = (Observable observable) -> {
            ReadOnlyProperty<T> oldProperty = property;
            S u = this.source.getValue();
            if ((property = (null == u) ? null : this.getChildProperty.apply(u)) == oldProperty) {
                return;
            }
            try {
                try {
                    if (null != oldProperty) {
                        try {
                            oldProperty.removeListener(propertyInvalidationListener);
                        } finally {
                            dependencies.remove(oldProperty);
                        }
                    }
                } finally {
                    if (null != property) {
                        try {
                            property.addListener(propertyInvalidationListener);
                        } finally {
                            dependencies.add(property);
                        }
                    }
                }
            } finally {
                invalidate();
            }
        };
        S target = source.getValue();
        if (null != target) {
            property = this.getChildProperty.apply(target);
            if (null != property) {
                dependencies.add(property);
                property.addListener(propertyInvalidationListener);
            }
        } else {
            property = null;
        }
        source.addListener(sourceInvalidationListener);
    }

    /**
     * Initializes a new child property wrapper object.
     *
     * @param source The parent {@link ObjectExpression}.
     * @param getChildProperty Gets the {@link ObjectExpression} that represents the child property.
     */
    public ChildPropertyWrapper(ReadOnlyObjectProperty<S> source, Function<S, ReadOnlyProperty<T>> getChildProperty) {
        this(null, null, source, getChildProperty);
    }

    public S getSource() {
        return source.getValue();
    }

    public ReadOnlyObjectProperty<S> sourceProperty() {
        return source;
    }

    /**
     * Gets the default value for instances when the child property is null.
     *
     * @return The default value for instances when the child property is null.
     */
    protected T onChildValueNull() {
        return null;
    }

    @Override
    public T get() {
        ReadOnlyProperty<T> p = property;
        return (null == p) ? onChildValueNull() : p.getValue();
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
    public boolean isValid() {
        return valid;
    }

    @Override
    public void invalidate() {
        valid = false;
        fireValueChangedEvent();
    }

    @Override
    public ObservableList<?> getDependencies() {
        if (null == unmodifiableDependencies) {
            unmodifiableDependencies = FXCollections.unmodifiableObservableList(dependencies);
        }
        return unmodifiableDependencies;
    }

    @Override
    public void dispose() {
        try {
            source.removeListener(sourceInvalidationListener);
        } finally {
            try {
                if (null != property) {
                    try {
                        property.addListener(propertyInvalidationListener);
                    } finally {
                        property = null;
                    }
                }
            } finally {
                source = null;
                dependencies.clear();
            }
        }
    }

}
