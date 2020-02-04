package scheduler.view.customer;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.CustomerFactory;
import scheduler.dao.CustomerImpl;
import scheduler.filter.ModelFilter;
import scheduler.util.Alerts;
import scheduler.view.CrudAction;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.TaskWaiter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/customer/ManageCustomers")
@FXMLResource("/scheduler/view/customer/ManageCustomers.fxml")
public class ManageCustomers extends ListingController<CustomerModel> {
    
    private static final Logger LOG = Logger.getLogger(ManageCustomers.class.getName());
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Customers"}.
     */
    public static final String RESOURCEKEY_MANAGECUSTOMERS = "manageCustomers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Loading Customers"}.
     */
    public static final String RESOURCEKEY_LOADINGCUSTOMERS = "loadingCustomers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Database Access Error"}.
     */
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading customers..."}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGCUSTOMERS = "errorLoadingCustomers";
    
    @FXML
    @Override
    protected void initialize() {
        super.initialize();
    }
    
    public static void loadInto(MainController mc, Stage stage, ModelFilter<CustomerModel> filter) throws IOException {
        loadInto(ManageCustomers.class, mc, stage, filter);
    }
    
    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewCustomer(event);
    }

    @Override
    protected CrudAction<CustomerModel> onEditItem(Event event, CustomerModel item) {
        return getMainController().editCustomer(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, CustomerModel item) {
        getMainController().deleteCustomer(event, item);
    }

    @Override
    protected void onFilterChanged(Stage owner) {
        TaskWaiter.execute(new CustomersLoadTask(owner));
    }
    
    private class CustomersLoadTask extends ItemsLoadTask<CustomerImpl> {
        CustomersLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
        }

        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCUSTOMERS));
        }

        @Override
        protected CustomerModel toModel(CustomerImpl result) { return new CustomerModel(result); }

        @Override
        protected Iterable<CustomerImpl> getResult(Connection connection, ModelFilter<CustomerModel> filter) throws Exception {
            return (new CustomerFactory()).load(connection, filter);
        }
        
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCUSTOMERS));
        }
        
    }

}
