/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Pair;

/**
 *
 * @author Leonard T. Erwine
 * @param <V>
 */
public class ObjectKeyMap<V extends ObjectKeyMap.KeyedObject> implements Set<V> {
    private final NodeSet<V> nodes;
    
    public ObjectKeyMap() {
        nodes = new NodeSet<>();
    }

    public ObjectKeyMap(Set<? extends V> c) {
        nodes = new NodeSet<>();
    }

    public ObjectKeyMap(int initialCapacity, float loadFactor) {
        nodes = new NodeSet<>(initialCapacity, loadFactor);
    }
    
    public ObjectKeyMap(int initialCapacity) {
        nodes = new NodeSet<>(initialCapacity);
    }

    @Override
    public int size() { return nodes.allNodes.size(); }

    @Override
    public boolean isEmpty() { return nodes.allNodes.isEmpty(); }

    @Override
    public boolean contains(Object o) {
        if (Objects.isNull(o))
            return nodes.allNodes.stream().anyMatch((Node<V> item) -> Objects.isNull(item.value.get()));
        return o instanceof KeyedObject &&
                nodes.allNodes.stream().anyMatch((Node<V> item) -> { return Objects.equals(o, item.getValue()); });
    }

    @Override
    public Iterator<V> iterator() { return stream().iterator(); }

    @Override
    public Object[] toArray() { return nodes.allNodes.stream().map((Node<V> item) -> item.value.get()).toArray(); }

    @Override
    public <T> T[] toArray(T[] a) { return nodes.allNodes.stream().map((Node<V> item) -> (T)item.value.get()).toArray((int value) -> a); }

    @Override
    public boolean add(V e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.isEmpty() || c.stream().allMatch((Object o) -> {
            if (Objects.isNull(o))
                return nodes.allNodes.stream().anyMatch((Node<V> item) -> Objects.isNull(item.value.get()));
            return o instanceof KeyedObject &&
                nodes.allNodes.stream().anyMatch((Node<V> item) -> { return Objects.equals(o, item.getValue()); });
        });
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        if (c.isEmpty())
            return true;
        
        final ArrayList<Pair<String, V>> items = new ArrayList<>();
        if (c.stream().anyMatch((V o) -> {
            ObservableValue<String> getKey;
            String k;
            if (o == null || (getKey = o.keyProperty()) == null || (k = getKey.getValue()) == null)
                return true;
            final String lk = k.toLowerCase();
            if (items.stream().anyMatch((Pair<String, V> i) -> lk.equals(i.getKey())) ||
                    nodes.allNodes.stream().anyMatch((Node<V> i) -> lk.equals(i.getKey())))
                return true;
            items.add(new Pair<>(lk, o));
            return false;
        }))
            return false;
        items.forEach((Pair<String, V> item) -> {
            Node<V> node = new Node<V>(item.getValue(), this);
            if (node.isValid()) {
                String key = node.getKey();
                if (key != null && nodes.contains(node)) {
                    try {
                        nodes.add(node);
                        return;
                    } catch (Exception e) { }
                }
            }
            nodes.allNodes.add(node);
        });
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() { nodes.clear(); }

    @Override
    public Stream<V> stream() { return nodes.allNodes.stream().map((Node<V> item) -> item.value.get()); }

    @Override
    public void forEach(Consumer<? super V> action) { nodes.allNodes.stream().forEach((Node<V> item) -> { action.accept(item.value.get()); }); }

    public boolean containsKey(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public V get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Set<String> keySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Set<Node<V>> entrySet() { return nodes; }

    public interface KeyedObject { ObservableValue<String> keyProperty(); }
    
    static class NodeSet<V extends KeyedObject> extends HashSet<Node<V>> {
        private final ArrayList<Node<V>> allNodes;

        NodeSet() {
            allNodes = new ArrayList<>();
        }

        NodeSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
            allNodes = new ArrayList<>(initialCapacity);
        }

        public NodeSet(int initialCapacity) {
            super(initialCapacity);
            allNodes = new ArrayList<>(initialCapacity);
        }

        @Override
        public void clear() {
            allNodes.stream().forEach((Node<V> t) -> {
                t.target = null;
                if (t.keyObservable != null) {
                    t.keyObservable.removeListener(t.keyChangeListener);
                    t.keyObservable = null;
                }
                t.value.set(null);
            });
            allNodes.clear();
            stream().forEach((Node<V> t) -> {
                t.target = null;
                if (t.keyObservable != null) {
                    t.keyObservable.removeListener(t.keyChangeListener);
                    t.keyObservable = null;
                }
                t.value.set(null);
            });
            super.clear();
        }

        @Override
        public boolean remove(Object o) {
            return super.remove(o); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean add(Node<V> e) {
            return super.add(e); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return super.removeAll(c); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return super.retainAll(c); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean addAll(Collection<? extends Node<V>> c) {
            return super.addAll(c); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    public static class Node<V extends KeyedObject> implements java.util.Map.Entry<String, V> {
        private ObjectKeyMap<V> target;
        private ObservableValue<String> keyObservable;
        private KeyChangeListener keyChangeListener;
        
        private final ReadOnlyStringWrapper key;

        @Override
        public int hashCode() { return Objects.hashCode(key.get()); }

        @Override
        public boolean equals(Object obj) {
            return Objects.nonNull(obj) && getClass() == obj.getClass() && Objects.equals(key.get(), ((Node<V>)obj).key.get());
        }

        @Override
        public String getKey() { return key.get(); }

        public ReadOnlyStringProperty keyProperty() { return key.getReadOnlyProperty(); }
        
        private final ObjectProperty<V> value;

        @Override
        public V getValue() { return value.get(); }

        @Override
        public V setValue(V value) {
            this.value.set(value);
            return value;
        }
        
        public ObjectProperty valueProperty() { return value; }

        private final ReadOnlyBooleanWrapper valid;

        public boolean isValid() { return valid.get(); }

        public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }

        private Node(V value, ObjectKeyMap<V> target) {
            this.target = target;
            if ((this.value = new SimpleObjectProperty<>(value)).get() == null) {
                keyObservable = null;
                valid = new ReadOnlyBooleanWrapper(false);
                key = new ReadOnlyStringWrapper();
            } else if ((keyObservable = value.keyProperty()) == null) {
                valid = new ReadOnlyBooleanWrapper(false);
                key = new ReadOnlyStringWrapper();
            } else
                valid = new ReadOnlyBooleanWrapper((key = new ReadOnlyStringWrapper(keyObservable.getValue())).get() != null);
            keyChangeListener = new KeyChangeListener<V>(this);
            keyObservable.addListener(keyChangeListener);
            this.value.addListener((ObservableValue<? extends V> observable, V oldValue, V newValue) -> {
                if (keyObservable != null)
                    keyObservable.removeListener(keyChangeListener);
                if ((keyObservable = (newValue == null) ? null : newValue.keyProperty()) != null) {
                    key.set(keyObservable.getValue());
                    keyObservable.addListener(keyChangeListener);
                    valid.set(key.get() != null);
                } else
                    valid.set(false);
            });
        }
    }
    
    private static class KeyChangeListener<V extends KeyedObject> implements ChangeListener<String> {
        private final Node<V> node;
        
        KeyChangeListener(Node<V> node) { this.node = node; }
        
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            node.valid.set(newValue != null);
        }
    }
}
