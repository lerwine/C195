package view;

import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.db.DataRow;
import static view.Controller.getFXMLResourceName;
import static view.Controller.getGlobalizationResourceName;

/**
 * The base class for controllers of a JavaFX {@link javafx.scene.Parent} node that is displayed in a child window
 * for editing a single {@link model.db.DataRow}.
 * 
 * @author Leonard T. Erwine
 * @param <T>
 *            The type of {@link model.db.DataRow} that is being edited.
 */
public abstract class EditItemController<T extends DataRow> extends Controller {
    //<editor-fold defaultstate="collapsed" desc="Fields">
    
    private Runnable closeWindow;
    
    private Stage currentStage;
    
    //<editor-fold defaultstate="collapsed" desc="model property">
    
    private final SimpleObjectProperty<T> model;
    
    /**
     * Gets the model that is being edited.
     * @return
     *            The model that is being edited.
     */
    protected final T getModel() { return model.get(); }
    
    /**
     * Sets the model that to be edited.
     * @param value
     *            The model to be edited.
     */
    protected final void setModel(T value) { model.set(value); }
    
    /**
     * Gets the JavaFX property that holds the model being edited.
     * @return
     *            The JavaFX property that holds the model being edited.
     */
    protected final SimpleObjectProperty<T> modelProperty() { return model; }
    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="newRow property">
    
    private final BooleanBinding newRow;
    
    /**
     * Indicates whether the current {@link #modelProperty()} represents a new data row.
     * @return
     *            {@code true} if the current {@link #modelProperty()} represents a new data row; otherwise, {@code false}.
     */
    public final boolean isNewRow() { return newRow.get(); }
    
