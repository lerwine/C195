package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;
import static scheduler.view.event.ItemMutateEvent.ITEM_MUTATE_EVENT;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CountryMutateEvent extends ItemMutateEvent<CountryModel> {

    private static final long serialVersionUID = 6220482582846221386L;

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

    public CountryMutateEvent(CountryModel source, EventTarget target, EventType<CountryMutateEvent> type, Event fxEvent) {
        super(source, target, type, fxEvent);
    }

}
