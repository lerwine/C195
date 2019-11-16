/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;

/**
 * FXML Controller class
 *
 * @author webmaster
 */
public class LoginScreenController implements Initializable {

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField passwordTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    void loginButtonClick(ActionEvent event) {
        String userName = this.userNameTextField.getText();
        if (userName.trim().length() == 0)
            utils.NotificationHelper.showNotificationDialog("Error", "Invalid User Name", "User name cannot be empty", Alert.AlertType.WARNING);
        else {
            String password = this.passwordTextField.getText();
            if (password.length() == 0)
                utils.NotificationHelper.showNotificationDialog("Error", "Invalid Password", "Password cannot be empty", Alert.AlertType.WARNING);
            else {
                try {
                    if (User.TryLoadCurrentUser(userName, password))
                        HomeScreenController.changeScene((Node)event.getSource(), HomeScreenController.VIEW_PATH);
                } catch (Exception e) {
                    utils.NotificationHelper.showNotificationDialog("Login Error", "Error validating user login", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }
    
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
}
