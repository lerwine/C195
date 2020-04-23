package scheduler.view;

import java.util.Collections;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/HelpContent.fxml")
public class HelpContent {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootBorderPane"
    private BorderPane rootBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="titleLable"
    private Label titleLabel; // Value injected by FXMLLoader

    @FXML // fx:id="contentScrollPane"
    private ScrollPane contentScrollPane; // Value injected by FXMLLoader

    @FXML
    private void onCloseButtonAction(ActionEvent event) {
        hide();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'HelpContent.fxml'.";
        assert titleLabel != null : "fx:id=\"titleLabel\" was not injected: check your FXML file 'HelpContent.fxml'.";
        assert contentScrollPane != null : "fx:id=\"contentScrollPane\" was not injected: check your FXML file 'HelpContent.fxml'.";

    }

    void initialize(Pane parent) {
        rootBorderPane.setVisible(false);
        parent.getChildren().add(parent);
        rootBorderPane.prefWidthProperty().bind(parent.widthProperty());
        rootBorderPane.minWidthProperty().bind(parent.widthProperty());
        rootBorderPane.prefHeightProperty().bind(parent.heightProperty());
        rootBorderPane.minHeightProperty().bind(parent.heightProperty());
    }

    private void show(String title, Iterator<Text> iterator) {
        if (iterator.hasNext()) {
            Text t = iterator.next();
            while (null == t) {
                if (!iterator.hasNext()) {
                    hide();
                    return;
                }
                t = iterator.next();
            }
            TextFlow source = new TextFlow();
            ObservableList<Node> children = source.getChildren();
            children.add(t);
            while (iterator.hasNext()) {
                if (null != t) {
                    children.add(t);
                }
            }
            show(title, source);
        } else {
            hide();
        }
    }

    public <T extends Iterable<Text>> void show(String title, T source) {
        if (null == source) {
            show(title, Collections.emptyIterator());
        } else {
            show(title, source.iterator());
        }
    }

    public void show(String title, Stream<Text> source) {
        if (null == source) {
            show(title, Collections.emptyIterator());
        } else {
            show(title, source.iterator());
        }
    }

    public void show(String title, Node source) {
        if (null == source) {
            hide();
        } else {
            titleLabel.setText((null == title || title.trim().isEmpty()) ? resources.getString(MainResourceKeys.RESOURCEKEY_SCHEDULERHELP) : title);
            contentScrollPane.setContent(source);
        }
    }

    public void hide() {
        contentScrollPane.setContent(new Text(""));
        titleLabel.setText("");
        rootBorderPane.setVisible(false);
    }
}
