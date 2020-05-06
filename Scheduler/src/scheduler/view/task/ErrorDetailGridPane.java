package scheduler.view.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.Values;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML ErrorDetailGridPane class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/view/task/ErrorDetailGridPane.fxml")
public class ErrorDetailGridPane extends GridPane {

    private static final Logger LOG = Logger.getLogger(ErrorDetailGridPane.class.getName());

    public static ErrorDetailGridPane of(String logMessage, Throwable error, int maxDepth, boolean ignoreCause) {
        return of(logMessage, error, maxDepth, Optional.of(ignoreCause));
    }

    public static ErrorDetailGridPane of(String logMessage, Throwable error, int maxDepth) {
        return of(logMessage, error, maxDepth, Optional.empty());
    }

    public static ErrorDetailGridPane of(String logMessage, Throwable error, boolean ignoreCause) {
        return of(logMessage, error, 32, ignoreCause);
    }

    public static ErrorDetailGridPane of(Throwable error, int maxDepth, boolean ignoreCause) {
        return of(null, error, maxDepth, ignoreCause);
    }

    public static ErrorDetailGridPane of(String logMessage, Throwable error) {
        return of(logMessage, error, 32);
    }

    public static ErrorDetailGridPane of(Throwable error, int maxDepth) {
        return of(null, error, maxDepth);
    }

    public static ErrorDetailGridPane of(Throwable error, boolean ignoreCause) {
        return of(null, error, ignoreCause);
    }

    public static ErrorDetailGridPane of(Throwable error) {
        return of(null, error);
    }

