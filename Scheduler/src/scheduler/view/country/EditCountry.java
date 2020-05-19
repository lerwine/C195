package scheduler.view.country;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.event.CityDaoEvent;
import scheduler.fx.ErrorDetailControl;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.predefined.PredefinedData;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.city.CityModel;
import static scheduler.view.country.EditCountryResourceKeys.*;
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

    private static final Logger LOG = Logger.getLogger(EditCountry.class.getName());

    public static CountryModel editNew(Window parentWindow, boolean keepOpen) throws IOException {
        CountryModel.Factory factory = CountryModel.getFactory();
        return EditItem.showAndWait(parentWindow, EditCountry.class, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static CountryModel edit(CountryModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCountry.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;

    private final ReadOnlyStringWrapper windowTitle;

    @ModelEditor
    private CountryModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="countryNameValueLabel"
    private Label countryNameValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameComboBox"
    private ComboBox<PredefinedCountry> countryNameComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="nameValidationLabel"
    private Label nameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="citiesLabel"
    private Label citiesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="citiesTableView"
    private TableView<CityModel> citiesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="newButtonBar"
    private ButtonBar newButtonBar; // Value injected by FXMLLoader

    private ObservableList<CityModel> itemList;
    private ObservableList<PredefinedCountry> countryList;
    private final ReadOnlyBooleanWrapper changed;

    public EditCountry() {
        changed = new ReadOnlyBooleanWrapper(true);
        valid = new ReadOnlyBooleanWrapper(false);
        windowTitle = new ReadOnlyStringWrapper();
        addEventHandler(CityDaoEvent.CITY_DAO_INSERT, (CityDaoEvent event) -> {
            LOG.info("Caught event!");
        });
    }

    @FXML
    void onCityDeleteMenuItemAction(ActionEvent event) {
        CityModel item = citiesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            getMainController().deleteCity(item, waitBorderPane);
        }
    }

    @FXML
    void onCityEditMenuItemAction(ActionEvent event) {
        CityModel item = citiesTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            getMainController().openCity(item, getScene().getWindow());
        }
    }

    @FXML
    void onNewButtonAction(ActionEvent event) {
        getMainController().addNewCity(model, getScene().getWindow(), true);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert countryNameValueLabel != null : "fx:id=\"countryNameValueLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert countryNameComboBox != null : "fx:id=\"countryNameComboBox\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesLabel != null : "fx:id=\"citiesLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesTableView != null : "fx:id=\"citiesTableView\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert newButtonBar != null : "fx:id=\"newButtonBar\" was not injected: check your FXML file 'EditCountry.fxml'.";

        itemList = FXCollections.observableArrayList();
        countryList = FXCollections.observableArrayList();
        citiesTableView.setItems(itemList);

        if (model.isNewItem()) {
        } else {
        }
    }

    @Override
    public void onEditNew() {
        collapseNode(countryNameValueLabel);
        collapseNode(citiesLabel);
        collapseNode(citiesTableView);
        restoreNode(countryNameComboBox);
        collapseNode(newButtonBar);
        countryList.addAll(PredefinedData.getCountryMap().values());
        restoreNode(nameValidationLabel);
        windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWCOUNTRY));
        countryNameComboBox.setItems(countryList);
        countryNameComboBox.getSelectionModel().selectedItemProperty().addListener(this::onCountryNameChanged);
    }

    @Override
    public void onEditExisting(boolean isInitialize) {
        restoreLabeled(countryNameValueLabel, model.getName());
        restoreNode(citiesLabel);
        restoreNode(citiesTableView);
        collapseNode(countryNameComboBox);
        restoreNode(newButtonBar);
        if (!isInitialize) {
            countryNameComboBox.getSelectionModel().selectedItemProperty().removeListener(this::onCountryNameChanged);
        }
        collapseNode(nameValidationLabel);
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

    // CURRENT: Update model from listeners
    public boolean applyChangesToModel() {
        PredefinedCountry value = countryNameComboBox.getValue();
        if (null == value) {
            return false;
        }
        model.setPredefinedData(value);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void onCountryNameChanged(Observable observable) {
        PredefinedCountry c = ((ReadOnlyObjectProperty<PredefinedCountry>) observable).get();
        if (null == c) {
            changed.set(true);
            valid.set(false);
        } else {
            valid.set(true);
            if (!model.isNewItem()) {
                changed.set(!c.getRegionCode().equals(model.getPredefinedData().getRegionCode()));
            }
        }
    }

    private void onCityAdded(CityDaoEvent event) {
        LOG.info(String.format("%s event handled", event.getEventType().getName()));
        CityDAO dao = event.getTarget();
        if (dao.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
            itemList.add(new CityModel(dao));
        }
    }

    private void onCityUpdated(CityDaoEvent event) {
        LOG.info(String.format("%s event handled", event.getEventType().getName()));
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
        LOG.info(String.format("%s event handled", event.getEventType().getName()));
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
    public boolean isChanged() {
        return changed.get();
    }

    @Override
    public ReadOnlyBooleanProperty changedProperty() {
        return changed.getReadOnlyProperty();
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
                result.forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
                changed.set(false);
            }
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), getException());
            super.failed();
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

}
