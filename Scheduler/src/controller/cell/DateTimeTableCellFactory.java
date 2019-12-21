/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.cell;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class DateTimeTableCellFactory<S, T extends TemporalAccessor> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null) ? "" : scheduler.App.getShortDateTimeFormatter().format(item));
            }
        };
    }
    
}
