package scheduler.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;
import scheduler.dao.DataAccessObject;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
import scheduler.view.annotations.DaoChangeType;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventListener;
import scheduler.view.event.FxmlViewEventType;
import static scheduler.util.AnnotationHelper.getAnnotatedEventHandlerMethods;
import scheduler.view.annotations.HandlesDataLoaded;
import scheduler.view.event.DataLoadedEvent;
import scheduler.view.event.DataLoadedEventListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class EventHelper {

    private static final Logger LOG = Logger.getLogger(EventHelper.class.getName());

    private static void invokeEventMethods(Object target, Iterator<Method> methodIterator, EventObject event) {
        if (!methodIterator.hasNext()) {
            return;
        }
        Method method = methodIterator.next();
        boolean wasAccessible = method.isAccessible();
        if (!wasAccessible) {
            method.setAccessible(true);
        }
        try {
            if (method.getParameters().length == 0) {
                method.invoke(target, new Object[0]);
            } else {
                method.invoke(target, new Object[]{event});
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, String.format("Error invoking %s", method.toGenericString()), ex);
        } finally {
            if (!wasAccessible) {
                method.setAccessible(false);
            }
        }
        invokeEventMethods(target, methodIterator, event);
    }

    /**
     * Fires a {@link DataObjectEvent} on a target object.
     * <p>
     * The target object can receive the event in two ways:</p>
     * <dl>
     * <dt>Annotate a method using {@link HandlesDataObjectEvent}</dt>
     * <dd>The annotated method must return void and can up to one parameter. If it defines the parameter, it must assignable from event object
     * type.</dd>
     * <dt>Implement the {@link DataObjectEventListener} interface</dt>
     * <dd>The {@link DataObjectEventListener#onDataObjectEvent(DataObjectEvent)} method will be called with the event object as the parameter</dd>
     * </dl>
     *
     * @param <T> The type of {@link DataAccessObject} for the {@link DataObjectEvent}.
     * @param target The object to fire the event on.
     * @param event The {@link DataObjectEvent} to be fired.
     */
    public static <T extends DataAccessObject> void fireDataObjectEvent(Object target, DataObjectEvent<T> event) {
        LOG.log(Level.FINE, () -> String.format("Firing DataObjectEvent %s on %s", event.getChangeAction().name(), event.getDataObject().getClass().getName()));
        if (null == target) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        if (target instanceof DataObjectEventListener) {
            try {
                ((DataObjectEventListener<T>)target).onDataObjectEvent(event);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Error invoking interface implementation method", ex);
            }
        }
        final DaoChangeAction action = event.getChangeAction();
        Iterator<Method> methods = getAnnotatedEventHandlerMethods(targetClass, HandlesDataObjectEvent.class, eventClass, true, (t)
                -> t.type() == DaoChangeType.ANY || t.type().getChangeAction() == action).iterator();
        invokeEventMethods(target, methods, event);
    }

    /**
     * Fires a {@link FxmlViewEvent} on a target object.
     * <p>
     * The target object can receive the event in two ways:</p>
     * <dl>
     * <dt>Annotate a method using {@link HandlesFxmlViewEvent}</dt>
     * <dd>The annotated method must return void and can up to one parameter. If it defines the parameter, it must assignable from event object
     * type.</dd>
     * <dt>Implement the {@link FxmlViewEventListener} or {@link FxmlViewControllerEventListener} interface</dt>
     * <dd>The {@link FxmlViewEventListener#onFxmlViewEvent(FxmlViewEvent)} method will be called with the event object as the parameter. If the event is a
     * {@link FxmlViewControllerEvent}, then the {@link FxmlViewControllerEventListener#onFxmlViewControllerEvent(FxmlViewControllerEvent)} method will be
     * called with the event object as the parameter.</dd>
     * </dl>
     *
     * @param <T> The type of root {@link Parent} for the view.
     * @param <U> The type of controller
     * @param target The object to fire the event on.
     * @param event The {@link FxmlViewEvent} to be fired.
     */
    public static <T extends Parent, U> void fireFxmlViewEvent(Object target, FxmlViewEvent<T> event) {
        if (event instanceof FxmlViewControllerEvent) {
            LOG.log(Level.FINE, () -> String.format("Firing FxmlViewControllerEvent %s for %s", event.getType().name(),
                    ((FxmlViewControllerEvent<T, U>) event).getController().getClass().getName()));
        } else {
            LOG.log(Level.FINE, () -> String.format("Firing FxmlViewEvent %s", event.getType().name()));
        }
        if (null == target) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        final FxmlViewEventType reason = event.getType();
        Iterator<Method> methods = getAnnotatedEventHandlerMethods(targetClass, HandlesFxmlViewEvent.class, eventClass, true, (t) -> {
            FxmlViewEventHandling h = t.value();
            LOG.log(Level.FINER, () -> String.format("Found annotation %s", h.name()));
            if (h == FxmlViewEventHandling.ANY) {
                LOG.log(Level.FINER, () -> "Returning true because annotation was for ANY");
                return true;
            }
            if (h.getType() == reason) {
                LOG.log(Level.FINER, () -> "Returning true because type matched");
                return true;
            }
            LOG.log(Level.FINER, () -> "Returning false");
            return false;
        }
        ).iterator();

        invokeEventMethods(target, methods, event);
        if (target instanceof FxmlViewControllerEventListener) {
            if (event instanceof FxmlViewControllerEvent) {
                try {
                    ((FxmlViewControllerEventListener<T, U>) target).onFxmlViewControllerEvent((FxmlViewControllerEvent<T, U>) event);
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "Error invoking interface implementation method", ex);
                }
            }
        } else if (target instanceof FxmlViewEventListener) {
            try {
                ((FxmlViewEventListener<T>) target).onFxmlViewEvent(event);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Error invoking interface implementation method", ex);
            }
        }
    }

    /**
     * Fires a {@link DataLoadedEvent} on a target object.
     * <p>
     * The target object can receive the event in two ways:</p>
     * <dl>
     * <dt>Annotate a method using {@link HandlesDataLoaded}</dt>
     * <dd>The annotated method must return void and can up to one parameter. If it defines the parameter, it must assignable from event object
     * type.</dd>
     * <dt>Implement the {@link DataLoadedEventListener} interface</dt>
     * <dd>The {@link DataLoadedEventListener#onDataLoaded(scheduler.view.event.DataLoadedEvent)} method will be called with the event object as the parameter</dd>
     * </dl>
     *
     * @param <T> The type of {@link DataAccessObject} for the {@link DataObjectEvent}.
     * @param target The object to fire the event on.
     * @param event The {@link DataObjectEvent} to be fired.
     */
    public static <T> void fireDataLoadedEvent(Object target, DataLoadedEvent<T> event) {
        LOG.log(Level.FINE, () -> (null == event.getSource()) ? "Firing DataLoadedEvent"
                : String.format("Firing DataLoadedEvent for %s", event.getSource().getClass().getName()));
        if (null == target) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        Iterator<Method> methods = getAnnotatedEventHandlerMethods(targetClass, HandlesDataLoaded.class, eventClass, true).iterator();
        invokeEventMethods(target, methods, event);
        if (target instanceof DataLoadedEventListener) {
            try {
                ((DataLoadedEventListener<T>) target).onDataLoaded(event);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Error invoking interface implementation method", ex);
            }
        }
    }
}
