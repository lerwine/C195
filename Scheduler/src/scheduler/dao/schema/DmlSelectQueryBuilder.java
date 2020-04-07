package scheduler.dao.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import scheduler.util.ReadOnlyList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class DmlSelectQueryBuilder implements DmlSelectTable, List<DmlSelectQueryBuilder.ResultColumn>, Consumer<StringBuffer>, Supplier<StringBuffer> {
    private final DbTable dbTable;
    private String alias;
    private final ArrayList<ResultColumn> backingList;
    private final HashMap<String, JoinedTable> joinMap;
    private final Map<String, JoinedTable> joins;
    private final HashMap<String, ResultColumn> columnMap;
    private final Map<String, ResultColumn> resultColumns;
    private boolean readOnly;
    private int tableCount;
    public DmlSelectQueryBuilder(DbTable dbTable, String alias, Iterable<DbColumn> toAdd) {
        this.dbTable = Objects.requireNonNull(dbTable);
        this.alias = (null == alias) ? "" : alias;
        backingList = new ArrayList<>();
        joinMap = new HashMap<>();
        joins = Collections.unmodifiableMap(joinMap);
        readOnly = false;
        tableCount = 1;
        columnMap = new HashMap<>();
        resultColumns = Collections.unmodifiableMap(columnMap);
        if (null != toAdd)
            addAll(toAdd.iterator());
    }
    
    public DmlSelectQueryBuilder(DbTable dbTable, String alias) {
        this(dbTable, alias, null);
    }
    
    public DmlSelectQueryBuilder(DbTable dbTable, Iterable<DbColumn> toAdd) {
        this(dbTable, "", toAdd);
    }
    
    public DmlSelectQueryBuilder(DbTable dbTable) {
        this(dbTable, "", null);
    }

    @Override
    public synchronized void accept(StringBuffer t) {
        Iterator<ResultColumn> iterator = backingList.iterator();
        if (iterator.hasNext()) {
            t.append("SELECT ");
            iterator.next().accept(t);
            while (iterator.hasNext()) {
                iterator.next().accept(t.append(", "));
            }
            String n = dbTable.getDbName().toString();
            t.append("\n\tFROM ").append(n);
            if (!n.equals(getName()))
                t.append(" ").append(getName());
            joinMap.values().forEach((j) -> j.accept(t));
        }
    }
    
    public StringBuffer build() {
        StringBuffer result = new StringBuffer();
        accept(result);
        return result;
    }

    @Override
    public StringBuffer get() {
        return build();
    }
    
    private static final Logger LOG = Logger.getLogger(DmlSelectQueryBuilder.class.getName());
    
    private synchronized JoinedTable join(DmlSelectTable targetTable, HashMap<String, JoinedTable> targetMap, DbColumn joinFrom, TableJoinType type,
            DbColumn joinTo, String alias) {
        LOG.log(Level.INFO, String.format("Joining (%s) %s.%s to %s.%s", targetTable.getDbTable().getDbName(), joinFrom.getTable().getDbName(), joinFrom.getDbName(), joinTo.getTable().getDbName(), joinTo.getDbName()));
        if (readOnly)
            throw new UnsupportedOperationException("Builder is read-only");
        if (joinFrom.getTable() != targetTable.getDbTable())
            throw new IllegalArgumentException("That column does not exist on the current table");
        if (joinFrom.getType().getValueType() != joinTo.getType().getValueType())
            throw new IllegalArgumentException("Join-from and Join-to columns are not the same type");
        if (tableCount == 1 && backingList.stream().anyMatch((t) -> t instanceof ColumnCount))
            throw new UnsupportedOperationException("Table joins not supported with grouping columns");
        JoinedTable joinedTable = new JoinedTable(targetTable, joinFrom, type, joinTo, alias);
        String name = joinedTable.getName();
        String a = (this.alias.isEmpty()) ? this.dbTable.toString() : this.alias;
        if (a.equalsIgnoreCase(name) || getResultTableNames().anyMatch((t) -> t.equalsIgnoreCase(name))) {
            if (joinedTable.alias.isEmpty()) {
                joinedTable.alias = joinedTable.getDbTable().toString();
                if (joinedTable.alias.equalsIgnoreCase(name) || joinedTable.alias.equalsIgnoreCase(a) ||
                        getResultTableNames().anyMatch((t) -> t.equalsIgnoreCase(joinedTable.alias)))
                    throw new IllegalArgumentException("The default alias and table name are already being used");
            } else
                throw new IllegalArgumentException("That alias name is already being used");
        }
        this.alias = a;
        targetMap.put(joinedTable.getName(), joinedTable);
        tableCount++;
        return joinedTable;
    }
    
    private synchronized boolean add(DmlSelectTable targetTable, DbColumn column, String alias) {
        if (readOnly)
            throw new UnsupportedOperationException("Builder is read-only");
        LOG.log(Level.INFO, String.format("Adding %s.%s to %s", column.getTable().getDbName(), column.getDbName(), targetTable.getDbTable().getDbName()));
        
        if (targetTable.getDbTable() != column.getTable())
            throw new IllegalArgumentException("That column does not exist on the current table");
        TableColumn tableColumn = new TableColumn(targetTable, column, alias);
        String name = tableColumn.getName().toLowerCase();
        if (columnMap.containsKey(name))
            return false;
        backingList.add(tableColumn);
        columnMap.put(name, tableColumn);
        return true;
    }
    
    private synchronized boolean addCountOf(DmlSelectTable targetTable, DbColumn column, String alias) {
        if (readOnly)
            throw new UnsupportedOperationException("Builder is read-only");
        if (targetTable.getDbTable() != column.getTable())
            throw new IllegalArgumentException("That column does not exist on the current table");
        if (tableCount > 1)
            throw new UnsupportedOperationException("Grouping columns not supported with joined table queries");
        ColumnCount columnCount = new ColumnCount(targetTable, column, alias);
        String name = columnCount.getName().toLowerCase();
        if (columnMap.containsKey(name))
            return false;
        backingList.add(columnCount);
        columnMap.put(name, columnCount);
        return true;
    }
    
    private synchronized void changeColumnAlias(ResultColumn target, String newAlias) {
        if (readOnly)
            throw new UnsupportedOperationException("Builder is read-only");
        String name;
        String oldName = target.getName().toLowerCase();
        if (null == newAlias || newAlias.isEmpty()) {
            String oldAlias = target.alias;
            if (oldAlias.isEmpty())
                return;
            target.alias = "";
            name = target.getName().toLowerCase();
            if (columnMap.containsKey(name)) {
                target.alias = oldAlias;
                throw new UnsupportedOperationException("Duplicate names not supported");
            }
        } else if (newAlias.equalsIgnoreCase(oldName)) {
            target.alias = newAlias;
            return;
        } else {
            name = newAlias.toLowerCase();
            if (columnMap.containsKey(name))
                throw new UnsupportedOperationException("Duplicate names not supported");
            target.alias = newAlias;
        }
        columnMap.remove(name);
        columnMap.put(name, target);
    }
    
    @Override
    public DbTable getDbTable() {
        return dbTable;
    }

    @Override
    public synchronized String getName() {
        return (alias.isEmpty()) ? dbTable.getDbName().toString() : alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public synchronized void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public Map<String, JoinedTable> getJoins() {
        return joins;
    }

    public Map<String, ResultColumn> getResultColumns() {
        return resultColumns;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }
    
    public synchronized void makeReadOnly() {
        readOnly = true;
    }

    @Override
    public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, DbColumn ...toAdd) {
        return join(joinFrom, type, joinTo, (String)null, toAdd);
    }
    
    @Override
    public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, Stream<DbColumn> toAdd) {
        return join(joinFrom, type, joinTo, (String)null, toAdd);
    }
    
    @Override
    public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo) {
        return join(joinFrom, type, joinTo, (String)null);
    }
    
    @Override
    public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias, DbColumn ...toAdd) {
        JoinedTable result = join(this, joinMap, joinFrom, type, joinTo, alias);
        if (null != toAdd && toAdd.length > 0)
            result.addAll(toAdd);
        return result;
    }
    
    @Override
    public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias, Stream<DbColumn> toAdd) {
        JoinedTable result = join(this, joinMap, joinFrom, type, joinTo, alias);
        if (null != toAdd)
            result.addAll(toAdd);
        return result;
    }
    
    @Override
    public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias) {
        return join(this, joinMap, joinFrom, type, joinTo, alias);
    }
    
    @Override
    public DmlSelectQueryBuilder getBuilder() {
        return this;
    }

    @Override
    public boolean containsColumn(DbColumn column) {
        return backingList.stream().anyMatch((t) -> t.isMatch(column));
    }

    public Stream<String> getResultTableNames() {
        Stream.Builder<String> builder = Stream.builder();
        joinMap.keySet().forEach((t) -> {
            builder.accept(t);
            joinMap.get(t).buildTableNames(builder);
        });
        return builder.build();
    }
    
    @Override
    public boolean contains(Object o) {
        if (null == o)
            return false;
        if (o instanceof DbColumn)
            return containsColumn((DbColumn)o);
        if (o instanceof String)
            return contains((String)o);
        return backingList.contains(o);
    }

    @Override
    public boolean add(DbColumn column, String alias) {
        return add(this, column, alias);
    }
    
    @Override
    public boolean add(DbColumn column) {
        return add(this, column, null);
    }
    
    @Override
    public boolean addCountOf(DbColumn column, String alias) {
        return addCountOf(this, column, alias);
    }
    
    @Override
    public boolean addAll(DbColumn[] columns) {
        boolean modified = false;
        for (DbColumn c : columns) {
            if (null != c && add(this, c, null))
                modified = true;
        }
        return modified;
    }
    
    private  boolean addAll(Iterator<DbColumn> iterator) {
        boolean modified = false;
        while (iterator.hasNext()) {
            DbColumn c = iterator.next();
            if (null != c && add(this, c, null))
                modified = true;
        }
        return modified;
    }
    
    @Override
    public boolean addAll(Stream<DbColumn> columns) {
        return addAll(columns.iterator());
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
    public Iterator<ResultColumn> iterator() {
        return listIterator(0);
    }

    @Override
    public Object[] toArray() {
        return backingList.toArray();
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T> T[] toArray(T[] a) {
        return backingList.toArray(a);
    }

    @Override
    public boolean add(ResultColumn e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backingList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ResultColumn> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends ResultColumn> c) {
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
    public ResultColumn get(int index) {
        return backingList.get(index);
    }

    @Override
    public ResultColumn set(int index, ResultColumn element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, ResultColumn element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultColumn remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return backingList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backingList.lastIndexOf(o);
    }

    @Override
    public ListIterator<ResultColumn> listIterator() {
        return listIterator(0);
    }

    private synchronized ResultColumn iteratorGet(int index) {
        if (index >= 0 && index < backingList.size())
            return backingList.get(index);
        return null;
    }
    @Override
    public ListIterator<ResultColumn> listIterator(int index) {
        if (index < 0 || index > backingList.size())
            throw new ArrayIndexOutOfBoundsException();
        return new ListIterator<ResultColumn>() {
            int currentIndex = index;
            @Override
            public boolean hasNext() {
                return currentIndex < backingList.size();
            }

            @Override
            public ResultColumn next() {
                ResultColumn result = iteratorGet(currentIndex);
                if (null == result)
                    throw new NoSuchElementException();
                currentIndex++;
                return result;
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex > 0;
            }

            @Override
            public ResultColumn previous() {
                ResultColumn result = iteratorGet(currentIndex - 1);
                while (null == result) {
                    if (currentIndex <= backingList.size())
                        throw new NoSuchElementException();
                    currentIndex = backingList.size();
                    result = iteratorGet(currentIndex - 1);
                }
                return result;
            }

            @Override
            public int nextIndex() {
                if (currentIndex > backingList.size())
                    currentIndex = backingList.size();
                return currentIndex;
            }

            @Override
            public int previousIndex() {
                if (currentIndex > backingList.size())
                    currentIndex = backingList.size();
                return currentIndex - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("List is read-only.");
            }

            @Override
            public void set(ResultColumn e) {
                throw new UnsupportedOperationException("List is read-only.");
            }

            @Override
            public void add(ResultColumn e) {
                throw new UnsupportedOperationException("List is read-only.");
            }
            
        };
    }

    @Override
    public List<ResultColumn> subList(int fromIndex, int toIndex) {
        ResultColumn[] arr = toArray(new ResultColumn[0]);
        return ReadOnlyList.of((fromIndex == 0 && toIndex == arr.length) ? arr : Arrays.copyOfRange(arr, fromIndex, toIndex));
    }

    public class JoinedTable implements DmlSelectTable {
        private final DmlSelectTable joinedFromTable;
        private final DbColumn joinedFrom;
        private final TableJoinType joinType;
        private final DbColumn joinedTo;
        private String alias;
        private final HashMap<String, JoinedTable> joinMap;
        private final Map<String, JoinedTable> joins;

        private JoinedTable(DmlSelectTable joinedFromTable, DbColumn joinedFrom, TableJoinType joinType, DbColumn joinedTo, String alias) {
            this.joinedFromTable = Objects.requireNonNull(joinedFromTable);
            this.joinedFrom = Objects.requireNonNull(joinedFrom);
            this.joinType = Objects.requireNonNull(joinType);
            this.joinedTo = Objects.requireNonNull(joinedTo);
            this.alias = (null == alias || alias.isEmpty()) ? joinedTo.getTable().toString() : alias;
            joinMap = new HashMap<>();
            joins = Collections.unmodifiableMap(joinMap);
        }

        public DbColumn getJoinedFrom() {
            return joinedFrom;
        }

        public DmlSelectTable getJoinedFromTable() {
            return joinedFromTable;
        }

        public TableJoinType getJoinType() {
            return joinType;
        }

        public DbColumn getJoinedTo() {
            return joinedTo;
        }
        
        @Override
        public DbTable getDbTable() {
            return joinedTo.getTable();
        }

        @Override
        public String getName() {
            return (alias.isEmpty()) ? joinedTo.getTable().getDbName().toString() : alias;
        }

        @Override
        public String getAlias() {
            return alias;
        }

        @Override
        public Map<String, JoinedTable> getJoins() {
            return joins;
        }

        @Override
        public boolean isReadOnly() {
            return readOnly;
        }

        @Override
        public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, DbColumn ...toAdd) {
            return join(joinFrom, type, joinTo, (String)null, toAdd);
        }

        @Override
        public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, Stream<DbColumn> toAdd) {
            return join(joinFrom, type, joinTo, (String)null, toAdd);
        }
    
        @Override
        public JoinedTable join(DbColumn left, TableJoinType type, DbColumn right) {
            return DmlSelectQueryBuilder.this.join(left, type, right, (String)null);
        }

        @Override
        public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias, DbColumn ...toAdd) {
            JoinedTable result = DmlSelectQueryBuilder.this.join(this, joinMap, joinFrom, type, joinTo, alias);
            if (null != toAdd && toAdd.length > 0)
                result.addAll(toAdd);
            return result;
        }

        @Override
        public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias, Stream<DbColumn> toAdd) {
            JoinedTable result = DmlSelectQueryBuilder.this.join(this, joinMap, joinFrom, type, joinTo, alias);
            if (null != toAdd)
                result.addAll(toAdd);
            return result;
        }
    
        @Override
        public JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias) {
            return DmlSelectQueryBuilder.this.join(this, joinMap, joinFrom, type, joinTo, alias);
        }
    
        @Override
        public boolean add(DbColumn column, String alias) {
            return DmlSelectQueryBuilder.this.add(this, column, alias);
        }

        @Override
        public boolean add(DbColumn column) {
            return DmlSelectQueryBuilder.this.add(this, column, null);
        }

        @Override
        public boolean addCountOf(DbColumn column, String alias) {
            return DmlSelectQueryBuilder.this.addCountOf(this, column, alias);
        }
    
        @Override
        public boolean addAll(DbColumn[] columns) {
            boolean modified = false;
            for (DbColumn c : columns) {
                if (null != c && DmlSelectQueryBuilder.this.add(this, c, null))
                    modified = true;
            }
            return modified;
        }

        @Override
        public boolean addAll(Stream<DbColumn> columns) {
            boolean modified = false;
            Iterator<DbColumn> iterator = columns.iterator();
            while (iterator.hasNext()) {
                DbColumn c = iterator.next();
                if (null != c && DmlSelectQueryBuilder.this.add(this, c, null))
                    modified = true;
            }
            return modified;
        }

        @Override
        public boolean containsColumn(DbColumn column) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public DmlSelectQueryBuilder getBuilder() {
            return DmlSelectQueryBuilder.this;
        }

        private void buildTableNames(Stream.Builder<String> builder) {
            joinMap.keySet().forEach((t) -> {
                builder.accept(t);
                joinMap.get(t).buildTableNames(builder);
            });
        }

        private void accept(StringBuffer t) {
            String name = joinedTo.getTable().getDbName().toString();
            t.append("\n\t").append(joinType).append(" ").append(name);
            if (!getName().equals(name))
                t.append(" ").append(getName());
            t.append(" ON ").append(joinedFromTable.getName()).append(".").append(joinedFrom).append("=").append(getName())
                    .append(".").append(joinedTo);
            joinMap.values().forEach((j) -> j.accept(t));
        }
        
    }
    
    public abstract class ResultColumn {
        private String alias;
        private final DmlSelectTable resultTable;

        public DmlSelectTable getResultTable() {
            return resultTable;
        }
        
        public abstract String getDbName();
        
        protected String getDefaultAlias() { return getDbName(); }
        
        public String getName() {
            return (alias.isEmpty()) ? getDbName() : alias;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            changeColumnAlias(this, alias);
        }

        protected ResultColumn(DmlSelectTable owner, String alias) {
            this.resultTable = Objects.requireNonNull(owner);
            this.alias = (null == alias) ? "" : alias;
        }
        
        protected abstract boolean isMatch(DbColumn column);

        @Override
        public int hashCode() {
            return getName().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof ResultColumn && obj == this;
        }

        protected abstract void accept(StringBuffer t);
        
    }
    
    public final class ColumnCount extends ResultColumn {
        private final DbColumn dbColumn;
        
        private ColumnCount(DmlSelectTable owner, DbColumn dbColumn, String alias) {
            super(owner, (null == alias || alias.isEmpty()) ? dbColumn.toString() : alias);
            this.dbColumn = dbColumn;
        }

        @Override
        public String getDbName() {
            return dbColumn.getDbName().toString();
        }

        @Override
        protected String getDefaultAlias() {
            return dbColumn.toString();
        }
        
        public DbColumn getDbColumn() {
            return dbColumn;
        }

        @Override
        protected boolean isMatch(DbColumn column) {
            return column == dbColumn;
        }

        @Override
        protected void accept(StringBuffer t) {
            t.append("COUNT(");
            if (tableCount > 1)
                t.append(getResultTable().getName()).append(".");
            t.append(dbColumn.getDbName()).append(") AS ").append(getName());
        }
        
    }
    public final class TableColumn extends ResultColumn {
        private final DbColumn dbColumn;
        
        private TableColumn(DmlSelectTable owner, DbColumn dbColumn, String alias) {
            super(owner, (null == alias || alias.isEmpty()) ? dbColumn.toString() : alias);
            this.dbColumn = dbColumn;
        }
        
        @Override
        public String getDbName() {
            return dbColumn.getDbName().toString();
        }
        
        @Override
        protected String getDefaultAlias() {
            return dbColumn.toString();
        }
        
        public DbColumn getDbColumn() {
            return dbColumn;
        }

        @Override
        protected boolean isMatch(DbColumn column) {
            return column == dbColumn;
        }

        @Override
        protected void accept(StringBuffer t) {
            if (tableCount > 1)
                t.append(getResultTable().getName()).append(".").append(dbColumn.getDbName()).append(" AS ").append(getName());
            else if (dbColumn.getDbName().toString().equals(getName()))
                t.append(getName());
            else
                t.append(dbColumn.getDbName()).append(" AS ").append(getName());
        }
        
    }
    
}
