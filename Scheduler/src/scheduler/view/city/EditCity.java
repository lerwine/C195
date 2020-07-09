package scheduler.view.city;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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
import scheduler.RegionTable;
import scheduler.ZoneIdMappings;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
import scheduler.events.AddressEvent;
import scheduler.events.AddressFailedEvent;
import scheduler.events.AddressOpRequestEvent;
import scheduler.events.AddressSuccessEvent;
import scheduler.events.CityEvent;
import scheduler.events.CitySuccessEvent;
import scheduler.fx.TimeZoneListCellFactory;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ParentWindowShowingListener;
import scheduler.util.Tuple;
import scheduler.util.Values;
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
 * <dt>{@link #addressesTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates)
 * {@link AddressOpRequestEvent} &#123;</dt>
 * <dd>{@link javafx.event.Event#eventType} = {@link AddressOpRequestEvent#ADDRESS_OP_REQUEST "SCHEDULER_ADDRESS_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr;
 * {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(AddressOpRequestEvent)}
 * <dl>
 * <dt>SCHEDULER_ADDRESS_EDIT_REQUEST {@link AddressOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link AddressOpRequestEvent#EDIT_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link EditAddress#edit(AddressModel, javafx.stage.Window) EditAddress.edit}(({@link AddressModel}) {@link scheduler.events.ModelEvent#getFxRecordModel()},
 * {@link javafx.stage.Window}) (creates) {@link scheduler.events.AddressEvent#ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"} &rArr;
 * {@link scheduler.model.ui.AddressModel.Factory}</dd>
 * <dt>SCHEDULER_ADDRESS_DELETE_REQUEST {@link AddressOpRequestEvent} &#123;
 * {@link javafx.event.Event#eventType} = {@link AddressOpRequestEvent#DELETE_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link scheduler.dao.AddressDAO.DeleteTask#DeleteTask(scheduler.model.RecordModelContext, boolean) new AddressDAO.DeleteTask}({@link AddressOpRequestEvent},
 * {@code false}) (creates) {@link scheduler.events.AddressEvent#ADDRESS_EVENT_TYPE "SCHEDULER_ADDRESS_EVENT"} &rArr;
 * {@link scheduler.model.ui.AddressModel.Factory}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/city/EditCity")
@FXMLResource("/scheduler/view/city/EditCity.fxml")
public final class EditCity extends VBox implements EditItem.ModelEditor<CityDAO, CityModel, CityEvent> {

    private static final Object TARGET_COUNTRY_KEY = new Object();
    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCity.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditCity.class.getName());

    public static CityModel edit(CityModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCity.class, model, false);
    }

    public static CityModel editNew(CountryItem<? extends ICountryDAO> country, Window parentWindow, boolean keepOpen) throws IOException {
        CityModel.Factory factory = CityModel.FACTORY;
        CityModel model = factory.createNew(factory.getDaoFactory().createNew());
        EditCity control = new EditCity();
        if (null != country) {
            control.getProperties().put(TARGET_COUNTRY_KEY, country);
        }
        return EditItem.showAndWait(parentWindow, control, model, keepOpen);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CountryModel> countryOptionList;
    private final ObservableList<TimeZone> allTimeZones;
    private final ObservableList<TimeZone> timeZoneOptionList;
    private final ObservableList<AddressModel> addressItemList;
    private ObjectBinding<CountryModel> selectedCountry;
    private ObjectBinding<TimeZone> selectedTimeZone;
    private StringBinding normalizedName;
    private StringBinding nameValidationMessage;

    @ModelEditor
    private CityModel model;

    @ModelEditor
    private boolean keepOpen;

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

    @FXML // fx:id="timeZoneComboBox"
    private ComboBox<TimeZone> timeZoneComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneListCellFactory"
    private TimeZoneListCellFactory timeZoneListCellFactory; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneValidationLabel"
    private Label timeZoneValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="showAllTimeZonesCheckBox"
    private CheckBox showAllTimeZonesCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="addressesLabel"
    private Label addressesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addCityButtonBar"
    private ButtonBar addCityButtonBar; // Value injected by FXMLLoader
    private final ShowingChangedListener windowShowingChangedListener;

    public EditCity() {
        windowShowingChangedListener = new ShowingChangedListener();
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        countryOptionList = FXCollections.observableArrayList();
        allTimeZones = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(RegionTable.getAllTimeZones()));
        timeZoneOptionList = FXCollections.observableArrayList();
        timeZoneOptionList.addAll(allTimeZones);
        addressItemList = FXCollections.observableArrayList();
    }

    @FXML
    void onAddAddressButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAddAddressButtonAction", event);
        try {
            EditAddress.editNew(model, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    void onAddressDeleteMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAddressDeleteMenuItemAction", event);
        AddressModel item = addressesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            deleteItem(item);
        }
    }

    @FXML
    void onAddressEditMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAddressEditMenuItemAction", event);
        AddressModel item = addressesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            editItem(item);
        }
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
    }

    @FXML
    void onCountryComboBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCountryComboBoxAction", event);
        onChange(nameTextField.getText());
        onZoneOptionChange(selectedCountry.get());
    }

    @FXML
    void onTimeZoneComboBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onTimeZoneComboBoxAction", event);
        onChange(nameTextField.getText());
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    void onItemActionRequest(AddressOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            try {
                EditAddress.edit(event.getFxRecordModel(), getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        } else {
            deleteItem(event.getFxRecordModel());
        }
    }

    @FXML
    void onNewCountryButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCountryButtonAction", event);
        CountryModel c;
        try {
            c = EditCountry.editNew(getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading country edit window", ex);
            c = null;
        }
        if (null != c) {
            countryOptionList.add(c);
            countryOptionList.sort(CountryProperties::compare);
            countryComboBox.getSelectionModel().select(c);
        }
    }

    @FXML
    void onShowAllTimeZonesCheckBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onShowAllTimeZonesCheckBoxAction", event);
        CountryModel country = selectedCountry.get();
        if (null != country) {
            onZoneOptionChange(country);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneListCellFactory != null : "fx:id=\"timeZoneListCellFactory\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneValidationLabel != null : "fx:id=\"timeZoneValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert showAllTimeZonesCheckBox != null : "fx:id=\"showAllTimeZonesCheckBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesLabel != null : "fx:id=\"addressesLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addCityButtonBar != null : "fx:id=\"addCityButtonBar\" was not injected: check your FXML file 'EditCity.fxml'.";

        countryComboBox.setItems(countryOptionList);
        timeZoneComboBox.setItems(timeZoneOptionList);
        addressesTableView.setItems(addressItemList);
        selectedCountry = Bindings.select(countryComboBox.selectionModelProperty(), "selectedItem");
        selectedTimeZone = Bindings.select(timeZoneComboBox.selectionModelProperty(), "selectedItem");
        StringBinding selectedTzCode = Bindings.createStringBinding(() -> {
            TimeZone tz = selectedTimeZone.get();
            if (null == tz) {
                return "";
            }
            return ZoneIdMappings.fromZoneId(tz.toZoneId().getId());
        }, selectedTimeZone);
        normalizedName = BindingHelper.asNonNullAndWsNormalized(nameTextField.textProperty());
        nameValidationMessage = Bindings.createStringBinding(() -> {
            String c = selectedTzCode.get();
            String nn = normalizedName.get();
            if (nn.isEmpty()) {
                return resources.getString(RESOURCEKEY_REQUIRED);
            }
            if (nn.contains(";")) {
                return resources.getString(RESOURCEKEY_CANNOTCONTAINSEMICOLON);
            }
            int maxLen = CityDAO.MAX_LENGTH_NAME - c.length() - 1;
            LOG.fine(() -> String.format("Testing max lengh of %d", maxLen));
            if (nn.length() > maxLen) {
                return resources.getString(RESOURCEKEY_NAMETOOLONG);
            }
            return "";
        }, normalizedName, selectedTzCode);
        nameValidationLabel.textProperty().bind(nameValidationMessage);
        nameValidationLabel.visibleProperty().bind(nameValidationMessage.isNotEmpty());
        countryValidationLabel.visibleProperty().bind(selectedCountry.isNull());
        timeZoneValidationLabel.visibleProperty().bind(selectedTimeZone.isNull());
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> onChange(newValue));
        nameTextField.setText(model.getName());
        TimeZone zoneId = model.getTimeZone();
        if (null != zoneId) {
            String id = zoneId.toZoneId().getId();
            timeZoneOptionList.stream().filter((t) -> t.toZoneId().getId().equals(id)).findFirst()
                    .ifPresent((t) -> {
                        timeZoneComboBox.getSelectionModel().select(t);
                    });
        }

        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
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
        windowShowingChangedListener.initialize(sceneProperty());
    }

    private void onCityInserted(CitySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCityInserted", event);
        windowShowingChangedListener.isInsert = false;
        model.dataObject().removeEventHandler(CitySuccessEvent.INSERT_SUCCESS, this::onCityInserted);
        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.INSERT_SUCCESS, this::onAddressAdded);
        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.UPDATE_SUCCESS, this::onAddressUpdated);
        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.DELETE_SUCCESS, this::onAddressDeleted);
        restoreNode(addressesLabel);
        restoreNode(addressesTableView);
        restoreNode(addCityButtonBar);
        initializeEditMode();
    }

    private void editItem(AddressModel item) {
        try {
            EditAddress.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void deleteItem(AddressModel target) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            DataAccessObject.DeleteDaoTask<AddressDAO, AddressModel, AddressEvent> task = AddressModel.FACTORY.createDeleteTask(target);
            task.setOnSucceeded((e) -> {
                AddressEvent addressEvent = task.getValue();
                if (null != addressEvent && addressEvent instanceof AddressFailedEvent) {
                    scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                            ((AddressFailedEvent) addressEvent).getMessage(), ButtonType.OK);
                }
            });
            waitBorderPane.startNow(task);
        }
    }

    private void onChange(String cityName) {
        valid.set(Values.isNotNullWhiteSpaceOrEmpty(cityName) && null != selectedCountry.get() && null != selectedTimeZone.get());
        if (model.getRowState() != DataRowState.NEW) {
            if (normalizedName.get().equals(Values.asNonNullAndWsNormalized(model.getName()))
                    && ModelHelper.areSameRecord(selectedCountry.get(), model.getCountry())) {
                TimeZone z1 = selectedTimeZone.get();
                TimeZone z2 = model.getTimeZone();
                if ((null == z1) ? null == z2 : null != z2 && z1.getID().equals(z2.getID())) {
                    modified.set(false);
                    return;
                }
            }
            modified.set(true);
        }
    }

    private void initializeEditMode() {
        windowTitle.bind(Bindings.format(resources.getString(RESOURCEKEY_EDITCITY), nameTextField.textProperty()));
        modified.set(false);
    }

    private void onZoneOptionChange(CountryModel country) {
        if (null != country) {
            Locale locale = country.getLocale();
            if (null != locale && !showAllTimeZonesCheckBox.isSelected()) {
                timeZoneListCellFactory.setCurrentCountry(null);
                List<TimeZone> zonesForCountry = RegionTable.getZonesForCountry(locale.getCountry());
                if (!zonesForCountry.isEmpty()) {
                    TimeZone selTz = selectedTimeZone.get();
                    timeZoneOptionList.setAll(zonesForCountry);
                    if (null != selTz) {
                        if (!timeZoneOptionList.contains(selTz)) {
                            timeZoneOptionList.add(selTz);
                        }
                        timeZoneComboBox.getSelectionModel().select(selTz);
                    }
                    return;
                }
            }
        }
        timeZoneListCellFactory.setCurrentCountry(country);
        if (timeZoneOptionList.size() != allTimeZones.size()) {
            TimeZone selTz = selectedTimeZone.get();
            timeZoneOptionList.setAll(allTimeZones);
            if (null != selTz) {
                timeZoneComboBox.getSelectionModel().select(selTz);
            }
        }
    }

    private void onAddressAdded(AddressSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAddressAdded", event);
        if (model.getRowState() != DataRowState.NEW) {
            AddressDAO dao = event.getDataAccessObject();
            if (dao.getCity().getPrimaryKey() == model.getPrimaryKey()) {
                addressItemList.add(new AddressModel(dao));
            }
        }
    }

    private void onAddressUpdated(AddressSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAddressUpdated", event);
        if (model.getRowState() != DataRowState.NEW) {
            AddressDAO dao = event.getDataAccessObject();
            int pk = dao.getPrimaryKey();
            AddressModel m = addressItemList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != m) {
                if (dao.getCity().getPrimaryKey() != model.getPrimaryKey()) {
                    addressItemList.remove(m);
                }
            } else if (dao.getCity().getPrimaryKey() == model.getPrimaryKey()) {
                addressItemList.add(new AddressModel(dao));
            }
        }
    }

    private void onAddressDeleted(AddressSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAddressDeleted", event);
        AddressModel.FACTORY.find(addressItemList, event.getDataAccessObject()).ifPresent((t) -> {
            addressItemList.remove(t);
        });
    }

    @Override
    public FxRecordModel.FxModelFactory<CityDAO, CityModel, CityEvent> modelFactory() {
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

    private void loadCountries(List<CountryDAO> result, CountryItem<? extends ICountryDAO> country) {
        HashMap<String, CountryModel> map = new HashMap<>();
        CountryModel.Factory factory = CountryModel.FACTORY;
        for (Locale al : Locale.getAvailableLocales()) {
            if (!(al.getDisplayCountry().isEmpty() || al.getDisplayLanguage().isEmpty())) {
                map.put(al.toLanguageTag(), factory.createNew(CountryDAO.FACTORY.fromLocale(al)));
            }
        }
        if (null != result && !result.isEmpty()) {
            result.forEach((t) -> map.put(t.getLocale().toLanguageTag(), factory.createNew(t)));
            map.values().stream().sorted(CountryProperties::compare).forEach((t) -> countryOptionList.add(t));
            if (null != country) {
                Locale locale = country.getLocale();
                if (null != locale) {
                    String t = locale.toLanguageTag();
                    if (map.containsKey(t)) {
                        countryComboBox.getSelectionModel().select(map.get(t));
                    } else {
                        String c = locale.getCountry();
                        CountryModel cm = map.values().stream().filter((u) -> u.getLocale().getCountry().equals(c)).findFirst().orElse(null);
                        if (null != cm) {
                            countryComboBox.getSelectionModel().select(cm);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void applyChanges() {
        model.setName(normalizedName.get());
        model.setCountry(selectedCountry.get());
        model.setTimeZone(selectedTimeZone.get());
    }

    private class ShowingChangedListener extends ParentWindowShowingListener {

        private boolean isAttached = false;
        private boolean isInsert;

        @Override
        public void initialize(ReadOnlyObjectProperty<Scene> sceneProperty) {
            isInsert = model.isNewRow();
            super.initialize(sceneProperty);
        }

        @Override
        protected synchronized void onShowingChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            super.onShowingChanged(observable, oldValue, newValue);
            if (newValue) {
                if (!isAttached) {
                    if (isInsert) {
                        if (keepOpen) {
                            model.dataObject().addEventHandler(CitySuccessEvent.INSERT_SUCCESS, EditCity.this::onCityInserted);
                            isAttached = true;
                        }
                    } else {
                        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.INSERT_SUCCESS, EditCity.this::onAddressAdded);
                        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.UPDATE_SUCCESS, EditCity.this::onAddressUpdated);
                        AddressModel.FACTORY.addEventHandler(AddressSuccessEvent.DELETE_SUCCESS, EditCity.this::onAddressDeleted);
                        isAttached = true;
                    }
                }
            } else if (isAttached) {
                if (isInsert) {
                    model.dataObject().removeEventHandler(CitySuccessEvent.INSERT_SUCCESS, EditCity.this::onCityInserted);
                } else {
                    AddressModel.FACTORY.removeEventHandler(AddressSuccessEvent.INSERT_SUCCESS, EditCity.this::onAddressAdded);
                    AddressModel.FACTORY.removeEventHandler(AddressSuccessEvent.UPDATE_SUCCESS, EditCity.this::onAddressUpdated);
                    AddressModel.FACTORY.removeEventHandler(AddressSuccessEvent.DELETE_SUCCESS, EditCity.this::onAddressDeleted);
                }
                isAttached = false;
            }
        }

    }

    private class EditDataLoadTask extends Task<Tuple<List<CountryDAO>, List<AddressDAO>>> {

        private final int pk;

        private EditDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES));
            pk = model.getPrimaryKey();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            Tuple<List<CountryDAO>, List<AddressDAO>> result = getValue();
            loadCountries(result.getValue1(), model.getCountry());
            if (null != result.getValue2() && !result.getValue2().isEmpty()) {
                AddressModel.Factory factory = AddressModel.FACTORY;
                result.getValue2().forEach((t) -> addressItemList.add(factory.createNew(t)));
            }
        }

        @Override
        protected Tuple<List<CountryDAO>, List<AddressDAO>> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AddressDAO.FactoryImpl cf = AddressDAO.FACTORY;
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
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
            super.succeeded();
            List<CountryDAO> result = getValue();
            CountryItem<? extends ICountryDAO> targetCountry = model.getCountry();
            if (null == targetCountry) {
                ObservableMap<Object, Object> properties = getProperties();
                if (properties.containsKey(TARGET_COUNTRY_KEY)) {
                    targetCountry = (CountryModel) properties.get(TARGET_COUNTRY_KEY);
                    properties.remove(TARGET_COUNTRY_KEY);
                }
            }
            loadCountries(result, targetCountry);
        }

        @Override
        protected List<CountryDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                return nf.load(dbConnector.getConnection(), nf.getAllItemsFilter());
            }
        }

    }

}
