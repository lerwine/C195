/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 *
 * @author Leonard T. Erwine
 */
@Deprecated
public class FieldValidation {
    private final ReadOnlyBooleanWrapper valid;

    public boolean isValid() { return valid.get(); }

    public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }
    
    private final ReadOnlyStringWrapper message;

    public String getMessage() { return message.get(); }

    public ReadOnlyStringProperty messageProperty() { return message.getReadOnlyProperty(); }
    
    FieldValidation() {
        valid = new ReadOnlyBooleanWrapper(true);
        message = new ReadOnlyStringWrapper("");
    }
    FieldValidation(String message) {
        valid = new ReadOnlyBooleanWrapper(message == null || message.trim().isEmpty());
        this.message = new ReadOnlyStringWrapper((valid.getValue()) ? "" : message);
    }
}
