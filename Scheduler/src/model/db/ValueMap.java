/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Explicitly defines how the value an object field is translated to and from the database column value.
 * This annotation is for fields that also have the {@link Column} annotation.
 * If this annotation is not used, then the value type mapping is inferred from the field type. 
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValueMap {
    /**
     * A {@link MapType} value to define how a field value is mapped to and from the database column value.
     * @return A {@link MapType} value that defines how a field value is mapped to and from the database column value.
     */
    public MapType value();
}
