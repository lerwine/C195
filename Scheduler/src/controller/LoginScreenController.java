/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javax.persistence.EntityManager;
import utils.InvalidOperationException;
import utils.NotificationHelper;

/**
 * FXML Controller class
 *
 * @author webmaster
 */
public class LoginScreenController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/LoginScreen.fxml";
    
    @FXML
    private ComboBox languageComboBox;
    
    @FXML
    private Label userNameLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordTextField;
    
    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;
    
    private ObservableList<Locale> locales;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        locales = FXCollections.observableArrayList(new Locale("en"), new Locale("es"), new Locale("de"), new Locale("hi"));
        
        languageComboBox.setCellFactory((p) -> new ListCell<Locale>() {
            @Override
            protected void updateItem(Locale t, boolean bln) {
                super.updateItem(t, bln);
                setText((t == null) ? "" : t.getDisplayName(t));
            }
        });
        languageComboBox.setItems(locales);
        int index = -1;
        String d = Locale.getDefault().toString();
        for (int i = 0; i < locales.size(); i++) {
            if (locales.get(i).toString().equals(d)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            d = Locale.getDefault().getISO3Language();
            for (int i = 0; i < locales.size(); i++) {
                if (locales.get(i).getISO3Language().equals(d)) {
                    index = i;
                    break;
                }
            }
            if (index < 0)
                index = 0;
        }
        languageComboBox.getSelectionModel().select(index);
        scheduler.Context.setCurrentLocale(locales.get(index));
        refreshCultureSensitive();
    }
    
    void refreshCultureSensitive() {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        scheduler.Context.setWindowTitle(rb.getString("appointmentSchedulerLogin"));
        userNameLabel.setText(rb.getString("userName") + ":");
        passwordLabel.setText(rb.getString("password") + ":");
        loginButton.setText(rb.getString("login"));
        exitButton.setText(rb.getString("exit"));
    }
    
    @FXML
    void languageChanged(ActionEvent event) {
        Object item = languageComboBox.getSelectionModel().getSelectedItem();
        if (item == null || !(item instanceof Locale))
            return;
        scheduler.Context.setCurrentLocale((Locale)item);
        refreshCultureSensitive();
    }
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        try {
            if (scheduler.Context.trySetCurrentUser(userNameTextField.getText(), passwordTextField.getText()))
                scheduler.Context.changeScene((Node)event.getSource(), HomeScreenController.VIEW_PATH);
            else
                NotificationHelper.showNotificationDialog(scheduler.Context.getMessage("authentication"),
                        scheduler.Context.getMessage("authError"), scheduler.Context.getMessage("invalidCredentials"),
                        Alert.AlertType.WARNING);
        } catch (InvalidOperationException | ClassNotFoundException ex) {
            NotificationHelper.showNotificationDialog(scheduler.Context.getMessage("authentication"),
                    scheduler.Context.getMessage("authError"), "", Alert.AlertType.ERROR);
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        } catch (SQLException ex) {
            NotificationHelper.showNotificationDialog(scheduler.Context.getMessage("authentication"),
                    scheduler.Context.getMessage("authError"), scheduler.Context.getMessage("dbCredentialAccessError"),
                    Alert.AlertType.ERROR);
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        }
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
}
