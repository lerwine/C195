package scheduler.model.ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Pair;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.DataRecord;
import scheduler.model.ModelHelper;
import scheduler.observables.RowStateProperty;
import scheduler.util.DB;
import scheduler.view.ModelFilter;

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
public abstract class FxRecordModel<T extends DataAccessObject> implements FxDbModel<T>, DataRecord<LocalDateTime> {

    private final ReadOnlyObjectWrapper<T> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
    private final ReadOnlyStringWrapper createdBy;
    private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringWrapper lastModifiedBy;
    private final ReadOnlyBooleanWrapper newItem;
    private final RowStateProperty rowState;

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
        rowState = new RowStateProperty(rs);
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

    /**
     * Indicates whether this represents a new item that has not been saved to the database.
     *
     * @return {@code true} if the current item has not been saved to the database; otherwise, {@code false}.
     */
    public boolean isNewItem() {
        return newItem.get();
    }

    /**
     * Gets the property that indicates whether the current item has not yet been saved to the database.
     *
     * @return The property that indicates whether the current item has not yet been saved to the database.
     */
    public ReadOnlyBooleanProperty newItemProperty() {
        return newItem.getReadOnlyProperty();
    }

    public boolean modelEquals(T model) {
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
         * @param dao The source {@link DataAccessObject}.
         */
        public void updateItem(U item, T dao) {
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException(String.format("%s has been deleted", dao.getClass().getName()));
            }

            FxRecordModel<T> model = (FxRecordModel<T>) ModelHelper.requiredAssignable(dao, item);
            DataRowState rs = dao.getRowState();
            if (item.isNewItem()) {
                model.dataObject.set(dao);
                model.primaryKey.set(dao.getPrimaryKey());
                model.newItem.set(rs == DataRowState.NEW);
            } else {
                if (rs == DataRowState.NEW || dao.getPrimaryKey() != item.getPrimaryKey()) {
                    throw new IllegalArgumentException("Invalid data access object");
                }
                model.dataObject.set(dao);
            }

            model.rowState.set(rs);
            model.createDate.set(DB.toLocalDateTime(dao.getCreateDate()));
            model.createdBy.set(dao.getCreatedBy());
            model.lastModifiedDate.set(DB.toLocalDateTime(dao.getLastModifiedDate()));
            model.lastModifiedBy.set(dao.getLastModifiedBy());
        }

        public final void loadAsync(Stage stage, DaoFilter<T> filter, ObservableList<U> target, Consumer<ObservableList<U>> onSuccess,
                Consumer<Throwable> onFail) {
            DataAccessObject.DaoFactory<T> factory = getDaoFactory();
            factory.loadAsync(stage, filter, (t) -> {
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
                DataAccessObject.DaoFactory<T> daoFactory = getDaoFactory();
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
