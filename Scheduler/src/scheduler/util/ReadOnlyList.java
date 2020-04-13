package scheduler.util;

import java.util.ArrayList;
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
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <E> The type of elements in this collection.
 */
public interface ReadOnlyList<E> extends List<E> {

    /**
     * Overridden to prevent changes to the current list.
     *
     * @param index The buildIndex of the candidate insertion point.
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
     * @param index The buildIndex of the candidate target element.
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
     * @param index The buildIndex of the candidate insertion point.
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
     * @param index The buildIndex of the candidate element.
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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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

            @SuppressWarnings("unchecked")
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
    
    @SuppressWarnings("unchecked")
    public static <T> ReadOnlyList<T> empty() {
            return of ((T[])new Object[0], 0, 0);
    }
    
    /**
     * Builds a {@link ReadOnlyList} from a backing array with a pre-determined length.
     * 
     * @param <E> The element type.
     */
    public static class Builder<E> implements List<E> {
        private E[] backingArray;
        private int buildIndex;
        private int buildCount;
        private ReadOnlyList<E> builtList;
        
        /**
         * Builds a {@link ReadOnlyList}.
         * 
         * @return The built {@link ReadOnlyList}.
         */
        public synchronized ReadOnlyList<E> build() {
            if (null == builtList)
                builtList = ReadOnlyList.of(backingArray);
            return builtList;
        }

        /**
         * Get the size of the {@link ReadOnlyList} to be built.
         * 
         * @return The size of the {@link ReadOnlyList} to be built.
         */
        public int getTargetSize() {
            return backingArray.length;
        }

        /**
         * Changes the size of the {@link ReadOnlyList} to be built.
         * 
         * @param targetSize The new size of the {@link ReadOnlyList} to be built.
         * @throws IllegalStateException if the target list was already built.
         */
        @SuppressWarnings("unchecked")
        public synchronized void setTargetSize(int targetSize) {
            if (null != builtList)
                throw new IllegalStateException("Cannot change size after target list is built");
            E[] source = backingArray;
            if (backingArray.length < targetSize) {
                backingArray = Arrays.copyOf(source, targetSize);
            } else if (backingArray.length > targetSize) {
                backingArray = (E[])(new Object[targetSize]);
                System.arraycopy(source, 0, backingArray, 0, targetSize);
            }
            if (buildIndex > targetSize) {
                buildCount = buildIndex = targetSize;
            } else if (buildCount > targetSize)
                buildCount = targetSize;
        }

        /**
         * Gets the index at which the next element will be placed.
         * 
         * @return The index where the next call to {@link #accept(java.lang.Object)} will place the element.
         */
        public int getBuildIndex() {
            return buildIndex;
        }

        /**
         * Sets the index at which the next element will be placed.
         * 
         * @param index The index where the next call to {@link #accept(java.lang.Object)} will place the element.
         * @throws IndexOutOfBoundsException if {@code buildIndex} is less than zero or greater than the buildIndex of the last built element.
         */
        public synchronized void setBuildIndex(int index) {
            if (index < 0 || index > buildCount)
                throw new IndexOutOfBoundsException();
            this.buildIndex = index;
        }
        
        /**
         * Creates a new {@link ReadOnlyList} with a backing array of a specified length.
         * 
         * @param length The length of the backing array which will also be the {@link Collection#size()} of the {@link ReadOnlyList}.
         */
        @SuppressWarnings("unchecked")
        public Builder(int length) {
            backingArray = (E[])(new Object[length]);
            buildIndex = 0;
            buildCount = 0;
            builtList = null;
        }
        
        /**
         * Sets the value of the element at current builder {@link #buildIndex} and advances the {@link #buildIndex} by {@code 1}.
         * 
         * @param e The element to be added.
         * @throws NoSuchElementException if {@link #buildIndex} was already at the end of the {@link #backingArray}.
         */
        public synchronized void accept(E e) {
            if (buildIndex == backingArray.length)
                throw new NoSuchElementException();
            if (buildIndex < buildCount)
                backingArray[buildIndex++] = e;
            else {
                backingArray[buildCount++] = e;
                buildIndex = buildCount;
            }
        }

        private void insert(int index, E e) {
            int shiftCount = buildCount - index;
            if (shiftCount > 0) {
                System.arraycopy(backingArray, index, backingArray, index + 1, shiftCount);
                backingArray[index] = e;
                if (buildIndex >= index)
                    buildIndex++;
                buildCount++;
            } else
                append(e);
        }
        
        private void append(E e) {
            if (buildIndex < buildCount)
                backingArray[buildCount++] = e;
            else {
                backingArray[buildIndex++] = e;
                buildCount = buildIndex;
            }
        }
        
