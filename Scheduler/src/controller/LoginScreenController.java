/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private ComboBox languageComboBox;
    
    @FXML
    private Label userNameLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private Label passwordLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField passwordTextField;

    private ObservableList<String> languages;
    
    private HashMap<String, Locale> locales;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        locales = new HashMap<>();
        languages = FXCollections.observableArrayList();
        Stream.of(new Locale("en"), new Locale("es"), new Locale("de")).forEach((l) -> {
            String key = l.getDisplayName(l);
            locales.put(key, l);
            languages.add(key);
        });
        languageComboBox.setItems(languages);
        String n = languages.get(0);
        languageComboBox.setValue(n);
        setFromResource(ResourceBundle.getBundle("Messages", locales.get(n)));
    }
    
    void setFromResource(ResourceBundle rb) {
        userNameLabel.setText(rb.getString("userName") + ":");
        passwordLabel.setText(rb.getString("password") + ":");
        loginButton.setText(rb.getString("login"));
        exitButton.setText(rb.getString("exit"));
    }
    
    @FXML
    void languageChanged(ActionEvent event) {
        setFromResource(ResourceBundle.getBundle("Messages", locales.get((String)languageComboBox.getValue())));
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
