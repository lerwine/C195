package scheduler.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.filter.DaoFilter;
import scheduler.util.AlertHelper;
import scheduler.util.EventHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.event.DataLoadedEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventType;
import scheduler.view.model.ItemModel;

/**
 * Base class for controllers that present a {@link TableView} containing {@link ItemModel} objects.
 * <p>
 * Inherited classes invoke {@link #loadInto(Class, MainController, Stage, ModelFilter)} or
 * {@link #loadInto(Class, MainController, Stage, ModelFilter, Object)} to instantiate a new controller instance and its view. Typically, the
 * {@link MainController} calls a method on the inherited class, so that method can pass the {@link MainController} to the aforementioned method.</p>
 *
 * <p>
 * The {@link #filterx} field is set to the specified {@link ModelFilter} during the {@link FxmlViewEventType#LOADED} event.</p>
 *
 * <p>
 * List items are retrieved from the database by a background that that is started by {@link #onFxmlViewEvent(FxmlViewEvent)} during the
 * {@link FxmlViewEventType#BEFORE_SHOW} event. The {@link DataLoadedEvent} event fired when the items have been loaded. Implementing classes can
 * either use the {@link scheduler.view.annotations.HandlesDataLoaded} annotation on a method or implement the
 * {@link scheduler.view.event.DataLoadedEventListener} to receive this event.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of data access object that corresponds to the model object type.
 * @param <U> The type of model objects presented by the ListingController.
 */
public abstract class ListingController<T extends DataAccessObject, U extends ItemModel<T>> {

    private static final Logger LOG = Logger.getLogger(ListingController.class.getName());

    /**
     * Loads an item listing controller into the view of the {@link MainController}.
     * <p>
     * This is called by listing controllers such as and
     * {@link scheduler.view.appointment.ManageAppointments#loadInto(scheduler.view.MainController, Stage, scheduler.view.appointment.AppointmentModelFilter)}.</p>
     *
     * <p>
     * This calls {@link MainController#loadContent(java.lang.Class, java.lang.Object)}, to load the view and controller.</p>
     *
     * <p>
     * When the {@link FxmlViewEventType#LOADED} {@link scheduler.view.event.FxmlViewEvent} occurs, this sets the {@link #filterx} property from the
     * parameters of this method.</p>
     *
     * <p>
     * When the {@link #onFxmlViewEvent(FxmlViewEvent)} method handles the {@link FxmlViewEventType#BEFORE_SHOW}
     * {@link scheduler.view.event.FxmlViewEvent} it calls {@link DataAccessObject.DaoFactory#loadAsync(Stage, DaoFilter, Consumer, Consumer)} to load
     * {@link DataAccessObject} items.</p>
     *
     * @param <T> The type of data access object.
     * @param <U> The type of model.
     * @param <S> The listing controller type.
     * @param controllerClass The class of the listing controller
     * @param mainController The target main controller.
     * @param stage The stage of the main controller.
     * @param filter The list item filter to be applied.
     * @param loadEventListener An object that can listen for FXML load events. This object can implement {@link FxmlViewControllerEventListener} or
     * use the {@link scheduler.view.annotations.HandlesFxmlViewEvent} annotation to handle view/controller life-cycle events.
     * @return The instantiated controller.
     * @throws IOException if not able to load the FXML view.
     */
    protected static <T extends DataAccessObject, U extends ItemModel<T>, S extends ListingController<T, U>> S loadInto(Class<S> controllerClass,
            MainController mainController, Stage stage, ModelFilter<T, U, ? extends DaoFilter<T>> filter,
            Object loadEventListener) throws IOException {
        return mainController.loadContent(controllerClass, (FxmlViewControllerEventListener<Parent, S>) (event) -> {
            if (event.getType() == FxmlViewEventType.LOADED) {
                ((ListingController<T, U>) event.getController()).filter.set(filter);
            }

            EventHelper.fireFxmlViewEvent(loadEventListener, event);
        });
    }

    /**
     * Loads an item listing controller into the view of the {@link MainController}. This method invokes
     * {@link #loadInto(Class, MainController, Stage, ModelFilter, Object)} with the last argument as {@code null}.
     *
     * @param <T> The type of data access object.
     * @param <U> The type of model.
     * @param <S> The listing controller type.
     * @param controllerClass The class of the listing controller
     * @param mainController The target main controller.
     * @param stage The stage of the main controller.
     * @param filter The list item filter to be applied.
     * @return The instantiated controller.
     * @throws IOException if not able to load the FXML view.
     */
    protected static <T extends DataAccessObject, U extends ItemModel<T>, S extends ListingController<T, U>> S loadInto(Class<S> controllerClass,
            MainController mainController, Stage stage, ModelFilter<T, U, ? extends DaoFilter<T>> filter) throws IOException {
        return loadInto(controllerClass, mainController, stage, filter, null);
    }
    
