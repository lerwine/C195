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
 * Declares a class as being mapped to a row in a database table or view.
 * This annotation is for classes that are derived from the {@link Record} class.
 * Individual target object fields annotated by {@link Column} are mapped to database columns.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * Specifies the name of the database table or view.
     * @return The name of the database table or view.
     */
    public String name();
    
    /**
     * Specifies the name of the primary key column in the associated table or view.
     * The value of the primary key will be stored in the {@link Record#id} field.
     * @return The name of the primary key column in the associated result table or view.
     */
    public String pk();
    
    /**
     * Indicates whether the database source is read-only (such as views).
     * The default value is {@code false}.
     * @return {@code true} if the database source is read-only; otherwise, {@code false}. 
     */
    public boolean readOnly() default false;
}
