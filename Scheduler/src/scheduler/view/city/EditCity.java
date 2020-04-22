package scheduler.view.city;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.util.AlertHelper;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.address.AddressModelImpl;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import static scheduler.view.city.EditCityResourceKeys.*;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;

/**
 * FXML Controller class for editing a {@link CityModelImpl}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/city/EditCity.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/city/EditCity")
@FXMLResource("/scheduler/view/city/EditCity.fxml")
public final class EditCity extends EditItem.EditController<CityDAO, CityModelImpl> {

    private static final Logger LOG = Logger.getLogger(EditCity.class.getName());

    public static CityModelImpl edit(CityModelImpl model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditCity.class, mainController, stage);
    }

    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="languageTextField"
    private TextField languageTextField; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneTextField"
    private TextField timeZoneTextField; // Value injected by FXMLLoader

    @FXML // fx:id="countryNameLabel"
    private Label countryNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModelImpl> addressesTableView; // Value injected by FXMLLoader

    private ObservableList<AddressModelImpl> itemList;

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
        event.getStage().setTitle(String.format(getResourceString(RESOURCEKEY_EDITCITY), getModel().getName()));
    }

    @Override
    protected ItemModel.ModelFactory<CityDAO, CityModelImpl> getFactory() {
        return CityModelImpl.getFactory();
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.city.EditCity#getValidationExpression
    }

    @Override
    protected void updateModel(CityModelImpl model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.city.EditCity#updateModel
    }

    private class ItemsLoadTask extends TaskWaiter<List<AddressDAO>> {

        private final int pk;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGADDRESSES));
            pk = getModel().getPrimaryKey();
        }

        @Override
        protected void processResult(List<AddressDAO> result, Stage owner) {
            if (null != result && !result.isEmpty()) {
                AddressModelImpl.Factory factory = AddressModelImpl.getFactory();
                result.forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, ex);
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
