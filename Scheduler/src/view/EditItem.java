/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import expressions.ReadOnlyDataRowProperty;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import scheduler.InvalidOperationException;
import util.SqlConnectionDependency;
import scheduler.Util;
import util.Alerts;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 * @param <R>
 */
@GlobalizationResource("view/EditItem")
@FXMLResource("/view/EditItem.fxml")
public class EditItem<R extends model.db.DataRow> extends Controller {
    //<editor-fold defaultstate="collapsed" desc="fields">
    
    private Stage stage;
    private ItemController<R> contentController;
    private final ShowAndWaitResult<R> result;
    
    //<editor-fold defaultstate="collapsed" desc="Constants">
    
    public static final String RESOURCEKEY_ADD = "add";
    public static final String RESOURCEKEY_REQUIRED = "required";
    public static final String RESOURCEKEY_CREATED = "created";
    public static final String RESOURCEKEY_BY = "by";
    public static final String RESOURCEKEY_UPDATED = "updated";
    public static final String RESOURCEKEY_SAVE = "save";
    public static final String RESOURCEKEY_CANCEL = "cancel";
    public static final String RESOURCEKEY_SAVEANYWAY = "saveAnyway";
    public static final String RESOURCEKEY_DELETE = "delete";
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
    public static final String RESOURCEKEY_LOADERRORTITLE = "loadErrorTitle";
    public static final String RESOURCEKEY_LOADERRORMESSAGE = "loadErrorMessage";
    
    //</editor-fold>
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    
    @FXML // fx:id="parentVBox"
    private VBox parentVBox; // Value injected by FXMLLoader
    
    @FXML // fx:id="contentBorderPane"
    private BorderPane contentBorderPane; // Value injected by FXMLLoader
    
    @FXML
    protected Label createdLabel;
    
    @FXML
    protected Label createDateValue;
    
    @FXML
    protected Label createdByLabel;
    
    @FXML
    protected Label createdByValue;
    
    @FXML
    protected Label lastUpdateLabel;
    
    @FXML
    protected Label lastUpdateValue;
    
    @FXML
    protected Label lastUpdateByLabel;
    
    @FXML
    protected Label lastUpdateByValue;
    
    @FXML
    private Button saveChangesButton;
    
    @FXML
    private Button deleteButton;
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="target property">
    
    private final ReadOnlyDataRowProperty<R> target;
    
    public R getTarget() { return target.get(); }
    
