package scheduler.model.ui;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.ModelHelper;
import scheduler.observables.DataAccessObjectProperty;
import scheduler.view.ModelFilter;
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

    private final DataAccessObjectProperty<T> dataObject;
//    private final ReadOnlyBooleanWrapper valid;

    /**
     * Initializes a new ModelBase object.
     *
     * @param dao The {@link DataAccessObject} to be used for data access operations.
     */
    protected FxRecordModel(T dao) {
        if (dao.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException(String.format("%s has been deleted", dao.getClass().getName()));
        }
        dataObject = new DataAccessObjectProperty<>(this, "dataObject", dao);
    }

    @Override
    public final T getDataObject() {
        return dataObject.get();
    }

    @Override
    public final ReadOnlyObjectProperty<T> dataObjectProperty() {
        return dataObject;
    }

    @Override
    public final int getPrimaryKey() {
        return dataObject.getPrimaryKey();
    }

    @Override
    public final ReadOnlyIntegerProperty primaryKeyProperty() {
        return dataObject.primaryKeyProperty();
    }

    @Override
    public final LocalDateTime getCreateDate() {
        return dataObject.getCreateDate();
    }

    @Override
    public final ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return dataObject.createDateProperty();
    }

    @Override
    public final String getCreatedBy() {
        return dataObject.getCreatedBy();
    }

    @Override
    public final ReadOnlyStringProperty createdByProperty() {
        return dataObject.createdByProperty();
    }

    @Override
    public final LocalDateTime getLastModifiedDate() {
        return dataObject.getLastModifiedDate();
    }

    @Override
    public final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return dataObject.lastModifiedDateProperty();
    }

    @Override
    public final String getLastModifiedBy() {
        return dataObject.getLastModifiedBy();
    }

    @Override
    public final ReadOnlyStringProperty lastModifiedByProperty() {
        return dataObject.lastModifiedByProperty();
    }

    @Override
    public final DataRowState getRowState() {
        return dataObject.getRowState();
    }

    @Override
    public final ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return dataObject.rowStateProperty();
    }

    /**
     * Indicates whether this represents a new item that has not been saved to the database.
     *
     * @return {@code true} if the current item has not been saved to the database; otherwise, {@code false}.
     */
    public final boolean isNewRow() {
        return dataObject.isNewRow();
    }

    /**
     * Gets the property that indicates whether the current item has not yet been saved to the database.
     *
     * @return The property that indicates whether the current item has not yet been saved to the database.
     */
    public final ReadOnlyBooleanProperty newRowProperty() {
        return dataObject.newRowProperty();
    }

    public final boolean isChange() {
        return dataObject.isChange();
    }

    public final ReadOnlyBooleanProperty changeProperty() {
        return dataObject.changeProperty();
    }

    public final boolean isExistingInDb() {
        return dataObject.isExistingInDb();
    }

    public final ReadOnlyBooleanProperty existingInDbProperty() {
        return dataObject.existingInDbProperty();
    }

    public final boolean modelEquals(T model) {
        return null != model && dataObject.get().equals(model);
    }

    protected final synchronized ReadOnlyIntegerProperty createReadOnlyDaoIntegerProperty(String name, ToIntFunction<T> getter) {
        return dataObject.createReadOnlyIntegerProperty(name, getter);
    }

    protected final synchronized ReadOnlyBooleanProperty createReadOnlyDaoBooleanProperty(String name, Predicate<T> getter) {
        return dataObject.createReadOnlyBooleanProperty(name, getter);
    }

    protected final synchronized ReadOnlyStringProperty createReadOnlyDaoStringProperty(String name, Function<T, String> getter) {
        return dataObject.createReadOnlyStringProperty(name, getter);
    }

    protected final synchronized ReadOnlyObjectProperty<LocalDateTime> createReadOnlyDaoDateTimeProperty(String name, Function<T, Timestamp> getter) {
        return dataObject.createReadOnlyDateTimeProperty(name, getter);
    }

    protected final synchronized <U> ReadOnlyObjectProperty<U> createReadOnlyDaoObjectProperty(String name, Function<T, U> getter) {
        return dataObject.createReadOnlyObjectProperty(name, getter);
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
            T currentDao = item.getDataObject();
            if (Objects.requireNonNull(updatedDao) == currentDao) {
                return;
            }
            if (updatedDao.getRowState() == DataRowState.NEW) {
                throw new IllegalArgumentException("Replacement data access object cannot have a new row state");
            } else if (currentDao.getRowState() != DataRowState.NEW && currentDao.getPrimaryKey() != updatedDao.getPrimaryKey()) {
                throw new IllegalArgumentException("Replacement data access object does not represent the same record as the current data object.");
            }
            ((FxRecordModel<T>) item).dataObject.set(updatedDao);
            updateItemProperties(item, updatedDao);
        }

        public final void loadAsync(WaitBorderPane waitBorderPane, DaoFilter<T> filter, ObservableList<U> target, Consumer<ObservableList<U>> onSuccess,
                Consumer<Throwable> onFail) {
            DataAccessObject.DaoFactory<T> factory = getDaoFactory();
            factory.loadAsync(waitBorderPane, filter, (t) -> {
                ArrayList<U> newItems = new ArrayList<>();
                t.forEach((u) -> {
                    Optional<U> existing = target.stream().filter((s) -> s.getDataObject().equals(u)).findFirst();
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
                    Optional<U> existing = target.stream().filter((s) -> s.getDataObject().equals(u)).findFirst();
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
                    if (null != m && ModelHelper.areSameRecord(m.getDataObject(), dao)) {
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

    }

    @FunctionalInterface
    public interface PropertyValueExporter<T extends FxRecordModel<? extends DataAccessObject>> {

        Pair<String, String> toIdentity(T model, int index, Iterable<String> exportData);
    }

}
