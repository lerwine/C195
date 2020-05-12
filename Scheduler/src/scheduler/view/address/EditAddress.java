package scheduler.view.address;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;
import scheduler.view.EditItem;
import scheduler.view.ErrorDetailControl;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.customer.CustomerModel;
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

    private final ObservableList<CustomerModel> itemList;

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

    public EditAddress() {
        this.valid = new ReadOnlyBooleanWrapper(false);
        this.windowTitle = new ReadOnlyStringWrapper();
        itemList = FXCollections.observableArrayList();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        waitBorderPane.startNow(new ItemsLoadTask());
        windowTitle.set(resources.getString(((model.isNewItem())) ? RESOURCEKEY_ADDNEWADDRESS : RESOURCEKEY_EDITADDRESS));
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

    private class ItemsLoadTask extends Task<List<CustomerDAO>> {

        private final AddressDAO dao;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = model.getDataObject();
        }

        @Override
        protected void succeeded() {
            List<CustomerDAO> result = getValue();
            if (null != result && !result.isEmpty()) {
                CustomerModel.Factory factory = CustomerModel.getFactory();
                result.forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
            super.succeeded();
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DBREADERROR), getException());
            super.failed();
        }

        @Override
        protected List<CustomerDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
                return cf.load(dbConnector.getConnection(), cf.getByAddressFilter(dao));
            }
        }

    }

}
