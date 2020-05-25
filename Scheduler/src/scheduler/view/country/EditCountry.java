package scheduler.view.country;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.event.CityDaoEvent;
import scheduler.fx.ErrorDetailControl;
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.PredefinedData;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.ObservableObjectDerivitive;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.city.EditCity;
import static scheduler.view.country.EditCountryResourceKeys.*;
import scheduler.view.event.ItemActionRequestEvent;
import scheduler.view.task.WaitBorderPane;

/**
 * FXML Controller class for editing a {@link CountryModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/country/EditCountry.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/EditCountry")
@FXMLResource("/scheduler/view/country/EditCountry.fxml")
public final class EditCountry extends VBox implements EditItem.ModelEditor<CountryDAO, CountryModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCountry.class.getName()), Level.FINER);

    public static CountryModel editNew(Window parentWindow, boolean keepOpen) throws IOException {
        CountryModel.Factory factory = CountryModel.getFactory();

        return EditItem.showAndWait(parentWindow, EditCountry.class, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static CountryModel edit(CountryModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCountry.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CityModel> itemList;
    private final ObservableList<CountryModel> countryList;

    @ModelEditor
    private CountryModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="countryNameValueLabel"
    private Label countryNameValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameComboBox"
    private ComboBox<CountryModel> countryNameComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="nameValidationLabel"
    private Label nameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="languageLabel"
    private Label languageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="defaultTimeZoneLabel"
    private Label defaultTimeZoneLabel; // Value injected by FXMLLoader

    @FXML // fx:id="citiesLabel"
    private Label citiesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="citiesTableView"
    private TableView<CityModel> citiesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="newButtonBar"
    private ButtonBar newButtonBar; // Value injected by FXMLLoader
    private ObservableObjectDerivitive<CountryModel> selectedCountryName;

    public EditCountry() {
        windowTitle = new ReadOnlyStringWrapper();
        valid = new ReadOnlyBooleanWrapper();
        itemList = FXCollections.observableArrayList();
        countryList = FXCollections.observableArrayList();
        addEventHandler(CityDaoEvent.CITY_DAO_INSERT, (CityDaoEvent event) -> {
            LOG.info("Caught event!");
        });
    }

    private void deleteCity(CityModel item) {
        if (null != item) {
            Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            if (response.isPresent() && response.get() == ButtonType.YES) {
                waitBorderPane.startNow(new DeleteTask(item, getScene().getWindow()));
            }
        }
    }

    private void openCity(CityModel item) {
        if (null != item) {
            try {
                EditCity.edit(item, getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        }
    }

    @FXML
    void onCityDeleteMenuItemAction(ActionEvent event) {
        deleteCity(citiesTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onCityEditMenuItemAction(ActionEvent event) {
        openCity(citiesTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onItemActionRequest(ItemActionRequestEvent<CityModel> event) {
        if (event.isDelete()) {
            deleteCity(event.getItem());
        } else {
            openCity(event.getItem());
        }
    }

    @FXML
    void onNewButtonAction(ActionEvent event) {
        try {
            EditCity.editNew(model, getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @SuppressWarnings("unchecked")
    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert countryNameValueLabel != null : "fx:id=\"countryNameValueLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert countryNameComboBox != null : "fx:id=\"countryNameComboBox\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert languageLabel != null : "fx:id=\"languageLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert defaultTimeZoneLabel != null : "fx:id=\"defaultTimeZoneLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesLabel != null : "fx:id=\"citiesLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesTableView != null : "fx:id=\"citiesTableView\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert newButtonBar != null : "fx:id=\"newButtonBar\" was not injected: check your FXML file 'EditCountry.fxml'.";
    }

    @Override
    public void onEditNew() {
        collapseNode(countryNameValueLabel);
        collapseNode(citiesLabel);
        collapseNode(citiesTableView);
        restoreNode(countryNameComboBox);
        collapseNode(newButtonBar);
        restoreNode(nameValidationLabel);
        windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWCOUNTRY));
        // TODO: Filter out existing items (since we won't be able to add duplicate-named countries, anyway)
        PredefinedData.getCountryOptions(null).sorted(Country::compare).forEach((c) -> countryList.add(new CountryModel(c)));
        countryNameComboBox.setItems(countryList);
        selectedCountryName = ObservableObjectDerivitive.ofSelection(countryNameComboBox);
        valid.bind(selectedCountryName.isNotNull());
        languageLabel.textProperty().bind(model.languageProperty());
        defaultTimeZoneLabel.textProperty().bind(model.defaultTimeZoneDisplayProperty());
        nameValidationLabel.visibleProperty().bind(selectedCountryName.isNull());
        CountryDAO.PredefinedCountryElement pdc = model.getPredefinedElement();
        if (null != pdc) {
            String rc = pdc.getLocale().getCountry();
            countryList.stream().filter((t) -> t.getPredefinedElement().getLocale().getCountry().equals(rc)).findFirst().ifPresent((t)
                    -> countryNameComboBox.getSelectionModel().select(t));
        }
    }

    @Override
    public void onEditExisting(boolean isInitialize) {
        if (!isInitialize) {
            valid.unbind();
            languageLabel.textProperty().unbind();
            defaultTimeZoneLabel.textProperty().unbind();
            nameValidationLabel.visibleProperty().unbind();
        }
        valid.set(true);
        nameValidationLabel.setVisible(false);
        restoreLabeled(countryNameValueLabel, model.getName());
        restoreNode(citiesLabel);
        restoreNode(citiesTableView);
        collapseNode(countryNameComboBox);
        restoreNode(newButtonBar);
        languageLabel.setText(model.getLanguage());
        defaultTimeZoneLabel.setText(model.getDefaultTimeZoneDisplay());
        citiesTableView.setItems(itemList);
        windowTitle.set(String.format(resources.getString(RESOURCEKEY_EDITCOUNTRY), model.getName()));
        sceneProperty().addListener(new InvalidationListener() {
            private boolean isListening = false;

            {
                onChange(null != getScene());
            }

            private void onChange(boolean hasParent) {
                if (hasParent) {
                    LOG.info("Scene is not null");
                    if (!isListening) {
                        getMainController().addDaoEventHandler(CityDaoEvent.CITY_DAO_INSERT, EditCountry.this::onCityAdded);
                        getMainController().addDaoEventHandler(CityDaoEvent.CITY_DAO_UPDATE, EditCountry.this::onCityUpdated);
                        getMainController().addDaoEventHandler(CityDaoEvent.CITY_DAO_DELETE, EditCountry.this::onCityDeleted);
                        isListening = true;
                    }
                } else {
                    LOG.info("Scene is null");
                    if (isListening) {
                        getMainController().removeDaoEventHandler(CityDaoEvent.CITY_DAO_INSERT, EditCountry.this::onCityAdded);
                        getMainController().removeDaoEventHandler(CityDaoEvent.CITY_DAO_UPDATE, EditCountry.this::onCityUpdated);
                        getMainController().removeDaoEventHandler(CityDaoEvent.CITY_DAO_DELETE, EditCountry.this::onCityDeleted);
                        isListening = false;
                    }
                }
            }

            @Override
            public void invalidated(Observable observable) {
                onChange(null != ((ObservableObjectValue<?>) observable).get());
            }
        });
        waitBorderPane.startNow(new ItemsLoadTask());
    }

    private void onCityAdded(CityDaoEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        CityDAO dao = event.getTarget();
        if (dao.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
            itemList.add(new CityModel(dao));
        }
    }

    private void onCityUpdated(CityDaoEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        CityDAO dao = event.getTarget();
        int pk = dao.getPrimaryKey();
        Optional<CityModel> match = itemList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny();
        if (match.isPresent()) {
            CityModel item = match.get();
            if (dao.getCountry().getPrimaryKey() != model.getPrimaryKey()) {
                itemList.remove(item);
            } else {
                CityModel.getFactory().updateItem(item, dao);
            }
        } else if (dao.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
            itemList.add(new CityModel(dao));
        }
    }

    private void onCityDeleted(CityDaoEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        int pk = event.getTarget().getPrimaryKey();
        itemList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().ifPresent((t) -> itemList.remove(t));
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
    public FxRecordModel.ModelFactory<CountryDAO, CountryModel> modelFactory() {
        return CountryModel.getFactory();
    }

    @Override
    public void updateModel() {
        model.setPredefinedElement(countryNameComboBox.getSelectionModel().getSelectedItem().getPredefinedElement());
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
                CityModel.Factory factory = CityModel.getFactory();
                result.stream().sorted(City::compare).forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
        }

        @Override
        protected List<CityDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                CityDAO.FactoryImpl cf = CityDAO.getFactory();
                return cf.load(dbConnector.getConnection(), cf.getByCountryFilter(pk));
            }
        }

    }

    private class DeleteTask extends Task<String> {

        private final CityModel model;
        private final Window parentWindow;
        private final CityDAO dao;

        DeleteTask(CityModel model, Window parentWindow) {
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
                String message = CityDAO.getFactory().getDeleteDependencyMessage(model.getDataObject(), connector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                CityDAO.getFactory().delete(dao, connector.getConnection());
                if (dao.getRowState() == DataRowState.DELETED) {
                    CityModel.getFactory().updateItem(model, dao);
                }
            }
            return null;
        }
    }

}
