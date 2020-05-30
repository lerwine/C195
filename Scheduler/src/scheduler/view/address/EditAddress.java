package scheduler.view.address;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.ICityDAO;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;
import scheduler.view.EditItem;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing an {@link AddressModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/address/EditAddress.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends VBox implements EditItem.ModelEditor<AddressDAO, AddressModel> {

    public static AddressModel editNew(CityModel city, Window parentWindow, boolean keepOpen) throws IOException {
        AddressModel.Factory factory = AddressModel.getFactory();
        EditAddress control = new EditAddress();
        control.initialCity = city;
        return EditItem.showAndWait(parentWindow, EditAddress.class, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static AddressModel edit(AddressModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditAddress.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CustomerModel> itemList;
    // TODO: Replace this with property on control
    private CityModel initialCity;

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
    private Label addressError;

    @FXML
    private TextField postalCodeTextField;

    @FXML
    private Label postalCodeError;

    @FXML
    private TextField phoneTextField;

    @FXML
    private ComboBox<CityItem<? extends ICityDAO>> cityComboBox;

    @FXML
    private Label cityError;

    public EditAddress() {
        windowTitle = new ReadOnlyStringWrapper("");
        valid = new ReadOnlyBooleanWrapper(false);
        modified = new ReadOnlyBooleanWrapper(false);
        itemList = FXCollections.observableArrayList();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        waitBorderPane.startNow(pane, new ItemsLoadTask());
        // CURRENT: Create individual validators
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
    public boolean isModified() {
        return modified.get();
    }

    @Override
    public ReadOnlyBooleanProperty modifiedProperty() {
        return modified.getReadOnlyProperty();
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
    public void onNewModelSaved() {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.address.EditAddress#applyEditMode
    }

    @Override
    public void updateModel() {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.address.EditAddress#updateModel
    }

    private class ItemsLoadTask extends Task<List<CustomerDAO>> {

        private final AddressDAO dao;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = model.dataObject();
        }

        @Override
        protected void done() {
            if (model.isNewRow()) {
                windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWADDRESS));
            } else {
                windowTitle.set(resources.getString(RESOURCEKEY_EDITADDRESS));
            }
            super.done();
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
        protected List<CustomerDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
                return cf.load(dbConnector.getConnection(), cf.getByAddressFilter(dao));
            }
        }

    }

}
