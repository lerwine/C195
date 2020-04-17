package scheduler.view.country;

import java.io.IOException;
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
 * <p>
 * The associated view is {@code /resources/scheduler/view/country/ManageCountries.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends ListingController<CountryDAO, CountryModel> {

    public static ManageCountries loadInto(MainController mainController, Stage stage, ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> filter,
            Object loadEventListener) throws IOException {
        return loadInto(ManageCountries.class, mainController, stage, filter, loadEventListener);
    }

    public static ManageCountries loadInto(MainController mainController, Stage stage,
            ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> filter) throws IOException {
        return loadInto(mainController, stage, filter, null);
    }

    @Override
    protected boolean cannotAddNew() {
        return true;
    }

    @Override
    protected void onAddNewItem(Stage stage) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onEditItem(Stage stage, CountryModel item) throws IOException {
        getMainController().openCountry(stage, item);
    }

    @Override
    protected void onDeleteItem(Stage stage, CountryModel item) {
        getMainController().deleteCountry(stage, item);
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
