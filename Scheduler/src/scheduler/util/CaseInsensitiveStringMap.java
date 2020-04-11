package scheduler.util;

import com.sun.javafx.collections.MapListenerHelper;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * Maps case-insensitive string keys to values.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <V> The type of mapped values.
 */
public class CaseInsensitiveStringMap<V> implements ObservableMap<String, V> {

    private static final Logger LOG = Logger.getLogger(CaseInsensitiveStringMap.class.getName());

    private final LinkedList<Entry> backingList;
    private final boolean nullAllowed;
    private final EntrySet entrySet;
    private final ReadOnlyEntrySet readOnlyEntrySet;
    private final KeySet keySet;
    private final Values values;
    private final ReadOnlyMap<String, V> readOnlyMap;
    private final LinkedList<MapChangeListener.Change<String, V>> changeEvents;
    private MapListenerHelper<String, V> listenerHelper;

    /**
     * Indicates whether {@code null} values are allowed by this map.
     *
     * @return {@code true} if {@code null} values are allowed by this map; otherwise {@code false}.
     */
    public boolean isNullAllowed() {
        return nullAllowed;
    }

    /**
     * Gets a read-only wrapper of the current map.
     * 
     * @return A read-only wrapper of the current map.
     */
    public ReadOnlyMap<String, V> getReadOnlyMap() {
        return readOnlyMap;
    }

    private MapChangeListener.Change<String, V> addChangeEvent(String key, V old, V added, boolean wasAdded, boolean wasRemoved) {
        MapChangeListener.Change<String, V> result;
        synchronized (backingList) {
            result = new MapChangeListener.Change<String, V>(CaseInsensitiveStringMap.this) {
                @Override
                public boolean wasAdded() {
                    return wasAdded;
                }

                @Override
                public boolean wasRemoved() {
                    return wasRemoved;
                }

                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public V getValueAdded() {
                    return added;
                }

                @Override
                public V getValueRemoved() {
                    return old;
                }

            };
            changeEvents.add(result);
        }
        return result;
    }

    private void fireChangeEvents(MapListenerHelper<String, V> listeners, Iterator<MapChangeListener.Change<String, V>> iterator) {
        if (iterator.hasNext()) {
            try {
                MapListenerHelper.fireValueChangedEvent(listeners, iterator.next());
            } finally {
                fireChangeEvents(listeners, iterator);
            }
        }
    }

    private void fireChangeEvents() {
        MapListenerHelper<String, V> listeners;
        Iterator<MapChangeListener.Change<String, V>> iterator;
        synchronized (backingList) {
            if (changeEvents.isEmpty()) {
                return;
            }
            listeners = listenerHelper;
            if (null == listeners) {
                changeEvents.clear();
                return;
            }
            Stream.Builder<MapChangeListener.Change<String, V>> builder = Stream.builder();
            changeEvents.forEach((t) -> builder.accept(t));
            iterator = builder.build().iterator();
            changeEvents.clear();
        }
        fireChangeEvents(listeners, iterator);
    }

    /**
     * Creates a new case-insensitive String-to-value mapping with the same mappings as the source {@link Map}.
     * 
     * @param m The map whose mappings are to be placed in this map.
     * @param nullAllowed {@code true} if {@code null} values are allowed.
     */
    public CaseInsensitiveStringMap(Map<String, ? extends V> m, boolean nullAllowed) {
        this(nullAllowed);
        if (nullAllowed) {
            m.keySet().forEach((t) -> {
                if (null == t) {
                    throw new NullPointerException();
                }
                for (Entry e : backingList) {
                    if (e.key.equalsIgnoreCase(t)) {
                        e.value = m.get(t);
                        return;
                    }
                }
                backingList.add(new Entry(t, m.get(t)));
            });
        } else {
            m.keySet().forEach((t) -> {
                if (null == t) {
                    throw new NullPointerException();
                }
                V v = Objects.requireNonNull(m.get(t));
                for (Entry e : backingList) {
                    if (e.key.equalsIgnoreCase(t)) {
                        e.value = v;
                        return;
                    }
                }
                backingList.add(new Entry(t, v));
            });
        }
    }

    /**
     * Creates a new case-insensitive String-to-value mapping, which does not allow null values, with the same mappings as the source {@link Map}.
     * 
     * @param m The map whose mappings are to be placed in this map.
     */
    public CaseInsensitiveStringMap(Map<String, ? extends V> m) {
        this(m, false);
    }

    /**
     * Creates a new case-insensitive String-to-value mapping.
     * 
     * @param nullAllowed {@code true} if {@code null} values are allowed.
     */
    public CaseInsensitiveStringMap(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
        backingList = new LinkedList<>();
        entrySet = new EntrySet();
        readOnlyEntrySet = new ReadOnlyEntrySet();
        keySet = new KeySet();
        values = new Values();
        readOnlyMap = new ReadOnly();
        changeEvents = new LinkedList<>();
    }

