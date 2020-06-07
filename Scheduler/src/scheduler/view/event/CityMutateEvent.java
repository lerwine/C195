package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.ui.CityModel;

/**
 * Event that is fired when a {@link CityModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CityMutateEvent extends ItemMutateEvent<CityModel> {

    private static final long serialVersionUID = 2299267381558318300L;

    public static final EventType<CityMutateEvent> CITY_MUTATE_EVENT = new EventType<>(
            ITEM_MUTATE_EVENT,
            "CITY_MUTATE_EVENT");

    public static final EventType<CityMutateEvent> CITY_INSERT_EVENT = new EventType<>(
            CITY_MUTATE_EVENT,
            "CITY_INSERT_EVENT");

    public static final EventType<CityMutateEvent> CITY_UPDATE_EVENT = new EventType<>(
            CITY_MUTATE_EVENT,
            "CITY_UPDATE_EVENT");

    public static final EventType<CityMutateEvent> CITY_DELETE_EVENT = new EventType<>(
            CITY_MUTATE_EVENT,
            "CITY_DELETE_EVENT");

    public CityMutateEvent(CityModel source, EventTarget target, EventType<CityMutateEvent> type, Event fxEvent) {
        super(source, target, type, fxEvent);
    }

}
