/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Extends {@link List} interface with mutation methods overridden to throw {@link UnsupportedOperationException}.
 *
 * @author lerwi
 * @param <E> The type of elements in this collection.
 */
public interface ReadOnlyList<E> extends List<E> {

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param index The index of the candidate insertion point.
     * @param c The candidate element collection.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param operator The candidate replacement operator.
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param c  The candidate element comparer.
     */
    @Override
    public default void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param index The index of the candidate target element.
     * @param element The candidate replacement element.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default E set(int index, E element) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param index The index of the candidate insertion point.
     * @param element  The candidate element collection.
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default void add(int index, E element) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param index The index of the candidate element.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default E remove(int index) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param e The candidate element.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default boolean add(E e) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param o The candidate element.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default boolean remove(Object o) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param c The candidate element collection.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param c The candidate element collection.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param filter The candidate filter.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @param c The candidate element collection.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    /**
     * Overridden to prevent changes to the current list.
     * 
     * @throws UnsupportedOperationException - List is read-only.
     */
    @Override
    public default void clear() {
        throw new UnsupportedOperationException("Target list is read-only.");
    }

    @Override
    public default <T> T[] toArray(T[] a) {
        Object[] elementData = toArray();
        if (a.length < elementData.length)
            return (T[]) Arrays.copyOf(elementData, elementData.length, a.getClass());
        System.arraycopy(elementData, 0, a, 0, elementData.length);
        if (a.length > elementData.length)
            a[elementData.length] = null;
        return a;
    }

}
