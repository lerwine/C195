/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import java.util.function.Predicate;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.db.DataRow;

/**
 *
 * @author Leonard T. Erwine
 * @param <T>
 */
public class ReadOnlyDataRowProperty<T extends DataRow> extends ReadOnlyObjectWrapper<T> {
    private final BooleanBinding newRow;

    public BooleanBinding isNewRow() { return newRow; }

    private final BooleanBinding deleted;

    public BooleanBinding isDeleted() { return deleted; }

    private final BooleanBinding modified;

    public BooleanBinding isModified() { return modified; }

    private final BooleanBinding saved;

    public BooleanBinding isSaved() { return saved; }

    public ReadOnlyDataRowProperty() {
        super();
        newRow = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
        saved = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_UNMODIFIED);
    }

    public ReadOnlyDataRowProperty(T initialValue) {
        super(initialValue);
        newRow = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
        saved = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_UNMODIFIED);
    }

    public ReadOnlyDataRowProperty(Object bean, String name) {
        super(bean, name);
        newRow = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
        saved = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_UNMODIFIED);
    }

    public ReadOnlyDataRowProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
        newRow = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
        saved = new PredicateBinding((T value) -> value.getRowState() == DataRow.ROWSTATE_UNMODIFIED);
    }
    
    protected class PredicateBinding extends BooleanBinding {
        private final Predicate<T> predicate;
        PredicateBinding(Predicate<T> predicate) {
            super.bind(ReadOnlyDataRowProperty.this);
            this.predicate = predicate;
        }
        @Override
        protected boolean computeValue() {
            T value = ReadOnlyDataRowProperty.this.get();
            return value != null && predicate.test(value);
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(ReadOnlyDataRowProperty.this);
        }

        @Override
        public void dispose() {
            super.unbind(ReadOnlyDataRowProperty.this);
            super.dispose();
        }
    }
}
