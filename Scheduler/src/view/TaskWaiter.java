package view;

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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import util.DbConnectedCallable;
import util.DbConnectionConsumer;
import util.DbConnector;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 *
 * @author erwinel
 * @param <T>
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/view/TaskWaiter.fxml")
public abstract class TaskWaiter<T> extends Task<T> {
    
    private static final Logger LOG = Logger.getLogger(TaskWaiter.class.getName());
    private final Scene scene;
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

    public TaskWaiter(Stage stage) {
        this(stage, null);
    }
    
    public TaskWaiter(Stage stage, String operation) {
        this(stage, operation, null);
    }
    
    public TaskWaiter(Stage stage, String operation, String heading) {
        super();
        Objects.requireNonNull(stage);
        scene = stage.getScene();
        parent = scene.getRoot();
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
            updateTitle(resources.getString("pleaseWait"));
            updateMessage(resources.getString("connectingToDb"));
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
        scene.setRoot(newParent);
        contentPane.getChildren().add(parent);
    }
    
    private void restoreScene() {
        LOG.log(Level.INFO, "restoreScene called");
        cancelButton.setOnAction(null);
        contentPane.getChildren().remove(parent);
        scene.setRoot(parent);
    }

    public static TaskWaiter<?> fromConsumer(Stage stage, DbConnectionConsumer consumer) {
        return fromConsumer(stage, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(Stage stage, String operation, DbConnectionConsumer consumer) {
        return fromConsumer(stage, operation, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(Stage stage, String operation, String heading, DbConnectionConsumer consumer) {
        return new TaskWaiterImpl<>(stage, operation, heading, () -> {
           DbConnector.apply(consumer);
           return null;
        });
    }
    
    public static void acceptAsync(Stage stage, String operation, String heading, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(TaskWaiter.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public static void acceptAsync(Stage stage, String operation, String heading, DbConnectionConsumer consumer, Runnable onSuccess) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(TaskWaiter.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            onSuccess.run();
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }
    
    public static void acceptAsync(Stage stage, String operation, String heading, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(TaskWaiter.class.getName()).log(Level.SEVERE, null, ex);
                onError.accept(ex);
                return;
            }
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }
    
    public static TaskWaiter<?> acceptAsync(Stage stage, String operation, String heading, DbConnectionConsumer consumer) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        execute(task);
        return task;
    }
    
    public static void acceptAsync(Stage stage, String operation, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(stage, operation, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(Stage stage, String operation, DbConnectionConsumer consumer, Runnable onSuccess) {
        acceptAsync(stage, operation, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(Stage stage, String operation, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        acceptAsync(stage, operation, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(Stage stage, String operation, DbConnectionConsumer consumer) {
        return acceptAsync(stage, operation, null, consumer);
    }
    
    public static void acceptAsync(Stage stage, DbConnectionConsumer consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(stage, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(Stage stage, DbConnectionConsumer consumer, Runnable onSuccess) {
        acceptAsync(stage, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(Stage stage, DbConnectionConsumer consumer, Consumer<Exception> onError) {
        acceptAsync(stage, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(Stage stage, DbConnectionConsumer consumer) {
        return acceptAsync(stage, null, consumer);
    }
    
    public static <T> TaskWaiter<T> fromCallable(Stage stage, DbConnectedCallable<T> callable) {
        return fromCallable(stage, null, callable);
    }
    
    public static <T> TaskWaiter<T> fromCallable(Stage stage, String operation, DbConnectedCallable<T> callable) {
        return fromCallable(stage, operation, null, callable);
    }
    
    public static <T> TaskWaiter<T> fromCallable(Stage stage, String operation, String heading, DbConnectedCallable<T> callable) {
        return new TaskWaiterImpl<>(stage, operation, heading, () -> DbConnector.call(callable));
    }

    public static <T> void callAsync(Stage stage, String operation, String heading, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(stage, operation, heading, () -> DbConnector.call(callable));
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            T result;
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(TaskWaiter.class.getName()).log(Level.SEVERE, null, ex);
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

    public static <T> void callAsync(Stage stage, String operation, String heading, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(stage, operation, heading, () -> DbConnector.call(callable));
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            T result;
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(TaskWaiter.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            onSuccess.accept(result);
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        execute(task);
    }

    public static <T> TaskWaiter<T> callAsync(Stage stage, String operation, String heading, DbConnectedCallable<T> callable) {
        return new TaskWaiterImpl<>(stage, operation, heading, () -> DbConnector.call(callable));
    }

    public static <T> void callAsync(Stage stage, String operation, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        callAsync(stage, operation, null, callable, onSuccess, onError);
    }

    public static <T> void callAsync(Stage stage, String operation, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        callAsync(stage, operation, null, callable, onSuccess);
    }

    public static <T> TaskWaiter<T> callAsync(Stage stage, String operation, DbConnectedCallable<T> callable) {
        return callAsync(stage, operation, null, callable);
    }

    public static <T> void callAsync(Stage stage, DbConnectedCallable<T> callable, Consumer<T> onSuccess, Consumer<Exception> onError) {
        callAsync(stage, null, callable, onSuccess, onError);
    }

    public static <T> void callAsync(Stage stage, DbConnectedCallable<T> callable, Consumer<T> onSuccess) {
        callAsync(stage, null, callable, onSuccess);
    }

    public static <T> TaskWaiter<T> callAsync(Stage stage, DbConnectedCallable<T> callable) {
        return callAsync(stage, null, callable);
    }

    protected abstract T getResult() throws Exception;
}
