/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scene;

import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 *
 * @author Leonard T. Erwine
 */
public class ListingController extends Controller {
    
    protected static <C extends ListingController> void setAsRootContent(Class<? extends C> ctlClass) {
        setAsRootContent(ctlClass, null);
    }
    
    protected static <C extends ListingController> void setAsRootContent(Class<? extends C> ctlClass, Consumer<SetContentContext<C>> onBeforeSetContent) {
        setAsRootContent(ctlClass, onBeforeSetContent, null);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    protected static <C extends ListingController> void setAsRootContent(Class<? extends C> ctlClass, Consumer<SetContentContext<C>> onBeforeSetContent,
                Consumer<SetContentContext<C>> onAfterSetScene) {
        SetStageContextRW<C> context = new SetStageContextRW<>();
        try {
            scheduler.App app = scheduler.App.getCurrent();
            Stage stage = app.getRootStage();
            context.setStage(app.getRootStage());
            ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), app.getCurrentLocale());
            context.setResourceBundle(rb);
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)), rb);
            Node content = loader.load();
            context.setController(loader.getController());
            if (onBeforeSetContent != null)
                onBeforeSetContent.accept(context.getContext());
           scene.RootController.getCurrent().setContent(content, context.getContext().getController());
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(ListingController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(ListingController.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error setting %s as root content", ctlClass.getName()), ex);
            context.setError(ex);
        }
        if (onAfterSetScene != null)
            onAfterSetScene.accept(context.getContext());
    }
}
