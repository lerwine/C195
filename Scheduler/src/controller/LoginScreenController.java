/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
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
import javax.persistence.EntityManager;
import utils.SchedulerContext;
import utils.InvalidOperationException;

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
        try {
            // Open a new SQL connection dependency
            SchedulerContext.EmDependency dependency = new SchedulerContext.EmDependency();
            EntityManager em = dependency.open(SchedulerContext.DEFAULT_CONTEXT);
            try {
                // Attempt to set current user according to login and password.
                if (SchedulerContext.DEFAULT_CONTEXT.trySetCurrentUser(em, userNameTextField.getText(),
                        passwordTextField.getText()))
                    // If true, change to the home screen.
                    HomeScreenController.changeScene((Node)event.getSource(), HomeScreenController.VIEW_PATH);
            } finally { dependency.close(); }
        } catch (InvalidOperationException ex) {
            utils.NotificationHelper.showNotificationDialog("Login", "Login Error", "Database access error",
                    Alert.AlertType.ERROR);
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RuntimeException ex) {
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
}
