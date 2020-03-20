package scheduler.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the database table name associated with the class. This is used by {@link scheduler.dao.DataObjectImpl} for database operations.
 *
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName_old {

    /**
     * Get the name of the database table associated with the class.
     *
     * @return the name of the database table associated with the class.
     */
    public String value();
}
