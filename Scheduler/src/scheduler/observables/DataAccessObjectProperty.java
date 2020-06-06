package scheduler.observables;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DbRecord;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
@Deprecated
public class DataAccessObjectProperty<T extends DbRecord> extends DataObjectProperty<T> {

    private final ReadOnlyObjectProperty<LocalDateTime> createDate;
    private final ReadOnlyStringProperty createdBy;
    private final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringProperty lastModifiedBy;

    public DataAccessObjectProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
        createDate = createReadOnlyDateTimeProperty(DataAccessObject.PROP_CREATEDATE, (t) -> (null == t) ? null : t.getCreateDate());
        createdBy = createReadOnlyStringProperty(DataAccessObject.PROP_CREATEDBY, (t) -> (null == t) ? "" : t.getCreatedBy());
        lastModifiedDate = createReadOnlyDateTimeProperty(DataAccessObject.PROP_LASTMODIFIEDDATE, (t) -> (null == t) ? null : t.getLastModifiedDate());
        lastModifiedBy = createReadOnlyStringProperty(DataAccessObject.PROP_LASTMODIFIEDBY, (t) -> (null == t) ? "" : t.getLastModifiedBy());
    }

    public final LocalDateTime getCreateDate() {
        return createDate.get();
    }
    
    public final ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return createDate;
    }
    
    public final String getCreatedBy() {
        return createdBy.get();
    }
    
    public final ReadOnlyStringProperty createdByProperty() {
        return createdBy;
    }
    
    public final LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }
    
    public final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate;
    }
    
    public final String getLastModifiedBy() {
        return lastModifiedBy.get();
    }
    
    public final ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy;
    }
    
}
