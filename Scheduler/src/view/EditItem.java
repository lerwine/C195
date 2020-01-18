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
import expressions.ReadOnlyModelProperty;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import util.DbConnector;
import util.Alerts;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

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
    
    private Stage stage;
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
    
    private final ReadOnlyModelProperty<M> target;
    
    /**
     * Gets the {@link ItemModel} being edited.
     * @return The {@link ItemModel} being edited.
     */
    public M getTarget() { return target.get(); }
    
    /**
     * Gets the {@link ReadOnlyObjectProperty} containing the {@link ItemModel} being edited.
     * @return The {@link ReadOnlyObjectProperty} containing the {@link ItemModel} being edited.
     */
    public ReadOnlyObjectProperty<M> targetProperty() { return target.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="windowTitle property">
    
    private final StringProperty windowTitle;
    
    /**
     * Gets the window title to be applied to the current {@link #stage}.
     * @return The window title to be applied to the current {@link #stage}.
     */
    public String getWindowTitle() { return windowTitle.get(); }
    
    /**
     * Sets the window title to be applied to the current {@link #stage}.
     * @param value The window title to be applied to the current {@link #stage}.
     */
    public void setWindowTitle(String value) { windowTitle.set((value == null || value.trim().isEmpty()) ? getDefaultWindowTitle() : value); }
    
    /**
     * Gets the {@link StringProperty} that contains the window title to be applied to the current {@link #stage}.
     * @return The {@link StringProperty} that contains the window title to be applied to the current {@link #stage}.
     */
    public StringProperty windowTitleProperty() { return windowTitle; }
    
    private String getDefaultWindowTitle() {
        return scheduler.App.CURRENT.get().getResources().getString(scheduler.App.RESOURCEKEY_APPOINTMENTSCHEDULER);
    }
    
    //</editor-fold>

    /**
     * Initializes a new EditItem controller.
     */
    protected EditItem() {
        result = new ShowAndWaitResult<>();
        stage = null;
        target = new ReadOnlyModelProperty<>();
        windowTitle = new SimpleStringProperty(getDefaultWindowTitle()) {
            @Override
            public void set(String newValue) {
                super.set((newValue == null || (newValue = newValue.trim()).isEmpty()) ? getDefaultWindowTitle() : newValue);
            }
        };
        windowTitle.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (stage != null)
                stage.setTitle(newValue);
        });
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
            if (stage != null)
                stage.hide();
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
            if (stage != null)
                stage.hide();
        });
        Objects.requireNonNull(cancelButton, String.format("fx:id=\"cancelButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            result.successful.set(true);
            result.canceled.set(true);
            if (stage != null)
                stage.hide();
        });
    }
    
    //<editor-fold defaultstate="collapsed" desc="showAndWait overloads">

    public static <M extends ItemModel<?>> EditItem.ShowAndWaitResult<M> showAndWait(Class<? extends ItemController<M>> contentClass, M target) {
        return showAndWait(contentClass, target, null);
    }
    
    public static <M extends ItemModel<?>> EditItem.ShowAndWaitResult<M> showAndWait(Class<? extends ItemController<M>> contentClass, M target,
            EditItem<?> parent) {
        return showAndWait(contentClass, target, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE, parent);
    }
    
    public static <M extends ItemModel<?>> EditItem.ShowAndWaitResult<M> showAndWait(Class<? extends ItemController<M>> contentClass, M target,
            double width, double height) {
        return showAndWait(contentClass, target, width, height, null);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    public static <M extends ItemModel<?>> EditItem.ShowAndWaitResult<M> showAndWait(Class<? extends ItemController<M>> contentClass, M target,
            double width, double height, EditItem<?> parent) {
        scheduler.App app = scheduler.App.CURRENT.get();
        ResourceBundle editItemRb = null;
        final EditItem<M> editItem;
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
            LOG.log(Level.SEVERE,
                        String.format("Unexpected error loading view and controller for %s", EditItem.class.getName()), ex);
            ShowAndWaitResult<M> resultObj = new ShowAndWaitResult<>();
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
                M row = editItem.target.get();
                restoreNode(editItem.createdByLabel);
                restoreLabeled(editItem.createdByValue, row.getCreatedBy());
                DateTimeFormatter formatter = scheduler.App.CURRENT.get().getFullDateTimeFormatter();
                restoreNode(editItem.createdLabel);
                restoreLabeled(editItem.createDateValue, formatter.format(row.getCreateDate()));
                restoreNode(editItem.lastUpdateByLabel);
                restoreLabeled(editItem.lastUpdateByValue, row.getLastModifiedBy());
                restoreNode(editItem.lastUpdateLabel);
                restoreLabeled(editItem.lastUpdateValue, formatter.format(row.getLastModifiedDate()));
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
            Alerts.showErrorAlert(editItemRb.getString(RESOURCEKEY_LOADERRORTITLE), editItemRb.getString(RESOURCEKEY_LOADERRORMESSAGE));
            if (contentClass == null)
                LOG.log(Level.SEVERE, null, ex);
            else
                LOG.log(Level.SEVERE, String.format("Unexpected error opening %s as a child window",contentClass.getName()), ex);
            editItem.result.fault.set(ex);
            if (editItem.contentController != null)
                editItem.contentController.onError(editItem.result);
            return editItem.result;
        }
        
        editItem.contentController.afterCloseDialog(editItem.result);
        return editItem.result;
    }
    
    //</editor-fold>
    
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
        
    public BooleanBinding isNewRow() { return target.isNewRow(); }

    public BooleanBinding isModified() { return target.isModified(); }

    public BooleanBinding isDeleted() { return target.isDeleted(); }
    
}
