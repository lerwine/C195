package scheduler.dao.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the {@link DbTable} associated with a class.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DatabaseTable {
    
    /**
     * Gets the {@link DbTable} associated with the annotated class.
     *
     * @return The {@link DbTable} associated with the annotated class.
     */
    public DbTable value();
    
}
