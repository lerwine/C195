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
import javafx.scene.layout.Pane;
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
@FXMLResource("/view/TaskWaiter.fxml")
public abstract class TaskWaiter<T> extends Task<T> {
    private static final Logger LOG = Logger.getLogger(TaskWaiter.class.getName());
    
    //private final Scene scene;
    private final ViewManager viewManager;
    private final Parent parent;
    
    @FXML // ResourceBundle injected by the FXMLLoader
    private ResourceBundle resources;

    @FXML
    private Pane contentPane;

    @FXML
    private Label headingLabel;

    @FXML
    private Label operationLabel;

    @FXML
    private Button cancelButton;

    public TaskWaiter(ViewManager viewManager) {
        this(viewManager, null);
    }
    
    public TaskWaiter(ViewManager viewManager, String operation) {
        this(viewManager, operation, null);
    }
    
    public TaskWaiter(ViewManager viewManager, String operation, String heading) {
        super();
        this.viewManager = Objects.requireNonNull(viewManager);
        parent = viewManager.getRoot();
        titleProperty().addListener((observable) -> {
            if (headingLabel != null)
                updateHeadingLabel();
        });
        messageProperty().addListener((observable) -> {
            if (operationLabel != null)
                updateOperationLabel();
        });
        if (heading != null && !heading.trim().isEmpty()) {
            updateTitle(heading);
            updateMessage((operation == null) ? "" : operation);
        } else if (operation == null || operation.trim().isEmpty()) {
            updateTitle(resources.getString(App.RESOURCEKEY_PLEASEWAIT));
            updateMessage(resources.getString(App.RESOURCEKEY_CONNECTINGTODB));
        } else {
            updateTitle("");
            updateMessage(operation);
        }
    }

    private void updateHeadingLabel() {
        String s = getTitle();
        if (s == null || s.trim().isEmpty())
            SchedulerController.collapseNode(headingLabel);
        else
            SchedulerController.restoreLabeled(headingLabel, s);
    }

    private void updateOperationLabel() {
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
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert operationLabel != null : "fx:id=\"operationLabel\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        cancelButton.setOnAction((event) -> {
            cancelButton.setOnAction(null);
            if (!isCancelled())
                cancel(true);
        });
        updateHeadingLabel();
        updateOperationLabel();
    }

    @Override
    protected T call() throws Exception {
        LOG.log(Level.INFO, "Task called");
        if (Platform.isFxApplicationThread())
            loadBusyFxml();
        else
            Platform.runLater(() -> {
                loadBusyFxml();
            });
        try {
            LOG.log(Level.INFO, "Getting result");
            return getResult();
        } finally {
            if (Platform.isFxApplicationThread())
                restoreScene();
            else
                Platform.runLater(() -> {
                    restoreScene();
                });
        }
    }

    private void loadBusyFxml() throws RuntimeException {
        LOG.log(Level.INFO, "loadBusyFxml called");
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
        viewManager.setRoot(newParent);
        contentPane.getChildren().add(parent);
    }
    
    private void restoreScene() {
        LOG.log(Level.INFO, "restoreScene called");
        cancelButton.setOnAction(null);
        contentPane.getChildren().remove(parent);
        viewManager.setRoot(parent);
    }

    public static TaskWaiter<?> fromConsumer(ViewManager viewManager, DbConnectionConsumer consumer) {
        return fromConsumer(viewManager, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(ViewManager viewManager, String operation, DbConnectionConsumer consumer) {
        return fromConsumer(viewManager, operation, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(ViewManager viewManager, String operation, String heading, DbConnectionConsumer consumer) {
        return new TaskWaiterImpl<>(viewManager, operation, heading, () -> {
           DbConnector.apply(consumer);
           return null;
        });
    }
    
    public static void acceptAsync(ViewManager viewManager, String operation, String heading, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(viewManager, operation, heading, consumer);
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
    
    public static void acceptAsync(ViewManager viewManager, String operation, String heading, DbConnectionConsumer consumer, Runnable onSuccess) {
        TaskWaiter<?> task = fromConsumer(viewManager, operation, heading, consumer);
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
    
    public static void acceptAsync(ViewManager viewManager, String operation, String heading, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(viewManager, operation, heading, consumer);
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
    
    public static TaskWaiter<?> acceptAsync(ViewManager viewManager, String operation, String heading, DbConnectionConsumer consumer) {
        TaskWaiter<?> task = fromConsumer(viewManager, operation, heading, consumer);
        execute(task);
        return task;
    }
    
    public static void acceptAsync(ViewManager viewManager, String operation, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(viewManager, operation, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(ViewManager viewManager, String operation, DbConnectionConsumer consumer, Runnable onSuccess) {
        acceptAsync(viewManager, operation, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(ViewManager viewManager, String operation, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        acceptAsync(viewManager, operation, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(ViewManager viewManager, String operation, DbConnectionConsumer consumer) {
        return acceptAsync(viewManager, operation, null, consumer);
    }
    
    public static void acceptAsync(ViewManager viewManager, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(viewManager, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(ViewManager viewManager, DbConnectionConsumer consumer, Runnable onSuccess) {
        acceptAsync(viewManager, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(ViewManager viewManager, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        acceptAsync(viewManager, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(ViewManager viewManager, DbConnectionConsumer consumer) {
        return acceptAsync(viewManager, null, consumer);
    }
    
    public static <T> TaskWaiter<T> fromCallable(ViewManager viewManager, DbConnectedCallable<T> callable) {
        return fromCallable(viewManager, null, callable);
    }
    
    public static <T> TaskWaiter<T> fromCallable(ViewManager viewManager, String operation, DbConnectedCallable<T> callable) {
        return fromCallable(viewManager, operation, null, callable);
    }
    
    public static <T> TaskWaiter<T> fromCallable(ViewManager viewManager, String operation, String heading, DbConnectedCallable<T> callable) {
        return new TaskWaiterImpl<>(viewManager, operation, heading, () -> DbConnector.call(callable));
    }

    public static <T> void callAsync(ViewManager viewManager, String operation, String heading, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(viewManager, operation, heading, () -> DbConnector.call(callable));
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

    public static <T> void callAsync(ViewManager viewManager, String operation, String heading, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(viewManager, operation, heading, () -> DbConnector.call(callable));
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

    public static <T> TaskWaiter<T> callAsync(ViewManager viewManager, String operation, String heading, DbConnectedCallable<T> callable) {
        return new TaskWaiterImpl<>(viewManager, operation, heading, () -> DbConnector.call(callable));
    }

    public static <T> void callAsync(ViewManager viewManager, String operation, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        callAsync(viewManager, operation, null, callable, onSuccess, onError);
    }

    public static <T> void callAsync(ViewManager viewManager, String operation, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        callAsync(viewManager, operation, null, callable, onSuccess);
    }

    public static <T> TaskWaiter<T> callAsync(ViewManager viewManager, String operation, DbConnectedCallable<T> callable) {
        return callAsync(viewManager, operation, null, callable);
    }

    public static <T> void callAsync(ViewManager viewManager, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        callAsync(viewManager, null, callable, onSuccess, onError);
    }

    public static <T> void callAsync(ViewManager viewManager, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        callAsync(viewManager, null, callable, onSuccess);
    }

    public static <T> TaskWaiter<T> callAsync(ViewManager viewManager, DbConnectedCallable<T> callable) {
        return callAsync(viewManager, null, callable);
    }

    protected abstract T getResult() throws Exception;
}
