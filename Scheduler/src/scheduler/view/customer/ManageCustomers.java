package scheduler.view.customer;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.AppointmentFactory;
import scheduler.dao.CustomerFactory;
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
    
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit"}.
     */
    public static final String RESOURCEKEY_EDIT = "edit";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Database Access Error"}.
     */
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete"}.
     */
    public static final String RESOURCEKEY_DELETE = "delete";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Active"}.
     */
    public static final String RESOURCEKEY_ACTIVE = "active";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Address"}.
     */
    public static final String RESOURCEKEY_ADDRESS = "address";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created By"}.
     */
    public static final String RESOURCEKEY_CREATEDBY = "createdBy";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created On"}.
     */
    public static final String RESOURCEKEY_CREATEDON = "createdOn";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Customer Name"}.
     */
    public static final String RESOURCEKEY_CUSTOMERNAME = "customerName";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Customers"}.
     */
    public static final String RESOURCEKEY_MANAGECUSTOMERS = "manageCustomers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated By"}.
     */
    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated On"}.
     */
    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "New"}.
     */
    public static final String RESOURCEKEY_NEW = "new";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "City"}.
     */
    public static final String RESOURCEKEY_CITY = "city";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code"}.
     */
    public static final String RESOURCEKEY_POSTALCODE = "postalCode";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Country"}.
     */
    public static final String RESOURCEKEY_COUNTRY = "country";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone"}.
     */
    public static final String RESOURCEKEY_PHONE = "phone";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Loading Customers"}.
     */
    public static final String RESOURCEKEY_LOADINGCUSTOMERS = "loadingCustomers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading customers..."}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGCUSTOMERS = "errorLoadingCustomers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That customer is referenced in one or more appointments..."}.
     */
    public static final String RESOURCEKEY_CUSTOMERHASAPPOINTMENTS = "customerHasAppointments";

    //</editor-fold>
    
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
        getMainController().deleteCustomer(event, item, (connection) -> {
            AppointmentFactory factory = new AppointmentFactory();
            if (factory.count(connection, AppointmentFactory.customerIdIs(item.getDataObject().getPrimaryKey())) == 0)
                return "";
            return getResourceString(RESOURCEKEY_CUSTOMERHASAPPOINTMENTS);
        });
    }

    @Override
    protected void onFilterChanged(Stage owner) { TaskWaiter.execute(new CustomersLoadTask(owner)); }
    
    private class CustomersLoadTask extends ItemsLoadTask<CustomerFactory.CustomerImpl> {
        CustomersLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
        }

        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCUSTOMERS));
        }

        @Override
        protected CustomerModel toModel(CustomerFactory.CustomerImpl result) { return new CustomerModel(result); }

        @Override
        protected Iterable<CustomerFactory.CustomerImpl> getResult(Connection connection, ModelFilter<CustomerModel> filter) throws Exception {
            return (new CustomerFactory()).load(connection, filter);
        }
        
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCUSTOMERS));
        }
        
    }

}
