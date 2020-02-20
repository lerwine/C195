package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.App;
import scheduler.dao.DataObjectImpl;
import scheduler.util.Alerts;
import scheduler.util.DbConnector;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Base FXML Controller class for editing {@link ItemModel} items in a new modal window.
 *
 * @author Leonard T. Erwine
 * @param <D> The type of data access object that the model represents.
 * @param <M> The type of model being edited.
 */
@GlobalizationResource("view/EditItem")
@FXMLResource("/view/EditItem.fxml")
public final class EditItem<D extends DataObjectImpl, M extends ItemModel<D>> extends SchedulerController {
    //<editor-fold defaultstate="collapsed" desc="fields">

    private ShowAndWaitResult<M> result;

    private EditController<D, M> contentController;

    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
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

    //</editor-fold>
    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    //</editor-fold>
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert contentBorderPane != null : String.format("fx:id=\"contentBorderPane\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert createdLabel != null : String.format("fx:id=\"createdLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert lastUpdateLabel != null : String.format("fx:id=\"lastUpdateLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert createDateValue != null : String.format("fx:id=\"createDateValue\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert createdByLabel != null : String.format("fx:id=\"createdByLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert createdByValue != null : String.format("fx:id=\"createdByValue\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert lastUpdateValue != null : String.format("fx:id=\"lastUpdateValue\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert lastUpdateByLabel != null : String.format("fx:id=\"lastUpdateByLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert lastUpdateByValue != null : String.format("fx:id=\"lastUpdateByValue\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        Objects.requireNonNull(saveChangesButton, String.format("fx:id=\"saveChangesButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            throw new UnsupportedOperationException("Not implemented");
//            contentController.getDaoFactory().applyChanges(contentController.model);
//            TaskWaiter.execute(new SaveTask((Stage)saveChangesButton.getScene().getWindow()));
        });
        Objects.requireNonNull(deleteButton, String.format("fx:id=\"deleteButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            ResourceBundle rb = getResources();
            Optional<ButtonType> response = Alerts.showWarningAlert(App.getResourceString(App.RESOURCEKEY_CONFIRMDELETE),
                    App.getResourceString(App.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            if (response.isPresent() && response.get() == ButtonType.YES) {
                TaskWaiter.execute(new DeleteTask((Stage) deleteButton.getScene().getWindow()));
            }
        });
        Objects.requireNonNull(cancelButton, String.format("fx:id=\"cancelButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            result.canceled = true;
            result.successful = false;
            result.deleteOperation = false;
            result.fault = null;
            cancelButton.getScene().getWindow().hide();
        });
    }

    /**
     * This method is called from {@link #waitEdit(java.lang.Class, scheduler.view.ItemModel, javafx.stage.Stage)} after the view is loaded.
     *
     * @param view The view loaded by the {@link javafx.fxml.FXMLLoader}.
     * @param model The {@link ItemModel} being edited.
     */
    private void onLoaded(Parent view, ItemModel<?> model) {
        if (model.isNewItem()) {
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
            createdByValue.setText(model.getCreatedBy());
            createDateValue.setText(dtf.format(model.getCreateDate()));
            lastUpdateByValue.setText(model.getLastModifiedBy());
            lastUpdateValue.setText(dtf.format(model.getLastModifiedDate()));
        }
    }

    private <C extends EditController<D, M>> void onShow(Parent view, M model, Class<C> controllerClass, Stage parent) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        stage.setScene(new Scene(view));
        try {
            load(stage, controllerClass, (Parent p, C c) -> {
                contentController = c;
                ((EditController<?, M>) c).model = model;
            }, (Parent p, C c) -> {
                contentBorderPane.setCenter(p);
                c.getValidationExpression().addListener((observable) -> {
                    saveChangesButton.setDisable(!c.getValidationExpression().get());
                });
                saveChangesButton.setDisable(!c.getValidationExpression().get());
            }, getResources());
        } catch (IOException ex) {
            Alerts.logAndAlert(LOG, EditItem.class, "onShow", String.format("Error loading FXML for %s", controllerClass.getName()), ex);
            result.fault = ex;
            result.successful = false;
            result.canceled = false;
            result.deleteOperation = false;
            return;
        }
        stage.showAndWait();
    }

    /**
     * Displays a dialog window to edit an {@link ItemModel}.
     *
     * @param <M> The type of {@link ItemModel} to be edited.
     * @param <C> The type of controller for the editor.
     * @param controllerClass The {@link EditController} class.
     * @param model The {@link ItemModel} to be edited.
     * @param parent The {@link Stage} that represents the parent window.
     * @return A {@link ShowAndWaitResult} object that represents the result of the dialog window.
     */
    public static <M extends ItemModel<?>, C extends EditController<?, M>> ShowAndWaitResult<M> waitEdit(Class<C> controllerClass,
            M model, Stage parent) {
        final ShowAndWaitResult<M> result = new ShowAndWaitResult<>(model);
        try {
            load(parent, EditItem.class, (Parent v, EditItem ctrl) -> {
                ctrl.result = result;
                ctrl.onLoaded(v, model);
            }, (Parent v, EditItem ctrl) -> {
                ctrl.onShow(v, model, controllerClass, parent);
            });
        } catch (IOException ex) {
            Alerts.logAndAlert(LOG, EditItem.class, "waitEdit", String.format("Error loading FXML for %s", EditItem.class.getName()), ex);
            result.fault = ex;
            result.successful = false;
            result.canceled = false;
            result.deleteOperation = false;
        }
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Nested classes">
    /**
     * Represents the results of the {@link #waitEdit(java.lang.Class, scheduler.view.ItemModel, javafx.stage.Stage)} method.
     *
     * @param <M> The type of model being edited.
     */
    public static class ShowAndWaitResult<M extends ItemModel<?>> {

        private boolean successful;

        /**
         * Returns {@code true} if the operation was successful; otherwise {@code false}.
         *
         * @return {@code true} if the operation was successful; otherwise {@code false} if {@link #canceled} is {@code true} or {@link #fault} is not {@code null}.
         */
        public boolean isSuccessful() {
            return successful;
        }

        private boolean canceled;

        /**
         * Returns {@code true} if the operation was canceled; otherwise {@code false}.
         *
         * @return {@code true} if the operation was canceled; otherwise {@code false}.
         */
        public boolean isCanceled() {
            return canceled;
        }

        private Throwable fault;

        /**
         * Gets the {@code Throwable} that occurred while trying to perform the operation.
         *
         * @return the {@code Throwable} that occurred while trying to perform the operation.
         */
        public Throwable getFault() {
            return fault;
        }

        private boolean deleteOperation;

        /**
         * Returns {@code true} if the result was for a delete operation; otherwise {@code false} if it was an insert or update.
         *
         * @return {@code true} if the result was for a delete operation; otherwise {@code false} if it was an insert or update.
         */
        public boolean isDeleteOperation() {
            return deleteOperation;
        }

        private final M target;

        /**
         * Gets the {@link ItemModel} that was edited or deleted.
         *
         * @return the {@link ItemModel} that was edited or deleted.
         */
        public M getTarget() {
            return target;
        }

        private ShowAndWaitResult(M target) {
            this.target = Objects.requireNonNull(target);
            successful = false;
            canceled = false;
            fault = null;
            deleteOperation = false;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Background tasks">
    private class SaveTask extends TaskWaiter<String> {

        SaveTask(Stage stage) {
            super(stage, App.getResourceString(App.RESOURCEKEY_SAVINGCHANGES));
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null == message || message.trim().isEmpty()) {
                try {
                    contentController.model.refreshFromDAO();
                    result.successful = true;
                    result.fault = null;
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, "Error calling refreshFromDAO", ex);
                    result.successful = false;
                    result.fault = ex;
                }
                result.deleteOperation = false;
                result.canceled = false;
                owner.hide();
            } else {
                Alerts.showWarningAlert(App.getResourceString(App.RESOURCEKEY_SAVEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error saving record", ex);
            Alerts.showErrorAlert(App.getResourceString(App.RESOURCEKEY_SAVEFAILURE), App.getResourceString(App.RESOURCEKEY_ERRORSAVINGCHANGES), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = contentController.getDaoFactory().getSaveConflictMessage(contentController.model.getDataObject(), connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            LOG.logp(Level.WARNING, getClass().getName(), "getResult", "Not implemeted");
            contentController.getDaoFactory().save(contentController.model.getDataObject(), connection);
           
            return null;
        }
    }

    private class DeleteTask extends TaskWaiter<String> {

        DeleteTask(Stage stage) {
            super(stage, App.getResourceString(App.RESOURCEKEY_DELETINGRECORD));
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null == message || message.trim().isEmpty()) {
                result.successful = true;
                result.deleteOperation = true;
                result.fault = null;
                result.canceled = false;
                owner.hide();
            } else {
                Alerts.showWarningAlert(App.getResourceString(App.RESOURCEKEY_DELETEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error deleting record", ex);
            Alerts.showErrorAlert(App.getResourceString(App.RESOURCEKEY_DELETEFAILURE), App.getResourceString(App.RESOURCEKEY_ERRORDELETINGFROMDB), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = contentController.getDaoFactory().getDeleteDependencyMessage(contentController.model.getDataObject(), connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            contentController.getDaoFactory().delete(contentController.model.getDataObject(), connection);
            return null;
        }
    }

    //</editor-fold>
    /**
     * Base class for item edit content controllers.
     *
     * @param <D> The type of data access object that the model represents.
     * @param <M> The type of model being edited.
     */
    public static abstract class EditController<D extends DataObjectImpl, M extends ItemModel<D>> extends SchedulerController {
        //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">

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

        //</editor-fold>
        private M model;

        /**
         * Gets the current {@link ItemModel} being edited.
         *
         * @return the current {@link ItemModel} being edited.
         */
        protected M getModel() {
            return model;
        }

        /**
         * This gets called to get an instance of the {@link DataObjectImpl.Factory}.
         *
         * @return An instance of the {@link DataObjectImpl.Factory}.
         */
        protected abstract DataObjectImpl.Factory<D, M> getDaoFactory();

        /**
         * Gets the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         *
         * @return the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         */
        protected abstract BooleanExpression getValidationExpression();
//
//        /**
//         * Gets a message indicating whether any existing record conflicts with the proposed change.
//         *
//         * @param connection The {@link Connection} to use to look for database conflicts.
//         * @return The human-readable conflict message or {@code null} if there are no conflicts.
//         * @throws SQLException if unable to check for conflicts.
//         */
//        protected abstract String getSaveConflictMessage(Connection connection) throws SQLException;
//
//        /**
//         * Gets a message indicating whether any other records have a dependency on the current record.
//         *
//         * @param connection The {@link Connection} to use to look for dependencies.
//         * @return The human-readable dependency message or {@code null} if there are no dependencies.
//         * @throws SQLException if unable to check for dependencies.
//         */
//        protected abstract String getDeleteDependencyMessage(Connection connection) throws SQLException;

    }

    //</editor-fold>
}
