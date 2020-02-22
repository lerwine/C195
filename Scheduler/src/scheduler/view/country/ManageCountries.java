package scheduler.view.country;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.stage.Stage;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.view.EditItem;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.dao.ModelFilter;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.MainController;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends ListingController<CountryImpl, CountryModel> {

    private static final Logger LOG = Logger.getLogger(ManageCountries.class.getName());

    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit"}.
     */
    public static final String RESOURCEKEY_EDIT = "edit";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Database Access Error"}.
     */
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete"}.
     */
    public static final String RESOURCEKEY_DELETE = "delete";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created By"}.
     */
    public static final String RESOURCEKEY_CREATEDBY = "createdBy";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created On"}.
     */
    public static final String RESOURCEKEY_CREATEDON = "createdOn";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Countries"}.
     */
    public static final String RESOURCEKEY_MANAGECOUNTRIES = "manageCountries";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name"}.
     */
    public static final String RESOURCEKEY_NAME = "name";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated By"}.
     */
    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated On"}.
     */
    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "New"}.
     */
    public static final String RESOURCEKEY_NEW = "new";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading countries..."}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGCOUNTRIES = "errorLoadingCountries";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Loading Countries"}.
     */
    public static final String RESOURCEKEY_LOADINGCOUNTRIES = "loadingCountries";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That country is referenced by one or more cities..."}.
     */
    public static final String RESOURCEKEY_COUNTRYHASCITIES = "countryHasCities";

    //</editor-fold>
    public static void setContent(MainController mainController, Stage stage, ModelFilter<CountryImpl, CountryModel> filter) throws IOException {
        setContent(mainController, ManageCountries.class, stage).changeFilter(filter, stage);
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
    protected CountryModel toModel(CountryImpl result) {
        return new CountryModel(result);
    }

    @Override
    protected DataObjectImpl.Factory<CountryImpl, CountryModel> getDaoFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ItemEventManager<ItemEvent<CountryModel>> getItemAddManager(MainController mainController) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ItemEventManager<ItemEvent<CountryModel>> getItemRemoveManager(MainController mainController) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
