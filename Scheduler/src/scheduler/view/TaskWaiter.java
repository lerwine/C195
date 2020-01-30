package scheduler.view;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import scheduler.App;
import scheduler.util.DbConnectedCallable;
import scheduler.util.DbConnectionConsumer;
import scheduler.util.DbConnector;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Controller / Task for showing busy indicator while background process is running.
 * 
 * @author erwinel
 * @param <T> Type of value produced by the task.
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/TaskWaiter.fxml")
public abstract class TaskWaiter<T> extends Task<T> {
    private static final Logger LOG = Logger.getLogger(TaskWaiter.class.getName());
    
    private final Window owner;
    private Popup popup;
    
    @FXML // ResourceBundle injected by the FXMLLoader
    private ResourceBundle resources;

    @FXML
    private Label headingLabel;

    @FXML
    private Label operationLabel;

    @FXML
    private Button cancelButton;

    public TaskWaiter(Window owner) {
        this(owner, null);
    }
    
    public TaskWaiter(Window owner, String operation) {
        this(owner, operation, null);
    }
    
    public TaskWaiter(Window owner, String operation, String heading) {
        super();
        this.owner = Objects.requireNonNull(owner);
        titleProperty().addListener((observable) -> {
            if (headingLabel != null)
                updateHeadingLabel();
        });
        messageProperty().addListener((observable) -> {
            if (operationLabel != null)
                updateOperationLabel();
        });
        updateTitle((null == heading) ? "" : heading);
        updateMessage((null == operation) ? "" : operation);
    }

    private void updateHeadingLabel() {
        if (null == headingLabel)
            return;
        String s = getTitle();
        if (s == null || s.trim().isEmpty())
            SchedulerController.collapseNode(headingLabel);
        else
            SchedulerController.restoreLabeled(headingLabel, s);
    }

    private void updateOperationLabel() {
        if (null == operationLabel)
            return;
        String s = getMessage();
        if (s == null || s.trim().isEmpty())
            SchedulerController.collapseNode(operationLabel);
        else
            SchedulerController.restoreLabeled(operationLabel, s);
    }
    
    public static void schedule(TaskWaiter<?> task, long delay, TimeUnit unit) {
        ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
        try {
            svc.schedule(task, delay, unit);
        } finally {
            svc.shutdown();
        }
    }
    
    public static void execute(TaskWaiter<?> task) {
        ExecutorService svc = Executors.newSingleThreadExecutor();
        try {
            svc.execute(task);
        } finally {
            svc.shutdown();
        }
    }
    
