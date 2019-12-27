/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import property.NonNullableStringProperty;

/**
 *
 * @author Leonard T. Erwine
 */
public class ValidatingTextField extends AnchorPane {

    private final TextField textField;
    private final Label validationMessageLabel;
    
    private final ReadOnlyBooleanWrapper valid;
    public boolean isValid() { return valid.get(); }
    public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }
    
    private final BooleanBinding required;
    public boolean isRequired() { return required.get(); }
    public BooleanBinding requiredProperty() { return required; }
    
    private final NonNullableStringProperty text;
    public String getText() { return text.get(); }
    public void setText(String value) { text.set(value); }
    public NonNullableStringProperty textProperty() { return text; }
    
    private final NonNullableStringProperty emptyValidationMessage;
    public String getEmptyValidationMessage() { return emptyValidationMessage.get(); }
    public void setEmptyValidationMessage(String value) { emptyValidationMessage.set(value); }
    public NonNullableStringProperty emptyValidationMessageProperty() { return emptyValidationMessage; }
    
    private final ReadOnlyStringWrapper customValidationMessage;
    public String getCustomValidationMessage() { return customValidationMessage.get(); }
    public ReadOnlyStringProperty customValidationMessageProperty() { return customValidationMessage.getReadOnlyProperty(); }
    
    private final ObjectProperty<EventHandler<ActionEvent>> onAction;
    public EventHandler getOnAction() { return onAction.get(); }
    public void setOnAction(EventHandler<ActionEvent> value) { onAction.set(value); }
    public ObjectProperty onActionProperty() { return onAction; }

    private final ObjectProperty<EventHandler<ValidationEvent>> onValidate;
    public EventHandler getOnValidate() { return onValidate.get(); }
    public void setOnValidate(EventHandler<ValidationEvent> value) { onValidate.set(value); }
    public ObjectProperty onValidateProperty() { return onValidate; }

    public ValidatingTextField() {
        super();
        text = new NonNullableStringProperty();
        valid = new ReadOnlyBooleanWrapper(true);
        emptyValidationMessage = new NonNullableStringProperty();
        customValidationMessage = new ReadOnlyStringWrapper();
        required = emptyValidationMessage.isNotWhiteSpace();
        onValidate = new SimpleObjectProperty<>();
        onAction = new SimpleObjectProperty<>();
        VBox vbox = new VBox();
        ObservableList<Node> nodes = vbox.getChildren();
        textField = new TextField();
        nodes.add(textField);
        validationMessageLabel = new Label();
        setMinHeight(textField.getHeight());
        nodes.add(validationMessageLabel);
        text.bindBidirectional(textField.textProperty());
        textField.setOnAction((ActionEvent ae) -> {
            EventHandler<ActionEvent> ah = onAction.get();
            if (ah != null)
                ah.handle(new ActionEvent(this, ae.getTarget()));
        });
        
        text.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            ValidationEvent ve = new ValidationEvent(oldValue, newValue, this, null, new EventType<>("onValidate"));
            EventHandler<ValidationEvent> vh = onValidate.get();
            if (vh != null)
                vh.handle(ve);
            customValidationMessage.set(ve.message);
            valid.set(ve.valid);
        });
        customValidationMessage.addListener((observable) -> { onValidationChange(); });
        emptyValidationMessage.addListener((observable) -> { onValidationChange(); });
        valid.addListener((observable) -> { onValidationChange(); });
    }

    private void onValidationChange() {
        String msg = customValidationMessage.get();
        boolean show = !valid.get();
        if (show && msg.trim().isEmpty() && (msg = emptyValidationMessage.get()).trim().isEmpty())
            show = false;
        if (show) {
            validationMessageLabel.setText(msg);
            validationMessageLabel.setMaxHeight(USE_PREF_SIZE);
            validationMessageLabel.setMaxWidth(USE_PREF_SIZE);
            validationMessageLabel.setPrefHeight(USE_PREF_SIZE);
            validationMessageLabel.setPrefWidth(USE_PREF_SIZE);
            validationMessageLabel.setMinHeight(USE_PREF_SIZE);
            validationMessageLabel.setMinWidth(USE_PREF_SIZE);
            validationMessageLabel.setScaleX(1);
            validationMessageLabel.setScaleY(1);
            validationMessageLabel.setVisible(true);
        } else {
            validationMessageLabel.setVisible(false);
            validationMessageLabel.setMinHeight(0);
            validationMessageLabel.setMinWidth(0);
            validationMessageLabel.setPrefHeight(0);
            validationMessageLabel.setPrefWidth(0);
            validationMessageLabel.setMaxHeight(0);
            validationMessageLabel.setMaxWidth(0);
            validationMessageLabel.setScaleX(0);
            validationMessageLabel.setScaleY(0);
        }
    }

    public class ValidationEvent extends Event {

        private boolean valid;
        public boolean isValid() { return valid; }
        public void setValid(boolean value) { valid = value; }

        private final String oldValue;
        public String getOldValue() { return oldValue; }
        
        private final String newValue;
        public String getNewValue() { return newValue; }
        
        private String message;
        public String getMessage() { return message; }
        public void setMessage(String value) { message = (value == null) ? "" : value; }

        ValidationEvent(String oldValue, String newValue, EventType<? extends ValidationEvent> eventType) {
            super(eventType);
            this.oldValue = oldValue;
            this.newValue = newValue;
            valid = !(required.get() && text.isWhiteSpace().get());
            message = "";
        }

        ValidationEvent(String oldValue, String newValue, Object source, EventTarget target, EventType<? extends ValidationEvent> eventType) {
            super(source, target, eventType);
            this.oldValue = oldValue;
            this.newValue = newValue;
            valid = !(required.get() && text.isWhiteSpace().get());
            message = "";
        }
        
    }
}
