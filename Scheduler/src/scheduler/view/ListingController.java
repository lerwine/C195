/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.util.Iterator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import scheduler.filter.ModelFilter;
import scheduler.util.Alerts;
import sun.reflect.misc.ReflectUtil;

/**
 * Base class for controllers that present a {@link TableView} containing {@link ItemModel} objects.
 * @author Leonard T. Erwine
 * @param <M> The type of model objects presented by the ListingController.
 */
public abstract class ListingController<M extends ItemModel<?>> extends MainContentController {
    //<editor-fold defaultstate="collapsed" desc="itemsList">
    
    private ModelFilter<M> itemsFilter = ModelFilter.empty();
    
    /**
     * Gets the {@link ModelFilter} that is used to filter items bound to the {@link #listingTableView}.
     * @return The {@link javafx.collections.ObservableList} that is used to filter items bound to the {@link #listingTableView}.
     */
    protected ModelFilter<M> getItemsFilter() { return itemsFilter; }
    
    protected void setItemsFilter(ModelFilter<M> value) {
        if (value == null) {
            if (itemsFilter.isEmpty())
                return;
            itemsFilter = ModelFilter.empty();
        } else {
            if (value == itemsFilter)
                return;
            itemsFilter = value;
        }
        onItemsFilterChanged();
    }
    
    protected void onItemsFilterChanged() { }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="itemsList">
    
    private final ObservableList<M> itemsList = FXCollections.observableArrayList();
    
    /**
     * Gets the {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     * @return The {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     */
    protected ObservableList<M> getItemsList() { return itemsList; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
    //<editor-fold defaultstate="collapsed" desc="listingTableView">

    /**
     * The {@link TableView} control injected by the {@link FXMLLoader}.
     */
    @FXML
    protected TableView<M> listingTableView;
    
    //</editor-fold>
    
    /**
     * The {@link MenuItem} injected by the {@link FXMLLoader} for editing an {@link ItemModel}.
     * This is defined within the {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     */
    @FXML
    protected MenuItem editMenuItem;
    
    /**
     * The {@link MenuItem} injected by the {@link FXMLLoader} for deleting an {@link ItemModel}.
     * This is defined within the {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     */
    @FXML
    protected MenuItem deleteMenuItem;
    
    /**
     * The {@link Button} control injected by the {@link FXMLLoader} for adding a new {@link ItemModel}.
     */
    @FXML
    protected Button newButton;
    
    //</editor-fold>
    
    private static final Logger LOG = Logger.getLogger(ListingController.class.getName());
    
    /**
     * Called by the {@link FXMLLoader} to complete controller initialization.
     */
    @FXML
    protected void initialize() {
        Objects.requireNonNull(listingTableView, String.format("fx:id=\"listingTableView\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setItems(itemsList);
        listingTableView.setOnKeyTyped((event) -> {
            if (event.isAltDown() || event.isShortcutDown())
                return;
            if (event.isMetaDown() || event.isControlDown()) {
                if (event.getCode() == KeyCode.N)
                    onAddNewItem(event);
                return;
            }
            if (event.isShiftDown())
                return;
            M item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null)
                return;
            if (event.getCode() == KeyCode.DELETE)
                onDeleteItem(event, item);
            else if (event.getCode() == KeyCode.ENTER)
                onEditItem(event, item);
        });
        Objects.requireNonNull(editMenuItem, String.format("fx:id=\"editMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            M item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                ResourceBundle rb = scheduler.App.getCurrent().getResources();
                Alerts.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
            }
            else
                onEditItem(event, item);
        });
        Objects.requireNonNull(deleteMenuItem, String.format("fx:id=\"deleteMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            M item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                ResourceBundle rb = scheduler.App.getCurrent().getResources();
                Alerts.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
            }
            else
                onDeleteItem(event, item);
        });
        Objects.requireNonNull(newButton, String.format("fx:id=\"newButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> onAddNewItem(event));
    }
    
    /**
     * This gets called when the user clicks the {@link #newButton} control or types the {@link KeyCode#N} key while {@link KeyEvent#isMetaDown()}
     * or {@link KeyEvent#isControlDown()}.
     * @param event Contextual information about the event.
     */
    protected abstract void onAddNewItem(Event event);
    
    /**
     * This gets called when the user types the {@link KeyCode#ENTER} key or clicks the {@link #editMenuItem} in the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     * @param event Contextual information about the event.
     * @param item The selected item to be edited.
     */
    protected abstract void onEditItem(Event event, M item);

    /**
     * This gets called when the user types the {@link KeyCode#DELETE} key or clicks the {@link #deleteMenuItem} in the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     * @param event Contextual information about the event.
     * @param item The selected item to be deleted.
     */
    protected abstract void onDeleteItem(Event event, M item);
    
    protected int indexOfListItemByPrimaryKey(int pk) {
        Iterator<M> iterator = getItemsList().iterator();
        int index = -1;
        while (iterator.hasNext()) {
            ++index;
            if (iterator.next().getDataObject().getPrimaryKey() == pk)
                return index;
        }
        return -1;
    }
    
    protected M getListItemByPrimaryKey(int pk) {
        Iterator<M> iterator = getItemsList().iterator();
        while (iterator.hasNext()) {
            M item = iterator.next();
            if (item.getDataObject().getPrimaryKey() == pk)
                return item;
        }
        return null;
    }
    
    protected boolean removeListItemByPrimaryKey(int pk) {
        Iterator<M> iterator = getItemsList().iterator();
        while (iterator.hasNext()) {
            M item = iterator.next();
            if (item.getDataObject().getPrimaryKey() == pk) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    protected boolean updateListItem(M item) {
        Objects.requireNonNull(item);
        int pk = item.getDataObject().getPrimaryKey();
        ObservableList<M> items = getItemsList();
        for (int i = 0; i < items.size(); i++) {
            M m = items.get(i);
            if (m.getDataObject().getPrimaryKey() == pk) {
                if (m != item)
                    items.set(i, item);
                return true;
            }
        }
        return false;
    }
    
    public static <M extends ItemModel<?>, L extends ListingController<M>> ViewControllerFactory<L> createItemsFactory(ModelFilter<M> filter,
            ViewControllerInitializer<L> intializer) {
        return new ViewControllerFactory<L>() {
            @Override
            public void beforeLoad(FXMLLoader loader) {
                if (null != intializer)
                    intializer.beforeLoad(loader);
            }

            @Override
            public void onLoaded(L newController, Parent newView, SchedulerController currentController, Parent currentView) {
                if (null != intializer)
                    intializer.onLoaded(newController, newView, currentController, currentView);
                ((ListingController<M>)newController).itemsFilter = (filter == null) ? ModelFilter.empty() : filter;
            }

            @Override
            public void onApplied(L currentController, Parent currentView, SchedulerController oldController, Parent oldView) {
                if (null != intializer)
                    intializer.onApplied(currentController, currentView, oldController, oldView);
            }

            @Override
            public L call(Class<L> param) {
                try {
                    return (L)ReflectUtil.newInstance(param);
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ViewControllerFactory.class.getName()).log(Level.SEVERE, "Error instantiating controller", ex);
                    throw new RuntimeException("Error instantiating controller", ex);
                }
            }
            
        };
    }
}
