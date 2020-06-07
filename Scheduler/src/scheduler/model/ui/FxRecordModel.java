package scheduler.model.ui;

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
import javafx.util.Pair;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.util.DB;
import scheduler.view.ModelFilter;
import scheduler.view.event.ItemMutateEvent;
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
public abstract class FxRecordModel<T extends DataAccessObject> implements IFxRecordModel<T> {

    private static final Logger LOG = Logger.getLogger(FxRecordModel.class.getName());

    private final T dataObject;
    private ReadOnlyJavaBeanIntegerProperty primaryKey;
    private ReadOnlyJavaBeanObjectProperty<DataRowState> rowState;
    private ReadOnlyJavaBeanObjectProperty<Timestamp> rawCreateDate;
    private ReadOnlyJavaBeanStringProperty createdBy;
    private ReadOnlyJavaBeanObjectProperty<Timestamp> rawLastModifiedDate;
    private ReadOnlyJavaBeanStringProperty lastModifiedBy;
    private final ReadOnlyObjectProperty<LocalDateTime> createDate;
    private final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDate;
    private final ReadOnlyBooleanProperty newRow;
    private final ReadOnlyBooleanProperty change;
    private final ReadOnlyBooleanProperty existingInDb;

    /**
     * Initializes a new ModelBase object.
     *
     * @param dao The {@link DataAccessObject} to be used for data access operations.
     */
    protected FxRecordModel(T dao) {
        if (dao.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException(String.format("%s has been deleted", dao.getClass().getName()));
        }
        dataObject = dao;
        try {
            primaryKey = ReadOnlyJavaBeanIntegerPropertyBuilder.create().bean(dao).name(DataAccessObject.PROP_PRIMARYKEY).build();
            rowState = ReadOnlyJavaBeanObjectPropertyBuilder.<DataRowState>create().bean(dao).name(DataAccessObject.PROP_ROWSTATE).build();
            rawCreateDate = ReadOnlyJavaBeanObjectPropertyBuilder.<Timestamp>create().bean(dao).name(CountryDAO.PROP_CREATEDATE).build();
            createdBy = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(dao).name(DataAccessObject.PROP_CREATEDBY).build();
            rawLastModifiedDate = ReadOnlyJavaBeanObjectPropertyBuilder.<Timestamp>create().bean(dao).name(CountryDAO.PROP_LASTMODIFIEDDATE).build();
            lastModifiedBy = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(dao).name(DataAccessObject.PROP_LASTMODIFIEDBY).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        createDate = new ReadOnlyObjectBindingProperty<>(this, "createDate",
                () -> DB.toLocalDateTime(rawCreateDate.get()), rawCreateDate);
        lastModifiedDate = new ReadOnlyObjectBindingProperty<>(this, "lastModifiedDate",
                () -> DB.toLocalDateTime(rawLastModifiedDate.get()), rawLastModifiedDate);
        newRow = new ReadOnlyBooleanBindingProperty(this, "newRow", () -> DataRowState.isNewRow(rowState.get()), rowState);
        change = new ReadOnlyBooleanBindingProperty(this, "change", () -> DataRowState.isChange(rowState.get()), rowState);
        existingInDb = new ReadOnlyBooleanBindingProperty(this, "existingInDb",() ->  DataRowState.existsInDb(rowState.get()), rowState);
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
        return primaryKey;
    }

    @Override
    public LocalDateTime getCreateDate() {
        return createDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
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
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
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
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState;
    }

    public boolean isNewRow() {
        return newRow.get();
    }

    public ReadOnlyBooleanProperty newRowProperty() {
        return newRow;
    }

    public boolean isChange() {
        return change.get();
    }

    public ReadOnlyBooleanProperty changeProperty() {
        return change;
    }

    public boolean isExistingInDb() {
        return existingInDb.get();
    }

    public ReadOnlyBooleanProperty existingInDbProperty() {
        return existingInDb;
    }

    public final boolean modelEquals(T model) {
        return null != model && dataObject.equals(model);
    }

    public static abstract class ModelFactory<T extends DataAccessObject, U extends FxRecordModel<T>> {

        public abstract DataAccessObject.DaoFactory<T> getDaoFactory();

        public abstract U createNew(T dao);

        /**
         * Applies changes made in the {@link FxRecordModel} to the underlying {@link DataAccessObject}.
         *
         * @param item The model item.
         * @return The {@link DataAccessObject} with changes applied.
         */
        public abstract T updateDAO(U item);

        protected abstract void updateItemProperties(U item, T dao);

        public abstract ModelFilter<T, U, ? extends DaoFilter<T>> getAllItemsFilter();

        public abstract ModelFilter<T, U, ? extends DaoFilter<T>> getDefaultFilter();

        /**
         * Updates the {@link FxRecordModel} with changes from a {@link DataAccessObject}.
         *
         * @param item The {@link FxRecordModel} to be updated.
         * @param updatedDao The source {@link DataAccessObject}.
         */
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

        public abstract ItemMutateEvent<U> createInsertEvent(U source, Event fxEvent);
        
        public abstract ItemMutateEvent<U> createUpdateEvent(U source, Event fxEvent);
        
        public abstract ItemMutateEvent<U> createDeleteEvent(U source, Event fxEvent);
        
    }

    @FunctionalInterface
    public interface PropertyValueExporter<T extends FxRecordModel<? extends DataAccessObject>> {

        Pair<String, String> toIdentity(T model, int index, Iterable<String> exportData);
    }

}
