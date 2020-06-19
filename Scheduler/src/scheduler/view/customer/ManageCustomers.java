package scheduler.view.customer;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.events.CustomerEvent;
import scheduler.fx.MainListingControl;
import scheduler.model.Customer;
import scheduler.model.ui.CustomerModel;
import scheduler.util.AlertHelper;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.customer.ManageCustomersResourceKeys.*;

/**
 * FXML Controller class for viewing a list of {@link CustomerModel} items.
 * <p>
 * The associated view is {@code /resources/scheduler/view/customer/ManageCustomers.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/customer/ManageCustomers")
@FXMLResource("/scheduler/view/customer/ManageCustomers.fxml")
public final class ManageCustomers extends MainListingControl<CustomerDAO, CustomerModel, CustomerEvent> {

    private static final Logger LOG = Logger.getLogger(ManageCustomers.class.getName());

    public static ManageCustomers loadIntoMainContent(CustomerModelFilter filter) {
        ManageCustomers newContent = new ManageCustomers();
        Scheduler.getMainController().replaceContent(newContent);
        newContent.setFilter(filter);
        return newContent;
    }

    @FXML // fx:id="customerFilterBorderPane"
    private BorderPane customerFilterBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="activeCustomersRadioButton"
    private RadioButton activeCustomersRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="customerFilterToggleGroup"
    private ToggleGroup customerFilterToggleGroup; // Value injected by FXMLLoader

    @FXML // fx:id="inactiveCustomersRadioButton"
    private RadioButton inactiveCustomersRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="allCustomersRadioButton"
    private RadioButton allCustomersRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="helpBorderPane"
    private BorderPane helpBorderPane; // Value injected by FXMLLoader

    @FXML
    private void filterButtonClick(ActionEvent event) {
        restoreNode(customerFilterBorderPane);
    }

    @FXML
    private void onHelpButtonAction(ActionEvent event) {
        restoreNode(helpBorderPane);
    }

    @FXML
    private void onHelpOKButtonAction(ActionEvent event) {
        collapseNode(helpBorderPane);
    }

    @FXML
    private void onCustomerFilterCancelButtonAction(ActionEvent event) {
        customerFilterBorderPane.setVisible(false);
    }

    @FXML
    private void onCustomerFilterOKButtonAction(ActionEvent event) {
        if (inactiveCustomersRadioButton.isSelected()) {
            setFilter(CustomerModelFilter.inactive());
        } else if (allCustomersRadioButton.isSelected()) {
            setFilter(CustomerModelFilter.all());
        } else {
            setFilter(CustomerModelFilter.active());
        }
        collapseNode(customerFilterBorderPane);
    }

    @Override
    protected void initialize() {
        super.initialize();
        assert customerFilterBorderPane != null : "fx:id=\"customerFilterBorderPane\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert activeCustomersRadioButton != null : "fx:id=\"activeCustomersRadioButton\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert customerFilterToggleGroup != null : "fx:id=\"customerFilterToggleGroup\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert inactiveCustomersRadioButton != null : "fx:id=\"inactiveCustomersRadioButton\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert allCustomersRadioButton != null : "fx:id=\"allCustomersRadioButton\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert helpBorderPane != null : "fx:id=\"helpBorderPane\" was not injected: check your FXML file 'ManageCustomers.fxml'.";

        bindExtents(customerFilterBorderPane, this);
        bindExtents(helpBorderPane, this);
    }

    @Override
    protected Comparator<? super CustomerDAO> getComparator() {
        return Customer::compare;
    }

    @Override
    protected CustomerModel.Factory getModelFactory() {
        return CustomerModel.FACTORY;
    }

    @Override
    protected String getLoadingTitle() {
        return getResources().getString(RESOURCEKEY_LOADINGCUSTOMERS);
    }

    @Override
    protected String getFailMessage() {
        return getResources().getString(RESOURCEKEY_ERRORLOADINGCUSTOMERS);
    }

    @Override
    protected void onNewItem() {
        try {
            EditCustomer.editNew(null, getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onEditItem(CustomerEvent event) {
        try {
            CustomerModel m = event.getModel();
            Window w = getScene().getWindow();
            EditCustomer.edit(m, w);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onDeleteItem(CustomerEvent event) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            MainController.startBusyTaskNow(new DataAccessObject.DeleteTask<>(event));
        }
    }

    @Override
    protected EventType<CustomerEvent> getInsertedEventType() {
        return CustomerEvent.DB_INSERT_EVENT_TYPE;
    }

    @Override
    protected EventType<CustomerEvent> getUpdatedEventType() {
        return CustomerEvent.UPDATED_EVENT_TYPE;
    }

    @Override
    protected EventType<CustomerEvent> getDeletedEventType() {
        return CustomerEvent.DB_DELETE_EVENT_TYPE;
    }

}
