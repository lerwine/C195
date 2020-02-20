/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javafx.stage.Stage;
import scheduler.util.ThrowableFunction;

/**
 * Implementation of the {@link TaskWaiter} interface.
 *
 * @author erwinel
 * @param <T> The result type.
 */
public class TaskWaiterImpl<T> extends TaskWaiter<T> {

    private final ThrowableFunction<Connection, T, SQLException> func;

    public TaskWaiterImpl(Stage window, ThrowableFunction<Connection, T, SQLException> func) {
        super(window);
        this.func = Objects.requireNonNull(func);
    }

    public TaskWaiterImpl(Stage window, String operation, ThrowableFunction<Connection, T, SQLException> func) {
        super(window, operation);
        this.func = Objects.requireNonNull(func);
    }

    public TaskWaiterImpl(Stage window, String operation, String heading, ThrowableFunction<Connection, T, SQLException> func) {
        super(window, operation, heading);
        this.func = Objects.requireNonNull(func);
    }

    @Override
    protected T getResult(Connection connection) throws SQLException {
        return func.apply(connection);
    }

    @Override
    protected void processResult(T result, Stage owner) {
    }

    @Override
    protected void processException(Throwable ex, Stage owner) {
    }

}