    private ObjectProperty<ModelFilter<T, U, ? extends DaoFilter<T>>> filter;
    private final ObservableList<U> itemsList = FXCollections.observableArrayList();

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    /**
     * The {@link Label} control for page heading text, injected by the {@link FXMLLoader}.
     */
    @FXML
    private Label headingLabel;

    /**
     * The help {@link Button} control injected by the {@link FXMLLoader}.
     */
    @FXML
    private Button helpButton;

    /**
     * The {@link Label} control for page sub-heading text, injected by the {@link FXMLLoader}.
     */
    @FXML
    private Label subHeadingLabel;

    /**
     * The {@link TableView} control injected by the {@link FXMLLoader}.
     */
    @FXML
    protected TableView<U> listingTableView;

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
    protected ObservableList<U> getItemsList() {
        return itemsList;
    }

    public TableView<U> getListingTableView() {
        return listingTableView;
    }

    /**
     * Called by the {@link FXMLLoader} to complete controller initialization.
     */
    @FXML
    protected void initialize() {
        assert listingTableView != null : "fx:id=\"listingTableView\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert editMenuItem != null : "fx:id=\"editMenuItem\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert deleteMenuItem != null : "fx:id=\"deleteMenuItem\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert newButton != null : "fx:id=\"newButton\" was not injected: check your FXML file 'ManageAppointments.fxml'.";

        filter = new SimpleObjectProperty<>();
        listingTableView.setItems(itemsList);
        //listingTableView.setOnKeyTyped(this::onListingTableViewKeyTyped);
        listingTableView.setOnKeyReleased(this::onListingTableKeyReleased);
        editMenuItem.setOnAction(this::onEditMenuItemAction);
        deleteMenuItem.setOnAction(this::onDeleteMenuItemAction);
        newButton.setOnAction(this::onNewButtonAction);
        helpButton.setOnAction(this::onHelpButtonAction);
    }
    
    protected String getResourceString(String key) {
        return resources.getString(key);
    }

    protected ResourceBundle getResources() {
        return resources;
    }

    void onNewButtonAction(ActionEvent event) {
        Stage stage = (Stage) listingTableView.getScene().getWindow();
        try {
            onAddNewItem(stage);
        } catch (IOException ex) {
            // PENDING: Internationalize message
            AlertHelper.showErrorAlert(stage, LOG, "Error loading view", ex);
        }
    }

    void onHelpButtonAction(ActionEvent event) {
        // CURRENT: Implement help popup
    }

