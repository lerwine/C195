package scheduler.model.ui;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.DbRecord;
import scheduler.model.DataRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface IFxRecordModel<T extends DbRecord> extends FxDbModel<T>, DataRecord<LocalDateTime> {

    ReadOnlyObjectProperty<LocalDateTime> createDateProperty();

    ReadOnlyStringProperty createdByProperty();

    ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty();

    ReadOnlyStringProperty lastModifiedByProperty();

}
