package scheduler.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Initializes a JavaFX controller.
 * @author erwinel
 * @param <C> The JavaFX controller type.
 */
public interface ViewControllerInitializer<C extends SchedulerController> {
    
    /**
     * This gets called before the JavaFX controller is created and the FXML view is loaded.
     * @param loader The {@link FXMLLoader} that will be used to load the FXML view and JavaFX controller.
     */
    default void beforeLoad(FXMLLoader loader) { }

    /**
     * This gets called after the JavaFX controller is created and the FXML view is loaded, but before it is added to the
     * target {@link javafx.stage.Stage}.
     * @param newController The newly instantiated JavaFX controller for the newly loaded FXML view.
     * @param newView The newly loaded FXML view.
     * @param currentController The JavaFX controller for the FXML view that will be replaced by the new on or {@code null} if this controller will be
     * the for the initial view.
     * @param currentView he FXML view that will be replaced by the new one or {@code null} if this will be the initial view.
     */
    default void onLoaded(C newController, Parent newView, SchedulerController currentController, Parent currentView) { }

    /**
     * This gets called after the FXML view is added to the target {@link javafx.stage.Stage}.
     * @param currentController The JavaFX controller FXML view that was added to the target {@link javafx.stage.Stage}.
     * @param currentView The FXML view that was added to the target {@link javafx.stage.Stage}.
     * @param oldController The controller for the FXML view that was replaced by the current view in its target {@link javafx.stage.Stage} or
     * {@code null} if this was the initial view.
     * @param oldView The FXML view that was replaced by the current view in its target {@link javafx.stage.Stage} or {@code null} if this was
     * the initial view.
     */
    default void onApplied(C currentController, Parent currentView, SchedulerController oldController, Parent oldView) { }
    
}
