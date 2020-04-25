package scheduler.model.ui;

import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.model.Country;
import scheduler.model.db.CountryRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CountryDbItem<T extends CountryRowData> extends CountryItem, UIDbModel<T> {
    
}
