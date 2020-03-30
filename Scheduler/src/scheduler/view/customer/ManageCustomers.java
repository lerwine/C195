package scheduler.view.customer;

import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import scheduler.dao.CustomerImpl;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.ItemModel;
import scheduler.view.MainController;

/**
 * FXML Controller class for viewing a list of {@link CustomerModel} items. This is loaded as content of {@link MainController} using
 * {@link #setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.CustomerFilter)}.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/customer/ManageCustomers")
@FXMLResource("/scheduler/view/customer/ManageCustomers.fxml")
public final class ManageCustomers extends ListingController<CustomerImpl, CustomerModel> implements ManageCustomersConstants {

    private static final Logger LOG = Logger.getLogger(ManageCustomers.class.getName());

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewCustomer(event);
    }

    @Override
    protected void onEditItem(Event event, CustomerModel item) {
        getMainController().editCustomer(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, CustomerModel item) {
        getMainController().deleteCustomer(event, item);
    }

    @Override
    protected CustomerModel toModel(CustomerImpl dao) {
        return new CustomerModel(dao);
    }

    @Override
    protected CustomerImpl.FactoryImpl getDaoFactory() {
        return CustomerImpl.getFactory();
    }

    @Override
    protected ItemModel.ModelFactory<CustomerImpl, CustomerModel> getModelFactory() {
        return CustomerModel.getFactory();
    }

}
