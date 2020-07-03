package scheduler.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.collections.ObservableListBase;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Tracks {@link Stage} visibility. This allows the application to get the currently visible stages when needed.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class StageManager extends ObservableListBase<Stage> {

    private static final Logger LOG = Logger.getLogger(StageManager.class.getName());

    public static final StageManager INSTANCE = new StageManager();
    private static final Object STAGE_PROPERTY_KEY = new Object();
    public static final StageStyle DEFAULT_STAGE_STYLE = StageStyle.UTILITY;

    public static Window getWindow(Node... node) {
        if (null != node) {
            for (Node n : node) {
                if (null != n) {
                    Scene scene = n.getScene();
                    if (null != scene) {
                        Window window = scene.getWindow();
                        if (null != window) {
                            return window;
                        }
                    }
                }
            }
        }
        return StageManager.getCurrentStage((Window) null);
    }

    private static Stage getCurrentStage() {
        Stage stage = getLast();
        if (null == stage && null == (stage = INSTANCE.showingStage)) {
            return INSTANCE.primaryStage;
        }
        return stage;
    }

    public static Stage getCurrentStage(Window window) {
        if (null != window) {
            if (window instanceof Stage) {
                return (Stage) window;
            }
            if (window instanceof PopupWindow) {
                return getCurrentStage(((PopupWindow) window).getOwnerWindow());
            }
        }
        return getCurrentStage();
    }

    public static Stage getCurrentStage(Scene scene) {
        if (null != scene) {
            return getCurrentStage(scene.getWindow());
        }
        return getCurrentStage();
    }

    public static Stage getCurrentStage(Node referenceNode) {
        if (null != referenceNode) {
            return getCurrentStage(referenceNode.getScene());
        }
        return getCurrentStage();
    }

    /**
     * Opens a new {@link Modality#APPLICATION_MODAL} window.
     *
     * @param content The {@link Scene#root} control for the new window.
     * @param owner The {@link Window} that will be the owner of the new window or {@code null} to use the latest {@link Window} loaded using this {@code StageManager}.
     * @param style Specifies the style for the new window ({@link Stage}) or {@code null} to use the {@link #DEFAULT_STAGE_STYLE}.
     * @param beforeShow The delegate to invoke after the {@code content} has been added to the {@link Scene} of the new {@link Stage}, but before it is shown.
     * @throws IOException if the {@code beforeShow} delegate throws an exception.
     * @throws IllegalStateException if {@code owner} is null and the view {@link scheduler.view.MainController} has not been initialized.
     * @throws NullPointerException if {@code content} is null.
     */
    public static void showAndWait(Parent content, Window owner, StageStyle style, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        if (null == owner && (null == (owner = getCurrentStage()))) {
            throw new IllegalStateException();
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle((null == style) ? DEFAULT_STAGE_STYLE : style);
        stage.setScene(new Scene(content));
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                LOG.entering(getClass().getName(), "handle", event);
                synchronized (INSTANCE.backingList) {
                    if (null != INSTANCE.showingStage && INSTANCE.showingStage == stage) {
                        INSTANCE.showingStage = null;
                    }
                }
                stage.removeEventHandler(WindowEvent.WINDOW_SHOWN, this);
            }
        });
        synchronized (INSTANCE.backingList) {
            INSTANCE.showingStage = stage;
            stage.initOwner(owner);
            register(stage, true);
        }
        if (null != beforeShow) {
            beforeShow.accept(stage);
        }
        stage.showAndWait();
    }

    /**
     * Opens a new {@link Modality#APPLICATION_MODAL} window.
     *
     * @param content The {@link Scene#root} control for the new window.
     * @param owner The {@link Window} that will be the owner of the new window or {@code null} to use the latest {@link Window} loaded using this {@code StageManager}.
     * @param style Specifies the style for the new window ({@link Stage}) or {@code null} to use the {@link #DEFAULT_STAGE_STYLE}.
     * @throws IllegalStateException if {@code owner} is null and the view {@link scheduler.view.MainController} has not been initialized.
     * @throws NullPointerException if {@code content} is null.
     */
    public static void showAndWait(Parent content, Window owner, StageStyle style) {
        if (null == owner && (null == (owner = getCurrentStage()))) {
            throw new IllegalStateException();
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle((null == style) ? DEFAULT_STAGE_STYLE : style);
        stage.setScene(new Scene(content));
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                LOG.entering(getClass().getName(), "handle", event);
                synchronized (INSTANCE.backingList) {
                    if (null != INSTANCE.showingStage && INSTANCE.showingStage == stage) {
                        INSTANCE.showingStage = null;
                    }
                }
                stage.removeEventHandler(WindowEvent.WINDOW_SHOWN, this);
            }
        });
        synchronized (INSTANCE.backingList) {
            INSTANCE.showingStage = stage;
            stage.initOwner(owner);
            register(stage, true);
        }
        stage.showAndWait();
    }

    /**
     * Opens a new {@link Modality#APPLICATION_MODAL} {@link StageStyle#UTILITY} window.
     *
     * @param content The {@link Scene#root} control for the new window.
     * @param owner The {@link Window} that will be the owner of the new window or {@code null} to use the latest {@link Window} loaded using this {@code StageManager}.
     * @param beforeShow The delegate to invoke after the {@code content} has been added to the {@link Scene} of the new {@link Stage}, but before it is shown.
     * @throws IOException if the {@code beforeShow} delegate throws an exception.
     * @throws IllegalStateException if {@code owner} is null and the view {@link scheduler.view.MainController} has not been initialized.
     * @throws NullPointerException if {@code content} is null.
     */
    public static void showAndWait(Parent content, Window owner, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        showAndWait(content, owner, DEFAULT_STAGE_STYLE, beforeShow);
    }

    /**
     * Opens a new {@link Modality#APPLICATION_MODAL} {@link StageStyle#UTILITY} window.
     *
     * @param content The {@link Scene#root} control for the new window.
     * @param owner The {@link Window} that will be the owner of the new window or {@code null} to use the latest {@link Window} loaded using this {@code StageManager}.
     * @throws IllegalStateException if {@code owner} is null and the view {@link scheduler.view.MainController} has not been initialized.
     * @throws NullPointerException if {@code content} is null.
     */
    public static void showAndWait(Parent content, Window owner) {
        showAndWait(content, owner, DEFAULT_STAGE_STYLE);
    }

    /**
     * Gets value indicating whether a {@link Stage} will be automatically un-registered with the {@code StageManager} when closed.
     *
     * @param stage The target {@link Stage}.
     * @return An {@link Optional} {@code true} value if the {@link Stage} will be automatically un-registered when closed; An {@link Optional} {@code false} value if the
     * {@link Stage} will remain registered when closed; Otherwise, an {@link Optional#EMPTY} value if the {@link Stage} is not registered with the {@code StageManager}.
     */
    public static Optional<Boolean> getUnregisterWhenHidden(Stage stage) {
        ObservableMap<Object, Object> properties = stage.getProperties();
        synchronized (properties) {
            if (properties.containsKey(STAGE_PROPERTY_KEY)) {
                return Optional.of((boolean) properties.get(STAGE_PROPERTY_KEY));
            }
        }
        return Optional.empty();
    }

    /**
     * Executes a {@link Runnable} if the {@link Stage} is registered with the {@code StageManager}.
     *
     * @param stage The target {@link Stage}.
     * @param runnable The {@link Runnable} to invoke if the {@code stage} is registered with the {@code StageManager}.
     * @return {@code true} if the {@link Stage} was registered with the {@code StageManager}; otherwise {@code false}.
     */
    public static boolean ifRegistered(Stage stage, Runnable runnable) {
        ObservableMap<Object, Object> properties = stage.getProperties();
        synchronized (properties) {
            if (properties.containsKey(STAGE_PROPERTY_KEY)) {
                runnable.run();
                return true;
            }
        }
        return false;
    }

    private static boolean setUnregisterWhenHidden(Stage stage, boolean value) {
        ObservableMap<Object, Object> properties = stage.getProperties();
        synchronized (properties) {
            if (properties.containsKey(STAGE_PROPERTY_KEY)) {
                properties.put(STAGE_PROPERTY_KEY, value);
                return false;
            }
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, INSTANCE::onStageShown);
            stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, INSTANCE::onStageHidden);
            properties.put(STAGE_PROPERTY_KEY, value);
        }
        return true;
    }

    private static Optional<Boolean> removeUnregisterWhenHidden(Stage stage) {
        ObservableMap<Object, Object> properties = stage.getProperties();
        synchronized (properties) {
            if (properties.containsKey(STAGE_PROPERTY_KEY)) {
                boolean result = (boolean) properties.get(STAGE_PROPERTY_KEY);
                properties.remove(STAGE_PROPERTY_KEY);
                stage.removeEventHandler(WindowEvent.WINDOW_SHOWN, INSTANCE::onStageShown);
                stage.removeEventHandler(WindowEvent.WINDOW_HIDDEN, INSTANCE::onStageHidden);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    /**
     * Tests whether a {@link Stage} is registered with the {@code StageManager}.
     *
     * @param stage The target {@link Stage}.
     * @return {@code true} if the {@link Stage} was registered with the {@code StageManager}; otherwise {@code false}.
     */
    public static boolean isRegistered(Stage stage) {
        return null != stage && stage.getProperties().containsKey(STAGE_PROPERTY_KEY);
    }

    /**
     * This is to only be called once upon application startup.
     *
     * @param primaryStage The application primary stage.
     * @throws IllegalStateException The primary stage was already set.
     */
    public static void setPrimaryStage(Stage primaryStage) {
        synchronized (primaryStage) {
            if (null != INSTANCE.primaryStage) {
                throw new IllegalStateException();
            }
            INSTANCE.primaryStage = Objects.requireNonNull(primaryStage);
        }
        INSTANCE.beginChange();
        try {
            synchronized (INSTANCE.backingList) {
                if (setUnregisterWhenHidden(primaryStage, false) && primaryStage.isShowing()) {
                    INSTANCE.backingList.addFirst(primaryStage);
                    INSTANCE.nextAdd(0, 1);
                }
            }
        } finally {
            INSTANCE.endChange();
        }
    }

    /**
     * Registers a stage so it will be added to this collection when it is visible and removed when it is not.
     *
     * @param stage The {@link Stage} to register.
     * @param unregisterWhenHidden {@code true} to automatically unregister after the stage is hidden; otherwise {@code false} if it is to remain registered after it is hidden.
     * @return {@code true} if the {@link Stage} was registered; otherwise, {@code false} if it was already registered.
     */
    public static boolean register(Stage stage, boolean unregisterWhenHidden) {
        ObservableMap<Object, Object> properties = stage.getProperties();
        if (properties.containsKey(STAGE_PROPERTY_KEY)) {
            return false;
        }
        INSTANCE.beginChange();
        try {
            synchronized (INSTANCE.backingList) {
                if (setUnregisterWhenHidden(stage, unregisterWhenHidden)) {
                    if (stage.isShowing()) {
                        int index = INSTANCE.backingList.size();
                        INSTANCE.backingList.addLast(stage);
                        INSTANCE.nextAdd(index, index + 1);
                    }
                } else {
                    return false;
                }
            }
        } finally {
            INSTANCE.endChange();
        }
        return true;
    }

    /**
     * Unregisters a {@link Stage} so it will no longer be added to and removed from this collection when it shown and hidden.
     *
     * @param stage The {@link Stage} to unregister.
     * @return {@code true} if the {@link Stage} was unregistered; otherwise, {@code false} if it had not been registered.
     */
    public static boolean unregister(Stage stage) {
        ObservableMap<Object, Object> properties = stage.getProperties();
        if (!properties.containsKey(STAGE_PROPERTY_KEY)) {
            return false;
        }
        INSTANCE.beginChange();
        try {
            synchronized (INSTANCE.backingList) {
                if (removeUnregisterWhenHidden(stage).isPresent()) {
                    int index = INSTANCE.backingList.indexOf(stage);
                    if (index >= 0) {
                        INSTANCE.backingList.remove(stage);
                        INSTANCE.nextRemove(index, stage);
                    }
                    return true;
                }
            }
        } finally {
            INSTANCE.endChange();
        }
        return false;
    }

    /**
     * Iterates through the {@link Stage} objects starting with the last one that was added.
     *
     * @return An {@link Iterator} that iterates through the {@link Stage} objects in reverse order, starting with the last one that was added.
     */
    public static Iterator<Stage> descendingIterator() {
        return INSTANCE.backingList.descendingIterator();
    }

    /**
     * Gets the last {@link Stage} that was added.
     *
     * @return The last {@link Stage} that was added.
     */
    public static Stage getLast() {
        synchronized (INSTANCE.backingList) {
            if (!INSTANCE.backingList.isEmpty()) {
                return INSTANCE.backingList.getLast();
            }
        }
        return INSTANCE.primaryStage;
    }

    public static Stage getPrimaryStage() {
        return INSTANCE.primaryStage;
    }

    private final LinkedList<Stage> backingList = new LinkedList<>();
    private Stage primaryStage;
    private Stage showingStage;

    // Singleton instance
    private StageManager() {
    }

    @Override
    public Stage get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }

    private void onStageShown(WindowEvent event) {
        LOG.entering(getClass().getName(), "onStageShown", event);
        Stage stage = (Stage) event.getSource();
        synchronized (backingList) {
            ifRegistered(stage, () -> {
                beginChange();
                try {
                    int index = backingList.size();
                    backingList.addLast(stage);
                    nextAdd(index, index + 1);
                } finally {
                    endChange();
                }
            });
        }
    }

    private void onStageHidden(WindowEvent event) {
        LOG.entering(getClass().getName(), "onStageHidden", event);
        Stage stage = (Stage) event.getSource();
        synchronized (stage) {
            Optional<Boolean> unregisterWhenHidden = removeUnregisterWhenHidden(stage);
            unregisterWhenHidden.ifPresent((Boolean b) -> {
                if (b) {
                    unregister(stage);
                }
                int index = backingList.indexOf(stage);
                if (index >= 0) {
                    beginChange();
                    try {
                        backingList.remove(index);
                        nextRemove(index, stage);
                    } finally {
                        endChange();
                    }
                }
            });
        }
    }

}
