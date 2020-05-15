package scheduler.view;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
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
import scheduler.fx.ErrorDetailControl;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.AlertHelper;
import scheduler.util.AnnotationHelper;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.StageManager;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.EditItemResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.task.WaitBorderPane;

/**
 * Wrapper FXML Controller class for editing {@link FxRecordModel} items in a new modal window.
 * <p>
 * This controller manages the {@link #saveChangesButton}, {@link #deleteButton}, and cancel button controls as well as labels for displaying the
 * values for the {@link FxRecordModel#createdBy}, {@link FxRecordModel#createDate}, {@link FxRecordModel#lastModifiedBy} and
 * {@link FxRecordModel#lastModifiedDate} properties. Properties that are specific to the {@link FxRecordModel} type are edited in a nested view and
 * controller. Controllers for the nested editor views inherit from {@link EditItem.EditController}.</p>
 * <p>
 * The nested editor view can load the {@code EditItem} view and controller, including the nested view and controller using
 * {@link EditItem.EditController#edit(ItemModel, Class, MainController, Stage)} or
 * {@link EditItem.EditController#editNew(Class, MainController, Stage)}.</p>
 * <p>
 * The view for this controller is {@code /resources/scheduler/view/user/EditUser.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of data access object that the model represents.
 * @param <U> The type of model being edited.
 * @param <S> The content node.
 */
@GlobalizationResource("scheduler/view/EditItem")
@FXMLResource("/scheduler/view/EditItem.fxml")
public final class EditItem<T extends DataAccessObject, U extends FxRecordModel<T>, S extends Region & EditItem.ModelEditor<T, U>> extends StackPane {

    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    public static <T extends DataAccessObject, U extends FxRecordModel<T>, S extends Region & EditItem.ModelEditor<T, U>>
            U showAndWait(Window parentWindow, Class<? extends S> editorType, U model, boolean keepOpen) throws IOException {
        S editorRegion;
        try {
            editorRegion = editorType.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IOException("Error creating editor region", ex);
        }
        EditItem<T, U, S> result = new EditItem<>(editorRegion, model, keepOpen);
        StageManager.showAndWait(result, parentWindow, (t) -> {
            ViewControllerLoader.initializeCustomControl(result);
            try {
                AnnotationHelper.injectModelEditorField(model, "model", editorRegion);
                AnnotationHelper.injectModelEditorField(result.waitBorderPane, "waitBorderPane", editorRegion);
            } catch (IllegalAccessException ex) {
                throw new IOException("Error injecting fields", ex);
            }
            t.titleProperty().bind(editorRegion.windowTitleProperty());
            ViewControllerLoader.initializeCustomControl(editorRegion);
        });
        return (result.model.isNewItem() || result.editorRegion.isChanged()) ? null : result.model;
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

    @FXML // fx:id="waitBorderPane"
    private WaitBorderPane waitBorderPane; // Value injected by FXMLLoader

    private EditItem(S editorRegion, U model, boolean keepOpen) {
        this.editorRegion = editorRegion;
        this.model = model;
        this.keepOpen = keepOpen;
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

        parentVBox.getChildren().add(0, editorRegion);
        VBox.setVgrow(editorRegion, Priority.ALWAYS);
        VBox.setMargin(editorRegion, new Insets(8.0));
        saveChangesButton.disableProperty().bind(editorRegion.validProperty().not());

        if (model.isNewItem()) {
            deleteButton.setDisable(true);
            collapseNode(deleteButton);
            collapseNode(createdLabel);
            collapseNode(createdValue);
            collapseNode(lastUpdateLabel);
            collapseNode(lastUpdateValue);
            editorRegion.onEditNew();
        } else {
            onEditExisting(true);
        }
    }

    private void onEditExisting(boolean isInitialize) {
        restoreNode(deleteButton);
        deleteButton.setDisable(false);
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        Locale.getDefault(Locale.Category.DISPLAY);
        restoreNode(createdLabel);
        restoreLabeled(createdValue, String.format(resources.getString(RESOURCEKEY_ONBYNAME),
                dtf.format(model.getCreateDate()), model.getCreatedBy()));
        restoreNode(lastUpdateLabel);
        restoreLabeled(lastUpdateValue, String.format(resources.getString(RESOURCEKEY_ONBYNAME),
                dtf.format(model.getLastModifiedDate()), model.getLastModifiedBy()));
        editorRegion.onEditExisting(isInitialize);
    }

    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        editorRegion.applyChangesToModel();
        waitBorderPane.startNow(new SaveTask(editorRegion.modelFactory().updateDAO(model)));
    }

    @FXML
    private void onDeleteButtonAction(ActionEvent event) {
        Stage stage = (Stage) getScene().getWindow();
        Optional<ButtonType> response = AlertHelper.showWarningAlert(stage, LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            waitBorderPane.startNow(new DeleteTask(stage));
        }
    }

    @FXML
    private void onCancelButtonAction(ActionEvent event) {
        getScene().getWindow().hide();
    }

    public interface ModelEditor<T extends DataAccessObject, U extends FxRecordModel<T>> {

        FxRecordModel.ModelFactory<T, U> modelFactory();

        boolean isValid();

        ReadOnlyBooleanProperty validProperty();

        boolean isChanged();

        ReadOnlyBooleanProperty changedProperty();

        String getWindowTitle();

        ReadOnlyStringProperty windowTitleProperty();

        boolean applyChangesToModel();

        void onEditNew();

        void onEditExisting(boolean isInitialize);

    }

    private class SaveTask extends Task<String> {

        private final T dataAccessobject;
        private final DaoFactory<T> daoFactory;
        private final boolean closeOnSuccess;

        SaveTask(T dataAccessobject) {
            closeOnSuccess = dataAccessobject.isExisting() || !keepOpen;
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_SAVINGCHANGES));
            this.dataAccessobject = dataAccessobject;
            daoFactory = editorRegion.modelFactory().getDaoFactory();
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_SAVEFAILURE), getException(),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_ERRORSAVINGCHANGES));
            super.failed();
        }

        @Override
        protected void succeeded() {
            String message = getValue();
            if (null == message) {
                editorRegion.modelFactory().updateItem(model, dataAccessobject);
                if (closeOnSuccess) {
                    getScene().getWindow().hide();
                } else {
                    onEditExisting(false);
                }
            } else {
                AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                        AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_SAVEFAILURE), message);
            }
            super.succeeded();
        }

        @Override
        protected String call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));

            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES));
                String message = daoFactory.getSaveDbConflictMessage(dataAccessobject, dbConnector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }

                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                daoFactory.save(dataAccessobject, dbConnector.getConnection());
            }

            return null;
        }
    }

    private class DeleteTask extends Task<String> {

        private final T dataAccessobject;
        private final DaoFactory<T> daoFactory;

        DeleteTask(Stage stage) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            daoFactory = editorRegion.modelFactory().getDaoFactory();
            dataAccessobject = model.getDataObject();
        }

        @Override
        protected void succeeded() {
            String message = getValue();
            if (null == message) {
                editorRegion.modelFactory().updateItem(model, dataAccessobject);
                getScene().getWindow().hide();
            } else {
                AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                        AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETEFAILURE), message);
            }
            super.succeeded();
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETEFAILURE), getException(),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_ERRORDELETINGFROMDB));
            super.failed();
        }

        @Override
        protected String call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));

            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES));
                String message = daoFactory.getDeleteDependencyMessage(dataAccessobject, dbConnector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }

                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                daoFactory.delete(dataAccessobject, dbConnector.getConnection());
            }

            return null;
        }
    }

}