    /**
     * Gets the JavaFX property that indicates whether the current {@link #modelProperty()} represents a new data row.
     * @return
     *            A JavaFX property returns {@code true} if the current {@link #modelProperty()} represents a new data row;
     *            otherwise, it will return {@code false}.
     */
    public final BooleanBinding newRowProperty() { return newRow; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="canceled property">
    
    private final ReadOnlyBooleanWrapper canceled;
    
    /**
     * Indicates whether the changes to the current {@link #modelProperty()} are unsaved.
     * This property is set to true after a successful save, and just before the window is closed.
     * @return
     *            {@code true} if the changes to the current {@link #modelProperty()} have not been saved; otherwise, {@code false}.
     */
    public final boolean isCanceled() { return canceled.get(); }
    
    /**
     * Gets the JavaFX property that indicates whether the changes to the current {@link #modelProperty()} are unsaved.
     * @return
     *            A JavaFX property returns {@code true} if the changes to the current {@link #modelProperty()} have not been saved;
     *            otherwise, it will return {@code false} to indicate changes were successfully saved.
     */
    public final ReadOnlyBooleanProperty canceledProperty() { return canceled.getReadOnlyProperty(); }
    
    protected final void setCanceled(boolean value) { canceled.set(value); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="abstract valid property">
    
    /**
     * Indicates whether all input fields are valid.
     * @return
     *          {@code true} if all fields are valid; otherwise, {@code false}.
     */
    public abstract boolean isValid();
    
    /**
     * Indicates whether all input fields are valid.
     * @return
     *          An expression that produces a {@code true} value if all fields are valid; otherwise, {@code false}.
     */
    public abstract BooleanExpression validProperty();
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
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
    protected Button saveChangesButton;
    
    @FXML
    protected Button cancelButton;
    
    //</editor-fold>
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
    /**
     * Creates new ItemController instance.
     */
    public EditItemController() {
        model = new ReadOnlyObjectWrapper<>();
        newRow = new BooleanBinding() {
            { super.bind(model); }
            
            @Override
            protected boolean computeValue() {
                T m = model.get();
                return m == null || m.getRowState() == DataRow.ROWSTATE_NEW;
            }
            
            @Override
            public ObservableList<?> getDependencies() { return javafx.collections.FXCollections.singletonObservableList(model); }
            
            @Override
            public void dispose() {
                super.unbind(model);
                super.dispose();
            }
        };
        canceled = new ReadOnlyBooleanWrapper(true);
        model.addListener((ObservableValue<? extends T> observable, T oldValue, T newValue) -> {
            DateTimeFormatter dtf = scheduler.App.CURRENT.get().getFullDateTimeFormatter();
            createDateValue.setText(dtf.format(newValue.getCreateDate()));
            lastUpdateValue.setText(dtf.format(newValue.getLastUpdate()));
            createdByValue.setText(newValue.getCreatedBy());
            lastUpdateByValue.setText(newValue.getLastUpdateBy());
        });
        newRow.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                collapseNode(createdLabel);
                collapseNode(createDateValue);
                collapseNode(createdByLabel);
                collapseNode(createdByValue);
                collapseNode(lastUpdateLabel);
                collapseNode(lastUpdateValue);
                collapseNode(lastUpdateByLabel);
                collapseNode(lastUpdateByValue);
            } else {
                restoreNode(createdLabel);
                restoreNode(createDateValue);
                restoreNode(createdByLabel);
                restoreNode(createdByValue);
                restoreNode(lastUpdateLabel);
                restoreNode(lastUpdateValue);
                restoreNode(lastUpdateByLabel);
                restoreNode(lastUpdateByValue);
            }
        });
    }
    
    /**
     * This method is called by the FXMLLoader when initialization is complete
     */
    @FXML
    protected void initialize() {
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
        assert saveChangesButton != null : String.format("fx:id=\"saveChangesButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert cancelButton != null : String.format("fx:id=\"cancelButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        saveChangesButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Event handler methods">

    /**
     * This gets invoked when changes are to be saved.
     * @param event
     *          Describes the current event.
     */
    @FXML
    protected final void saveChangesClick(ActionEvent event) {
        //saveChanges();
        canceled.set(false);
        //closeWindow.run();
    }
    
    /**
     * This gets invoked when changes are to be saved.
     * @return
     *      {@code true} if changes where successfully saved; otherwise {@code false}.
     */
    protected abstract boolean saveChanges();
    
    /**
     * This gets invoked when changes are to be discarded.
     * @param event
     *          Describes the current event.
     */
    @FXML
    protected final void cancelClick(ActionEvent event) {
//        if (canCancel()) {
//            canceled.set(true);
//            closeWindow.run();
//        }
    }
    
    /**
     * This gets invoked to determine if it is okay for the changes to be discarded (usually by prompting the user).
     * @return
     *      {@code true} if changes can be discarded; otherwise {@code false}.
     */
    protected boolean canCancel() { return true; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="showAndWait overloads">

    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param <R>
     *      The type of result to be returned
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param beforeShow
     *          The delegate method to invoke before the new window is shown.
     * @param getReturnValue
     *          The delegate method which will produce the return value.
     * @return
     *          The result value.
     */
    @SuppressWarnings("UseSpecificCatch")
    protected final static <C extends EditItemController<?>, R> R showAndWait(Class<? extends C> ctlClass, double width, double height,
            Consumer<ContentChangeContext<C>> beforeShow, Function<ContentChangeContext<C>, R> getReturnValue) {
        return showAndWait(ctlClass, width, height, null, beforeShow, getReturnValue);
    }
    
    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param <R>
     *      The type of result to be returned
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param parent
     *          The parent controller which is associated with the stage that will be the parent of the new window.
     * @param beforeShow
     *          The delegate method to invoke before the new window is shown.
     * @param getReturnValue
     *          The delegate method which will produce the return value.
     * @return
     *          The result value.
     */
    @SuppressWarnings("UseSpecificCatch")
    protected final static <C extends EditItemController<?>, R> R showAndWait(Class<? extends C> ctlClass, double width, double height,
            EditItemController<?> parent, Consumer<ContentChangeContext<C>> beforeShow, Function<ContentChangeContext<C>, R> getReturnValue) {
        ContentChangeContextFactory<C> context = new ContentChangeContextFactory<>();
        try {
            scheduler.App app = scheduler.App.CURRENT.get();
            Stage stage = new Stage();
            ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), app.getCurrentLocale());
            context.setResourceBundle(rb);
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)), rb);
            Parent p = loader.load();
            context.setParent(p);
            stage.setScene(new Scene(p, width, height));
            C controller = loader.getController();
            ((EditItemController<?>)controller).currentStage = stage;
            ((EditItemController<?>)controller).closeWindow = () -> stage.hide();
            controller.validProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                ((EditItemController<?>)controller).saveChangesButton.setDisable(!newValue);
            });
            context.setController(controller);
            if (beforeShow != null)
                beforeShow.accept(context.get());
            stage.initOwner((parent == null) ? app.getPrimaryStage(): parent.currentStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            String title = context.get().getWindowTitle();
            stage.setTitle((title.isEmpty()) ? scheduler.App.CURRENT.get()
                    .getResources().getString(scheduler.App.RESOURCEKEY_APPOINTMENTSCHEDULER) : title);
            stage.showAndWait();
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(EditItemController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(EditItemController.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error opening %s as a child window", ctlClass.getName()), ex);
            context.setError(ex);
        }
        return getReturnValue.apply(context.get());
    }
    
    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param <R>
     *      The type of result to be returned
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param getReturnValue
     *          The delegate method which will produce the return value.
     * @return
     *          The result value.
     */
    protected final static <C extends EditItemController<?>, R> R showAndWait(Class<? extends C> ctlClass, double width, double height,
            Function<ContentChangeContext<C>, R> getReturnValue) {
        return showAndWait(ctlClass, width, height, null, null, getReturnValue);
    }
    
    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param <R>
     *      The type of result to be returned
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param parent
     *          The parent controller which is associated with the stage that will be the parent of the new window.
     * @param getReturnValue
     *          The delegate method which will produce the return value.
     * @return
     *          The result value.
     */
    protected final static <C extends EditItemController<?>, R> R showAndWait(Class<? extends C> ctlClass, double width, double height,
            EditItemController<?> parent, Function<ContentChangeContext<C>, R> getReturnValue) {
        return showAndWait(ctlClass, width, height, parent, null, getReturnValue);
    }
    
    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param beforeShow
     *          The delegate method to invoke before the new window is shown.
     * @param afterClose
     *          The delegate method to invoke after the new window is hidden.
     */
    protected final static <C extends EditItemController<?>> void showAndWait(Class<? extends C> ctlClass, double width, double height,
            Consumer<ContentChangeContext<C>> beforeShow, Consumer<ContentChangeContext<C>> afterClose) {
        showAndWait(ctlClass, width, height, null, beforeShow, afterClose);
    }
    
    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param parent
     *          The parent controller which is associated with the stage that will be the parent of the new window.
     * @param beforeShow
     *          The delegate method to invoke before the new window is shown.
     * @param afterClose
     *          The delegate method to invoke after the new window is hidden.
     */
    @SuppressWarnings("UseSpecificCatch")
    protected final static <C extends EditItemController<?>> void showAndWait(Class<? extends C> ctlClass, double width, double height,
            EditItemController<?> parent, Consumer<ContentChangeContext<C>> beforeShow, Consumer<ContentChangeContext<C>> afterClose) {
        ContentChangeContextFactory<C> context = new ContentChangeContextFactory<>();
        try {
            scheduler.App app = scheduler.App.CURRENT.get();
            Stage stage = app.getPrimaryStage();
            ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), app.getCurrentLocale());
            context.setResourceBundle(rb);
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)), rb);
            Parent p = loader.load();
            context.setParent(p);
            stage.setScene(new Scene(p, width, height));
            C controller = loader.getController();
            ((EditItemController<?>)controller).currentStage = stage;
            ((EditItemController<?>)controller).closeWindow = () -> stage.hide();
            context.setController(controller);
            if (beforeShow != null)
                beforeShow.accept(context.get());
            stage.initOwner((parent == null) ? app.getPrimaryStage(): parent.currentStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            String title = context.get().getWindowTitle();
            stage.setTitle((title.isEmpty()) ? scheduler.App.CURRENT.get()
                    .getResources().getString(scheduler.App.RESOURCEKEY_APPOINTMENTSCHEDULER) : title);
            stage.showAndWait();
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(EditItemController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(EditItemController.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error opening %s as a child window", ctlClass.getName()), ex);
            context.setError(ex);
        }
        if (afterClose != null)
            afterClose.accept(context.get());
    }
    
    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param beforeShow
     *          The delegate method to invoke before the new window is shown.
     */
    protected final static <C extends EditItemController<?>> void showAndWait(Class<? extends C> ctlClass, double width, double height,
            Consumer<ContentChangeContext<C>> beforeShow) {
        showAndWait(ctlClass, width, height, beforeShow, (Consumer<ContentChangeContext<C>>)null);
    }
    
    /**
     * Displays a new modal window.
     * @param <C>
     *      The type of controller class that will be created.
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param parent
     *          The parent controller which is associated with the stage that will be the parent of the new window.
     * @param beforeShow
     *          The delegate method to invoke before the new window is shown.
     */
    protected final static <C extends EditItemController<?>> void showAndWait(Class<? extends C> ctlClass, double width, double height,
            EditItemController<?> parent, Consumer<ContentChangeContext<C>> beforeShow) {
        showAndWait(ctlClass, width, height, parent, beforeShow, (Consumer<ContentChangeContext<C>>)null);
    }
    
    /**
     * Displays a new modal window.
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     */
    @SuppressWarnings("UseSpecificCatch")
    protected final static void showAndWait(Class<? extends EditItemController<?>> ctlClass, double width, double height) {
        showAndWait(ctlClass, width, height, (EditItemController<?>)null);
    }
    
    /**
     * Displays a new modal window.
     * @param ctlClass
     *          The class of the controller that will be used.
     * @param width
     *          The width of the new window.
     * @param height
     *          The height of the new window.
     * @param parent
     *          The parent controller which is associated with the stage that will be the parent of the new window.
     */
    @SuppressWarnings("UseSpecificCatch")
    protected final static void showAndWait(Class<? extends EditItemController<?>> ctlClass, double width, double height, EditItemController<?> parent) {
        try {
            scheduler.App app = scheduler.App.CURRENT.get();
            Stage stage = app.getPrimaryStage();
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)),
                    ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), app.getCurrentLocale()));
            stage.setScene(new Scene(loader.load(), width, height));
            EditItemController<?> controller = loader.getController();
            controller.currentStage = stage;
            controller.closeWindow = () -> stage.hide();
            stage.initOwner((parent == null) ? app.getPrimaryStage() : parent.currentStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(scheduler.App.CURRENT.get().getResources().getString(scheduler.App.RESOURCEKEY_APPOINTMENTSCHEDULER));
            stage.showAndWait();
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(EditItemController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(EditItemController.class.getName()).log(Level.SEVERE,
                    String.format("Unexpected error opening %s as a child window", ctlClass.getName()), ex);
        }
    }

    //</editor-fold>
}
