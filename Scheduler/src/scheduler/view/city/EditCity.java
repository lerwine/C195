package scheduler.view.city;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.EditItem;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.address.AddressModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;
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

    public static CityModel edit(CityModel model) throws IOException {
        return EditItem.showAndWait(EditCity.class, model);
    }

    private final ReadOnlyBooleanWrapper valid;

    private final ReadOnlyStringWrapper windowTitle;

    @ModelEditor
    private CityModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="languageTextField"
    private TextField languageTextField; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneTextField"
    private TextField timeZoneTextField; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameLabel"
    private Label countryNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    private ObservableList<AddressModel> itemList;

    public EditCity() {
        this.valid = new ReadOnlyBooleanWrapper(false);
        this.windowTitle = new ReadOnlyStringWrapper();
    }

    @FXML
    void onAddCityButtonAction(ActionEvent event) {

    }

    @FXML
    void onCityDeleteMenuItemAction(ActionEvent event) {

    }

    @FXML
    void onCityEditMenuItemAction(ActionEvent event) {

    }

    @FXML
    void onOpenCountryButtonAction(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert languageTextField != null : "fx:id=\"languageTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert timeZoneTextField != null : "fx:id=\"timeZoneTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryNameLabel != null : "fx:id=\"countryNameLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'EditCity.fxml'.";

        itemList = FXCollections.observableArrayList();
        addressesTableView.setItems(itemList);
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        event.getStage().setTitle(String.format(resources.getString(RESOURCEKEY_EDITCITY), model.getName()));
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
    public boolean applyChangesToModel() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.city.EditCity#applyChangesToModel
    }

    private class ItemsLoadTask extends TaskWaiter<List<AddressDAO>> {

        private final int pk;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES));
            pk = model.getPrimaryKey();
        }

        @Override
        protected void processResult(List<AddressDAO> result, Stage owner) {
            if (null != result && !result.isEmpty()) {
                AddressModel.Factory factory = AddressModel.getFactory();
                result.forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<AddressDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            AddressDAO.FactoryImpl cf = AddressDAO.getFactory();
            return cf.load(connection, cf.getByCityFilter(pk));
        }

    }

}
