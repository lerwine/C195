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
public class AppointmentTypeListCell extends ListCell<String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(scheduler.App.getCurrent().getAppointmentTypeDisplay(item));
    }
}
