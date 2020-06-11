package scheduler.view.country;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataRowState;
import scheduler.model.CityProperties;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ParentWindowChangeListener;
import scheduler.util.Values;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.city.EditCity;
import static scheduler.view.country.EditCountryResourceKeys.*;
import scheduler.view.event.CityEvent;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

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
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CityModel> itemList;
    private final ObservableList<Locale> localeList;
    private ObjectBinding<Locale> selectedLocale;

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

    public EditCountry() {
        windowTitle = new ReadOnlyStringWrapper(this, "", "");
        valid = new ReadOnlyBooleanWrapper(this, "", false);
        modified = new ReadOnlyBooleanWrapper(this, "", true);
        itemList = FXCollections.observableArrayList();
        localeList = FXCollections.observableArrayList();
        Arrays.stream(Locale.getAvailableLocales()).filter((t)
                -> Values.isNotNullWhiteSpaceOrEmpty(t.getLanguage()) && Values.isNotNullWhiteSpaceOrEmpty(t.getCountry()))
                .sorted(Values::compareLocaleCountryFirst).forEach((t) -> localeList.add(t));
    }

    @SuppressWarnings("incomplete-switch")
    @FXML
    private void onCitiesTableViewKeyReleased(KeyEvent event) {
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            CityModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = citiesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteCity(item);
                    }
                    break;
                case ENTER:
                    item = citiesTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        openCity(item);
                    }
                    break;
            }
        }
    }

    @FXML
    private void onCityDeleteMenuItemAction(ActionEvent event) {
        deleteCity(citiesTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onCityEditMenuItemAction(ActionEvent event) {
        openCity(citiesTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onItemActionRequest(CityEvent event) {
        if (event.isDeleteRequest()) {
            deleteCity(event.getModel());
        } else {
            openCity(event.getModel());
        }
    }

    @FXML
    private void onLocaleComboBoxAction(ActionEvent event) {
        valid.set(null != selectedLocale.get());
        modified.set(!Objects.equals(selectedLocale.get(), model.getLocale()));
    }

    @FXML
    private void onNewButtonAction(ActionEvent event) {
        try {
            EditCity.editNew(model, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
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

        BooleanBinding modificationBinding = model.rowStateProperty().isEqualTo(DataRowState.NEW)
                .or(selectedLocale.isNotEqualTo(model.localeProperty()));
        modificationBinding.addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> String.format("modificationBinding changed from %s to %s ", oldValue, newValue));
            modified.set(newValue);
        });
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
        } else {
            initializeEditMode();
            WaitTitledPane pane = new WaitTitledPane();
            pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                    .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
            waitBorderPane.startNow(pane, new ItemsLoadTask());
        }
    }

    private void initializeEditMode() {
        citiesTableView.setItems(itemList);
        windowTitle.set(String.format(resources.getString(RESOURCEKEY_EDITCOUNTRY), model.getName()));
        ParentWindowChangeListener.setWindowChangeListener(this, new ChangeListener<Window>() {
            private boolean isListening = false;

            @Override
            public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                if (null != oldValue) {
                    oldValue.removeEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowHidden);
                }
                if (null != newValue) {
                    newValue.addEventHandler(WindowEvent.WINDOW_HIDDEN, this::onWindowHidden);
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
                        getMainController().addModelEventHandler(CityEvent.CITY_INSERTED_EVENT, EditCountry.this::onCityAdded);
                        getMainController().addModelEventHandler(CityEvent.CITY_UPDATED_EVENT, EditCountry.this::onCityUpdated);
                        getMainController().addModelEventHandler(CityEvent.CITY_DELETED_EVENT, EditCountry.this::onCityDeleted);
                        isListening = true;
                    }
                } else if (isListening) {
                    getMainController().removeModelEventHandler(CityEvent.CITY_INSERTED_EVENT, EditCountry.this::onCityAdded);
                    getMainController().removeModelEventHandler(CityEvent.CITY_UPDATED_EVENT, EditCountry.this::onCityUpdated);
                    getMainController().removeModelEventHandler(CityEvent.CITY_DELETED_EVENT, EditCountry.this::onCityDeleted);
                    isListening = false;
                }
            }

        });
    }

    @Override
    public void onNewModelSaved() {
        restoreNode(citiesLabel);
        restoreNode(citiesTableView);
        restoreNode(newButtonBar);
        initializeEditMode();
    }

    private void onCityAdded(CityEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        // XXX: See if we need to get/set model.
        CityDAO dao = event.getDataAccessObject();
        if (dao.getCountry().getPrimaryKey() == model.getPrimaryKey()) {
            itemList.add(new CityModel(dao));
        }
    }

    private void onCityUpdated(CityEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        // XXX: See if we need to get/set model.
        CityDAO dao = event.getDataAccessObject();
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

    private void onCityDeleted(CityEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        // XXX: See if we need to get/set model.
        int pk = event.getDataAccessObject().getPrimaryKey();
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
    public FxRecordModel.ModelFactory<CountryDAO, CountryModel> modelFactory() {
        return CountryModel.getFactory();
    }

    @Override
    public void updateModel() {
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
                CityModel.Factory factory = CityModel.getFactory();
                result.stream().sorted(CityProperties::compare).forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
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

    private class DeleteTask extends Task<String> {

        private final CityModel cityModel;
        private final Window parentWindow;
        private final CityDAO dao;

        DeleteTask(CityModel model, Window parentWindow) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            dao = model.dataObject();
            cityModel = model;
            this.parentWindow = parentWindow;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            String message = getValue();
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(parentWindow, LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETEFAILURE), message);
            } else if (dao.getRowState() == DataRowState.DELETED) {
                CityModel.getFactory().updateItem(cityModel, dao);
            }
        }

        @Override
        protected String call() throws Exception {
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES));
                String message = CityDAO.FACTORY.getDeleteDependencyMessage(dao, connector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                CityDAO.FACTORY.delete(dao, connector.getConnection());
            }
            return null;
        }
    }

}
