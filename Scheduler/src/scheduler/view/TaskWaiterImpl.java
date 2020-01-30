/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.util.Objects;
import java.util.concurrent.Callable;
import javafx.stage.Window;

/**
 *
 * @author erwinel
 */
public class TaskWaiterImpl<T> extends TaskWaiter<T> {
    private final Callable<T> callable;
    
    public TaskWaiterImpl(Window window, Callable<T> callable) {
        super(window);
        Objects.requireNonNull(callable);
        this.callable = callable;
    }
    
    public TaskWaiterImpl(Window window, String operation, Callable<T> callable) {
        super(window, operation);
        Objects.requireNonNull(callable);
        this.callable = callable;
    }
    
    public TaskWaiterImpl(Window window, String operation, String heading, Callable<T> callable) {
        super(window, operation, heading);
        Objects.requireNonNull(callable);
        this.callable = callable;
    }

    @Override
    protected T getResult() throws Exception {
        return callable.call();
    }

}
