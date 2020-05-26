/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.fx;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.FileSystemException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.xml.bind.JAXBException;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.addCssClass;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.Values;
import scheduler.util.ViewControllerLoader;
import scheduler.view.SymbolText;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/fx/ErrorDetailTitledPane.fxml")
public final class ErrorDetailTitledPane extends TitledPane {

    public static ErrorDetailTitledPane of(String heading, String message, Throwable error, int maxDepth) throws IOException {
        ErrorDetailTitledPane result = new ErrorDetailTitledPane();
        ViewControllerLoader.initializeCustomControl(result);
        addCssClass(result, CssClassName.ERROR);
        result.setText((Values.isNullWhiteSpaceOrEmpty(heading)) ? error.getClass().getSimpleName() : heading);
        ObservableList<Node> children = result.contentVBox.getChildren();
        if (!Values.isNullWhiteSpaceOrEmpty(message)) {
            children.add(0, NodeUtil.createCompactHBox(NodeUtil.createLabel(message, CssClassName.MESSAGE), NodeUtil.createSymbolButton(SymbolText.COPY, (event) -> {
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(message);
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            })));
        }
        result.intialize(error, maxDepth, new LinkedList<>());
        result.okButton = NodeUtil.createButton("OK");
        result.contentVBox.getChildren().add(NodeUtil.createButtonBar(result.okButton));
        return result;
    }

    public static ErrorDetailTitledPane of(String heading, String message, Throwable error) throws IOException {
        return ErrorDetailTitledPane.of(heading, message, error, 32);
    }

    private Button okButton;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="contentVBox"
    private VBox contentVBox; // Value injected by FXMLLoader

    @FXML // fx:id="errorMessageHeadingLabel"
    private Label errorMessageHeadingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="errorMessageHBox"
    private HBox errorMessageHBox; // Value injected by FXMLLoader

    @FXML // fx:id="errorMessageTextLabel"
    private Label errorMessageTextLabel; // Value injected by FXMLLoader

    @FXML // fx:id="errorTypeHBox"
    private HBox errorTypeHBox; // Value injected by FXMLLoader

    @FXML // fx:id="errorTypeLabel"
    private Label errorTypeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="property1HeadingLabel"
    private Label property1HeadingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="property1HBox"
    private HBox property1HBox; // Value injected by FXMLLoader

    @FXML // fx:id="property1TextLabel"
    private Label property1TextLabel; // Value injected by FXMLLoader

    @FXML // fx:id="property2HeadingLabel"
    private Label property2HeadingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="property2HBox"
    private HBox property2HBox; // Value injected by FXMLLoader

    @FXML // fx:id="property2TextLabel"
    private Label property2TextLabel; // Value injected by FXMLLoader

    @FXML // fx:id="property3HeadingLabel"
    private Label property3HeadingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="property3HBox"
    private HBox property3HBox; // Value injected by FXMLLoader

    @FXML // fx:id="property3TextLabel"
    private Label property3TextLabel; // Value injected by FXMLLoader

    @FXML // fx:id="detailAccordion"
    private Accordion detailAccordion; // Value injected by FXMLLoader

