/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class OperationFailureException extends Exception {

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
