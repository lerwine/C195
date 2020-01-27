package scheduler.view;

import java.util.Objects;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Manages an FXML view.
 * @author erwinel
 */
public interface ViewManager {

    /**
     * Gets the {@link Parent} content node.
     * @return The {@link Parent} content node.
     */
    Parent getContent();

    /**
     * Sets the {@link Parent} content node.
     * @param content The new {@link Parent} content node.
     */
    void setContent(Parent content);

    void setContent(Parent content, double width, double height);

    /**
     * Gets the {@link javafx.scene.Scene#root} node of the {@link javafx.scene.Scene} for the current {@link Stage}.
     * @return The {@link javafx.scene.Scene#root} node of the {@link javafx.scene.Scene} for the current {@link Stage}.
     */
    Parent getRoot();

    /**
     * Sets the {@link javafx.scene.Scene#root} node of the {@link javafx.scene.Scene} for the current {@link Stage}.
     * @param content The {@link javafx.scene.Scene#root} node for the {@link javafx.scene.Scene} of the current {@link Stage}.
     */
    void setRoot(Parent content);

    /**
     * Gets the {@link Stage#title} of the current {@link Stage}.
     * @return The {@link Stage#title} of the current {@link Stage}.
     */
    String getWindowTitle();

    /**
     * Sets the {@link Stage#title} of the current {@link Stage}.
     * @param text The {@link Stage#title}for the current {@link Stage}.
     */
    void setWindowTitle(String text);
    
    /**
     * Hides the current {@link Stage}.
     */
    void closeWindow();

    /**
     * Creates a new {@link Stage} and {@link ViewManager} as a child of the current {@link Stage}.
     * @param modality The {@link Modality} for the new {@link Stage}.
     * @return A {@link Stage} and {@link ViewManager} that is a child of the current {@link Stage}.
     */
    Pair<Stage, ViewManager> newChild(Modality modality);
    
    /**
     * Creates a {@link ViewManager} to manage a {@link Stage}.
     * @param stage The {@link Stage} of the view.
     * @return A {@link ViewManager} to manage a {@link Stage}.
     */
    public static ViewManager of(Stage stage) {
        return new ViewManager() {
            private Parent content;
            @Override
            public Parent getContent() { return content; }
            @Override
            public void setContent(Parent content) {
                Objects.requireNonNull(content);
                if (null != this.content && content == this.content)
                    return;
                stage.setScene(new Scene(content));
            }
            @Override
            public void setContent(Parent content, double width, double height) {
                stage.setScene(new Scene(Objects.requireNonNull(content), width, height));
            }
            @Override
            public Parent getRoot() { return stage.getScene().getRoot(); }
            @Override
            public void setRoot(Parent content) { stage.getScene().setRoot(content); }
            @Override
            public String getWindowTitle() { return stage.getTitle(); }
            @Override
            public void setWindowTitle(String text) { stage.setTitle(text); }
            @Override
            public void closeWindow() { stage.hide(); }
            @Override
            public Pair<Stage, ViewManager> newChild(Modality modality) {
                final Stage childStage = new Stage();
                childStage.initOwner(stage);
                childStage.initModality(modality);
                return new Pair<>(childStage, ViewManager.of(childStage));
            }
        };
    }
}
