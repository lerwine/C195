package scheduler.view.annotations;

import scheduler.view.event.FxmlViewEventType;

/**
 * Defines the type of events that a method annotated with {@link scheduler.view.annotations.HandlesFxmlViewEvent} can handle.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum FxmlViewEventHandling {
    /**
     * Invokes the annotated method for all {@link scheduler.view.event.FxmlViewEvent}s.
     */
    ANY(null),
    
    /**
     * Only invoke the annotated method for {@link scheduler.view.event.FxmlViewEvent}s where {@link scheduler.view.event.FxmlViewEvent#type} is
     * {@link FxmlViewEventType#LOADED}.
     */
    LOADED(FxmlViewEventType.LOADED),
    
    /**
     *  Only invoke the annotated method for {@link scheduler.view.event.FxmlViewEvent}s where {@link scheduler.view.event.FxmlViewEvent#type} is
     * {@link FxmlViewEventType#BEFORE_SHOW}.
     */
    BEFORE_SHOW(FxmlViewEventType.BEFORE_SHOW),
    
    /**
     *  Only invoke the annotated method for {@link scheduler.view.event.FxmlViewEvent}s where {@link scheduler.view.event.FxmlViewEvent#type} is
     * {@link FxmlViewEventType#SHOWN}.
     */
    SHOWN(FxmlViewEventType.SHOWN),
    
    /**
     *  Only invoke the annotated method for {@link scheduler.view.event.FxmlViewEvent}s where {@link scheduler.view.event.FxmlViewEvent#type} is
     * {@link FxmlViewEventType#UNLOADED}.
     */
    UNLOADED(FxmlViewEventType.UNLOADED);
    
    private final FxmlViewEventType type;

    /**
     * The {@link FxmlViewEventType} for the handling method.
     * 
     * @return The {@link scheduler.view.event.FxmlViewEvent#type} that the handling method is limited to or {@code null} if it handles all events.
     */
    public FxmlViewEventType getType() {
        return type;
    }
    
    private FxmlViewEventHandling(FxmlViewEventType type) {
        this.type = type;
    }
}
