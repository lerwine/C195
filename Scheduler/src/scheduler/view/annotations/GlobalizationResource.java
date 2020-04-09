package scheduler.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the name of the resource bundle that contains the localized string values for the annotated controller type.
 * This annotation is utilized by the {@link scheduler.util.ViewControllerLoader} to determine which {@link java.util.ResourceBundle} to load along with the FXML resource
 * specified by the {@link FXMLResource} annotation.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GlobalizationResource {

    /**
     * Gets the name of the resource for localized string values.
     *
     * @return The name of the resource for localized string values that is loaded along with the {@link FXMLResource}.
     */
    public String value();
}
