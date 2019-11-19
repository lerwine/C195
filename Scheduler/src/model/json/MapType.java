/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.json;

/**
 * Specifies how a field value is converted to a JSON value.
 * @author Leonard T. Erwine
 */
public enum MapType {
    /**
     * Mapping type is to be inferred from the field type.
     * All optional values that are not present are treated as undefined values.
     */
    INFERRED,
    
    /**
     * Field type is {@link String} or {@link Optional<String>}.
     * If the value is not present, then it is treated as an undefined value.
     */
    STRING,
    
    /**
     * Field type is {@link String} or {@link Optional<String>}.
     * If value is not present or it is an empty string, then it treated as an undefined value.
     */
    NONEMPTY,
    
    /**
     * Field type is {@link LocalDateTime} or {@link Optional<LocalDateTime>}.
     * If the value is not present, then it is treated as an undefined value.
     */
    DATETIME,
    
    /**
     * Field type is {@link Integer} or {@link Optional<Integer>}.
     * If the value is not present, then it is treated as an undefined value.
     */
    INTEGER,

    /**
     * Field type is {@link Integer} or {@link Optional<Integer>}.
     * If the value is not present or zero, then it is treated as an undefined value.
     */
    NONZERO,
    
    /**
     * Field type is {@link Boolean} or {@link Optional<Boolean>}.
     * If the value is not present, then it is treated as an undefined value.
     */
    BOOLEAN,
    
    /**
     * Field type is {@link Boolean} or {@link Optional<Boolean>}.
     * If the value is not present or {@code false}, then it is treated as an undefined value.
     */
    TRUE,
    
    /**
     * Field type is {@link Boolean} or {@link Optional<Boolean>}.
     * If the value is not present or {@code true}, then it is treated as an undefined value.
     */
    FALSE
}
