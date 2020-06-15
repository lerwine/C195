package scheduler.view;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.AlertHelper;
import scheduler.util.AnnotationHelper;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ParentWindowChangeListener;
import scheduler.util.StageManager;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.EditItemResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.event.ActivityType;
import scheduler.view.event.ModelItemEvent;
import scheduler.view.task.WaitBorderPane;

/**
 * The parent FXML custom control for editing {@link FxRecordModel} items in a new modal window.
 * <p>
 * This controller manages the {@link #saveChangesButton}, {@link #deleteButton}, and cancel button controls as well as labels for displaying the
 * values for the {@link FxRecordModel#getCreatedBy()}, {@link FxRecordModel#getCreateDate()}, {@link FxRecordModel#getLastModifiedBy()} and
 * {@link FxRecordModel#getLastModifiedDate()} properties. Properties that are specific to the {@link FxRecordModel} type are edited in a child
 * {@link EditItem.ModelEditor} custom control.</p>
 * <p>
 * The child editor is intended to be instantiated through the {@link EditItem#showAndWait(Window, Class, FxRecordModel, boolean)} method.</p>
 * <p>
 * The view for this controller is {@code /resources/scheduler/view/EditItem.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of data access object that the model represents.
 * @param <U> The type of model being edited.
 * @param <S> The content node.
 */
@GlobalizationResource("scheduler/view/EditItem")
@FXMLResource("/scheduler/view/EditItem.fxml")
public final class EditItem<T extends DataAccessObject, U extends FxRecordModel<T>, S extends Region & EditItem.ModelEditor<T, U, E>, E extends ModelItemEvent<U, T>> extends StackPane {

    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    /**
     * Opens a new window for editing an {@link FxRecordModel} item.
     *
     * @param <T> The type of data access object.
     * @param <U> The type of {@link FxRecordModel} that corresponds to the data access object.
     * @param <S> The type of {@link ModelEditor} control for editing the model properties.
     * @param parentWindow The parent window
     * @param editorRegion
     * @param model
     * @param keepOpen
     * @return
     * @throws IOException
     */
    public static <T extends DataAccessObject, U extends FxRecordModel<T>, S extends Region & EditItem.ModelEditor<T, U, E>, E extends ModelItemEvent<U, T>>
            U showAndWait(Window parentWindow, S editorRegion, U model, boolean keepOpen) throws IOException {
        EditItem<T, U, S, E> result = new EditItem<>(editorRegion, model, keepOpen);
        ViewControllerLoader.initializeCustomControl(result);
        try {
            AnnotationHelper.injectModelEditorField(model, "model", editorRegion);
            AnnotationHelper.injectModelEditorField(result.waitBorderPane, "waitBorderPane", editorRegion);
        } catch (IllegalAccessException ex) {
            throw new IOException("Error injecting fields", ex);
        }
        ViewControllerLoader.initializeCustomControl(editorRegion);
        ParentWindowChangeListener.setWindowChangeListener(result, result::onWindowChanged);
        StageManager.showAndWait(result, parentWindow);
        return (result.model.isNewRow()) ? null : result.model;
    }

    /**
     * Opens a new window for editing an {@link FxRecordModel} item.
     *
     * @param <T> The type of data access object.
     * @param <U> The type of {@link FxRecordModel} that corresponds to the data access object.
     * @param <S> The type of {@link ModelEditor} control for editing the model properties.
     * @param parentWindow The parent window
     * @param editorType
     * @param model
     * @param keepOpen
     * @return
     * @throws IOException
     */
    public static <T extends DataAccessObject, U extends FxRecordModel<T>, S extends Region & EditItem.ModelEditor<T, U, E>, E extends ModelItemEvent<U, T>>
            U showAndWait(Window parentWindow, Class<? extends S> editorType, U model, boolean keepOpen) throws IOException {
        S editorRegion;
        try {
            editorRegion = editorType.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException("Error creating editor region", ex);
        }
        return showAndWait(parentWindow, editorRegion, model, keepOpen);
    }

    private final S editorRegion;
    private final U model;
    private final boolean keepOpen;

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

    private EditItem(S editorRegion, U model, boolean keepOpen) {
        this.editorRegion = editorRegion;
        this.model = model;
        this.keepOpen = keepOpen;
    }

