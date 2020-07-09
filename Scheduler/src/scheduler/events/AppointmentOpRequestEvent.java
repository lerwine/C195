package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;

/**
 * Events representing a request to edit or delete an {@link AppointmentDAO} from the database, which may include a related {@link AppointmentModel}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentOpRequestEvent extends OperationRequestEvent<AppointmentDAO, AppointmentModel> {

    private static final long serialVersionUID = -9002234106125075233L;

    /**
     * Base {@link EventType} for all {@code AppointmentOpRequestEvent}s.
     */
    public static final EventType<AppointmentOpRequestEvent> APPOINTMENT_OP_REQUEST = new EventType<>(OP_REQUEST_EVENT, "SCHEDULER_APPOINTMENT_OP_REQUEST");

    /**
     * {@link EventType} for edit {@code AppointmentOpRequestEvent}s.
     */
    public static final EventType<AppointmentOpRequestEvent> EDIT_REQUEST = new EventType<>(APPOINTMENT_OP_REQUEST, "SCHEDULER_APPOINTMENT_EDIT_REQUEST");

    /**
     * {@link EventType} for delete {@code AppointmentOpRequestEvent}s.
     */
    public static final EventType<AppointmentOpRequestEvent> DELETE_REQUEST = new EventType<>(APPOINTMENT_OP_REQUEST, "SCHEDULER_APPOINTMENT_DELETE_REQUEST");

    public AppointmentOpRequestEvent(ModelEvent<AppointmentDAO, AppointmentModel> event, Object source, EventTarget target, boolean isDelete) {
        super(event, source, target, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public AppointmentOpRequestEvent(ModelEvent<AppointmentDAO, AppointmentModel> event, boolean isDelete) {
        super(event, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public AppointmentOpRequestEvent(AppointmentModel target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

}
