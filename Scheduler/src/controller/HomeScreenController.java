/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * FXML Controller class
 *
 * @author webmaster
 */
public class HomeScreenController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/HomeScreen.fxml";

    @FXML
    private MenuItem newAppointmentMenuItem;

    @FXML
    private MenuItem newCityMenuItem;

    @FXML
    private MenuItem newAddressMenuItem;

    @FXML
    private Menu usersMenu;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    void newAppointmentMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), UserAppointmentsController.VIEW_PATH, (UserAppointmentsController controller) -> {
            controller.applyModel(scheduler.App.getCurrentUser().get());
        });
    }
    
    @FXML
    void myAppointmentsMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), UserAppointmentsController.VIEW_PATH, (UserAppointmentsController controller) -> {
            controller.applyModel(scheduler.App.getCurrentUser().get());
        });
    }
    
    @FXML
    void newCustomerMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCustomersController.VIEW_PATH);
    }
    
    @FXML
    void allCustomersMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCustomersController.VIEW_PATH);
    }
    
    @FXML
    void newCountryMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void newCityMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void newAddressMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void allCountriesMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void newUserMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageUsersController.VIEW_PATH);
    }
    
    @FXML
    void allUsersMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageUsersController.VIEW_PATH);
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
}
