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
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class DateTimeTableCellFactory<S, T extends TemporalAccessor> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private final DateTimeFormatter formatter;

    public DateTimeTableCellFactory(DateTimeFormatter formatter) {
        this.formatter = (null == formatter) ? DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault(Locale.Category.DISPLAY)).withZone(ZoneId.systemDefault()) : formatter;
    }
    public DateTimeTableCellFactory() {
        this(null);
    }
    
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new DateTimeTableCell<>(formatter);
    }
}
