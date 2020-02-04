/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.DataObjectImpl;
import scheduler.util.Alerts;
import scheduler.util.DbConnector;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Base FXML Controller class for editing {@link ItemModel} items in a new modal window.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/EditItem")
@FXMLResource("/view/EditItem.fxml")
public final class EditItem extends SchedulerController {
    //<editor-fold defaultstate="collapsed" desc="fields">
    
    private ShowAndWaitResult<?> result;
    
    private EditController<?> contentController;
    
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    
    public static final String RESOURCEKEY_REQUIRED = "required";
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
    public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";
    public static final String RESOURCEKEY_LOADERRORTITLE = "loadErrorTitle";
    public static final String RESOURCEKEY_LOADERRORMESSAGE = "loadErrorMessage";
    public static final String RESOURCEKEY_SAVINGCHANGES = "savingChanges";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    public static final String RESOURCEKEY_ERRORSAVINGCHANGES = "errorSavingChanges";
    public static final String RESOURCEKEY_ERRORDELETINGFROMDB = "errorDeletingFromDb";
    public static final String RESOURCEKEY_DELETEDEPENDENCYERROR = "deleteDependencyError";
    public static final String RESOURCEKEY_VALIDATIONERROR = "validationError";
    public static final String RESOURCEKEY_DELETEFAILURE = "deleteFailure";
    public static final String RESOURCEKEY_SAVEDEPENDENCYERROR = "saveDependencyError";
    public static final String RESOURCEKEY_SAVEFAILURE = "saveFailure";
    public static final String RESOURCEKEY_DELETINGRECORD = "deletingRecord";
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
    @FXML
    private VBox parentVBox; // Value injected by FXMLLoader
    
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
            TaskWaiter.execute(new SaveTask((Stage)deleteButton.getScene().getWindow()));
        });
        Objects.requireNonNull(deleteButton, String.format("fx:id=\"deleteButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            ResourceBundle rb = getResources();
            Optional<ButtonType> response = Alerts.showWarningAlert(getResourceString(RESOURCEKEY_CONFIRMDELETE),
                    getResourceString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            if (response.isPresent() && response.get() == ButtonType.YES)
                TaskWaiter.execute(new DeleteTask((Stage)deleteButton.getScene().getWindow()));
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
    
    public static <M extends ItemModel<?>, C extends EditController<M>> ShowAndWaitResult<M> waitEdit(Class<C> controllerClass, M model, Stage parent) {
        final ShowAndWaitResult<M> result = new ShowAndWaitResult<>(model);
        try {
            load(parent, EditItem.class, (Parent v, EditItem ctrl) -> {
                ctrl.result = result;
            }, (Parent v, EditItem ctrl) -> {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(parent);
                stage.setScene(new Scene(v));
                try {
                    load(stage, controllerClass, (Parent p, C c) -> {
                        ctrl.contentController = c;
                        ((EditController<M>)c).model = model;
                    }, (Parent p, C c) -> {
                        ctrl.contentBorderPane.setCenter(p);
                    });
                } catch (IOException ex) {
                    Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, null, ex);
                    result.fault = ex;
                    result.successful = false;
                    result.canceled = false;
                    result.deleteOperation = false;
                    return;
                }
                stage.showAndWait();
            });
        } catch (IOException ex) {
            result.fault = ex;
            result.successful = false;
            result.canceled = false;
            result.deleteOperation = false;
            Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static class ShowAndWaitResult<M extends ItemModel<?>> {

        private boolean successful;

        public boolean isSuccessful() { return successful; }

        private boolean canceled;

        public boolean isCanceled() { return canceled; }

        private Throwable fault;

        public Throwable getFault() { return fault; }

        private boolean deleteOperation;

        public boolean isDeleteOperation() { return deleteOperation; }

        private final M target;

        public M getTarget() { return target; }

        private ShowAndWaitResult(M target) {
            this.target = Objects.requireNonNull(target);
            successful = false;
            canceled = false;
            fault = null;
            deleteOperation = false;
        }
    }
    
    private class SaveTask extends TaskWaiter<String> {
        private final DataObjectImpl dao;
        SaveTask(Stage stage) {
            super(stage, getResourceString(RESOURCEKEY_SAVINGCHANGES));
            dao = result.getTarget().getDataObject();
        }

        @Override
        protected void processResult(String message, Window owner) {
            if (null == message || message.trim().isEmpty()) {
                result.deleteOperation = false;
                result.successful = true;
                result.fault = null;
                result.canceled = false;
                owner.hide();
            } else
                Alerts.showWarningAlert(getResourceString(RESOURCEKEY_SAVEFAILURE), message);
        }

        @Override
        protected void processException(Throwable ex, Window owner) {
            Alerts.showWarningAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORSAVINGCHANGES));
            LOG.log(Level.SEVERE, "Error saving record", ex);
        }

        @Override
        protected String getResult() throws Exception {
            try (DbConnector dep = new DbConnector()) {
                String message = dao.getValidationMessageForSave(dep.getConnection());
                if (null != message && !message.trim().isEmpty())
                    return message;
                dao.delete(dep.getConnection());
            }
            return null;
        }
    }
    
    private class DeleteTask extends TaskWaiter<String> {
        private final DataObjectImpl dao;
        DeleteTask(Stage stage) {
            super(stage, getResourceString(RESOURCEKEY_DELETINGRECORD));
            dao = result.getTarget().getDataObject();
        }

        @Override
        protected void processResult(String message, Window owner) {
            if (null == message || message.trim().isEmpty()) {
                result.successful = true;
                result.deleteOperation = true;
                result.fault = null;
                result.canceled = false;
                owner.hide();
            } else
                Alerts.showWarningAlert(getResourceString(RESOURCEKEY_DELETEFAILURE), message);
        }

        @Override
        protected void processException(Throwable ex, Window owner) {
            Alerts.showWarningAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORDELETINGFROMDB));
            LOG.log(Level.SEVERE, "Error deleting record", ex);
        }

        @Override
        protected String getResult() throws Exception {
            try (DbConnector dep = new DbConnector()) {
                String message = dao.getValidationMessageForDelete(dep.getConnection());
                if (null != message && !message.trim().isEmpty())
                    return message;
                dao.delete(dep.getConnection());
            }
            return null;
        }
    }
    
    /**
     * Base class for item edit content controllers.
     * @param <M> The type of model being edited.
     */
    public static abstract class EditController<M extends ItemModel<?>> extends SchedulerController {

        private M model;
        
        protected M getModel() { return model; }
        
        protected abstract void updateModelAndDao();
    }
}
