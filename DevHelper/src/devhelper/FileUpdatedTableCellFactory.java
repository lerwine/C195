/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.TimeZone;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class FileUpdatedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault());
    
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>() {
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem((T)item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (item instanceof File) {
                    File file = (File)item;
                    if (file.exists())
                        super.setText(formatter.format(Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime()));
                    else
                        super.setText("");
                }
                else if (item instanceof LocalDateTime)
                    super.setText(formatter.format((LocalDateTime)item));
                else if (item instanceof Long)
                    super.setText(formatter.format(Instant.ofEpochMilli((long)item).atZone(ZoneId.systemDefault()).toLocalDateTime()));
                else if (item instanceof String)
                    super.setText((String)item);
                else
                    super.setText(item.toString());
            }
        };
    }
    
}
