package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import scheduler.App;
import scheduler.util.DbConnector;
import scheduler.util.ThrowableConsumer;
import scheduler.util.ThrowableFunction;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Controller / Task for showing a busy indicator while background process is running.
 * @author erwinel
 * @param <T> Type of value produced by the task.
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/TaskWaiter.fxml")
public abstract class TaskWaiter<T> extends Task<T> {
    private static final Logger LOG = Logger.getLogger(TaskWaiter.class.getName());
    private final EventHandler<WindowEvent> onHidden;
    private EventHandler<WindowEvent> oldOnHidden;
    private final Stage owner;
    
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

    /**
     * Initializes a new TaskWaiter.
     * @param owner The owner {@link Stage} that will be masked while the task is executing.
     */
    public TaskWaiter(Stage owner) {
        this(owner, null);
    }
    
    /**
     * Initializes a new TaskWaiter.
     * @param owner The owner {@link Stage} that will be masked while the task is executing.
     * @param operation The name of the operation to be displayed as the message text.
     */
    public TaskWaiter(Stage owner, String operation) {
        this(owner, operation, null);
    }
    
    /**
     * Initializes a new TaskWaiter.
     * @param owner The owner {@link Stage} that will be masked while the task is executing.
     * @param operation The name of the operation to be displayed as the message text.
     * @param heading The text to be displayed for the {@link #headingLabel}.
     */
    public TaskWaiter(Stage owner, String operation, String heading) {
        super();
        onHidden = (WindowEvent event) -> {
            owner.setOnHidden(oldOnHidden);
            try {
                if (isRunning() && !isCancelled())
                    cancel(true);
            } finally {
                if (null != oldOnHidden)
                    oldOnHidden.handle(event);
            }
        };
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
    
    /**
     * Processes the result in the FX application thread.
     * @param result The result value to process.
     * @param owner The owner {@link Stage} that was masked while the task was executing.
     */
    protected abstract void processResult(T result, Stage owner);

    /**
     * Processes an exception in the FX application thread.
     * @param ex The exception that was thrown.
     * @param owner The owner {@link Stage} that was masked while the task was executing.
     */
    protected abstract void processException(Throwable ex, Stage owner);
    
    @Override
    protected void succeeded() {
        if (Platform.isFxApplicationThread())
            try { processResult(getValue(), owner); }
            catch (Exception ex) { processException(ex, owner); }
        else
            Platform.runLater(() -> {
                try { processResult(getValue(), owner); }
                catch (Exception ex) { processException(ex, owner); }
            });
        super.succeeded();
    }
        
    @Override
    protected void failed() {
        if (Platform.isFxApplicationThread())
            processException(getException(), owner);
        else
            Platform.runLater(() -> processException(getException(), owner));
        super.failed();
    }

    @Override
    protected void cancelled() {
        LOG.log(Level.WARNING, String.format("\"%s\" operation cancelled", getTitle()));
        super.cancelled();
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
    
    /**
     * Schedules a {@link TaskWaiter} for execution.
     * @param task The {@link TaskWaiter} to execute after the specified delay.
     * @param delay The time from now to delay execution.
     * @param unit The time unit of the delay parameter.
     */
    public static void schedule(TaskWaiter<?> task, long delay, TimeUnit unit) {
        ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
        try {
            svc.schedule(task, delay, unit);
        } finally {
            svc.shutdown();
        }
    }
    
    /**
     * Executes a {@link TaskWaiter} immediately.
     * @param task The {@link TaskWaiter} to execute.
     */
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
            showBusyView();
        else
            Platform.runLater(() -> {
                showBusyView();
            });
        try {
            LOG.log(Level.INFO, "Getting result");
            return DbConnector.apply((connection) -> getResult(connection));
        } finally {
            if (Platform.isFxApplicationThread())
                hideBusyView();
            else
                Platform.runLater(() -> {
                    hideBusyView();
                });
        }
    }

    private void showBusyView() throws RuntimeException {
        LOG.log(Level.INFO, "showPopup called");
        if (!contentPane.getChildren().isEmpty())
            return;
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
        Parent oldParent = owner.getScene().getRoot();
        owner.getScene().setRoot(newParent);
        contentPane.getChildren().add(oldParent);
        oldOnHidden = owner.getOnHidden();
        owner.setOnHidden(onHidden);
    }
    
    private void hideBusyView() {
        LOG.log(Level.INFO, "hidePopup called");
        owner.setOnHidden(oldOnHidden);
        oldOnHidden = null;
        cancelButton.setOnAction(null);
        if (contentPane.getChildren().isEmpty())
            return;
        Parent oldParent = (Parent) contentPane.getChildren().get(0);
        contentPane.getChildren().clear();
        owner.getScene().setRoot(oldParent);
    }

    public static TaskWaiter<?> fromConsumer(Stage owner, ThrowableConsumer<Connection, SQLException> consumer) {
        return fromConsumer(owner, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(Stage owner, String operation, ThrowableConsumer<Connection, SQLException> consumer) {
        return fromConsumer(owner, operation, null, consumer);
    }
    
    public static TaskWaiter<?> fromConsumer(Stage owner, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer) {
        return new TaskWaiterImpl<>(owner, operation, heading, (connection) -> {
           consumer.accept(connection);
           return null;
        });
    }
    
    public static void acceptAsync(Stage owner, String operation, String heading,  ThrowableConsumer<Connection, SQLException> consumer,
            Runnable onSuccess, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(owner, operation, heading, consumer);
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
    
    public static void acceptAsync(Stage owner, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer,
            Runnable onSuccess) {
        TaskWaiter<?> task = fromConsumer(owner, operation, heading, consumer);
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
    
    public static void acceptAsync(Stage owner, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(owner, operation, heading, consumer);
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
    
    public static TaskWaiter<?> acceptAsync(Stage owner, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer) {
        TaskWaiter<?> task = fromConsumer(owner, operation, heading, consumer);
        execute(task);
        return task;
    }
    
    public static void acceptAsync(Stage owner, String operation, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(owner, operation, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(Stage owner, String operation, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess) {
        acceptAsync(owner, operation, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(Stage owner, String operation, ThrowableConsumer<Connection, SQLException> consumer, Consumer<Exception> onError) {
        acceptAsync(owner, operation, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(Stage owner, String operation, ThrowableConsumer<Connection, SQLException> consumer) {
        return acceptAsync(owner, operation, null, consumer);
    }
    
    public static void acceptAsync(Stage owner, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(owner, null, consumer, onSuccess, onError);
    }
    
    public static void acceptAsync(Stage owner, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess) {
        acceptAsync(owner, null, consumer, onSuccess);
    }
    
    public static void acceptAsync(Stage owner, ThrowableConsumer<Connection, SQLException> consumer, Consumer<Exception> onError) {
        acceptAsync(owner, null, consumer, onError);
    }
    
    public static TaskWaiter<?> acceptAsync(Stage owner, ThrowableConsumer<Connection, SQLException> consumer) {
        return acceptAsync(owner, null, consumer);
    }
    
    public static <T> TaskWaiter<T> fromFunction(Stage owner, ThrowableFunction<Connection, T, SQLException> func) {
        return fromFunction(owner, null, func);
    }
    
    public static <T> TaskWaiter<T> fromFunction(Stage owner, String operation, ThrowableFunction<Connection, T, SQLException> func) {
        return fromFunction(owner, operation, null, func);
    }
    
    public static <T> TaskWaiter<T> fromFunction(Stage owner, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func) {
        return new TaskWaiterImpl<>(owner, operation, heading, func);
    }

    public static <T> void applyAsync(Stage owner, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess, Consumer<Exception> onError) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(owner, operation, heading, func);
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

    public static <T> void applyAsync(Stage owner, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(owner, operation, heading, func);
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

    public static <T> TaskWaiter<T> applyAsync(Stage owner, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func) {
        return new TaskWaiterImpl<>(owner, operation, heading, func);
    }

    public static <T> void applyAsync(Stage owner, String operation, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess, Consumer<Exception> onError) {
        applyAsync(owner, operation, null, func, onSuccess, onError);
    }

    public static <T> void applyAsync(Stage owner, String operation, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess) {
        applyAsync(owner, operation, null, func, onSuccess);
    }

    public static <T> TaskWaiter<T> applyAsync(Stage owner, String operation, ThrowableFunction<Connection, T, SQLException> func) {
        return applyAsync(owner, operation, null, func);
    }

    public static <T> void applyAsync(Stage owner, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess, Consumer<Exception> onError) {
        applyAsync(owner, null, func, onSuccess, onError);
    }

    public static <T> void applyAsync(Stage owner, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess) {
        applyAsync(owner, null, func, onSuccess);
    }

    public static <T> TaskWaiter<T> applyAsync(Stage owner, ThrowableFunction<Connection, T, SQLException> func) {
        return applyAsync(owner, null, func);
    }

    protected abstract T getResult(Connection connection) throws SQLException;
}
