package scheduler.view.country;

import java.io.IOException;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import scheduler.dao.CountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.ModelFilter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.model.ItemModel;

/**
 * FXML Controller class for viewing a list of {@link CountryModel} items.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends ListingController<CountryDAO, CountryModel> implements MangageCountriesConstants {

    public static ManageCountries loadInto(MainController mainController, Stage stage, ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> filter,
            Object loadEventListener) throws IOException {
        return loadInto(ManageCountries.class, mainController, stage, filter, loadEventListener);
    }

    public static ManageCountries loadInto(MainController mainController, Stage stage,
            ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> filter) throws IOException {
        return loadInto(mainController, stage, filter, null);
    }

    @Override
    protected void onAddNewItem(Event event) throws IOException {
        getMainController().addNewCountry((Stage)((Button)event.getSource()).getScene().getWindow());
    }

    @Override
    protected void onEditItem(Event event, CountryModel item) throws IOException {
        getMainController().editCountry((Stage)((Button)event.getSource()).getScene().getWindow(), item);
    }

    @Override
    protected void onDeleteItem(Event event, CountryModel item) {
        getMainController().deleteCountry((Stage)((Button)event.getSource()).getScene().getWindow(), item);
    }

    @Override
    protected CountryModel toModel(CountryDAO dao) {
        return new CountryModel(dao);
    }

    @Override
    protected ItemModel.ModelFactory<CountryDAO, CountryModel> getModelFactory() {
        return CountryModel.getFactory();
    }

}
