/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneListCellFactory implements Callback<ListView<java.util.TimeZone>, ListCell<java.util.TimeZone>> {
    @Override
    public ListCell<java.util.TimeZone> call(ListView<java.util.TimeZone> param) { return new TimeZoneListCell(); }
}
