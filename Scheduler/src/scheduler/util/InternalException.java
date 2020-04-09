package scheduler.util;

/**
 * An exception for an internal application error.
 * This is intended for re-throwing exceptions that should never occur, usually indicating there is a bug in the code.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class InternalException extends RuntimeException {

    /**
     * Serialization identifier
     */
    private static final long serialVersionUID = -3675524083708610412L;

    /**
     * Constructs a new internal exception with the specified detail message.
     *
     * @param message The detail message.
     */
    public InternalException(String message) {
        super(message);
    }

    /**
     * Constructs a new internal exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause The cause.
     */
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
