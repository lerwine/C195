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

/**
 *
 * @author lerwi
 */
public class EventHelper {

    private static final Logger LOG = Logger.getLogger(EventHelper.class.getName());
    
    private static void invokeEventMethods(Object target, Iterator<Method> methodIterator, EventObject event) {
        if (!methodIterator.hasNext()) {
            return;
        }
        Method method = methodIterator.next();
        try {
            boolean wasAccessible = method.isAccessible();
            if (!wasAccessible) {
                method.setAccessible(true);
            }
            try {
                method.invoke(target, new Object[]{event});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, String.format("Error invoking %s", method.toGenericString()), ex);
            } finally {
                if (!wasAccessible) {
                    method.setAccessible(false);
                }
            }
        } finally {
            invokeEventMethods(target, methodIterator, event);
        }
    }

    public static <T extends DataAccessObject> void invokeDataObjectEventMethods(Object target, DataObjectEvent<T> event) {
        LOG.log(Level.INFO, String.format("Firing DataObjectEvent %s on %s", event.getChangeAction().name(),
                event.getDataObject().getClass().getName()));
        if (null == target) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        try {
            if (target instanceof DataObjectEventListener) {
                try {
                    Method method = targetClass.getMethod("onDataObjectEvent", eventClass);
                    if (method.getParameterTypes()[0].isAssignableFrom(eventClass)) {
                        ((DataObjectEventListener<T>) target).onDataObjectEvent(event);
                    }
                } catch (NoSuchMethodException | SecurityException ex) {
                    LOG.log(Level.SEVERE, "Error getting interface implementation method", ex);
                }
            }
        } finally {
            final DaoChangeAction action = event.getChangeAction();
            Iterator<Method> methods = getAnnotatedEventHandlerMethods(targetClass, HandlesDataObjectEvent.class, eventClass, (t)
                    -> t.type() == DaoChangeType.ANY || t.type().getChangeAction() == action).iterator();
            invokeEventMethods(target, methods, event);
        }
    }

    public static <T extends Parent, U> void invokeViewLifecycleEventMethods(Object target, FxmlViewEvent<T> event) {
        if (event instanceof FxmlViewControllerEvent) {
            LOG.log(Level.INFO, String.format("Firing FxmlViewControllerEvent %s for %s", event.getType().name(),
                    ((FxmlViewControllerEvent<T, U>)event).getController().getClass().getName()));
        } else {
            LOG.log(Level.INFO, String.format("Firing FxmlViewEvent %s", event.getType().name()));
        }
        if (null == target) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        try {
            final FxmlViewEventType reason = event.getType();
            Iterator<Method> methods = getAnnotatedEventHandlerMethods(targetClass, HandlesFxmlViewEvent.class, eventClass, (t) -> {
                FxmlViewEventHandling h = t.value();
                LOG.log(Level.FINE, String.format("Found annotation %s", h.name()));
                if (h == FxmlViewEventHandling.ANY) {
                    LOG.log(Level.FINE, "Returning true because annotation was for ANY");
                    return true;
                }
                if (h.getType() == reason) {
                    LOG.log(Level.FINE, "Returning true because type matched");
                    return true;
                }
                LOG.log(Level.FINE, "Returning false");
                return false;
            }
            ).iterator();
            invokeEventMethods(target, methods, event);
        } finally {
            if (target instanceof FxmlViewControllerEventListener) {
                if (event instanceof FxmlViewControllerEvent) {
                    try {
                        Method method = targetClass.getMethod("onFxmlViewControllerEventListener", eventClass);
                        if (method.getParameterTypes()[0].isAssignableFrom(eventClass)) {
                            ((FxmlViewControllerEventListener<T, U>) target).onFxmlViewControllerEvent((FxmlViewControllerEvent<T, U>) event);
                        }
                    } catch (NoSuchMethodException | SecurityException ex) {
                        LOG.log(Level.SEVERE, "Error getting interface implementation method", ex);
                    }
                }
            } else if (target instanceof FxmlViewEventListener) {
                try {
                    Method method = targetClass.getMethod("onFxmlViewEvent", eventClass);
                    if (method.getParameterTypes()[0].isAssignableFrom(eventClass)) {
                        ((FxmlViewEventListener<T>) target).onFxmlViewEvent(event);
                    }
                } catch (NoSuchMethodException | SecurityException ex) {
                    LOG.log(Level.SEVERE, "Error getting interface implementation method", ex);
                }
            }
        }
    }

}
