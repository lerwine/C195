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
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class DateTimeTableCellFactory<S, T extends TemporalAccessor> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private final DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault(Locale.Category.DISPLAY)).withZone(ZoneId.systemDefault());

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new DateTimeTableCell<>(fmt);
    }
}
