package scheduler.view.appointment;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import scheduler.dao.AppointmentFilter;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.util.Alerts;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.EditItem;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.dao.ModelListingFilter;

/**
 * FXML Controller class for viewing a list of {@link AppointmentModel} items. This is loaded as content of {@link MainController} using
 * {@link #setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.AppointmentFilter)}.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public final class ManageAppointments extends ListingController<AppointmentImpl, AppointmentModel> implements ManageAppointmentsResourceKeys {

    private static final Logger LOG = Logger.getLogger(ManageAppointments.class.getName());

    /**
     * Loads {@link AppointmentModel} listing view and controller into the {@link MainController}.
     *
     * @param mainController The {@link MainController} to contain the {@link AppointmentModel} listing.
     * @param stage The {@link Stage} for the view associated with the current main controller.
     * @param filter The {@link AppointmentFilter} to use for loading and filtering {@link AppointmentModel} items.
     * @throws IOException if unable to load the view.
     */
    public static void setContent(MainController mainController, Stage stage, AppointmentFilter filter) throws IOException {
        setContent(mainController, ManageAppointments.class, stage, filter);
    }
    private FilterOptionState filterState = null;

    @FXML // fx:id="headingLabel"
    private Label headingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="subHeadingLabel"
    private Label subHeadingLabel; // Value injected by FXMLLoader

    @FXML
    void filterButtonClick(ActionEvent event) {
        FilterOptionState result = EditAppointmentFilter.waitEdit(filterState, (Stage) ((Button) event.getSource()).getScene().getWindow());
        if (null != result && !result.equals(filterState)) {
            filterState = result;
            Alerts.showWarningAlert(((Button) event.getSource()).getScene().getWindow(), "Reload after filterButtonClick not implemented");
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert subHeadingLabel != null : "fx:id=\"subHeadingLabel\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert listingTableView != null : "fx:id=\"listingTableView\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
    }

    @Override
    protected ItemEventManager<ItemEvent<AppointmentModel>> getItemAddManager() {
        return getMainController().getAppointmentAddManager();
    }

    @Override
    protected ItemEventManager<ItemEvent<AppointmentModel>> getItemRemoveManager() {
        return getMainController().getAppointmentRemoveManager();
    }

    @Override
    protected void onDeleteItem(Event event, AppointmentModel item) {
        getMainController().deleteAppointment(event, item);
    }

    @Override
    protected AppointmentModel toModel(AppointmentImpl dao) {
        return new AppointmentModel(dao);
    }

    @Override
    protected DataObjectImpl.Factory<AppointmentImpl, AppointmentModel> getDaoFactory() {
        return AppointmentImpl.getFactory();
    }

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewAppointment(event);
    }

    @Override
    protected EditItem.ShowAndWaitResult<AppointmentModel> onEditItem(Event event, AppointmentModel item) {
        return getMainController().editAppointment(event, item);
    }

    @Override
    public synchronized void changeFilter(ModelListingFilter<AppointmentImpl, AppointmentModel> value, Stage stage, Consumer<Boolean> onChangeComplete) {
        assert null == value || value instanceof AppointmentFilter : "Invalid filter type";
        super.changeFilter(value, stage, onChangeComplete);
    }

    @Override
    protected void onItemsLoaded(ModelListingFilter<AppointmentImpl, AppointmentModel> filter, Stage owner) {
        super.onItemsLoaded(filter, owner);

        AppointmentFilter appointmentFilter = (AppointmentFilter) filter;
    }

}
