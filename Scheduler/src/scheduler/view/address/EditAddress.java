package scheduler.view.address;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
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
import scheduler.fx.ErrorDetailControl;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.EditItem;
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

    public static AddressModel editNew(CityItem city, Window parentWindow, boolean keepOpen) throws IOException {
        AddressModel.Factory factory = AddressModel.getFactory();
        AddressModel model = factory.createNew(factory.getDaoFactory().createNew());
        if (null != city) {
            model.setCity(city);
        }
        return EditItem.showAndWait(parentWindow, EditAddress.class, model, keepOpen);
    }

    public static AddressModel edit(AddressModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditAddress.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper changed;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CustomerModel> itemList;
    private boolean addressValid;
    private boolean postalCodeValid;
    private boolean cityValid;

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
    private ComboBox<CityItem> cityComboBox;

    @FXML
    private Label cityError;

    public EditAddress() {
        changed = new ReadOnlyBooleanWrapper(true);
        this.valid = new ReadOnlyBooleanWrapper(false);
        addressValid = postalCodeValid = cityValid = false;
        this.windowTitle = new ReadOnlyStringWrapper();
        itemList = FXCollections.observableArrayList();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        waitBorderPane.startNow(new ItemsLoadTask());
        if (model.isNewItem()) {
            address1TextField.textProperty().addListener(this::validateAddress1);
            address2TextField.textProperty().addListener(this::validateAddress2);
            postalCodeTextField.textProperty().addListener(this::validatePostalCode);
            cityComboBox.getSelectionModel().selectedItemProperty().addListener(this::validateCity);
        } else {
            address1TextField.textProperty().addListener(this::checkAddress1Changed);
            address2TextField.textProperty().addListener(this::checkAddress2Changed);
            postalCodeTextField.textProperty().addListener(this::checkPostalCodeChanged);
            phoneTextField.textProperty().addListener(this::checkPhoneChanged);
            cityComboBox.getSelectionModel().selectedItemProperty().addListener(this::checkCityChanged);
        }
    }

    private void validateAddress1(String address1) {
        if (address1.trim().isEmpty() || address2TextField.getText().trim().isEmpty()) {
            if (addressValid) {
                addressValid = false;
                valid.set(false);
                restoreNode(addressError);
            }
        } else if (!addressValid) {
            addressValid = true;
            collapseNode(addressError);
            if (postalCodeValid && cityValid) {
                valid.set(true);
            }
        }
    }

    private void validateAddress1(Observable observable) {
        validateAddress1(((StringProperty) observable).get());
    }

    private void checkAddress1Changed(Observable observable) {
        String s = ((StringProperty) observable).get();
        validateAddress1(s);
        if (s.equals(model.getAddress1()) && address2TextField.getText().equals(model.getAddress2()) &&
                postalCodeTextField.getText().equals(model.getPostalCode()) && phoneTextField.getText().equals(model.getPhone())) {
            CityItem selectedItem = cityComboBox.getSelectionModel().getSelectedItem();
            changed.set(null == selectedItem || selectedItem.getPrimaryKey() != model.getCity().getPrimaryKey());
        } else {
            changed.set(true);
        }
    }

    private void validateAddress2(String address2) {
        if (address2.trim().isEmpty() || address1TextField.getText().trim().isEmpty()) {
            if (addressValid) {
                addressValid = false;
                valid.set(false);
                restoreNode(addressError);
            }
        } else if (!addressValid) {
            addressValid = true;
            collapseNode(addressError);
            if (postalCodeValid && cityValid) {
                valid.set(true);
            }
        }
    }

    private void validateAddress2(Observable observable) {
        validateAddress2(((StringProperty) observable).get());
    }

    private void checkAddress2Changed(Observable observable) {
        String s = ((StringProperty) observable).get();
        validateAddress2(s);
        if (s.equals(model.getAddress2()) && address1TextField.getText().equals(model.getAddress1()) &&
                postalCodeTextField.getText().equals(model.getPostalCode()) && phoneTextField.getText().equals(model.getPhone())) {
            CityItem selectedItem = cityComboBox.getSelectionModel().getSelectedItem();
            changed.set(null == selectedItem || selectedItem.getPrimaryKey() != model.getCity().getPrimaryKey());
        } else {
            changed.set(true);
        }
    }

    private void validatePostalCode(String postalCode) {
        if (postalCode.trim().isEmpty()) {
            if (postalCodeValid) {
                postalCodeValid = false;
                valid.set(false);
                restoreNode(postalCodeError);
            }
        } else if (!postalCodeValid) {
            postalCodeValid = true;
            collapseNode(postalCodeError);
            if (addressValid && cityValid) {
                valid.set(true);
            }
        }
    }

    private void validatePostalCode(Observable observable) {
        validatePostalCode(((StringProperty) observable).get());
    }

    private void checkPostalCodeChanged(Observable observable) {
        String s = ((StringProperty) observable).get();
        validatePostalCode(s);
        if (s.equals(model.getPostalCode()) && address1TextField.getText().equals(model.getAddress1()) && 
                address2TextField.getText().equals(model.getAddress2()) && phoneTextField.getText().equals(model.getPhone())) {
            CityItem selectedItem = cityComboBox.getSelectionModel().getSelectedItem();
            changed.set(null == selectedItem || selectedItem.getPrimaryKey() != model.getCity().getPrimaryKey());
        } else {
            changed.set(true);
        }
    }

    private void checkPhoneChanged(Observable observable) {
        String s = ((StringProperty) observable).get();
        if (s.equals(model.getPhone()) && address1TextField.getText().equals(model.getAddress1()) && 
                address2TextField.getText().equals(model.getAddress2()) && postalCodeTextField.getText().equals(model.getPostalCode())) {
            CityItem selectedItem = cityComboBox.getSelectionModel().getSelectedItem();
            changed.set(null == selectedItem || selectedItem.getPrimaryKey() != model.getCity().getPrimaryKey());
        } else {
            changed.set(true);
        }
    }

    private void validateCity(CityItem city) {
        if (null == city) {
            if (cityValid) {
                cityValid = false;
                valid.set(false);
                restoreNode(cityError);
            }
        } else if (!cityValid) {
            cityValid = true;
            collapseNode(cityError);
            if (addressValid && postalCodeValid) {
                valid.set(true);
            }
        }
    }

    private void validateCity(Observable observable) {
        validateCity(((ReadOnlyObjectProperty<CityItem>) observable).get());
    }

    private void checkCityChanged(Observable observable) {
        CityItem city = ((ReadOnlyObjectProperty<CityItem>) observable).get();
        validateCity(city);
        CityItem selectedItem = cityComboBox.getSelectionModel().getSelectedItem();
        changed.set(null == selectedItem || selectedItem.getPrimaryKey() != model.getCity().getPrimaryKey() ||
                !(address1TextField.getText().equals(model.getAddress1()) && address2TextField.getText().equals(model.getAddress2()) &&
                postalCodeTextField.getText().equals(model.getPostalCode()) && phoneTextField.getText().equals(model.getPhone())));
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
    public boolean isChanged() {
        return changed.get();
    }

    @Override
    public ReadOnlyBooleanProperty changedProperty() {
        return changed.getReadOnlyProperty();
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
        protected void done() {
            if (model.isNewItem()) {
                windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWADDRESS));
            } else {
                changed.set(false);
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
