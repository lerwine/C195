package scheduler.model.statement;

import java.sql.Connection;
import java.sql.SQLException;
import scheduler.model.schema.IDbColumn;
import scheduler.model.schema.IDbSchema;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The schema type.
 * @param <U> The result type.
 */
public interface IDml<T extends IDbSchema<? extends IDbColumn<T>>, U> {

    /**
     * Gets the schema for the DML query.
     * 
     * @return The schema for the DML query.
     */
    T getSchema();
    
    /**
     * Executes the DML query.
     * 
     * @param connection The database connection to use.
     * @return The result value.
     * @throws java.sql.SQLException If unable to execute the DML query.
     */
    U execute(Connection connection) throws SQLException;
}
