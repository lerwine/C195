package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.util.AlertHelper;
import scheduler.util.EventHelper;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEventType;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;

/**
 * Wrapper FXML Controller class for editing {@link ItemModel} items in a new modal window.
 * <p>This controller manages the {@link #saveChangesButton}, {@link #deleteButton}, {@link #cancelButton} controls as well as labels for displaying the values for
 * the {@link ItemModel#createdBy}, {@link ItemModel#createDate}, {@link ItemModel#lastModifiedBy} and {@link ItemModel#lastModifiedDate} properties. Properties that are
 * specific to the {@link ItemModel} type are edited in a nested view and controller. Controllers for the nested editor views inherit from {@link EditItem.EditController}.</p>
 * <p>The nested editor view can load the {@code EditItem} view and controller, including the nested view and controller using
 * {@link EditItem.EditController#edit(ItemModel, Class, MainController, Stage)} or {@link EditItem.EditController#editNew(Class, MainController, Stage)}.</p>
 * <p>The view for this controller is {@code /resources/scheduler/view/user/EditUser.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of data access object that the model represents.
 * @param <U> The type of model being edited.
 */
@GlobalizationResource("scheduler/view/EditItem")
@FXMLResource("/scheduler/view/EditItem.fxml")
public final class EditItem<T extends DataAccessObject, U extends ItemModel<T>> extends SchedulerController {

    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    private EditController<T, U> contentController;

    @FXML // fx:id="parentVBox"
    private VBox parentVBox; // Value injected by FXMLLoader

    @FXML // fx:id="contentPane"
    private StackPane contentPane; // Value injected by FXMLLoader

    @FXML // fx:id="createdLabel"
    private Label createdLabel; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateLabel"
    private Label lastUpdateLabel; // Value injected by FXMLLoader

    @FXML // fx:id="createDateValue"
    private Label createDateValue; // Value injected by FXMLLoader

    @FXML // fx:id="createdByLabel"
    private Label createdByLabel; // Value injected by FXMLLoader

    @FXML // fx:id="createdByValue"
    private Label createdByValue; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateValue"
    private Label lastUpdateValue; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateByLabel"
    private Label lastUpdateByLabel; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateByValue"
    private Label lastUpdateByValue; // Value injected by FXMLLoader

    @FXML // fx:id="saveChangesButton"
    private Button saveChangesButton; // Value injected by FXMLLoader

    @FXML // fx:id="deleteButton"
    private Button deleteButton; // Value injected by FXMLLoader

    private void onContentLoaded(ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController) {
        contentController = viewAndController.getController();
        contentController.model = contentController.getFactory().createNew(
                contentController.getFactory().getDaoFactory().createNew());
    }

    private void onContentLoaded(U model, ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController) {
        contentController = viewAndController.getController();
        contentController.model = model;
    }

    private void onInitForNew(Parent view) {
        collapseNode(deleteButton);
        collapseNode(createdLabel);
        collapseNode(createDateValue);
        collapseNode(createdByLabel);
        collapseNode(createdByValue);
        collapseNode(lastUpdateByLabel);
        collapseNode(lastUpdateByValue);
        collapseNode(lastUpdateLabel);
        collapseNode(lastUpdateValue);
        contentPane.getChildren().add(view);
        saveChangesButton.disableProperty().bind(contentController.getValidationExpression().not());
    }

    private void onInitForEdit(Parent view, EditController<T, U> controller) {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        Locale.getDefault(Locale.Category.DISPLAY);
        createdByValue.setText(controller.model.getCreatedBy());
        createDateValue.setText(dtf.format(controller.model.getCreateDate()));
        lastUpdateByValue.setText(controller.model.getLastModifiedBy());
        lastUpdateValue.setText(dtf.format(controller.model.getLastModifiedDate()));
        contentPane.getChildren().add(view);
        saveChangesButton.disableProperty().bind(contentController.getValidationExpression().not());
    }

