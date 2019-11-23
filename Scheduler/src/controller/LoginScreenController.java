/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import entity.User;

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
    }
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        /*Logger log = Logger.getLogger(LoginScreenController.class.getName());
        log.info("Login button click");
        try {
        ArrayList<User> users = User.GetAll();
        String s = "";
        for (int i = 0; i < users.size(); i++) {
        if (s.length() > 0)
        s += "\n";
        User u = users.get(i);
        s += u.getUserName() + "," + u.getPassword() + "," + u.getCreateDate().toString() + "," + u.getCreatedBy() + "," + u.getLastUpdate().toString() + "," + u.getLastUpdateBy();
        }
        utils.NotificationHelper.showNotificationDialog("Debug", "All users", s, Alert.AlertType.ERROR);
        } catch (Exception ex) {
        log.log(Level.SEVERE, null, ex);
        }
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
        }*/
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
}
