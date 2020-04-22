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
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityElement;
import scheduler.dao.CustomerDAO;
import scheduler.util.AlertHelper;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.city.CityModel;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;

/**
 * FXML Controller class for editing an {@link AddressModelImpl}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/address/EditAddress.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends EditItem.EditController<AddressDAO, AddressModelImpl> {

    private static final Logger LOG = Logger.getLogger(EditAddress.class.getName());

    public static AddressModelImpl editNew(MainController mainController, Stage stage) throws IOException {
        return editNew(EditAddress.class, mainController, stage);
    }

    public static AddressModelImpl edit(AddressModelImpl model, MainController mainController, Stage stage) throws IOException {
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
    private ComboBox<CityModel<? extends CityElement>> cityComboBox;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {

        itemList = FXCollections.observableArrayList();
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        AddressModelImpl model = this.getModel();
        event.getStage().setTitle(getResourceString(((model.isNewItem())) ? RESOURCEKEY_ADDNEWADDRESS : RESOURCEKEY_EDITADDRESS));
    }

    @Override
    protected ItemModel.ModelFactory<AddressDAO, AddressModelImpl> getFactory() {
        return AddressModelImpl.getFactory();
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.address.EditAddress#getValidationExpression
    }

    @Override
    protected void updateModel(AddressModelImpl model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.address.EditAddress#updateModel
    }

    private ObservableList<CustomerModelImpl> itemList;

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
                CustomerModelImpl.Factory factory = CustomerModelImpl.getFactory();
                result.forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, ex);
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