    @HandlesDataObjectEvent
    protected void onDataObjectEvent(DataObjectEvent<? extends DataAccessObject> event) {
        EventHelper.fireDataObjectEvent(contentController, event);
    }

    @SuppressWarnings("unused")
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert parentVBox != null : "fx:id=\"parentVBox\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createdLabel != null : "fx:id=\"createdLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateLabel != null : "fx:id=\"lastUpdateLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createDateValue != null : "fx:id=\"createDateValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createdByLabel != null : "fx:id=\"createdByLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert createdByValue != null : "fx:id=\"createdByValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateValue != null : "fx:id=\"lastUpdateValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateByLabel != null : "fx:id=\"lastUpdateByLabel\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert lastUpdateByValue != null : "fx:id=\"lastUpdateByValue\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert saveChangesButton != null : "fx:id=\"saveChangesButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'EditItem.fxml'.";
    }
    
    @SuppressWarnings("unused")
    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        contentController.onSaving(stage, contentController.model);
        ItemModel.ModelFactory<T, U> factory = contentController.getFactory();
        T dao = factory.updateDAO(contentController.model);
        TaskWaiter.startNow(new SaveTask(dao, stage));
    }

    @SuppressWarnings("unused")
    @FXML
    private void onDeleteButtonAction(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        if (contentController.onDeleting(stage, contentController.model)) {
            TaskWaiter.startNow(new DeleteTask(stage));
        }
    }

    @SuppressWarnings("unused")
    @FXML
    private void onCancelButtonAction(ActionEvent event) {
        contentController.cancelEdit((Stage)((Button)event.getSource()).getScene().getWindow());
    }

    /**
     * Base class for item edit content controllers that are nested within an {@code EditItem} view and controller.
     * <p>The parent {@code EditItem} view and controller and the nested view and controller are loaded and instantiated using
     * {@link EditItem.EditController#edit(ItemModel, Class, MainController, Stage)} or {@link EditItem.EditController#editNew(Class, MainController, Stage)}.</p>
     * @param <T> The type of data access object that the model represents.
     * @param <U> The type of model being edited.
     */
    public static abstract class EditController<T extends DataAccessObject, U extends ItemModel<T>> extends SchedulerController {

        /**
         * This gets called when an {@link ItemModel} is about to be saved to the database.
         * 
         * @param stage The {@link Stage} of the controller that initiated the save operation.
         * @param model The {@link ItemModel} for the object to be saved.
         */
        protected void onSaving(Stage stage, U model) {
            updateModel(model);
        }

        /**
         * This gets called when an {@link ItemModel} is about to be deleted.
         * 
         * @param stage The {@link Stage} of the controller that initiated the delete operation.
         * @param model The {@link ItemModel} for the object to be deleted.
         * @return {@code true} if the item can be deleted; otherwise, {@code false} to cancel the delete operation.
         */
        protected boolean onDeleting(Stage stage, U model) {
            Optional<ButtonType> response = AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            return response.isPresent() && response.get() == ButtonType.YES;
        }

        /**
         * This gets called when changes to an {@link ItemModel} are discarded and the window is closed.
         * 
         * @param stage The {@link Stage} of the controller that initiated the cancel operation.
         */
        protected void cancelEdit(Stage stage) {
            model = null;
            stage.hide();
        }

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add"}.
         */
        public static final String RESOURCEKEY_ADD = "add";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "* Required"}.
         */
        public static final String RESOURCEKEY_REQUIRED = "required";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created"}.
         */
        public static final String RESOURCEKEY_CREATED = "created";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "by"}.
         */
        public static final String RESOURCEKEY_BY = "by";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated"}.
         */
        public static final String RESOURCEKEY_UPDATED = "updated";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Save"}.
         */
        public static final String RESOURCEKEY_SAVE = "save";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Cancel"}.
         */
        public static final String RESOURCEKEY_CANCEL = "cancel";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Would you like to save, anyway?"}.
         */
        public static final String RESOURCEKEY_SAVEANYWAY = "saveAnyway";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete"}.
         */
        public static final String RESOURCEKEY_DELETE = "delete";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Confirm Delete"}.
         */
        public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This action cannot be undone!..."}.
         */
        public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Validation Error"}.
         */
        public static final String RESOURCEKEY_VALIDATIONERROR = "validationError";

        /**
         * Load an {@code EditItem} view and controller and the nested view and controller for editing a new {@link ItemModel}.
         * 
         * @param <T> The type of {@link DataAccessObject} supported by the {@link ItemModel}.
         * @param <U> The {@link ItemModel} type.
         * @param controllerClass The class of the controller for the nested edit controls.
         * @param mainController The {@link MainController} instance that will contain the {@code EditItem} view and controller.
         * @param stage The {@link Stage} of the initiating event for this action.
         * @return The newly saved {@link ItemModel} or {@code null} if the edit was canceled.
         * @throws IOException if unable to load any of the FXML resources.
         */
        @SuppressWarnings("unchecked")
        protected static <T extends DataAccessObject, U extends ItemModel<T>> U editNew(Class<? extends EditController<T, U>> controllerClass,
                MainController mainController, Stage stage) throws IOException {
            EditItem<T, U> fc = ViewControllerLoader.showAndWait(new FxmlViewControllerEventListener<Parent, EditItem<T, U>>() {

                private ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController;

                @Override
                public void onFxmlViewControllerEvent(FxmlViewControllerEvent<Parent, EditItem<T, U>> event) {
                    LOG.log(Level.INFO, String.format("Handling FxmlViewControllerEvent %s for %s", event.getType(),
                            event.getController().getClass().getName()));
                    switch (event.getType()) {
                        case LOADED:
                            try {
                                viewAndController = ViewControllerLoader.loadViewAndController(controllerClass);
                                event.getController().onContentLoaded(viewAndController);
                                EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                        viewAndController.toEvent(this, FxmlViewEventType.LOADED, stage));
                            } catch (IOException ex) {
                                // PENDING: Internationalize message
                                AlertHelper.showErrorAlert(event.getStage(), LOG, "Error loading edit window content", ex);
                            }
                            break;
                        case BEFORE_SHOW:
                            event.getController().onInitForNew(viewAndController.getView());
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.BEFORE_SHOW, event.getStage()));
                            break;
                        case SHOWN:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.SHOWN, event.getStage()));
                            break;
                        case UNLOADED:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.UNLOADED, event.getStage()));
                            break;
                    }
                }

            }, stage, EditItem.class);

            U model = fc.contentController.model;
            T dataAccessObject = (null == model) ? null : model.getDataObject();
            if (null != dataAccessObject) {
                EventHelper.fireDataObjectEvent(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.CREATED,
                        dataAccessObject));
            }
            return model;
        }

        /**
         * Load an {@code EditItem} view and controller and the nested view and controller for editing an existing {@link ItemModel}.
         * 
         * @param <T> The type of {@link DataAccessObject} supported by the {@link ItemModel}.
         * @param <U> The {@link ItemModel} type.
         * @param model The {@link ItemModel} to be edited.
         * @param controllerClass The class of the controller for the nested edit controls.
         * @param mainController The {@link MainController} instance that will contain the {@code EditItem} view and controller.
         * @param stage The {@link Stage} of the initiating event for this action.
         * @return The edited or deleted {@link ItemModel} or {@code null} if the edit was canceled.
         * @throws IOException if unable to load any of the FXML resources.
         */
        @SuppressWarnings("unchecked")
        protected static <T extends DataAccessObject, U extends ItemModel<T>> U edit(U model, Class<? extends EditController<T, U>> controllerClass,
                MainController mainController, Stage stage) throws IOException {
            EditItem<T, U> fc = ViewControllerLoader.showAndWait(new FxmlViewControllerEventListener<Parent, EditItem<T, U>>() {

                private ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController;

                @Override
                public void onFxmlViewControllerEvent(FxmlViewControllerEvent<Parent, EditItem<T, U>> event) {
                    switch (event.getType()) {
                        case LOADED:
                            try {
                                viewAndController = ViewControllerLoader.loadViewAndController(controllerClass);
                                event.getController().onContentLoaded(model, viewAndController);
                                EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                        viewAndController.toEvent(this, FxmlViewEventType.LOADED, stage));
                            } catch (IOException ex) {
                                AlertHelper.showErrorAlert(event.getStage(), LOG, "Error loading edit window content", ex);
                            }
                            break;
                        case BEFORE_SHOW:
                            event.getController().onInitForEdit(viewAndController.getView(), viewAndController.getController());
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.BEFORE_SHOW, event.getStage()));
                            break;
                        case SHOWN:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.SHOWN, event.getStage()));
                            break;
                        case UNLOADED:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.UNLOADED, event.getStage()));
                            break;
                    }
                }

            }, stage, EditItem.class);

            U r = fc.contentController.model;
            T dataAccessObject = (null == r) ? null : r.getDataObject();
            if (null != dataAccessObject) {
                if (dataAccessObject.getRowState() == DataRowState.DELETED) {
                    EventHelper.fireDataObjectEvent(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.DELETED,
                            dataAccessObject));
                } else {
                    EventHelper.fireDataObjectEvent(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.UPDATED,
                            dataAccessObject));
                }
            }
            return r;
        }

        private U model;

        /**
         * Gets the current {@link ItemModel} being edited.
         *
         * @return the current {@link ItemModel} being edited.
         */
        protected U getModel() {
            return model;
        }

        protected abstract ItemModel.ModelFactory<T, U> getFactory();

        /**
         * Gets the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         *
         * @return the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         */
        protected abstract BooleanExpression getValidationExpression();

        /**
         * Applies changes contained within the controller to the {@link ItemModel} being edited.
         * 
         * @param model The {@link ItemModel} being edited.
         */
        protected abstract void updateModel(U model);
        
        protected String getSaveDbConflictMessage(T dao, Connection connection) throws SQLException {
            return getFactory().getDaoFactory().getSaveDbConflictMessage(dao, connection);
        }
        
        /**
         * This gets called to save changes to a data access object.
         * 
         * @param dao The data access object to be saved.
         * @param connection The database connection to use.
         * @throws SQLException if unable to save changes.
         */
        protected void save(T dao, Connection connection) throws SQLException {
            getFactory().getDaoFactory().save(dao, connection);
        }

        /**
         * This gets called to update the model after the data access object changes have been saved.
         * 
         * @param model The model to be refreshed.
         * @param dao The data access object that was saved.
         * @param stage The stage of the controller that initiated the save operation.
         */
        protected void updateItem(U model, T dao, Stage stage) {
            getFactory().updateItem(model, dao);
        }

    }

    private class SaveTask extends TaskWaiter<String> {

        private final T dataAccessobject;

        SaveTask(T dataAccessobject, Stage stage) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVINGCHANGES));
            this.dataAccessobject = dataAccessobject;
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null == message || message.trim().isEmpty()) {
                contentController.updateItem(contentController.model, dataAccessobject, owner);
                owner.hide();
            } else {
                AlertHelper.showWarningAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            AlertHelper.showErrorAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVEFAILURE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORSAVINGCHANGES), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = contentController.getSaveDbConflictMessage(dataAccessobject, connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            
            contentController.save(dataAccessobject, connection);

            return "";
        }
    }

    private class DeleteTask extends TaskWaiter<String> {

        private final T dataAccessobject;
        private final DaoFactory<T> daoFactory;

        DeleteTask(Stage stage) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETINGRECORD));
            daoFactory = contentController.getFactory().getDaoFactory();
            dataAccessobject = contentController.model.getDataObject();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null == message || message.trim().isEmpty()) {
                owner.hide();
            } else {
                AlertHelper.showWarningAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            AlertHelper.showErrorAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORDELETINGFROMDB), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = daoFactory.getDeleteDependencyMessage(dataAccessobject, connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            daoFactory.delete(dataAccessobject, connection);
            return null;
        }
    }

}
