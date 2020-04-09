package scheduler.controls;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import javafx.scene.control.TableCell;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class DateTimeTableCell<S, T extends TemporalAccessor> extends TableCell<S, T> {

    private final DateTimeFormatter fmt;

    DateTimeTableCell(DateTimeFormatter fmt) {
        this.fmt = (null == fmt) ? DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault(Locale.Category.DISPLAY)).withZone(ZoneId.systemDefault()) : fmt;
    }

    public DateTimeTableCell() {
        this(null);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : fmt.format(item));
    }
}
