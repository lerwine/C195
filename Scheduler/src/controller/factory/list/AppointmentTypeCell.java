/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.factory.list;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import model.AppointmentType;

/**
 *
 * @author Leonard T. Erwine
 */
public class AppointmentTypeCell implements Callback<ListView<AppointmentType>, ListCell<AppointmentType>> {

    @Override
    public ListCell<AppointmentType> call(ListView<AppointmentType> param) {
        return new ListCell<AppointmentType>() {
            @Override
            protected void updateItem(AppointmentType item, boolean empty) {
                super.updateItem(item, empty);
                setText(item.getDisplayText());
            }
        };
    }
    
}
