/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Leonard T. Erwine
 */
public class InvalidArgumentException extends Exception {
    private final String name;
    
    /**
     * Gets the name of the invalid argument.
     * @return 
     */
    public final String getName() { return name; }
    
    /**
     * Constructs a new invalid argument exception with the specified argument name.
     * @param name  The name of the invalid argument.
     */
    public InvalidArgumentException(String name) {
        super((name == null || name.trim().length() == 0) ? "Invalid argument" : "Invalid argument: " + name);
        this.name = (name == null) ? "" : name;
    }

    /**
     * Constructs a new invalid argument exception with the specified argument name and detail message.
     * @param name      The name of the invalid argument.
     * @param message   The detail message.
     */
    public InvalidArgumentException(String name, String message) {
        super((message == null || message.trim().length() == 0) ? ((name == null || name.trim().length() == 0) ? "Invalid argument" : "Invalid argument: " + name) : message);
        this.name = (name == null) ? "" : name;
    }

    /**
     * Constructs a new invalid argument exception with the specified argument name and cause.
     * @param name      The name of the invalid argument.
     * @param cause     The cause.
     */
    public InvalidArgumentException(String name, Throwable cause) {
        super((name == null || name.trim().length() == 0) ? "Invalid argument" : "Invalid argument: " + name, cause);
        this.name = (name == null) ? "" : name;
    }

    /**
     * Constructs a new invalid argument exception with the specified argument name, detail message and cause.
     * @param name      The name of the invalid argument.
     * @param message   The detail message.
     * @param cause     The cause.
     */
    public InvalidArgumentException(String name, String message, Throwable cause) {
        super((message == null || message.trim().length() == 0) ? ((name == null || name.trim().length() == 0) ? "Invalid argument" : "Invalid argument: " + name) : message, cause);
        this.name = (name == null) ? "" : name;
    }
}
