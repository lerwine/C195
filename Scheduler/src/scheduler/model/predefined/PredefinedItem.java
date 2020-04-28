package scheduler.model.predefined;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import scheduler.dao.DataRowState;
import scheduler.model.RelatedRecord;
import scheduler.model.ui.FxModel;

/**
 * Base class for pre-defined items that can also be referenced in the database.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class PredefinedItem implements FxModel, RelatedRecord {

    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyObjectWrapper<DataRowState> rowState;
    private final ReadOnlyBooleanWrapper existing = new ReadOnlyBooleanWrapper();

    public PredefinedItem() {
        this.primaryKey = new ReadOnlyIntegerWrapper(Integer.MIN_VALUE);
        this.rowState = new ReadOnlyObjectWrapper<>(DataRowState.NEW);
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty existingProperty() {
        return existing.getReadOnlyProperty();
    }

}
