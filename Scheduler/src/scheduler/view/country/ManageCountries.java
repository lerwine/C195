package scheduler.view.country;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.stage.Stage;
import scheduler.dao.CountryImpl;
import scheduler.view.EditItem;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.dao.ModelFilter;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import static scheduler.view.ListingController.setContent;
import scheduler.view.MainController;

/**
 * FXML Controller class for viewing a list of {@link CountryModel} items. This is loaded as content of {@link MainController} using
 * {@link #setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.ModelFilter)}.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends ListingController<CountryImpl, CountryModel> implements MangageCountriesConstants {

    private static final Logger LOG = Logger.getLogger(ManageCountries.class.getName());

    /**
     * Loads {@link CountryModel} listing view and controller into the {@link MainController}.
     *
     * @param mainController The {@link MainController} to contain the {@link CountryModel} listing.
     * @param stage The {@link Stage} for the view associated with the current main controller.
     * @param filter The {@link ModelFilter} to use for loading and filtering {@link CountryModel} items.
     * @throws IOException if unable to load the view.
     */
    public static void setContent(MainController mainController, Stage stage, ModelFilter<CountryImpl, CountryModel> filter) throws IOException {
        setContent(mainController, ManageCountries.class, stage, filter);
    }

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewCountry(event);
    }

    @Override
    protected EditItem.ShowAndWaitResult<CountryModel> onEditItem(Event event, CountryModel item) {
        return getMainController().editCountry(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, CountryModel item) {
        getMainController().deleteCountry(event, item);
    }

    @Override
    protected CountryModel toModel(CountryImpl dao) {
        return new CountryModel(dao);
    }

    @Override
    protected CountryImpl.FactoryImpl getDaoFactory() {
        return CountryImpl.getFactory();
    }

    @Override
    protected ItemEventManager<ItemEvent<CountryModel>> getItemAddManager() {
        return getMainController().getCountryAddManager();
    }

    @Override
    protected ItemEventManager<ItemEvent<CountryModel>> getItemRemoveManager() {
        return getMainController().getCountryRemoveManager();
    }

}
