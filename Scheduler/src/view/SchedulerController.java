/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import expressions.NonNullableStringProperty;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * Base class for controllers.
 * Derived classes must be annotated by {@link view.annotations.FXMLResource} to specify the name of the FXML resource
 * to be associated with the current controller, and by {@link view.annotations.GlobalizationResource} to specify the
 * resource bundle to load with the target FXML resource.
 * @author Leonard T. Erwine
 */
public abstract class SchedulerController {
    
    @FXML // ResourceBundle injected by the FXMLLoader
    private ResourceBundle resources;
    
    private static final Logger LOG = Logger.getLogger(SchedulerController.class.getName());

    /**
     * Gets the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     * @return
     */
    protected ResourceBundle getResources() { return resources; }
    
    /**
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}.
     * This value is specified using the {@link scene.annotations.FXMLResourceName} annotation.
     * 
     * @param <C>
     *          The type of controller.
     * @param ctlClass
     *          The {@link java.lang.Class} for the target controller.
     * @return
     *      The name of the FXML resource associated with the target controller or null if resource name is not specified.
     */
    public static <C> String getFXMLResourceName(Class<? extends C> ctlClass) {
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
     * 
     * @param <C>
     *          The type of controller.
     * @param ctlClass
     *          The {@link java.lang.Class} for the target controller.
     * @return
     *      The name of the internationalization resource bundle to be loaded with the target controller.
     */
    public static <C> String getGlobalizationResourceName(Class<? extends C> ctlClass) {
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
     * Creates and initializes a {@link ContentChangeContext} object.
     * The {@link ContentChangeContext} object produced will contain contextual information about the loaded FXML resource.
     * 
     * @param <C>
     *          The type of controller associated with the target FXML resource.
     */
    public static class ContentChangeContextFactory<C extends SchedulerController> implements Supplier<ContentChangeContext<C>> {
        private final ContentChangeContext<C> context;
        
        /**
         * Gets the read-only {@link ContentChangeContext} object that can be provided to handler functions.
         * 
         * @return
         *          A read-only {@link ContentChangeContext} object that can be provided to handler functions.
         */
        @Override
        public ContentChangeContext<C> get() { return context; }
        
        /**
         * Creates a ContentChangeContextFactory object.
         */
        public ContentChangeContextFactory() { context = new ContentChangeContext<>(); }
        
        /**
         * Sets the controller obtained from the {@link javafx.fxml.FXMLLoader}.
         * 
         * @param value
         *          The controller returned by the {@link javafx.fxml.FXMLLoader#getController()} method of
         *          the {@link javafx.fxml.FXMLLoader}.
         */
        public void setController(C value) { context.controller.set(value); }
        
        /**
         * Sets the {@link javafx.scene.Parent} node that was loaded by the {@link javafx.fxml.FXMLLoader}.
         * 
         * @param value
         *          The {@link javafx.scene.Parent} node that was returned by the
         *          {@link javafx.fxml.FXMLLoader#load(java.net.URL, java.util.ResourceBundle)} method of the
         *          {@link javafx.fxml.FXMLLoader}.
         */
        public void setParent(Parent value) { context.parent.set(value); }
        
        /**
         * Gets the {@link java.util.ResourceBundle} obtained from the {@link javafx.fxml.FXMLLoader}.
         * 
         * @param value
         *          The {@link java.util.ResourceBundle} returned by the {@link javafx.fxml.FXMLLoader#getResources()}
         *          method of the {@link javafx.fxml.FXMLLoader}.
         */
        public void setResourceBundle(ResourceBundle value) { context.resources.set(value); }
        
        /**
         * Sets the error or exception that was caught while trying to load and initialize the target FXML resource.
         * 
         * @param value
         *          The error or exception that was caught while trying to load and an initialize the target FXML resource.
         */
        public void setError(Throwable value) { context.error.set(value); }
    }
    
    /**
     * Contextual information about a loaded FXML resource.
     * @param <C>
     *          The type of controller associated with the target {@link javafx.scene.Parent} node.
     */
    public static class ContentChangeContext<C extends SchedulerController> {
        //<editor-fold defaultstate="collapsed" desc="controller property">
        
        private final ReadOnlyObjectWrapper<C> controller = new ReadOnlyObjectWrapper<>();

        /**
         * Gets the controller associated with the {@link #parent} node loaded by the {@link javafx.fxml.FXMLLoader}.
         *
         * @return
         *          The controller associated with the {@link #parent} node loaded by the {@link javafx.fxml.FXMLLoader}.
         */
        public C getController() { return controller.get(); }
        
        /**
         * The property that contains the controller associated with the {@link #parent} node.
         *
         * @return
         *          The property that returns the controller associated with the {@link #parent} node.
         */
        public ReadOnlyObjectProperty<C> controllerProperty() { return controller.getReadOnlyProperty(); }
        
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="parent property">
        
        private final ReadOnlyObjectWrapper<Parent> parent = new ReadOnlyObjectWrapper<>();
        
        /**
         * Gets the target {@link javafx.scene.Parent} node, which was loaded by the {@link javafx.fxml.FXMLLoader}.
         *
         * @return
         *          The target {@link javafx.scene.Parent} node, which was loaded by the {@link javafx.fxml.FXMLLoader}.
         */
        public Parent getParent() { return parent.get(); }
        
        /**
         * The property that returns the {@link javafx.scene.Parent} node loaded by the {@link javafx.fxml.FXMLLoader}.
         *
         * @return
         *          The property that contains the {@link javafx.scene.Parent} node loaded by the
         *          {@link javafx.fxml.FXMLLoader}.
         */
        public ReadOnlyObjectProperty<Parent> parentProperty() { return parent.getReadOnlyProperty(); }
        
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="error property">
        
        private final ReadOnlyObjectWrapper<Throwable> error = new ReadOnlyObjectWrapper<>();
        
        /**
         * Gets the error or exception that was caught while trying to load and initialize the target FXML resource.
         *
         * @return
         *          The error or exception that was caught while trying to load and initialize the target FXML resource
         *          or {@code null} if no error or exception occurred.
         */
        public Throwable getError() { return error.get(); }
        
        /**
         * The property that returns the error or exception that was caught while trying to load and initialize the
         * target FXML resource.
         *
         * @return
         *          The property that returns the error or exception that was caught while trying to load and initialize
         *          the target FXML resource.
         */
        public ReadOnlyObjectProperty<Throwable> errorProperty() { return error.getReadOnlyProperty(); }
        
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="resources property">
        
        private final ReadOnlyObjectWrapper<ResourceBundle> resources = new ReadOnlyObjectWrapper<>();
        
        /**
         * Gets the {@link java.util.ResourceBundle} loaded with the {@link javafx.fxml.FXMLLoader}.
         *
         * @return
         *          The {@link java.util.ResourceBundle} loaded with the {@link javafx.fxml.FXMLLoader}.
         */
        public ResourceBundle getResources() { return resources.get(); }
        
        /**
         * The property that returns the {@link java.util.ResourceBundle} loaded with the {@link javafx.fxml.FXMLLoader}.
         *
         * @return
         *          The property that returns the {@link java.util.ResourceBundle} loaded with the
         *          {@link javafx.fxml.FXMLLoader}.
         */
        public ReadOnlyObjectProperty<ResourceBundle> resourcesProperty() { return resources.getReadOnlyProperty(); }
        
        //</editor-fold>
        
        private final StringProperty windowTitle;

        public String getWindowTitle() { return windowTitle.get(); }

        /**
         * Sets the title for the current window of the target {@link javafx.scene.Parent} node.
         * 
         * @param value
         *          The title for the current window of the target {@link javafx.scene.Parent} node.
         */
        public void setWindowTitle(String value) { windowTitle.set(value); }

        public StringProperty windowTitleProperty() { return windowTitle; }
        
        public ContentChangeContext() {
            this.windowTitle = new NonNullableStringProperty(scheduler.App.CURRENT.get()
                    .getResources().getString(scheduler.App.RESOURCEKEY_APPOINTMENTSCHEDULER), true);
        }
        
    }
    
    /**
     * Collapses a JavaFX scene graph {@link javafx.scene.Node}.
     * This adds the CSS class "collapsed" to the {@link javafx.scene.Node#styleClass} list, which sets
     * vertical and horizontal dimensions to zero and sets the {@link javafx.scene.Node#visible}
     * property to {@code false}.
     * 
     * @param node
     *              The JavaFX scene graph {@link javafx.scene.Node} to be collapsed and hidden.
     */
    protected static void collapseNode(Node node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains("collapsed"))
            classes.add("collapsed");
    }
    
    /**
     * Restores the visibility and dimensions of a JavaFX scene graph {@link javafx.scene.Node}.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list.
     * 
     * @param node
     *              The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     */
    protected static void restoreNode(Node node) {
        node.getStyleClass().remove("collapsed");
    }
    
    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control and sets
     * the {@link javafx.scene.control.Labeled#text} property.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list and sets the
     * {@link javafx.scene.control.Labeled#text} property.
     * 
     * @param control
     *              The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text
     *              The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    protected static void restoreLabeled(Labeled control, String text) {
        control.getStyleClass().remove("collapsed");
        control.setText(text);
    }
    
}
