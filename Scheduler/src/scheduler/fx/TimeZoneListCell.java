package scheduler.fx;

import java.util.TimeZone;
import javafx.scene.control.ListCell;

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
        if (item == null) {
            setText("");
        } else {
            item.getDisplayName();
            int u = item.getRawOffset();
            boolean n = (u < 0);
            if (n)
                u *= -1;
            int s = u / 1000;
            u -= (s * 1000);
            int m = s / 60;
            s -= (m * 60);
            int h = m / 60;
            m -= (h * 60);
            if (u > 0)
                setText(String.format("%s (%s%02d:%02d:%02d.%d)", item.getID(), (n) ? "-" : "+", h, m, s, u));
            else if (s > 0)
                setText(String.format("%s (%s%02d:%02d:%02d)", item.getID(), (n) ? "-" : "+", h, m, s));
            else
                setText(String.format("%s (%s%02d:%02d)", item.getID(), (n) ? "-" : "+", h, m));
        }
    }
}
