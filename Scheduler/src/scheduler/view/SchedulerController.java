package scheduler.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

/**
 * Base class for controllers. Derived classes must be annotated by {@link scheduler.view.annotations.FXMLResource} to specify the name of the FXML
 * resource to be associated with the current controller, and by {@link scheduler.view.annotations.GlobalizationResource} to specify the resource
 bundle to loadViewAndController with the target FXML resource.
 *
 * @author Leonard T. Erwine
 */
public abstract class SchedulerController implements ISchedulerController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    protected ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    /**
     * Gets the {@link java.net.URL} injected by the {@link javafx.fxml.FXMLLoader}.
     *
     * @return The {@link java.net.URL} injected by the {@link javafx.fxml.FXMLLoader}.
     */
    @Override
    public final URL getLocation() {
        return location;
    }

    /**
     * Gets the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     *
     * @return The {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     */
    @Override
    public final ResourceBundle getResources() {
        return resources;
    }

}
