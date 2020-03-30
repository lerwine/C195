/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.scene.Parent;
import scheduler.dao.DaoChangeAction;
import scheduler.dao.DataObjectEvent;
import scheduler.dao.DataObjectEventListener;
import scheduler.dao.DataObjectImpl;
import scheduler.view.ViewControllerLifecycleEvent;
import scheduler.view.ViewControllerLifecycleEventListener;
import scheduler.view.ViewLifecycleEvent;
import scheduler.view.ViewLifecycleEventListener;
import scheduler.view.ViewLifecycleEventReason;
import scheduler.view.annotations.DaoChangeType;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.annotations.HandlesViewLifecycleEvent;
import scheduler.view.annotations.ViewLifecycleEventType;

/**
 *
 * @author lerwi
 */
public class AnnotationHelper {

    public static final String getGlobalizationResourceName(Class<?> target) {
        Class<GlobalizationResource> ac = GlobalizationResource.class;
        if (target.isAnnotationPresent(ac)) {
            String n = target.getAnnotation(ac).value();
            if (n != null && !n.trim().isEmpty()) {
                return n;
            }
            Logger.getLogger(AnnotationHelper.class.getName()).log(Level.WARNING, String.format("Value not defined for annotation %s in type %s",
                    ac.toGenericString(), target.getName()));
        } else {
            Logger.getLogger(AnnotationHelper.class.getName()).log(Level.WARNING, String.format("Annotation %s not present in type %s",
                    ac.toGenericString(), target.getName()));
        }
        return "";
    }

    /**
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}. This value is specified using the
     * {@link FXMLResource} annotation.
     *
     * @param target The {@link java.lang.Class} for the target controller.
     * @return The name of the FXML resource associated with the target controller or null if resource name is not specified.
     */
    public static final String getFXMLResourceName(Class<?> target) {
        Class<FXMLResource> ac = FXMLResource.class;
        if (target.isAnnotationPresent(ac)) {
            String n = target.getAnnotation(ac).value();
            if (n != null && !n.trim().isEmpty()) {
                return n;
            }
            Logger.getLogger(AnnotationHelper.class.getName()).log(Level.WARNING, String.format("Value not defined for annotation %s in type %s",
                    ac.toGenericString(), target.getName()));
        } else {
            Logger.getLogger(AnnotationHelper.class.getName()).log(Level.WARNING, String.format("Annotation %s not present in type %s",
                    ac.toGenericString(), target.getName()));
        }
        return "";
    }

    private static <T extends Annotation> Stream<Method> getAnnotatedMethods(Class<?> targetClass, Class<T> annotationClass,
            Class<? extends EventObject> eventClass, Predicate<T> filter) {
        Stream.Builder<Method> builder = Stream.builder();
        do {
            for (Method m : targetClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(annotationClass) && (null == filter || filter.test(m.getAnnotation(annotationClass)))) {
                    Class<?>[] parameters = m.getParameterTypes();
                    if (parameters.length != 1) {
                        Logger.getLogger(AnnotationHelper.class.getName()).log(Level.WARNING,
                                String.format("Method %s uses the %s annotation, but has the wrong number arguments", m.toString(),
                                        annotationClass.getName()));
                    } else if (parameters[0].isAssignableFrom(eventClass))
                        builder.accept(m);
                    else if (parameters[0].isAssignableFrom(ViewLifecycleEvent.class))
                        Logger.getLogger(AnnotationHelper.class.getName()).log(Level.INFO,
                                String.format("Method %s uses the %s annotation, but was skipped because the argument is not assignable from %s",
                                        m.toString(), annotationClass.getName(), eventClass.getName()));
                    else
                        Logger.getLogger(AnnotationHelper.class.getName()).log(Level.WARNING,
                                String.format("Method %s uses the %s annotation, but has the wrong type of argument", m.toString(),
                                        annotationClass.getName()));
                }
            }
            targetClass = targetClass.getSuperclass();
        } while (null != targetClass && !targetClass.equals(Object.class));
        return builder.build();
    }

    private static void invokeEventMethods(Object target, Iterator<Method> methodIterator, EventObject event) {
        if (!methodIterator.hasNext())
            return;
        Method method = methodIterator.next();
        try {
            boolean wasAccessible = method.isAccessible();
            if (!wasAccessible) {
                method.setAccessible(true);
            }
            try {
                method.invoke(target, new Object[] { event });
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(AnnotationHelper.class.getName()).log(Level.SEVERE, String.format("Error invoking %s", method.toGenericString()), ex);
            } finally {
                if (!wasAccessible) {
                    method.setAccessible(false);
                }
            }
        } finally {
            invokeEventMethods(target, methodIterator, event);
        }
    }
    
    public static void invokeDataObjectEventMethods(Object target, DataObjectEvent<? extends DataObjectImpl> event) {
        if (null == target)
            return;
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        try {
            if (target instanceof DataObjectEventListener) {
                try {
                    Method method = targetClass.getMethod("onDataObjectEvent", eventClass);
                    if (method.getParameterTypes()[0].isAssignableFrom(eventClass))
                        ((DataObjectEventListener)target).onDataObjectEvent(event);
                } catch (NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(AnnotationHelper.class.getName()).log(Level.SEVERE, "Error getting interface implementation method", ex);
                }
            }
        } finally {
            final DaoChangeAction action = event.getChangeAction();
            Iterator<Method> methods = getAnnotatedMethods(targetClass, HandlesDataObjectEvent.class, eventClass, (t) ->
                t.type() == DaoChangeType.ANY || t.type().getChangeAction() == action).iterator();
            invokeEventMethods(target, methods, event);
        }
    }

    public static void invokeViewLifecycleEventMethods(Object target, ViewLifecycleEvent<? extends Parent> event) {
        if (null == target)
            return;
        Class<?> targetClass = target.getClass();
        Class<? extends EventObject> eventClass = event.getClass();
        try {
            if (target instanceof ViewControllerLifecycleEventListener) {
                if (event instanceof ViewControllerLifecycleEvent) {
                    try {
                        Method method = targetClass.getMethod("onViewControllerLifecycleEvent", eventClass);
                        if (method.getParameterTypes()[0].isAssignableFrom(eventClass))
                            ((ViewControllerLifecycleEventListener)target).onViewControllerLifecycleEvent((ViewControllerLifecycleEvent)event);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        Logger.getLogger(AnnotationHelper.class.getName()).log(Level.SEVERE, "Error getting interface implementation method", ex);
                    }
                }
            } else if (target instanceof ViewLifecycleEventListener) {
                try {
                    Method method = targetClass.getMethod("onViewLifecycleEvent", eventClass);
                    if (method.getParameterTypes()[0].isAssignableFrom(eventClass))
                        ((ViewLifecycleEventListener)target).onViewLifecycleEvent(event);
                } catch (NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(AnnotationHelper.class.getName()).log(Level.SEVERE, "Error getting interface implementation method", ex);
                }
            }
        } finally {
            final ViewLifecycleEventReason reason = event.getReason();
            Iterator<Method> methods = getAnnotatedMethods(targetClass, HandlesViewLifecycleEvent.class, eventClass, (t) ->
                t.type() == ViewLifecycleEventType.ANY || t.type().getReason() == reason).iterator();
            invokeEventMethods(target, methods, event);
        }
    }

}
