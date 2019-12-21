/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.cell;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class AppointmentTypeTableCellFactory<S> implements Callback<TableColumn<S, String>, TableCell<S, String>> {

    @Override
    public TableCell<S, String> call(TableColumn<S, String> param) {
        return new TableCell<S, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(scheduler.App.getAppointmentTypeDisplay(item));
            }
        };
    }
    
}
