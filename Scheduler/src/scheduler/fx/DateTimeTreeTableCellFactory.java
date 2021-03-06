package scheduler.fx;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <S> The row item type.
 * @param <T> The cell item type.
 */
public class DateTimeTreeTableCellFactory<S, T extends TemporalAccessor> implements Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> {

    private final StringProperty format;
    private final ReadOnlyObjectWrapper<DateTimeFormatter> formatter;

    public DateTimeTreeTableCellFactory(@NamedArg("format") String format) {
        this.format = new SimpleStringProperty(this, "format", Values.asNonNullOrWhitespace(format, ()
                -> DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, FormatStyle.SHORT,
                        Chronology.ofLocale(Locale.getDefault(Locale.Category.FORMAT)), Locale.getDefault(Locale.Category.DISPLAY))));
        formatter = new ReadOnlyObjectWrapper<>(this, "format", DateTimeFormatter.ofPattern(this.format.get(), Locale.getDefault(Locale.Category.DISPLAY))
                .withZone(ZoneId.systemDefault()));
        this.format.addListener(this::onFormatChanged);
    }

    public String getFormat() {
        return format.get();
    }

    public void setFormat(String value) {
        format.set(value);
    }

    public StringProperty formatProperty() {
        return format;
    }

    public DateTimeFormatter getFormatter() {
        return formatter.get();
    }

    public ReadOnlyObjectProperty<DateTimeFormatter> formatterProperty() {
        return formatter.getReadOnlyProperty();
    }

    @Override
    public TreeTableCell<S, T> call(TreeTableColumn<S, T> param) {
        return new DateTimeTreeTableCell<>(formatter);
    }

    private void onFormatChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (null == newValue || newValue.isEmpty()) {
            format.set(DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, FormatStyle.SHORT,
                    Chronology.ofLocale(Locale.getDefault(Locale.Category.FORMAT)), Locale.getDefault(Locale.Category.DISPLAY)));
        } else {
            formatter.set(DateTimeFormatter.ofPattern(this.format.get(), Locale.getDefault(Locale.Category.DISPLAY))
                    .withZone(ZoneId.systemDefault()));
        }
    }

}
