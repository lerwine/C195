/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 * An operation was attempted on an object in a state not performed by the method.
 * @author Leonard T. Erwine
 */
public class InvalidOperationException extends Exception {
    /**
     * Constructs a new invalid operation exception.
     */
    public InvalidOperationException() {
        super();
    }

    /**
     * Constructs a new invalid operation exception with the specified detail message.
     * @param message   The detail message.
     */
    public InvalidOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new invalid operation exception with the specified detail message and cause.
     * @param message   The detail message.
     * @param cause     The cause.
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
