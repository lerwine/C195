package scheduler.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbTable;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.event.FxmlViewEvent;

/**
 * Utility class for getting annotated information.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AnnotationHelper {

    private static final Logger LOG = Logger.getLogger(AnnotationHelper.class.getName());

    /**
     * Gets the base name of the {@link java.util.ResourceBundle} to be loaded along with the FXML resource for the specified {@link java.lang.Class}.
     * <p>
     * This value is specified using the {@link GlobalizationResource} annotation.</p>
     *
     * @param target The {@link java.lang.Class} for the target controller.
     * @return The base name of the {@link java.util.ResourceBundle} associated with the {@code target} controller or an empty string if the
     * {@link java.util.ResourceBundle} is not specified in a {@link GlobalizationResource} annotation.
     */
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
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}.
     * <p>
     * This value is specified using the {@link FXMLResource} annotation.</p>
     *
     * @param target The {@link java.lang.Class} for the target controller.
     * @return The name of the FXML resource associated with the target controller or an empty string if the FXML resource is not specified in an
     * {@link FXMLResource} annotation.
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
     * @param allowZeroLengthParameters {@code true} to accept methods with zero-length parameters; otherwise {@code false} to require exactly 1
     * parameter.
     * @param filter A {@link Predicate} that is used to filter the results or {@code null} to return all results.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedEventHandlerMethods(Class<?> targetClass, Class<T> annotationClass,
            Class<? extends EventObject> eventClass, boolean allowZeroLengthParameters, Predicate<T> filter) {
        LOG.log(Level.FINE, String.format("Checking methods of %s ", targetClass.getName()));
        Stream.Builder<Method> builder = Stream.builder();
        do {
            for (Method m : targetClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(annotationClass)) {
                    LOG.log(Level.FINE, String.format("Method %s has annotation", m.getName()));
                    if (null == filter || filter.test(m.getAnnotation(annotationClass))) {
                        Class<?>[] parameters = m.getParameterTypes();
                        if ((allowZeroLengthParameters) ? parameters.length > 1 : parameters.length != 1) {
                            LOG.log(Level.WARNING, String.format("Method %s uses the %s annotation, but has the wrong number arguments", m.toString(),
                                    annotationClass.getName()));
                        } else if (parameters.length == 0 || parameters[0].isAssignableFrom(eventClass)) {
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
        return getAnnotatedEventHandlerMethods(targetClass, annotationClass, eventClass, false, filter);
    }

    /**
     * Gets methods that have a specific annotation.
     *
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param eventClass The type of {@link EventObject} to look for in the single parameter.
     * @param allowZeroLengthParameters {@code true} to accept methods with zero-length parameters; otherwise {@code false} to require exactly 1
     * parameter.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedEventHandlerMethods(Class<?> targetClass, Class<T> annotationClass,
            Class<? extends EventObject> eventClass, boolean allowZeroLengthParameters) {
        return getAnnotatedEventHandlerMethods(targetClass, annotationClass, eventClass, allowZeroLengthParameters, null);
    }

    /**
     * Gets methods that have a specific annotation.
     *
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param eventClass The type of {@link EventObject} to look for in the single parameter.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedEventHandlerMethods(Class<?> targetClass, Class<T> annotationClass,
            Class<? extends EventObject> eventClass) {
        return getAnnotatedEventHandlerMethods(targetClass, annotationClass, eventClass, null);
    }

    /**
     * Gets the {@code DbTable} from the {@link DatabaseTable} annotation of a given {@link Class}. This is used by classes that inherit from
     * {@link scheduler.dao.DataAccessObject} to specify the data table which the data access object represents.
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

    public static void injectModelEditorField(Object value, String name, Object target) throws IllegalAccessException {
        Class<?> fieldType = value.getClass();
        Class<ModelEditor> a = ModelEditor.class;
        for (Field f : target.getClass().getDeclaredFields()) {
            if (f.getName().equals(name) && f.isAnnotationPresent(a) && f.getType().isAssignableFrom(fieldType)) {
                boolean accessible = f.isAccessible();
                try {
                    if (!accessible) {
                        f.setAccessible(true);
                    }
                    f.set(target, value);
                } finally {
                    if (!accessible) {
                        f.setAccessible(false);
                    }
                }
                break;
            }
        }
    }
}
