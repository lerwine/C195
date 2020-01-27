package scheduler.view;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.util.Callback;
import sun.reflect.misc.ReflectUtil;

/**
 * Creates and initializes a JavaFX controller.
 * @author erwinel
 * @param <C> The JavaFX controller type.
 */
public interface ViewControllerFactory<C extends SchedulerController> extends ViewControllerInitializer<C>, Callback<Class<C>, C> {

    /**
     * Gets the dimensions for the initial {@link javafx.scene.Scene}.
     * @param controller The JavaFX controller for the view.
     * @param view The target view.
     * @return The dimensions for the initial {@link javafx.scene.Scene} or {@code null} if no explicit dimensions are required.
     */
    default Dimension2D getDimensions(C controller, Parent view) { return null; }
    
    /**
     * Composes a View Controller factory.
     * @param <C> The JavaFX controller type.
     * @param initializer Initializes the JavaFX controller.
     * @param baseFactory The base View Controller factory.
     * @return A composed View Controller factory that first calls the {@code baseFactory} and then calls the {@code initializer} for
     * each {@link ViewControllerInitializer} method.
     */
    public static <C extends SchedulerController> ViewControllerFactory<C> of(ViewControllerInitializer<C> initializer,
            ViewControllerFactory<C> baseFactory) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(baseFactory);
        return new ViewControllerFactory<C>() {
            @Override
            public void beforeLoad(FXMLLoader loader) {
                baseFactory.beforeLoad(loader);
                initializer.beforeLoad(loader);
            }

            @Override
            public void onLoaded(C newController, Parent newView, SchedulerController currentController, Parent currentView) {
                baseFactory.onLoaded(newController, newView, currentController, currentView);
                initializer.onLoaded(newController, newView, currentController, currentView);
            }

            @Override
            public void onApplied(C currentController, Parent currentView, SchedulerController oldController, Parent oldView) {
                try { baseFactory.onApplied(currentController, currentView, oldController, oldView); }
                finally { initializer.onApplied(currentController, currentView, oldController, oldView); }
            }

            @Override
            public C call(Class<C> param) { return baseFactory.call(param); }
        };
    }
    
    /**
     * Creates a View Controller factory.
     * @param <C> The JavaFX controller type.
     * @param initializer Initializes the JavaFX controller.
     * @return A View Controller factory that uses a default factory method and invokes the {@code initializer} for initialization.
     */
    public static <C extends SchedulerController> ViewControllerFactory<C> of(ViewControllerInitializer<C> initializer) {
        Objects.requireNonNull(initializer);
        return new ViewControllerFactory<C>() {
            @Override
            public void beforeLoad(FXMLLoader loader) { initializer.beforeLoad(loader); }

            @Override
            public void onLoaded(C newController, Parent newView, SchedulerController currentController, Parent currentView) {
                initializer.onLoaded(newController, newView, currentController, currentView);
            }

            @Override
            public void onApplied(C currentController, Parent currentView, SchedulerController oldController, Parent oldView) {
                initializer.onApplied(currentController, currentView, oldController, oldView);
            }

            @Override
            public C call(Class<C> param) {
                try {
                    return (C)ReflectUtil.newInstance(param);
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ViewControllerFactory.class.getName()).log(Level.SEVERE, "Error instantiating controller", ex);
                    throw new RuntimeException("Error instantiating controller", ex);
                }
            }
        };
    }
    
}
