package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
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
import scheduler.AppResources;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.ModelFilter;
import scheduler.util.Alerts;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventListener;
import scheduler.util.ItemEventManager;

/**
 * Base class for controllers that present a {@link TableView} containing {@link ItemModel} objects. This is loaded as the content of
 * {@link MainController} using {@link #setContent(scheduler.view.MainController, java.lang.Class, javafx.stage.Stage, scheduler.dao.ModelFilter)}.
 *
 * @author Leonard T. Erwine
 * @param <T> The type of data access object that corresponds to the model object type.
 * @param <S> The type of model objects presented by the ListingController.
 */
public abstract class ListingController<T extends DataObjectImpl, S extends ItemModel<T>> extends MainController.MainContentController {

    private static final Logger LOG = Logger.getLogger(ListingController.class.getName());

    public static <T extends DataObjectImpl, S extends ItemModel<T>> ListingController<T, S> setContent(MainController mainController,
            Class<? extends ListingController<T, S>> controllerClass, Stage stage, ModelFilter<T, S> filter) throws IOException {
        ListingController<T, S> controller = setContent(mainController, controllerClass, stage);
        controller.changeFilter(filter, stage, null);
        return controller;
    }
    private ItemEventListener<ItemEvent<S>> itemAddedListener;
    private ItemEventListener<ItemEvent<S>> itemRemovedListener;
    private ModelFilter<T, S> filter;
    private final ObservableList<S> itemsList = FXCollections.observableArrayList();

    /**
     * The {@link TableView} control injected by the {@link FXMLLoader}.
     */
    @FXML
    protected TableView<S> listingTableView;

    /**
     * The {@link MenuItem} injected by the {@link FXMLLoader} for editing an {@link ItemModel}. This is defined within the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     */
    @FXML
    protected MenuItem editMenuItem;

    /**
     * The {@link MenuItem} injected by the {@link FXMLLoader} for deleting an {@link ItemModel}. This is defined within the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     */
    @FXML
    protected MenuItem deleteMenuItem;

    /**
     * The {@link Button} control injected by the {@link FXMLLoader} for adding a new {@link ItemModel}.
     */
    @FXML
    protected Button newButton;

    /**
     * Gets the {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     *
     * @return The {@link javafx.collections.ObservableList} that is bound to the {@link #listingTableView}.
     */
    protected ObservableList<S> getItemsList() {
        return itemsList;
    }