        private void removeAt(int index) {
            int shiftCount = buildCount - index - 1;
            if (shiftCount > 0)
                System.arraycopy(backingArray, index + 1, backingArray, index, shiftCount);
            backingArray[--buildCount] = null;
            if (buildIndex > buildCount)
                buildIndex = buildCount;
            else if (buildIndex > index)
                buildIndex--;
        }
        
        @Override
        public int size() {
            return buildCount;
        }

        @Override
        public boolean isEmpty() {
            return buildCount == 0;
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        @Override
        public Iterator<E> iterator() {
            return listIterator(0);
        }

        @Override
        public Object[] toArray() {
            return Arrays.copyOf(backingArray, backingArray.length);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length < backingArray.length) {
                return (T[]) Arrays.copyOf(backingArray, backingArray.length, a.getClass());
            }
            System.arraycopy(backingArray, 0, a, 0, backingArray.length);
            if (a.length > backingArray.length) {
                a[backingArray.length] = null;
            }
            return a;
        }

        @Override
        public synchronized boolean add(E e) {
            if (buildCount == backingArray.length)
                return false;
            append(e);
            return true;
        }

        @Override
        public synchronized boolean remove(Object o) {
            int i = indexOf(o);
            if (i < 0)
                return false;
            removeAt(i);
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().allMatch((t) -> contains((E)t));
        }

        @Override
        public synchronized boolean addAll(Collection<? extends E> c) {
            boolean listModified = false;
            for (E t : c) {
                if (add(t))
                    listModified = true;
                else if (buildCount == backingArray.length)
                    break;
            }
            return listModified;
        }

        @Override
        public synchronized boolean addAll(int index, Collection<? extends E> c) {
            if (index < 0 || index > buildCount)
                throw new IndexOutOfBoundsException();
            boolean listModified = false;
            for (E t : c) {
                if (buildCount == backingArray.length)
                    break;
                insert(index++, t);
                listModified = true;
            }
            return listModified;
        }

        @SuppressWarnings("unchecked")
        @Override
        public synchronized boolean removeAll(Collection<?> c) {
            boolean listModified = false;
            for (Object t : c) {
                if (remove((E)t))
                    listModified = true;
            }
            return listModified;
        }

        @Override
        public synchronized boolean retainAll(Collection<?> c) {
            boolean listModified = false;
            for (int index = 0; index < buildCount; index++) {
                if (!c.contains(backingArray[index])) {
                    listModified = true;
                    removeAt(index--);
                }
            }
            return listModified;
        }

        @Override
        public synchronized void clear() {
            for (int index = 0; index < buildCount; index++) {
                backingArray[index] = null;
            }
            buildIndex = buildCount = 0;
        }

        @Override
        public synchronized E get(int index) {
            if (index < 0 || index >= buildCount)
                throw new IndexOutOfBoundsException();
            return backingArray[index];
        }

        @Override
        public synchronized E set(int index, E element) {
            if (index < 0 || index > buildCount)
                throw new IndexOutOfBoundsException();
            E result = backingArray[index];
            backingArray[index] = element;
            return result;
        }

        @Override
        public synchronized void add(int index, E element) {
            if (index < 0 || index >= buildCount)
                throw new IndexOutOfBoundsException();
            if (buildCount == backingArray.length)
                throw new IllegalStateException();
            insert(index, element);
        }

        @Override
        public synchronized E remove(int index) {
            E item = get(index);
            removeAt(index);
            return item;
        }

        @Override
        public synchronized int indexOf(Object o) {
            if (o == null) {
                for (int i = 0; i < buildCount; i++)
                    if (backingArray[i] == null)
                        return i;
            } else {
                for (int i = 0; i < buildCount; i++)
                    if (o.equals(backingArray[i]))
                        return i;
            }
            return -1;
        }

