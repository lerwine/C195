package scheduler.util;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.view.ViewAndController;

/**
 * Utility class for loading an FXML resource and instantiating its controller.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ViewControllerLoader {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ViewControllerLoader.class.getName()), Level.FINER);

    // private static final String PANE_CONTROLLER_PROPERTY_KEY = "ViewControllerLoader.PaneContentController";

    private static <T extends Parent, S> ViewAndController<T, S> loadViewAndController(Class<S> controllerClass,
            ResourceBundle resourceBundle) throws IOException {
        String path = AppResources.getFXMLResourceName(controllerClass);
        LOG.fine(() -> String.format("Loading %s", path));
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(path), resourceBundle);
        ViewAndController<T, S> result = new ViewAndController<T, S>() {
            private final T view = loader.load();
            private final S controller = loader.getController();

            @Override
            public T getView() {
                return view;
            }

            @Override
            public S getController() {
                return controller;
            }
        };
        LOG.fine(() -> String.format("%s loaded", path));
        if (null == result.getController()) {
            throw new InternalException("Controller not instantiated");
        }
        if (!controllerClass.isAssignableFrom(result.getController().getClass())) {
            throw new InternalException("Controller type mismatch.");
        }
        return result;
    }

    /**
     * Loads a view and its controller.
     *
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param controllerClass The controller class. This class must have the {@link scheduler.view.annotations.FXMLResource} and
     * {@link scheduler.view.annotations.GlobalizationResource} annotations.
     * @return The {@link ViewAndController} object that contains the loaded view and controller.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> ViewAndController<T, S> loadViewAndController(Class<S> controllerClass) throws IOException {
        return loadViewAndController(controllerClass, ResourceBundleHelper.getBundle(controllerClass));
    }

    public static <T extends Node> T loadView(Object controller, T view, ResourceBundle resourceBundle) throws IOException {
        Class<?> c = controller.getClass();
        String path = AppResources.getFXMLResourceName(c);
        LOG.fine(() -> String.format("Loading %s", path));
        FXMLLoader loader = new FXMLLoader(c.getResource(path), resourceBundle);
        loader.setController(controller);
        loader.setRoot(view);
        T result = loader.load();
        LOG.fine(() -> String.format("%s loaded", path));
        return result;
    }

    public static <T extends Node> T loadView(Object controller, T view) throws IOException {
        return loadView(controller, view, ResourceBundleHelper.getBundle(controller.getClass()));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Node> void initializeCustomControl(T customControl, ResourceBundle resources) throws IOException {
        Class<T> c = (Class<T>) customControl.getClass();
        String path = AppResources.getFXMLResourceName(c);
        LOG.fine(() -> String.format("Loading %s", path));
        FXMLLoader loader = new FXMLLoader(c.getResource(path),
                (null == resources) ? ResourceBundleHelper.getBundle(c) : resources);
        loader.setRoot(customControl);
        loader.setController(customControl);
        loader.load();
        LOG.fine(() -> String.format("%s loaded", path));
    }

    /**
     * Loads the FXML for a custom control.
     *
     * @param <T> The custom control type. This class must have the {@link scheduler.view.annotations.FXMLResource} and
     * {@link scheduler.view.annotations.GlobalizationResource} annotations.
     * @param customControl The custom control to be initialized.
     * @throws IOException If unable to load the FXML.
     */
    public static <T extends Node> void initializeCustomControl(T customControl) throws IOException {
        initializeCustomControl(customControl, null);
    }

    /**
     * Shows a view in a new application-modal window.
     *
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * @param parent The parent {@link Stage}.
     * @param viewAndController The view and controller.
     * @param loadEventListener An object that can listen for load events.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, ViewAndController<T, S> viewAndController,
            Object loadEventListener) throws IOException {
        Stage childStage = new Stage();
        Scene scene = new Scene(viewAndController.getView());
        childStage.initOwner(parent);
        childStage.initModality(Modality.NONE);
        childStage.initModality(Modality.APPLICATION_MODAL);
        childStage.setScene(scene);
        childStage.showAndWait();
        return viewAndController.getController();
    }

    /**
     * Shows a view in a new application-modal window.
     *
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * @param parent The parent {@link Stage}.
     * @param viewAndController The view and controller.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, ViewAndController<T, S> viewAndController) throws IOException {
        return showAndWait(source, parent, viewAndController, (Object) null);
    }

    /**
     * Loads a view and controller, showing it in a modal window.
     *
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * @param parent The parent {@link Stage}.
     * @param controllerClass The controller class.
     * <p>
     * This class definition for this parameter must have the {@link scheduler.view.annotations.FXMLResource} and
     * {@link scheduler.view.annotations.GlobalizationResource} annotations.</p>
     * @param loadEventListener An object that can listen for load events.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, Class<S> controllerClass,
            Object loadEventListener) throws IOException {
        ViewAndController<T, S> viewAndController = loadViewAndController(controllerClass);
        return showAndWait(source, parent, viewAndController, loadEventListener);
    }

    /**
     * Loads a view and controller, showing it in a modal window.
     *
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * @param parent The parent {@link Stage}.
     * @param controllerClass The controller class. This class must have the {@link scheduler.view.annotations.FXMLResource} and
     * {@link scheduler.view.annotations.GlobalizationResource} annotations.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, Class<S> controllerClass) throws IOException {
        return showAndWait(source, parent, controllerClass, (Object) null);
    }

}
