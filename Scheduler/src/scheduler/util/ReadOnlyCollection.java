package scheduler.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Extends {@link Collection} interface with mutation methods overridden to throw {@link UnsupportedOperationException}.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <E> The type of elements in this collection.
 */
public interface ReadOnlyCollection<E> extends Collection<E> {

    /**
     * Overridden to prevent changes to the current collection.
     * 
     * @param e The candidate element.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Collection is read-only.
     */
    @Override
    public default boolean add(E e) {
        throw new UnsupportedOperationException("Target collection is read-only.");
    }

    /**
     * Overridden to prevent changes to the current collection.
     * 
     * @param o The candidate element.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Collection is read-only.
     */
    @Override
    public default boolean remove(Object o) {
        throw new UnsupportedOperationException("Target collection is read-only.");
    }

    /**
     * Overridden to prevent changes to the current collection.
     * 
     * @param c The candidate element collection.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Collection is read-only.
     */
    @Override
    public default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Target collection is read-only.");
    }

    /**
     * Overridden to prevent changes to the current collection.
     * 
     * @param c The candidate element collection.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Collection is read-only.
     */
    @Override
    public default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Target collection is read-only.");
    }

    /**
     * Overridden to prevent changes to the current collection.
     * 
     * @param filter The candidate filter.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Collection is read-only.
     */
    @Override
    public default boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("Target collection is read-only.");
    }

    /**
     * Overridden to prevent changes to the current collection.
     * 
     * @param c The candidate element collection.
     * @return (throws exception by default)
     * @throws UnsupportedOperationException - Collection is read-only.
     */
    @Override
    public default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Target collection is read-only.");
    }

    /**
     * Overridden to prevent changes to the current collection.
     * 
     * @throws UnsupportedOperationException - Collection is read-only.
     */
    @Override
    public default void clear() {
        throw new UnsupportedOperationException("Target collection is read-only.");
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
