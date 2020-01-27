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
import scheduler.expressions.ReadOnlyModelProperty;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import scheduler.util.DbConnector;
import scheduler.util.Alerts;
import static scheduler.view.SchedulerController.collapseNode;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import sun.reflect.misc.ReflectUtil;

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
    
    private ViewManager childViewManager;
    
    public ViewManager getChildViewManager() { return childViewManager; }
    
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
            getViewManager().closeWindow();
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
            getViewManager().closeWindow();
        });
        Objects.requireNonNull(cancelButton, String.format("fx:id=\"cancelButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            result.successful.set(true);
            result.canceled.set(true);
            getViewManager().closeWindow();
        });
        childViewManager = new ViewManager() {
            @Override
            public Parent getContent() { return (Parent)contentBorderPane.getCenter(); }
            @Override
            public void setContent(Parent content) {
                contentBorderPane.getChildren().clear();
                contentBorderPane.setCenter(content);
            }
            @Override
            public Parent getRoot() { return getViewManager().getRoot(); }
            @Override
            public void setRoot(Parent content) { getViewManager().setRoot(content); }
            @Override
            public String getWindowTitle() { return getViewManager().getWindowTitle(); }
            @Override
            public void setWindowTitle(String text) { getViewManager().setWindowTitle(text); }
            @Override
            public void closeWindow() { getViewManager().closeWindow(); }
            @Override
            public Pair<Stage, ViewManager> newChild(Modality modality) { return getViewManager().newChild(modality); }
            @Override
            public void setContent(Parent content, double width, double height) {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
    
    //<editor-fold defaultstate="collapsed" desc="showAndWait overloads">

    public static <M extends ItemModel<?>> EditItem.ShowAndWaitResult<M> showAndWait(ViewManager parentViewManager,
            Class<? extends ItemController<M>> contentClass, M target) {
        return showAndWait(parentViewManager, contentClass, target, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    public static <M extends ItemModel<?>, C extends ItemController<M>> EditItem.ShowAndWaitResult<M> showAndWait(ViewManager parentViewManager,
            Class<C> contentClass, M target, final double width, final double height) {
        final EditItem<M> editItem;
        FXMLLoader loader;
        Pair<Stage, ViewManager> stageAndManager;
        try {
            stageAndManager = parentViewManager.newChild(Modality.APPLICATION_MODAL);
            editItem = EditItem.setView(EditItem.class, stageAndManager.getValue(), new ViewControllerFactory<EditItem>() {
                @Override
                public Dimension2D getDimensions(EditItem controller, Parent view) {
                    double w =  (width <= 0.0) ? controller.parentVBox.getWidth() : width;
                    double h = (height <= 0.0) ? controller.parentVBox.getHeight() : height;
                    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                    
                    if (w <= 0.0)
                        w = Math.ceil(primaryScreenBounds.getWidth() * 0.75);
                    else if (w > primaryScreenBounds.getWidth())
                        w = primaryScreenBounds.getWidth();
                    if (h <= 0.0)
                        return new Dimension2D(w, Math.ceil(primaryScreenBounds.getHeight() * 0.75));
                    if (h > primaryScreenBounds.getHeight())
                        return new Dimension2D(w, primaryScreenBounds.getHeight());
                    return new Dimension2D(w, h);
                }
                
                @Override
                public void onLoaded(EditItem newController, Parent newView, SchedulerController currentController, Parent currentView) {
                    newController.target = target;
                }

                @Override
                public void onApplied(EditItem currentController, Parent currentView, SchedulerController oldController, Parent oldView) {
                    try {
                        currentController.contentController =  setView(contentClass, currentController.getChildViewManager(),
                                new ViewControllerFactory<C>() {
                            @Override
                            public void beforeLoad(FXMLLoader loader) {
                                ViewControllerFactory.super.beforeLoad(loader); //To change body of generated methods, choose Tools | Templates.
                            }

                            @Override
                            public void onLoaded(C newController, Parent newView, SchedulerController currentController, Parent currentView) {
                                ViewControllerFactory.super.onLoaded(newController, newView, currentController, currentView); //To change body of generated methods, choose Tools | Templates.
                            }

                            @Override
                            public void onApplied(C currentController, Parent currentView, SchedulerController oldController, Parent oldView) {
                                ViewControllerFactory.super.onApplied(currentController, currentView, oldController, oldView); //To change body of generated methods, choose Tools | Templates.
                            }

                            @Override
                            public C call(Class<C> param) {
                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            }
                            
                        });
                        if (currentController.result.target.isNewRow().get()) {
                            collapseNode(currentController.createdLabel);
                            collapseNode(currentController.createDateValue);
                            collapseNode(currentController.createdByLabel);
                            collapseNode(currentController.createdByValue);
                            collapseNode(currentController.lastUpdateLabel);
                            collapseNode(currentController.lastUpdateValue);
                            collapseNode(currentController.lastUpdateByLabel);
                            collapseNode(currentController.lastUpdateByValue);
                            collapseNode(currentController.deleteButton);
                        } else {
                            M row = (M)currentController.target;
                            restoreNode(currentController.createdByLabel);
                            restoreLabeled(currentController.createdByValue, row.getCreatedBy());
                            DateTimeFormatter formatter = scheduler.App.getCurrent().getFullDateTimeFormatter();
                            restoreNode(currentController.createdLabel);
                            restoreLabeled(currentController.createDateValue, formatter.format(row.getCreateDate()));
                            restoreNode(currentController.lastUpdateByLabel);
                            restoreLabeled(currentController.lastUpdateByValue, row.getLastModifiedBy());
                            restoreNode(currentController.lastUpdateLabel);
                            restoreLabeled(currentController.lastUpdateValue, formatter.format(row.getLastModifiedDate()));
                            restoreNode(currentController.deleteButton);
                        }
                        currentController.contentController.accept(currentController);
                        currentController.contentController.validProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                            currentController.saveChangesButton.setDisable(!newValue);
                        });
                    } catch (Exception ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                
                @Override
                public EditItem call(Class<EditItem> param) {
                    try {
                        return (EditItem)ReflectUtil.newInstance(param);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(ViewControllerFactory.class.getName()).log(Level.SEVERE, "Error instantiating controller", ex);
                        throw new RuntimeException("Error instantiating controller", ex);
                    }
                }
                
            });
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, String.format("Unexpected error loading view and controller for %s", EditItem.class.getName()), ex);
            ShowAndWaitResult<M> resultObj = new ShowAndWaitResult<>();
            resultObj.fault.set(ex);
            return resultObj;
        }
        
        stageAndManager.getKey().showAndWait();
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
    
}