    /**
     * Creates a new case-insensitive String-to-value mapping, which does not allow null values.
     */
    public CaseInsensitiveStringMap() {
        this(false);
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (null != key && key instanceof String) {
            synchronized (backingList) {
                String k = (String) key;
                return backingList.stream().anyMatch((x) -> x.key.equalsIgnoreCase(k));
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (null != value) {
            synchronized (backingList) {
                return backingList.stream().anyMatch((x) -> (value.equals(x.getValue())));
            }
        } else if (nullAllowed) {
            synchronized (backingList) {
                return nullAllowed && backingList.stream().anyMatch((x) -> (null == x.getValue()));
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (Objects.requireNonNull(key) instanceof String) {
            String k = (String) key;
            synchronized (backingList) {
                for (Entry e : backingList) {
                    if (e.key.equalsIgnoreCase(k)) {
                        return e.value;
                    }
                }
            }
            return null;
        }
        throw new ClassCastException();
    }

    @Override
    public V put(String key, V value) {
        if (Objects.requireNonNull(key) instanceof String) {
            if (null == value && !nullAllowed) {
                throw new NullPointerException();
            }
            Entry changed = null;
            V oldValue = null;
            String oldKey = "";
            synchronized (backingList) {
                String k = (String) key;
                for (Entry e : backingList) {
                    if (e.key.equalsIgnoreCase(k)) {
                        oldKey = e.key;
                        if (!k.equals(oldKey)) {
                            try {
                                e.vetoableChangeSupport.fireVetoableChange(Entry.PROP_KEY, oldKey, k);
                            } catch (PropertyVetoException ex) {
                                LOG.log(Level.FINE, "Key change cancelled", ex);
                                return e.value;
                            }
                        }
                        try {
                            e.vetoableChangeSupport.fireVetoableChange(Entry.PROP_VALUE, e.value, value);
                        } catch (PropertyVetoException ex) {
                            LOG.log(Level.FINE, "Value change cancelled", ex);
                            return e.value;
                        }
                        oldValue = (changed = e).value;
                        e.key = k;
                        e.value = value;
                        addChangeEvent(k, oldValue, value, false, false);
                        break;
                    }
                }
                if (null == changed) {
                    addChangeEvent(k, null, value, true, false);
                    backingList.add(new Entry(key, value));
                }
            }
            try {
                if (null != changed) {
                    try {
                        if (!oldKey.equals(changed.key))
                            changed.propertyChangeSupport.firePropertyChange(Entry.PROP_KEY, oldKey, changed.key);
                    } finally {
                        changed.propertyChangeSupport.firePropertyChange(Entry.PROP_VALUE, oldValue, changed.value);
                    }
                }
            } finally {
                fireChangeEvents();
            }
            return oldValue;
        }
        throw new ClassCastException();
    }

    @Override
    public V remove(Object key) {
        if (Objects.requireNonNull(key) instanceof String) {
            String k = (String) key;
            V oldValue = null;
            synchronized (backingList) {
                for (int i = 0; i < backingList.size(); i++) {
                    Entry e = backingList.get(i);
                    if (e.key.equalsIgnoreCase(k)) {
                        oldValue = e.value;
                        backingList.remove(i);
                        addChangeEvent(k, oldValue, null, false, true);
                        break;
                    }
                }
            }
            fireChangeEvents();
            return oldValue;
        }
        throw new ClassCastException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        m.keySet().forEach((t) -> put(t, m.get(t)));
    }

    @Override
    public void clear() {
        synchronized (backingList) {
            if (backingList.isEmpty())
                return;
            backingList.forEach((t) -> addChangeEvent(t.key, t.value, null, false, true));
            backingList.clear();
        }
        fireChangeEvents();
    }

    @Override
    public Set<String> keySet() {
        return keySet;
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return entrySet;
    }

    @Override
    public void addListener(MapChangeListener<? super String, ? super V> listener) {
        synchronized (backingList) {
            listenerHelper = MapListenerHelper.addListener(listenerHelper, listener);
        }
    }

    @Override
    public void removeListener(MapChangeListener<? super String, ? super V> listener) {
        synchronized (backingList) {
            listenerHelper = MapListenerHelper.removeListener(listenerHelper, listener);
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        synchronized (backingList) {
            listenerHelper = MapListenerHelper.addListener(listenerHelper, listener);
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        synchronized (backingList) {
            listenerHelper = MapListenerHelper.removeListener(listenerHelper, listener);
        }
    }

    private class Entry implements Map.Entry<String, V> {

        private final Map.Entry<String, V> readOnly;
        public static final String PROP_KEY = "key";
        private String key;

        /**
         * Get the value of key
         *
         * @return the value of key
         */
        @Override
        public String getKey() {
            return key;
        }

        private V value;

        public static final String PROP_VALUE = "value";

        /**
         * Get the value of value
         *
         * @return the value of value
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Set the value of value
         *
         * @param value new value of value
         * @throws java.beans.PropertyVetoException
         */
        @Override
        public V setValue(V value) {
            if (null == value && !nullAllowed) {
                throw new NullPointerException();
            }
            V oldValue;
            synchronized (backingList) {
                oldValue = this.value;
                try {
                    vetoableChangeSupport.fireVetoableChange(PROP_VALUE, oldValue, value);
                } catch (PropertyVetoException ex) {
                    LOG.log(Level.FINE, "Value change cancelled", ex);
                    return this.value;
                }
                this.value = value;
            }
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
            return oldValue;
        }

        Entry(String key, V value) {
            this.key = key;
            this.value = value;
            readOnly = new Map.Entry<String, V>() {
                @Override
                public String getKey() {
                    return Entry.this.key;
                }

                @Override
                public V getValue() {
                    return Entry.this.value;
                }

                @Override
                public V setValue(V value) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

        /**
         * Add PropertyChangeListener.
         *
         * @param listener
         */
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        /**
         * Remove PropertyChangeListener.
         *
         * @param listener
         */
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        private transient final VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

        /**
         * Add VetoableChangeListener.
         *
         * @param listener
         */
        public void addVetoableChangeListener(VetoableChangeListener listener) {
            vetoableChangeSupport.addVetoableChangeListener(listener);
        }

        /**
         * Remove VetoableChangeListener.
         *
         * @param listener
         */
        public void removeVetoableChangeListener(VetoableChangeListener listener) {
            vetoableChangeSupport.removeVetoableChangeListener(listener);
        }

    }

    private class KeySet implements ReadOnlySet<String> {

        @Override
        public int size() {
            return backingList.size();
        }

        @Override
        public boolean isEmpty() {
            return backingList.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return containsKey((String)o);
        }

        @Override
        public Iterator<String> iterator() {
            return backingList.stream().map((t) -> t.key).iterator();
        }

        @Override
        public Object[] toArray() {
            return backingList.stream().map((t) -> t.key).toArray();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().allMatch((t) -> containsKey((String)t));
        }

    }

    private class Values implements ReadOnlyCollection<V> {

        @Override
        public int size() {
            return backingList.size();
        }

        @Override
        public boolean isEmpty() {
            return backingList.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue((V)o);
        }

        @Override
        public Iterator<V> iterator() {
            return backingList.stream().map((t) -> t.value).iterator();
        }

        @Override
        public Object[] toArray() {
            return backingList.stream().map((t) -> t.value).toArray();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().allMatch((t) -> containsValue((V)t));
        }

    }

    private class EntrySet implements ReadOnlySet<Map.Entry<String, V>> {

        @Override
        public int size() {
            return backingList.size();
        }

        @Override
        public boolean isEmpty() {
            return backingList.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return backingList.contains(o);
        }

        @Override
        public Iterator<Map.Entry<String, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public Object[] toArray() {
            return backingList.toArray();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return backingList.containsAll(c);
        }

    }

    private class EntryIterator implements ListIterator<Map.Entry<String, V>> {

        private final ListIterator<Entry> backingIterator;

        EntryIterator() {
            backingIterator = backingList.listIterator();
        }

        EntryIterator(int index) {
            backingIterator = backingList.listIterator(index);
        }

        EntryIterator(ListIterator<Entry> iterator) {
            backingIterator = (null == iterator) ? backingList.listIterator() : iterator;
        }

        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }

        @Override
        public Map.Entry<String, V> next() {
            return backingIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            return backingIterator.hasPrevious();
        }

        @Override
        public Map.Entry<String, V> previous() {
            return backingIterator.previous();
        }

        @Override
        public int nextIndex() {
            return backingIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return backingIterator.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Map.Entry<String, V> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Map.Entry<String, V> e) {
            throw new UnsupportedOperationException();
        }
    }

    private class ReadOnly implements ReadOnlyMap<String, V> {

        @Override
        public int size() {
            return backingList.size();
        }

        @Override
        public boolean isEmpty() {
            return backingList.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return CaseInsensitiveStringMap.this.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return CaseInsensitiveStringMap.this.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return CaseInsensitiveStringMap.this.get(key);
        }

        @Override
        public Set<String> keySet() {
            return keySet;
        }

        @Override
        public Collection<V> values() {
            return values;
        }

        @Override
        public Set<Entry<String, V>> entrySet() {
            return readOnlyEntrySet;
        }

    }

    private class ReadOnlyEntrySet extends EntrySet {

        @Override
        public Iterator<Map.Entry<String, V>> iterator() {
            return new ReadOnlyEntryIterator();
        }

        @Override
        public Object[] toArray() {
            return backingList.stream().map((t) -> t.readOnly).toArray();
        }

        @Override
        public boolean contains(Object o) {
            return null != o && o instanceof Map.Entry && backingList.stream().anyMatch((t) -> o.equals(t.readOnly));
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().allMatch((e) -> null != e && e instanceof Map.Entry && backingList.stream().anyMatch((t) -> e.equals(t.readOnly)));
        }

    }

    private class ReadOnlyEntryIterator extends EntryIterator {

        ReadOnlyEntryIterator() {
            super();
        }

        ReadOnlyEntryIterator(int index) {
            super(index);
        }

        ReadOnlyEntryIterator(ListIterator<Entry> iterator) {
            super(iterator);
        }

        @Override
        public Map.Entry<String, V> next() {
            return ((Entry) super.next()).readOnly;
        }

        @Override
        public Map.Entry<String, V> previous() {
            return ((Entry) super.previous()).readOnly;
        }
    }

}
