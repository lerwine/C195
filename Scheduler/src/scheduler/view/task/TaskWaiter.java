package scheduler.view.task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import scheduler.AppResources;
import scheduler.util.DbConnector;
import scheduler.util.ResourceBundleLoader;
import scheduler.util.ThrowableConsumer;
import scheduler.util.ThrowableFunction;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;

/**
 * Controller / Task for showing a busy indicator while background process is running. If the current {@link Stage} is closed while this is executing,
 * the task will be canceled.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> Type of value produced by the task.
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/TaskWaiter.fxml")
public abstract class TaskWaiter<T> extends Task<T> {

    private static final Logger LOG = Logger.getLogger(TaskWaiter.class.getName());

    /**
     * Schedules a {@link TaskWaiter} for execution.
     *
     * @param task The {@link TaskWaiter} to startNow after the specified delay.
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
     * Starts a {@link TaskWaiter} immediately.
     *
     * @param task The {@link TaskWaiter} to startNow.
     */
    public static void startNow(TaskWaiter<?> task) {
        ExecutorService svc = Executors.newSingleThreadExecutor();
        try {
            svc.execute(task);
        } finally {
            svc.shutdown();
        }
    }

    /**
     * Creates a {@link TaskWaiter} from a {@link ThrowableConsumer}.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @return A new {@link TaskWaiter} object that invokes the specified {@code consumer} when executed.
     */
    public static TaskWaiter<?> fromConsumer(Stage stage, ThrowableConsumer<Connection, SQLException> consumer) {
        return fromConsumer(stage, null, consumer);
    }

    /**
     * Creates a {@link TaskWaiter} from a {@link ThrowableConsumer}.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @return A new {@link TaskWaiter} object that invokes the specified {@code consumer} when executed.
     */
    public static TaskWaiter<?> fromConsumer(Stage stage, String operation, ThrowableConsumer<Connection, SQLException> consumer) {
        return fromConsumer(stage, operation, null, consumer);
    }

    /**
     * Creates a {@link TaskWaiter} from a {@link ThrowableConsumer}.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @return A new {@link TaskWaiter} object that invokes the specified {@code consumer} when executed.
     */
    public static TaskWaiter<?> fromConsumer(Stage stage, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer) {
        return new TaskWaiterImpl<>(stage, operation, heading, (connection) -> {
            consumer.accept(connection);
            return null;
        });
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onSuccess The {@link Runnable} to invoke after the {@code consumer} is executed without any exceptions. This will be executed in the FX
     * application thread.
     * @param onError The {@link Consumer} to invoke if the main {@code consumer} throws an exception. This will be executed in the FX application
     * thread.
     */
    public static void acceptAsync(Stage stage, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer,
            Runnable onSuccess, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, "Error getting task results", ex);
                onError.accept(ex);
                return;
            }
            onSuccess.run();
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        startNow(task);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onSuccess The {@link Runnable} to invoke after the {@code consumer} is executed without any exceptions. This will be executed in the FX
     * application thread.
     */
    public static void acceptAsync(Stage stage, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer,
            Runnable onSuccess) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, "Error getting task results", ex);
                return;
            }
            onSuccess.run();
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        startNow(task);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onError The {@link Consumer} to invoke if the main {@code consumer} throws an exception. This will be executed in the FX application
     * thread.
     */
    public static void acceptAsync(Stage stage, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer, Consumer<Exception> onError) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, "Error getting task results", ex);
                onError.accept(ex);
            }
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        startNow(task);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @return The {@link TaskWaiter} that was started.
     */
    public static TaskWaiter<?> acceptAsync(Stage stage, String operation, String heading, ThrowableConsumer<Connection, SQLException> consumer) {
        TaskWaiter<?> task = fromConsumer(stage, operation, heading, consumer);
        startNow(task);
        return task;
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onSuccess The {@link Runnable} to invoke after the {@code consumer} is executed without any exceptions. This will be executed in the FX
     * application thread.
     * @param onError The {@link Consumer} to invoke if the main {@code consumer} throws an exception. This will be executed in the FX application
     * thread.
     */
    public static void acceptAsync(Stage stage, String operation, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(stage, operation, null, consumer, onSuccess, onError);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onSuccess The {@link Runnable} to invoke after the {@code consumer} is executed without any exceptions. This will be executed in the FX
     * application thread.
     */
    public static void acceptAsync(Stage stage, String operation, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess) {
        acceptAsync(stage, operation, null, consumer, onSuccess);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onError The {@link Consumer} to invoke if the main {@code consumer} throws an exception. This will be executed in the FX application
     * thread.
     */
    public static void acceptAsync(Stage stage, String operation, ThrowableConsumer<Connection, SQLException> consumer, Consumer<Exception> onError) {
        acceptAsync(stage, operation, null, consumer, onError);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @return The {@link TaskWaiter} that was started.
     */
    public static TaskWaiter<?> acceptAsync(Stage stage, String operation, ThrowableConsumer<Connection, SQLException> consumer) {
        return acceptAsync(stage, operation, null, consumer);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onSuccess The {@link Runnable} to invoke after the {@code consumer} is executed without any exceptions. This will be executed in the FX
     * application thread.
     * @param onError The {@link Consumer} to invoke if the main {@code consumer} throws an exception. This will be executed in the FX application
     * thread.
     */
    public static void acceptAsync(Stage stage, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess, Consumer<Exception> onError) {
        acceptAsync(stage, null, consumer, onSuccess, onError);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onSuccess The {@link Runnable} to invoke after the {@code consumer} is executed without any exceptions. This will be executed in the FX
     * application thread.
     */
    public static void acceptAsync(Stage stage, ThrowableConsumer<Connection, SQLException> consumer, Runnable onSuccess) {
        acceptAsync(stage, null, consumer, onSuccess);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @param onError The {@link Consumer} to invoke if the main {@code consumer} throws an exception. This will be executed in the FX application
     * thread.
     */
    public static void acceptAsync(Stage stage, ThrowableConsumer<Connection, SQLException> consumer, Consumer<Exception> onError) {
        acceptAsync(stage, null, consumer, onError);
    }

    /**
     * Asynchronously executes a {@link ThrowableConsumer}, showing a busy indicator while background process is running.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param consumer The {@link ThrowableConsumer} that performs an operation using an opened database {@link Connection}. This will be executed
     * from a background thread.
     * @return The {@link TaskWaiter} that was started.
     */
    public static TaskWaiter<?> acceptAsync(Stage stage, ThrowableConsumer<Connection, SQLException> consumer) {
        return acceptAsync(stage, null, consumer);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @return A new {@link TaskWaiter} object that invokes the specified {@code func} when executed.
     */
    public static <T> TaskWaiter<T> fromFunction(Stage stage, ThrowableFunction<Connection, T, SQLException> func) {
        return fromFunction(stage, null, func);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @return A new {@link TaskWaiter} object that invokes the specified {@code func} when executed.
     */
    public static <T> TaskWaiter<T> fromFunction(Stage stage, String operation, ThrowableFunction<Connection, T, SQLException> func) {
        return fromFunction(stage, operation, null, func);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @return A new {@link TaskWaiter} object that invokes the specified {@code func} when executed.
     */
    public static <T> TaskWaiter<T> fromFunction(Stage stage, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func) {
        return new TaskWaiterImpl<>(stage, operation, heading, func);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @param onSuccess The {@link Consumer} to accept the results of {@code func}. This will be executed in the FX application thread.
     * @param onError The {@link Consumer} to invoke if {@code func} throws an exception. This will be executed in the FX application thread.
     */
    public static <T> void applyAsync(Stage stage, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess, Consumer<Exception> onError) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(stage, operation, heading, func);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            T result;
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, "Error getting task results", ex);
                onError.accept(ex);
                return;
            }
            onSuccess.accept(result);
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        startNow(task);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @param onSuccess The {@link Consumer} to accept the results of {@code func}. This will be executed in the FX application thread.
     */
    public static <T> void applyAsync(Stage stage, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess) {
        TaskWaiterImpl<T> task = new TaskWaiterImpl<>(stage, operation, heading, func);
        EventHandler<WorkerStateEvent> onCompleted = (event) -> {
            T result;
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, "Error getting task results", ex);
                return;
            }
            onSuccess.accept(result);
        };
        task.setOnCancelled(onCompleted);
        task.setOnFailed(onCompleted);
        task.setOnSucceeded(onCompleted);
        startNow(task);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The heading text to display above the {@code operation} text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @return The {@link TaskWaiter} that was started.
     */
    public static <T> TaskWaiter<T> applyAsync(Stage stage, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func) {
        return new TaskWaiterImpl<>(stage, operation, heading, func);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @param onSuccess The {@link Consumer} to accept the results of {@code func}. This will be executed in the FX application thread.
     * @param onError The {@link Consumer} to invoke if {@code func} throws an exception. This will be executed in the FX application thread.
     */
    public static <T> void applyAsync(Stage stage, String operation, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess, Consumer<Exception> onError) {
        applyAsync(stage, operation, null, func, onSuccess, onError);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @param onSuccess The {@link Consumer} to accept the results of {@code func}. This will be executed in the FX application thread.
     */
    public static <T> void applyAsync(Stage stage, String operation, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess) {
        applyAsync(stage, operation, null, func, onSuccess);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @return The {@link TaskWaiter} that was started.
     */
    public static <T> TaskWaiter<T> applyAsync(Stage stage, String operation, ThrowableFunction<Connection, T, SQLException> func) {
        return applyAsync(stage, operation, null, func);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @param onSuccess The {@link Consumer} to accept the results of {@code func}. This will be executed in the FX application thread.
     * @param onError The {@link Consumer} to invoke if {@code func} throws an exception. This will be executed in the FX application thread.
     */
    public static <T> void applyAsync(Stage stage, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess, Consumer<Exception> onError) {
        applyAsync(stage, null, func, onSuccess, onError);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @param onSuccess The {@link Consumer} to accept the results of {@code func}. This will be executed in the FX application thread.
     */
    public static <T> void applyAsync(Stage stage, ThrowableFunction<Connection, T, SQLException> func, Consumer<T> onSuccess) {
        applyAsync(stage, null, func, onSuccess);
    }

    /**
     * Asynchronously executes a {@link ThrowableFunction}, showing a busy indicator while background process is running.
     *
     * @param <T> The type of result to be produced asynchronously.
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param func The {@link ThrowableFunction} that gets the results using an opened database {@link Connection}. This will be executed from a
     * background thread.
     * @return The {@link TaskWaiter} that was started.
     */
    public static <T> TaskWaiter<T> applyAsync(Stage stage, ThrowableFunction<Connection, T, SQLException> func) {
        return applyAsync(stage, null, func);
    }
    private final EventHandler<WindowEvent> onHidden;
    private EventHandler<WindowEvent> oldOnHidden;
    private final Stage owner;
    @FXML
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
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     */
    public TaskWaiter(Stage stage) {
        this(stage, null);
    }

    /**
     * Initializes a new TaskWaiter.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     */
    public TaskWaiter(Stage stage, String operation) {
        this(stage, operation, null);
    }

    /**
     * Initializes a new TaskWaiter.
     *
     * @param stage The {@link Stage} for the view that will be masked while the task is executing.
     * @param operation The description of the operation to be displayed as the message text while the task is executing.
     * @param heading The text to be displayed for the {@link #headingLabel}.
     */
    public TaskWaiter(Stage stage, String operation, String heading) {
        super();
        this.owner = Objects.requireNonNull(stage);
        onHidden = (WindowEvent event) -> {
            stage.setOnHidden(oldOnHidden);
            try {
                if (isRunning() && !isCancelled()) {
                    cancel(true);
                }
            } finally {
                if (null != oldOnHidden) {
                    oldOnHidden.handle(event);
                }
            }
        };
        titleProperty().addListener((observable) -> {
            if (headingLabel != null) {
                updateHeadingLabel();
            }
        });
        messageProperty().addListener((observable) -> {
            if (operationLabel != null) {
                updateOperationLabel();
            }
        });
        updateTitle((null == heading) ? "" : heading);
        updateMessage((null == operation) ? "" : operation);
    }

    /**
     * Processes the result from {@link #getResult(java.sql.Connection)} in the FX application thread.
     *
     * @param result The result value to process.
     * @param stage The {@link Stage} for the view that was masked while the task was executing.
     */
    protected abstract void processResult(T result, Stage stage);

    /**
     * Processes an exception (which occurred in {@link #getResult(java.sql.Connection)}) in the FX application thread.
     *
     * @param ex The exception that was thrown.
     * @param stage The {@link Stage} for the view that was masked while the task was executing.
     */
    protected abstract void processException(Throwable ex, Stage stage);

    @Override
    protected void succeeded() {
        if (Platform.isFxApplicationThread()) {
            try {
                processResult(getValue(), owner);
            } catch (Exception ex) {
                processException(ex, owner);
            }
        } else {
            Platform.runLater(() -> {
                try {
                    processResult(getValue(), owner);
                } catch (Exception ex) {
                    processException(ex, owner);
                }
            });
        }
        super.succeeded();
    }

    @Override
    protected void failed() {
        if (Platform.isFxApplicationThread()) {
            processException(getException(), owner);
        } else {
            Platform.runLater(() -> processException(getException(), owner));
        }
        super.failed();
    }

    @Override
    protected void cancelled() {
        LOG.log(Level.WARNING, String.format("\"%s\" operation cancelled", getTitle()));
        super.cancelled();
    }

    private void updateHeadingLabel() {
        if (null == headingLabel) {
            return;
        }
        String s = getTitle();
        if (s == null || s.trim().isEmpty()) {
            collapseNode(headingLabel);
        } else {
            restoreLabeled(headingLabel, s);
        }
    }

    private void updateOperationLabel() {
        if (null == operationLabel) {
            return;
        }
        String s = getMessage();
        if (s == null || s.trim().isEmpty()) {
            collapseNode(operationLabel);
        } else {
            restoreLabeled(operationLabel, s);
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
            if (!isCancelled()) {
                cancel(true);
            }
        });
        if (getTitle().trim().isEmpty() && getMessage().trim().isEmpty()) {
            updateTitle(resources.getString(AppResources.RESOURCEKEY_PLEASEWAIT));
            updateMessage(resources.getString(AppResources.RESOURCEKEY_CONNECTINGTODB));
        }
        updateHeadingLabel();
        updateOperationLabel();
    }

    @Override
    protected T call() throws Exception {
        LOG.log(Level.FINE, "Task called");
        if (Platform.isFxApplicationThread()) {
            showBusyView();
        } else {
            Platform.runLater(() -> {
                showBusyView();
            });
        }
        try {
            LOG.log(Level.FINE, "Getting result");
            return DbConnector.apply((connection) -> getResult(connection));
        } finally {
            if (Platform.isFxApplicationThread()) {
                hideBusyView();
            } else {
                Platform.runLater(() -> {
                    hideBusyView();
                });
            }
        }
    }

    /**
     * This is called asynchronously to get the result value.
     *
     * @param connection The opened database connection to be used.
     * @return The result value.
     * @throws SQLException if unable to get results from the database.
     */
    protected abstract T getResult(Connection connection) throws SQLException;

    private void showBusyView() throws RuntimeException {
        LOG.log(Level.FINE, "showBusyView called");
        if (null != contentPane) {
            return;
        }
        ResourceBundle rb = ResourceBundleLoader.getBundle(TaskWaiter.class);
        FXMLLoader loader = new FXMLLoader(TaskWaiter.class.getResource(AppResources.getFXMLResourceName(TaskWaiter.class)), rb);
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
        LOG.log(Level.FINE, "hideBusyView called");
        owner.setOnHidden(oldOnHidden);
        oldOnHidden = null;
        cancelButton.setOnAction(null);
        if (contentPane.getChildren().isEmpty()) {
            return;
        }
        Parent oldParent = (Parent) contentPane.getChildren().get(0);
        contentPane.getChildren().clear();
        owner.getScene().setRoot(oldParent);
    }

}
