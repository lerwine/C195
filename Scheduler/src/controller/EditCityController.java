package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.CityRow;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditCityController extends ItemControllerBase<CityRow> {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditCity.fxml";

    private int countryId;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    @FXML
    private Label countryLabel;
    
    @FXML
    private ComboBox countryComboBox;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }    

    @Override
    protected void applyModelAsNew(CityRow model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void applyModelAsEdit(CityRow model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
