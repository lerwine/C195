package model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the base name of the {@link java.util.ResourceBundle} to use for initializing fields.
 * If this annotates a class, then this will be the base name of the default {@link java.util.ResourceBundle} for all fields
 * annotated with {@link ResourceKey}.
 * If this annotates a field, then this will override the default {@link java.util.ResourceBundle} base name for the class.
 * {@link controller.ControllerBase#initialize(java.net.URL, java.util.ResourceBundle)} uses this annotation to initialize field values.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ResourceName {
    /**
     * The base name of the {@link java.util.ResourceBundle} to use for initializing fields.
     * @return
     */
    public String value();
}
