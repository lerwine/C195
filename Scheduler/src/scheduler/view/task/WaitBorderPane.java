package scheduler.view.task;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import scheduler.fx.CssClassName;
import scheduler.fx.ErrorDetailTitledPane;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.addCssClass;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/task/WaitBorderPane.fxml")
public final class WaitBorderPane extends BorderPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(WaitBorderPane.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(WaitBorderPane.class.getName());

    @FXML
    private Accordion waitAccordion;

    @SuppressWarnings("LeakingThisInConstructor")
    public WaitBorderPane() {
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        assert waitAccordion != null : "fx:id=\"waitAccordion\" was not injected: check your FXML file 'WaitMessage.fxml'.";
        collapseNode(this);
        LOG.exiting(LOG.getName(), "initialize");
    }

    private void onRunning(WaitTitledPaneEvent event) {
        LOG.entering(LOG.getName(), "onRunning", event);
        WaitTitledPane pane = event.getTarget();
        waitAccordion.getPanes().add(pane);
        restoreNode(this);
        pane.setExpanded(true);
        LOG.exiting(LOG.getName(), "onRunning");
    }

    private void removeEventHandlers(WaitTitledPane pane) {
        pane.removeEventHandler(WaitTitledPaneEvent.RUNNING, this::onRunning);
        pane.removeEventHandler(WaitTitledPaneEvent.CANCELED, this::onCanceled);
        pane.removeEventHandler(WaitTitledPaneEvent.FAILED, this::onFailed);
        pane.removeEventHandler(WaitTitledPaneEvent.SUCCEEDED, this::onSucceeded);
    }

    private void removePane(TitledPane pane) {
        ObservableList<TitledPane> panes = waitAccordion.getPanes();
        panes.remove(pane);
        if (panes.isEmpty()) {
            collapseNode(this);
            return;
        }

        if (!panes.stream().anyMatch((t) -> (t).isExpanded())) {
            panes.stream().filter((t) -> (t instanceof ErrorDetailTitledPane)).findFirst().orElseGet(() -> panes.get(panes.size() - 1)).setExpanded(true);
        }
    }

    private void onSucceeded(WaitTitledPaneEvent event) {
        LOG.entering(LOG.getName(), "onSucceeded", event);
        WaitTitledPane pane = event.getTarget();
        removeEventHandlers(pane);
        removePane(pane);
        LOG.exiting(LOG.getName(), "onSucceeded");
    }

    private void onCanceled(WaitTitledPaneEvent event) {
        LOG.entering(LOG.getName(), "onCanceled", event);
        ObservableList<TitledPane> panes = waitAccordion.getPanes();
        WaitTitledPane oldPane = event.getTarget();
        removeEventHandlers(oldPane);
        TitledPane newPane = addCssClass(new TitledPane(), CssClassName.WARNING);
        newPane.setText(oldPane.getText());
        newPane.setContent(NodeUtil.createCompactVBox(NodeUtil.createLabel("Operation canceled."),
                NodeUtil.createButtonBar(NodeUtil.createButton("OK", (e) -> {
                    LOG.entering(LOG.getName(), "onOkButtonAction", new Object[]{event, oldPane, newPane, e});
                    onOkButtonAction(event, oldPane, newPane);
                }))));
        panes.set(panes.indexOf(oldPane), newPane);
        newPane.setExpanded(true);
    }

    private void onOkButtonAction(WaitTitledPaneEvent event, WaitTitledPane oldPane, TitledPane newPane) {
        removePane(newPane);
        WaitTitledPaneEvent ev = new WaitTitledPaneEvent(event.getSource(), oldPane, event.getTask(), WaitTitledPaneEvent.CANCEL_ACK);
        LOG.fine(() -> String.format("Firing %s%n\ton %s", ev, oldPane.getClass().getName()));
        oldPane.fireEvent(ev);
        LOG.exiting(LOG.getName(), "onOkButtonAction");
    }

    private void onFailed(WaitTitledPaneEvent event) {
        LOG.entering(LOG.getName(), "onFailed", event);
        ObservableList<TitledPane> panes = waitAccordion.getPanes();
        WaitTitledPane oldPane = event.getTarget();
        removeEventHandlers(oldPane);
        ErrorDetailTitledPane newPane;
        try {
            newPane = ErrorDetailTitledPane.of(oldPane.getText(), "An unexpected error has occurred", event.getTask().getException());
        } catch (IOException ex) {
            Logger.getLogger(WaitBorderPane.class.getName()).log(Level.SEVERE, "Error creating detail pane", ex);
            removePane(oldPane);
            WaitTitledPaneEvent ev = new WaitTitledPaneEvent(event.getSource(), oldPane, event.getTask(), WaitTitledPaneEvent.FAIL_ACK);
            LOG.fine(() -> String.format("Firing %s%n\ton %s", ev, oldPane.getClass().getName()));
            oldPane.fireEvent(ev);
            return;
        }
        newPane.setOnAction((e) -> onErrorDetailTitledPaneAction(event, oldPane, newPane));
        panes.set(panes.indexOf(oldPane), newPane);
        newPane.setExpanded(true);
        LOG.exiting(LOG.getName(), "onFailed");
    }

    private void onErrorDetailTitledPaneAction(WaitTitledPaneEvent event, WaitTitledPane oldPane, ErrorDetailTitledPane newPane) {
        LOG.entering(LOG.getName(), "onErrorDetailTitledPaneAction", new Object[]{event, oldPane, newPane});
        removePane(newPane);
        WaitTitledPaneEvent ev = new WaitTitledPaneEvent(event.getSource(), oldPane, event.getTask(), WaitTitledPaneEvent.FAIL_ACK);
        LOG.fine(() -> String.format("Firing %s%n\ton %s", ev, oldPane.getClass().getName()));
        oldPane.fireEvent(ev);
        LOG.exiting(LOG.getName(), "onErrorDetailTitledPaneAction");
    }

    /**
     * Schedules a {@link Task} for execution and displays a {@link WaitTitledPane} while the task is active.
     *
     * @param pane The {@link WaitTitledPane} to use for displaying status.
     * @param task The {@link Task} to startNow after the specified delay.
     * @param delay The time from now to delay execution.
     * @param unit The time unit of the delay parameter.
     */
    public void schedule(WaitTitledPane pane, Task<?> task, long delay, TimeUnit unit) {
        LOG.entering(LOG.getName(), "schedule", new Object[]{pane, task, delay, unit});
        pane.addEventHandler(WaitTitledPaneEvent.RUNNING, this::onRunning);
        pane.addEventHandler(WaitTitledPaneEvent.CANCELED, this::onCanceled);
        pane.addEventHandler(WaitTitledPaneEvent.FAILED, this::onFailed);
        pane.addEventHandler(WaitTitledPaneEvent.SUCCEEDED, this::onSucceeded);
        pane.schedule(task, delay, unit);
        LOG.exiting(LOG.getName(), "schedule");
    }

    public void schedule(Task<?> task, long delay, TimeUnit unit) {
        schedule(new WaitTitledPane(), task, delay, unit);
    }

    /**
     * Starts a {@link Task} immediately and displays a {@link WaitTitledPane} while the task is active.
     *
     * @param pane
     * @param task
     */
    public synchronized void startNow(WaitTitledPane pane, Task<?> task) {
        LOG.entering(LOG.getName(), "startNow", new Object[]{pane, task});
        pane.addEventHandler(WaitTitledPaneEvent.RUNNING, this::onRunning);
        pane.addEventHandler(WaitTitledPaneEvent.CANCELED, this::onCanceled);
        pane.addEventHandler(WaitTitledPaneEvent.FAILED, this::onFailed);
        pane.addEventHandler(WaitTitledPaneEvent.SUCCEEDED, this::onSucceeded);
        pane.startNow(task);
        LOG.exiting(LOG.getName(), "startNow");
    }

    public void startNow(Task<?> task) {
        startNow(new WaitTitledPane(), task);
    }
}
