package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.beans.PropertyChangeEvent;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.DbObject;
import scheduler.model.ui.FxDbModel;
import scheduler.util.DB;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class DataObjectProperty<T extends DbObject> extends ReadOnlyObjectWrapper<T> implements ObservableDerivitive<T> {

    private static final Logger LOG = Logger.getLogger(DataObjectProperty.class.getName());

    private final HashMap<String, PropertyChangeHandler<?>> propertyMap;
    private final ReadOnlyIntegerProperty primaryKey;
    private final ReadOnlyObjectProperty<DataRowState> rowState;
    private final DerivedBooleanProperty<DataRowState> newRow;
    private final DerivedBooleanProperty<DataRowState> change;
    private final DerivedBooleanProperty<DataRowState> existingInDb;
    private final DerivedBooleanProperty<DataRowState> deleted;

    public DataObjectProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
        propertyMap = new HashMap<>();
        addListener(this::onDbObjectChanged);
        primaryKey = createReadOnlyIntegerProperty(DataAccessObject.PROP_PRIMARYKEY, (t) -> (null == t) ? Integer.MIN_VALUE : t.getPrimaryKey());
        rowState = createReadOnlyObjectProperty(DataAccessObject.PROP_ROWSTATE, (t) -> (null == t) ? DataRowState.DELETED : t.getRowState());
        newRow = new DerivedBooleanProperty<>(this, "newRow", rowState, (t) -> null != t && t == DataRowState.NEW);
        change = new DerivedBooleanProperty<>(this, "change", rowState, DataRowState::isChange);
        existingInDb = new DerivedBooleanProperty<>(this, "existingInDb", rowState, DataRowState::existsInDb);
        deleted = new DerivedBooleanProperty<>(this, "newRow", rowState, (t) -> null != t && t == DataRowState.DELETED);
    }

    public final int getPrimaryKey() {
        return primaryKey.get();
    }

    public final ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey;
    }

    public final DataRowState getRowState() {
        return rowState.get();
    }

    public final ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState;
    }

    public boolean isNewRow() {
        return newRow.get();
    }

    public ReadOnlyBooleanProperty newRowProperty() {
        return newRow.getReadOnlyBooleanProperty();
    }

    public boolean isDeleted() {
        return deleted.get();
    }

    public ReadOnlyBooleanProperty deletedProperty() {
        return deleted.getReadOnlyBooleanProperty();
    }

    public boolean isChange() {
        return change.get();
    }

    public ReadOnlyBooleanProperty changeProperty() {
        return change.getReadOnlyBooleanProperty();
    }

    public boolean isExistingInDb() {
        return existingInDb.get();
    }

    public ReadOnlyBooleanProperty existingInDbProperty() {
        return existingInDb.getReadOnlyBooleanProperty();
    }

    private void onDbObjectChanged(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (null != oldValue) {
            oldValue.removePropertyChangeListener(this::onDbPropertyChange);
        }
        if (null != newValue) {
            newValue.addPropertyChangeListener(this::onDbPropertyChange);
        }
        propertyMap.values().forEach((t) -> t.accept(newValue));
    }

    public final synchronized ReadOnlyIntegerProperty createReadOnlyIntegerProperty(String name, ToIntFunction<T> getter) {
        ReadOnlyIntegerProperty result = new ReadOnlyIntegerProperty() {
            private final PropertyChangeHandler<Integer> propertyChangeHandler;
            private ExpressionHelper<Number> helper = null;

            {
                propertyChangeHandler = new PropertyChangeHandler<>(getter.applyAsInt(DataObjectProperty.this.get()),
                        (t) -> getter.applyAsInt(t), this::onChanged);
                if (propertyMap.containsKey(name)) {
                    PropertyChangeHandler<?> h = propertyMap.get(name);
                    while (null != h.next) {
                        h = h.next;
                    }
                    h.next = propertyChangeHandler;
                } else {
                    propertyMap.put(name, propertyChangeHandler);
                }
            }

            private void onChanged() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public int get() {
                return getter.applyAsInt(DataObjectProperty.this.get());
            }

            protected void fireValueChangedEvent() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public void addListener(ChangeListener<? super Number> listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(ChangeListener<? super Number> listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public Object getBean() {
                return DataObjectProperty.this;
            }

            @Override
            public String getName() {
                return name;
            }

        };
        return result;
    }

    public final synchronized ReadOnlyBooleanProperty createReadOnlyBooleanProperty(String name, Predicate<T> getter) {
        ReadOnlyBooleanProperty result = new ReadOnlyBooleanProperty() {
            private final PropertyChangeHandler<Boolean> propertyChangeHandler;
            private ExpressionHelper<Boolean> helper = null;

            {
                propertyChangeHandler = new PropertyChangeHandler<>(getter.test(DataObjectProperty.this.get()),
                        (t) -> getter.test(t), this::onChanged);
                if (propertyMap.containsKey(name)) {
                    PropertyChangeHandler<?> h = propertyMap.get(name);
                    while (null != h.next) {
                        h = h.next;
                    }
                    h.next = propertyChangeHandler;
                } else {
                    propertyMap.put(name, propertyChangeHandler);
                }
            }

            private void onChanged() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public boolean get() {
                return getter.test(DataObjectProperty.this.get());
            }

            protected void fireValueChangedEvent() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public void addListener(ChangeListener<? super Boolean> listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(ChangeListener<? super Boolean> listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public Object getBean() {
                return DataObjectProperty.this;
            }

            @Override
            public String getName() {
                return name;
            }

        };
        return result;
    }

    public final synchronized ReadOnlyStringProperty createReadOnlyStringProperty(String name, Function<T, String> getter) {
        ReadOnlyStringProperty result = new ReadOnlyStringProperty() {
            private final PropertyChangeHandler<String> propertyChangeHandler;
            private ExpressionHelper<String> helper = null;

            {
                propertyChangeHandler = new PropertyChangeHandler<>(getter.apply(DataObjectProperty.this.get()), getter, this::onChanged);
                if (propertyMap.containsKey(name)) {
                    PropertyChangeHandler<?> h = propertyMap.get(name);
                    while (null != h.next) {
                        h = h.next;
                    }
                    h.next = propertyChangeHandler;
                } else {
                    propertyMap.put(name, propertyChangeHandler);
                }
            }

            private void onChanged() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public String get() {
                return getter.apply(DataObjectProperty.this.get());
            }

            protected void fireValueChangedEvent() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public void addListener(ChangeListener<? super String> listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(ChangeListener<? super String> listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public Object getBean() {
                return DataObjectProperty.this;
            }

            @Override
            public String getName() {
                return name;
            }

        };
        return result;
    }

    public final synchronized ReadOnlyObjectProperty<LocalDateTime> createReadOnlyDateTimeProperty(String name, Function<T, Timestamp> getter) {
        ReadOnlyObjectProperty<LocalDateTime> result = new ReadOnlyObjectProperty<LocalDateTime>() {
            private final PropertyChangeHandler<LocalDateTime> propertyChangeHandler;
            private ExpressionHelper<LocalDateTime> helper = null;
            private LocalDateTime currentValue;
            private Timestamp currentTs;

            {
                currentValue = apply(getter.apply(DataObjectProperty.this.get()));
                propertyChangeHandler = new PropertyChangeHandler<>(currentValue, (t) -> apply(getter.apply(t)), this::onChanged);
                if (propertyMap.containsKey(name)) {
                    PropertyChangeHandler<?> h = propertyMap.get(name);
                    while (null != h.next) {
                        h = h.next;
                    }
                    h.next = propertyChangeHandler;
                } else {
                    propertyMap.put(name, propertyChangeHandler);
                }
            }

            private void onChanged() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            private LocalDateTime apply(Timestamp value) {
                if (!Objects.equals(value, currentTs)) {
                    currentTs = value;
                    currentValue = (null == value) ? null : DB.toLocalDateTime(currentTs);
                }
                return currentValue;
            }

            @Override
            public LocalDateTime get() {
                return currentValue;
            }

            protected void fireValueChangedEvent() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public void addListener(ChangeListener<? super LocalDateTime> listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(ChangeListener<? super LocalDateTime> listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public Object getBean() {
                return DataObjectProperty.this;
            }

            @Override
            public String getName() {
                return name;
            }

        };
        return result;
    }

    public final synchronized <U> ReadOnlyObjectProperty<U> createReadOnlyObjectProperty(String name, Function<T, U> getter) {
        ReadOnlyObjectProperty<U> result = new ReadOnlyObjectProperty<U>() {
            private final PropertyChangeHandler<U> propertyChangeHandler;
            private ExpressionHelper<U> helper = null;

            {
                propertyChangeHandler = new PropertyChangeHandler<>(getter.apply(DataObjectProperty.this.get()), getter, this::onChanged);
                if (propertyMap.containsKey(name)) {
                    PropertyChangeHandler<?> h = propertyMap.get(name);
                    while (null != h.next) {
                        h = h.next;
                    }
                    h.next = propertyChangeHandler;
                } else {
                    propertyMap.put(name, propertyChangeHandler);
                }
            }

            private void onChanged() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public U get() {
                return getter.apply(DataObjectProperty.this.get());
            }

            protected void fireValueChangedEvent() {
                ExpressionHelper.fireValueChangedEvent(helper);
            }

            @Override
            public void addListener(ChangeListener<? super U> listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(ChangeListener<? super U> listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public Object getBean() {
                return DataObjectProperty.this;
            }

            @Override
            public String getName() {
                return name;
            }

        };
        return result;
    }

    public final <U extends DbObject, S extends FxDbModel<? extends U>> ReadOnlyObjectProperty<S> createReadOnlyNestedModelProperty(String name,
            final ReadOnlyObjectProperty<U> daoProperty, final Function<U, S> factory) {
        ReadOnlyObjectProperty<S> result = new ReadOnlyObjectProperty<S>() {
            private S model = factory.apply(daoProperty.get());
            private ExpressionHelper<S> helper = null;

            {
                daoProperty.addListener(this::onDaoChanged);
            }

            private synchronized void onDaoChanged(ObservableValue<? extends U> observable, U oldValue, U newValue) {
                if (!Objects.equals((null == model) ? null : model.dataObject(), newValue)) {
                    S m = factory.apply(newValue);
                    if (!Objects.equals(m, model)) {
                        model = m;
                        ExpressionHelper.fireValueChangedEvent(helper);
                    }
                }
            }

            @Override
            public S get() {
                return model;
            }

            @Override
            public void addListener(ChangeListener<? super S> listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(ChangeListener<? super S> listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public Object getBean() {
                return DataObjectProperty.this;
            }

            @Override
            public String getName() {
                return name;
            }

        };
        return result;
    }

    public final <U extends DbObject, S extends FxDbModel<? extends U>> ReadOnlyObjectProperty<S> createReadOnlyNestedModelProperty(String name,
            Function<T, U> getter, Function<U, S> factory) {
        return createReadOnlyNestedModelProperty(name, createReadOnlyObjectProperty(name, getter), factory);
    }

    // We are handling any exceptions on purpose.
    @SuppressWarnings("UseSpecificCatch")
    private void onDbPropertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyMap.containsKey(propertyName)) {
            try {
                propertyMap.get(propertyName).accept(get());
            } catch (Throwable ex) {
                LOG.log(Level.SEVERE, String.format("Uncaught exception firing %s property change event",
                        propertyName), ex);
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
            }
        }
    }

    private class PropertyChangeHandler<U> implements Consumer<T> {

        private final Function<T, U> getter;
        private final Runnable onChange;
        private U lastKnownValue;
        private PropertyChangeHandler<?> next;

        PropertyChangeHandler(U initialValue, Function<T, U> getter, Runnable onChange) {
            this.getter = getter;
            this.onChange = onChange;
            lastKnownValue = initialValue;
            next = null;
        }

        @Override
        public void accept(T obj) {
            try {
                U newValue = getter.apply(obj);
                if (!Objects.equals(lastKnownValue, newValue)) {
                    lastKnownValue = newValue;
                    onChange.run();
                }
            } catch (Throwable e) {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            } finally {
                if (null != next) {
                    next.accept(obj);
                }
            }
        }
    }

}