    void onEditMenuItemAction(ActionEvent event) {
        U item = listingTableView.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) listingTableView.getScene().getWindow();
        if (item == null) {
            ResourceBundle rb = AppResources.getResources();
            AlertHelper.showWarningAlert(stage, LOG,
                    rb.getString(AppResources.RESOURCEKEY_NOTHINGSELECTED), rb.getString(AppResources.RESOURCEKEY_NOITEMWASSELECTED));
        } else {
            try {
                onEditItem(stage, item);
            } catch (IOException ex) {
                // PENDING: Internationalize message
                AlertHelper.showErrorAlert((Stage) listingTableView.getScene().getWindow(), LOG, "Error loading view", ex);
            }
        }
    }

    void onDeleteMenuItemAction(ActionEvent event) {
        U item = listingTableView.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) listingTableView.getScene().getWindow();
        if (item == null) {
            ResourceBundle rb = AppResources.getResources();
            AlertHelper.showWarningAlert(stage, LOG,
                    rb.getString(AppResources.RESOURCEKEY_NOTHINGSELECTED), rb.getString(AppResources.RESOURCEKEY_NOITEMWASSELECTED));
        } else {
            onDeleteItem(stage, item);
        }
    }

    void onListingTableKeyReleased(KeyEvent event) {
        if (event.isAltDown() || event.isShortcutDown()) {
            return;
        }

        Stage stage = (Stage) listingTableView.getScene().getWindow();
        if (event.isMetaDown() || event.isControlDown()) {
            if (event.getCode() == KeyCode.N) {
                try {
                    onAddNewItem(stage);
                } catch (IOException ex) {
                    // PENDING: Internationalize message
                    AlertHelper.showErrorAlert(stage, LOG, "Error loading view", ex);
                }
            }
            return;
        }
        if (event.isShiftDown()) {
            return;
        }
        U item = listingTableView.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        if (event.getCode() == KeyCode.DELETE) {
            onDeleteItem(stage, item);
        } else if (event.getCode() == KeyCode.ENTER) {
            try {
                onEditItem(stage, item);
            } catch (IOException ex) {
                // PENDING: Internationalize message
                AlertHelper.showErrorAlert(stage, LOG, "Error loading view", ex);
            }
        }
    }

    void onListingTableViewKeyTyped(KeyEvent event) {
        if (event.isAltDown() || event.isShortcutDown()) {
            return;
        }

        Stage stage = (Stage) listingTableView.getScene().getWindow();
        if (event.isMetaDown() || event.isControlDown()) {
            if (event.getCode() == KeyCode.N) {
                try {
                    onAddNewItem(stage);
                } catch (IOException ex) {
                    // PENDING: Internationalize message
                    AlertHelper.showErrorAlert(stage, LOG, "Error loading view", ex);
                }
            }
            return;
        }
        if (event.isShiftDown()) {
            return;
        }
        U item = listingTableView.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        if (event.getCode() == KeyCode.DELETE) {
            onDeleteItem(stage, item);
        } else if (event.getCode() == KeyCode.ENTER) {
            try {
                onEditItem(stage, item);
            } catch (IOException ex) {
                // PENDING: Internationalize message
                AlertHelper.showErrorAlert(stage, LOG, "Error loading view", ex);
            }
        }
    }

    /**
     * Gets the filter being used for the items listing.
     *
     * @return The {@link ModelFilter} being used for the items listing.
     */
    protected ModelFilter<T, U, ? extends DaoFilter<T>> getFilter() {
        return filter.get();
    }

    protected void setFilter(ModelFilter<T, U, ? extends DaoFilter<T>> value) {
        filter.set(value);
    }

    protected ObjectProperty<ModelFilter<T, U, ? extends DaoFilter<T>>> filterProperty() {
        return filter;
    }

    /**
     * Gets the {@link ItemModel.ModelFactory} object responsible for CRUD operations on model items.
     *
     * @return The {@link ItemModel.ModelFactory} object responsible for CRUD operations on model items.
     */
    protected abstract ItemModel.ModelFactory<T, U> getModelFactory();

    /**
     * This gets invoked when an {@link FxmlViewEvent} occurs.
     *
     * @param event The {@link FxmlViewEvent} object describing the event.
     */
    @HandlesFxmlViewEvent
    protected void onFxmlViewEvent(FxmlViewEvent<? extends Parent> event) {
        if (event.getType() == FxmlViewEventType.BEFORE_SHOW) {
            ModelFilter<T, U, ? extends DaoFilter<T>> modelFilter = filter.get();
            restoreLabeled(headingLabel, modelFilter.getHeadingText());
            String subHeadingText = modelFilter.getSubHeadingText();
            if (null == subHeadingText || subHeadingText.trim().isEmpty()) {
                collapseNode(subHeadingLabel);
            } else {
                restoreLabeled(subHeadingLabel, subHeadingText);
            }
            getModelFactory().getDaoFactory().loadAsync(event.getStage(), modelFilter.getDaoFilter(), this::onItemsLoaded, (t) -> onItemLoadError(t, event.getStage()));
            filter.addListener((observable) -> {
                onFilterChanged(((ObjectProperty<ModelFilter<T, U, ? extends DaoFilter<T>>>)observable).get());
            });
        }
    }

    private void onFilterChanged(ModelFilter<T, U, ? extends DaoFilter<T>> filter) {
        DaoFilter<T> daoFilter;
        if (null == filter) {
            ModelFilter<T, U, ? extends DaoFilter<T>> defaultFilter = getModelFactory().getDefaultFilter();
            if (null != defaultFilter) {
                this.filter.set(defaultFilter);
                return;
            }
            daoFilter = getModelFactory().getDaoFactory().getAllItemsFilter();
            collapseNode(headingLabel);
            collapseNode(subHeadingLabel);
        } else {
            restoreLabeled(headingLabel, filter.getHeadingText());
            String subHeadingText = filter.getSubHeadingText();
            if (null == subHeadingText || subHeadingText.trim().isEmpty()) {
                collapseNode(subHeadingLabel);
            } else {
                restoreLabeled(subHeadingLabel, subHeadingText);
            }
            daoFilter = filter.getDaoFilter();
        }
        Stage stage = (Stage)listingTableView.getScene().getWindow();
        getModelFactory().getDaoFactory().loadAsync(stage, daoFilter, this::onItemsLoaded, (t) -> onItemLoadError(t, stage));
    }
    
    protected void onItemLoadError(Throwable error, Stage stage) {
        AlertHelper.showErrorAlert((Stage)listingTableView.getScene().getWindow(), LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR),
                "Unexpected error loading items from database", error);
    }
    
    protected void onItemsLoaded(List<T> items) {
        itemsList.clear();
        ItemModel.ModelFactory<T, U> factory = getModelFactory();
        items.forEach((u) -> {
            itemsList.add(factory.createNew(u));
        });
        EventHelper.fireDataLoadedEvent(items, new DataLoadedEvent<>(this, itemsList));
    }

    /**
     * This gets invoked whenever a {@link DataAccessObject} is added, modified or deleted.
     *
     * @param event Information about the {@link DataAccessObject} operation.
     */
    @HandlesDataObjectEvent
    protected void onDataObjectEvent(DataObjectEvent<T> event) {
        DataAccessObject dao = event.getDataObject();
        if (getModelFactory().getDaoFactory().isAssignableFrom(dao)) {
            switch (event.getChangeAction()) {
                case DELETED:
                    removeListItemByPrimaryKey(dao.getPrimaryKey());
                    break;
                case CREATED:
                    // TODO: Add to list if matching the current filter.
                    break;
                default:
                    // TODO: Update listing item if it exists
                    break;
            }
        }
    }

    protected boolean cannotAddNew() { return true; }
    
    /**
     * This gets called when the user clicks the {@link #newButton} control or types the {@link KeyCode#N} key while {@link KeyEvent#isMetaDown()} or
     * {@link KeyEvent#isControlDown()}.
     *
     * @param stage The {@link Stage} of the view that initiated this operation.
     * @throws java.io.IOException
     */
    protected abstract void onAddNewItem(Stage stage) throws IOException;

    /**
     * This gets called when the user types the {@link KeyCode#ENTER} key or clicks the {@link #editMenuItem} in the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     *
     * @param stage The {@link Stage} of the view that initiated this operation.
     * @param item The selected item to be edited.
     * @throws java.io.IOException
     */
    protected abstract void onEditItem(Stage stage, U item) throws IOException;

    /**
     * This gets called when the user types the {@link KeyCode#DELETE} key or clicks the {@link #deleteMenuItem} in the
     * {@link javafx.scene.control.ContextMenu} for the {@link #listingTableView} control.
     *
     * @param stage The {@link Stage} of the view that initiated this operation.
     * @param item The selected item to be deleted.
     */
    protected abstract void onDeleteItem(Stage stage, U item);

    /**
     * Gets the index of the {@link ItemModel} in the current {@link #itemsList} where the {@link ItemModel#primaryKeyProperty()} matches the given
     * value.
     *
     * @param pk The value of the primary key.
     * @return The index of the list item whose {@link ItemModel#primaryKeyProperty()} matches {@code pk} or {@code -1} if no match was found.
     */
    protected int indexOfListItemByPrimaryKey(int pk) {
        Iterator<U> iterator = getItemsList().iterator();
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
    protected U getListItemByPrimaryKey(int pk) {
        Iterator<U> iterator = getItemsList().iterator();
        while (iterator.hasNext()) {
            U item = iterator.next();
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
        Iterator<U> iterator = getItemsList().iterator();
        while (iterator.hasNext()) {
            U item = iterator.next();
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
    protected boolean updateListItem(U item) {
        int pk = item.getPrimaryKey();
        ObservableList<U> items = getItemsList();
        for (int i = 0; i < items.size(); i++) {
            U m = items.get(i);
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
    protected boolean upsertListItem(U item) {
        int pk = item.getPrimaryKey();
        ObservableList<U> items = getItemsList();
        for (int i = 0; i < items.size(); i++) {
            U m = items.get(i);
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
     * Creates an {@link ItemModel} from a {@link DataAccessObject}.
     *
     * @param dao The data access object.
     * @return The new {@link ItemModel}.
     */
    protected abstract U toModel(T dao);

}
