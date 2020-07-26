package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.filter.DaoFilter;
import scheduler.events.ModelEvent;
import scheduler.events.OperationRequestEvent;
import scheduler.model.fx.EntityModel;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.util.ViewControllerLoader;
import scheduler.util.WeakEventHandlingReference;

/**
 * Base class for item list management.
 * <h3>Event handling</h3>
 * <h4>SCHEDULER_OP_REQUEST_EVENT</h4>
 * The {@link #onItemActionRequest(OperationRequestEvent)} handles {@link OperationRequestEvent}s fired by {@link scheduler.fx.ItemEditTableCell}s through the
 * {@link scheduler.fx.ItemEditTableCellFactory}.
 * <dl>
 * <dt>{@link #listingTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link OperationRequestEvent} &#123;</dt>
 * <dd> {@link javafx.event.Event#eventType} = {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr;
 * {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(OperationRequestEvent)}
 * <dl>
 * <dt>{@link OperationRequestEvent} &#123; {@link scheduler.events.ModelEvent#getOperation()} = {@link scheduler.events.DbOperationType#NONE} &#125;</dt>
 * <dd>&rarr; {@link #onEditItem(EntityModelImpl) onEditItem}({@link ModelEvent#getEntityModel()})
 * <dl>
 * <dt>(&rarr;) {@link scheduler.events.AppointmentOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.AppointmentOpRequestEvent#EDIT_REQUEST "SCHEDULER_APPOINTMENT_EDIT_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.appointment.ManageAppointments}</dd>
 * <dt>(&rarr;) {@link scheduler.events.CountryOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.CountryOpRequestEvent#EDIT_REQUEST "SCHEDULER_COUNTRY_EDIT_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.country.ManageCountries}</dd>
 * <dt>(&rarr;) {@link scheduler.events.CustomerOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.CustomerOpRequestEvent#EDIT_REQUEST "SCHEDULER_CUSTOMER_EDIT_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.customer.ManageCustomers}</dd>
 * <dt>(&rarr;) {@link scheduler.events.UserOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.UserOpRequestEvent#EDIT_REQUEST "SCHEDULER_USER_EDIT_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.user.ManageUsers}</dd>
 * </dl>
 * </dd>
 * <dt>{@link OperationRequestEvent} &#123; {@link scheduler.events.ModelEvent#getOperation()} = {@link scheduler.events.DbOperationType#DB_DELETE}} &#125;</dt>
 * <dd>&rarr; {@link #onDeleteItem(EntityModelImpl) onDeleteItem}({@link OperationRequestEvent})
 * <dl>
 * <dt>(&rarr;) {@link scheduler.events.AppointmentOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.AppointmentOpRequestEvent#DELETE_REQUEST "SCHEDULER_APPOINTMENT_DELETE_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.appointment.ManageAppointments}</dd>
 * <dt>(&rarr;) {@link scheduler.events.CountryOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.CountryOpRequestEvent#DELETE_REQUEST "SCHEDULER_COUNTRY_DELETE_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.country.ManageCountries}</dd>
 * <dt>(&rarr;) {@link scheduler.events.CustomerOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.CustomerOpRequestEvent#DELETE_REQUEST "SCHEDULER_CUSTOMER_DELETE_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.customer.ManageCustomers}</dd>
 * <dt>(&rarr;) {@link scheduler.events.UserOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link scheduler.events.UserOpRequestEvent#DELETE_REQUEST "SCHEDULER_USER_DELETE_REQUEST"} &#125;</dt>
 * <dd>&rarr; {@link scheduler.view.user.ManageUsers}</dd>
 * </dl></dd>
 * </dl>
 *
 * @param <D> Data access object type wrapped by the model.
 * @param <M> The FX model type.
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class MainListingControl<D extends DataAccessObject, M extends EntityModel<D>> extends StackPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(MainListingControl.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(MainListingControl.class.getName());

    private final ObjectProperty<ModelFilter<D, M, ? extends DaoFilter<D>>> filter;
    private final ObservableList<M> items;
    private final WeakEventHandlingReference<ModelEvent<D, M>> insertEventHandler;
    private final WeakEventHandlingReference<ModelEvent<D, M>> updateEventHandler;
    private final WeakEventHandlingReference<ModelEvent<D, M>> deleteEventHandler;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="headingLabel"
    private Label headingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="subHeadingLabel"
    private Label subHeadingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="listingTableView"
    private TableView<M> listingTableView; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    protected MainListingControl() {
        filter = new SimpleObjectProperty<>();
        items = FXCollections.observableArrayList();
        insertEventHandler = WeakEventHandlingReference.create(this::onItemInserted);
        updateEventHandler = WeakEventHandlingReference.create(this::onItemUpdated);
        deleteEventHandler = WeakEventHandlingReference.create(this::onItemDeleted);
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @SuppressWarnings("unchecked")
    protected void initialize() {
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert subHeadingLabel != null : "fx:id=\"subHeadingLabel\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert listingTableView != null : "fx:id=\"listingTableView\" was not injected: check your FXML file 'ManageCustomers.fxml'.";

        listingTableView.setItems(items);

        filter.addListener((observable) -> {
            if (Platform.isFxApplicationThread()) {
                onFilterChanged(((ObjectProperty<ModelFilter<D, M, ? extends DaoFilter<D>>>) observable).get());
            } else {
                Platform.runLater(() -> onFilterChanged(((ObjectProperty<ModelFilter<D, M, ? extends DaoFilter<D>>>) observable).get()));
            }
        });
        EntityModel.EntityModelFactory<D, M> factory = getModelFactory();
        factory.addEventHandler(getInsertedEventType(), insertEventHandler.getWeakEventHandler());
        factory.addEventHandler(getUpdatedEventType(), updateEventHandler.getWeakEventHandler());
        factory.addEventHandler(getDeletedEventType(), deleteEventHandler.getWeakEventHandler());
    }

    @FXML
    private void onDeleteMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDeleteMenuItemAction", event);
        M item = listingTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onDeleteItem(item);
        }
        LOG.exiting(LOG.getName(), "onDeleteMenuItemAction");
    }

    @FXML
    private void onEditMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onEditMenuItemAction", event);
        M item = listingTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onEditItem(item);
        }
        LOG.exiting(LOG.getName(), "onEditMenuItemAction");
    }

    @FXML
    private void onItemActionRequest(OperationRequestEvent<D, M> event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            onEditItem(event.getEntityModel());
        } else {
            onDeleteItem(event.getEntityModel());
        }
        LOG.exiting(LOG.getName(), "onItemActionRequest");
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onListingTableViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onListingTableViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            M item;
            switch (event.getCode()) {
                case DELETE:
                    item = listingTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onDeleteItem(item);
                    }
                    break;
                case ENTER:
                    item = listingTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onEditItem(item);
                    }
                    break;
            }
        }
        LOG.exiting(LOG.getName(), "onListingTableViewKeyReleased");
    }

    @FXML
    private void onNewButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewButtonAction", event);
        onNewItem();
        LOG.exiting(LOG.getName(), "onNewButtonAction");
    }

    protected final ResourceBundle getResources() {
        return resources;
    }

    protected final ObservableList<M> getItems() {
        return FXCollections.unmodifiableObservableList(listingTableView.getItems());
    }

    public TableView<M> getListingTableView() {
        return listingTableView;
    }

    public final ModelFilter<D, M, ? extends DaoFilter<D>> getFilter() {
        return filter.get();
    }

    public final void setFilter(ModelFilter<D, M, ? extends DaoFilter<D>> value) {
        filter.set(value);
    }

    public final ObjectProperty<ModelFilter<D, M, ? extends DaoFilter<D>>> filterProperty() {
        return filter;
    }

    private void onFilterChanged(ModelFilter<D, M, ? extends DaoFilter<D>> filter) {
        LOG.entering(LOG.getName(), "onFilterChanged", filter);
        if (null == filter) {
            listingTableView.setItems(null);
        } else {
            headingLabel.setText(filter.getHeadingText());
            String s = filter.getSubHeadingText();
            if (null == s || s.trim().isEmpty()) {
                collapseNode(subHeadingLabel);
            } else {
                restoreLabeled(subHeadingLabel, s);
            }
            Task<List<D>> task = createLoadTask(filter);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this::onLoadTaskSucceeded);
            MainController.startBusyTaskNow(task);
        }
        LOG.exiting(LOG.getName(), "onNewButtonAction");
    }

    @SuppressWarnings("unchecked")
    private void onLoadTaskSucceeded(WorkerStateEvent event) {
        LOG.entering(LOG.getName(), "onLoadTaskSucceeded", filter);
        setItems((List<D>) event.getSource().getValue());
        LOG.exiting(LOG.getName(), "onLoadTaskSucceeded");
    }

    @SuppressWarnings("unchecked")
    private void setItems(List<D> daoItems) {
        items.clear();
        if (null != daoItems && !daoItems.isEmpty()) {
            daoItems.stream().sorted(getComparator()).forEach((D t) -> items.add((M) t.cachedModel(true)));
        }
    }

    protected LoadItemsTask createLoadTask(ModelFilter<D, M, ? extends DaoFilter<D>> filter) {
        return new LoadItemsTask(filter);
    }

    protected abstract Comparator<? super D> getComparator();

    protected abstract EntityModel.EntityModelFactory<D, M> getModelFactory();

    protected abstract String getLoadingTitle();

    protected abstract String getFailMessage();

    protected abstract void onNewItem();

    protected abstract void onEditItem(M item);

    protected abstract void onDeleteItem(M item);

    protected abstract EventType<? extends ModelEvent<D, M>> getInsertedEventType();

    protected abstract EventType<? extends ModelEvent<D, M>> getUpdatedEventType();

    protected abstract EventType<? extends ModelEvent<D, M>> getDeletedEventType();

    private void onItemInserted(ModelEvent<D, M> event) {
        LOG.entering(LOG.getName(), "onItemInserted", event);
        ModelFilter<D, M, ? extends DaoFilter<D>> f = filter.get();
        if (null != f) {
            M entityModel = event.getEntityModel();
            if (f.test(entityModel)) {
                items.add(entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onItemInserted");
    }

    private void onItemUpdated(ModelEvent<D, M> event) {
        LOG.entering(LOG.getName(), "onItemUpdated", event);
        M entityModel = event.getEntityModel();
        EntityModel.EntityModelFactory<D, M> mf = getModelFactory();
        if (null != mf) {
            M m = mf.find(items, entityModel).orElse(null);
            ModelFilter<D, M, ? extends DaoFilter<D>> f = filter.get();
            if (null != f) {
                if (null != m) {
                    if (f.test(entityModel)) {
                        if (m != entityModel) {
                            items.remove(m);
                            items.add(entityModel);
                        }
                    } else if (m == entityModel) {
                        items.remove(entityModel);
                    }
                } else if (f.test(entityModel)) {
                    items.add(entityModel);
                }
            } else {
                items.add(entityModel);
            }
            if (null != m) {
                if (m != entityModel) {
                    items.remove(m);
                    items.add(entityModel);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onItemUpdated");
    }

    private void onItemDeleted(ModelEvent<D, M> event) {
        LOG.entering(LOG.getName(), "onItemDeleted", event);
        if (!items.isEmpty()) {
            getModelFactory().find(items, event.getEntityModel()).ifPresent((t) -> items.remove(t));
        }
        LOG.exiting(LOG.getName(), "onItemDeleted");
    }

    protected class LoadItemsTask extends Task<List<D>> {

        private final ModelFilter<D, M, ? extends DaoFilter<D>> filter;
        private final ResourceBundle appResources;

        LoadItemsTask(ModelFilter<D, M, ? extends DaoFilter<D>> filter) {
            this.filter = filter;
            appResources = AppResources.getResources();
            updateTitle(getLoadingTitle());
        }

        public ModelFilter<D, M, ? extends DaoFilter<D>> getFilter() {
            return filter;
        }

        @Override
        protected void failed() {
            LOG.entering(LOG.getName(), "failed");
            updateMessage(getFailMessage());
            super.failed();
            LOG.exiting(LOG.getName(), "failed");
        }

        protected String getConnectingMessage() {
            return appResources.getString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB);
        }

        protected String getConnectedMessage() {
            return appResources.getString(AppResourceKeys.RESOURCEKEY_READINGFROMDB);
        }

        @Override
        protected List<D> call() throws Exception {
            LOG.entering(LOG.getName(), "call");
            updateMessage(getConnectingMessage());
            try (DbConnector dbConnector = new DbConnector()) {
                LOG.exiting(LOG.getName(), "call");
                return onConnected(dbConnector.getConnection());
            }
        }

        protected List<D> onConnected(Connection connection) throws Exception {
            updateMessage(getConnectedMessage());
            return getModelFactory().getDaoFactory().load(connection, filter.getDaoFilter());
        }
    }

}
