/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import javafx.scene.control.ListCell;
import view.ChildModel;

/**
 * List cell for displaying the name of a {@link 
 * @author Leonard T. Erwine
 * @param <T>
 */
public class CustomerListCell<T extends view.customer.AppointmentCustomer<? extends scheduler.dao.Customer>> extends ListCell<T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getName());
    }
}
