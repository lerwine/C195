/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author Leonard T. Erwine
 * @param <R>
 */
public class ModelEvent<R extends model.Record> extends Event {

    private final ReadOnlyObjectWrapper<R> model;
    public R getModel() { return model.get(); }
    public ReadOnlyObjectProperty<R> modelProperty() { return model.getReadOnlyProperty(); }
    
    public ModelEvent(Object source, R model, String name) { this(source, model, null, name); }
    
    public ModelEvent(Object source, R model, EventTarget target, String name) {
        super(source, target, new EventType<ModelEvent<R>>(name));
        this.model = new ReadOnlyObjectWrapper<>(model);
    }
}
