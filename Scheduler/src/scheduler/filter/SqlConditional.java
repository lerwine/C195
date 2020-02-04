package scheduler.filter;

import java.util.function.Supplier;

/**
 * Defines an object that can produce a conditional SQL statement for use in a WHERE clause.
 * @author erwinel
 */
@FunctionalInterface
public interface SqlConditional extends Supplier<String> {

    /**
     * Indicates whether the current object represents a compound statement that should be enclosed in parenthesis when included with
     * other statements.
     * @return {@code true} if this represents a compound statement; otherwise {@code false}.
     */
    default boolean isCompound() { return false; }

    /**
     * Indicates whether this contains no conditional statement.
     * @return {@code true} if this contains no conditional statement; otherwise, {@code false}.
     */
    default boolean isEmpty() { return false; }
}
