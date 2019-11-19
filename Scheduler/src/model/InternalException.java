/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * An exception intended to be thrown when validating abstract and overloaded return values from a parent class,
 * suggesting that there may be an unexpected bug in the extending class.
 * @author Leonard T. Erwine
 */
public class InternalException extends RuntimeException {
    /**
     * Constructs a new internal exception with {@code null} as its detail message.
     */
    public InternalException() { super(); }
    
    /**
     * Constructs a new internal exception with the specified detail message.
     * @param message   The detail message.
     */
    public InternalException(String message) { super(message); }
    
    /**
     * Constructs a new internal exception with the specified detail message and cause.
     * @param message   The detail message.
     * @param cause     The cause for this exception.
     */
    public InternalException(String message, Throwable cause) { super(message, cause); }
    
    /**
     * Constructs a new internal exception with the specified cause, where the detail message is derived from the cause.
     * @param cause 
     */
    public InternalException(Throwable cause) { super(cause); }
    
    /**
     * Constructs a new internal exception with the specified detail message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     * @param message               The detail message.
     * @param cause                 The cause for this exception.
     * @param enableSuppression     Whether or not suppression is enabled.
     * @param writableStackTrace    Whether or not the stack trace should be writable.
     */
    protected InternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
