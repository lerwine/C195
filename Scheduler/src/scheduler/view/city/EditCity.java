package scheduler.view.city;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.PredefinedData;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.ObservableObjectDerivitive;
import scheduler.observables.ObservableStringDerivitive;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
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

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CountryModel> countryOptionList;
    private final ObservableList<CityModel> cityOptionList;
    private final ObservableList<AddressModel> addressItemList;
    private ObservableObjectDerivitive<CityModel> selectedCity;
    private ObservableObjectDerivitive<CountryModel> selectedCountry;

    @ModelEditor
    private CityModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="nameValueLabel"
    private Label nameValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="nameValueComboBox"
    private ComboBox<CityModel> nameValueComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="nameValidationLabel"
    private Label nameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameValueLabel"
    private Label countryNameValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameValueComboBox"
    private ComboBox<CountryModel> countryNameValueComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameValidationLabel"
    private Label countryNameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="languageLabel"
    private Label languageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneLabel"
    private Label timeZoneLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesLabel"
    private Label addressesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addCityButtonBar"
    private ButtonBar addCityButtonBar; // Value injected by FXMLLoader

    public EditCity() {
        windowTitle = new ReadOnlyStringWrapper();
        valid = new ReadOnlyBooleanWrapper();
        addressItemList = FXCollections.observableArrayList();
        countryOptionList = FXCollections.observableArrayList();
        cityOptionList = FXCollections.observableArrayList();
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
    void initialize() {
        assert nameValueLabel != null : "fx:id=\"nameValueLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert nameValueComboBox != null : "fx:id=\"nameValueComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryNameValueLabel != null : "fx:id=\"countryNameValueLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryNameValueComboBox != null : "fx:id=\"countryNameValueComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryNameValidationLabel != null : "fx:id=\"countryNameValidationLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert languageLabel != null : "fx:id=\"languageLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneLabel != null : "fx:id=\"timeZoneLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesLabel != null : "fx:id=\"addressesLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addCityButtonBar != null : "fx:id=\"addCityButtonBar\" was not injected: check your FXML file 'EditCity.fxml'.";

    }

    @Override
    public void onEditNew() {
        selectedCountry = ObservableObjectDerivitive.ofSelection(countryNameValueComboBox);
        selectedCity = ObservableObjectDerivitive.ofSelection(nameValueComboBox);

        countryNameValueComboBox.setItems(countryOptionList);
        ObservableMap<Object, Object> properties = getProperties();
        CountryModel targetCountry;
        if (properties.containsKey(TARGET_COUNTRY_KEY)) {
            targetCountry = (CountryModel) properties.get(TARGET_COUNTRY_KEY);
            CountryDAO.PredefinedCountryElement predefinedElement = targetCountry.getPredefinedElement();
            PredefinedData.getCountryOptions(null).sorted(Country::compare).forEach((t)
                    -> countryOptionList.add((Objects.equals(t.getPredefinedElement(), predefinedElement)) ? targetCountry : new CountryModel(t)));
        } else {
            targetCountry = null;
            PredefinedData.getCountryOptions(null).sorted(Country::compare).forEach((t) -> countryOptionList.add(new CountryModel(t)));
        }
        nameValueComboBox.setItems(cityOptionList);
        selectedCountry.addListener((observable, oldValue, newValue) -> {
            nameValueComboBox.getSelectionModel().clearSelection();
            cityOptionList.clear();
            if (null == newValue) {
                restoreNode(countryNameValidationLabel);
            }
            // TODO: Filter out existing items (since we won't be able to add duplicate-named cities, anyway)
            newValue.getPredefinedElement().getCities().stream().map((t) -> t.getDataAccessObject()).sorted(City::compare).forEach((t) -> {
                cityOptionList.add(new CityModel(t));
            });
        });
        countryNameValidationLabel.visibleProperty().bind(selectedCountry.isNull());
        valid.bind(selectedCity.isNotNull());
        nameValidationLabel.textProperty().bind(ObservableStringDerivitive.of(selectedCountry, selectedCity, (t, u) -> {
            if (null == t) {
                return "Country must be selected";
            }
            return (null == u) ? "* Required" : "";
        }));
        nameValidationLabel.visibleProperty().bind(selectedCity.isNull());
        languageLabel.textProperty().bind(ObservableStringDerivitive.ofNested(selectedCity, (t) -> t.languageProperty()));
        timeZoneLabel.textProperty().bind(ObservableStringDerivitive.ofNested(selectedCity, (t) -> t.timeZoneDisplayProperty()));
        if (null != targetCountry) {
            countryNameValueComboBox.getSelectionModel().select(targetCountry);
        } else {
            CityDAO.PredefinedCityElement predefinedElement = model.getPredefinedElement();
            if (null != predefinedElement) {
                CityDAO cityDao = predefinedElement.getDataAccessObject();
                ICountryDAO countryDao = cityDao.getCountry();
                countryOptionList.stream().filter((t) -> Objects.equals(t.getDataObject(), countryDao)).findFirst().ifPresent((t) -> {
                    countryNameValueComboBox.getSelectionModel().select(t);
                    cityOptionList.stream().filter((u) -> Objects.equals(u.getDataObject(), cityDao)).findFirst().ifPresent((u) -> {
                        nameValueComboBox.getSelectionModel().select(u);
                    });
                });
            }
        }
    }

    @Override
    public void onEditExisting(boolean isInitialize) {
        if (!isInitialize) {
            valid.unbind();
            countryNameValidationLabel.visibleProperty().unbind();
            nameValidationLabel.textProperty().unbind();
            nameValidationLabel.visibleProperty().unbind();
            languageLabel.textProperty().unbind();
            timeZoneLabel.textProperty().unbind();
        }
        valid.set(true);
        countryNameValidationLabel.setVisible(false);
        nameValidationLabel.setVisible(false);
        collapseNode(nameValueComboBox);
        restoreNode(nameValueLabel);
        collapseNode(countryNameValueComboBox);
        restoreNode(countryNameValueLabel);
        restoreNode(addressesLabel);
        restoreNode(addressesTableView);
        restoreNode(addCityButtonBar);
        windowTitle.set(String.format(resources.getString(RESOURCEKEY_EDITCITY), model.getName()));
        addressesTableView.setItems(addressItemList);
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        waitBorderPane.startNow(pane, new ItemsLoadTask());
        nameValueLabel.setText(model.getName());
        countryNameValueLabel.setText(model.getCountryName());
        languageLabel.setText(model.getLanguage());
        timeZoneLabel.setText(model.getTimeZoneDisplay());
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
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    @Override
    public void updateModel() {
        model.setPredefinedElement(nameValueComboBox.getSelectionModel().getSelectedItem().getPredefinedElement());
    }

    private class ItemsLoadTask extends Task<List<AddressDAO>> {

        private final int pk;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES));
            pk = model.getPrimaryKey();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            List<AddressDAO> result = getValue();
            if (null != result && !result.isEmpty()) {
                AddressModel.Factory factory = AddressModel.getFactory();
                result.forEach((t) -> {
                    addressItemList.add(factory.createNew(t));
                });
            }
        }

        @Override
        protected List<AddressDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AddressDAO.FactoryImpl cf = AddressDAO.getFactory();
                return cf.load(dbConnector.getConnection(), cf.getByCityFilter(pk));
            }
        }

    }

    private class DeleteTask extends Task<String> {

        private final AddressModel model;
        private final Window parentWindow;
        private final AddressDAO dao;

        DeleteTask(AddressModel model, Window parentWindow) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            dao = model.getDataObject();
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
                String message = AddressDAO.getFactory().getDeleteDependencyMessage(model.getDataObject(), connector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                AddressDAO.getFactory().delete(dao, connector.getConnection());
                if (dao.getRowState() == DataRowState.DELETED) {
                    AddressModel.getFactory().updateItem(model, dao);
                }
            }
            return null;
        }
    }

}
