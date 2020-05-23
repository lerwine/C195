package scheduler.model.predefined;

import com.sun.javafx.binding.ExpressionHelper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import scheduler.dao.DataRowState;
import scheduler.dao.DbRecord;
import scheduler.model.ui.FxDbModel;
import scheduler.observables.DataAccessObjectProperty;
import scheduler.util.DB;
import scheduler.util.PropertyBindable;

/**
 * Base class for pre-defined items that can also be referenced in the database.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public abstract class PredefinedItem<T extends DbRecord> implements FxDbModel<T>, IPredefinedItem {

    private final DataAccessObjectProperty<T> dataObject;
    private final ReadOnlyBooleanWrapper valid;

    protected PredefinedItem() {
        valid = new ReadOnlyBooleanWrapper(true);
        dataObject = new DataAccessObjectProperty<>(this, "dataObject", null);
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
    public final DataRowState getRowState() {
        return dataObject.getRowState();
    }

    @Override
    public final ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return dataObject.rowStateProperty();
    }

    @Override
    public final boolean isValid() {
        return valid.get();
    }

    @Override
    public final ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public final T getDataObject() {
        return dataObject.get();
    }
    
    protected final void setDataObject(T dao) {
        dataObject.set(dao);
    }

    @Override
    public final ReadOnlyObjectProperty<? extends T> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
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
    
    protected static class PredefinedDataProperty<T extends PredefinedItem<? extends DbRecord>> extends ReadOnlyObjectProperty<T> {

        private ExpressionHelper<T> helper = null;
        private final T value;

        PredefinedDataProperty(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void addListener(ChangeListener<? super T> listener) {
            helper = ExpressionHelper.addListener(helper, this, listener);
        }

        @Override
        public void removeListener(ChangeListener<? super T> listener) {
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
            return value;
        }

        @Override
        public String getName() {
            return "predefinedData";
        }
    }

    protected class BasePlaceHolderDAO extends PropertyBindable implements DbRecord {

        private final Timestamp TS = DB.toUtcTimestamp(LocalDateTime.now());

        @Override
        public int getPrimaryKey() {
            return Integer.MIN_VALUE;
        }

        @Override
        public Timestamp getCreateDate() {
            return TS;
        }

        @Override
        public String getCreatedBy() {
            return "";
        }

        @Override
        public Timestamp getLastModifiedDate() {
            return TS;
        }

        @Override
        public String getLastModifiedBy() {
            return "";
        }

        @Override
        public DataRowState getRowState() {
            return DataRowState.NEW;
        }

        @Override
        public boolean isExisting() {
            return false;
        }

    }

}
