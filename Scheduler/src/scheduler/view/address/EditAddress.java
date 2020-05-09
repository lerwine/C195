package scheduler.view.address;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.EditItem;
import scheduler.view.ErrorDetailDialog;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.customer.CustomerModel;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;
import scheduler.view.task.WaitBorderPane;

/**
 * FXML Controller class for editing an {@link AddressModelImpl}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/address/EditAddress.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends VBox implements EditItem.ModelEditor<AddressDAO, AddressModel> {

    private static final Logger LOG = Logger.getLogger(EditAddress.class.getName());

    public static AddressModel editNew() throws IOException {
        AddressModel.Factory factory = AddressModel.getFactory();
        return EditItem.showAndWait(EditAddress.class, factory.createNew(factory.getDaoFactory().createNew()));
    }

    public static AddressModel edit(AddressModel model) throws IOException {
        return EditItem.showAndWait(EditAddress.class, model);
    }

    private final ReadOnlyBooleanWrapper valid;

    private final ReadOnlyStringWrapper windowTitle;

    @ModelEditor
    private AddressModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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
    private ObservableList<CustomerModel> itemList;

    public EditAddress() {
        this.valid = new ReadOnlyBooleanWrapper(false);
        this.windowTitle = new ReadOnlyStringWrapper();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {

        itemList = FXCollections.observableArrayList();
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        event.getStage().setTitle(resources.getString(((model.isNewItem())) ? RESOURCEKEY_ADDNEWADDRESS : RESOURCEKEY_EDITADDRESS));
    }

    @Override
    public FxRecordModel.ModelFactory<AddressDAO, AddressModel> modelFactory() {
        return AddressModel.getFactory();
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    @Override
    public boolean applyChangesToModel() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.address.EditAddress#applyChangesToModel
    }

    private class ItemsLoadTask extends TaskWaiter<List<CustomerDAO>> {

        private final AddressDAO dao;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = model.getDataObject();
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
