package scheduler.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import scheduler.App;
import scheduler.AppResources;
import scheduler.util.Values;

/**
 * FXML Controller class
 *
 * @author lerwi
 */
public class ErrorDialogDetailController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="logMessageLabel"
    private Label logMessageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="logMessageTextField"
    private TextField logMessageTextField; // Value injected by FXMLLoader

    @FXML // fx:id="typeLabel"
    private Label typeLabel; // Value injected by FXMLLoader

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

    @FXML // fx:id="stackTracePane"
    private TitledPane stackTracePane; // Value injected by FXMLLoader

    @FXML // fx:id="stackTraceTextArea"
    private TextArea stackTraceTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="relatedExceptionsPane"
    private TitledPane relatedExceptionsPane; // Value injected by FXMLLoader

    @FXML // fx:id="causedByPane"
    private TitledPane causedByPane; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert logMessageLabel != null : "fx:id=\"logMessageLabel\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert logMessageTextField != null : "fx:id=\"logMessageTextField\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert typeLabel != null : "fx:id=\"typeLabel\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert typeTextField != null : "fx:id=\"typeTextField\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert messgeTextField != null : "fx:id=\"messgeTextField\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert errorCodeLabel != null : "fx:id=\"errorCodeLabel\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert errorCodeTextField != null : "fx:id=\"errorCodeTextField\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert stateLabel != null : "fx:id=\"stateLabel\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert stateTextField != null : "fx:id=\"stateTextField\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert detailAccordion != null : "fx:id=\"detailAccordion\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert stackTracePane != null : "fx:id=\"stackTracePane\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert stackTraceTextArea != null : "fx:id=\"stackTraceTextArea\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert relatedExceptionsPane != null : "fx:id=\"relatedExceptionsPane\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
        assert causedByPane != null : "fx:id=\"causedByPane\" was not injected: check your FXML file 'ErrorDialogDetail.fxml'.";
    }

    private void initialize(Throwable error, String message, int maxDepth, Optional<Boolean> ignoreCause) throws IOException {
        if (ignoreCause.isPresent()) {
            SchedulerController.collapseNode(logMessageLabel);
            SchedulerController.collapseNode(logMessageTextField);
            if (Values.isNullWhiteSpaceOrEmpty(message)) {
                SchedulerController.collapseNode(typeLabel);
                SchedulerController.collapseNode(typeTextField);
            } else {
                typeTextField.setText(message);
            }
        } else {
            if (Values.isNullWhiteSpaceOrEmpty(message)) {
                SchedulerController.collapseNode(logMessageLabel);
                SchedulerController.collapseNode(logMessageTextField);
            } else {
                logMessageTextField.setText(message);
            }
            typeTextField.setText(error.getClass().getName());
        }
        String s = error.getLocalizedMessage();
        if (Values.isNullWhiteSpaceOrEmpty(s)) {
            SchedulerController.collapseNode(messageLabel);
            SchedulerController.collapseNode(messgeTextField);
        } else {
            messgeTextField.setText(s);
        }
        if (error instanceof SQLException) {
            SQLException sqlException = (SQLException) error;
            errorCodeTextField.setText(NumberFormat.getIntegerInstance(Locale.getDefault(Locale.Category.DISPLAY)).format(sqlException.getErrorCode()));
            s = sqlException.getSQLState();
            if (Values.isNullWhiteSpaceOrEmpty(s)) {
                SchedulerController.collapseNode(stateLabel);
                SchedulerController.collapseNode(stateTextField);
            } else {
                stateTextField.setText(s);
            }
            SQLException ex;
            if (maxDepth < 1 || (ignoreCause.isPresent() && !ignoreCause.get()) || null == (ex = sqlException.getNextException())) {
                SchedulerController.collapseNode(relatedExceptionsPane);
            } else {
                SQLException n;
                if (maxDepth == 1 || null == (n = ex.getNextException())) {
                    relatedExceptionsPane.setContent(load(ex, maxDepth - 1, false, ex.getClass().getName()));
                } else {
                    Accordion ra = new Accordion();
                    relatedExceptionsPane.setContent(ra);
                    TitledPane rp = new TitledPane();
                    rp.setText(ex.getClass().getName());
                    ra.getPanes().add(rp);
                    rp.setContent(load(ex, maxDepth - 1, false, null));
                    for (int i = maxDepth - 2; i >= 0; i--) {
                        rp = new TitledPane();
                        rp.setText(n.getClass().getName());
                        ra.getPanes().add(rp);
                        rp.setContent(load(n, i, false, null));
                        if (null == (n = n.getNextException())) {
                            break;
                        }
                    }
                }
            }
        } else {
            SchedulerController.collapseNode(errorCodeLabel);
            SchedulerController.collapseNode(errorCodeTextField);
            SchedulerController.collapseNode(stateLabel);
            SchedulerController.collapseNode(stateTextField);
            SchedulerController.collapseNode(relatedExceptionsPane);
        }

        Throwable causedBy;
        if (maxDepth < 1 || (ignoreCause.isPresent() && ignoreCause.get()) || null == (causedBy = error.getCause())) {
            SchedulerController.collapseNode(causedByPane);
        } else {
            Throwable c;
            if (maxDepth == 1 || null == (c = causedBy.getCause())) {
                causedByPane.setContent(load(causedBy, maxDepth - 1, true, causedBy.getClass().getName()));
            } else {
                Accordion ca = new Accordion();
                causedByPane.setContent(ca);
                TitledPane cp = new TitledPane();
                cp.setText(causedBy.getClass().getName());
                ca.getPanes().add(cp);
                cp.setContent(load(causedBy, maxDepth - 1, true, null));
                for (int i = maxDepth - 2; i >= 0; i--) {
                    cp = new TitledPane();
                    cp.setText(c.getClass().getName());
                    ca.getPanes().add(cp);
                    cp.setContent(load(c, i, true, null));
                    if (null == (c = c.getCause())) {
                        break;
                    }
                }
            }
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        stackTraceTextArea.setText(sw.toString());
    }

    private static GridPane load(Throwable error, int maxDepth, boolean ignoreCause, String message) throws IOException {
        FXMLLoader loader = new FXMLLoader(ErrorDialogDetailController.class.getResource("/scheduler/view/ErrorDialogDetail.fxml"),
                AppResources.getResources());
        GridPane view = loader.load();
        ((ErrorDialogDetailController) loader.getController()).initialize(error, message, maxDepth, Optional.of(ignoreCause));
        return view;
    }

    public static GridPane load(Throwable error, String logMessage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ErrorDialogDetailController.class.getResource("/scheduler/view/ErrorDialogDetail.fxml"),
                AppResources.getResources());
        GridPane view = loader.load();
        ((ErrorDialogDetailController) loader.getController()).initialize(error, logMessage, 32, Optional.empty());
        return view;
    }
}
