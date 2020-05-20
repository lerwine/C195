package scheduler.model.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import scheduler.dao.DataRowState;
import scheduler.dao.DataAccessObject;
import scheduler.model.DataRecord;
import scheduler.observables.RowStateProperty;
import scheduler.util.IPropertyBindable;
import scheduler.dao.DbObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public abstract class RelatedModel<T extends DbObject> implements FxDbModel<T> {

    private final ReadOnlyObjectWrapper<T> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final RowStateProperty rowState;
    private final ReadOnlyBooleanWrapper valid;

    protected RelatedModel(T dao) {
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", dao.getPrimaryKey());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", dao);
        if (dao instanceof DataRecord) {
            rowState = new RowStateProperty(this, "rowState", ((DataRecord<?>) dao).getRowState());
        } else {
            rowState = new RowStateProperty(this, "rowState", DataRowState.UNMODIFIED);
        }
        if (dao instanceof IPropertyBindable) {
            IPropertyBindable d = (IPropertyBindable) dao;
            d.addPropertyChangeListener((evt) -> {
                String propertyName = evt.getPropertyName();
                switch (propertyName) {
                    case DataAccessObject.PROP_CREATEDATE:
                    case DataAccessObject.PROP_CREATEDBY:
                    case DataAccessObject.PROP_LASTMODIFIEDBY:
                    case DataAccessObject.PROP_LASTMODIFIEDDATE:
                        break;
                    case DataAccessObject.PROP_PRIMARYKEY:
                        Object i = evt.getNewValue();
                        if (null != i && i instanceof Integer) {
                            primaryKey.set((int) i);
                        }
                        break;
                    case DataAccessObject.PROP_ROWSTATE:
                        Object rs = evt.getNewValue();
                        if (null != rs && rs instanceof DataRowState) {
                            rowState.set((DataRowState) rs);
                        }
                        break;
                    default:
                        onDataObjectPropertyChanged(dao, propertyName);
                        break;
                }
            });
        }
        valid = new ReadOnlyBooleanWrapper(true);
    }

    @Override
    public T getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<T> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
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

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    protected abstract void onDataObjectPropertyChanged(T dao, String propertyName);

}
