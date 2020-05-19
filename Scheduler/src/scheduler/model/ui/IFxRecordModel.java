package scheduler.model.ui;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.DataAccessObject;
import scheduler.dao.IDataAccessObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface IFxRecordModel<T extends IDataAccessObject> extends FxDbModel<T> {

    ReadOnlyObjectProperty<LocalDateTime> createDateProperty();

    ReadOnlyStringProperty createdByProperty();

    ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty();

    ReadOnlyStringProperty lastModifiedByProperty();

}
