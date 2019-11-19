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
 * Defines the property name for use with @{link java.beans.PropertyChangeSupport}.
 * This annotation is for fields that also have the {@link Column} annotation.
 * If this annotation is not used, then the property name is assumed to be the same as the field name.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyName {
    /**
     * Specifies name of the property for use with @{link java.beans.PropertyChangeSupport}.
     * @return The name of the property for use with @{link java.beans.PropertyChangeSupport}.
     */
    public String value();
}
