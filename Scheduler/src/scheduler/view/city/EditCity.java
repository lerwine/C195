package scheduler.view.city;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.fx.ErrorDetailControl;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.ObservableObjectDerivitive;
import scheduler.util.DbConnector;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.task.WaitBorderPane;

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

    private static final Logger LOG = Logger.getLogger(EditCity.class.getName());

    public static CityModel edit(CityModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCity.class, model, false);
    }

    public static CityModel editNew(CountryItem<? extends ICountryDAO> country, Window parentWindow, boolean keepOpen) throws IOException {
        CityModel.Factory factory = CityModel.getFactory();
        CityModel model = factory.createNew(factory.getDaoFactory().createNew());
        // TODO: Select country in view
        return EditItem.showAndWait(parentWindow, EditCity.class, model, keepOpen);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyStringWrapper windowTitle;
    private ObservableList<AddressModel> itemList;

    @ModelEditor
    private CityModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="nameValueLabel"
    private Label nameValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="nameValueComboBox"
    private ComboBox<PredefinedCity> nameValueComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="nameValidationLabel"
    private Label nameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameValueLabel"
    private Label countryNameValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameValueComboBox"
    private ComboBox<PredefinedCountry> countryNameValueComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameValidationLabel"
    private Label countryNameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="languageLabel"
    private Label languageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneLabel"
    private Label timeZoneLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    public EditCity() {
        windowTitle = new ReadOnlyStringWrapper();
        valid = new ReadOnlyBooleanWrapper();
    }

    @FXML
    void onAddAddressButtonAction(ActionEvent event) {

    }

    @FXML
    void onAddressDeleteMenuItemAction(ActionEvent event) {

    }

    @FXML
    void onAddressEditMenuItemAction(ActionEvent event) {

    }

    @FXML
    void onItemActionRequest(ActionEvent event) {

    }

    @FXML
    void onOpenCountryButtonAction(ActionEvent event) {

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
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'EditCity.fxml'.";

        itemList = FXCollections.observableArrayList();
        addressesTableView.setItems(itemList);
        ObservableObjectDerivitive.ofSelection(nameValueComboBox).addListener((observable, oldValue, newValue) -> {
            valid.set(null != newValue);
        });

        waitBorderPane.startNow(new ItemsLoadTask());
        windowTitle.set(String.format(resources.getString(RESOURCEKEY_EDITCITY), model.getName()));
    }

    @Override
    public void onEditNew() {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.city.EditCity#onEditNew
    }

    @Override
    public void onEditExisting(boolean isInitialize) {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.city.EditCity#onEditExisting
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
                    itemList.add(factory.createNew(t));
                });
            }
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), getException());
            super.failed();
        }

        @Override
        protected List<AddressDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                AddressDAO.FactoryImpl cf = AddressDAO.getFactory();
                return cf.load(dbConnector.getConnection(), cf.getByCityFilter(pk));
            }
        }

    }

}
