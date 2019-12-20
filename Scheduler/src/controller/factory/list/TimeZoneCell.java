/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.factory.list;

import java.util.TimeZone;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneCell implements Callback<ListView<TimeZone>, ListCell<TimeZone>> {

    @Override
    public ListCell<TimeZone> call(ListView<TimeZone> param) {
        return new ListCell<TimeZone>() {
            @Override
            protected void updateItem(TimeZone item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null) ? "" : item.getDisplayName(scheduler.App.getCurrentLocale()));
            }
        };
    }
    
}
