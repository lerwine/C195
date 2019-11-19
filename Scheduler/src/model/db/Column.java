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
 * Indicates that a field is mapped to a database column from a database table or view..
 * Use the {@link ValueMap} annotation to explicitly specify the value mapping type.
 * This annotation is for fields of classes that are derived from the {@link Record} class.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * The name of the result set column.
     * If this is empty or a value is not provided, then it will be assumed that the associated database column
     * has the same name as the annotated field.
     * @return The name of the database column or empty if the database column has the same name as the annotated field.
     */
    public String value() default "";
}
