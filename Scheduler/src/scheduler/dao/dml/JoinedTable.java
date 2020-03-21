/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;

/**
 * Represents a table that was joined to another table.
 * 
 * @author lerwi
 * @param <T> The type of {@link JoinedTable} that references other joined tables.
 * This is typically a self-reference type when implemented in concrete classes.
 */
// TODO: Replace with ParentJoinedTable and/or ChildJoinedTable?
public interface JoinedTable<T extends JoinedTable<T>> extends JoinableTable<T> {
    
    /**
     * Gets the parent table of the join relationship.
     * 
     * @return The parent table of the join relationship.
     */
    JoinableTable<T> getParentTable();

    /**
     * Gets the primary table for all join relationships.
     * 
     * @return The primary table for all join relationships.
     */
    JoinableTable<T> getPrimaryTable();

    /**
     * Gets the parent column for the join relationship.
     * 
     * @return The parent column for the join relationship.
     */
    DbColumn getParentColumn();

    /**
     * Gets the child column for the join relationship.
     * 
     * @return The child column for the join relationship.
     */
    DbColumn getChildColumn();

    /**
     *
     * @return
     */
    TableJoinType getType();
    
}
