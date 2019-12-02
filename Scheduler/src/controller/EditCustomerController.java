/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.db.Customer;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditCustomerController extends ItemControllerBase<Customer> {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditCustomer.fxml";

    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    @FXML
    private Label activeLabel;
    
    @FXML
    private RadioButton activeYesRadioButton;
    
    @FXML
    private RadioButton activeNoRadioButton;
    
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
    protected void applyModelAsNew(Customer model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void applyModelAsEdit(Customer model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
