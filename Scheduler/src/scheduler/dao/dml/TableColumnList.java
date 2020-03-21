/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import java.util.Optional;
import scheduler.util.ReadOnlyList;

/**
 *
 * @author lerwi
 * @param <E>
 */
public interface TableColumnList<E extends ColumnReference> extends TableReference, ReadOnlyList<E> {
    
    /**
     * Checks whether a column reference name is being used for any columns of the current list or in any child joins (if applicable).
     * 
     * @param name The column reference name to search for.
     * @return {@code true} if the column reference {@code name} is being used for any columns of the current list or in any child joins;
     * otherwise, {@code false}.
     */
    default boolean isColumnRefNameUsed(String name) {
        return null != name && !name.trim().isEmpty() && stream().anyMatch((t) -> t.getName().equalsIgnoreCase(name));
    }
    
    /**
     * Gets the {@link ColumnReference} that is referenced by a specified name within the current list or in any child joins (if applicable).
     * 
     * @param name The column reference name to search for.
     * @return The {@link ColumnReference} that is referenced by the specified {@code name} or {@code null} if no match was found within the
     * current list or in any child joins.
     */
    default E get(String name) {
        if (null != name && !name.trim().isEmpty()) {
            Optional<E> result = stream().filter((t) -> t.getName().equalsIgnoreCase(name)).findFirst();
            if (result.isPresent())
                return result.get();
        }
        return null;
    }
    
}
