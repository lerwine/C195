package scheduler.util;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <R> The result type.
 */
@FunctionalInterface
public interface ResultSetFunction<R> {

    /**
     * Applies this function to the given argument.
     *
     * @param rs the result set.
     * @return the function result
     * @throws java.sql.SQLException if unable to create the result.
     */
    R apply(ResultSet rs) throws SQLException;
}
