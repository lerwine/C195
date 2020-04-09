package scheduler.view.event;

/**
 * Represents a {@link FxmlViewEvent} type.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public enum FxmlViewEventType {
    /**
     * The view has been loaded and controller has initialized by the {@link javafx.fxml.FXMLLoader}. This is the first event in the view/controller
     * life-cycle.
     */
    LOADED,
    /**
     * View has been added as the {@link javafx.scene.Scene#root} for the {@link javafx.scene.Scene} of a {@link javafx.stage.Stage} or the view has
     * been added to another {@link javafx.scene.Parent} container. This event follows {@link #LOADED} and precedes {@link #SHOWN}
     */
    BEFORE_SHOW,
    /**
     * Indicates that the owning {@link javafx.stage.Stage} has been shown. This event follows {@link #BEFORE_SHOW} and precedes {@link #UNLOADED}
     */
    SHOWN,
    /**
     * Indicates that the owning {@link javafx.stage.Stage} has been hidden or the view has been removed from its {@link javafx.scene.Parent}
     * container. This is the last event in the view/controller life-cycle.
     */
    UNLOADED
}
