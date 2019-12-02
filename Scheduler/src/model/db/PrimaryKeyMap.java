/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Leonard T. Erwine
 */
public class PrimaryKeyMap<R extends DataRow> implements Set<R> {
    private final Object syncRoot;
    private final HashMap<Integer, R> map;
    private final HashSet<R> set;
    private final RowPropertyChangeListener<R> propertyChangeListener;
    
    public PrimaryKeyMap() {
        syncRoot = new Object();
        map = new HashMap<>();
        set = new HashSet<>();
        propertyChangeListener = new RowPropertyChangeListener<>(this);
    }

    @Override
    public int size() { return set.size(); }

    @Override
    public boolean isEmpty() { return set.isEmpty(); }

    @Override
    public boolean contains(Object o) {
        if (o != null && o instanceof DataRow) {
            synchronized(syncRoot) {
                int pk = ((DataRow)o).getPrimaryKey();
                return map.containsKey(pk) && map.get(pk) == o;
            }
        }
        return false;
    }

    @Override
    public Iterator<R> iterator() { return set.iterator(); }

    @Override
    public Object[] toArray() { return set.toArray(); }

    @Override
    public <T> T[] toArray(T[] a) { return set.toArray(a); }

    @Override
    public boolean add(R e) {
        if (e == null)
            return false;
        synchronized(syncRoot) {
            int pk = e.getPrimaryKey();
            if (map.containsKey(pk))
                return false;
            map.put(pk, e);
            set.add(e);
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null || !(o instanceof DataRow))
            return false;
        R e = (R)o;
        synchronized(syncRoot) {
            int pk = e.getPrimaryKey();
            if (map.containsKey(pk) && map.get(pk) == e && set.remove(o)) {
                map.remove(pk);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends R> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeIf(Predicate<? super R> filter) {
        return Set.super.removeIf(filter); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Stream<R> stream() {
        return Set.super.stream(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void forEach(Consumer<? super R> action) {
        Set.super.forEach(action); //To change body of generated methods, choose Tools | Templates.
    }

    static class RowPropertyChangeListener<R extends DataRow> implements PropertyChangeListener {
        private final PrimaryKeyMap<R> primaryKeyMap;
        
        RowPropertyChangeListener(PrimaryKeyMap<R> source) { primaryKeyMap = source; }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}