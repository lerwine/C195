package scheduler.observables;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.dao.DataRowState;
import scheduler.util.Values;

/**
 * A {@link SimpleIntegerProperty} that represents a valid data rows state value. The value will be {@link Values#ROWSTATE_NEW}, {@link Values#ROWSTATE_UNMODIFIED},
 * {@link Values#ROWSTATE_MODIFIED} or {@link Values#ROWSTATE_DELETED}.
 *
 * @author erwinel
 */
public class RowStateProperty extends SimpleObjectProperty<DataRowState> {

    private final ReadOnlyObjectProperty<DataRowState> readOnlyProperty;

    /**
     * Returns the readonly property, that is synchronized with this {@code RowStateProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyObjectProperty<DataRowState> getReadOnlyProperty() {
        return readOnlyProperty;
    }

    public RowStateProperty() {
        this(DataRowState.NEW);
    }

    public RowStateProperty(DataRowState initialValue) {
        super((null == initialValue) ? DataRowState.NEW : initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
    }

    public RowStateProperty(Object bean, String name) {
        this(bean, name, DataRowState.NEW);
    }

    public RowStateProperty(Object bean, String name, DataRowState initialValue) {
        super(bean, name, (null == initialValue) ? DataRowState.NEW : initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
    }

    @Override
    public void set(DataRowState newValue) {
        super.set((null == newValue) ? DataRowState.NEW : newValue);
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<DataRowState> {

        @Override
        public DataRowState get() {
            return RowStateProperty.this.get();
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
                super.fireValueChangedEvent();
            });
        }
    };
}
