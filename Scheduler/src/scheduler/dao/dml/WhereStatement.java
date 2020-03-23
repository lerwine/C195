package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Predicate;
import scheduler.dao.DataObject;

/**
 * Represents a statement within an SQL WHERE clause which can also filter corresponding data access objects.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 */
public interface WhereStatement<T extends DataObject> extends Predicate<T> {
    /**
     * Appends the current SQL statement to a {@link StringBuilder}.
     * This does not add any leading or trailing whitespace.
     * 
     * @param stringBuilder The {@link StringBuilder} to append to.
     */
    void appendSqlStatement(StringBuilder stringBuilder);
    
    /**
     * Applies any values to a {@link PreparedStatement} and returns the next index.
     * 
     * @param ps The {@link PreparedStatement} to apply values to.
     * @param currentIndex The parameter index for the next value to be applied.
     * @return The next parameter index, which was incremented for each value applied.
     * @throws java.sql.SQLException if unable to set statement values.
     */
    int applyValues(PreparedStatement ps, int currentIndex) throws SQLException;
}
