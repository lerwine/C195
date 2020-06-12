package scheduler.model.ui;

import com.sun.javafx.event.EventHandlerManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.util.Pair;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.util.DB;
import scheduler.view.ModelFilter;
import scheduler.view.event.ActivityType;
import scheduler.view.event.ModelItemEvent;
import scheduler.view.task.WaitBorderPane;

/**
 * Java FX object model for a {@link DataAccessObject} object.
 * <dl>
 * <dt>{@link FxRecordModel.ModelFactory}</dt><dd>Base factory class for {@link FxDbModel} objects.</dd>
 * <dt>{@link scheduler.dao.DataAccessObject}</dt><dd>Base class for corresponding data access objects.</dd>
 * </dl>
 * Entity-specific extensions:
 * <ul>
 * <li>{@link scheduler.model.ui.AddressModel}</li>
 * <li>{@link scheduler.model.ui.CustomerModel}</li>
 * <li>{@link scheduler.model.ui.AddressModel}</li>
 * <li>{@link scheduler.model.ui.CityModel}</li>
 * <li>{@link scheduler.model.ui.CountryModel}</li>
 * </ul>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} to be used for data access operations.
 */
public abstract class FxRecordModel<T extends DataAccessObject> implements IFxRecordModel<T>, EventTarget {

    private static final Logger LOG = Logger.getLogger(FxRecordModel.class.getName());
    /**
     * The name of the 'newRow' property.
     */
    public static final String PROP_NEWROW = "newRow";
    /**
     * The name of the 'changed' property.
     */
    public static final String PROP_CHANGED = "changed";
    /**
     * The name of the 'existingInDb' property.
     */
    public static final String PROP_EXISTINGINDB = "existingInDb";

    private final EventHandlerManager eventHandlerManager;
    private final T dataObject;
    private final ReadOnlyJavaBeanIntegerProperty primaryKey;
    private final ReadOnlyJavaBeanObjectProperty<DataRowState> rowState;
    private final ReadOnlyJavaBeanObjectProperty<Timestamp> rawCreateDate;
    private final ReadOnlyJavaBeanStringProperty createdBy;
    private final ReadOnlyJavaBeanObjectProperty<Timestamp> rawLastModifiedDate;
    private final ReadOnlyJavaBeanStringProperty lastModifiedBy;
    private final ReadOnlyObjectBindingProperty<LocalDateTime> createDate;
    private final ReadOnlyObjectBindingProperty<LocalDateTime> lastModifiedDate;
    private final ReadOnlyBooleanBindingProperty newRow;
    private final ReadOnlyBooleanBindingProperty changed;
    private final ReadOnlyBooleanBindingProperty existingInDb;

    /**
     * Initializes a new ModelBase object.
     *
     * @param dao The {@link DataAccessObject} to be used for data access operations.
     * @todo Add listeners for {@link #dataObject} changes in case a another object that represents the same record is modified.
     * @todo Add listeners for {@link DataAccessObject} changes for properties containing related {@link FxDbModel} objects so the property is updated
     * whenever a change occurs.
     */
    protected FxRecordModel(T dao) {
        if (dao.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException(String.format("%s has been deleted", dao.getClass().getName()));
        }
        eventHandlerManager = new EventHandlerManager(this);
        dataObject = dao;
        try {
            primaryKey = ReadOnlyJavaBeanIntegerPropertyBuilder.create().bean(dao).name(PROP_PRIMARYKEY).build();
            rowState = ReadOnlyJavaBeanObjectPropertyBuilder.<DataRowState>create().bean(dao).name(PROP_ROWSTATE).build();
            rawCreateDate = ReadOnlyJavaBeanObjectPropertyBuilder.<Timestamp>create().bean(dao).name(PROP_CREATEDATE).build();
            createdBy = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(dao).name(PROP_CREATEDBY).build();
            rawLastModifiedDate = ReadOnlyJavaBeanObjectPropertyBuilder.<Timestamp>create().bean(dao).name(PROP_LASTMODIFIEDDATE).build();
            lastModifiedBy = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(dao).name(PROP_LASTMODIFIEDBY).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        createDate = new ReadOnlyObjectBindingProperty<>(this, PROP_CREATEDATE, () -> DB.toLocalDateTime(rawCreateDate.get()), rawCreateDate);
        lastModifiedDate = new ReadOnlyObjectBindingProperty<>(this, PROP_LASTMODIFIEDDATE,
                () -> DB.toLocalDateTime(rawLastModifiedDate.get()), rawLastModifiedDate);
        newRow = new ReadOnlyBooleanBindingProperty(this, PROP_NEWROW, () -> DataRowState.isNewRow(rowState.get()), rowState);
        changed = new ReadOnlyBooleanBindingProperty(this, PROP_CHANGED, () -> DataRowState.isChanged(rowState.get()), rowState);
        existingInDb = new ReadOnlyBooleanBindingProperty(this, PROP_EXISTINGINDB, () -> DataRowState.existsInDb(rowState.get()), rowState);
    }

    @Override
    public final T dataObject() {
        return dataObject;
    }

    @Override
    public final int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public final ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey;
    }

