package scheduler.util;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import scheduler.AppResources;
import scheduler.view.ViewAndController;
import scheduler.view.ViewControllerLifecycleEvent;
import scheduler.view.ViewLifecycleEventReason;

/**
 *
 * @author lerwi
 */
public class ViewControllerLoader {
    
    private static <T extends Parent, S> ViewAndController<T, S> loadViewAndController(Class<S> controllerClass,
            ResourceBundle resourceBundle) throws IOException {
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(AppResources.getFXMLResourceName(controllerClass)),resourceBundle);
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
        return result;
    }
    
    /**
     * Loads a view and its controller.
     * 
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param controllerClass The controller class.
     * This class must have the {@link scheduler.view.annotations.FXMLResource} and {@link scheduler.view.annotations.GlobalizationResource}
     * annotations.
     * @param baseResourceClass A class with the {@link scheduler.view.annotations.GlobalizationResource} annotation which defines the name of the
     * base resource bundle used in creating a merged {@link ResourceBundle}.
     * @return The {@link ViewAndController} object that contains the loaded view and controller.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> ViewAndController<T, S> loadViewAndController(Class<S> controllerClass,
            Class<?> baseResourceClass) throws IOException {
        return loadViewAndController(controllerClass, (null == baseResourceClass) ? ResourceBundleLoader.getBundle(controllerClass)
                        : ResourceBundleLoader.getMergedBundle(controllerClass, baseResourceClass));
    }
    
    /**
     * Loads a view and its controller.
     * 
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param controllerClass The controller class.
     * This class must have the {@link scheduler.view.annotations.FXMLResource} and {@link scheduler.view.annotations.GlobalizationResource}
     * annotations.
     * @return The {@link ViewAndController} object that contains the loaded view and controller.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> ViewAndController<T, S> loadViewAndController(Class<S> controllerClass) throws IOException {
        return loadViewAndController(controllerClass, ResourceBundleLoader.getBundle(controllerClass));
    }

    /**
     * Shows a view in a new application-modal window.
     * 
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * This object can use the {@link scheduler.view.annotations.HandlesViewLifecycleEvent} annotation to handle view/controller life-cycle events.
     * @param parent The parent {@link Stage}.
     * @param viewAndController The view and controller.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, ViewAndController<T, S> viewAndController) throws IOException {
        ViewControllerLifecycleEvent<T, S> event = viewAndController.toEvent(source, ViewLifecycleEventReason.LOADED, parent);
        AnnotationHelper.invokeViewLifecycleEventMethods(source, event);
        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(), event);
        Stage childStage = new Stage();
        Scene scene = new Scene(viewAndController.getView());
        childStage.initOwner(parent);
        childStage.initModality(Modality.NONE);
        childStage.initModality(Modality.APPLICATION_MODAL);
        childStage.setScene(scene);
        event = viewAndController.toEvent(source, ViewLifecycleEventReason.ADDED, parent);
        AnnotationHelper.invokeViewLifecycleEventMethods(source, event);
        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(), event);
        childStage.setOnHidden((WindowEvent we) -> {
            ViewControllerLifecycleEvent<T, S> e = viewAndController.toEvent(source, ViewLifecycleEventReason.UNLOADED, parent);
            AnnotationHelper.invokeViewLifecycleEventMethods(source, e);
            AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(), e);
        });
        childStage.setOnShown((WindowEvent we) -> {
            ViewControllerLifecycleEvent<T, S> e = viewAndController.toEvent(source, ViewLifecycleEventReason.SHOWN, parent);
            AnnotationHelper.invokeViewLifecycleEventMethods(source, e);
            AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(), e);
        });
        childStage.showAndWait();
        return viewAndController.getController();
    }
    
    /**
     * Loads a view and controller, showing it in a modal window.
     * 
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * This object can use the {@link scheduler.view.annotations.HandlesViewLifecycleEvent} annotation to handle view/controller life-cycle events.
     * @param parent The parent {@link Stage}.
     * @param controllerClass The controller class.
     * This class must have the {@link scheduler.view.annotations.FXMLResource} and {@link scheduler.view.annotations.GlobalizationResource}
     * annotations.
     * @param baseResourceClass A class with the {@link scheduler.view.annotations.GlobalizationResource} annotation which defines the name of the
     * base resource bundle used in creating a merged {@link ResourceBundle}.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, Class<S> controllerClass, Class<?> baseResourceClass) throws IOException {
        ViewAndController<T, S> viewAndController = loadViewAndController(controllerClass, baseResourceClass);
        return showAndWait(source, parent, viewAndController);
    }
    
    /**
     * Loads a view and controller, showing it in a modal window.
     * 
     * @param <T> The type of {@link Parent} representing the view.
     * @param <S> The type of controller.
     * @param source The event source object for this operation.
     * This object can use the {@link scheduler.view.annotations.HandlesViewLifecycleEvent} annotation to handle view/controller life-cycle events.
     * @param parent The parent {@link Stage}.
     * @param controllerClass The controller class.
     * This class must have the {@link scheduler.view.annotations.FXMLResource} and {@link scheduler.view.annotations.GlobalizationResource}
     * annotations.
     * @return The controller object.
     * @throws IOException If unable to load the view.
     */
    public static <T extends Parent, S> S showAndWait(Object source, Stage parent, Class<S> controllerClass) throws IOException {
        ViewAndController<T, S> viewAndController = loadViewAndController(controllerClass);
        return showAndWait(source, parent, viewAndController);
    }
    
}
