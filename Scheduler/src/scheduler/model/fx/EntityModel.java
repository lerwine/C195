package scheduler.model.fx;

import com.sun.javafx.event.EventHandlerManager;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import scheduler.model.DataEntity;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.util.DateTimeUtil;
import scheduler.util.LogHelper;
import scheduler.view.ModelFilter;

/**
 * Base class for {@link EntityModel} objects that contain a backing {@link DataAccessObject}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} to be used for data access operations.
 */
public abstract class EntityModel<T extends DataAccessObject> implements PartialEntityModel<T>, DataEntity<LocalDateTime> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EntityModel.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(EntityModel.class.getName());

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

    private final T dataObject;
    private final ReadOnlyBooleanBindingProperty newRow;
    private final ReadOnlyBooleanBindingProperty existingInDb;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
    private final ReadOnlyStringWrapper createdBy;
    private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringWrapper lastModifiedBy;
    private final ReadOnlyObjectWrapper<DataRowState> rowState;

    /**
     * Initializes a new ModelBase object.
     *
     * @param dao The {@link DataAccessObject} to be used for data access operations.
     */
    protected EntityModel(T dao) {
        if (dao.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException(String.format("%s has been deleted", dao.getClass().getName()));
        }
        primaryKey = new ReadOnlyIntegerWrapper(this, PROP_PRIMARYKEY, dao.getPrimaryKey());
        createDate = new ReadOnlyObjectWrapper<>(this, PROP_CREATEDATE, DateTimeUtil.toLocalDateTime(dao.getCreateDate()));
        createdBy = new ReadOnlyStringWrapper(this, PROP_CREATEDBY, dao.getCreatedBy());
        lastModifiedDate = new ReadOnlyObjectWrapper<>(this, PROP_LASTMODIFIEDDATE, DateTimeUtil.toLocalDateTime(dao.getLastModifiedDate()));
        lastModifiedBy = new ReadOnlyStringWrapper(this, PROP_LASTMODIFIEDBY, dao.getLastModifiedBy());
        rowState = new ReadOnlyObjectWrapper<>(this, PROP_ROWSTATE, dao.getRowState());

        dataObject = dao;
        newRow = new ReadOnlyBooleanBindingProperty(this, PROP_NEWROW, () -> DataRowState.isNewRow(rowState.get()), rowState);
        existingInDb = new ReadOnlyBooleanBindingProperty(this, PROP_EXISTINGINDB, () -> DataRowState.existsInDb(rowState.get()), rowState);
    }

    @Override
    public final T dataObject() {
        return dataObject;
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    @Override
    public LocalDateTime getCreateDate() {
        return createDate.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return createDate.getReadOnlyProperty();
    }

    @Override
    public String getCreatedBy() {
        return createdBy.get();
    }

    public ReadOnlyStringProperty createdByProperty() {
        return createdBy.getReadOnlyProperty();
    }

    @Override
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate.getReadOnlyProperty();
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy.get();
    }

    public ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy.getReadOnlyProperty();
    }

    @Override
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

    public final boolean isNewRow() {
        return newRow.get();
    }

    public final ReadOnlyBooleanProperty newRowProperty() {
        return newRow;
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

    protected void onModelEvent(ModelEvent<T, ? extends EntityModel<T>> event) {
        LOG.entering(LOG.getName(), "onModelEvent", event);
        T dao = event.getDataAccessObject();
        rowState.set(dao.getRowState());
        lastModifiedDate.set(DateTimeUtil.toLocalDateTime(dao.getLastModifiedDate()));
        lastModifiedBy.set(dao.getLastModifiedBy());
        switch (event.getOperation()) {
            case DB_INSERT:
                primaryKey.set(dao.getPrimaryKey());
                createDate.set(DateTimeUtil.toLocalDateTime(dao.getCreateDate()));
                createdBy.set(dao.getCreatedBy());
                break;
            case DB_UPDATE:
                break;
            default:
                LOG.exiting(LOG.getName(), "onModelEvent");
                return;
        }
        onDaoChanged(event);
        LOG.exiting(LOG.getName(), "onModelEvent");
    }

    protected abstract void onDaoChanged(ModelEvent<T, ? extends EntityModel<T>> event);

    /**
     *
     * @param <D> The {@link DataAccessObject} type
     * @param <M> The {@link EntityModel} type.
     */
    public static abstract class EntityModelFactory<D extends DataAccessObject, M extends EntityModel<D>>
            implements EventTarget {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EntityModelFactory.class.getName()), Level.FINE);