    @FXML
    void onCancelButtonAction(ActionEvent event) {
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
    void onDeleteButtonAction(ActionEvent event) {
        FxRecordModel.ModelFactory<T, U, E> factory = editorRegion.modelFactory();
        E deleteEvent = factory.createModelItemEvent(model, event.getSource(), editorRegion, ActivityType.DELETING);
        editorRegion.fireEvent(deleteEvent);
        if (!deleteEvent.isConsumed()) {
            Stage stage = (Stage) getScene().getWindow();
            AlertHelper.showWarningAlert(stage, LOG,
                    resources.getString(RESOURCEKEY_CONFIRMDELETE),
                    resources.getString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO)
                    .ifPresent((t) -> {
                        if (t == ButtonType.YES) {
                            waitBorderPane.startNow(new DeleteTask(deleteEvent));
                        }
                    });
        }
    }

    @FXML
    void onSaveButtonAction(ActionEvent event) {
        FxRecordModel.ModelFactory<T, U, E> factory = editorRegion.modelFactory();
        // FIXME: Need to find a good way to ensure the model is updated after DAO is updated.. Perhaps passing event instead of DAO
        E updateEvent = (model.isNewRow())
                ? factory.createModelItemEvent(model, event.getSource(), editorRegion, ActivityType.INSERTING)
                : factory.createModelItemEvent(model, event.getSource(), editorRegion, ActivityType.UPDATING);
        editorRegion.fireEvent(updateEvent);
        if (!updateEvent.isConsumed()) {
            editorRegion.updateModel();
            waitBorderPane.startNow(new SaveTask(updateEvent));
        }
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

    private void onWindowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        if (null != oldValue && oldValue instanceof Stage) {
            ((Stage) oldValue).titleProperty().unbind();
        }
        if (null != newValue && newValue instanceof Stage) {
            ((Stage) newValue).titleProperty().bind(editorRegion.windowTitleProperty());
        }
    }

    /**
     * Base class for editing specific {@link FxRecordModel} items. Derived controls are intended to be instantiated through the
     * {@link EditItem#showAndWait(Window, Class, FxRecordModel, boolean)} method. This control will be inserted as the first child node of the parent
     * {@code EditItem} control.
     *
     * @param <T> The type of {@link DataAccessObject} object that corresponds to the current {@link FxRecordModel}.
     * @param <U> The {@link FxRecordModel} type.
     */
    public interface ModelEditor<T extends DataAccessObject, U extends FxRecordModel<T>, E extends ModelItemEvent<U, T>> {

        /**
         * Gets the factory object for managing the current {@link FxRecordModel}.
         *
         * @return The factory object for managing the current {@link FxRecordModel}.
         */
        FxRecordModel.ModelFactory<T, U, E> modelFactory();

        /**
         * Gets the window title for the current parent {@link Stage}.
         *
         * @return The window title for the current parent {@link Stage}.
         */
        String getWindowTitle();

        /**
         * Gets the property that specifies the window title for the current parent {@link Stage}. This is bound to the {@link Stage#titleProperty()}
         * of the parent {@link Stage}.
         *
         * @return The property that specifies the window title for the current parent {@link Stage}. This is bound to the
         * {@link Stage#titleProperty()} of the parent {@link Stage}.
         */
        ReadOnlyStringProperty windowTitleProperty();

        boolean isValid();

        /**
         * The inverse value of this property is bound to the {@link Button#disableProperty()} of the {@link EditItem#saveChangesButton}.
         *
         * @return A {@link ReadOnlyBooleanProperty} that contains a {@code false} value if the {@link EditItem#saveChangesButton} is to be disabled,
         * otherwise {@code true} if it is to be enabled.
         */
        ReadOnlyBooleanProperty validProperty();

        boolean isModified();

        ReadOnlyBooleanProperty modifiedProperty();

        /**
         * This gets called to re-initialize the controller for edit mode after a new model has been inserted into the database
         * 
         */
        void onNewModelSaved();

        void updateModel();

    }

    private class SaveTask extends Task<Void> {

        private final E event;
        private final DaoFactory<T, E> daoFactory;
        private final boolean closeOnSuccess;

        SaveTask(E event) {
            this.event = event;
            closeOnSuccess = event.getActivity() != ActivityType.INSERTING || !keepOpen;
            updateTitle(resources.getString(RESOURCEKEY_SAVINGCHANGES));
            daoFactory = editorRegion.modelFactory().getDaoFactory();
        }

        @Override
        protected void succeeded() {
            switch (event.getStatus()) {
                case CANCELED:
                case EVALUATING:
                    break;
                case SUCCEEDED:
                    if (closeOnSuccess) {
                        getScene().getWindow().hide();
                    }
                    break;
                default:
                    AlertHelper.showWarningAlert(getScene().getWindow(), LOG, event.getSummaryTitle(), event.getDetailMessage());
                    break;
            }
            super.succeeded();
        }

        @Override
        protected void failed() {
            event.setFaulted("Operation failed", "Delete operation encountered an unexpected error");
            super.failed();
        }

        @Override
        protected void cancelled() {
            event.setCanceled();
            super.cancelled();
        }

        @Override
        protected Void call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));

            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                daoFactory.save(event, dbConnector.getConnection());
            }

            return null;
        }
    }

    private class DeleteTask extends Task<Void> {

        private final E event;
        private final DaoFactory<T, E> daoFactory;

        DeleteTask(E event) {
            this.event = event;
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_SAVINGCHANGES));
            daoFactory = editorRegion.modelFactory().getDaoFactory();
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
        }

        @Override
        protected void succeeded() {
            switch (event.getStatus()) {
                case CANCELED:
                case EVALUATING:
                    break;
                case SUCCEEDED:
                    getScene().getWindow().hide();
                    break;
                default:
                    AlertHelper.showWarningAlert(getScene().getWindow(), LOG, event.getSummaryTitle(), event.getDetailMessage());
                    break;
            }
            super.succeeded();
        }

        @Override
        protected Void call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));

            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                daoFactory.delete(event, dbConnector.getConnection());
            }

            return null;
        }
    }

}
