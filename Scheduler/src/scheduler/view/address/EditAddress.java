package scheduler.view.address;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CustomerDAO;
import scheduler.view.EditItem;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.MainController;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.city.CityModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.event.FxmlViewEvent;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.task.TaskWaiter;
import scheduler.model.db.CityRowData;
import scheduler.model.ui.CityItem;

/**
 * FXML Controller class for editing an {@link AddressModelImpl}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/address/EditAddress.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends EditItem.EditController<AddressDAO, AddressModel> {

    private static final Logger LOG = Logger.getLogger(EditAddress.class.getName());

    public static AddressModel editNew(MainController mainController, Stage stage) throws IOException {
        return editNew(EditAddress.class, mainController, stage);
    }

    public static AddressModel edit(AddressModel model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditAddress.class, mainController, stage);
    }

    @FXML
    private TextField address1TextField;

    @FXML
    private TextField address2TextField;

    @FXML
    private Label address1Error;

    @FXML
    private TextField postalCodeTextField;

    @FXML
    private Label postalCodeError;

    @FXML
    private TextField phoneTextField;

    @FXML
    private ComboBox<CityItem> cityComboBox;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {

        itemList = FXCollections.observableArrayList();
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        AddressModel model = this.getModel();
        event.getStage().setTitle(getResourceString(((model.isNewItem())) ? RESOURCEKEY_ADDNEWADDRESS : RESOURCEKEY_EDITADDRESS));
    }

    @Override
    protected FxRecordModel.ModelFactory<AddressDAO, AddressModel> getFactory() {
        return AddressModel.getFactory();
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.address.EditAddress#getValidationExpression
    }

    @Override
    protected void updateModel(AddressModel model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.address.EditAddress#updateModel
    }

    private ObservableList<CustomerModel> itemList;

    private class ItemsLoadTask extends TaskWaiter<List<CustomerDAO>> {

        private final AddressDAO dao;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = getModel().getDataObject();
        }

        @Override
        protected void processResult(List<CustomerDAO> result, Stage owner) {
            if (null != result && !result.isEmpty()) {
                CustomerModel.Factory factory = CustomerModel.getFactory();
                result.forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<CustomerDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
            return cf.load(connection, cf.getByAddressFilter(dao));
        }

    }

}
