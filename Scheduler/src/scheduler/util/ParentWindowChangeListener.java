package scheduler.util;

import com.sun.javafx.event.EventHandlerManager;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ParentWindowChangeListener implements EventTarget {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ParentWindowChangeListener.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(ParentWindowChangeListener.class.getName());

    private final EventHandlerManager eventHandlerManager;
    private final ReadOnlyObjectProperty<Scene> sceneProperty;
    private final ReadOnlyObjectWrapper<Window> currentWindow;
    private final ReadOnlyObjectWrapper<Stage> currentStage;
    private final SimpleObjectProperty<EventHandler<WindowEvent>> onCloseRequest;
    private final SimpleObjectProperty<EventHandler<WindowEvent>> onWindowNotNullAndShown;
    private final SimpleObjectProperty<EventHandler<WindowEvent>> onWindowNullOrHidden;
    private final SimpleObjectProperty<EventHandler<WindowEvent>> onStageNotNullAndShown;
    private final SimpleObjectProperty<EventHandler<WindowEvent>> onStageNullOrHidden;

    public ParentWindowChangeListener(ReadOnlyObjectProperty<Scene> sceneProperty) {
        LOG.entering(LOG.getName(), "<init>", sceneProperty);
        eventHandlerManager = new EventHandlerManager(this);
        onWindowNotNullAndShown = new SimpleObjectProperty<>();
        onWindowNullOrHidden = new SimpleObjectProperty<>();
        onStageNotNullAndShown = new SimpleObjectProperty<>();
        onStageNullOrHidden = new SimpleObjectProperty<>();
        onCloseRequest = new SimpleObjectProperty<>();
        this.sceneProperty = Objects.requireNonNull(sceneProperty);
        currentWindow = new ReadOnlyObjectWrapper<>();
        currentStage = new ReadOnlyObjectWrapper<>();
        Scene scene = sceneProperty.get();
        if (null != scene) {
            onSceneChanged(this.sceneProperty, null, scene);
        }
        sceneProperty.addListener(this::onSceneChanged);
        currentWindow.addListener(this::onCurrentWindowChanged);
        currentStage.addListener(this::onCurrentStageChanged);
        onCloseRequest.addListener(this::onCloseRequestHandlerChanged);
        onWindowNotNullAndShown.addListener(this::windowNotNullAndShownHandlerChanged);
        onWindowNullOrHidden.addListener(this::windowNullOrHiddenHandlerChanged);
        eventHandlerManager.addEventHandler(WindowEvent.WINDOW_SHOWN, this::onWindowShown);
        eventHandlerManager.addEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowHidden);
        LOG.exiting(LOG.getName(), "<init>");
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

    public Stage getCurrentStage() {
        return currentStage.get();
    }

    public ReadOnlyObjectProperty<Stage> currentStageProperty() {
        return currentStage.getReadOnlyProperty();
    }

    public EventHandler<WindowEvent> getOnCloseRequest() {
        return onCloseRequest.get();
    }

    public void setOnCloseRequest(EventHandler<WindowEvent> value) {
        onCloseRequest.set(value);
    }

    public ObjectProperty<EventHandler<WindowEvent>> onCloseRequestProperty() {
        return onCloseRequest;
    }

    public EventHandler<WindowEvent> getOnWindowNotNullAndShown() {
        return onWindowNotNullAndShown.get();
    }

    public void setOnWindowNotNullAndShown(EventHandler<WindowEvent> value) {
        onWindowNotNullAndShown.set(value);
    }

    public ObjectProperty<EventHandler<WindowEvent>> onWindowNotNullAndShownProperty() {
        return onWindowNotNullAndShown;
    }

    public EventHandler<WindowEvent> getOnWindowNullOrHidden() {
        return onWindowNullOrHidden.get();
    }

    public void setOnWindowNullOrHidden(EventHandler<WindowEvent> value) {
        onWindowNullOrHidden.set(value);
    }

    public ObjectProperty<EventHandler<WindowEvent>> onWindowNullOrHiddenProperty() {
        return onWindowNullOrHidden;
    }

    public EventHandler<WindowEvent> getOnStageNotNullAndShown() {
        return onStageNotNullAndShown.get();
    }

    public void setOnStageNotNullAndShown(EventHandler<WindowEvent> value) {
        onStageNotNullAndShown.set(value);
    }

    public ObjectProperty<EventHandler<WindowEvent>> onStageNotNullAndShownProperty() {
        return onStageNotNullAndShown;
    }

    public EventHandler<WindowEvent> getOnStageNullOrHidden() {
        return onStageNullOrHidden.get();
    }

    public void setOnStageNullOrHidden(EventHandler<WindowEvent> value) {
        onStageNullOrHidden.set(value);
    }

    public ObjectProperty<EventHandler<WindowEvent>> onStageNullOrHiddenProperty() {
        return onStageNullOrHidden;
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        EventDispatchChain result = tail.append(eventHandlerManager);
        LOG.exiting(LOG.getName(), "buildEventDispatchChain");
        return result;
    }

    public void addEventHandler(EventType<WindowEvent> type, EventHandler<WindowEvent> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    public void addEventFilter(EventType<WindowEvent> type, EventHandler<WindowEvent> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    public void removeEventHandler(EventType<WindowEvent> type, EventHandler<WindowEvent> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    public void removeEventFilter(EventType<WindowEvent> type, EventHandler<WindowEvent> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

    public void hide() {
        LOG.entering(LOG.getName(), "hide");
        Window window = getCurrentWindow();
        if (null != window) {
            window.hide();
        }
        LOG.exiting(LOG.getName(), "hide");
    }

    private synchronized void onCloseRequestHandlerChanged(ObservableValue<? extends EventHandler<WindowEvent>> observable, EventHandler<WindowEvent> oldValue,
            EventHandler<WindowEvent> newValue) {
        LOG.entering(LOG.getName(), "onCloseRequestHandlerChanged", new Object[]{oldValue, newValue});
        if (null == oldValue) {
            if (null != newValue) {
                eventHandlerManager.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, newValue);
            }
        } else if (null == newValue) {
            eventHandlerManager.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, oldValue);
        } else if (oldValue != newValue) {
            eventHandlerManager.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, oldValue);
            eventHandlerManager.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, newValue);
        }
        LOG.exiting(LOG.getName(), "onCloseRequestHandlerChanged");
    }

    private void windowNotNullAndShownHandlerChanged(ObservableValue<? extends EventHandler<WindowEvent>> observable, EventHandler<WindowEvent> oldValue,
            EventHandler<WindowEvent> newValue) {
        LOG.entering(LOG.getName(), "windowNotNullAndShownHandlerChanged", new Object[]{oldValue, newValue});
        if (null == oldValue) {
            if (null != newValue) {
                eventHandlerManager.addEventHandler(WindowEvent.WINDOW_SHOWN, newValue);
            }
        } else if (null == newValue) {
            eventHandlerManager.removeEventHandler(WindowEvent.WINDOW_SHOWN, oldValue);
        } else if (oldValue != newValue) {
            eventHandlerManager.removeEventHandler(WindowEvent.WINDOW_SHOWN, oldValue);
            eventHandlerManager.addEventHandler(WindowEvent.WINDOW_SHOWN, newValue);
        }
        LOG.exiting(LOG.getName(), "windowNotNullAndShownHandlerChanged");
    }

    private void windowNullOrHiddenHandlerChanged(ObservableValue<? extends EventHandler<WindowEvent>> observable, EventHandler<WindowEvent> oldValue,
            EventHandler<WindowEvent> newValue) {
        LOG.entering(LOG.getName(), "windowNullOrHiddenHandlerChanged", new Object[]{oldValue, newValue});
        if (null == oldValue) {
            if (null != newValue) {
                eventHandlerManager.addEventHandler(WindowEvent.WINDOW_HIDDEN, newValue);
            }
        } else if (null == newValue) {
            eventHandlerManager.removeEventHandler(WindowEvent.WINDOW_HIDDEN, oldValue);
        } else if (oldValue != newValue) {
            eventHandlerManager.removeEventHandler(WindowEvent.WINDOW_HIDDEN, oldValue);
            eventHandlerManager.addEventHandler(WindowEvent.WINDOW_HIDDEN, newValue);
        }
        LOG.exiting(LOG.getName(), "windowNullOrHiddenHandlerChanged");
    }

    private synchronized void onSceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        LOG.entering(LOG.getName(), "onSceneChanged", new Object[]{oldValue, newValue});
        ReadOnlyObjectProperty<Window> property;
        if (null != newValue) {
            property = newValue.windowProperty();
            if (null != oldValue) {
                ReadOnlyObjectProperty<Window> oldProperty;
                if (oldValue == newValue || (oldProperty = oldValue.windowProperty()) == property) {
                    LOG.exiting(LOG.getName(), "onSceneChanged");
                    return;
                }
                oldProperty.removeListener(this::onSceneWindowChanged);
            }
            onSceneWindowChanged(property, currentWindow.get(), newValue.getWindow());
            property.addListener(this::onSceneWindowChanged);
        } else if (null != oldValue) {
            property = oldValue.windowProperty();
            property.removeListener(this::onSceneWindowChanged);
            onSceneWindowChanged(property, currentWindow.get(), null);
        }
        LOG.exiting(LOG.getName(), "onSceneChanged");
    }

    protected synchronized void onSceneWindowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        LOG.entering(LOG.getName(), "onSceneWindowChanged", new Object[]{oldValue, newValue});
        if (Values.notSameInstance(oldValue, newValue)) {
            currentWindow.set(newValue);
        }
        LOG.exiting(LOG.getName(), "onSceneWindowChanged");
    }

    protected synchronized void onCurrentWindowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        LOG.entering(LOG.getName(), "onCurrentWindowChanged", new Object[]{oldValue, newValue});
        if (Values.areSameInstance(oldValue, newValue)) {
            return;
        }

        if (null != newValue) {
            if (null != oldValue) {
                if (oldValue == newValue) {
                    return;
                }
                oldValue.removeEventHandler(WindowEvent.WINDOW_SHOWING, this::onWindowEvent);
                oldValue.removeEventHandler(WindowEvent.WINDOW_SHOWN, this::onWindowEvent);
                oldValue.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this::onWindowEvent);
                oldValue.removeEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowEvent);
                oldValue.removeEventHandler(WindowEvent.WINDOW_HIDING, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_SHOWING, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_SHOWN, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_HIDING, this::onWindowEvent);

                try {
                    if (newValue instanceof Stage) {
                        currentStage.set((Stage) newValue);
                    } else if (oldValue instanceof Stage) {
                        currentStage.set(null);
                    }
                } finally {
                    if (oldValue.isShowing() != newValue.isShowing()) {
                        WindowEvent event = new WindowEvent(newValue, (newValue.isShowing()) ? WindowEvent.WINDOW_SHOWN : WindowEvent.WINDOW_HIDDEN);
                        LOG.fine(() -> String.format("Firing event %s", event));
                        Event.fireEvent(this, event);
                    }
                }
            } else {
                newValue.addEventHandler(WindowEvent.WINDOW_SHOWING, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_SHOWN, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowEvent);
                newValue.addEventHandler(WindowEvent.WINDOW_HIDING, this::onWindowEvent);
                try {
                    if (newValue instanceof Stage) {
                        currentStage.set((Stage) newValue);
                    }
                } finally {
                    if (newValue.isShowing()) {
                        WindowEvent event = new WindowEvent(newValue, WindowEvent.WINDOW_SHOWN);
                        LOG.fine(() -> String.format("Firing event %s", event));
                        Event.fireEvent(this, event);
                    }
                }
            }
        } else if (null != oldValue) {
            oldValue.removeEventHandler(WindowEvent.WINDOW_SHOWING, this::onWindowEvent);
            oldValue.removeEventHandler(WindowEvent.WINDOW_SHOWN, this::onWindowEvent);
            oldValue.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this::onWindowEvent);
            oldValue.removeEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowEvent);
            oldValue.removeEventHandler(WindowEvent.WINDOW_HIDING, this::onWindowEvent);
            try {
                if (oldValue instanceof Stage) {
                    currentStage.set(null);
                }
            } finally {
                if (oldValue.isShowing()) {
                    WindowEvent event = new WindowEvent(newValue, WindowEvent.WINDOW_HIDDEN);
                    LOG.fine(() -> String.format("Firing event %s", event));
                    Event.fireEvent(this, event);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onCurrentWindowChanged");
    }

    protected synchronized void onCurrentStageChanged(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
        LOG.entering(LOG.getName(), "onCurrentStageChanged", new Object[]{oldValue, newValue});
        if (null == oldValue) {
            if (null != newValue && newValue.isShowing()) {
                EventHandler<WindowEvent> handler = onStageNotNullAndShown.get();
                if (null != handler) {
                    WindowEvent event = new WindowEvent(newValue, WindowEvent.WINDOW_SHOWN);
                    handler.handle(event);
                }
            }
        } else if (null == newValue) {
            if (oldValue.isShowing()) {
                EventHandler<WindowEvent> handler = onStageNullOrHidden.get();
                if (null != handler) {
                    WindowEvent event = new WindowEvent(oldValue, WindowEvent.WINDOW_HIDDEN);
                    handler.handle(event);
                }
            }
        } else if (oldValue != newValue) {
            if (newValue.isShowing()) {
                EventHandler<WindowEvent> handler = onStageNotNullAndShown.get();
                if (null != handler) {
                    WindowEvent event = new WindowEvent(newValue, WindowEvent.WINDOW_SHOWN);
                    handler.handle(event);
                }
            } else if (oldValue.isShowing()) {
                EventHandler<WindowEvent> handler = onStageNullOrHidden.get();
                if (null != handler) {
                    WindowEvent event = new WindowEvent(oldValue, WindowEvent.WINDOW_HIDDEN);
                    handler.handle(event);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onCurrentStageChanged");
    }

    private synchronized void onWindowEvent(WindowEvent event) {
        LOG.entering(LOG.getName(), "onWindowEvent", event);
        WindowEvent copy = event.copyFor(event.getSource(), this);
        Event.fireEvent(this, copy);
        LOG.exiting(LOG.getName(), "onWindowEvent");
    }

    private synchronized void onWindowShown(WindowEvent event) {
        LOG.entering(LOG.getName(), "onWindowShown", event);
        Stage stage = currentStage.get();
        if (null != stage) {
            EventHandler<WindowEvent> handler = onStageNotNullAndShown.get();
            if (null != handler) {
                handler.handle(event);
            }
        }
        LOG.exiting(LOG.getName(), "onWindowShown");
    }

    private synchronized void onWindowHidden(WindowEvent event) {
        LOG.entering(LOG.getName(), "onWindowHidden", event);
        Stage stage = currentStage.get();
        if (null != stage) {
            EventHandler<WindowEvent> handler = onStageNullOrHidden.get();
            if (null != handler) {
                handler.handle(event);
            }
        }
        LOG.exiting(LOG.getName(), "onWindowHidden");
    }

}
