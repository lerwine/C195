package scheduler.fx;

import java.util.TimeZone;
import javafx.scene.control.ListCell;
import scheduler.model.CityProperties;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class TimeZoneListCell extends ListCell<TimeZone> {
    /*
    <ComboBox fx:id="timeZoneComboBox" maxWidth="1.7976931348623157E308">
        <cellFactory>
            <TimeZoneListCellFactory />
        </cellFactory>
        <buttonCell>
            <TimeZoneListCell />
        </buttonCell>
    </ComboBox>
    */

    @Override
    protected void updateItem(TimeZone item, boolean empty) {
        super.updateItem(item, empty);
        setText(CityProperties.getTimeZoneDisplayText(item));
    }
}
