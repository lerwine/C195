/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

/**
 * Defines the usage of a field in INSERT and UPDATE queries.
 * This is used by the {@link Mode} annotation.
 * @author Leonard T. Erwine
 */
public enum QueryMode {
    /**
     * Field is included in both INSERT and UPDATE queries.
     */
    All,
    
    /**
     * Field is not included in UPDATE queries.
     */
    InsertOnly,
    
    /**
     * Field is included in neither INSERT nor UPDATE queries.
     */
    ReadOnly
}
