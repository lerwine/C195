package scheduler.view.task;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/task/WaitMessage.fxml")
public class WaitMessage extends BorderPane {

    private static final Logger LOG = Logger.getLogger(WaitMessage.class.getName());

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="headingLabel"
    private Label headingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    private Task<?> first = null;
    private Task<?> last = null;

    @FXML
    void onCancelButtonAction(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'WaitMessage.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'WaitMessage.fxml'.";

    }

    private synchronized void updateVisibility() {
        if (null == first) {
            setVisible(false);
        } else {
            setVisible(true);
            updateMessage();
        }
    }

    private synchronized void updateMessage() {
        headingLabel.setText(last.headingText);
        messageLabel.setText(last.messageText);
        if (last.messageText.isEmpty()) {
            collapseNode(messageLabel);
        }
    }

    private synchronized void addTask(Task<?> task) {
        if (null == (task.previous = last)) {
            first = last = task;
            if (Platform.isFxApplicationThread()) {
                updateVisibility();
            } else {
                Platform.runLater(() -> updateVisibility());
            }
        } else {
            last = last.previous = task;
            if (Platform.isFxApplicationThread()) {
                updateMessage();
            } else {
                Platform.runLater(() -> updateMessage());
            }
        }
    }

    private synchronized void removeTask(Task<?> task) {
        task.waitMessage = null;
        if (null == task.previous) {
            if (null == (first = task.next)) {
                last = null;
                if (Platform.isFxApplicationThread()) {
                    updateVisibility();
                } else {
                    Platform.runLater(() -> updateVisibility());
                }
                return;
            }
            first.previous = task.next = null;
        } else if (null == (task.previous.next = task.next)) {
            task.previous = (last = task.previous).next = null;
        } else {
            task.next.previous = task.previous;
            task.previous = task.next = null;
        }
        updateMessage();
    }

    public synchronized void startNow(Task<?> task) {
        if (null != task.waitMessage) {
            throw new IllegalStateException();
        }
        ExecutorService svc = Executors.newSingleThreadExecutor();
        try {
            task.waitMessage = this;
            svc.execute(task);
        } finally {
            svc.shutdown();
        }
    }

    public static abstract class Task<V> extends javafx.concurrent.Task<V> {

        private String headingText = AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_PLEASEWAIT);
        private String messageText = "";
        private WaitMessage waitMessage = null;
        private Task<?> previous = null;
        private Task<?> next = null;

        public String getHeadingText() {
            return headingText;
        }

        protected void setHeadingText(String text) {
            if (null == text || (text = text.trim()).isEmpty()) {
                text = AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_PLEASEWAIT);
            }
            if (text.equals(headingText)) {
                return;
            }
            headingText = text;
            if (next == null) {
                WaitMessage w = waitMessage;
                if (null != w) {
                    Platform.runLater(() -> w.updateMessage());
                }
            }
        }

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String text) {
            if ((text = (null == text) ? "" : text.trim()).equals(messageText)) {
                return;
            }
            messageText = text;
            if (next == null) {
                WaitMessage w = waitMessage;
                if (null != w) {
                    Platform.runLater(() -> w.updateMessage());
                }
            }
        }

        @Override
        protected V call() throws Exception {
            waitMessage.addTask(this);
            try {
                return getResult();
            } finally {
                waitMessage.removeTask(this);
            }
        }

        @Override
        protected void succeeded() {
            if (Platform.isFxApplicationThread()) {
                try {
                    processResult(getValue());
                } catch (Exception ex) {
                    processException(ex);
                }
            } else {
                Platform.runLater(() -> {
                    try {
                        processResult(getValue());
                    } catch (Exception ex) {
                        processException(ex);
                    }
                });
            }
            super.succeeded();
        }

        @Override
        protected void cancelled() {
            LOG.log(Level.WARNING, String.format("\"%s\" operation cancelled", getTitle()));
            super.cancelled();
        }

        @Override
        protected void failed() {
            if (Platform.isFxApplicationThread()) {
                processException(getException());
            } else {
                Platform.runLater(() -> processException(getException()));
            }
            super.failed();
        }

        protected abstract V getResult();

        protected abstract void processResult(V value);

        protected abstract void processException(Throwable exception);

    }
}
