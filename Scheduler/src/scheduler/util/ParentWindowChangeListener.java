package scheduler.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ParentWindowChangeListener {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ParentWindowChangeListener.class.getName()), Level.FINER);

    private final ReadOnlyObjectWrapper<Window> currentWindow;
    private final ReadOnlyObjectWrapper<Stage> currentStage;

    public ParentWindowChangeListener() {
        currentWindow = new ReadOnlyObjectWrapper<>(null);
        currentStage = new ReadOnlyObjectWrapper<>(null);
        currentWindow.addListener(this::onWindowChanged);
        currentStage.addListener(this::onStageChanged);
    }

    public void initialize(ReadOnlyObjectProperty<Scene> sceneProperty) {
        sceneProperty.addListener(this::onSceneChanged);
        onSceneChanged(sceneProperty, null, sceneProperty.get());
    }

    public Window getCurrentWindow() {
        return currentWindow.get();
    }

    public ReadOnlyObjectProperty<Window> currentWindowProperty() {
        return currentWindow.getReadOnlyProperty();
    }

    private synchronized void onSceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        if (null != oldValue) {
            oldValue.windowProperty().removeListener(this::onWindowChangedImpl);
        }
        if (null != newValue) {
            newValue.windowProperty().addListener(this::onWindowChangedImpl);
            currentWindow.set(newValue.getWindow());
        } else {
            currentWindow.set(null);
        }
    }

    private synchronized void onWindowChangedImpl(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        currentWindow.set(newValue);
        if (null == newValue) {
            currentStage.set(null);
        } else if (newValue instanceof Stage) {
            currentStage.set((Stage) newValue);
        }
    }

    protected void onWindowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {

    }

    protected void onStageChanged(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {

    }

}
