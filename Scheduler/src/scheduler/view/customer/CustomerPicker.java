/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.customer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scheduler.view.SchedulerController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.CityModel;
import scheduler.view.country.CountryModel;

/**
 * FXML Controller class
 *
 * @author lerwi
 */
@GlobalizationResource("scheduler/view/appointment/CustomerPicker")
@FXMLResource("/scheduler/view/appointment/CustomerPicker.fxml")
public class CustomerPicker extends SchedulerController {

    @FXML // fx:id="countryFilterCheckBox"
    private CheckBox countryFilterCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountryModel> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityFilterCheckBox"
    private CheckBox cityFilterCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<CityModel> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="statusComboBox"
    private ComboBox<CountryModel> statusComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="nameSearchTextField"
    private TextField nameSearchTextField; // Value injected by FXMLLoader

    @FXML // fx:id="newCustomerButton"
    private Button newCustomerButton; // Value injected by FXMLLoader

    @FXML // fx:id="customersTableView"
    private TableView<CustomerModel> customersTableView; // Value injected by FXMLLoader

    @FXML // fx:id="selectCustomerButton"
    private Button selectCustomerButton; // Value injected by FXMLLoader

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader

    @FXML
    void cancelButtonClick(ActionEvent event) {

    }

    @FXML
    void cityComboBoxChange(ActionEvent event) {

    }

    @FXML
    void cityFilterCheckBoxCheckChange(ActionEvent event) {

    }

    @FXML
    void countryComboBoxChange(ActionEvent event) {

    }

    @FXML
    void countryFilterCheckBoxCheckChange(ActionEvent event) {

    }

    @FXML
    void statusComboBoxChange(ActionEvent event) {

    }

    @FXML
    void nameSearchTextFieldChange(ActionEvent event) {

    }

    @FXML
    void newCustomerButtonClick(ActionEvent event) {

    }

    @FXML
    void selectCustomerButtonClick(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert countryFilterCheckBox != null : "fx:id=\"countryFilterCheckBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert cityFilterCheckBox != null : "fx:id=\"cityFilterCheckBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert nameSearchTextField != null : "fx:id=\"nameSearchTextField\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert newCustomerButton != null : "fx:id=\"newCustomerButton\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert customersTableView != null : "fx:id=\"customersTableView\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert selectCustomerButton != null : "fx:id=\"selectCustomerButton\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'CustomerPicker.fxml'.";

    }
    
    public static class StatusOption {

        private final ReadOnlyStringWrapper displayText = new ReadOnlyStringWrapper();

        public String getDisplayText() {
            return displayText.get();
        }

        public ReadOnlyStringProperty displayTextProperty() {
            return displayText.getReadOnlyProperty();
        }
        
    }
}
