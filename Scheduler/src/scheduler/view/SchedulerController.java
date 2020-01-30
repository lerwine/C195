/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Base class for controllers.
 * Derived classes must be annotated by {@link scheduler.view.annotations.FXMLResource} to specify the name of the FXML resource
 * to be associated with the current controller, and by {@link scheduler.view.annotations.GlobalizationResource} to specify the
 * resource bundle to load with the target FXML resource.
 * @author Leonard T. Erwine
 */
public abstract class SchedulerController {
    
    private static final Logger LOG = Logger.getLogger(SchedulerController.class.getName());
    
    @FXML
    private ResourceBundle resources;
    
    /**
     * Gets the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     * @return The {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     */
    protected final ResourceBundle getResources() { return resources; }
    
    /**
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}.
     * This value is specified using the {@link scene.annotations.FXMLResourceName} annotation.
     * 
     * @param <C> The type of controller.
     * @param ctlClass The {@link java.lang.Class} for the target controller.
     * @return The name of the FXML resource associated with the target controller or null if resource name is not specified.
     */
    public static final <C> String getFXMLResourceName(Class<? extends C> ctlClass) {
        Class<FXMLResource> ac = FXMLResource.class;
        String message;
        if (ctlClass.isAnnotationPresent(ac)) {
            String n = ctlClass.getAnnotation(ac).value();
            if (n != null && !n.trim().isEmpty())
                return n;
            message = String.format("Value not defined for annotation scene.annotations.FXMLResourceName in type %s",
                    ctlClass.getName());
        } else
            message = String.format("Annotation scene.annotations.FXMLResourceName not present in type %s", ctlClass.getName());
        LOG.log(Level.SEVERE, message);
        return null;
    }
    
    /**
     * Gets the name of the internationalization resource bundle to be loaded with the specified controller
     * {@link java.lang.Class}.
     * This value is specified using the {@link scene.annotations.GlobalizationResource} annotation.
     * @param <C> The type of controller.
     * @param ctlClass The {@link java.lang.Class} for the target controller.
     * @return The name of the internationalization resource bundle to be loaded with the target controller.
     */
    public static final <C> String getGlobalizationResourceName(Class<? extends C> ctlClass) {
        Class<GlobalizationResource> ac = GlobalizationResource.class;
        String message;
        if (ctlClass.isAnnotationPresent(ac)) {
            String n = ctlClass.getAnnotation(ac).value();
            if (n != null && !n.trim().isEmpty())
                return n;
            message = String.format("Value not defined for annotation scene.annotations.GlobalizationResource in type %s",
                    ctlClass.getName());
        } else
            message = String.format("Annotation scene.annotations.GlobalizationResource not present in type %s", ctlClass.getName());
        LOG.log(Level.SEVERE, message);
        return scheduler.App.GLOBALIZATION_RESOURCE_NAME;
    }

    /**
     * View/Controller factory for modal dialog windows.
     * @param <C> The type of controller.
     * @param <V> The type of view.
     */
    public static abstract class Modal<C extends SchedulerController, V extends Parent> extends ViewControllerFactory<C, V> {

        /**
         * Initializes a new View/Controller factory for a modal dialog window.
         * @param controllerClass The {@link Class} of the controller.
         */
        protected Modal(Class<C> controllerClass) { super(controllerClass); }
        
        /**
         * Displays a modal dialog window and returns the controller.
         * @param parentStage The {@link Stage} that represents the the dialog window's parent window.
         * @param width The width of the dialog window.
         * @param height The height of the dialog window.
         * @return The controller that was used for the dialog window.
         * @throws IOException if unable to load the view.
         */
        public C showAndWait(Stage parentStage, double width, double height) throws IOException {
            load();
            Stage stage = new Stage();
            stage.initOwner(parentStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(getView(), width, height);
            stage.setScene(scene);
            onBeforeShow(stage);
            getController().onBeforeShow(getView(), stage);
            stage.showAndWait();
            Pair<C, V> cv = reset();
            onUnloaded(cv.getKey(), cv.getValue());
            cv.getKey().onUnloaded(cv.getValue());
            return cv.getKey();
        }
        
    }
    
