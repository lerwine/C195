package scheduler.view;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author Leonard T. Erwine
 */
public class ControllerChangeEvent extends Event {

    private final ReadOnlyObjectWrapper<SchedulerController> oldController;
    private final ReadOnlyObjectWrapper<SchedulerController> newController;

    public ControllerChangeEvent(Object source, SchedulerController oldController, SchedulerController newController, String name) {
        this(source, oldController, newController, null, name);
    }

    public ControllerChangeEvent(Object source, SchedulerController oldController, SchedulerController newController, EventTarget target, String name) {
        super(source, target, new EventType<ControllerChangeEvent>(name));
        this.oldController = new ReadOnlyObjectWrapper<>(oldController);
        this.newController = new ReadOnlyObjectWrapper<>(newController);
    }

    public SchedulerController getOldController() {
        return oldController.get();
    }

    public ReadOnlyObjectProperty<SchedulerController> oldControllerProperty() {
        return oldController.getReadOnlyProperty();
    }

    public SchedulerController getNewController() {
        return newController.get();
    }

    public ReadOnlyObjectProperty<SchedulerController> newControllerProperty() {
        return newController.getReadOnlyProperty();
    }

}
