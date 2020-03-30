package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.dao.DaoChangeAction;
import scheduler.dao.DataObjectEvent;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.DataObjectImpl.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.util.AlertHelper;
import scheduler.util.AnnotationHelper;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesDataObjectEvent;

/**
 * Base FXML Controller class for editing {@link ItemModel} items in a new modal window.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of data access object that the model represents.
 * @param <U> The type of model being edited.
 */
@GlobalizationResource("view/EditItem")
@FXMLResource("/view/EditItem.fxml")
public final class EditItem<T extends DataObjectImpl, U extends ItemModel<T>> extends SchedulerController {

    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    protected static <T extends DataObjectImpl, U extends ItemModel<T>> void editNew(Class<? extends EditController<T, U>> controllerClass,
            MainController mainController, Stage stage) throws IOException {
        EditItem<T, U> fc = ViewControllerLoader.showAndWait(new ViewControllerLifecycleEventListener<Parent, EditItem<T, U>>() {

            private ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController;

            @Override
            public void onViewControllerLifecycleEvent(ViewControllerLifecycleEvent<Parent, EditItem<T, U>> event) {
                switch (event.getReason()) {
                    case LOADED:
                        try {
                            viewAndController = ViewControllerLoader.loadViewAndController(controllerClass);
                            event.getController().onContentLoaded(viewAndController);
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(this, ViewLifecycleEventReason.LOADED, stage));
                        break;
                    case ADDED:
                        event.getController().onIntitForNew(viewAndController.getView(), viewAndController.getController());
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(event.getController(), ViewLifecycleEventReason.ADDED, event.getStage()));
                        break;
                    case SHOWN:
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(event.getController(), ViewLifecycleEventReason.SHOWN, event.getStage()));
                        break;
                    case UNLOADED:
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(event.getController(), ViewLifecycleEventReason.UNLOADED, event.getStage()));
                        break;
                }
            }

        }, stage, EditItem.class);

        T dataAccessObject = fc.contentController.model.getDataObject();
        if (null != dataAccessObject) {
            AnnotationHelper.invokeDataObjectEventMethods(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.CREATED,
                    dataAccessObject));
        }
    }

    protected static <T extends DataObjectImpl, U extends ItemModel<T>> void edit(U model, Class<? extends EditController<T, U>> controllerClass,
            MainController mainController, Stage stage) throws IOException {
        EditItem<T, U> fc = ViewControllerLoader.showAndWait(new ViewControllerLifecycleEventListener<Parent, EditItem<T, U>>() {

            private ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController;

            @Override
            public void onViewControllerLifecycleEvent(ViewControllerLifecycleEvent<Parent, EditItem<T, U>> event) {
                switch (event.getReason()) {
                    case LOADED:
                        try {
                            event.getController().onContentLoaded(model, ViewControllerLoader.loadViewAndController(controllerClass));
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(this, ViewLifecycleEventReason.LOADED, stage));
                        break;
                    case ADDED:
                        event.getController().onIntitForEdit(viewAndController.getView(), viewAndController.getController());
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(event.getController(), ViewLifecycleEventReason.ADDED, event.getStage()));
                        break;
                    case SHOWN:
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(event.getController(), ViewLifecycleEventReason.SHOWN, event.getStage()));
                        break;
                    case UNLOADED:
                        AnnotationHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                                viewAndController.toEvent(event.getController(), ViewLifecycleEventReason.UNLOADED, event.getStage()));
                        break;
                }
            }

        }, stage, EditItem.class);

        T dataAccessObject = fc.contentController.model.getDataObject();
        if (null != dataAccessObject) {
            if (dataAccessObject.getRowState() == DataRowState.DELETED) {
                AnnotationHelper.invokeDataObjectEventMethods(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.DELETED,
                        dataAccessObject));
            } else {
                AnnotationHelper.invokeDataObjectEventMethods(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.UPDATED,
                        dataAccessObject));
            }
        }
    }

