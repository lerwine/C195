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
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
import scheduler.dao.DataAccessObject;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbTable;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventType;
import scheduler.view.annotations.DaoChangeType;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Utility class for getting annotated information.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class AnnotationHelper {

    private static final Logger LOG = Logger.getLogger(AnnotationHelper.class.getName());

    public static final String getGlobalizationResourceName(Class<?> target) {
        Class<GlobalizationResource> ac = GlobalizationResource.class;
        if (target.isAnnotationPresent(ac)) {
            String n = target.getAnnotation(ac).value();
            if (n != null && !n.trim().isEmpty()) {
                return n;
            }
            LOG.log(Level.WARNING, String.format("Value not defined for annotation %s in type %s",
                    ac.toGenericString(), target.getName()));
        } else {
            LOG.log(Level.WARNING, String.format("Annotation %s not present in type %s",
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
            LOG.log(Level.WARNING, String.format("Value not defined for annotation %s in type %s", ac.toGenericString(), target.getName()));
        } else {
            LOG.log(Level.WARNING, String.format("Annotation %s not present in type %s", ac.toGenericString(), target.getName()));
        }
        return "";
    }

    /**
     * Gets methods that have a specific annotation.
     * 
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param eventClass The type of {@link EventObject} to look for in the single parameter.
     * @param filter A {@link Predicate} that is used to filter the results or {@code null} to return all results.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedEventHandlerMethods(Class<?> targetClass, Class<T> annotationClass,
            Class<? extends EventObject> eventClass, Predicate<T> filter) {
        LOG.log(Level.FINE, String.format("Checking methods of %s ", targetClass.getName()));
        Stream.Builder<Method> builder = Stream.builder();
        do {
            for (Method m : targetClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(annotationClass)) {
                    LOG.log(Level.FINE, String.format("Method %s has annotation", m.getName()));
                    if (null == filter || filter.test(m.getAnnotation(annotationClass))) {
                        Class<?>[] parameters = m.getParameterTypes();
                        if (parameters.length != 1) {
                            LOG.log(Level.WARNING, String.format("Method %s uses the %s annotation, but has the wrong number arguments", m.toString(),
                                    annotationClass.getName()));
                        } else if (parameters[0].isAssignableFrom(eventClass)) {
                            builder.accept(m);
                        } else if (parameters[0].isAssignableFrom(FxmlViewEvent.class)) {
                            LOG.log(Level.FINE,
                                    String.format("Method %s uses the %s annotation, but was skipped because the argument is not assignable from %s",
                                            m.toString(), annotationClass.getName(), eventClass.getName()));
                        } else {
                            LOG.log(Level.WARNING,
                                    String.format("Method %s uses the %s annotation, but has the wrong type of argument", m.toString(),
                                            annotationClass.getName()));
                        }
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        } while (null != targetClass && !targetClass.equals(Object.class));
        return builder.build();
    }

    /**
     * Gets the {@code DbTable} from the {@link DatabaseTable} annotation of a given {@link Class}.
     * This is used by classes that inherit from {@link scheduler.dao.DataAccessObject} to specify the data table which the data access object represents.
     * 
     * @param target The target {@link Class}.
     * @return The {@code DbTable} value from the {@link Class}'s {@link DatabaseTable} or {@code null} if the annotation is not present.
     */
    public static final DbTable getDbTable(Class<?> target) {
        Class<DatabaseTable> ac = DatabaseTable.class;
        if (target.isAnnotationPresent(ac)) {
            return target.getAnnotation(ac).value();
        }
        return null;
    }

}
