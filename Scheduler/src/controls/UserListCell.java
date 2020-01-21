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
 * @param <T>
 */
public class UserListCell<T extends view.user.AppointmentUser<?>> extends ListCell<T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : item.getUserName());
    }
}
