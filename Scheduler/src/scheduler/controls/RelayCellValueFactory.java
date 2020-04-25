package scheduler.controls;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * A cell value factory that returns the row data itself. This is intended to be used with {@link ItemEditTableCellFactory}.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelayCellValueFactory<T> implements Callback<CellDataFeatures<T, T>, ObservableValue<T>> {
    
    @Override
    public ObservableValue<T> call(CellDataFeatures<T, T> param) {
        return new ReadOnlyObjectWrapper<>(param.getValue());
    }

}
