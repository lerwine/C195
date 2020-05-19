package scheduler.fx;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainResourceKeys;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/fx/HelpContent.fxml")
public class HelpContent extends BorderPane {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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
        assert titleLabel != null : "fx:id=\"titleLabel\" was not injected: check your FXML file 'HelpContent.fxml'.";
        assert contentScrollPane != null : "fx:id=\"contentScrollPane\" was not injected: check your FXML file 'HelpContent.fxml'.";
        parentProperty().addListener(this::parentChanged);
    }

    @SuppressWarnings("unchecked")
    private void parentChanged(Observable observable) {
        prefWidthProperty().unbind();
        minWidthProperty().unbind();
        prefHeightProperty().unbind();
        minHeightProperty().unbind();
        Parent parent = ((ObservableValue<Parent>) observable).getValue();

        if (null != parent) {
            while (!(parent instanceof Region) && null != parent.getParent()) {
                if (null == parent.getParent()) {
                    parent = parent.getParent();
                }
            }
            Region pr = (Region) parent;
            prefWidthProperty().bind(pr.widthProperty());
            minWidthProperty().bind(pr.widthProperty());
            prefHeightProperty().bind(pr.heightProperty());
            minHeightProperty().bind(pr.heightProperty());
        }
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
            restoreNode(this);
        }
    }

    public <T extends Node> T show(String title, String fxmlResourceName, String bundleBaseName) throws IOException {
        FXMLLoader loader;
        if (null != bundleBaseName && !bundleBaseName.trim().isEmpty()) {
            Class<? extends HelpContent> c = getClass();
            loader = new FXMLLoader(c.getResource(fxmlResourceName), ResourceBundle.getBundle(bundleBaseName, Locale.getDefault(),
                    c.getClassLoader()));
        } else {
            loader = new FXMLLoader(getClass().getResource(fxmlResourceName));
        }
        T result = loader.load();
        show(title, result);
        return result;
    }

    public void hide() {
        contentScrollPane.setContent(new Text(""));
        titleLabel.setText("");
        collapseNode(this);
    }
}
