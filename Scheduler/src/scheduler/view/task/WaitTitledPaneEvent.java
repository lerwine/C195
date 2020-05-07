package scheduler.view.task;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.view.task.WaitTitledPaneEvent}
 */
public class WaitTitledPaneEvent extends Event {

    public static final EventType<WaitTitledPaneEvent> BASE = new EventType<WaitTitledPaneEvent>(ANY, "WAIT_TITLED_PANE_EVENT");

    public static final EventType<WaitTitledPaneEvent> RUNNING = new EventType<WaitTitledPaneEvent>(BASE, "WAIT_TITLED_PANE_EVENT_RUNNING");

    public static final EventType<WaitTitledPaneEvent> DONE = new EventType<WaitTitledPaneEvent>(BASE, "WAIT_TITLED_PANE_EVENT_DONE");
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
