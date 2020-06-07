package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;
import static scheduler.view.event.ItemMutateEvent.ITEM_MUTATE_EVENT;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CityMutateEvent extends ItemMutateEvent<CityModel> {

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

    public CityMutateEvent(CityModel source, EventType<CityMutateEvent> type, Event fxEvent) {
        super(source, type, fxEvent);
    }

    @Override
    public CityDAO getTarget() {
        return (CityDAO) super.getTarget();
    }

}
