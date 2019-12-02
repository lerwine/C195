/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.App;
import scheduler.InvalidOperationException;
import scheduler.Messages;

/**
 * FXML Controller class for the application login screen.
 * @author webmaster
 */
public class LoginScreenController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/LoginScreen.fxml";
    
    /**
     * The {@link ComboBox} that lets the user select their preferred language.
     */
    @FXML
    private ComboBox languageComboBox;
    
    /**
     * The {@link Label} for the User Name {@link TextField}.
     */
    @FXML
    private Label userNameLabel;

    /**
     * The {@link TextField} where the user provides the User Name.
     */
    @FXML
    private TextField userNameTextField;

    /**
     * The {@link Label} for the {@link PasswordField}.
     */
    @FXML
    private Label passwordLabel;

    /**
     * The {@link PasswordField} where the user provides the password.
     */
    @FXML
    private PasswordField passwordTextField;
    
    /**
     * The {@link Button} which begins the login attempt.
     */
    @FXML
    private Button loginButton;

    /**
     * The {@link Button} which cancels login and closes the application.
     */
    @FXML
    private Button exitButton;
    
    private ObservableList<Locale> locales;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        locales = FXCollections.observableArrayList(new Locale("en"), new Locale("es"), new Locale("de"), new Locale("hi"));
        int index = -1;
        
        String d = App.getCurrentLocale().toString();
        for (int i = 0; i < locales.size(); i++) {
            if (locales.get(i).toString().equals(d)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            d = App.getCurrentLocale().getISO3Language();
            for (int i = 0; i < locales.size(); i++) {
                if (locales.get(i).getISO3Language().equals(d)) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                locales.add(0, App.getCurrentLocale());
                index = 0;
            }
        }
        
        languageComboBox.setCellFactory((p) -> new ListCell<Locale>() {
            @Override
            protected void updateItem(Locale t, boolean bln) {
                super.updateItem(t, bln);
                setText((t == null) ? "" : t.getDisplayName(t));
            }
        });
        languageComboBox.setItems(locales);
        languageComboBox.getSelectionModel().select(index);
    }
    
    private Stage currentStage;
    
    public void setCurrentStage(Stage stage) {
        currentStage = stage;
        refreshCultureSensitive();
    }
    
    void refreshCultureSensitive() {
        if (currentStage != null)
            currentStage.setTitle(Messages.current().getAppointmentSchedulerLogin());
        userNameLabel.setText(Messages.current().getUserName() + ":");
        passwordLabel.setText(Messages.current().getPassword() + ":");
        loginButton.setText(Messages.current().getLogin());
        exitButton.setText(Messages.current().getExit());
    }
    
    @FXML
    void languageChanged(ActionEvent event) {
        Object item = languageComboBox.getSelectionModel().getSelectedItem();
        if (item == null || !(item instanceof Locale))
            return;
        App.setCurrentLocale((Locale)item);
        refreshCultureSensitive();
    }
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        try {
            if (App.trySetCurrentUser(userNameTextField.getText(), passwordTextField.getText()))
                App.changeScene((Node)event.getSource(), HomeScreenController.VIEW_PATH, (Stage stage, HomeScreenController controller) -> {
                    stage.setTitle(Messages.current().getAppointmentScheduler());
                });
            else
                Messages.current().notifyInvalidCredentials();
        } catch (InvalidOperationException | ClassNotFoundException ex) {
                Messages.current().notifyCredentialValidationError();
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        } catch (SQLException ex) {
            Messages.current().notifyDbCredentialAccessError();
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        }
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }

}
