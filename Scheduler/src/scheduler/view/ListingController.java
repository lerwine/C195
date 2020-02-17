package scheduler.view;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.App;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.LookupFilter;
import scheduler.util.Alerts;
import scheduler.util.DbConnector;

/**
 * Base class for controllers that present a {@link TableView} containing {@link ItemModel} objects.
 * @author Leonard T. Erwine
 * @param <D>
 * @param <M> The type of model objects presented by the ListingController.
 */
public abstract class ListingController<D extends DataObjectImpl, M extends ItemModel<D>> extends MainController.MainContentController {
    //<editor-fold defaultstate="collapsed" desc="itemsFilter">
    
    private LookupFilter<D, M> filter;
    
    /**
     * Gets the current filter.
     * @return The current filter.
     */
    public LookupFilter<D, M> getFilter() { return filter; }
    
    /**
     * Gets the default filter.
     * @return The default filter.
     */
    protected abstract LookupFilter<D, M> getDefaultFilter();
    
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
                ResourceBundle rb = App.getResources();
                Alerts.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
            }
            else
                onEditItem(event, item);
        });
        Objects.requireNonNull(deleteMenuItem, String.format("fx:id=\"deleteMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            M item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                ResourceBundle rb = App.getResources();
                Alerts.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
            }
            else
                onDeleteItem(event, item);
        });
        Objects.requireNonNull(newButton, String.format("fx:id=\"newButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> onAddNewItem(event));
    }
    
    /**
     * Sets the {@link #filter} and starts a {@link TaskWaiter} if the filter has changed.
     * @param value The new {@link ModelFilter}.
     * @param owner The owner {@link Stage} to use when showing the {@link javafx.stage.Popup} window.
     */
    public void changeFilter(LookupFilter<D, M> value, Stage owner) {
        if (null == value)
            value = getDefaultFilter();
        if (null != filter && value.equals(filter))
            return;
        filter = value;
        onFilterChanged(Objects.requireNonNull(owner));
    }
    
    /**
     * This gets called whenever the current {@link #filter} has changed.
     * @param owner The owner {@link Stage} to use when showing the {@link javafx.stage.Popup} window.
     */
    private void onFilterChanged(Stage owner) {
        TaskWaiter.execute(new ItemsLoadTask(owner, filter.getLoadingMessage()));
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
     * @return 
     */
    protected abstract EditItem.ShowAndWaitResult<M> onEditItem(Event event, M item);

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
    
//    protected final static <M  extends ItemModel<?>, C extends ListingController<M>> void setContent(Class<C> controllerClass, MainController mc,
//            Stage stage, ModelFilter<M> filter) throws IOException {
//        mc.setContent(controllerClass, stage, (Parent v, C c) -> {
//            c.changeFilter(filter, stage);
//        });
//    }
        
    protected abstract DataObjectImpl.Factory<D> getDaoFactory();
    
    protected abstract M toModel(D result);
        
    protected class ItemsLoadTask extends TaskWaiter<List<D>> {
//        private final ModelFilter<M> currentFilter;
        
        protected ItemsLoadTask(Stage owner, String operation) {
            super(owner, operation);
//            currentFilter = filter;
        }
        
        protected void processNullResult(Window owner) {
            
        }
        
//        protected void onItemsLoaded(ModelFilter<M> filter, Window owner) { }
        
//        protected abstract Iterable<T> getResult(Connection connection, ModelFilter<M> filter) throws Exception;
    
        @Override
        protected final List<D> getResult() throws Exception {
            try (DbConnector dep = new DbConnector()) {
                DataObjectImpl.Factory<D> factory = getDaoFactory();
                return filter.apply(dep.getConnection(), factory);
            }
        }
        
        @Override
        protected final void processResult(List<D> result, Window owner) {
            if (null == result) {
                LOG.logp(Level.SEVERE, getClass().getName(), "processResult", String.format("\"%s\" operation returned null", getTitle()));
                processNullResult(owner);
            } else {
                itemsList.clear();
                Iterator<D> it = result.iterator();
                while (it.hasNext())
                    itemsList.add(toModel(it.next()));
                throw new UnsupportedOperationException("Not implemented");
            }
        }
        
        @Override
        protected void processException(Throwable ex, Window owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", String.format("\"%s\" operation error", getTitle()), ex);
            Alerts.showErrorAlert(ex);
        }
        
    }
    
}
