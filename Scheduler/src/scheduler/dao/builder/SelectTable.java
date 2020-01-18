/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.builder;

/**
 *
 * @author erwinel
 */
public interface SelectTable {
    /**
     * Gets the name of the current database table, as defined in the database.
     * @return The name of the current database table, as defined in the database.
     */
    String getTableName();

    /**
     * Adds a LEFT JOIN clause.
     * @param foreignKeyColumn The column in the current table to be joined.
     * @param tableName The name of the table to be joined to.
     * @param refKeyColumn The column in the joining table that will be joined to the column in the current table.
     * @param joinAs The alias by which the table will be referred to when referencing column selections.
     * @return A {@link JoinedSelectTable} object that describes the joined table.
     */
    JoinedSelectTable leftJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs);

    /**
     * Adds a LEFT JOIN clause.
     * @param foreignKeyColumn The column in the current table to be joined.
     * @param tableName The name of the table to be joined to.
     * @param refKeyColumn The column in the joining table that will be joined to the column in the current table.
     * @return A {@link JoinedSelectTable} object that describes the joined table.
     */
    default JoinedSelectTable leftJoin(String foreignKeyColumn, String tableName, String refKeyColumn) {
        return leftJoin(foreignKeyColumn, tableName, refKeyColumn, tableName);
    }

    /**
     * Adds a INNER JOIN clause.
     * @param foreignKeyColumn The column in the current table to be joined.
     * @param tableName The name of the table to be joined to.
     * @param refKeyColumn The column in the joining table that will be joined to the column in the current table.
     * @param joinAs The alias by which the table will be referred to when referencing column selections.
     * @return A {@link JoinedSelectTable} object that describes the joined table.
     */
    JoinedSelectTable innerJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs);

    /**
     * Adds a INNER JOIN clause.
     * @param foreignKeyColumn The column in the current table to be joined.
     * @param tableName The name of the table to be joined to.
     * @param refKeyColumn The column in the joining table that will be joined to the column in the current table.
     * @return A {@link JoinedSelectTable} object that describes the joined table.
     */
    default JoinedSelectTable innerJoin(String foreignKeyColumn, String tableName, String refKeyColumn) {
        return innerJoin(foreignKeyColumn, tableName, refKeyColumn, tableName);
    }

    /**
     * Adds a RIGHT JOIN clause.
     * @param foreignKeyColumn The column in the current table to be joined.
     * @param tableName The name of the table to be joined to.
     * @param refKeyColumn The column in the joining table that will be joined to the column in the current table.
     * @param joinAs The alias by which the table will be referred to when referencing column selections.
     * @return A {@link JoinedSelectTable} object that describes the joined table.
     */
    JoinedSelectTable rightJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs);

    /**
     * Adds a RIGHT JOIN clause.
     * @param foreignKeyColumn The column in the current table to be joined.
     * @param tableName The name of the table to be joined to.
     * @param refKeyColumn The column in the joining table that will be joined to the column in the current table.
     * @return A {@link JoinedSelectTable} object that describes the joined table.
     */
    default JoinedSelectTable rightJoin(String foreignKeyColumn, String tableName, String refKeyColumn) {
        return rightJoin(foreignKeyColumn, tableName, refKeyColumn, tableName);
    }

    boolean containsJoin(String name, boolean includeNested);

    default boolean containsJoin(String name) { return SelectTable.this.containsJoin(name, false); }

    JoinedSelectTable getJoin(String name, boolean includeNested);

    default JoinedSelectTable getJoin(String name) { return getJoin(name, false); }

    void addColumn(String name, String alias);

    default void addColumn(String name) { addColumn(name, name); }
}
