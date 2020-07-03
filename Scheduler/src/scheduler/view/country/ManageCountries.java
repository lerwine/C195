package scheduler.view.country;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.CountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.events.CountryEvent;
import scheduler.events.CountryFailedEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.fx.MainListingControl;
import scheduler.model.CountryProperties;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.CountryModel;
import scheduler.util.AlertHelper;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainController;
import scheduler.view.ModelFilter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.country.ManageCountriesResourceKeys.*;

/**
 * FXML Controller class for viewing a list of {@link CountryModel} items.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends MainListingControl<CountryDAO, CountryModel, CountryEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ManageCountries.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ManageCountries.class.getName());

    public static ManageCountries loadIntoMainContent(ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> filter) {
        ManageCountries newContent = new ManageCountries();
        Scheduler.getMainController().replaceContent(newContent);
        newContent.setFilter(filter);
        return newContent;
    }

    public static ManageCountries loadIntoMainContent() {
        return loadIntoMainContent(CountryModel.FACTORY.getAllItemsFilter());
    }

    @FXML // fx:id="helpBorderPane"
    private BorderPane helpBorderPane; // Value injected by FXMLLoader

    @FXML
    private void onHelpButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onHelpButtonAction", event);
        restoreNode(helpBorderPane);
    }

    @FXML
    private void onHelpOKButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onHelpOKButtonAction", event);
        collapseNode(helpBorderPane);
    }

    @Override
    protected void initialize() {
        super.initialize();
        assert helpBorderPane != null : "fx:id=\"helpBorderPane\" was not injected: check your FXML file 'ManageCountries.fxml'.";

    }

    @Override
    protected Comparator<? super CountryDAO> getComparator() {
        return CountryProperties::compare;
    }

    @Override
    protected CountryModel.Factory getModelFactory() {
        return CountryModel.FACTORY;
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
        try {
            EditCountry.editNew(getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onEditItem(CountryModel item) {
        try {
            Window w = getScene().getWindow();
            EditCountry.edit(item, w);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onDeleteItem(RecordModelContext<CountryDAO, CountryModel> item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            CountryDAO.DeleteTask task = new CountryDAO.DeleteTask(item, false);
            task.setOnSucceeded((e) -> {
                CountryEvent result = task.getValue();
                if (result instanceof CountryFailedEvent) {
                    scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure", ((CountryFailedEvent) result).getMessage(), ButtonType.OK);
                }
            });
            MainController.startBusyTaskNow(new CountryDAO.DeleteTask(item, false));
        }
    }

    @Override
    protected EventType<CountrySuccessEvent> getInsertedEventType() {
        return CountrySuccessEvent.INSERT_SUCCESS;
    }

    @Override
    protected EventType<CountrySuccessEvent> getUpdatedEventType() {
        return CountrySuccessEvent.UPDATE_SUCCESS;
    }

    @Override
    protected EventType<CountrySuccessEvent> getDeletedEventType() {
        return CountrySuccessEvent.DELETE_SUCCESS;
    }

}
