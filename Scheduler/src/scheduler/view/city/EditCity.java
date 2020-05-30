package scheduler.view.city;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
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
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
import scheduler.model.CountryProperties;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Tuple;
import scheduler.view.EditItem;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.city.EditCityResourceKeys.*;
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
    private static final Logger LOG = Logger.getLogger(EditCity.class.getName());

    public static CityModel edit(CityModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCity.class, model, false);
    }

    public static CityModel editNew(CountryModel country, Window parentWindow, boolean keepOpen) throws IOException {
        CityModel.Factory factory = CityModel.getFactory();
        CityModel model = factory.createNew(factory.getDaoFactory().createNew());
        EditCity control = new EditCity();
        if (null != country) {
            control.getProperties().put(TARGET_COUNTRY_KEY, country);
        }
        return EditItem.showAndWait(parentWindow, control, model, keepOpen);
    }

    private static int timeZoneCompare(TimeZone o1, TimeZone o2) {
        if (null == o1) {
            return (null == o2) ? 0 : 1;
        }
        if (null == o2) {
            return -1;
        }
        if (o1 == o2) {
            return 0;
        }
        int result = o1.getRawOffset() - o2.getRawOffset();
        if (result == 0 && (result = o1.getDisplayName().compareTo(o2.getDisplayName())) == 0) {
            ZoneId z1 = o1.toZoneId();
            ZoneId z2 = o2.toZoneId();
            if (null == z1) {
                return (null == z2) ? 0 : -1;
            }
            if (null == z2) {
                return 1;
            }
            Locale d = Locale.getDefault(Locale.Category.DISPLAY);
            if ((result = z1.getDisplayName(TextStyle.FULL, d).compareTo(z2.getDisplayName(TextStyle.FULL, d))) == 0
                    && (result = z1.getId().compareTo(z2.getId())) == 0) {
                return o1.getID().compareTo(o2.getID());
            }
        }
        return result;
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CountryItem<? extends ICountryDAO>> countryOptionList;
    private final ObservableList<TimeZone> timeZoneOptionList;
    private final ObservableList<AddressModel> addressItemList;
    private ObjectBinding<CountryItem<? extends ICountryDAO>> selectedCountry;
    private ObjectBinding<ZoneId> selectedZoneId;
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

    @FXML // fx:id="languageLabel"
    private Label languageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="zoneIdComboBox"
    private ComboBox<TimeZone> timeZoneComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="zoneIdValidationLabel"
    private Label timeZoneValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesLabel"
    private Label addressesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addCityButtonBar"
    private ButtonBar addCityButtonBar; // Value injected by FXMLLoader

    public EditCity() {
        windowTitle = new ReadOnlyStringWrapper("");
        valid = new ReadOnlyBooleanWrapper(false);
        modified = new ReadOnlyBooleanWrapper(false);
        countryOptionList = FXCollections.observableArrayList();
        timeZoneOptionList = FXCollections.observableArrayList();
        ZoneId.getAvailableZoneIds().stream().map((t) -> TimeZone.getTimeZone(t)).sorted(EditCity::timeZoneCompare)
                .forEach((t) -> timeZoneOptionList.add(t));
        addressItemList = FXCollections.observableArrayList();
    }

    @FXML
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
    private void onItemActionRequest(ItemActionRequestEvent<AddressModel> event) {
        if (event.isDelete()) {
            deleteAddress(event.getItem());
        } else {
            editAddress(event.getItem());
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert languageLabel != null : "fx:id=\"languageLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneValidationLabel != null : "fx:id=\"timeZoneValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesLabel != null : "fx:id=\"addressesLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addCityButtonBar != null : "fx:id=\"addCityButtonBar\" was not injected: check your FXML file 'EditCity.fxml'.";

        countryComboBox.setItems(countryOptionList);
        timeZoneComboBox.setItems(timeZoneOptionList);
        addressesTableView.setItems(addressItemList);
        selectedCountry = Bindings.select(countryComboBox.selectionModelProperty(), "selectedItem");
        ObjectBinding<TimeZone> selectedTimeZone = Bindings.select(timeZoneComboBox.selectionModelProperty(), "selectedItem");
        selectedZoneId = Bindings.createObjectBinding(() -> {
            TimeZone z = selectedTimeZone.get();
            return (null == z) ? null : z.toZoneId();
        }, selectedTimeZone);
        normalizedName = BindingHelper.asNonNullAndWsNormalized(nameTextField.textProperty());
        BooleanBinding nameValid = normalizedName.isNotEmpty();
        nameValidationLabel.visibleProperty().bind(nameValid.not());
        countryValidationLabel.visibleProperty().bind(selectedCountry.isNull());
        timeZoneValidationLabel.visibleProperty().bind(selectedTimeZone.isNull());
        
        BooleanBinding validationBinding = nameValid
                .and(selectedCountry.isNotNull())
                .and(selectedTimeZone.isNotNull());
        
        validationBinding.addListener((observable, oldValue, newValue) -> {
            valid.set(newValue);
        });
        valid.set(validationBinding.get());
        
        // Create binding that indicates when control values are different than the model values.
        BooleanBinding modificationBinding = Bindings.notEqual( // Name has changed
                normalizedName,
                BindingHelper.asNonNullAndWsNormalized(model.nameProperty())
        )
                .or(Bindings.notEqual( // Country has changed
                        selectedCountry,
                        model.countryProperty()
                ))
                .or(Bindings.notEqual( // Time zone has changed
                        Bindings.createStringBinding(() -> {
                            ZoneId z = selectedZoneId.get();
                            return (null == z) ? "" : z.getId();
                        }, selectedZoneId),
                        Bindings.createStringBinding(() -> {
                            ZoneId z = model.getZoneId();
                            return (null == z) ? "" : z.getId();
                        }, model.zoneIdProperty())
                ));
        
        modificationBinding.addListener((observable, oldValue, newValue) -> {
            modified.set(newValue);
        });
        modified.set(modificationBinding.get());
        
        nameTextField.setText(model.getName());
        languageLabel.textProperty().bind(Bindings.selectString(selectedCountry, "language"));
        ZoneId zoneId = model.getZoneId();
        if (null != zoneId) {
            String id = zoneId.getId();
            timeZoneOptionList.stream().filter((t) -> t.toZoneId().getId().equals(id)).findFirst()
                    .ifPresent((t) -> timeZoneComboBox.getSelectionModel().select(t));
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
            waitBorderPane.startNow(pane, new ItemsLoadTask());
            windowTitle.bind(Bindings.format(resources.getString(RESOURCEKEY_EDITCITY), nameTextField.textProperty()));
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

    @Override
    public void onNewModelSaved() {
        restoreNode(addressesLabel);
        restoreNode(addressesTableView);
        restoreNode(addCityButtonBar);
        windowTitle.bind(Bindings.format(resources.getString(RESOURCEKEY_EDITCITY), nameTextField.textProperty()));
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
        model.setZoneId(selectedZoneId.get());
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

    private class ItemsLoadTask extends Task<Tuple<List<CountryDAO>, List<AddressDAO>>> {

        private final int pk;

        private ItemsLoadTask() {
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
            ObservableMap<Object, Object> properties = getProperties();
            CountryItem<? extends ICountryDAO> targetCountry = model.getCountry();
            if (null == targetCountry && properties.containsKey(TARGET_COUNTRY_KEY)) {
                targetCountry = (CountryModel) properties.get(TARGET_COUNTRY_KEY);
                properties.remove(TARGET_COUNTRY_KEY);
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

        private final AddressModel model;
        private final Window parentWindow;
        private final AddressDAO dao;

        DeleteTask(AddressModel model, Window parentWindow) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            dao = model.dataObject();
            this.model = model;
            this.parentWindow = parentWindow;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            String message = getValue();
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(parentWindow, LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETEFAILURE), message);
            }
        }

        @Override
        protected String call() throws Exception {
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES));
                String message = AddressDAO.FACTORY.getDeleteDependencyMessage(model.dataObject(), connector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                AddressDAO.FACTORY.delete(dao, connector.getConnection());
                if (dao.getRowState() == DataRowState.DELETED) {
                    AddressModel.getFactory().updateItem(model, dao);
                }
            }
            return null;
        }
    }

}
