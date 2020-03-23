package scheduler.observables;

import java.util.function.Predicate;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.DataRowState;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <M>
 */
public class ReadOnlyModelProperty<M extends scheduler.view.ItemModel<?>> extends ReadOnlyObjectWrapper<M> {

    private final BooleanBinding newRow;
    private final BooleanBinding deleted;
    private final BooleanBinding modified;
    private final BooleanBinding saved;

    public ReadOnlyModelProperty() {
        super();
        newRow = new PredicateBinding((M value) -> value.getDataObject().getRowState() == DataRowState.NEW);
        deleted = new PredicateBinding((M value) -> value.getDataObject().getRowState() == DataRowState.DELETED);
        modified = new PredicateBinding((M value) -> value.getDataObject().getRowState() == DataRowState.MODIFIED);
        saved = new PredicateBinding((M value) -> value.getDataObject().getRowState() == DataRowState.UNMODIFIED);
    }

    public BooleanBinding isNewRow() {
        return newRow;
    }

    public BooleanBinding isDeleted() {
        return deleted;
    }

    public BooleanBinding isModified() {
        return modified;
    }

    public BooleanBinding isSaved() {
        return saved;
    }

    protected class PredicateBinding extends BooleanBinding {

        private final Predicate<M> predicate;

        PredicateBinding(Predicate<M> predicate) {
            super.bind(ReadOnlyModelProperty.this);
            this.predicate = predicate;
        }

        @Override
        protected boolean computeValue() {
            M value = ReadOnlyModelProperty.this.get();
            return value != null && predicate.test(value);
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(ReadOnlyModelProperty.this);
        }

        @Override
        public void dispose() {
            super.unbind(ReadOnlyModelProperty.this);
            super.dispose();
        }
    }
}
