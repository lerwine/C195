package scheduler.view.customer;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.CustomerDAO;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.model.ui.FxRecordModel;

/**
 * FXML Controller class for viewing a list of {@link CustomerModel} items.
 * <p>
 * The associated view is {@code /resources/scheduler/view/customer/ManageCustomers.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/customer/ManageCustomers")
@FXMLResource("/scheduler/view/customer/ManageCustomers.fxml")
public final class ManageCustomers extends ListingController<CustomerDAO, CustomerModel> {

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
    protected void onAddNewItem(Stage stage) throws IOException {
        getMainController(stage.getScene()).addNewCustomer(stage);
    }

    @Override
    protected void onEditItem(Stage stage, CustomerModel item) throws IOException {
        getMainController(stage.getScene()).editCustomer(stage, item);
    }

    @Override
    protected void onDeleteItem(Stage stage, CustomerModel item) {
        getMainController(stage.getScene()).deleteCustomer(stage, item);
    }

    @Override
    protected CustomerModel toModel(CustomerDAO dao) {
        return new CustomerModel(dao);
    }

    @Override
    protected FxRecordModel.ModelFactory<CustomerDAO, CustomerModel> getModelFactory() {
        return CustomerModel.getFactory();
    }

}
