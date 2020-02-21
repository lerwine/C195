package scheduler.view;

import java.sql.Connection;
import java.sql.SQLException;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import scheduler.App;
import scheduler.dao.DataObjectImpl;
import scheduler.util.Alerts;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventListener;
import scheduler.util.ItemEventManager;
import scheduler.dao.ModelFilter;

/**
 * Base class for controllers that present a {@link TableView} containing {@link ItemModel} objects.
 *
 * @author Leonard T. Erwine
 * @param <T>
 * @param <S> The type of model objects presented by the ListingController.
 */
public abstract class ListingController<T extends DataObjectImpl, S extends ItemModel<T>> extends MainController.MainContentController {

    private ItemEventListener<ItemEvent<S>> itemAddedListener;
    private ItemEventListener<ItemEvent<S>> itemRemovedListener;

    private ModelFilter<T, S> filter;

    //<editor-fold defaultstate="collapsed" desc="itemsList">
    private final ObservableList<S> itemsList = FXCollections.observableArrayList();

    /**
     * Gets the {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     *
     * @return The {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     */
    protected ObservableList<S> getItemsList() {
        return itemsList;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    //<editor-fold defaultstate="collapsed" desc="listingTableView">
    /**
     * The {@link TableView} control injected by the {@link FXMLLoader}.
     */
    @FXML
    protected TableView<S> listingTableView;

    //</editor-fold>
    /**
     * The {@link MenuItem} injected by the {@link FXMLLoader} for editing an {@link ItemModel}. This is defined within the {@link javafx.scene.control.ContextMenu} for the
     * {@link #listingTableView} control.
     */
    @FXML
    protected MenuItem editMenuItem;

    /**
     * The {@link MenuItem} injected by the {@link FXMLLoader} for deleting an {@link ItemModel}. This is defined within the {@link javafx.scene.control.ContextMenu} for the
     * {@link #listingTableView} control.
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
            if (event.isAltDown() || event.isShortcutDown()) {
                return;
            }
            if (event.isMetaDown() || event.isControlDown()) {
                if (event.getCode() == KeyCode.N) {
                    onAddNewItem(event);
                }
                return;
            }
            if (event.isShiftDown()) {
                return;
            }
            S item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }
            if (event.getCode() == KeyCode.DELETE) {
                onDeleteItem(event, item);
            } else if (event.getCode() == KeyCode.ENTER) {
                onEditItem(event, item);
            }
        });
        Objects.requireNonNull(editMenuItem, String.format("fx:id=\"editMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            S item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                ResourceBundle rb = App.getResources();
                Alerts.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
            } else {
                onEditItem(event, item);
            }
        });
        Objects.requireNonNull(deleteMenuItem, String.format("fx:id=\"deleteMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> {
            S item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                ResourceBundle rb = App.getResources();
                Alerts.showWarningAlert(rb.getString(scheduler.App.RESOURCEKEY_NOTHINGSELECTED), rb.getString(scheduler.App.RESOURCEKEY_NOITEMWASSELECTED));
            } else {
                onDeleteItem(event, item);
            }
        });
        Objects.requireNonNull(newButton, String.format("fx:id=\"newButton\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()))).setOnAction((event) -> onAddNewItem(event));
        itemAddedListener = (event) -> {
            if (null == filter || filter.test(event.getTarget())) {
                itemsList.add(event.getTarget());
            }
        };
        itemRemovedListener = (event) -> {
            removeListItemByPrimaryKey(event.getTarget().getPrimaryKey());
        };
    }

    protected abstract ItemEventManager<ItemEvent<S>> getItemAddManager(MainController mainController);

    protected abstract ItemEventManager<ItemEvent<S>> getItemRemoveManager(MainController mainController);

    @Override
    protected void onBeforeShow(Node currentView, Stage stage) {
        getItemAddManager(getMainController()).addListener(itemAddedListener);
        getItemAddManager(getMainController()).addListener(itemRemovedListener);
        super.onBeforeShow(currentView, stage);
    }

    @Override
    protected void onUnloaded(Node view) {
        getItemAddManager(getMainController()).removeListener(itemAddedListener);
        getItemAddManager(getMainController()).removeListener(itemRemovedListener);
        super.onUnloaded(view); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Sets the {@link #filter} and starts a {@link TaskWaiter} if the filter has changed.
     *
     * @param value The new {@link ModelFilter}.
     * @param owner The owner {@link Stage} to use when showing the {@link javafx.stage.Popup} window.
     */
    public void changeFilter(ModelFilter<T, S> value, Stage owner) {
        if (null == value) {
            value = getDaoFactory().getDefaultFilter();
        }
        if (null != filter && value.equals(filter)) {
            return;
        }
        filter = value;
        onFilterChanged(Objects.requireNonNull(owner));
    }

    /**
     * This gets called whenever the current {@link #filter} has changed.
     *
     * @param owner The owner {@link Stage} to use when showing the {@link javafx.stage.Popup} window.
     */
    private void onFilterChanged(Stage owner) {
        TaskWaiter.execute(createItemsLoadTask(owner));
    }

    /**
     * This gets called when the user clicks the {@link #newButton} control or types the {@link KeyCode#N} key while {@link KeyEvent#isMetaDown()} or
     * {@link KeyEvent#isControlDown()}.
     *
     * @param event Contextual information about the event.
     */
    protected abstract void onAddNewItem(Event event);

    /**
     * This gets called when the user types the {@link KeyCode#ENTER} key or clicks the {@link #editMenuItem} in the {@link javafx.scene.control.ContextMenu} for the
     * {@link #listingTableView} control.
     *
     * @param event Contextual information about the event.
     * @param item The selected item to be edited.
     * @return
     */
    protected abstract EditItem.ShowAndWaitResult<S> onEditItem(Event event, S item);

    /**
     * This gets called when the user types the {@link KeyCode#DELETE} key or clicks the {@link #deleteMenuItem} in the {@link javafx.scene.control.ContextMenu} for the
     * {@link #listingTableView} control.
     *
     * @param event Contextual information about the event.
     * @param item The selected item to be deleted.
     */
    protected abstract void onDeleteItem(Event event, S item);

    protected int indexOfListItemByPrimaryKey(int pk) {
        Iterator<S> iterator = getItemsList().iterator();
        int index = -1;
        while (iterator.hasNext()) {
            ++index;
            if (iterator.next().getPrimaryKey() == pk) {
                return index;
            }
        }
        return -1;
    }

    protected S getListItemByPrimaryKey(int pk) {
        Iterator<S> iterator = getItemsList().iterator();
        while (iterator.hasNext()) {
            S item = iterator.next();
            if (item.getPrimaryKey() == pk) {
                return item;
            }
        }
        return null;
    }

    protected boolean removeListItemByPrimaryKey(int pk) {
        Iterator<S> iterator = getItemsList().iterator();
        while (iterator.hasNext()) {
            S item = iterator.next();
            if (item.getPrimaryKey() == pk) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    protected boolean updateListItem(S item) {
        Objects.requireNonNull(item);
        int pk = item.getPrimaryKey();
        ObservableList<S> items = getItemsList();
        for (int i = 0; i < items.size(); i++) {
            S m = items.get(i);
            if (m.getPrimaryKey() == pk) {
                if (m != item) {
                    items.set(i, item);
                }
                return true;
            }
        }
        return false;
    }

    protected abstract DataObjectImpl.Factory<T, S> getDaoFactory();

    protected abstract S toModel(T result);

    protected void onItemsLoaded(ModelFilter<T, S> filter, Stage owner) {
    }

    protected ItemsLoadTask createItemsLoadTask(Stage owner) {
        return new ItemsLoadTask(owner);
    }

    protected class ItemsLoadTask extends TaskWaiter<List<T>> {

        private final ModelFilter<T, S> currentFilter;

        protected ItemsLoadTask(Stage owner) {
            super(owner, filter.getLoadingMessage());
            currentFilter = filter;
        }

        @Override
        protected final List<T> getResult(Connection connection) throws SQLException {
            return currentFilter.get(connection);
        }

        @Override
        protected final void processResult(List<T> result, Stage owner) {
            if (null == result) {
                LOG.logp(Level.SEVERE, getClass().getName(), "processResult", String.format("\"%s\" operation returned null", getTitle()));
            } else {
                itemsList.clear();
                Iterator<T> it = result.iterator();
                while (it.hasNext()) {
                    itemsList.add(toModel(it.next()));
                }
            }
            onItemsLoaded(currentFilter, owner);
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", String.format("\"%s\" operation error", getTitle()), ex);
            Alerts.showErrorAlert(ex);
        }

    }

}
