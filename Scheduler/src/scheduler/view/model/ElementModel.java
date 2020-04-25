package scheduler.view.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.db.RowData;

/**
 * Interface for object models that reference a {@link RowData}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link RowData} being represented.
 * @deprecated Use {@link scheduler.model.DataRecord} or {@link scheduler.model.ui.UIModel}, instead.
 */
public interface ElementModel<T extends RowData> {
    T getDataObject();

    ReadOnlyProperty<T> dataObjectProperty();

    int getPrimaryKey();

    ReadOnlyIntegerProperty primaryKeyProperty();
}
