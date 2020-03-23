package scheduler.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class ArrayListIterator<T> implements ListIterator<T> {
    private final T[] backingArray;
    private int index;
    
    public ArrayListIterator(T[] source) {
        backingArray = (null == source) ? (T[])(new Object[0]) : source;
        index = 0;
    }

    public ArrayListIterator(int index, T[] source) {
        backingArray = (null == source) ? (T[])(new Object[0]) : source;
        if (index < 0 || index > backingArray.length)
            throw new IndexOutOfBoundsException();
        this.index = index;
    }

    @Override
    public boolean hasNext() {
        return index < backingArray.length;
    }

    @Override
    public T next() {
        if (index < backingArray.length)
            return backingArray[index++];
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasPrevious() {
        if (index > backingArray.length)
            index = backingArray.length;
        return index > 0;
    }

    @Override
    public T previous() {
        if (index > backingArray.length)
            index = backingArray.length;
        if (index > 0)
            return backingArray[--index];
        throw new NoSuchElementException();
    }

    @Override
    public int nextIndex() {
        return (index < backingArray.length) ? index : backingArray.length;
    }

    @Override
    public int previousIndex() {
        return index - 1;
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
}
