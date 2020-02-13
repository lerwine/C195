package scheduler.view.city;

import java.sql.Connection;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scheduler.dao.AddressFactory;
import scheduler.view.EditItem;
import scheduler.view.address.AddressModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.country.CountryModel;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/city/EditCity")
@FXMLResource("/scheduler/view/city/EditCity.fxml")
public final class EditCity extends EditItem.EditController<CityModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New City"}.
     */
    public static final String RESOURCEKEY_ADDNEWCITY = "addNewCity";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Country:"}.
     */
    public static final String RESOURCEKEY_COUNTRY = "country";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit City"}.
     */
    public static final String RESOURCEKEY_EDITCITY = "editCity";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name:"}.
     */
    public static final String RESOURCEKEY_NAME = "name";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name cannot be empty."}.
     */
    public static final String RESOURCEKEY_NAMECANNOTBEEMPTY = "nameCannotBeEmpty";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That city is referenced by one or more addresses and cannot be deleted."}.
     */
    public static final String RESOURCEKEY_CITYHASADDRESSES = "cityHasAddresses";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Addresses"}.
     */
    public static final String RESOURCEKEY_ADDRESSES = "addresses";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Address"}.
     */
    public static final String RESOURCEKEY_ADDRESS = "address";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone Number"}.
     */
    public static final String RESOURCEKEY_PHONENUMBER = "phoneNumber";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code"}.
     */
    public static final String RESOURCEKEY_POSTALCODE = "postalCode";

    //</editor-fold>
    
    private int countryId;
    
    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ChoiceBox<CountryModel> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="editCountryButton"
    private Button editCountryButton; // Value injected by FXMLLoader

    @FXML // fx:id="newCountryButton"
    private Button newCountryButton; // Value injected by FXMLLoader

    @FXML // fx:id="addressesLabel"
    private Label addressesLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressTableView"
    private TableView<AddressModel> addressTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addAddressButton"
    private Button addAddressButton; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert editCountryButton != null : "fx:id=\"editCountryButton\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert newCountryButton != null : "fx:id=\"newCountryButton\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressesLabel != null : "fx:id=\"addressesLabel\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addressTableView != null : "fx:id=\"addressTableView\" was not injected: check your FXML file 'EditCity.fxml'.";
        assert addAddressButton != null : "fx:id=\"addAddressButton\" was not injected: check your FXML file 'EditCity.fxml'.";

    }
    
    @Override
    protected void updateDao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String getSaveConflictMessage(Connection connection) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String getDeleteDependencyMessage(Connection connection) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
//        AddressFactory factory = new AddressFactory();
//        if (factory.count(connection, AddressFactory.getFilterWhereCityIdIs(getModel().getDataObject().getPrimaryKey())) == 0)
//            return "";
//        return getResourceString(RESOURCEKEY_CITYHASADDRESSES);
    }

}
