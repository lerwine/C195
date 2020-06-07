package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;
import static scheduler.view.event.ItemMutateEvent.ITEM_MUTATE_EVENT;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CountryMutateEvent extends ItemMutateEvent<CountryModel> {

    public static final EventType<CountryMutateEvent> COUNTRY_MUTATE_EVENT = new EventType<>(
            ITEM_MUTATE_EVENT,
            "COUNTRY_MUTATE_EVENT");

    public static final EventType<CountryMutateEvent> COUNTRY_INSERT_EVENT = new EventType<>(
            COUNTRY_MUTATE_EVENT,
            "COUNTRY_INSERT_EVENT");

    public static final EventType<CountryMutateEvent> COUNTRY_UPDATE_EVENT = new EventType<>(
            COUNTRY_MUTATE_EVENT,
            "COUNTRY_UPDATE_EVENT");

    public static final EventType<CountryMutateEvent> COUNTRY_DELETE_EVENT = new EventType<>(
            COUNTRY_MUTATE_EVENT,
            "COUNTRY_DELETE_EVENT");

    public CountryMutateEvent(CountryModel source, EventType<CountryMutateEvent> type, Event fxEvent) {
        super(source, type, fxEvent);
    }

    @Override
    public CountryDAO getTarget() {
        return (CountryDAO) super.getTarget();
    }

}