//    /**
//     * Displays a dialog window to edit an {@link ItemModel}.
//     *
//     * @param <M> The type of {@link ItemModel} to be edited.
//     * @param <C> The type of controller for the editor.
//     * @param controllerClass The {@link EditController} class.
//     * @param model The {@link ItemModel} to be edited.
//     * @param parent The {@link Stage} that represents the parent window.
//     * @return A {@link ShowAndWaitResult} object that represents the result of the dialog window.
//     */
//    @Deprecated
//    public static <M extends ItemModel<?>, C extends EditController<?, M>> ShowAndWaitResult<M> waitEdit(Class<C> controllerClass,
//            M model, Stage parent) {
//        final ShowAndWaitResult<M> result = new ShowAndWaitResult<>(model);
//        try {
//            loadViewAndController(parent, EditItem.class, (Parent v, EditItem ctrl) -> {
//                ctrl.result = result;
//                ctrl.onLoaded(v, model);
//            }, (Parent v, EditItem ctrl) -> {
//                ctrl.onShow(v, model, controllerClass, parent);
//            });
//        } catch (IOException ex) {
//            AlertHelper.logAndAlertError(parent, LOG, EditItem.class, "waitEdit", String.format("Error loading FXML for %s", EditItem.class.getName()), ex);
//            result.fault = ex;
//            result.successful = false;
//            result.canceled = false;
//            result.deleteOperation = false;
//        }
//        return result;
//    }
//    //private ShowAndWaitResult<U> result;
    private EditController<T, U> contentController;

    @FXML
    private BorderPane contentBorderPane; // Value injected by FXMLLoader

    @FXML
    private Label createdLabel; // Value injected by FXMLLoader

    @FXML
    private Label createDateValue; // Value injected by FXMLLoader

    @FXML
    private Label createdByLabel; // Value injected by FXMLLoader

    @FXML
    private Label createdByValue; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateLabel; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateValue; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateByLabel; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateByValue; // Value injected by FXMLLoader

    @FXML
    private Button saveChangesButton; // Value injected by FXMLLoader

    @FXML
    private Button deleteButton; // Value injected by FXMLLoader

    @FXML
    private Button cancelButton; // Value injected by FXMLLoader

    private void onContentLoaded(ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController) {
        contentController = viewAndController.getController();
        contentController.model = contentController.getFactory().createNew(
                contentController.getFactory().getDaoFactory().createNew());
    }

    private void onContentLoaded(U model, ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController) {
        contentController = viewAndController.getController();
        contentController.model = model;
    }

    private void onIntitForNew(Parent view, EditController<T, U> controller) {
        collapseNode(deleteButton);
        collapseNode(createdLabel);
        collapseNode(createDateValue);
        collapseNode(createdByLabel);
        collapseNode(createdByValue);
        collapseNode(lastUpdateByLabel);
        collapseNode(lastUpdateByValue);
        collapseNode(lastUpdateLabel);
        collapseNode(lastUpdateValue);
        contentBorderPane.setCenter(view);
    }

    private void onIntitForEdit(Parent view, EditController<T, U> controller) {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        Locale.getDefault(Locale.Category.DISPLAY);
        createdByValue.setText(controller.model.getCreatedBy());
        createDateValue.setText(dtf.format(controller.model.getCreateDate()));
        lastUpdateByValue.setText(controller.model.getLastModifiedBy());
        lastUpdateValue.setText(dtf.format(controller.model.getLastModifiedDate()));
        contentBorderPane.setCenter(view);
    }

    @HandlesDataObjectEvent
    protected void onDataObjectEvent(DataObjectEvent<? extends DataObjectImpl> event) {
        AnnotationHelper.invokeDataObjectEventMethods(contentController, event);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert contentBorderPane != null : String.format("fx:id=\"contentBorderPane\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createdLabel != null : String.format("fx:id=\"createdLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateLabel != null : String.format("fx:id=\"lastUpdateLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createDateValue != null : String.format("fx:id=\"createDateValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createdByLabel != null : String.format("fx:id=\"createdByLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createdByValue != null : String.format("fx:id=\"createdByValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateValue != null : String.format("fx:id=\"lastUpdateValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateByLabel != null : String.format("fx:id=\"lastUpdateByLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateByValue != null : String.format("fx:id=\"lastUpdateByValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(saveChangesButton, String.format("fx:id=\"saveChangesButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            ItemModel.ModelFactory<T, U> factory = contentController.getFactory();
            T dao = factory.applyChanges(contentController.model);
            TaskWaiter.execute(new SaveTask(dao, (Stage) saveChangesButton.getScene().getWindow()));
        });
        Objects.requireNonNull(deleteButton, String.format("fx:id=\"deleteButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            Optional<ButtonType> response = AlertHelper.showWarningAlert(AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            if (response.isPresent() && response.get() == ButtonType.YES) {
                TaskWaiter.execute(new DeleteTask((Stage) saveChangesButton.getScene().getWindow()));
            }
        });
        Objects.requireNonNull(cancelButton, String.format("fx:id=\"cancelButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            cancelButton.getScene().getWindow().hide();
        });
    }

