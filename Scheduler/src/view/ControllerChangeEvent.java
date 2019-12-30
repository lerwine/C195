/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author Leonard T. Erwine
 */
public class ControllerChangeEvent extends Event {

    private final ReadOnlyObjectWrapper<Controller> oldController;
    public Controller getOldController() { return oldController.get(); }
    public ReadOnlyObjectProperty<Controller> oldControllerProperty() { return oldController.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<Controller> newController;
    public Controller getNewController() { return newController.get(); }
    public ReadOnlyObjectProperty<Controller> newControllerProperty() { return newController.getReadOnlyProperty(); }
    
    public ControllerChangeEvent(Object source, Controller oldController, Controller newController, String name) {
        this(source, oldController, newController, null, name);
    }
    
    public ControllerChangeEvent(Object source, Controller oldController, Controller newController, EventTarget target, String name) {
        super(source, target, new EventType<ControllerChangeEvent>(name));
        this.oldController = new ReadOnlyObjectWrapper<>(oldController);
        this.newController = new ReadOnlyObjectWrapper<>(newController);
    }
    
}
