package model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the name of the {@link java.util.ResourceBundle} key to use for initializing the annotated field.
 * {@link controller.ControllerBase#initialize(java.net.URL, java.util.ResourceBundle)} uses this annotation to initialize field values.
 * The base name of the {@link java.util.ResourceBundle} is specified by the {@link ResourceName} annotation.
 * If neither the annotated field nor the associated class are annotated with {@link ResourceName}, then this annotation will have no effect.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResourceKey {
    /**
     * The name of the {@link java.util.ResourceBundle} key to use for initializing the annotated field.
     * @return
     */
    public String value();
}
