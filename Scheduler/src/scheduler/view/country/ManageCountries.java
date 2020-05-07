package scheduler.view.country;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.Scheduler;
import static scheduler.Scheduler.getMainController;
import scheduler.controls.MainListingControl;
import scheduler.dao.CountryDAO;
import scheduler.dao.event.CountryDaoEvent;
import scheduler.dao.filter.DaoFilter;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.ModelFilter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.model.ui.FxRecordModel;
import static scheduler.view.country.MangageCountriesResourceKeys.*;

// CURRENT: No values for Name, Updated On or Updated By are being displayed in listing.
// TODO: Need to add headings for main content screens and leave window title alone for non-popups.
// Nothing happens when hitting enter

/**
 * FXML Controller class for viewing a list of {@link CountryModel} items.
 * <p>
 * The associated view is {@code /resources/scheduler/view/country/ManageCountries.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends MainListingControl<CountryDAO, CountryModel, CountryDaoEvent> {

    public static ManageCountries loadIntoMainContent(ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> filter) {
        ManageCountries newContent = new ManageCountries();
        Scheduler.getMainController().replaceContent(newContent);
        newContent.setFilter(filter);
        return newContent;
    }
    public static ManageCountries loadIntoMainContent() {
        return loadIntoMainContent(CountryModel.getFactory().getAllItemsFilter());
    }

    @FXML // fx:id="helpBorderPane"
    private BorderPane helpBorderPane; // Value injected by FXMLLoader

    @FXML
    void onHelpButtonAction(ActionEvent event) {
        helpBorderPane.setVisible(true);
    }

    @FXML
    void onHelpOKButtonAction(ActionEvent event) {
        helpBorderPane.setVisible(false);
    }

    @Override
    protected void initialize() {
        super.initialize();
        assert helpBorderPane != null : "fx:id=\"helpBorderPane\" was not injected: check your FXML file 'ManageCountries.fxml'.";

    }

    @Override
    protected CountryModel.Factory getModelFactory() {
        return CountryModel.getFactory();
    }

    @Override
    protected String getLoadingTitle() {
        return getResources().getString(RESOURCEKEY_LOADINGCOUNTRIES);
    }

    @Override
    protected String getFailMessage() {
        return getResources().getString(RESOURCEKEY_ERRORLOADINGCOUNTRIES);
    }

    @Override
    protected void onNewItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onEditItem(CountryModel item) {
        getMainController().openCountry((Stage) getScene().getWindow(), item);
    }

    @Override
    protected void onDeleteItem(CountryModel item) {
        getMainController().deleteCountry((Stage) getScene().getWindow(), item);
    }

    @Override
    protected EventType<CountryDaoEvent> getInsertedEventType() {
        return CountryDaoEvent.COUNTRY_DAO_INSERT;
    }

    @Override
    protected EventType<CountryDaoEvent> getUpdatedEventType() {
        return CountryDaoEvent.COUNTRY_DAO_UPDATE;
    }

    @Override
    protected EventType<CountryDaoEvent> getDeletedEventType() {
        return CountryDaoEvent.COUNTRY_DAO_DELETE;
    }

}
