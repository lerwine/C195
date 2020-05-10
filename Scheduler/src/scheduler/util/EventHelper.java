package scheduler.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.dao.DataAccessObject;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
import static scheduler.util.AnnotationHelper.getAnnotatedEventHandlerMethods;
import scheduler.view.annotations.DaoChangeType;
import scheduler.view.annotations.HandlesDataLoaded;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.event.DataLoadedEvent;
import scheduler.view.event.DataLoadedEventListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Listener type.
 * @param <E> Event object type.
 */
public final class EventHelper<T, E extends EventObject> {

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

    private static Method getListenerMethod(Class<?> listenerType, String methodName, Class<?> eventType) {
        for (Method m : listenerType.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 1 && eventType.isAssignableFrom(p[0])) {
                    return m;
                }
            }
        }
        return null;
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
    @SuppressWarnings("unchecked")
    public static <T extends DataAccessObject> void fireDataObjectEvent(Object target, DataObjectEvent<T> event) {
        LOG.log(Level.FINE, () -> String.format("Firing DataObjectEvent %s on %s", event.getChangeAction().name(), event.getTarget().getClass().getName()));
        if (null == target) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        if (target instanceof DataObjectEventListener) {
            try {
                Method m = getListenerMethod(DataObjectEventListener.class, "onDataObjectEvent", event.getClass());
                if (null != m) {
                    m.invoke(target, event);
                } else {
                    ((DataObjectEventListener<T>) target).onDataObjectEvent(event);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.WARNING, "Error invoking interface implementation method", ex);
            }
        }
        final DaoChangeAction action = event.getChangeAction();
        Iterator<Method> methods = getAnnotatedEventHandlerMethods(targetClass, HandlesDataObjectEvent.class, eventClass, true, (t)
                -> t.type() == DaoChangeType.ANY || t.type().getChangeAction() == action).iterator();
        invokeEventMethods(target, methods, event);
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
     * <dd>The {@link DataLoadedEventListener#onDataLoaded(scheduler.view.event.DataLoadedEvent)} method will be called with the event object as the
     * parameter</dd>
     * </dl>
     *
     * @param <T> The type of {@link DataAccessObject} for the {@link DataObjectEvent}.
     * @param target The object to fire the event on.
     * @param event The {@link DataObjectEvent} to be fired.
     */
    @SuppressWarnings("unchecked")
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
                Method m = getListenerMethod(DataLoadedEventListener.class, "onDataLoaded", event.getClass());
                if (null != m) {
                    m.invoke(target, event);
                } else {
                    ((DataLoadedEventListener<T>) target).onDataLoaded(event);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.WARNING, "Error invoking interface implementation method", ex);
            }
        }
    }

    private final HashSet<T> listeners;
    private final String methodName;

    public EventHelper(String methodName) {
        this.listeners = new HashSet<>();
        this.methodName = methodName;
    }

    public synchronized void addListener(T listener) {
        if (null != listener && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(T listener) {
        if (null != listener && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void raiseEvent(E event) {
        Iterator<Object> iterator = Arrays.stream(listeners.toArray()).iterator();
        Class<?> eventType = event.getClass();
        while (iterator.hasNext()) {
            Object target = iterator.next();
            Method m = getListenerMethod(target.getClass(), methodName, eventType);
            if (null != m) {
                try {
                    m.invoke(target, event);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(EventHelper.class.getName()).log(Level.SEVERE, "Error invoking listener method", ex);
                }
            }
        }
    }

}
