package scheduler.dao.schema;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;
import scheduler.util.MapHelper;

/**
 *
 * @author lerwi
 */
public class SchemaHelper {
    private static final Map<String, DbName> DB_NAME_MAP;
    private static final Map<DbName, DbTable> TABLE_NAME_MAP;
    private static final Map<DbTable, Map<DbName, DbColumn>> TABLE_COLUMN_MAPPINGS;
    private static final Map<DbTable, List<DbColumn>> COLUMNS_BY_TABLE;
    private static final Map<DbTable, DbColumn> PRIMARY_KEY_BY_TABLE;
    private static final Map<DbColumn, Map<DbName, DbColumn>> REFERENCED_COLUMN_MAPPINGS;
    private static final Map<DbColumn, Map<DbName, DbColumn>> REFERRING_COLUMN_MAPPINGS;
    
    static {
        DB_NAME_MAP = Collections.unmodifiableMap(MapHelper.toMap(DbName.values(), (t) -> t.getValue().toLowerCase()));
        TABLE_NAME_MAP = Collections.unmodifiableMap(MapHelper.toMap(DbTable.values(), (t) -> t.getDbName()));
        COLUMNS_BY_TABLE = Collections.unmodifiableMap(
                MapHelper.remap(
                        MapHelper.groupMap(DbColumn.values(), (t) -> t.getTable()),
                        (t) -> Collections.unmodifiableList(t)
                )
        );
        TABLE_COLUMN_MAPPINGS = Collections.unmodifiableMap(MapHelper.remap(COLUMNS_BY_TABLE, (t) -> MapHelper.toMap(t, (u )-> u.getDbName())));
        PRIMARY_KEY_BY_TABLE = Collections.unmodifiableMap(MapHelper.remap(COLUMNS_BY_TABLE, (t, u) -> {
            DbName n = t.getPkColName();
            return u.stream().filter((s) -> s.getDbName() == n).findFirst().get();
        }));
        REFERENCED_COLUMN_MAPPINGS = Collections.unmodifiableMap(MapHelper.toMap(Arrays.stream(DbColumn.values()).filter((t) ->
                !t.getForeignKeys().isEmpty()),
                (DbColumn t, Map<DbColumn, Map<DbName, DbColumn>> u) ->
                        u.put(t, Collections.unmodifiableMap(
                                MapHelper.toMap(t.getForeignKeys(),
                                        (ForeignKey f) -> f.getColumnName(),
                                        (ForeignKey f) -> TABLE_COLUMN_MAPPINGS.get(f.getTable()).get(f.getColumnName())
                                )
                        ))
        ));
        REFERRING_COLUMN_MAPPINGS = Collections.unmodifiableMap(
                MapHelper.remap(MapHelper.toMap(REFERENCED_COLUMN_MAPPINGS.keySet(),
                        (DbColumn t, Map<DbColumn, HashMap<DbName, DbColumn>> u) -> {
                            Map<DbName, DbColumn> m = REFERENCED_COLUMN_MAPPINGS.get(t);
                            m.keySet().forEach((k) -> {
                                DbColumn p = m.get(k);
                                if (u.containsKey(p))
                                    u.get(p).put(t.getDbName(), t);
                                else {
                                    HashMap<DbName, DbColumn> h = new HashMap<>();
                                    h.put(t.getDbName(), t);
                                    u.put(p, h);
                                }
                            });
                        }),
                        (t) -> Collections.unmodifiableMap(t)
                )
        );
    }

    /**
     * Converts a string value to a {@link DbName} {@code enum} value.
     * 
     * @param name The string value to convert.
     * @return The equivalent {@link DbName} {@code enum} value.
     * @throws NoSuchElementException if no {@link DbName} matches the given {@code name}.
     */
    public static DbName toDbName(String name) {
        name = name.toLowerCase();
        if (DB_NAME_MAP.containsKey(name))
            return DB_NAME_MAP.get(name);
        throw new NoSuchElementException();
    }
    
    public static boolean isDbTable(DbName name) {
        return TABLE_NAME_MAP.containsKey(name);
    }
    
