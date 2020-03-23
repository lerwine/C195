/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
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
     * @param c The candidate element comparer.
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
     * @param element The candidate element collection.
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
        if (a.length < elementData.length) {
            return (T[]) Arrays.copyOf(elementData, elementData.length, a.getClass());
        }
        System.arraycopy(elementData, 0, a, 0, elementData.length);
        if (a.length > elementData.length) {
            a[elementData.length] = null;
        }
        return a;
    }

    public static <T> ReadOnlyList<T> of(T initial, T ...additionalValues) {
        if (null == additionalValues || additionalValues.length == 0)
            return of((T[])(new Object[] { initial }), 0, 1);
        Object[] arr = new Object[additionalValues.length + 1];
        arr[0] = initial;
        System.arraycopy(additionalValues, 0, arr, 1, additionalValues.length);
        return of((T[])arr, 0, arr.length);
    }
    
    public static <T> ReadOnlyList<T> of(T[] source, int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > source.length) {
            throw new IndexOutOfBoundsException();
        }
        return new ReadOnlyList<T>() {
            private final T[] items = source;
            private final int offset = fromIndex;
            private final int end = toIndex;
            private final int size = toIndex - fromIndex;

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isEmpty() {
                return size == 0;
            }

            @Override
            public boolean contains(Object o) {
                if (null == o) {
                    for (int i = offset; i < end; i++) {
                        if (null == items[i]) {
                            return true;
                        }
                    }
                } else {
                    for (int i = offset; i < end; i++) {
                        if (o.equals(items[i])) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public Iterator<T> iterator() {
                return listIterator(0);
            }

            @Override
            public Object[] toArray() {
                Object[] result = new Object[size];
                if (size > 0)
                    System.arraycopy(items, offset, result, 0, size);
                return result;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return c.stream().allMatch((t) -> contains((T)t));
            }

            @Override
            public T get(int index) {
                if (index < 0 || index >= size)
                    throw new IndexOutOfBoundsException();
                return items[offset + index];
            }

            @Override
            public int indexOf(Object o) {
                if (null == o) {
                    for (int i = offset; i < end; i++) {
                        if (null == items[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = offset; i < end; i++) {
                        if (o.equals(items[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public int lastIndexOf(Object o) {
                if (null == o) {
                    for (int i = end - 1; i >= offset; i--) {
                        if (null == items[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = end - 1; i >= offset; i--) {
                        if (o.equals(items[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public ListIterator<T> listIterator() {
                return listIterator(0);
            }

            @Override
            public ListIterator<T> listIterator(int index) {
                if (index < 0 || index > size)
                    throw new IndexOutOfBoundsException();
                
                return new ListIterator<T>() {
                    private int position = index;
                    
                    @Override
                    public boolean hasNext() {
                        return position < size;
                    }

                    @Override
                    public T next() {
                        if (position < size)
                            return items[offset + (position++)];
                        throw new NoSuchElementException();
                    }

                    @Override
                    public boolean hasPrevious() {
                        return position > 0;
                    }

                    @Override
                    public T previous() {
                        if (position > 0)
                            return items[offset + (--position)];
                        throw new NoSuchElementException();
                    }

                    @Override
                    public int nextIndex() {
                        return (position < size) ? position : size;
                    }

                    @Override
                    public int previousIndex() {
                        return position - 1;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("List is unmodifiable.");
                    }

                    @Override
                    public void set(T e) {
                        throw new UnsupportedOperationException("List is unmodifiable.");
                    }

                    @Override
                    public void add(T e) {
                        throw new UnsupportedOperationException("List is unmodifiable.");
                    }
                };
            }

            @Override
            public List<T> subList(int fromIndex, int toIndex) {
                if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
                    throw new IndexOutOfBoundsException();
                return of(items, offset + fromIndex, offset + toIndex);
            }
        };
    }
    
    public static <T> ReadOnlyList<T> of(T[] source) {
        return of(source, 0, source.length);
    }
    
    public static <T> ReadOnlyList<T> empty() {
            return of ((T[])new Object[0], 0, 0);
    }
}
