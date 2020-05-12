package scheduler.view.task;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import static scheduler.AppResourceKeys.RESOURCEKEY_CANCELLING;
import static scheduler.AppResourceKeys.RESOURCEKEY_CLOSE;
import static scheduler.AppResourceKeys.RESOURCEKEY_NOFN;
import static scheduler.AppResourceKeys.RESOURCEKEY_PLEASEWAIT;
import static scheduler.util.NodeUtil.addCssClass;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.removeCssClass;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.util.ViewControllerLoader;
import scheduler.fx.CssClassName;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/task/WaitTitledPane.fxml")
public class WaitTitledPane extends TitledPane {

    private static final Logger LOG = Logger.getLogger(WaitTitledPane.class.getName());

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="statusStackPane"
    private StackPane statusStackPane; // Value injected by FXMLLoader

    @FXML // fx:id="progressIndicator"
    private ProgressIndicator progressIndicator; // Value injected by FXMLLoader

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader
    
    private final ReadOnlyBooleanWrapper faulted;
    private final ObjectProperty<EventHandler<WaitTitledPaneEvent>> onTaskRunning;
    private final ObjectProperty<EventHandler<WaitTitledPaneEvent>> onTaskDone;
    private Task<?> currentTask;
    private ErrorDetailGridPane errorDetail;

