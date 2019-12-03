/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows property change events to be deferred.
 * Also ensures that all property change events are fired, even if any of them throw an exception.
 * @author Leonard T. Erwine
 * @param <T> Type of target object.
 */
public class DeferrablePropertyChangeSupport<T> extends PropertyChangeSupport {
    private final T target;
    private final LinkedHashMap<String, DeferredPropertyChangeEvent<T>> deferredEvents;
    
    static class DeferredPropertyChangeEvent<T> {
        private final Object oldValue;
        private final Function<T, Object> getValue;
        DeferredPropertyChangeEvent(Object oldValue, Function<T, Object> getValue) {
            this.oldValue = oldValue;
            this.getValue = getValue;
        }
    }
    
    static class FieldValueSupplier<T> implements Function<T, Object> {
        private final Field field;
        
        FieldValueSupplier(Class<T> type, String name) throws NoSuchFieldException { field = type.getField(name); }
        
        @Override
        public Object apply(T t) {
            Object result;
            try {
                boolean wasAccessible = field.isAccessible();
                if (!wasAccessible)
                    field.setAccessible(true);
                try { result = field.get(t); }
                finally {
                    if (!wasAccessible)
                        field.setAccessible(false);
                }
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DeferrablePropertyChangeSupport.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error getting field value", ex);
            }
            return result;
        }
    }
    
    public DeferrablePropertyChangeSupport(T target) {
        super(target);
        deferredEvents = new LinkedHashMap<>();
        this.target = target;
    }

    public void deferPropertyChangeEvent(String propertyName, String fieldName) throws NoSuchFieldException {
        deferPropertyChangeEvent(propertyName, new FieldValueSupplier<>((Class<T>)target.getClass(), fieldName));
    }

    public void deferPropertyChangeEvent(String propertyName) throws NoSuchFieldException {
        deferPropertyChangeEvent(propertyName, new FieldValueSupplier<>((Class<T>)target.getClass(), propertyName));
    }

    public void deferPropertyChangeEvent(String propertyName, Function<T, Object> getValue) {
        synchronized(deferredEvents) {
            deferredEvents.put(propertyName, new DeferredPropertyChangeEvent<>(getValue.apply(target), getValue));
        }
    }
    
    public void fireDeferredPropertyChanges() {
        HashSet<Map.Entry<String,DeferredPropertyChangeEvent<T>>> entries = new HashSet<>();
        synchronized(deferredEvents) {
            deferredEvents.entrySet().forEach((Map.Entry<String,DeferredPropertyChangeEvent<T>> e) -> {
                entries.add(e);
            });
            deferredEvents.clear();
        }
        fireNextDeferredPropertyChange(entries.iterator());
    }
    
    private void fireNextDeferredPropertyChange(Iterator<Map.Entry<String,DeferredPropertyChangeEvent<T>>> iterator) {
        if (iterator.hasNext())
            try {
                Map.Entry<String,DeferredPropertyChangeEvent<T>> e = iterator.next();
                DeferredPropertyChangeEvent<T> d = e.getValue();
                Object newValue = d.getValue.apply(target);
                firePropertyChange(e.getKey(), d.oldValue, newValue);
            } finally { fireNextDeferredPropertyChange(iterator); }
    }
}
