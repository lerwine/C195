package scheduler.view.country;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.CountryFactory;
import scheduler.dao.CountryImpl;
import scheduler.filter.ModelFilter;
import scheduler.util.Alerts;
import scheduler.view.CrudAction;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public class ManageCountries extends ListingController<CountryModel> {
    
    private static final Logger LOG = Logger.getLogger(ManageCountries.class.getName());
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Countries"}.
     */
    public static final String RESOURCEKEY_MANAGECOUNTRIES = "manageCountries";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Loading Countries"}.
     */
    public static final String RESOURCEKEY_LOADINGCOUNTRIES = "loadingCountries";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Database Access Error"}.
     */
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading countries..."}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGCOUNTRIES = "errorLoadingCountries";

    public static void loadInto(MainController mc, Stage stage, ModelFilter<CountryModel> filter) throws IOException {
        loadInto(ManageCountries.class, mc, stage, filter);
    }
    
    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewCountry(event);
    }

    @Override
    protected CrudAction<CountryModel> onEditItem(Event event, CountryModel item) {
        return getMainController().editCountry(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, CountryModel item) {
        getMainController().deleteCountry(event, item);
    }

    @Override
    protected void onFilterChanged(Stage owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private class CountriesLoadTask extends ItemsLoadTask<CountryImpl> {
        CountriesLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGCOUNTRIES));
        }

        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCOUNTRIES));
        }

        @Override
        protected CountryModel toModel(CountryImpl result) { return new CountryModel(result); }

        @Override
        protected Iterable<CountryImpl> getResult(Connection connection, ModelFilter<CountryModel> filter) throws Exception {
            return (new CountryFactory()).load(connection, filter);
        }
        
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCOUNTRIES));
        }
        
    }

}