    @SuppressWarnings("LeakingThisInConstructor")
    public WaitTitledPane() {
        this.faulted = new ReadOnlyBooleanWrapper(false);
        onTaskRunning = new SimpleObjectProperty<>();
        onTaskRunning.addListener((observable, oldValue, newValue) -> {
            if (null != oldValue) {
                removeEventHandler(WaitTitledPaneEvent.RUNNING, oldValue);
            }
            if (null != newValue) {
                addEventHandler(WaitTitledPaneEvent.RUNNING, newValue);
            }
        });
        onTaskDone = new SimpleObjectProperty<>();
        onTaskDone.addListener((observable, oldValue, newValue) -> {
            if (null != oldValue) {
                removeEventHandler(WaitTitledPaneEvent.DONE, oldValue);
            }
            if (null != newValue) {
                addEventHandler(WaitTitledPaneEvent.DONE, newValue);
            }
        });
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError(ex);
        }
    }

    @FXML
    private synchronized void onCancelButtonAction(ActionEvent event) {
        Task<?> task = removeTaskEventHandlers();
        if (null != task && !task.isDone()) {
            messageLabel.setText(resources.getString(RESOURCEKEY_CANCELLING));
            cancelButton.setText(resources.getString(RESOURCEKEY_CLOSE));
            task.cancel(true);
        } else {
            fireEvent(new WaitTitledPaneEvent(event.getSource(), this, task, WaitTitledPaneEvent.DONE));
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert statusStackPane != null : "fx:id=\"statusStackPane\" was not injected: check your FXML file 'WaitTitledPane.fxml'.";
        assert progressIndicator != null : "fx:id=\"progressIndicator\" was not injected: check your FXML file 'WaitTitledPane.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'WaitTitledPane.fxml'.";

    }

    public boolean isFaulted() {
        return faulted.get();
    }

    public ReadOnlyBooleanProperty faultedProperty() {
        return faulted.getReadOnlyProperty();
    }

    public EventHandler<? extends WaitTitledPaneEvent> getOnTaskRunning() {
        return onTaskRunning.get();
    }

    public void setOnTaskRunning(EventHandler<WaitTitledPaneEvent> value) {
        onTaskRunning.set(value);
    }

    public ObjectProperty<EventHandler<WaitTitledPaneEvent>> onTaskRunningProperty() {
        return onTaskRunning;
    }

    public EventHandler<? extends WaitTitledPaneEvent> getOnTaskDone() {
        return onTaskDone.get();
    }

    public void setOnTaskDone(EventHandler<WaitTitledPaneEvent> value) {
        onTaskDone.set(value);
    }

    public ObjectProperty<EventHandler<WaitTitledPaneEvent>> onTaskDoneProperty() {
        return onTaskDone;
    }

    private void onRunning(WorkerStateEvent event) {
        fireEvent(new WaitTitledPaneEvent(event.getSource(), this, currentTask, WaitTitledPaneEvent.RUNNING));
    }

    private void onFailed(WorkerStateEvent event) {
        Task<?> task = removeTaskEventHandlers();
        if (null != task) {
            progressIndicator.setVisible(false);
            Throwable ex = task.getException();
            LOG.log(Level.SEVERE, "Background task failed", ex);
            errorDetail = ErrorDetailGridPane.of(ex);
            statusStackPane.getChildren().add(errorDetail);
            cancelButton.setText(resources.getString(RESOURCEKEY_CLOSE));
            addCssClass(this, CssClassName.ERROR_TITLED_PANE);
            removeCssClass(this, CssClassName.WAIT_TITLED_PANE);
            this.setExpanded(true);
            faulted.set(true);
        }
    }

    private void onFinished(WorkerStateEvent event) {
        Task<?> task = removeTaskEventHandlers();
        if (null != task) {
            fireEvent(new WaitTitledPaneEvent(event.getSource(), this, task, WaitTitledPaneEvent.DONE));
        }
    }

    /**
     * Schedules a {@link TaskWaiter} for execution.
     *
     * @param task The {@link TaskWaiter} to startNow after the specified delay.
     * @param delay The time from now to delay execution.
     * @param unit The time unit of the delay parameter.
     */
    public void schedule(Task<?> task, long delay, TimeUnit unit) {
        Objects.requireNonNull(task);
        if (Platform.isFxApplicationThread()) {
            prepare(task);
            ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
            try {
                svc.schedule(task, delay, unit);
            } finally {
                svc.shutdown();
            }
        } else {
            Platform.runLater(() -> schedule(task, delay, unit));
        }
    }

    public void startNow(Task<?> task) {
        if (Platform.isFxApplicationThread()) {
            prepare(task);
            ExecutorService svc = Executors.newSingleThreadExecutor();
            try {
                svc.execute(task);
            } finally {
                svc.shutdown();
            }
        } else {
            Platform.runLater(() -> startNow(task));
        }
    }
    
    private void onTitleChanged(Observable observable) {
        String s = ((ReadOnlyStringProperty)observable).get();
        if (null == s || s.trim().isEmpty())
            setText(resources.getString(RESOURCEKEY_PLEASEWAIT));
        else
            setText(s);
    }

    private void onStatusChanged(String message, double totalWork, double workDone, double progress) {
        if (null == message || message.trim().isEmpty()) {
            if (totalWork > 0.0 && workDone >= 0.0) {
                if (progress < 0.0) {
                    progress = workDone / totalWork;
                }
                message = String.format(resources.getString(RESOURCEKEY_NOFN), workDone, totalWork);
            } else {
                message = "";
            }
        } else if (progress < 0.0 && totalWork > 0.0 && workDone >= 0.0){
            progress = workDone / totalWork;
        }
        
        if (progress < 0.0) {
            progressIndicator.setProgress(-1);
        } else if (progress > 100.0) {
            progressIndicator.setProgress(1.0);
        } else if (progress > 1.0) {
            progressIndicator.setProgress(progress / 100.0);
        } else {
            progressIndicator.setProgress(progress);
        }
        
        messageLabel.setText(message);
        if (message.isEmpty())
            collapseNode(messageLabel);
        else
            restoreLabeled(messageLabel, message);
    }

    private void onMessageChanged(Observable observable) {
        onStatusChanged(((ReadOnlyStringProperty)observable).get(), currentTask.getTotalWork(), currentTask.getWorkDone(), currentTask.getProgress());
    }

    private void onTotalWorkChanged(Observable observable) {
        onStatusChanged(currentTask.getMessage(), ((ReadOnlyDoubleProperty)observable).get(), currentTask.getWorkDone(), currentTask.getProgress());
    }

    private void onWorkDoneChanged(Observable observable) {
        onStatusChanged(currentTask.getMessage(), currentTask.getTotalWork(), ((ReadOnlyDoubleProperty)observable).get(), currentTask.getProgress());
    }

    private void onProgressChanged(Observable observable) {
        onStatusChanged(currentTask.getMessage(), currentTask.getTotalWork(), currentTask.getWorkDone(), ((ReadOnlyDoubleProperty)observable).get());
    }

    private synchronized void prepare(Task<?> task) {
        if (null != currentTask || task.getState() != Worker.State.READY) {
            throw new IllegalStateException();
        }
        if (faulted.get()) {
            statusStackPane.getChildren().remove(errorDetail);
            errorDetail = null;
            progressIndicator.setVisible(true);
            addCssClass(this, CssClassName.WAIT_TITLED_PANE);
            removeCssClass(this, CssClassName.ERROR_TITLED_PANE);
            faulted.set(false);
        }
        currentTask = task;
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, this::onRunning);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, this::onFailed);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, this::onFinished);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this::onFinished);
        task.titleProperty().addListener(this::onTitleChanged);
        task.messageProperty().addListener(this::onMessageChanged);
        task.totalWorkProperty().addListener(this::onTotalWorkChanged);
        task.workDoneProperty().addListener(this::onWorkDoneChanged);
        task.progressProperty().addListener(this::onProgressChanged);
        onTitleChanged(task.titleProperty());
        onStatusChanged(task.getMessage(), task.getTotalWork(), task.getWorkDone(), task.getProgress());
    }

    private synchronized Task<?> removeTaskEventHandlers() {
        Task<?> task = currentTask;
        currentTask = null;
        if (null != task) {
            task.removeEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, this::onRunning);
            task.removeEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, this::onFailed);
            task.removeEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, this::onFinished);
            task.removeEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this::onFinished);
            task.titleProperty().removeListener(this::onTitleChanged);
            task.messageProperty().removeListener(this::onMessageChanged);
            task.totalWorkProperty().removeListener(this::onTotalWorkChanged);
            task.workDoneProperty().removeListener(this::onWorkDoneChanged);
            task.progressProperty().removeListener(this::onProgressChanged);
        }
        return task;
    }

}
