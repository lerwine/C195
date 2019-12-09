/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.factory;

import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class CustomerNameTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>() {
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem((T)item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (item instanceof model.Customer)
                    super.setText(((model.Customer)item).getCustomerName());
                else if (item instanceof String)
                    super.setText((String)item);
                else
                    super.setText(item.toString());
            }
        };
    }
    
}