    /**
     * Called by the {@link FXMLLoader} to complete controller initialization.
     */
    @FXML
    protected void initialize() {
        Objects.requireNonNull(listingTableView, String.format("fx:id=\"listingTableView\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setItems(itemsList);
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
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            S item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                ResourceBundle rb = AppResources.getResources();
                Alerts.showWarningAlert(rb.getString(AppResources.RESOURCEKEY_NOTHINGSELECTED), rb.getString(AppResources.RESOURCEKEY_NOITEMWASSELECTED));
            } else {
                onEditItem(event, item);
            }
        });
        Objects.requireNonNull(deleteMenuItem, String.format("fx:id=\"deleteMenuItem\" (Context menu item) was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            S item = listingTableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                ResourceBundle rb = AppResources.getResources();
                Alerts.showWarningAlert(rb.getString(AppResources.RESOURCEKEY_NOTHINGSELECTED), rb.getString(AppResources.RESOURCEKEY_NOITEMWASSELECTED));
            } else {
                onDeleteItem(event, item);
            }
        });
        Objects.requireNonNull(newButton, String.format("fx:id=\"newButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> onAddNewItem(event));
        itemAddedListener = (event) -> {
            if (null == filter || filter.test(event.getTarget())) {
                itemsList.add(event.getTarget());
            }
        };
        itemRemovedListener = (event) -> {
            removeListItemByPrimaryKey(event.getTarget().getPrimaryKey());
        };
    }

    /**
     * Gets the {@link ItemEventManager} from the {@link MainController} that emits an event whenever a new item has been inserted into the database.
     *
     * @return The {@link ItemEventManager} from the {@link MainController}.
     */
    protected abstract ItemEventManager<ItemEvent<S>> getItemAddManager();

    /**
     * Gets the {@link ItemEventManager} from the {@link MainController} that emits an event whenever an item has been removed into the database.
     *
     * @return The {@link ItemEventManager} from the {@link MainController}.
     */
    protected abstract ItemEventManager<ItemEvent<S>> getItemRemoveManager();

    @Override
    protected void onBeforeShow(Node currentView, Stage stage) {
        getItemAddManager().addListener(itemAddedListener);
        getItemAddManager().addListener(itemRemovedListener);
        super.onBeforeShow(currentView, stage);
    }

    @Override
    protected void onUnloaded(Node view) {
        getItemAddManager().removeListener(itemAddedListener);
        getItemAddManager().removeListener(itemRemovedListener);
        super.onUnloaded(view);
    }

    /**
     * Sets the {@link #filter} and starts a {@link TaskWaiter} if the filter has changed.
     *
     * @param value The new {@link ModelFilter}.
     * @param stage The {@link Stage} to whose content is to be masked while items are loaded from the database.
     * @param onChangeComplete The {@link Consumer} to invoke after the filter has been changed and items have been loaded. This will contain a
     * {@code true} parameter if the filter required items to be reloaded from the database or {@code false} if the filter was the same and no action
     * was needed. This can also pass a @code false} value if the filter is changed again before the items load task is finished.
     */
    public synchronized void changeFilter(ModelFilter<T, S> value, Stage stage, Consumer<Boolean> onChangeComplete) {
        Objects.requireNonNull(stage);
        if (null == value) {
            value = getDaoFactory().getDefaultFilter();
        }
        if (null != filter && ModelFilter.areEqual(value, filter)) {
            if (null != onChangeComplete) {
                onChangeComplete.accept(Boolean.FALSE);
            }
            return;
        }
        filter = value;
        TaskWaiter.execute(createItemsLoadTask(stage, onChangeComplete));
    }

    /**
     * Creates a new {@link ItemsLoadTask} for loading items from the database asynchronously.
     *
     * @param stage The {@link Stage} to whose content is to be masked while items are loaded from the database.
     * @param onChangeComplete The {@link Consumer} to invoke after the filter has been changed and items have been loaded. This can be null.
     * @return The new {@link ItemsLoadTask}.
     */
    protected ItemsLoadTask createItemsLoadTask(Stage stage, Consumer<Boolean> onChangeComplete) {
        return new ItemsLoadTask(stage, onChangeComplete);
    }

    /**
     * This gets called when the user clicks the {@link #newButton} control or types the {@link KeyCode#N} key while {@link KeyEvent#isMetaDown()} or
     * {@link KeyEvent#isControlDown()}.
     *
     * @param event Contextual information about the event.
     */
    protected abstract void onAddNewItem(Event event);

    /**
     * This gets called when the user types the {@link KeyCode#ENTER} key or clicks the {@link #editMenuItem} in the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     *
     * @param event Contextual information about the event.
     * @param item The selected item to be edited.
     * @return
     */
    protected abstract EditItem.ShowAndWaitResult<S> onEditItem(Event event, S item);

    /**
     * This gets called when the user types the {@link KeyCode#DELETE} key or clicks the {@link #deleteMenuItem} in the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     *
     * @param event Contextual information about the event.
     * @param item The selected item to be deleted.
     */
    protected abstract void onDeleteItem(Event event, S item);

    /**
     * Gets the index of the {@link ItemModel} in the current {@link #itemsList} where the {@link ItemModel#primaryKeyProperty()} matches the given
     * value.
     *
     * @param pk The value of the primary key.
     * @return The index of the list item whose {@link ItemModel#primaryKeyProperty()} matches {@code pk} or {@code -1} if no match was found.
     */
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

    /**
     * Gets the {@link ItemModel} in the current {@link #itemsList} whose {@link ItemModel#primaryKeyProperty()} matches the given value.
     *
     * @param pk The value of the primary key.
     * @return The {@link ItemModel} whose {@link ItemModel#primaryKeyProperty()} matches the given value or {@code null} if no match was found.
     */
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

    /**
     * Removes the {@link ItemModel} from the current {@link #itemsList} whose {@link ItemModel#primaryKeyProperty()} matches the given value. This
     * does not delete the item from the database.
     *
     * @param pk The value of the primary key.
     * @return {@code true} if the item was removed or {@code false} if no match was found.
     */
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

    /**
     * Replaces an {@link ItemModel} in the current {@link #itemsList} a matching {@link ItemModel#primaryKeyProperty()}. This does not update any
     * item in the database.
     *
     * @param item The {@link ItemModel} to replace into the list.
     * @return {@code true} if an item with a matching {@link ItemModel#primaryKeyProperty()} was found and replaced; otherwise, {@code false} if no
     * match was found.
     */
    protected boolean updateListItem(S item) {
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

    /**
     * Inserts an {@link ItemModel} into the current {@link #itemsList}, replacing any with a matching {@link ItemModel#primaryKeyProperty()}. This
     * does not insert or update any item in the database.
     *
     * @param item The {@link ItemModel} to insert into the list.
     * @return {@code true} if the {@code item} was appended to the list; otherwise, {@code false} if it replaced one with a matching
     * {@link ItemModel#primaryKeyProperty()}.
     */
    protected boolean upsertListItem(S item) {
        int pk = item.getPrimaryKey();
        ObservableList<S> items = getItemsList();
        for (int i = 0; i < items.size(); i++) {
            S m = items.get(i);
            if (m.getPrimaryKey() == pk) {
                if (m != item) {
                    items.set(i, item);
                }
                return false;
            }
        }
        items.add(item);
        return true;
    }

    /**
     * Gets the {@link DataObjectImpl.Factory} to be used with this listing.
     *
     * @return The {@link DataObjectImpl.Factory} to be used with this listing.
     */
    protected abstract DataObjectImpl.Factory<T, S> getDaoFactory();

    /**
     * Creates an {@link ItemModel} from a {@link DataObjectImpl}.
     *
     * @param dao The data access object.
     * @return The new {@link ItemModel}.
     */
    protected abstract S toModel(T dao);

    /**
     * This gets called after the filter has been changed and the items have been loaded into the current {@link #itemsList}.
     *
     * @param filter The new {@link ModelFilter} being applied.
     * @param owner The {@link Stage} for the {@link javafx.event.ActionEvent} that triggered the filter change.
     */
    protected void onItemsLoaded(ModelFilter<T, S> filter, Stage owner) {
    }

    /**
     * A {@link TaskWaiter} that asynchronously retrieves data from the database, using a {@link ModelFilter}.
     */
    protected class ItemsLoadTask extends TaskWaiter<List<T>> {

        private final ModelFilter<T, S> currentFilter;
        private final Consumer<Boolean> onComplete;

        /**
         * Creates a new items loader task.
         *
         * @param stage The {@link Stage} to whose content is to be masked while items are loaded from the database.
         * @param onComplete The {@link Consumer} to invoke after the task is completed. This will pass a {@code true} value if the items have been
         * loaded; otherwise, {@code false} if this task was superceded by another.
         */
        protected ItemsLoadTask(Stage stage, Consumer<Boolean> onComplete) {
            super(stage, filter.getLoadingMessage());
            currentFilter = filter;
            this.onComplete = onComplete;
        }

        /**
         * Creates a new items loader task.
         *
         * @param stage The {@link Stage} to whose content is to be masked while items are loaded from the database.
         */
        protected ItemsLoadTask(Stage stage) {
            this(stage, null);
        }

        @Override
        protected final List<T> getResult(Connection connection) throws SQLException {
            return currentFilter.get(connection);
        }

        @Override
        protected synchronized final void processResult(List<T> result, Stage owner) {
            if (null != filter && filter != currentFilter) {
                if (null != onComplete) {
                    onComplete.accept(false);
                }
                return;
            }
            try {
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
            } finally {
                if (null != onComplete) {
                    onComplete.accept(true);
                }
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", String.format("\"%s\" operation error", getTitle()), ex);
            Alerts.showErrorAlert(ex);
        }

    }

}
