package scheduler.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a function can receive {@link scheduler.view.ViewLifecycleEvent}s.
 * This expects the annotated method to have the same signature as
 * {@link scheduler.view.ViewLifecycleEventListener#onViewLifecycleEvent(scheduler.view.ViewLifecycleEvent)} or
 * {@link scheduler.view.ViewControllerLifecycleEventListener#onViewControllerLifecycleEvent(scheduler.view.ViewControllerLifecycleEvent)}.
 * 
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlesViewLifecycleEvent {
    ViewLifecycleEventType type() default ViewLifecycleEventType.ANY;
}
