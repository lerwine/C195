package scheduler.view.task;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class WaitTitledPaneEvent extends Event {

    private static final long serialVersionUID = -8940568199287753069L;

    public static final EventType<WaitTitledPaneEvent> BASE = new EventType<WaitTitledPaneEvent>(ANY,
            "WAIT_TITLED_PANE_EVENT");

    public static final EventType<WaitTitledPaneEvent> RUNNING = new EventType<WaitTitledPaneEvent>(BASE, "WAIT_TITLED_PANE_EVENT_RUNNING");

    public static final EventType<WaitTitledPaneEvent> DONE = new EventType<WaitTitledPaneEvent>(BASE, "WAIT_TITLED_PANE_EVENT_DONE");

    public static final EventType<WaitTitledPaneEvent> SUCCEEDED = new EventType<WaitTitledPaneEvent>(DONE, "WAIT_TITLED_PANE_EVENT_SUCCEEDED");

    public static final EventType<WaitTitledPaneEvent> FAILED = new EventType<WaitTitledPaneEvent>(DONE, "WAIT_TITLED_PANE_EVENT_FAILED");

    public static final EventType<WaitTitledPaneEvent> CANCELED = new EventType<WaitTitledPaneEvent>(DONE, "WAIT_TITLED_PANE_EVENT_CANCELED");

    public static final EventType<WaitTitledPaneEvent> FAIL_ACK = new EventType<WaitTitledPaneEvent>(BASE, "WAIT_TITLED_PANE_EVENT_FAIL_ACK");

    public static final EventType<WaitTitledPaneEvent> CANCEL_ACK = new EventType<WaitTitledPaneEvent>(BASE, "WAIT_TITLED_PANE_EVENT_CANCEL_ACK");

    private final Task<?> task;

    public WaitTitledPaneEvent(Object source, WaitTitledPane target, Task<?> task, EventType<? extends WaitTitledPaneEvent> eventType) {
        super(source, target, eventType);
        this.task = task;
    }

    @Override
    public WaitTitledPane getTarget() {
        return (WaitTitledPane) super.getTarget();
    }

    public Task<?> getTask() {
        return task;
    }

}
