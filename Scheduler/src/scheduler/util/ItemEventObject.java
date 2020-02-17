/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.util.EventObject;

/**
 * Information about a object-related event.
 * @param <T> The target item type.
 */
public class ItemEventObject<T> extends EventObject {
    private final T item;

    /**
     * Gets the target object.
     * @return The target object.
     */
    public T getItem() { return item; }

    public ItemEventObject(Object source, T item) {
        super(source);
        this.item = item;
    }
}
