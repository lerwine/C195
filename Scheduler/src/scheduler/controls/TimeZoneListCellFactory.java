/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.controls;

import java.time.ZoneId;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneListCellFactory implements Callback<ListView<ZoneId>, ListCell<ZoneId>> {
    @Override
    public ListCell<ZoneId> call(ListView<ZoneId> param) { return new TimeZoneListCell(); }
}
