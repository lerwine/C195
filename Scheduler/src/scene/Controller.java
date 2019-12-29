/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scene;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;
import scheduler.App;
import scheduler.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
public abstract class Controller implements Initializable {
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public static <C extends Controller> String getFXMLResourceName(Class<? extends C> ctlClass) {
        Class<FXMLResource> ac = FXMLResource.class;
        if (ctlClass.isAnnotationPresent(ac)) {
            String n = ctlClass.getAnnotation(ac).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        String message = String.format("Annotation scene.annotations.FXMLResourceName not present in type %s", ctlClass.getName());
        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, message);
        throw new InternalException(message);
    }
    
    public static <C extends Controller> String getGlobalizationResourceName(Class<? extends C> ctlClass) {
        Class<GlobalizationResource> ac = GlobalizationResource.class;
        if (ctlClass.isAnnotationPresent(ac)) {
            String n = ctlClass.getAnnotation(ac).value();
            if (n != null && !n.isEmpty())
                return n;
        }
        Logger.getLogger(Controller.class.getName()).log(Level.WARNING,
                String.format("Annotation scene.annotations.GlobalizationResource not present in type %s", ctlClass.getName()));
        return App.GLOBALIZATION_RESOURCE_NAME;
    }

    public static class SetStageContextRW<C extends Controller> {
        private final ReadOnlyObjectWrapper<SetContentContext<C>> context;
        public SetContentContext<C> getContext() { return context.get(); }
        public ReadOnlyObjectProperty<SetContentContext<C>> contextProperty() { return context.getReadOnlyProperty(); }
        public SetStageContextRW() { context = new ReadOnlyObjectWrapper<>(new SetContentContext<>()); }
        public void setController(C value) { context.get().controller.set(value); }
        public void setStage(Stage value) { context.get().stage.set(value); }
        public void setResourceBundle(ResourceBundle value) { context.get().resourceBundle.set(value); }
        public void setError(Throwable value) { context.get().error.set(value); }
    }
    
    public static class SetContentContext<C extends Controller> {
        private final ReadOnlyObjectWrapper<C> controller = new ReadOnlyObjectWrapper<>();
        public C getController() { return controller.get(); }
        public ReadOnlyObjectProperty<C> controllerProperty() { return controller.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<Stage> stage = new ReadOnlyObjectWrapper<>();
        public Stage getStage() { return stage.get(); }
        public ReadOnlyObjectProperty<Stage> stageProperty() { return stage.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<Throwable> error = new ReadOnlyObjectWrapper<>();
        public Throwable getError() { return error.get(); }
        public ReadOnlyObjectProperty<Throwable> errorProperty() { return error.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<ResourceBundle> resourceBundle = new ReadOnlyObjectWrapper<>();
        public ResourceBundle getResourceBundle() { return resourceBundle.get(); }
        public ReadOnlyObjectProperty<ResourceBundle> resourceBundleProperty() { return resourceBundle.getReadOnlyProperty(); }
    }
}