//        private static final Logger LOG = Logger.getLogger(EntityModelFactory.class.getName());

        private final EventHandlerManager eventHandlerManager;

        protected EntityModelFactory() {
            eventHandlerManager = new EventHandlerManager(this);
        }

        public abstract DataAccessObject.DaoFactory<D, M> getDaoFactory();

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
                        if (null != m && ModelHelper.areSameRecord(m.dataObject(), model.dataObject())) {
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

        public abstract Class<? extends ModelEvent<D, M>> getModelResultEventClass();

        public abstract EventType<? extends ModelEvent<D, M>> getSuccessEventType();

        public abstract EventType<? extends OperationRequestEvent<D, M>> getBaseRequestEventType();

        public abstract EventType<? extends OperationRequestEvent<D, M>> getEditRequestEventType();

        public abstract EventType<? extends OperationRequestEvent<D, M>> getDeleteRequestEventType();

        public abstract OperationRequestEvent<D, M> createEditRequestEvent(M model, Object source);

        public abstract OperationRequestEvent<D, M> createDeleteRequestEvent(M model, Object source);

        public abstract DataAccessObject.SaveDaoTask<D, M> createSaveTask(M model);

        public abstract DataAccessObject.DeleteDaoTask<D, M> createDeleteTask(M model);

        @Override
        public final EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
            EventDispatchChain result = tail.append(eventHandlerManager);
            LOG.exiting(LOG.getName(), "buildEventDispatchChain");
            return result;
        }

        /**
         * Registers a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <T extends ModelEvent<D, M>> void addEventHandler(EventType<T> type, EventHandler<? super T> eventHandler) {
            eventHandlerManager.addEventHandler(type, eventHandler);
        }

        /**
         * Registers a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventFilter The event filter.
         */
        public final <T extends ModelEvent<D, M>> void addEventFilter(EventType<T> type, EventHandler<? super T> eventFilter) {
            eventHandlerManager.addEventFilter(type, eventFilter);
        }

        /**
         * Unregisters a {@link ModelEvent} handler in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventHandler The event handler.
         */
        public final <T extends ModelEvent<D, M>> void removeEventHandler(EventType<T> type, EventHandler<? super T> eventHandler) {
            eventHandlerManager.removeEventHandler(type, eventHandler);
        }

        /**
         * Unregisters a {@link ModelEvent} filter in the {@code EventHandlerManager} for {@link DataAccessObject} types supported by this {@code DaoFactory}.
         *
         * @param <T> The {@link ModelEvent} type.
         * @param type The event type.
         * @param eventFilter The event filter.
         */
        public final <T extends ModelEvent<D, M>> void removeEventFilter(EventType<T> type, EventHandler<? super T> eventFilter) {
            eventHandlerManager.removeEventFilter(type, eventFilter);
        }

        /**
         * Validates a {@link EntityModel} before an insert or update operation.
         *
         * @param target The {@link EntityModel} containing the {@link DataAccessObject} being inserted or updated in the database.
         * @return The {@link ModelEvent} representing the validation results, which may be {@code null} if there are no validation errors.
         */
        public abstract ModelEvent<D, M> validateForSave(M target);

    }

    @FunctionalInterface
    public interface PropertyValueExporter<T extends EntityModel<? extends DataAccessObject>> {

        Pair<String, String> toIdentity(T model, int index, Iterable<String> exportData);
    }

}
