package scheduler.view.country;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataRowState;
import scheduler.events.CityEvent;
import scheduler.events.CityFailedEvent;
import scheduler.events.CityOpRequestEvent;
import scheduler.events.CitySuccessEvent;
import scheduler.events.CountryEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.model.CityProperties;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.EntityModelImpl;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Values;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.city.EditCity;
import static scheduler.view.country.EditCountryResourceKeys.*;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing a {@link CountryModel}.
 * <h3>Event Handling</h3>
 * <h4>SCHEDULER_CITY_OP_REQUEST</h4>
 * <dl>
 * <dt>{@link #citiesTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link CityOpRequestEvent}
 * &#123;</dt>
 * <dd>{@link javafx.event.Event#eventType} = {@link CityOpRequestEvent#CITY_OP_REQUEST "SCHEDULER_CITY_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr;
 * {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(CityOpRequestEvent)}
 * <dl>
 * <dt>SCHEDULER_CITY_EDIT_REQUEST {@link CityOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link CityOpRequestEvent#EDIT_REQUEST}
 * &#125;</dt>
 * <dd>&rarr; {@link EditCity#edit(CityModel, javafx.stage.Window) EditCity.edit}(({@link CityModel}) {@link scheduler.events.ModelEvent#getEntityModel()},
 * {@link javafx.stage.Window}) (creates) {@link scheduler.events.CityEvent#CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"} &rArr;
 * {@link scheduler.model.ui.CityModel.Factory}</dd>
 * <dt>SCHEDULER_CITY_DELETE_REQUEST {@link CityOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link CityOpRequestEvent#DELETE_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link scheduler.dao.CityDAO.DeleteTask#DeleteTask(scheduler.model.ui.CityModel, boolean) new CityDAO.DeleteTask}({@link CityOpRequestEvent},
 * {@code false}) (creates) {@link scheduler.events.CityEvent#CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"} &rArr;
 * {@link scheduler.model.ui.CityModel.Factory}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/EditCountry")
@FXMLResource("/scheduler/view/country/EditCountry.fxml")
public final class EditCountry extends VBox implements EditItem.ModelEditor<CountryDAO, CountryModel, CountryEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCountry.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditCountry.class.getName());

    public static CountryModel editNew(Window parentWindow, boolean keepOpen) throws IOException {
        CountryModel.Factory factory = CountryModel.FACTORY;
        return EditItem.showAndWait(parentWindow, EditCountry.class, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static CountryModel edit(CountryModel model, Window parentWindow) throws IOException {
        CountryModel.Factory factory = CountryModel.FACTORY;
        return EditItem.showAndWait(parentWindow, EditCountry.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CityModel> itemList;
    private final ObservableList<Locale> localeList;
    private ObjectBinding<Locale> selectedLocale;

    @ModelEditor
    private CountryModel model;

    @ModelEditor
    private boolean keepOpen;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="localeComboBox"
    private ComboBox<Locale> localeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="languageValidationLabel"
    private Label languageValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="citiesLabel"
    private Label citiesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="citiesTableView"
    private TableView<CityModel> citiesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="newButtonBar"
    private ButtonBar newButtonBar; // Value injected by FXMLLoader

    private final EventHandler<CitySuccessEvent> onCityAdded;
    private final EventHandler<CitySuccessEvent> onCityUpdated;
    private final EventHandler<CitySuccessEvent> onCityDeleted;
    private final EventHandler<CountrySuccessEvent> onCountryInserted;
    private WeakEventHandler<CountrySuccessEvent> insertedHandler;
    private BooleanBinding modificationBinding;

    public EditCountry() {
        windowTitle = new ReadOnlyStringWrapper(this, "", "");
        valid = new ReadOnlyBooleanWrapper(this, "", false);
        modified = new ReadOnlyBooleanWrapper(this, "", true);
        itemList = FXCollections.observableArrayList();
        localeList = FXCollections.observableArrayList();
        Arrays.stream(Locale.getAvailableLocales()).filter((t)
                -> Values.isNotNullWhiteSpaceOrEmpty(t.getLanguage()) && Values.isNotNullWhiteSpaceOrEmpty(t.getCountry()))
                .sorted(Values::compareLocaleCountryFirst).forEach((t) -> localeList.add(t));
        onCityAdded = (CitySuccessEvent event) -> {
            LOG.entering(LOG.getName(), "onCityAdded", event);
            CityModel m = event.getEntityModel();
            if (null == m) {
                CityDAO dao = event.getDataAccessObject();
                if (dao.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
                    itemList.add(new CityModel(dao));
                }
            } else if (m.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
                itemList.add(m);
            }
        };
        onCityUpdated = (CitySuccessEvent event) -> {
            LOG.entering(LOG.getName(), "onCityUpdated", event);
            CityModel item = event.getEntityModel();
            if (null == item) {
                CityDAO dao = event.getDataAccessObject();
                int pk = dao.getPrimaryKey();
                item = itemList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().orElse(null);
                if (null == item) {
                    if (dao.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
                        itemList.add(new CityModel(dao));
                    }
                    return;
                }
            }
            if (item.getCountry().getPrimaryKey() != model.getPrimaryKey()) {
                itemList.remove(item);
            } else if (!itemList.contains(item)) {
                int pk = item.getPrimaryKey();
                CityModel existing = itemList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().orElse(null);
                if (null == existing) {
                    itemList.add(item);
                } else {
                    itemList.set(itemList.indexOf(existing), item);
                }
            }
        };
        onCityDeleted = (CitySuccessEvent event) -> {
            LOG.entering(LOG.getName(), "onCityDeleted", event);
            CityModel.FACTORY.find(itemList, event.getDataAccessObject()).ifPresent((t) -> {
                itemList.remove(t);
            });
        };
        onCountryInserted = (CountrySuccessEvent event) -> {
            LOG.entering(LOG.getName(), "onCountryInserted", event);
            model.dataObject().removeEventHandler(CountrySuccessEvent.INSERT_SUCCESS, insertedHandler);
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.INSERT_SUCCESS, new WeakEventHandler<>(onCityAdded));
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.UPDATE_SUCCESS, new WeakEventHandler<>(onCityUpdated));
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(onCityDeleted));
            restoreNode(citiesLabel);
            restoreNode(citiesTableView);
            restoreNode(newButtonBar);
            initializeEditMode();
            modified.set(false);
        };
    }

    @SuppressWarnings("incomplete-switch")
    @FXML
    private void onCitiesTableViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onCitiesTableViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            CityModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = citiesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteItem(item);
                    }
                    break;
                case ENTER:
                    item = citiesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editItem(item);
                    }
                    break;
            }
        }
    }

    @FXML
    private void onCityDeleteMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCityDeleteMenuItemAction", event);
        CityModel item = citiesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            deleteItem(item);
        }
    }

    @FXML
    private void onCityEditMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCityEditMenuItemAction", event);
        CityModel item = citiesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            editItem(item);
        }
    }

    @FXML
    private void onItemActionRequest(CityOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            try {
                EditCity.edit(event.getEntityModel(), getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        } else {
            deleteItem(event.getEntityModel());
        }
    }

    @FXML
    private void onLocaleComboBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onLocaleComboBoxAction", event);
        valid.set(null != selectedLocale.get());
        boolean m = modificationBinding.get();
        modified.set(m);
    }

    @FXML
    private void onNewButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewButtonAction", event);
        try {
            EditCity.editNew(model, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert localeComboBox != null : "fx:id=\"localeComboBox\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert languageValidationLabel != null : "fx:id=\"languageValidationLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesLabel != null : "fx:id=\"citiesLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesTableView != null : "fx:id=\"citiesTableView\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert newButtonBar != null : "fx:id=\"newButtonBar\" was not injected: check your FXML file 'EditCountry.fxml'.";

        localeComboBox.setItems(localeList);
        selectedLocale = Bindings.select(localeComboBox.selectionModelProperty(), "selectedItem");
        languageValidationLabel.visibleProperty().bind(selectedLocale.isNull());

        modificationBinding = model.rowStateProperty().isEqualTo(DataRowState.NEW)
                .or(selectedLocale.isNotEqualTo(model.localeProperty()));

        modified.set(modificationBinding.get());
        
        Locale locale = model.getLocale();
        if (null != locale) {
            if (!localeList.contains(locale)) {
                localeList.add(locale);
            }
            localeComboBox.getSelectionModel().select(locale);
        }
        if (model.isNewRow()) {
            collapseNode(citiesLabel);
            collapseNode(citiesTableView);
            collapseNode(newButtonBar);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWCOUNTRY));
            if (keepOpen) {
                insertedHandler = new WeakEventHandler<>(onCountryInserted);
                model.dataObject().addEventHandler(CountrySuccessEvent.INSERT_SUCCESS, insertedHandler);
            }
        } else {
            initializeEditMode();
            WaitTitledPane pane = new WaitTitledPane();
            pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                    .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
            waitBorderPane.startNow(pane, new ItemsLoadTask());
        }
    }

    private void editItem(CityModel item) {
        try {
            EditCity.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void deleteItem(CityModel target) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            CityDAO.DeleteTask task = new CityDAO.DeleteTask(target, false);
            task.setOnSucceeded((e) -> {
                CityEvent cityEvent = task.getValue();
                if (null != cityEvent && cityEvent instanceof CityFailedEvent) {
                    scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                            ((CityFailedEvent) cityEvent).getMessage(), ButtonType.OK);
                }
            });
            waitBorderPane.startNow(task);
        }
    }

    private void initializeEditMode() {
        citiesTableView.setItems(itemList);
        windowTitle.set(String.format(resources.getString(RESOURCEKEY_EDITCOUNTRY), model.getName()));
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public boolean isModified() {
        return modified.get();
    }

    @Override
    public ReadOnlyBooleanProperty modifiedProperty() {
        return modified.getReadOnlyProperty();
    }

    @Override
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    @Override
    public EntityModelImpl.EntityModelFactory<CountryDAO, CountryModel, CountryEvent> modelFactory() {
        return CountryModel.FACTORY;
    }

    @Override
    public void applyChanges() {
        model.setLocale(selectedLocale.get());
    }

    private class ItemsLoadTask extends Task<List<CityDAO>> {

        private final int pk;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
            pk = model.getPrimaryKey();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            List<CityDAO> result = getValue();
            if (null != result && !result.isEmpty()) {
                CityModel.Factory factory = CityModel.FACTORY;
                result.stream().sorted(CityProperties::compare).forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.INSERT_SUCCESS, new WeakEventHandler<>(EditCountry.this.onCityAdded));
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.UPDATE_SUCCESS, new WeakEventHandler<>(EditCountry.this.onCityUpdated));
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(EditCountry.this.onCityDeleted));
        }

        @Override
        protected List<CityDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CityDAO.FactoryImpl cf = CityDAO.FACTORY;
                return cf.load(dbConnector.getConnection(), cf.getByCountryFilter(pk));
            }
        }

    }

}
