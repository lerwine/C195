/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import java.util.function.Predicate;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Record;
import model.db.DataRow;

/**
 *
 * @author Leonard T. Erwine
 */
@Deprecated
public class RecordProperty<R extends Record> extends SimpleObjectProperty<R> {

    private final BooleanBinding dataRow;

    public BooleanBinding isDataRow() { return dataRow; }

    private final BooleanBinding newRow;

    public BooleanBinding isNewRow() { return newRow; }

    private final BooleanBinding deleted;

    public BooleanBinding isDeleted() { return deleted; }

    private final BooleanBinding modified;

    public BooleanBinding isModified() { return modified; }

    public RecordProperty() {
        super();
        dataRow = new PredicateBinding((R value) -> value instanceof DataRow);
        newRow = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
    }

    public RecordProperty(R initialValue) {
        super(initialValue);
        dataRow = new PredicateBinding((R value) -> value instanceof DataRow);
        newRow = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
    }

    public RecordProperty(Object bean, String name) {
        super(bean, name);
        dataRow = new PredicateBinding((R value) -> value instanceof DataRow);
        newRow = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
    }

    public RecordProperty(Object bean, String name, R initialValue) {
        super(bean, name, initialValue);
        dataRow = new PredicateBinding((R value) -> value instanceof DataRow);
        newRow = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_NEW);
        deleted = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_DELETED);
        modified = new DataRowPredicateBinding((DataRow value) -> value.getRowState() == DataRow.ROWSTATE_MODIFIED);
    }
    
    private class PredicateBinding extends BooleanBinding {
        private final Predicate<R> predicate;
        PredicateBinding(Predicate<R> predicate) {
            super.bind(RecordProperty.this);
            this.predicate = predicate;
        }
        @Override
        protected boolean computeValue() {
            R value = RecordProperty.this.get();
            return value != null && predicate.test(value);
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(RecordProperty.this);
        }

        @Override
        public void dispose() {
            super.unbind(RecordProperty.this);
            super.dispose();
        }
    }
    
    private class DataRowPredicateBinding extends BooleanBinding {
        private final Predicate<DataRow> predicate;
        DataRowPredicateBinding(Predicate<DataRow> predicate) {
            super.bind(RecordProperty.this);
            this.predicate = predicate;
        }
        @Override
        protected boolean computeValue() {
            R value = RecordProperty.this.get();
            return value != null && value instanceof DataRow && predicate.test((DataRow)value);
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(RecordProperty.this);
        }

        @Override
        public void dispose() {
            super.unbind(RecordProperty.this);
            super.dispose();
        }
    }
}
