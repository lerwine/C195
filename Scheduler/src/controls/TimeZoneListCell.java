/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;
import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneListCell extends ListCell<java.util.TimeZone> {
    final Locale locale = scheduler.App.CURRENT.get().getCurrentLocale();
    
    @Override
    protected void updateItem(TimeZone item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null)
            setText("");
        else {
            ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(item.toZoneId());
            String zoneOffset = zonedDateTime.getOffset().getId();
            setText(String.format("%s (%s)", item.getDisplayName(locale), (zoneOffset.equalsIgnoreCase("Z")) ? "+00:00" : zoneOffset));
        }
    }
}