    /**
     * Represents a view nested within a {@link Parent} node.
     * @param <P> The type of {@link Parent} node.
     * @param <C> The type of controller for the nested view.
     * @param <V> The type of nested {@link Node} that represents the view.
     */
    public static abstract class NestedViewFactory<P extends Parent, C extends SchedulerController, V extends Node> extends ViewControllerFactory<C, V> {
        
        /**
         * Initializes a new View/Controller factory.
         * @param controllerClass The {@link Class} of the controller.
         */
        protected NestedViewFactory(Class<C> controllerClass) { super(controllerClass); } 
        
        /**
         * This gets called before the view is added to the {@link Parent} node.
         * @param parent The new {@link Parent} for the current view.
         */
        protected void onBeforeShow(P parent) {
            Scene scene = parent.getScene();
            if (null != scene) {
                Stage stage = (Stage)scene.getWindow();
                onBeforeShow(stage);
            }
        }

        /**
         * This gets called when the current view has been added to the {@link Parent} node.
         * @param parent The {@link Parent} node that contains the nested view.
         */
        protected void onShown(P parent) { }
        
        /**
         * Sets the child {@link Node} that represents the nested view.
         * @param parent The {@link Parent} node that will contain the nested view.
         */
        protected abstract void setChildOf(P parent);
        
        /**
         * Clears the child {@link Node} that represents the nested view.
         * @param parent The {@link Parent} node that contains the nested view.
         */
        protected abstract void clearChild(P parent);
        
        /**
         * Loads the view and adds it to a {@link Parent} node.
         * @param parent The {@link Parent} node.
         * @param currentFactory The factory that loaded the current nested view, which is about to be replaced.
         * @throws IOException if unable to load the view.
         */
        public void addToParent(P parent, NestedViewFactory<P, ? extends SchedulerController, ? extends Node> currentFactory) throws IOException {
            load();
            onBeforeShow(parent);
            setChildOf(parent);
            
            try {
                if (null != currentFactory)
                    currentFactory.unload(parent, false);
            } finally {
                onShown(parent);
            }
        }
        
        private void unload(P parent, boolean removeFromParent) {
            Pair<C, V> cv = reset();
            if (null == cv.getKey())
                return;
            if (removeFromParent)
                clearChild(parent);
            try { onUnloaded(cv.getKey(), cv.getValue()); }
            finally { cv.getKey().onUnloaded(cv.getValue()); }
        }
    }
    
    /**
     * Represents a View/Controller factory.
     * @param <C> The type of controller.
     * @param <V> The type of view.
     */
    public static abstract class ViewControllerFactory<C extends SchedulerController, V extends Node> {
        private final Class<C> controllerClass;
        
        /**
         * Gets the {@link Class} of the controller.
         * @return The {@link Class} of the controller.
         */
        public final Class<C> getControllerClass() { return controllerClass; }
        
        /**
         * Initializes a new View/Controller factory.
         * @param controllerClass The {@link Class} of the controller.
         */
        protected ViewControllerFactory(Class<C> controllerClass) { this.controllerClass = Objects.requireNonNull(controllerClass); } 
        
        /**
         * This gets called to instantiate the new controller.
         * @param c The {@link Class} of the controller to create.
         * @return The newly instantiated controller.
         * @throws InstantiationException if not able to instantiate the controller.
         * @throws IllegalAccessException if not allowed to instantiate the controller.
         */
        protected C create(Class<C> c) throws InstantiationException, IllegalAccessException { return c.newInstance(); }
        
        /**
         * This get called before the View is loaded and the controller is instantiated.
         * @param loader The {@link FXMLLoader} that loads the view.
         */
        protected void onBeforeLoad(FXMLLoader loader) { }
        
        /**
         * This called after the controller is instantiated, and before it is initialized by the {@link FXMLLoader}.
         * @param controller The newly instantiated controller.
         */
        protected void onBeforeInit(C controller) { }
        
        /**
         * This gets called after view is loaded and the controller is initialized by the {@link FXMLLoader}.
         */
        protected void onLoaded() { controller.onLoaded(view); }
        
