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
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
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
import static scheduler.AppResourceKeys.RESOURCEKEY_CANCELLING;
import static scheduler.AppResourceKeys.RESOURCEKEY_CLOSE;
import static scheduler.AppResourceKeys.RESOURCEKEY_NOFN;
import static scheduler.AppResourceKeys.RESOURCEKEY_PLEASEWAIT;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/task/WaitTitledPane.fxml")
public final class WaitTitledPane extends TitledPane {

    private static final Logger LOG = Logger.getLogger(WaitTitledPane.class.getName());

    private Task<?> currentTask;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="progressIndicator"
    private ProgressIndicator progressIndicator; // Value injected by FXMLLoader

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public WaitTitledPane() {
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError(ex);
        }
    }

    @FXML
    private synchronized void onCancelButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onCancelButtonAction", event);
        cancelButton.setDisable(true);
        if (null == currentTask) {
            return;
        }
        if (!currentTask.isDone()) {
            messageLabel.setText(resources.getString(RESOURCEKEY_CANCELLING));
            cancelButton.setText(resources.getString(RESOURCEKEY_CLOSE));
            currentTask.cancel(true);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'WaitTitledPane.fxml'.";
        assert progressIndicator != null : "fx:id=\"progressIndicator\" was not injected: check your FXML file 'WaitTitledPane.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'WaitTitledPane.fxml'.";

    }

    private void onRunning(WorkerStateEvent event) {
        LOG.entering(getClass().getName(), "onRunning", event);
        Task<?> task = (Task<?>) event.getSource();
        LOG.finer(() -> String.format("%s task started", task.getTitle()));
        fireEvent(new WaitTitledPaneEvent(event.getSource(), this, task, WaitTitledPaneEvent.RUNNING));
    }

    private void onFailed(WorkerStateEvent event) {
        LOG.entering(getClass().getName(), "onFailed", event);
        removeTaskEventHandlers();
        Task<?> task = (Task<?>) event.getSource();
        LOG.log(Level.SEVERE, String.format("Background task %s failed", task.getTitle()), task.getException());
        fireEvent(new WaitTitledPaneEvent(event.getSource(), this, (Task<?>) event.getSource(), WaitTitledPaneEvent.FAILED));
    }

    private void onSucceeded(WorkerStateEvent event) {
        LOG.entering(getClass().getName(), "onSucceeded", event);
        removeTaskEventHandlers();
        Task<?> task = (Task<?>) event.getSource();
        LOG.finer(() -> String.format("%s task succeeded", task.getTitle()));
        fireEvent(new WaitTitledPaneEvent(event.getSource(), this, task, WaitTitledPaneEvent.SUCCEEDED));
    }

    private void onCanceled(WorkerStateEvent event) {
        LOG.entering(getClass().getName(), "onCanceled", event);
        removeTaskEventHandlers();
        Task<?> task = (Task<?>) event.getSource();
        LOG.warning(() -> String.format("%s task canceled", task.getTitle()));
        fireEvent(new WaitTitledPaneEvent(event.getSource(), this, task, WaitTitledPaneEvent.CANCELED));
    }

    public WaitTitledPane addOnDone(EventHandler<WaitTitledPaneEvent> eventHandler) {
        addEventHandler(WaitTitledPaneEvent.DONE, eventHandler);
        return this;
    }

    public WaitTitledPane removeOnDone(EventHandler<WaitTitledPaneEvent> eventHandler) {
        removeEventHandler(WaitTitledPaneEvent.DONE, eventHandler);
        return this;
    }

    public WaitTitledPane addOnRunning(EventHandler<WaitTitledPaneEvent> eventHandler) {
        addEventHandler(WaitTitledPaneEvent.RUNNING, eventHandler);
        return this;
    }

    public WaitTitledPane removeOnRunning(EventHandler<WaitTitledPaneEvent> eventHandler) {
        removeEventHandler(WaitTitledPaneEvent.RUNNING, eventHandler);
        return this;
    }

    public WaitTitledPane addOnSucceeded(EventHandler<WaitTitledPaneEvent> eventHandler) {
        addEventHandler(WaitTitledPaneEvent.SUCCEEDED, eventHandler);
        return this;
    }

    public WaitTitledPane removeOnSucceeded(EventHandler<WaitTitledPaneEvent> eventHandler) {
        removeEventHandler(WaitTitledPaneEvent.SUCCEEDED, eventHandler);
        return this;
    }

    public WaitTitledPane addOnCanceled(EventHandler<WaitTitledPaneEvent> eventHandler) {
        addEventHandler(WaitTitledPaneEvent.CANCELED, eventHandler);
        return this;
    }

    public WaitTitledPane removeOnCanceled(EventHandler<WaitTitledPaneEvent> eventHandler) {
        removeEventHandler(WaitTitledPaneEvent.CANCELED, eventHandler);
        return this;
    }

    public WaitTitledPane addOnCancelAcknowledged(EventHandler<WaitTitledPaneEvent> eventHandler) {
        addEventHandler(WaitTitledPaneEvent.CANCEL_ACK, eventHandler);
        return this;
    }

    public WaitTitledPane removeOnCancelAcknowledged(EventHandler<WaitTitledPaneEvent> eventHandler) {
        removeEventHandler(WaitTitledPaneEvent.CANCEL_ACK, eventHandler);
        return this;
    }

    public WaitTitledPane addOnFailed(EventHandler<WaitTitledPaneEvent> eventHandler) {
        addEventHandler(WaitTitledPaneEvent.FAILED, eventHandler);
        return this;
    }

    public WaitTitledPane removeOnFailed(EventHandler<WaitTitledPaneEvent> eventHandler) {
        removeEventHandler(WaitTitledPaneEvent.FAILED, eventHandler);
        return this;
    }

    public WaitTitledPane addOnFailAcknowledged(EventHandler<WaitTitledPaneEvent> eventHandler) {
        addEventHandler(WaitTitledPaneEvent.FAIL_ACK, eventHandler);
        return this;
    }

    public WaitTitledPane removeOnFailAcknowledged(EventHandler<WaitTitledPaneEvent> eventHandler) {
        removeEventHandler(WaitTitledPaneEvent.FAIL_ACK, eventHandler);
        return this;
    }

    /**
     * Schedules a {@link Task} for execution.
     *
     * @param task The {@link Task} to startNow after the specified delay.
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
        String s = ((ReadOnlyStringProperty) observable).get();
        if (null == s || s.trim().isEmpty()) {
            setText(resources.getString(RESOURCEKEY_PLEASEWAIT));
        } else {
            setText(s);
        }
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
        } else if (progress < 0.0 && totalWork > 0.0 && workDone >= 0.0) {
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
        if (message.isEmpty()) {
            collapseNode(messageLabel);
        } else {
            restoreLabeled(messageLabel, message);
        }
    }

    private void onMessageChanged(Observable observable) {
        onStatusChanged(((ReadOnlyStringProperty) observable).get(), currentTask.getTotalWork(), currentTask.getWorkDone(), currentTask.getProgress());
    }

    private void onTotalWorkChanged(Observable observable) {
        onStatusChanged(currentTask.getMessage(), ((ReadOnlyDoubleProperty) observable).get(), currentTask.getWorkDone(), currentTask.getProgress());
    }

    private void onWorkDoneChanged(Observable observable) {
        onStatusChanged(currentTask.getMessage(), currentTask.getTotalWork(), ((ReadOnlyDoubleProperty) observable).get(), currentTask.getProgress());
    }

    private void onProgressChanged(Observable observable) {
        onStatusChanged(currentTask.getMessage(), currentTask.getTotalWork(), currentTask.getWorkDone(), ((ReadOnlyDoubleProperty) observable).get());
    }

    private synchronized void prepare(Task<?> task) {
        if (null != currentTask || task.getState() != Worker.State.READY) {
            throw new IllegalStateException();
        }
        currentTask = task;
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, this::onRunning);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, this::onFailed);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, this::onCanceled);
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this::onSucceeded);
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
            task.removeEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, this::onCanceled);
            task.removeEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this::onSucceeded);
            task.titleProperty().removeListener(this::onTitleChanged);
            task.messageProperty().removeListener(this::onMessageChanged);
            task.totalWorkProperty().removeListener(this::onTotalWorkChanged);
            task.workDoneProperty().removeListener(this::onWorkDoneChanged);
            task.progressProperty().removeListener(this::onProgressChanged);
        }
        return task;
    }

}
