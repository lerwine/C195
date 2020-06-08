package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.FxRecordModel;

/**
 * Event that is fired when a {@link CountryModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CountryEvent extends ModelItemEvent<CountryModel, CountryDAO> {

    private static final long serialVersionUID = 6220482582846221386L;

    public static final EventType<CountryEvent> COUNTRY_MODEL_EVENT = new EventType<>(
            MODEL_ITEM_EVENT,
            "COUNTRY_MODEL_EVENT");

    public static final EventType<CountryEvent> COUNTRY_EDIT_REQUEST_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_EDIT_REQUEST_EVENT");

    public static final EventType<CountryEvent> COUNTRY_DELETE_REQUEST_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_DELETE_REQUEST_EVENT");

    public static final EventType<CountryEvent> COUNTRY_INSERTING_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_INSERTING_EVENT");

    public static final EventType<CountryEvent> COUNTRY_INSERTED_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_INSERTED_EVENT");

    public static final EventType<CountryEvent> COUNTRY_UPDATING_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_UPDATING_EVENT");

    public static final EventType<CountryEvent> COUNTRY_UPDATED_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_UPDATED_EVENT");

    public static final EventType<CountryEvent> COUNTRY_DELETING_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_DELETING_EVENT");

    public static final EventType<CountryEvent> COUNTRY_DELETED_EVENT = new EventType<>(
            COUNTRY_MODEL_EVENT,
            "COUNTRY_DELETED_EVENT");

    private CountryEvent(CountryEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CountryEvent(CountryModel model, Object source, EventTarget target, EventType<CountryEvent> type, Event fxEvent) {
        super(model, source, target, type, fxEvent);
    }

    public CountryEvent(Object source, CountryDAO target, EventType<CountryEvent> type, Event fxEvent) {
        super(source, target, type, fxEvent);
    }

    @Override
    public FxRecordModel.ModelFactory<CountryDAO, CountryModel> getModelFactory() {
        return CountryModel.getFactory();
    }

    @Override
    public synchronized CountryEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryEvent(this, newSource, newTarget);
    }

    @Override
    public EventType<CountryEvent> getEventType() {
        return (EventType<CountryEvent>) super.getEventType();
    }

}
