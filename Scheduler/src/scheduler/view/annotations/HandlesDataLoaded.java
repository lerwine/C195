package scheduler.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a function can receive {@link scheduler.view.event.DataLoadedEvent}s.
 * This expects the annotated method to have zero-length parameters or to have the same parameters as
 * {@link scheduler.view.event.DataLoadedEventListener#onDataLoaded(scheduler.view.event.DataLoadedEvent)}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlesDataLoaded {
}
