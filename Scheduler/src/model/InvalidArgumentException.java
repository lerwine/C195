/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Exception thrown for invalid argument values.
 * @author Leonard T. Erwine
 */
public class InvalidArgumentException extends Exception {
    private final String paramName;
    
    public final String getParamName() { return this.paramName; }
    
    /**
     * Constructs a new invalid argument exception with {@code null} as its detail message.
     */
    public InvalidArgumentException() {
        super(); 
        this.paramName = "";
    }
    
    /**
     * Constructs a new invalid argument exception with the specified detail message.
     * @param paramName   The name of the parameter.
     */
    public InvalidArgumentException(String paramName) {
        this(paramName, (String)null);
    }
    
    /**
     * Constructs a new invalid argument exception with the specified detail message.
     * @param paramName   The name of the parameter.
     * @param message   The detail message.
     */
    public InvalidArgumentException(String paramName, String message) {
        super(((message == null || message.length() == 0) && paramName != null && paramName.length() > 0) ? "Parameter " + paramName + " is invalid" : message);
        this.paramName = (paramName == null) ? "" : paramName;
    }
    
    /**
     * Constructs a new invalid argument exception with the specified detail message and cause.
     * @param paramName   The name of the parameter.
     * @param cause     The cause for this exception.
     */
    public InvalidArgumentException(String paramName, Throwable cause) { this(paramName, cause, null); }
    
    /**
     * Constructs a new invalid argument exception with the specified detail message and cause.
     * @param paramName   The name of the parameter.
     * @param message   The detail message.
     * @param cause     The cause for this exception.
     */
    public InvalidArgumentException(String paramName, Throwable cause, String message) {
        super(((message == null || message.length() == 0) && paramName != null && paramName.length() > 0) ? "Parameter " + paramName + " is invalid" : message,
                cause);
        this.paramName = (paramName == null) ? "" : paramName;
    }
    
    /**
     * Constructs a new invalid argument exception with the specified cause, where the detail message is derived from the cause.
     * @param cause 
     */
    public InvalidArgumentException(Throwable cause) { this(null, cause); }
    
    /**
     * Constructs a new invalid argument exception with the specified detail message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     * @param message               The detail message.
     * @param cause                 The cause for this exception.
     * @param enableSuppression     Whether or not suppression is enabled.
     * @param writableStackTrace    Whether or not the stack trace should be writable.
     * @param paramName   The name of the parameter.
     */
    protected InvalidArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String paramName) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.paramName = (paramName == null) ? "" : paramName;
    }
}
