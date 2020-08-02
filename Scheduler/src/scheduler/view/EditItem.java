package scheduler.view;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.events.ModelEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.events.OperationRequestEvent;
import scheduler.model.fx.EntityModel;
import scheduler.util.AlertHelper;
import scheduler.util.AnnotationHelper;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ParentWindowChangeListener;
import scheduler.util.StageManager;
import scheduler.util.ThrowableConsumer;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.EditItemResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.task.WaitBorderPane;

/**
 * The parent FXML custom control for editing {@link EntityModel} items in a new modal window.
 * <p>
 * This controller manages the {@link #saveChangesButton}, {@link #deleteButton}, and cancel button controls as well as labels for displaying the values for the
 * {@link EntityModel#getCreatedBy()}, {@link EntityModel#getCreateDate()}, {@link EntityModel#getLastModifiedBy()} and {@link EntityModel#getLastModifiedDate()} properties.
 * Properties that are specific to the {@link EntityModel} type are edited in a child {@link EditItem.ModelEditorController} custom control.</p>
 * <p>
 * The child editor is intended to be instantiated through the {@link EditItem#showAndWait(Window, Class, EntityModelImpl, boolean)} method.</p>
 * <p>
 * The child {@link EditItem.ModelEditorController} can be initialized with the current {@link EntityModel} by annotating a field named {@code "model"} with {@link ModelEditor}. It
 * can also be initialized with the current {@link WaitBorderPane} by annotating a field named {@code "waitBorderPane"} with {@link ModelEditor}</p>
 * <p>
 * The child {@link EditItem.ModelEditorController} can be notified when a new {@link EntityModel} has been successfully saved and the edit window is going to remain open by
 * annotating a method named {@code "onModelInserted"} having a single parameter, that is the same as the generic event type, with {@link ModelEditor}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of model being edited.
 * @param <U> The content node.
 */
