package scheduler.view.country;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.CountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.fx.MainListingControl;
import scheduler.model.CountryProperties;
import scheduler.model.ui.CountryModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainController;
import scheduler.view.ModelFilter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.country.ManageCountriesResourceKeys.*;
import scheduler.view.event.CountryEvent;

/**
 * FXML Controller class for viewing a list of {@link CountryModel} items.
 * <p>
 * The associated view is {@code /resources/scheduler/view/country/ManageCountries.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public final class ManageCountries extends MainListingControl<CountryDAO, CountryModel, CountryEvent> {

    private static final Logger LOG = Logger.getLogger(ManageCountries.class.getName());

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
        restoreNode(helpBorderPane);
    }

    @FXML
    private void onHelpOKButtonAction(ActionEvent event) {
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
    protected void onEditItem(CountryEvent event) {
        try {
            EditCountry.edit(event.getModel(), getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onDeleteItem(CountryEvent event) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            MainController.startBusyTaskNow(new DeleteTask(event));
        }
    }

    @Override
    protected EventType<CountryEvent> getInsertedEventType() {
        return CountryEvent.INSERTED_EVENT_TYPE;
    }

    @Override
    protected EventType<CountryEvent> getUpdatedEventType() {
        return CountryEvent.UPDATED_EVENT_TYPE;
    }

    @Override
    protected EventType<CountryEvent> getDeletedEventType() {
        return CountryEvent.DELETED_EVENT_TYPE;
    }

    /**
     * @todo use implementation of {@link scheduler.dao.DataAccessObject.DeleteTask}
     */
    @Deprecated
    private class DeleteTask extends Task<CountryEvent> {

        private final CountryEvent event;

        DeleteTask(CountryEvent event) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            this.event = event;
        }

        @Override
        protected void cancelled() {
            event.setCanceled();
            super.cancelled();
        }

        @Override
        protected void failed() {
            event.setFaulted("Operation failed", "Operation encountered an unexpected error");
            super.failed();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            CountryEvent e = getValue();
            switch (e.getStatus()) {
                case CANCELED:
                case EVALUATING:
                case SUCCEEDED:
                    break;
                default:
                    AlertHelper.showWarningAlert(getScene().getWindow(), LOG, e.getSummaryTitle(), e.getDetailMessage());
                    break;
            }
        }

        @Override
        protected CountryEvent call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                return CountryDAO.FACTORY.delete(event, connector.getConnection());
            }
        }
    }

}
