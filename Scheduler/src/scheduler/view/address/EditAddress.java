package scheduler.view.address;

import java.io.IOException;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityElement;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.CityModel;
import scheduler.view.model.ItemModel;

/**
 * FXML Controller class for editing an {@link AddressModelImpl}.
 * <p>The associated view is <a href="file:../../resources/scheduler/view/address/EditAddress.fxml">/resources/scheduler/view/address/EditAddress.fxml</a>.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends EditItem.EditController<AddressDAO, AddressModelImpl> {

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New AddressDAO"}.
     */
    public static final String RESOURCEKEY_ADDNEWADDRESS = "addNewAddress";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "AddressDAO:"}.
     */
    public static final String RESOURCEKEY_ADDRESS = "address";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "AddressDAO cannot be empty."}.
     */
    public static final String RESOURCEKEY_ADDRESSCANNOTBEEMPTY = "addressCannotBeEmpty";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "CityElement:"}.
     */
    public static final String RESOURCEKEY_CITY = "city";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Country:"}.
     */
    public static final String RESOURCEKEY_COUNTRY = "country";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit AddressDAO:"}.
     */
    public static final String RESOURCEKEY_EDITADDRESS = "editAddress";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone Number:"}.
     */
    public static final String RESOURCEKEY_PHONENUMBER = "phoneNumber";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code:"}.
     */
    public static final String RESOURCEKEY_POSTALCODE = "postalCode";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code cannot be empty."}.
     */
    public static final String RESOURCEKEY_POSTALCODECANNOTBEEMPTY = "postalCodeCannotBeEmpty";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for
     * {@code "That address is referenced by one or more customers and cannot be deleted."}.
     */
    public static final String RESOURCEKEY_ADDRESSHASCUSTOMERS = "addressHasCustomers";

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
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.address.EditAddress#updateModel
    }

}
