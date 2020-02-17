package scheduler.view.customer;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.LookupFilter;
import scheduler.util.Alerts;
import scheduler.view.EditItem;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/customer/ManageCustomers")
@FXMLResource("/scheduler/view/customer/ManageCustomers.fxml")
public final class ManageCustomers extends ListingController<CustomerImpl, CustomerModel> {

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

//    public static void setContent(MainController mc, Stage stage, ModelFilter<CustomerModel> filter) throws IOException {
//        ListingController.setContent(ManageCustomers.class, mc, stage, filter);
//    }
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
        getMainController().deleteCustomer(event, item, (connection) -> {
            throw new UnsupportedOperationException("Not implemented");
//            AppointmentFactory factory = new AppointmentFactory();
//            if (factory.count(connection, AppointmentFactory.customerIdIs(item.getDataObject().getPrimaryKey())) == 0)
//                return "";
//            return getResourceString(RESOURCEKEY_CUSTOMERHASAPPOINTMENTS);
        });
    }

    @Override
    protected CustomerModel toModel(CustomerImpl result) {
        try {
            return new CustomerModel(result);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ManageCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected LookupFilter<CustomerImpl, CustomerModel> getDefaultFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected DataObjectImpl.Factory<CustomerImpl> getDaoFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    protected void onFilterChanged(Stage owner) { TaskWaiter.execute(new CustomersLoadTask(owner)); }
    private class CustomersLoadTask extends ItemsLoadTask {

        CustomersLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
        }

        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCUSTOMERS));
        }

//        @Override
//        protected Iterable<CustomerFactory.CustomerImpl> getResult(Connection connection, ModelFilter<CustomerModel> filter) throws Exception {
//            return (new CustomerFactory()).load(connection, filter);
//        }
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGCUSTOMERS));
        }

    }

}
