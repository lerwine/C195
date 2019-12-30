package view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the name of the resource bundle that contains the localized string value for the annotated controller type.
 * This is used by classes derived from {@link scene.Controller}.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GlobalizationResource {
    /**
     * Gets the name of the resource for localized string values.
     * @return The name of the resource for localized string values.
     */
    public String value();
}