    @Override
    public final LocalDateTime getCreateDate() {
        return createDate.get();
    }

    @Override
    public final ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return createDate;
    }

    @Override
    public final String getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public final ReadOnlyStringProperty createdByProperty() {
        return createdBy;
    }

    @Override
    public final LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    @Override
    public final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate;
    }

    @Override
    public final String getLastModifiedBy() {
        return lastModifiedBy.get();
    }

    @Override
    public final ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy;
    }

    @Override
    public final DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public final ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState;
    }

    public final boolean isNewRow() {
        return newRow.get();
    }

    public final ReadOnlyBooleanProperty newRowProperty() {
        return newRow;
    }

    public final boolean isChanged() {
        return changed.get();
    }

    public final ReadOnlyBooleanProperty changedProperty() {
        return changed;
    }

    public final boolean isExistingInDb() {
        return existingInDb.get();
    }

    public final ReadOnlyBooleanProperty existingInDbProperty() {
        return existingInDb;
    }

    public final boolean modelEquals(T model) {
        return null != model && dataObject.equals(model);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return dataObject.buildEventDispatchChain(tail.append(eventHandlerManager));
    }

    /**
     * Registers a {@link ModelItemEvent} handler in the {@code EventHandlerManager} for the current {@link FxRecordModel}.
     *
     * @param <U> The type of {@link ModelItemEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public final <U extends ModelItemEvent<? extends FxRecordModel<T>, T>> void addEventHandler(EventType<U> type, WeakEventHandler<U> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    /**
     * Registers a {@link ModelItemEvent} filter in the {@code EventHandlerManager} for the current {@link FxRecordModel}.
     *
     * @param <U> The type of {@link ModelItemEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public final <U extends ModelItemEvent<? extends FxRecordModel<T>, T>> void addEventFilter(EventType<U> type, WeakEventHandler<U> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    /**
     * Unregisters a {@link ModelItemEvent} handler in the {@code EventHandlerManager} for the current {@link FxRecordModel}.
     *
     * @param <U> The type of {@link ModelItemEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public final <U extends ModelItemEvent<? extends FxRecordModel<T>, T>> void removeEventHandler(EventType<U> type, WeakEventHandler<U> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    /**
     * Unregisters a {@link ModelItemEvent} filter in the {@code EventHandlerManager} for the current {@link FxRecordModel}.
     *
     * @param <U> The type of {@link ModelItemEvent}.
     * @param type The event type.
     * @param eventHandler The event handler.
     */
    public final <U extends ModelItemEvent<? extends FxRecordModel<T>, T>> void removeEventFilter(EventType<U> type, WeakEventHandler<U> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

    public static abstract class ModelFactory<T extends DataAccessObject, U extends FxRecordModel<T>> implements EventTarget {

        private final EventHandlerManager eventHandlerManager;

        protected ModelFactory(EventType<? extends ModelItemEvent<U, T>> anyEventType) {
            eventHandlerManager = new EventHandlerManager(this);
            eventHandlerManager.addEventHandler(anyEventType, this::handleModelEvent);
        }

        private void handleModelEvent(ModelItemEvent<U, T> event) {
            if (!event.isConsumed()) {
                U model = event.getState().getModel();
                if (null != model) {
                    Event.fireEvent(model, event);
                } else {
                    Event.fireEvent(event.getDataAccessObject(), event);
                }
            }
        }

        public abstract DataAccessObject.DaoFactory<T> getDaoFactory();

        public abstract U createNew(T dao);

        /**
         * Applies changes made in the {@link FxRecordModel} to the underlying {@link DataAccessObject}.
         *
         * @param item The model item.
         * @return The {@link DataAccessObject} with changes applied.
         * @deprecated Use {@link #handle(scheduler.view.event.ModelItemEvent)}.
         * @todo Remove usages of scheduler.model.ui.FxRecordModel.ModelFactory#delete
         */
        @Deprecated
        public abstract T updateDAO(U item);

        /**
         *
         * @deprecated Use {@link #handle(scheduler.view.event.ModelItemEvent)}.
         * @todo Remove usages of scheduler.model.ui.FxRecordModel.ModelFactory#updateItemProperties
         */
        @Deprecated
        protected abstract void updateItemProperties(U item, T dao);

        public abstract ModelFilter<T, U, ? extends DaoFilter<T>> getAllItemsFilter();

        public abstract ModelFilter<T, U, ? extends DaoFilter<T>> getDefaultFilter();

        /**
         * Updates the {@link FxRecordModel} with changes from a {@link DataAccessObject}.
         *
         * @param item The {@link FxRecordModel} to be updated.
         * @param updatedDao The source {@link DataAccessObject}.
         * @deprecated Use {@link #handle(scheduler.view.event.ModelItemEvent)}.
         * @todo Remove usages of scheduler.model.ui.FxRecordModel.ModelFactory#updateItemProperties
         */
        @Deprecated
        public final void updateItem(U item, T updatedDao) {
            T currentDao = item.dataObject();
            if (Objects.requireNonNull(updatedDao) == currentDao) {
                updateItemProperties(item, currentDao);
                return;
            }
            if (updatedDao.getRowState() == DataRowState.NEW) {
                throw new IllegalArgumentException("Replacement data access object cannot have a new row state");
            } else if (currentDao.getRowState() != DataRowState.NEW && currentDao.getPrimaryKey() != updatedDao.getPrimaryKey()) {
                throw new IllegalArgumentException("Replacement data access object does not represent the same record as the current data object.");
            }
            getDaoFactory().synchronize(updatedDao, currentDao);
            updateItemProperties(item, currentDao);
        }

        public final void loadAsync(WaitBorderPane waitBorderPane, DaoFilter<T> filter, ObservableList<U> target, Consumer<ObservableList<U>> onSuccess,
                Consumer<Throwable> onFail) {
            DataAccessObject.DaoFactory<T> factory = getDaoFactory();
            factory.loadAsync(waitBorderPane, filter, (t) -> {
                ArrayList<U> newItems = new ArrayList<>();
                t.forEach((u) -> {
                    Optional<U> existing = target.stream().filter((s) -> s.dataObject().equals(u)).findFirst();
                    if (existing.isPresent()) {
                        updateItem(existing.get(), u);
                        newItems.add(existing.get());
                    } else {
                        newItems.add(createNew(u));
                    }
                });
                for (int i = 0; i < target.size() && i < newItems.size(); i++) {
                    target.set(i, newItems.get(i));
                }
                while (target.size() < newItems.size()) {
                    target.add(newItems.get(target.size()));
                }
                while (target.size() > newItems.size()) {
                    target.remove(newItems.size());
                }
                if (null != onSuccess) {
                    onSuccess.accept(target);
                }
            }, onFail);
        }

        public final void loadAsync(WaitBorderPane waitBorderPane, DaoFilter<T> filter, ObservableList<U> target, Consumer<ObservableList<U>> onSuccess) {
            loadAsync(waitBorderPane, filter, target, onSuccess, null);
        }

        public final void loadAsync(DaoFilter<T> filter, ObservableList<U> target, Consumer<ObservableList<U>> onSuccess,
                Consumer<Throwable> onFail) {
            DataAccessObject.DaoFactory<T> factory = getDaoFactory();
            factory.loadAsync(filter, (t) -> {
                ArrayList<U> newItems = new ArrayList<>();
                t.forEach((u) -> {
                    Optional<U> existing = target.stream().filter((s) -> s.dataObject().equals(u)).findFirst();
                    if (existing.isPresent()) {
                        updateItem(existing.get(), u);
                        newItems.add(existing.get());
                    } else {
                        newItems.add(createNew(u));
                    }
                });
                for (int i = 0; i < target.size() && i < newItems.size(); i++) {
                    target.set(i, newItems.get(i));
                }
                while (target.size() < newItems.size()) {
                    target.add(newItems.get(target.size()));
                }
                while (target.size() > newItems.size()) {
                    target.remove(newItems.size());
                }
                if (null != onSuccess) {
                    onSuccess.accept(target);
                }
            }, onFail);
        }

        public final void loadAsync(DaoFilter<T> filter, ObservableList<U> target, Consumer<ObservableList<U>> onSuccess) {
            loadAsync(filter, target, onSuccess, null);
        }

        public final Optional<U> find(Iterator<U> source, T dao) {
            if (null != source) {
                while (source.hasNext()) {
                    U m = source.next();
                    if (null != m && ModelHelper.areSameRecord(m.dataObject(), dao)) {
                        return Optional.of(m);
                    }
                }
            }
            return Optional.empty();
        }

        public final Optional<U> find(Iterable<U> source, T dao) {
            if (null != source) {
                return find(source.iterator(), dao);
            }
            return Optional.empty();
        }

        public final Optional<U> find(Stream<U> source, T dao) {
            if (null != source) {
                return find(source.iterator(), dao);
            }
            return Optional.empty();
        }

        public abstract <E extends ModelItemEvent<U, T>> E createModelItemEvent(U model, Object source, EventTarget target, ActivityType action);

        @Override
        public final EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            return getDaoFactory().buildEventDispatchChain(tail.append(eventHandlerManager));
        }

        /**
         * Registers a {@link ModelItemEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <S> The type of {@link ModelItemEvent}.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <S extends ModelItemEvent<U, T>> void addEventHandler(EventType<S> type, WeakEventHandler<S> eventHandler) {
            eventHandlerManager.addEventHandler(type, eventHandler);
        }

        /**
         * Registers a {@link ModelItemEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <S> The type of {@link ModelItemEvent}.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <S extends ModelItemEvent<U, T>> void addEventFilter(EventType<S> type, WeakEventHandler<S> eventHandler) {
            eventHandlerManager.addEventFilter(type, eventHandler);
        }

        /**
         * Unregisters a {@link ModelItemEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <S> The type of {@link ModelItemEvent}.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <S extends ModelItemEvent<U, T>> void removeEventHandler(EventType<S> type, WeakEventHandler<S> eventHandler) {
            eventHandlerManager.removeEventHandler(type, eventHandler);
        }

        /**
         * Unregisters a {@link ModelItemEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <S> The type of {@link ModelItemEvent}.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <S extends ModelItemEvent<U, T>> void removeEventFilter(EventType<S> type, WeakEventHandler<S> eventHandler) {
            eventHandlerManager.removeEventFilter(type, eventHandler);
        }

    }

    @FunctionalInterface
    public interface PropertyValueExporter<T extends FxRecordModel<? extends DataAccessObject>> {

        Pair<String, String> toIdentity(T model, int index, Iterable<String> exportData);
    }

}