    @FXML
    void onCopyErrorMessageAction(ActionEvent event) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(errorMessageTextLabel.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    @FXML
    void onCopyErrorTypeAction(ActionEvent event) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(errorTypeLabel.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    @FXML
    void onCopyProperty1Action(ActionEvent event) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(property1TextLabel.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    @FXML
    void onCopyProperty2Action(ActionEvent event) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(property2TextLabel.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    @FXML
    void onCopyProperty3Action(ActionEvent event) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(property3TextLabel.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert errorMessageHeadingLabel != null : "fx:id=\"errorMessageHeadingLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert errorMessageHBox != null : "fx:id=\"errorMessageHBox\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert errorMessageTextLabel != null : "fx:id=\"errorMessageTextLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert errorTypeHBox != null : "fx:id=\"errorTypeHBox\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert errorTypeLabel != null : "fx:id=\"errorTypeLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property1HeadingLabel != null : "fx:id=\"property1HeadingLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property1HBox != null : "fx:id=\"property1HBox\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property1TextLabel != null : "fx:id=\"property1TextLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property2HeadingLabel != null : "fx:id=\"property2HeadingLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property2HBox != null : "fx:id=\"property2HBox\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property2TextLabel != null : "fx:id=\"property2TextLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property3HeadingLabel != null : "fx:id=\"property3HeadingLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property3HBox != null : "fx:id=\"property3HBox\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert property3TextLabel != null : "fx:id=\"property3TextLabel\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";
        assert detailAccordion != null : "fx:id=\"detailAccordion\" was not injected: check your FXML file 'ErrorDetailTitledPane.fxml'.";

    }

    public final void setOnAction(EventHandler<ActionEvent> value) {
        okButton.setOnAction(value);
    }

    private void intialize(Throwable error, int maxDepth, LinkedList<Throwable> ignore) throws IOException {
        String message = error.getLocalizedMessage();
        if (Values.isNullWhiteSpaceOrEmpty(message)) {
            message = error.getMessage();
        }
        if (Values.isNullWhiteSpaceOrEmpty(message)) {
            collapseNode(errorMessageHeadingLabel);
            collapseNode(errorMessageHBox);
        } else {
            errorMessageTextLabel.setText(message);
        }
        errorTypeLabel.setText(error.getClass().getName());

        LinkedList<Throwable> causedBy = new LinkedList<>();
        if (error instanceof SQLException) {
            SQLException ex = (SQLException) error;
            property1HeadingLabel.setText("Error Code:");
            property1TextLabel.setText(NumberFormat.getIntegerInstance().format(ex.getErrorCode()));
            message = ex.getSQLState();
            if (Values.isNullWhiteSpaceOrEmpty(message)) {
                collapseNode(property2HeadingLabel);
                collapseNode(property2HBox);
            } else {
                property2HeadingLabel.setText("State:");
                property2TextLabel.setText(message);
            }
            collapseNode(property3HeadingLabel);
            collapseNode(property3HBox);
            if (maxDepth > 0) {
                for (SQLException e = ex.getNextException(); null != e; e = e.getNextException()) {
                    if (!ignore.contains(e)) {
                        causedBy.add(e);
                        ignore.add(e);
                    }
                }
            }
        } else if (error instanceof URISyntaxException) {
            URISyntaxException ex = (URISyntaxException) error;
            property1HeadingLabel.setText("Index:");
            property1TextLabel.setText(NumberFormat.getIntegerInstance().format(ex.getIndex()));
            String input = ex.getInput();
            message = ex.getReason();
            if (Values.isNullWhiteSpaceOrEmpty(input)) {
                if (Values.isNullWhiteSpaceOrEmpty(message)) {
                    collapseNode(property2HeadingLabel);
                    collapseNode(property2HBox);
                } else {
                    property2HeadingLabel.setText("Reason:");
                    property2TextLabel.setText(message);
                }
                collapseNode(property3HeadingLabel);
                collapseNode(property3HBox);
            } else {
                property2HeadingLabel.setText("Input:");
                property2TextLabel.setText(input);
                if (Values.isNullWhiteSpaceOrEmpty(message)) {
                    collapseNode(property3HeadingLabel);
                    collapseNode(property3HBox);
                } else {
                    property3TextLabel.setText(message);
                }
            }
        } else if (error instanceof FileSystemException) {
            FileSystemException ex = (FileSystemException) error;
            String file = ex.getFile();
            String otherFile = ex.getOtherFile();
            message = ex.getReason();
            if (Values.isNullWhiteSpaceOrEmpty(file)) {
                if (Values.isNullWhiteSpaceOrEmpty(otherFile)) {
                    if (Values.isNullWhiteSpaceOrEmpty(message)) {
                        collapseNode(property1HeadingLabel);
                        collapseNode(property1HBox);
                    } else {
                        property1HeadingLabel.setText("Reason:");
                        property1TextLabel.setText(message);
                    }
                    collapseNode(property2HeadingLabel);
                    collapseNode(property2HBox);
                } else {
                    property1HeadingLabel.setText("Other File:");
                    property1TextLabel.setText(otherFile);
                    if (Values.isNullWhiteSpaceOrEmpty(message)) {
                        collapseNode(property2HeadingLabel);
                        collapseNode(property2HBox);
                    } else {
                        property2HeadingLabel.setText("Reason:");
                        property2TextLabel.setText(message);
                    }
                }
                collapseNode(property3HeadingLabel);
                collapseNode(property3HBox);
            } else {
                property1HeadingLabel.setText("File:");
                property1TextLabel.setText(file);
                if (Values.isNullWhiteSpaceOrEmpty(otherFile)) {
                    if (Values.isNullWhiteSpaceOrEmpty(message)) {
                        collapseNode(property2HeadingLabel);
                        collapseNode(property2HBox);
                    } else {
                        property2HeadingLabel.setText("Reason:");
                        property2TextLabel.setText(message);
                    }
                    collapseNode(property3HeadingLabel);
                    collapseNode(property3HBox);
                } else {
                    property2HeadingLabel.setText("Other File:");
                    property2TextLabel.setText(otherFile);
                    if (Values.isNullWhiteSpaceOrEmpty(message)) {
                        collapseNode(property3HeadingLabel);
                        collapseNode(property3HBox);
                    } else {
                        property3TextLabel.setText(message);
                    }
                }
            }
        } else {
            if (error instanceof DateTimeParseException) {
                DateTimeParseException ex = (DateTimeParseException) error;
                message = ex.getParsedString();
                property1HeadingLabel.setText("Error Index:");
                property1TextLabel.setText(NumberFormat.getIntegerInstance().format(ex.getErrorIndex()));
                if (Values.isNullWhiteSpaceOrEmpty(message)) {
                    collapseNode(property2HeadingLabel);
                    collapseNode(property2HBox);
                } else {
                    property2HeadingLabel.setText("Parsed String:");
                    property2TextLabel.setText(message);
                }
            } else if (error instanceof MissingResourceException) {
                MissingResourceException ex = (MissingResourceException) error;
                String key = ex.getKey();
                message = ex.getClassName();
                if (Values.isNullWhiteSpaceOrEmpty(key)) {
                    if (Values.isNullWhiteSpaceOrEmpty(message)) {
                        collapseNode(property1HeadingLabel);
                        collapseNode(property1HBox);
                    } else {
                        property1HeadingLabel.setText("Class Name:");
                        property1TextLabel.setText(message);
                    }
                    collapseNode(property2HeadingLabel);
                    collapseNode(property2HBox);
                } else {
                    property1HeadingLabel.setText("Key:");
                    property1TextLabel.setText(key);
                    if (Values.isNullWhiteSpaceOrEmpty(message)) {
                        collapseNode(property2HeadingLabel);
                        collapseNode(property2HBox);
                    } else {
                        property2HeadingLabel.setText("Class Name:");
                        property2TextLabel.setText(message);
                    }
                }
            } else {
                if (error instanceof ParseException) {
                    property1HeadingLabel.setText("Error Offset:");
                    property1TextLabel.setText(NumberFormat.getIntegerInstance().format(((ParseException) error).getErrorOffset()));
                } else if (error instanceof JAXBException) {
                    message = ((JAXBException) error).getErrorCode();
                    if (Values.isNullWhiteSpaceOrEmpty(message)) {
                        collapseNode(property1HeadingLabel);
                        collapseNode(property1HBox);
                    } else {
                        property1HeadingLabel.setText("Error Code:");
                        property1TextLabel.setText(message);
                    }
                }
                collapseNode(property2HeadingLabel);
                collapseNode(property2HBox);
            }
            collapseNode(property3HeadingLabel);
            collapseNode(property3HBox);
        }
        if (maxDepth > 0) {
            Throwable cause = error.getCause();
            if (null != cause && !ignore.contains(cause)) {
                causedBy.add(cause);
                ignore.add(cause);
            }
        }
        try (StringWriter sw = new StringWriter()) {
            try (PrintWriter pw = new PrintWriter(sw)) {
                error.printStackTrace(pw);
                message = sw.toString();
            }
        } catch (Throwable ex) {
            Logger.getLogger(ErrorDetailTitledPane.class.getName()).log(Level.WARNING, "Error getting stack trace", ex);
            message = "";
        }

        ObservableList<TitledPane> panes;
        if (causedBy.isEmpty()) {
            if (Values.isNullWhiteSpaceOrEmpty(message)) {
                collapseNode(detailAccordion);
                return;
            }
            panes = detailAccordion.getPanes();
        } else {
            panes = detailAccordion.getPanes();
            causedBy.forEach((t) -> {
                ErrorDetailTitledPane result = new ErrorDetailTitledPane();
                try {
                    ViewControllerLoader.initializeCustomControl(result);
                    addCssClass(result, CssClassName.WARNING);
                    result.setText(String.format("Caused by %s", error.getClass().getSimpleName()));
                    result.intialize(t, maxDepth - 1, ignore);
                    panes.add(result);
                } catch (IOException ex) {
                    Logger.getLogger(ErrorDetailTitledPane.class.getName()).log(Level.SEVERE, "Error initializing custom control", ex);
                }
            });
            if (Values.isNullWhiteSpaceOrEmpty(message)) {
                return;
            }
        }
        TitledPane titledPane = new TitledPane();
        titledPane.setText("Stack Trace");
        addCssClass(titledPane, CssClassName.WARNING);
        panes.add(titledPane);
        TextArea textArea = new TextArea();
        titledPane.setContent(textArea);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setText(message);
    }

}
