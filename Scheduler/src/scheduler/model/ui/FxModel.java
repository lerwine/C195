package scheduler.model.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.DAO;
import scheduler.dao.DataRowState;
import scheduler.model.DataModel;
import scheduler.model.RelatedRecord;

/**
 * Interface for UI {@code DbDataModel}s with bindable JavaFX properties.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface FxModel extends DataModel {

    ReadOnlyIntegerProperty primaryKeyProperty();

    /**
     * Gets a value indicating if all model properties are valid.
     *
     * @return {@code true} if all properties are valid; otherwise, {@code false} if one or more properties are not valid.
     */
    boolean isValid();

    ReadOnlyBooleanProperty validProperty();

    /**
     * Gets the backing data access object.
     *
     * @return The backing data access object.
     */
    DAO getDataObject();

    /**
     * Gets the {@link ReadOnlyProperty} that contains the backing data access object.
     *
     * @return The {@link ReadOnlyProperty} that contains the backing data access object.
     */
    ReadOnlyObjectProperty<? extends DAO> dataObjectProperty();

    DataRowState getRowState();

    /**
     * Gets the property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     *
     * @return The property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     */
    ReadOnlyObjectProperty<? extends DataRowState> rowStateProperty();

}
