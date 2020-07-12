package scheduler.observables;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <M>
 */
public class ReadOnlyModelProperty<M extends scheduler.model.ui.EntityModel<?>> extends ReadOnlyObjectWrapper<M> {

    public ReadOnlyModelProperty() {
        super();
    }

    public BooleanBinding isNewRow() {
        return Bindings.createBooleanBinding(() -> {
            M value = get();
            if (null != value) {
                DataAccessObject dao = value.dataObject();
                return null != dao && dao.getRowState() == DataRowState.NEW;
            }
            return false;
        }, this);
    }

    public BooleanBinding isDeleted() {
        return Bindings.createBooleanBinding(() -> {
            M value = get();
            if (null != value) {
                DataAccessObject dao = value.dataObject();
                return null != dao && dao.getRowState() == DataRowState.NEW;
            }
            return false;
        }, this);
    }

    public BooleanBinding isModified() {
        return Bindings.createBooleanBinding(() -> {
            M value = get();
            if (null != value) {
                DataAccessObject dao = value.dataObject();
                return null != dao && dao.getRowState() == DataRowState.MODIFIED;
            }
            return false;
        }, this);
    }

    public BooleanBinding isSaved() {
        return Bindings.createBooleanBinding(() -> {
            M value = get();
            if (null != value) {
                DataAccessObject dao = value.dataObject();
                return null != dao && dao.getRowState() == DataRowState.UNMODIFIED;
            }
            return false;
        }, this);
    }
}
