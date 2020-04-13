package scheduler.util;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import scheduler.AppResources;
import scheduler.view.ViewAndController;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventType;

/**
 * Utility class for loading an FXML resource and instantiating its controller.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ViewControllerLoader {

    private static <T extends Parent, S> ViewAndController<T, S> loadViewAndController(Class<S> controllerClass,
            ResourceBundle resourceBundle) throws IOException {
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(AppResources.getFXMLResourceName(controllerClass)), resourceBundle);
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
        if (null == result.getController())
            throw new InternalException("Controller not instantiated");
        if (!controllerClass.isAssignableFrom(result.getController().getClass()))
            throw new InternalException("Controller type mismatch.");
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

    /**
     * Shows a view in a new application-modal window.
     *
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * @param parent The parent {@link Stage}.
     * @param viewAndController The view and controller.
     * @param loadEventListener An object that can listen for load events. This object can implement
     * {@link scheduler.view.event.FxmlViewControllerEventListener} or use the {@link scheduler.view.annotations.HandlesFxmlViewEvent} annotation
     * to handle view/controller life-cycle events.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, ViewAndController<T, S> viewAndController,
            Object loadEventListener) throws IOException {
        FxmlViewControllerEvent<T, S> event = viewAndController.toEvent(source, FxmlViewEventType.LOADED, parent);
        EventHelper.fireFxmlViewEvent(source, event);
        EventHelper.fireFxmlViewEvent(viewAndController.getController(), event);
        Stage childStage = new Stage();
        Scene scene = new Scene(viewAndController.getView());
        childStage.initOwner(parent);
        childStage.initModality(Modality.NONE);
        childStage.initModality(Modality.APPLICATION_MODAL);
        childStage.setScene(scene);
        event = viewAndController.toEvent(source, FxmlViewEventType.BEFORE_SHOW, parent);
        EventHelper.fireFxmlViewEvent(source, event);
        EventHelper.fireFxmlViewEvent(viewAndController.getController(), event);
        childStage.setOnHidden((WindowEvent we) -> {
            FxmlViewControllerEvent<T, S> e = viewAndController.toEvent(source, FxmlViewEventType.UNLOADED, parent);
            EventHelper.fireFxmlViewEvent(source, e);
            EventHelper.fireFxmlViewEvent(viewAndController.getController(), e);
        });
        childStage.setOnShown((WindowEvent we) -> {
            FxmlViewControllerEvent<T, S> e = viewAndController.toEvent(source, FxmlViewEventType.SHOWN, parent);
            EventHelper.fireFxmlViewEvent(source, e);
            EventHelper.fireFxmlViewEvent(viewAndController.getController(), e);
        });
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
     * <p>
     * This parameter can implement {@link scheduler.view.event.FxmlViewControllerEventListener} or use the
     * {@link scheduler.view.annotations.HandlesFxmlViewEvent} annotation to handle view/controller life-cycle events.</p>
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

    private static final String PANE_CONTROLLER_PROPERTY_KEY = "ViewControllerLoader.PaneContentController";

    /**
     * This is intended to be invoked by a controller that uses
     * {@link #replacePaneContent(Object, StackPane, scheduler.view.ViewAndController, java.lang.Object)} to manage contents when its host window has
     * closed. This is to ensure that the {@link FxmlViewEventType#UNLOADED} event is raised on the nested controller.
     *
     * @param source The event source object for this operation.
     * @param pane The {@link StackPane} that may contain a loaded view and controller.
     */
    public static void clearPaneContent(Object source, StackPane pane) {
        Stage stage = (Stage) pane.getScene().getWindow();
        ObservableMap<Object, Object> properties = pane.getProperties();
        Object oldController;
        Optional<Node> oldView;
        ObservableList<Node> children = pane.getChildren();
        if (children.isEmpty() || !properties.containsKey(PANE_CONTROLLER_PROPERTY_KEY)) {
            oldController = null;
            oldView = Optional.empty();
        } else {
            if (null == (oldController = properties.get(PANE_CONTROLLER_PROPERTY_KEY))) {
                oldView = Optional.empty();
            } else {
                oldView = children.stream().filter((t) -> t instanceof Parent).findFirst();
                if (!oldView.isPresent()) {
                    oldController = null;
                }
            }
            properties.remove(PANE_CONTROLLER_PROPERTY_KEY);
        }
        children.clear();

        if (null != oldController) {
            EventHelper.fireFxmlViewEvent(oldController, new FxmlViewEvent<>(source, FxmlViewEventType.UNLOADED,
                    (Parent) oldView.get(), stage));
        }
    }

    public static <T extends Parent, S> S replacePaneContent(Object source, StackPane pane, ViewAndController<T, S> viewAndController,
            Object loadEventListener) throws IOException {
        Stage stage = (Stage) pane.getScene().getWindow();
        ObservableMap<Object, Object> properties = pane.getProperties();
        Object oldController;
        Optional<Node> oldView;
        ObservableList<Node> children = pane.getChildren();
        if (children.isEmpty() || !properties.containsKey(PANE_CONTROLLER_PROPERTY_KEY)) {
            oldController = null;
            oldView = Optional.empty();
        } else if (null == (oldController = properties.get(PANE_CONTROLLER_PROPERTY_KEY))) {
            oldView = Optional.empty();
        } else {
            oldView = children.stream().filter((t) -> t instanceof Parent).findFirst();
            if (!oldView.isPresent()) {
                oldController = null;
            }
        }

        FxmlViewControllerEvent<T, S> event = viewAndController.toEvent(source, FxmlViewEventType.LOADED, stage);
        EventHelper.fireFxmlViewEvent(loadEventListener, event);
        EventHelper.fireFxmlViewEvent(viewAndController.getController(), event);

        children.clear();
        children.add(viewAndController.getView());
        properties.put(PANE_CONTROLLER_PROPERTY_KEY, viewAndController.getController());
        event = viewAndController.toEvent(source, FxmlViewEventType.BEFORE_SHOW, stage);
        EventHelper.fireFxmlViewEvent(loadEventListener, event);
        EventHelper.fireFxmlViewEvent(viewAndController.getController(), event);
        event = viewAndController.toEvent(source, FxmlViewEventType.SHOWN, stage);
        EventHelper.fireFxmlViewEvent(loadEventListener, event);
        EventHelper.fireFxmlViewEvent(viewAndController.getController(), event);

        if (null != oldController) {
            EventHelper.fireFxmlViewEvent(oldController, new FxmlViewEvent<>(source, FxmlViewEventType.UNLOADED,
                    (Parent) oldView.get(), stage));
        }

        return viewAndController.getController();
    }

    public static <T extends Parent, S> S replacePaneContent(Object source, StackPane pane, Class<S> controllerClass,
            Object loadEventListener) throws IOException {
        ViewAndController<T, S> viewAndController = loadViewAndController(controllerClass);
        return replacePaneContent(source, pane, viewAndController, loadEventListener);
    }

    public static <T extends Parent, S> S replacePaneContent(Object source, StackPane pane, Class<S> controllerClass) throws IOException {
        ViewAndController<T, S> viewAndController = loadViewAndController(controllerClass);
        return replacePaneContent(source, pane, viewAndController, null);
    }

}
