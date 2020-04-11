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
 * Utility class for getting database schema-related information.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class SchemaHelper {

    private static final Map<String, DbName> DB_NAME_MAP;
    private static final Map<DbColumn, Boolean> AUDIT_MAP;
    private static final Map<DbName, DbTable> TABLE_NAME_MAP;
    private static final Map<DbTable, Map<DbName, DbColumn>> TABLE_COLUMN_MAPPINGS;
    private static final Map<DbTable, List<DbColumn>> COLUMNS_BY_TABLE;
    private static final Map<DbTable, DbColumn> PRIMARY_KEY_BY_TABLE;
    private static final Map<DbColumn, Map<DbName, DbColumn>> REFERENCED_COLUMN_MAPPINGS;
    private static final Map<DbColumn, Map<DbName, DbColumn>> REFERRING_COLUMN_MAPPINGS;

    static {
        DB_NAME_MAP = Collections.unmodifiableMap(MapHelper.toMap(DbName.values(), (t) -> t.toString().toLowerCase()));
        TABLE_NAME_MAP = Collections.unmodifiableMap(MapHelper.toMap(DbTable.values(), (t) -> t.getDbName()));
        COLUMNS_BY_TABLE = Collections.unmodifiableMap(
                MapHelper.remap(
                        MapHelper.groupMap(DbColumn.values(), (t) -> t.getTable()),
                        (t) -> Collections.unmodifiableList(t)
                )
        );
        TABLE_COLUMN_MAPPINGS = Collections.unmodifiableMap(MapHelper.remap(COLUMNS_BY_TABLE, (t) -> MapHelper.toMap(t, (u) -> u.getDbName())));
        PRIMARY_KEY_BY_TABLE = Collections.unmodifiableMap(MapHelper.remap(COLUMNS_BY_TABLE, (t, u) -> {
            DbName n = t.getPkColName();
            return u.stream().filter((s) -> s.getDbName() == n).findFirst().get();
        }));

        AUDIT_MAP = MapHelper.toMap(Arrays.stream(DbColumn.values()).filter((t) -> t.getUsageCategory() == ColumnCategory.AUDIT),
                (t, u) -> u.put(t, t.getDbName() == DbName.CREATED_BY || t.getDbName() == DbName.CREATE_DATE));

        REFERENCED_COLUMN_MAPPINGS = Collections.unmodifiableMap(MapHelper.toMap(Arrays.stream(DbColumn.values()).filter((t)
                -> !t.getForeignKeys().isEmpty()),
                (DbColumn t, Map<DbColumn, Map<DbName, DbColumn>> u)
                -> u.put(t, Collections.unmodifiableMap(
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
                                if (u.containsKey(p)) {
                                    u.get(p).put(t.getDbName(), t);
                                } else {
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
        if (DB_NAME_MAP.containsKey(name)) {
            return DB_NAME_MAP.get(name);
        }
        throw new NoSuchElementException();
    }

    /**
     * Checks whether a {@link DbName} value refers to a {@link DbTable}.
     *
     * @param name The {@link DbName} value to check.
     * @return {@code true} if {@code name} matches a {@link DbTable#dbName} value; otherwise, {@code false}.
     */
    public static boolean isDbTable(DbName name) {
        return TABLE_NAME_MAP.containsKey(name);
    }

    /**
     * Gets the {@link DbTable} value that matches a given {@link DbName} value.
     *
     * @param name The {@link DbName} of the table to search for.
     * @return The {@link DbTable} value that matches the given {@code name}.
     * @throws NoSuchElementException if {@code name} does not match any {@link DbTable#dbName} value.
     */
    public static DbTable toDbTable(DbName name) {
        if (TABLE_NAME_MAP.containsKey(name)) {
            return TABLE_NAME_MAP.get(name);
        }
        throw new NoSuchElementException();
    }

    /**
     * Gets the primary key column for the specified data table.
     *
     * @param table The {@link DbTable} value representing the target data table definition.
     * @return The {@link DbColumn} that refers to the primary key of the given {@code table}.
     */
    public static DbColumn getPrimaryKey(DbTable table) {
        return PRIMARY_KEY_BY_TABLE.get(table);
    }

    /**
     * Gets the {@link DbColumn} value for a data table that matches a given {@link DbName} value.
     *
     * @param table The {@link DbTable} value representing the target data table definition.
     * @param name The {@link DbName} of the column to search for.
     * @return The {@link DbColumn} value of the specified data {@code table} that matches the given {@code name}.
     * @throws NoSuchElementException if {@code name} does not match any {@link DbColumn#dbName} value for the columns of the specified data
     * {@code table}.
     */
    public static DbColumn getDbColumn(DbTable table, DbName name) {
        Map<DbName, DbColumn> map = TABLE_COLUMN_MAPPINGS.get(table);
        if (map.containsKey(name)) {
            return map.get(name);
        }
        throw new NoSuchElementException();
    }

    /**
     * Gets a {@link Map} to look up {@code DbColumn} values by their {@link DbColumn#dbName} for a given {@link DbTable}.
     *
     * @param table The {@link DbTable} value representing the target data table definition.
     * @return A read-only {@link Map} where the {@link DbName} keys map to the {@link DbColumn}s of the specified data {@code table}.
     */
    public static Map<DbName, DbColumn> getTableColumnMap(DbTable table) {
        return TABLE_COLUMN_MAPPINGS.get(table);
    }

    /**
     * Gets all {@link DbColumn}s for a specified {@link DbTable}.
     *
     * @param table The {@link DbTable} value representing the target database table.
     * @return A read-only {@link List} of all {@link DbColumn}s for the given data {@code table}.
     */
    public static List<DbColumn> getTableColumns(DbTable table) {
        return COLUMNS_BY_TABLE.get(table);
    }

    /**
     * Gets a filtered {@link Stream} of {@link DbColumn}s for a specified {@link DbTable}.
     *
     * @param table The {@link DbTable} value representing the target database table.
     * @param predicate A {@link Predicate} that determines which {@link DbColumn}s to include in the results.
     * @return A {@link Stream} of {@link DbColumn}s from the specified data {@code table} where the given {@code predicate} returned a {@code true}
     * value.
     */
    public static Stream<DbColumn> getTableColumns(DbTable table, Predicate<DbColumn> predicate) {
        return COLUMNS_BY_TABLE.get(table).stream().filter(predicate);
    }

    /**
     * Gets a filtered {@link Stream} of all known {@link DbColumn}s.
     *
     * @param predicate A {@link Predicate} that determines which {@link DbColumn}s to include in the results.
     * @return A {@link Stream} of all {@link DbColumn}s where the given {@code predicate} returned a {@code true} value.
     */
    public static Stream<DbColumn> getColumns(Predicate<DbColumn> predicate) {
        return Arrays.stream(DbColumn.values()).filter(predicate);
    }

    /**
     * Test whether a column can be included in an UPDATE statement.
     *
     * @param column The {@link DbColumn} to be tested.
     * @return {@code true} if the {@link DbColumn} can be included in an update statement; otherwise, {@code false}.
     */
    public static boolean isUpdatable(DbColumn column) {
        return !(column.getUsageCategory() == ColumnCategory.PRIMARY_KEY || (AUDIT_MAP.containsKey(column) && AUDIT_MAP.get(column)));
    }

    /**
     * Tests whether a column is one that stores user-provided data, excluding foreign key values.
     *
     * @param column The column to test.
     * @return {@code true} if {@link DbColumn#getUsageCategory()} is {@link ColumnCategory#DATA}, {@link ColumnCategory#UNIQUE_KEY} or
     * {@link ColumnCategory#CRYPTO_HASH}; otherwise, {@code false}.
     */
    public static boolean isEntityData(DbColumn column) {
        switch (column.getUsageCategory()) {
            case DATA:
            case UNIQUE_KEY:
            case CRYPTO_HASH:
                return true;
        }
        return false;
    }

    /**
     * Tests whether a column is suitable for inclusion in table joins.
     *
     * @param column The column to test.
     * @return {@code true} if {@link DbColumn#getUsageCategory()} is {@link ColumnCategory#DATA}, {@link ColumnCategory#UNIQUE_KEY} or
     * {@link ColumnCategory#FOREIGN_KEY}; otherwise, {@code false}.
     */
    public static boolean isForJoinedData(DbColumn column) {
        switch (column.getUsageCategory()) {
            case DATA:
            case UNIQUE_KEY:
            case FOREIGN_KEY:
                return true;
        }
        return false;
    }

    /**
     * Gets the columns that are referenced in a foreign-key relationship to the given column.
     *
     * @param column The {@link DbColumn} value representing a data column that may refer to other columns.
     * @return A read-only {@link Collection} of {@link DbColumn} values that represent the joining column for the {@link DbTable}s that the given
     * {@code column} refers to.
     */
    public static Collection<DbColumn> getReferencedColumns(DbColumn column) {
        if (REFERENCED_COLUMN_MAPPINGS.containsKey(column)) {
            return REFERENCED_COLUMN_MAPPINGS.get(column).values();
        }
        return Collections.emptySet();
    }

    /**
     * Gets the columns that refer to the given column in a foreign-key relationship.
     *
     * @param column The {@link DbColumn} value representing a data column that may referred to by other columns.
     * @return A read-only {@link Collection} of {@link DbColumn} values that represent the joining column for the {@link DbTable}s that refer to the
     * given {@code column}.
     */
    public static Collection<DbColumn> getReferencingColumns(DbColumn column) {
        if (REFERRING_COLUMN_MAPPINGS.containsKey(column)) {
            return REFERRING_COLUMN_MAPPINGS.get(column).values();
        }
        return Collections.emptySet();
    }

    public static DbTable getReferencedTable(DbColumn column, DbName foreignKeyColName) {
        if (REFERENCED_COLUMN_MAPPINGS.containsKey(column)) {
            Map<DbName, DbColumn> map = REFERENCED_COLUMN_MAPPINGS.get(column);
            if (map.containsKey(foreignKeyColName)) {
                return map.get(foreignKeyColName).getTable();
            }
        }
        throw new NoSuchElementException();
    }

    public static DbTable getReferringTable(DbColumn foreignKeyColumn, DbName colName) {
        if (REFERRING_COLUMN_MAPPINGS.containsKey(foreignKeyColumn)) {
            Map<DbName, DbColumn> map = REFERRING_COLUMN_MAPPINGS.get(foreignKeyColumn);
            if (map.containsKey(colName)) {
                return map.get(colName).getTable();
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Checks whether to columns are equal or are involved in the same foreign-key relationship.
     * 
     * @param a The first {@link DbColumn} to test.
     * @param b The second {@link DbColumn} to test.
     * @return {@code true} if both columns are equal or if they are both involved in the same foreign-key relationship.
     */
    public static boolean areColumnsRelated(DbColumn a, DbColumn b) {
        if (a == b) {
            return true;
        }
        DbName n;
        Map<DbName, DbColumn> map;
        if (REFERENCED_COLUMN_MAPPINGS.containsKey(a)) {
            map = REFERENCED_COLUMN_MAPPINGS.get(a);
            n = b.getDbName();
            if (map.containsKey(n) && map.get(n) == b) {
                return true;
            }
        }
        if (REFERENCED_COLUMN_MAPPINGS.containsKey(b)) {
            map = REFERENCED_COLUMN_MAPPINGS.get(b);
            n = a.getDbName();
            return map.containsKey(n) && map.get(n) == a;
        }
        return false;
    }

}
