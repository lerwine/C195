package scheduler.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the name of the resource that contains the FXML markup associated with the annotated controller. This annotation is utilized by the
 * {@link scheduler.util.ViewControllerLoader} to determine which FXML resource to load in order to load the appropriate view and instantiate the controller. The
 * {@link GlobalizationResource} annotation specifies which resource bundle to load along with the FXML.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FXMLResource {

    /**
     * Gets the name of the resource that contains the FXML markup.
     *
     * @return The name of the resource that contains the FXML markup for the view associated with the controller class.
     */
    public String value();
}
