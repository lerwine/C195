/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import scheduler.expressions.ReadOnlyModelProperty;
import java.sql.SQLException;
import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import scheduler.util.DbConnector;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Base FXML Controller class for editing {@link ItemModel} items in a new modal window.
 *
 * @author Leonard T. Erwine
 * @param <M> The type of {@link ItemModel} begin edited.
 */
@GlobalizationResource("view/EditItem")
@FXMLResource("/view/EditItem.fxml")
public class EditItem<M extends ItemModel<?>> extends SchedulerController {
    //<editor-fold defaultstate="collapsed" desc="fields">
    
    private ItemController<M> contentController;
    private final ShowAndWaitResult<M> result;
    
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    
//    public static final String RESOURCEKEY_ADD = "add";
    public static final String RESOURCEKEY_REQUIRED = "required";
//    public static final String RESOURCEKEY_CREATED = "created";
//    public static final String RESOURCEKEY_BY = "by";
//    public static final String RESOURCEKEY_UPDATED = "updated";
//    public static final String RESOURCEKEY_SAVE = "save";
//    public static final String RESOURCEKEY_CANCEL = "cancel";
//    public static final String RESOURCEKEY_SAVEANYWAY = "saveAnyway";
//    public static final String RESOURCEKEY_DELETE = "delete";
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
    public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";
    public static final String RESOURCEKEY_LOADERRORTITLE = "loadErrorTitle";
    public static final String RESOURCEKEY_LOADERRORMESSAGE = "loadErrorMessage";
    
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
    
    //<editor-fold defaultstate="collapsed" desc="target property">
    
    private M target;
    
    /**
     * Gets the {@link ItemModel} being edited.
     * @return The {@link ItemModel} being edited.
     */
    public M getTarget() { return target; }
    
    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a new EditItem controller.
     */
    protected EditItem() {
        result = new ShowAndWaitResult<>();
    }
    
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
            result.deleteOperation.set(true);
            try (DbConnector dep =  new DbConnector()) {
                result.getTarget().saveChanges(dep.getConnection());
                result.successful.set(result.target.isSaved().get());
            } catch (SQLException | ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
//            getViewManager().closeWindow();
        });
        Objects.requireNonNull(deleteButton, String.format("fx:id=\"deleteButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            ResourceBundle rb = getResources();
            Alert alert = new Alert(Alert.AlertType.WARNING, rb.getString(RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            alert.setTitle(RESOURCEKEY_CONFIRMDELETE);
            alert.initStyle(StageStyle.UTILITY);
            // TODO: Show confirmation dialog
            result.deleteOperation.set(true);
            try (DbConnector dep =  new DbConnector()) {
                result.getTarget().delete(dep.getConnection());
                result.successful.set(result.target.isDeleted().get());
            } catch (SQLException | ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
//            getViewManager().closeWindow();
        });
        Objects.requireNonNull(cancelButton, String.format("fx:id=\"cancelButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            result.successful.set(true);
            result.canceled.set(true);
//            getViewManager().closeWindow();
        });
    }
    
    public static class ShowAndWaitResult<M extends ItemModel<?>> {

        private final ReadOnlyBooleanWrapper successful;

        public boolean isSuccessful() { return successful.get(); }

        public ReadOnlyBooleanProperty successfulProperty() { return successful.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper canceled;

        public boolean isCanceled() { return canceled.get(); }

        public ReadOnlyBooleanProperty canceledProperty() { return canceled.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<Throwable> fault;

        public Throwable getFault() { return fault.get(); }

        public ReadOnlyObjectProperty<Throwable> faultProperty() { return fault.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper deleteOperation;

        public boolean isDeleteOperation() { return deleteOperation.get(); }

        public ReadOnlyBooleanProperty deleteOperationProperty() { return deleteOperation.getReadOnlyProperty(); }
        
        private final ReadOnlyModelProperty<M> target;

        public M getTarget() { return target.get(); }

        public ReadOnlyObjectProperty<M> targetProperty() { return target.getReadOnlyProperty(); }
        
        private ShowAndWaitResult() {
            successful = new ReadOnlyBooleanWrapper(false);
            canceled = new ReadOnlyBooleanWrapper(false);
            fault = new ReadOnlyObjectWrapper<>();
            deleteOperation = new ReadOnlyBooleanWrapper(false);
            target = new ReadOnlyModelProperty<>();
        }
    }
    
}
