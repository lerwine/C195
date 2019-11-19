/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Leonard T. Erwine
 */
public class InvalidOperationException extends Exception {
    /**
     * Constructs a new invalid operation exception with {@code null} as its detail message.
     */
    public InvalidOperationException() { super(); }
    
    /**
     * Constructs a new invalid operation exception with the specified detail message.
     * @param message   The name of the parameter.
     */
    public InvalidOperationException(String message) { super(message); }
    
    /**
     * Constructs a new invalid operation exception with the specified detail message and cause.
     * @param message   The name of the parameter.
     * @param cause     The cause for this exception.
     */
    public InvalidOperationException(String message, Throwable cause) { super(message, cause); }
    
    /**
     * Constructs a new invalid operation exception with the specified cause, where the detail message is derived from the cause.
     * @param cause 
     */
    public InvalidOperationException(Throwable cause) { super(cause); }
    
    /**
     * Constructs a new invalid operation exception with the specified detail message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     * @param message               The detail message.
     * @param cause                 The cause for this exception.
     * @param enableSuppression     Whether or not suppression is enabled.
     * @param writableStackTrace    Whether or not the stack trace should be writable.
     */
    protected InvalidOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