    public static DbTable toDbTable(DbName name) {
        if (TABLE_NAME_MAP.containsKey(name))
            return TABLE_NAME_MAP.get(name);
        throw new NoSuchElementException();
    }
    
    public static DbColumn getPrimaryKey(DbTable table) {
        return PRIMARY_KEY_BY_TABLE.get(table);
    }
    
    public static DbColumn getDbColumn(DbTable table, DbName name) {
        Map<DbName, DbColumn> map = TABLE_COLUMN_MAPPINGS.get(table);
        if (map.containsKey(name))
            return map.get(name);
        throw new NoSuchElementException();
    }
    
    public static Map<DbName, DbColumn> getTableColumnMap(DbTable table) {
        return TABLE_COLUMN_MAPPINGS.get(table);
    }
    
    public static List<DbColumn> getTableColumns(DbTable table) {
        return COLUMNS_BY_TABLE.get(table);
    }
    
    public static Stream<DbColumn> getTableColumns(DbTable table, Predicate<DbColumn> predicate) {
        return COLUMNS_BY_TABLE.get(table).stream().filter(predicate);
    }
    
    public static Stream<DbColumn> getColumns(Predicate<DbColumn> predicate) {
        return Arrays.stream(DbColumn.values()).filter(predicate);
    }
    
    /**
     * Tests whether a column is suitable for inclusion in table joins.
     * 
     * @param column The column to test.
     * @return {@code true} if {@link DbColumn#getUsage()} is {@link ColumnUsage#DATA}, {@link ColumnUsage#UNIQUE_KEY} or
     * {@link ColumnUsage#CRYPTO_HASH}; otherwise, {@code false}.
     */
    public static boolean isEntityData(DbColumn column) {
        switch (column.getUsage()) {
            case CRYPTO_HASH:
            case DATA:
            case UNIQUE_KEY:
                return true;
        }
        return false;
    }

    public static Collection<DbColumn> getReferencedColumns(DbColumn column) {
        if (REFERENCED_COLUMN_MAPPINGS.containsKey(column))
            return REFERENCED_COLUMN_MAPPINGS.get(column).values();
        return Collections.emptySet();
    }
    
    public static Collection<DbColumn> getReferencingColumns(DbColumn column) {
        if (REFERRING_COLUMN_MAPPINGS.containsKey(column))
            return REFERRING_COLUMN_MAPPINGS.get(column).values();
        return Collections.emptySet();
    }
    
    public static DbTable getReferencedTable(DbColumn column, DbName foreignKeyColName) {
        if (REFERENCED_COLUMN_MAPPINGS.containsKey(column)) {
            Map<DbName, DbColumn> map = REFERENCED_COLUMN_MAPPINGS.get(column);
            if (map.containsKey(foreignKeyColName))
                return map.get(foreignKeyColName).getTable();
        }
        throw new NoSuchElementException();
    }
    
    public static DbTable getReferringTable(DbColumn foreignKeyColumn, DbName colName) {
        if (REFERRING_COLUMN_MAPPINGS.containsKey(foreignKeyColumn)) {
            Map<DbName, DbColumn> map = REFERRING_COLUMN_MAPPINGS.get(foreignKeyColumn);
            if (map.containsKey(colName))
                return map.get(colName).getTable();
        }
        throw new NoSuchElementException();
    }
    
    public static boolean areColumnsRelated(DbColumn a, DbColumn b) {
        DbName n;
        Map<DbName, DbColumn> map;
        if (REFERENCED_COLUMN_MAPPINGS.containsKey(a)) {
            map = REFERENCED_COLUMN_MAPPINGS.get(a);
            n = b.getDbName();
            if (map.containsKey(n) && map.get(n) == b)
                return true;
        }
        if (REFERRING_COLUMN_MAPPINGS.containsKey(b)) {
            map = REFERENCED_COLUMN_MAPPINGS.get(b);
            n = a.getDbName();
            return map.containsKey(n) && map.get(n) == a;
        }
        return false;
    }
}
