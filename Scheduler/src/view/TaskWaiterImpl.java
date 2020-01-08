/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.Objects;
import java.util.concurrent.Callable;
import javafx.stage.Stage;

/**
 *
 * @author erwinel
 */
public class TaskWaiterImpl<T> extends TaskWaiter<T> {
    private final Callable<T> callable;
    
    public TaskWaiterImpl(Stage stage, Callable<T> callable) {
        super(stage);
        Objects.requireNonNull(callable);
        this.callable = callable;
    }
    
    public TaskWaiterImpl(Stage stage, String operation, Callable<T> callable) {
        super(stage, operation);
        Objects.requireNonNull(callable);
        this.callable = callable;
    }
    
    public TaskWaiterImpl(Stage stage, String operation, String heading, Callable<T> callable) {
        super(stage, operation, heading);
        Objects.requireNonNull(callable);
        this.callable = callable;
    }

    @Override
    protected T getResult() throws Exception {
        return callable.call();
    }

}
