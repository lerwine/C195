package scheduler.view.city;

import java.io.IOException;
import java.util.Arrays;
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
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import javafx.stage.WindowEvent;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.ZoneIdMappings;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
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
import scheduler.util.ParentWindowChangeListener;
import scheduler.util.Tuple;
import scheduler.util.Values;
import scheduler.view.EditItem;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.country.EditCountry;
import scheduler.view.event.AddressEvent;
import scheduler.view.event.ItemActionRequestEvent;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing a {@link CityModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/city/EditCity.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/city/EditCity")
@FXMLResource("/scheduler/view/city/EditCity.fxml")
public final class EditCity extends VBox implements EditItem.ModelEditor<CityDAO, CityModel> {

    private static final Object TARGET_COUNTRY_KEY = new Object();
    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCity.class.getName()), Level.FINER);

    public static CityModel edit(CityModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCity.class, model, false);
    }

    public static CityModel editNew(CountryItem<? extends ICountryDAO> country, Window parentWindow, boolean keepOpen) throws IOException {
        CityModel.Factory factory = CityModel.getFactory();
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
    private final ObservableList<CountryItem<? extends ICountryDAO>> countryOptionList;
    private final ObservableList<TimeZone> timeZoneOptionList;
    private final ObservableList<AddressModel> addressItemList;
    private ObjectBinding<CountryItem<? extends ICountryDAO>> selectedCountry;
    private ObjectBinding<TimeZone> selectedTimeZone;
    private StringBinding normalizedName;

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
    private ComboBox<CountryItem<? extends ICountryDAO>> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryValidationLabel"
    private Label countryValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneComboBox"
    private ComboBox<TimeZone> timeZoneComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneValidationLabel"
    private Label timeZoneValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesLabel"
    private Label addressesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addCityButtonBar"
    private ButtonBar addCityButtonBar; // Value injected by FXMLLoader
    private StringBinding nameValidationMessage;

    public EditCity() {
        windowTitle = new ReadOnlyStringWrapper("");
        valid = new ReadOnlyBooleanWrapper(false);
        modified = new ReadOnlyBooleanWrapper(true);
        countryOptionList = FXCollections.observableArrayList();
        timeZoneOptionList = FXCollections.observableArrayList();
        Arrays.stream(TimeZone.getAvailableIDs()).map((t) -> TimeZone.getTimeZone(t)).sorted(Values::compareTimeZones)
                .forEach((t) -> timeZoneOptionList.add(t));
        addressItemList = FXCollections.observableArrayList();
    }

    @FXML
    @SuppressWarnings("unused-parameter")
    private void onAddAddressButtonAction(ActionEvent event) {
        try {
            EditAddress.editNew(model, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    private void onAddressDeleteMenuItemAction(ActionEvent event) {
        deleteAddress(addressesTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onAddressEditMenuItemAction(ActionEvent event) {
        editAddress(addressesTableView.getSelectionModel().getSelectedItem());
    }

    @SuppressWarnings("incomplete-switch")
    @FXML
    private void onAddressesTableViewKeyReleased(KeyEvent event) {
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AddressModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = addressesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteAddress(item);
                    }
                    break;
                case ENTER:
                    item = addressesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editAddress(item);
                    }
                    break;
            }
        }
    }

    @FXML
    private void onComboBoxAction(ActionEvent event) {
        onChange(nameTextField.getText());
    }

    @FXML
    private void onItemActionRequest(ItemActionRequestEvent<AddressModel> event) {
        if (event.isDelete()) {
            deleteAddress(event.getItem());
        } else {
            editAddress(event.getItem());
        }
    }

    @FXML
    private void onNewCountryButtonAction(ActionEvent event) {
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

    private void onChange(String cityName) {
        valid.set(Values.isNotNullWhiteSpaceOrEmpty(cityName) && null != selectedCountry.get() && null != selectedTimeZone.get());
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

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneValidationLabel != null : "fx:id=\"timeZoneValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
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
                    .ifPresent((t) -> timeZoneComboBox.getSelectionModel().select(t));
        }

        ParentWindowChangeListener.setWindowChangeListener(this, new ChangeListener<Window>() {
            private boolean isListening = false;

            @Override
            public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                if (null != oldValue) {
                    oldValue.removeEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowHidden);
                }
                if (null != newValue) {
                    oldValue.addEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowHidden);
                    onChange(true);
                } else {
                    onChange(false);
                }
            }

            private void onWindowHidden(WindowEvent event) {
                onChange(false);
            }

            private void onChange(boolean hasParent) {
                if (hasParent) {
                    if (!isListening) {
                        getMainController().addModelEventHandler(AddressEvent.ADDRESS_INSERTED_EVENT, EditCity.this::onAddressAdded);
                        getMainController().addModelEventHandler(AddressEvent.ADDRESS_UPDATED_EVENT, EditCity.this::onAddressUpdated);
                        getMainController().addModelEventHandler(AddressEvent.ADDRESS_DELETED_EVENT, EditCity.this::onAddressDeleted);
                        isListening = true;
                    }
                } else if (isListening) {
                    getMainController().addModelEventHandler(AddressEvent.ADDRESS_INSERTED_EVENT, EditCity.this::onAddressAdded);
                    getMainController().addModelEventHandler(AddressEvent.ADDRESS_UPDATED_EVENT, EditCity.this::onAddressUpdated);
                    getMainController().addModelEventHandler(AddressEvent.ADDRESS_DELETED_EVENT, EditCity.this::onAddressDeleted);
                    isListening = false;
                }
            }

        });

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
    }

    private void deleteAddress(AddressModel item) {
        if (null != item) {
            Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            if (response.isPresent() && response.get() == ButtonType.YES) {
                waitBorderPane.startNow(new DeleteTask(item, getScene().getWindow()));
            }
        }
    }

    private void editAddress(AddressModel item) {
        if (null != item) {
            try {
                EditAddress.edit(item, getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        }
    }

    private void initializeEditMode() {
        windowTitle.bind(Bindings.format(resources.getString(RESOURCEKEY_EDITCITY), nameTextField.textProperty()));
    }

    private void onAddressAdded(AddressEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AddressDAO dao = event.getDataAccessObject();
            // TODO: Update model if null
            if (dao.getCity().getPrimaryKey() == model.getPrimaryKey()) {
                addressItemList.add(new AddressModel(dao));
            }
        }
    }

    private void onAddressUpdated(AddressEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AddressDAO dao = event.getDataAccessObject();
            // TODO: Update model if null
            int pk = dao.getPrimaryKey();
            AddressModel m = addressItemList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != m) {
                AddressModel.getFactory().updateItem(m, dao);
                if (dao.getCity().getPrimaryKey() != model.getPrimaryKey()) {
                    addressItemList.remove(m);
                }
            } else if (dao.getCity().getPrimaryKey() == model.getPrimaryKey()) {
                addressItemList.add(new AddressModel(dao));
            }
        }
    }

    private void onAddressDeleted(AddressEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AddressDAO dao = event.getDataAccessObject();
            // TODO: Update model if null
            int pk = dao.getPrimaryKey();
            addressItemList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> addressItemList.remove(t));
        }
    }

    @Override
    public void onNewModelSaved() {
        restoreNode(addressesLabel);
        restoreNode(addressesTableView);
        restoreNode(addCityButtonBar);
    }

    @Override
    public FxRecordModel.ModelFactory<CityDAO, CityModel> modelFactory() {
        return CityModel.getFactory();
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
    public void updateModel() {
        model.setName(normalizedName.get());
        model.setCountry(selectedCountry.get());
        model.setTimeZone(selectedTimeZone.get());
    }

    private void loadCountries(List<CountryDAO> result, CountryItem<? extends ICountryDAO> country) {
        HashMap<String, CountryItem<? extends ICountryDAO>> map = new HashMap<>();
        CountryModel.Factory factory = CountryModel.getFactory();
        for (Locale al : Locale.getAvailableLocales()) {
            if (!(al.getDisplayCountry().isEmpty() || al.getDisplayLanguage().isEmpty())) {
                map.put(al.toLanguageTag(), factory.createNew(CountryDAO.FACTORY.fromLocale(al)));
            }
        }
        if (null != result && !result.isEmpty()) {
            result.forEach((t) -> map.put(t.getLocale().toLanguageTag(), factory.createNew(t)));
        }
        if (null != country) {
            Locale locale = country.getLocale();
            if (null != locale) {
                map.put(locale.toLanguageTag(), country);
            }
        }
        map.values().stream().sorted(CountryProperties::compare).forEach((t) -> countryOptionList.add(t));
        if (null != country) {
            countryComboBox.getSelectionModel().select(country);
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
                AddressModel.Factory factory = AddressModel.getFactory();
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

    private class DeleteTask extends Task<String> {

        private final AddressModel addressModel;
        private final Window parentWindow;
        private final AddressDAO dao;

        DeleteTask(AddressModel model, Window parentWindow) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            dao = model.dataObject();
            addressModel = model;
            this.parentWindow = parentWindow;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            String message = getValue();
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(parentWindow, LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETEFAILURE), message);
            } else if (dao.getRowState() == DataRowState.DELETED) {
                AddressModel.getFactory().updateItem(addressModel, dao);
            }
        }

        @Override
        protected String call() throws Exception {
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES));
                String message = AddressDAO.FACTORY.getDeleteDependencyMessage(dao, connector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                AddressDAO.FACTORY.delete(dao, connector.getConnection());
            }
            return null;
        }
    }

}
