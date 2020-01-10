package scheduler.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the name of the primary key column for the associated table in the database.
 * This is used by {@link scheduler.dao.DataObjectImpl} for database operations.
 * The {@link TableName} annotation specifies the data table name.
 * 
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrimaryKey {
    /**
     * Gets the name of the primary key column for the associated table in the database.
     * 
     * @return The name of the primary key column for the associated table in the database.
     */
    public String value();
}
