package scheduler.observables;

import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.dao.DataRowState;

/**
 * A {@link SimpleIntegerProperty} that contains a {@link DataRowState}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RowStateProperty extends SimpleObjectProperty<DataRowState> {

    private final ReadOnlyPropertyImpl readOnlyProperty;
    private final CalculatedBooleanProperty<DataRowState> valid;
    private final CalculatedBooleanProperty<DataRowState> newRow;
    private final CalculatedBooleanProperty<DataRowState> change;
    private final CalculatedBooleanProperty<DataRowState> existingInDb;

    public RowStateProperty(Object bean, String name, DataRowState initialValue) {
        super(bean, name, initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
        valid = new CalculatedBooleanProperty<>(this, "valid", this, Objects::nonNull);
        newRow = new CalculatedBooleanProperty<>(this, "newRow", this, (t) -> null != t && t == DataRowState.NEW);
        change = new CalculatedBooleanProperty<>(this, "change", this, DataRowState::isChange);
        existingInDb = new CalculatedBooleanProperty<>(this, "existingInDb", this, DataRowState::existsInDb);
    }

    /**
     * Returns the readonly property, that is synchronized with this {@code RowStateProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyObjectProperty<DataRowState> getReadOnlyProperty() {
        return readOnlyProperty;
    }

    public DataRowState getSafe() {
        DataRowState status = get();
        return (null == status) ? DataRowState.NEW : status;
    }

    @Override
    public void set(DataRowState newValue) {
        super.set((null == newValue) ? DataRowState.NEW : newValue);
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    public boolean isNewRow() {
        return newRow.get();
    }

    public ReadOnlyBooleanProperty newRowProperty() {
        return newRow.getReadOnlyBooleanProperty();
    }

    public boolean isChange() {
        return change.get();
    }

    public ReadOnlyBooleanProperty changeProperty() {
        return change.getReadOnlyBooleanProperty();
    }

    public boolean isExistingInDb() {
        return existingInDb.get();
    }

    public ReadOnlyBooleanProperty existingInDbProperty() {
        return existingInDb.getReadOnlyBooleanProperty();
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<DataRowState> {

        @Override
        public DataRowState get() {
            return RowStateProperty.this.getSafe();
        }

        @Override
        public Object getBean() {
            return RowStateProperty.this.getBean();
        }

        @Override
        public String getName() {
            return RowStateProperty.this.getName();
        }

        private ReadOnlyPropertyImpl() {
            RowStateProperty.this.addListener((observable, oldValue, newValue) -> {
                ReadOnlyPropertyImpl.this.fireValueChangedEvent();
            });
        }

    }

}
