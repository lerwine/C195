package scheduler.controls;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneListCellFactory implements Callback<ListView<TimeZone>, ListCell<TimeZone>> {

    @Override
    public ListCell<TimeZone> call(ListView<TimeZone> param) {
        return new TimeZoneListCell();
    }
    
    public static ObservableList<TimeZone> getZoneIdOptions() {
        Stream.Builder<TimeZone> builder = Stream.builder();
        ZoneId.getAvailableZoneIds().forEach((i) -> {
            builder.accept(TimeZone.getTimeZone(ZoneId.of(i)));
        });
        ObservableList<TimeZone> result = FXCollections.observableArrayList();
        builder.build().sorted((TimeZone o1, TimeZone o2) ->o1.getRawOffset() - o2.getRawOffset()).forEach((tz) -> result.add(tz));
        return result;
    }
}
