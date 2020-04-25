package scheduler.model.ui;

import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.model.Country;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryItem extends Country, UIModel {

    ReadOnlyStringProperty nameProperty();
    
}
