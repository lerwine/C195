package scheduler.view.customer;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import scheduler.Scheduler;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.CustomerDAO;
import scheduler.dao.event.CustomerDaoEvent;
import scheduler.fx.MainListingControl;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
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
public final class ManageCustomers extends MainListingControl<CustomerDAO, CustomerModel, CustomerDaoEvent> {

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
    void filterButtonClick(ActionEvent event) {
        restoreNode(customerFilterBorderPane);
    }

    @FXML
    private void onHelpButtonAction(ActionEvent event) {
        restoreNode(helpBorderPane);
    }

    @FXML
    void onHelpOKButtonAction(ActionEvent event) {
        collapseNode(helpBorderPane);
    }

    @FXML
    void onCustomerFilterCancelButtonAction(ActionEvent event) {
        customerFilterBorderPane.setVisible(false);
    }

    @FXML
    void onCustomerFilterOKButtonAction(ActionEvent event) {
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
    protected CustomerModel.Factory getModelFactory() {
        return CustomerModel.getFactory();
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
        getMainController().addNewCustomer(null, getScene().getWindow(), true);
    }

    @Override
    protected void onEditItem(CustomerModel item) {
        getMainController().editCustomer(item, getScene().getWindow());
    }

    @Override
    protected void onDeleteItem(CustomerModel item) {
        getMainController().deleteCustomer(item);
    }

    @Override
    protected EventType<CustomerDaoEvent> getInsertedEventType() {
        return CustomerDaoEvent.CUSTOMER_DAO_INSERT;
    }

    @Override
    protected EventType<CustomerDaoEvent> getUpdatedEventType() {
        return CustomerDaoEvent.CUSTOMER_DAO_UPDATE;
    }

    @Override
    protected EventType<CustomerDaoEvent> getDeletedEventType() {
        return CustomerDaoEvent.CUSTOMER_DAO_DELETE;
    }

}
