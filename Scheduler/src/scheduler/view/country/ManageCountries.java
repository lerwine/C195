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
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.ItemModel;
import scheduler.view.MainController;
import scheduler.view.ModelFilter;

/**
 * FXML Controller class for viewing a list of {@link CountryModel} items. This is loaded as content of {@link MainController} using
 * {@link #setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.ModelListingFilter)}.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends ListingController<CountryImpl, CountryModel> implements MangageCountriesConstants {

    private static final Logger LOG = Logger.getLogger(ManageCountries.class.getName());

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewCountry(event);
    }

    @Override
    protected void onEditItem(Event event, CountryModel item) {
        getMainController().editCountry(event, item);
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
    protected ItemModel.ModelFactory<CountryImpl, CountryModel> getModelFactory() {
        return CountryModel.getFactory();
    }

}