        private C controller;
        
        /**
         * Gets the controller for the current view.
         * @return The controller for the current view or {@code null} if no view was yet loaded.
         */
        public final C getController() { return controller; }
        
        private V view;
        
        /**
         * Gets the {@link Node} that represents the view.
         * @return The view or {@code null} if no view was yet loaded.
         */
        public final V getView() { return view; }
        
        public static <C extends SchedulerController> C loadInto(Class<C> controllerClass, Stage stage, boolean show) throws IOException {
            FXMLLoader loader = new FXMLLoader(controllerClass.getResource(getFXMLResourceName(controllerClass)), ResourceBundle.getBundle(getGlobalizationResourceName(controllerClass),
                    Locale.getDefault(Locale.Category.DISPLAY)));
            Parent view = loader.load();
            C controller = loader.getController();
            controller.onLoaded(view);
            Scene scene = new Scene(view);
            controller.onBeforeShow(view, stage);
            stage.setScene(scene);
            if (show)
                stage.show();
            return controller;
        }
        
        /**
         * Loads the view and instantiates the controller.
         * @throws IOException If unable to load the view.
         */
        public void load() throws IOException  {
            if (null != controller)
                return;
            FXMLLoader loader = new FXMLLoader(controllerClass.getResource(getFXMLResourceName(controllerClass)), ResourceBundle.getBundle(getGlobalizationResourceName(controllerClass),
                    Locale.getDefault(Locale.Category.DISPLAY)), null, (c) -> {
                try {
                    C ctl = create((Class<C>)c);
                    onBeforeInit(ctl);
                    return ctl;
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException("Error instantiating controller", ex);
                }
            });
            onBeforeLoad(loader);
            view = loader.load();
            controller = loader.getController();
            onLoaded();
            controller.onLoaded(view);
        }
        
        /**
         * Removes the view and controller.
         * @return The controller and view that was removed.
         */
        protected final Pair<C, V> reset() {
            Pair<C, V> result = new Pair<>(controller, view);
            view = null;
            controller = null;
            return result;
        }
        
        /**
         * This gets called before the view is shown.
         * @param stage The {@link Stage} representing the current window.
         */
        protected void onBeforeShow(Stage stage) {
            controller.onBeforeShow(view, stage);
        }

        /**
         * This gets called after the view is unloaded.
         * @param controller The controller of the unloaded view.
         * @param view The unloaded view.
         */
        protected void onUnloaded(C controller, V view) { controller.onUnloaded(view); }
    }
   
    /**
     * This gets called after view is loaded and the controller is initialized by the {@link FXMLLoader}.
     * @param view The root {@link node} representing the view.
     */
    protected void onLoaded(Node view) { }
    
    /**
     * This gets called before the dialog window is shown.
     * @param currentView The current view.
     * @param stage The {@link Stage} representing the current window.
     */
    protected void onBeforeShow(Node currentView, Stage stage) { }
    
    /**
     * This gets called after the view is removed from the hierarchy of the current {@link javafx.scene.Scene} or the current window is closed.
     * @param view
     */
    protected void onUnloaded(Node view) { }
    
    /**
     * Collapses a JavaFX scene graph {@link javafx.scene.Node}.
     * This adds the CSS class "collapsed" to the {@link javafx.scene.Node#styleClass} list, which sets
     * vertical and horizontal dimensions to zero and sets the {@link javafx.scene.Node#visible}
     * property to {@code false}.
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be collapsed and hidden.
     */
    protected static void collapseNode(Node node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains("collapsed"))
            classes.add("collapsed");
    }
    
    /**
     * Restores the visibility and dimensions of a JavaFX scene graph {@link javafx.scene.Node}.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list.
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     */
    protected static void restoreNode(Node node) {
        node.getStyleClass().remove("collapsed");
    }
    
    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control and sets the
     * {@link javafx.scene.control.Labeled#text} property.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list and sets the
     * {@link javafx.scene.control.Labeled#text} property.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    protected static void restoreLabeled(Labeled control, String text) {
        control.getStyleClass().remove("collapsed");
        control.setText(text);
    }

}
