/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.controls;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 * @param <T>
 */
public class DateTimeTableCellFactory<S, T extends TemporalAccessor> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault(Locale.Category.DISPLAY)).withZone(ZoneId.systemDefault());
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null) ? "" : fmt.format(item));
            }
        };
    }
    
}
