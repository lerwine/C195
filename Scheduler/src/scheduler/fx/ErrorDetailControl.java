package scheduler.fx;

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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.createLabel;
import scheduler.util.StageManager;
import scheduler.util.Values;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/fx/ErrorDetailControl.fxml")
public class ErrorDetailControl extends GridPane {

    private static final Logger LOG = Logger.getLogger(ErrorDetailControl.class.getName());

    public static Optional<ButtonType> showAndWait(String title, Window owner, Throwable error, int maxDepth, String message, boolean ignoreCause,
            ButtonType... buttons) {
        ErrorDetailControl content = new ErrorDetailControl();
        try {
            content.initialize(error, maxDepth, Optional.of(ignoreCause));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading exception detail", ex);
            if (null == content) {
                ObservableList<Node> children = content.getChildren();
                children.add(createLabel(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_ERRORGETTINGEXCEPTIONDETAILS), CssClassName.TOPCONTROLLABEL));
                String m = ex.getLocalizedMessage();
                if (null == m || m.trim().isEmpty()) {
                    m = ex.getMessage();
                }
                if (null != m && !m.trim().isEmpty()) {
                    children.add(createLabel(ex.getMessage(), CssClassName.TOPLABELEDCONTROL));
                }
                m = error.getLocalizedMessage();
                if (null == m || m.trim().isEmpty()) {
                    m = error.getMessage();
                }
                if (null != m && !m.trim().isEmpty()) {
                    Label label = createLabel(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_ORIGINALERRORMESSAGE), CssClassName.TOPCONTROLLABEL);
                    VBox.setMargin(label, new Insets(8, 0, 0, 0));
                    children.add(label);
                    children.add(createLabel(error.getMessage(), CssClassName.TOPLABELEDCONTROL));
                }
            }
        }
        Alert alert;
        if (null == message || message.trim().isEmpty()) {
            message = AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORDETAILS);
        }
        if (null == buttons || buttons.length == 0) {
            alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        } else {
            alert = new Alert(Alert.AlertType.ERROR, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORDETAILS), buttons);
        }
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        if (null == owner) {
            owner = StageManager.getCurrentStage((Window) null);
        }
        if (null != owner) {
            alert.initOwner(owner);
        }
        alert.getDialogPane().setExpandableContent(content);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> showAndWait(String title, Throwable error, String message, ButtonType... buttons) {
        return showAndWait(title, null, error, 32, message, false, buttons);
    }

    public static Optional<ButtonType> logShowAndWait(Logger log, String title, Window owner, Throwable error, String userMessage, ButtonType... buttons) {
        if (null != log) {
            log.log(Level.SEVERE, userMessage, error);
        }
        return showAndWait(title, owner, error, 32, userMessage, false, buttons);
    }

    public static Optional<ButtonType> logShowAndWait(Logger log, String title, Throwable error, String userMessage, ButtonType... buttons) {
        return logShowAndWait(log, title, null, error, userMessage, buttons);
    }

    public static Optional<ButtonType> logShowAndWait(Logger log, String title, Throwable error, ButtonType... buttons) {
        if (null != log) {
            log.log(Level.SEVERE, null, error);
        }
        return showAndWait(title, null, error, 32, null, false, buttons);
    }

    private static GridPane load(Throwable error, int maxDepth, boolean ignoreCause, String message) throws IOException {
        FXMLLoader loader = new FXMLLoader(ErrorDetailControl.class.getResource("/scheduler/view/ErrorDetailDialog.fxml"),
                AppResources.getResources());
        GridPane view = loader.load();
        ((ErrorDetailControl) loader.getController()).initialize(error, maxDepth, Optional.of(ignoreCause));
        return view;
    }

    private static GridPane load(Throwable error) throws IOException {
        FXMLLoader loader = new FXMLLoader(ErrorDetailControl.class.getResource("/scheduler/view/ErrorDetailDialog.fxml"),
                AppResources.getResources());
        GridPane view = loader.load();
        ((ErrorDetailControl) loader.getController()).initialize(error, 32, Optional.empty());
        return view;
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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

    @SuppressWarnings("LeakingThisInConstructor")
    public ErrorDetailControl() {
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

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

    private void initialize(Throwable error, int maxDepth, Optional<Boolean> ignoreCause) throws IOException {
        String message = error.getLocalizedMessage();
        if (null == message || message.trim().isEmpty()) {
            message = error.getMessage();
        }
        if (ignoreCause.isPresent()) {
            collapseNode(logMessageLabel);
            collapseNode(logMessageTextField);
            if (Values.isNullWhiteSpaceOrEmpty(message)) {
                collapseNode(typeLabel);
                collapseNode(typeTextField);
            } else {
                typeTextField.setText(message);
            }
        } else {
            if (Values.isNullWhiteSpaceOrEmpty(message)) {
                collapseNode(logMessageLabel);
                collapseNode(logMessageTextField);
            } else {
                logMessageTextField.setText(message);
            }
            typeTextField.setText(error.getClass().getName());
        }
        String s = error.getLocalizedMessage();
        if (Values.isNullWhiteSpaceOrEmpty(s)) {
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
            SQLException ex;
            if (maxDepth < 1 || (ignoreCause.isPresent() && !ignoreCause.get()) || null == (ex = sqlException.getNextException())) {
                collapseNode(relatedExceptionsPane);
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
            collapseNode(errorCodeLabel);
            collapseNode(errorCodeTextField);
            collapseNode(stateLabel);
            collapseNode(stateTextField);
            collapseNode(relatedExceptionsPane);
        }

        Throwable causedBy;
        if (maxDepth < 1 || (ignoreCause.isPresent() && ignoreCause.get()) || null == (causedBy = error.getCause())) {
            collapseNode(causedByPane);
        } else {
            Throwable c;
            Region contentNode;
            if (maxDepth == 1 || null == (c = causedBy.getCause())) {
                contentNode = load(causedBy, maxDepth - 1, true, causedBy.getClass().getName());
                causedByPane.setContent(contentNode);
            } else {
                Accordion ca = new Accordion();
                contentNode = ca;
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
            bindExtents(contentNode, causedByPane);
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        stackTraceTextArea.setText(sw.toString());
    }

}