        @Override
        public synchronized int lastIndexOf(Object o) {
            if (o == null) {
                for (int i = buildCount - 1; i >= 0; i--)
                    if (backingArray[i] == null)
                        return i;
            } else {
                for (int i = buildCount - 1; i >= 0; i--)
                    if (o.equals(backingArray[i]))
                        return i;
            }
            return -1;
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            if (null != builtList)
                return builtList.listIterator(index);
            if (index < 0 || index > buildCount)
                throw new IndexOutOfBoundsException();
            return new ListIterator<E>() {
                private int currentIndex = index;
                private int lastIndex = -1;
                @Override
                public boolean hasNext() {
                    return currentIndex < Builder.this.buildCount;
                }

                @Override
                public synchronized E next() {
                    if (currentIndex < Builder.this.buildCount) {
                        try {
                            E result = get(currentIndex++);
                            lastIndex = currentIndex + 1;
                            return result;
                        } catch (IndexOutOfBoundsException ex) {
                            currentIndex = Builder.this.buildCount;
                            throw new NoSuchElementException(ex.getMessage());
                        }
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public synchronized boolean hasPrevious() {
                    if (currentIndex > Builder.this.buildCount)
                        currentIndex = Builder.this.buildCount;
                    return currentIndex > 0;
                }

                @Override
                public synchronized E previous() {
                    if (currentIndex > Builder.this.buildCount)
                        currentIndex = Builder.this.buildCount;
                    if (currentIndex > 0) {
                        try {
                            E result = get(--currentIndex);
                            lastIndex = currentIndex;
                            return result;
                        } catch (IndexOutOfBoundsException ex) {
                            currentIndex = Builder.this.buildCount;
                            throw new NoSuchElementException(ex.getMessage());
                        }
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public synchronized int nextIndex() {
                    if (currentIndex > Builder.this.buildCount)
                        currentIndex = Builder.this.buildCount;
                    return currentIndex;
                }

                @Override
                public synchronized int previousIndex() {
                    if (currentIndex > Builder.this.buildCount)
                        currentIndex = Builder.this.buildCount;
                    return currentIndex - 1;
                }

                @Override
                public void remove() {
                    if (lastIndex < 0)
                        throw new IllegalStateException();
                    try { Builder.this.removeAt(lastIndex); }
                    finally { lastIndex = -1; }
                }

                @Override
                public void set(E e) {
                    if (lastIndex < 0)
                        throw new IllegalStateException();
                    Builder.this.set(lastIndex, e);
                }

                @Override
                public void add(E e) {
                    if (lastIndex < 0)
                        throw new IllegalStateException();
                    try { Builder.this.add(lastIndex, e); }
                    finally { lastIndex = -1; }
                }
                
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        public synchronized List<E> subList(int fromIndex, int toIndex) {
            if (null != builtList)
                return builtList.subList(fromIndex, toIndex);
            if (fromIndex < 0 || toIndex > buildCount || fromIndex > toIndex)
                throw new IndexOutOfBoundsException();
            if (fromIndex == 0)
                return ReadOnlyList.of(Arrays.copyOf(backingArray, toIndex));
            E[] elementData = (E[])(new Object[toIndex - fromIndex]);
            if (elementData.length > 0)
                System.arraycopy(backingArray, fromIndex, elementData, 0, elementData.length);
            return ReadOnlyList.of(elementData);
        }
    }
    
    public static class Wrapper<E> extends ArrayList<E> {
        private static final long serialVersionUID = -7259044324459981946L;
        private final ReadOnlyList<E> readOnlyList = new ReadOnlyList<E>() {
                @Override
                public int size() {
                    return Wrapper.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return Wrapper.this.isEmpty();
                }

                @Override
                public boolean contains(Object o) {
                    return Wrapper.this.contains(o);
                }

                @Override
                public Iterator<E> iterator() {
                    return listIterator();
                }

                @Override
                public Object[] toArray() {
                    return Wrapper.this.toArray();
                }

                @Override
                public boolean containsAll(Collection<?> c) {
                    return Wrapper.this.containsAll(c);
                }

                @Override
                public E get(int index) {
                    return Wrapper.this.get(index);
                }

                @Override
                public int indexOf(Object o) {
                    return Wrapper.this.indexOf(o);
                }

                @Override
                public int lastIndexOf(Object o) {
                    return Wrapper.this.lastIndexOf(o);
                }

                private ListIterator<E> listIterator(ListIterator<E> backingIterator) {
                    return new ListIterator<E>() {
                        @Override
                        public boolean hasNext() {
                            return backingIterator.hasNext();
                        }

                        @Override
                        public E next() {
                            return backingIterator.next();
                        }

                        @Override
                        public boolean hasPrevious() {
                            return backingIterator.hasPrevious();
                        }

                        @Override
                        public E previous() {
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
                            throw new UnsupportedOperationException("List is read-only.");
                        }

                        @Override
                        public void set(E e) {
                            throw new UnsupportedOperationException("List is read-only.");
                        }

                        @Override
                        public void add(E e) {
                            throw new UnsupportedOperationException("List is read-only.");
                        }
                    };
                }
                
                @Override
                public ListIterator<E> listIterator() {
                    return listIterator(Wrapper.this.listIterator());
                }

                @Override
                public ListIterator<E> listIterator(int index) {
                    return listIterator(Wrapper.this.listIterator(index));
                }

                @Override
                public List<E> subList(int fromIndex, int toIndex) {
                    return Wrapper.this.subList(fromIndex, toIndex);
                }
                
            };
        
        public Wrapper() {
            super();
        }

        public Wrapper(int initialCapacity) {
            super(initialCapacity);
        }

        public Wrapper(Collection<? extends E> c) {
            super(c);
        }

        public ReadOnlyList<E> getReadOnlyList() {
            return readOnlyList;
        }
        
    }
}
