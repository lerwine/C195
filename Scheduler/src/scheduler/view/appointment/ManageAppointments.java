package scheduler.view.appointment;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import scheduler.dao.AppointmentDAO;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.model.ItemModel;

/**
 * FXML Controller class for viewing a list of {@link AppointmentModel} items.
 * <p>Typically, {@link MainController} invokes {@link #loadInto(MainController, Stage, AppointmentModelFilter)} or
 * {@link #loadInto(MainController, Stage, AppointmentModelFilter, Object)}, which loads the view and instantiates the controller by
 * calling {@link #loadInto(Class, MainController, Stage, scheduler.view.ModelFilter)} or
 * {@link #loadInto(Class, MainController, Stage, scheduler.view.ModelFilter, Object)}
 * on the base class.</p>
 * <p>
 * The associated view is {@code /resources/scheduler/view/appointment/ManageAppointments.fxml}.</p>
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public final class ManageAppointments extends ListingController<AppointmentDAO, AppointmentModel> {

    private static final Logger LOG = Logger.getLogger(ManageAppointments.class.getName());

    public static ManageAppointments loadInto(MainController mainController, Stage stage, AppointmentModelFilter filter,
            Object loadEventListener) throws IOException {
        return loadInto(ManageAppointments.class, mainController, stage, filter, loadEventListener);
    }

    public static ManageAppointments loadInto(MainController mainController, Stage stage, AppointmentModelFilter filter) throws IOException {
        return loadInto(mainController, stage, filter, null);
    }
    
    // PENDING: The value of the field ManageAppointments.filterState is not used
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
        // TODO: Implement scheduler.view.appointment.ManageAppointments#filterButtonClick(ActionEvent event)
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert subHeadingLabel != null : "fx:id=\"subHeadingLabel\" was not injected: check your FXML file 'ManageAppointments.fxml'.";

    }

    @Override
    protected void onDeleteItem(Stage stage, AppointmentModel item) {
        getMainController().deleteAppointment(stage, item);
    }

    @Override
    protected AppointmentModel toModel(AppointmentDAO dao) {
        return new AppointmentModel(dao);
    }

    @Override
    protected void onAddNewItem(Stage stage) throws IOException {
        getMainController().addNewAppointment(stage);
    }

    @Override
    protected void onEditItem(Stage stage, AppointmentModel item) throws IOException {
        getMainController().editAppointment(stage, item);
    }

    @Override
    protected ItemModel.ModelFactory<AppointmentDAO, AppointmentModel> getModelFactory() {
        return AppointmentModel.getFactory();
    }

}
