package scheduler.util;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
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

    public static StageListener createStageChangeHandler(ReadOnlyObjectProperty<Scene> sceneProperty, ChangeListener<Scene> onSceneChanged, ChangeListener<Stage> onStageChanged) {
        return new StageListener(sceneProperty) {
            private final WeakReference<ChangeListener<Scene>> sceneChangeListener = new WeakReference<>(onSceneChanged);
            private final WeakReference<ChangeListener<Stage>> stageChangeListener = new WeakReference<>(onStageChanged);

            {
                Scene scene = sceneProperty.get();
                if (null != scene) {
                    try {
                        onSceneChanged.changed(sceneProperty, null, scene);
                    } finally {
                        Window window = scene.getWindow();
                        if (null != window && window instanceof Stage) {
                            onStageChanged.changed(currentStageProperty(), null, (Stage) window);
                        }
                    }
                }
                getSceneProperty().addListener(this::onSceneChangedImpl);
                currentStageProperty().addListener(this::onStageChanged);
            }

            private synchronized void onSceneChangedImpl(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                ChangeListener<Scene> listener = sceneChangeListener.get();
                if (null != listener) {
                    listener.changed(observable, oldValue, newValue);
                } else {
                    getSceneProperty().removeListener(this::onSceneChangedImpl);
                }
            }

            private void onStageChanged(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
                ChangeListener<Stage> c = stageChangeListener.get();
                if (null != c) {
                    c.changed(currentStageProperty(), oldValue, newValue);
                } else {
                    currentStageProperty().removeListener(this::onStageChanged);
                }
            }
        };
    }

    public static StageListener createStageChangeHandler(ReadOnlyObjectProperty<Scene> sceneProperty, ChangeListener<Stage> listener) {
        return new StageListener(sceneProperty) {
            private final WeakReference<ChangeListener<Stage>> changeListener = new WeakReference<>(listener);

            {
                Scene scene = sceneProperty.get();
                if (null != scene) {
                    Window window = scene.getWindow();
                    if (null != window && window instanceof Stage) {
                        listener.changed(currentStageProperty(), null, (Stage) window);
                    }
                }
                currentStageProperty().addListener(this::onStageChanged);
            }

            private void onStageChanged(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
                ChangeListener<Stage> c = changeListener.get();
                if (null != c) {
                    c.changed(currentStageProperty(), oldValue, newValue);
                } else {
                    currentStageProperty().removeListener(this::onStageChanged);
                }
            }
        };
    }

    public static ParentWindowChangeListener createWindowChangeHandler(ReadOnlyObjectProperty<Scene> sceneProperty, ChangeListener<Scene> onSceneChanged, ChangeListener<Window> onWindowChanged) {
        return new ParentWindowChangeListener(sceneProperty) {
            private final WeakReference<ChangeListener<Scene>> sceneChangeListener = new WeakReference<>(onSceneChanged);
            private final WeakReference<ChangeListener<Window>> windowChangeListener = new WeakReference<>(onWindowChanged);

            {
                Scene scene = sceneProperty.get();
                if (null != scene) {
                    try {
                        onSceneChanged.changed(sceneProperty, null, scene);
                    } finally {
                        Window window = scene.getWindow();
                        if (null != window) {
                            onWindowChanged.changed(currentWindowProperty(), null, window);
                        }
                    }
                }
                getSceneProperty().addListener(this::onSceneChangedImpl);
                currentWindowProperty().addListener(this::onWindowChangedImpl);
            }

            private void onWindowChangedImpl(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                ChangeListener<Window> listener = windowChangeListener.get();
                if (null != listener) {
                    listener.changed(currentWindowProperty(), oldValue, newValue);
                } else {
                    currentWindowProperty().removeListener(this::onWindowChangedImpl);
                }
            }

            private synchronized void onSceneChangedImpl(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                ChangeListener<Scene> listener = sceneChangeListener.get();
                if (null != listener) {
                    listener.changed(observable, oldValue, newValue);
                } else {
                    getSceneProperty().removeListener(this::onSceneChangedImpl);
                }
            }

        };
    }

    public static ParentWindowChangeListener createWindowChangeHandler(ReadOnlyObjectProperty<Scene> sceneProperty, ChangeListener<Window> listener) {
        return new ParentWindowChangeListener(sceneProperty) {
            private final WeakReference<ChangeListener<Window>> changeListener = new WeakReference<>(listener);

            {
                currentWindowProperty().addListener(this::onWindowChangedImpl);
            }

            private void onWindowChangedImpl(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                ChangeListener<Window> c = changeListener.get();
                if (null != c) {
                    c.changed(currentWindowProperty(), oldValue, newValue);
                } else {
                    currentWindowProperty().removeListener(this::onWindowChangedImpl);
                }
            }
        };
    }

    private final ReadOnlyObjectProperty<Scene> sceneProperty;
    private final ReadOnlyObjectWrapper<Window> currentWindow;

    private ParentWindowChangeListener(ReadOnlyObjectProperty<Scene> sceneProperty) {
        this.sceneProperty = Objects.requireNonNull(sceneProperty);
        Scene scene = sceneProperty.get();
        currentWindow = new ReadOnlyObjectWrapper<>((null == scene) ? null : scene.getWindow());
        sceneProperty.addListener(this::onSceneChanged);
    }

    protected final ReadOnlyObjectProperty<Scene> getSceneProperty() {
        return sceneProperty;
    }

    public final Window getCurrentWindow() {
        return currentWindow.get();
    }

    public final ReadOnlyObjectProperty<Window> currentWindowProperty() {
        return currentWindow.getReadOnlyProperty();
    }

    private synchronized void onSceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        if (null != oldValue) {
            oldValue.windowProperty().removeListener(this::onWindowChanged);
        } else if (null == newValue) {
            return;
        }
        if (null != newValue) {
            newValue.windowProperty().addListener(this::onWindowChanged);
            onWindowChanged(newValue.windowProperty(), currentWindow.get(), newValue.getWindow());
        } else {
            Window oldWindow = currentWindow.get();
            if (null != oldWindow) {
                onWindowChanged(oldValue.windowProperty(), currentWindow.get(), null);
            }
        }
    }

    protected synchronized void onWindowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        currentWindow.set(newValue);
    }

    public static class StageListener extends ParentWindowChangeListener {

        private final ReadOnlyObjectWrapper<Stage> currentStage;

        private StageListener(ReadOnlyObjectProperty<Scene> sceneProperty) {
            super(sceneProperty);
            Window window = super.currentWindow.get();
            currentStage = new ReadOnlyObjectWrapper<>((null != window && window instanceof Stage) ? (Stage) window : null);
        }

        public Stage getCurrentStage() {
            return currentStage.get();
        }

        public ReadOnlyObjectProperty<Stage> currentStageProperty() {
            return currentStage.getReadOnlyProperty();
        }

        @Override
        protected synchronized void onWindowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
            try {
                super.onWindowChanged(observable, oldValue, newValue);
            } finally {
                if (null != newValue && newValue instanceof Stage) {
                    currentStage.set((Stage) newValue);
                } else if (null != currentStage.get()) {
                    currentStage.set(null);
                }
            }
        }

    }
}
