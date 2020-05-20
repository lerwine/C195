package scheduler.view.task;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
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
public class WaitBorderPane extends BorderPane {

    private static final Logger LOG = Logger.getLogger(WaitBorderPane.class.getName());

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
    void initialize() {
        assert waitAccordion != null : "fx:id=\"waitAccordion\" was not injected: check your FXML file 'WaitMessage.fxml'.";
        collapseNode(this);
    }

    private void onRunning(WaitTitledPaneEvent event) {
        WaitTitledPane pane = event.getTarget();
        waitAccordion.getPanes().add(pane);
        restoreNode(this);
        pane.setExpanded(true);
    }

    private void onFinished(WaitTitledPaneEvent event) {
        ObservableList<TitledPane> panes = waitAccordion.getPanes();
        panes.remove(event.getTarget());
        if (panes.isEmpty()) {
            collapseNode(this);
            return;
        }
        Optional<TitledPane> f = panes.stream().filter((t) -> ((WaitTitledPane) t).isExpanded()).findFirst();
        if (!f.isPresent()) {
            f = panes.stream().filter((t) -> ((WaitTitledPane) t).isFaulted()).findFirst();
            if (f.isPresent()) {
                f.get().setExpanded(true);
            } else {
                panes.get(panes.size() - 1).setExpanded(true);
            }
        }
        restoreNode(this);
    }

    /**
     * Schedules a {@link Task} for execution.
     *
     * @param task The {@link Task} to startNow after the specified delay.
     * @param delay The time from now to delay execution.
     * @param unit The time unit of the delay parameter.
     */
    public void schedule(Task<?> task, long delay, TimeUnit unit) {
        WaitTitledPane pane = new WaitTitledPane();
        pane.setOnTaskRunning(this::onRunning);
        pane.setOnTaskDone(this::onFinished);
        pane.schedule(task, delay, unit);
    }

    public synchronized void startNow(Task<?> task) {
        WaitTitledPane pane = new WaitTitledPane();
        pane.setOnTaskRunning(this::onRunning);
        pane.setOnTaskDone(this::onFinished);
        pane.startNow(task);
    }

}
