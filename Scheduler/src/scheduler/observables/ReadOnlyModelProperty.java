/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.observables;

import java.util.function.Predicate;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.util.Values;

/**
 *
 * @author erwinel
 * @param <M>
 */
public class ReadOnlyModelProperty<M extends scheduler.view.ItemModel<?>> extends ReadOnlyObjectWrapper<M> {
    private final BooleanBinding newRow;

    public BooleanBinding isNewRow() { return newRow; }

    private final BooleanBinding deleted;

    public BooleanBinding isDeleted() { return deleted; }

    private final BooleanBinding modified;

    public BooleanBinding isModified() { return modified; }

    private final BooleanBinding saved;

    public BooleanBinding isSaved() { return saved; }
    
    public ReadOnlyModelProperty() {
        super();
        newRow = new PredicateBinding((M value) -> value.getDataObject().getRowState() == Values.ROWSTATE_NEW);
        deleted = new PredicateBinding((M value) -> value.getDataObject().getRowState() == Values.ROWSTATE_DELETED);
        modified = new PredicateBinding((M value) -> value.getDataObject().getRowState() == Values.ROWSTATE_MODIFIED);
        saved = new PredicateBinding((M value) -> value.getDataObject().getRowState() == Values.ROWSTATE_UNMODIFIED);
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
