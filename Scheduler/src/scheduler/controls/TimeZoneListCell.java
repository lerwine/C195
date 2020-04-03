package scheduler.controls;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class TimeZoneListCell extends ListCell<ZoneId> {

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
    private final Locale locale;

    TimeZoneListCell(Locale locale) {
        this.locale = (null == locale) ? Locale.getDefault(Locale.Category.DISPLAY) : locale;
    }

    public TimeZoneListCell() {
        this(null);
    }

    @Override
    protected void updateItem(ZoneId item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null) {
            setText("");
        } else {
            ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(item);
            if (zonedDateTime != null) {
                String zoneOffset = zonedDateTime.getOffset().getId();
                if (zoneOffset != null) {
                    setText(String.format("%s (%s)", item.getDisplayName(TextStyle.FULL, locale), (zoneOffset.equalsIgnoreCase("Z")) ? "+00:00" : zoneOffset));
                    return;
                }
            }
            setText(item.getDisplayName(TextStyle.FULL, locale));
        }
    }
}
