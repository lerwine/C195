package scheduler.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Extends {@link Map} interface with mutation methods overridden to throw {@link UnsupportedOperationException}.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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
    
    public static class Wrapper<K, V> extends HashMap<K, V> {
        /**
         *
         */
        private static final long serialVersionUID = -6245260755716089941L;
        private final ReadOnlyMap<K, V> readOnlyMap = new ReadOnlyMap<K, V>() {
            @Override
            public int size() {
                return Wrapper.this.size();
            }

            @Override
            public boolean isEmpty() {
                return Wrapper.this.isEmpty();
            }

            @Override
            public boolean containsKey(Object key) {
                return Wrapper.this.containsKey(key);
            }

            @Override
            public boolean containsValue(Object value) {
                return Wrapper.this.containsValue(value);
            }

            @Override
            public V get(Object key) {
                return Wrapper.this.get(key);
            }

            @Override
            public Set<K> keySet() {
                return Wrapper.this.keySet();
            }

            @Override
            public Collection<V> values() {
                return Wrapper.this.values();
            }

            @Override
            public Set<Entry<K, V>> entrySet() {
                return Wrapper.this.entrySet();
            }
            
        };
        
        public Wrapper() {
            super();
        }
        
        public Wrapper(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }
        
        public Wrapper(int initialCapacity) {
            super(initialCapacity);
        }
        
        public Wrapper(Map<? extends K, ? extends V> m) {
            super(m);
        }

        public ReadOnlyMap<K, V> getReadOnlyMap() {
            return readOnlyMap;
        }
        
    }
}