    public ReadOnlyObjectProperty<R> targetProperty() { return target.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="windowTitle property">
    
    private final StringProperty windowTitle;
    
    public String getWindowTitle() { return windowTitle.get(); }
    
    public void setWindowTitle(String value) { windowTitle.set((value == null || value.trim().isEmpty()) ? getDefaultWindowTitle() : value); }
    
    public StringProperty windowTitleProperty() { return windowTitle; }
    
    private String getDefaultWindowTitle() {
        return scheduler.App.CURRENT.get().getResources().getString(scheduler.App.RESOURCEKEY_APPOINTMENTSCHEDULER);
    }
    
    //</editor-fold>
    
    private EditItem() {
        result = new ShowAndWaitResult<>();
        stage = null;
        target = new ReadOnlyDataRowProperty<>();
        windowTitle = new SimpleStringProperty(getDefaultWindowTitle()) {
            @Override
            public void set(String newValue) {
                super.set((newValue == null || (newValue = newValue.trim()).isEmpty()) ? getDefaultWindowTitle() : newValue);
            }

            @Override
            public void setValue(String v) {
                super.set((v == null || (v = v.trim()).isEmpty()) ? getDefaultWindowTitle() : v);
            }
        };
        windowTitle.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (stage != null)
                stage.setTitle(newValue);
        });
    }
    
    @FXML
    private void cancelButtonClick(ActionEvent event) {
        result.successful.set(true);
        result.canceled.set(true);
        if (stage != null)
            stage.hide();
    }

    @FXML
    private void deleteButtonClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "This action cannot be undone!\n\nAre you sure you want to delete this item?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        alert.initStyle(StageStyle.UTILITY);
        // TODO: Show confirmation dialog
        result.deleteOperation.set(true);
        try (SqlConnectionDependency dep =  new SqlConnectionDependency()) {
            result.getTarget().delete(dep.getConnection());
            result.successful.set(result.target.isDeleted().get());
        } catch (SQLException | InvalidOperationException ex) {
            Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (stage != null)
            stage.hide();
    }

    @FXML
    private void saveChangesButtonClick(ActionEvent event) {
        result.deleteOperation.set(true);
        try (SqlConnectionDependency dep =  new SqlConnectionDependency()) {
            result.getTarget().saveChanges(dep.getConnection());
            result.successful.set(result.target.isSaved().get());
        } catch (SQLException | InvalidOperationException ex) {
            Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (stage != null)
            stage.hide();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert contentBorderPane != null : "fx:id=\"contentBorderPane\" was not injected: check your FXML file 'EditItem.fxml'.";
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
        assert saveChangesButton != null : "fx:id=\"saveChangesButton\" was not injected: check your FXML file 'EditItem.fxml'.";
        assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'EditItem.fxml'.";
    }
    
    //<editor-fold defaultstate="collapsed" desc="showAndWait overloads">

    public static <R extends model.db.DataRow, C extends ItemController<R>> EditItem.ShowAndWaitResult<R> showAndWait(Class<? extends C> contentClass,
            R target) {
        return showAndWait(contentClass, target, null);
    }
    
    public static <R extends model.db.DataRow, C extends ItemController<R>> EditItem.ShowAndWaitResult<R> showAndWait(Class<? extends C> contentClass,
            R target, EditItem<?> parent) {
        return showAndWait(contentClass, target, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE, parent);
    }
    
    public static <R extends model.db.DataRow, C extends ItemController<R>> EditItem.ShowAndWaitResult<R> showAndWait(Class<? extends C> contentClass,
            R target, double width, double height) {
        return showAndWait(contentClass, target, width, height, null);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    public static <R extends model.db.DataRow, C extends ItemController<R>> EditItem.ShowAndWaitResult<R> showAndWait(Class<? extends C> contentClass,
            R target, double width, double height, EditItem<?> parent) {
        scheduler.App app = scheduler.App.CURRENT.get();
        ResourceBundle editItemRb = null;
        final EditItem<R> editItem;
        Parent fxmlParent;
        FXMLLoader loader;
        try {
            editItemRb = ResourceBundle.getBundle(getGlobalizationResourceName(EditItem.class), Locale.getDefault(Locale.Category.DISPLAY));
            loader = new FXMLLoader(EditItem.class.getResource(getFXMLResourceName(EditItem.class)), editItemRb);
            fxmlParent = loader.load();
            editItem = loader.getController();
            editItem.stage = new Stage();
            editItem.target.set(target);
            if (width <= 0.0)
                width = editItem.parentVBox.getWidth();
            if (height <= 0.0)
                height = editItem.parentVBox.getHeight();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            if (width <= 0.0)
                width = Math.ceil(primaryScreenBounds.getWidth() * 0.75);
            else if (width > primaryScreenBounds.getWidth())
                width = primaryScreenBounds.getWidth();
            if (height <= 0.0)
                height = Math.ceil(primaryScreenBounds.getHeight() * 0.75);
            else if (height > primaryScreenBounds.getHeight())
                height = primaryScreenBounds.getHeight();
            editItem.stage.setScene(new Scene(fxmlParent, width, height));
            editItem.stage.setTitle(editItem.getWindowTitle());
        } catch (Exception ex) {
            if (editItemRb != null)
                Alerts.showErrorAlert(editItemRb.getString(RESOURCEKEY_LOADERRORTITLE), editItemRb.getString(RESOURCEKEY_LOADERRORMESSAGE));
            Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error loading view and controller for %s", EditItem.class.getName()), ex);
            ShowAndWaitResult<R> resultObj = new ShowAndWaitResult<>();
            resultObj.fault.set(ex);
            return resultObj;
        }
        try {
            ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(contentClass), Locale.getDefault(Locale.Category.DISPLAY));
            loader = new FXMLLoader(contentClass.getResource(getFXMLResourceName(contentClass)), rb);
            editItem.contentBorderPane.setCenter(loader.load());
            editItem.contentController = loader.getController();
            if (editItem.result.target.isNewRow().get()) {
                collapseNode(editItem.createdLabel);
                collapseNode(editItem.createDateValue);
                collapseNode(editItem.createdByLabel);
                collapseNode(editItem.createdByValue);
                collapseNode(editItem.lastUpdateLabel);
                collapseNode(editItem.lastUpdateValue);
                collapseNode(editItem.lastUpdateByLabel);
                collapseNode(editItem.lastUpdateByValue);
                collapseNode(editItem.deleteButton);
            } else {
                R row = editItem.target.get();
                restoreNode(editItem.createdByLabel);
                restoreLabeled(editItem.createdByValue, row.getCreatedBy());
                DateTimeFormatter formatter = scheduler.App.CURRENT.get().getFullDateTimeFormatter();
                restoreNode(editItem.createdLabel);
                restoreLabeled(editItem.createDateValue, formatter.format(row.getCreateDate()));
                restoreNode(editItem.lastUpdateByLabel);
                restoreLabeled(editItem.lastUpdateByValue, row.getLastUpdateBy());
                restoreNode(editItem.lastUpdateLabel);
                restoreLabeled(editItem.lastUpdateValue, formatter.format(row.getLastUpdate()));
                restoreNode(editItem.deleteButton);
            }
            editItem.contentController.accept(editItem);
            editItem.stage.initOwner((parent == null) ? app.getPrimaryStage(): parent.stage);
            editItem.stage.initModality(Modality.APPLICATION_MODAL);
            editItem.contentController.validProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                editItem.saveChangesButton.setDisable(!newValue);
            });
            editItem.stage.showAndWait();
        } catch (Exception ex) {
            Util.showErrorAlert(editItemRb.getString(RESOURCEKEY_LOADERRORTITLE), editItemRb.getString(RESOURCEKEY_LOADERRORMESSAGE));
            if (contentClass == null)
                Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error opening %s as a child window",contentClass.getName()), ex);
            editItem.result.fault.set(ex);
            if (editItem.contentController != null)
                editItem.contentController.onError(editItem.result);
            return editItem.result;
        }
        
        editItem.contentController.afterCloseDialog(editItem.result);
        return editItem.result;
    }
    
    //</editor-fold>
    
    public static class ShowAndWaitResult<R extends model.db.DataRow> {

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
        
        private final ReadOnlyDataRowProperty<R> target;

        public R getTarget() { return target.get(); }

        public ReadOnlyObjectProperty<R> targetProperty() { return target.getReadOnlyProperty(); }
        
        private ShowAndWaitResult() {
            successful = new ReadOnlyBooleanWrapper(false);
            canceled = new ReadOnlyBooleanWrapper(false);
            fault = new ReadOnlyObjectWrapper<>();
            deleteOperation = new ReadOnlyBooleanWrapper(false);
            target = new ReadOnlyDataRowProperty<>();
        }
    }
        
    public BooleanBinding isNewRow() { return target.isNewRow(); }

    public BooleanBinding isModified() { return target.isModified(); }

    public BooleanBinding isDeleted() { return target.isDeleted(); }
    
}
