/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.controls;

import javafx.scene.control.ListCell;

/**
 * List cell for displaying the name of a {@link 
 * @author Leonard T. Erwine
 * @param <T>
 */
public class CustomerListCell<T extends scheduler.view.customer.AppointmentCustomer<?>> extends ListCell<T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getName());
    }
}
