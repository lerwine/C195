package scheduler.view.customer;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import scheduler.dao.CustomerDAO;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.model.ItemModel;

/**
 * FXML Controller class for viewing a list of {@link CustomerModelImpl} items.
 * <p>
 * The associated view is <a href="file:../../resources/scheduler/view/customer/ManageCustomers.fxml">/resources/scheduler/view/customer/ManageCustomers.fxml</a>.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@GlobalizationResource("scheduler/view/customer/ManageCustomers")
@FXMLResource("/scheduler/view/customer/ManageCustomers.fxml")
public final class ManageCustomers extends ListingController<CustomerDAO, CustomerModelImpl> implements ManageCustomersConstants {

    private static final Logger LOG = Logger.getLogger(ManageCustomers.class.getName());

    public static ManageCustomers loadInto(MainController mainController, Stage stage, CustomerModelFilter filter,
            Object loadEventListener) throws IOException {
        return loadInto(ManageCustomers.class, mainController, stage, filter, loadEventListener);
    }

    public static ManageCustomers loadInto(MainController mainController, Stage stage, CustomerModelFilter filter) throws IOException {
        return loadInto(mainController, stage, filter, null);
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void onAddNewItem(Event event) throws IOException {
        getMainController().addNewCustomer((Stage) ((Button) event.getSource()).getScene().getWindow());
    }

    @Override
    protected void onEditItem(Event event, CustomerModelImpl item) throws IOException {
        getMainController().editCustomer((Stage) ((Button) event.getSource()).getScene().getWindow(), item);
    }

    @Override
    protected void onDeleteItem(Event event, CustomerModelImpl item) {
        getMainController().deleteCustomer((Stage) ((Button) event.getSource()).getScene().getWindow(), item);
    }

    @Override
    protected CustomerModelImpl toModel(CustomerDAO dao) {
        return new CustomerModelImpl(dao);
    }

    @Override
    protected ItemModel.ModelFactory<CustomerDAO, CustomerModelImpl> getModelFactory() {
        return CustomerModelImpl.getFactory();
    }

}
