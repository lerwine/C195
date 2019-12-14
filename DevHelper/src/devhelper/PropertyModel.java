/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine
 */
public class PropertyModel {
    //<editor-fold defaultstate="collapsed" desc="key">
    
    private final StringProperty key;
    
    public String getKey() { return key.get(); }
    
    public void setKey(String value) { key.set((value == null) ? "" : value); }
    
    public StringProperty keyProperty() {
        return key;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="value">
    
    private final StringProperty value;
    
    public String getValue() {
        return value.get();
    }
    
    public void setValue(String value) { this.value.set((value == null) ? "" : value); }
    
    public StringProperty valueProperty() { return value; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="isValid">
    
    private final ReadOnlyBooleanWrapper valid;
    
    public boolean isValid() { return valid.get(); }
    
    public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="validationMessage">
    
    private final ReadOnlyStringWrapper validationMessage;
    
    public String getValidationMessage() { return validationMessage.get(); }
    
    public ReadOnlyStringProperty validationMessageProperty() { return validationMessage.getReadOnlyProperty(); }
    
    @Override
    public int hashCode() {
        String current = key.get();
        return Objects.hashCode((current == null) ? "" : current.trim().toLowerCase());
    }

    //</editor-fold>
    @Override    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        String other = ((PropertyModel)obj).key.get();
        String current;
        if ((current = key.get()) == null || (current = current.trim()).isEmpty())
            return other == null || other.trim().isEmpty();
        return !(other = other.trim()).isEmpty() && current.toLowerCase().equals(other.toLowerCase());
    }

    public PropertyModel(String key, String value) {
        this.key = new SimpleStringProperty((key == null) ? "" : key);
        this.value = new SimpleStringProperty((value == null) ? "" : value);
        this.valid = new ReadOnlyBooleanWrapper(!this.key.get().trim().isEmpty());
        this.validationMessage = new ReadOnlyStringWrapper((this.valid.get()) ? "" : "Key cannot be empty.");
        this.key.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue == null)
                this.key.set("");
            else if (newValue.trim().isEmpty()) {
                this.validationMessage.set("Key cannot be empty.");
                this.valid.set(false);
            } else {
                this.validationMessage.set("");
                this.valid.set(true);
            }
        });
    }
}
