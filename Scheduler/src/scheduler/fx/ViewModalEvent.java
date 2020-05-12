package scheduler.fx;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ViewModalEvent extends Event {

    public static final EventType<ViewModalEvent> VIEW_MODAL = new EventType<ViewModalEvent>(Event.ANY, "SCHEDULER_VIEW_MODAL");
    private final ButtonType type;

    public ViewModalEvent(Object source, EventTarget target, ButtonType type) {
        super(source, target, VIEW_MODAL);
        this.type = type;
    }

    public ButtonType getType() {
        return type;
    }
    

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" [ source = ").append(getSource());
        sb.append(", target = ").append(getTarget());
        sb.append(", eventType = ").append(getEventType());
        sb.append(", consumed = ").append(isConsumed());
        return sb.append("]").toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<ViewModalEvent> getEventType() {
        return (EventType<ViewModalEvent>) super.getEventType();
    }
    
}
