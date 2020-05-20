package scheduler.model.predefined;

import com.sun.javafx.binding.ExpressionHelper;
import java.beans.PropertyChangeEvent;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.DbRecord;
import scheduler.model.ui.FxDbModel;
import scheduler.util.DB;
import scheduler.util.PropertyBindable;

/**
 * Base class for pre-defined items that can also be referenced in the database.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class PredefinedItem<T extends DbRecord> implements FxDbModel<T>, IPredefinedItem {

    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyObjectWrapper<DataRowState> rowState;
    private final ReadOnlyBooleanWrapper existing = new ReadOnlyBooleanWrapper();
    private final ReadOnlyBooleanWrapper valid;

    public PredefinedItem() {
        valid = new ReadOnlyBooleanWrapper(true);
        primaryKey = new ReadOnlyIntegerWrapper(Integer.MIN_VALUE);
        rowState = new ReadOnlyObjectWrapper<>(DataRowState.NEW);
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
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty existingProperty() {
        return existing.getReadOnlyProperty();
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    private void onDaoPropertyChange(PropertyChangeEvent evt) {
        DbRecord dao = (DbRecord) evt.getSource();
        String propertyName = evt.getPropertyName();
        switch (propertyName) {
            case DataAccessObject.PROP_PRIMARYKEY:
                primaryKey.set(dao.getPrimaryKey());
                break;
            case DataAccessObject.PROP_ROWSTATE:
                DataRowState rs = dao.getRowState();
                rowState.set(rs);
                break;
            default:
                onDaoPropertyChanged(dao, propertyName);
                break;
        }
    }

    protected abstract void onDaoPropertyChanged(DbRecord dao, String propertyName);

    protected abstract void onDataObjectChanged(DbRecord dao);

    protected <U extends DbRecord> void dataObjectChanged(ObservableValue<? extends U> observable, U oldValue, U newValue) {
        if (!(oldValue instanceof PredefinedItem)) {
            oldValue.removePropertyChangeListener(this::onDaoPropertyChange);
        }
        if (!(newValue instanceof PredefinedItem)) {
            newValue.addPropertyChangeListener(this::onDaoPropertyChange);
        }
        primaryKey.set(newValue.getPrimaryKey());
        rowState.set(newValue.getRowState());
        onDataObjectChanged(newValue);
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
