/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine
 * @param <V>
 */
public class NamedObjectMap<V extends NamedObjectMap.NamedObject> implements Set<V> {
    private final NodeMap<V> setNodes;
    private final NodeList<V> allNodes;
    private final Object syncRoot;
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public NamedObjectMap() {
        this.syncRoot = new Object();
        setNodes = new NodeMap<>(this);
        allNodes = new NodeList<>(this);
    }
    
    public NamedObjectMap(Collection<? extends V> c) {
        this.syncRoot = new Object();
        setNodes = new NodeMap<>(this);
        allNodes = new NodeList<>(this, c);
    }
    
    public NamedObjectMap(int initialCapacity, float loadFactor) {
        this.syncRoot = new Object();
        setNodes = new NodeMap<>(this, initialCapacity, loadFactor);
        allNodes = new NodeList<>(this, initialCapacity);
    }
    
    public NamedObjectMap(int initialCapacity) {
        this.syncRoot = new Object();
        setNodes = new NodeMap<>(this, initialCapacity);
        allNodes = new NodeList<>(this, initialCapacity);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overrides">
    
    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<V> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean add(V e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Stream<V> stream() {
        return Set.super.stream();
    }

    @Override
    public void forEach(Consumer<? super V> action) {
        Set.super.forEach(action);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Mapping methods">
    
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public V get(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Set<String> keySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Node<V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public V getOrDefault(Object key, V defaultValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //</editor-fold>
    
    public interface NamedObject { ObservableValue<String> keyProperty(); }
    
    private static class NodeMap<V extends NamedObject> implements Map<String, V> {
        private final NamedObjectMap<V> target;
        private final HashSet<Node<V>> innerSet;
        
        private NodeMap(NamedObjectMap<V> target) {
            this.target = target;
            innerSet = new HashSet<>();
        }

        public NodeMap(NamedObjectMap<V> target, int initialCapacity, float loadFactor) {
            innerSet = new HashSet<>(initialCapacity, loadFactor);
            this.target = target;
        }

        public NodeMap(NamedObjectMap<V> target, int initialCapacity) {
            innerSet = new HashSet<>(initialCapacity);
            this.target = target;
        }

        @Override
        public int size() { return innerSet.size(); }

        @Override
        public boolean isEmpty() { return innerSet.isEmpty(); }

        @Override
        public boolean containsKey(Object key) {
            if (key == null || !(key instanceof String))
                return false;
            String lc = ((String)key).toLowerCase();
            return innerSet.stream().anyMatch((Node<V> item) -> {
                String k = item.getKey();
                return k != null && lc.equals(k.toLowerCase());
            });
        }

        @Override
        public boolean containsValue(Object value) {
            return value != null && innerSet.stream().anyMatch((Node<V> item) -> value == item);
        }

        @Override
        public V get(Object key) {
            if (key == null || !(key instanceof String))
                return null;
            String lc = ((String)key).toLowerCase();
            Optional<Node<V>> result = innerSet.stream().filter((Node<V> item) -> {
                String k = item.getKey();
                return k != null && lc.equals(k.toLowerCase());
            }).findFirst();
            return (result.isPresent()) ? result.get().getValue() : null;
        }

        @Override
        public V put(String key, V value) {
            if (value == null)
                throw new IllegalArgumentException();
            synchronized(target.syncRoot) {
                Node<V> node = new Node<>(value, target);
                String k = node.getKey();
                final String lc;
                if (k != null && (lc = key.toLowerCase()).equals(k.toLowerCase()) && !innerSet.stream().anyMatch((Node<V> item) -> {
                    String s = item.key.get();
                    return s != null && s.toLowerCase().equals(lc);
                }) && innerSet.add(node)) {
                    target.allNodes.innerList.add(node);
                    return value;
                }
                throw new IllegalArgumentException();
            }
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void putAll(Map<? extends String, ? extends V> m) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Set<String> keySet() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Collection<V> values() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Set<Entry<String, V>> entrySet() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return Map.super.getOrDefault(key, defaultValue); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public static class NodeList<V extends NamedObject> implements List<Node<V>> {
        private final NamedObjectMap<V> target;
        private final ArrayList<Node<V>> innerList;
        
        private NodeList(NamedObjectMap<V> target) {
            this.target = target;
            innerList = new ArrayList<>();
        }

        private NodeList(NamedObjectMap<V> target, Collection<? extends V> c) {
            this.target = target;
            innerList = new ArrayList<>();
            if (c == null)
                return;
            synchronized(target.syncRoot) {
                c.stream().forEach((V value) -> {
                    Node<V> item = new Node<>(value, target);
                    innerList.add(item);
                    String key = item.getKey();
                    if (key != null) {
                        String lc = key.toLowerCase();
                        if (!target.setNodes.innerSet.stream().map((Node<V> node) -> node.getKey()).anyMatch((String k) -> {
                            return k != null && k.toLowerCase().equals(lc);
                        }))
                            target.setNodes.innerSet.add(item);
                    }
                });
            }
        }

        public NodeList(NamedObjectMap<V> target, int initialCapacity) {
            innerList = new ArrayList<>(initialCapacity);
            this.target = target;
        }
        
        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Iterator<Node<V>> iterator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean add(Node<V> e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean addAll(Collection<? extends Node<V>> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean addAll(int index, Collection<? extends Node<V>> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Node<V> get(int index) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Node<V> set(int index, Node<V> element) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void add(int index, Node<V> element) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Node<V> remove(int index) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ListIterator<Node<V>> listIterator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ListIterator<Node<V>> listIterator(int index) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<Node<V>> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Stream<Node<V>> stream() {
            return List.super.stream(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void forEach(Consumer<? super Node<V>> action) {
            List.super.forEach(action); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public static class Node<V extends NamedObject> implements java.util.Map.Entry<String, V> {
        private final Object syncRoot;
        private final KeyChangeListener<V> listener;
        ObservableValue<String> observable;
        private NamedObjectMap<V> map;

        //<editor-fold defaultstate="collapsed" desc="key">
        
        private final ReadOnlyStringWrapper key;
        
        @Override
        public String getKey() { return key.get(); }
        
        public ReadOnlyStringProperty keyProperty() { return key.getReadOnlyProperty(); }
        
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="value">
        
        private final ObjectProperty<V> value;
        
        @Override
        public V getValue() { return value.get(); }
        
        public V setValue(V value) {
            this.value.set(value);
            return value;
        }
        
        public ObjectProperty valueProperty() { return value; }
        
        //</editor-fold>
        
        private Node(V value, NamedObjectMap<V> target) {
            syncRoot = new Object();
            map = target;
            listener = new KeyChangeListener<>(this);
            if (Objects.isNull((this.value = new SimpleObjectProperty<>(value)).get())) {
                observable = null;
                key = new ReadOnlyStringWrapper(null);
            } else if ((observable = value.keyProperty()) == null)
                key = new ReadOnlyStringWrapper(null);
            else {
                key = new ReadOnlyStringWrapper(observable.getValue());
                observable.addListener(listener);
            }
            this.value.addListener((ObservableValue<? extends V> p, V oldValue, V newValue) -> {
                synchronized(syncRoot) {
                    NamedObjectMap<V> t = map;
                    if (t == null)
                        return;
                    map = null;
                    String k;
                    synchronized(t.syncRoot) {
                        try {
                            if (observable != null)
                                observable.removeListener(listener);
                            observable = null;
                            if (Objects.isNull(newValue) || (observable = value.keyProperty()) == null)
                                k = null;
                            else {
                                k = observable.getValue();
                                observable.addListener(listener);
                            }
                        } finally { map = t; }
                    }
                    key.set(k);
                }
            });
        }

        @Override
        public int hashCode() {
            String k = this.key.get();
            return ((k == null) ? "" : k.toLowerCase()).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && getClass() == obj.getClass() && this == obj;
        }
    }
    
    private class Monitor {
        private final Object target;
        private final Object syncRoot;
        
        Monitor(Object target) {
            this.target = target;
            syncRoot = new Object();
        }

        public void run(Runnable runnable) {
            
        }
        
        public <T> void accept(Supplier<T> supplier, Consumer<T> consumer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> void accept(T value, Consumer<T> consumer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U> void accept(Supplier<T> a, Supplier<U> b, BiConsumer<T, U> consumer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U> void accept(Supplier<T> a, U b, BiConsumer<T, U> consumer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U> void accept(T a, U b, BiConsumer<T, U> consumer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> boolean test(Supplier<T> supplier, Predicate<T> predicate) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> boolean test(T value, Predicate<T> predicate) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U> boolean test(Supplier<T> a, Supplier<U> b, BiPredicate<T, U> predicate) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U> boolean test(Supplier<T> a, U b, BiPredicate<T, U> predicate) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U> boolean test(T a, U b, BiPredicate<T, U> predicate) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> T get(Supplier<T> supplier) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, R> R apply(Supplier<T> supplier, Function<T, R> function) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, R> R apply(T value, Function<T, R> function) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U, R> R apply(Supplier<T> a, Supplier<U> b, BiFunction<T, U, R> function) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U, R> R apply(Supplier<T> a, U b, BiFunction<T, U, R> function) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T, U, R> R apply(T a, U b, BiFunction<T, U, R> function) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> T apply(Supplier<T> supplier, UnaryOperator<T> op) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> T apply(T value, UnaryOperator<T> op) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> T apply(Supplier<T> a, Supplier<T> b, BinaryOperator<T> op) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> T apply(Supplier<T> a, T b, BinaryOperator<T> op) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public <T> T apply(T a, T b, BinaryOperator<T> op) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private static class KeyChangeListener<V extends NamedObject> implements ChangeListener<String> {
        private final Node<V> node;
        
        KeyChangeListener(Node<V> node) { this.node = node; }
        
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            NamedObjectMap<V> map = node.map;
            if (map == null)
                return;
            
            synchronized(map.syncRoot) {
                try {
                    if (newValue == null) {
                        if (map.setNodes.contains(node))
                            map.setNodes.remove(node);
                    } else {
                        String lc = newValue.toLowerCase();
                        if (oldValue == null || !oldValue.toLowerCase().equals(lc)) {
                            if (map.setNodes.contains(node)) {
                                if (map.setNodes.stream().anyMatch((Node<V> n) -> {
                                    String k = n.key.get();
                                    return k != null && k.toLowerCase().equals(lc);
                                }))
                                    map.setNodes.remove(node);
                            } else if (!map.setNodes.stream().anyMatch((Node<V> n) -> {
                                    String k = n.key.get();
                                    return k != null && k.toLowerCase().equals(lc);
                                }))
                                    map.setNodes.add(node);
                        }
                    }
                } finally {
                    node.key.set(newValue);
                }
            }
        }
    }
}
