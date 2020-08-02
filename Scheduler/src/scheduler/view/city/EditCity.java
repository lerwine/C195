package scheduler.view.city;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.PartialCountryDAO;
import scheduler.events.AddressEvent;
import scheduler.events.AddressFailedEvent;
import scheduler.events.AddressOpRequestEvent;
import scheduler.events.AddressSuccessEvent;
import scheduler.events.CityEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.CountryHelper;
import scheduler.model.fx.AddressModel;
import scheduler.model.fx.CityModel;
import scheduler.model.fx.CountryModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialCountryModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.clearAndSelectEntity;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.isInShownWindow;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ThrowableConsumer;
import scheduler.util.Tuple;
import scheduler.util.Values;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.EditItem;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.country.EditCountry;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing a {@link CityModel}.
 * <h3>Event Handling</h3>
 * <h4>SCHEDULER_ADDRESS_OP_REQUEST</h4>
 * <dl>
 * <dt>{@link #addressesTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link AddressOpRequestEvent} &#123;</dt>
 * <dd>{@link javafx.event.Event#eventType} = {@link AddressOpRequestEvent#ADDRESS_OP_REQUEST "SCHEDULER_ADDRESS_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr; {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(AddressOpRequestEvent)}
 * <dl>
 * <dt>SCHEDULER_ADDRESS_EDIT_REQUEST {@link AddressOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link AddressOpRequestEvent#EDIT_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link EditAddress#edit(AddressModel, javafx.stage.Window) EditAddress.edit}(({@link AddressModel}) {@link scheduler.events.ModelEvent#getEntityModel()},
 * {@link javafx.stage.Window}) (creates) {@link scheduler.events.AddressEvent#ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"} &rArr;
 * {@link scheduler.model.fx.AddressModel.Factory}</dd>
 * <dt>SCHEDULER_ADDRESS_DELETE_REQUEST {@link AddressOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link AddressOpRequestEvent#DELETE_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link scheduler.dao.AddressDAO.DeleteTask#DeleteTask(scheduler.model.fx.AddressModel, boolean) new AddressDAO.DeleteTask}({@link AddressOpRequestEvent},
 * {@code false}) (creates) {@link scheduler.events.AddressEvent#ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"} &rArr; {@link scheduler.model.fx.AddressModel.Factory}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/city/EditCity")
@FXMLResource("/scheduler/view/city/EditCity.fxml")
public final class EditCity extends VBox implements EditItem.ModelEditorController<CityModel> {

    private static final Object TARGET_COUNTRY_KEY = new Object();
    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCity.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(EditCity.class.getName());

    public static void editNew(PartialCountryModel<? extends PartialCountryDAO> country, Window parentWindow, boolean keepOpen, Consumer<CityModel> beforeShow) throws IOException {
        CityModel model = CityDAO.FACTORY.createNew().cachedModel(true);
        if (null != country) {
            model.setCountry(country);
        }
        if (null != beforeShow) {
            beforeShow.accept(model);
        }
        EditCity control = new EditCity();
        EditItem.showAndWait(parentWindow, control, model, keepOpen);
    }

    public static void editNew(PartialCountryModel<? extends PartialCountryDAO> country, Window parentWindow, boolean keepOpen) throws IOException {
        editNew(country, parentWindow, keepOpen, null);
    }

    public static void edit(CityModel model, Window parentWindow, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem.showAndWait(parentWindow, EditCity.class, model, false, beforeShow);
    }

    public static void edit(CityModel model, Window parentWindow) throws IOException {
        edit(model, parentWindow, null);
    }

    //<editor-fold defaultstate="collapsed" desc="Instance Fields">
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CountryModel> countryOptionList;
    private final ObservableList<AddressModel> addressItemList;
    private final WeakEventHandlingReference<AddressSuccessEvent> addressInsertEventHandler;
    private final WeakEventHandlingReference<AddressSuccessEvent> addressUpdateEventHandler;
    private final WeakEventHandlingReference<AddressSuccessEvent> addressDeleteEventHandler;
    private final WeakEventHandlingReference<CountrySuccessEvent> countryInsertEventHandler;
    private final WeakEventHandlingReference<CountrySuccessEvent> countryDeleteEventHandler;
    private ObjectBinding<CountryModel> selectedCountry;
    private StringBinding normalizedName;
    private StringBinding nameValidationMessage;

    @ModelEditor
    private CityModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="nameValidationLabel"
    private Label nameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountryModel> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryValidationLabel"
    private Label countryValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesLabel"
    private Label addressesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addCityButtonBar"
    private ButtonBar addCityButtonBar; // Value injected by FXMLLoader
    private BooleanBinding modificationBinding;
    private BooleanBinding validityBinding;

    //</editor-fold>
    public EditCity() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        countryOptionList = FXCollections.observableArrayList();
        addressItemList = FXCollections.observableArrayList();
        addressInsertEventHandler = WeakEventHandlingReference.create(this::onAddressInserted);
        addressUpdateEventHandler = WeakEventHandlingReference.create(this::onAddressUpdated);
        addressDeleteEventHandler = WeakEventHandlingReference.create(this::onAddressDeleted);
        countryInsertEventHandler = WeakEventHandlingReference.create(this::onCountryInserted);
        countryDeleteEventHandler = WeakEventHandlingReference.create(this::onCountryDeleted);
    }

    @ModelEditor
    private void onModelInserted(CityEvent event) {
        LOG.entering(LOG.getName(), "onModelInserted", event);
        restoreNode(addressesLabel);
        restoreNode(addressesTableView);
        restoreNode(addCityButtonBar);
        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.INSERT_SUCCESS, addressInsertEventHandler.getWeakEventHandler());
        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.UPDATE_SUCCESS, addressUpdateEventHandler.getWeakEventHandler());
        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.DELETE_SUCCESS, addressDeleteEventHandler.getWeakEventHandler());
        initializeEditMode();
        LOG.exiting(LOG.getName(), "onModelInserted");
    }

    @FXML
    void onAddAddressButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAddAddressButtonAction", event);
        try {
            EditAddress.editNew(model, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onAddAddressButtonAction");
    }

    @FXML
    void onAddressDeleteMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAddressDeleteMenuItemAction", event);
        AddressModel item = addressesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            deleteItem(item);
        }
        LOG.exiting(LOG.getName(), "onAddressDeleteMenuItemAction");
    }

    @FXML
    void onAddressEditMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAddressEditMenuItemAction", event);
        AddressModel item = addressesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            editItem(item);
        }
        LOG.exiting(LOG.getName(), "onAddressEditMenuItemAction");
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    void onAddressesTableViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onAddressesTableViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AddressModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = addressesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteItem(item);
                    }
                    break;
                case ENTER:
                    item = addressesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editItem(item);
                    }
                    break;
            }
        }
        LOG.exiting(LOG.getName(), "onAddressesTableViewKeyReleased");
    }

    @FXML
    void onItemActionRequest(AddressOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            editItem(event.getEntityModel());
        } else {
            deleteItem(event.getEntityModel());
        }
        LOG.exiting(LOG.getName(), "onItemActionRequest");
    }

    @FXML
    void onNewCountryButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCountryButtonAction", event);
        try {
            EditCountry.editNew(getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading country edit window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewCountryButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesLabel != null : "fx:id=\"addressesLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addCityButtonBar != null : "fx:id=\"addCityButtonBar\" was not injected: check your FXML file 'EditCity.fxml'.";

        countryComboBox.setItems(countryOptionList);
        addressesTableView.setItems(addressItemList);
        selectedCountry = Bindings.select(countryComboBox.selectionModelProperty(), "selectedItem");
        normalizedName = BindingHelper.asNonNullAndWsNormalized(nameTextField.textProperty());
        nameValidationMessage = Bindings.when(normalizedName.isEmpty())
                .then(resources.getString(RESOURCEKEY_REQUIRED))
                .otherwise(
                        Bindings.when(normalizedName.length().greaterThan(CityDAO.MAX_LENGTH_NAME))
                                .then(resources.getString(RESOURCEKEY_NAMETOOLONG)).otherwise("")
                );
        nameValidationLabel.textProperty().bind(nameValidationMessage);
        nameValidationLabel.visibleProperty().bind(nameValidationMessage.isNotEmpty());
        countryValidationLabel.visibleProperty().bind(selectedCountry.isNull());
        nameTextField.setText(model.getName());

        WaitTitledPane pane = WaitTitledPane.create();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        
        validityBinding = nameValidationMessage.isEmpty().and(selectedCountry.isNotNull());
        valid.set(validityBinding.get());
        validityBinding.addListener((observable, oldValue, newValue) -> valid.set(newValue));
        if (model.isNewRow()) {
            waitBorderPane.startNow(pane, new CountriesLoadTask());
            collapseNode(addressesLabel);
            collapseNode(addressesTableView);
            collapseNode(addCityButtonBar);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWCITY));
        } else {
            waitBorderPane.startNow(pane, new EditDataLoadTask());
            initializeEditMode();
        }
        LOG.exiting(LOG.getName(), "initialize");
    }

    private void editItem(AddressModel item) {
        try {
            EditAddress.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void deleteItem(AddressModel target) {
        AddressOpRequestEvent deleteRequestEvent = new AddressOpRequestEvent(target, this, true);
        Event.fireEvent(model.dataObject(), deleteRequestEvent);
        Window window = getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(window, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(window, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    DataAccessObject.DeleteDaoTask<AddressDAO, AddressModel> task = AddressModel.FACTORY.createDeleteTask(target);
                    task.setOnSucceeded((e) -> {
                        AddressEvent addressEvent = (AddressEvent) task.getValue();
                        if (null != addressEvent && addressEvent instanceof AddressFailedEvent) {
                            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                                    ((ModelFailedEvent<AddressDAO, AddressModel>) addressEvent).getMessage(), ButtonType.OK);
                        }
                    });
                    waitBorderPane.startNow(task);
                }
            });
        }
    }

    private void initializeEditMode() {
        windowTitle.bind(Bindings.format(resources.getString(RESOURCEKEY_EDITCITY), nameTextField.textProperty()));
        modified.set(false);
        modificationBinding = normalizedName.isNotEqualTo(model.nameProperty()).and(selectedCountry.isNotEqualTo(model.countryProperty()));
        modificationBinding.addListener((observable, oldValue, newValue) -> modified.set(newValue));
    }

    @Override
    public EntityModel.EntityModelFactory<CityDAO, CityModel> modelFactory() {
        return CityModel.FACTORY;
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

    private void loadCountries(List<CountryDAO> result, PartialCountryModel<? extends PartialCountryDAO> country) {
        HashMap<String, CountryModel> map = new HashMap<>();
        for (Locale al : Locale.getAvailableLocales()) {
            if (!(al.getDisplayCountry().isEmpty() || al.getDisplayLanguage().isEmpty())) {
                map.put(al.toLanguageTag(), CountryDAO.FACTORY.fromLocale(al).cachedModel(true));
            }
        }
        if (null != result && !result.isEmpty()) {
            result.forEach((t) -> map.put(t.getLocale().toLanguageTag(), t.cachedModel(true)));
            map.values().stream().sorted(CountryHelper::compare).forEach((t) -> countryOptionList.add(t));
            if (null != country) {
                Locale locale = country.getLocale();
                if (null != locale) {
                    String t = locale.toLanguageTag();
                    if (map.containsKey(t)) {
                        clearAndSelectEntity(countryComboBox, map.get(t));
                    } else {
                        String c = locale.getCountry();
                        CountryModel cm = map.values().stream().filter((u) -> u.getLocale().getCountry().equals(c)).findFirst().orElse(null);
                        clearAndSelectEntity(countryComboBox, cm);
                    }
                }
            }
        }
        CountryModel.FACTORY.addEventHandler(CountrySuccessEvent.INSERT_SUCCESS, countryInsertEventHandler.getWeakEventHandler());
        CountryModel.FACTORY.addEventHandler(CountrySuccessEvent.DELETE_SUCCESS, countryDeleteEventHandler.getWeakEventHandler());
    }

    @Override
    public boolean applyChanges() {
        model.setName(normalizedName.get());
        model.setCountry(selectedCountry.get());
        return true;
    }

    private void onAddressInserted(AddressSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAddressInserted", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AddressModel entityModel = event.getEntityModel();
            if (entityModel.getCity().getPrimaryKey() == model.getPrimaryKey()) {
                addressItemList.add(entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onAddressInserted");
    }

    private void onAddressUpdated(AddressSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAddressUpdated", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AddressModel entityModel = event.getEntityModel();
            AddressModel m = ModelHelper.findByPrimaryKey(entityModel.getPrimaryKey(), addressItemList).orElse(null);
            if (null != m) {
                if (entityModel.getCity().getPrimaryKey() != model.getPrimaryKey()) {
                    addressItemList.remove(m);
                }
            } else if (entityModel.getCity().getPrimaryKey() == model.getPrimaryKey()) {
                addressItemList.add(entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onAddressUpdated");
    }

    private void onAddressDeleted(AddressSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAddressDeleted", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AddressModel.FACTORY.find(addressItemList, event.getEntityModel()).ifPresent(addressItemList::remove);
        }
        LOG.exiting(LOG.getName(), "onAddressDeleted");
    }

    private void onCountryInserted(CountrySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCountryInserted", event);
        if (isInShownWindow(this)) {
            CountryModel countryModel = event.getEntityModel();
            countryOptionList.add(countryModel);
            countryOptionList.sort(CountryHelper::compare);
            clearAndSelectEntity(countryComboBox, countryModel);
        }
        LOG.exiting(LOG.getName(), "onCountryInserted");
    }

    private void onCountryDeleted(CountrySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCountryDeleted", event);
        if (isInShownWindow(this)) {
            ModelHelper.findByPrimaryKey(event.getEntityModel().getPrimaryKey(), countryOptionList).ifPresent((t) -> {
                CountryModel currentCountry = selectedCountry.get();
                if (null != currentCountry && currentCountry == t) {
                    countryComboBox.getSelectionModel().clearSelection();
                    countryOptionList.remove(t);
                }
            });
        }
        LOG.exiting(LOG.getName(), "onCountryDeleted");
    }

    private class EditDataLoadTask extends Task<Tuple<List<CountryDAO>, List<AddressDAO>>> {

        private final int pk;

        private EditDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES));
            pk = model.getPrimaryKey();
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.city.EditCity.EditDataLoadTask", "succeeded");
            super.succeeded();
            Tuple<List<CountryDAO>, List<AddressDAO>> result = getValue();
            loadCountries(result.getValue1(), model.getCountry());
            if (null != result.getValue2() && !result.getValue2().isEmpty()) {
                result.getValue2().forEach((t) -> addressItemList.add(t.cachedModel(true)));
            }
            AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.INSERT_SUCCESS, addressInsertEventHandler.getWeakEventHandler());
            AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.UPDATE_SUCCESS, addressUpdateEventHandler.getWeakEventHandler());
            AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.DELETE_SUCCESS, addressDeleteEventHandler.getWeakEventHandler());
            LOG.exiting("scheduler.view.city.EditCity.EditDataLoadTask", "succeeded");
        }

        @Override
        protected Tuple<List<CountryDAO>, List<AddressDAO>> call() throws Exception {
            LOG.entering("scheduler.view.city.EditCity.EditDataLoadTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AddressDAO.FactoryImpl cf = AddressDAO.FACTORY;
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                LOG.exiting("scheduler.view.city.EditCity.EditDataLoadTask", "call");
                return Tuple.of(
                        nf.load(dbConnector.getConnection(), nf.getAllItemsFilter()),
                        cf.load(dbConnector.getConnection(), cf.getByCityFilter(pk))
                );
            }
        }

    }

    private class CountriesLoadTask extends Task<List<CountryDAO>> {

        private CountriesLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES));
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.city.EditCity.CountriesLoadTask", "succeeded");
            super.succeeded();
            List<CountryDAO> result = getValue();
            PartialCountryModel<? extends PartialCountryDAO> targetCountry = model.getCountry();
            if (null == targetCountry) {
                ObservableMap<Object, Object> properties = getProperties();
                if (properties.containsKey(TARGET_COUNTRY_KEY)) {
                    targetCountry = (PartialCountryModel<? extends PartialCountryDAO>) properties.get(TARGET_COUNTRY_KEY);
                    properties.remove(TARGET_COUNTRY_KEY);
                }
            }
            loadCountries(result, targetCountry);
            LOG.exiting("scheduler.view.city.EditCity.CountriesLoadTask", "succeeded");
        }

        @Override
        protected List<CountryDAO> call() throws Exception {
            LOG.entering("scheduler.view.city.EditCity.CountriesLoadTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                LOG.exiting("scheduler.view.city.EditCity.CountriesLoadTask", "call");
                return nf.load(dbConnector.getConnection(), nf.getAllItemsFilter());
            }
        }

    }

}
