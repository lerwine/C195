package scheduler.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Base class for controllers. Derived classes must be annotated by {@link scheduler.view.annotations.FXMLResource} to specify the name of the FXML
 * resource to be associated with the current controller, and by {@link scheduler.view.annotations.GlobalizationResource} to specify the resource
 * bundle to load with the target FXML resource.
 *
 * @author Leonard T. Erwine
 */
public abstract class SchedulerController implements ISchedulerController {

    private static final Logger LOG = Logger.getLogger(SchedulerController.class.getName());

    /**
     * Loads a view and controller. The path of the view to load is identified by the {@link FXMLResource} annotation on the {@code controllerClass}.
     * The {@link ResourceBundle} loaded with the controller is identified by the {@link GlobalizationResource} annotation on the
     * {@code controllerClass}.
     *
     * @param <V> The type of {@link Node} that represents the view.
     * @param <C> The type of {@link SchedulerController}.
     * @param stage The {@link Stage} that will contain the view.
     * @param controllerClass The {@link Class} of the {@link SchedulerController} to instantiate.
     * @param onLoaded This gets called after the view is loaded and the controller is instantiated.
     * @param show This gets called to insert the view into the {@link Stage}.
     * @param baseResourceClass The {@link Class} to use for loading the base {@link ResourceBundle} that will be merged with the
     * {@link ResourceBundle} identified by the {@link GlobalizationResource} annotation on the {@code controllerClass}.
     * @return The instantiated and initialized {@link SchedulerController}.
     * @throws IOException if not able to load the view.
     */
    public static <V extends Node, C extends SchedulerController> C load(Stage stage, Class<C> controllerClass, BiConsumer<V, C> onLoaded,
            BiConsumer<V, C> show, Class<?> baseResourceClass) throws IOException {
        Objects.requireNonNull(stage);
        Objects.requireNonNull(show);
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(AppResources.getFXMLResourceName(controllerClass)),
                (null == baseResourceClass) ? ResourceBundleLoader.getBundle(controllerClass)
                        : ResourceBundleLoader.getMergedBundle(controllerClass, baseResourceClass));
        V view = loader.load();
        C controller = loader.getController();
        controller.onLoaded(view);
        if (null != onLoaded) {
            onLoaded.accept(view, controller);
        }
        controller.onBeforeShow(view, stage);
        show.accept(view, controller);
        return controller;
    }

    /**
     * Loads a view and controller. The path of the view to load is identified by the {@link FXMLResource} annotation on the {@code controllerClass}.
     * The {@link ResourceBundle} loaded with the controller is identified by the {@link GlobalizationResource} annotation on the
     * {@code controllerClass}.
     *
     * @param <V> The type of {@link Node} that represents the view.
     * @param <C> The type of {@link SchedulerController}.
     * @param stage The {@link Stage} that will contain the view.
     * @param controllerClass The {@link Class} of the {@link SchedulerController} to instantiate.
     * @param onLoaded This gets called after the view is loaded and the controller is instantiated.
     * @param show This gets called to insert the view into the {@link Stage}.
     * @return The instantiated and initialized {@link SchedulerController}.
     * @throws IOException if not able to load the view.
     */
    public static <V extends Node, C extends SchedulerController> C load(Stage stage, Class<C> controllerClass, BiConsumer<V, C> onLoaded,
            BiConsumer<V, C> show) throws IOException {
        return load(stage, controllerClass, onLoaded, show, null);
    }

    /**
     * Loads a view and controller. The path of the view to load is identified by the {@link FXMLResource} annotation on the {@code controllerClass}.
     * The {@link ResourceBundle} loaded with the controller is identified by the {@link GlobalizationResource} annotation on the
     * {@code controllerClass}.
     *
     * @param <V> The type of {@link Node} that represents the view.
     * @param <C> The type of {@link SchedulerController}.
     * @param stage The {@link Stage} that will contain the view.
     * @param controllerClass The {@link Class} of the {@link SchedulerController} to instantiate.
     * @param show This gets called to insert the view into the {@link Stage}.
     * @return The instantiated and initialized {@link SchedulerController}.
     * @throws IOException if not able to load the view.
     */
    public static <V extends Node, C extends SchedulerController> C load(Stage stage, Class<C> controllerClass,
            BiConsumer<V, C> show) throws IOException {
        return load(stage, controllerClass, null, show);
    }

    /**
     * Loads a view and controller. The path of the view to load is identified by the {@link FXMLResource} annotation on the {@code controllerClass}.
     * The {@link ResourceBundle} loaded with the controller is identified by the {@link GlobalizationResource} annotation on the
     * {@code controllerClass}.
     *
     * @param <V> The type of {@link Node} that represents the view.
     * @param <C> The type of {@link SchedulerController}.
     * @param stage The {@link Stage} that will contain the view.
     * @param controllerClass The {@link Class} of the {@link SchedulerController} to instantiate.
     * @param show This gets called to insert the view into the {@link Stage}.
     * @param baseResourceClass The {@link Class} to use for loading the base {@link ResourceBundle} that will be merged with the
     * {@link ResourceBundle} identified by the {@link GlobalizationResource} annotation on the {@code controllerClass}.
     * @return The instantiated and initialized {@link SchedulerController}.
     * @throws IOException if not able to load the view.
     */
    public static <V extends Node, C extends SchedulerController> C load(Stage stage, Class<C> controllerClass,
            BiConsumer<V, C> show, Class<?> baseResourceClass) throws IOException {
        return load(stage, controllerClass, null, show, baseResourceClass);
    }

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

    /**
     * This gets called after view is loaded and the controller is initialized by the {@link FXMLLoader}.
     *
     * @param view The root {@link Node} representing the view.
     */
    protected void onLoaded(Node view) {
    }

    /**
     * This gets called before the dialog window is shown.
     *
     * @param currentView The current view.
     * @param stage The {@link Stage} representing the current window.
     */
    protected void onBeforeShow(Node currentView, Stage stage) {
    }

    /**
     * This gets called after the view is removed from the hierarchy of the current {@link javafx.scene.Scene} or the current window is closed.
     *
     * @param view The {@link Node} representing the view that was unloaded.
     */
    protected void onUnloaded(Node view) {
    }

}
