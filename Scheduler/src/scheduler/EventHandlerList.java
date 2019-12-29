/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 *
 * @author Leonard T. Erwine
 * @param <E>
 */
public class EventHandlerList<E extends Event> extends javafx.collections.ModifiableObservableListBase<EventHandler<E>> {
    private final Object syncRoot = new Object();
    
    private final ArrayList<EventHandler<E>> delegate = new ArrayList<>();
    
    @Override
    public EventHandler<E> get(int index) { return delegate.get(index); }

    @Override
    public int size() { return delegate.size(); }

    @Override
    protected void doAdd(int index, EventHandler<E> element) {
        if (element == null)
            throw new NullPointerException("Element cannot be null");
        synchronized(syncRoot) {
            if (delegate.contains(element))
                throw new IllegalArgumentException();
            delegate.add(index, element);
        }
    }

    @Override
    protected EventHandler<E> doSet(int index, EventHandler<E> element) {
        if (element == null)
            throw new NullPointerException("Element cannot be null");
        synchronized(syncRoot) {
            if (delegate.contains(element))
                throw new IllegalArgumentException();
            return delegate.set(index, element);
        }
    }

    @Override
    protected EventHandler<E> doRemove(int index) {
        synchronized(syncRoot) {
            return delegate.remove(index);
        }
    }
    
    public void invokeAll(Supplier<E> getEvent) {
        if (getEvent == null)
            throw new NullPointerException("Event supplier cannot be null");
        // Save current handlers in case others are added or removed while the current handlers are invoked.
        final HashSet<EventHandler<E>> handlers = new HashSet<>();
        synchronized(syncRoot) {
            delegate.stream().forEach((EventHandler<E> item) -> {
                try { handlers.add(item); }
                catch (Exception ex) {
                   Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error adding event handler for invocation", ex);
                }
            });
        }
        Iterator<EventHandler<E>> iterator = handlers.iterator();
        if (iterator.hasNext()) {
            E event = getEvent.get();
            if (event == null)
                throw new NullPointerException("Event supplier must not return a null object");
            invokeNextEventHandlers(iterator, event);
        }
    }
    
    private static <E extends Event> void invokeNextEventHandlers(Iterator<EventHandler<E>> iterator, E event) {
        try {
            EventHandler<E> handler = iterator.next();
            if (handler != null)
                handler.handle(event); }
        finally {
            if (iterator.hasNext())
                invokeNextEventHandlers(iterator, event);
        }
    }
}
