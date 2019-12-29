package scene;

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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.db.DataRow;
import static scene.Controller.getFXMLResourceName;
import static scene.Controller.getGlobalizationResourceName;
import scheduler.Util;

/**
 * A controller for an JavaFX scene that is displayed as a child window for editing a single item.
 * @author Leonard T. Erwine
 * @param <T>
 *            The type of model that is being edited.
 */
public abstract class ItemController<T extends DataRow> extends Controller {
    private Runnable closeWindow;
    
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
    
    @FXML
    private Label createdLabel;

    @FXML
    private Label createDateValue;

    @FXML
    private Label createdByLabel;

    @FXML
    private Label createdByValue;

    @FXML
    private Label lastUpdateLabel;

    @FXML
    private Label lastUpdateValue;

    @FXML
    private Label lastUpdateByLabel;

    @FXML
    private Label lastUpdateByValue;

    @FXML
    private Button saveChangesButton;

    /**
     * Creates new ItemController instance.
     */
    public ItemController() {
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
            DateTimeFormatter dtf = scheduler.App.getCurrent().getFullDateTimeFormatter();
            createDateValue.setText(dtf.format(newValue.getCreateDate()));
            lastUpdateValue.setText(dtf.format(newValue.getLastUpdate()));
            createdByValue.setText(newValue.getCreatedBy());
            lastUpdateByValue.setText(newValue.getLastUpdateBy());
        });
        newRow.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                Util.collapseControlVertical(createdLabel);
                Util.collapseControlVertical(createDateValue);
                Util.collapseControlVertical(createdByLabel);
                Util.collapseControlVertical(createdByValue);
                Util.collapseControlVertical(lastUpdateLabel);
                Util.collapseControlVertical(lastUpdateValue);
                Util.collapseControlVertical(lastUpdateByLabel);
                Util.collapseControlVertical(lastUpdateByValue);
            } else {
                Util.restoreControlVertical(createdLabel);
                Util.restoreControlVertical(createDateValue);
                Util.restoreControlVertical(createdByLabel);
                Util.restoreControlVertical(createdByValue);
                Util.restoreControlVertical(lastUpdateLabel);
                Util.restoreControlVertical(lastUpdateValue);
                Util.restoreControlVertical(lastUpdateByLabel);
                Util.restoreControlVertical(lastUpdateByValue);
            }
        });
    }
    
    public abstract boolean isValid();
    
    public abstract BooleanExpression validProperty();
    
    @FXML
    void saveChangesClick(ActionEvent event) {
        saveChanges();
        canceled.set(false);
        closeWindow.run();
    }
    
    protected abstract boolean saveChanges();
    
    @FXML
    void cancelClick(ActionEvent event) {
        if (canCancel()) {
            canceled.set(true);
            closeWindow.run();
        }
    }
    
    protected boolean canCancel() { return true; }
    
    @SuppressWarnings("UseSpecificCatch")
    protected final static <C extends ItemController<?>, R> R showAndWait(Class<? extends C> ctlClass, double width, double height,
            Consumer<SetContentContext<C>> beforeShow, Function<SetContentContext<C>, R> getReturnValue) {
        SetStageContextRW<C> context = new SetStageContextRW<>();
        try {
            scheduler.App app = scheduler.App.getCurrent();
            Stage stage = new Stage();
            context.setStage(app.getRootStage());
            ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), app.getCurrentLocale());
            context.setResourceBundle(rb);
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)), rb);
            stage.setScene(new Scene(loader.load(), width, height));
            C controller = loader.getController();
            ((ItemController<?>)controller).closeWindow = () -> stage.hide();
            controller.validProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                ((ItemController<?>)controller).saveChangesButton.setDisable(!newValue);
            });
            context.setController(controller);
            if (beforeShow != null)
                beforeShow.accept(context.getContext());
            stage.showAndWait();
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error opening %s as a child window", ctlClass.getName()), ex);
            context.setError(ex);
        }
        return getReturnValue.apply(context.getContext());
    }
    
    protected final static <C extends ItemController<?>, R> R showAndWait(Class<? extends C> ctlClass, double width, double height,
            Function<SetContentContext<C>, R> getReturnValue) {
        return showAndWait(ctlClass, width, height, null, getReturnValue);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    protected final static <C extends ItemController<?>> void showAndWait(Class<? extends C> ctlClass, double width, double height,
            Consumer<SetContentContext<C>> beforeShow, Consumer<SetContentContext<C>> afterClose) {
        SetStageContextRW<C> context = new SetStageContextRW<>();
        try {
            scheduler.App app = scheduler.App.getCurrent();
            Stage stage = app.getRootStage();
            context.setStage(app.getRootStage());
            ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), app.getCurrentLocale());
            context.setResourceBundle(rb);
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)), rb);
            stage.setScene(new Scene(loader.load(), width, height));
            C controller = loader.getController();
            ((ItemController<?>)controller).closeWindow = () -> stage.hide();
            context.setController(controller);
            if (beforeShow != null)
                beforeShow.accept(context.getContext());
            stage.showAndWait();
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error opening %s as a child window", ctlClass.getName()), ex);
            context.setError(ex);
        }
        if (afterClose != null)
            afterClose.accept(context.getContext());
    }
    
    protected final static <C extends ItemController<?>> void showAndWait(Class<? extends C> ctlClass, double width, double height,
            Consumer<SetContentContext<C>> beforeShow) {
        showAndWait(ctlClass, width, height, beforeShow, (Consumer<SetContentContext<C>>)null);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    protected final static void showAndWait(Class<? extends ItemController<?>> ctlClass, double width, double height) {
        try {
            scheduler.App app = scheduler.App.getCurrent();
            Stage stage = app.getRootStage();
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)),
                    ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), app.getCurrentLocale()));
            stage.setScene(new Scene(loader.load(), width, height));
            ((ItemController<?>)loader.getController()).closeWindow = () -> stage.hide();
            stage.showAndWait();
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE,
                    String.format("Unexpected error opening %s as a child window", ctlClass.getName()), ex);
        }
    }
}
