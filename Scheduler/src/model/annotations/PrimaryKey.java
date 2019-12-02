package model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    public String value();
}
