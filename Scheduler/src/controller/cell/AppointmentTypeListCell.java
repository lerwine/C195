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
public class AppointmentTypeListCell extends ListCell<model.AppointmentType> {
    @Override
    protected void updateItem(model.AppointmentType item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getDisplayText());
    }
}
