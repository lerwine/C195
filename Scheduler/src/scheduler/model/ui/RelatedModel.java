package scheduler.model.ui;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.DataRowState;
import scheduler.dao.DbObject;
import scheduler.observables.DataObjectProperty;
import scheduler.observables.DerivedBooleanProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public abstract class RelatedModel<T extends DbObject> implements FxDbModel<T> {

    private final DataObjectProperty<T> dataObject;
    private final DerivedBooleanProperty<DataRowState> valid;

    protected RelatedModel(T dao) {
        dataObject = new DataObjectProperty<>(this, "dataObject", dao);
        valid = new DerivedBooleanProperty<>(this, "valid", dataObject.rowStateProperty(), (t) -> null != t && t != DataRowState.DELETED);
    }

    @Override
    public T getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<T> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public int getPrimaryKey() {
        return dataObject.getPrimaryKey();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return dataObject.primaryKeyProperty();
    }

    @Override
    public DataRowState getRowState() {
        return dataObject.getRowState();
    }

    @Override
    public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return dataObject.rowStateProperty();
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    protected final ReadOnlyIntegerProperty createReadOnlyDaoIntegerProperty(String name, ToIntFunction<T> getter) {
        return dataObject.createReadOnlyIntegerProperty(name, getter);
    }

    protected final ReadOnlyBooleanProperty createReadOnlyDaoBooleanProperty(String name, Predicate<T> getter) {
        return dataObject.createReadOnlyBooleanProperty(name, getter);
    }

    protected final ReadOnlyStringProperty createReadOnlyDaoStringProperty(String name, Function<T, String> getter) {
        return dataObject.createReadOnlyStringProperty(name, getter);
    }

    protected final ReadOnlyObjectProperty<LocalDateTime> createReadOnlyDaoDateTimeProperty(String name, Function<T, Timestamp> getter) {
        return dataObject.createReadOnlyDateTimeProperty(name, getter);
    }

    protected final <U> ReadOnlyObjectProperty<U> createReadOnlyDaoObjectProperty(String name, Function<T, U> getter) {
        return dataObject.createReadOnlyObjectProperty(name, getter);
    }

    protected final <U extends DbObject, S extends FxDbModel<? extends U>> ReadOnlyObjectProperty<S> createReadOnlyNestedDaoModelProperty(String name,
            ReadOnlyObjectProperty<U> daoProperty, Function<U, S> factory) {
        return dataObject.createReadOnlyNestedModelProperty(name, daoProperty, factory);
    }

    protected final <U extends DbObject, S extends FxDbModel<? extends U>> ReadOnlyObjectProperty<S> createReadOnlyNestedDaoModelProperty(String name,
            Function<T, U> getter, Function<U, S> factory) {
        return dataObject.createReadOnlyNestedModelProperty(name, getter, factory);
    }

}
