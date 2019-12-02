/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.db.Appointment;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditAppointmentController extends ItemControllerBase<Appointment> {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditAppointment.fxml";

    @FXML
    private Label customerLabel;
    
    @FXML
    private ComboBox customerComboBox;
    
    @FXML
    private Label userLabel;
    
    @FXML
    private ComboBox userComboBox;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private TextField titleTextField;
    
    @FXML
    private Label titleErrorLabel;
    
    @FXML
    private Label descriptionLabel;
    
    @FXML
    private TextField descriptionTextField;
    
    @FXML
    private Label locationLabel;
    
    @FXML
    private TextField locationTextField;
    
    @FXML
    private Label locationErrorLabel;
    
    @FXML
    private Label contactLabel;
    
    @FXML
    private TextField contactTextField;
    
    @FXML
    private Label contactErrorLabel;
    
    @FXML
    private Label typeLabel;
    
    @FXML
    private TextField typeTextField;
    
    @FXML
    private Label urlLabel;
    
    @FXML
    private TextField urlTextField;
    
    @FXML
    private Label urlErrorLabel;
    
    @FXML
    private Label timeZoneLabel;
    
    @FXML
    private ComboBox timeZoneComboBox;
    
    @FXML
    private RadioButton hours12RadioButton;
    
    @FXML
    private RadioButton hours24RadioButton;
    
    @FXML
    private Label startLabel;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private ComboBox startHourComboBox;
    
    @FXML
    private ComboBox startMinuteComboBox;
    
    @FXML
    private RadioButton startAmRadioButton;
    
    @FXML
    private RadioButton startPmRadioButton;
    
    @FXML
    private Label endLabel;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox endHourComboBox;
    
    @FXML
    private ComboBox endMinuteComboBox;
    
    @FXML
    private RadioButton endAmRadioButton;
    
    @FXML
    private RadioButton endPmRadioButton;
    
    @FXML
    private Label endErrorLabel;
    
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
    protected void applyModelAsNew(Appointment model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void applyModelAsEdit(Appointment model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
