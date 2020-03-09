package scheduler.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Base class for controllers. Derived classes must be annotated by {@link scheduler.view.annotations.FXMLResource} to specify the name of the FXML resource to be associated with
 * the current controller, and by {@link scheduler.view.annotations.GlobalizationResource} to specify the resource bundle to load with the target FXML resource.
 *
 * @author Leonard T. Erwine
 */
public abstract class SchedulerController {

    private static final Logger LOG = Logger.getLogger(SchedulerController.class.getName());

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    /**
     * Gets the {@link java.net.URL} injected by the {@link javafx.fxml.FXMLLoader}.
     *
     * @return The {@link java.net.URL} injected by the {@link javafx.fxml.FXMLLoader}.
     */
    protected final URL getLocation() {
        return location;
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    protected ResourceBundle resources;

    /**
     * Gets the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     *
     * @return The {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     */
    protected final ResourceBundle getResources() {
        return resources;
    }

    /**
     * Gets a string from the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     *
     * @param key The key of the string to get.
     * @return A string from the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     */
    protected final String getResourceString(String key) {
        return resources.getString(key);
    }

    /**
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}. This value is specified using the {@link FXMLResource} annotation.
     *
     * @param <C> The type of controller.
     * @param ctlClass The {@link java.lang.Class} for the target controller.
     * @return The name of the FXML resource associated with the target controller or null if resource name is not specified.
     */
    @Deprecated
    public static final <C> String getFXMLResourceName(Class<? extends C> ctlClass) {
        Class<FXMLResource> ac = FXMLResource.class;
        String message;
        if (ctlClass.isAnnotationPresent(ac)) {
            String n = ctlClass.getAnnotation(ac).value();
            if (n != null && !n.trim().isEmpty()) {
                return n;
            }
            message = String.format("Value not defined for annotation scene.annotations.FXMLResourceName in type %s",
                    ctlClass.getName());
        } else {
            message = String.format("Annotation scene.annotations.FXMLResourceName not present in type %s", ctlClass.getName());
        }
        LOG.logp(Level.SEVERE, SchedulerController.class.getName(), "getFXMLResourceName", message);
        return null;
    }

//    /**
//     * Gets the name of the internationalization resource bundle to be loaded with the specified controller {@link Class}. This value is specified using the
//     * {@link GlobalizationResource} annotation.
//     *
//     * @param <C> The type of controller.
//     * @param ctlClass The {@link Class} for the target controller.
//     * @return The name of the internationalization resource bundle to be loaded with the target controller.
//     */
//    public static final <C> String getGlobalizationResourceName(Class<? extends C> ctlClass) {
//        Class<GlobalizationResource> ac = GlobalizationResource.class;
//        String message;
//        if (ctlClass.isAnnotationPresent(ac)) {
//            String n = ctlClass.getAnnotation(ac).value();
//            if (n != null && !n.trim().isEmpty()) {
//                return n;
//            }
//            message = String.format("Value not defined for annotation scene.annotations.GlobalizationResource in type %s",
//                    ctlClass.getName());
//        } else {
//            message = String.format("Annotation scene.annotations.GlobalizationResource not present in type %s", ctlClass.getName());
//        }
//        LOG.logp(Level.SEVERE, SchedulerController.class.getName(), "getGlobalizationResourceName", message);
//        return scheduler.App.GLOBALIZATION_RESOURCE_NAME;
//    }

    /**
     * Loads a view and controller. The path of the view to load is identified by the {@link FXMLResource} annotation on the {@code controllerClass}. The {@link ResourceBundle}
     * loaded with the controller is identified by the {@link GlobalizationResource} annotation on the {@code controllerClass}.
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
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(getFXMLResourceName(controllerClass)),
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
     * Loads a view and controller. The path of the view to load is identified by the {@link FXMLResource} annotation on the {@code controllerClass}. The {@link ResourceBundle}
     * loaded with the controller is identified by the {@link GlobalizationResource} annotation on the {@code controllerClass}.
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

    public static <V extends Node, C extends SchedulerController> C load(Stage stage, Class<C> controllerClass,
            BiConsumer<V, C> show) throws IOException {
        return load(stage, controllerClass, null, show);
    }

    public static <V extends Node, C extends SchedulerController> C load(Stage stage, Class<C> controllerClass,
            BiConsumer<V, C> show, Class<?> baseResourceClass) throws IOException {
        return load(stage, controllerClass, null, show, baseResourceClass);
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

    /**
     * Collapses a JavaFX scene graph {@link javafx.scene.Node}. This adds the CSS class "collapsed" to the {@link javafx.scene.Node#styleClass} list, which sets vertical and
     * horizontal dimensions to zero and sets the {@link javafx.scene.Node#visible} property to {@code false}.
     *
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be collapsed and hidden.
     */
    protected static void collapseNode(Node node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains("collapsed")) {
            classes.add("collapsed");
        }
    }

    /**
     * Restores the visibility and dimensions of a JavaFX scene graph {@link javafx.scene.Node}. This removes the CSS class "collapsed" from the
     * {@link javafx.scene.Node#styleClass} list.
     *
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     */
    protected static void restoreNode(Node node) {
        node.getStyleClass().remove("collapsed");
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control and sets the {@link javafx.scene.control.Labeled#text} property. This removes
     * the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    protected static void restoreLabeled(Labeled control, String text) {
        control.getStyleClass().remove("collapsed");
        control.setText(text);
    }

}
