package scheduler.view.appointment;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.ModelFilter;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.ListingController;
import scheduler.view.MainController;

/**
 * FXML Controller class for listing {@link AppointmentModel} items.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public final class ManageAppointments extends ListingController<AppointmentImpl, AppointmentModel> implements ManageAppointmentsResourceKeys {

    private static final Logger LOG = Logger.getLogger(ManageAppointments.class.getName());

    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    @FXML
    private Label headingLabel;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    public ManageAppointments() {
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();
        assert headingLabel != null : String.format("fx:id=\"headingLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
    }

    public static void setContent(MainController mainController, Stage stage, ModelFilter<AppointmentImpl, AppointmentModel> filter) throws IOException {
        setContent(mainController, ManageAppointments.class, stage).changeFilter(filter, stage);
    }
    
    //</editor-fold>

    @Override
    protected ItemEventManager<ItemEvent<AppointmentModel>> getItemAddManager(MainController mainController) {
        return getMainController().getAppointmentAddManager();
    }

    @Override
    protected ItemEventManager<ItemEvent<AppointmentModel>> getItemRemoveManager(MainController mainController) {
        return getMainController().getAppointmentRemoveManager();
    }

    @Override
    protected void onDeleteItem(Event event, AppointmentModel item) {
        getMainController().deleteAppointment(event, item);
    }

    @Override
    protected AppointmentModel toModel(AppointmentImpl result) {
        return new AppointmentModel(result);
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

}
