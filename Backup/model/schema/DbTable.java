package scheduler.model.schema;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


public abstract class DbTable<T extends DbTable.DbColumn<? extends DbTable<T>>> implements IDbSchema<T> {
    /**
     * The name of the 'createDate' column.
     */
    public static final String COLNAME_CREATEDATE = "createDate";

    /**
     * The name of the 'createdBy' column.
     */
    public static final String COLNAME_CREATEDBY = "createdBy";

    /**
     * The name of the 'lastUpdate' column.
     */
    public static final String COLNAME_LASTUPDATE = "lastUpdate";

    /**
     * The name of the 'lastUpdateBy' column.
     */
    public static final String COLNAME_LASTUPDATEBY = "lastUpdateBy";
    
    protected abstract T getPrimaryKeyColumn();
    
    protected abstract T getCreateDateColumn();
    
    protected abstract T getCreatedByColumn();
    
    protected abstract T getLastUpdateColumn();
    
    protected abstract T getLastUpdateByColumn();
    
    public static abstract class DbColumn<T extends DbTable<? extends DbColumn<T>>> implements IDbColumn<T> {
        private final T schema;
        private final String name;
        private final DbColType type;
        protected DbColumn(T schema, DbColType type, String name) {
            this.schema = schema;
            this.name = name;
            this.type = type;
        }
        
        @Override
        public T getSchema() {
            return schema;
        }

        @Override
        public DbColType getType() {
            return type;
        }
        
        @Override
        public String getName() {
            return name;
        }
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return null != o && o instanceof DbColumn && ((DbColumn)o).schema == this;
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return null != c && (c == this || c.stream().allMatch((t) -> null != t && t instanceof DbColumn && ((DbColumn)t).schema == this));
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if (null != o && o instanceof DbColumn && ((DbColumn)o).schema == this) {
            int e = this.size();
            for (int i = 0; i < e; i++) {
                if (get(i) == o)
                    return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (null != o && o instanceof DbColumn && ((DbColumn)o).schema == this) {
            for (int i = this.size() - 1; i >= 0; i--) {
                if (get(i) == o)
                    return i;
            }
        }
        return -1;
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException();
        return new ListIterator<T>() {
            private int ptr = index;
            
            @Override
            public boolean hasNext() {
                return ptr < size();
            }

            @Override
            public T next() {
                if (ptr < size()) {
                    return get(ptr++);
                }
                throw new NoSuchElementException();
            }

            @Override
            public boolean hasPrevious() {
                return ptr > 0;
            }

            @Override
            public T previous() {
                if (ptr > 0) {
                    return get(--ptr);
                }
                throw new NoSuchElementException();
            }

            @Override
            public int nextIndex() {
                return (ptr < size()) ? ptr : size();
            }

            @Override
            public int previousIndex() {
                return ptr - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(T e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(T e) {
                throw new UnsupportedOperationException();
            }
            
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex > toIndex || fromIndex < 0 || toIndex > size())
            throw new IndexOutOfBoundsException();
        return new List<T>() {
            private final int offset = fromIndex;
            private final int length = toIndex - fromIndex;

            @Override
            public int size() {
                return length;
            }

            @Override
            public boolean isEmpty() {
                return length == 0;
            }

            @Override
            public boolean contains(Object o) {
                if (length == 0 || !DbTable.this.contains(o))
                    return false;
                Iterator<T> it = iterator();
                while (it.hasNext()) {
                    if (it.next() == o)
                        return true;
                }
                return false;
            }

            @Override
            public Iterator<T> iterator() {
                return listIterator();
            }

            @Override
            public Object[] toArray() {
                Object[] result = new Object[length];
                for (int i = 0; i < length; i++) {
                    result[i] = DbTable.this.get(i + offset);
                }
                return result;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                Object[] elementData = toArray();
                if (a.length < length)
                    return (T[]) Arrays.copyOf(elementData, length, a.getClass());
                System.arraycopy(elementData, 0, a, 0, length);
                if (a.length > length)
                    a[length] = null;
                return a;
            }

            @Override
            public boolean add(T e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return null != c && (c == this || c.stream().allMatch((t) -> {
                    if (length > 0 && null != t && t instanceof DbColumn && ((DbColumn)t).schema == DbTable.this) {
                        Iterator<T> it = iterator();
                        while (it.hasNext()) {
                            if (it.next() == t)
                                return true;
                        }
                    }
                    return false;
                }));
            }

            @Override
            public boolean addAll(Collection<? extends T> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(int index, Collection<? extends T> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            public T get(int index) {
                if (index < 0 || index >= length)
                    throw new IndexOutOfBoundsException();
                return DbTable.this.get(index + offset);
            }

            @Override
            public T set(int index, T element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(int index, T element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public T remove(int index) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int indexOf(Object o) {
                if (length > 0 && null != o && o instanceof DbColumn && ((DbColumn)o).schema == DbTable.this) {
                    for (int i = 0; i < length; i++) {
                        if (DbTable.this.get(i + offset) == o)
                            return i;
                    }
                }
                return -1;
            }

            @Override
            public int lastIndexOf(Object o) {
                if (length > 0 && null != o && o instanceof DbColumn && ((DbColumn)o).schema == DbTable.this) {
                    for (int i = length - 1; i >= 0; i--) {
                        if (DbTable.this.get(i + offset) == o)
                            return i;
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
                if (index < 0 || index > length)
                    throw new IndexOutOfBoundsException();
                return new ListIterator<T>() {
                    int ptr = index;
                    @Override
                    public boolean hasNext() {
                        return ptr < length;
                    }

                    @Override
                    public T next() {
                        if (ptr < length) {
                            return DbTable.this.get(offset + (ptr++));
                        }
                        throw new NoSuchElementException();
                    }

                    @Override
                    public boolean hasPrevious() {
                        return ptr > 0;
                    }

                    @Override
                    public T previous() {
                        if (ptr > 0) {
                            return DbTable.this.get(offset + (--ptr));
                        }
                        throw new NoSuchElementException();
                    }

                    @Override
                    public int nextIndex() {
                        return (ptr < length) ? ptr : length;
                    }

                    @Override
                    public int previousIndex() {
                        return ptr - 1;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void set(T e) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void add(T e) {
                        throw new UnsupportedOperationException();
                    }
                    
                };
            }

            @Override
            public List<T> subList(int fromIndex, int toIndex) {
                if (fromIndex > toIndex || fromIndex < 0 || toIndex > length)
                    throw new IndexOutOfBoundsException();
                
                return (fromIndex == 0 && toIndex == length) ? this : DbTable.this.subList(offset + fromIndex, offset + toIndex);
            }
            
        };
    }
    
}
