/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import scheduler.Util;

/**
 *
 * @author Leonard T. Erwine
 * @param <R>
 *          The type of object being presented in the listing.
 */
public abstract class ListingController<R extends model.db.DataRow> extends SchedulerController {
    //<editor-fold defaultstate="collapsed" desc="itemsList">
    
    private final ObservableList<R> itemsList = FXCollections.observableArrayList();
    /**
     * Gets the {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     * @return The {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     */
    protected ObservableList<R> getItemsList() { return itemsList; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
    //<editor-fold defaultstate="collapsed" desc="listingTableView">
    
    @FXML // fx:id="listingTableView"
    protected TableView<R> listingTableView; // Value injected by FXMLLoader
    
    //</editor-fold>
    
    @FXML
    protected MenuItem editMenuItem;
    
    @FXML
    protected MenuItem deleteMenuItem;
    
    @FXML
    protected Button newButton;
    
    //</editor-fold>
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert listingTableView != null : String.format("fx:id=\"listingTableView\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert editMenuItem != null : String.format("fx:id=\"editMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert deleteMenuItem != null : String.format("fx:id=\"deleteMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newButton != null : String.format("fx:id=\"newButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        listingTableView.setItems(itemsList);
    }
    
    @FXML
    protected void deleteMenuItemClick(ActionEvent event) {
        R item = listingTableView.getSelectionModel().getSelectedItem();
        if (item == null) {
            ResourceBundle rb = scheduler.App.CURRENT.get().getResources();
            Util.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
        }
        else
            verifyDeleteItem(item);
    }

    @FXML
    protected void editMenuItemClick(ActionEvent event) {
        R item = listingTableView.getSelectionModel().getSelectedItem();
        if (item == null) {
            ResourceBundle rb = scheduler.App.CURRENT.get().getResources();
            Util.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
        }
        else
            onEditItem(item);
    }

    @FXML
    protected void listingTableViewKeyTyped(KeyEvent event) {
        if (event.isAltDown() || event.isShortcutDown())
            return;
        if (event.isMetaDown() || event.isControlDown()) {
            if (event.getCode() == KeyCode.N)
                onAddNewItem();
            return;
        }
        if (event.isShiftDown())
            return;
        R item = listingTableView.getSelectionModel().getSelectedItem();
        if (item == null)
            return;
        if (event.getCode() == KeyCode.DELETE)
            verifyDeleteItem(item);
        else if (event.getCode() == KeyCode.ENTER)
            onEditItem(item);
    }

    @FXML
    protected void newButtonClick(ActionEvent event) { onAddNewItem(); }

    private void verifyDeleteItem(R item) {
        ResourceBundle rb = scheduler.App.CURRENT.get().getResources();
        Optional<ButtonType> response = Util.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_CONFIRMDELETE), rb.getString(scheduler.App.RESOURCEKEY_AREYOURSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES)
            onDeleteItem(item);
    }

    protected abstract void onAddNewItem();

    protected abstract void onEditItem(R item);

    protected abstract void onDeleteItem(R item);
    
    protected static <C extends ListingController> void setAsRootContent(Class<? extends C> ctlClass) {
        setAsRootContent(ctlClass, null);
    }
    
    protected static <C extends ListingController> void setAsRootContent(Class<? extends C> ctlClass, Consumer<ContentChangeContext<C>> onBeforeSetContent) {
        setAsRootContent(ctlClass, onBeforeSetContent, null);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    protected static <C extends ListingController> void setAsRootContent(Class<? extends C> ctlClass, Consumer<ContentChangeContext<C>> onBeforeSetContent,
                Consumer<ContentChangeContext<C>> onAfterSetScene) {
        ContentChangeContextFactory<C> context = new ContentChangeContextFactory<>();
        try {
            scheduler.App app = scheduler.App.CURRENT.get();
            ResourceBundle rb = ResourceBundle.getBundle(getGlobalizationResourceName(ctlClass), Locale.getDefault(Locale.Category.DISPLAY));
            context.setResourceBundle(rb);
            FXMLLoader loader = new FXMLLoader(ctlClass.getResource(getFXMLResourceName(ctlClass)), rb);
            Parent content = loader.load();
            context.setParent(content);
            context.setController(loader.getController());
            if (onBeforeSetContent != null)
                onBeforeSetContent.accept(context.get());
           view.RootController.getCurrent().setContent(content, context.get().getController());
        } catch (Exception ex) {
            if (ctlClass == null)
                Logger.getLogger(ListingController.class.getName()).log(Level.SEVERE, null, ex);
            else
                Logger.getLogger(ListingController.class.getName()).log(Level.SEVERE,
                        String.format("Unexpected error setting %s as root content", ctlClass.getName()), ex);
            context.setError(ex);
        }
        if (onAfterSetScene != null)
            onAfterSetScene.accept(context.get());
    }
}
