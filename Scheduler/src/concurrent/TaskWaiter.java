package concurrent;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 *
 * @author erwinel
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/concurrent/TaskWaiter.fxml")
public class TaskWaiter<T> extends Task<T> {
    private static final Logger LOG = Logger.getLogger(TaskWaiter.class.getName());
    private static final Object MONITOR = new Object();
    private final Scene scene;
    private final Parent parent;
    private final Callable<T> callable;
    
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

    private TaskWaiter(Stage stage, Callable<T> callable) {
        Objects.requireNonNull(stage);
        this.callable = callable;
        scene = stage.getScene();
        parent = scene.getRoot();
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
    
    public static TaskWaiter<?> fromRunnable(Stage stage, DbConnectedRunnable runnable) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromRunnable(runnable));
    }

    public static <T> TaskWaiter<T> fromSupplier(Stage stage, DbConnectedSupplier<T> supplier) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromSupplier(supplier));
    }
    
    public static <T> TaskWaiter<?> fromConsumer(Stage stage, DbConnectedSupplier<T> supplier, DbConnectedConsumer<T> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromConsumer(supplier, consumer));
    }

    public static <T> TaskWaiter<?> fromConsumer(Stage stage, DbConnectedSupplier<T> supplier, Consumer<T> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromConsumer(supplier, consumer));
    }
    
    public static <T> TaskWaiter<?> fromConsumer(Stage stage, Supplier<T> supplier, DbConnectedConsumer<T> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromConsumer(supplier, consumer));
    }
    
    public static <T> TaskWaiter<?> fromConsumer(Stage stage, T value, DbConnectedConsumer<T> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromConsumer(value, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, DbConnectedSupplier<T> supplier1, Supplier<U> supplier2, BiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, Supplier<T> supplier1, Supplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, T t, DbConnectedSupplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(t, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, T t, DbConnectedSupplier<U> supplier2, BiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(t, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, T t, Supplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(t, supplier2, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, DbConnectedSupplier<T> supplier1, U u, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, u, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, DbConnectedSupplier<T> supplier1, U u, BiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, u, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, Supplier<T> supplier1, U u, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(supplier1, u, consumer));
    }
    
    public static <T, U> TaskWaiter<?> fromBiConsumer(Stage stage, T t, U u, DbConnectedBiConsumer<T, U> consumer) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiConsumer(t, u, consumer));
    }
    
    public static <T, R> TaskWaiter<R> fromFunction(Stage stage, DbConnectedSupplier<T> supplier, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromFunction(supplier, function));
    }
    
    public static <T, R> TaskWaiter<R> fromFunction(Stage stage, DbConnectedSupplier<T> supplier, Function<T, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromFunction(supplier, function));
    }
    
    public static <T, R> TaskWaiter<R> fromFunction(Stage stage, Supplier<T> supplier, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromFunction(supplier, function));
    }
    
    public static <T, R> TaskWaiter<R> fromFunction(Stage stage, T value, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromFunction(value, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, DbConnectedSupplier<T> supplier1, Supplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, Supplier<T> supplier1, Supplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, DbConnectedSupplier<T> supplier1, U u, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, u, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, DbConnectedSupplier<T> supplier1, U u, BiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, u, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, Supplier<T> supplier1, U u, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(supplier1, u, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, T t, DbConnectedSupplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(t, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, T t, DbConnectedSupplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(t, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, T t, Supplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(t, supplier2, function));
    }
    
    public static <T, U, R> TaskWaiter<R> fromBiFunction(Stage stage, T t, U u, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBiFunction(t, u, function));
    }
    
    public static <T> TaskWaiter<Boolean> fromPredicate(Stage stage, DbConnectedSupplier<T> supplier, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromPredicate(supplier, predicate));
    }

    public static <T> TaskWaiter<Boolean> fromPredicate(Stage stage, DbConnectedSupplier<T> supplier, Predicate<T> predicate) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromPredicate(supplier, predicate));
    }

    public static <T> TaskWaiter<Boolean> fromPredicate(Stage stage, Supplier<T> supplier, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromPredicate(supplier, predicate));
    }

    public static <T> TaskWaiter<Boolean> fromPredicate(Stage stage, T value, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromPredicate(value, predicate));
    }

    public static TaskWaiter<Boolean> fromBooleanSupplier(Stage stage, DbConnectedBooleanSupplier supplier) {
        Objects.requireNonNull(stage);
        return new TaskWaiter<>(stage, DbConnectableExecutor.fromBooleanSupplier(supplier));
    }
    
    @FXML
    void initialize() {
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert operationLabel != null : "fx:id=\"operationLabel\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'TaskWaiter.fxml'.";
        cancelButton.setOnAction((ActionEvent event) -> {
            cancelButton.setOnAction(null);
            if (!isCancelled())
                cancel(true);
        });
    }

    @Override
    protected T call() throws Exception {
        Platform.runLater(() -> {
            ResourceBundle rb = ResourceBundle.getBundle(view.SchedulerController.getGlobalizationResourceName(TaskWaiter.class), Locale.getDefault(Locale.Category.DISPLAY));
            FXMLLoader loader = new FXMLLoader(view.login.LoginScene.class.getResource(view.SchedulerController.getFXMLResourceName(TaskWaiter.class)), rb);
            loader.setController(this);
            final Parent newParent;
            try {
                newParent = loader.load();
            } catch (IOException ex) {
                Logger.getLogger(TaskWaiter.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error loading FXML", ex);
            }
            scene.setRoot(newParent);
            contentPane.getChildren().add(parent);
        });
        try {
            return callable.call();
        } finally {
            Platform.runLater(() -> {
                cancelButton.setOnAction(null);
                contentPane.getChildren().remove(parent);
                scene.setRoot(parent);
            });
        }
    }
    
}
