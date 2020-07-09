package scheduler.model.ui;

import com.sun.javafx.event.EventHandlerManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;
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
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.util.Pair;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.events.ModelEvent;
import scheduler.events.OperationRequestEvent;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.util.DB;
import scheduler.util.LogHelper;
import scheduler.view.ModelFilter;

/**
 * Java FX object model for a {@link DataAccessObject} object.
 * <dl>
 * <dt>{@link FxRecordModel.FxModelFactory}</dt><dd>Base factory class for {@link FxDbModel} objects.</dd>
 * <dt>{@link scheduler.dao.DataAccessObject}</dt><dd>Base class for corresponding data access objects.</dd>
 * </dl>
 * Entity-specific implementations:
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
public abstract class FxRecordModel<T extends DataAccessObject> implements IFxRecordModel<T> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FxRecordModel.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(FxRecordModel.class.getName());

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

//    private final EventHandlerManager eventHandlerManager;
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
     * @todo Add listeners for {@link DataAccessObject} changes for properties containing related {@link FxDbModel} objects so the property is updated
     * whenever a change occurs.
     */
    protected FxRecordModel(T dao) {
        if (dao.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException(String.format("%s has been deleted", dao.getClass().getName()));
        }
//        eventHandlerManager = new EventHandlerManager(this);
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

    public static abstract class FxModelFactory<D extends DataAccessObject, M extends FxRecordModel<D>, E extends ModelEvent<D, M>> implements EventTarget {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FxModelFactory.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(FxModelFactory.class.getName());

        private final EventHandlerManager eventHandlerManager;

        protected FxModelFactory() {
            eventHandlerManager = new EventHandlerManager(this);
//            eventHandlerManager.addEventHandler(anyEventType, this::onModelEvent);
        }

        public abstract DataAccessObject.DaoFactory<D, E> getDaoFactory();

        public abstract M createNew(D dao);

        public abstract ModelFilter<D, M, ? extends DaoFilter<D>> getAllItemsFilter();

        public abstract ModelFilter<D, M, ? extends DaoFilter<D>> getDefaultFilter();

        public final Optional<M> find(Iterator<M> source, D dao) {
            if (null != source && null != dao) {
                if (dao.getRowState() == DataRowState.NEW) {
                    while (source.hasNext()) {
                        M m = source.next();
                        if (null != m && ModelHelper.areSameRecord(m.dataObject(), dao)) {
                            return Optional.of(m);
                        }
                    }
                } else {
                    int pk = dao.getPrimaryKey();
                    while (source.hasNext()) {
                        M m = source.next();
                        if (null != m && m.getRowState() != DataRowState.NEW && m.getPrimaryKey() == pk) {
                            return Optional.of(m);
                        }
                    }
                }
            }
            return Optional.empty();
        }

        public final Optional<M> find(Iterator<M> source, M model) {
            if (null != source && null != model) {
                if (model.getRowState() == DataRowState.NEW) {
                    while (source.hasNext()) {
                        M m = source.next();
                        if (null != m && ModelHelper.areSameRecord(m.dataObject(), model)) {
                            return Optional.of(m);
                        }
                    }
                } else {
                    int pk = model.getPrimaryKey();
                    while (source.hasNext()) {
                        M m = source.next();
                        if (null != m && m.getRowState() != DataRowState.NEW && m.getPrimaryKey() == pk) {
                            return Optional.of(m);
                        }
                    }
                }
            }
            return Optional.empty();
        }

        public final Optional<M> find(Iterable<M> source, D dao) {
            if (null != source) {
                return find(source.iterator(), dao);
            }
            return Optional.empty();
        }

        public final Optional<M> find(Iterable<M> source, M model) {
            if (null != source && null != model) {
                return find(source.iterator(), model);
            }
            return Optional.empty();
        }

        public final Optional<M> find(Stream<M> source, D dao) {
            if (null != source) {
                return find(source.iterator(), dao);
            }
            return Optional.empty();
        }

        public abstract OperationRequestEvent<D, M> createEditRequestEvent(M model, Object source);

        public abstract OperationRequestEvent<D, M> createDeleteRequestEvent(M model, Object source);

        public abstract EventType<? extends OperationRequestEvent<D, M>> getBaseRequestEventType();

        public abstract EventType<? extends OperationRequestEvent<D, M>> getEditRequestEventType();

        public abstract EventType<? extends OperationRequestEvent<D, M>> getDeleteRequestEventType();

        public abstract DataAccessObject.SaveDaoTask<D, M, E> createSaveTask(M model);

        public abstract DataAccessObject.DeleteDaoTask<D, M, E> createDeleteTask(M model);

        @Override
        public final EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
            return tail.append(eventHandlerManager);
        }

        /**
         * Registers a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <T extends E> void addEventHandler(EventType<T> type, EventHandler<T> eventHandler) {
            eventHandlerManager.addEventHandler(type, eventHandler);
        }

        /**
         * Registers a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventFilter The event filter.
         */
        public final <T extends E> void addEventFilter(EventType<T> type, EventHandler<T> eventFilter) {
            eventHandlerManager.addEventFilter(type, eventFilter);
        }

        /**
         * Unregisters a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <T extends E> void removeEventHandler(EventType<T> type, EventHandler<T> eventHandler) {
            eventHandlerManager.removeEventHandler(type, eventHandler);
        }

        /**
         * Unregisters a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this
         * {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventFilter The event filter.
         */
        public final <T extends E> void removeEventFilter(EventType<T> type, EventHandler<T> eventFilter) {
            eventHandlerManager.removeEventFilter(type, eventFilter);
        }

        /**
         * Validates a {@link FxRecordModel} before an insert or update operation.
         *
         * @param target The {@link FxRecordModel} containing the {@link DataAccessObject} being inserted or updated in the database.
         * @return The {@link ModelEvent} representing the validation results, which may be {@code null} if there are no validation errors.
         */
        public abstract ModelEvent<D, M> validateForSave(M target);

    }

    @FunctionalInterface
    public interface PropertyValueExporter<T extends FxRecordModel<? extends DataAccessObject>> {

        Pair<String, String> toIdentity(T model, int index, Iterable<String> exportData);
    }

}
