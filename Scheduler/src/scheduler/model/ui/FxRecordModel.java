package scheduler.model.ui;

import java.beans.PropertyChangeEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.DataRecord;
import scheduler.model.ModelHelper;
import scheduler.observables.RowStateProperty;
import scheduler.util.DB;
import scheduler.view.ModelFilter;
import scheduler.view.task.WaitBorderPane;

/**
 * Java FX object model for a {@link DataAccessObject} object.
 * <dl>
 * <dt>{@link ItemModel.ModelFactory}</dt><dd>Base factory class for {@link FxDbModel} objects.</dd>
 * <dt>{@link scheduler.dao.DataAccessObject}</dt><dd>Base class for corresponding data access objects.</dd>
 * </dl>
 * Entity-specific extensions:
 * <ul>
 * <li>{@link scheduler.view.appointment.AppointmentModel}</li>
 * <li>{@link scheduler.view.customer.CustomerModelImpl}</li>
 * <li>{@link scheduler.view.address.AddressModelImpl}</li>
 * <li>{@link scheduler.view.city.CityModelImpl}</li>
 * <li>{@link scheduler.view.country.CityCountryModelImpl}</li>
 * </ul>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} to be used for data access operations.
 */
public abstract class FxRecordModel<T extends DataAccessObject> implements IFxRecordModel<T>, DataRecord<LocalDateTime> {

    private final ReadOnlyObjectWrapper<T> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
    private final ReadOnlyStringWrapper createdBy;
    private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringWrapper lastModifiedBy;
    private final ReadOnlyBooleanWrapper newItem;
    private final RowStateProperty rowState;
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
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", dao);
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", dao.getPrimaryKey());
        createDate = new ReadOnlyObjectWrapper<>(this, "createDate", DB.toLocalDateTime(dao.getCreateDate()));
        createdBy = new ReadOnlyStringWrapper(this, "createdBy", dao.getCreatedBy());
        lastModifiedDate = new ReadOnlyObjectWrapper<>(this, "lastModifiedDate", DB.toLocalDateTime(dao.getLastModifiedDate()));
        lastModifiedBy = new ReadOnlyStringWrapper(this, "lastModifiedBy", dao.getLastModifiedBy());
        DataRowState rs = dao.getRowState();
        newItem = new ReadOnlyBooleanWrapper(this, "newItem", rs == DataRowState.NEW);
        rowState = new RowStateProperty(this, "rowState", rs);
//        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        dao.addPropertyChangeListener(this::onDaoPropertyChange);
        dataObject.addListener(this::dataObjectChanged);

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
        return primaryKey.get();
    }

    @Override
    public final ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    @Override
    public final LocalDateTime getCreateDate() {
        return createDate.get();
    }

    @Override
    public final ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return createDate.getReadOnlyProperty();
    }

    @Override
    public final String getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public final ReadOnlyStringProperty createdByProperty() {
        return createdBy.getReadOnlyProperty();
    }

    @Override
    public final LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    @Override
    public final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate.getReadOnlyProperty();
    }

    @Override
    public final String getLastModifiedBy() {
        return lastModifiedBy.get();
    }

    @Override
    public final ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy.getReadOnlyProperty();
    }

    @Override
    public final DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public final ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

    /**
     * Indicates whether this represents a new item that has not been saved to the database.
     *
     * @return {@code true} if the current item has not been saved to the database; otherwise, {@code false}.
     */
    public final boolean isNewItem() {
        return newItem.get();
    }

    /**
     * Gets the property that indicates whether the current item has not yet been saved to the database.
     *
     * @return The property that indicates whether the current item has not yet been saved to the database.
     */
    public final ReadOnlyBooleanProperty newItemProperty() {
        return newItem.getReadOnlyProperty();
    }

    private void onDaoPropertyChange(PropertyChangeEvent evt) {
        T dao = dataObject.get();
        String propertyName = evt.getPropertyName();
        switch (propertyName) {
            case DataAccessObject.PROP_CREATEDATE:
                createDate.set(DB.toLocalDateTime(dao.getCreateDate()));
                break;
            case DataAccessObject.PROP_CREATEDBY:
                createdBy.set(dao.getCreatedBy());
                break;
            case DataAccessObject.PROP_LASTMODIFIEDBY:
                lastModifiedBy.set(dao.getLastModifiedBy());
                break;
            case DataAccessObject.PROP_LASTMODIFIEDDATE:
                lastModifiedDate.set(DB.toLocalDateTime(dao.getLastModifiedDate()));
                break;
            case DataAccessObject.PROP_PRIMARYKEY:
                primaryKey.set(dao.getPrimaryKey());
                break;
            case DataAccessObject.PROP_ROWSTATE:
                DataRowState rs = dao.getRowState();
                rowState.set(rs);
                if (newItem.get()) {
                    if (rs != DataRowState.NEW) {
                        newItem.set(false);
                    }
                } else if (rs == DataRowState.NEW) {
                    newItem.set(true);
                }
                break;
            default:
                onDaoPropertyChanged(dao, propertyName);
                break;
        }
    }

    private void dataObjectChanged(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        oldValue.removePropertyChangeListener(this::onDaoPropertyChange);
        newValue.addPropertyChangeListener(this::onDaoPropertyChange);
        if (newItem.get()) {
            primaryKey.set(newValue.getPrimaryKey());
            createDate.set(DB.toLocalDateTime(newValue.getCreateDate()));
            createdBy.set(newValue.getCreatedBy());
            lastModifiedDate.set(DB.toLocalDateTime(newValue.getLastModifiedDate()));
            lastModifiedBy.set(newValue.getLastModifiedBy());
            newItem.set(false);
            rowState.set(newValue.getRowState());
        } else {
            LocalDateTime d = DB.toLocalDateTime(newValue.getCreateDate());
            if (!d.equals(createDate.get())) {
                createDate.set(d);
            }
            String s = newValue.getCreatedBy();
            if (!s.equals(createdBy.get())) {
                createdBy.set(s);
            }
            d = DB.toLocalDateTime(newValue.getLastModifiedDate());
            if (!d.equals(lastModifiedDate.get())) {
                lastModifiedDate.set(d);
            }
            s = newValue.getLastModifiedBy();
            if (!s.equals(lastModifiedBy.get())) {
                lastModifiedBy.set(s);
            }
        }
        onDataObjectChanged(newValue);
    }

    protected abstract void onDaoPropertyChanged(T dao, String propertyName);

    protected abstract void onDataObjectChanged(T dao);

    public final boolean modelEquals(T model) {
        return null != model && dataObject.get().equals(model);
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