//    /**
//     * This method is called from {@link #waitEdit(java.lang.Class, scheduler.view.ItemModel, javafx.stage.Stage)} after the view is loaded.
//     *
//     * @param view The view loaded by the {@link javafx.fxml.FXMLLoader}.
//     * @param model The {@link ItemModel} being edited.
//     */
//    private void onLoaded(Parent view, ItemModel<?> model) {
//        if (model.isNewItem()) {
//            collapseNode(deleteButton);
//            collapseNode(createdLabel);
//            collapseNode(createDateValue);
//            collapseNode(createdByLabel);
//            collapseNode(createdByValue);
//            collapseNode(lastUpdateByLabel);
//            collapseNode(lastUpdateByValue);
//            collapseNode(lastUpdateLabel);
//            collapseNode(lastUpdateValue);
//        } else {
//            DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
//            Locale.getDefault(Locale.Category.DISPLAY);
//            createdByValue.setText(model.getCreatedBy());
//            createDateValue.setText(dtf.format(model.getCreateDate()));
//            lastUpdateByValue.setText(model.getLastModifiedBy());
//            lastUpdateValue.setText(dtf.format(model.getLastModifiedDate()));
//        }
//    }
//    private <C extends EditController<T, U>> void onShow(Parent view, U model, Class<C> controllerClass, Stage parent) {
//        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.initOwner(parent);
//        stage.setScene(new Scene(view));
//        try {
//            loadViewAndController(stage, controllerClass, (Parent p, C c) -> {
//                contentController = c;
//                ((EditController<?, U>) c).model = model;
//            }, (Parent p, C c) -> {
//                contentBorderPane.setCenter(p);
//                c.getValidationExpression().addListener((observable) -> {
//                    saveChangesButton.setDisable(!c.getValidationExpression().get());
//                });
//                saveChangesButton.setDisable(!c.getValidationExpression().get());
//            }, EditItem.class);
//        } catch (IOException ex) {
//            AlertHelper.logAndAlertError(parent, LOG, EditItem.class, "onShow", String.format("Error loading FXML for %s", controllerClass.getName()), ex);
//            result.fault = ex;
//            result.successful = false;
//            result.canceled = false;
//            result.deleteOperation = false;
//            return;
//        }
//        stage.showAndWait();
//    }
//    /**
//     * Represents the results of the {@link #waitEdit(java.lang.Class, scheduler.view.ItemModel, javafx.stage.Stage)} method.
//     *
//     * @param <M> The type of model being edited.
//     */
//    public static class ShowAndWaitResult<M extends ItemModel<?>> {
//
//        private boolean successful;
//
//        /**
//         * Returns {@code true} if the operation was successful; otherwise {@code false}.
//         *
//         * @return {@code true} if the operation was successful; otherwise {@code false} if {@link #canceled} is {@code true} or {@link #fault} is not
//         * {@code null}.
//         */
//        public boolean isSuccessful() {
//            return successful;
//        }
//
//        private boolean canceled;
//
//        /**
//         * Returns {@code true} if the operation was canceled; otherwise {@code false}.
//         *
//         * @return {@code true} if the operation was canceled; otherwise {@code false}.
//         */
//        public boolean isCanceled() {
//            return canceled;
//        }
//
//        private Throwable fault;
//
//        /**
//         * Gets the {@code Throwable} that occurred while trying to perform the operation.
//         *
//         * @return the {@code Throwable} that occurred while trying to perform the operation.
//         */
//        public Throwable getFault() {
//            return fault;
//        }
//
//        private boolean deleteOperation;
//
//        /**
//         * Returns {@code true} if the result was for a delete operation; otherwise {@code false} if it was an insert or update.
//         *
//         * @return {@code true} if the result was for a delete operation; otherwise {@code false} if it was an insert or update.
//         */
//        public boolean isDeleteOperation() {
//            return deleteOperation;
//        }
//
//        private final M target;
//
//        /**
//         * Gets the {@link ItemModel} that was edited or deleted.
//         *
//         * @return the {@link ItemModel} that was edited or deleted.
//         */
//        public M getTarget() {
//            return target;
//        }
//
//        private ShowAndWaitResult(M target) {
//            this.target = Objects.requireNonNull(target);
//            successful = false;
//            canceled = false;
//            fault = null;
//            deleteOperation = false;
//        }
//    }
    /**
     * Base class for item edit content controllers.
     *
     * @param <T> The type of data access object that the model represents.
     * @param <U> The type of model being edited.
     */
    public static abstract class EditController<T extends DataObjectImpl, U extends ItemModel<T>> extends SchedulerController {

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

    }

    private class SaveTask extends TaskWaiter<String> {

        private final T dataAccessobject;
        private final DaoFactory<T> daoFactory;

        SaveTask(T dataAccessobject, Stage stage) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVINGCHANGES));
            this.dataAccessobject = dataAccessobject;
            daoFactory = contentController.getFactory().getDaoFactory();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null == message || message.trim().isEmpty()) {
                contentController.getFactory().updateItem(contentController.model, dataAccessobject);
                owner.hide();
            } else {
                AlertHelper.showWarningAlert(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error saving record", ex);
            AlertHelper.showErrorAlert(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVEFAILURE), AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORSAVINGCHANGES), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = daoFactory.getSaveConflictMessage(contentController.model.getDataObject(), connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            LOG.logp(Level.WARNING, getClass().getName(), "getResult", "Not implemeted");
            daoFactory.save(contentController.model.getDataObject(), connection);

            return null;
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
                AlertHelper.showWarningAlert(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error deleting record", ex);
            AlertHelper.showErrorAlert(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE),
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
