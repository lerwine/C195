/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Extends {@link Map} interface with mutation methods overridden to throw {@link UnsupportedOperationException}.
 *
 * @author lerwi
 * @param <K> The type of keys maintained by this map.
 * @param <V> The type of mapped values.
 */
public interface ReadOnlyMap<K, V> extends Map<K, V> {

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param key The candidate key value.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default V remove(Object key) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param key The candidate key.
     * @param value The candidate value.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default V put(K key, V value) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param m The candidate mapping.
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default void clear() {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param function The candidate function.
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param key The candidate key.
     * @param value The candidate value.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param key The candidate key.
     * @param value The candidate value.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param key The candidate key.
     * @param oldValue The candidate old value.
     * @param newValue The candidate new value.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param key The candidate key.
     * @param value The candidate value.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default V replace(K key, V value) {
        throw new UnsupportedOperationException("Map is read-only.");
    }

    /**
     * Overridden to prevent changes to the current map.
     * 
     * @param key The candidate key.
     * @param value The candidate value.
     * @param remappingFunction The candidate function.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Map is read-only.
     */
    @Override
    public default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Map is read-only.");
    }
    
}
