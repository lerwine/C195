/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 * An exception which indicates an internal error, usually for an exception that should never occur.
 * This usually indicates there is a bug in the code.
 * @author Leonard T. Erwine
 */
public class InternalException extends RuntimeException {
    /**
     * Constructs a new internal exception with the specified detail message.
     * @param message   The detail message.
     */
    public InternalException(String message) {
        super(message);
    }

    /**
     * Constructs a new internal exception with the specified detail message and cause.
     * @param message   The detail message.
     * @param cause     The cause.
     */
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
