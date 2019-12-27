/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneListCell extends ListCell<java.util.TimeZone> {
    @Override
    protected void updateItem(java.util.TimeZone item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getDisplayName(scheduler.App.getCurrent().getCurrentLocale()));
    }
}
