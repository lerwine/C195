package scheduler.fx;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableCell;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class DateTimeTreeTableCell<S, T extends TemporalAccessor> extends TreeTableCell<S, T> {

    private final ReadOnlyObjectProperty<DateTimeFormatter> property;
    private DateTimeFormatter formatter;

    DateTimeTreeTableCell(ReadOnlyObjectProperty<DateTimeFormatter> formatter) {
        this.property = formatter;
        setWrapText(true);
        if (null != formatter) {
            formatter.addListener(this::onFormatterChanged);
            this.formatter = formatter.get();
            if (null != this.formatter) {
                return;
            }
        }
        this.formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault(Locale.Category.DISPLAY)).withZone(ZoneId.systemDefault());
    }

    public DateTimeTreeTableCell(DateTimeFormatter formatter) {
        this.property = null;
        setWrapText(true);
        this.formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault(Locale.Category.DISPLAY)).withZone(ZoneId.systemDefault());
    }

    public DateTimeTreeTableCell() {
        this((DateTimeFormatter) null);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((empty || null == item) ? "" : formatter.format(item));
    }

    private void onFormatterChanged(ObservableValue<? extends DateTimeFormatter> observable, DateTimeFormatter oldValue, DateTimeFormatter newValue) {
        formatter = (null == newValue)
                ? DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault(Locale.Category.DISPLAY)).withZone(ZoneId.systemDefault())
                : newValue;
    }
}
