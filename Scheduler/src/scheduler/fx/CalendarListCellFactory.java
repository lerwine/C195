package scheduler.fx;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.view.appointment.CalendarCellData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CalendarListCellFactory implements Callback<ListView<CalendarCellData>, ListCell<CalendarCellData>> {

    private final DateTimeFormatter formatter;
    private final BooleanProperty singleLine;

    public CalendarListCellFactory() {
        formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
        singleLine = new SimpleBooleanProperty(false);
    }

    public boolean isSingleLine() {
        return singleLine.get();
    }

    public void setSingleLine(boolean value) {
        singleLine.set(value);
    }

    public BooleanProperty singleLineProperty() {
        return singleLine;
    }

    @Override
    public CalendarListCell call(ListView<CalendarCellData> param) {
        return new CalendarListCell(formatter, singleLine);
    }

}
