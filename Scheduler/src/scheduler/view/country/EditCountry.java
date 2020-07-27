package scheduler.view.country;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
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
import javafx.event.Event;
import javafx.fxml.FXML;
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
import scheduler.events.ModelFailedEvent;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.CityHelper;
import scheduler.model.fx.CityModel;
import scheduler.model.fx.CountryModel;
import scheduler.model.fx.EntityModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.clearAndSelect;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.isInShownWindow;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ThrowableConsumer;
import scheduler.util.Values;
import scheduler.util.WeakEventHandlingReference;
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
 * <dt>{@link #citiesTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link CityOpRequestEvent} &#123;</dt>
 * <dd>{@link javafx.event.Event#eventType} = {@link CityOpRequestEvent#CITY_OP_REQUEST "SCHEDULER_CITY_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr; {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(CityOpRequestEvent)}
 * <dl>
 * <dt>SCHEDULER_CITY_EDIT_REQUEST {@link CityOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link CityOpRequestEvent#EDIT_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link EditCity#edit(CityModel, javafx.stage.Window) EditCity.edit}(({@link CityModel}) {@link scheduler.events.ModelEvent#getEntityModel()},
 * {@link javafx.stage.Window}) (creates) {@link scheduler.events.CityEvent#CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"} &rArr; {@link scheduler.model.fx.CityModel.Factory}</dd>
 * <dt>SCHEDULER_CITY_DELETE_REQUEST {@link CityOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link CityOpRequestEvent#DELETE_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link scheduler.dao.CityDAO.DeleteTask#DeleteTask(scheduler.model.fx.CityModel, boolean) new CityDAO.DeleteTask}({@link CityOpRequestEvent},
 * {@code false}) (creates) {@link scheduler.events.CityEvent#CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"} &rArr; {@link scheduler.model.fx.CityModel.Factory}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/EditCountry")
@FXMLResource("/scheduler/view/country/EditCountry.fxml")
public final class EditCountry extends VBox implements EditItem.ModelEditorController<CountryModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCountry.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(EditCountry.class.getName());

    public static void editNew(Window parentWindow, boolean keepOpen, Consumer<CountryModel> beforeShow) throws IOException {
        CountryModel model = CountryDAO.FACTORY.createNew().cachedModel(true);
        if (null != beforeShow) {
            beforeShow.accept(model);
        }
        EditItem.showAndWait(parentWindow, EditCountry.class, model, keepOpen);
    }

    public static void editNew(Window parentWindow, boolean keepOpen) throws IOException {
        editNew(parentWindow, keepOpen, null);
    }

    public static void edit(CountryModel model, Window parentWindow, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem.showAndWait(parentWindow, EditCountry.class, model, false, beforeShow);
    }

    public static void edit(CountryModel model, Window parentWindow) throws IOException {
        edit(model, parentWindow, null);
    }

    //<editor-fold defaultstate="collapsed" desc="Instance Fields">
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CityModel> itemList;
    private final ObservableList<Locale> localeList;
    private final WeakEventHandlingReference<CitySuccessEvent> cityInsertEventHandler;
    private final WeakEventHandlingReference<CitySuccessEvent> cityUpdateEventHandler;
    private final WeakEventHandlingReference<CitySuccessEvent> cityDeleteEventHandler;
    private BooleanBinding modificationBinding;
    private ObjectBinding<Locale> modelLocale;

    @ModelEditor
    private CountryModel model;

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

    //</editor-fold>
    public EditCountry() {
        windowTitle = new ReadOnlyStringWrapper(this, "", "");
        valid = new ReadOnlyBooleanWrapper(this, "", false);
        modified = new ReadOnlyBooleanWrapper(this, "", true);
        itemList = FXCollections.observableArrayList();
        localeList = FXCollections.observableArrayList();
        Arrays.stream(Locale.getAvailableLocales()).filter((t)
                -> Values.isNotNullWhiteSpaceOrEmpty(t.getLanguage()) && Values.isNotNullWhiteSpaceOrEmpty(t.getCountry()))
                .sorted(Values::compareLocaleCountryFirst).forEach((t) -> localeList.add(t));
        cityInsertEventHandler = WeakEventHandlingReference.create(this::onCityInserted);
        cityUpdateEventHandler = WeakEventHandlingReference.create(this::onCityUpdated);
        cityDeleteEventHandler = WeakEventHandlingReference.create(this::onCityDeleted);
    }

    @ModelEditor
    private void onModelInserted(CountryEvent event) {
        LOG.entering(LOG.getName(), "onModelInserted", event);
        CityModel.FACTORY.addEventHandler(CitySuccessEvent.INSERT_SUCCESS, cityInsertEventHandler.getWeakEventHandler());
        CityModel.FACTORY.addEventHandler(CitySuccessEvent.UPDATE_SUCCESS, cityUpdateEventHandler.getWeakEventHandler());
        CityModel.FACTORY.addEventHandler(CitySuccessEvent.DELETE_SUCCESS, cityDeleteEventHandler.getWeakEventHandler());
        restoreNode(citiesLabel);
        restoreNode(citiesTableView);
        restoreNode(newButtonBar);
        initializeEditMode();
        modified.set(false);
        LOG.exiting(LOG.getName(), "onModelInserted");
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
        LOG.exiting(LOG.getName(), "onCitiesTableViewKeyReleased");
    }

    @FXML
    private void onCityDeleteMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCityDeleteMenuItemAction", event);
        CityModel item = citiesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            deleteItem(item);
        }
        LOG.exiting(LOG.getName(), "onCityDeleteMenuItemAction");
    }

    @FXML
    private void onCityEditMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCityEditMenuItemAction", event);
        CityModel item = citiesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            editItem(item);
        }
        LOG.exiting(LOG.getName(), "onCityEditMenuItemAction");
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
        LOG.exiting(LOG.getName(), "onItemActionRequest");
    }

    @FXML
    private void onLocaleComboBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onLocaleComboBoxAction", event);
        valid.set(null != localeComboBox.getSelectionModel().getSelectedItem());
        if (null != modificationBinding) {
            boolean m = modificationBinding.get();
            modified.set(m);
        }
        LOG.exiting(LOG.getName(), "onLocaleComboBoxAction");
    }

    @FXML
    private void onNewButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewButtonAction", event);
        try {
            EditCity.editNew(model, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        assert localeComboBox != null : "fx:id=\"localeComboBox\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert languageValidationLabel != null : "fx:id=\"languageValidationLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesLabel != null : "fx:id=\"citiesLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesTableView != null : "fx:id=\"citiesTableView\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert newButtonBar != null : "fx:id=\"newButtonBar\" was not injected: check your FXML file 'EditCountry.fxml'.";

        localeComboBox.setItems(localeList);
        languageValidationLabel.visibleProperty().bind(localeComboBox.getSelectionModel().selectedItemProperty().isNull());

        Locale locale = model.getLocale();
        if (null != locale) {
            if (!localeList.contains(locale)) {
                localeList.add(locale);
            }
            clearAndSelect(localeComboBox, locale);
        }
        if (model.isNewRow()) {
            collapseNode(citiesLabel);
            collapseNode(citiesTableView);
            collapseNode(newButtonBar);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWCOUNTRY));
        } else {
            initializeEditMode();
            WaitTitledPane pane = new WaitTitledPane();
            pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                    .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
            waitBorderPane.startNow(pane, new ItemsLoadTask());
        }
        LOG.exiting(LOG.getName(), "initialize");
    }

    private void editItem(CityModel item) {
        try {
            EditCity.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void deleteItem(CityModel target) {
        CityOpRequestEvent deleteRequestEvent = new CityOpRequestEvent(target, this, true);
        Event.fireEvent(target.dataObject(), deleteRequestEvent);
        Stage stage = (Stage) getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stage, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    CityDAO.DeleteTask task = new CityDAO.DeleteTask(target, false);
                    task.setOnSucceeded((e) -> {
                        CityEvent cityEvent = (CityEvent) task.getValue();
                        if (null != cityEvent && cityEvent instanceof CityFailedEvent) {
                            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                                    ((ModelFailedEvent<CityDAO, CityModel>) cityEvent).getMessage(), ButtonType.OK);
                        }
                    });
                    waitBorderPane.startNow(task);
                }
            });
        }
    }

    private void initializeEditMode() {
        modelLocale = Bindings.createObjectBinding(() -> {
            Locale l = model.getLocale();
            if (null == l) {
                return null;
            }
            String s = l.toLanguageTag();
            return localeComboBox.getItems().stream().filter((t) -> t.toLanguageTag().equals(s)).findAny().orElse(null);
        }, model.localeProperty());
        modificationBinding = localeComboBox.getSelectionModel().selectedItemProperty().isNotEqualTo(modelLocale);
        modified.set(modificationBinding.get());
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
    public EntityModel.EntityModelFactory<CountryDAO, CountryModel> modelFactory() {
        return CountryModel.FACTORY;
    }

    @Override
    public boolean applyChanges() {
        model.setLocale(localeComboBox.getSelectionModel().getSelectedItem());
        return true;
    }

    private void onCityInserted(CitySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCityInserted", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            CityModel m = event.getEntityModel();
            if (m.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
                itemList.add(m);
            }
        }
        LOG.exiting(LOG.getName(), "onCityInserted");
    }

    private void onCityUpdated(CitySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCityUpdated", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            CityModel item = event.getEntityModel();
            if (item.getCountry().getPrimaryKey() != model.getPrimaryKey()) {
                itemList.remove(item);
            } else if (!itemList.contains(item)) {
                CityModel existing = ModelHelper.findByPrimaryKey(item.getPrimaryKey(), itemList).orElse(null);
                if (null == existing) {
                    itemList.add(item);
                } else {
                    itemList.set(itemList.indexOf(existing), item);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onCityUpdated");
    }

    private void onCityDeleted(CitySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCityDeleted", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            CityModel.FACTORY.find(itemList, event.getEntityModel()).ifPresent(itemList::remove);
        }
        LOG.exiting(LOG.getName(), "onCityDeleted");
    }

    private class ItemsLoadTask extends Task<List<CityDAO>> {

        private final int pk;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
            pk = model.getPrimaryKey();
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.country.EditCountry.ItemsLoadTask", "succeeded");
            super.succeeded();
            List<CityDAO> result = getValue();
            if (null != result && !result.isEmpty()) {
                result.stream().sorted(CityHelper::compare).forEach((t) -> itemList.add(t.cachedModel(true)));
            }
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.INSERT_SUCCESS, cityInsertEventHandler.getWeakEventHandler());
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.UPDATE_SUCCESS, cityUpdateEventHandler.getWeakEventHandler());
            CityModel.FACTORY.addEventHandler(CitySuccessEvent.DELETE_SUCCESS, cityDeleteEventHandler.getWeakEventHandler());
            LOG.exiting("scheduler.view.country.EditCountry.ItemsLoadTask", "succeeded");
        }

        @Override
        protected List<CityDAO> call() throws Exception {
            LOG.entering("scheduler.view.country.EditCountry.ItemsLoadTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CityDAO.FactoryImpl cf = CityDAO.FACTORY;
                LOG.exiting("scheduler.view.country.EditCountry.ItemsLoadTask", "call");
                return cf.load(dbConnector.getConnection(), cf.getByCountryFilter(pk));
            }
        }

    }

}
