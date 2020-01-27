/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
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

    private ViewManager viewManager;
    
    protected final ViewManager getViewManager() { return viewManager; }
    
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
     * This gets called before the FXML view associated with this controller is added to the target {@link javafx.stage.Stage}.
     * @param newView The FXML view associated with this controller.
     * @param currentController The JavaFX controller for the FXML view that will be replaced by the one associated with this controller.
     * @param currentView The FXML view that will be replaced by the one associated with this controller.
     */
    protected void onLoaded(Parent newView, SchedulerController currentController, Parent currentView) { }
    
    /**
     * This gets called after the FXML view associated with this controller is added to the target {@link javafx.stage.Stage}.
     * @param currentView The FXML view associated with this controller.
     * @param oldController The JavaFX controller for the FXML view that was replaced by the one associated with this controller.
     * @param oldView The FXML view that was replaced by the one associated with this controller.
     */
    protected void onApplied(Parent currentView, SchedulerController oldController, Parent oldView) { }
    
    /**
     * This gets called before the FXML view associated with this controller is removed from the target {@link javafx.stage.Stage} or
     * its {@link javafx.stage.Stage} is being hidden.
     * @param newController The JavaFX controller for the FXML view that will be replacing the current one or {@code null} if the current
     * {@link javafx.stage.Stage} is being hidden.
     * @param newParent The FXML view that will be replacing the current one or {@code null} if the current {@link javafx.stage.Stage} is being hidden.
     */
    protected void onUnloading(SchedulerController newController, Parent newParent) { }
    
    /**
     * This gets called after the FXML view associated with this controller has been removed from the target {@link javafx.stage.Stage} or
     * its {@link javafx.stage.Stage} was hidden.
     * @param newController The JavaFX controller for the FXML view that replaced the current one or {@code null} if the current
     * {@link javafx.stage.Stage} was hidden.
     * @param newParent The FXML view that replaced the current one or {@code null} if the current {@link javafx.stage.Stage} was hidden.
     */
    protected void onUnloaded(SchedulerController newController, Parent newParent) { }
    
    /**
     * Sets the FXML view associated with a {@link ViewManager}.
     * The {@link FXMLResource} applied to the controller class defines the path of the FXML view resource.
     * The {@link GlobalizationResource} applied to the controller class defines the globalization resource bundle to load.
     * @param <C> The type of JavaFX controller for the target FXML view.
     * @param controllerClass The class of the JavaFX controller for the target FXML view.
     * @param viewManager The {@link ViewManager} that is used to control the current view.
     * @param factory The {@link ViewControllerFactory} that creates and initializes the JavaFX controller or {@code null} to use a default factory.
     * @return The JavaFX controller.
     * @throws Exception if not able to load the target FXML view.
     */
    public static <C extends SchedulerController> C setView(Class<C> controllerClass, ViewManager viewManager,
            ViewControllerFactory<C> factory) throws Exception {
        Objects.requireNonNull(controllerClass, "Controller class cannot be null");
        Objects.requireNonNull(viewManager, "View manager cannot be null");
        ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(controllerClass), Locale.getDefault(Locale.Category.DISPLAY));
        FXMLLoader loader;
        if (null == factory)
            loader = new FXMLLoader(controllerClass.getResource(getFXMLResourceName(controllerClass)), rb);
        else {
            loader = new FXMLLoader(controllerClass.getResource(getFXMLResourceName(controllerClass)), rb, null, (c) -> factory.call((Class<C>)c));
            factory.beforeLoad(loader);
        }
        Parent parent = loader.load();
        C controller = loader.getController();
        ((SchedulerController)controller).viewManager = viewManager;
        if (null == factory) {
            controller.onLoaded(parent, null, null);
            viewManager.setContent(parent);
            controller.onApplied(parent, null, null);
        } else {
            factory.onLoaded(controller, parent, null, null);
            controller.onLoaded(parent, null, null);
            Dimension2D d = factory.getDimensions(controller, parent);
            if (null == d)
                viewManager.setContent(parent);
            else
                viewManager.setContent(parent, d.getWidth(), d.getHeight());
            try { factory.onApplied(controller, parent, null, null); }
            finally { controller.onApplied(parent, null, null); }
        }
        return controller;
    }
    
    /**
     * Sets the FXML view associated with a {@link ViewManager}.
     * The {@link FXMLResource} applied to the controller class defines the path of the FXML view resource.
     * The {@link GlobalizationResource} applied to the controller class defines the globalization resource bundle to load.
     * @param <C> The type of JavaFX controller for the new FXML view.
     * @param controllerClass The class of the JavaFX controller for the target FXML view.
     * @param viewManager The {@link ViewManager} that is used to control the current view.
     * @return The JavaFX controller.
     * @throws Exception if not able to load the target FXML view.
     */
    public static <C extends SchedulerController> C setView(Class<C> controllerClass, ViewManager viewManager) throws Exception {
        return setView(controllerClass, viewManager, null);
    }
    
    /**
     * Replaces the current FXML view with a new one.
     * The {@link FXMLResource} applied to the controller class defines the path of the FXML view resource.
     * The {@link GlobalizationResource} applied to the controller class defines the globalization resource bundle to load.
     * @param <C> The type of JavaFX controller for the new FXML view.
     * @param controllerClass The class of the JavaFX controller for the new FXML view.
     * @param factory The {@link ViewControllerFactory} that creates and initializes the new JavaFX controller or {@code null} to use a default factory.
     * @return The JavaFX controller.
     * @throws Exception if not able to load the new FXML view.
     */
    public <C extends SchedulerController> C replaceView(Class<C> controllerClass, ViewControllerFactory<C> factory) throws Exception {
        Objects.requireNonNull(controllerClass, "Controller class cannot be null");
        ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(controllerClass), Locale.getDefault(Locale.Category.DISPLAY));
        FXMLLoader loader;
        if (null == factory)
            loader = new FXMLLoader(controllerClass.getResource(getFXMLResourceName(controllerClass)), rb);
        else {
            loader = new FXMLLoader(controllerClass.getResource(getFXMLResourceName(controllerClass)), rb, null, (c) -> factory.call((Class<C>)c));
            factory.beforeLoad(loader);
        }
        Parent parent = loader.load();
        C controller = loader.getController();
        ((SchedulerController)controller).viewManager = viewManager;
        Parent oldView = viewManager.getContent();
        if (null != factory)
            factory.onLoaded(controller, parent, this, oldView);
        controller.onLoaded(parent, this, oldView);
        onUnloading(controller, parent);
        viewManager.setContent(parent);
        try {
            if (null == factory)
                controller.onApplied(parent, this, oldView);
            else
                try { factory.onApplied(controller, parent, this, oldView); }
                finally { controller.onApplied(parent, this, oldView); }
        } finally {
            viewManager = null;
            onUnloaded(controller, parent);
        }
        return controller;
    }
    
    /**
     * Replaces the current FXML view with a new one.
     * The {@link FXMLResource} applied to the controller class defines the path of the FXML view resource.
     * The {@link GlobalizationResource} applied to the controller class defines the globalization resource bundle to load.
     * @param <C> The type of JavaFX controller for the new FXML view.
     * @param controllerClass  The class of the JavaFX controller for the new FXML view.
     * @return The JavaFX controller.
     * @throws Exception if not able to load the new FXML view.
     */
    public <C extends SchedulerController> C replaceView(Class<C> controllerClass) throws Exception {
        return replaceView(controllerClass, null);
    }
    
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
