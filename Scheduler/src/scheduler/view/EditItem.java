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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
 * @param <T> The type of data access object that the model represents.
 * @param <U> The type of model being edited.
 * @param <S> The content node.
 * @param <E> The base {@link ModelEvent} type.
 */
@GlobalizationResource("scheduler/view/EditItem")
@FXMLResource("/scheduler/view/EditItem.fxml")
public final class EditItem<
        T extends DataAccessObject, U extends EntityModel<T>, S extends Region & EditItem.ModelEditorController<T, U, E>, E extends ModelEvent<T, U>> extends StackPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditItem.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    private static final String FIELD_NAME_WAIT_BORDER_PANE = "waitBorderPane";
    private static final String FIELD_NAME_MODEL = "model";
    private static final String METHOD_NAME_ON_MODEL_INSERTED = "onModelInserted";

    /**
     * Opens a new window for editing an {@link EntityModel} item.
     *
     * @param <T> The type of data access object.
     * @param <U> The type of {@link EntityModel} that corresponds to the data access object.
     * @param <S> The type of {@link ModelEditorController} control for editing the model properties.
     * @param <E> The {@link ModelEvent} type.
     * @param parentWindow The parent window.
     * @param editorRegion The {@link EditItem.ModelEditorController} that will be used for editing the entity-specific {@link EntityModel} properties.
     * @param model The {@link EntityModel} to be edited.
     * @param keepOpen {@code true} to keep the window open after saving the new {@link EntityModel}; otherwise {@code false} to close the window immediately after a successful
     * insert.
     * @param beforeShow The delegate to invoke after the {@code root} has been added to the {@link Scene} of the new {@link Stage}, but before it is shown.
     * @throws IOException if unable to open the edit window.
     */
    public static <T extends DataAccessObject, U extends EntityModel<T>, S extends Region & EditItem.ModelEditorController<T, U, E>, E extends ModelEvent<T, U>>
            void showAndWait(Window parentWindow, S editorRegion, U model, boolean keepOpen, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem<T, U, S, E> root = new EditItem<>(editorRegion, model, keepOpen);
        ViewControllerLoader.initializeCustomControl(root);
        Class<ModelEditor> annotationClass = ModelEditor.class;
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_MODEL, model);
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_WAIT_BORDER_PANE, root.waitBorderPane);
        ViewControllerLoader.initializeCustomControl(editorRegion);
        StageManager.showAndWait(root, parentWindow, beforeShow);
    }

    public static <T extends DataAccessObject, U extends EntityModel<T>, S extends Region & EditItem.ModelEditorController<T, U, E>, E extends ModelEvent<T, U>>
            void showAndWait(Window parentWindow, S editorRegion, U model, boolean keepOpen) throws IOException {
        EditItem<T, U, S, E> result = new EditItem<>(editorRegion, model, keepOpen);
        ViewControllerLoader.initializeCustomControl(result);
        Class<ModelEditor> annotationClass = ModelEditor.class;
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_MODEL, model);
        AnnotationHelper.tryInjectField(editorRegion, annotationClass, FIELD_NAME_WAIT_BORDER_PANE, result.waitBorderPane);
        ViewControllerLoader.initializeCustomControl(editorRegion);
        StageManager.showAndWait(result, parentWindow);
    }

    /**
     * Opens a new window for editing an {@link EntityModel} item.
     *
     * @param <T> The type of data access object.
     * @param <U> The type of {@link EntityModel} that corresponds to the data access object.
     * @param <S> The type of {@link ModelEditorController} control for editing the model properties.
     * @param <E> The {@link ModelEvent} type.
     * @param parentWindow The parent window.
     * @param editorType The {@link EditItem.ModelEditorController} class that will be instantiated for editing the entity-specific {@link EntityModel} properties.
     * @param model The {@link EntityModel} to be edited.
     * @param keepOpen {@code true} to keep the window open after saving the new {@link EntityModel}; otherwise {@code false} to close the window immediately after a successful
     * insert.
     * @param beforeShow The delegate to invoke after the {@code root} has been added to the {@link Scene} of the new {@link Stage}, but before it is shown.
     * @throws IOException if unable to open the edit window.
     */
    public static <T extends DataAccessObject, U extends EntityModel<T>, S extends Region & EditItem.ModelEditorController<T, U, E>, E extends ModelEvent<T, U>>
            void showAndWait(Window parentWindow, Class<? extends S> editorType, U model, boolean keepOpen, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        S editorRegion;
        try {
            editorRegion = editorType.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException("Error creating editor region", ex);
        }
        showAndWait(parentWindow, editorRegion, model, keepOpen, beforeShow);
    }

    /**
     * Opens a new window for editing an {@link EntityModel} item.
     *
     * @param <T> The type of data access object.
     * @param <U> The type of {@link EntityModel} that corresponds to the data access object.
     * @param <S> The type of {@link ModelEditorController} control for editing the model properties.
     * @param <E> The {@link ModelEvent} type.
     * @param parentWindow The parent window.
     * @param editorType The {@link EditItem.ModelEditorController} class that will be instantiated for editing the entity-specific {@link EntityModel} properties.
     * @param model The {@link EntityModel} to be edited.
     * @param keepOpen {@code true} to keep the window open after saving the new {@link EntityModel}; otherwise {@code false} to close the window immediately after a successful
     * insert.
     * @throws IOException if unable to open the edit window.
     */
    public static <T extends DataAccessObject, U extends EntityModel<T>, S extends Region & EditItem.ModelEditorController<T, U, E>, E extends ModelEvent<T, U>>
            void showAndWait(Window parentWindow, Class<? extends S> editorType, U model, boolean keepOpen) throws IOException {
        S editorRegion;
        try {
            editorRegion = editorType.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException("Error creating editor region", ex);
        }
        EditItem.showAndWait(parentWindow, editorRegion, model, keepOpen);
    }

    //<editor-fold defaultstate="collapsed" desc="Fields">
    @SuppressWarnings("unused")
    private final ParentWindowChangeListener.StageListener stageChangeListener;
    private final S editorRegion;
    private final U model;
    private final boolean keepOpen;
    private final Method onModelInserted;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="parentVBox"
    private VBox parentVBox; // Value injected by FXMLLoader

    @FXML // fx:id="contentGridPane"
    private GridPane contentGridPane; // Value injected by FXMLLoader

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
    private EditItem(S editorRegion, U model, boolean keepOpen) {
        stageChangeListener = ParentWindowChangeListener.createStageChangeHandler(sceneProperty(), (observable, oldValue, newValue) -> {
            if (null != oldValue) {
                oldValue.titleProperty().unbind();
            }
            if (null != newValue) {
                newValue.titleProperty().bind(editorRegion.windowTitleProperty());
            }
        });
        this.editorRegion = editorRegion;
        this.model = model;
        this.keepOpen = keepOpen;
        onModelInserted = (keepOpen) ? AnnotationHelper.getAnnotatedInstanceMethodsByNameAndParameter(editorRegion.getClass(), ModelEditor.class,
                Void.TYPE, METHOD_NAME_ON_MODEL_INSERTED, editorRegion.modelFactory().getModelEventClass()).findFirst().orElse(null) : null;
    }

    @FXML
    void onCancelButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCancelButtonAction", event);
        Stage stage = (Stage) getScene().getWindow();
        ButtonType response;
        if (editorRegion.isModified()) {
            if (model.getRowState() == DataRowState.NEW) {
                response = AlertHelper.showWarningAlert(stage, LOG,
                        resources.getString(RESOURCEKEY_CONFIRMCANCELNEW),
                        resources.getString(RESOURCEKEY_AREYOUSURECANCELNEW), ButtonType.YES, ButtonType.NO)
                        .orElse(ButtonType.NO);
            } else {
                response = AlertHelper.showWarningAlert(stage, LOG,
                        resources.getString(RESOURCEKEY_CONFIRMDISCARDCHANGES),
                        resources.getString(RESOURCEKEY_AREYOUSUREDISCARDCHANGES), ButtonType.YES, ButtonType.NO)
                        .orElse(ButtonType.NO);
            }
            if (response != ButtonType.YES) {
                return;
            }
        }

        getScene().getWindow().hide();
    }

    @FXML
    @SuppressWarnings("unchecked")
    void onDeleteButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDeleteButtonAction", event);
        OperationRequestEvent<T, U> deleteRequestEvent = editorRegion.modelFactory().createDeleteRequestEvent(model, resources);
        Event.fireEvent(model.dataObject(), deleteRequestEvent);
        Stage stage = (Stage) getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stage, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stage, LOG,
                    resources.getString(RESOURCEKEY_CONFIRMDELETE),
                    resources.getString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO)
                    .ifPresent((t) -> {
                        if (t == ButtonType.YES) {
                            DataAccessObject.DeleteDaoTask<T, U, E> task = editorRegion.modelFactory().createDeleteTask(model);
                            task.setOnSucceeded((e) -> {
                                E result = task.getValue();
                                if (result instanceof ModelFailedEvent) {
                                    scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure", ((ModelFailedEvent<T, U>) result).getMessage(), ButtonType.OK);
                                } else {
                                    getScene().getWindow().hide();
                                }
                            });
                            waitBorderPane.startNow(task);
                        }
                    });
        }
    }

    @FXML
    @SuppressWarnings({"incomplete-switch", "unchecked"})
    void onSaveButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onSaveButtonAction", event);
        editorRegion.applyChanges();
        DataAccessObject.SaveDaoTask<T, U, E> task = editorRegion.modelFactory().createSaveTask(model);
        task.setOnSucceeded((e) -> {
            E modelEvent = task.getValue();
            if (modelEvent instanceof ModelFailedEvent) {
                scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Save Changes Failure", ((ModelFailedEvent<T, U>) modelEvent).getMessage(), ButtonType.OK);
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
                            onEditMode();
                        } else {
                            getScene().getWindow().hide();
                        }
                        break;
                    case DB_UPDATE:
                        getScene().getWindow().hide();
                        break;
                }
            }
        });
        waitBorderPane.startNow(task);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert parentVBox != null : "fx:id=\"parentVBox\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert contentGridPane != null : "fx:id=\"contentGridPane\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createdLabel != null : "fx:id=\"createdLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateLabel != null : "fx:id=\"lastUpdateLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createdValue != null : "fx:id=\"createdValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateValue != null : "fx:id=\"lastUpdateValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert saveChangesButton != null : "fx:id=\"saveChangesButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert waitBorderPane != null : "fx:id=\"waitBorderPane\" was not injected: check your FXML file 'EditItem.fxml'.";

        parentVBox.getChildren().add(0, editorRegion);
        VBox.setVgrow(editorRegion, Priority.ALWAYS);
        VBox.setMargin(editorRegion, new Insets(8.0));
        saveChangesButton.disableProperty().bind(editorRegion.validProperty().and(editorRegion.modifiedProperty().or(model.newRowProperty())).not());
        if (model.isNewRow()) {
            deleteButton.setDisable(true);
            collapseNode(deleteButton);
            collapseNode(createdLabel);
            collapseNode(createdValue);
            collapseNode(lastUpdateLabel);
            collapseNode(lastUpdateValue);
        } else {
            onEditMode();
        }
    }

    private void onEditMode() {
        restoreNode(deleteButton);
        cancelButton.textProperty().bind(Bindings.when(editorRegion.modifiedProperty())
                .then(resources.getString(RESOURCEKEY_CANCEL))
                .otherwise(resources.getString(RESOURCEKEY_CLOSE))
        );
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

    /**
     * Base class for editing specific {@link EntityModel} items. Derived controls are intended to be instantiated through the
     * {@link EditItem#showAndWait(Window, Class, EntityModelImpl, boolean)} method. This control will be inserted as the first child node of the parent {@code EditItem} control.
     *
     * @param <T> The type of {@link DataAccessObject} object that corresponds to the current {@link EntityModel}.
     * @param <U> The {@link EntityModel} type.
     * @param <E> The {@link ModelEvent} type.
     */
    public interface ModelEditorController<T extends DataAccessObject, U extends EntityModel<T>, E extends ModelEvent<T, U>> {

        /**
         * Gets the factory object for managing the current {@link EntityModel}.
         *
         * @return The factory object for managing the current {@link EntityModel}.
         */
        EntityModel.EntityModelFactory<T, U, E, ? extends E> modelFactory();

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
         */
        void applyChanges();
    }

}
