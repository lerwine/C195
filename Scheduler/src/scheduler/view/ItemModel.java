package scheduler.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
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
import scheduler.dao.DaoFilter;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.DataRowState;
import scheduler.util.DB;

/**
 * Java FX object model for a {@link DataObjectImpl} object.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link DataObjectImpl} to be used for data access operations.
 */
public abstract class ItemModel<T extends DataObjectImpl> {

    private final ReadOnlyObjectWrapper<T> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
    private final ReadOnlyStringWrapper createdBy;
    private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringWrapper lastModifiedBy;
    private final ReadOnlyBooleanWrapper newItem;

    public final T getDataObject() {
        return dataObject.get();
    }

    public final ReadOnlyObjectProperty<T> dataObjectProperty() {
        return dataObject;
    }

    public int getPrimaryKey() {
        return primaryKey.get();
    }

    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    public LocalDateTime getCreateDate() {
        return createDate.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return createDate.getReadOnlyProperty();
    }

    public String getCreatedBy() {
        return createdBy.get();
    }

    public ReadOnlyStringProperty createdByProperty() {
        return createdBy.getReadOnlyProperty();
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate.getReadOnlyProperty();
    }

    public String getLastModifiedBy() {
        return lastModifiedBy.get();
    }

    public ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy.getReadOnlyProperty();
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

    /**
     * Initializes a new ModelBase object.
     *
     * @param dao The {@link DataObjectImpl} to be used for data access operations.
     */
    protected ItemModel(T dao) {
        assert dao.getRowState() != DataRowState.DELETED : String.format("%s has been deleted", dao.getClass().getName());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", dao);
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", dao.getPrimaryKey());
        createDate = new ReadOnlyObjectWrapper<>(this, "createDate", DB.fromUtcTimestamp(dao.getCreateDate()));
        createdBy = new ReadOnlyStringWrapper(this, "createdBy", dao.getCreatedBy());
        lastModifiedDate = new ReadOnlyObjectWrapper<>(this, "lastModifiedDate", DB.fromUtcTimestamp(dao.getLastModifiedDate()));
        lastModifiedBy = new ReadOnlyStringWrapper(this, "lastModifiedBy", dao.getLastModifiedBy());
        newItem = new ReadOnlyBooleanWrapper(this, "newItem", dao.getRowState() == DataRowState.NEW);
    }

    public boolean modelEquals(T model) {
        return null != model && dataObject.get().equals(model);
    }
    
    public static abstract class ModelFactory<T extends DataObjectImpl, S extends ItemModel<T>> {
        
        public abstract DataObjectImpl.DaoFactory<T> getDaoFactory();

        public abstract S createNew(T dao);
        
        public abstract ModelFilter<T, S> getAllItemsFilter();
        
        public abstract ModelFilter<T, S> getDefaultFilter();
        
        /**
         * Applies changes made in the model to the data access object.
         * 
         * @param item The model item.
         * @return The data access object with changes applied.
         */
        public abstract T applyChanges(S item);
        
        protected void updateItem(S item, T dao) {
            assert dao.getRowState() != DataRowState.DELETED : String.format("%s has been deleted", dao.getClass().getName());
            ItemModel<T> model = (ItemModel<T>)item;
            if (item.isNewItem()) {
                model.dataObject.set(dao);
                model.primaryKey.set(dao.getPrimaryKey());
                model.newItem.set(dao.getRowState() == DataRowState.NEW);
            } else {
                assert dao.getRowState() != DataRowState.NEW && dao.getPrimaryKey() == item.getPrimaryKey() : "Invalid data access object";
                model.dataObject.set(dao);
            }
            model.createDate.set(DB.fromUtcTimestamp(dao.getCreateDate()));
            model.createdBy.set(dao.getCreatedBy());
            model.lastModifiedDate.set(DB.fromUtcTimestamp(dao.getLastModifiedDate()));
            model.lastModifiedBy.set(dao.getLastModifiedBy());
        }
        
        public final void loadAsync(Stage stage, DaoFilter<T> filter, ObservableList<S> target, Consumer<ObservableList<S>> onSuccess, Consumer<Throwable> onFail) {
            DataObjectImpl.DaoFactory<T> factory = getDaoFactory();
            factory.loadAsync(stage, filter, (t) -> {
                ArrayList<S> newItems = new ArrayList<>();
                t.forEach((u) -> {
                    Optional<S> existing = target.stream().filter((s) -> s.getDataObject().equals(u)).findFirst();
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
                while (target.size() < newItems.size())
                    target.add(newItems.get(target.size()));
                while (target.size() > newItems.size())
                    target.remove(newItems.size());
                if (null != onSuccess)
                    onSuccess.accept(target);
            }, onFail);
        }
    }
}
