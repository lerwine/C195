package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ValidationFailureException extends Exception {

    private static final long serialVersionUID = -2828047283241803581L;

    public ValidationFailureException(String message) {
        super((null == message || message.trim().isEmpty()) ? "Unknown validation failure" : message);
    }

    public ValidationFailureException(String message, Throwable cause) {
        super((null == message || message.trim().isEmpty()) ? "Unexpected validation failure" : message, cause);
    }

    public ValidationFailureException(Throwable cause) {
        this(null, cause);
    }

}
