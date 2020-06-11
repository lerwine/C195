package scheduler.fx;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.RegionTable;
import scheduler.model.ui.CountryModel;
import scheduler.util.NodeUtil;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class TimeZoneListCellFactory implements Callback<ListView<TimeZone>, ListCell<TimeZone>> {

    public static ObservableList<TimeZone> getZoneIdOptions() {
        Stream.Builder<TimeZone> builder = Stream.builder();
        ZoneId.getAvailableZoneIds().forEach((i) -> {
            builder.accept(TimeZone.getTimeZone(ZoneId.of(i)));
        });
        ObservableList<TimeZone> result = FXCollections.observableArrayList();
        builder.build().sorted((TimeZone o1, TimeZone o2) -> o1.getRawOffset() - o2.getRawOffset()).forEach((tz) -> result.add(tz));
        return result;
    }

    private final ObjectProperty<CountryModel> currentCountry;
    private final ObservableList<TimeZone> timeZones;

    public TimeZoneListCellFactory() {
        currentCountry = new SimpleObjectProperty<>(null);
        timeZones = FXCollections.<TimeZone>observableArrayList();
        currentCountry.addListener(this::currentCountryChanged);
    }

    public CountryModel getCurrentCountry() {
        return currentCountry.get();
    }

    public void setCurrentCountry(CountryModel value) {
        currentCountry.set(value);
    }

    public ObjectProperty<CountryModel> currentCountryProperty() {
        return currentCountry;
    }

    @Override
    public TimeZoneListCell call(ListView<TimeZone> param) {
        return new TimeZoneListCell() {
            private TimeZone currentValue = null;

            {
                timeZones.addListener((Change<? extends TimeZone> c) -> {
                    onValueChanged(c.getList());
                });
            }

            private void onValueChanged(ObservableList<? extends TimeZone> list) {
                if (null == currentValue || list.isEmpty() || list.contains(currentValue)) {
                    NodeUtil.removeCssClass(this, CssClassName.INFO);
                } else {
                    NodeUtil.addCssClass(this, CssClassName.INFO);
                }
            }

            @Override
            protected void updateItem(TimeZone item, boolean empty) {
                super.updateItem(item, empty);
                if (!Objects.equals(item, currentValue)) {
                    currentValue = item;
                    onValueChanged(timeZones);
                }
            }

        };
    }

    private void currentCountryChanged(ObservableValue<? extends CountryModel> observable, CountryModel oldValue, CountryModel newValue) {
        if (null != newValue) {
            String countryCode = newValue.getLocale().getCountry();
            List<TimeZone> zonesForCountry = RegionTable.getZonesForCountry(countryCode);
            if (!zonesForCountry.isEmpty()) {
                if (timeZones.isEmpty()) {
                    timeZones.addAll(zonesForCountry);
                } else {
                    timeZones.setAll(zonesForCountry);
                }
                return;
            }
        }
        if (!timeZones.isEmpty()) {
            timeZones.clear();
        }
    }

}
