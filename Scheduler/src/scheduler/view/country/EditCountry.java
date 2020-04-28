package scheduler.view.country;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.view.EditItem;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.city.CityModel;
import static scheduler.view.country.EditCountryResourceKeys.*;
import scheduler.view.event.FxmlViewEvent;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.task.TaskWaiter;

/**
 * FXML Controller class for editing a {@link CountryModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/country/EditCountry.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/EditCountry")
@FXMLResource("/scheduler/view/country/EditCountry.fxml")
public final class EditCountry extends EditItem.EditController<CountryDAO, CountryModel> {

    private static final Logger LOG = Logger.getLogger(EditCountry.class.getName());

    public static CountryModel edit(CountryModel model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditCountry.class, mainController, stage);
    }

    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="citiesTableView"
    private TableView<CityModel> citiesTableView; // Value injected by FXMLLoader

    @FXML
    void onCityDeleteMenuItemAction(ActionEvent event) {

    }

    @FXML
    void onCityEditMenuItemAction(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCountry.fxml'.";
        assert citiesTableView != null : "fx:id=\"citiesTableView\" was not injected: check your FXML file 'EditCountry.fxml'.";

        itemList = FXCollections.observableArrayList();
        citiesTableView.setItems(itemList);
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        event.getStage().setTitle(String.format(getResourceString(RESOURCEKEY_EDITCOUNTRY), getModel().getName()));
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return Bindings.createBooleanBinding(() -> true);
    }

    @Override
    protected FxRecordModel.ModelFactory<CountryDAO, CountryModel> getFactory() {
        return CountryModel.getFactory();
    }

    @Override
    protected void updateModel(CountryModel model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.country.EditCountry#updateModel
    }

    private ObservableList<CityModel> itemList;

    private class ItemsLoadTask extends TaskWaiter<List<CityDAO>> {

        private final int pk;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCITIES));
            pk = getModel().getPrimaryKey();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void processResult(List<CityDAO> result, Stage owner) {
            if (null != result && !result.isEmpty()) {
                CityModel.Factory factory = CityModel.getFactory();
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
        protected List<CityDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            CityDAO.FactoryImpl cf = CityDAO.getFactory();
            return cf.load(connection, cf.getByCountryFilter(pk));
        }

    }

}