    @FXML
    void initialize() {
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert operationLabel != null : "fx:id=\"operationLabel\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        cancelButton.setOnAction((event) -> {
            cancelButton.setOnAction(null);
            if (!isCancelled())
                cancel(true);
        });
        if (getTitle().trim().isEmpty() && getMessage().trim().isEmpty()) {
            updateTitle(resources.getString(App.RESOURCEKEY_PLEASEWAIT));
            updateMessage(resources.getString(App.RESOURCEKEY_CONNECTINGTODB));
        }
        updateHeadingLabel();
        updateOperationLabel();
    }

    @Override
    protected T call() throws Exception {
        LOG.log(Level.INFO, "Task called");
        if (Platform.isFxApplicationThread())
            showPopup();
        else
            Platform.runLater(() -> {
                showPopup();
            });
        try {
            LOG.log(Level.INFO, "Getting result");
            return getResult();
        } finally {
            if (Platform.isFxApplicationThread())
                hidePopup();
            else
                Platform.runLater(() -> {
                    hidePopup();
                });
        }
    }

    private void showPopup() throws RuntimeException {
        LOG.log(Level.INFO, "showPopup called");
        ResourceBundle rb = ResourceBundle.getBundle(SchedulerController.getGlobalizationResourceName(TaskWaiter.class),
                Locale.getDefault(Locale.Category.DISPLAY));
        FXMLLoader loader = new FXMLLoader(TaskWaiter.class.getResource(SchedulerController.getFXMLResourceName(TaskWaiter.class)), rb);
        loader.setController(this);
        final Parent newParent;
        try {
            newParent = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading FXML", ex);
            throw new RuntimeException("Error loading FXML", ex);
        }
        popup = new Popup();
        popup.setHideOnEscape(false);
        popup.setAutoHide(false);
        popup.setAutoFix(true);
        popup.getContent().add(newParent);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
        popup.show(owner);
        popup.setWidth(owner.getWidth());
        popup.setHeight(owner.getHeight());
    }
    
    private void hidePopup() {
        LOG.log(Level.INFO, "hidePopup called");
        cancelButton.setOnAction(null);
        popup.hide();
    }

    public static TaskWaiter<?> fromConsumer(Window window, DbConnectionConsumer consumer) {
        return fromConsumer(window, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(Window window, String operation, DbConnectionConsumer consumer) {
        return fromConsumer(window, operation, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(Window window, String operation, String heading, DbConnectionConsumer consumer) {
        return new TaskWaiterImpl<>(window, operation, heading, () -> {
           DbConnector.apply(consumer);
           return null;
        });
    }
    
    public static void acceptAsync(Window window, String operation, String heading, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(window, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, null, ex);
                onError.accept(ex);
                return;
            }
            onSuccess.run();
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }
    
    public static void acceptAsync(Window window, String operation, String heading, DbConnectionConsumer consumer, Runnable onSuccess) {
        TaskWaiter<?> task = fromConsumer(window, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return;
            }
            onSuccess.run();
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }
    
    public static void acceptAsync(Window window, String operation, String heading, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(window, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, null, ex);
                onError.accept(ex);
            }
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }
    
    public static TaskWaiter<?> acceptAsync(Window window, String operation, String heading, DbConnectionConsumer consumer) {
        TaskWaiter<?> task = fromConsumer(window, operation, heading, consumer);
        execute(task);
        return task;
    }
    
    public static void acceptAsync(Window window, String operation, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(window, operation, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(Window window, String operation, DbConnectionConsumer consumer, Runnable onSuccess) {
        acceptAsync(window, operation, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(Window window, String operation, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        acceptAsync(window, operation, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(Window window, String operation, DbConnectionConsumer consumer) {
        return acceptAsync(window, operation, null, consumer);
    }
    
    public static void acceptAsync(Window window, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(window, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(Window window, DbConnectionConsumer consumer, Runnable onSuccess) {
        acceptAsync(window, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(Window window, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        acceptAsync(window, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(Window window, DbConnectionConsumer consumer) {
        return acceptAsync(window, null, consumer);
    }
    
    public static <T> TaskWaiter<T> fromCallable(Window window, DbConnectedCallable<T> callable) {
        return fromCallable(window, null, callable);
    }
    
    public static <T> TaskWaiter<T> fromCallable(Window window, String operation, DbConnectedCallable<T> callable) {
        return fromCallable(window, operation, null, callable);
    }
    
    public static <T> TaskWaiter<T> fromCallable(Window window, String operation, String heading, DbConnectedCallable<T> callable) {
        return new TaskWaiterImpl<>(window, operation, heading, () -> DbConnector.call(callable));
    }

    public static <T> void callAsync(Window window, String operation, String heading, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(window, operation, heading, () -> DbConnector.call(callable));
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            T result;
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, null, ex);
                onError.accept(ex);
                return;
            }
            onSuccess.accept(result);
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }

    public static <T> void callAsync(Window window, String operation, String heading, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(window, operation, heading, () -> DbConnector.call(callable));
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            T result;
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return;
            }
            onSuccess.accept(result);
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }

    public static <T> TaskWaiter<T> callAsync(Window window, String operation, String heading, DbConnectedCallable<T> callable) {
        return new TaskWaiterImpl<>(window, operation, heading, () -> DbConnector.call(callable));
    }

    public static <T> void callAsync(Window window, String operation, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        callAsync(window, operation, null, callable, onSuccess, onError);
    }

    public static <T> void callAsync(Window window, String operation, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        callAsync(window, operation, null, callable, onSuccess);
    }

    public static <T> TaskWaiter<T> callAsync(Window window, String operation, DbConnectedCallable<T> callable) {
        return callAsync(window, operation, null, callable);
    }

    public static <T> void callAsync(Window window, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        callAsync(window, null, callable, onSuccess, onError);
    }

    public static <T> void callAsync(Window window, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        callAsync(window, null, callable, onSuccess);
    }

    public static <T> TaskWaiter<T> callAsync(Window window, DbConnectedCallable<T> callable) {
        return callAsync(window, null, callable);
    }

    protected abstract T getResult() throws Exception;
}
