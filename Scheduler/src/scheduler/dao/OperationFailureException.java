package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class OperationFailureException extends Exception {

    private static final long serialVersionUID = -2306473162719264646L;

    public OperationFailureException(String message) {
        super((null == message || message.trim().isEmpty()) ? "Unknown validation failure" : message);
    }

    public OperationFailureException(String message, Throwable cause) {
        super((null == message || message.trim().isEmpty()) ? "Unexpected validation failure" : message, cause);
    }

    public OperationFailureException(Throwable cause) {
        this(null, cause);
    }

}