@GlobalizationResource("scheduler/view/EditItem")
@FXMLResource("/scheduler/view/EditItem.fxml")
public final class EditItem<T extends EntityModel<?>, U extends Region & EditItem.ModelEditorController<T>> extends StackPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditItem.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    private static final String FIELD_NAME_WAIT_BORDER_PANE = "waitBorderPane";
    private static final String FIELD_NAME_MODEL = "model";
    private static final String FIELD_NAME_EDITWINDOWROOT = "editWindowRoot";
    private static final String METHOD_NAME_ON_MODEL_INSERTED = "onModelInserted";

    /**
     * Opens a new window for editing an {@link EntityModel} item.
     *
     * @param <T> The type of {@link EntityModel} that corresponds to the data access object.
     * @param <U> The type of {@link ModelEditorController} control for editing the model properties.
     * @param parentWindow The parent window.
     * @param editorRegion The {@link EditItem.ModelEditorController} that will be used for editing the entity-specific {@link EntityModel} properties.
     * @param model The {@link EntityModel} to be edited.
     * @param keepOpen {@code true} to keep the window open after saving the new {@link EntityModel}; otherwise {@code false} to close the window immediately after a successful
     * insert.
     * @param beforeShow The delegate to invoke after the {@code root} has been added to the {@link Scene} of the new {@link Stage}, but before it is shown.
     * @throws IOException if unable to open the edit window.
     */
    public static <T extends EntityModel<?>, U extends Region & EditItem.ModelEditorController<T>>
            void showAndWait(Window parentWindow, U editorRegion, T model, boolean keepOpen, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        LOG.entering(LOG.getName(), "showAndWait", new Object[]{parentWindow, editorRegion, model, keepOpen, beforeShow});
        EditItem<T, U> root = new EditItem<>(editorRegion, model, keepOpen);
        ViewControllerLoader.initializeCustomControl(root);
        Class<ModelEditor> annotationClass = ModelEditor.class;
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_MODEL, model);
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_EDITWINDOWROOT, root);
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_WAIT_BORDER_PANE, root.waitBorderPane);
        ViewControllerLoader.initializeCustomControl(editorRegion);
        StageManager.showAndWait(root, parentWindow, beforeShow);
        LOG.exiting(LOG.getName(), "showAndWait");
    }

    public static <U extends EntityModel<?>, S extends Region & EditItem.ModelEditorController<U>>
            void showAndWait(Window parentWindow, S editorRegion, U model, boolean keepOpen) throws IOException {
        LOG.entering(LOG.getName(), "showAndWait", new Object[]{parentWindow, editorRegion, model, keepOpen});
        EditItem<U, S> root = new EditItem<>(editorRegion, model, keepOpen);
        ViewControllerLoader.initializeCustomControl(root);
        Class<ModelEditor> annotationClass = ModelEditor.class;
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_MODEL, model);
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_EDITWINDOWROOT, root);
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_WAIT_BORDER_PANE, root.waitBorderPane);
        ViewControllerLoader.initializeCustomControl(editorRegion);
        StageManager.showAndWait(root, parentWindow);
        LOG.exiting(LOG.getName(), "showAndWait");
    }

    /**
     * Opens a new window for editing an {@link EntityModel} item.
     *
     * @param <T> The type of {@link EntityModel} that corresponds to the data access object.
     * @param <U> The type of {@link ModelEditorController} control for editing the model properties.
     * @param parentWindow The parent window.
     * @param editorType The {@link EditItem.ModelEditorController} class that will be instantiated for editing the entity-specific {@link EntityModel} properties.
     * @param model The {@link EntityModel} to be edited.
     * @param keepOpen {@code true} to keep the window open after saving the new {@link EntityModel}; otherwise {@code false} to close the window immediately after a successful
     * insert.
     * @param beforeShow The delegate to invoke after the {@code root} has been added to the {@link Scene} of the new {@link Stage}, but before it is shown.
     * @throws IOException if unable to open the edit window.
     */
    public static <T extends EntityModel<?>, U extends Region & EditItem.ModelEditorController<T>>
            void showAndWait(Window parentWindow, Class<? extends U> editorType, T model, boolean keepOpen, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        LOG.entering(LOG.getName(), "showAndWait", new Object[]{parentWindow, editorType, model, keepOpen, beforeShow});
        U editorRegion;
        try {
            editorRegion = editorType.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException("Error creating editor region", ex);
        }
        showAndWait(parentWindow, editorRegion, model, keepOpen, beforeShow);
        LOG.exiting(LOG.getName(), "showAndWait");
    }

    /**
     * Opens a new window for editing an {@link EntityModel} item.
     *
     * @param <T> The type of {@link EntityModel} that corresponds to the data access object.
     * @param <U> The type of {@link ModelEditorController} control for editing the model properties.
     * @param parentWindow The parent window.
     * @param editorType The {@link EditItem.ModelEditorController} class that will be instantiated for editing the entity-specific {@link EntityModel} properties.
     * @param model The {@link EntityModel} to be edited.
     * @param keepOpen {@code true} to keep the window open after saving the new {@link EntityModel}; otherwise {@code false} to close the window immediately after a successful
     * insert.
     * @throws IOException if unable to open the edit window.
     */
    public static <T extends EntityModel<?>, U extends Region & EditItem.ModelEditorController<T>>
            void showAndWait(Window parentWindow, Class<? extends U> editorType, T model, boolean keepOpen) throws IOException {
        LOG.entering(LOG.getName(), "showAndWait", new Object[]{parentWindow, editorType, model, keepOpen});
        U editorRegion;
        try {
            editorRegion = editorType.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException("Error creating editor region", ex);
        }
        showAndWait(parentWindow, editorRegion, model, keepOpen);
        LOG.exiting(LOG.getName(), "showAndWait");
    }

    //<editor-fold defaultstate="collapsed" desc="Fields">
    private final ParentWindowChangeListener stageChangeListener;
    private final U editorRegion;
    private final T model;
    private final boolean keepOpen;
    private final Method onModelInserted;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="contentAnchorPane"
    private AnchorPane contentAnchorPane; // Value injected by FXMLLoader

    @FXML // fx:id="createdLabel"
    private Label createdLabel; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateLabel"
    private Label lastUpdateLabel; // Value injected by FXMLLoader

    @FXML // fx:id="createdValue"
    private Label createdValue; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateValue"
    private Label lastUpdateValue; // Value injected by FXMLLoader

    @FXML // fx:id="saveChangesButton"
    private Button saveChangesButton; // Value injected by FXMLLoader

    @FXML // fx:id="deleteButton"
    private Button deleteButton; // Value injected by FXMLLoader

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader

    @FXML // fx:id="waitBorderPane"
    private WaitBorderPane waitBorderPane; // Value injected by FXMLLoader

    //</editor-fold>
    private EditItem(U editorRegion, T model, boolean keepOpen) {
        stageChangeListener = new ParentWindowChangeListener(sceneProperty());
        stageChangeListener.currentStageProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "new", "stageChangeHandler#currentStage"), "change", new Object[]{oldValue, newValue});
            if (null != oldValue) {
                oldValue.titleProperty().unbind();
            }
            if (null != newValue) {
                newValue.titleProperty().bind(editorRegion.windowTitleProperty());
            }
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "new", "stageChangeHandler#currentStage"), "change");
        });
        this.editorRegion = editorRegion;
        this.model = model;
        this.keepOpen = keepOpen;
        onModelInserted = (keepOpen) ? AnnotationHelper.getAnnotatedInstanceMethodsByNameAndParameter(editorRegion.getClass(), ModelEditor.class,
                Void.TYPE, METHOD_NAME_ON_MODEL_INSERTED, editorRegion.modelFactory().getModelResultEventClass()).findFirst().orElse(null) : null;
    }

    @FXML
    void onCancelButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onCancelButtonAction", event);
        ButtonType response;
        if (editorRegion.isModified()) {
            if (model.getRowState() == DataRowState.NEW) {
                response = AlertHelper.showWarningAlert(stageChangeListener.getCurrentWindow(), LOG,
                        resources.getString(RESOURCEKEY_CONFIRMCANCELNEW),
                        resources.getString(RESOURCEKEY_AREYOUSURECANCELNEW), ButtonType.YES, ButtonType.NO)
                        .orElse(ButtonType.NO);
            } else {
                response = AlertHelper.showWarningAlert(stageChangeListener.getCurrentWindow(), LOG,
                        resources.getString(RESOURCEKEY_CONFIRMDISCARDCHANGES),
                        resources.getString(RESOURCEKEY_AREYOUSUREDISCARDCHANGES), ButtonType.YES, ButtonType.NO)
                        .orElse(ButtonType.NO);
            }
            if (response != ButtonType.YES) {
                LOG.exiting(getClass().getName(), "onCancelButtonAction");
                return;
            }
        }

        stageChangeListener.hide();
        LOG.exiting(getClass().getName(), "onCancelButtonAction");
    }

    @FXML
    @SuppressWarnings("unchecked")
    void onDeleteButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onDeleteButtonAction", event);
        OperationRequestEvent<?, T> deleteRequestEvent = editorRegion.modelFactory().createDeleteRequestEvent(model, resources);
        Event.fireEvent(model.dataObject(), deleteRequestEvent);
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stageChangeListener.getCurrentWindow(), deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stageChangeListener.getCurrentWindow(), LOG,
                    resources.getString(RESOURCEKEY_CONFIRMDELETE),
                    resources.getString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO)
                    .ifPresent((t) -> {
                        if (t == ButtonType.YES) {
                            DataAccessObject.DeleteDaoTask<?, T> task = editorRegion.modelFactory().createDeleteTask(model);
                            task.setOnSucceeded(this::onDeleteDaoTaskSucceeded);
                            waitBorderPane.startNow(task);
                        }
                    });
        }
        LOG.exiting(getClass().getName(), "onDeleteButtonAction");
    }

    @FXML
    public void onSaveButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onSaveButtonAction", event);
        if (editorRegion.applyChanges()) {
            DataAccessObject.SaveDaoTask<?, T> task = editorRegion.modelFactory().createSaveTask(model);
            task.setOnSucceeded(this::onSaveDaoTaskSucceeded);
            waitBorderPane.startNow(task);
        }
        LOG.exiting(getClass().getName(), "onSaveButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(getClass().getName(), "initialize");
        assert contentAnchorPane != null : "fx:id=\"contentAnchorPane\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createdLabel != null : "fx:id=\"createdLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateLabel != null : "fx:id=\"lastUpdateLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createdValue != null : "fx:id=\"createdValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateValue != null : "fx:id=\"lastUpdateValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert saveChangesButton != null : "fx:id=\"saveChangesButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert waitBorderPane != null : "fx:id=\"waitBorderPane\" was not injected: check your FXML file 'EditItem.fxml'.";

        AnchorPane.setTopAnchor(editorRegion, 0.0);
        AnchorPane.setRightAnchor(editorRegion, 0.0);
        AnchorPane.setBottomAnchor(editorRegion, 0.0);
        AnchorPane.setLeftAnchor(editorRegion, 0.0);
        contentAnchorPane.getChildren().add(editorRegion);
        if (model.isNewRow()) {
            saveChangesButton.disableProperty().bind(editorRegion.validProperty().not());
            deleteButton.setDisable(true);
            collapseNode(deleteButton);
            collapseNode(createdLabel);
            collapseNode(createdValue);
            collapseNode(lastUpdateLabel);
            collapseNode(lastUpdateValue);
        } else {
            onEditMode();
        }
        LOG.exiting(getClass().getName(), "initialize");
    }

    public Button getSaveChangesButton() {
        return saveChangesButton;
    }

    private void onEditMode() {
        restoreNode(deleteButton);
        saveChangesButton.disableProperty().bind(editorRegion.validProperty().and(editorRegion.modifiedProperty()).not());
        cancelButton.textProperty().bind(Bindings.when(editorRegion.modifiedProperty())
                .then(resources.getString(RESOURCEKEY_CANCEL))
                .otherwise(resources.getString(RESOURCEKEY_CLOSE))
        );
        editorRegion.modifiedProperty().addListener((observable, oldValue, newValue) -> {
        });
        deleteButton.setDisable(false);
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        Locale.getDefault(Locale.Category.DISPLAY);
        restoreNode(createdLabel);
        restoreLabeled(createdValue, String.format(resources.getString(RESOURCEKEY_ONBYNAME),
                dtf.format(model.getCreateDate()), model.getCreatedBy()));
        restoreNode(lastUpdateLabel);
        restoreLabeled(lastUpdateValue, String.format(resources.getString(RESOURCEKEY_ONBYNAME),
                dtf.format(model.getLastModifiedDate()), model.getLastModifiedBy()));
    }

    private void onSaveDaoTaskSucceeded(WorkerStateEvent event) {
        LOG.entering(getClass().getName(), "onSaveDaoTaskSucceeded", event);
        @SuppressWarnings("unchecked")
        ModelEvent<?, T> modelEvent = ((DataAccessObject.SaveDaoTask<?, T>) event.getSource()).getValue();
        if (modelEvent instanceof ModelFailedEvent) {
            scheduler.util.AlertHelper.showWarningAlert(stageChangeListener.getCurrentWindow(), "Save Changes Failure", ((ModelFailedEvent<?, T>) modelEvent).getMessage(), ButtonType.OK);
        } else {
            switch (modelEvent.getOperation()) {
                case DB_INSERT:
                    if (keepOpen) {
                        if (null != onModelInserted) {
                            boolean accessible = onModelInserted.isAccessible();
                            if (!accessible) {
                                onModelInserted.setAccessible(true);
                            }
                            try {
                                onModelInserted.invoke(editorRegion, modelEvent);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                LOG.log(Level.SEVERE, "Error invoking onModelInserted method", ex);
                            } finally {
                                if (!accessible) {
                                    onModelInserted.setAccessible(false);
                                }
                            }
                        }
                        saveChangesButton.disableProperty().unbind();
                        onEditMode();
                    } else {
                        stageChangeListener.hide();
                    }
                    break;
                case DB_UPDATE:
                    stageChangeListener.hide();
                    break;
                default:
                    throw new AssertionError(modelEvent.getOperation().name());
            }
        }
        LOG.exiting(getClass().getName(), "onSaveDaoTaskSucceeded");
    }

    private void onDeleteDaoTaskSucceeded(WorkerStateEvent event) {
        LOG.entering(getClass().getName(), "onDeleteDaoTaskSucceeded", event);
        @SuppressWarnings("unchecked")
        ModelEvent<?, T> modelEvent = ((DataAccessObject.DeleteDaoTask<?, T>) event.getSource()).getValue();
        if (modelEvent instanceof ModelFailedEvent) {
            scheduler.util.AlertHelper.showWarningAlert(stageChangeListener.getCurrentWindow(), "Delete Failure", ((ModelFailedEvent<?, T>) modelEvent).getMessage(), ButtonType.OK);
        } else {
            stageChangeListener.hide();
        }
        LOG.exiting(getClass().getName(), "onDeleteDaoTaskSucceeded");
    }

    /**
     * Base class for editing specific {@link EntityModel} items. Derived controls are intended to be instantiated through the
     * {@link EditItem#showAndWait(Window, Class, EntityModelImpl, boolean)} method. This control will be inserted as the first child node of the parent {@code EditItem} control.
     *
     * @param <T> The {@link EntityModel} type.
     */
    public interface ModelEditorController<T extends EntityModel<?>> {

        /**
         * Gets the factory object for managing the current {@link EntityModel}.
         *
         * @return The factory object for managing the current {@link EntityModel}.
         */
        EntityModel.EntityModelFactory<?, T> modelFactory();

        /**
         * Gets the window title for the current parent {@link Stage}.
         *
         * @return The window title for the current parent {@link Stage}.
         */
        String getWindowTitle();

        /**
         * Gets the property that specifies the window title for the current parent {@link Stage}. This is bound to the {@link Stage#titleProperty()} of the parent {@link Stage}.
         *
         * @return The property that specifies the window title for the current parent {@link Stage}. This is bound to the {@link Stage#titleProperty()} of the parent
         * {@link Stage}.
         */
        ReadOnlyStringProperty windowTitleProperty();

        boolean isValid();

        /**
         * The inverse value of this property is bound to the {@link Button#disableProperty()} of the {@link EditItem#saveChangesButton}.
         *
         * @return A {@link ReadOnlyBooleanProperty} that contains a {@code false} value if the {@link EditItem#saveChangesButton} is to be disabled, otherwise {@code true} if it
         * is to be enabled.
         */
        ReadOnlyBooleanProperty validProperty();

        boolean isModified();

        ReadOnlyBooleanProperty modifiedProperty();

        /**
         * Applies changes to the underlying {@link EntityModel}.
         *
         * @return {@code true} if changes have been applied or {@code false} to abort.
         */
        boolean applyChanges();
    }

}
