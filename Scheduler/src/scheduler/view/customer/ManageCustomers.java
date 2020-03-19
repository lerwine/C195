package scheduler.view.customer;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import scheduler.dao.CustomerFilter;
import scheduler.dao.CustomerImpl;
import scheduler.view.EditItem;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
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

    /**
     * Loads {@link CustomerModel} listing view and controller into the {@link MainController}.
     *
     * @param mainController The {@link MainController} to contain the {@link CustomerModel} listing.
     * @param stage The {@link Stage} for the view associated with the current main controller.
     * @param filter The {@link CustomerFilter} to use for loading and filtering {@link CustomerModel} items.
     * @throws IOException if unable to load the view.
     */
    public static void setContent(MainController mainController, Stage stage, CustomerFilter filter) throws IOException {
        setContent(mainController, ManageCustomers.class, stage, filter);
    }

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
    protected EditItem.ShowAndWaitResult<CustomerModel> onEditItem(Event event, CustomerModel item) {
        return getMainController().editCustomer(event, item);
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
    protected ItemEventManager<ItemEvent<CustomerModel>> getItemAddManager() {
        return getMainController().getCustomerAddManager();
    }

    @Override
    protected ItemEventManager<ItemEvent<CustomerModel>> getItemRemoveManager() {
        return getMainController().getCustomerRemoveManager();
    }

}
