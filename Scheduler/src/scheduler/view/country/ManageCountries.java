package scheduler.view.country;

import java.util.Comparator;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import scheduler.Scheduler;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.CountryDAO;
import scheduler.dao.event.CountryDaoEvent;
import scheduler.dao.filter.DaoFilter;
import scheduler.fx.MainListingControl;
import scheduler.model.Country;
import scheduler.model.ui.CountryModel;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.ModelFilter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.country.MangageCountriesResourceKeys.*;

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
        restoreNode(helpBorderPane);
    }

    @FXML
    void onHelpOKButtonAction(ActionEvent event) {
        collapseNode(helpBorderPane);
    }

    @Override
    protected void initialize() {
        super.initialize();
        assert helpBorderPane != null : "fx:id=\"helpBorderPane\" was not injected: check your FXML file 'ManageCountries.fxml'.";

    }

    @Override
    protected Comparator<? super CountryDAO> getComparator() {
        return Country::compare;
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
        getMainController().addNewCountry(getScene().getWindow(), true);
    }

    @Override
    protected void onEditItem(CountryModel item) {
        getMainController().openCountry(item, getScene().getWindow());
    }

    @Override
    protected void onDeleteItem(CountryModel item) {
        getMainController().deleteCountry(item, null);
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
