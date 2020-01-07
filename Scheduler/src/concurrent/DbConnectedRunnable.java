/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;

/**
 * Functionally similar to {@link java.lang.Runnable},
 * but with a single argument, which is a successfully opened
 * {@link java.sql.Connection}.
 *
 * @author erwinel
 */
@FunctionalInterface
public interface DbConnectedRunnable {
    
    public void run(Connection c) throws Exception;
    
}
