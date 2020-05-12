package scheduler.view.country;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.predefined.PredefinedData;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.EditItem;
import scheduler.view.ErrorDetailControl;
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

    public static CountryModel edit(CountryModel model) throws IOException {
        return EditItem.showAndWait(EditCountry.class, model);
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

    @FXML // fx:id="citiesTableView"
    private TableView<CityModel> citiesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="saveButton"
    private Button saveButton; // Value injected by FXMLLoader

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader***
    private ObservableList<CityModel> itemList;
    private ObservableList<PredefinedCountry> countryList;

    public EditCountry() {
        this.valid = new ReadOnlyBooleanWrapper(false);
        this.windowTitle = new ReadOnlyStringWrapper();
    }

    @FXML
    void onCancelButtonAction(ActionEvent event) {

    }

    @FXML
    void onCityDeleteMenuItemAction(ActionEvent event) {

    }

    @FXML
    void onCityEditMenuItemAction(ActionEvent event) {

    }

    @FXML
    void onSaveButtonAction(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert countryNameValueLabel != null : "fx:id=\"countryNameValueLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert countryNameComboBox != null : "fx:id=\"countryNameComboBox\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesTableView != null : "fx:id=\"citiesTableView\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert saveButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'EditCountry.fxml'.";

        itemList = FXCollections.observableArrayList();
        countryList = FXCollections.observableArrayList();
        citiesTableView.setItems(itemList);

        if (model.isNewItem()) {
            collapseNode(countryNameValueLabel);
            collapseNode(citiesTableView);
            restoreNode(countryNameComboBox);
            restoreNode(saveButton);
            cancelButton.setText("Cancel");
            countryList.addAll(PredefinedData.getCountryMap().values());
            restoreNode(nameValidationLabel);
            countryNameComboBox.setItems(countryList);
        } else {
            restoreLabeled(countryNameValueLabel, model.getName());
            restoreNode(citiesTableView);
            collapseNode(countryNameComboBox);
            collapseNode(saveButton);
            collapseNode(nameValidationLabel);
            cancelButton.setText("Close");
            waitBorderPane.startNow(new ItemsLoadTask());
            windowTitle.set(String.format(resources.getString(RESOURCEKEY_EDITCOUNTRY), model.getName()));
        }
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
    public boolean applyChangesToModel() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.country.EditCountry#applyChangesToModel
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
