package scheduler.view.annotations;

import scheduler.view.ViewControllerLifecycleEventListener;
import scheduler.view.ViewLifecycleEventReason;

/**
 * Types of events that a {@link ViewControllerLifecycleEventListener} can handle.
 * 
 * @author lerwi
 */
public enum ViewLifecycleEventType {
    /**
     * Handles any {@link scheduler.view.ViewLifecycleEvent};
     */
    ANY(null),
    
    /**
     * Handles {@link scheduler.view.ViewLifecycleEvent}s where {@link scheduler.view.ViewLifecycleEvent#reason} is
     * {@link ViewLifecycleEventType.LOADED}.
     */
    LOADED(ViewLifecycleEventReason.LOADED),
    
    /**
     * Handles {@link scheduler.view.ViewLifecycleEvent}s where {@link scheduler.view.ViewLifecycleEvent#reason} is
     * {@link ViewLifecycleEventType.ADDED}.
     */
    ADDED(ViewLifecycleEventReason.ADDED),
    
    /**
     * Handles {@link scheduler.view.ViewLifecycleEvent}s where {@link scheduler.view.ViewLifecycleEvent#reason} is
     * {@link ViewLifecycleEventType.SHOWN}.
     */
    SHOWN(ViewLifecycleEventReason.SHOWN),
    
    /**
     * Handles {@link scheduler.view.ViewLifecycleEvent}s where {@link scheduler.view.ViewLifecycleEvent#reason} is
     * {@link ViewLifecycleEventType.UNLOADED}.
     */
    UNLOADED(ViewLifecycleEventReason.UNLOADED);
    
    private final ViewLifecycleEventReason reason;

    /**
     * The {@link ViewLifecycleEventReason} for the handling method.
     * 
     * @return The {@link scheduler.view.ViewLifecycleEvent#reason} that the handling method is limited to or {@code null} if it handles all events.
     */
    public ViewLifecycleEventReason getReason() {
        return reason;
    }
    
    private ViewLifecycleEventType(ViewLifecycleEventReason reason) {
        this.reason = reason;
    }
}
