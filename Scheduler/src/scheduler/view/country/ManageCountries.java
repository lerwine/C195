package scheduler.view.country;

import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import scheduler.events.CountryOpRequestEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.model.ModelHelper.CountryHelper;
import scheduler.model.fx.CountryModel;
import scheduler.util.AlertHelper;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainController;
import scheduler.view.MainListingControl;
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
public final class ManageCountries extends MainListingControl<CountryDAO, CountryModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ManageCountries.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(ManageCountries.class.getName());

    public static ManageCountries loadIntoMainContent(ModelFilter<CountryDAO, CountryModel, DaoFilter<CountryDAO>> filter) {
        ManageCountries newContent = initialize(new ManageCountries());
        Scheduler.getMainController().replaceContent(newContent);
        newContent.setFilter(filter);
        return newContent;
    }

    public static ManageCountries loadIntoMainContent() {
        return loadIntoMainContent(CountryModel.FACTORY.getAllItemsFilter());
    }

    @FXML // fx:id="helpBorderPane"
    private BorderPane helpBorderPane; // Value injected by FXMLLoader

    private ManageCountries() {
        
    }
    
    @FXML
    private void onHelpButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onHelpButtonAction", event);
        restoreNode(helpBorderPane);
        LOG.exiting(LOG.getName(), "onHelpButtonAction");
    }

    @FXML
    private void onHelpOKButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onHelpOKButtonAction", event);
        collapseNode(helpBorderPane);
        LOG.exiting(LOG.getName(), "onHelpOKButtonAction");
    }

    @Override
    protected void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        super.initialize();
        assert helpBorderPane != null : "fx:id=\"helpBorderPane\" was not injected: check your FXML file 'ManageCountries.fxml'.";
        LOG.exiting(LOG.getName(), "initialize");
    }

    @Override
    protected Comparator<? super CountryDAO> getComparator() {
        return CountryHelper::compare;
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
    protected void onDeleteItem(CountryModel item) {
        CountryOpRequestEvent deleteRequestEvent = new CountryOpRequestEvent(item, this, true);
        Event.fireEvent(item.dataObject(), deleteRequestEvent);
        Stage stage = (Stage) getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stage, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    CountryDAO.DeleteTask task = new CountryDAO.DeleteTask(item, false);
                    task.setOnSucceeded(this::onDeleteTaskSucceeded);
                    MainController.startBusyTaskNow(task);
                }
            });
        }
    }

    private void onDeleteTaskSucceeded(WorkerStateEvent event) {
        LOG.entering(LOG.getName(), "onDeleteTaskSucceeded", event);
        CountryEvent countryEvent = (CountryEvent) event.getSource().getValue();
        if (null != countryEvent && countryEvent instanceof CountryFailedEvent) {
            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                    ((ModelFailedEvent<CountryDAO, CountryModel>) countryEvent).getMessage(), ButtonType.OK);
        }
        LOG.exiting(LOG.getName(), "onDeleteTaskSucceeded");
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
