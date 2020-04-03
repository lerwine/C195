package scheduler.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a function can receive {@link scheduler.dao.event.DataObjectEvent}s.
 * This expects the annotated method to have the same signature as
 * {@link scheduler.dao.event.DataObjectEventListener#onDataObjectEvent(scheduler.dao.event.DataObjectEvent)}.
 *
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlesDataObjectEvent {
    /**
     * If defined, this indicates what {@link scheduler.dao.event.DaoChangeAction} the annotated method is for.
     * 
     * @return A {@link DaoChangeType} that determines what {@link scheduler.dao.event.DaoChangeAction} the annotated method is for. 
     */
    DaoChangeType type() default DaoChangeType.ANY;
}
