/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

/**
 * Represents a data row from the country data table.
 * 
 * @author erwinel
 */
public interface Country extends DataObject {
    
    /**
     * Gets the name of the current country.
     * 
     * @return The name of the current country.
     */
    String getName();

}
