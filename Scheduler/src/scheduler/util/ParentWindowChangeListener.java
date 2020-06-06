package scheduler.util;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class ParentWindowChangeListener implements ChangeListener<Window> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ParentWindowChangeListener.class.getName()), Level.FINER);

    public static ParentWindowChangeListener setWindowChangeListener(Node target, ChangeListener<Window> listener) {
        Objects.requireNonNull(listener);
        Scene scene = target.getScene();
        ReadOnlyObjectProperty<Window> windowProperty = (null == scene) ? null : scene.windowProperty();
        ParentWindowChangeListener result = new ParentWindowChangeListener(windowProperty) {
            @Override
            public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                LOG.fine(() -> String.format("Window changed from %s to %s", (null == oldValue) ? "null" : "not null",
                        (null == newValue) ? "null" : "not null"));
                listener.changed(observable, oldValue, newValue);
            }
        };
        target.sceneProperty().addListener(result::onScenechanged);
        if (null != result.currentWindow) {
            listener.changed(result.windowProperty, null, result.currentWindow);
        }
        return result;
    }

    private Window currentWindow;
    private ReadOnlyObjectProperty<Window> windowProperty;

    public ParentWindowChangeListener(ReadOnlyObjectProperty<Window> windowProperty) {
        this.windowProperty = windowProperty;
        currentWindow = (null == windowProperty) ? null : windowProperty.get();
    }

    public Window getCurrentWindow() {
        return currentWindow;
    }

    public void onScenechanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        LOG.fine(() -> String.format("Scene changed from %s to %s", (null == oldValue) ? "null" : "not null",
                (null == newValue) ? "null" : "not null"));
        if (null != windowProperty) {
            windowProperty.removeListener(this);
        }
        Window oldWindow = currentWindow;
        if (null != newValue) {
            currentWindow = (windowProperty = newValue.windowProperty()).get();
            windowProperty.addListener(this);
        } else {
            currentWindow = null;
        }
        if (!Objects.equals(oldWindow, currentWindow)) {
            changed((null == windowProperty) ? oldValue.windowProperty() : windowProperty, oldWindow, currentWindow);
        }
    }

}
