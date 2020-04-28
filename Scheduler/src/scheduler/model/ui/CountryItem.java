package scheduler.model.ui;

import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.model.db.CountryRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryItem extends CountryRowData, FxModel {

    ReadOnlyStringProperty nameProperty();
    
}
