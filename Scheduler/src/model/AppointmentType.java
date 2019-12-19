/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * Represents an appointment type selection item.
 * @author Leonard T. Erwine
 */
public class AppointmentType {
    /**
     * Code for Phone Conference appointments, where the phone number is encoded into the URL field.
     */
    public static final String APPOINTMENT_CODE_PHONE = "phone";
    /**
     * Code for Virtual Meetings specified in the URL field.
     */
    public static final String APPOINTMENT_CODE_VIRTUAL = "virtual";
    /**
     * Code for appointments where the implicit location is at the Customer Site.
     */
    public static final String APPOINTMENT_CODE_CUSTOMER = "customer";
    /**
     * Code for appointments where the implicit location is at the Home Office.
     */
    public static final String APPOINTMENT_CODE_HOME = "home";
    /**
     * CCode for appointments where the implicit location is at the Germany Office.
     */
    public static final String APPOINTMENT_CODE_GERMANY = "germany";
    /**
     * Code for appointments where the implicit location is at the India Office.
     */
    public static final String APPOINTMENT_CODE_INDIA = "india";
    /**
     * Code for appointments where the implicit location is at the Honduras Office.
     */
    public static final String APPOINTMENT_CODE_HONDURAS = "honduras";
    /**
     * Code for appointments at other explicit physical locations.
     */
    public static final String APPOINTMENT_CODE_OTHER = "other";
    
    private final ReadOnlyStringWrapper code;

    /**
     * Gets the database code for the current appointment type.
     * @return The database code for the current appointment type.
     */
    public String getCode() { return code.get(); }

    public ReadOnlyStringProperty codeProperty() { return code.getReadOnlyProperty(); }
    
    private final ReadOnlyStringWrapper displayText;

    /**
     * Gets the human-readable short description of the appointment type.
     * @return The human-readable short description of the appointment type. 
     */
    public String getDisplayText() { return displayText.get(); }

    public ReadOnlyStringProperty displayTextProperty() { return displayText.getReadOnlyProperty(); }
    
    private final ReadOnlyBooleanWrapper urlRequired;

    /**
     * Indicates whether the current appointment type requires a URL.
     * @return True if the current appointment type requires a URL; otherwise, false.
     */
    public boolean isUrlRequired() { return urlRequired.get(); }

    public ReadOnlyBooleanProperty urlRequiredProperty() { return urlRequired.getReadOnlyProperty(); }
    
    private final ReadOnlyBooleanWrapper locationRequired;

    /**
     * Indicates whether the current appointment type requires a value in the Location field.
     * @return True if the current appointment type requires a value in the Location field; otherwise, false.
     */
    public boolean isLocationRequired() { return locationRequired.get(); }

    public ReadOnlyBooleanProperty locationRequiredProperty() { return locationRequired.getReadOnlyProperty(); }
    
    private final ReadOnlyBooleanWrapper phoneUrl;

    /**
     * Indicates whether the current appointment type encodes a phone number in the URL field.
     * @return True if the current appointment type encodes a phone number in the URL field; otherwise, false.
     */
    public boolean isPhoneUrl() { return phoneUrl.get(); }

    public ReadOnlyBooleanProperty phoneUrlProperty() { return phoneUrl.getReadOnlyProperty(); }
    
    /**
     * Creates a new Appointment Type object.
     * @param code The appointment type code that gets stored in the database.
     * @param displayText The human-readable short description of the appointment type.
     */
    public AppointmentType(String code, String displayText) {
        this.displayText = new ReadOnlyStringWrapper(displayText);
        this.code = new ReadOnlyStringWrapper(code);
        switch (code) {
            case APPOINTMENT_CODE_PHONE:
                locationRequired = new ReadOnlyBooleanWrapper(false);
                urlRequired = new ReadOnlyBooleanWrapper(true);
                phoneUrl = new ReadOnlyBooleanWrapper(true);
                break;
            case APPOINTMENT_CODE_VIRTUAL:
                locationRequired = new ReadOnlyBooleanWrapper(false);
                urlRequired = new ReadOnlyBooleanWrapper(true);
                phoneUrl = new ReadOnlyBooleanWrapper(false);
                break;
            case APPOINTMENT_CODE_CUSTOMER:
            case APPOINTMENT_CODE_HOME:
            case APPOINTMENT_CODE_GERMANY:
            case APPOINTMENT_CODE_INDIA:
            case APPOINTMENT_CODE_HONDURAS:
                locationRequired = new ReadOnlyBooleanWrapper(false);
                urlRequired = new ReadOnlyBooleanWrapper(false);
                phoneUrl = new ReadOnlyBooleanWrapper(false);
                break;
            default:
                locationRequired = new ReadOnlyBooleanWrapper(true);
                urlRequired = new ReadOnlyBooleanWrapper(false);
                phoneUrl = new ReadOnlyBooleanWrapper(false);
                break;
        }
    }
}
