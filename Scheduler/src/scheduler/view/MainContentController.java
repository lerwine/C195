/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author erwinel
 */
public class MainContentController extends SchedulerController {
    private MainController mainController;
    
    public MainController getMainController() { return mainController; }

    @Override
    protected void onUnloaded(SchedulerController newController, Parent newParent) {
        mainController = null;
        super.onUnloaded(newController, newParent);
    }
    
    @Override
    public <C extends SchedulerController> C replaceView(Class<C> controllerClass) throws Exception {
        assert MainContentController.class.isAssignableFrom(controllerClass) : "View must inherit from MainContentController";
        return super.replaceView(controllerClass);
    }

    @Override
    public <C extends SchedulerController> C replaceView(Class<C> controllerClass, ViewControllerFactory<C> factory) throws Exception {
        assert MainContentController.class.isAssignableFrom(controllerClass) : "View must inherit from MainContentController";
        return super.replaceView(controllerClass, factory);
    }
    
    public static <C extends MainContentController> ViewControllerInitializer<C> createInitializer(MainController mainController, ViewControllerInitializer<C> initializer) {
        return new ViewControllerInitializer<C>() {
            @Override
            public void beforeLoad(FXMLLoader loader) {
                if (null != initializer)
                    initializer.beforeLoad(loader);
            }

            @Override
            public void onLoaded(C newController, Parent newView, SchedulerController currentController, Parent currentView) {
                if (null != initializer)
                    initializer.onLoaded(newController, newView, currentController, currentView);
                ((MainContentController)newController).mainController = mainController;
            }
            @Override
            public void onApplied(C currentController, Parent currentView, SchedulerController oldController, Parent oldView) {
                if (null != initializer)
                    initializer.onApplied(currentController, currentView, oldController, oldView);
            }
        };
    }
}
