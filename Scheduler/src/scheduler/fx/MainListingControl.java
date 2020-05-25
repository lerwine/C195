package scheduler.fx;

import java.io.IOException;
import java.sql.Connection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.util.ViewControllerLoader;
import scheduler.view.MainController;
import scheduler.view.ModelFilter;
import scheduler.view.event.ItemActionRequestEvent;

/**
 * Base class for item list management.
 *
 * @param <D> Data access object type wrapped by the model.
 * @param <M> The FX model type.
 * @param <T> The data object event type.
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class MainListingControl<D extends DataAccessObject, M extends FxRecordModel<D>, T extends DataObjectEvent<D>> extends StackPane {

    private static final Logger LOG = Logger.getLogger(MainListingControl.class.getName());
    private final ObjectProperty<ModelFilter<D, M, ? extends DaoFilter<D>>> filter;
    private final ObservableList<M> items;

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
        addEventFilter(getInsertedEventType(), this::onInsertedEvent);
        addEventFilter(getUpdatedEventType(), this::onUpdatedEvent);
        addEventFilter(getDeletedEventType(), this::onDeletedEvent);

        listingTableView.setItems(items);

//        sceneProperty().addListener(new InvalidationListener() {
//            private boolean isListening = false;
//
//            {
//                onChange(null != getScene());
//            }
//
//            private void onChange(boolean hasParent) {
//                if (hasParent) {
//                    LOG.info("Scene is not null");
//                    if (!isListening) {
////                        Scheduler.getMainController().addDaoEventHandler(getInsertedEventType(), MainListingControl.this::onInsertedEvent);
////                        Scheduler.getMainController().addDaoEventHandler(getUpdatedEventType(), MainListingControl.this::onUpdatedEvent);
////                        Scheduler.getMainController().addDaoEventHandler(getDeletedEventType(), MainListingControl.this::onDeletedEvent);
//                        isListening = true;
//                    }
//                } else {
//                    LOG.info("Scene is null");
//                    if (isListening) {
//                        addEventHandler(getInsertedEventType(), MainListingControl.this::onInsertedEvent);
//                        addEventHandler(getUpdatedEventType(), MainListingControl.this::onUpdatedEvent);
//                        addEventHandler(getDeletedEventType(), MainListingControl.this::onDeletedEvent);
////                        Scheduler.getMainController().removeDaoEventHandler(getInsertedEventType(), MainListingControl.this::onInsertedEvent);
////                        Scheduler.getMainController().removeDaoEventHandler(getUpdatedEventType(), MainListingControl.this::onUpdatedEvent);
////                        Scheduler.getMainController().removeDaoEventHandler(getDeletedEventType(), MainListingControl.this::onDeletedEvent);
//                        isListening = false;
//                    }
//                }
//            }
//
//            @Override
//            public void invalidated(Observable observable) {
//                onChange(null != ((ObservableObjectValue<?>) observable).get());
//            }
//
//        });
        filter.addListener((observable) -> {
            if (Platform.isFxApplicationThread()) {
                onFilterChanged(((ObjectProperty<ModelFilter<D, M, ? extends DaoFilter<D>>>) observable).get());
            } else {
                Platform.runLater(() -> onFilterChanged(((ObjectProperty<ModelFilter<D, M, ? extends DaoFilter<D>>>) observable).get()));
            }
        });
    }

    @FXML
    private void onDeleteMenuItemAction(ActionEvent event) {
        M item = listingTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onDeleteItem(item);
        }
    }

    @FXML
    private void onEditMenuItemAction(ActionEvent event) {
        M item = listingTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onEditItem(item);
        }
    }

    @FXML
    private void onItemActionRequest(ItemActionRequestEvent<M> event) {
        if (event.isDelete()) {
            onDeleteItem(event.getItem());
        } else {
            onEditItem(event.getItem());
        }
        event.consume();
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onListingTableViewKeyReleased(KeyEvent event) {
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
    }

    @FXML
    private void onNewButtonAction(ActionEvent event) {
        onNewItem();
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

    private void onFilterChanged(ModelFilter<D, M, ? extends DaoFilter<D>> f) {
        if (null == f) {
            listingTableView.setItems(null);
        } else {
            headingLabel.setText(f.getHeadingText());
            String s = f.getSubHeadingText();
            if (null == s || s.trim().isEmpty()) {
                collapseNode(subHeadingLabel);
            } else {
                restoreLabeled(subHeadingLabel, s);
            }
            Task<List<D>> task = createLoadTask(f);
            task.setOnSucceeded((event) -> {
                setItems(task.getValue());
            });
            MainController.startBusyTaskNow(task);
        }
    }

    private void setItems(List<D> daoItems) {
        items.clear();
        if (null != daoItems && !daoItems.isEmpty()) {
            FxRecordModel.ModelFactory<D, M> factory = getModelFactory();
            daoItems.stream().sorted(getComparator()).forEach((D t) -> items.add(factory.createNew(t)));
        }
    }

    protected LoadItemsTask createLoadTask(ModelFilter<D, M, ? extends DaoFilter<D>> filter) {
        return new LoadItemsTask(filter);
    }

    protected void onInsertedEvent(T event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        ModelFilter<D, M, ? extends DaoFilter<D>> f = filter.get();
        if (null != f) {
            D dao = event.getTarget();
            if (f.getDaoFilter().test(dao)) {
                items.add(getModelFactory().createNew(dao));
            }
        }
    }

    protected void onUpdatedEvent(T event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        D dao = event.getTarget();
        FxRecordModel.ModelFactory<D, M> mf = getModelFactory();
        if (null != mf) {
            Optional<M> m = mf.find(items, dao);
            ModelFilter<D, M, ? extends DaoFilter<D>> f = filter.get();
            if (m.isPresent()) {
                if (null != f && !f.getDaoFilter().test(dao)) {
                    items.remove(m.get());
                    return;
                }
                mf.updateItem(m.get(), dao);
            } else if (null != f && f.getDaoFilter().test(dao)) {
                getItems().add(mf.createNew(dao));
            }
        }
    }

    protected void onDeletedEvent(T event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        if (!items.isEmpty()) {
            getModelFactory().find(items, event.getTarget()).ifPresent((t) -> items.remove(t));
        }
    }

    protected abstract Comparator<? super D> getComparator();

    protected abstract FxRecordModel.ModelFactory<D, M> getModelFactory();

    protected abstract String getLoadingTitle();

    protected abstract String getFailMessage();

    protected abstract void onNewItem();

    protected abstract void onEditItem(M item);

    protected abstract void onDeleteItem(M item);

    protected abstract EventType<T> getInsertedEventType();

    protected abstract EventType<T> getUpdatedEventType();

    protected abstract EventType<T> getDeletedEventType();

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
            updateMessage(getFailMessage());
            super.failed();
        }

        protected String getConnectingMessage() {
            return appResources.getString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB);
        }

        protected String getConnectedMessage() {
            return appResources.getString(AppResourceKeys.RESOURCEKEY_READINGFROMDB);
        }

        @Override
        protected List<D> call() throws Exception {
            updateMessage(getConnectingMessage());
            try (DbConnector dbConnector = new DbConnector()) {
                return onConnected(dbConnector.getConnection());
            }
        }

        protected List<D> onConnected(Connection connection) throws Exception {
            updateMessage(getConnectedMessage());
            return getModelFactory().getDaoFactory().load(connection, filter.getDaoFilter());
        }
    }

}
