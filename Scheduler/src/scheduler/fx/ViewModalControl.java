/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.fx;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * View-modal FXML Controller
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
@FXMLResource("/scheduler/fx/ViewModalControl.fxml")
public class ViewModalControl extends Control {

    private static final Logger LOG = Logger.getLogger(ViewModalControl.class.getName());

    @FXML // fx:id="backingBorderPane"
    private BorderPane backingBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="titleLabel"
    private Label titleLabel; // Value injected by FXMLLoader

    @FXML // fx:id="contentScrollPane"
    private ScrollPane contentScrollPane; // Value injected by FXMLLoader

    @FXML // fx:id="buttonBar"
    private ButtonBar buttonBar; // Value injected by FXMLLoader

    private final ObservableList<ButtonActionHandler> handlers;
    private final StringProperty title;
    private final ObjectProperty<Node> content;
    private final ListProperty<ButtonType> buttonTypes;
    private final ObjectProperty<EventHandler<ViewModalEvent>> onHidden;
    private EventHandler<ViewModalEvent> showingHandler = null;

    @SuppressWarnings("LeakingThisInConstructor")
    public ViewModalControl() {
        onHidden = new SimpleObjectProperty<>();
        onHidden.addListener(this::onOnHiddenChanged);
        handlers = FXCollections.observableArrayList();
        title = new SimpleStringProperty();
        content = new SimpleObjectProperty<>();
        buttonTypes = new SimpleListProperty<>(FXCollections.observableArrayList());
        buttonTypes.add(ButtonType.CLOSE);
        try {
            ViewControllerLoader.loadView(this, new BorderPane());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert backingBorderPane != null : "fx:id=\"backingBorderPane\" was not injected: check your FXML file 'ViewModalControl.fxml'.";
        assert titleLabel != null : "fx:id=\"titleLabel\" was not injected: check your FXML file 'ViewModalControl.fxml'.";
        assert contentScrollPane != null : "fx:id=\"contentScrollPane\" was not injected: check your FXML file 'ViewModalControl.fxml'.";
        assert buttonBar != null : "fx:id=\"buttonBar\" was not injected: check your FXML file 'ViewModalControl.fxml'.";

        getChildren().add(backingBorderPane);
        buttonTypes.addListener(this::onListChanged);
        titleLabel.textProperty().bind(title);
        contentScrollPane.contentProperty().bind(content);
        onListChanged(buttonTypes);
        backingBorderPane.minWidthProperty().bind(widthProperty());
        backingBorderPane.prefWidthProperty().bind(widthProperty());
        backingBorderPane.minHeightProperty().bind(heightProperty());
        backingBorderPane.prefHeightProperty().bind(heightProperty());
        backingBorderPane.visibleProperty().bind(visibleProperty());
        setVisible(false);
    }

    /**
     * Gets the title displayed along the top.
     * 
     * @return The title displayed along the top.
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Sets the title displayed along the top.
     * 
     * @param value The title to be displayed along the top.
     */
    public void setTitle(String value) {
        title.set(value);
    }

    public StringProperty titleProperty() {
        return title;
    }

    /**
     * Gets the center content node.
     * 
     * @return The center content node.
     */
    public Node getContent() {
        return content.get();
    }

    /**
     * Sets the center content node.
     * 
     * @param value The new center content node.
     */
    public void setContent(Node value) {
        content.set(value);
    }

    public ObjectProperty contentProperty() {
        return content;
    }

    /**
     * Gets the button types displayed along the bottom.
     * 
     * @return The button types displayed along the bottom.
     */
    public ObservableList<ButtonType> getButtonTypes() {
        return buttonTypes.get();
    }

    /**
     * Gets the button types to be displayed along the bottom.
     * 
     * @param value The button types to be displayed along the bottom.
     */
    public void setButtonTypes(ObservableList<ButtonType> value) {
        buttonTypes.set(value);
    }

    public ListProperty<ButtonType> buttonTypesProperty() {
        return buttonTypes;
    }

    /**
     * Gets the event handler that will be called when the view modal control is hidden.
     * 
     * @return The event handler that will be called when the view modal control is hidden.
     */
    public EventHandler<ViewModalEvent> getOnHidden() {
        return onHidden.get();
    }

    /**
     * Sets the event handler that will be called when the view modal control is hidden.
     * 
     * @param value The event handler that will be called when the view modal control is hidden.
     */
    public void setOnHidden(EventHandler<ViewModalEvent> value) {
        onHidden.set(value);
    }

    public ObjectProperty<EventHandler<ViewModalEvent>> onHiddenProperty() {
        return onHidden;
    }
    
    private void fireOnHidden(ButtonType button) {
        fireEvent(new ViewModalEvent(this, content.get(), button));
    }

    /**
     * Displays a JavaFX node as modal content.
     * 
     * @param title The title to be displayed along the top.
     * @param source The content to be displayed.
     * @param onHidden The event handler that will be called when the view modal control is hidden.
     * @param button The button types to be displayed along the bottom.
     */
    public synchronized void show(String title, Node source, EventHandler<ViewModalEvent> onHidden, ButtonType ...button) {
        Objects.requireNonNull(source);
        if (null != showingHandler) {
            setContent(null);
            fireOnHidden(null);
            removeEventHandler(ViewModalEvent.VIEW_MODAL, showingHandler);
        }
        showingHandler = onHidden;
        buttonTypes.set(FXCollections.observableArrayList(button));
        if (null != onHidden)
            addEventHandler(ViewModalEvent.VIEW_MODAL, onHidden);
        setTitle(title);
        setContent(source);
        setVisible(true);
    }
    
    /**
     * Loads FXML and displays it as modal content.
     * 
     * @param <T> The content node type.
     * @param title The title to be displayed along the top.
     * @param fxmlResourceName The name of the FXML resource to load.
     * @param onHidden The event handler that will be called when the view modal control is hidden.
     * @param button The button types to be displayed along the bottom.
     * @return The loaded content node.
     * @throws IOException If unable to load FXML resource.
     */
    public <T extends Node> T show(String title, String fxmlResourceName, EventHandler<ViewModalEvent> onHidden,
            ButtonType ...button) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResourceName));
        T result = loader.load();
        show(title, result, onHidden, button);
        return result;
    }

    /**
     * Displays a JavaFX text nodes as modal content.
     * 
     * @param title The title to be displayed along the top.
     * @param iterator The text nodes to be displayed.
     * @param onHidden The event handler that will be called when the view modal control is hidden.
     * @param button The button types to be displayed along the bottom.
     */
    public synchronized void show(String title, Iterator<Text> iterator, EventHandler<ViewModalEvent> onHidden, ButtonType ...button) {
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
        show(title, source, onHidden, button);
    }
    
    /**
     * Displays a JavaFX text nodes as modal content.
     * 
     * @param <T> The type of object containing the text nodes.
     * @param title The title to be displayed along the top.
     * @param source The text nodes to be displayed.
     * @param onHidden The event handler that will be called when the view modal control is hidden.
     * @param button The button types to be displayed along the bottom.
     */
    public <T extends Iterable<Text>> void show(String title, T source, EventHandler<ViewModalEvent> onHidden, ButtonType ...button) {
        if (null == source) {
            show(title, Collections.emptyIterator());
        } else {
            show(title, source.iterator(), onHidden, button);
        }
    }

    /**
     * Displays a JavaFX text nodes as modal content.
     * 
     * @param title The title to be displayed along the top.
     * @param source The text nodes to be displayed.
     * @param onHidden The event handler that will be called when the view modal control is hidden.
     * @param button The button types to be displayed along the bottom.
     */
    public void show(String title, Stream<Text> source, EventHandler<ViewModalEvent> onHidden, ButtonType ...button) {
        if (null == source) {
            show(title, Collections.emptyIterator(), onHidden, button);
        } else {
            show(title, source.iterator(), onHidden, button);
        }
    }

    /**
     * Loads FXML and displays it as modal content.
     * 
     * @param <T> The content node type.
     * @param title The title to be displayed along the top.
     * @param fxmlResourceName The name of the FXML resource to load.
     * @param button The button types to be displayed along the bottom.
     * @return The loaded content node.
     * @throws IOException If unable to load FXML resource.
     */
    public <T extends Node> T show(String title, String fxmlResourceName, ButtonType ...button) throws IOException {
        return show(title, fxmlResourceName, null, button);
    }

    /**
     * Displays a JavaFX text nodes as modal content.
     * 
     * @param title The title to be displayed along the top.
     * @param iterator The text nodes to be displayed.
     * @param onHidden The event handler that will be called when the view modal control is hidden.
     * @param button The button types to be displayed along the bottom.
     */
    private void show(String title, Iterator<Text> iterator, ButtonType ...button) {
        show(title, iterator, null, button);
    }

    /**
     * Displays a JavaFX text nodes as modal content.
     * 
     * @param <T> The type of object containing the text nodes.
     * @param title The title to be displayed along the top.
     * @param source The text nodes to be displayed.
     * @param button The button types to be displayed along the bottom.
     */
    public <T extends Iterable<Text>> void show(String title, T source, ButtonType ...button) {
        show(title, source, null, button);
    }

    /**
     * Displays a JavaFX text nodes as modal content.
     * 
     * @param title The title to be displayed along the top.
     * @param source The text nodes to be displayed.
     * @param button The button types to be displayed along the bottom.
     */
    public void show(String title, Stream<Text> source, ButtonType ...button) {
        show(title, source, null, button);
    }

    /**
     * Displays a JavaFX node as modal content.
     * 
     * @param title The title to be displayed along the top.
     * @param source The content to be displayed.
     * @param button The button types to be displayed along the bottom.
     */
    public void show(String title, Node source, ButtonType ...button) {
        show(title, source, null, button);
    }

    public void hide(ButtonType type) {
        setTitle("");
        setVisible(false);
        setContent(null);
        fireOnHidden(type);
    }

    public void hide() {
        hide(null);
    }

    private void onOnHiddenChanged(ObservableValue<? extends EventHandler<ViewModalEvent>> observable, EventHandler<ViewModalEvent> oldValue,
            EventHandler<ViewModalEvent> newValue) {
        if (null != oldValue)
            removeEventHandler(ViewModalEvent.VIEW_MODAL, oldValue);
        if (null != newValue)
            addEventHandler(ViewModalEvent.VIEW_MODAL, newValue);
    }
    @SuppressWarnings("unchecked")
    private synchronized void onListChanged(Observable observable) {
        ObservableList<ButtonType> items = ((ListProperty<ButtonType>) observable).get();
        HashSet<ButtonType> types = new HashSet<>();
        if (null != items && !items.isEmpty()) {
            items.forEach((t) -> {
                if (null != t && !types.contains(t)) {
                    types.add(t);
                }
            });
        }
        if (types.isEmpty()) {
            types.add(ButtonType.CLOSE);
        }
        ButtonActionHandler[] change = handlers.stream().filter((t) -> !types.contains(t.type)).toArray(ButtonActionHandler[]::new);
        ObservableList<Node> buttons = buttonBar.getButtons();
        if (change.length > 0) {
            for (ButtonActionHandler h : change) {
                h.button.removeEventHandler(ActionEvent.ACTION, h);
                buttons.remove(h.button);
                handlers.remove(h);
            }
        }
        change = types.stream().filter((t) -> !handlers.stream().anyMatch((u) -> u.type == t)).map((t) -> new ButtonActionHandler(t)).toArray(ButtonActionHandler[]::new);
        if (change.length > 0) {
            for (ButtonActionHandler h : change) {
                buttons.add(h.button);
                handlers.add(h);
                h.button.addEventHandler(ActionEvent.ACTION, h);
            }
        }
    }

    private class ButtonActionHandler implements EventHandler<ActionEvent> {

        private final ButtonType type;
        private final Button button;

        ButtonActionHandler(ButtonType type) {
            button = new Button((this.type = type).getText());
            ButtonBar.setButtonData(button, type.getButtonData());
        }

        @Override
        public void handle(ActionEvent event) {
            hide(type);
        }

    }

}
