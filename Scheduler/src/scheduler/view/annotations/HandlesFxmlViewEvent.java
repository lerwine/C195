package scheduler.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a function can receive {@link scheduler.view.event.FxmlViewEvent}s. This expects the annotated method to have zero-length parameters
 * or to have the same parameters as {@link scheduler.view.event.FxmlViewEventListener#onFxmlViewEvent(scheduler.view.event.FxmlViewEvent)} or
 * {@link scheduler.view.event.FxmlViewControllerEventListener#onFxmlViewControllerEvent(scheduler.view.event.FxmlViewControllerEvent)}.
 *
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlesFxmlViewEvent {

    /**
     * Restricts the type of event that the annotated method should be called for.
     *
     * @return A {@link FxmlViewEventHandling} value that indicates how/if calls to the annotated method should be restricted. The default is to allow
     * all event types ({@link FxmlViewEventHandling#ANY}).
     */
    FxmlViewEventHandling value() default FxmlViewEventHandling.ANY;

}
