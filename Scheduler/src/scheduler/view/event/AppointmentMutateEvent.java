package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.ui.AppointmentModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentMutateEvent extends ItemMutateEvent<AppointmentModel> {

    private static final long serialVersionUID = -1145658585716643269L;

    public static final EventType<AppointmentMutateEvent> APPOINTMENT_MUTATE_EVENT = new EventType<>(
            ITEM_MUTATE_EVENT,
            "APPOINTMENT_MUTATE_EVENT");

    public static final EventType<AppointmentMutateEvent> APPOINTMENT_INSERT_EVENT = new EventType<>(
            APPOINTMENT_MUTATE_EVENT,
            "APPOINTMENT_INSERT_EVENT");

    public static final EventType<AppointmentMutateEvent> APPOINTMENT_UPDATE_EVENT = new EventType<>(
            APPOINTMENT_MUTATE_EVENT,
            "APPOINTMENT_UPDATE_EVENT");

    public static final EventType<AppointmentMutateEvent> APPOINTMENT_DELETE_EVENT = new EventType<>(
            APPOINTMENT_MUTATE_EVENT,
            "APPOINTMENT_DELETE_EVENT");

    public AppointmentMutateEvent(AppointmentModel source, EventTarget target, EventType<AppointmentMutateEvent> type, Event fxEvent) {
        super(source, target, type, fxEvent);
    }

}
