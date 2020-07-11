package scheduler.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbTable;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

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
     * @param filter A {@link Predicate} that is used to filter the results.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethods(Class<?> targetClass, Class<T> annotationClass,
            BiPredicate<Method, T> filter) {
        if (null == filter) {
            return getAnnotatedInstanceMethods(targetClass, annotationClass);
        }
        LOG.fine(String.format("Checking methods of %s ", targetClass.getName()));
        Stream.Builder<Method> builder = Stream.builder();
        do {
            for (Method m : targetClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(annotationClass)) {
                    LOG.fine(() -> String.format("Method %s has annotation", m.getName()));
                    final int memberModifiers = m.getModifiers();
                    if ((memberModifiers & (Modifier.STATIC | Modifier.NATIVE)) == 0 && filter.test(m, m.getAnnotation(annotationClass))) {
                        builder.accept(m);
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
     * @param filter A {@link Predicate} that is used to filter the results
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethods(Class<?> targetClass, Class<T> annotationClass,
            Predicate<T> filter) {
        if (null == filter) {
            return getAnnotatedInstanceMethods(targetClass, annotationClass);
        }
        LOG.fine(String.format("Checking methods of %s ", targetClass.getName()));
        Stream.Builder<Method> builder = Stream.builder();
        do {
            for (Method m : targetClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(annotationClass)) {
                    LOG.fine(() -> String.format("Method %s has annotation", m.getName()));
                    final int memberModifiers = m.getModifiers();
                    if ((memberModifiers & (Modifier.STATIC | Modifier.NATIVE)) == 0 && filter.test(m.getAnnotation(annotationClass))) {
                        builder.accept(m);
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
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethods(Class<?> targetClass, Class<T> annotationClass) {
        LOG.fine(String.format("Checking methods of %s ", targetClass.getName()));
        Stream.Builder<Method> builder = Stream.builder();
        do {
            for (Method m : targetClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(annotationClass)) {
                    LOG.fine(() -> String.format("Method %s has annotation", m.getName()));
                    final int memberModifiers = m.getModifiers();
                    if ((memberModifiers & (Modifier.STATIC | Modifier.NATIVE)) == 0) {
                        builder.accept(m);
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
     * @param returnType The method return type.
     * @param methodName The name of the method.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethods(Class<?> targetClass, Class<T> annotationClass,
            Class<?> returnType, String methodName) {
        if (null == returnType) {
            if (null == methodName) {
                return getAnnotatedInstanceMethods(targetClass, annotationClass);
            }
            return getAnnotatedInstanceMethods(targetClass, annotationClass, (m, a) -> m.getName().equals(methodName));
        }
        if (null == methodName) {
            return getAnnotatedInstanceMethods(targetClass, annotationClass, (m, a) -> returnType.isAssignableFrom(m.getReturnType()));
        }
        return getAnnotatedInstanceMethods(targetClass, annotationClass,
                (m, a) -> m.getName().equals(methodName) && returnType.isAssignableFrom(m.getReturnType()));
    }

    /**
     * Gets methods that have a specific annotation.
     *
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param methodName The name of the method.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethods(Class<?> targetClass, Class<T> annotationClass,
            String methodName) {
        return getAnnotatedInstanceMethods(targetClass, annotationClass, null, methodName);
    }

    /**
     * Gets methods that have a specific annotation.
     *
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param returnType The method return type.
     * @param methodName The name of the method.
     * @param parameterTypes The method parameter types.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethodsByNameAndParameter(Class<?> targetClass, Class<T> annotationClass,
            Class<?> returnType, String methodName, Class<?>... parameterTypes) {
        if (null == parameterTypes || parameterTypes.length == 0) {
            if (null == returnType) {
                if (null == methodName) {
                    return getAnnotatedInstanceMethods(targetClass, annotationClass,
                            (m, a) -> m.getParameters().length == 0);
                }
                return getAnnotatedInstanceMethods(targetClass, annotationClass,
                        (m, a) -> m.getName().equals(methodName) && m.getParameters().length == 0);
            }
            if (null == methodName) {
                return getAnnotatedInstanceMethods(targetClass, annotationClass,
                        (m, a) -> m.getName().equals(methodName) && m.getParameters().length == 0 && returnType.isAssignableFrom(m.getReturnType()));
            }
            return getAnnotatedInstanceMethods(targetClass, annotationClass,
                    (m, a) -> m.getParameters().length == 0 && returnType.isAssignableFrom(m.getReturnType()));
        }
        int len = parameterTypes.length;
        if (Arrays.stream(parameterTypes).anyMatch((t) -> null != t)) {
            if (null == returnType) {
                if (null == methodName) {
                    return getAnnotatedInstanceMethods(targetClass, annotationClass,
                            (m, a) -> {
                                Parameter[] parameters = m.getParameters();
                                if (parameters.length != len) {
                                    return false;
                                }
                                for (int i = 0; i < len; i++) {
                                    Class<?> pt = parameterTypes[i];
                                    if (null != pt && !parameters[i].getType().isAssignableFrom(pt)) {
                                        return false;
                                    }
                                }
                                return true;
                            });
                }
                return getAnnotatedInstanceMethods(targetClass, annotationClass,
                        (m, a) -> {
                            if (!m.getName().equals(methodName)) {
                                return false;
                            }
                            Parameter[] parameters = m.getParameters();
                            if (parameters.length != len) {
                                return false;
                            }
                            for (int i = 0; i < len; i++) {
                                Class<?> pt = parameterTypes[i];
                                if (null != pt && !parameters[i].getType().isAssignableFrom(pt)) {
                                    return false;
                                }
                            }
                            return true;
                        });
            }
            if (null == methodName) {
                return getAnnotatedInstanceMethods(targetClass, annotationClass,
                        (m, a) -> {
                            Parameter[] parameters = m.getParameters();
                            if (parameters.length != len || !returnType.isAssignableFrom(m.getReturnType())) {
                                return false;
                            }
                            for (int i = 0; i < len; i++) {
                                Class<?> pt = parameterTypes[i];
                                if (null != pt && !parameters[i].getType().isAssignableFrom(pt)) {
                                    return false;
                                }
                            }
                            return true;
                        });
            }
            return getAnnotatedInstanceMethods(targetClass, annotationClass,
                    (m, a) -> {
                        if (!m.getName().equals(methodName)) {
                            return false;
                        }
                        Parameter[] parameters = m.getParameters();
                        if (parameters.length != len || !returnType.isAssignableFrom(m.getReturnType())) {
                            return false;
                        }
                        for (int i = 0; i < len; i++) {
                            Class<?> pt = parameterTypes[i];
                            if (null != pt && !parameters[i].getType().isAssignableFrom(pt)) {
                                return false;
                            }
                        }
                        return true;
                    });
        }
        if (null == returnType) {
            if (null == methodName) {
                return getAnnotatedInstanceMethods(targetClass, annotationClass,
                        (m, a) -> m.getParameters().length == len);
            }
            return getAnnotatedInstanceMethods(targetClass, annotationClass,
                    (m, a) -> m.getName().equals(methodName) && m.getParameters().length == len);
        }
        if (null == methodName) {
            return getAnnotatedInstanceMethods(targetClass, annotationClass,
                    (m, a) -> m.getParameters().length == len && returnType.isAssignableFrom(m.getReturnType()));
        }
        return getAnnotatedInstanceMethods(targetClass, annotationClass,
                (m, a) -> m.getName().equals(methodName) && m.getParameters().length == len && returnType.isAssignableFrom(m.getReturnType()));
    }

    /**
     * Gets methods that have a specific annotation.
     *
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param methodName The name of the method.
     * @param parameterTypes The method parameter types.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethodsByNameAndParameter(Class<?> targetClass, Class<T> annotationClass,
            String methodName, Class<?>... parameterTypes) {
        return getAnnotatedInstanceMethodsByNameAndParameter(targetClass, annotationClass, null, methodName, parameterTypes);
    }

    /**
     * Gets methods that have a specific annotation.
     *
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param returnType The method return type or {@code null} for any return type.
     * @param parameterTypes The method parameter types.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethodsByReturnAndParameter(Class<?> targetClass, Class<T> annotationClass,
            Class<?> returnType, Class<?>... parameterTypes) {
        return getAnnotatedInstanceMethodsByNameAndParameter(targetClass, annotationClass, returnType, null, parameterTypes);
    }

    /**
     * Gets methods that have a specific annotation.
     *
     * @param <T> The type of {@link Annotation}.
     * @param targetClass The class to search for annotated methods.
     * @param annotationClass The type of {@link Annotation} to look for.
     * @param parameterTypes The method parameter types.
     * @return A {@link Stream} of {@link Method} objects.
     */
    public static <T extends Annotation> Stream<Method> getAnnotatedInstanceMethodsByParameter(Class<?> targetClass, Class<T> annotationClass,
            Class<?>... parameterTypes) {
        return getAnnotatedInstanceMethodsByNameAndParameter(targetClass, annotationClass, null, null, parameterTypes);
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

    public static <T extends Annotation> boolean tryInjectField(Object target, Class<T> annotationClass, String fieldName, Object value) {
        if (null == fieldName) {
            return false;
        }
        try {
            return injectField(target, annotationClass, fieldName, value) > 0;
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, String.format("Unexpected exception setting value of field %s", fieldName), ex);
            return false;
        }
    }

    public static <T extends Annotation> int injectField(Object target, Class<T> annotationClass, String fieldName, Object value) throws IllegalAccessException {
        int result = 0;
        for (Field f : target.getClass().getDeclaredFields()) {
            if (f.getName().equals(fieldName) && f.isAnnotationPresent(annotationClass)) {
                final int memberModifiers = f.getModifiers();
                if ((memberModifiers & (Modifier.STATIC | Modifier.FINAL)) == 0) {
                    boolean accessible = f.isAccessible();
                    try {
                        if (!accessible) {
                            f.setAccessible(true);
                        }
                        f.set(target, value);
                        result++;
                    } finally {
                        if (!accessible) {
                            f.setAccessible(false);
                        }
                    }
                }
            }
        }
        return result;
    }

}
