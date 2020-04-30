package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.*;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
import scheduler.util.AlertHelper;
import scheduler.util.EventHelper;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.address.AddressModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEventType;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.task.TaskWaiter;

/**
 * Wrapper FXML Controller class for editing {@link FxRecordModel} items in a new modal window.
 * <p>
 * This controller manages the {@link #saveChangesButton}, {@link #deleteButton}, and cancel button controls as well as labels for displaying the
 * values for the {@link FxRecordModel#createdBy}, {@link FxRecordModel#createDate}, {@link FxRecordModel#lastModifiedBy} and {@link FxRecordModel#lastModifiedDate}
 * properties. Properties that are specific to the {@link FxRecordModel} type are edited in a nested view and controller. Controllers for the nested
 * editor views inherit from {@link EditItem.EditController}.</p>
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
 */
@GlobalizationResource("scheduler/view/EditItem")
@FXMLResource("/scheduler/view/EditItem.fxml")
public final class EditItem<T extends DataAccessObject, U extends FxRecordModel<T>> implements DataObjectEventListener<T> {

    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    private MainController mainController;

    private EditController<T, U> contentController;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
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

    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        contentController.onSaving(stage, contentController.model);
        FxRecordModel.ModelFactory<T, U> factory = contentController.getFactory();
        T dao = factory.updateDAO(contentController.model);
        TaskWaiter.startNow(new SaveTask(dao, stage));
    }

    @FXML
    private void onDeleteButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        if (contentController.onDeleting(stage, contentController.model)) {
            TaskWaiter.startNow(new DeleteTask(stage));
        }
    }

    @FXML
    private void onCancelButtonAction(ActionEvent event) {
        contentController.cancelEdit((Stage) ((Button) event.getSource()).getScene().getWindow());
    }

    @Override
    public void onDataObjectEvent(DataObjectEvent<T> event) {
        EventHelper.fireDataObjectEvent(contentController, event);
    }
    
    private void onUnload(Parent view) {
        contentPane.getChildren().remove(view);
        if (view instanceof Region) {
            Region r = (Region)view;
            r.prefWidthProperty().unbind();
            r.minWidthProperty().unbind();
            r.prefHeightProperty().unbind();
            r.minHeightProperty().unbind();
        }
    }
    
    private void onBeforeShow(boolean initForNew, Parent view, Stage stage) {
        contentPane.getChildren().add(view);
        if (view instanceof Region) {
            Region r = (Region)view;
            r.prefWidthProperty().bind(contentPane.widthProperty());
            r.minWidthProperty().bind(contentPane.widthProperty());
            r.prefHeightProperty().bind(contentPane.heightProperty());
            r.minHeightProperty().bind(contentPane.heightProperty());
        }
        saveChangesButton.disableProperty().bind(contentController.getValidationExpression().not());
        if (initForNew) {
            collapseNode(deleteButton);
            collapseNode(createdLabel);
            collapseNode(createDateValue);
            collapseNode(createdByLabel);
            collapseNode(createdByValue);
            collapseNode(lastUpdateByLabel);
            collapseNode(lastUpdateByValue);
            collapseNode(lastUpdateLabel);
            collapseNode(lastUpdateValue);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
            Locale.getDefault(Locale.Category.DISPLAY);
            createdByValue.setText(contentController.model.getCreatedBy());
            createDateValue.setText(dtf.format(contentController.model.getCreateDate()));
            lastUpdateByValue.setText(contentController.model.getLastModifiedBy());
            lastUpdateValue.setText(dtf.format(contentController.model.getLastModifiedDate()));
        }
    }

    private ViewAndController<? extends Parent, ? extends EditController<T, U>> loadChildViewAndController(MainController mainController,
            Class<? extends EditController<T, U>> controllerClass, U model, Stage stage) {
        this.mainController = mainController;
        mainController.addDaoEventListener(this);
        ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController = null;
        try {
            viewAndController = ViewControllerLoader.loadViewAndController(controllerClass);
            contentController = viewAndController.getController();
            contentController.initializeModel(this, model);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_ERRORLOADINGEDITWINDOWCONTENT), stage, ex);
        }
        return viewAndController;
    }

    private static class ViewControllerLoadListener<T extends DataAccessObject, U extends FxRecordModel<T>>
            implements FxmlViewControllerEventListener<Parent, EditItem<T, U>> {

        private final MainController mainController;
        private ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController;
        private final Class<? extends EditController<T, U>> controllerClass;
        private final U model;

        private ViewControllerLoadListener(MainController mainController, U model, Class<? extends EditController<T, U>> controllerClass) {
            this.mainController = mainController;
            this.controllerClass = controllerClass;
            this.model = model;
        }

        @Override
        public void onFxmlViewControllerEvent(FxmlViewControllerEvent<Parent, EditItem<T, U>> event) {
            EditItem<T, U> currentController = event.getController();
            LOG.log(Level.INFO, String.format("Handling FxmlViewControllerEvent %s for %s", event.getType(),
                    currentController.getClass().getName()));
            FxmlViewEventType type = event.getType();
            switch (type) {
                case LOADED:
                    viewAndController = currentController.loadChildViewAndController(mainController, controllerClass, model, event.getStage());
                    break;
                case BEFORE_SHOW:
                    if (null != viewAndController) {
                        currentController.onBeforeShow(true, viewAndController.getView(), event.getStage());
                    }
                    break;
                case SHOWN:
                    if (null == viewAndController) {
                        event.getStage().close();
                    }
                    break;
                case UNLOADED:
                    if (null != viewAndController) {
                        currentController.onUnload(viewAndController.getView());
                    }
                    break;
            }
            if (null != viewAndController) {
                EventHelper.fireFxmlViewEvent(currentController.contentController,
                        viewAndController.toEvent(currentController, type, event.getStage()));
            }
        }

    }

    /**
     * Base class for item edit content controllers that are nested within an {@code EditItem} view and controller.
     * <p>
     * The parent {@code EditItem} view and controller and the nested view and controller are loaded and instantiated using
     * {@link EditItem.EditController#edit(ItemModel, Class, MainController, Stage)} or
     * {@link EditItem.EditController#editNew(Class, MainController, Stage)}.</p>
     *
     * @param <T> The type of data access object that the model represents.
     * @param <U> The type of model being edited.
     */
    public static abstract class EditController<T extends DataAccessObject, U extends FxRecordModel<T>> {

//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add"}.
//         */
//        public static final String RESOURCEKEY_ADD = "add";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "* Required"}.
//         */
//        public static final String RESOURCEKEY_REQUIRED = "required";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created"}.
//         */
//        public static final String RESOURCEKEY_CREATED = "created";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "by"}.
//         */
//        public static final String RESOURCEKEY_BY = "by";

//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated"}.
//         */
//        public static final String RESOURCEKEY_UPDATED = "updated";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Save"}.
//         */
//        public static final String RESOURCEKEY_SAVE = "save";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Cancel"}.
//         */
//        public static final String RESOURCEKEY_CANCEL = "cancel";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Would you like to save, anyway?"}.
//         */
//        public static final String RESOURCEKEY_SAVEANYWAY = "saveAnyway";

//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete"}.
//         */
//        public static final String RESOURCEKEY_DELETE = "delete";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Confirm Delete"}.
//         */
//        public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This action cannot be undone!..."}.
//         */
//        public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";
//
//        /**
//         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Validation Error"}.
//         */
//        public static final String RESOURCEKEY_VALIDATIONERROR = "validationError";

        /**
         * Load an {@code EditItem} view and controller and the nested view and controller for editing a new {@link FxRecordModel}.
         *
         * @param <T> The type of {@link DataAccessObject} supported by the {@link FxRecordModel}.
         * @param <U> The {@link FxRecordModel} type.
         * @param controllerClass The class of the controller for the nested edit controls.
         * @param mainController The {@link MainController} instance that will contain the {@code EditItem} view and controller.
         * @param stage The {@link Stage} of the initiating event for this action.
         * @return The newly saved {@link FxRecordModel} or {@code null} if the edit was canceled.
         * @throws IOException if unable to load any of the FXML resources.
         */
        @SuppressWarnings("unchecked")
        protected static <T extends DataAccessObject, U extends FxRecordModel<T>> U editNew(Class<? extends EditController<T, U>> controllerClass,
                MainController mainController, Stage stage) throws IOException {
            EditItem<T, U> fc = ViewControllerLoader.showAndWait(new ViewControllerLoadListener<>(mainController, null, controllerClass),
                    stage, EditItem.class);
            mainController.removeDaoEventListener(fc);
            return ((EditController<T, U>) fc.contentController).model;
        }

        @SuppressWarnings("unchecked")
        protected static <T extends DataAccessObject, U extends FxRecordModel<T>> U editNew(Class<? extends EditController<T, U>> controllerClass,
                MainController mainController, Stage stage, Object loadEventListener) throws IOException {
            EditItem<T, U> fc = ViewControllerLoader.showAndWait(new ViewControllerLoadListener<>(mainController, null, controllerClass),
                    stage, EditItem.class, loadEventListener);
            mainController.removeDaoEventListener(fc);
            return ((EditController<T, U>) fc.contentController).model;
        }

        /**
         * Load an {@code EditItem} view and controller and the nested view and controller for editing an existing {@link FxRecordModel}.
         *
         * @param <T> The type of {@link DataAccessObject} supported by the {@link FxRecordModel}.
         * @param <U> The {@link FxRecordModel} type.
         * @param model The {@link FxRecordModel} to be edited.
         * @param controllerClass The class of the controller for the nested edit controls.
         * @param mainController The {@link MainController} instance that will contain the {@code EditItem} view and controller.
         * @param stage The {@link Stage} of the initiating event for this action.
         * @return The edited or deleted {@link FxRecordModel} or {@code null} if the edit was canceled.
         * @throws IOException if unable to load any of the FXML resources.
         */
        @SuppressWarnings("unchecked")
        protected static <T extends DataAccessObject, U extends FxRecordModel<T>> U edit(U model, Class<? extends EditController<T, U>> controllerClass,
                MainController mainController, Stage stage) throws IOException {
            EditItem<T, U> fc = ViewControllerLoader.showAndWait(new ViewControllerLoadListener<>(mainController, model, controllerClass),
                    stage, EditItem.class);
            mainController.removeDaoEventListener(fc);
            return ((EditController<T, U>) fc.contentController).model;
        }

        private EditItem<T, U> parentController;

        private U model;

        @FXML // ResourceBundle that was given to the FXMLLoader
        private ResourceBundle resources;

//        @FXML // URL location of the FXML file that was given to the FXMLLoader
//        private URL location;
        private void initializeModel(EditItem<T, U> parentController, U model) {
            this.parentController = parentController;
            if (null == model) {
                FxRecordModel.ModelFactory<T, U> factory = getFactory();
                this.model = factory.createNew(factory.getDaoFactory().createNew());
            } else {
                this.model = model;
            }
        }

        protected String getResourceString(String key) {
            return resources.getString(key);
        }

        protected ResourceBundle getResources() {
            return resources;
        }

        /**
         * This gets called when an {@link FxRecordModel} is about to be saved to the database.
         *
         * @param stage The {@link Stage} of the controller that initiated the save operation.
         * @param model The {@link FxRecordModel} for the object to be saved.
         */
        protected void onSaving(Stage stage, U model) {
            updateModel(model);
        }

        /**
         * This gets called when an {@link FxRecordModel} is about to be deleted.
         *
         * @param stage The {@link Stage} of the controller that initiated the delete operation.
         * @param model The {@link FxRecordModel} for the object to be deleted.
         * @return {@code true} if the item can be deleted; otherwise, {@code false} to cancel the delete operation.
         */
        protected boolean onDeleting(Stage stage, U model) {
            Optional<ButtonType> response = AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            return response.isPresent() && response.get() == ButtonType.YES;
        }

        /**
         * This gets called when changes to an {@link FxRecordModel} are discarded and the window is closed.
         *
         * @param stage The {@link Stage} of the controller that initiated the cancel operation.
         */
        protected void cancelEdit(Stage stage) {
            model = null;
            stage.hide();
        }

        protected boolean isNotEditable() {
            return false;
        }

        /**
         * Gets the current {@link FxRecordModel} being edited.
         *
         * @return the current {@link FxRecordModel} being edited.
         */
        protected U getModel() {
            return model;
        }

        protected abstract FxRecordModel.ModelFactory<T, U> getFactory();

        /**
         * Gets the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         *
         * @return the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         */
        protected abstract BooleanExpression getValidationExpression();

        /**
         * Applies changes contained within the controller to the {@link FxRecordModel} being edited.
         *
         * @param model The {@link FxRecordModel} being edited.
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

        public void deleteAddress(Stage stage, AddressModel item) {
            parentController.mainController.deleteAddress(stage, item);
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
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_SAVEFAILURE), owner, ex,
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORSAVINGCHANGES));
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CHECKINGDEPENDENCIES));
            String message = contentController.getSaveDbConflictMessage(dataAccessobject, connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }

            updateMessage(AppResources.getResourceString(RESOURCEKEY_COMPLETINGOPERATION));
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
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DELETEFAILURE), owner, ex,
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORDELETINGFROMDB));
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CHECKINGDEPENDENCIES));
            String message = daoFactory.getDeleteDependencyMessage(dataAccessobject, connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            updateMessage(AppResources.getResourceString(RESOURCEKEY_COMPLETINGOPERATION));
            daoFactory.delete(dataAccessobject, connection);
            return null;
        }
    }

}