    private static ErrorDetailGridPane of(String logMessage, Throwable error, int maxDepth, Optional<Boolean> ignoreCause) {
        ErrorDetailGridPane result = new ErrorDetailGridPane(error);
        try {
            ViewControllerLoader.initializeCustomControl(result);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError(ex);
        }
        if (null == logMessage || logMessage.trim().isEmpty()) {
            collapseNode(result.logMessageLabel);
            collapseNode(result.logMessageTextField);
        } else {
            result.logMessageTextField.setText(logMessage);
        }

        if (error instanceof SQLException) {
            SQLException sqlException = (SQLException) error;
            SQLException ex;
            if (maxDepth < 1 || (ignoreCause.isPresent() && !ignoreCause.get()) || null == (ex = sqlException.getNextException())) {
                collapseNode(result.relatedExceptionsPane);
            } else {
                SQLException n;
                if (maxDepth == 1 || null == (n = ex.getNextException())) {
                    result.relatedExceptionsPane.setContent(of(ex, maxDepth - 1, false));
                } else {
                    Accordion ra = new Accordion();
                    result.relatedExceptionsPane.setContent(ra);
                    TitledPane rp = new TitledPane();
                    rp.setText(ex.getClass().getName());
                    ra.getPanes().add(rp);
                    rp.setContent(of(ex, maxDepth - 1, false));
                    for (int i = maxDepth - 2; i >= 0; i--) {
                        rp = new TitledPane();
                        rp.setText(n.getClass().getName());
                        ra.getPanes().add(rp);
                        rp.setContent(of(n, i, false));
                        if (null == (n = n.getNextException())) {
                            break;
                        }
                    }
                }
            }
        }

        Throwable causedBy;
        if (maxDepth < 1 || (ignoreCause.isPresent() && ignoreCause.get()) || null == (causedBy = error.getCause())) {
            collapseNode(result.causedByPane);
        } else {
            Throwable c;
            if (maxDepth == 1 || null == (c = causedBy.getCause())) {
                result.causedByPane.setContent(of(causedBy, maxDepth - 1, true));
            } else {
                Accordion ca = new Accordion();
                result.causedByPane.setContent(ca);
                TitledPane cp = new TitledPane();
                cp.setText(causedBy.getClass().getName());
                ca.getPanes().add(cp);
                cp.setContent(of(causedBy, maxDepth - 1, true));
                for (int i = maxDepth - 2; i >= 0; i--) {
                    cp = new TitledPane();
                    cp.setText(c.getClass().getName());
                    ca.getPanes().add(cp);
                    cp.setContent(of(c, i, true));
                    if (null == (c = c.getCause())) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    private final Throwable error;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="logMessageLabel"
    private Label logMessageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="logMessageTextField"
    private TextField logMessageTextField; // Value injected by FXMLLoader

    @FXML // fx:id="typeTextField"
    private TextField typeTextField; // Value injected by FXMLLoader

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="messgeTextField"
    private TextField messgeTextField; // Value injected by FXMLLoader

    @FXML // fx:id="errorCodeLabel"
    private Label errorCodeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="errorCodeTextField"
    private TextField errorCodeTextField; // Value injected by FXMLLoader

    @FXML // fx:id="stateLabel"
    private Label stateLabel; // Value injected by FXMLLoader

    @FXML // fx:id="stateTextField"
    private TextField stateTextField; // Value injected by FXMLLoader

    @FXML // fx:id="detailAccordion"
    private Accordion detailAccordion; // Value injected by FXMLLoader

    @FXML // fx:id="relatedExceptionsPane"
    private TitledPane relatedExceptionsPane; // Value injected by FXMLLoader

    @FXML // fx:id="causedByPane"
    private TitledPane causedByPane; // Value injected by FXMLLoader

    @FXML // fx:id="stackTracePane"
    private TitledPane stackTracePane; // Value injected by FXMLLoader

    @FXML // fx:id="stackTraceTextArea"
    private TextArea stackTraceTextArea; // Value injected by FXMLLoader

    private ErrorDetailGridPane(Throwable error) {
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert logMessageLabel != null : "fx:id=\"logMessageLabel\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert logMessageTextField != null : "fx:id=\"logMessageTextField\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert typeTextField != null : "fx:id=\"typeTextField\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert messgeTextField != null : "fx:id=\"messgeTextField\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert errorCodeLabel != null : "fx:id=\"errorCodeLabel\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert errorCodeTextField != null : "fx:id=\"errorCodeTextField\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert stateLabel != null : "fx:id=\"stateLabel\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert stateTextField != null : "fx:id=\"stateTextField\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert detailAccordion != null : "fx:id=\"detailAccordion\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert relatedExceptionsPane != null : "fx:id=\"relatedExceptionsPane\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert causedByPane != null : "fx:id=\"causedByPane\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert stackTracePane != null : "fx:id=\"stackTracePane\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";
        assert stackTraceTextArea != null : "fx:id=\"stackTraceTextArea\" was not injected: check your FXML file 'ErrorDetailGridPane.fxml'.";

        typeTextField.setText(error.getClass().getName());
        String s = error.getLocalizedMessage();
        if (null == s || s.trim().isEmpty()) {
            s = error.getMessage();
        }
        if (null == s || s.trim().isEmpty()) {
            collapseNode(messageLabel);
            collapseNode(messgeTextField);
        } else {
            messgeTextField.setText(s);
        }

        if (error instanceof SQLException) {
            SQLException sqlException = (SQLException) error;
            errorCodeTextField.setText(NumberFormat.getIntegerInstance(Locale.getDefault(Locale.Category.DISPLAY)).format(sqlException.getErrorCode()));
            s = sqlException.getSQLState();
            if (Values.isNullWhiteSpaceOrEmpty(s)) {
                collapseNode(stateLabel);
                collapseNode(stateTextField);
            } else {
                stateTextField.setText(s);
            }
        } else {
            collapseNode(errorCodeLabel);
            collapseNode(errorCodeTextField);
            collapseNode(stateLabel);
            collapseNode(stateTextField);
            collapseNode(relatedExceptionsPane);
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        stackTraceTextArea.setText(sw.toString());

    }
}
