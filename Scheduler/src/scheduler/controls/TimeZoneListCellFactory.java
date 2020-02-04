package scheduler.controls;

import java.time.ZoneId;
import java.util.Locale;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneListCellFactory implements Callback<ListView<ZoneId>, ListCell<ZoneId>> {
    private final Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
    @Override
    public ListCell<ZoneId> call(ListView<ZoneId> param) { return new TimeZoneListCell(locale); }
}
