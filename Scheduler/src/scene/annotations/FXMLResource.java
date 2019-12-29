package scene.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the name of the resource that contains the FXML markup associated with the annotated controller.
 * This is used by classes derived from {@link scene.Controller}.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FXMLResource {
    /**
     * Gets the name of the resource that contains the FXML markup.
     * @return The name of the resource that contains the FXML markup.
     */
    public String value();
}
