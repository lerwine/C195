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
 * Specifies how the annotated field is used in INSERT and UPDATE queries.
 * This annotation is for fields that also have the {@link Column} annotation.
 * @author Leonard T. Erwine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mode {
    /**
     * A {@link QueryMode} value to indicate how the annotated field is used in INSERT and UPDATE queries.
     * @return A {@link QueryMode} value that indicates how the annotated field is used in INSERT and UPDATE queries.
     */
    QueryMode value();
}
