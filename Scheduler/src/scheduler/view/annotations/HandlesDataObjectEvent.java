package scheduler.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a function can receive {@link scheduler.dao.event.DataObjectEvent}s. This expects the annotated method to have zero-length
 * parameters or to have the same parameters as
 * {@link scheduler.dao.event.DataObjectEventListener#onDataObjectEvent(scheduler.dao.event.DataObjectEvent)}.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlesDataObjectEvent {

    /**
     * Restricts the type of event that the annotated method should be called for.
     *
     * @return A {@link DaoChangeType} value that indicates if/what {@link scheduler.dao.event.DaoChangeAction} the annotated method should be
     * restricted to. The default is to allow all event types ({@link DaoChangeType#ANY}).
     */
    DaoChangeType type() default DaoChangeType.ANY;

}
