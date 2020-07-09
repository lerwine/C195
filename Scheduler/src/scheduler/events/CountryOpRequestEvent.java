package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;

public final class CountryOpRequestEvent extends OperationRequestEvent<CountryDAO, CountryModel> {

    private static final long serialVersionUID = 2755073772787755720L;

    /**
     * Base {@link EventType} for all {@code CountryOpRequestEvent}s.
     */
    public static final EventType<CountryOpRequestEvent> COUNTRY_OP_REQUEST = new EventType<>(OP_REQUEST_EVENT, "SCHEDULER_COUNTRY_OP_REQUEST");

    /**
     * {@link EventType} for edit {@code CountryOpRequestEvent}s.
     */
    public static final EventType<CountryOpRequestEvent> EDIT_REQUEST = new EventType<>(COUNTRY_OP_REQUEST, "SCHEDULER_COUNTRY_EDIT_REQUEST");

    /**
     * {@link EventType} for delete {@code CountryOpRequestEvent}s.
     */
    public static final EventType<CountryOpRequestEvent> DELETE_REQUEST = new EventType<>(COUNTRY_OP_REQUEST, "SCHEDULER_COUNTRY_DELETE_REQUEST");

    public CountryOpRequestEvent(ModelEvent<CountryDAO, CountryModel> event, Object source, EventTarget target, boolean isDelete) {
        super(event, source, target, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public CountryOpRequestEvent(ModelEvent<CountryDAO, CountryModel> event, boolean isDelete) {
        super(event, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public CountryOpRequestEvent(CountryModel target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

}
