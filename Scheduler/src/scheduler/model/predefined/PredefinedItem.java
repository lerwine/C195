package scheduler.model.predefined;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import scheduler.dao.DAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.IDataAccessObject;
import scheduler.dao.ValidationResult;
import scheduler.model.DataRecord;
import scheduler.model.ui.FxDbModel;
import scheduler.model.ui.FxModel;
import scheduler.util.DB;
import scheduler.util.PropertyBindable;

/**
 * Base class for pre-defined items that can also be referenced in the database.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class PredefinedItem implements FxModel, IPredefinedItem {

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

    protected class BasePlaceHolderDAO extends PropertyBindable implements IDataAccessObject {

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
