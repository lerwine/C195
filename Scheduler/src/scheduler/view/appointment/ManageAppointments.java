package scheduler.view.appointment;

import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.view.ItemModel;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

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

    private FilterOptionState filterState = null;

    @FXML // fx:id="headingLabel"
    private Label headingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="subHeadingLabel"
    private Label subHeadingLabel; // Value injected by FXMLLoader

    @FXML
    void filterButtonClick(ActionEvent event) {
//        FilterOptionState result = EditAppointmentFilter.waitEdit(filterState, (Stage) ((Button) event.getSource()).getScene().getWindow());
//        if (null != result && !result.equals(filterState)) {
//            filterState = result;
//            AlertHelper.showWarningAlert(((Button) event.getSource()).getScene().getWindow(), "Reload after filterButtonClick not implemented");
//        }
        throw new UnsupportedOperationException();
        // TODO: Implement this
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
    protected void onDeleteItem(Event event, AppointmentModel item) {
        getMainController().deleteAppointment(event, item);
    }

    @Override
    protected AppointmentModel toModel(AppointmentImpl dao) {
        return new AppointmentModel(dao);
    }

    @Override
    protected DataObjectImpl.DaoFactory<AppointmentImpl> getDaoFactory() {
        return AppointmentImpl.getFactory();
    }

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewAppointment(event);
    }

    @Override
    protected void onEditItem(Event event, AppointmentModel item) {
        getMainController().editAppointment(event, item);
    }

    @Override
    protected ItemModel.ModelFactory<AppointmentImpl, AppointmentModel> getModelFactory() {
        return AppointmentModel.getFactory();
    }

}
